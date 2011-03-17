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

import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.utility.Enumerator;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.logic.common.ExistenceChecker;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.Operators;
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
 * Formal proof checker for basic rules.
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

    /** Maps local proof line labels to local line number Integers. */
    private Map label2line;

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
        label2line = new HashMap();
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
                ok = check(reasonType.getAdd(), i, line.getFormula().getElement());
            } else if (reason instanceof Rename) {
                ok = check(reasonType.getRename(), i, line.getFormula().getElement());
            } else if (reason instanceof ModusPonens) {
                ok = check(reasonType.getModusPonens(), i, line.getFormula().getElement());
            } else if (reason instanceof SubstFree) {
                ok = check(reasonType.getSubstFree(), i, line.getFormula().getElement());
            } else if (reason instanceof SubstPred) {
                ok = check(reasonType.getSubstPred(), i, line.getFormula().getElement());
            } else if (reason instanceof SubstFunc) {
                ok = check(reasonType.getSubstFunc(), i, line.getFormula().getElement());
            }
            lineProved[i] = ok;
            if (line.getLabel() != null && line.getLabel().length() > 0) {
                final Integer n = (Integer) label2line.get(line.getLabel());
                if (n != null) {
                    final ModuleContext lc = new ModuleContext(moduleContext.getModuleLocation(),
                        moduleContext.getLocationWithinModule() + ".get("
                        + ((Integer) label2line.get(line.getLabel()))
                        + ").getLabel()");
                    setLocationWithinModule(context + ".get("  + i + ").getLabel()");
                    handleProofCheckException(
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_CODE,
                        BasicProofErrors.LOCAL_LABEL_ALREADY_EXISTS_TEXT
                        + line.getLabel(),
                        getCurrentContext(),
                        lc);
                }
                label2line.put(line.getLabel(), new Integer(i));
            }
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
            handleProofCheckException(
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                + add.getReference(),
                getDiffModuleContextOfProofLineFormula(i, expected));
            return ok;
        }
        return ok;
    }

    private boolean check(final Rename rename, final int i, final Element element) {
        System.out.println("Testing rename");
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(rename.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + rename.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + rename.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element expected = FormulaUtility.replaceSubjectVariableQuantifier(
                rename.getOriginalSubjectVariable(),
                rename.getReplacementSubjectVariable(), f, rename.getOccurrence(),
                new Enumerator());
            final Element current = resolver.getNormalizedFormula(element);
            if (!EqualsUtility.equals(expected, current)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + rename.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
            } else {
                ok = true;
            }
        }
        System.out.println("rename status: " + ok);
        return ok;
    }

    private boolean check(final SubstFree substfree, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substfree.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substfree.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substfree.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            final Element expected = f.replace(substfree.getSubjectVariable(),
                resolver.getNormalizedFormula(substfree.getSubstituteTerm()));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substfree.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        return ok;
    }

    private boolean check(final SubstPred substpred, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substpred.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substpred.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substpred.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            final Element expected = f.replace(substpred.getPredicateVariable(),
                resolver.getNormalizedFormula(substpred.getSubstituteFormula()));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substpred.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        return ok;
    }

    private boolean check(final SubstFunc substfunc, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n = (Integer) label2line.get(substfunc.getReference());
        if (n == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + substfunc.getReference(),
                getCurrentContext());
        } else if (!lineProved[n.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + substfunc.getReference(),
                getCurrentContext());
        } else {
            final Element f = getNormalizedProofLine(n);
            final Element current = resolver.getNormalizedFormula(element);
            final Element expected = f.replace(substfunc.getFunctionVariable(),
                resolver.getNormalizedFormula(substfunc.getSubstituteTerm()));
            if (!EqualsUtility.equals(current, expected)) {
                ok = false;
                handleProofCheckException(
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_CODE,
                    BasicProofErrors.EXPECTED_FORMULA_DIFFERS_TEXT
                    + substfunc.getReference(),
                    getDiffModuleContextOfProofLineFormula(i, expected));
                return ok;
            }
        }
        return ok;
    }

    private boolean check(final ModusPonens mp, final int i, final Element element) {
        final String context = currentContext.getLocationWithinModule();
        boolean ok = true;
        final Integer n1 = (Integer) label2line.get(mp.getReference1());
        if (n1 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference1(),
                getCurrentContext());
        } else if (!lineProved[n1.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference1()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        final Integer n2 = (Integer) label2line.get(mp.getReference2());
        if (n2 == null) {
            ok = false;
            setLocationWithinModule(context + ".getReference2()");
            handleProofCheckException(
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_CODE,
                BasicProofErrors.SUCH_A_LOCAL_LABEL_DOESNT_EXIST_TEXT
                + mp.getReference2(),
                getCurrentContext());
        } else if (!lineProved[n2.intValue()]) {
            ok = false;
            setLocationWithinModule(context + ".getReference2()");
            handleProofCheckException(
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_CODE,
                BasicProofErrors.THIS_IS_NO_REFERENCE_TO_A_PROVED_FORMULA_TEXT
                + mp.getReference1(),
                getCurrentContext());
        }
        if (ok) {
            final Element f1 = getNormalizedProofLine(n1);
            final Element f2 = getNormalizedProofLine(n2);
            final Element current = getNormalizedProofLine(i);
            if (!Operators.IMPLICATION_OPERATOR.equals(f1.getList().getOperator())
                    || f1.getList().size() != 2) {
                ok = false;
                setLocationWithinModule(context + ".getReference1()");
                handleProofCheckException(
                    BasicProofErrors.IMPLICATION_EXPECTED_CODE,
                    BasicProofErrors.IMPLICATION_EXPECTED_TEXT
                    + mp.getReference1(),
                    getCurrentContext());
            } else if (!f2.equals(f1.getList().getElement(0))) {
                ok = false;
                setLocationWithinModule(context + ".getReference2()");
                handleProofCheckException(
                    BasicProofErrors.MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_CODE,
                    BasicProofErrors.MUST_BE_HYPOTHESIS_OF_FIRST_REFERENCE_TEXT
                    + mp.getReference2(),
                    getCurrentContext(),
                    getModuleContextOfProofLineFormula(n1.intValue()));
            } else if (!current.equals(f1.getList().getElement(1))) {
                ok = false;
                setLocationWithinModule(context + ".getReference1()");
                handleProofCheckException(
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_CODE,
                    BasicProofErrors.CURRENT_MUST_BE_CONCLUSION_TEXT
                    + mp.getReference1(),
                    getCurrentContext(),
                    getModuleContextOfProofLineFormula(n1.intValue()));
            } else {
                ok = true;
            }
        }
        return ok;
    }

    private ModuleContext getModuleContextOfProofLineFormula(final int i) {
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()");
    }

    private ModuleContext getDiffModuleContextOfProofLineFormula(final int i,
            final Element expected) {
        final String diff = FormulaUtility.getDifferenceLocation(
            proof.get(i).getFormula().getElement(),  expected);
        return new ModuleContext(moduleContext.getModuleLocation(),
            moduleContext.getLocationWithinModule() + ".get(" + i
            + ").getFormula().getElement()" + diff);
    }

    private Element getNormalizedProofLine(final String label) {
        if (label == null || label.length() > 0 || null == label2line.get(label)) {
            return null;
        }
        return getNormalizedProofLine(((Integer) label2line.get(label)).intValue());
    }

    private Element getNormalizedProofLine(final Integer n) {
        if (n == null) {
            return null;
        }
        return getNormalizedProofLine(n.intValue());
    }

    private Element getNormalizedProofLine(final int i) {
        if (i < 0 || i >= proof.size()) {
            return null;
        }
        return resolver.getNormalizedFormula(proof.get(i).getFormula().getElement());
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
        System.out.println(context);    // FIXME
        System.setProperty("qedeq.test.xmlLocationFailures", "true");  // FIXME
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
     * Add new {@link ProofCheckException} to exception list.
     *
     * @param code              Error code.
     * @param msg               Error message.
     * @param context           Error context.
     * @param referenceContext  Reference context.
     */
    private void handleProofCheckException(final int code, final String msg,
            final ModuleContext context, final ModuleContext referenceContext) {
        System.out.println(context);    // FIXME
        System.setProperty("qedeq.test.xmlLocationFailures", "true");  // FIXME
        final ProofCheckException ex = new ProofCheckException(code, msg, null, context,
            referenceContext);
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
