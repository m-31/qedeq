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

package org.qedeq.kernel.xml.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.kernel.common.SourcePosition;


/**
 * Simple XPath like description of a location in an XML file.
 *
 * @author    Michael Meyling
 */
public class SimpleXPath {

    /** List with element names. */
    private final List elements;

    /** List with element occurrence numbers. */
    private final List numbers;

    /** Attribute of element. */
    private String attribute;

    /** Starting position in source. */
    private SourcePosition start;

    /** Ending position in source. */
    private SourcePosition end;

    /** Starting position within element (if below zero). */
    private final int position;

    /** Starting length within element (if below zero). */
    private final int length;

    /**
     * Constructor with simple XPath string as parameter.
     * This is not the standard XPath definition but it is similar to a subset of
     * the abbreviation XPath notation.
     * <p>
     * <code>/element1/element2[3]@attribute</code> is an example for such
     * a notation. This selects from the first occurrence of <code>element1</code>
     * and from the third occurrence of subnode <code>element2</code> the attribute
     * <code>attribute</code>. The attribute is optional. It is always exactly one node or
     * the attribute of one node specified.
     * <p>
     * The general syntax could be described as follows:
     * {"/"<em>element</em>"["<em>index</em>"]"}+
     * ["@"<em>attribute</em>]
     *
     *
     * @param   xpath   String with the syntax as described above. If the syntax is violated
     *                  RuntimeExceptions may occur.
     */
    public SimpleXPath(final String xpath) {
        elements = new ArrayList();
        numbers = new ArrayList();
        attribute = null;
        this.position = -1;
        this.length = -1;
        init(xpath);
    }

    /**
     * Constructor with simple XPath string as parameter.
     * @param   xpath       String with the syntax as described above. If the syntax is violated
     *                      RuntimeExceptions may occur. {@link #SimpleXPath(String)}
     * @param   position    Starting position within element (if not less than zero).
     * @param   length      Length of area within element (if not less than zero).
     */
    public SimpleXPath(final String xpath, final int position, final int length) {
        elements = new ArrayList();
        numbers = new ArrayList();
        attribute = null;
        this.position = position;
        this.length = length;
        init(xpath);
    }

    /**
     * Empty constructor.
     */
    public SimpleXPath() {
        elements = new ArrayList();
        numbers = new ArrayList();
        attribute = null;
        this.position = -1;
        this.length = -1;
    }

    /**
     * Copy constructor.
     *
     * @param   original    XPath to copy.
     */
    public SimpleXPath(final SimpleXPath original) {
        elements = new ArrayList();
        numbers = new ArrayList();
        attribute = null;
        this.position = original.position;
        this.length = original.length;
        init(original.toString());
    }

    /**
     * Copy constructor.
     *
     * @param   original    XPath to copy.
     * @param   position    Starting position within element (if not less than zero).
     * @param   length      Length of area within element (if not less than zero).
     */
    public SimpleXPath(final SimpleXPath original, final int position, final int length) {
        elements = new ArrayList();
        numbers = new ArrayList();
        attribute = null;
        this.position = position;
        this.length = length;
        init(original.toString());
    }

    /**
     * Initialize all object attributes according to XPath parameter.
     *
     * @see SimpleXPath#SimpleXPath(String)
     *
     * @param   xpath   String with the syntax as described above. If the syntax is violated
     *                  RuntimeExceptions may occur.
     */
    private void init(final String xpath) {
        if (xpath == null) {
            throw new NullPointerException();
        }
        if (xpath.length() <= 0) {
            throw new RuntimeException("XPath must not be empty");
        }
        if (xpath.charAt(0) != '/') {
            throw new RuntimeException("XPath must start with '/': " + xpath);
        }
        if (xpath.indexOf("//") >= 0) {
            throw new RuntimeException("empty tag not permitted: " + xpath);
        }
        if (xpath.endsWith("/")) {
            throw new RuntimeException("XPath must not end with '/': " + xpath);
        }
        final StringTokenizer tokenizer = new StringTokenizer(xpath, "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && token.indexOf('@') >= 0) {
                attribute = token.substring(token.indexOf('@') + 1);
                if (attribute.length() <= 0) {
                    throw new RuntimeException("empty attribute not permitted: " + xpath);
                }
                token = token.substring(0, token.indexOf('@'));
            }
            if (token.indexOf('[') < 0) {
                elements.add(token);
                numbers.add(new Integer(1));
            } else {
                final StringTokenizer getnu = new StringTokenizer(token, "[]");
                elements.add(getnu.nextToken());
                final Integer i;
                try {
                    i = new Integer(getnu.nextToken());
                } catch (RuntimeException e) {
                    throw new RuntimeException("not an integer: " + xpath, e);
                }
                if (i.intValue() <= 0) {
                    throw new RuntimeException("integer must be greater zero: " + xpath);
                }
                numbers.add(i);
            }
        }
    }

    /**
     * Get number of collected exceptions.
     *
     * @return  Number of collected exceptions.
     */
    public final int size() {
        return elements.size();
    }

    /**
     * Get <code>i</code>-th Element name.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted element name.
     */
    public final String getElementName(final int i) {
        return (String) elements.get(i);
    }

    /**
     * Get <code>i</code>-th occurrence number.
     *
     * @param   i   Starts with 0 and must be smaller than {@link #size()}.
     * @return  Wanted element occurrence number.
     */
    public final int getElementOccurrence(final int i) {
        return ((Integer) numbers.get(i)).intValue();
    }

    /**
     * Add new element to end of XPath.
     *
     * @param   elementName element to add.
     */
    public final void addElement(final String elementName) {
        attribute = null;
        elements.add(elementName);
        numbers.add(new Integer(1));
    }

    /**
     * Add new element to end of XPath.
     *
     * @param   elementName element to add.
     * @param   occurrence  Occurrence number of element. Starts with 1.
     */
    public final void addElement(final String elementName, final int occurrence) {
        attribute = null;
        elements.add(elementName);
        numbers.add(new Integer(occurrence));
    }

    /**
     * Get last XPath element name.
     *
     * @return  Last element name. Could be <code>null</code> if no elements exist.
     */
    public final String getLastElement() {
        int size = elements.size();
        if (size <= 0) {
            return null;
        }
        return (String) elements.get(size - 1);
    }

    /**
     * Get XPath element name before last.
     *
     * @return  Before last element name. Could be <code>null</code> if no more than one element
     *          exist.
     */
    public final String getBeforeLastElement() {
        int size = elements.size();
        if (size <= 1) {
            return null;
        }
        return (String) elements.get(size - 2);
    }

    /**
     * Delete last XPath element if any.
     */
    public void deleteLastElement() {
        int size = elements.size();
        if (size > 0) {
            elements.remove(size - 1);
            numbers.remove(size - 1);
            attribute = null;
        }
    }

    /**
     * Set attribute.
     *
     * @param   attribute   Attribute, maybe <code>null</code>.
     */
    public final void setAttribute(final String attribute) {
        this.attribute = attribute;
    }

    /**
     * Get attribute.
     *
     * @return  Attribute, maybe <code>null</code>.
     */
    public final String getAttribute() {
        return attribute;
    }

    /**
     * Set starting location of XPath.
     *
     * @param   position    Starting point of this XPath.
     */
    public final void setStartLocation(final SourcePosition position) {
        start = position;
    }

    /**
     * Get start location.
     *
     * @return  File position.
     */
    public final SourcePosition getStartLocation() {
        return start;
    }

    /**
     * Set ending location of XPath.
     *
     * @param   position    Ending point of this XPath.
     */
    public final void setEndLocation(final SourcePosition position) {
        end = position;
    }

    /**
     * Get end location.
     *
     * @return  File position.
     */
    public final SourcePosition getEndLocation() {
        return end;
    }

    /**
     * Is there a further restriction for this element? If so one can get the relative starting
     * position by calling {@link getRelativeStart}.
     *
     * @return  Are there further position restrictions?
     */
    public final boolean portion() {
        return position >= 0;
    }

    //FIXME javadoc
    public final int getRelativeStart() {
        return position;
    }

    //FIXME javadoc
    public final int getLength() {
        return length;
    }

    public final boolean equals(final Object obj) {
        if (!(obj instanceof SimpleXPath)) {
            return false;
        }
        final SimpleXPath other = (SimpleXPath) obj;
        if (!EqualsUtility.equals(this.getAttribute(), other.getAttribute())) {
            return false;
        }
        if (position != other.position || length != other.length) {
            return false;
        }
        return equalsElements(other);
    }

    /**
     * Are the elements and occurrences of this and another element equal? No special treatment
     * of "*" elements.
     *
     * @param   other   Compare with this object.
     * @return  Are the elements of this and the parameter object equal?
     */
    public final boolean equalsElements(final SimpleXPath other) {
        final int size = this.size();
        if (size != other.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!EqualsUtility.equals(this.getElementName(i), other.getElementName(i))) {
                return false;
            }
            if (getElementOccurrence(i) != other.getElementOccurrence(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Match the elements and occurrences of this finder object and current elements?
     * This object may contain "*" elements.
     *
     * @param   current         Compare with this current elements. These elements should not
     *                          contain "*" elements.
     * @param   currentSummary  Contains only "*" elements. This parameter must be identify the same
     *                          XPath as <code>current</code>
     * @return  Match the elements of this finder object and the parameter objects?
     */
    public final boolean matchesElements(final SimpleXPath current,
            final SimpleXPath currentSummary) {
        final int size = current.size();
        if (size != size()) {
            return false;
        }
        if (size != currentSummary.size()) {
            throw new IllegalArgumentException("summary size doesn't match");
        }

        for (int i = 0; i < size; i++) {
            if ("*".equals(getElementName(i))) {
                if (getElementOccurrence(i) != currentSummary.getElementOccurrence(i)) {
                    return false;
                }
                continue;
            }
            if (!EqualsUtility.equals(current.getElementName(i), getElementName(i))) {
                return false;
            }
            if (current.getElementOccurrence(i) != getElementOccurrence(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Match the elements and occurrences of this finder object and current elements?
     * This object may contain "*" elements. Checks only to current.size().
     *
     * @param   current         Compare with this current elements. These elements should not
     *                          contain "*" elements.
     * @param   currentSummary  Contains only "*" elements. This parameter must be identify the same
     *                          XPath as <code>current</code>
     * @return  Match the elements of this finder object and the parameter objects?
     */
    public final boolean matchesElementsBegining(final SimpleXPath current,
            final SimpleXPath currentSummary) {
        final int size = current.size();
        if (size > size()) {
            return false;
        }
        if (size != currentSummary.size()) {
            throw new IllegalArgumentException("summary size doesn't match");
        }

        for (int i = 0; i < size; i++) {
            if ("*".equals(getElementName(i))) {
                if (getElementOccurrence(i) != currentSummary.getElementOccurrence(i)) {
                    return false;
                }
                continue;
            }
            if (!EqualsUtility.equals(current.getElementName(i), getElementName(i))) {
                return false;
            }
            if (current.getElementOccurrence(i) != getElementOccurrence(i)) {
                return false;
            }
        }
        return true;
    }

    public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < size(); i++) {
            buffer.append("/");
            buffer.append(getElementName(i));
            if (getElementOccurrence(i) != 1) {
                buffer.append("[");
                buffer.append(getElementOccurrence(i));
                buffer.append("]");
            }
        }
        if (getAttribute() != null) {
            buffer.append("@");
            buffer.append(getAttribute());
        }
        return buffer.toString();
    }

    public final int hashCode() {
        int code = 0;
        if (attribute != null) {
            code ^= attribute.hashCode();
        }
        for (int i = 0; i < size(); i++) {
            code ^= i + 1;
            code ^= getElementName(i).hashCode();
            code ^= getElementOccurrence(i);
        }
        return code;
    }

}
