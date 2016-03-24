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

package org.qedeq.kernel.bo.service.unicode;

import org.qedeq.base.io.SourcePosition;


/**
 * QEDEQ module reference finder.
 *
 * @author  Michael Meyling
 */
public interface ReferenceFinder {

    /**
     * Return string for reference link.
     *
     * @param   reference       Link to this.
     * @param   startDelta      Absolute source position start of reference.
     * @param   endDelta        Absolute source position end of reference.
     * @return  Return reference as string.
     */
    public String getReferenceLink(final String reference,
        final SourcePosition startDelta, final SourcePosition endDelta);

    /**
     * Add warning.
     *
     * @param   code        Warning code.
     * @param   msg         Warning message.
     * @param   startDelta  Skip position relative to location start).
     * @param   endDelta    Mark until this column (relative to location start).
     */
    public void addWarning(final int code, final String msg, final SourcePosition startDelta,
            final SourcePosition endDelta);

}
