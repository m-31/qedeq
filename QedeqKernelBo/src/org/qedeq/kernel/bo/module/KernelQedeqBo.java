package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.common.Element2Latex;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
import org.qedeq.kernel.se.state.LoadingImportsState;
import org.qedeq.kernel.se.state.WellFormedState;

/**
 * Kernel internal QedeqBo with additional methods.
 *
 * @author  Michael Meyling
 */
public interface KernelQedeqBo extends QedeqBo {

    /**
     * Get internal kernel services.
     *
     * @return  Internal kernel services.
     */
    public InternalKernelServices getKernelServices();

    /**
     * Get labels and URLs of all referenced modules.
     *
     * @return  URLs of all referenced modules.
     */
    public KernelModuleReferenceList getKernelRequiredModules();

    /**
     * Get label references for QEDEQ module.
     *
     * @return  Label references.
     */
    public ModuleLabels getLabels();

    /**
     * Return mapper for transforming elements into LaTeX.
     *
     * @return  Transformer to get LaTeX out of elements.
     */
    public Element2Latex getElement2Latex();

    /**
     * Return mapper for transforming elements into UTF-8 text.
     *
     * @return  Transformer to get UTF-8 text out of elements.
     */
    public Element2Utf8 getElement2Utf8();

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   service      This service generated the error.
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileException createSourceFileException(Service service, ModuleDataException
            exception);

    /**
     * Add errors and warnings for service.
     *
     * @param plugin    Add errors for this service.
     * @param errors    These errors occurred.
     * @param warnings  These warnings occurred.
     */
    public void addPluginErrorsAndWarnings(Plugin plugin, SourceFileExceptionList errors,
            SourceFileExceptionList warnings);

    /**
     * Remove all errors and warnings for all services.
     */
    public void clearAllPluginErrorsAndWarnings();

    /**
     * Get the predicate and function existence checker. Is only not <code>null</code>
     * if logic was successfully checked.
     *
     * @return  Checker. Checks if a predicate or function constant is defined.
     */
    public ModuleConstantsExistenceChecker getExistenceChecker();

    /**
     * Set failure module state.
     *
     * @param   loadImportsFailed       Module state.
     * @param   sfl                     Exception that occurred during loading required modules.
     * @throws  IllegalArgumentException  <code>state</code> is no failure state
     */
    public void setLoadingImportsFailureState(LoadingImportsState loadImportsFailed,
            SourceFileExceptionList sfl);

    /**
     * Set logical well formed module state. Must not be <code>null</code>.
     *
     * @param   stateLoadImports        module state
     */
    public void setLoadingImportsProgressState(LoadingImportsState stateLoadImports);

    /**
     * Set loaded imports state.
     *
     * @param   imports                 These imports were loaded.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedImports(final KernelModuleReferenceList imports);

    /**
     * Set dependency failure module state.
     *
     * @param   loadRequiredFailed      Module state.
     * @param   sfl                     Exception that occurred during loading required modules.
     * @throws  IllegalArgumentException  <code>loadRequiredFailed</code> is no failure state
     * @throws  IllegalStateException   Module is not yet loaded.
     * @throws  NullPointerException    <code>loadRequiredFailed</code> is <code>null</code>.
     */
    public void setDependencyFailureState(DependencyState loadRequiredFailed,
            SourceFileExceptionList sfl);

    /**
     * Set dependency module state. Must not be <code>null</code>.
     *
     * @param   state                       Module state
     * @throws  IllegalStateException       Module is not yet loaded.
     * @throws  IllegalArgumentException    <code>state</code> is failure state or loaded required
     *                                      state.
     * @throws  NullPointerException        <code>state</code> is <code>null</code>.
     */
    public void setDependencyProgressState(DependencyState state);

    /**
     * Set loaded required requirements state.
     *
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules();

    /**
     * Set failure module state.
     *
     * @param   stateExternalCheckingFailed   Module state.
     * @param   sfl                           Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setWellfFormedFailureState(WellFormedState stateExternalCheckingFailed,
            SourceFileExceptionList sfl);

    /**
     * Set logical well formed module state. Must not be <code>null</code>.
     *
     * @param   stateInternalChecking   module state
     */
    public void setWellFormedProgressState(WellFormedState stateInternalChecking);

    /**
     * Set logical formally proved module progress state. Must not be <code>null</code>.
     *
     * @param   state                       module state
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setFormallyProvedProgressState(FormallyProvedState state);

    /**
     * Set logical formally proved module failure state. Must not be <code>null</code>.
     *
     * @param   state                       module state
     * @param   sfl                          Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setFormallyProvedFailureState(FormallyProvedState state,
            SourceFileExceptionList sfl);

    /**
     * Set {@link ModuleConstantsExistenceChecker}. Doesn't do any status handling.
     *
     * @param   existence   Set this checker.
     */
    public void setExistenceChecker(ModuleConstantsExistenceChecker existence);

    /**
     * Set logic well formed state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public void setWellFormed(ModuleConstantsExistenceChecker checker);


    /**
     * Set currently running service.
     *
     * @param   service  Set currently running service. Might be <code>null</code>.
     */
// FIXME
//    public void setCurrentlyRunningService(Service service);

}
