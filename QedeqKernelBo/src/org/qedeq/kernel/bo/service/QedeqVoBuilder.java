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

package org.qedeq.kernel.bo.service;

import org.qedeq.kernel.se.base.list.Atom;
import org.qedeq.kernel.se.base.list.Element;
import org.qedeq.kernel.se.base.list.ElementList;
import org.qedeq.kernel.se.base.module.Add;
import org.qedeq.kernel.se.base.module.Author;
import org.qedeq.kernel.se.base.module.AuthorList;
import org.qedeq.kernel.se.base.module.Axiom;
import org.qedeq.kernel.se.base.module.Chapter;
import org.qedeq.kernel.se.base.module.ChapterList;
import org.qedeq.kernel.se.base.module.Existential;
import org.qedeq.kernel.se.base.module.FormalProof;
import org.qedeq.kernel.se.base.module.FormalProofLine;
import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.base.module.FormalProofList;
import org.qedeq.kernel.se.base.module.Formula;
import org.qedeq.kernel.se.base.module.FunctionDefinition;
import org.qedeq.kernel.se.base.module.Header;
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
import org.qedeq.kernel.se.base.module.ModusPonens;
import org.qedeq.kernel.se.base.module.Node;
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
import org.qedeq.kernel.se.base.module.SubstFree;
import org.qedeq.kernel.se.base.module.SubstFunc;
import org.qedeq.kernel.se.base.module.SubstPred;
import org.qedeq.kernel.se.base.module.Universal;
import org.qedeq.kernel.se.base.module.UsedByList;
import org.qedeq.kernel.se.common.IllegalModuleDataException;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.dto.list.DefaultAtom;
import org.qedeq.kernel.se.dto.list.DefaultElementList;
import org.qedeq.kernel.se.dto.module.AddVo;
import org.qedeq.kernel.se.dto.module.AuthorListVo;
import org.qedeq.kernel.se.dto.module.AuthorVo;
import org.qedeq.kernel.se.dto.module.AxiomVo;
import org.qedeq.kernel.se.dto.module.ChapterListVo;
import org.qedeq.kernel.se.dto.module.ChapterVo;
import org.qedeq.kernel.se.dto.module.ExistentialVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineListVo;
import org.qedeq.kernel.se.dto.module.FormalProofLineVo;
import org.qedeq.kernel.se.dto.module.FormalProofListVo;
import org.qedeq.kernel.se.dto.module.FormalProofVo;
import org.qedeq.kernel.se.dto.module.FormulaVo;
import org.qedeq.kernel.se.dto.module.FunctionDefinitionVo;
import org.qedeq.kernel.se.dto.module.HeaderVo;
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
import org.qedeq.kernel.se.dto.module.UniversalVo;
import org.qedeq.kernel.se.dto.module.UsedByListVo;


/**
 * FIXME 20110125 m31: why do we need this builder? To make copies. Why don't we take the original?
 * At least use director pattern or transfer creation methods into BOs or use visitor pattern!
 * We have lots of duplicate code here!
 * <p>
 * An builder for creating {@link org.qedeq.kernel.se.dto.module.QedeqVo}s. This builder takes
 * something that implements the QEDEQ interfaces (beginning with
 * (@link org.qedeq.kernel.base.module.Qedeq} and makes copies that are out of the package
 * <code>org.qedeq.kernel.dto.*</code>. Only elements that are not <code>null</code> are
 * copied.

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
        if (subsection.getId() != null) {
            setLocationWithinModule(context + ".getId()");
            s.setId(subsection.getId());
        }
        if (subsection.getLevel() != null) {
            setLocationWithinModule(context + ".getLevel()");
            s.setLevel(subsection.getLevel());
        }
        if (subsection.getTitle() != null) {
            setLocationWithinModule(context + ".getTitle()");
            s.setTitle(create(subsection.getTitle()));
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
            } else if (node.getNodeType() instanceof InitialPredicateDefinition) {
                setLocationWithinModule(context + ".getNodeType().getInitialPredicateDefinition()");
                n.setNodeType(create((InitialPredicateDefinition) node.getNodeType()));
            } else if (node.getNodeType() instanceof PredicateDefinition) {
                setLocationWithinModule(context + ".getNodeType().getPredicateDefinition()");
                n.setNodeType(create((PredicateDefinition) node.getNodeType()));
            } else if (node.getNodeType() instanceof InitialFunctionDefinition) {
                setLocationWithinModule(context + ".getNodeType().getInitialFunctionDefinition()");
                n.setNodeType(create((InitialFunctionDefinition) node.getNodeType()));
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
                throw new IllegalModuleDataException(ServiceErrors.RUNTIME_ERROR_CODE,
                    ServiceErrors.RUNTIME_ERROR_TEXT + " "
                    + "unexpected node type: " + node.getNodeType().getClass(),
                    getCurrentContext());
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
        if (axiom.getDefinedOperator() != null) {
            setLocationWithinModule(context + ".getDefinedOperator()");
            a.setDefinedOperator(axiom.getDefinedOperator());
        }
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

    private final InitialPredicateDefinitionVo create(final InitialPredicateDefinition definition) {
        if (definition == null) {
            return null;
        }
        final InitialPredicateDefinitionVo d = new InitialPredicateDefinitionVo();
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
        if (definition.getPredCon() != null) {
            setLocationWithinModule(context + ".getPredCon()");
            d.setPredCon(create(definition.getPredCon()));
        }
        if (definition.getDescription() != null) {
            setLocationWithinModule(context + ".getDescription()");
            d.setDescription(create(definition.getDescription()));
        }
        setLocationWithinModule(context);
        return d;
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

    private final InitialFunctionDefinitionVo create(final InitialFunctionDefinition definition) {
        if (definition == null) {
            return null;
        }
        final InitialFunctionDefinitionVo d = new InitialFunctionDefinitionVo();
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
        if (definition.getFunCon() != null) {
            setLocationWithinModule(context + ".getFunCon()");
            d.setFunCon(create(definition.getFunCon()));
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
        if (proposition.getFormalProofList() != null) {
            setLocationWithinModule(context + ".getFormalProofList()");
            p.setFormalProofList(create(proposition.getFormalProofList()));
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

    private final FormalProofListVo create(final FormalProofList proofList) {
        if (proofList == null) {
            return null;
        }
        final FormalProofListVo list = new FormalProofListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < proofList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(proofList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final FormalProofVo create(final FormalProof proof) {
        if (proof == null) {
            return null;
        }
        final FormalProofVo p = new FormalProofVo();
        final String context = getCurrentContext().getLocationWithinModule();
        setLocationWithinModule(context);
        if (proof.getPrecedingText() != null) {
            setLocationWithinModule(context + ".getPrecedingText()");
            p.setPrecedingText(create(proof.getPrecedingText()));
        }
        if (proof.getFormalProofLineList() != null) {
            setLocationWithinModule(context + ".getFormalProofLineList()");
            p.setFormalProofLineList(create(proof.getFormalProofLineList()));
        }
        if (proof.getSucceedingText() != null) {
            setLocationWithinModule(context + ".getSucceedingText()");
            p.setSucceedingText(create(proof.getSucceedingText()));
        }
        setLocationWithinModule(context);
        return p;
    }

    private final FormalProofLineListVo create(final FormalProofLineList proofList) {
        if (proofList == null) {
            return null;
        }
        final FormalProofLineListVo list = new FormalProofLineListVo();
        final String context = getCurrentContext().getLocationWithinModule();
        for (int i = 0; i < proofList.size(); i++) {
            setLocationWithinModule(context + ".get(" + i + ")");
            list.add(create(proofList.get(i)));
        }
        setLocationWithinModule(context);
        return list;
    }

    private final FormalProofLineVo create(final FormalProofLine proofLine) {
        if (proofLine == null) {
            return null;
        }
        final FormalProofLineVo line = new FormalProofLineVo();
        final String context = getCurrentContext().getLocationWithinModule();
        if (proofLine.getLabel() != null) {
            setLocationWithinModule(context + ".getLabel()");
            line.setLabel(proofLine.getLabel());
        }
        if (proofLine.getFormula() != null) {
            setLocationWithinModule(context + ".getFormula()");
            line.setFormula(create(proofLine.getFormula()));
        }
        if (proofLine.getReason() != null) {
            final Reason reason = proofLine.getReason();
            Reason result = null;
            if (reason instanceof ModusPonens) {
                setLocationWithinModule(context + ".getModusPonens()");
                final ModusPonens r = (ModusPonens) reason;
                result = new ModusPonensVo(r.getReference1(), r.getReference2());
            } else if (reason instanceof Add) {
                setLocationWithinModule(context + ".getAdd()");
                final Add r = (Add) reason;
                result = new AddVo(r.getReference());
            } else if (reason instanceof Rename) {
                setLocationWithinModule(context + ".getRename()");
                final Rename r = (Rename) reason;
                result = new RenameVo(r.getReference(), r.getOriginalSubjectVariable(),
                    r.getReplacementSubjectVariable(), r.getOccurrence());
            } else if (reason instanceof SubstFree) {
                setLocationWithinModule(context + ".getSubstFree()");
                final SubstFree r = (SubstFree) reason;
                result = new SubstFreeVo(r.getReference(), r.getSubjectVariable(),
                    r.getSubstituteTerm());
            } else if (reason instanceof SubstFunc) {
                setLocationWithinModule(context + ".getSubstFunc()");
                final SubstFunc r = (SubstFunc) reason;
                result = new SubstFuncVo(r.getReference(), r.getFunctionVariable(),
                    r.getSubstituteTerm());
            } else if (reason instanceof SubstPred) {
                setLocationWithinModule(context + ".getSubstPred()");
                final SubstPred r = (SubstPred) reason;
                result = new SubstPredVo(r.getReference(), r.getPredicateVariable(),
                    r.getSubstituteFormula());
            } else if (reason instanceof Existential) {
                setLocationWithinModule(context + ".getExistential()");
                final Existential r = (Existential) reason;
                result = new ExistentialVo(r.getReference(), r.getSubjectVariable());
            } else if (reason instanceof Universal) {
                setLocationWithinModule(context + ".getUniversal()");
                final Universal r = (Universal) reason;
                result = new UniversalVo(r.getReference(), r.getSubjectVariable());
            } else {
                throw new RuntimeException("unknown reason class: " + reason.getClass());
            }
            line.setReason(result);
        }
        setLocationWithinModule(context);
        return line;
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
