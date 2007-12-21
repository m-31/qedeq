/* $Id: QedeqBoDuplicateLanguageChecker.java,v 1.3 2007/12/21 23:33:46 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.control;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.bo.module.QedeqBo;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTransverser;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class QedeqBoDuplicateLanguageChecker extends AbstractModuleVisitor {

    /** QEDEQ module input object. */
    private final QedeqBo original;

    /** Current context during creation. */
    private final QedeqNotNullTransverser transverser;

    /**
     * Constructor.
     *
     * @param   globalContext     Module location information.
     * @param   qedeq             BO QEDEQ module object.
     */
    private QedeqBoDuplicateLanguageChecker(final URL globalContext, final QedeqBo qedeq) {
        transverser = new QedeqNotNullTransverser(globalContext, this);
        original = qedeq;
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   globalContext       Module location information.
     * @param   qedeq               Basic QEDEQ module object.
     * @throws  ModuleDataException      Major problem occurred.
     */
    public static void check(final URL globalContext, final QedeqBo qedeq)
            throws ModuleDataException {
        final QedeqBoDuplicateLanguageChecker checker
            = new QedeqBoDuplicateLanguageChecker(globalContext, qedeq);
        checker.check();
    }

    private final void check() throws ModuleDataException {
        transverser.accept(original.getQedeq());
    }

    public final void visitEnter(final LatexList list) throws ModuleDataException {
        if (list == null) {
            return;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final Map languages = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            final Latex latex = list.get(i);
            setLocationWithinModule(context + ".get(" + i + ")");
            if (latex == null) {
                throw new LatexListDataException(1000, "Null pointer not permitted.",
                    getCurrentContext());
            }
            if (languages.containsKey(latex.getLanguage())) {
                throw new LatexListDataException(1001, "Language entry exists already",
                    getCurrentContext(), (ModuleContext) languages.get(latex.getLanguage()));
            }
            languages.put(list.get(i).getLanguage(), getCurrentContext());
        }
        setLocationWithinModule(context);
        transverser.setBlocked(true);
    }
/*
            try {
                if (latexList.get(i) == null) {
                    throw new NullPointerListEntryException(1000, "Null pointer not permitted.");
                }
                for (int j = 0; j < list.size(); j++) {
                    if ((list.get(j)).getLanguage().equals(latexList.get(i).getLanguage())) {
                        throw new DuplicateLanguageEntryException(1001,
                                "Language entry exists already", i);
                    }
                }
                list.add(create(latexList.get(i)));
            } catch (NullPointerListEntryException e) {
                throw new IllegalModuleDataException(e.getErrorCode(),
                    e.getMessage(), new ModuleContext(getCurrentContext()), e);
            } catch (DuplicateLanguageEntryException e) {
                throw new IllegalModuleDataException(e.getErrorCode(), e.getMessage(),
                    new ModuleContext(getCurrentContext()), new ModuleContext(getCurrentContext(),
                        context + ".get(" + (e.getIndex() + 1) + ")"), e);
            }

 */
    public final void visitLeave(final LatexList list) {
        transverser.setBlocked(false);
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    public void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    public final ModuleContext getCurrentContext() {
        return transverser.getCurrentContext();
    }

    /**
     * Get original QEDEQ module.
     *
     * @return  Original QEDEQ module.
     */
    protected final Qedeq getQedeqOriginal() {
        return original.getQedeq();
    }

}
