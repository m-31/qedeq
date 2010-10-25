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

package org.qedeq.kernel.bo.service.utf8;



/**
 * QEDEQ module reference finder.
 *
 * @author  Michael Meyling
 */
public interface ReferenceFinder {

    public boolean isExternalModuleReference(final String moduleLabel);

    public String getExternalReference(final String moduleLabel);

    public String getExternalReference(final String moduleLabel, final String label,
        final String subReference);

    public boolean isLocalReference(final String label);

    public String getLocalReference(final String label, final String subReference);

}
