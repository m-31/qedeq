/* $Id: ModuleDataException2XmlFileException.java,v 1.2 2007/05/10 00:37:52 m31 Exp $
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
import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.bo.module.ModuleDataException;
import org.qedeq.kernel.common.SourceArea;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;
import org.qedeq.kernel.trace.Trace;
import org.qedeq.kernel.xml.parser.DefaultSourceFileExceptionList;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationFinder;
import org.xml.sax.SAXException;


/**
 * Converts a {@link org.qedeq.kernel.bo.module.ModuleDataException} into a
 * {@link org.qedeq.kernel.common.SourceFileException}.
 *
 * @version $Revision: 1.2 $
 * @author  Michael Meyling
 */
public final class ModuleDataException2XmlFileException {

    /**
     * Constructor.
     */
    private ModuleDataException2XmlFileException() {
        // nothing to do
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ module.
     * @return  Newly created instance.
     */
    public static final SourceFileExceptionList createXmlFileExceptionList(final ModuleDataException
            exception, final Qedeq qedeq) {
        final DefaultSourceFileExceptionList list = new DefaultSourceFileExceptionList();
        final SourceFileException e = new SourceFileException(exception, createSourceArea(qedeq,
            exception.getContext()),
            createSourceArea(qedeq, exception.getReferenceContext()));
        list.add(e);
        return list;
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
            Trace.trace(SourceFileException.class, method, e);
            return null;
        };

        SimpleXPath find = null;
        try {
            find = XPathLocationFinder.getXPathLocation(
                context.getModuleLocation(), xpath);
            if (find.getStartLocation() == null) {
                return null;
            }
            return new SourceArea(find.getStartLocation().getAddress(), find.getStartLocation(),
                find.getEndLocation());
        } catch (ParserConfigurationException e) {
            Trace.trace(SourceFileException.class, method, e);
        } catch (SAXException e) {
            Trace.trace(SourceFileException.class, method, e);
        } catch (IOException e) {
            Trace.trace(SourceFileException.class, method, e);
        }
        return null;
    }



}
