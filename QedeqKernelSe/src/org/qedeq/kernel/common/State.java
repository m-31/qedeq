package org.qedeq.kernel.common;

public interface State {

    /**
     * Get meaning of module state.
     *
     * @return meaning of module state.
     */
    public String getText();

    /**
     * Is this a failure state?
     *
     * @return is this a failure state?
     */
    public boolean isFailure();

    /**
     * Get module state code.
     *
     * @return Module state.
     */
    public int getCode();

}