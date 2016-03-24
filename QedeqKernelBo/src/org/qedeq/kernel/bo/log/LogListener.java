/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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


/**
 * Log event listener. Here one can listen to high level application events.
 *
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
     * @param   url     URL.
     */
    public void logRequest(String text, String url);

    /**
     * Log successful reply for an request.
     *
     * @param   text    Reply.
     * @param   url         URL.
     */
    public void logSuccessfulReply(String text, String url);

    /**
     * Log failure reply for an request.
     *
     * @param   text        Reply.
     * @param   url         URL.
     * @param   description Reason for reply.
     */
    public void logFailureReply(String text, String url, String description);

    /**
     * Log message state for URL.
     *
     * @param   text    Message state.
     * @param   url     URL.
     */
    public void logMessageState(String text, String url);

    /**
     * Log failure state for URL.
     *
     * @param   text    Failure state.
     * @param   url     URL.
     * @param   description Reason.
     */
    public void logFailureState(String text, String url, String description);

    /**
     * Log successful state for URL.
     *
     * @param   text    State.
     * @param   url     URL.
     */
    public void logSuccessfulState(String text, String url);

}
