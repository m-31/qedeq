/* $Id: ProofListVo.java,v 1.7 2007/05/10 00:37:50 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2007,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.dto.module;

import java.util.ArrayList;
import java.util.List;

import org.qedeq.kernel.base.module.Proof;
import org.qedeq.kernel.base.module.ProofList;
import org.qedeq.kernel.utility.EqualsUtility;


/**
 * List of proofs.
 *
 * @version $Revision: 1.7 $
 * @author  Michael Meyling
 */
public class ProofListVo implements ProofList {

    /** Contains all list elements. */
    private final List list;

    /**
     * Constructs an empty list of proofs.
     */
    public ProofListVo() {
        this.list = new ArrayList();

    }

    /**
     * Add proof to this list.
     *
     * @param   proof   Proof to add.
     */
    public final void add(final ProofVo proof) {
        list.add(proof);
    }

    public final int size() {
        return list.size();
    }

    public final Proof get(final int index) {
        return (Proof) list.get(index);
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ProofListVo)) {
            return false;
        }
        final ProofListVo otherList = (ProofListVo) obj;
        if (size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!EqualsUtility.equals(get(i), otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < size(); i++) {
            hash = hash ^ (i + 1);
            if (get(i) != null) {
                hash = hash ^ get(i).hashCode();
            }
        }
        return hash;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer("Proofs:\n");
        for (int i = 0; i < size(); i++) {
            if (i != 0) {
                buffer.append("\n");
            }
            buffer.append((i + 1) + ":\t");
            buffer.append(get(i) != null ? get(i).toString() : null);
        }
        return buffer.toString();
    }

}
