/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.base.io;



/**
 * Wraps a text output stream.
 *
 * @author  Michael Meyling
 */
public class SubTextInput extends TextInput {

    /** Absolute start of this input relative to base {@link SubTextInput}. */
    private final int absoluteStart;

    /** Absolute end of this input relative to base {@link SubTextInput}. */
    private final int absoluteEnd;

    /**
     * Constructor.
     *
     * @param   source  Text input.
     */
    public SubTextInput(final String source) {
        super(source);
        this.absoluteStart = 0;
        this.absoluteEnd = source.length();
    }

    /**
     * Constructor.
     *
     * @param   original    Parent input.
     * @param   absoluteStart       Input part starts here.
     * @param   absoluteEnd         Input part ends here.
     */
    public SubTextInput(final SubTextInput original, final int absoluteStart, final int absoluteEnd) {
        super(original.getAbsoluteSubstring(absoluteStart, absoluteEnd));
        this.absoluteStart = absoluteStart;
        this.absoluteEnd = absoluteEnd;
    }

    /**
     * Get the absolute start position of the current {@link SubTextInput}.
     *
     * @return  Absolute start position.
     */
    public int getAbsoluteStart() {
        return absoluteStart;
    }

    /**
     * Get the absolute end position of the current {@link SubTextInput}.
     *
     * @return  Absolute end position.
     */
    public int getAbsoluteEnd() {
        return absoluteEnd;
    }

    /**
     * Get the absolute position of the current position.
     *
     * @return  Absolute position of current position.
     */
    public int getAbsolutePosition() {
        return getAbsoluteStart() + getPosition();
    }

    /**
     * Set the current position by calculating the relative position
     * from the given absolute position.
     *
     * @param   absolutePosition    This should be the absolute position.
     */
    public void setAbsolutePosition(final int absolutePosition) {
        setPosition(absolutePosition - getAbsoluteStart());
    }

    /**
     * Get sub string of source. The given parameters have values for the underlying original
     * SubTextInput at the base.
     *
     * @param absoluteFrom  Absolute start of sub string.
     * @param absoluteTo    Absolute end of sub string.
     * @return  Sub string.
     */
    public String getAbsoluteSubstring(final int absoluteFrom, final int absoluteTo) {
        return getSubstring(absoluteFrom - getAbsoluteStart(), absoluteTo - getAbsoluteStart());
    }

    /**
     * Get sub string of source as a new {@link SubTextInput}. The given parameters have
     * values for the underlying original SubTextInput at the base.
     *
     * @param absoluteFrom  Absolute start of sub string.
     * @param absoluteTo    Absolute end of sub string.
     * @return  Sub string.
     */
    public SubTextInput getSubTextInput(final int absoluteFrom, final int absoluteTo) {
        return new SubTextInput(this, absoluteFrom, absoluteTo);
    }

}
