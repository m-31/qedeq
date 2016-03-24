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
package org.qedeq.kernel.bo.logic.proof.checker;

import java.io.File;

import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.logic.common.LogicalCheckExceptionList;
import org.qedeq.kernel.bo.logic.common.ReferenceResolver;
import org.qedeq.kernel.bo.logic.proof.common.ProofChecker;
import org.qedeq.kernel.bo.logic.proof.common.RuleChecker;
import org.qedeq.kernel.bo.module.KernelNodeBo;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.test.QedeqBoTestCase;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.common.DefaultModuleAddress;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.RuleKey;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;

/**
 * For testing of checking formal proofs.
 *
 * @author Michael Meyling
 */
public class ProofCheckerTest extends QedeqBoTestCase {

    private ProofChecker checker0;

    private ProofChecker checker1;

    private ProofChecker checker2;

    private RuleChecker ruleCheckerAll;
    
    private ReferenceResolver resolverLocal;

    private Element disjunction_idempotence_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("OR", new Element[] {
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                })
            }),
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("A")
            })
        });

    private Element disjunction_weakening_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("A")
            }),
            new DefaultElementList("OR", new Element[] {
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("B")
                })
            })
        });

    private Element disjunction_addition_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("IMPL", new Element[] {
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("A")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("B")
                })
            }),
            new DefaultElementList("IMPL", new Element[] {
                new DefaultElementList("IMPL", new Element[] {
                    new DefaultElementList("PREDVAR", new Element[] {
                        new DefaultAtom("C")
                    }),
                    new DefaultElementList("PREDVAR", new Element[] {
                        new DefaultAtom("A")
                    })
                }),
                new DefaultElementList("IMPL", new Element[] {
                    new DefaultElementList("PREDVAR", new Element[] {
                        new DefaultAtom("C")
                    }),
                    new DefaultElementList("PREDVAR", new Element[] {
                        new DefaultAtom("B")
                    })
                })
            })
        });

    private Element universal_instantiation_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("FORALL", new Element[] {
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("x")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("\\phi"),
                    new DefaultElementList("VAR", new Element[] {
                        new DefaultAtom("x")
                    })
                })
            }),
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("\\phi"),
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y")
                })
            })
        });

    private Element existencial_generalization_axiom =
        new DefaultElementList("IMPL", new Element[] {
            new DefaultElementList("PREDVAR", new Element[] {
                new DefaultAtom("\\phi"),
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("y")
                })
            }),
            new DefaultElementList("EXISTS", new Element[] {
                new DefaultElementList("VAR", new Element[] {
                    new DefaultAtom("x")
                }),
                new DefaultElementList("PREDVAR", new Element[] {
                    new DefaultAtom("\\phi"),
                    new DefaultElementList("VAR", new Element[] {
                        new DefaultAtom("x")
                    })
                })
            })
        });

    public void setUp() throws Exception {
        super.setUp();
        checker0 = new ProofChecker0Impl();
        checker1 = new ProofChecker1Impl();
        checker2 = new ProofChecker2Impl();
        ruleCheckerAll = new RuleChecker() {
            public RuleKey getRule(String ruleName) {
                return new RuleKey(ruleName, "0.01.00");
            }
        };
        resolverLocal = new ReferenceResolver() {
            public Element getNormalizedFormula(Element element) {
                return element;
            }

            public Element getNormalizedLocalProofLineReference(String reference) {
                return null;
            }

            public Element getNormalizedReferenceFormula(String reference) {
                if ("axiom:disjunction_idempotence".equals(reference)) {
                    return disjunction_idempotence_axiom;
                } else if ("axiom:disjunction_weakening".equals(reference)) {
                    return disjunction_weakening_axiom;
                } else if ("axiom:disjunction_addition".equals(reference)) {
                    return disjunction_addition_axiom;
                } else if ("axiom:universalInstantiation".equals(reference)) {
                    return universal_instantiation_axiom;
                } else if ("axiom:existencialGeneralization".equals(reference)) {
                    return existencial_generalization_axiom;
                }
                return null;
            }

            public ModuleContext getReferenceContext(String reference) {
                return null;
            }

            public boolean isLocalProofLineReference(String reference) {
                return false;
            }

            public boolean isProvedFormula(String reference) {
                return true;
            }
        };
    }


    public void testCheck1() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkWellFormedness(address);
        final KernelQedeqBo bo = (KernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelNodeBo node = bo.getLabels().getNode("proposition:one");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 9; i++) {
            list.add(original.get(i));
        }
        LogicalCheckExceptionList e0 = 
            checker0.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e0);
        assertEquals(1, e0.size());
        assertEquals(37400, e0.get(0).getErrorCode());
        LogicalCheckExceptionList e1 = 
            checker1.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e1);
        assertEquals(0, e1.size());
        LogicalCheckExceptionList e2 = 
            checker2.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e2);
        assertEquals(0, e2.size());
    }

    public void testCheck4() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkWellFormedness(address);
        final KernelQedeqBo bo = (KernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelNodeBo node = bo.getLabels().getNode("proposition:four");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        final FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 8; i++) {
            list.add(original.get(i));
        }
        LogicalCheckExceptionList e0 = 
            checker0.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e0);
        assertEquals(1, e0.size());
        assertEquals(37400, e0.get(0).getErrorCode());
        LogicalCheckExceptionList e1 = 
            checker1.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e1);
        assertEquals(0, e1.size());
        LogicalCheckExceptionList e2 = 
            checker2.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e2);
        assertEquals(0, e2.size());
    }

    public void testCheck6() throws Exception {
        final ModuleAddress address = new DefaultModuleAddress(new File(getDocDir(),
            "sample/qedeq_sample3.xml"));
        KernelContext.getInstance().checkWellFormedness(address);
        final KernelQedeqBo bo = (KernelQedeqBo) KernelContext.getInstance().getQedeqBo(address);
        assertTrue(bo.isWellFormed());
        assertNotNull(bo.getWarnings());
        assertEquals(0, bo.getWarnings().size());
        assertEquals(0, bo.getErrors().size());
        final KernelNodeBo node = bo.getLabels().getNode("proposition:six");
        final Proposition prop = node.getNodeVo().getNodeType().getProposition();
        final FormalProofLineList original = prop.getFormalProofList().get(0)
            .getFormalProofLineList();
        FormalProofLineListVo list = new FormalProofLineListVo();
        for (int i = 0; i < 15; i++) {
            list.add(original.get(i));
        }
        LogicalCheckExceptionList e0 = 
            checker0.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e0);
        assertEquals(1, e0.size());
        assertEquals(37400, e0.get(0).getErrorCode());
        LogicalCheckExceptionList e2 = 
            checker1.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e2);
        assertEquals(1, e2.size());
        assertEquals(37200, e2.get(0).getErrorCode());
        list.add(original.get(15));
        LogicalCheckExceptionList e3 = 
            checker1.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e3);
        assertEquals(0, e3.size());

        list = new FormalProofLineListVo();
        for (int i = 0; i < 15; i++) {
            list.add(original.get(i));
        }
        LogicalCheckExceptionList e4 = 
            checker2.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e4);
//        System.out.println(e2);
//        Element2LatexImpl transform = new Element2LatexImpl(new ModuleLabels());
//        System.out.println(transform.getLatex(universal_instantiation_axiom));
//        System.out.println(e2);
        assertEquals(1, e4.size());
//        System.out.println(e2.get(0));
        assertEquals(37200, e4.get(0).getErrorCode());
        list.add(original.get(15));
        LogicalCheckExceptionList e5 = 
            checker2.checkProof(prop.getFormula().getElement(), list, ruleCheckerAll,
                DefaultModuleAddress.MEMORY.createModuleContext(), resolverLocal);
        assertNotNull(e5);
//        System.out.println(e2);
//        Element2LatexImpl transform = new Element2LatexImpl(new ModuleLabels());
//        System.out.println(transform.getLatex(universal_instantiation_axiom));
//        System.out.println(e3);
        assertEquals(0, e5.size());
    }

}
