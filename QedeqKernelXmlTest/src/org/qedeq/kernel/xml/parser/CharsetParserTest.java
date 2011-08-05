/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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
package org.qedeq.kernel.xml.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.io.TextInput;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

/**
 * Tests correct charset handling by {@link SaxParser},
 * {@link org.qedeq.kernel.xml.parser.SaxDefaultHandler} (and higher level classes).
 *
 * @author  Michael Meyling
 */
public class CharsetParserTest extends QedeqBoTestCase {

    /**
     * Test parsing with default SAX parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse1() throws Exception {
        final ModuleAddress address = getServices()
            .getModuleAddress(getFile("charset/qedeq_utf8_with_errors_01.xml"));
        getServices().loadModule(address);
        assertFalse(getServices().checkModule(address));
        final String[] errors = getSourceFileExceptionList(address);
//        for (int i = 0; i < errors.length; i++) {
//            System.out.println(errors[i]);
//        }
        assertEquals(2, errors.length);
        String[] lines = errors[0].split("\n");
        assertTrue(lines[0].endsWith(":105:28"));
        assertTrue(lines[1].endsWith("\"\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df\u00e8\u00e9"
            + "\u00ea\u00eb\u00c8\u00c9\u00ca\u00cb\u20ac\" [2]"));
    }

    /**
     * Test parsing with default SAX parser.
     *
     * @throws  Exception   Something bad happened.
     */
    public void testParse2() throws Exception {
        final ModuleAddress address = getServices()
            .getModuleAddress(getFile("charset/qedeq_utf16_with_errors_01.xml"));
        getServices().loadModule(address);
        assertFalse(getServices().checkModule(address));
        final String[] errors = getSourceFileExceptionList(address);
        assertEquals(2, errors.length);
        String[] lines = errors[0].split("\n");
        assertTrue(lines[0].endsWith(":105:28"));
        assertTrue(lines[1].endsWith("\"\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df\u00e8\u00e9"
            + "\u00ea\u00eb\u00c8\u00c9\u00ca\u00cb\u20ac\" [2]"));
    }

    /**
     * Get description of source file exception list.
     *
     * @param   address  Get description for this module exceptions.
     * @return  Error description and location.
     * @throws  IOException Reading failed.
     */
    public String[] getSourceFileExceptionList(final ModuleAddress address) throws IOException {
        final List list = new ArrayList();
        final SourceFileExceptionList sfl = getServices().getQedeqBo(address)
            .getErrors();
        if (sfl != null) {
            final StringBuffer buffer
                = new StringBuffer(getServices().getSource(address));
            final TextInput input = new TextInput(buffer);
            input.setPosition(0);
            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < sfl.size(); i++) {
                buf.setLength(0);
                final SourceFileException sf = sfl.get(i);
                buf.append(sf.getDescription());
                if (sf.getSourceArea() != null && sf.getSourceArea().getStartPosition()
                        != null) {
                    buf.append("\n");
                    input.setRow(sf.getSourceArea().getStartPosition().getRow());
                    buf.append(StringUtility.replace(input.getLine(), "\t", " "));
                    buf.append("\n");
                    final StringBuffer whitespace = StringUtility.getSpaces(
                        sf.getSourceArea().getStartPosition().getColumn() - 1);
                    buffer.append(whitespace);
                    buffer.append("^");
                }
                list.add(buf.toString());
            }
        }
        return (String[]) list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }


}
