/* $Id: AbstractVoModuleTest.java,v 1.7 2008/03/27 05:12:40 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.dto.module;

import java.util.HashMap;
import java.util.Map;

import org.qedeq.kernel.base.module.Author;
import org.qedeq.kernel.base.module.AuthorList;
import org.qedeq.kernel.base.module.Axiom;
import org.qedeq.kernel.base.module.Chapter;
import org.qedeq.kernel.base.module.ChapterList;
import org.qedeq.kernel.base.module.Formula;
import org.qedeq.kernel.base.module.FunctionDefinition;
import org.qedeq.kernel.base.module.Header;
import org.qedeq.kernel.base.module.Import;
import org.qedeq.kernel.base.module.ImportList;
import org.qedeq.kernel.base.module.Latex;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.base.module.LinkList;
import org.qedeq.kernel.base.module.LiteratureItem;
import org.qedeq.kernel.base.module.LiteratureItemList;
import org.qedeq.kernel.base.module.Location;
import org.qedeq.kernel.base.module.LocationList;
import org.qedeq.kernel.base.module.Node;
import org.qedeq.kernel.base.module.NodeType;
import org.qedeq.kernel.base.module.PredicateDefinition;
import org.qedeq.kernel.base.module.Proof;
import org.qedeq.kernel.base.module.ProofList;
import org.qedeq.kernel.base.module.Proposition;
import org.qedeq.kernel.base.module.Qedeq;
import org.qedeq.kernel.base.module.Rule;
import org.qedeq.kernel.base.module.Section;
import org.qedeq.kernel.base.module.SectionList;
import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.base.module.Subsection;
import org.qedeq.kernel.base.module.SubsectionList;
import org.qedeq.kernel.base.module.SubsectionType;
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.test.AbstractValueObjectTest;

/**
 * Test helper. Gives concrete class for an interface.
 *
 * @version $Revision: 1.7 $
 * @author    Michael Meyling
 */
public abstract class AbstractVoModuleTest extends AbstractValueObjectTest {

    /** Maps abstract classes to an implementation class. */
    private static final Map interface2ConcreteClass = new HashMap();

    static {
        interface2ConcreteClass.put(Author.class, AuthorVo.class);
        interface2ConcreteClass.put(AuthorList.class, AuthorListVo.class);
        interface2ConcreteClass.put(Axiom.class, AxiomVo.class);
        interface2ConcreteClass.put(Chapter.class, ChapterVo.class);
        interface2ConcreteClass.put(ChapterList.class, ChapterListVo.class);
        interface2ConcreteClass.put(PredicateDefinition.class, PredicateDefinitionVo.class);
        interface2ConcreteClass.put(FunctionDefinition.class, FunctionDefinitionVo.class);
        interface2ConcreteClass.put(Formula.class, FormulaVo.class);
        interface2ConcreteClass.put(Header.class, HeaderVo.class);
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
        interface2ConcreteClass.put(Rule.class, RuleVo.class);
        interface2ConcreteClass.put(Section.class, SectionVo.class);
        interface2ConcreteClass.put(SectionList.class, SectionListVo.class);
        interface2ConcreteClass.put(Specification.class, SpecificationVo.class);
        interface2ConcreteClass.put(Subsection.class, SubsectionVo.class);
        interface2ConcreteClass.put(SubsectionList.class, SubsectionListVo.class);
        interface2ConcreteClass.put(Term.class, TermVo.class);
        interface2ConcreteClass.put(UsedByList.class, UsedByListVo.class);
        interface2ConcreteClass.put(VariableList.class, VariableListVo.class);

        interface2ConcreteClass.put(NodeType.class, PredicateDefinitionVo.class);
        interface2ConcreteClass.put(SubsectionType.class, NodeVo.class);
    }

    protected Class abstractToConcreteClass(final Class clazz) {
        return (Class) interface2ConcreteClass.get(clazz);
    }

}
