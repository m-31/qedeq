package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileException;
import org.qedeq.kernel.common.SourceFileExceptionList;

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

}
