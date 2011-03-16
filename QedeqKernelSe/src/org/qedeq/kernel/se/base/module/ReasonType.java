/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2011,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.se.base.module;


/**
 * Encapsulates all rules of interference.
 *
 * @author  Michael Meyling
 */
public interface ReasonType {

    /**
     * Untyped reason.
     *
     * @return  Reason.
     */
    public Reason getReason();

    /**
     * Get ModusPonens reason for deriving this line.
     *
     * @return  Reason.
     */
    public ModusPonens getModusPonens();

    /**
     * Get Addition reason for deriving this line.
     *
     * @return  Reason.
     */
    public Add getAdd();

    /**
     * Get Rename bound subject variable reason for deriving this line.
     *
     * @return  Reason.
     */
    public Rename getRename();

    /**
     * Get Substitute free subject variable reason for deriving this line.
     *
     * @return  Reason.
     */
    public SubstFree getSubstFree();

    /**
     * Get Substitute function variable reason for deriving this line.
     *
     * @return  Reason.
     */
    public SubstFunc getSubstFunc();

    /**
     * Get Substitute predicate variable reason for deriving this line.
     *
     * @return  Reason.
     */
    public SubstPred getSubstPred();

    /**
     * Get Existential generalization reason for deriving this line.
     *
     * @return  Reason.
     */
    public Existential getExistential();

    /**
     * Get Universal generalization reason for deriving this line.
     *
     * @return  Reason.
     */
    public Universal getUniversal();

}
