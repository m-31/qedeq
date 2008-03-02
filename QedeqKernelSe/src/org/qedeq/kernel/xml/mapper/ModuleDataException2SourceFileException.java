/* $Id: ModuleDataException2XmlFileException.java,v 1.4 2008/01/26 12:39:09 m31 Exp $
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

package org.qedeq.kernel.xml.mapper;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;


/**
 * Converts a {@link org.qedeq.kernel.common.ModuleDataException} into a
 * {@link org.qedeq.kernel.common.SourceFileException}.
 *
 * @version $Revision: 1.4 $
 * @author  Michael Meyling
 */
public final class ModuleDataException2SourceFileException {

    /** This class. */
    private static final Class CLASS = ModuleDataException2SourceFileException.class;

    /**
     * Constructor.
     */
    private ModuleDataException2SourceFileException() {
        // nothing to do
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ module.
     * @return  Newly created instance.
     */
    public static final SourceFileExceptionList createSourceFileExceptionList(
            final ModuleDataException exception, final Qedeq qedeq) {
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()), createSourceArea(qedeq, exception.getReferenceContext()));
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList(e);
        return list;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ module.
     * @return  Newly created instance.
     */
    public static final SourceFileException createSourceFileException(final ModuleDataException
            exception, final Qedeq qedeq) {
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()), createSourceArea(qedeq, exception.getReferenceContext()));
        return e;
    }

    /**
     * Get area in XML source file for QEDEQ module context.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public static SourceArea createSourceArea(final Qedeq qedeq, final ModuleContext context) {
        final String method = "createSourceArea(Qedeq, ModuleContext)";
        if (qedeq == null || context == null) {
            return null;
        }
        final String xpath;
        try {
            xpath = Context2SimpleXPath.getXPath(context, qedeq).toString();
        } catch (ModuleDataException e) {
            Trace.trace(CLASS, method, e);
            return null;
        };

        SimpleXPath find = null;
        try {
            find = XPathLocationParser.getXPathLocation(
                KernelContext.getInstance().getLocalFilePath(context.getModuleLocation()),
                xpath,
                context.getModuleLocation().getURL());
            if (find.getStartLocation() == null) {
                return null;
            }
            return new SourceArea(context.getModuleLocation().getURL(), find.getStartLocation(),
                find.getEndLocation());
        } catch (ParserConfigurationException e) {
            Trace.trace(CLASS, method, e);
        } catch (SAXException e) {
            Trace.trace(CLASS, method, e);
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
        }
        return null;
    }



}
