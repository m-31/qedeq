/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.YodaUtility;
import org.qedeq.kernel.bo.log.ModuleLogListener;
import org.qedeq.kernel.bo.log.QedeqLog;
import org.qedeq.kernel.bo.logic.ProofFinderFactoryImpl;
import org.qedeq.kernel.bo.logic.proof.common.ProofFinder;
import org.qedeq.kernel.bo.logic.proof.common.ProofFinderArgumentException;
import org.qedeq.kernel.bo.logic.proof.common.ProofFinderFactory;
import org.qedeq.kernel.bo.logic.proof.common.ProofFoundException;
import org.qedeq.kernel.bo.logic.proof.common.ProofNotFoundException;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.bo.service.basis.ControlVisitor;
import org.qedeq.kernel.bo.service.basis.ModuleServicePluginExecutor;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;
import org.qedeq.kernel.se.state.WellFormedState;
import org.qedeq.kernel.se.visitor.InterruptException;


/**
 * Finds simple formal proofs.
 *
 * @author  Michael Meyling
 */
public final class SimpleProofFinderExecutor extends ControlVisitor implements ModuleServicePluginExecutor {

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

    /** All parameters for this search. */
    private Parameters parameters;

    /**
     * Constructor.
     *
     * @param   plugin      This plugin we work for.
     * @param   qedeq       QEDEQ BO object.
     * @param   parameters  Parameters.
     */
    SimpleProofFinderExecutor(final ModuleService plugin, final KernelQedeqBo qedeq,
            final Parameters parameters) {
        super(plugin, qedeq);
        final String method = "SimpleProofFinderExecutor(Plugin, KernelQedeqBo, Map)";
        final String finderFactoryClass = parameters.getString("checkerFactory");
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
        noSave = parameters.getBoolean("noSave");
        this.parameters = parameters;
    }

    private ModuleService getPlugin() {
        return (ModuleService) getService();
    }

    public Object executePlugin(final InternalModuleServiceCall call, final Object data) throws InterruptException {
        getServices().checkWellFormedness(call.getInternalServiceProcess(), getKernelQedeqBo());
        QedeqLog.getInstance().logRequest("Trying to create formal proofs", getKernelQedeqBo().getUrl());
        try {
            validFormulas = new FormalProofLineListVo();
            traverse(call.getInternalServiceProcess());
            QedeqLog.getInstance().logSuccessfulReply(
                "Proof creation successful", getKernelQedeqBo().getUrl());
        } catch (SourceFileExceptionList e) {
            final String msg = "Proof creation not (fully?) successful";
            QedeqLog.getInstance().logFailureReply(msg, getKernelQedeqBo().getUrl(), e.getMessage());
            return Boolean.FALSE;
        } finally {
            getKernelQedeqBo().addPluginErrorsAndWarnings(getPlugin(), getErrorList(), getWarningList());
        }
        return Boolean.TRUE;
    }

    public void visitEnter(final Axiom axiom) throws ModuleDataException {
        if (axiom == null) {
            return;
        }
        validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
            new AddVo(getNodeBo().getNodeVo().getId())));
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
                new AddVo(getNodeBo().getNodeVo().getId())));
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
            new AddVo(getNodeBo().getNodeVo().getId())));
        setBlocked(true);
    }

    public void visitLeave(final FunctionDefinition definition) {
        setBlocked(false);
    }

    public void visitEnter(final Proposition proposition)
            throws ModuleDataException {
        final String method = "visitEnter(Proposition)";
        Trace.begin(CLASS, this, method);
        if (proposition == null) {
            Trace.end(CLASS, this, method);
            return;
        }
        if (proposition.getFormalProofList() == null) {
            FormalProofLineList proof = null;
            // we try finding a proof
            try {
                finder = finderFactory.createProofFinder();
                finder.findProof(proposition.getFormula().getElement(), validFormulas,
                    getCurrentContext(), parameters, new ModuleLogListener() {
                        public void logMessageState(final String text) {
                            QedeqLog.getInstance().logMessageState(text, getKernelQedeqBo().getUrl());
                        }
                    }, getKernelQedeqBo().getElement2Utf8());
            } catch (ProofFoundException e) {
                proof = e.getProofLines();
            } catch (ProofNotFoundException e) {
                addWarning(e);
            } catch (ProofFinderArgumentException e) {
                Trace.trace(CLASS, "visitEnter(Proposition)", "Wrong Configuration in Proof Finder Arguments", e);
                addError(e);
            } finally {
                finder = null;  // so we always new if we are currently searching
            }
            if (proof != null) {
                QedeqLog.getInstance().logMessage("proof found for "
                    + super.getLocationDescription());
                // TODO 20110323 m31: we do a dirty cast to modify the current module
                Object state;
                try {
                    state = YodaUtility.getFieldValue(getKernelQedeqBo(), "stateManager");
                    YodaUtility.executeMethod(state, "setWellFormedState", new Class[] {
                        WellFormedState.class},
                        new Object[] {WellFormedState.STATE_UNCHECKED});
                    ((PropositionVo) proposition).addFormalProof(new FormalProofVo(proof));
                    YodaUtility.executeMethod(state, "setErrors", new Class[] {
                            SourceFileExceptionList.class},
                            new Object[] {null});
                } catch (Exception e) {
                    final String msg = "changing properties failed";
                    Trace.fatal(CLASS, "visitEnter(Proposition)", msg, e);
                    QedeqLog.getInstance().logMessage(msg + " " +  e.toString());
                }
            } else {
                QedeqLog.getInstance().logMessage("proof not found for "
                    + super.getLocationDescription());
            }
            if (proof != null && !noSave) {
                final File file = getServices().getLocalFilePath(
                    getKernelQedeqBo().getModuleAddress());
                try {
                    QedeqLog.getInstance().logMessage(
                        "Saving file \"" + file + "\"");
                    QedeqFileDao dao = getServices().getQedeqFileDao();
                    dao.saveQedeq(getInternalServiceCall().getInternalServiceProcess(), getKernelQedeqBo(), file);
                    if (!getKernelQedeqBo().getModuleAddress().isFileAddress()) {
                        QedeqLog.getInstance().logMessage("Only the the buffered file changed!");
                    }
                } catch (Exception e) {
                    final String msg = "Saving file \"" + file + "\" failed";
                    Trace.fatal(CLASS, "visitEnter(Proposition)", msg, e);
                    QedeqLog.getInstance().logMessage(msg + " " +  e.toString());
                }
            }
        } else {
            Trace.info(CLASS, method, "has already a proof: "
                + super.getLocationDescription());
            validFormulas.add(new FormalProofLineVo(new FormulaVo(getNodeBo().getFormula()),
                new AddVo(getNodeBo().getNodeVo().getId())));
        }
        setBlocked(true);
        Trace.end(CLASS, this, method);
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

    public String getLocationDescription() {
        final String s = super.getLocationDescription();
        if (finder == null) {
            return s;
        }
        return s + " " + finder.getExecutionActionDescription();
    }

}
