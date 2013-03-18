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

package org.qedeq.kernel.se.state;

import org.qedeq.kernel.se.common.State;


/**
 * Represents a mathematical module state. All existing instances of this class are the public
 * constants of this class.
 *
 * @author  Michael Meyling
 */
public final class LoadingImportsState extends AbstractState implements State {

    /** Undefined loading state. */
    public static final LoadingImportsState STATE_UNDEFINED = new LoadingImportsState(
        LoadingImportsStateDescriptions.STATE_STRING_UNDEFINED, false,
        LoadingImportsStateDescriptions.STATE_CODE_UNDEFINED);

    /** Loading directly required (= imported) modules. */
    public static final LoadingImportsState STATE_LOADING_IMPORTS = new LoadingImportsState(
        LoadingImportsStateDescriptions.STATE_STRING_LOADING_IMPORTS, false,
        LoadingImportsStateDescriptions.STATE_CODE_LOADING_IMPORTS);

    /** Loading directly required modules failed. */
    public static final LoadingImportsState STATE_LOADING_IMPORTS_FAILED = new LoadingImportsState(
        LoadingImportsStateDescriptions.STATE_STRING_LOADING_IMPORTS_FAILED, true,
        LoadingImportsStateDescriptions.STATE_CODE_LOADING_IMPORTS_FAILED);

    /** Completely loaded. */
    public static final LoadingImportsState STATE_LOADED_IMPORTED_MODULES = new LoadingImportsState(
        LoadingImportsStateDescriptions.STATE_STRING_LOADED_IMPORTED_MODULES, false,
        LoadingImportsStateDescriptions.STATE_CODE_LOADED_IMPORTED_MODULES);


    /**
     * Creates new module state.
     *
     * @param   text    meaning of this state, <code>null</code> is not permitted.
     * @param   failure is this a failure state?
     * @param   code    code of this state.
     * @throws  IllegalArgumentException    text == <code>null</code>
     */
    private LoadingImportsState(final String text, final boolean failure, final int code) {
        super(text, failure, code);
    }

    /**
     * Are all directly required modules loaded?
     *
     * @return  Are all directly imported modules loaded?
     */
    public boolean areAllDirectlyRequiredLoaded() {
        return getCode() == STATE_LOADED_IMPORTED_MODULES.getCode();
    }

}
