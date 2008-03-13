/* $Id: ListVisitor.java,v 1.2 2007/02/25 20:05:36 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.visitor;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.ElementList;
import org.qedeq.kernel.common.ModuleDataException;


/**
 * Visit elements of the list package.
 *
 * @version $Revision: 1.2 $
 * @author Michael Meyling
 */
public interface ListVisitor {

    /**
     * Visit certain element. Begin of visit.
     *
     * @param atom  Begin visit of this element.
     * @throws      ModuleDataException  Major problem occurred.
     */
    public void visitEnter(Atom atom) throws ModuleDataException;

    /**
     * Visit certain element. Begin of visit.
     *
     * @param list  Begin visit of this element.
     * @throws      ModuleDataException  Major problem occurred.
     */
    public void visitEnter(ElementList list) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param atom  End visit of this element.
     * @throws      ModuleDataException  Major problem occurred.
     */
    public void visitLeave(Atom atom) throws ModuleDataException;

    /**
     * Visit certain element. End of visit.
     *
     * @param list  End visit of this element.
     * @throws      ModuleDataException  Major problem occurred.
     */
    public void visitLeave(ElementList list) throws ModuleDataException;

}
