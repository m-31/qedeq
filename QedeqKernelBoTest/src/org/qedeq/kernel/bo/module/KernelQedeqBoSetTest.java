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

import java.util.Iterator;

import org.qedeq.base.test.QedeqTestCase;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.bo.common.Element2Latex;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.AbstractState;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingImportsState;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.state.WellFormedState;

/**
 * Test class.
 *
 * @author Michael Meyling
 */
public class KernelQedeqBoSetTest extends QedeqTestCase {

    private static final String URL1 = "http://www.qedeq.org/current/sample/qedeq_sample1.xml";

    private static final String URL2 = "http://www.qedeq.org/current/sample/qedeq_sample2.xml";

    private static final String URL4 = "http://www.qedeq.org/current/sample/qedeq_sample4.xml";


    /** {} */
    private KernelQedeqBoSet empty;

    /** {"one"} */
    private KernelQedeqBoSet one;

    /** {"two"} */
    private KernelQedeqBoSet two;

    /** {"two"} */
    private KernelQedeqBoSet two2;

    /** {"one", "two"} */
    private KernelQedeqBoSet oneTwo;

    public KernelQedeqBoSetTest(){
        super();
    }

    public KernelQedeqBoSetTest(final String name){
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        initAttributes();
    }

    /**
     * 
     */
    private void initAttributes() {
        empty = new KernelQedeqBoSet();
        one = new KernelQedeqBoSet(new KernelQedeqBo[] {createKernelQedeqBo(URL1)});
        two = new KernelQedeqBoSet(new KernelQedeqBo[] {createKernelQedeqBo(URL2)});
        two2 = new KernelQedeqBoSet();
        two2.add(createKernelQedeqBo(URL2));
        oneTwo = new KernelQedeqBoSet();
        oneTwo.add(createKernelQedeqBo(URL1));
        oneTwo.add(createKernelQedeqBo(URL2));
    }

    protected void tearDown() throws Exception {
        empty = null;
        one = null;
        two = null;
        two2 = null;
        oneTwo = null;
        super.tearDown();
    }


    /**
     * Test constructor.
     */
    public void testConstructor() {
        try {
            new KernelQedeqBoSet((KernelQedeqBo) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new KernelQedeqBoSet((KernelQedeqBoSet) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            new KernelQedeqBoSet((KernelQedeqBo) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(one, new KernelQedeqBoSet(createKernelQedeqBo(URL1)));
        assertEquals(one, new KernelQedeqBoSet(new KernelQedeqBoSet(new KernelQedeqBoSet(createKernelQedeqBo(
            URL1)))));
    }

    public void testSetOperations() {
        KernelQedeqBo o = createKernelQedeqBo(URL1);
        KernelQedeqBo t = createKernelQedeqBo(URL2);
        assertTrue(one.contains(o));
        KernelQedeqBo otto = createKernelQedeqBo(URL4);
        assertFalse(one.contains(otto));
        one.remove(o);
        assertEquals(empty, one);
        one.remove(otto);
        assertEquals(empty, one);
        assertTrue(one.isEmpty());
        assertFalse(two.isEmpty());
        two.add(o);
        assertEquals(two, oneTwo);
        one.add(o);
        assertTrue(one.contains(o));
        assertFalse(one.contains(t));
        one.intersection(oneTwo);
        assertTrue(one.contains(o));
        assertFalse(one.contains(t));
        one.add(oneTwo);
        assertTrue(one.contains(o));
        assertTrue(one.contains(t));
        assertEquals(2, one.size());
    }

    public void testSetOperations2() {
        try {
            one.add((KernelQedeqBo) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.add((KernelQedeqBoSet) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        try {
            one.intersection(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }

    public void testRemoveSet() {
        try {
            one.remove((KernelQedeqBoSet) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(oneTwo, oneTwo.remove(empty));
        assertEquals(2, oneTwo.size());
        assertEquals(one, one.remove(one));
        assertEquals(0, one.size());
        initAttributes();
        assertEquals(oneTwo, oneTwo.remove(one));
        assertEquals(new KernelQedeqBoSet(createKernelQedeqBo(URL2)), oneTwo);
        initAttributes();
        assertEquals(oneTwo, oneTwo.remove(two));
        assertEquals(oneTwo, new KernelQedeqBoSet(createKernelQedeqBo(URL1)));
        initAttributes();
        assertEquals(2, oneTwo.size());
        assertEquals(oneTwo, oneTwo.remove(createKernelQedeqBo(URL4)));
        assertEquals(2, oneTwo.size());
    }

    public void testRemove() {
        try {
            one.remove((KernelQedeqBo) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(one, one.remove(createKernelQedeqBo(URL2)));
        assertEquals(1, one.size());
        assertEquals(two, two.remove(createKernelQedeqBo(URL2)));
        assertEquals(empty, two);
        assertEquals(0, two.size());
        assertEquals(oneTwo, oneTwo.remove(createKernelQedeqBo(URL2)));
    }

    public void testAdd() {
        try {
            one.add((KernelQedeqBo) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(one, one.add(createKernelQedeqBo(URL2)));
        assertEquals(2, one.size());
        assertEquals(oneTwo, one);
        assertEquals(one, one.add(createKernelQedeqBo(URL2)));
        assertEquals(2, one.size());
        assertEquals(oneTwo, one);
    }

    public void testAdd2() {
        try {
            one.add((KernelQedeqBoSet) null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertEquals(one, one.add(new KernelQedeqBoSet(createKernelQedeqBo(URL2))));
        assertEquals(2, one.size());
        assertEquals(oneTwo, one);
        assertEquals(one, one.add(new KernelQedeqBoSet(createKernelQedeqBo(URL2))));
        assertEquals(2, one.size());
        assertEquals(oneTwo, one);
    }

    public void testContains() {
        try {
            one.contains(null);
            fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
        assertFalse(empty.contains(createKernelQedeqBo(URL2)));
        assertFalse(one.contains(createKernelQedeqBo(URL2)));
        assertTrue(one.contains(createKernelQedeqBo(URL1)));
        assertTrue(oneTwo.contains(createKernelQedeqBo(URL1)));
        assertTrue(oneTwo.contains(createKernelQedeqBo(URL2)));
    }

    public void testIsEmpty() {
        assertTrue(empty.isEmpty());
        assertFalse(oneTwo.isEmpty());
    }

    public void testIterator() {
        Iterator iterator = empty.iterator();
        assertFalse(iterator.hasNext());
        Iterator iterator2 = one.iterator();
        assertTrue(iterator2.hasNext());
        assertEquals(createKernelQedeqBo(URL1), iterator2.next());
        assertFalse(iterator2.hasNext());
    }

    /**
     * Test toString.
     */
    public void testToString() {
        assertEquals("{}", empty.toString());
        assertEquals("{" + URL1 + "}", one.toString());
        assertEquals("{" + URL2 + "}", two.toString());
        assertEquals("{" + URL2 + "}", two2.toString());
        assertEquals("{" + URL4 + "}", new KernelQedeqBoSet(createKernelQedeqBo(URL4)).toString());
        assertTrue(("{" + URL1 + ", " + URL2 + "}").equals(oneTwo.toString())
            || ("{\"" + URL2 + ", " + URL1 + "}").equals(oneTwo.toString()));
    }

    /**
     * Test hashCode.
     */
    public void testHashCode() {
        assertFalse(empty.hashCode() == two2.hashCode());
        assertFalse(empty.hashCode() == one.hashCode());
        assertFalse(two.hashCode() == one.hashCode());
        assertTrue(two.hashCode() == two2.hashCode());
    }

    /**
     * Test equals.
     */
    public void testEquals() {
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(one));
        assertFalse(empty.equals(two));
        assertFalse(empty.equals(two2));
        assertFalse(one.equals(null));
        assertTrue(one.equals(one));
        assertFalse(one.equals(two));
        assertFalse(one.equals(two2));
        assertFalse(two.equals(null));
        assertFalse(two.equals(one));
        assertTrue(two.equals(two));
        assertTrue(two.equals(two2));
        assertFalse(two2.equals(null));
        assertFalse(two2.equals(one));
        assertTrue(two2.equals(two));
        assertTrue(two2.equals(two2));
        assertTrue(empty.equals(new KernelQedeqBoSet()));
    }

    private KernelQedeqBo createKernelQedeqBo(final String url) {
        return new KernelQedeqBo() {
            public String toString() {
                return url;
            }

            public int hashCode() {
                return url.hashCode();
            }

            public boolean equals(final Object obj) {
                if (obj instanceof KernelQedeqBo) {
                    return EqualsUtility.equals(((KernelQedeqBo) obj).getUrl(),
                        this.getUrl());
                }
                return false;
                
            }

            public boolean hasBasicFailures() {
                return false;
            }

            public boolean hasErrors() {
                return false;
            }

            public boolean hasWarnings() {
                return false;
            }

            public ModuleAddress getModuleAddress() {
                return null;
            }

            public AbstractState getLastSuccessfulState() {
                return null;
            }

            public AbstractState getCurrentState() {
                return null;
            }

            public Service getCurrentlyRunningService() {
                return null;
            }

            public LoadingState getLoadingState() {
                return null;
            }

            public int getLoadingCompleteness() {
                return 0;
            }

            public LoadingImportsState getLoadingImportsState() {
                return null;
            }

            public DependencyState getDependencyState() {
                return null;
            }

            public WellFormedState getWellFormedState() {
                return null;
            }

            public FormallyProvedState getFormallyProvedState() {
                return null;
            }

            public SourceFileExceptionList getErrors() {
                return null;
            }

            public SourceFileExceptionList getWarnings() {
                return null;
            }

            public String getStateDescription() {
                return "unknown";
            }

            public String getName() {
                String name = url.substring(url.lastIndexOf('/') + 1);
                name = name.substring(0, name.lastIndexOf('.'));
                return name;
            }

            public String getRuleVersion() {
                return "1.00.00";
            }

            public String getUrl() {
                return url;
            }

            public boolean isLoaded() {
                return false;
            }

            public Qedeq getQedeq() {
                return null;
            }

            public boolean hasLoadedImports() {
                return false;
            }

            public boolean hasLoadedRequiredModules() {
                return false;
            }

            public ModuleReferenceList getRequiredModules() {
                return null;
            }

            public boolean isWellFormed() {
                return false;
            }

            public boolean isFullyFormallyProved() {
                return false;
            }

            public String[] getSupportedLanguages() {
                return null;
            }

            public boolean isSupportedLanguage(String language) {
                return false;
            }

            public String getOriginalLanguage() {
                return null;
            }

            public InternalKernelServices getKernelServices() {
                return null;
            }

            public KernelModuleReferenceList getKernelRequiredModules() {
                return null;
            }

            public ModuleLabels getLabels() {
                return null;
            }

            public Element2Latex getElement2Latex() {
                return null;
            }

            public Element2Utf8 getElement2Utf8() {
                return null;
            }

            public SourceFileException createSourceFileException(Service service, ModuleDataException exception) {
                return null;
            }

            public void addPluginErrorsAndWarnings(ModuleService plugin, SourceFileExceptionList errors,
                    SourceFileExceptionList warnings) {
            }

            public void clearAllPluginErrorsAndWarnings() {
            }

            public ModuleConstantsExistenceChecker getExistenceChecker() {
                return null;
            }

            public void setLoadingImportsFailureState(LoadingImportsState loadImportsFailed, SourceFileExceptionList sfl) {
            }

            public void setLoadingImportsProgressState(LoadingImportsState stateLoadImports) {
            }

            public void setLoadedImports(KernelModuleReferenceList imports) {
            }

            public void setDependencyFailureState(DependencyState loadRequiredFailed, SourceFileExceptionList sfl) {
            }

            public void setDependencyProgressState(DependencyState state) {
            }

            public void setLoadedRequiredModules() {
            }

            public void setWellfFormedFailureState(WellFormedState stateExternalCheckingFailed,
                    SourceFileExceptionList sfl) {
            }

            public void setWellFormedProgressState(WellFormedState stateInternalChecking) {
            }

            public void setFormallyProvedProgressState(FormallyProvedState state) {
            }

            public void setFormallyProvedFailureState(FormallyProvedState state, SourceFileExceptionList sfl) {
            }

            public void setExistenceChecker(ModuleConstantsExistenceChecker existence) {
            }

            public void setWellFormed(ModuleConstantsExistenceChecker checker) {
            }
            
        };
    }
}
