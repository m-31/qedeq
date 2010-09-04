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
     * @param   original    Original to copy from.
     */
    public KernelNodeNumbers(final KernelNodeNumbers original) {
        chapterNumbering = original.chapterNumbering;
        chapterNumber = original.chapterNumber;
        axiomNumber = original.axiomNumber;
        functionDefinitionNumber = original.functionDefinitionNumber;
        predicateDefinitionNumber = original.predicateDefinitionNumber;
        propositionNumber = original.propositionNumber;
        ruleNumber = original.ruleNumber;
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
     * Is chapter numbering currently on?
     *
     * @return  Chapter numbering is on.
     */
    public boolean isChapterNumbering() {
        return chapterNumbering;
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
