package org.qedeq.kernel.bo.module;


/**
 * Reference to another QEDEQ module, a node, a part of a node formula, a formal proof line.
 *
 * @author  Michael Meyling
 */
public interface Reference {

    /**
     * Does the the reference point to another QEDEQ module?
     *
     * @return  The reference goes to another module.
     */
    public boolean isExternal();

    /**
     * Does the the reference point to another QEDEQ module but not to a node?
     *
     * @return  The reference goes directly to to another module.
     */
    public boolean isExternalModuleReference();

    /**
     * Get label for QEDEQ module the reference points to.
     *
     * @return  QEDEQ module label.
     */
    public String getExternalQedeqLabel();

    /**
     * Get external QEDEQ module the reference points to.
     *
     * @return  QEDEQ module.
     */
    public KernelQedeqBo getExternalQedeq();

    /**
     * Does the the reference point to a node? It might also be a sub reference to
     * a node like a part of a proposition or a formal proof line.
     *
     * @return  The reference goes to a node or to a label within a node.
     */
    public boolean isNodeReference();

    /**
     * Label of the node the reference points to.
     *
     * @return  Node label the reference points to.
     */
    public String getNodeLabel();

    /**
     * Node the reference points to. Might be in an external QEDEQ module. The reference might
     * also have a sub reference or proof line reference.
     *
     * @return  Node the reference points to.
     */
    public KernelNodeBo getNode();

    /**
     * Does the the reference point to the same node?
     *
     * @return  The reference is in the same node as the label.
     */
    public boolean isNodeLocalReference();

    /**
     * Does the the reference point to a sub reference of a node? This is a part of a node formula.
     * If this is true it can not have a proof line reference.
     *
     * @return  The reference goes to a part of a node formula.
     */
    public boolean isSubReference();

    /**
     * Sub node label the reference points to.
     *
     * @return  Sub node label the reference points to.
     */
    public String getSubLabel();

    /**
     * Does the the reference point to a proof line of a node? If this is true
     * it can not have a node sub reference.
     *
     * @return  The reference goes to a proof line.
     */
    public boolean isProofLineReference();

    /**
     * Proof line label of a node the reference points to.
     *
     * @return  Proof line label the reference points to.
     */
    public String getProofLineLabel();

}
