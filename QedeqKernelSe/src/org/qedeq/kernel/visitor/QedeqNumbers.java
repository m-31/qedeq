package org.qedeq.kernel.visitor;

/**
 * Contains various counter values for a {@link org.qedeq.kernel.base.module.Qedeq}.
 *
 * @author  Michael Meyling
 */
public class QedeqNumbers {

    /** Number of imports. */
    private int imports;

    /** Number of chapters. */
    private int chapters;

    /** Number of subsections (within current chapter). */
    private int sections;

    /** Number of subsections (within current section). */
    private int subsectionsAndNodes;

    /** Import we currently work on (or lastly visited). */
    private int importNumber;

    /** Chapter numbering currently on? */
    private boolean chapterNumbering;

    /** Chapter number the node is within. */
    private int chapterNumber;

    /** Absolute chapter number the node is within. Includes chapters with no numbers. */
    private int absoluteChapterNumber;

    /** Section numbering currently on? */
    private boolean sectionNumbering;

    /** Section number the node is within. */
    private int sectionNumber;

    /** Absolute section number the node is within. Includes sections with no numbers. */
    private int absoluteSectionNumber;

    /** Sub section number for section. */
    private int subsectionNumber;

    /** Node number the node for section. */
    private int nodeNumber;

    /** Axioms before node (including this one). */
    private int axiomNumber;

    /** Function definitions before node (including this one). */
    private int functionDefinitionNumber;

    /** Predicate definitions before node (including this one). */
    private int predicateDefinitionNumber;

    /** Propositions before node (including this one). */
    private int propositionNumber;

    /** Rule definitions before node (including this one). */
    private int ruleNumber;

    /** No further numbering will be done. */
    private boolean finished;

    /**
     * Constructor.
     *
     * @param   imports     Number of imported QEDEQ modules.
     * @param   chapters    Number of chapters in QEDEQ module.
     */
    public QedeqNumbers(final int imports, final int chapters) {
        this.imports = imports;
        this.chapters = chapters;
    }

    /**
     * Copy constructor.
     *
     * @param   original    Original to copy values from.
     */
    public QedeqNumbers(final QedeqNumbers original) {
        imports = original.imports;
        chapters = original.chapters;
        sections = original.sections;
        subsectionsAndNodes = original.subsectionsAndNodes;
        importNumber = original.importNumber;
        chapterNumbering = original.chapterNumbering;
        chapterNumber = original.chapterNumber;
        absoluteChapterNumber = original.absoluteChapterNumber;
        sectionNumbering = original.sectionNumbering;
        sectionNumber = original.sectionNumber;
        absoluteSectionNumber = original.absoluteSectionNumber;
        subsectionNumber = original.subsectionNumber;
        axiomNumber = original.axiomNumber;
        functionDefinitionNumber = original.functionDefinitionNumber;
        predicateDefinitionNumber = original.predicateDefinitionNumber;
        propositionNumber = original.propositionNumber;
        ruleNumber = original.ruleNumber;
        finished = original.finished;
    }

    /**
     * Last import number.
     *
     * @return  Import number.
     */
    public int getImportNumber() {
        return importNumber;
    }

    /**
     * Increase import number.
     */
    public void increaseImportNumber() {
        importNumber++;
    }

    /**
     * Chapter number the node is within.
     *
     * @return  Chapter number.
     */
    public int getChapterNumber() {
        return chapterNumber;
    }

    /**
     * Increase chapter number.
     *
     * @param   sections            Number of subsections in new chapter.
     * @param   chapterNumbering    Chapter numbering on?
     */
    public void increaseChapterNumber(final int sections, final boolean chapterNumbering) {
        this.chapterNumbering = chapterNumbering;
        absoluteChapterNumber++;
        if (chapterNumbering) {
            chapterNumber++;
        }
        this.sections = sections;
        this.subsectionsAndNodes = 0;
        this.sectionNumber = 0;
        this.absoluteSectionNumber = 0;
        this.sectionNumbering = true;
        this.subsectionNumber = 0;
        this.nodeNumber = 0;
    }

    /**
     * Is chapter numbering currently on?
     *
     * @return  Chapter numbering is on.
     */
    public boolean isChapterNumbering() {
        return chapterNumbering;
    }

    /**
     * Absolute chapter number the node is within. This includes chapters with no numbers.
     *
     * @return  Chapter number.
     */
    public int getAbsouteChapterNumber() {
        return absoluteChapterNumber;
    }

    /**
     * Section number the node is within.
     *
     * @return  Section number.
     */
    public int getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Increase chapter number.
     *
     * @param   subsectionsAndNodes         Number of subsections and nodes for current section.
     * @param   sectionNumbering    Should this section be numbered?
     */
    public void increaseSectionNumber(final int subsectionsAndNodes,
            final boolean sectionNumbering) {
        this.subsectionsAndNodes = subsectionsAndNodes;
        this.sectionNumbering = sectionNumbering;
        absoluteSectionNumber++;
        if (sectionNumbering) {
            sectionNumber++;
        }
        this.subsectionsAndNodes = 0;
        subsectionNumber = 0;
        nodeNumber = 0;
    }

    /**
     * Absolute section number the node is within. This includes sections with no numbers.
     *
     * @return  Section number.
     */
    public int getAbsouteSectionNumber() {
        return absoluteSectionNumber;
    }

    /**
     * Is section numbering currently on?
     *
     * @return  Section numbering is on.
     */
    public boolean isSectionNumbering() {
        return sectionNumbering;
    }

    /**
     * Sub section number within section.
     *
     * @return  Sub section number.
     */
    public int getSubsectionNumber() {
        return subsectionNumber;
    }

    /**
     * Increase subsection number.
     */
    public void increaseSubsectionNumber() {
        subsectionNumber++;
    }

    /**
     * Node number within section.
     *
     * @return  Node number.
     */
    public int getNodeNumber() {
        return nodeNumber;
    }

    /**
     * Increase node number.
     */
    public void increaseNodeNumber() {
        nodeNumber++;
    }

    /**
     * Get number of axioms before node (including this one).
     *
     * @return  Number of axioms before node (including this one).
     */
    public int getAxiomNumber() {
        return axiomNumber;
    }

    /**
     * Increase number of axioms before node (including this one).
     */
    public void increaseAxiomNumber() {
        axiomNumber++;
    }

    /**
     * Get number of function definitions before node (including this one).
     *
     * @return  Number function definitions before node (including this one).
     */
    public int getFunctionDefinitionNumber() {
        return functionDefinitionNumber;
    }

    /**
     * Increase number of function definitions before node (including this one).
     */
    public void increaseFunctionDefinitionNumber() {
        functionDefinitionNumber++;
    }

    /**
     * Get number of predicate definitions before node (including this one).
     *
     * @return  Number of predicate definitions before node (including this one).
     */
    public int getPredicateDefinitionNumber() {
        return predicateDefinitionNumber;
    }

    /**
     * Increase number of predicate definitions before node (including this one).
     */
    public void increasePredicateDefinitionNumber() {
        predicateDefinitionNumber++;
    }

    /**
     * Get number of Propositions before node (including this one).
     *
     * @return  Get number of Propositions before node (including this one).
     */
    public int getPropositionNumber() {
        return propositionNumber;
    }

    /**
     * Increase number of predicate definitions before node (including this one).
     */
    public void increasePropositionNumber() {
        propositionNumber++;
    }

    /**
     * Get number of rule definitions before node (including this one).
     *
     * @return  Number of rule definitions before node (including this one).
     */
    public int getRuleNumber() {
        return ruleNumber;
    }

    /**
     * Increase number of rule definitions before node (including this one).
     */
    public void increaseRuleNumber() {
        ruleNumber++;
    }

    /**
     * Is there nothing more to be numbered?
     *
     * @return  There will be no more number changes.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Set if numbering has ended.
     *
     * @param   finished    Will there be no more number changes?
     */
    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    /**
     * Get calculated visit percentage.
     *
     * @return  Value between 0 and 100.
     */
    public double getVisitPercentage() {
        if (finished) {
            return 100;
        }
        double result = 0;
        if (importNumber < imports && chapterNumber == 0) {
            result = (double) importNumber / (imports + 1) / (chapters + 1);
        } else {
            result = (double) absoluteChapterNumber / (chapters + 1);
            result += (double) absoluteSectionNumber / (sections + 1) / (chapters + 1);
            result += (double) (subsectionNumber + nodeNumber)
                / (subsectionsAndNodes + 1) / (sections + 1) / (chapters + 1);
        }
        if (result > 1) {
            System.out.println(result * 100);
        }
        return 100 * result;
    }

}
