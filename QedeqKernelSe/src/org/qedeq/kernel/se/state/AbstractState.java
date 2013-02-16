package org.qedeq.kernel.se.state;

import org.qedeq.kernel.se.common.State;

/**
 * Represents a module state. All existing instances of this class should be unique.
 *
 * @author  Michael Meyling
 */
public abstract class AbstractState implements State {

    /** Meaning of this state. */
    private final String text;

    /** Is this state a failure? */
    private final boolean failure;

    /** Code for state. */
    private final int code;

    /**
     * Creates new module state.
     *
     * @param   text    meaning of this state, <code>null</code> is not permitted.
     * @param   failure is this a failure state?
     * @param   code    code of this state.
     * @throws  IllegalArgumentException    text == <code>null</code>
     */
    protected AbstractState(final String text, final boolean failure, final int code) {
        this.text = text;
        if (this.text == null) {
            throw new IllegalArgumentException("text==null");
        }
        this.failure = failure;
        this.code = code;
    }

    public String getText() {
        return this.text;
    }

    public boolean isFailure() {
        return this.failure;
    }

    public int getCode() {
        return this.code;
    }

    public String toString() {
        return this.text;
    }

    public int hashCode() {
        return this.text.hashCode();
    }

    public boolean equals(final Object obj) {
        // every instance is unique
        return (this == obj);
    }

}
