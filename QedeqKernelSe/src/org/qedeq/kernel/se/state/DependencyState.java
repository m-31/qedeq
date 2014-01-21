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

package org.qedeq.kernel.se.state;

import org.qedeq.kernel.se.common.State;


/**
 * Represents a mathematical module state. All existing instances of this class are the public
 * constants of this class.
 *
 * @author  Michael Meyling
 */
public final class DependencyState extends AbstractState implements State {

    /** Undefined loading state. */
    public static final DependencyState STATE_UNDEFINED = new DependencyState(
        DependencyStateDescriptions.STATE_STRING_UNDEFINED, false,
        DependencyStateDescriptions.STATE_CODE_UNDEFINED);

    /** Loading required modules. */
    public static final DependencyState STATE_LOADING_REQUIRED_MODULES = new DependencyState(
        DependencyStateDescriptions.STATE_STRING_LOADING_REQUIRED_MODULES, false,
        DependencyStateDescriptions.STATE_CODE_LOADING_REQUIRED_MODULES);

    /** Loading required modules failed. */
    public static final DependencyState STATE_LOADING_REQUIRED_MODULES_FAILED = new DependencyState(
        DependencyStateDescriptions.STATE_STRING_LOADING_REQUIRED_MODULES_FAILED, true,
        DependencyStateDescriptions.STATE_CODE_LOADING_REQUIRED_MODULES_FAILED);

    /** Completely loaded. */
    public static final DependencyState STATE_LOADED_REQUIRED_MODULES = new DependencyState(
        DependencyStateDescriptions.STATE_STRING_LOADED_REQUIRED_MODULES, false,
        DependencyStateDescriptions.STATE_CODE_LOADED_REQUIRED_MODULES);


    /**
     * Creates new module state.
     *
     * @param   text    meaning of this state, <code>null</code> is not permitted.
     * @param   failure is this a failure state?
     * @param   code    code of this state.
     * @throws  IllegalArgumentException    text == <code>null</code>
     */
    private DependencyState(final String text, final boolean failure, final int code) {
        super(text, failure, code);
    }

    /**
     * Are all required modules loaded?
     *
     * @return  Are all required modules loaded?
     */
    public boolean areAllRequiredLoaded() {
        return getCode() == STATE_LOADED_REQUIRED_MODULES.getCode();
    }


}
