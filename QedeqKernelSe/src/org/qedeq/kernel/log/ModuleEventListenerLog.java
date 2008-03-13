/* $Id: ModuleEventListenerLog.java,v 1.4 2007/08/21 21:03:30 m31 Exp $
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

package org.qedeq.kernel.log;

import org.qedeq.kernel.common.QedeqBo;

/**
 * Listener that writes events to the {@link org.qedeq.kernel.log.QedeqLog}.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class ModuleEventListenerLog implements ModuleEventListener {

    /**
     * Constructor.
     */
    public ModuleEventListenerLog() {
    }

    public void addModule(final QedeqBo prop) {
        QedeqLog.getInstance().logSuccessfulState("Module added", prop.getUrl());
    }

    public void stateChanged(final QedeqBo prop) {
        if (prop.hasFailures()) {
            QedeqLog.getInstance().logFailureState("Module state changed: "
                + prop.getStateDescription(), prop.getUrl(), prop.getException().getMessage());
        } else {
            QedeqLog.getInstance().logSuccessfulState("Module state changed: "
                + prop.getStateDescription(), prop.getUrl());
        }
    }

    public void removeModule(final QedeqBo prop) {
        QedeqLog.getInstance().logSuccessfulState("Module removed", prop.getUrl());
    }

}
