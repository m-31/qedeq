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

package org.qedeq.kernel.bo.logic.proof.finder;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.log.LogListener;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.service.Element2LatexImpl;
import org.qedeq.kernel.bo.service.Element2Utf8Impl;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.ModusPonensVo;
import org.qedeq.kernel.se.dto.module.SubstPredVo;


/**
 * Utilities for proofs finders.
 *
 * @author  Michael Meyling
 */
public final class ProofFinderUtility {

    /** Maps new proof line numbers to old ones. */
    private List new2Old;

    /**
     * Constructor.
     *
     */
    private ProofFinderUtility() {
    }

    /**
     * Shorten given formal proof.
     *
     * @param   lines   Lines of formulas.
     * @param   reasons Reasons for derivation of formula. Must be Add, MP or SubstPred.
     * @param   log     For BO logging.
     * @param   trans   Transformer (used for logging).
     * @return  Reduced formal proof.
     */
    public static FormalProofLineList shortenProof(final List lines, final List reasons,
            final LogListener log, final Element2Utf8 trans) {
        return (new ProofFinderUtility()).shorten(lines, reasons, log, trans);
    }

    private FormalProofLineList shorten(final List lines, final List reasons,
            final LogListener log, final Element2Utf8 trans) {
        final boolean[] used = new boolean[lines.size()];
        int n = lines.size() - 1;
        used[n] = true;
        while (n >= 0) {
            if (used[n]) {
                Reason r = (Reason) reasons.get(n);
                if (r instanceof ModusPonensBo) {
                    final ModusPonensBo mp = (ModusPonensBo) reasons.get(n);
                    used[mp.getN1()] = true;
                    used[mp.getN2()] = true;
                } else if (r instanceof SubstPredBo) {
                    final SubstPredBo substPred = (SubstPredBo) reasons.get(n);
                    used[substPred.getN()] = true;
                } else if (r instanceof Add) {
                    // ignore
                } else {
                    throw new IllegalStateException("unexpected reason class: " + r.getClass());
                }
            }
            n--;
        }
        for (int j = 0; j < lines.size(); j++) {
            if (used[j]) {
                log.logMessage(getUtf8Line(lines, reasons, j, trans));
            }
        }
        new2Old = new ArrayList();
        for (int j = 0; j < lines.size(); j++) {
            if (used[j]) {
                new2Old.add(new Integer(j));
            }
        }
        final FormalProofLineListVo result = new FormalProofLineListVo();
        for (int j = 0; j < new2Old.size(); j++) {
            n = ((Integer) new2Old.get(j)).intValue();
            final Reason r = (Reason) reasons.get(n);
            final Reason newReason;
            if (r instanceof ModusPonensBo) {
                final ModusPonensBo mp = (ModusPonensBo) reasons.get(n);
                newReason = new ModusPonensVo("" + (1 + old2new(mp.getN1())),
                    "" + (1 + old2new(mp.getN2())));
            } else if (r instanceof SubstPredBo) {
                final SubstPredBo substPred = (SubstPredBo) reasons.get(n);
                newReason = new SubstPredVo("" + (1 + old2new(substPred.getN())),
                   substPred.getPredicateVariable(), substPred.getSubstituteFormula());
            } else if (r instanceof Add) {
                final Add add = (Add) reasons.get(n);
                newReason = new AddVo(add.getReference());
            } else {
                throw new IllegalStateException("unexpected reason class: " + r.getClass());
            }
            result.add(new FormalProofLineVo("" + (1 + j),
                new FormulaVo((Element) lines.get(new2old(j))),
                newReason));
        }
        for (int j = 0; j < result.size(); j++) {
            System.out.print(result.get(j).getLabel() + ": ");
            print(result.get(j).getFormula().getElement());
            System.out.println("  " + result.get(j).getReason());
        }
        return result;
    }

    private int old2new(final int old) {
        int result = new2Old.indexOf(new Integer(old));
        if (result < 0) {
            throw new IllegalArgumentException("not found: " + old);
        }
        return result;
    }

    private int new2old(final int n) {
        final int result = ((Integer) new2Old.get(n)).intValue();
        return result;
    }

    /**
     * Println UTF-8 representation of formula to <code>System.out</code>.
     *
     * @param   element For this element.
     */
    public static void println(final Element element) {
        System.out.println(getUtf8(element));
    }

    /**
     * Print UTF-8 representation of formula to <code>System.out</code>.
     *
     * @param   element For this element.
     */
    public static void print(final Element element) {
        System.out.print(getUtf8(element));
    }

    /**
     * Get UTF-8 representation for formula.
     *
     * @param   element For this element.
     * @return  Get the UTF-8 display text.
     */
    public static String getUtf8(final Element element) {
        ModuleLabels labels = new ModuleLabels();
        Element2LatexImpl converter = new Element2LatexImpl(labels);
        Element2Utf8Impl textConverter = new Element2Utf8Impl(converter);
        return textConverter.getUtf8(element);
    }

    /**
     * Get UTF-8 representation of proof line.
     *
     * @param   lines       Proof line formulas.
     * @param   reasons     Proof line reasons.
     * @param   i           Proof line to print.
     * @param   trans       Transformer to get UTF-8 out of a formula.
     * @return  UTF-8 display text for proof line.
     */
    public static String getUtf8Line(final List lines, final List reasons, final int i,
            final Element2Utf8 trans) {
        final StringBuffer result = new StringBuffer();
        result.append((i + 1) + ": ");
        Reason reason = (Reason) reasons.get(i);
        Element formula = (Element) lines.get(i);
        return getUtf8Line(formula, reason, i, trans);
    }

    /**
     * Get UTF-8 representation of proof line.
     *
     * @param   formula     Proof line formula.
     * @param   reason      Proof line reason.
     * @param   i           Proof line number.
     * @param   trans       Transformer to get UTF-8 out of a formula.
     * @return  UTF-8 display text for proof line.
     */
    public static String getUtf8Line(final Element formula, final Reason reason, final int i,
            final Element2Utf8 trans) {
        final StringBuffer result = new StringBuffer();
        result.append((i + 1) + ": ");
        result.append(ProofFinderUtility.getUtf8(formula));
        result.append("  : ");
        if (reason instanceof SubstPred) {
            SubstPred s = (SubstPred) reason;
            result.append("Subst " + s.getReference() + " ");
            result.append(ProofFinderUtility.getUtf8(s.getPredicateVariable()));
            result.append(" by ");
            result.append(ProofFinderUtility.getUtf8(s.getSubstituteFormula()));
        } else {
            result.append(reason);
        }
        return result.toString();
    }

}
