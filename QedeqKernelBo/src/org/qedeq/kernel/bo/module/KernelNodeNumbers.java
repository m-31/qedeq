package org.qedeq.kernel.bo.module;

/**
 * Contains various counter values for a {@link KernelNodeBo}.
 *
 * @author  Michael Meyling
 */
public class KernelNodeNumbers {

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

    /** Sub section number the node is within. */
    private int subsectionNumber;

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


    /**
     * Constructor.
     */
    public KernelNodeNumbers() {
        // nothing to do
    }

    /**
     * Copy constructor.
     *
     * @param   original    Original to copy values from.
     */
    public KernelNodeNumbers(final KernelNodeNumbers original) {
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
     */
    public void increaseChapterNumber() {
        chapterNumber++;
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
     * Set flag for: chapter numbering currently on?
     *
     * @param   chapterNumbering    Should the chapter(s) be counted?
     */
    public void setChapterNumbering(final boolean chapterNumbering) {
        this.chapterNumbering = chapterNumbering;
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
     * Increase absolute chapter number.
     */
    public void increaseAbsoluteChapterNumber() {
        absoluteChapterNumber++;
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
     */
    public void increaseSectionNumber() {
        sectionNumber++;
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
     * Increase absolute section number.
     */
    public void increaseAbsoluteSectionNumber() {
        absoluteSectionNumber++;
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
     * Set flag for: section numbering currently on?
     *
     * @param   sectionNumbering    Should the section(s) be counted?
     */
    public void setSectionNumbering(final boolean sectionNumbering) {
        this.sectionNumbering = sectionNumbering;
    }

    /**
     * Sub section number the node is within.
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

}
