/* $Id: QedeqBoDuplicateLanguageChecker.java,v 1.4 2008/01/26 12:39:09 m31 Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.QedeqBo;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class QedeqBoDuplicateLanguageChecker extends AbstractModuleVisitor {

    /** QEDEQ module input object. */
    private final Qedeq original;

    /** Current context during creation. */
    private final QedeqNotNullTraverser traverser;

    /**
     * Constructor.
     *
     * @param   globalContext     Module location information.
     * @param   qedeq             BO QEDEQ module object.
     */
    private QedeqBoDuplicateLanguageChecker(final ModuleAddress globalContext,
            final Qedeq qedeq) {
        traverser = new QedeqNotNullTraverser(globalContext, this);
        original = qedeq;
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   prop              QEDEQ BO.
     * @throws  ModuleDataException      Major problem occurred.
     */
    public static void check(final QedeqBo prop)
            throws ModuleDataException {
        final QedeqBoDuplicateLanguageChecker checker
            = new QedeqBoDuplicateLanguageChecker(prop.getModuleAddress(),  prop.getQedeq());
        checker.check();
    }

    private final void check() throws ModuleDataException {
        traverser.accept(original);
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
        traverser.setBlocked(true);
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
        traverser.setBlocked(false);
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
        return traverser.getCurrentContext();
    }

    /**
     * Get original QEDEQ module.
     *
     * @return  Original QEDEQ module.
     */
    protected final Qedeq getQedeqOriginal() {
        return original;
    }

}
