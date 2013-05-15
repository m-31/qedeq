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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import org.qedeq.base.io.Parameters;
import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.kernel.bo.logic.model.FourDynamicModel;
import org.qedeq.kernel.bo.logic.model.SixDynamicModel;
import org.qedeq.kernel.bo.logic.model.ThreeDynamicModel;
import org.qedeq.kernel.bo.logic.model.UnaryDynamicModel;
import org.qedeq.kernel.bo.module.PluginBo;
import org.qedeq.kernel.bo.service.heuristic.DynamicHeuristicCheckerPlugin;
import org.qedeq.kernel.bo.service.latex.Qedeq2LatexPlugin;
import org.qedeq.kernel.bo.service.logic.SimpleProofFinderPlugin;
import org.qedeq.kernel.bo.service.unicode.Qedeq2UnicodeTextPlugin;
import org.qedeq.kernel.bo.service.unicode.Qedeq2Utf8Plugin;
import org.qedeq.kernel.se.common.Plugin;

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

    /** Text field for default language of QEDEQ to UTF-8 show plugin. */
    private JTextField qedeq2Utf8ShowLanguageTF;

    /** Checkbox for info parameter of QEDEQ to UTF-8 show plugin. */
    private JCheckBox qedeq2Utf8ShowInfoCB;

    /** Text field for maximum column of QEDEQ to UTF-8 show plugin. */
    private JTextField qedeq2Utf8ShowMaximumColumnTF;

    /** Checkbox for info parameter of QEDEQ to LaTeX plugin. */
    private JCheckBox qedeq2LatexInfoCB;

    /** Checkbox for brief parameter of QEDEQ to LaTeX plugin. */
    private JCheckBox qedeq2LatexBriefCB;

    /** Checkbox for info parameter of QEDEQ to UTF-8 plugin. */
    private JCheckBox qedeq2Utf8InfoCB;

    /** Text field for maximum column of QEDEQ to UTF-8 plugin. */
    private JTextField qedeq2Utf8MaximumColumnTF;

    /** Checkbox for brief parameter of QEDEQ to UTF-8 plugin. */
    private JCheckBox qedeq2Utf8BriefCB;

    /** Plugin for converting QEDEQ modules into LaTeX. */
    private final Qedeq2LatexPlugin qedeq2latex;

    /** Plugin for converting QEDEQ modules into UTF-8 text. */
    private final Qedeq2Utf8Plugin qedeq2utf8;

    /** Plugin for showing QEDEQ modules as UTF-8 text. */
    private final Qedeq2UnicodeTextPlugin qedeq2utf8Show;

    /** Plugin for finding simple propositional calculus proofs. */
    private final SimpleProofFinderPlugin proofFinder;

    /** Plugin for checking formulas with the help of a dynamically calculated static model. */
    private DynamicHeuristicCheckerPlugin dynamicHeuristicChecker;

    /** Class string for dynamic static model. */
    private String dynamicHeuristicCheckerModel = "";

    /** Text field for maximum proof length of proof finder plugin. */
    private JTextField proofFinderMaximumProofLengthTF;

    /** Text field for number of additional propositional variables for substitution. */
    private JTextField proofFinderExtraVarsTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderPropositionVariableOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderPropositionVariableWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderPartFormulaOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderPartFormulaWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderDisjunctionOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderDisjunctionWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderImplicationOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderImplicationWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderNegationOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderNegationWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderConjunctionOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderConjunctionWeightTF;

    /** Text field for number for order of substitution. */
    private JTextField proofFinderEquivalenceOrderTF;

    /** Text field for number for weight of substitution. */
    private JTextField proofFinderEquivalenceWeightTF;

    /** Text field for number of new proof lines before a new log entry. */
    private JTextField proofFinderLogFrequenceTF;

    /** Text field for sequence of formula numbers we want to skip. */
    private JTextField proofFinderSkipFormulasTF;

    /** Here are the tabs. */
    private JTabbedPane tabbedPane;

    /** Here are the creators of class {@link PluginGuiPreferencesCreator}. */
    private List creators;

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
            qedeq2utf8Show = new Qedeq2UnicodeTextPlugin();
            dynamicHeuristicChecker = new DynamicHeuristicCheckerPlugin();
            proofFinder = new SimpleProofFinderPlugin();
            creators = new ArrayList();
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
     * Assembles the GUI components of the panel.
     */
    public final void setupView() {
        final JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        creators.add(qedeq2Utf8ShowConfig(qedeq2utf8Show));
        creators.add(qedeq2LatexConfig(qedeq2latex));
        creators.add(qedeq2Utf8Config(qedeq2utf8));
//        creators.add(heuristicCheckerConfig(heuristicChecker));
        creators.add(dynamicHeuristicCheckerConfig(dynamicHeuristicChecker));
        creators.add(proofFinderConfig(proofFinder));
        final Iterator iter = creators.iterator();
        while (iter.hasNext()) {
            PluginGuiPreferencesCreator creator = (PluginGuiPreferencesCreator) iter.next();
            tabbedPane.addTab(creator.getName(), creator.create(QedeqGuiConfig.getInstance()
                .getPluginEntries(creator.getPlugin())));
        }

//        tabbedPane.setBorder(GuiHelper.getEmptyBorder());
        tabbedPane.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10),
                tabbedPane.getBorder()));
        content.add(tabbedPane, BorderLayout.CENTER);

        content.add(GuiHelper.addSpaceAndAlignRight(buildButtonPanel()), BorderLayout.SOUTH);

        // let the container calculate the ideal size
        this.pack();

        final Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = getWidth();
        if (2 * width < screenSize.width) {
            width = 2 * width;
        }
        setBounds((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2,
            width, getHeight());
    }

    /**
     * Assembles settings for {@link Qedeq2LatexPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private PluginGuiPreferencesCreator qedeq2LatexConfig(final PluginBo plugin) {
        return new PluginGuiPreferencesCreator(plugin) {
            JComponent create(final Parameters parameters) {
                FormLayout layout = new FormLayout(
                    "left:pref, 5dlu, fill:pref:grow");    // columns

                DefaultFormBuilder builder = new DefaultFormBuilder(layout);
                builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                builder.getPanel().setOpaque(false);

                qedeq2LatexInfoCB = new JCheckBox(" Also write reference labels (makes it easier for authors)",
                    parameters.getBoolean("info"));
                builder.append(qedeq2LatexInfoCB);

                builder.nextLine();
                qedeq2LatexBriefCB = new JCheckBox(" Leave out main text entries. Creates a very brief document.",
                    parameters.getBoolean("brief"));
                builder.append(qedeq2LatexBriefCB);

                return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getServiceDescription());
            }
        };
    }

    /**
     * Assembles settings for {@link Qedeq2Utf8Plugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private PluginGuiPreferencesCreator qedeq2Utf8Config(final PluginBo plugin) {
        return new PluginGuiPreferencesCreator(plugin) {
            JComponent create(final Parameters parameters) {
                FormLayout layout = new FormLayout(
                    "left:pref, 5dlu, fill:50dlu, fill:pref:grow");    // columns

                DefaultFormBuilder builder = new DefaultFormBuilder(layout);
                builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                builder.getPanel().setOpaque(false);

                qedeq2Utf8InfoCB = new JCheckBox(" Also write reference labels (makes it easier for authors)",
                    parameters.getBoolean("info"));
                builder.append(qedeq2Utf8InfoCB, 4);

                builder.nextLine();
                builder.append("Maximum row length");
                qedeq2Utf8MaximumColumnTF = new JTextField(parameters.getString(
                    "maximumColumn"));
                qedeq2Utf8MaximumColumnTF.setToolTipText("After this character number the line is broken."
                    + "0 means no break at all.");
                builder.append(qedeq2Utf8MaximumColumnTF);

                builder.nextLine();
                qedeq2Utf8BriefCB = new JCheckBox(" Leave out main text entries. Creates a very brief document.",
                    parameters.getBoolean("brief"));
                builder.append(qedeq2Utf8BriefCB);

                return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getServiceDescription());
            }
        };
    }

    /**
     * Assembles settings for {@link Qedeq2Utf8TextPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private PluginGuiPreferencesCreator qedeq2Utf8ShowConfig(final PluginBo plugin) {
        return new PluginGuiPreferencesCreator(plugin) {
            JComponent create(final Parameters parameters) {
                FormLayout layout = new FormLayout(
                    "left:pref, 5dlu, fill:50dlu, fill:pref:grow");    // columns

                DefaultFormBuilder builder = new DefaultFormBuilder(layout);
                builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                builder.getPanel().setOpaque(false);

                builder.append("Default language");
                qedeq2Utf8ShowLanguageTF = new JTextField(parameters.getString(
                    "language"));
                qedeq2Utf8ShowLanguageTF.setToolTipText("Default language for showing module contents.");
                builder.append(qedeq2Utf8ShowLanguageTF);

                builder.nextLine();
                qedeq2Utf8ShowInfoCB = new JCheckBox(" Also write reference labels (makes it easier for authors)",
                    parameters.getBoolean("info"));
                builder.append(qedeq2Utf8ShowInfoCB, 4);

                builder.nextLine();
                builder.append("Maximum row length");
                qedeq2Utf8ShowMaximumColumnTF = new JTextField(parameters.getString(
                    "maximumColumn"));
                qedeq2Utf8ShowMaximumColumnTF.setToolTipText("After this character number the line is broken."
                    + "0 means no break at all.");
                builder.append(qedeq2Utf8ShowMaximumColumnTF);

                return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getServiceDescription());
            }
        };
    }


    /**
     * Assembles settings for {@link DynamicHeuristicCheckerPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private PluginGuiPreferencesCreator dynamicHeuristicCheckerConfig(final PluginBo plugin) {
        return new PluginGuiPreferencesCreator(plugin) {
            JComponent create(final Parameters parameters) {
                FormLayout layout = new FormLayout(
                    "left:pref, 5dlu, fill:pref:grow",          // columns
                    "top:pref:grow, top:pref:grow, top:pref:grow");      // rows

                DefaultFormBuilder builder = new DefaultFormBuilder(layout);
                builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                builder.getPanel().setOpaque(false);

                final ThreeDynamicModel modelThree = new ThreeDynamicModel();
                final FourDynamicModel modelFour = new FourDynamicModel();
                final SixDynamicModel modelSix = new SixDynamicModel();

                final ButtonGroup dynamicHeuristicCheckerModelBG = new ButtonGroup();

                final UnaryDynamicModel modelOne = new UnaryDynamicModel();
                dynamicHeuristicCheckerModel = parameters.getString(
                    "model");
                final ActionListener modelSelectionListener = new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        dynamicHeuristicCheckerModel = e.getActionCommand();
                    }
                };

                // model with one element
                {
                    final JRadioButton dynamicHeuristicCheckerOneModelRB = new JRadioButton("One");
                    if (dynamicHeuristicCheckerModel.equals(modelOne.getClass().getName())) {
                        dynamicHeuristicCheckerOneModelRB.setSelected(true);
                    }
                    dynamicHeuristicCheckerOneModelRB.setActionCommand(modelOne.getClass().getName());
                    dynamicHeuristicCheckerOneModelRB.addActionListener(modelSelectionListener);
                    dynamicHeuristicCheckerModelBG.add(dynamicHeuristicCheckerOneModelRB);
                    builder.append(dynamicHeuristicCheckerOneModelRB);
                    builder.append(getDescription(modelOne.getDescription()));
                }

                // model with three elements
                {
                    final JRadioButton dynamicHeuristicCheckerThreeModelRB = new JRadioButton("Three");
                    if (dynamicHeuristicCheckerModel.equals(modelThree.getClass().getName())) {
                        dynamicHeuristicCheckerThreeModelRB.setSelected(true);
                    }
                    dynamicHeuristicCheckerThreeModelRB.setActionCommand(modelThree.getClass().getName());
                    dynamicHeuristicCheckerThreeModelRB.addActionListener(modelSelectionListener);
                    dynamicHeuristicCheckerModelBG.add(dynamicHeuristicCheckerThreeModelRB);
                    builder.append(dynamicHeuristicCheckerThreeModelRB);
                    builder.append(getDescription(modelThree.getDescription()));
                }

                // model with four elements
                {
                    final JRadioButton dynamicHeuristicCheckerFourModelRB = new JRadioButton("Four");
                    if (dynamicHeuristicCheckerModel.equals(modelFour.getClass().getName())) {
                        dynamicHeuristicCheckerFourModelRB.setSelected(true);
                    }
                    dynamicHeuristicCheckerFourModelRB.setActionCommand(modelFour.getClass().getName());
                    dynamicHeuristicCheckerFourModelRB.addActionListener(modelSelectionListener);
                    dynamicHeuristicCheckerModelBG.add(dynamicHeuristicCheckerFourModelRB);
                    builder.append(dynamicHeuristicCheckerFourModelRB);
                    builder.append(getDescription(modelFour.getDescription()));
                }

                // model with five elements
                {
                    final JRadioButton dynamicHeuristicCheckerSixModelRB = new JRadioButton("Six");
                    if (dynamicHeuristicCheckerModel.equals(modelSix.getClass().getName())) {
                        dynamicHeuristicCheckerSixModelRB.setSelected(true);
                    }
                    dynamicHeuristicCheckerSixModelRB.setActionCommand(modelSix.getClass().getName());
                    dynamicHeuristicCheckerSixModelRB.addActionListener(modelSelectionListener);
                    dynamicHeuristicCheckerModelBG.add(dynamicHeuristicCheckerSixModelRB);
                    builder.append(dynamicHeuristicCheckerSixModelRB);
                    builder.append(getDescription(modelSix.getDescription()));
                }

                return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getServiceDescription());
            }
        };
    }

    /**
     * Assembles settings for {@link ProofFinderPlugin}.
     *
     * @param   plugin  The transformation plugin.
     * @return  Created panel.
     */
    private PluginGuiPreferencesCreator proofFinderConfig(final PluginBo plugin) {
        return new PluginGuiPreferencesCreator(plugin) {
            JComponent create(final Parameters parameters) {
                FormLayout layout = new FormLayout(
                    "left:pref, 5dlu, fill:50dlu, 5dlu, fill:50dlu, fill:pref:grow");    // columns

                DefaultFormBuilder builder = new DefaultFormBuilder(layout);
                builder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                builder.getPanel().setOpaque(false);

                builder.append("Maximum proof lines");
                proofFinderMaximumProofLengthTF = new JTextField(parameters.getString(
                    "maximumProofLines"));
                proofFinderMaximumProofLengthTF.setToolTipText("After this proof line number we abandom."
                    + " the search. This is not the maximum proof line length of the final proof but the"
                    + " but the maximum number of all generated proof lines.");
                builder.append(proofFinderMaximumProofLengthTF);
                builder.nextLine();

                builder.append("Log frequence");
                proofFinderLogFrequenceTF = new JTextField(parameters.getString(
                    "logFrequence"));
                proofFinderLogFrequenceTF.setToolTipText("After this number of new proof lines we"
                    + " create a logging output.");
                builder.append(proofFinderLogFrequenceTF);
                builder.nextLine();

                builder.append("Skip formulas");
                proofFinderSkipFormulasTF = new JTextField(parameters.getString(
                    "skipFormulas"));
                proofFinderSkipFormulasTF.setToolTipText("Skip these list of formula numbers (see log output)."
                    + " This a comma separated list of numbers.");
                builder.append(proofFinderSkipFormulasTF);
                builder.nextLine();

                proofFinderExtraVarsTF = new JTextField(parameters.getString(
                    "extraVars"));
                builder.append("Extra proposition variables");
                builder.append(proofFinderExtraVarsTF);
                proofFinderExtraVarsTF.setToolTipText("We use these number of extra proposition variables"
                        + " beside the ones we have in our initial formulas and the goal formula.");
                builder.nextLine();

                builder.appendSeparator();
                builder.append("Operator");
                builder.append("Order");
                builder.append("Weight");
                builder.nextLine();

                builder.append("Proposition variable");
                proofFinderPropositionVariableOrderTF = new JTextField(parameters.getString(
                    "propositionVariableOrder"));
                builder.append(proofFinderPropositionVariableOrderTF);
                proofFinderPropositionVariableWeightTF = new JTextField(parameters.getString(
                    "propositionVariableWeight"));
                builder.append(proofFinderPropositionVariableWeightTF);
                builder.nextLine();

                builder.append("Part formula");
                proofFinderPartFormulaOrderTF = new JTextField(parameters.getString(
                    "partFormulaOrder"));
                builder.append(proofFinderPartFormulaOrderTF);
                proofFinderPartFormulaWeightTF = new JTextField(parameters.getString(
                    "partFormulaWeight"));
                builder.append(proofFinderPartFormulaWeightTF);
                builder.nextLine();

                builder.append("Disjunction");
                proofFinderDisjunctionOrderTF = new JTextField(parameters.getString(
                    "disjunctionOrder"));
                builder.append(proofFinderDisjunctionOrderTF);
                proofFinderDisjunctionWeightTF = new JTextField(parameters.getString(
                    "disjunctionWeight"));
                builder.append(proofFinderDisjunctionWeightTF);
                builder.nextLine();

                builder.append("Implication");
                proofFinderImplicationOrderTF = new JTextField(parameters.getString(
                    "implicationOrder"));
                builder.append(proofFinderImplicationOrderTF);
                proofFinderImplicationWeightTF = new JTextField(parameters.getString(
                    "implicationWeight"));
                builder.append(proofFinderImplicationWeightTF);
                builder.nextLine();

                builder.append("Negation");
                proofFinderNegationOrderTF = new JTextField(parameters.getString(
                    "negationOrder"));
                builder.append(proofFinderNegationOrderTF);
                proofFinderNegationWeightTF = new JTextField(parameters.getString(
                    "negationWeight"));
                builder.append(proofFinderNegationWeightTF);
                builder.nextLine();

                builder.append("Conjunction");
                proofFinderConjunctionOrderTF = new JTextField(parameters.getString(
                    "conjunctionOrder"));
                builder.append(proofFinderConjunctionOrderTF);
                proofFinderConjunctionWeightTF = new JTextField(parameters.getString(
                    "conjunctionWeight"));
                builder.append(proofFinderConjunctionWeightTF);
                builder.nextLine();

                builder.append("Equivalence");
                proofFinderEquivalenceOrderTF = new JTextField(parameters.getString(
                    "equivalenceOrder"));
                builder.append(proofFinderEquivalenceOrderTF);
                proofFinderEquivalenceWeightTF = new JTextField(parameters.getString(
                    "equivalenceWeight"));
                builder.append(proofFinderEquivalenceWeightTF);
                builder.nextLine();

                return GuiHelper.addSpaceAndTitle(builder.getPanel(), plugin.getServiceDescription());
            }
        };
    }

    private JTextArea getDescription(final String text) {
        JTextArea description = new JTextArea(text);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        return description;
    }

    /**
     * Create button panel with "OK" and "Cancel".
     *
     * @return  Button panel.
     */
    private JPanel buildButtonPanel() {
        ButtonBarBuilder bbuilder = ButtonBarBuilder.createLeftToRightBuilder();

        JButton def = new JButton("Default");
        def.addActionListener(new  ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                final int i = tabbedPane.getSelectedIndex();
                PluginGuiPreferencesCreator creator = (PluginGuiPreferencesCreator) creators.get(i);
                tabbedPane.setComponentAt(i, creator.createDefault());
            }
        });
        def.setToolTipText("Reset to default values for plugin currently displayed.");

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
        bbuilder.addGriddedButtons(new JButton[]{def});
        bbuilder.addUnrelatedGap();
        bbuilder.addUnrelatedGap();
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
                final Plugin plugin = qedeq2utf8Show;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "language",
                        qedeq2Utf8ShowLanguageTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "info", qedeq2Utf8ShowInfoCB.isSelected());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "maximumColumn",
                    qedeq2Utf8ShowMaximumColumnTF.getText());
            }
            {
                final Plugin plugin = qedeq2latex;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "info", qedeq2LatexInfoCB.isSelected());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "brief", qedeq2LatexBriefCB.isSelected());
            }
            {
                final Plugin plugin = qedeq2utf8;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "info", qedeq2Utf8InfoCB.isSelected());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "maximumColumn",
                    qedeq2Utf8MaximumColumnTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "brief", qedeq2Utf8BriefCB.isSelected());
            }
            {
                final Plugin plugin = dynamicHeuristicChecker;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "model", dynamicHeuristicCheckerModel);
            }
            {
                final Plugin plugin = proofFinder;
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "maximumProofLines",
                    proofFinderMaximumProofLengthTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "logFrequence",
                        proofFinderLogFrequenceTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "skipFormulas",
                        proofFinderSkipFormulasTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "extraVars",
                        proofFinderExtraVarsTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "propositionVariableOrder",
                        proofFinderPropositionVariableOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "propositionVariableWeight",
                        proofFinderPropositionVariableWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "partFormulaOrder",
                        proofFinderPartFormulaOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "partFormulaWeight",
                        proofFinderPartFormulaWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "disjunctionOrder",
                        proofFinderDisjunctionOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "disjunctionWeight",
                        proofFinderDisjunctionWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "implicationOrder",
                        proofFinderImplicationOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "implicationWeight",
                        proofFinderImplicationWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "negationOrder",
                        proofFinderNegationOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "negationWeight",
                        proofFinderNegationWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "conjunctionOrder",
                        proofFinderConjunctionOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "conjunctionWeight",
                        proofFinderConjunctionWeightTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "equivalenceOrder",
                        proofFinderEquivalenceOrderTF.getText());
                QedeqGuiConfig.getInstance().setPluginKeyValue(plugin, "equivalenceWeight",
                        proofFinderEquivalenceWeightTF.getText());
            }
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
            JOptionPane.showMessageDialog(this, e.toString(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        try {
            QedeqGuiConfig.getInstance().store();
        } catch (IOException e) {
            Trace.fatal(CLASS, this, "save", "couldn't save preferences", e);
            JOptionPane.showMessageDialog(this, "Couldn't save preferences", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This class is a basic creator for tabbed content.
     */
    private abstract class PluginGuiPreferencesCreator {

        /** We work for this plugin. */
        private final PluginBo plugin;

        /**
         * Constructor.
         *
         * @param   plugin  Plugin we work for.
         */
        PluginGuiPreferencesCreator(final PluginBo plugin) {
            this.plugin = plugin;
        }

        /**
         * Get plugin we work for.
         *
         * @return  Plugin.
         */
        public PluginBo getPlugin() {
            return plugin;
        }

        /**
         * Get plugin action name.
         *
         * @return  Plugin action name.
         */
        public String getName() {
            return plugin.getServiceAction();
        }

        /**
         * Get default plugin values.
         *
         * @return  Default values.
         */
        private Parameters getDefaultPluginValues() {
            final Parameters parameters = new Parameters();
            plugin.setDefaultValuesForEmptyPluginParameters(parameters);
            return parameters;
        }

        JComponent createDefault() {
            return create(getDefaultPluginValues());
        }

        abstract JComponent create(Parameters parameters);

    }

}
