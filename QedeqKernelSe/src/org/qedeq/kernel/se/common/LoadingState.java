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

package org.qedeq.kernel.se.common;

/**
 * Represents a module state. Every instance of this class is unique.
 *
 * @author Michael Meyling
 */
public final class LoadingState implements State {

    /** Undefined loading state. */
    public static final LoadingState STATE_UNDEFINED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_UNDEFINED, false,
        LoadingStateDescriptions.STATE_CODE_UNDEFINED);

    /** Trying to access web address. */
    public static final LoadingState STATE_LOCATING_WITHIN_WEB = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOCATING_WITHIN_WEB, false,
        LoadingStateDescriptions.STATE_CODE_LOCATING_WITHIN_WEB);

    /** Try to access web address failed. */
    public static final LoadingState STATE_LOCATING_WITHIN_WEB_FAILED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOCATING_WITHIN_WEB_FAILED, true,
        LoadingStateDescriptions.STATE_CODE_LOCATING_WITHIN_WEB_FAILED);

    /** Loading from web address. */
    public static final LoadingState STATE_LOADING_FROM_WEB = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_WEB, false,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_WEB);

    /** Loading from web address failed. */
    public static final LoadingState STATE_LOADING_FROM_WEB_FAILED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_WEB_FAILED, true,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_WEB_FAILED);

    /** Loading from local file. */
    public static final LoadingState STATE_LOADING_FROM_LOCAL_FILE = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_LOCAL_FILE, false,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_LOCAL_FILE);

    /** Loading from local file failed. */
    public static final LoadingState STATE_LOADING_FROM_LOCAL_FILE_FAILED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_LOCAL_FILE_FAILED, true,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_LOCAL_FILE_FAILED);

    /** Loading from local file buffer. */
    public static final LoadingState STATE_LOADING_FROM_BUFFER = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_BUFFER, false,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_BUFFER);

    /** Loading from local file buffer failed. */
    public static final LoadingState STATE_LOADING_FROM_BUFFER_FAILED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_FROM_BUFFER_FAILED, true,
        LoadingStateDescriptions.STATE_CODE_LOADING_FROM_BUFFER_FAILED);

    /** Loading into memory. */
    public static final LoadingState STATE_LOADING_INTO_MEMORY = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_INTO_MEMORY, false,
        LoadingStateDescriptions.STATE_CODE_LOADING_INTO_MEMORY);

    /** Loading into memory failed. */
    public static final LoadingState STATE_LOADING_INTO_MEMORY_FAILED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADING_INTO_MEMORY_FAILED, true,
        LoadingStateDescriptions.STATE_CODE_LOADING_INTO_MEMORY_FAILED);

    /** Completely loaded. */
    public static final LoadingState STATE_LOADED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_LOADED, false,
        LoadingStateDescriptions.STATE_CODE_LOADED);

    /** Deleted. */
    public static final LoadingState STATE_DELETED = new LoadingState(
        LoadingStateDescriptions.STATE_STRING_DELETED, false,
        LoadingStateDescriptions.STATE_CODE_DELETED);

    /** meaning of this state. */
    private final String text;

    /** is this state a failure? */
    private final boolean failure;

    /** Code for state. */
    private final int code;

    /**
     * Creates new module state.
     *
     * @param text meaning of this state, <code>null</code> is not permitted.
     * @param failure is this a failure state?
     * @param code code of this state.
     * @throws IllegalArgumentException text == <code>null</code>
     */
    private LoadingState(final String text, final boolean failure, final int code) {
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

    public final boolean equals(final Object obj) {
        // every instance is unique
        return (this == obj);
    }

}
