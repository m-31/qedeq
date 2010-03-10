package org.qedeq.kernel.bo;

import org.qedeq.kernel.common.ModuleContext;


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

/**
 * Represents a reference list of modules. Every entry has a symbolic name for one referenced QEDEQ
 * module. This module label acts as a prefix for all references to that module. The module label
 * must be an unique String.
 *
 * @author Michael Meyling
 */
public interface ModuleReferenceList {

    /**
     * Get number of module references.
     *
     * @return  Number of module references.
     */
    public int size();

    /**
     * Get label for certain module.
     *
     * @param   index   Entry index.
     * @return  Label of module.
     */
    public String getLabel(int index);

    /**
     * Get properties of referenced module.
     *
     * @param   index   Entry index.
     * @return  Module properties for that module.
     */
    public QedeqBo getQedeqBo(int index);

    /**
     * Get import context of referenced module.
     *
     * @param   index   Entry index.
     * @return  Context for that module.
     */
    public ModuleContext getModuleContext(int index);

    /**
     * Get QedeqBo of referenced module via label. Might be <code>null</code>.
     *
     * @param   label   Label for referenced module or <code>null</code> if not found.
     * @return  Module properties for that module.
     */
    public QedeqBo getQedeqBo(String label);

}
