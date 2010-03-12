package org.qedeq.kernel.bo.module;

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

    public void setChapterNumbering(boolean chapterNumbering) {
        this.chapterNumbering = chapterNumbering;
    }

    public boolean isChapterNumbering() {
        return chapterNumbering;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void increaseChapterNumber() {
        chapterNumber++;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getAxiomNumber() {
        return axiomNumber;
    }

    public void increaseAxiomNumber() {
        axiomNumber++;
    }

    public void setAxiomNumber(int axiomNumber) {
        this.axiomNumber = axiomNumber;
    }

    public int getFunctionDefinitionNumber() {
        return functionDefinitionNumber;
    }

    public void increaseFunctionDefinitionNumber() {
        functionDefinitionNumber++;
    }

    public void setFunctionDefinitionNumber(int functionDefinitionNumber) {
        this.functionDefinitionNumber = functionDefinitionNumber;
    }

    public int getPredicateDefinitionNumber() {
        return predicateDefinitionNumber;
    }

    public void increasePredicateDefinitionNumber() {
        predicateDefinitionNumber++;
    }

    public void setPredicateDefinitionNumber(int predicateDefinitionNumber) {
        this.predicateDefinitionNumber = predicateDefinitionNumber;
    }

    public int getPropositionNumber() {
        return propositionNumber;
    }

    public void increasePropositionNumber() {
        propositionNumber++;
    }

    public void setPropositionNumber(int propositionNumber) {
        this.propositionNumber = propositionNumber;
    }

    public int getRuleNumber() {
        return ruleNumber;
    }

    public void increaseRuleNumber() {
        ruleNumber++;
    }

    public void setRuleNumber(int ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

}
