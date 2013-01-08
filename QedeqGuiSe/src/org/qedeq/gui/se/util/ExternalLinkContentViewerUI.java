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

package org.qedeq.gui.se.util;

import java.net.URL;

import javax.help.JHelpContentViewer;
import javax.help.plaf.basic.BasicContentViewerUI;
import javax.swing.JComponent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.ComponentUI;

/**
 * A UI subclass that will open external links (website or mail links) in an external browser.
 * Thanks to mw219725 see <a href="http://bioputer.mimuw.edu.pl/modevo/">cytoscape plugins</a>.
 *
 * @author  Michael Meyling
 */
public class ExternalLinkContentViewerUI extends BasicContentViewerUI {

        public ExternalLinkContentViewerUI(final JHelpContentViewer b) {
            super(b);
    }

    public static ComponentUI createUI(final JComponent x) {
        return new ExternalLinkContentViewerUI((JHelpContentViewer) x);
    }

    public void hyperlinkUpdate(final HyperlinkEvent he) {
        if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                URL u = he.getURL();

                if (u.getProtocol().equalsIgnoreCase("mailto")
                        || u.getProtocol().equalsIgnoreCase("http")
                        || u.getProtocol().equalsIgnoreCase("https")
                        || u.getProtocol().equalsIgnoreCase("ftp")) {
                    BareBonesBrowserLaunch.openURL(u.toString());
                    return;
                }
            } catch (Throwable t) {
                // opening failed, ignore
            }
        }
        super.hyperlinkUpdate(he);
    }

}
