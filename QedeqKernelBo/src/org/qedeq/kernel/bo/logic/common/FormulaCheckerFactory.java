package org.qedeq.kernel.bo.logic.common;


/**
 * Can create a {@link FormulaChecker}.
 *
 * @author  Michael Meyling
 */
public interface FormulaCheckerFactory {

    /**
     * Create a {@link FormulaChecker}.
     *
     * @return  Instance that can check formulas for correctness.
     */
    public FormulaChecker createFormulaChecker();

}
