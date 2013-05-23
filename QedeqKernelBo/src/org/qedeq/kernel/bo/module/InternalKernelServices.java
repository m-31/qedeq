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

package org.qedeq.kernel.bo.module;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.common.KernelProperties;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.visitor.ContextChecker;
import org.qedeq.kernel.se.visitor.InterruptException;

/**
 * The kernel internal service methods are assembled here. Needed by the kernel and its helpers.
 *
 * @author  Michael Meyling
 */
public interface InternalKernelServices extends KernelProperties {

    /**
     * Get access to configuration parameters.
     *
     * @return  Configuration access.
     */
    public QedeqConfig getConfig();

    /**
     * Get buffer directory for QEDEQ module files.
     *
     * @return  buffer directory.
     */
    public File getBufferDirectory();

    /**
     * Get directory for generated files.
     *
     * @return  Generation directory.
     */
    public File getGenerationDirectory();

    /**
     * Get {@link KernelQedeqBo} for an address.
     *
     * @param   address Look for this address.
     * @return  Existing or new {@link KernelQedeqBo}, if address is malformed
     *          <code>null</code> is returned.
     */
    public KernelQedeqBo getKernelQedeqBo(ModuleAddress address);

    /**
     * Transform an URL address into a local file path where the QEDEQ module is buffered.
     * If the QEDEQ module is a local file the path to that file is given.
     *
     * @param   address     Get local address for this QEDEQ module address.
     * @return  Local file path for that <code>address</code>.
     */
    public File getLocalFilePath(ModuleAddress address);

    /**
     * Load QEDEQ module.
     *
     * @param   process Working process.
     * @param   address Load module from this address.
     * @return  BO for QEDEQ module. Loading still might have failed. Check status.
     * @throws  InterruptException User canceled request.
     */
    public KernelQedeqBo loadKernelModule(final InternalServiceProcess process, final ModuleAddress address)
            throws InterruptException;

    /**
     * Load specified QEDEQ module from QEDEQ parent module.
     *
     * @param   process Working process.
     * @param   parent  Parent module address.
     * @param   spec    Specification for another QEDEQ module.
     * @return  Loaded module.
     * @throws  SourceFileExceptionList     Loading failed.
     * @throws  InterruptException User canceled request.
     */
    public KernelQedeqBo loadModule(InternalServiceProcess process, ModuleAddress parent,
            Specification spec) throws SourceFileExceptionList, InterruptException;

    /**
     * Get required modules of given module. You can check the status to know if the loading was
     * successful.
     *
     * @param   process Working process.
     * @param   qedeq   Module to check.
     * @return  Successful loading.
     * @throws  InterruptException User canceled request.
     */
    public boolean loadRequiredModules(InternalServiceProcess process, KernelQedeqBo qedeq) throws InterruptException;

    /**
     * Check if all formulas of a QEDEQ module and its required modules are well formed.
     *
     * @param   process Working process.
     * @param   qedeq   Module to check.
     * @return  Was check successful?
     */
    public boolean checkWellFormedness(InternalServiceProcess process, KernelQedeqBo qedeq);

    /**
     * Check if all propositions of this and all required modules have correct formal proofs.
     *
     * @param   process Working process.
     * @param   qedeq   Module to check.
     * @return  Was check successful?
     */
    public boolean checkFormallyProved(InternalServiceProcess process, KernelQedeqBo qedeq);

    /**
     * Execute plugin on given QEDEQ module.
     *
     * @param   parent      Parent service process. Might be <code>null</code>
     * @param   id          Plugin id.
     * @param   qedeq       QEDEQ module.
     * @param   data        Process data. Additional data beside module.
     * @return  Plugin specific resulting object. Might be <code>null</code>.
     * @throws  InterruptException    Process execution was canceled by user.
     */
    public Object executePlugin(final InternalServiceProcess parent, final String id, final KernelQedeqBo qedeq,
        final Object data) throws InterruptException;

    /**
     * Get DAO for reading and writing QEDEQ modules from or to a file.
     *
     * @return  DAO.
     */
    public QedeqFileDao getQedeqFileDao();

    /**
     * Creates a list with a {@link org.qedeq.kernel.se.common.SourceFileException} with dummy
     * position.
     *
     * @param   address This source had a problem.
     * @param   code    Failure code.
     * @param   message Textual description of failure.
     * @param   e       Wrapped exception.
     * @return  Created list.
     */
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message,
         String address, IOException e);

    /**
     * Creates a list with a {@link org.qedeq.kernel.se.common.SourceFileException} with dummy
     * position.
     *
     * @param   address This source had a problem.
     * @param   code    Failure code.
     * @param   message Textual description of failure.
     * @param   e       Wrapped exception.
     * @return  Created list.
     */
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message,
        String address, RuntimeException e);

    /**
     * Creates a list with a {@link org.qedeq.kernel.se.common.SourceFileException} with dummy
     * position.
     *
     * @param   address This source had a problem.
     * @param   code    Failure code.
     * @param   message Textual description of failure.
     * @param   e       Wrapped exception.
     * @return  Created list.
     */
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message,
        String address, Exception e);

    /**
     * Get context checker.
     *
     * @return  Checker for testing if context is valid.
     */
    public ContextChecker getContextChecker();

    public InternalServiceProcess createServiceProcess(final String action);

    /**
     * Create service process for given module. Locks also module access.
     *
     * @param   service             The service that runs in current thread.
     * @param   qedeq               QEDEQ module for service.
     * @param   configParameters    Config parameters for the service.
     * @param   parameters          Parameter for this service call.
     * @param   process             We run in this process.
     * @param   parent              Parent process that creates a new one.
     * @return  Created service call.
     * @throws  InterruptException  Locking of module was canceled by user.
     */
    public InternalServiceCall createServiceCall(Service service,
            final KernelQedeqBo qedeq, final Parameters configParameters, final Parameters parameters,
            final InternalServiceProcess process, final InternalServiceCall parent)
            throws InterruptException;


    public boolean lockModule(InternalServiceProcess process, KernelQedeqBo qedeq, Service service)
            throws InterruptException;

    public boolean unlockModule(InternalServiceProcess process, KernelQedeqBo qedeq);

}
