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

package org.qedeq.kernel.se.test;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qedeq.base.io.TextOutput;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.ChangedRule;
import org.qedeq.kernel.se.base.module.ChangedRuleList;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.ChapterList;
import org.qedeq.kernel.se.base.module.Conclusion;
import org.qedeq.kernel.se.base.module.ConditionalProof;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
import org.qedeq.kernel.se.base.module.Hypothesis;
import org.qedeq.kernel.se.base.module.Import;
import org.qedeq.kernel.se.base.module.ImportList;
import org.qedeq.kernel.se.base.module.InitialFunctionDefinition;
import org.qedeq.kernel.se.base.module.InitialPredicateDefinition;
import org.qedeq.kernel.se.base.module.Latex;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.LinkList;
import org.qedeq.kernel.se.base.module.LiteratureItem;
import org.qedeq.kernel.se.base.module.LiteratureItemList;
import org.qedeq.kernel.se.base.module.Location;
import org.qedeq.kernel.se.base.module.LocationList;
import org.qedeq.kernel.se.base.module.Node;
import org.qedeq.kernel.se.base.module.NodeType;
import org.qedeq.kernel.se.base.module.PredicateDefinition;
import org.qedeq.kernel.se.base.module.Proof;
import org.qedeq.kernel.se.base.module.ProofList;
import org.qedeq.kernel.se.base.module.Proposition;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.base.module.Reason;
import org.qedeq.kernel.se.base.module.Rename;
import org.qedeq.kernel.se.base.module.Rule;
import org.qedeq.kernel.se.base.module.Section;
import org.qedeq.kernel.se.base.module.SectionList;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.base.module.Subsection;
import org.qedeq.kernel.se.base.module.SubsectionList;
import org.qedeq.kernel.se.base.module.SubsectionType;
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Term;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.AuthorListVo;
import org.qedeq.kernel.se.dto.module.AuthorVo;
import org.qedeq.kernel.se.dto.module.AxiomVo;
import org.qedeq.kernel.se.dto.module.ChangedRuleListVo;
import org.qedeq.kernel.se.dto.module.ChangedRuleVo;
import org.qedeq.kernel.se.dto.module.ChapterListVo;
import org.qedeq.kernel.se.dto.module.ChapterVo;
import org.qedeq.kernel.se.dto.module.ConclusionVo;
import org.qedeq.kernel.se.dto.module.ConditionalProofVo;
import org.qedeq.kernel.se.dto.module.ExistentialVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofListVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.FunctionDefinitionVo;
import org.qedeq.kernel.se.dto.module.HeaderVo;
import org.qedeq.kernel.se.dto.module.HypothesisVo;
import org.qedeq.kernel.se.dto.module.ImportListVo;
import org.qedeq.kernel.se.dto.module.ImportVo;
import org.qedeq.kernel.se.dto.module.InitialFunctionDefinitionVo;
import org.qedeq.kernel.se.dto.module.InitialPredicateDefinitionVo;
import org.qedeq.kernel.se.dto.module.LatexListVo;
import org.qedeq.kernel.se.dto.module.LatexVo;
import org.qedeq.kernel.se.dto.module.LinkListVo;
import org.qedeq.kernel.se.dto.module.LiteratureItemListVo;
import org.qedeq.kernel.se.dto.module.LiteratureItemVo;
import org.qedeq.kernel.se.dto.module.LocationListVo;
import org.qedeq.kernel.se.dto.module.LocationVo;
import org.qedeq.kernel.se.dto.module.ModusPonensVo;
import org.qedeq.kernel.se.dto.module.NodeVo;
import org.qedeq.kernel.se.dto.module.PredicateDefinitionVo;
import org.qedeq.kernel.se.dto.module.ProofListVo;
import org.qedeq.kernel.se.dto.module.ProofVo;
import org.qedeq.kernel.se.dto.module.PropositionVo;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.dto.module.RenameVo;
import org.qedeq.kernel.se.dto.module.RuleVo;
import org.qedeq.kernel.se.dto.module.SectionListVo;
import org.qedeq.kernel.se.dto.module.SectionVo;
import org.qedeq.kernel.se.dto.module.SpecificationVo;
import org.qedeq.kernel.se.dto.module.SubsectionListVo;
import org.qedeq.kernel.se.dto.module.SubsectionVo;
import org.qedeq.kernel.se.dto.module.SubstFreeVo;
import org.qedeq.kernel.se.dto.module.SubstFuncVo;
import org.qedeq.kernel.se.dto.module.SubstPredVo;
import org.qedeq.kernel.se.dto.module.TermVo;
import org.qedeq.kernel.se.dto.module.UniversalVo;
import org.qedeq.kernel.se.dto.module.UsedByListVo;

/**
 * Test helper. Creates full QEDEQ module.
 *
 * @author    Michael Meyling
 */
public class QedeqVoCreator {

    /** Maps abstract classes to an implementation class. */
    private static final Map interface2ConcreteClass = new HashMap();

    static {
        interface2ConcreteClass.put(Author.class, AuthorVo.class);
        interface2ConcreteClass.put(AuthorList.class, AuthorListVo.class);
        interface2ConcreteClass.put(Axiom.class, AxiomVo.class);
        interface2ConcreteClass.put(ChangedRule.class, ChangedRuleVo.class);
        interface2ConcreteClass.put(ChangedRuleList.class, ChangedRuleListVo.class);
        interface2ConcreteClass.put(Chapter.class, ChapterVo.class);
        interface2ConcreteClass.put(ChapterList.class, ChapterListVo.class);
        interface2ConcreteClass.put(ConditionalProof.class, ConditionalProofVo.class);
        interface2ConcreteClass.put(Conclusion.class, ConclusionVo.class);
        interface2ConcreteClass.put(InitialPredicateDefinition.class, InitialPredicateDefinitionVo.class);
        interface2ConcreteClass.put(PredicateDefinition.class, PredicateDefinitionVo.class);
        interface2ConcreteClass.put(Existential.class, ExistentialVo.class);
        interface2ConcreteClass.put(InitialFunctionDefinition.class, InitialFunctionDefinitionVo.class);
        interface2ConcreteClass.put(FunctionDefinition.class, FunctionDefinitionVo.class);
        interface2ConcreteClass.put(FormalProof.class, FormalProofVo.class);
        interface2ConcreteClass.put(FormalProofList.class, FormalProofListVo.class);
        interface2ConcreteClass.put(FormalProofLineList.class, FormalProofLineListVo.class);
        interface2ConcreteClass.put(Formula.class, FormulaVo.class);
        interface2ConcreteClass.put(Header.class, HeaderVo.class);
        interface2ConcreteClass.put(Hypothesis.class, HypothesisVo.class);
        interface2ConcreteClass.put(Import.class, ImportVo.class);
        interface2ConcreteClass.put(ImportList.class, ImportListVo.class);
        interface2ConcreteClass.put(Latex.class, LatexVo.class);
        interface2ConcreteClass.put(LatexList.class, LatexListVo.class);
        interface2ConcreteClass.put(LinkList.class, LinkListVo.class);
        interface2ConcreteClass.put(LiteratureItem.class, LiteratureItemVo.class);
        interface2ConcreteClass.put(LiteratureItemList.class, LiteratureItemListVo.class);
        interface2ConcreteClass.put(Location.class, LocationVo.class);
        interface2ConcreteClass.put(LocationList.class, LocationListVo.class);
        interface2ConcreteClass.put(Node.class, NodeVo.class);
        interface2ConcreteClass.put(Proof.class, ProofVo.class);
        interface2ConcreteClass.put(ProofList.class, ProofListVo.class);
        interface2ConcreteClass.put(Proposition.class, PropositionVo.class);
        interface2ConcreteClass.put(Qedeq.class, QedeqVo.class);
        interface2ConcreteClass.put(Rename.class, RenameVo.class);
        interface2ConcreteClass.put(Rule.class, RuleVo.class);
        interface2ConcreteClass.put(Section.class, SectionVo.class);
        interface2ConcreteClass.put(SectionList.class, SectionListVo.class);
        interface2ConcreteClass.put(Specification.class, SpecificationVo.class);
        interface2ConcreteClass.put(Subsection.class, SubsectionVo.class);
        interface2ConcreteClass.put(SubsectionList.class, SubsectionListVo.class);
        interface2ConcreteClass.put(SubstFree.class, SubstFreeVo.class);
        interface2ConcreteClass.put(SubstFunc.class, SubstFuncVo.class);
        interface2ConcreteClass.put(SubstPred.class, SubstPredVo.class);
        interface2ConcreteClass.put(Term.class, TermVo.class);
        interface2ConcreteClass.put(Universal.class, UniversalVo.class);
        interface2ConcreteClass.put(UsedByList.class, UsedByListVo.class);
    }

    private int intCounter;
    
    private int stringCounter;

    public QedeqVoCreator() {
    }
    

    private List abstractToConcreteClass(final Class clazz) {
        List result = new ArrayList();
        if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
            result.add(clazz);
        } else {
            Object concreteClass = interface2ConcreteClass.get(clazz);
            if (concreteClass == null) {
                if (clazz == Element.class) {
                    result.add(DefaultAtom.class);
                    result.add(DefaultElementList.class);
                } else if (clazz == NodeType.class) {
                    result.add(PredicateDefinitionVo.class);
                    result.add(InitialPredicateDefinitionVo.class);
                    result.add(FunctionDefinitionVo.class);
                    result.add(InitialFunctionDefinitionVo.class);
                    result.add(RuleVo.class);
                    result.add(PropositionVo.class);
                    result.add(AxiomVo.class);
                } else if (clazz == SubsectionType.class) {
                    result.add(NodeVo.class);
                    result.add(SubsectionVo.class);
                } else if (clazz == FormalProofLine.class) {
                    result.add(FormalProofLineVo.class);
                    result.add(ConditionalProofVo.class);
                } else if (clazz == Reason.class) {
                    result.add(ModusPonensVo.class);
                    result.add(SubstFreeVo.class);
                    result.add(SubstPredVo.class);
                    result.add(RenameVo.class);
                    result.add(AddVo.class);
                    result.add(ExistentialVo.class);
                    result.add(UniversalVo.class);
                    result.add(SubstFuncVo.class);
                }
            } else {
                result.add(concreteClass);
            }
        }
        return result;
    }

    public List create(){
        return create(Qedeq.class);
    }

    public static void main(final String[] args) {
        final QedeqVoCreator c = new QedeqVoCreator();
        final List vos = c.create();
        for (int i = 0; i < vos.size(); i++) {
//            System.out.println(vos.get(i).toString());
//            System.out.println("**********************************");
        }
    }

    public TextOutput out = new TextOutput("out", new PrintStream(System.out));
    /**
     * Create variations of instances of given class.
     * 
     * @param   clazz
     * @return  A list with objects that are instances of <code>clazz</code>.
     */
    public List create(Class clazz) { 
//        out.pushLevel(clazz.getName());
//        out.println("Creating new for  " + clazz.getName());
        final List result = new ArrayList();

        Object vo = null;
        if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                vo = new Integer(++intCounter);
            } else {
                throw new RuntimeException("not yet supported: " + clazz);
            }
        } else if (clazz.equals(Integer.class)) {
            vo = new Integer(++intCounter);
        } else if (clazz.equals(String.class)) {
            vo = "s" + (++stringCounter);
        } else if (clazz.equals(Boolean.class)) {
            result.add(Boolean.FALSE);
            vo = Boolean.TRUE;
        } else if (clazz.equals(List.class)) {
            vo = new ArrayList();
        } else {
            List clazzes = abstractToConcreteClass(clazz);
            if (clazzes.size() == 0) {
                throw new RuntimeException("not yet supported: " + clazz);
            }
            for (int k = 0; k < clazzes.size(); k++) {
                final Class clazz2 = (Class) clazzes.get(k);
                vo = getEmptyObject(clazz2);
                result.add(vo);
                Method[] methods = clazz2.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().startsWith("set")) {
                        final Method setter = methods[i];
                        if (setter.getParameterTypes().length > 1) {
                            fail("setter with more than one parameter found: " + setter.getName());
                            continue;
                        }
                        if (clazz2 == ConditionalProofVo.class
                                && "setFormalProofLineList".equals(setter.getName())) {
                            continue;
                        }
                        final Class setClazz = setter.getParameterTypes()[0];
                        final List values = create(setClazz);
                        for (int j = 0; j < values.size(); j++) {
//                            System.out.println(setter);
                            vo = getEmptyObject(clazz2);
                            try {
                                setter.invoke(vo, new Object[] {values.get(j)});
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            result.add(vo);
                        }
                    } else if (methods[i].getName().equals("add")) {
                        final Method adder = methods[i];
                        if (adder.getParameterTypes().length > 1) {
                            final StringBuffer buffer = new StringBuffer("in class \"" + clazz
                                + "\" method \"add\" with more than one parameter found: "
                                + adder.getName());
                            for (int j = 0; j < adder.getParameterTypes().length; j++) {
                                buffer.append(" " + adder.getParameterTypes()[j]);
                            }
                            fail(buffer.toString());
                            continue;
                        }
                        final Class setClazz = adder.getParameterTypes()[0];
                        if (setClazz != clazz) {
                            final List values = create(setClazz);
                            for (int j = 0; j < values.size(); j++) {
//                                System.out.println(adder);
                                vo = getEmptyObject(clazz2);
                                try {
                                    adder.invoke(vo, new Object[] {values.get(j)});
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                result.add(vo);
                            }
                        }
                    }
                }
            }
        }
//        out.println("Creating done for " + clazz.getName());
//        out.popLevel(clazz.getName().length());
        return result;
    }
 

    /**
     * Get (if possible) empty instance of an class. This method could be overwritten to get more
     * objects.
     *
     * @param   clazz   For this class an instance is wanted.
     * @param   parent  This class has <code>clazz</code> as an attribute. Maybe <code>null</code>.
     * @param   attribute   Attribute name of parent that shall be filled.
     * @return  Just the result of the default constructor (if existing). Might be 
     *          <code>null</code>.
     * @throws  Exception   Something went wrong. Perhaps the preconditions in {@link #testAll()}
     *                      were violated.
     */
    private Object getEmptyObject(Class clazz) {

        if (clazz.equals(Element.class)) {              // application specific
            return new DefaultAtom("atom");
        }
        Constructor[] constructors = clazz.getConstructors();
        Constructor constructor = null;
        for (int j = 0; j < constructors.length; j++) {
            if (constructors[j].getParameterTypes().length == 0) {
                constructor = constructors[j];
            }
        }
        if (constructor == null) {
            return null;
        }
        try {
            return constructor.newInstance(new Object[0]);
        } catch (final Exception e) {
            System.out.println(constructor);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get name with first letter upper case.
     *
     * @param   name
     * @return  Name with first letter upper case.
     */
    public static final String getUName(final String name) {
        if (name.length() > 0) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }

    public void fail(final String message) {
        throw new RuntimeException(message);
    }

    
}
