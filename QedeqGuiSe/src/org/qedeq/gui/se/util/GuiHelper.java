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

package org.qedeq.gui.se.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.qedeq.base.io.ResourceLoaderUtility;
import org.qedeq.base.trace.Trace;
import org.qedeq.gui.se.main.GuiOptions;

import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

/**
 * Various GUI utility methods.
 *
 * @author Michael Meyling
 */
public final class GuiHelper {

    /** This class. */
    private static final Class CLASS = GuiHelper.class;

    /** Color for line highlighter. */
    private static Color lineHighlighterBackgroundColor = new Color(232, 242,
        254);

    /** Color for line highlighter and marked areas. */
    private static Color markedAndHiglightedBackgroundColor = new Color(232,
        242, 254, 128);

    /** Color for error text areas. */
    private static Color errorTextBackgroundColor = new Color(180, 206, 255);

    /** Color for warning text areas. */
    private static Color warningTextBackgroundColor = new Color(255, 255, 190);

    /** Width of empty border. */
    private static final int DEFAULT_EMPTY_BORDER_PIXEL_X = 10;

    /** Width of empty border. */
    private static final int DEFAULT_EMPTY_BORDER_PIXEL_Y = 10;

    /**
     * Hidden constructor.
     */
    private GuiHelper() {
        // nothing to do
    }

    /**
     * Configures the user interface; requests Swing settings and JGoodies Looks
     * options from the launcher.
     *
     * @param   options Set these options.
     */
    public static void configureUI(final GuiOptions options) {
        UIManager.put("ClassLoader", CLASS.getClassLoader());

        Options.setDefaultIconSize(new Dimension(18, 18));
        Options.setUseNarrowButtons(options.isUseNarrowButtons());
        Options.setTabIconsEnabled(options.isTabIconsEnabled());
        UIManager.put(Options.POPUP_DROP_SHADOW_ENABLED_KEY, options
            .isPopupDropShadowEnabled());
        // LATER m31 20100319: we make this now direct in QedeqPane, this line
        // didn't help. Why?
        // we want our disabled TextAreas to look same if not editable
        UIManager.put("TextArea.disabledBackground", UIManager
            .get("TextArea.background"));

        UIManager.put("ToolTip.font",
                new FontUIResource("Lucida Sans Unicode", Font.PLAIN,
                UIManager.getFont("ToolTip.font").getSize()));

           // Swing Settings
        LookAndFeel selectedLaf = options.getSelectedLookAndFeel();
        if (selectedLaf instanceof PlasticLookAndFeel) {
            PlasticLookAndFeel.setPlasticTheme(options.getSelectedTheme());
            PlasticLookAndFeel.setTabStyle(options.getPlasticTabStyle());
            PlasticLookAndFeel.setHighContrastFocusColorsEnabled(options
                .isPlasticHighContrastFocusEnabled());
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
            final String toolTipText, final ActionListener action,
            final KeyStroke keyStroke) {
        AbstractButton button = createToolBarButton(iconName, toolTipText);
        button.registerKeyboardAction(action, keyStroke,
            JComponent.WHEN_IN_FOCUSED_WINDOW);
        return button;
    }

    /**
     * Creates a JToggleButton configured for use in a JToolBar.
     *
     * @param   iconName    Name of icon.
     * @param   toolTipText Tool tip text.
     * @return  Radio button.
     */
    public static AbstractButton createToolBarRadioButton(
            final String iconName, final String toolTipText) {
        final JToggleButton button = new JToggleButton(GuiHelper
            .readImageIcon(iconName));
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
        final URL url = ResourceLoaderUtility.getResourceUrl("images/" + filename);
        if (url == null) {
            Trace.fatal(CLASS, "readImageIcon", "image icon not found: images/" + filename,
                new RuntimeException());
            return null;
        }
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
     * Get background color for error text areas.
     *
     * @return Background color.
     */
    public static Color getErrorTextBackgroundColor() {
        return errorTextBackgroundColor;
    }

    /**
     * Get background color for warning text areas.
     *
     * @return Background color.
     */
    public static Color getWarningTextBackgroundColor() {
        return warningTextBackgroundColor;
    }

    /**
     * Get background color for marked and highlightted text areas.
     *
     * @return Background color.
     */
    public static Color getCurrentAndMarkedBackgroundColor() {
        return markedAndHiglightedBackgroundColor;
    }

    /**
     * Paint current line background area with
     * {@link #getLineHighlighterBackgroundColor()}.
     *
     * @param   g   Graphics to use.
     * @param   c   Text component to work on.
     * @param   col Color to work with.
     */
    public static void paintCurrentLineBackground(final Graphics g,
            final JTextComponent c, final Color col) {
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

    /**
     * Adds boarder space and floats panel to the right.
     *
     * @param   panel   Panel to decorate.
     * @return  Panel with more decorations.
     */
    public static JComponent addSpaceAndAlignRight(final JPanel panel) {
        JPanel withSpace = new JPanel();
        withSpace.add(panel);
        JPanel alignRight = new JPanel();
        alignRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
        alignRight.add(withSpace);
        return alignRight;
    }

    /**
     * Width of horizontal empty border.
     *
     * @return  Return horizontal empty boarder pixel distance.
     */
    public static int getEmptyBoderPixelsX() {
        return DEFAULT_EMPTY_BORDER_PIXEL_X;
    }

    /**
     * Width of vertical empty border.
     *
     * @return  Return vertical empty boarder pixel distance.
     */
    public static int getEmptyBorderPixelsY() {
        return DEFAULT_EMPTY_BORDER_PIXEL_Y;
    }

    /**
     * A border that puts extra pixels at the sides and bottom of each pane.
     *
     * @return  Border with extra space.
     */
    public static Border getEmptyBorder() {
        return BorderFactory.createEmptyBorder(GuiHelper
            .getEmptyBorderPixelsY(), GuiHelper.getEmptyBoderPixelsX(),
            GuiHelper.getEmptyBorderPixelsY(), GuiHelper.getEmptyBoderPixelsX());
    }

    /**
     * A border that puts extra pixels at the sides and bottom of each pane.
     *
     * @return  Border with extra space.
     */
    public static Border getEmptyBorderStackable() {
        return BorderFactory.createEmptyBorder(GuiHelper
            .getEmptyBorderPixelsY() / 2, GuiHelper.getEmptyBoderPixelsX(),
            GuiHelper.getEmptyBorderPixelsY() / 2, GuiHelper.getEmptyBoderPixelsX());
    }

    /**
     * Calculate table column width according to contents. See
     * <a href="http://www.chka.de/swing/table/cell-sizes.html">cell-sizes.html</a>
     * (calculating initial column widths based on contents).
     *
     * @author  Christian Kaufhold
     *
     * @param   table   Calculate width for this table and update column width values.
     */
    public static void calcColumnWidths(final JTable table) {
        final JTableHeader header = table.getTableHeader();
        TableCellRenderer defaultHeaderRenderer = null;
        if (header != null) {
            defaultHeaderRenderer = header.getDefaultRenderer();
        }
        TableColumnModel columns = table.getColumnModel();
        TableModel data = table.getModel();
        int margin = columns.getColumnMargin(); // only JDK1.3
        int rowCount = data.getRowCount();
        int totalWidth = 0;
        for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
            final TableColumn column = columns.getColumn(i);
            final int columnIndex = column.getModelIndex();
            int width = -1;
            TableCellRenderer h = column.getHeaderRenderer();
            if (h == null) {
                h = defaultHeaderRenderer;
            }
            if (h != null) { // Not explicitly impossible
                Component c = h.getTableCellRendererComponent(table, column.getHeaderValue(),
                    false, false, -1, i);
                width = c.getPreferredSize().width;
            }
            for (int row = rowCount - 1; row >= 0; --row) {
                TableCellRenderer r = table.getCellRenderer(row, i);
                Component c = r.getTableCellRendererComponent(table, data.getValueAt(row,
                    columnIndex), false, false, row, i);
                width = Math.max(width, c.getPreferredSize().width);
            }
            if (width >= 0) {
                column.setPreferredWidth(width + margin);
            } else {
                // ???
            }
            totalWidth += column.getPreferredWidth();
        }
    }

    public static JComponent addSpaceAndTitle(final JPanel panel, final String title) {
        JPanel withSpace = new JPanel();
        withSpace.setBorder(getEmptyBorderStackable());
        withSpace.add(panel);
        withSpace.setLayout(new GridLayout(0, 1));
        JPanel withTitle = new JPanel();
        withTitle.setBorder(BorderFactory.createTitledBorder(title));
        withTitle.add(withSpace);
        withTitle.setLayout(new GridLayout(0, 1));
        return withTitle;
    }

}
