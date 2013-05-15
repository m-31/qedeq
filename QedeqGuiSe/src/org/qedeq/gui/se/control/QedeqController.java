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

package org.qedeq.gui.se.control;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.gui.se.tree.QedeqTreeCtrl;
import org.qedeq.gui.se.util.DataDictionary;
import org.qedeq.gui.se.util.GuiHelper;
import org.qedeq.gui.se.util.MenuHelper;
import org.qedeq.kernel.bo.KernelContext;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.Plugin;


/**
 * Controller for a the GUI application.
 *
 * A Controller, which represents the classes connecting the model and the view, and is used to
 * communicate between classes in the model and view.
 * Connects the models and the views. Handles program flow.
 *
 * LATER mime 20070605: encapsulate actions that need another thread
 *                      for later java versions: use Executor framework
 *       mime 20130417: perhaps the kernel should use a thread pooling framework
 *
 * @author  Michael Meyling
 */
public class QedeqController {

    /** Tree controller. */
    private QedeqTreeCtrl treeCtrl;

    /** About action. */
    private final Action aboutAction;

    /** Help action. */
    private final Action helpAction;

    /** Exit action. */
    private final Action exitAction;

    /** Add QEDEQ module from web. */
    private final Action addAction;

    /** Add QEDEQ module from local file. */
    private final Action addFileAction;

    /** Remove all QEDEQ modules from memory. */
    private final Action removeAllAction;

    /** Remove all QEDEQ modules from local buffer. */
    private final Action removeLocalBufferAction;

    /** Remove all selected QEDEQ modules from memory. */
    private final Action removeModuleAction;

    /** Add all modules from <a href="http://www.qedeq.org/">Hilbert II</a> webpage. */
    private final Action addAllModulesFromQedeqAction;

    /** Show all service processes. */
    private final Action processViewAction;

    /** Terminate all service processes. */
    private final Action terminateAllAction;

    /** Check if QEDEQ modules are well formed. */
    private final Action checkWellFormedAction;

    /** Check if QEDEQ modules are fully and correctly formally proved. */
    private final Action checkFormallyProvedAction;

    /** Remove all plugin results for QEDEQ module. */
    private final Action removePluginResultsAction;

    /** Show preferences window. */
    private final Action preferencesAction;

    /** Show plugin preferences window. */
    private final Action pluginPreferencesAction;

    /** Show parser window. */
    private final Action latexParserAction;

    /** Show parser window. */
    private final Action textParserAction;

    /** Show parser window. */
    private final Action proofTextParserAction;

    /** List of already wanted QEDEQ modules.*/
    private final ArrayList moduleHistory;

    /** Reference to main frame. */
    private final JFrame main;

    /** Maximum number of entries in history. */
    static final int MAXIMUM_MODULE_HISTORY = 20; // LATER 20070606: put into properties

    /**
     * Constructor.
     *
     * @param   main    Reference to main frame.
     */
    public QedeqController(final JFrame main) {
        this.main = main;
        aboutAction = new AboutAction(this);
        helpAction = new HelpAction(this);
        preferencesAction = new PreferencesAction(this);
        pluginPreferencesAction = new PluginPreferencesAction(this);
        latexParserAction = new LatexParserAction(this);
        textParserAction = new TextParserAction(this);
        proofTextParserAction = new ProofTextParserAction(this);
        exitAction = new ExitAction();
        addAction = new AddAction(this);
        addFileAction = new AddFileAction(this);
        addAllModulesFromQedeqAction = new AddAllModulesFromQedeqAction();
        removeAllAction = new RemoveAllAction();
        removeModuleAction = new RemoveModuleAction(this);
        removeLocalBufferAction = new RemoveLocalBufferAction();
        checkWellFormedAction = new CheckWellFormedAction(this);
        checkFormallyProvedAction = new CheckFormallyProvedAction(this);
        removePluginResultsAction = new RemovePluginResultsAction(this);
        processViewAction = new ProcessViewAction();
        terminateAllAction = new TerminateAllAction();

        final String[] list = KernelContext.getInstance().getConfig().getModuleHistory();
        moduleHistory = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            getModuleHistory().add(list[i]);
        }

        // LATER mime 20070606: dynamic evaluation from web page?
        if (getModuleHistory().size() == 0) {
            final String prefix = "http://wwww.qedeq.org/"
                + KernelContext.getInstance().getKernelVersionDirectory() + "/doc/";
            getModuleHistory().add(prefix + "math/qedeq_logic_v1.xml");
            getModuleHistory().add(prefix + "math/qedeq_formal_logic_v1.xml");
            getModuleHistory().add(prefix + "math/qedeq_set_theory_v1.xml");
            getModuleHistory().add(prefix + "project/qedeq_basic_concept.xml");
            getModuleHistory().add(prefix + "project/qedeq_logic_language.xml");
            getModuleHistory().add(prefix + "sample/qedeq_sample1.xml");
            getModuleHistory().add(prefix + "sample/qedeq_sample2.xml");
            getModuleHistory().add(prefix + "sample/qedeq_sample3.xml");
            getModuleHistory().add(prefix + "sample/qedeq_sample4.xml");
        }
    }

    /**
     * Set tree controller.
     *
     * @param   treeCtrl    Tree controller.
     */
    public void setTreeCtrl(final QedeqTreeCtrl treeCtrl) {
        this.treeCtrl = treeCtrl;
    }

    /**
     * Get selected module.
     *
     * @return  Selected modules.
     * @throws  NothingSelectedException    No modules were selected.
     */
    public QedeqBo[] getSelected() throws NothingSelectedException {
        return treeCtrl.getSelected();
    }

    /**
     * Get about action.
     *
     * @return  Action.
     */
    public Action getAboutAction() {
        return aboutAction;
    }

    /**
     * Get help action.
     *
     * @return  Action.
     */
    public Action getHelpAction() {
        return helpAction;
    }

    /**
     * Get preferences window startup action.
     *
     * @return  Action.
     */
    public Action getPreferencesAction() {
        return preferencesAction;
    }

    /**
     * Get plugin preferences window startup action.
     *
     * @return  Action.
     */
    public Action getPluginPreferencesAction() {
        return pluginPreferencesAction;
    }

    /**
     * Get parser window startup action.
     *
     * @return  Action.
     */
    public Action getLatexParserAction() {
        return latexParserAction;
    }

    /**
     * Get parser window startup action.
     *
     * @return  Action.
     */
    public Action getTextParserAction() {
        return textParserAction;
    }

    /**
     * Get parser window startup action.
     *
     * @return  Action.
     */
    public Action getProofTextParserAction() {
        return proofTextParserAction;
    }

    /**
     * Get exit action.
     *
     * @return  Action.
     */
    public Action getExitAction() {
        return exitAction;
    }

    /**
     * Get action for adding a new QEDEQ module out of the web.
     *
     * @return  Action.
     */
    public Action getAddAction() {
        return addAction;
    }

    /**
     * Get action for adding a new QEDEQ module from a local file.
     *
     * @return  Action.
     */
    public Action getAddFileAction() {
        return addFileAction;
    }

    /**
     * Get all plugin actions as menu entries.
     *
     * @return  Menu entries for plugins.
     */
    public JMenuItem[] getPluginMenuEntries() {
        final Plugin[] plugins = KernelContext.getInstance().getPlugins();
        final PluginAction[] pluginActions = new PluginAction[plugins.length];
        for (int i = 0; i < plugins.length; i++) {
            pluginActions[i] = new PluginAction(this, plugins[i]);
        }
        JMenuItem[] result = new JMenuItem[pluginActions.length];
        for (int i = 0; i < pluginActions.length; i++) {
            final JMenuItem item = MenuHelper.createMenuItem(pluginActions[i].getPlugin()
                .getServiceAction());
            item.addActionListener(pluginActions[i]);
            item.setIcon(pluginActions[i].getIcon());
            item.setToolTipText(GuiHelper.getToolTipText(pluginActions[i].getPlugin()
                .getServiceDescription()));
            result[i] = item;
        }
        return result;
    }

    /**
     * Get action for checking if the selected QEDEQ modules are well formed.
     *
     * @return  Action.
     */
    public Action getCheckWellFormedAction() {
        return checkWellFormedAction;
    }

    /**
     * Get action for checking if the selected QEDEQ modules are fully and formally correctly proved.
     *
     * @return  Action.
     */
    public Action getCheckFormallyProvedAction() {
        return checkFormallyProvedAction;
    }

    /**
     * Get action for removing all plugin results for the selected QEDEQ modules.
     *
     * @return  Action.
     */
    public Action getRemovePluginResultsAction() {
        return removePluginResultsAction;
    }

    /**
     * Get action for removing all QEDEQ modules from memory.
     *
     * @return  Action.
     */
    public Action getRemoveAllAction() {
        return removeAllAction;
    }

    /**
     * Get action for removing all seelected QEDEQ modules from memory.
     *
     * @return  Action.
     */
    public Action getRemoveModuleAction() {
        return removeModuleAction;
    }

    /**
     * Get action for removing all QEDEQ modules from memory and local buffer.
     *
     * @return  Action.
     */
    public Action getRemoveLocalBufferAction() {
        return removeLocalBufferAction;
    }

    /**
     * Get action for loading all QEDEQ modules for the current release from the QEDEQ web site.
     *
     * @return  Action.
     */
    public Action getAddAllModulesFromQedeqAction() {
        return addAllModulesFromQedeqAction;
    }

    /**
     * Get action for starting the service process viewer.
     *
     * @return  Action.
     */
    public Action getProcessViewAction() {
        return processViewAction;
    }

    /**
     * Get action for stopping all currently running plugin actions.
     *
     * @return  Action.
     */
    public Action getTerminateAllAction() {
        return terminateAllAction;
    }

    /**
     * This method returns a string from the resource bundle.
     *
     * @param   key     Name to look for.
     * @return  Value.
     */
    public String getString(final String key) {
        return DataDictionary.getInstance().getString(key);
    }

    /**
     * Returns a mnemonic from the resource bundle. Typically used as
     * keyboard shortcuts in menu items.
     *
     * @param   key     Name to look for.
     * @return  Mnemonic.
     */
    public char getMnemonic(final String key) {
        return DataDictionary.getInstance().getMnemonic(key);
    }

    void addToModuleHistory(final String url) {
        getModuleHistory().add(0, url);
        for (int i = 1; i < getModuleHistory().size(); i++) {
            if (url.equals(getModuleHistory().get(i))) {
                getModuleHistory().remove(i);
                i--;
            }
        }
        for (int i = getModuleHistory().size() - 1; i >= QedeqController.MAXIMUM_MODULE_HISTORY;
                i--) {
            getModuleHistory().remove(i);
        }
        KernelContext.getInstance().getConfig().saveModuleHistory(getModuleHistory());
    }

    ArrayList getModuleHistory() {
        return moduleHistory;
    }

    JFrame getMainFrame() {
        return main;
    }

    void selectionError() {
        JOptionPane.showMessageDialog(
            QedeqController.this.main,
            "No QEDEQ module selected! In the tree at least one QEDEQ module must be selected",
            "Error",
            JOptionPane.ERROR_MESSAGE,
            null);
    }

}

