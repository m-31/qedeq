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

package org.qedeq.kernel.bo.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.IllegalModuleDataException;
import org.qedeq.kernel.se.common.ModuleContext;


/**
 * Represents a reference list of modules. Every entry has a symbolic name for one referenced QEDEQ
 * module. This module label acts as a prefix for all references to that module. The module label
 * must be an unique String.
 *
 * @author  Michael Meyling
 */
public class KernelModuleReferenceList implements ModuleReferenceList {

    /** This class. */
    private static final Class CLASS = KernelModuleReferenceList.class;

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
    public KernelModuleReferenceList() {
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
     * @throws  IllegalModuleDataException  The <code>label</code> is empty or <code>null</code>.
     */
    public void add(final ModuleContext context, final String label, final QedeqBo prop)
            throws IllegalModuleDataException {
        if (label == null || label.length() <= 0) {
            throw new IllegalModuleDataException(10003, "An label was not defined.",
                new ModuleContext(context), null,
                null);  // LATER mime 20071026: organize exception codes
        }
        final ModuleContext con = new ModuleContext(context);
        labels.add(label);
        label2Context.put(label, con);
        contexts.add(con);
        Trace.param(CLASS, "add(ModuleContext, String, QedeqBo)", "context", con);
        props.add(prop);
    }

    /**
     * Add module reference to list.
     *
     * @param   context Within this context.
     * @param   label   Referenced module gets this label. Must not be <code>null</code> or empty.
     * @param   prop    Referenced module has this properties. Must not be <code>null</code>.
     * @throws  IllegalModuleDataException  The <code>id</code> already exists or is
     *          <code>null</code>. Also if <code>label</code> is empty or <code>null</code>.
     */
    public void addLabelUnique(final ModuleContext context, final String label,
            final QedeqBo prop) throws IllegalModuleDataException {
        if (labels.contains(label)) {
            // LATER mime 20071026: organize exception codes
            throw new IllegalModuleDataException(10004, "Label \"" + label
                + "\" defined more than once.", new ModuleContext(context), // use copy constructor!
                (ModuleContext) label2Context.get(label), null);
        }
        add(new ModuleContext(context), label, prop);
    }

    public int size() {
        return labels.size();
    }

    public String getLabel(final int index) {
        return (String) labels.get(index);
    }

    public QedeqBo getQedeqBo(final int index) {
        return (QedeqBo) props.get(index);
    }

    /**
     * Get {@link QedeqBo} of referenced module.
     *
     * @param   index   Entry index.
     * @return  Module properties for that module.
     */
    public KernelQedeqBo getKernelQedeqBo(final int index) {
        return (KernelQedeqBo) props.get(index);
    }

    public ModuleContext getModuleContext(final int index) {
        return (ModuleContext) contexts.get(index);
    }

    public QedeqBo getQedeqBo(final String label) {
        final int index = labels.indexOf(label);
        if (index < 0) {
            return null;
        }
        return (QedeqBo) props.get(index);
    }

    /**
     * Get KernelQedeqBo of referenced module via label. Might be <code>null</code>.
     *
     * @param   label   Label for referenced module or <code>null</code> if not found.
     * @return  QEQDEQ BO.
     */
    public KernelQedeqBo getKernelQedeqBo(final String label) {
        final int index = labels.indexOf(label);
        if (index < 0) {
            return null;
        }
        return (KernelQedeqBo) props.get(index);
    }

    /**
     * Is the given QEDEQ BO already in this list?
     *
     * @param   bo  QEDEQ BO.
     * @return  Already in list?
     */
    public boolean contains(final KernelQedeqBo bo) {
        return props.contains(bo);
    }

    /**
     * Delete a given QEDEQ BO already from list.
     *
     * @param   bo  QEDEQ BO.
     */
    public void remove(final KernelQedeqBo bo) {
        int index;
        while (0 <= (index = props.indexOf(bo))) {
            final String label = (String) labels.get(index);
            label2Context.remove(label);
            props.remove(index);
            labels.remove(index);
            contexts.remove(index);
        }
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof KernelModuleReferenceList)) {
            return false;
        }
        final ModuleReferenceList otherList = (ModuleReferenceList) obj;
        if (size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!EqualsUtility.equals(getLabel(i), otherList.getLabel(i))
                    || !EqualsUtility.equals(getQedeqBo(i),
                        otherList.getQedeqBo(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Empty reference list.
     */
    public void clear() {
        labels.clear();
        props.clear();
        contexts.clear();
        label2Context.clear();
    }

    /**
     * Copy all list entry references of <code>list</code> to this instance.
     *
     * @param   list    Copy from here.
     */
    public void set(final KernelModuleReferenceList list) {
        clear();
        this.labels.addAll(list.labels);
        this.props.addAll(list.props);
        this.contexts.addAll(list.contexts);
        this.label2Context.putAll(list.label2Context);
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < size(); i++) {
            hash = hash ^ (i + 1);
            if (getLabel(i) != null) {
                hash = hash ^ getLabel(i).hashCode();
                hash = hash ^ getQedeqBo(i).hashCode();
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
            buffer.append(getLabel(i)).append(": ").append(getQedeqBo(i)).append("\n");
        }
        return buffer.toString();
    }

}
