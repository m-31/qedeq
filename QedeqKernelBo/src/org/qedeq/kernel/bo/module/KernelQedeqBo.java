package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.common.Element2Latex;
import org.qedeq.kernel.bo.common.Element2Utf8;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.state.DependencyState;
import org.qedeq.kernel.se.state.FormallyProvedState;
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
     * @param   plugin      This plugin generated the error.
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileException createSourceFileException(Plugin plugin, ModuleDataException
            exception);

    /**
     * Add errors and warnings for plugin.
     *
     * @param plugin    Add errors for this plugin.
     * @param errors    These errors occurred.
     * @param warnings  These warnings occurred.
     */
    public void addPluginErrorsAndWarnings(Plugin plugin, SourceFileExceptionList errors,
            SourceFileExceptionList warnings);

    /**
     * Remove all errors and warnings for all plugins.
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
     * @param   loadRequiredFailed        Module state.
     * @param   sfl                       Exception that occurred during loading required modules.
     * @throws  IllegalArgumentException  <code>state</code> is no failure state
     */
    public void setDependencyFailureState(DependencyState loadRequiredFailed,
            SourceFileExceptionList sfl);

    /**
     * Set logical well formed module state. Must not be <code>null</code>.
     *
     * @param   plugin              Plugin that was executed.
     * @param   stateLoadRequired   module state
     */
    public void setDependencyProgressState(Plugin plugin, DependencyState stateLoadRequired);

    /**
     * Set loaded required requirements state.
     *
     * @param   list        URLs of all referenced modules. Must not be <code>null</code>.
     * @throws  IllegalStateException   Module is not yet loaded.
     */
    public void setLoadedRequiredModules(final KernelModuleReferenceList list);

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
     * @param   plugin      Plugin that was executed.
     * @param   stateInternalChecking   module state
     */
    public void setWellFormedProgressState(Plugin plugin, WellFormedState stateInternalChecking);

    /**
     * Set logical formally proved module progress state. Must not be <code>null</code>.
     *
     * @param   plugin      Plugin that was executed.
     * @param   state                       module state
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setFormallyProvedProgressState(Plugin plugin, FormallyProvedState state);

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
     * Set {@link ModuleConstantsExistenceCheckerImpl}. Doesn't do any status handling.
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
     * Set currently running plugin.
     *
     * @param   plugin  Set currently running plugin. Might be <code>null</code>.
     */
    public void setCurrentlyRunningPlugin(Plugin plugin);

}
