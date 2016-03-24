/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * The main author is Santhosh Kumar T. Visit his great home page under:
 * http://www.jroller.com/santhosh/
 *
 * The original source for this file is:
 * http://www.jroller.com/santhosh/entry/document_guard
 *
 * It is distributed under the GNU Lesser General Public License:
 * http://www.gnu.org/copyleft/lesser.html
 */

package org.qedeq.gui.se.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * Paint marked text blocks in another color. Precondition is an installed
 * {@link org.qedeq.gui.se.util.CurrentLineHighlighterUtility} for that text component.
 *
 * @author  Santhosh Kumar T
 * @author  Michael Meyling
 */
public class DocumentMarkerPainter implements Highlighter.HighlightPainter {

    /** Background color. */
    private Color color;

    /**
     * Constructor.
     *
     * @param   color   Background color.
     */
    public DocumentMarkerPainter(final Color color) {
        this.color = color;
    }

    public void paint(final Graphics g, final int p0, final int p1, final Shape bounds,
            final JTextComponent c) {
        specialPaint(g, p0 - 1, p1 + 1, bounds, c); // trick
    }

    private void specialPaint(final Graphics g, final int offs0, final int offs1,
            final Shape bounds, final JTextComponent c) {
        final Rectangle alloc = bounds.getBounds();
        try {
            // --- determine locations ---
            TextUI mapper = c.getUI();
            Rectangle p0 = mapper.modelToView(c, offs0);
            Rectangle p1 = mapper.modelToView(c, offs1);

            // --- render ---
            g.setColor(color);
            if (p0.y == p1.y) {
                // same line, render a rectangle
                Rectangle r = p0.union(p1);
                g.fillRect(r.x, r.y, r.width, r.height);
            } else {
                // different lines
                int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
                g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
                if ((p0.y + p0.height) != p1.y) {
                    g.fillRect(alloc.x, p0.y + p0.height, alloc.width, p1.y - (p0.y + p0.height));
                }
                g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
            }

// TODO mime 20080417: this code dosn't work; we want to higlight the current line even if it has
//                     text markers in it
////          paint highlighter, Q & D
//            try {
//                // cursor line within: highlight again with other color:
//                if (c instanceof JTextArea) {
//                    final JTextArea ta = (JTextArea) c;
//                    final int caretLine = ta.getLineOfOffset(c.getCaretPosition());
//                    if (ta.getLineOfOffset(offs0) <= caretLine
//                            && caretLine <= ta.getLineOfOffset(offs1)) {
//                        GuiHelper.paintCurrentLineBackground(g, c,
//                            GuiHelper.getCurrentAndMarkedBackgroundColor());
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        } catch (BadLocationException e) {
            // can't render
        }
    }

}
