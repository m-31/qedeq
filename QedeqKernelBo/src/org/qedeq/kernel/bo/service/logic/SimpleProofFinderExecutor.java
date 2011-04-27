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

import java.io.File;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.common.PluginExecutor;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofFinderFactoryImpl;
import org.qedeq.kernel.bo.logic.common.ProofFinder;
import org.qedeq.kernel.bo.logic.common.ProofFinderFactory;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.LogicalModuleState;
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

    /** List of axioms, definitions and propositions. */
    private FormalProofLineListVo validFormulas;

    /** Save changed modules directly? */
    private boolean noSave;

    /** Currently running proof finder. */
    private ProofFinder finder;

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
        final String finderFactoryClass
            = (parameters != null ? (String) parameters.get("checkerFactory") : null);
        if (finderFactoryClass != null && finderFactoryClass.length() > 0) {
            try {
                Class cl = Class.forName(finderFactoryClass);
                finderFactory = (ProofFinderFactory) cl.newInstance();
            } catch (ClassNotFoundException e) {
                Trace.fatal(CLASS, this, method, "ProofFinderFactory class not in class path: "
                    + finderFactoryClass, e);
            } catch (InstantiationException e) {
                Trace.fatal(CLASS, this, method, "ProofFinderFactory class could not be instanciated: "
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

    public Object executePlugin() {
        getServices().checkModule(getQedeqBo().getModuleAddress());
        QedeqLog.getInstance().logRequest(
            "Trying to create formal proofs for \""
            + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        try {
            validFormulas = new FormalProofLineListVo();
            traverse();
            QedeqLog.getInstance().logSuccessfulReply(
                "Proof creation finished for \"" + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"");
        } catch (SourceFileExceptionList e) {
            final String msg = "Proof creation not (fully?) successful for \""
                + IoUtility.easyUrl(getQedeqBo().getUrl()) + "\"";
            QedeqLog.getInstance().logFailureReply(msg, e.getMessage());
            return Boolean.FALSE;
        } finally {
            getQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
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
        if (proposition.getFormalProofList() == null) {
            final FormalProofLineList proof;
            // we try finding a proof
            try {
                finder = finderFactory.createProofFinder();
                proof = finder.findProof(proposition.getFormula().getElement(), validFormulas,
                    getCurrentContext());
            } finally {
                finder = null;  // so we always new if we are currently searching
            }
            // TODO 20110323 m31: we do a dirty cast to modify the current module
            Object state;
            try {
                state = YodaUtility.getFieldValue(getQedeqBo(), "stateManager");
                YodaUtility.executeMethod(state, "setLogicalState", new Class[] {
                    LogicalModuleState.class},
                    new Object[] {LogicalModuleState.STATE_UNCHECKED});
                ((PropositionVo) proposition).addFormalProof(new FormalProofVo(proof));
                YodaUtility.executeMethod(state, "setErrors", new Class[] {
                        SourceFileExceptionList.class},
                        new Object[] {null});
                    ((PropositionVo) proposition).addFormalProof(new FormalProofVo(proof));
            } catch (Exception e) {
                final String msg = "changing properties failed";
                Trace.fatal(CLASS, "visitEnter(Proposition)", msg, e);
                QedeqLog.getInstance().logMessage(msg + " " +  e.toString());
            }
            if (proof != null && !noSave) {
                final File file = getServices().getLocalFilePath(
                    getQedeqBo().getModuleAddress());
                try {
                    QedeqLog.getInstance().logMessage(
                        "Saving file \"" + file + "\"");
                    QedeqFileDao dao = getServices().getQedeqFileDao();
                    dao.saveQedeq(getQedeqBo(), file);
                    if (!getQedeqBo().getModuleAddress().isFileAddress()) {
                        QedeqLog.getInstance().logMessage("Only the the buffered file changed!");
                    }
                } catch (Exception e) {
                    final String msg = "Saving file \"" + file + "\" failed";
                    Trace.fatal(CLASS, "visitEnter(Proposition)", msg, e);
                    QedeqLog.getInstance().logMessage(msg + " " +  e.toString());
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

    public String getExecutionActionDescription() {
        final String s = super.getExecutionActionDescription();
        if (finder == null) {
            return s;
        }
        return s + " " + finder.getExecutionActionDescription();
    }

}
