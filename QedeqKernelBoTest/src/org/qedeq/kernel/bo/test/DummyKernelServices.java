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

package org.qedeq.kernel.bo.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.qedeq.kernel.bo.common.KernelServices;
import org.qedeq.kernel.bo.common.QedeqBo;
import org.qedeq.kernel.bo.common.ServiceJob;
import org.qedeq.kernel.se.common.ModuleAddress;
import org.qedeq.kernel.se.common.ModuleService;

public class DummyKernelServices implements KernelServices {

    public void removeAllModules() {
    }
    public boolean clearLocalBuffer() {
        return true;
    }
    public QedeqBo loadModule(ModuleAddress address) {
        return null;
    }
    public boolean loadRequiredModules(ModuleAddress address) {
        return false;
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
    public boolean checkWellFormedness(ModuleAddress address) {
        return false;
    }
    public boolean checkFormallyProved(ModuleAddress address) {
        return false;
    }
    public ModuleService[] getPlugins() {
        return null;
    }
    public void clearAllPluginResults(ModuleAddress address) {
    }
    public ServiceJob[] getServiceProcesses() {
        return null;
    }
    public ServiceJob[] getRunningServiceProcesses() {
        return null;
    }
    public void terminateAllServiceProcesses() {
    }
    public Object executePlugin(String id, ModuleAddress address, Object data) {
        return null;
    }
}
