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
package org.qedeq.kernel.bo.service.logic;

import java.io.File;

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.SourceFileException;

/**
 * Test the formal proof checker plugin.
 *
 * @author Michael Meyling
 */
public class FormalProofCheckerPluginTest extends QedeqBoTestCase {

    public FormalProofCheckerPluginTest() {
        super();
    }

    public FormalProofCheckerPluginTest(final String name) {
        super(name);
    }

    private String getArea(final String text) {
        if (text == null) {
            return "";
        }
        final int p = text.indexOf(".xml:");
        if (p < 0) {
            return text;
        }
        int n = text.indexOf("\n");
        if (n < 0) {
            n = text.length();
        }
        return text.substring(p + ".xml:".length(), n);

    }
    private String getArea(final SourceFileException e) {
        return getArea(e.toString());
    }

    public void testPlugin() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        getServices().checkFormallyProved(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
    }

    public void testPlugin2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getIndir(),
            "proof/proof_001.xml"));
        getServices().checkWellFormedness(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        getServices().checkFormallyProved(address);
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(16, bo.getErrors().size());
//        System.out.println("testPlugin2");
//        bo.getErrors().printStackTrace(System.out);
        assertEquals(37140, bo.getErrors().get(0).getErrorCode());
        assertEquals("1067:32:1067:38", getArea(bo.getErrors().get(0)));
        assertEquals(37140, bo.getErrors().get(1).getErrorCode());
        assertEquals("1133:25:1135:35", getArea(bo.getErrors().get(1)));
        assertEquals(37180, bo.getErrors().get(2).getErrorCode());
        assertEquals("1353:33:1353:41", getArea(bo.getErrors().get(2)));
        assertEquals(37150, bo.getErrors().get(3).getErrorCode());
        assertEquals("1461:20:1461:30", getArea(bo.getErrors().get(3)));
        assertEquals(37240, bo.getErrors().get(4).getErrorCode());
        assertEquals("979:13:1002:23", getArea(bo.getErrors().get(4)));
        assertEquals(37150, bo.getErrors().get(5).getErrorCode());
        assertEquals("1945:20:1945:30", getArea(bo.getErrors().get(5)));
        assertEquals(37140, bo.getErrors().get(6).getErrorCode());
        assertEquals("1960:25:1966:34", getArea(bo.getErrors().get(6)));
        assertEquals(37150, bo.getErrors().get(7).getErrorCode());
        assertEquals("1974:20:1974:30", getArea(bo.getErrors().get(7)));
        assertEquals(37140, bo.getErrors().get(8).getErrorCode());
        assertEquals("1987:23:1996:32", getArea(bo.getErrors().get(8)));
        assertEquals(37240, bo.getErrors().get(9).getErrorCode());
        assertEquals("1497:13:1520:23", getArea(bo.getErrors().get(9)));
        assertEquals(37150, bo.getErrors().get(10).getErrorCode());
        assertEquals("2151:20:2151:29", getArea(bo.getErrors().get(10)));
        assertEquals(37240, bo.getErrors().get(11).getErrorCode());
        assertEquals("2040:13:2057:23", getArea(bo.getErrors().get(11)));
        assertEquals(37140, bo.getErrors().get(12).getErrorCode());
        assertEquals("2233:30:2233:36", getArea(bo.getErrors().get(12)));
        assertEquals(37140, bo.getErrors().get(13).getErrorCode());
        assertEquals("2282:27:2284:36", getArea(bo.getErrors().get(13)));
        assertEquals(37200, bo.getErrors().get(14).getErrorCode());
        assertEquals("2310:21:2329:28", getArea(bo.getErrors().get(14)));
        assertEquals(37240, bo.getErrors().get(15).getErrorCode());
        assertEquals("2186:13:2201:23", getArea(bo.getErrors().get(15)));
    }

    public void testPlugin3() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getIndir(),
            "proof/proof_002.xml"));
        final boolean locationFailures = "true".equalsIgnoreCase(
            System.getProperty("qedeq.test.xmlLocationFailures", "false"));
        System.setProperty("qedeq.test.xmlLocationFailures", "false");
        try {
            getServices().checkWellFormedness(address);
            final QedeqBo bo = getServices().getQedeqBo(address);
            assertTrue(bo.isWellFormed());
            assertEquals(0, bo.getWarnings().size());
            assertEquals(0, bo.getErrors().size());
            getServices().checkFormallyProved(address);
            assertTrue(bo.isWellFormed());
            assertEquals(0, bo.getWarnings().size());
    //        System.out.println("testPlugin3");
    //        bo.getErrors().printStackTrace(System.out);
            assertEquals(37220, bo.getErrors().get(0).getErrorCode());
            assertEquals("522:19:522:24", getArea(bo.getErrors().get(0)));
            assertEquals(37210, bo.getErrors().get(1).getErrorCode());
            assertEquals("535:19:537:35", getArea(bo.getErrors().get(1)));
            assertEquals(37130, bo.getErrors().get(2).getErrorCode());
            assertEquals("550:24:550:69", getArea(bo.getErrors().get(2)));
            assertEquals(37130, bo.getErrors().get(3).getErrorCode());
            assertEquals("572:24:572:56", getArea(bo.getErrors().get(3)));
            assertEquals(37140, bo.getErrors().get(4).getErrorCode());
            assertEquals("578:34:578:40", getArea(bo.getErrors().get(4)));
            assertEquals(37210, bo.getErrors().get(5).getErrorCode());
            assertEquals("620:19:621:35", getArea(bo.getErrors().get(5)));
            assertEquals(37160, bo.getErrors().get(6).getErrorCode());
            assertEquals("643:19:643:33", getArea(bo.getErrors().get(6)));
            assertEquals(37160, bo.getErrors().get(7).getErrorCode());
            assertEquals("676:19:676:33", getArea(bo.getErrors().get(7)));
            assertEquals(37160, bo.getErrors().get(8).getErrorCode());
            assertEquals("704:19:704:32", getArea(bo.getErrors().get(8)));
            assertEquals(37160, bo.getErrors().get(9).getErrorCode());
            assertEquals("714:23:714:31", getArea(bo.getErrors().get(9)));
            assertEquals(37160, bo.getErrors().get(10).getErrorCode());
            assertEquals("714:19:714:32", getArea(bo.getErrors().get(10)));
            assertEquals(37240, bo.getErrors().get(11).getErrorCode());
            assertEquals("503:13:508:23", getArea(bo.getErrors().get(11)));
            assertEquals(37160, bo.getErrors().get(12).getErrorCode());
            assertEquals("819:19:819:35", getArea(bo.getErrors().get(12)));
            assertEquals(37160, bo.getErrors().get(13).getErrorCode());
            assertEquals("952:19:952:24", getArea(bo.getErrors().get(13)));
            assertEquals(37240, bo.getErrors().get(14).getErrorCode());
            assertEquals("722:13:737:23", getArea(bo.getErrors().get(14)));
            assertEquals(37130, bo.getErrors().get(15).getErrorCode());
            assertEquals("1020:24:1020:59", getArea(bo.getErrors().get(15)));
            assertEquals(37140, bo.getErrors().get(16).getErrorCode());
            assertEquals("1032:30:1032:36", getArea(bo.getErrors().get(16)));
            assertEquals(37160, bo.getErrors().get(17).getErrorCode());
            assertEquals("1058:19:1058:41", getArea(bo.getErrors().get(17)));
            assertEquals(37160, bo.getErrors().get(18).getErrorCode());
            assertEquals("1080:34:1080:43", getArea(bo.getErrors().get(18)));
            assertEquals(37140, bo.getErrors().get(19).getErrorCode());
            assertEquals("1110:30:1110:36", getArea(bo.getErrors().get(19)));
            assertEquals(37140, bo.getErrors().get(20).getErrorCode());
            assertEquals("1126:23:1129:33", getArea(bo.getErrors().get(20)));
            assertEquals(37160, bo.getErrors().get(21).getErrorCode());
            assertEquals("1195:19:1195:34", getArea(bo.getErrors().get(21)));
            assertEquals(37160, bo.getErrors().get(22).getErrorCode());
            assertEquals("1235:19:1235:33", getArea(bo.getErrors().get(22)));
            assertEquals(37210, bo.getErrors().get(23).getErrorCode());
            assertEquals("1297:19:1299:35", getArea(bo.getErrors().get(23)));
            assertEquals(37160, bo.getErrors().get(24).getErrorCode());
            assertEquals("1336:19:1336:33", getArea(bo.getErrors().get(24)));
            assertEquals(37160, bo.getErrors().get(25).getErrorCode());
            assertEquals("1358:19:1358:24", getArea(bo.getErrors().get(25)));
            assertEquals(37270, bo.getErrors().get(26).getErrorCode());
            assertEquals("1382:19:1384:33", getArea(bo.getErrors().get(26)));
            assertEquals(37270, bo.getErrors().get(27).getErrorCode());
            assertEquals("1411:19:1413:31", getArea(bo.getErrors().get(27)));
            assertEquals(37140, bo.getErrors().get(28).getErrorCode());
            assertEquals("1429:30:1429:36", getArea(bo.getErrors().get(28)));
            assertEquals(37150, bo.getErrors().get(29).getErrorCode());
            assertEquals("1444:20:1444:30", getArea(bo.getErrors().get(29)));
            assertEquals(37160, bo.getErrors().get(30).getErrorCode());
            assertEquals("1470:19:1470:28", getArea(bo.getErrors().get(30)));
            assertEquals(37240, bo.getErrors().get(31).getErrorCode());
            assertEquals("979:13:1002:23", getArea(bo.getErrors().get(31)));
            assertEquals(37160, bo.getErrors().get(32).getErrorCode());
            assertEquals("1535:19:1535:32", getArea(bo.getErrors().get(32)));
            assertEquals(37140, bo.getErrors().get(33).getErrorCode());
            assertEquals("1541:30:1541:36", getArea(bo.getErrors().get(33)));
            assertEquals(37160, bo.getErrors().get(34).getErrorCode());
            assertEquals("1573:19:1573:33", getArea(bo.getErrors().get(34)));
            assertEquals(37160, bo.getErrors().get(35).getErrorCode());
            assertEquals("1605:19:1605:33", getArea(bo.getErrors().get(35)));
            assertEquals(37140, bo.getErrors().get(36).getErrorCode());
            assertEquals("1627:32:1627:38", getArea(bo.getErrors().get(36)));
            assertEquals(37210, bo.getErrors().get(37).getErrorCode());
            assertEquals("1693:19:1694:35", getArea(bo.getErrors().get(37)));
            assertEquals(37160, bo.getErrors().get(38).getErrorCode());
            assertEquals("1734:34:1734:45", getArea(bo.getErrors().get(38)));
            assertEquals(37160, bo.getErrors().get(39).getErrorCode());
            assertEquals("1800:19:1800:35", getArea(bo.getErrors().get(39)));
            assertEquals(37160, bo.getErrors().get(40).getErrorCode());
            assertEquals("1843:23:1843:36", getArea(bo.getErrors().get(40)));
            assertEquals(37160, bo.getErrors().get(41).getErrorCode());
            assertEquals("1843:37:1843:49", getArea(bo.getErrors().get(41)));
            assertEquals(37270, bo.getErrors().get(42).getErrorCode());
            assertEquals("1889:19:1890:31", getArea(bo.getErrors().get(42)));
            assertEquals(37150, bo.getErrors().get(43).getErrorCode());
            assertEquals("1892:20:1892:30", getArea(bo.getErrors().get(43)));
            assertEquals(37270, bo.getErrors().get(44).getErrorCode());
            assertEquals("1918:19:1919:31", getArea(bo.getErrors().get(44)));
            assertEquals(37150, bo.getErrors().get(45).getErrorCode());
            assertEquals("1921:20:1921:30", getArea(bo.getErrors().get(45)));
            assertEquals(37140, bo.getErrors().get(46).getErrorCode());
            assertEquals("1934:23:1943:32", getArea(bo.getErrors().get(46)));
            assertEquals(37160, bo.getErrors().get(47).getErrorCode());
            assertEquals("1976:19:1976:26", getArea(bo.getErrors().get(47)));
            assertEquals(37240, bo.getErrors().get(48).getErrorCode());
            assertEquals("1478:13:1501:23", getArea(bo.getErrors().get(48)));
            assertEquals(37140, bo.getErrors().get(49).getErrorCode());
            assertEquals("2033:25:2035:34", getArea(bo.getErrors().get(49)));
            assertEquals(37160, bo.getErrors().get(50).getErrorCode());
            assertEquals("2065:19:2065:34", getArea(bo.getErrors().get(50)));
            assertEquals(37160, bo.getErrors().get(51).getErrorCode());
            assertEquals("2087:19:2087:29", getArea(bo.getErrors().get(51)));
            assertEquals(37150, bo.getErrors().get(52).getErrorCode());
            assertEquals("2091:20:2091:29", getArea(bo.getErrors().get(52)));
            assertEquals(37240, bo.getErrors().get(53).getErrorCode());
            assertEquals("1987:13:2004:23", getArea(bo.getErrors().get(53)));
            assertEquals(37130, bo.getErrors().get(54).getErrorCode());
            assertEquals("2161:24:2161:45", getArea(bo.getErrors().get(54)));
            assertEquals(37140, bo.getErrors().get(55).getErrorCode());
            assertEquals("2173:30:2173:36", getArea(bo.getErrors().get(55)));
            assertEquals(37140, bo.getErrors().get(56).getErrorCode());
            assertEquals("2222:27:2224:36", getArea(bo.getErrors().get(56)));
            assertEquals(37140, bo.getErrors().get(57).getErrorCode());
            assertEquals("2254:35:2254:41", getArea(bo.getErrors().get(57)));
            assertEquals(37200, bo.getErrors().get(58).getErrorCode());
            assertEquals("2250:21:2269:28", getArea(bo.getErrors().get(58)));
            assertEquals(37240, bo.getErrors().get(59).getErrorCode());
            assertEquals("2126:13:2141:23", getArea(bo.getErrors().get(59)));
            assertEquals(60, bo.getErrors().size());
        } finally {
            System.setProperty("qedeq.test.xmlLocationFailures",
                Boolean.toString(locationFailures));
        }
    }

    public void testPlugin4() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "math/qedeq_formal_logic_v1.xml"));
        getServices().checkFormallyProved(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
    }

}
