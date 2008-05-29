/* $Id: QedeqController.java,v 1.6 2008/05/15 21:26:46 m31 Exp $
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

package org.qedeq.gui.se.control;

import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.qedeq.gui.se.tree.NothingSelectedException;
import org.qedeq.gui.se.tree.QedeqTreeCtrl;
import org.qedeq.gui.se.util.DataDictionary;
import org.qedeq.kernel.base.module.LatexList;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.context.KernelContext;


/**
 * Controller for a the GUI application.
 *
 * A Controller, which represents the classes connecting the model and the view, and is used to
 * communicate between classes in the model and view.
 * Connects the models and the views. Handles program flow.
 *
 * LATER mime 20070605: encapsulate actions that need another thread
 *                     for later java versions: use Executor framework
 *
 * @version $Revision: 1.6 $
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

    /** Transform QEDEQ module into LaTeX. */
    private final Action makeLatexAction;

    /** Check logical correctness of QEDEQ module. */
    private final Action checkLogicAction;

    /** Show preferences window. */
    private final Action preferencesAction;

    /** Show parser window. */
    private final Action parserAction;

    /** List of already wanted QEDEQ modules.*/
    private final ArrayList moduleHistory;

    /** Reference to main frame. */
    private final JFrame main;

    /** Maximum number of entries in history. */
    static final int MAXIMUM_MODULE_HISTORY = 10; // LATER 20070606: put into properties

    /**
     * Constructor.
     *
     * @param   main    Reference to main frame.
     */
    public QedeqController(final JFrame main) {
        this.main = main;
        aboutAction = new AboutAction(this);
        helpAction = new HelpAction(this);
        preferencesAction = new PreferencesAction();
        parserAction = new ParserAction(this);
        exitAction = new ExitAction();
        addAction = new AddAction(this);
        addFileAction = new AddFileAction(this);
        addAllModulesFromQedeqAction = new AddAllModulesFromQedeqAction();
        removeAllAction = new RemoveAllAction();
        removeModuleAction = new RemoveModuleAction(this);
        removeLocalBufferAction = new RemoveLocalBufferAction(this);
        makeLatexAction = new MakeLatexAction(this);
        checkLogicAction = new CheckLogicAction(this);
        final String[] list = KernelContext.getInstance().getConfig().getModuleHistory();
        moduleHistory = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            getModuleHistory().add(list[i]);
        }

        // LATER mime 20070606: dynamic evaluation from web page?
        if (getModuleHistory().size() == 0) {
            final String prefix = "http://qedeq.org/"
                + KernelContext.getInstance().getKernelVersionDirectory() + "/doc/";
            getModuleHistory().add(prefix + "math/qedeq_logic_v1.xml");
            getModuleHistory().add(prefix + "math/qedeq_set_theory_v1.xml");
            getModuleHistory().add(prefix + "project/qedeq_basic_concept.xml");
            getModuleHistory().add(prefix + "project/qedeq_logic_language.xml");
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
     * Get parser window startup action.
     *
     * @return  Action.
     */
    public Action getParserAction() {
        return parserAction;
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
     * Get action for transforming the selected QEDEQ modules into LaTeX.
     *
     * @return  Action.
     */
    public Action getLatexAction() {
        return makeLatexAction;
    }

    /**
     * Get action for checking the logical correctness of the selected QEDEQ modules.
     *
     * @return  Action.
     */
    public Action getCheckLogicAction() {
        return checkLogicAction;
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

    // TODO mime 20070704: this should be part of QedeqBo
    String[] getSupportedLanguages(final QedeqBo prop) {
        // TODO mime 20070704: there should be a better way to
        // get all supported languages. Time for a new visitor?
        if (!prop.isLoaded()) {
            return new String[]{};
        }
        final LatexList list = prop.getQedeq().getHeader().getTitle();
        final String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).getLanguage();
        }
        return result;
    }

}

