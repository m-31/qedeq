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

package org.qedeq.kernel.bo.logic.proof.basic;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ProofChecker;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.wf.FormulaUtility;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.ReasonType;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.common.ModuleContext;


/**
 *
 *
 * @author  Michael Meyling
 */
public class ProofCheckerImpl implements ProofChecker {

    /** This class. */
    private static final Class CLASS = ProofCheckerImpl.class;

    /** Formula we want to prove. */
    private Element formula;

    /** Proof we want to check. */
    private FormalProofLineList proof;

    /** Module context of proof line list. */
    private ModuleContext moduleContext;

    /** Current context. */
    private ModuleContext currentContext;

    /** Resolver for external references. */
    private ReferenceResolver resolver;

    /** Existence checker for operators. */
    private ExistenceChecker existence;

    /** All exceptions that occurred during checking. */
    private LogicalCheckExceptionList exceptions;

    /** Array with proof status for each proof line. */
    private boolean[] lineProved;

    /** Is the proof invalid? */
    private boolean proofInvalid = false;

    /**
     * Constructor.
     *
     */
    public ProofCheckerImpl() {
    }

    public LogicalCheckExceptionList checkProof(final Element formula,
            final FormalProofLineList proof, final ModuleContext moduleContext,
            final ReferenceResolver resolver, final ExistenceChecker existence) {
        this.formula = formula;
        this.proof = proof;
        this.resolver = resolver;
        this.existence = existence;
        this.moduleContext = moduleContext;
        // use copy constructor for changing context
        currentContext = new ModuleContext(moduleContext);
        exceptions = new LogicalCheckExceptionList();
        final String context = moduleContext.getLocationWithinModule();
        lineProved = new boolean[proof.size()];
        for (int i = 0; i < proof.size(); i++) {
            boolean ok = true;
            setLocationWithinModule(context + ".get("  + i + ")");
            final FormalProofLine line = proof.get(i);
            if (line == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.PROOF_LINE_MUST_NOT_BE_NULL_CODE,
                    BasicProofErrors.ELEMENT_MUST_NOT_BE_NULL_TEXT,
                    getCurrentContext());
                continue;
            }
            setLocationWithinModule(context + ".get("  + i + ").getReasonType()");
            final ReasonType reasonType = line.getReasonType();
            if (reasonType == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_CODE,
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_TEXT,
                    getCurrentContext());
                continue;
            }
            final Reason reason = reasonType.getReason();
            if (reason == null) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_CODE,
                    BasicProofErrors.REASON_MUST_NOT_BE_NULL_TEXT,
                    getCurrentContext());
                continue;
            }
            // check if only basis rules are used
            // TODO 20110316 m31: this is a dirty trick to get the context of the reason
            //                    perhaps we can solve this more elegantly?
            String getReason = ".get" + StringUtility.getClassName(reason.getClass());
            if (getReason.endsWith("Vo")) {
                getReason = getReason.substring(0, getReason.length() - 2) + "()";
                setLocationWithinModule(context + ".get("  + i + ").getReasonType()"
                    + getReason);
            }
            if (
                       !(reason instanceof Add)
                    && !(reason instanceof Rename)
                    && !(reason instanceof SubstFree)
                    && !(reason instanceof SubstPred)
                    && !(reason instanceof SubstFunc)
                    && !(reason instanceof Existential)
                    && !(reason instanceof Universal)
                    && !(reason instanceof ModusPonens)
                ) {
                ok = false;
                handleProofCheckException(
                        BasicProofErrors.THIS_IS_NO_ALLOWED_BASIC_REASON_CODE,
                        BasicProofErrors.THIS_IS_NO_ALLOWED_BASIC_REASON_TEXT
                        + reason.getName(),
                        getCurrentContext());
                continue;
            }
            if (reason instanceof Add) {
                setLocationWithinModule(context + ".get("  + i + ").getReasonType()"
                        + ".getAdd()");
                ok = check(reasonType.getAdd(), i, line.getFormula().getElement());
            }
            if (reason instanceof ModusPonens) {
                setLocationWithinModule(context + ".get("  + i + ").getReasonType()"
                        + ".getModusPonens()");
                ok = check(reasonType.getModusPonens(), i, line.getFormula().getElement());
            }
            lineProved[i] = ok;
            if (!proofInvalid) {
                resolver.setLastProved(i);
            }
        }
        return exceptions;
    }

    private boolean check(final Add add, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        if (!resolver.hasProvedFormula(add.getReference())) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + add.getReference(),
                getCurrentContext());
            return ok;
        }
        final Element expected = resolver.getNormalizedReferenceFormula(add.getReference());
        final Element current = resolver.getNormalizedFormula(element);
        if (!EqualsUtility.equals(expected, current)) {
            ok = false;
            final ModuleContext lc = new ModuleContext(moduleContext.getModuleLocation(),
                moduleContext.getLocationWithinModule() + ".get(" + i + ").getFormula().getElement()"
                + FormulaUtility.getDifferenceLocation(current, expected));
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                + add.getReference(),
                lc);
            return ok;
        }
        return ok;
    }

    private boolean check(final ModusPonens mp, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        if (!resolver.hasProvedFormula(mp.getReference1())) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        if (!resolver.hasProvedFormula(mp.getReference2())) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        return ok;
    }


    /**
     * Add new {@link ProofCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param element   Element with error.
     * @param context   Error context.
     */
    private void handleProofCheckException(final int code, final String msg,
            final Element element, final ModuleContext context) {
        final ProofCheckException ex = new ProofCheckException(code, msg, element, context);
        proofInvalid = true;
        exceptions.add(ex);
    }

    /**
     * Add new {@link ProofCheckException} to exception list.
     *
     * @param code      Error code.
     * @param msg       Error message.
     * @param context   Error context.
     */
    private void handleProofCheckException(final int code, final String msg,
            final ModuleContext context) {
        System.out.println(context);    // FIXME
        System.setProperty("qedeq.test.xmlLocationFailures", "true");  // FIXME
        final ProofCheckException ex = new ProofCheckException(code, msg, context);
        proofInvalid = true;
        exceptions.add(ex);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    protected final ModuleContext getCurrentContext() {
        return currentContext;
    }


}
