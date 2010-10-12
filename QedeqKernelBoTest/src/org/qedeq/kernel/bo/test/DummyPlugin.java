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

import org.qedeq.kernel.common.Plugin;


public class DummyPlugin implements Plugin {

    private static DummyPlugin instance = new DummyPlugin();

    static public Plugin getInstance() {
        return instance;
    }
    
    public String getPluginId() {
        return DummyPlugin.class.getName();
    }

    public String getPluginName() {
        return "Dummy Plugin";
    }

    public String getPluginDescription() {
        return "Dummy Description";
    }


}