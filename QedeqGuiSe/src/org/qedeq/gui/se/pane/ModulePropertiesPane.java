/* $Id: ModulePropertiesPane.java,v 1.3 2007/12/21 23:34:47 m31 Exp $
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

package org.qedeq.gui.se.pane;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.qedeq.kernel.common.QedeqBo;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * View for {@link ModuleProperties}.
 *
 * @version $Revision: 1.3 $
 * @author  Michael Meyling
 */

public class ModulePropertiesPane extends JPanel {

    /** Reference to module properties. */
    private QedeqBo prop;

    /** State of module. */
    private JTextField state;

    /** Module name. */
    private JTextField name;

    /** Module rule version. */
    private JTextField ruleVersion;

    /** URL of module. */
    private JTextArea url;


    /**
     * Creates new Panel.
     */
    public ModulePropertiesPane(final QedeqBo prop) {
        super();
        this.prop = prop;
        setupView();
        updateView();
    }

    private JComponent buildTestPanel() {
        FormLayout layout = new FormLayout(
            "right:pref, 5dlu, fill:50dlu:grow");    // columns
//            + "pref, 3dlu, pref");                  // rows

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);
        builder.setDefaultDialogBorder();

        builder.append("Loading State");
        state = createTextField("", false);
        builder.append(state);

        builder.append("Name");
        name = createTextField("", false);
        builder.append(name);

        builder.append("Rule Version");
        ruleVersion = createTextField("", false);
        builder.append(ruleVersion);

        builder.append("URL");
//        url = createTextField("", false);
        url = new JTextArea();
        url.setEditable(false);
        url.setLineWrap(false);
        builder.append(wrapWithScrollPane(url));
//        builder.append(url);

        return builder.getPanel();
    }

   /**
     * Assembles the gui components of the panel.
     */
    public final void setupView() {
        this.setLayout(new GridLayout(1, 1));
        this.add(buildTestPanel());
/*
        this.stateLabel = new JLabel("State");
        this.add(this.stateLabel);
        this.stateLabel.setBounds(33, 20, 150, 17);

        this.stateTextField = new JTextField();
        this.stateTextField.setEditable(false);
        this.add(this.stateTextField);
        this.stateTextField.setBounds(140, 20, 440, 21);

        this.nameLabel = new JLabel("Name");
        this.add(this.nameLabel);
        this.nameLabel.setBounds(33, 60, 150, 17);

        this.nameTextField = new JTextField();
        this.nameTextField.setEditable(false);
        this.add(this.nameTextField);
        this.nameTextField.setBounds(140, 60, 440, 21);

        this.moduleVersionLabel = new JLabel("Module version");
        this.add(this.moduleVersionLabel);
        this.moduleVersionLabel.setBounds(33, 100, 150, 17);

        this.moduleVersionTextField = new JTextField();
        this.moduleVersionTextField.setEditable(false);
        this.add(this.moduleVersionTextField);
        this.moduleVersionTextField.setBounds(140, 100, 440, 21);

        this.ruleVersionLabel = new JLabel("Rule version");
        this.add(this.ruleVersionLabel);
        this.ruleVersionLabel.setBounds(33, 140, 150, 17);

        this.ruleVersionTextField = new JTextField();
        this.ruleVersionTextField.setEditable(false);
        this.add(this.ruleVersionTextField);
        this.ruleVersionTextField.setBounds(140, 140, 440, 21);

        this.urlLabel = new JLabel("URL");
        this.add(this.urlLabel);
        this.urlLabel.setBounds(33, 180, 150, 17);

        this.urlTextField = new JTextArea();
        this.urlTextField.setEditable(false);
        this.urlTextField.setLineWrap(true);
        this.urlTextField.setBackground(this.getBackground());
        this.urlTextField.setFont(this.ruleVersionTextField.getFont());
        this.urlTextField.setCaretPosition(0);
        this.urlTextField.setBorder(this.ruleVersionTextField.getBorder());
        this.add(this.urlTextField);
        this.urlTextField.setBounds(140, 180, 440, 21 * 2);

        this.failureLabel = new JLabel("Failure");
        this.add(this.failureLabel);
        this.failureLabel.setBounds(33, 241, 150, 17);

        this.failureTextField = new JTextArea();
        this.failureTextField.setEditable(false);
        this.failureTextField.setLineWrap(true);
        this.failureTextField.setWrapStyleWord(true);
        this.failureTextField.setAutoscrolls(true);
        this.failureTextField.setBackground(this.getBackground());
        this.failureTextField.setFont(this.ruleVersionTextField.getFont());
        this.failureTextField.setCaretPosition(0);

// TODO remove me
/*
        final JScrollPane scroller = new JScrollPane();
        final JViewport port = scroller.getViewport();
        port.add(failureTextField);
        this.add(scroller);
        scroller.setBounds(140, 240, 441, 21 * 4);
*/
/*
        this.add(failureTextField);
        failureTextField.setBounds(140, 240, 441, 21 * 4);
*/
        this.setPreferredSize(new Dimension(200, 200));
    }

    public void setModel(final QedeqBo prop) {
        this.prop = prop;
        updateView();
    }

    public QedeqBo getModel() {
        return this.prop;
    }



    /**
     * Update from model.
     */
    public void updateView() {

        if (prop != null) {
            state.setText(prop.getStateDescription());
            name.setText(prop.getName());
            ruleVersion.setText(prop.getRuleVersion());
            url.setText(prop.getUrl().toString());
        } else {
            state.setText("");
            name.setText("");
            ruleVersion.setText("");
            url.setText("");
        }
        invalidate();
        repaint();
    }

    private JTextField createTextField(final String selectedText, final boolean editable) {
        JTextField combo = new JTextField(selectedText);
        combo.setEditable(editable);
        return combo;
    }

    private Component wrapWithScrollPane(final Component c) {
        return new JScrollPane(
            c,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }



}
