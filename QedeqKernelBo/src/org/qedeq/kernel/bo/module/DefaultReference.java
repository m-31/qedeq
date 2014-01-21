/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.module;

import org.qedeq.base.utility.EqualsUtility;


/**
 * A reference to a QEDEQ module or one of its parts.
 *
 * @author  Michael Meyling
 */
public class DefaultReference implements Reference {

    /** In this node the reference is in. */
    private final KernelNodeBo nodeCurrent;

    /** To this QEDEQ module the reference points. */
    private final KernelQedeqBo qedeqGoal;

    /** Label for the external QEDEQ module the reference points to. */
    private final String qedeqGoalLabel;

    /** To this node the reference points to. */
    private final KernelNodeBo nodeGoal;

    /** Label for the node the reference points to. */
    private final String nodeGoalLabel;

    /** To this sub node label the reference points. */
    private final String subLabel;

    /** To this proof line label the reference points to. */
    private final String proofLineLabel;

    /**
     * Constructor.
     *
     * @param   nodeCurrent     In this node the reference is in.
     * @param   qedeqGoal       To this external QEDEQ module the reference points to.
     * @param   qedeqGoalLabel  Label for the external QEDEQ module the reference points to.
     * @param   nodeGoal        To this node the reference points to.
     * @param   nodeGoalLabel   Label for the node the reference points to.
     * @param   subLabel        To this sub node label the reference points.
     * @param   proofLineLabel  To this proof line label the reference points to.
     */
    public DefaultReference(final KernelNodeBo nodeCurrent,
            final KernelQedeqBo qedeqGoal, final String qedeqGoalLabel,
            final KernelNodeBo nodeGoal, final String nodeGoalLabel,
            final String subLabel, final String proofLineLabel) {
        this.nodeCurrent = nodeCurrent;
        this.qedeqGoal = qedeqGoal;
        this.qedeqGoalLabel = qedeqGoalLabel;
        this.nodeGoal = nodeGoal;
        this.nodeGoalLabel = nodeGoalLabel;
        this.subLabel = subLabel;
        this.proofLineLabel = proofLineLabel;
    }

    public boolean isExternalModuleReference() {
        return isExternal() && !isNodeReference();
    }

    public KernelQedeqBo getExternalQedeq() {
        return qedeqGoal;
    }

    public String getExternalQedeqLabel() {
        return qedeqGoalLabel;
    }

    public KernelNodeBo getNode() {
        return nodeGoal;
    }

    public String getNodeLabel() {
        return nodeGoalLabel;
    }

    public String getProofLineLabel() {
        return proofLineLabel;
    }

    public String getSubLabel() {
        return subLabel;
    }

    public boolean isExternal() {
        return qedeqGoalLabel != null && qedeqGoalLabel.length() > 0;
    }

    public boolean isNodeLocalReference() {
        return EqualsUtility.equals(nodeGoal, nodeCurrent);
    }

    public boolean isNodeReference() {
        return nodeGoalLabel != null && nodeGoalLabel.length() > 0;
    }

    public boolean isSubReference() {
        return subLabel != null && subLabel.length() > 0;
    }

    public boolean isProofLineReference() {
        return proofLineLabel != null && proofLineLabel.length() > 0;
    }

}
