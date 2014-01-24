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

package org.qedeq.kernel.bo.service.unicode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.base.io.IoUtility;
import org.qedeq.base.io.Parameters;
import org.qedeq.base.io.UrlUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.module.InternalModuleServiceCall;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * Test generating UTF-8 files for all known samples and scripts.
 * LATER 20110215 m31: perhaps support more LaTeX commands for unicode generation
 *
 * @author Michael Meyling
 */
public class Qedeq2UnicodeTextExecutorTest extends QedeqBoTestCase {

    /** This class. */
    private static final Class CLASS = Qedeq2UnicodeTextExecutorTest.class;
    private InternalModuleServiceCall call;

    protected void tearDown() throws Exception {
        endServiceCall(call);
        super.tearDown();
    }

    public void testGeneration1() throws Exception {
        try {
            generate(getDocDir(), "math/qedeq_logic_v1.xml", false);
        } catch (SourceFileExceptionList e) {
            assertEquals(4, e.size());
        }
    }

    public void testGeneration1b() throws Exception {
        generate(getDocDir(), "math/qedeq_formal_logic_v1.xml", false);
    }

    public void testGeneration2() throws Exception {
        try {
            generate(getDocDir(), "sample/qedeq_sample1.xml", false);
        } catch (SourceFileExceptionList e) {
            assertEquals(4, e.size());
        }
    }

    public void testGeneration3() throws Exception {
        generate(getDocDir(), "sample/qedeq_sample2.xml", false);
    }

    public void testGeneration3b() throws Exception {
        generate(getDocDir(), "sample/qedeq_sample3.xml", false);
    }

    public void testGeneration3c() throws Exception {
        generate(getDocDir(), "sample/qedeq_sample4.xml", false);
    }

    public void testGeneration4() throws Exception {
        generate(getDocDir(), "math/qedeq_set_theory_v1.xml", false);
    }

    public void testGeneration5() throws Exception {
        try {
            generate(getDocDir(), "project/qedeq_basic_concept.xml", false);
        } catch (SourceFileExceptionList e) {
            assertEquals(14, e.size());
        }
    }

    public void testGeneration6() throws Exception {
        try {
            generate(getDocDir(), "project/qedeq_logic_language.xml", true);
        } catch (SourceFileExceptionList e) {
//            System.out.println(e);
            assertEquals(8, e.size());
        }
    }

    /**
     * Call the generation of one UTF-8 file and copy XML source to same destination directory for
     * all supported languages.
     *
     * @param dir Start directory.
     * @param xml Relative path to XML file. Must not be <code>null</code>.
     * @param onlyEn Generate only for language "en".
     * @throws Exception Failure.
     */
    public void generate(final File dir, final String xml, final boolean onlyEn) throws Exception {
    	final File destinationDirectory = new File(getGenDir(), "doc");
        final SourceFileExceptionList sfe = new SourceFileExceptionList();
        try {
            generate(dir, xml, "en", destinationDirectory);
        } catch (SourceFileExceptionList e) {
            sfe.add(e);
        }
        if (!onlyEn) {
            try {
                generate(dir, xml, "de", destinationDirectory);
            } catch (SourceFileExceptionList e) {
                sfe.add(e);
            }
        } else {
            try {
                generate(dir, xml, "xx", destinationDirectory);
            } catch (SourceFileExceptionList e) {
                sfe.add(e);
            }
        }
        if (sfe.size() > 0) {
            throw sfe;
        }
    }

    /**
     * Call the generation of one UTF-8 file and copy XML source to same destination directory.
     *
     * @param dir Start directory.
     * @param xml Relative path to XML file. Must not be <code>null</code>.
     * @param language Generate text in this language. Can be <code>null</code>.
     * @param destinationDirectory Directory path for LaTeX file. Must not be <code>null</code>.
     */
    public void generate(final File dir, final String xml, final String language,
            final File destinationDirectory) throws IOException, SourceFileExceptionList, InterruptException {
        final File xmlFile = new File(dir, xml);
        final ModuleAddress address = getServices().getModuleAddress(
            UrlUtility.toUrl(xmlFile));
        final KernelQedeqBo prop = (KernelQedeqBo) getServices().loadModule(
            address);
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().loadRequiredModules(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        getServices().checkWellFormedness(prop.getModuleAddress());
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }
        if (prop.hasErrors()) {
            throw prop.getErrors();
        }

        final String web = "http://www.qedeq.org/"
            + getInternalServices().getKernelVersionDirectory() + (!xml.startsWith("sample") ? "/doc/" : "/") + xml;
        final ModuleAddress webAddress = new DefaultModuleAddress(web);
        getInternalServices().getLocalFilePath(webAddress);
        IoUtility.copyFile(xmlFile, getInternalServices().getLocalFilePath(webAddress));

        getServices().checkWellFormedness(webAddress);
        final QedeqBo webBo = getServices().getQedeqBo(webAddress);
        final File utfFile = new File(destinationDirectory, xml.substring(0, xml.lastIndexOf('.'))
            + "_" + language + ".utf8");
        System.out.println(utfFile);
        generate((KernelQedeqBo) webBo, utfFile, language, "1", false);
        final File utfFileB = new File(destinationDirectory, xml.substring(0, xml.lastIndexOf('.'))
                + "_" + language + ".b.utf8");
        System.out.println(utfFileB);
        generate((KernelQedeqBo) webBo, utfFileB , language, "1", true);
//        final File utfCopy = new File(dir, new File(new File(xml).getParent(), utfFile.getName())
//            .getPath());
//        IoUtility.copyFile(utfFile, utfCopy);
        final File xmlCopy = new File(destinationDirectory, xml);
        IoUtility.copyFile(xmlFile, xmlCopy);
        if (webBo.hasErrors()) {
            throw webBo.getErrors();
        }
        if (webBo.hasWarnings()) {
            throw webBo.getWarnings();
        }
    }

    /**
     * Generate LaTeX file out of XML file.
     *
     * @param 	prop 		Take this QEDEQ module.
     * @param 	to 			Write to this file. Could be <code>null</code>.
     * @param 	language 	Resulting language. Could be <code>null</code>.
     * @param 	level 		Resulting detail level. Could be <code>null</code>.
     * @param 	brief		Only brief node output.
     * @return File name of generated LaTeX file.
     * @throws SourceFileExceptionList Something went wrong.
     */
    public String generate(final KernelQedeqBo prop, final File to, final String language,
            final String level, final boolean brief) throws SourceFileExceptionList, InterruptException {
        final String method = "generate(String, String, String, String)";
        try {
            Trace.begin(CLASS, method);
            Trace.param(CLASS, method, "prop", prop);
            Trace.param(CLASS, method, "to", to);
            Trace.param(CLASS, method, "language", language);
            Trace.param(CLASS, method, "level", level);
            final Map parameters = new HashMap();
            parameters.put("info", "false");
            parameters.put("brief", Boolean.toString(brief));
            parameters.put("maximumColumn", "80");
            call = createServiceCall("generate utf8", prop);
            final String source =(new Qedeq2UnicodeTextExecutor(new Qedeq2Utf8Plugin(), prop, new Parameters(parameters)))
                .generateUtf8(call.getInternalServiceProcess(), language, "1");
            if (to != null) {
                IoUtility.createNecessaryDirectories(to);
                IoUtility.saveFile(to, source, "UTF-8");
                return to.getCanonicalPath();
            }
            return prop.getName();
        } catch (IOException e) {
            Trace.trace(CLASS, method, e);
            throw new RuntimeException(e);
        } finally {
            endServiceCall(call);
            Trace.end(CLASS, method);
        }
    }

}
