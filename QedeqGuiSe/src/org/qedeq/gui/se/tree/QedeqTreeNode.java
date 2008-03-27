/* $Id: QedeqTreeNode.java,v 1.3 2008/03/27 05:14:03 m31 Exp $
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

package org.qedeq.gui.se.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import org.qedeq.kernel.common.QedeqBo;



/**
 * Node for a JTreee.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public final class QedeqTreeNode extends DefaultMutableTreeNode {

    public QedeqTreeNode(final ModuleElement userObject, final boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public QedeqTreeNode(final QedeqBo userObject, final boolean allowsChildren) {
        super(userObject, allowsChildren);
    }


    /**
     * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
     */
    public final void setUserObject(final Object userObject) {

        if (userObject instanceof QedeqBo) {
            this.userObject = userObject;
        } else {
            throw new IllegalArgumentException("Only ModuleElements could be managed!");
        }
    }

}
