/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.kernel.bo.service;

import org.qedeq.kernel.base.list.Atom;
import org.qedeq.kernel.base.list.Element;
import org.qedeq.kernel.base.list.ElementList;
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
import org.qedeq.kernel.base.module.Term;
import org.qedeq.kernel.base.module.UsedByList;
import org.qedeq.kernel.base.module.VariableList;
import org.qedeq.kernel.common.IllegalModuleDataException;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.ModuleContext;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.dto.list.DefaultAtom;
import org.qedeq.kernel.dto.list.DefaultElementList;
import org.qedeq.kernel.dto.module.AuthorListVo;
import org.qedeq.kernel.dto.module.AuthorVo;
import org.qedeq.kernel.dto.module.AxiomVo;
import org.qedeq.kernel.dto.module.ChapterListVo;
import org.qedeq.kernel.dto.module.ChapterVo;
import org.qedeq.kernel.dto.module.FormulaVo;
import org.qedeq.kernel.dto.module.FunctionDefinitionVo;
import org.qedeq.kernel.dto.module.HeaderVo;
import org.qedeq.kernel.dto.module.ImportListVo;
import org.qedeq.kernel.dto.module.ImportVo;
import org.qedeq.kernel.dto.module.LatexListVo;
import org.qedeq.kernel.dto.module.LatexVo;
import org.qedeq.kernel.dto.module.LinkListVo;
import org.qedeq.kernel.dto.module.LiteratureItemListVo;
import org.qedeq.kernel.dto.module.LiteratureItemVo;
import org.qedeq.kernel.dto.module.LocationListVo;
import org.qedeq.kernel.dto.module.LocationVo;
import org.qedeq.kernel.dto.module.NodeVo;
import org.qedeq.kernel.dto.module.PredicateDefinitionVo;
import org.qedeq.kernel.dto.module.ProofListVo;
import org.qedeq.kernel.dto.module.ProofVo;
import org.qedeq.kernel.dto.module.PropositionVo;
import org.qedeq.kernel.dto.module.QedeqVo;
import org.qedeq.kernel.dto.module.RuleVo;
import org.qedeq.kernel.dto.module.SectionListVo;
import org.qedeq.kernel.dto.module.SectionVo;
import org.qedeq.kernel.dto.module.SpecificationVo;
import org.qedeq.kernel.dto.module.SubsectionListVo;
import org.qedeq.kernel.dto.module.SubsectionVo;
import org.qedeq.kernel.dto.module.TermVo;
import org.qedeq.kernel.dto.module.UsedByListVo;
import org.qedeq.kernel.dto.module.VariableListVo;


/**
 * An builder for creating {@link org.qedeq.kernel.dto.module.QedeqVo}s. This builder takes
 * something that implements the QEDEQ interfaces (beginning with
 * (@link org.qedeq.kernel.base.module.Qedeq} and makes copies that are out of the package
 * <code>org.qedeq.kernel.dto.*</code>. Only elements that are not <code>null</code> are
 * copied.
 *
 * <p>
 * LATER mime 20050707: use director pattern or transfer creation methods
 *  into BOs or use visitor pattern
 *
 * @version   $Revision: 1.1 $
 * @author    Michael Meyling
 */
public class QedeqVoBuilder {

    /** QEDEQ module input object. */
    private Qedeq original;

    /** Current context during creation. */
    private ModuleContext currentContext;

    /**
     * Constructor.
     *
     * @param   address QEDEQ address.
     */
    protected QedeqVoBuilder(final ModuleAddress address) {
        this.currentContext = address.createModuleContext();
    }

    /**
     * Create {@link QedeqVo} out of an {@link Qedeq} instance.
     * The resulting object has no references to the original {@link Qedeq} instance.
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link Qedeq} instance including its referenced objects.
     *
     * @param   address     Module address.
     * @param   original    Basic QEDEQ module object.
     * @return  Created copy object.
     * @throws  ModuleDataException     Invalid data found.
     */
    public static QedeqVo createQedeq(final ModuleAddress address,
            final Qedeq original) throws ModuleDataException {
        final QedeqVoBuilder creator = new QedeqVoBuilder(address);
        QedeqVo vo = creator.create(original);
        return vo;
    }

    /**
     * Create {@linkQedeqVo} out of an {@link Qedeq} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested. The resulting business object has no references to the original
     * {@link Qedeq} instance.
     *
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link Qedeq} instance including its referenced objects.
     *
     * @param   original    Basic QEDEQ module object.
     * @return  Copied QEDEQ object.
     * @throws  IllegalModuleDataException  Basic semantic error occurred.
     */
    protected final QedeqVo create(final Qedeq original) throws IllegalModuleDataException {
        this.original = original;
        getCurrentContext().setLocationWithinModule("");
        QedeqVo qedeq;
        if (original == null) {
            qedeq = null;
            return qedeq;
        }
        qedeq = new QedeqVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (original.getHeader() != null) {
            getCurrentContext().setLocationWithinModule(context + "getHeader()");
            qedeq.setHeader(create(original.getHeader()));
        }
        if (original.getChapterList() != null) {
            getCurrentContext().setLocationWithinModule(context + "getChapterList()");
            qedeq.setChapterList(create(original.getChapterList()));
        }
        if (original.getLiteratureItemList() != null) {
            getCurrentContext().setLocationWithinModule(context + "getLiteratureItemList()");
            qedeq.setLiteratureItemList(create(original.getLiteratureItemList()));
        }
        return qedeq;
    }

    /**
     * Create {@link HeaderVo} out of an {@link Header} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested. The resulting business object has no references to the original
     * {@link Header} instance.
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link Header} instance including its referenced objects.
     *
     * @param   header  Basic header object.
     * @return  Filled header business object. Is equal to the parameter <code>header</code>.
     * @throws  IllegalModuleDataException  Basic semantic error occurred.
     */
    private final HeaderVo create(final Header header)
            throws IllegalModuleDataException {
        if (header == null) {
            return null;
        }
        final HeaderVo h = new HeaderVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (header.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            h.setTitle(create(header.getTitle()));
        }
        if (header.getAuthorList() != null) {
            setLocationWithinModule(context + ".getAuthorList()");
            h.setAuthorList(create(header.getAuthorList()));
        }
        if (header.getSummary() != null) {
            setLocationWithinModule(context + ".getSummary()");
            h.setSummary(create(header.getSummary()));
        }
        if (header.getEmail() != null) {
            setLocationWithinModule(context + ".getEmail()");
            h.setEmail(header.getEmail());
        }
        if (header.getSpecification() != null) {
            setLocationWithinModule(context + ".getSpecification()");
            h.setSpecification(create(header.getSpecification()));
        }
        if (header.getImportList() != null) {
            setLocationWithinModule(context + ".getImportList()");
            h.setImportList(create(header.getImportList()));
        }
        if (header.getUsedByList() != null) {
            setLocationWithinModule(context + ".getUsedByList()");
            h.setUsedByList(create(header.getUsedByList()));
        }
        setLocationWithinModule(context);
        return h;
    }

    /**
     * Create {@link UsedByListVo} out of an {@link UsedByList} instance.
     * During that procedure some basic checking is done. E.g. the uniqueness of entries
     * is tested. The resulting business object has no references to the original
     * {@link UsedByList} instance.
     * <p>
     * During the creation process the caller must assert that no modifications are made
     * to the {@link UsedByList} instance including its referenced objects.
     *
     * @param   usedByList  Basic header object.
     * @return  Filled used by business object. Is equal to the parameter <code>usedByList</code>.
     */
    private final UsedByListVo create(final UsedByList usedByList) {
        if (usedByList == null) {
            return null;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final UsedByListVo list = new UsedByListVo();
        for (int i = 0; i < usedByList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(usedByList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final ImportListVo create(final ImportList importList) {
        if (importList == null) {
            return null;
        }
        final String context = getCurrentContext().getLocationWithinModule();
        final ImportListVo list = new ImportListVo();
        for (int i = 0; i < importList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(importList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final ImportVo create(final Import imp) {
        if (imp == null) {
            return null;
        }
        final ImportVo i = new ImportVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (imp.getLabel() != null) {
            setLocationWithinModule(context + ".getLabel()");
            i.setLabel(imp.getLabel());
        }
        if (imp.getSpecification() != null) {
            setLocationWithinModule(context + ".getSpecification()");
            i.setSpecification(create(imp.getSpecification()));
        }
        setLocationWithinModule(context);
        return i;
    }

    private final SpecificationVo create(final Specification specification) {
        if (specification == null) {
            return null;
        }
        final SpecificationVo s = new SpecificationVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (specification.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            s.setName(specification.getName());
        }
        if (specification.getRuleVersion() != null) {
            setLocationWithinModule(context + ".getRuleVersion()");
            s.setRuleVersion(specification.getRuleVersion());
        }
        if (specification.getLocationList() != null) {
            setLocationWithinModule(context + ".getLocationList()");
            s.setLocationList(create(specification.getLocationList()));
        }
        setLocationWithinModule(context);
        return s;
    }

    private final LocationListVo create(final LocationList locationList) {
        if (locationList == null) {
            return null;
        }
        final LocationListVo list = new LocationListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < locationList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(locationList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final LocationVo create(final Location location) {
        if (location == null) {
            return null;
        }
        final LocationVo loc = new LocationVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (location.getLocation() != null) {
            setLocationWithinModule(context + ".getLocation()");
            loc.setLocation(location.getLocation());
        }
        setLocationWithinModule(context);
        return loc;
    }

    private final AuthorListVo create(final AuthorList authorList) {
        if (authorList == null) {
            return null;
        }
        final AuthorListVo list = new AuthorListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < authorList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(authorList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final AuthorVo create(final Author author) {
        if (author == null) {
            return null;
        }
        final AuthorVo a = new AuthorVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (author.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            a.setName(create(author.getName()));
        }
        if (author.getEmail() != null) {
            setLocationWithinModule(context + ".getEmail()");
            a.setEmail(author.getEmail());
        }
        setLocationWithinModule(context);
        return a;
    }

    private final ChapterListVo create(final ChapterList chapterList)
            throws IllegalModuleDataException {
        if (chapterList == null) {
            return null;
        }
        final ChapterListVo list = new ChapterListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < chapterList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(chapterList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final ChapterVo create(final Chapter chapter)
            throws IllegalModuleDataException {
        if (chapter == null) {
            return null;
        }
        final ChapterVo c = new ChapterVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (chapter.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            c.setTitle(create(chapter.getTitle()));
        }
        if (chapter.getNoNumber() != null) {
            setLocationWithinModule(context + ".getNoNumber()");
            c.setNoNumber(chapter.getNoNumber());
        }
        if (chapter.getIntroduction() != null) {
            setLocationWithinModule(context + ".getIntroduction()");
            c.setIntroduction(create(chapter.getIntroduction()));
        }
        if (chapter.getSectionList() != null) {
            setLocationWithinModule(context + ".getSectionList()");
            c.setSectionList(create(chapter.getSectionList()));
        }
        setLocationWithinModule(context);
        return c;
    }

    private LiteratureItemListVo create(final LiteratureItemList literatureItemList)
            throws IllegalModuleDataException {
        if (literatureItemList == null) {
            return null;
        }
        final LiteratureItemListVo list = new LiteratureItemListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < literatureItemList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(literatureItemList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private LiteratureItemVo create(final LiteratureItem item)
            throws IllegalModuleDataException {
        if (item == null) {
            return null;
        }
        final LiteratureItemVo it = new LiteratureItemVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (item.getLabel() != null) {
            setLocationWithinModule(context + ".getLabel()");
            it.setLabel(item.getLabel());
        }
        if (item.getItem() != null) {
            setLocationWithinModule(context + ".getItem()");
            it.setItem(create(item.getItem()));
        }
        setLocationWithinModule(context);
        return it;

    }

    private final SectionListVo create(final SectionList sectionList)
            throws IllegalModuleDataException {
        if (sectionList == null) {
            return null;
        }
        final SectionListVo list = new SectionListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < sectionList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(sectionList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final SectionVo create(final Section section)
            throws IllegalModuleDataException {
        if (section == null) {
            return null;
        }
        final SectionVo s = new SectionVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (section.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            s.setTitle(create(section.getTitle()));
        }
        if (section.getNoNumber() != null) {
            setLocationWithinModule(context + ".getNoNumber()");
            s.setNoNumber(section.getNoNumber());
        }
        if (section.getIntroduction() != null) {
            setLocationWithinModule(context + ".getIntroduction()");
            s.setIntroduction(create(section.getIntroduction()));
        }
        if (section.getSubsectionList() != null) {
            setLocationWithinModule(context + ".getSubsectionList()");
            s.setSubsectionList(create(section.getSubsectionList()));
        }
        setLocationWithinModule(context);
        return s;
    }

    private final SubsectionListVo create(final SubsectionList subsectionList)
            throws IllegalModuleDataException {
        if (subsectionList == null) {
            return null;
        }
        final SubsectionListVo list = new SubsectionListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < subsectionList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            // TODO mime 20050608: here the Subsection context is type dependently specified
            if (subsectionList.get(i) instanceof Subsection) {
                list.add(create((Subsection) subsectionList.get(i)));
            } else if (subsectionList.get(i) instanceof Node) {
                list.add(create((Node) subsectionList.get(i)));
            } else {
                throw new IllegalArgumentException("unexpected subsection type: "
                    + subsectionList.get(i).getClass());
            }
        }
        setLocationWithinModule(context);
        return list;
    }

    private final SubsectionVo create(final Subsection subsection) {
        if (subsection == null) {
            return null;
        }
        final SubsectionVo s = new SubsectionVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (subsection.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            s.setTitle(create(subsection.getTitle()));
        }
        if (subsection.getLevel() != null) {
            setLocationWithinModule(context + ".getLevel()");
            s.setLevel(subsection.getLevel());
        }
        if (subsection.getLatex() != null) {
            setLocationWithinModule(context + ".getLatex()");
            s.setLatex(create(subsection.getLatex()));
        }
        setLocationWithinModule(context);
        return s;
    }

    private final NodeVo create(final Node node)
            throws IllegalModuleDataException {
        if (node == null) {
            return null;
        }
        final NodeVo n = new NodeVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (node.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            n.setName(create(node.getName()));
        }
        if (node.getId() != null) {
            setLocationWithinModule(context + ".getId()");
            n.setId(node.getId());
        }
        if (node.getLevel() != null) {
            setLocationWithinModule(context + ".getLevel()");
            n.setLevel(node.getLevel());
        }
        if (node.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            n.setTitle(create(node.getTitle()));
        }
        if (node.getPrecedingText() != null) {
            setLocationWithinModule(context + ".getPrecedingText()");
            n.setPrecedingText(create(node.getPrecedingText()));
        }
        if (node.getNodeType() != null) {
            setLocationWithinModule(context + ".getNodeType()");
            if (node.getNodeType() instanceof Axiom) {
                setLocationWithinModule(context + ".getNodeType().getAxiom()");
                n.setNodeType(create((Axiom) node.getNodeType()));
            } else if (node.getNodeType() instanceof PredicateDefinition) {
                setLocationWithinModule(context + ".getNodeType().getPredicateDefinition()");
                n.setNodeType(create((PredicateDefinition) node.getNodeType()));
            } else if (node.getNodeType() instanceof FunctionDefinition) {
                setLocationWithinModule(context + ".getNodeType().getFunctionDefinition()");
                n.setNodeType(create((FunctionDefinition) node.getNodeType()));
            } else if (node.getNodeType() instanceof Proposition) {
                setLocationWithinModule(context + ".getNodeType().getProposition()");
                n.setNodeType(create((Proposition) node.getNodeType()));
            } else if (node.getNodeType() instanceof Rule) {
                setLocationWithinModule(context + ".getNodeType().getRule()");
                n.setNodeType(create((Rule) node.getNodeType()));
            } else {
                throw new IllegalArgumentException("unexpected node type: "
                    + node.getNodeType().getClass());
            }
        }
        if (node.getSucceedingText() != null) {
            setLocationWithinModule(context + ".getSucceedingText()");
            n.setSucceedingText(create(node.getSucceedingText()));
        }
        setLocationWithinModule(context);
        return n;
    }

    private final AxiomVo create(final Axiom axiom) {
        if (axiom == null) {
            return null;
        }
        final AxiomVo a = new AxiomVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (axiom.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            a.setFormula(create(axiom.getFormula()));
        }
        if (axiom.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            a.setDescription(create(axiom.getDescription()));
        }
        setLocationWithinModule(context);
        return a;
    }

    private final PredicateDefinitionVo create(final PredicateDefinition definition) {
        if (definition == null) {
            return null;
        }
        final PredicateDefinitionVo d = new PredicateDefinitionVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (definition.getLatexPattern() != null) {
            setLocationWithinModule(context + ".getLatexPattern()");
            d.setLatexPattern(definition.getLatexPattern());
        }
        if (definition.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            d.setName(definition.getName());
        }
        if (definition.getArgumentNumber() != null) {
            setLocationWithinModule(context + ".getArgumentNumber()");
            d.setArgumentNumber(definition.getArgumentNumber());
        }
        if (definition.getVariableList() != null) {
            setLocationWithinModule(context + ".getVariableList()");
            d.setVariableList(create(definition.getVariableList()));
        }
        if (definition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            d.setFormula(create(definition.getFormula()));
        }
        if (definition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            d.setDescription(create(definition.getDescription()));
        }
        setLocationWithinModule(context);
        return d;
    }

    private final FunctionDefinitionVo create(final FunctionDefinition definition) {
        if (definition == null) {
            return null;
        }
        final FunctionDefinitionVo d = new FunctionDefinitionVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (definition.getLatexPattern() != null) {
            setLocationWithinModule(context + ".getLatexPattern()");
            d.setLatexPattern(definition.getLatexPattern());
        }
        if (definition.getArgumentNumber() != null) {
            setLocationWithinModule(context + ".getArgumentNumber()");
            d.setArgumentNumber(definition.getArgumentNumber());
        }
        if (definition.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            d.setName(definition.getName());
        }
        if (definition.getVariableList() != null) {
            setLocationWithinModule(context + ".getVariableList()");
            d.setVariableList(create(definition.getVariableList()));
        }
        if (definition.getTerm() != null) {
            setLocationWithinModule(context + ".getTerm()");
            d.setTerm(create(definition.getTerm()));
        }
        if (definition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            d.setDescription(create(definition.getDescription()));
        }
        setLocationWithinModule(context);
        return d;
    }

    private final PropositionVo create(final Proposition proposition) {
        if (proposition == null) {
            return null;
        }
        final PropositionVo p = new PropositionVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (proposition.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            p.setFormula(create(proposition.getFormula()));
        }
        if (proposition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            p.setDescription(create(proposition.getDescription()));
        }
        if (proposition.getProofList() != null) {
            setLocationWithinModule(context + ".getProofList()");
            p.setProofList(create(proposition.getProofList()));
        }
        setLocationWithinModule(context);
        return p;
    }

    private final RuleVo create(final Rule rule) {
        if (rule == null) {
            return null;
        }
        final RuleVo r = new RuleVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (rule.getName() != null) {
            setLocationWithinModule(context + ".getName()");
            r.setName(rule.getName());
        }
        if (rule.getLinkList() != null) {
            setLocationWithinModule(context + ".getLinkList()");
            r.setLinkList(create(rule.getLinkList()));
        }
        if (rule.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            r.setDescription(create(rule.getDescription()));
        }
        if (rule.getProofList() != null) {
            setLocationWithinModule(context + ".getProofList()");
            r.setProofList(create(rule.getProofList()));
        }
        setLocationWithinModule(context);
        return r;
    }

    private final LinkListVo create(final LinkList linkList) {
        if (linkList == null) {
            return null;
        }
        final LinkListVo list = new LinkListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < linkList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(linkList.get(i));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final VariableListVo create(final VariableList variableList) {
        if (variableList == null) {
            return null;
        }
        final VariableListVo list = new VariableListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < variableList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(variableList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final ProofListVo create(final ProofList proofList) {
        if (proofList == null) {
            return null;
        }
        final ProofListVo list = new ProofListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < proofList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(proofList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final ProofVo create(final Proof proof) {
        if (proof == null) {
            return null;
        }
        final ProofVo p = new ProofVo();
        final String context = getCurrentContext().getLocationWithinModule();
        setLocationWithinModule(context + ".getKind()");
        p.setKind(proof.getKind());
        setLocationWithinModule(context + ".getLevel()");
        p.setLevel(proof.getLevel());
        setLocationWithinModule(context);
        if (proof.getNonFormalProof() != null) {
            setLocationWithinModule(context + ".getNonFormalProof()");
            p.setNonFormalProof(create(proof.getNonFormalProof()));
        }
        setLocationWithinModule(context);
        return p;
    }

    private final FormulaVo create(final Formula formula) {
        if (formula == null) {
            return null;
        }
        final FormulaVo f = new FormulaVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (formula.getElement() != null) {
            setLocationWithinModule(context + ".getElement()");
            f.setElement(create(formula.getElement()));
        }
        setLocationWithinModule(context);
        return f;
    }

    private final TermVo create(final Term term) {
        if (term == null) {
            return null;
        }
        final TermVo f = new TermVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (term.getElement() != null) {
            setLocationWithinModule(context + ".getElement()");
            f.setElement(create(term.getElement()));
        }
        setLocationWithinModule(context);
        return f;
    }

    private final Element create(final Element element) {
        if (element == null) {
            return null;
        }
        final Element e;
        final String context = getCurrentContext().getLocationWithinModule();
        if (element.isList()) {
            setLocationWithinModule(context + ".getList()");
            e =  create(element.getList());
        } else if (element.isAtom()) {
//            setLocationWithinModule(context + ".getAtom()");
            return create(element.getAtom());
        } else {
            throw new RuntimeException("unknown element type: " + element);
        }
        setLocationWithinModule(context);
        return e;
    }


    private final DefaultElementList create(final ElementList list) {
        if (list == null) {
            return null;
        }
        final DefaultElementList n = new DefaultElementList(list.getOperator(), new Element[] {});
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < list.size(); i++) {
            if (list.getElement(i).isList()) {
                setLocationWithinModule(context + ".getElement(" + i + ")");
            }
            n.add(create(list.getElement(i)));
        }
        setLocationWithinModule(context);
        return n;
    }

    private final DefaultAtom create(final Atom atom) {
        if (atom == null) {
            return null;
        }
        return new DefaultAtom(atom.getString());
    }

    private final LatexListVo create(final LatexList latexList) {
        if (latexList == null) {
            return null;
        }
        final LatexListVo list = new LatexListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < latexList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(latexList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    /**
     * Creates LaTeX business object.
     *
     * @param   latex   LaTeX object.
     * @return  LaTeX business object.
     */
    private final LatexVo create(final Latex latex) {
        if (latex == null) {
            return null;
        }
        final LatexVo lat = new LatexVo();
        lat.setLanguage(latex.getLanguage());
        lat.setLatex(latex.getLatex());
        return lat;
    }

    /**
     * Set location information where are we within the original module.
     *
     * @param   locationWithinModule    Location within module.
     */
    protected void setLocationWithinModule(final String locationWithinModule) {
        getCurrentContext().setLocationWithinModule(locationWithinModule);
    }

    /**
     * Get current context within original.
     *
     * @return  Current context.
     */
    protected final ModuleContext getCurrentContext() {
        return currentContext;
    }

    /**
     * Get original QEDEQ module.
     *
     * @return  Original QEDEQ module.
     */
    protected final Qedeq getQedeqOriginal() {
        return original;
    }

}
