package org.qedeq.kernel.bo.module;

import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.logic.wf.ExistenceChecker;
import org.qedeq.kernel.common.ModuleDataException;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileException;

/**
 * Kernel internal QedeqBo with additional methods.
 *
 * @version $Revision: 1.1 $
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
     * Get the predicate and function existence checker. Is only not <code>null</code>
     * if logic was successfully checked.
     *
     * @return  Checker. Checks if a predicate or function constant is defined.
     */
    public ExistenceChecker getExistenceChecker();

    /**
     * Create exception out of {@link ModuleDataException}.
     *
     * @param   plugin      This plugin generated the error.
     * @param   exception   Take this exception.
     * @return  Newly created instance.
     */
    public SourceFileException createSourceFileException(final Plugin plugin, final ModuleDataException
            exception);

}
