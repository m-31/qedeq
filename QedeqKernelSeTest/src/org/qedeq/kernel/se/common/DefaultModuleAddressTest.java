/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.se.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.qedeq.base.io.Path;
import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.se.dto.module.LocationListVo;
import org.qedeq.kernel.se.dto.module.LocationVo;
import org.qedeq.kernel.se.dto.module.SpecificationVo;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class DefaultModuleAddressTest extends QedeqTestCase {

    private DefaultModuleAddress dflt;

    public DefaultModuleAddressTest(){
        super();
    }

    public DefaultModuleAddressTest(final String name){
        super(name);
    }

    /**
     * Test constructor.
     */
    public void testConstructor() throws Exception {
        try {
            dflt = new DefaultModuleAddress("memory");
        } catch (MalformedURLException e) {
            // ok
        }
        dflt = new DefaultModuleAddress();
        assertEquals("default", dflt.getFileName());
        assertEquals("memory:", dflt.getHeader());
        assertEquals("default", dflt.getName());
        assertEquals("", dflt.getPath());
        assertEquals("memory://default", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress(true, "uzsdaf234");
        assertEquals("uzsdaf234", dflt.getFileName());
        assertEquals("memory:", dflt.getHeader());
        assertEquals("uzsdaf234", dflt.getName());
        assertEquals("", dflt.getPath());
        assertEquals("memory://uzsdaf234", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress("hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("file://", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("", dflt.getPath());
        assertEquals("file://hulouo.xml", dflt.getUrl());
        assertEquals(true, dflt.isFileAddress());
        assertEquals(true, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress("file:hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("file://", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("", dflt.getPath());
        assertEquals("file://hulouo.xml", dflt.getUrl());
        assertEquals(true, dflt.isFileAddress());
        assertEquals(true, dflt.isRelativeAddress());
        dflt = new DefaultModuleAddress("file:///root/hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("file://", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("/root/", dflt.getPath());
        assertEquals("file:///root/hulouo.xml", dflt.getUrl());
        assertEquals(true, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress("http:hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("http:", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("", dflt.getPath());
        assertEquals("http:hulouo.xml", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(true, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress("http://unknown.com/hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("http://unknown.com", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("/", dflt.getPath());
        assertEquals("http://unknown.com/hulouo.xml", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress("ftp://asmith@ftp.example.org/xx/hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("ftp://asmith@ftp.example.org", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("/xx/", dflt.getPath());
        assertEquals("ftp://asmith@ftp.example.org/xx/hulouo.xml", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
        dflt = new DefaultModuleAddress(true, "i am only a memory");
        assertFalse(dflt.isFileAddress());
        assertFalse(dflt.isRelativeAddress());
        try {
            new DefaultModuleAddress(false, "i am only a memory");
            fail("Exception expected");
        } catch (Exception e) {
        }
        try {
            new DefaultModuleAddress(true, null);
            fail("Exception expected");
        } catch (Exception e) {
        }
        try {
            new DefaultModuleAddress((File) null);
            fail("Exception expected");
        } catch (Exception e) {
        }
        try {
            new DefaultModuleAddress((String) null);
            fail("Exception expected");
        } catch (Exception e) {
        }
        try {
            new DefaultModuleAddress((URL) null);
            fail("Exception expected");
        } catch (Exception e) {
        }
        try {
            new DefaultModuleAddress(null, null);
            fail("Exception expected");
        } catch (Exception e) {
        }
    }

    /**
     * Test hash code generation.
     */
    public void testHashCode() throws Exception {
        final String url = "ftp://asmith@ftp.example.org/xx/hulouo.xml";
        dflt = new DefaultModuleAddress(url);
        assertEquals(url.hashCode(), dflt.hashCode());
    }

    public void testDefaultModuleAddressURL() throws Exception {
        dflt = new DefaultModuleAddress(new URL("http://unknown.com/hulouo.xml"));
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("http://unknown.com", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("/", dflt.getPath());
        assertEquals("http://unknown.com/hulouo.xml", dflt.getUrl());
        assertEquals(false, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
    }

    public void testDefaultModuleAddressFile() throws Exception {
        final File file = new File("unknown/hulouo.xml");
        dflt = new DefaultModuleAddress(file);
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("file://", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals((SystemUtils.IS_OS_WINDOWS ? "/" : "") + FilenameUtils.separatorsToUnix(
            file.getCanonicalFile().getParentFile().getPath()) + "/", dflt.getPath());
        assertEquals("file://" + (SystemUtils.IS_OS_WINDOWS ? "/" : "")
            + FilenameUtils.separatorsToUnix(
            file.getCanonicalPath()), dflt.getUrl());
        assertEquals(true, dflt.isFileAddress());
        assertEquals(false, dflt.isRelativeAddress());
        assertEquals(dflt, dflt.createModuleContext().getModuleLocation());
    }

    public void testCreateModuleContext() throws Exception {
        dflt = new DefaultModuleAddress("unknown/hulouo.xml");
        assertEquals("hulouo.xml", dflt.getFileName());
        assertEquals("file://", dflt.getHeader());
        assertEquals("hulouo", dflt.getName());
        assertEquals("unknown/", dflt.getPath());
        assertEquals("file://unknown/hulouo.xml", dflt.getUrl());
        assertEquals(true, dflt.isFileAddress());
        assertEquals(true, dflt.isRelativeAddress());
        final ModuleContext context = dflt.createModuleContext(); 
        assertEquals(dflt, context.getModuleLocation());
        assertEquals("", context.getLocationWithinModule());
        assertEquals(null, context.getStartDelta());
        assertEquals(null, context.getEndDelta());
    }

    public void testEqualsObject() throws Exception {
        final String url = "ftp://asmith@ftp.example.org/xx/hulouo.xml";
        dflt = new DefaultModuleAddress(url);
        final DefaultModuleAddress second = new DefaultModuleAddress(url);
        assertEquals(dflt, second);
        final DefaultModuleAddress third = new DefaultModuleAddress("ftp://asmith@ftp.example.org/xy/hulouo.xml");
        assertFalse(EqualsUtility.equals(dflt, third));
        assertFalse(EqualsUtility.equals(url, dflt));
        assertFalse(dflt.equals(null));
    }

    public void testToString() throws Exception {
        final String url = "ftp://asmith@ftp.example.org/xx/hulouo.xml";
        dflt = new DefaultModuleAddress(url);
        assertEquals(url, dflt.toString());
    }

    public void testGetModulePaths1() throws Exception {
        dflt = new DefaultModuleAddress("http://logic.org/unknown/hulouo.xml");
        ModuleAddress[] r = dflt.getModulePaths(new SpecificationVo());
        assertEquals(0, r.length);
        LocationListVo list = new LocationListVo();
        list.add(new LocationVo("new"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
            list));
        ModuleAddress n = new DefaultModuleAddress("http://logic.org/unknown/new/hulouo.xml");
        assertEquals(1, r.length);
        assertEquals(n, r[0]);
        list.add(new LocationVo("../now"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
                list));
        n = new DefaultModuleAddress("http://logic.org/now/hulouo.xml");
        assertEquals(2, r.length);
        assertEquals(n, r[1]);
    }

    public void testGetModulePaths2() throws Exception {
        dflt = new DefaultModuleAddress("file:/unknown/hulouo.xml");
        ModuleAddress[] r = dflt.getModulePaths(new SpecificationVo());
        assertEquals(0, r.length);
        LocationListVo list = new LocationListVo();
        list.add(new LocationVo("new"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
            list));
        ModuleAddress n = new DefaultModuleAddress("file:/unknown/new/hulouo.xml");
        assertEquals(1, r.length);
        assertEquals(n, r[0]);
        list.add(new LocationVo("../now"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
                list));
        n = new DefaultModuleAddress("file:/now/hulouo.xml");
        assertEquals(2, r.length);
        assertEquals(n, r[1]);
    }

    // should also work with relative addresses!
    public void testGetModulePaths3() throws Exception {
        dflt = new DefaultModuleAddress("unknown/hulouo.xml");
        ModuleAddress[] r = dflt.getModulePaths(new SpecificationVo());
        assertEquals(0, r.length);
        LocationListVo list = new LocationListVo();
        list.add(new LocationVo("new"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
            list));
        ModuleAddress n = new DefaultModuleAddress("unknown/new/hulouo.xml");
        assertEquals(1, r.length);
        assertEquals(n, r[0]);
        list.add(new LocationVo("../now"));
        r = dflt.getModulePaths(new SpecificationVo("hulouo", "1.00.00",
                list));
        n = new DefaultModuleAddress("now/hulouo.xml");
        assertEquals(2, r.length);
        assertEquals(n, r[1]);
    }

    public void testCreateRelativeAddress() {
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("http://hi/ti", "http://hi/ho"));
        assertEquals("ho.xml", DefaultModuleAddress.createRelativeAddress("http://hi/tic.xml", "http://hi/ho.xml"));
        assertEquals("http://gu/hi/ho.xml", DefaultModuleAddress.createRelativeAddress("http://go/hi/tic.xml", "http://gu/hi/ho.xml"));
        assertEquals("../ho.xml", DefaultModuleAddress.createRelativeAddress("http://go/hi/tic.xml", "http://go/ho.xml"));
        assertEquals("/../now/hulouo.xml", (new Path("/../now/hulouo.xml")).toString());
        assertEquals("", DefaultModuleAddress.createRelativeAddress("http://hi/ti", "http://hi/ti"));
    }

    public void testCreateRelativeAddress2() {
        assertEquals("hi/ho", DefaultModuleAddress.createRelativeAddress("hi/ti", "hi/ho"));
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("/hi", "/ho"));
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("/ti/hi", "/ti/ho"));
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("hi", "ho"));
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("hi/ti", "ho"));
        assertEquals("hi/ti/hu", DefaultModuleAddress.createRelativeAddress("hi/ti/ta/ten", "hi/ti/hu"));
        assertEquals("ho", DefaultModuleAddress.createRelativeAddress("hi/ho", "ho"));
    }

}
