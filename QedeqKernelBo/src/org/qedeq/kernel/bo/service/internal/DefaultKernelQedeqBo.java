/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2014,  Michael Meyling <mime@qedeq.org>.
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

package org.qedeq.kernel.bo.service.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.qedeq.base.io.SourceArea;
import org.qedeq.base.trace.Trace;
import org.qedeq.base.utility.EqualsUtility;
import org.qedeq.base.utility.StringUtility;
import org.qedeq.kernel.bo.common.Element2Latex;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.common.ModuleReferenceList;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelModuleReferenceList;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.ModuleConstantsExistenceChecker;
import org.qedeq.kernel.bo.module.ModuleLabels;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.se.base.module.LatexList;
import org.qedeq.kernel.se.base.module.Qedeq;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleContext;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.ModuleService;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.dto.module.QedeqVo;
import org.qedeq.kernel.se.state.AbstractState;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingImportsState;
import org.qedeq.kernel.se.state.LoadingState;
import org.qedeq.kernel.se.state.WellFormedState;


/**
 * Represents a module and its states. This is a kernel internal representation.
 *
 * @author  Michael Meyling
 */
public class DefaultKernelQedeqBo implements KernelQedeqBo {

    /** This class. */
    private static final Class CLASS = DefaultKernelQedeqBo.class;

    /** Internal kernel services. */
    private final InternalKernelServices services;

    /** Address and module specification. */
    private final ModuleAddress address;

    /** Loaded QEDEQ module. */
    private QedeqVo qedeq;

    /** Required QEDEQ modules. */
    private KernelModuleReferenceList required;

    /** Dependent QEDEQ modules. */
    private KernelModuleReferenceList dependent;

    /** Predicate and function constant existence checker. */
    private ModuleConstantsExistenceChecker checker;

    /** Labels for this module, definitions, etc. */
    private ModuleLabels labels;

    /** Can map elements to LaTeX. */
    private Element2Latex converter;

    /** Can map elements to UTF-8 text. */
    private Element2Utf8 textConverter;

    /** Loader used for loading this object. */
    private QedeqFileDao loader;

    /** State change manager. */
    private final StateManager stateManager;

    /** Currently running service for this module. */
    private Service currentlyRunningService;

    /**
     * Creates new module properties.
     *
     * @param   services    Internal kernel services.
     * @param   address     Module address (not <code>null</code>).
     * @throws  NullPointerException    <code>address</code> is null.
     */
    public DefaultKernelQedeqBo(final InternalKernelServices services,
            final ModuleAddress address) {
        this.services = services;
        this.address = address;
        if (address == null) {
            throw new NullPointerException("ModuleAddress must not be null");
        }
        required = new KernelModuleReferenceList();
        dependent = new KernelModuleReferenceList();
        stateManager = new StateManager(this);
    }

    /**
     * Set loader used for loading this object.
     *
     * @param   loader  Responsible loader.
     */
    public void setQedeqFileDao(final QedeqFileDao loader) {
        this.loader = loader;
    }

    /**
     * Get loader used to load this object.
     *
     * @return  Loader.
     */
    public QedeqFileDao getLoader() {
        return this.loader;
    }

    public boolean hasBasicFailures() {
        return stateManager.hasBasicFailures();
    }

    public boolean hasErrors() {
        return stateManager.hasErrors();
    }

    public boolean hasWarnings() {
        return stateManager.hasWarnings();
    }

    public ModuleAddress getModuleAddress() {
        return address;
    }

    /**
     * Get internal kernel services.
     *
     * @return  Internal kernel services.
     */
    public InternalKernelServices getKernelServices() {
        return this.services;
    }

    /**
     * Set completeness percentage.
     *
     * @param   completeness    Completeness of loading into memory.
     */
    public void setLoadingCompleteness(final int completeness) {
        stateManager.setLoadingCompleteness(completeness);
    }

    public int getLoadingCompleteness() {
        return stateManager.getLoadingCompleteness();
    }

    /**
     * Delete QEDEQ module. Invalidates all dependent modules.
     */
    public void delete() {
        stateManager.delete();
    }

    /**
     * Set loading progress module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @throws  IllegalStateException   State is a failure state or module loaded state.
     */
    public void setLoadingProgressState(final LoadingState state) {
        stateManager.setLoadingProgressState(state);
    }

    /**
     * Set failure module state.
     *
     * @param   state   Module loading state. Must not be <code>null</code>.
     * @param   e       Exception that occurred during loading. Must not be <code>null</code>.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLoadingFailureState(final LoadingState state,
            final SourceFileExceptionList e) {
        stateManager.setLoadingFailureState(state, e);
    }

    public LoadingState getLoadingState() {
        return stateManager.getLoadingState();
    }

    public boolean isLoaded() {
        return stateManager.isLoaded();
    }

    /**
     * Set loading state to "loaded". Also puts <code>null</code> to {@link #getLabels()}.
     *
     * @param   qedeq       This module was loaded. Must not be <code>null</code>.
     * @param   labels      Module labels.
     * @param   converter   Can convert elements into LaTeX. Must not be <code>null</code>.
     * @param   textConverter   Can convert elements into UTF-8 text. Must not be <code>null</code>.
     * @throws  NullPointerException    One argument was <code>null</code>.
     */
    public void setLoaded(final QedeqVo qedeq, final ModuleLabels labels,
            final Element2Latex converter, final Element2Utf8 textConverter) {
        stateManager.setLoaded(qedeq, labels);
        this.converter = converter;
        this.textConverter = textConverter;
    }

    public Qedeq getQedeq() {
        return this.qedeq;
    }

    public Element2Latex getElement2Latex() {
        return this.converter;
    }

    public Element2Utf8 getElement2Utf8() {
        return this.textConverter;
    }

    public void setLoadingImportsProgressState(final LoadingImportsState state) {
        stateManager.setLoadingImportsProgressState(state);
    }

    public void setLoadingImportsFailureState(final LoadingImportsState state,
            final SourceFileExceptionList e) {
        stateManager.setLoadingImportsFailureState(state, e);
    }

    public LoadingImportsState getLoadingImportsState() {
        return stateManager.getLoadingImportsState();
    }

    public void setLoadedImports(final KernelModuleReferenceList list) {
        stateManager.setLoadedImports(list);
    }

    public boolean hasLoadedImports() {
        return stateManager.hasLoadedImports();
    }


    public void setDependencyProgressState(final DependencyState state) {
        stateManager.setDependencyProgressState(state);
    }

    public void setDependencyFailureState(final DependencyState state,
            final SourceFileExceptionList e) {
        stateManager.setDependencyFailureState(state, e);
    }

    public DependencyState getDependencyState() {
        return stateManager.getDependencyState();
    }

    public void setLoadedRequiredModules() {
        stateManager.setLoadedRequiredModules();
    }

    public ModuleReferenceList getRequiredModules() {
        return getKernelRequiredModules();
    }

    public KernelModuleReferenceList getKernelRequiredModules() {
        return required;
    }

    public boolean hasLoadedRequiredModules() {
        return stateManager.hasLoadedRequiredModules();
    }

    /**
     * Get labels and URLs of all directly dependent modules. These are all modules that are
     * currently known to import this module.
     *
     * @return  URLs of all referenced modules.
     */
    public KernelModuleReferenceList getDependentModules() {
        return dependent;
    }

    public void setWellFormed(final ModuleConstantsExistenceChecker checker) {
        stateManager.setWellFormed(checker);
    }

    public boolean isWellFormed() {
        return stateManager.isWellFormed();
    }

    public boolean isFullyFormallyProved() {
        return stateManager.isFullyFormallyProved();
    }

    public void setWellFormedProgressState(final WellFormedState state) {
        stateManager.setWellFormedProgressState(state);
    }

    public void setWellfFormedFailureState(final WellFormedState state,
            final SourceFileExceptionList e) {
        stateManager.setWellFormedFailureState(state, e);
    }

    public WellFormedState getWellFormedState() {
        return stateManager.getWellFormedState();
    }

    public void setFormallyProvedProgressState(final FormallyProvedState state) {
        stateManager.setFormallyProvedProgressState(state);
    }

    public void setFormallyProvedFailureState(final FormallyProvedState state,
            final SourceFileExceptionList e) {
        stateManager.setFormallyProvedFailureState(state, e);
    }

    public FormallyProvedState getFormallyProvedState() {
        return stateManager.getFormallyProvedState();
    }

    public SourceFileExceptionList getErrors() {
        return stateManager.getErrors();
    }

    public SourceFileExceptionList getWarnings() {
        return stateManager.getWarnings();
    }

    public String getStateDescription() {
        return stateManager.getStateDescription();
    }

    public AbstractState getCurrentState() {
        return stateManager.getCurrentState();
    }

    public AbstractState getLastSuccessfulState() {
        return stateManager.getLastSuccesfulState();
    }

    public synchronized Service getCurrentlyRunningService() {
        return currentlyRunningService;
    }

    public synchronized void setCurrentlyRunningService(final Service currentlyRunningService) {
        this.currentlyRunningService = currentlyRunningService;
    }

    public String getName() {
        if (address == null) {
            return "null";
        }
        return address.getName();
    }

    public String getRuleVersion() {
        if (address == null || qedeq == null
                || qedeq.getHeader() == null
                || qedeq.getHeader().getSpecification() == null
                || qedeq.getHeader().getSpecification().getRuleVersion() == null) {
            return "";
        }
        return qedeq.getHeader().getSpecification().getRuleVersion();
    }

    public String getUrl() {
        if (this.address == null) {
            return null;
        }
        return this.address.getUrl();
    }

    /**
     * Set label references for QEDEQ module.
     *
     * @param   labels  Label references.
     */
    public void setLabels(final ModuleLabels labels) {
        this.labels = labels;
    }

    public ModuleLabels getLabels() {
        return labels;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   service     This service generated the error.
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(final Service service,
            final ModuleDataException exception) {
        SourceArea referenceArea = null;
        if (exception.getReferenceContext() != null) {
            referenceArea = createSourceArea(qedeq, exception.getReferenceContext());
        }
        final SourceFileException e = new SourceFileException(service, exception,
            createSourceArea(qedeq, exception.getContext()), referenceArea);
        final SourceFileExceptionList list = new SourceFileExceptionList(e);
        return list;
    }

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   service     This service generated the error.
     * @param   exception   Take this exception.
     * @param   qedeq       Take this QEDEQ source. (This might not be accessible via
     *                      {@link #getQedeq()}.
     * @return  Newly created instance.
     */
    public SourceFileExceptionList createSourceFileExceptionList(final Service service,
            final ModuleDataException exception, final Qedeq qedeq) {
        final SourceFileException e = new SourceFileException(service, exception,
            createSourceArea(qedeq, exception.getContext()), createSourceArea(qedeq,
                exception.getReferenceContext()));
        final SourceFileExceptionList list = new SourceFileExceptionList(e);
        return list;
    }

    public SourceFileException createSourceFileException(final Service service, final ModuleDataException
            exception) {
        final SourceArea area = createSourceArea(qedeq, exception.getContext());
        SourceArea referenceArea = null;
        if (exception.getReferenceContext() != null) {
            referenceArea = createSourceArea(qedeq, exception.getReferenceContext());
        }
        final SourceFileException e = new SourceFileException(service, exception, area, referenceArea);
        return e;
    }

    /**
     * Create area in source file for QEDEQ module context.
     * If the system property "qedeq.test.xmlLocationFailures" is set to "true" a runtime
     * exception is thrown if the context is not found.
     *
     * @param   qedeq       Look at this QEDEQ module.
     * @param   context     Search for this context.
     * @return  Created file area. Maybe <code>null</code>.
     */
    public SourceArea createSourceArea(final Qedeq qedeq, final ModuleContext context) {
        final String method = "createSourceArea(Qedeq, ModuleContext)";
        SourceArea area = null;
        try {
            area = loader.createSourceArea(qedeq, context);
        } catch (RuntimeException e) {
            Trace.fatal(CLASS, method, "loader couldn't create context: " + context, e);
            if (Boolean.TRUE.toString().equalsIgnoreCase(
                    System.getProperty("qedeq.test.xmlLocationFailures"))) {
                throw e;
            }
        }
        if (area == null) {
            Trace.fatal(CLASS, "createSourceArea", "loader coudn't create context: "
                + context, new NullPointerException());
            area = new SourceArea(this.getModuleAddress().getUrl());
        }
        return area;
    }

    public String[] getSupportedLanguages() {
        // TODO m31 20070704: there should be a better way to
        // get all supported languages. Time for a new visitor?
        if (!isLoaded() || getQedeq() == null || getQedeq().getHeader() == null
                || getQedeq().getHeader().getTitle() == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final LatexList list = getQedeq().getHeader().getTitle();
        final List result = new ArrayList(list.size());
        for (int i = 0; i < list.size(); i++) {
            if (null != list.get(i) && list.get(i).getLanguage() != null
                    && list.get(i).getLanguage().trim().length() > 0) {
                result.add(list.get(i).getLanguage());
            }
        }
        return (String[]) result.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public boolean isSupportedLanguage(final String language) {
        return StringUtility.isIn(language, getSupportedLanguages());
    }

    public String getOriginalLanguage() {
        // TODO 20110316 m31: rework qedeq.xsd to have a default language
        final String[] supported = getSupportedLanguages();
        if (StringUtility.isNotIn("de", supported)) {
            if (supported.length == 0) {
                return "en";    // we have no German only documents
            }
            return supported[0];
        }
        return "de";
    }


    /**
     * Set {@link QedeqVo}. Doesn't do any status handling. Only for internal use.
     *
     * @param   qedeq   Set this value.
     */
    public void setQedeqVo(final QedeqVo qedeq) {
        this.qedeq = qedeq;
    }

    /**
     * Get {@link StateManager}. Only for internal use.
     *
     * @return StateManager
     */
    protected StateManager getStateManager() {
        return this.stateManager;
    }

    /**
     * Get the predicate and function existence checker. Is not <code>null</code>
     * if logic was (not necessarily successfully) checked.
     *
     * @return  Checker. Checks if a predicate or function constant is defined.
     */
    public ModuleConstantsExistenceChecker getExistenceChecker() {
        return checker;
    }

    public void setExistenceChecker(final ModuleConstantsExistenceChecker checker) {
        this.checker = checker;
    }

    public int hashCode() {
        return (getModuleAddress() == null ? 0 : getModuleAddress().hashCode());
    }

    public boolean equals(final Object obj) {
        if (obj instanceof DefaultKernelQedeqBo) {
            return EqualsUtility.equals(((DefaultKernelQedeqBo) obj).getModuleAddress(),
                this.getModuleAddress());
        }
        return false;
    }

    public String toString() {
       return address.getUrl();
    }

    public void addPluginErrorsAndWarnings(final ModuleService plugin, final SourceFileExceptionList errors,
            final SourceFileExceptionList warnings) {
        stateManager.addPluginResults(plugin, errors, warnings);
    }

    public void clearAllPluginErrorsAndWarnings() {
        stateManager.removeAllPluginResults();
    }

}
