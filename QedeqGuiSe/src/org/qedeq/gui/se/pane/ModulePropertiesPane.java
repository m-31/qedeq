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

import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.Plugin;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * View for {@link QedeqBo}s.
 *
 * @author  Michael Meyling
 */

public class ModulePropertiesPane extends JPanel {

    /** Reference to module properties. */
    private QedeqBo prop;

    /** State of module. */
    private JTextField state;

    /** Currently running plugin. */
    private JTextField plugin;

    /** Module name. */
    private JTextField name;

    /** Module rule version. */
    private JTextField ruleVersion;

    /** URL of module. */
    private JTextArea url;

    /** Module errors and warnings. */
    private JTextField errorsAndWarnings;


    /**
     * Creates new Panel.
     */
    public ModulePropertiesPane() {
        super();
        this.prop = null;
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

        builder.append("State");
        state = createTextField("", false);
        builder.append(state);

        builder.append("Name");
        name = createTextField("", false);
        builder.append(name);

        builder.append("Rule Version");
        ruleVersion = createTextField("", false);
        builder.append(ruleVersion);

        builder.append("URL");
        url = new JTextArea();
        url.setEditable(false);
        url.setLineWrap(false);
        builder.append(wrapWithScrollPane(url));

        builder.append("Running");
        plugin = createTextField("", false);
        builder.append(plugin);

        builder.append("Problems");
        errorsAndWarnings = createTextField("", false);
        builder.append(errorsAndWarnings);

        return builder.getPanel();
    }

   /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        this.setLayout(new GridLayout(1, 1));
        this.add(buildTestPanel());
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
            final Plugin p = prop.getCurrentlyRunningPlugin();
            if (p != null) {
                plugin.setText(p.getPluginActionName());
                plugin.setToolTipText(GuiHelper.getToolTipText(p.getPluginDescription()));
            } else {
                plugin.setText("");
                plugin.setToolTipText("");
            }
            errorsAndWarnings.setText(prop.getErrors().size() + " errors, " + prop.getWarnings().size() + " warnings");
        } else {
            state.setText("");
            name.setText("");
            ruleVersion.setText("");
            url.setText("");
            plugin.setText("");
            errorsAndWarnings.setText("");
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
