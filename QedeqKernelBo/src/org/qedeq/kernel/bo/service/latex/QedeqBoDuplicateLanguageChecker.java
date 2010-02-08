/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.latex;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.bo.module.ControlVisitor;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Checks if all formulas of a QEDEQ module are well formed.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public final class QedeqBoDuplicateLanguageChecker extends ControlVisitor {

    /**
     * Constructor.
     *
     * @param   bo  BO QEDEQ module object.
     */
    private QedeqBoDuplicateLanguageChecker(final KernelQedeqBo bo) {
        super(bo);
    }

    /**
     * Checks if all formulas of a QEDEQ module are well formed.
     *
     * @param   prop              QEDEQ BO.
     * @throws  DefaultSourceFileExceptionList An error occured.
     */
    public static void check(final KernelQedeqBo prop)
            throws DefaultSourceFileExceptionList {
        final QedeqBoDuplicateLanguageChecker checker
            = new QedeqBoDuplicateLanguageChecker(prop);
        checker.traverse();
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
        setBlocked(true);
    }

    public final void visitLeave(final LatexList list) {
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

}
