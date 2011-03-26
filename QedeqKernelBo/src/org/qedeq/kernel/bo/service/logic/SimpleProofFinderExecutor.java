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

import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofFinderFactoryImpl;
import org.qedeq.kernel.bo.logic.common.ProofFinderFactory;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.module.Reference;
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
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;
import org.qedeq.kernel.se.dto.module.ReasonTypeVo;


/**
 * Finds simple formal proofs.
 *
 * @author  Michael Meyling
 */
public final class SimpleProofFinderExecutor extends ControlVisitor implements PluginExecutor {

    /** This class. */
    private static final Class CLASS = SimpleProofFinderExecutor.class;

    /** Factory for generating new checkers. */
    private ProofFinderFactory finderFactory = null;

    /** Parameters for checker. */
    private Map parameters;

    /** List of axioms, definitions and propositions. */
    private FormalProofLineListVo validFormulas;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    SimpleProofFinderExecutor(final Plugin plugin, final KernelQedeqBo qedeq,
            final Map parameters) {
        super(plugin, qedeq);
        final String method = "SimpleProofFinderExecutor(Plugin, KernelQedeqBo, Map)";
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
    }

    private Map getParameters() {
        return parameters;
    }

    public Object executePlugin() {
        QedeqLog.getInstance().logRequest(
                "Try to create formal proofs for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        KernelContext.getInstance().checkModule(getQedeqBo().getModuleAddress());
        try {
            validFormulas = new FormalProofLineListVo();
            traverse();
        } catch (SourceFileExceptionList e) {
            final String msg = "Proof creation not fully successful for \"" + IoUtility.easyUrl(getQedeqBo().getUrl())
                + "\"";
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
            return Boolean.FALSE;
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        QedeqLog.getInstance().logSuccessfulReply(
                "Proof creation successful for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        return Boolean.TRUE;
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
            final FormalProofLineList proof = finderFactory.createProofFinder().findProof(
                proposition.getFormula().getElement(), validFormulas);
            // TODO 20110323 m31: we do a dirty cast to modify the current module
            ((PropositionVo) proposition).addFormalProof(new FormalProofVo(proof));
            if (proof != null) {
                Object obj;
                try {
                    obj = YodaUtility.getFieldValue(KernelContext.getInstance(), "services");
                    System.out.println(obj.getClass());
                    InternalKernelServices services = (InternalKernelServices) obj;
                    QedeqFileDao dao = services.getQedeqFileDao();
                    dao.saveQedeq(getQedeqBo(),
                        services.getLocalFilePath(getQedeqBo().getModuleAddress()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
        if (rule == null) {
            return;
        }
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

    public boolean hasProvedFormula(final String reference) {
        final Reference ref = getReference(reference, getCurrentContext(), false, false);
        if (ref == null) {
            System.out.println("ref == null");
            return false;
        }
        if (ref.isExternalModuleReference()) {
            System.out.println("ref is external module");
            return false;
        }
        if (!ref.isNodeReference()) {
            System.out.println("ref is no node reference");
            return false;
        }
        if (null == ref.getNode()) {
            System.out.println("ref node == null");
            return false;
        }
        if (ref.isSubReference()) {
            return false;
        }
        if (!ref.isProofLineReference()) {
            if (!ref.getNode().isProved()) {
                System.out.println("ref node is not marked as proved: " + reference);
            }
            if (!ref.getNode().isProved()) {
                return false;
            }
            if (!ref.getNode().hasFormula()) {
                System.out.println("node has no formula: " + reference);
                return false;
            }
            return ref.getNode().isProved();
        } else {
            System.out.println("proof line references are not ok!");
            return false;
        }
    }

}
