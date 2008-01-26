/* $Id: ModuleReferenceList.java,v 1.2 2008/01/26 12:39:09 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Represents a reference list of modules. Every entry has a symbolic name for one referenced QEDEQ
 * module. This module label acts as a prefix for all references to that module. The module label
 * must be an unique String.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public class ModuleReferenceList {

    /** This class. */
    private static final Class CLASS = ModuleReferenceList.class;

    /** Contains all labels. */
    private final List labels;

    /** Contains all module props. */
    private final List props;

    /** Contains all module import contexts. */
    private final List contexts;

    /** Maps labels to context. */
    private final Map label2Context;

    /**
     * Constructs an empty list of module references.
     */
    public ModuleReferenceList() {
        labels = new ArrayList();
        props = new ArrayList();
        contexts = new ArrayList();
        label2Context = new HashMap();
    }

    /**
     * Add module reference to list.
     *
     * @param   context Within this context.
     * @param   label   Referenced module gets this label. Must not be <code>null</code> or empty.
     * @param   prop    Referenced module has this properties. Must not be <code>null</code>.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    public final void add(final ModuleContext context, final String label, final ModuleProperties
                prop) throws IllegalModuleDataException {
        if (label == null || label.length() <= 0) {
            throw new IllegalModuleDataException(10003, "An label was not defined.", context, null,
                null);  // LATER mime 20071026: organize exception codes
        }
        if (labels.contains(label)) {
            // LATER mime 20071026: organize exception codes
            throw new IllegalModuleDataException(10004, "Label \"" + label
                + "\" defined more than once.", context,
                (ModuleContext) label2Context.get(label), null);
        }
        labels.add(label);
        label2Context.put(label, context);
        contexts.add(context);
        Trace.param(CLASS, "add(ModuleContext, String, ModuleProperties)", "context", context);
        props.add(prop);
    }

    /**
     * Get number of module references.
     *
     * @return  Number of module references.
     */
    public final int size() {
        return labels.size();
    }

    /**
     * Get label for certain module.
     *
     * @param   index   Entry index.
     * @return  Label of module.
     */
    public final String getLabel(final int index) {
        return (String) labels.get(index);
    }

    /**
     * Get properties of referenced module.
     *
     * @param   index   Entry index.
     * @return  Module properties for that module.
     */
    public final ModuleProperties getModuleProperties(final int index) {
        return (ModuleProperties) props.get(index);
    }

    /**
     * Get import context of referenced module.
     *
     * @param   index   Entry index.
     * @return  Context for that module.
     */
    public final ModuleContext getModuleContext(final int index) {
        return (ModuleContext) contexts.get(index);
    }

    /**
     * Get ModuleProperties of referenced module via label. Might be <code>null</code>.
     *
     * @param   label   Label for referenced module or <code>null</code> if not found.
     * @return  Module properties for that module.
     */
    public final ModuleProperties getModuleProperties(final String label) {
        final int index = labels.indexOf(label);
        if (index < 0) {
            return null;
        }
        return (ModuleProperties) props.get(index);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ModuleReferenceList)) {
            return false;
        }
        final ModuleReferenceList otherList = (ModuleReferenceList) obj;
        if (size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!EqualsUtility.equals(getLabel(i), otherList.getLabel(i))
                    || !EqualsUtility.equals(getModuleProperties(i),
                        otherList.getModuleProperties(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < size(); i++) {
            hash = hash ^ (i + 1);
            if (getLabel(i) != null) {
                hash = hash ^ getLabel(i).hashCode();
                hash = hash ^ getModuleProperties(i).hashCode();
            }
        }
        return hash;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer("module reference list:\n");
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            buffer.append((i + 1) + ":\t");
            buffer.append(getLabel(i)).append(": ").append(getModuleProperties(i)).append("\n");
        }
        return buffer.toString();
    }

}
