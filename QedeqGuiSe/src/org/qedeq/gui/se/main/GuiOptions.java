/* This file is part of the project "Hilbert II" - http://www.qedeq.org
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

package org.qedeq.gui.se.main;

import javax.swing.LookAndFeel;

import org.qedeq.base.trace.Trace;

import com.jgoodies.looks.BorderStyle;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;


/**
 * Container for GUI design settings for JGoodies.
 *
 * @version $Revision: 1.5 $
 * @author  Michael Meyling
 */
public final class GuiOptions {

    /** This class. */
    private static final Class CLASS = GuiOptions.class;

    /** JGoodies option .*/
    private boolean useNarrowButtons;

    /** JGoodies option .*/
    private boolean tabIconsEnabled;

    /** JGoodies option .*/
    private String plasticTabStyle;

    /** JGoodies option .*/
    private boolean plasticHighContrastFocusEnabled;

    /** JGoodies option .*/
    private Boolean popupDropShadowEnabled;

    /** JGoodies option .*/
    private HeaderStyle menuBarHeaderStyle;

    /** JGoodies option .*/
    private BorderStyle menuBarPlasticBorderStyle;

    /** JGoodies option .*/
    private BorderStyle menuBarWindowsBorderStyle;

    /** JGoodies option .*/
    private Boolean menuBar3DHint;

    /** JGoodies option .*/
    private HeaderStyle toolBarHeaderStyle;

    /** JGoodies option .*/
    private BorderStyle toolBarPlasticBorderStyle;

    /** JGoodies option .*/
    private BorderStyle toolBarWindowsBorderStyle;

    /** JGoodies option .*/
    private Boolean toolBar3DHint;

    /** JGoodies option .*/
    private LookAndFeel selectedLookAndFeel;

    /** JGoodies option .*/
    private PlasticTheme selectedTheme;

    /**
     * Constructor.
     */
    public GuiOptions() {
        setSelectedLookAndFeel(new PlasticXPLookAndFeel());
        setSelectedTheme(PlasticLookAndFeel.createMyDefaultTheme());
        setUseNarrowButtons(true);
        setTabIconsEnabled(true);
        setPlasticTabStyle(PlasticLookAndFeel.TAB_STYLE_DEFAULT_VALUE);
        setPlasticHighContrastFocusEnabled(false);
        setPopupDropShadowEnabled(null);
        setMenuBarHeaderStyle(null);
        setMenuBarPlasticBorderStyle(null);
        setMenuBarWindowsBorderStyle(null);
        setMenuBar3DHint(null);
        setToolBarHeaderStyle(null);
        setToolBarPlasticBorderStyle(null);
        setToolBarWindowsBorderStyle(null);
        setToolBar3DHint(null);
    }

    public Boolean getMenuBar3DHint() {
        return menuBar3DHint;
    }

    public void setMenuBar3DHint(final Boolean menuBar3DHint) {
        this.menuBar3DHint = menuBar3DHint;
    }

    public HeaderStyle getMenuBarHeaderStyle() {
        return menuBarHeaderStyle;
    }

    public void setMenuBarHeaderStyle(final HeaderStyle menuBarHeaderStyle) {
        this.menuBarHeaderStyle = menuBarHeaderStyle;
    }

    public BorderStyle getMenuBarPlasticBorderStyle() {
        return menuBarPlasticBorderStyle;
    }

    public void setMenuBarPlasticBorderStyle(final BorderStyle menuBarPlasticBorderStyle) {
        this.menuBarPlasticBorderStyle = menuBarPlasticBorderStyle;
    }

    public BorderStyle getMenuBarWindowsBorderStyle() {
        return menuBarWindowsBorderStyle;
    }

    public void setMenuBarWindowsBorderStyle(final BorderStyle menuBarWindowsBorderStyle) {
        this.menuBarWindowsBorderStyle = menuBarWindowsBorderStyle;
    }

    public Boolean isPopupDropShadowEnabled() {
        return popupDropShadowEnabled;
    }

    public void setPopupDropShadowEnabled(final Boolean popupDropShadowEnabled) {
        this.popupDropShadowEnabled = popupDropShadowEnabled;
    }

    public boolean isPlasticHighContrastFocusEnabled() {
        return plasticHighContrastFocusEnabled;
    }

    public void setPlasticHighContrastFocusEnabled(final boolean plasticHighContrastFocusEnabled) {
        this.plasticHighContrastFocusEnabled = plasticHighContrastFocusEnabled;
    }

    public String getPlasticTabStyle() {
        return plasticTabStyle;
    }

    public void setPlasticTabStyle(final String plasticTabStyle) {
        this.plasticTabStyle = plasticTabStyle;
    }

    public LookAndFeel getSelectedLookAndFeel() {
        return selectedLookAndFeel;
    }

    public void setSelectedLookAndFeel(final LookAndFeel selectedLookAndFeel) {
        this.selectedLookAndFeel = selectedLookAndFeel;
    }

    public void setSelectedLookAndFeel(final String selectedLookAndFeelClassName) {
        try {
            Class theClass = Class.forName(selectedLookAndFeelClassName);
            setSelectedLookAndFeel((LookAndFeel) theClass.newInstance());
        } catch (Exception e) {
            Trace.trace(CLASS, this, "setSelectedLookAndFeel", e);
            Trace.info(CLASS, this, "setSelectedLookAndFeel", "Can't create instance for "
                    + selectedLookAndFeelClassName);
        }
    }

    public PlasticTheme getSelectedTheme() {
        return selectedTheme;
    }

    public void setSelectedTheme(final PlasticTheme selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public boolean isTabIconsEnabled() {
        return tabIconsEnabled;
    }

    public void setTabIconsEnabled(final boolean tabIconsEnabled) {
        this.tabIconsEnabled = tabIconsEnabled;
    }

    public Boolean getToolBar3DHint() {
        return toolBar3DHint;
    }

    public void setToolBar3DHint(final Boolean toolBar3DHint) {
        this.toolBar3DHint = toolBar3DHint;
    }

    public HeaderStyle getToolBarHeaderStyle() {
        return toolBarHeaderStyle;
    }

    public void setToolBarHeaderStyle(final HeaderStyle toolBarHeaderStyle) {
        this.toolBarHeaderStyle = toolBarHeaderStyle;
    }

    public BorderStyle getToolBarPlasticBorderStyle() {
        return toolBarPlasticBorderStyle;
    }

    public void setToolBarPlasticBorderStyle(final BorderStyle toolBarPlasticBorderStyle) {
        this.toolBarPlasticBorderStyle = toolBarPlasticBorderStyle;
    }

    public BorderStyle getToolBarWindowsBorderStyle() {
        return toolBarWindowsBorderStyle;
    }

    public void setToolBarWindowsBorderStyle(final BorderStyle toolBarWindowsBorderStyle) {
        this.toolBarWindowsBorderStyle = toolBarWindowsBorderStyle;
    }

    public boolean isUseNarrowButtons() {
        return useNarrowButtons;
    }

    public void setUseNarrowButtons(final boolean useNarrowButtons) {
        this.useNarrowButtons = useNarrowButtons;
    }

}
