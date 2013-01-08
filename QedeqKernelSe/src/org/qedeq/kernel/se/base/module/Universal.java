/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2013,  Michael Meyling <mime@qedeq.org>.
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

import org.qedeq.kernel.se.base.list.Element;


/**
 * Rule of universal generalization.
 * <pre>
 *   A -&gt; B(x)
 *  -------------------
 *   A -&gt; forall x  B(x)
 * </pre>
 *
 * @author  Michael Meyling
 */
public interface Universal extends Reason {

    /**
     * Get this reason.
     *
     * @return  This reason.
     */
    public Universal getUniversal();
    /**
     * Get reference to formula. Usually this a formula of type A -&gt; B(x)
     *
     * @return  Reference to previously proved formula.
     */
    public String getReference();

    /**
     * Get free subject variable we want to quantify over.
     *
     * @return  Free subject variable.
     */
    public Element getSubjectVariable();

}
