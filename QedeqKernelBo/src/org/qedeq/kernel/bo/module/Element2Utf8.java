/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.se.base.list.Element;

/**
 * Transfer a QEDEQ formula or term into UTF-8 text.
 *
 * @author  Michael Meyling
 */
public interface Element2Utf8 {

    /**
     * Get UTF-8 element presentation.
     *
     * @param   element     Print this element.
     * @return  UTF-8 form of element.
     */
    public abstract String getUtf8(final Element element);


    /**
     * Get an UTF-8 element presentation that doesn't exceed the given maximum column
     * length. If the presentation doesn't fit into one row it is split and several rows
     * are returned.
     *
     * @param   element     Print this element.
     * @param   maxCols     Maximum column number. If equal or below zero a maximum is
     *                      neglected.
     * @return  UTF-8 form of element (might be one or several rows).
     */
    public abstract String[] getUtf8(final Element element, int maxCols);


}
