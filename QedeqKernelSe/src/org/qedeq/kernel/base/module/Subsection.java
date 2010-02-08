/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.base.module;


/**
 * Special subsection of a qedeq file.
 *
 * @version $Revision: 1.9 $
 * @author  Michael Meyling
 */
public interface Subsection extends SubsectionType {

    /**
     * Get label of node. The node is referenced with this label.
     *
     * @return  Returns the label.
     */
    public String getId();

    /**
     * Get level of subsection. Higher levels contain additional informations.
     *
     * @return  Returns the level.
     */
    public String getLevel();

    /**
     * Get title of subsection.
     *
     * @return  Returns the name.
     */
    public LatexList getTitle();

    /**
     * Get the LaTeX text.
     *
     * @return  Returns the LaTeX text.
     */
    public LatexList getLatex();
}
