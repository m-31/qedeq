/* $Id: AbstractModuleVisitor.java,v 1.2 2007/02/25 20:05:36 m31 Exp $
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

package org.qedeq.kernel.bo.control;

import org.qedeq.kernel.bo.visitor.AbstractModuleVisitor;
import org.qedeq.kernel.bo.visitor.QedeqNotNullTraverser;
import org.qedeq.kernel.common.DefaultSourceFileExceptionList;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceFileException;


/**
 * Basic visitor that gives some error collecting features. Also hides the
 * traverser that does the work.
 *
 * @version $Revision: 1.2 $
 * @author Michael Meyling
 */
public abstract class ControlVisitor extends AbstractModuleVisitor {

    /** QEDEQ BO object to work on. */
    private final KernelQedeqBo prop;

    /** Traverse QEDEQ module with this traverser. */
    private final QedeqNotNullTraverser traverser;

    /** List of Exceptions during Module load. */
    private DefaultSourceFileExceptionList errorList;


    /**
     * Constructor.
     *
     * @param   prop        Internal QedeqBo.
     */
    protected ControlVisitor(final KernelQedeqBo prop) {
        if (prop.getQedeq() == null) {
            throw new NullPointerException("Programming error, Module not loaded: "
                + prop.getModuleAddress());
        }
        this.prop = prop;
        this.traverser = new QedeqNotNullTraverser(prop.getModuleAddress(), this);
    }

    /**
     * Get QedeqBo.
     *
     * @return  QedeqBo.
     */
    protected KernelQedeqBo getQedeqBo() {
        return this.prop;
    }

    /**
     * Start traverse of QedeqBo. If during the traverse a {@link ModuleDataException}
     * occurs it is thrown till high level and transformed into a
     * {@link DefaultSourceFileExceptionList}. Otherwise all collected exceptions
     * (via {@link #addModuleDataException(ModuleDataException)} and
     * {@link #addSourceFileException(SourceFileException)}) are thrown.
     *
     * @throws  DefaultSourceFileExceptionList  All collected exceptions.
     */
    protected void traverse() throws DefaultSourceFileExceptionList {
        try {
            this.traverser.accept(this.prop.getQedeq());
        } catch (ModuleDataException me) {
            addModuleDataException(me);
        }
        if (errorList != null) {
            throw errorList;
        }
    }

    protected ModuleContext getCurrentContext() {
        return this.traverser.getCurrentContext();
    }

    /**
     * Get list of exceptions that occurred during loading referenced modules.
     *
     * @return  Exception list.
     */
    public DefaultSourceFileExceptionList getSourceFileExceptionList() {
        return errorList;
    }

    /**
     * Add exception to error collection.
     *
     * @param   me  Exception to be added.
     */
    protected void addModuleDataException(final ModuleDataException me) {
        addSourceFileException(prop.createSourceFileException(me));
    }

    /**
     * Add exception to error collection.
     *
     * @param   sf  Exception to be added.
     */
    protected void addSourceFileException(final SourceFileException sf) {
            if (errorList == null) {
                errorList = new DefaultSourceFileExceptionList(sf);
            } else {
                errorList.add(sf);
            }
    }

    /**
     * Set if further traverse is blocked.
     *
     * @param   blocked     Further traverse blocked?
     */
    protected void setBlocked(final boolean blocked) {
        traverser.setBlocked(blocked);
    }

}
