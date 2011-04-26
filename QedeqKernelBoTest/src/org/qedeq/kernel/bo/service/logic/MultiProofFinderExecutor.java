/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
 *
 * "Hilbert II" is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package org.qedeq.kernel.bo.service.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofFinderFactoryImpl;
import org.qedeq.kernel.bo.logic.common.MultiProofFinder;
import org.qedeq.kernel.bo.logic.common.ProofFinderFactory;
import org.qedeq.kernel.bo.logic.common.ProofFoundListener;
import org.qedeq.kernel.bo.logic.proof.finder.MultiProofFinderImpl;
import org.qedeq.kernel.bo.logic.proof.finder.ProofFinderUtility;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;
import org.qedeq.kernel.se.dto.module.ReasonTypeVo;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Finds simple formal proofs.
 *
 * @author  Michael Meyling
 */
public final class MultiProofFinderExecutor extends ControlVisitor implements PluginExecutor,
        ProofFoundListener {

    /** This class. */
    private static final Class CLASS = MultiProofFinderExecutor.class;

    /** Factory for generating new checkers. */
    private ProofFinderFactory finderFactory = null;

    /** Parameters for checker. */
    private Map parameters;

    /** List of formulas we need a proof for. */
    private ElementList goalFormulas;

    /** IDs for the goal formulas. */
    private List idsForGoalFormulas;

    /** List of axioms, definitions and propositions. */
    private FormalProofLineListVo validFormulas;

    /** Save changed modules directly? */
    private boolean noSave;

    /** Currently running proof finder. */
    private MultiProofFinder finder;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    MultiProofFinderExecutor(final Plugin plugin, final KernelQedeqBo qedeq,
            final Map parameters) {
        super(plugin, qedeq);
        final String method = "MultiProofFinderExecutor(Plugin, KernelQedeqBo, Map)";
        this.parameters = parameters;
        final String finderFactoryClass
            = (parameters != null ? (String) parameters.get("checkerFactory") : null);
        if (finderFactoryClass != null && finderFactoryClass.length() > 0) {
            try {
                Class cl = Class.forName(finderFactoryClass);
                finderFactory = (ProofFinderFactory) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "ProofCheckerFactory class not in class path: "
                    + finderFactoryClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "ProofCheckerFactory class could not be instanciated: "
                    + finderFactoryClass, e);
            } catch (IllegalAccessException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, access for instantiation failed for model: "
                    + finderFactoryClass, e);
            } catch (RuntimeException e) {
                Trace.fatal(CLASS, this, method,
                    "Programming error, instantiation failed for model: " + finderFactoryClass, e);
            }
        }
        // fallback is the default finder factory
        if (finderFactory == null) {
            finderFactory = new ProofFinderFactoryImpl();
        }
        String noSaveString = null;
        if (parameters != null) {
            noSaveString = (String) parameters.get("noSave");
        }
        noSave = "true".equalsIgnoreCase(noSaveString);
    }

    private Map getParameters() {
        return parameters;
    }

    public Object executePlugin() {
        getServices().checkModule(getQedeqBo().getModuleAddress());
        QedeqLog.getInstance().logRequest(
            "Trying to create formal proofs for \""
            + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        boolean result = false;
        try {
            validFormulas = new FormalProofLineListVo();
            goalFormulas = new DefaultElementList("goalFormulas");
            idsForGoalFormulas = new ArrayList();
            traverse();
            finder = new MultiProofFinderImpl();
            result = finder.findProof(
                    (ElementList) goalFormulas.copy(), validFormulas, this, getCurrentContext());
            QedeqLog.getInstance().logSuccessfulReply(
                    "Proof creation finished for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        } catch (SourceFileExceptionList e) {
            final String msg = "Proof creation not (fully?) successful for \""
                + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
            return Boolean.FALSE;
        } catch (InterruptException e) {
            final String msg = "Proof creation was interrupted for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
            e.printStackTrace();
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        if (result) {
            QedeqLog.getInstance().logSuccessfulReply(
                "Proof creation finished for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
            return Boolean.TRUE;
        } else {
            final String msg = "Proof creation not fully successful for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
            + "\"";
            QedeqLog.getInstance().logFailureReply(msg, "No proof found");
            return Boolean.FALSE;
        }
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
            new ReasonTypeVo(new AddVo(getNodeBo().getNodeVo().getId()))));
        setBlocked(true);
    }

    public void visitLeave(final Axiom axiom) {
        setBlocked(false);
    }

    public void visitEnter(final PredicateDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
                new ReasonTypeVo(new AddVo(getNodeBo().getNodeVo().getId()))));
        setBlocked(true);
    }

    public void visitLeave(final PredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialPredicateDefinition definition)
            throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final InitialPredicateDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final InitialFunctionDefinition definition)
            throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final InitialFunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final FunctionDefinition definition)
            throws ModuleDataException {
        if (definition == null) {
            return;
        }
        validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
                new ReasonTypeVo(new AddVo(getNodeBo().getNodeVo().getId()))));
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        if (proposition == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        // we try creating
        if (proposition.getFormalProofList() == null) {
            idsForGoalFormulas.add(getNodeBo().getNodeVo().getId());
            goalFormulas.add(proposition.getFormula().getElement());
        } else {
            validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
                    new ReasonTypeVo(new AddVo(getNodeBo().getNodeVo().getId()))));
        }
        setBlocked(true);
    }

    public void visitLeave(final Proposition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Rule rule) throws ModuleDataException {
        setBlocked(true);
    }

    public void visitLeave(final Rule rule) {
        setBlocked(false);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    public void proofFound(final Element formula, final FormalProofLineList proof) {
        int n;
        for (n = 0; n < goalFormulas.size(); n++) {
            if (formula.equals(goalFormulas.getElement(n))) {
                break;
            }
        }
        if (n >= goalFormulas.size()) {
            System.out.println("Not found formula: ");
            ProofFinderUtility.print(formula);
        }
        final String id = (String) idsForGoalFormulas.get(n);
        final KernelNodeBo node = getQedeqBo().getLabels().getNode(
            id);
        if (node == null) {
            System.out.println("node not found: " + id);
        }
        final Proposition proposition = node.getNodeVo().getNodeType().getProposition();
        if (proposition == null) {
            System.out.println("no proposition: " + node.getNodeVo());
        }
        // TODO 20110323 m31: we do a dirty cast to modify the current module
        ((PropositionVo) proposition).addFormalProof(new FormalProofVo(proof));
        try {
            QedeqFileDao dao = getServices().getQedeqFileDao();
            dao.saveQedeq(getQedeqBo(),
                getServices().getLocalFilePath(getQedeqBo().getModuleAddress()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
