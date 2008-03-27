/* $Id: LogListener.java,v 1.2 2008/03/27 05:16:26 m31 Exp $
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

import java.net.URL;

/**
 * Log event listener. Here one can listen to high level application events.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public interface LogListener {

    /**
     * Log message.
     *
     * @param   text    Message.
     */
    public void logMessage(String text);

    /**
     * Log request.
     *
     * @param   text    Request.
     */
    public void logRequest(String text);

    /**
     * Log successful reply for an request.
     *
     * @param   text    Reply.
     */
    public void logSuccessfulReply(String text);

    /**
     * Log failure reply for an request.
     *
     * @param   text        Reply.
     * @param   description Reason for reply.
     */
    public void logFailureReply(String text, String description);

    /**
     * Log message state for URL.
     *
     * @param   text    Message state.
     * @param   url     URL.
     */
    public void logMessageState(String text, URL url);

    /**
     * Log failure state for URL.
     *
     * @param   text    Failure state.
     * @param   url     URL.
     * @param   description Reason.
     */
    public void logFailureState(String text, URL url, String description);

    /**
     * Log successful state for URL.
     *
     * @param   text    State.
     * @param   url     URL.
     */
    public void logSuccessfulState(String text, URL url);

}
