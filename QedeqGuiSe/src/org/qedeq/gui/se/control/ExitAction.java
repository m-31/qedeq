/* $Id: ExitAction.java,v 1.1 2007/08/21 20:44:58 m31 Exp $
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

package org.qedeq.gui.se.control;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.qedeq.kernel.context.KernelContext;

/**
 * Exit from application.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
class ExitAction extends AbstractAction {

    public void actionPerformed(final ActionEvent e) {
        KernelContext.getInstance().shutdown();
        System.exit(0);
    }

}
