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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * Paint marked text blocks in underlined. Precondition is an installed
 * {@link org.qedeq.gui.se.util.CurrentLineHighlighterUtility} for that text component.
 *
 * @author  Michael Meyling
 */
public class UnderlineDocumentMarkerPainter implements Highlighter.HighlightPainter {

    /** Background color. */
    private Color color;

    /**
     * Constructor.
     *
     * @param   color   Underline color.
     */
    public UnderlineDocumentMarkerPainter(final Color color) {
        this.color = color;
    }

    public void paint(final Graphics g, final int p0, final int p1, final Shape bounds,
            final JTextComponent c) {
        specialPaint(g, p0 - 1, p1 + 1, bounds, c); // trick
    }

    private void specialPaint(final Graphics g, final int offs0, final int offs1,
            final Shape bounds, final JTextComponent c) {
//        final Rectangle alloc = bounds.getBounds();
        try {
            // --- determine locations ---
            TextUI mapper = c.getUI();
            Rectangle p0 = mapper.modelToView(c, offs0);
            Rectangle p1 = mapper.modelToView(c, offs1);

            // --- render ---
            g.setColor(color);
            float[] dashPattern = {3, 1, 3, 1 };
            ((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1,
                dashPattern, 0));
            ((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 1,
                    dashPattern, 0));
            if (p0.y == p1.y) {
                // same line, render a rectangle
                Rectangle r = p0.union(p1);
                g.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
            } else {
                // we have no clue so we render all characters:
                for (int i = offs0; i <= offs1; i++) {
                    Rectangle pc = mapper.modelToView(c, i);
                    g.drawLine(pc.x, pc.y + pc.height, pc.x + pc.width, pc.y + pc.height);
                }
            }

        } catch (BadLocationException e) {
            // can't render
        }
    }

}
