package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.se.common.LogicalState;
import org.qedeq.kernel.se.common.ModuleDataException;
import org.qedeq.kernel.se.common.Plugin;
import org.qedeq.kernel.se.common.SourceFileException;
import org.qedeq.kernel.se.common.SourceFileExceptionList;

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
     * @param   stateExternalCheckingFailed   Module state.
     * @param   sfl                           Exception that occurred during loading.
     * @throws  IllegalArgumentException    <code>state</code> is no failure state
     */
    public void setLogicalFailureState(LogicalState stateExternalCheckingFailed,
            final SourceFileExceptionList sfl);

    /**
     * Set loading progress module state. Must not be <code>null</code>.
     *
     * @param   stateInternalChecking   module state
     */
    public void setLogicalProgressState(LogicalState stateInternalChecking);

    /**
     * Set {@link ModuleConstantsExistenceCheckerImpl}. Doesn't do any status handling.
     *
     * @param   existence   Set this checker.
     */
    public void setExistenceChecker(ModuleConstantsExistenceChecker existence);

    /**
     * Set logic checked state. Also set the predicate and function existence checker.
     *
     * @param   checker Checks if a predicate or function constant is defined.
     */
    public void setChecked(final ModuleConstantsExistenceChecker checker);


}
