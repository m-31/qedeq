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

package org.qedeq.gui.se.pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.logic.model.ThreeModel;
import org.qedeq.kernel.bo.logic.model.UnaryModel;
import org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin;
import org.qedeq.kernel.bo.service.heuristic.HeuristicCheckerPlugin;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin;
import org.qedeq.kernel.bo.service.utf8.Qedeq2Utf8Plugin;
import org.qedeq.kernel.common.Plugin;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Configures the plugin parameters.
 *
 * @author  Michael Meyling
 */

public class PluginPreferencesDialog extends JDialog {

    /** This class. */
    private static final Class CLASS = PluginPreferencesDialog.class;

    /** Checkbox for info parameter of QEDEQ to LaTeX plugin. */
    private JCheckBox qedeq2LatexInfoCB;

    /** Checkbox for info parameter of QEDEQ to UTF-8 plugin. */
    private JCheckBox qedeq2Utf8InfoCB;

    /** Text field for maximum column of QEDEQ to UTF-8 plugin. */
    private JTextField qedeq2Utf8MaximumColumnTF;

    /** Plugin for converting QEDEQ modules into LaTeX. */
    private final Plugin qedeq2latex;

    /** Plugin for converting QEDEQ modules into UTF-8 text. */
    private final Plugin qedeq2utf8;

    /** Plugin for checking formulas with the help of a static model. */
    private HeuristicCheckerPlugin heuristicChecker;

    /** Plugin for checking formulas with the help of a dynamically calculated static model. */
    private DynamicHeuristicCheckerPlugin dynamicHeuristicChecker;

    /** Plugin for checking formulas with the help of a static model. */
    private String heuristicCheckerModel;

    /**
     * Creates new Panel.
     *
     * @param   parent  Parent frame.
     */
    public PluginPreferencesDialog(final JFrame parent) {
        super(parent, "Plugin Preferences");
        final String method = "Constructor";
        Trace.begin(CLASS, this, method);
        try {
            qedeq2latex = new Qedeq2LatexPlugin();
            qedeq2utf8 = new Qedeq2Utf8Plugin();
            heuristicChecker = new HeuristicCheckerPlugin();
            dynamicHeuristicChecker = new DynamicHeuristicCheckerPlugin();
            setModal(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setupView();
            updateView();
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "Initalization of PreferencesDialog failed.", method, e);
            throw e;
        } finally {
            Trace.end(CLASS, this, method);
        }
    }

    /**
     * Assembles settings for {@link Qedeq2LatexPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private JComponent qedeq2LatexConfig(final Plugin plugin) {
        FormLayout layout = new FormLayout(
            "left:pref, 5dlu, fill:pref:grow");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        qedeq2LatexInfoCB = new JCheckBox(" Also write reference labels (makes it easier for authors)",
            QedeqGuiConfig.getInstance().getPluginKeyValue(plugin, "info", true));
        builder.append(qedeq2LatexInfoCB);

        return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getPluginDescription());
    }

    /**
     * Assembles settings for {@link Qedeq2Utf8Plugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private JComponent qedeq2Utf8Config(final Plugin plugin) {
        FormLayout layout = new FormLayout(
            "left:pref, 5dlu, fill:50dlu, fill:pref:grow");    // columns

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        qedeq2Utf8InfoCB = new JCheckBox(" Also write reference labels (makes it easier for authors)",
            QedeqGuiConfig.getInstance().getPluginKeyValue(plugin, "info", true));
        builder.append(qedeq2Utf8InfoCB, 4);

        builder.nextLine();
        builder.append("Maximum row length");
        qedeq2Utf8MaximumColumnTF = new JTextField(QedeqGuiConfig.getInstance().getPluginKeyValue(
            plugin, "maximumColumn", "80"));
        qedeq2Utf8MaximumColumnTF.setToolTipText("After this character number the line is broken."
            + "0 means no break at all.");
        builder.append(qedeq2Utf8MaximumColumnTF);

        return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getPluginDescription());
    }

    /**
     * Assembles settings for {@link HeuristicCheckerPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private JComponent heuristicCheckerConfig(final Plugin plugin) {
        FormLayout layout = new FormLayout(
            "left:pref, 5dlu, fill:pref:grow",          // columns
            "top:pref:grow, top:pref:grow, top:pref:grow");      // rows

        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        builder.getPanel().setOpaque(false);

        final ThreeModel three = new ThreeModel();
        final UnaryModel unary = new UnaryModel();
        heuristicCheckerModel = QedeqGuiConfig.getInstance()
                .getPluginKeyValue(plugin, "model", three.getClass().getName());
        final ActionListener modelSelectionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                heuristicCheckerModel = e.getActionCommand();
            }
        };

        final ButtonGroup heuristicCheckerModelBG = new ButtonGroup();

        final JRadioButton heuristicCheckerUnaryModelRB = new JRadioButton("Unary Model");
        if (heuristicCheckerModel.equals(unary.getClass().getName())) {
            heuristicCheckerUnaryModelRB.setSelected(true);
        }
        heuristicCheckerUnaryModelRB.setActionCommand(unary.getClass().getName());
        heuristicCheckerUnaryModelRB.addActionListener(modelSelectionListener);
        heuristicCheckerModelBG.add(heuristicCheckerUnaryModelRB);

        final JRadioButton heuristicCheckerThreeModelRB = new JRadioButton("Three Model");
        if (heuristicCheckerModel.equals(three.getClass().getName())) {
            heuristicCheckerThreeModelRB.setSelected(true);
        }
        heuristicCheckerThreeModelRB.setActionCommand(three.getClass().getName());
        heuristicCheckerThreeModelRB.addActionListener(modelSelectionListener);
        heuristicCheckerModelBG.add(heuristicCheckerThreeModelRB);

        builder.append(heuristicCheckerUnaryModelRB);
        JTextArea description = new JTextArea(unary.getDescription());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        builder.append(description);

        builder.append(heuristicCheckerThreeModelRB);
        description = new JTextArea(three.getDescription());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        builder.append(description);

        return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getPluginDescription());
    }

    /**
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        final JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab(qedeq2latex.getPluginName(), qedeq2LatexConfig(qedeq2latex));
        tabbedPane.addTab(qedeq2utf8.getPluginName(), qedeq2Utf8Config(qedeq2utf8));
        tabbedPane.addTab(heuristicChecker.getPluginName(), heuristicCheckerConfig(heuristicChecker));

//        tabbedPane.setBorder(GuiHelper.getEmptyBorder());
        tabbedPane.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10),
                tabbedPane.getBorder()));
        content.add(tabbedPane, BorderLayout.CENTER);

        content.add(GuiHelper.addSpaceAndAlignRight(buildButtonPanel()), BorderLayout.SOUTH);

        // let the container calculate the ideal size
        this.pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            getWidth(), getHeight());
    }

    /**
     * Create button panel with "OK" and "Cancel".
     *
     * @return  Button panel.
     */
    private JPanel buildButtonPanel() {
        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton ok = new JButton("OK");
        ok.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PluginPreferencesDialog.this.save();
                PluginPreferencesDialog.this.dispose();
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                PluginPreferencesDialog.this.dispose();
            }
        });

        bbuilder.addGriddedButtons(new JButton[]{cancel, ok});

        final JPanel buttons = bbuilder.getPanel();
        return buttons;
    }

    /**
     * Update from model.
     */
    public void updateView() {
        invalidate();
        repaint();
    }

    void save() {
        try {
            {
                final Plugin plugin = qedeq2latex;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "info", qedeq2LatexInfoCB.isSelected());
            }
            {
                final Plugin plugin = qedeq2utf8;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "info", qedeq2Utf8InfoCB.isSelected());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "maximumColumn",
                    qedeq2Utf8MaximumColumnTF.getText());
            }
            {
                final Plugin plugin = heuristicChecker;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "model", heuristicCheckerModel);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        try {
            QedeqGuiConfig.getInstance().store();
        } catch (IOException e) {
            Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
            JOptionPane.showMessageDialog(this, "Couldn't save preferences", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

