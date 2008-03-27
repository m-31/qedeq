/* $Id: Parameter.java,v 1.3 2008/03/27 05:12:44 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2008,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.rel.test.gui;

import java.io.File;
import java.util.List;

import org.qedeq.kernel.trace.Trace;


/**
 * Show and edit parameters of an application. Valid parameter types are:
 * <ul>
 * <li>Boolean</li>
 * <li>Integer</li>
 * <li>Double</li>
 * <li>String</li>
 * <li>File</li>
 * <li>List</li>
 * </ul>
 *
 * TODO mime 20060831: check for correct type class
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */
public class Parameter {

    /** This class. */
    private static final Class CLASS = Parameter.class;

    /** Parameter name. Should be an valid java variable name. */
    private String name;

    /** Parameter label. That name that is asked for. */
    private String label;

    /** Parameter type. */
    private Class type;

    /** Comment for this parameter. */
    private String comment;

    /** Parameter value. */
    private Object value;

    /** Default value. */
    private String dflt;

    /** Possible string values. */
    private final List list;

    /** Current parameter value. */
    private Object valueCurrent;

    /**
     * Constructor.
     *
     * @param   name    Parameter name.
     * @param   label   Parameter label.
     * @param   type    Parameter type.
     * @param   comment Parameter comment.
     * @param   value   Value of parameter. Maybe <code>null</code>.
     */
    public Parameter(final String name, final String label, final Class type,
            final String comment, final String value) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.comment = comment;
        this.dflt = value;
        this.list = null;
        setStringValue(value);
    }

    /**
     * Constructor.
     *
     * @param   name    Parameter name.
     * @param   label   Parameter label.
     * @param   type    Parameter type.
     * @param   comment Parameter comment.
     * @param   value   Value of parameter. Maybe <code>null</code>.
     * @param   dflt    Default value of parameter. Maybe <code>null</code>.
     */
    public Parameter(final String name, final String label, final Class type,
            final String comment, final String value, final String dflt) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.comment = comment;
        this.dflt = dflt;
        this.list = null;
        setStringValue(value);
    }

    /**
     * Constructor.
     *
     * @param   name    Parameter name.
     * @param   label   Parameter label.
     * @param   comment Parameter comment.
     * @param   value   Value of parameter. Maybe <code>null</code>.
     * @param   dflt    Default value of parameter. Maybe <code>null</code>.
     * @param   list    List of possible string values.
     * @throws  NullPointerException    <code>list</code> is null.
     */
    public Parameter(final String name, final String label,
            final String comment, final String value, final String dflt, final List list) {
        this.name = name;
        this.label = label;
        this.type = List.class;
        this.comment = comment;
        this.dflt = dflt;
        this.list = list;
        if (list == null) {
            throw new NullPointerException("list is null");
        }
        setStringValue(value);
    }

    /**
     * Reset all parameters to default values.
     */
    public void resetToDefault() {
        setStringValue(dflt);
    }

    /**
     * Get parameter comment.
     *
     * @return  Comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set parameter comment.
     *
     * @param comment   Comment.
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Get parameter name.
     *
     * @return  Name of parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Set parameter name.
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get parameter type.
     *
     * @return  Type.
     */
    public Class getType() {
        return type;
    }

    /**
     * Get parameter value.
     *
     * @return  Value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get <code>Boolean</code> parameter value.
     *
     * @return  Boolean parameter value.
     * @throws  ClassCastException  Type is not <code>Boolean</code>.
     */
    public Boolean getBooleanValue() {
        return (Boolean) value;
    }

    /**
     * Get <code>Integer</code> parameter value.
     *
     * @return  Integer parameter value.
     * @throws  ClassCastException  Type is not <code>Integer</code>.
     */
    public Integer getIntegerValue() {
        return (Integer) value;
    }

    /**
     * Get <code>Double</code> parameter value.
     *
     * @return  Double parameter value.
     * @throws  ClassCastException  Type is not <code>Double</code>.
     */
    public Double getDoubleValue() {
        return (Double) value;
    }

    /**
     * Get parameter value as <code>String</code>. Works on any parameter type.
     *
     * @return  Parameter value as <code>String</code>.
     */
    public String getStringValue() {
        if (value == null) {
            return null;
        }
        if (value instanceof File) {    // get more or less platform independent form
            return value.toString().replace('\\', '/');
        } else if (value instanceof Double) {
            return FormatUtility.toString(getDoubleValue());
        }
        return value.toString();
    }

    /**
     * Get <code>File</code> parameter value.
     *
     * @return  File parameter value.
     * @throws  ClassCastException  Type is not <code>File</code>.
     */
    public File getFileValue() {
        return (File) value;
    }

    /**
     * Set <code>Boolean</code> parameter value.
     *
     * @param   value    Boolean parameter value.
     * @throws  ClassCastException  Type is not <code>Boolean</code>.
     */
    public void setValue(final Boolean value) {
        if (value == null) {
            this.value = null;
            return;
        }
        if (!Boolean.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.value = value;
    }

    /**
     * Set <code>Integer</code> parameter value.
     *
     * @param   value    Integer parameter value.
     * @throws  ClassCastException  Type is not <code>Integer</code>.
     */
    public void setValue(final Integer value) {
        if (value == null) {
            this.value = null;
            return;
        }
        if (!Integer.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.value = value;
    }

    /**
     * Set <code>Double</code> parameter value.
     *
     * @param   value    Double parameter value.
     * @throws  ClassCastException  Type is not <code>Double</code>.
     */
    public void setValue(final Double value) {
        if (value == null) {
            this.value = null;
            return;
        }
        if (!Double.class.equals(type)) {
            throw new IllegalArgumentException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.value = value;
    }

    /**
     * Set <code>String</code> parameter value.
     *
     * @param   value    String parameter value.
     * @throws  ClassCastException  Type is not {@link String} or {@link List}.
     * @throws  IllegalArgumentException    Type is {@link List} and <code>value</code> is not in
     *          list.
     */
    public void setValue(final String value) {
        if (value == null) {
            this.value = null;
            return;
        }
        if (List.class.equals(type)) {
            if (!list.contains(value)) {
                final StringBuffer allowed = new StringBuffer();
                allowed.append("{");
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        allowed.append(", ");
                    }
                    allowed.append("\"" + list.get(i) + "\"");
                }
                allowed.append("}");
                throw new IllegalArgumentException("argument was not in expected value list: \""
                        + value + "\" is not in " + allowed);
            }
        } else if (!String.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.value = value;
    }

    /**
     * Set <code>File</code> parameter value.
     *
     * @param   value    File parameter value.
     * @throws  ClassCastException  Type is not <code>File</code>.
     */
    public void setValue(final File value) {
        if (value == null) {
            this.value = null;
            return;
        }
        if (!File.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.value = value;
    }

    /**
     * Set parameter label.
     *
     * @param   label   Label for parameter.
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Get parameter label.
     *
     * @return  Label for parameter.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set value according to type. Uses constructor with <code>String</code> for
     * {@link #getType()}.
     *
     * @param   value
     */
    public void setStringValue(final String value) {
        Trace.param(CLASS, this, "setStringValue", "value", value);
        this.value = null;
        if (Boolean.class.equals(type)) {
            if (value != null) {
                setValue(new Boolean(value));
            }
        } else if (Integer.class.equals(type)) {
            if (value != null) {
                setValue(new Integer(value));
            }
        } else if (Double.class.equals(type)) {
            if (value != null) {
                setValue(new Double(value));
            }
        } else if (String.class.equals(type)) {
            if (value != null) {
                setValue(value);
            }
        } else if (File.class.equals(type)) {
            if (value != null) {
                setValue(new File(value));
            }
        } else if (List.class.equals(type)) {
            if (value != null) {
                setValue(value);
            }
        } else {
            throw new IllegalArgumentException("not supported type: " + type);
        }
    }

    /**
     * Get value list.
     *
     * @return  List of possible values.
     */
    public List getList() {
        return list;
    }


    /**
     * Get current parameter value.
     *
     * @return  Value.
     */
    public Object getCurrentValue() {
        return valueCurrent;
    }

    /**
     * Get current <code>Boolean</code> parameter value.
     *
     * @return  Boolean parameter value.
     * @throws  ClassCastException  Type is not <code>Boolean</code>.
     */
    public Boolean getCurrentBooleanValue() {
        return (Boolean) valueCurrent;
    }

    /**
     * Get current <code>Integer</code> parameter value.
     *
     * @return  Integer parameter value.
     * @throws  ClassCastException  Type is not <code>Integer</code>.
     */
    public Integer getCurrentIntegerValue() {
        return (Integer) valueCurrent;
    }

    /**
     * Get current <code>Double</code> parameter value.
     *
     * @return  Double parameter value.
     * @throws  ClassCastException  Type is not <code>Double</code>.
     */
    public Double getCurrentDoubleValue() {
        return (Double) valueCurrent;
    }

    /**
     * Get current parameter value as <code>String</code>. Works on any parameter type.
     *
     * @return  Parameter value as <code>String</code>.
     */
    public String getCurrentStringValue() {
        if (valueCurrent == null) {
            return null;
        }
        if (valueCurrent instanceof File) {    // get more or less platform independent form
            return valueCurrent.toString().replace('\\', '/');
        }
        return valueCurrent.toString();
    }

    /**
     * Get current <code>File</code> parameter value.
     *
     * @return  File parameter value.
     * @throws  ClassCastException  Type is not <code>File</code>.
     */
    public File getCurrentFileValue() {
        return (File) valueCurrent;
    }

    /**
     * Set current <code>Boolean</code> parameter value.
     *
     * @param   valueCurrent    Boolean parameter value.
     * @throws  ClassCastException  Type is not <code>Boolean</code>.
     */
    public void setCurrentValue(final Boolean valueCurrent) {
        if (value == null) {
            this.valueCurrent = null;
            return;
        }
        if (!Boolean.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.valueCurrent = valueCurrent;
    }

    /**
     * Set current <code>Integer</code> parameter value.
     *
     * @param   valueCurrent    Integer parameter value.
     * @throws  ClassCastException  Type is not <code>Integer</code>.
     */
    public void setCurrentValue(final Integer valueCurrent) {
        if (valueCurrent == null) {
            this.valueCurrent = null;
            return;
        }
        if (!Integer.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.valueCurrent = valueCurrent;
    }

    /**
     * Set current <code>Double</code> parameter value.
     *
     * @param   valueCurrent    Double parameter value.
     * @throws  ClassCastException  Type is not <code>Double</code>.
     */
    public void setCurrentValue(final Double valueCurrent) {
        if (valueCurrent == null) {
            this.valueCurrent = null;
            return;
        }
        if (!Double.class.equals(type)) {
            throw new IllegalArgumentException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.valueCurrent = valueCurrent;
    }

    /**
     * Set current <code>String</code> parameter value.
     *
     * @param   valueCurrent    String parameter value.
     * @throws  ClassCastException  Type is not {@link String} or {@link List}.
     * @throws  IllegalArgumentException    Type is {@link List} and <code>value</code> is not in
     *          list.
     */
    public void setCurrentValue(final String valueCurrent) {
        if (valueCurrent == null) {
            this.valueCurrent = null;
            return;
        }
        if (List.class.equals(type)) {
            if (!list.contains(valueCurrent)) {
                final StringBuffer allowed = new StringBuffer();
                allowed.append("{");
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        allowed.append(", ");
                    }
                    allowed.append("\"" + list.get(i) + "\"");
                }
                allowed.append("}");
                throw new IllegalArgumentException("argument was not in expected value list: \""
                    + valueCurrent + "\" is not in " + allowed);
            }
        } else if (!String.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.valueCurrent = valueCurrent;
    }

    /**
     * Set current <code>File</code> parameter value.
     *
     * @param   valueCurrent    File parameter value.
     * @throws  ClassCastException  Type is not <code>File</code>.
     */
    public void setCurrentValue(final File valueCurrent) {
        if (valueCurrent == null) {
            this.valueCurrent = null;
            return;
        }
        if (!File.class.equals(type)) {
            throw new ClassCastException("expected argument type was "
                    + type + ", but given argument is of type "
                    + type.getClass().getName());
        }
        this.valueCurrent = valueCurrent;
    }

}
