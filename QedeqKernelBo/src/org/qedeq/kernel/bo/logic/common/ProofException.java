package org.qedeq.kernel.bo.logic.common;

import org.qedeq.kernel.se.base.module.FormalProofLineList;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;

/**
 * Indicates we found something or have to abandon the search.
 *
 * @author  Michael Meyling.
 *
 */
public abstract class ProofException extends ModuleDataException {

    /** Proof lines found. If any. Might be <code>null</code>. */
    private final FormalProofLineList lines;

    /**
     * Constructor.
     *
     * @param   errorCode   Error code of this message.
     * @param   message     Error message.
     * @param   lines       Current proof lines.
     * @param   context     Error location.
     */
    public ProofException(final int errorCode, final String message, final FormalProofLineList lines,
            final ModuleContext context) {
        super(errorCode, message, context);
        this.lines = lines;
    }

    /**
     * Get proof lines we found. If any.
     *
     * @return  Found proof lines. Might be <code>null</code>.
     */
    public FormalProofLineList getProofLines() {
        return lines;
    }

}
