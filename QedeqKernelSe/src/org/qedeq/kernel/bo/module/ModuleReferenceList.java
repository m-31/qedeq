/* $Id: ModuleReferenceList.java,v 1.1 2007/12/21 23:33:46 m31 Exp $
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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.kernel.utility.EqualsUtility;


/**
 * Represents a reference list of modules. Every entry has a symbolic name for one referenced QEDEQ
 * module. This module label acts as a prefix for all references to that module. The module label
 * must be an unique String.
 *
 * @version $Revision: 1.1 $
 * @author  Michael Meyling
 */
public class ModuleReferenceList {

    /** Contains all labels. */
    private final List labels;

    /** Contains all module addresses. */
    private final List addresses;

    /** Maps labels to context. */
    private final Map label2Context;

    /**
     * Constructs an empty list of module references.
     */
    public ModuleReferenceList() {
        labels = new ArrayList();
        addresses = new ArrayList();
        label2Context = new HashMap();
    }

    /**
     * Add module reference to list.
     *
     * @param   context Within this context.
     * @param   label   Referenced module gets this label. Must not be <code>null</code> or empty.
     * @param   address Referenced module has this URL. Must not be <code>null</code>.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>.
     */
    public final void add(final ModuleContext context, final String label, final URL address)
            throws IllegalModuleDataException {
        if (label == null || label.length() <= 0 || address == null) {
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
        addresses.add(address);
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
     * Get URL of referenced module.
     *
     * @param   index   Entry index.
     * @return  URL for that module.
     */
    public final URL getUrl(final int index) {
        return (URL) addresses.get(index);
    }

    /**
     * Get URL of referenced module via label.
     *
     * @param   context Within this context.
     * @param   label   Label for referenced module.
     * @return  URL for that module.
     * @throws  IllegalModuleDataException  Label not found.
     */
    public final URL getUrl(final ModuleContext context, final String label)
            throws IllegalModuleDataException {
        final int index = labels.indexOf(label);
        if (index < 0) {
            throw new IllegalModuleDataException(10005, "This module label \"" + label
                + "\"was not found.", context, null, null);
                // LATER mime 20071026: organize exception codes
        }
        return (URL) addresses.get(index);
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
                    || !EqualsUtility.equals(getUrl(i), otherList.getUrl(i))) {
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
                hash = hash ^ getUrl(i).hashCode();
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
            buffer.append(getLabel(i)).append(": ").append(getUrl(i)).append("\n");
        }
        return buffer.toString();
    }

}
