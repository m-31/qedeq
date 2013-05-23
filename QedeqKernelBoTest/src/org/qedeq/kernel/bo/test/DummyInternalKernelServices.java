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

package org.qedeq.kernel.bo.test;

import java.io.File;
import java.io.IOException;

import org.qedeq.base.io.Parameters;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.InternalServiceCall;
import org.qedeq.kernel.bo.module.InternalServiceProcess;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.se.base.module.Specification;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.Service;
import org.qedeq.kernel.se.common.SourceFileExceptionList;
import org.qedeq.kernel.se.config.QedeqConfig;
import org.qedeq.kernel.se.visitor.ContextChecker;
import org.qedeq.kernel.se.visitor.InterruptException;

public class DummyInternalKernelServices implements InternalKernelServices {

    public File getBufferDirectory() {
        return null;
    }
    public File getGenerationDirectory() {
        return null;
    }
    public File getLocalFilePath(ModuleAddress address) {
        return null;
    }
    public QedeqFileDao getQedeqFileDao() {
        return null;
    }
    public KernelQedeqBo getKernelQedeqBo(ModuleAddress address) {
        return null;
    }
    public KernelQedeqBo loadModule(InternalServiceProcess proc, ModuleAddress parent,
            Specification spec) throws SourceFileExceptionList {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message, String address,
            IOException e) {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message, String address,
            Exception e) {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(int code, String message, String address,
            RuntimeException e) {
        return null;
    }
    public String getBuildId() {
        return null;
    }
    public QedeqConfig getConfig() {
        return null;
    }
    public String getDedication() {
        return null;
    }
    public String getDescriptiveKernelVersion() {
        return null;
    }
    public String getKernelCodeName() {
        return null;
    }
    public String getKernelVersion() {
        return null;
    }
    public String getKernelVersionDirectory() {
        return null;
    }
    public String getMaximalRuleVersion() {
        return null;
    }
    public boolean isRuleVersionSupported(String ruleVersion) {
        return false;
    }
    public boolean isSetConnectionTimeOutSupported() {
        return false;
    }
    public boolean isSetReadTimeoutSupported() {
        return false;
    }
    public ContextChecker getContextChecker() {
        return null;
    }
    public boolean loadRequiredModules(InternalServiceProcess process, KernelQedeqBo qedeq) {
        return false;
    }
    public boolean checkWellFormedness(InternalServiceProcess process, KernelQedeqBo qedeq) {
        return false;
    }
    public boolean checkFormallyProved(InternalServiceProcess process, KernelQedeqBo qedeq) {
        return false;
    }
    public Object executePlugin(InternalServiceProcess parent, String id, KernelQedeqBo qedeq, Object data) {
        return null;
    }
    public boolean lockModule(InternalServiceProcess process, KernelQedeqBo qedeq, Service service)
            throws InterruptException {
        return false;
    }
    public boolean unlockModule(InternalServiceProcess process, KernelQedeqBo qedeq) {
        return false;
    }
    public InternalServiceProcess createServiceProcess(String action) {
        return null;
    }
    public InternalServiceCall createServiceCall(Service service, KernelQedeqBo qedeq,
            Parameters configParameters, Parameters parameters,
            InternalServiceProcess process, InternalServiceCall parent) {
        return null;
    }
    public KernelQedeqBo loadKernelModule(InternalServiceProcess process,
            ModuleAddress address) throws InterruptException {
        return null;
    }

}
