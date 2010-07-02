/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.log;

import org.qedeq.base.io.IoUtility;
import org.qedeq.kernel.bo.QedeqBo;

/**
 * Listener that writes events to the {@link org.qedeq.kernel.bo.log.QedeqLog}.
 *
 * @author  Michael Meyling
 */
public final class ModuleEventListenerLog implements ModuleEventListener {

    /**
     * Constructor.
     */
    public ModuleEventListenerLog() {
    }

    public void addModule(final QedeqBo prop) {
        QedeqLog.getInstance().logSuccessfulState("Module added", IoUtility.easyUrl(prop.getUrl()));
    }

    public void stateChanged(final QedeqBo prop) {
        if (prop.hasErrors()) {
            QedeqLog.getInstance().logFailureState("Module state changed: "
                + prop.getStateDescription(), IoUtility.easyUrl(prop.getUrl()), prop.getErrors().getMessage());
        } else {
            QedeqLog.getInstance().logSuccessfulState("Module state changed: "
                + prop.getStateDescription(), IoUtility.easyUrl(prop.getUrl()));
        }
    }

    public void removeModule(final QedeqBo prop) {
        QedeqLog.getInstance().logSuccessfulState("Module removed", IoUtility.easyUrl(prop.getUrl()));
    }

}
