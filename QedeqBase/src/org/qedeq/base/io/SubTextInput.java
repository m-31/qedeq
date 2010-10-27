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

    private final int start;
    private final int end;

    /**
     * Constructor.
     *
     * @param   source  Text input.
     */
    public SubTextInput(final String source) {
        super(source);
        this.start = 0;
        this.end = source.length();
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
        this.start = absoluteStart;
        this.end = absoluteEnd;
    }

    public int getAbsoluteStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getAbsolutePosition() {
        return getAbsoluteStart() + getPosition();
    }

    public void setAbsolutePosition(final int position) {
        System.out.println("p " + position);
        System.out.println("a " + getAbsoluteStart());
        System.out.println("c " + getPosition());
        System.out.println("s " + asString());
        System.out.println("1 " + asString().substring(getPosition()));
        System.out.println("2 " + asString().substring(position - getAbsoluteStart()));
        setPosition(position - getAbsoluteStart());
    }

    public String getAbsoluteSubstring(final int absoluteFrom, final int absoluteTo) {
        return getSubstring(absoluteFrom - getAbsoluteStart(), absoluteTo - getAbsoluteStart());
    }

    public SubTextInput getSubTextInput(final int absoluteFrom, final int absoluteTo) {
        return new SubTextInput(this, absoluteFrom, absoluteTo);
    }


}
