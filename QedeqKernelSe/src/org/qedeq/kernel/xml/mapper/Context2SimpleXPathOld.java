/* $Id: Context2SimpleXPathOld.java,v 1.3 2007/12/21 23:33:47 m31 Exp $
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

import org.qedeq.kernel.bo.module.ModuleContext;
import org.qedeq.kernel.context.KernelContext;
import org.qedeq.kernel.xml.tracker.SimpleXPath;
import org.qedeq.kernel.xml.tracker.XPathLocationParser;
import org.xml.sax.SAXException;

/**
 * Parses complete QEDEQ modules to find a context location..
 *
 * <p>
 * Not in use any more.
 *
 * @version $Revision: 1.3 $
 * @author Michael Meyling
 * @deprecated
 */
public final class Context2SimpleXPathOld {

    /**
     * Constructor.
     */
    private Context2SimpleXPathOld() {
        // nothing to do
    }

    /**
     * Transform context information into XML specific location information.
     *
     * @param   context Abstract context description.
     * @return  Information where the context could be found.
     * @throws  RuntimeException    Don't!
     * @deprecated
     */
    public static final SimpleXPath getXPath(final ModuleContext context) {
        String xpath = Context2XPathOld.getXPath(context);
        try {
            final SimpleXPath find = XPathLocationParser.getXPathLocation(
                // FIXME mime 20071218: hard coded transformation of context into file location
                KernelContext.getInstance().getLocalFilePath(
                    context.getModuleLocation()), xpath, context.getModuleLocation().getURL());
            if (find.getStartLocation() == null) {
                System.out.println(context);
                throw new RuntimeException("start not found: " + find);
            }
            if (find.getEndLocation() == null) {
                System.out.println(context);
                throw new RuntimeException("end not found: " + find);
            }
            return find;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
