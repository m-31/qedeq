/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * Copyright 2000-2010,  Michael Meyling <mime@qedeq.org>.
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
import java.net.URL;
import java.util.Map;

import org.qedeq.kernel.base.module.Specification;
import org.qedeq.kernel.bo.QedeqBo;
import org.qedeq.kernel.bo.module.InternalKernelServices;
import org.qedeq.kernel.bo.module.KernelQedeqBo;
import org.qedeq.kernel.bo.module.QedeqFileDao;
import org.qedeq.kernel.common.ModuleAddress;
import org.qedeq.kernel.common.Plugin;
import org.qedeq.kernel.common.SourceFileExceptionList;

public class DummyInternalKernalServices implements InternalKernelServices {

    public File getBufferDirectory() {
        return null;
    }
    public File getGenerationDirectory() {
        return null;
    }
    public File getLocalFilePath(ModuleAddress address) {
        return null;
    }
    public void startupServices() {
    }
    public void removeAllModules() {
    }
    public void clearLocalBuffer() throws IOException {
    }
    public QedeqBo loadModule(ModuleAddress address) {
        return null;
    }
    public void loadRequiredModules(ModuleAddress address) throws SourceFileExceptionList {
    }
    public boolean loadAllModulesFromQedeq() {
        return false;
    }
    public void removeModule(ModuleAddress address) {
    }
    public ModuleAddress[] getAllLoadedModules() {
        return null;
    }
    public QedeqBo getQedeqBo(ModuleAddress address) {
        return null;
    }
    public String getSource(ModuleAddress address) throws IOException {
        return null;
    }
    public ModuleAddress getModuleAddress(URL url) throws IOException {
        return null;
    }
    public ModuleAddress getModuleAddress(String url) throws IOException {
        return null;
    }
    public ModuleAddress getModuleAddress(File file) throws IOException {
        return null;
    }
    public boolean checkModule(ModuleAddress address) {
        return false;
    }
    public Object executePlugin(String pluginName, ModuleAddress address, Map parameters) {
        return null;
    }
    public QedeqFileDao getQedeqFileDao() {
        return null;
    }
    public KernelQedeqBo getKernelQedeqBo(ModuleAddress address) {
        return null;
    }
    public KernelQedeqBo loadModule(ModuleAddress parent, Specification spec) throws SourceFileExceptionList {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(String address,
            IOException e) {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(String address,
            Exception e) {
        return null;
    }
    public SourceFileExceptionList createSourceFileExceptionList(String address,
            RuntimeException e) {
        return null;
    }
    public Plugin[] getPlugins() {
        return null;
    }
    public void clearAllPluginResults(ModuleAddress address) {
    }

}
