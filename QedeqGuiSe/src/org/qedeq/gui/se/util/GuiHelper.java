/* $Id: GuiHelper.java,v 1.6 2008/07/26 07:57:45 m31 Exp $
 *
 * This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2009,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.gui.se.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.ResourceLoaderUtility;
import org.qedeq.gui.se.main.GuiOptions;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

/**
 * Various GUI utility methods.
 *
 * @version $Revision: 1.6 $
 * @author  Michael Meyling
 */
public final class GuiHelper  {

    /** This class. */
    private static final Class CLASS = GuiHelper.class;

    /** Color for line highlighter. */
    private static Color lineHighlighterBackgroundColor = new Color(232, 242, 254);

    /** Color for line highlighter and marked areas. */
    private static Color markedAndHiglightedBackgroundColor = new Color(232, 242, 254, 128);

    /** Color for marked text areas. */
    private static Color markedTextBackgroundColor = new Color(255, 255, 190);

    /**
     * Hidden constructor.
     */
    private GuiHelper() {
        // nothing to do
    }

    /**
     * Configures the user interface; requests Swing settings and
     * JGoodies Looks options from the launcher.
     */
    public static void configureUI(final GuiOptions options) {
        UIManager.put("ClassLoader", CLASS.getClassLoader());

        Options.setDefaultIconSize(new Dimension(18, 18));
        Options.setUseNarrowButtons(options.isUseNarrowButtons());
        Options.setTabIconsEnabled(options.isTabIconsEnabled());
        UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY,
                options.isPopupDropShadowEnabled());

        // Swing Settings
        LookAndFeel selectedLaf = options.getSelectedLookAndFeel();
        if (selectedLaf instanceof PlasticLookAndFeel) {
            PlasticLookAndFeel.setPlasticTheme(options.getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(options.getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(
                options.isPlasticHighContrastFocusEnabled());
        } else if (selectedLaf.getClass() == MetalLookAndFeel.class) {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        }

        // Work around caching in MetalRadioButtonUI
        JRadioButton radio = new JRadioButton();
        radio.getUI().uninstallUI(radio);
        JCheckBox checkBox = new JCheckBox();
        checkBox.getUI().uninstallUI(checkBox);

        try {
            UIManager.setLookAndFeel(selectedLaf);
        } catch (Exception e) {
            Trace.trace(CLASS, "configureUI", "Can't change L&F", e);
        }

    }

    /**
     * Creates a JButton configured for use in a JToolBar.
     *
     * @param   iconName    Name of icon.
     * @param   toolTipText Tool tip text.
     * @return  Button.
     */
    public static AbstractButton createToolBarButton(final String iconName,
            final String toolTipText) {
        final JButton button = new JButton(GuiHelper.readImageIcon(iconName));
        button.setToolTipText(toolTipText);
        button.setFocusable(false);
        return button;
    }

    /**
     * Creates a JButton configured for use in a JToolBar.
     *
     * @param   iconName    Name of icon.
     * @param   toolTipText Tool tip text.
     * @param   action      Action for this button.
     * @param   keyStroke   Short cut key.
     * @return  Button.
     */
    public static AbstractButton createToolBarButton(final String iconName,
            final String toolTipText, final ActionListener action, final KeyStroke keyStroke) {
        AbstractButton button = createToolBarButton(iconName, toolTipText);
        button.registerKeyboardAction(action, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return button;
    }

    /**
     * Creates a JToggleButton configured for use in a JToolBar.
     *
     * @param   iconName    Name of icon.
     * @param   toolTipText Tool tip text.
     * @return  Radio button.
     */
    public static AbstractButton createToolBarRadioButton(final String iconName,
            final String toolTipText) {
        final JToggleButton button = new JToggleButton(GuiHelper.readImageIcon(iconName));
        button.setToolTipText(toolTipText);
        button.setFocusable(false);
        return button;
    }

    /**
     * Get an icon for given filename postfix.
     *
     * @param   filename    Look for this icon.
     * @return  Icon.
     */
    public static ImageIcon readImageIcon(final String filename) {
        URL url = ResourceLoaderUtility.getResourceUrl("images/" + filename);
        return new ImageIcon(url);
    }

    /**
     * Get background color for highlighting the current line.
     *
     * @return  Background color.
     */
    public static Color getLineHighlighterBackgroundColor() {
        return lineHighlighterBackgroundColor;
    }

    /**
     * Get background color for marked text areas.
     *
     * @return  Background color.
     */
    public static Color getMarkedTextBackgroundColor() {
        return markedTextBackgroundColor;
    }

    /**
     * Get background color for marked and highlightted text areas.
     *
     * @return  Background color.
     */
    public static Color getCurrentAndMarkedBackgroundColor() {
        return markedAndHiglightedBackgroundColor;
    }


    /**
     * Paint current line background area with {@link #getLineHighlighterBackgroundColor()}.
     *
     * @param   g   Graphics to use.
     * @param   c   Text component to work on.
     */
    public static void paintCurrentLineBackground(final Graphics g, final JTextComponent c,
            final Color col)  {
        // if something is selected we don't highlight
        if (c.getSelectionStart() != c.getSelectionEnd()) {
            return;
        }
        Rectangle r;
        try {
            r = c.modelToView(c.getCaretPosition());
        } catch (BadLocationException couldNotHappen) {
            throw new RuntimeException(couldNotHappen);
        }
        g.setColor(col);
        g.fillRect(0, r.y, c.getWidth(), r.height);
    }

}
