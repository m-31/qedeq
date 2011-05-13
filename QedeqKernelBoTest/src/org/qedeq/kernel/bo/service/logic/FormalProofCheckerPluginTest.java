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
package org.qedeq.kernel.bo.service.logic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;

/**
 * Test the formal proof checker plugin.
 *
 * FIXME 20110428 m31: test also error positions
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

    public void testPlugin() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        final Map parameters = new HashMap();
        getServices().executePlugin(FormalProofCheckerPlugin.class.getName(),
            address, parameters);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
    }

    public void testPlugin2() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getIndir(),
            "proof/proof_001.xml"));
        getServices().checkModule(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final Map parameters = new HashMap();
        getServices().executePlugin(FormalProofCheckerPlugin.class.getName(),
                address, parameters);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(16, bo.getErrors().size());
        System.out.println("testPlugin2");
        bo.getErrors().printStackTrace(System.out);
        assertEquals(37140, bo.getErrors().get(0).getErrorCode());
        assertEquals(37140, bo.getErrors().get(1).getErrorCode());
        assertEquals(37180, bo.getErrors().get(2).getErrorCode());
        assertEquals(37150, bo.getErrors().get(3).getErrorCode());
        assertEquals(37240, bo.getErrors().get(4).getErrorCode());
        assertEquals(37150, bo.getErrors().get(5).getErrorCode());
        assertEquals(37140, bo.getErrors().get(6).getErrorCode());
        assertEquals(37150, bo.getErrors().get(7).getErrorCode());
        assertEquals(37140, bo.getErrors().get(8).getErrorCode());
        assertEquals(37240, bo.getErrors().get(9).getErrorCode());
        assertEquals(37150, bo.getErrors().get(10).getErrorCode());
        assertEquals(37240, bo.getErrors().get(11).getErrorCode());
        assertEquals(37140, bo.getErrors().get(12).getErrorCode());
        assertEquals(37140, bo.getErrors().get(13).getErrorCode());
        assertEquals(37200, bo.getErrors().get(14).getErrorCode());
        assertEquals(37240, bo.getErrors().get(15).getErrorCode());
    }

    public void testPlugin3() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getIndir(),
            "proof/proof_002.xml"));
        getServices().checkModule(address);
        final QedeqBo bo = getServices().getQedeqBo(address);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final Map parameters = new HashMap();
        getServices().executePlugin(FormalProofCheckerPlugin.class.getName(),
                address, parameters);
        assertTrue(bo.isChecked());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(60, bo.getErrors().size());
        System.out.println("testPlugin3");
        bo.getErrors().printStackTrace(System.out);
        assertEquals(37220, bo.getErrors().get(0).getErrorCode());
        assertEquals(37210, bo.getErrors().get(1).getErrorCode());
        assertEquals(37130, bo.getErrors().get(2).getErrorCode());
        assertEquals(37130, bo.getErrors().get(3).getErrorCode());
        assertEquals(37140, bo.getErrors().get(4).getErrorCode());
        assertEquals(37210, bo.getErrors().get(5).getErrorCode());
        assertEquals(37160, bo.getErrors().get(6).getErrorCode());
        assertEquals(37160, bo.getErrors().get(7).getErrorCode());
        assertEquals(37160, bo.getErrors().get(8).getErrorCode());
        assertEquals(37160, bo.getErrors().get(9).getErrorCode());
        assertEquals(37160, bo.getErrors().get(10).getErrorCode());
        assertEquals(37240, bo.getErrors().get(11).getErrorCode());
        assertEquals(37160, bo.getErrors().get(12).getErrorCode());
        assertEquals(37160, bo.getErrors().get(13).getErrorCode());
        assertEquals(37240, bo.getErrors().get(14).getErrorCode());
        assertEquals(37130, bo.getErrors().get(15).getErrorCode());
        assertEquals(37140, bo.getErrors().get(16).getErrorCode());
        assertEquals(37160, bo.getErrors().get(17).getErrorCode());
        assertEquals(37160, bo.getErrors().get(18).getErrorCode());
        assertEquals(37140, bo.getErrors().get(19).getErrorCode());
        assertEquals(37140, bo.getErrors().get(20).getErrorCode());
        assertEquals(37160, bo.getErrors().get(21).getErrorCode());
        assertEquals(37160, bo.getErrors().get(22).getErrorCode());
        assertEquals(37210, bo.getErrors().get(23).getErrorCode());
        assertEquals(37160, bo.getErrors().get(24).getErrorCode());
        assertEquals(37160, bo.getErrors().get(25).getErrorCode());
        assertEquals(37270, bo.getErrors().get(26).getErrorCode());
        assertEquals(37270, bo.getErrors().get(27).getErrorCode());
        assertEquals(37140, bo.getErrors().get(28).getErrorCode());
        assertEquals(37150, bo.getErrors().get(29).getErrorCode());
        assertEquals(37160, bo.getErrors().get(30).getErrorCode());
        assertEquals(37240, bo.getErrors().get(31).getErrorCode());
        assertEquals(37160, bo.getErrors().get(32).getErrorCode());
        assertEquals(37140, bo.getErrors().get(33).getErrorCode());
        assertEquals(37160, bo.getErrors().get(34).getErrorCode());
        assertEquals(37160, bo.getErrors().get(35).getErrorCode());
        assertEquals(37140, bo.getErrors().get(36).getErrorCode());
        assertEquals(37210, bo.getErrors().get(37).getErrorCode());
        assertEquals(37160, bo.getErrors().get(38).getErrorCode());
        assertEquals(37160, bo.getErrors().get(39).getErrorCode());
        assertEquals(37160, bo.getErrors().get(40).getErrorCode());
        assertEquals(37160, bo.getErrors().get(41).getErrorCode());
        assertEquals(37270, bo.getErrors().get(42).getErrorCode());
        assertEquals(37150, bo.getErrors().get(43).getErrorCode());
        assertEquals(37270, bo.getErrors().get(44).getErrorCode());
        assertEquals(37150, bo.getErrors().get(45).getErrorCode());
        assertEquals(37140, bo.getErrors().get(46).getErrorCode());
        assertEquals(37160, bo.getErrors().get(47).getErrorCode());
        assertEquals(37240, bo.getErrors().get(48).getErrorCode());
        assertEquals(37140, bo.getErrors().get(49).getErrorCode());
        assertEquals(37160, bo.getErrors().get(50).getErrorCode());
        assertEquals(37160, bo.getErrors().get(51).getErrorCode());
        assertEquals(37150, bo.getErrors().get(52).getErrorCode());
        assertEquals(37240, bo.getErrors().get(53).getErrorCode());
        assertEquals(37130, bo.getErrors().get(54).getErrorCode());
        assertEquals(37140, bo.getErrors().get(55).getErrorCode());
        assertEquals(37140, bo.getErrors().get(56).getErrorCode());
        assertEquals(37140, bo.getErrors().get(57).getErrorCode());
        assertEquals(37200, bo.getErrors().get(58).getErrorCode());
        assertEquals(37240, bo.getErrors().get(59).getErrorCode());
    }

// FIXME correct
//    public void testPlugin4() throws Exception {
//        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
//            "doc/math/qedeq_propositional_v1.xml"));
//        final Map parameters = new HashMap();
//        getServices().executePlugin(FormalProofCheckerPlugin.class.getName(),
//            address, parameters);
//        final QedeqBo bo = getServices().getQedeqBo(address);
//        assertTrue(bo.isChecked());
//        assertEquals(0, bo.getWarnings().size());
//        assertEquals(0, bo.getErrors().size());
//    }

}
