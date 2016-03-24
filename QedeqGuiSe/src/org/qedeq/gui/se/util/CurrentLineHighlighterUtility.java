/* This file is part of the project "Hilbert II" - http://www.qedeq.org
 *
 * The main author is Santhosh Kumar T. Visit his great home page under:
 * http://www.jroller.com/santhosh/
 *
 * The original source for this file is:
 * http://www.jroller.com/santhosh/entry/currentlinehighlighter_contd
 *
 * It is distributed under the GNU Lesser General Public License:
 * http://www.gnu.org/copyleft/lesser.html
 */
package org.qedeq.gui.se.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * This class can be used to highlight the current line for any JTextComponent.
 * To be used as a static utility
 *
 * @author  Santhosh Kumar T
 * @author  Peter De Bruycker
 * @author  Michael Meyling (small adaption).
 */
public final class CurrentLineHighlighterUtility {

    /** NOI18N - used as client property. */
    private static final String LINE_HIGHLIGHT = "lineHighLight";

    /** NOI18N - used as client property. */
    private static final String PREVIOUS_CARET = "previousCaret";

    /**
     * Constructor.
     */
    private CurrentLineHighlighterUtility() {
        // nothing to do
    }

    /**
     * Installs CurrentLineHighlighterUtility for the given JTextComponent.
     *
     * @param   c   Install highlighter here.
     */
    public static void install(final JTextComponent c) {
        try {
            final Object obj = c.getHighlighter().addHighlight(0, 0, painter);
            c.putClientProperty(LINE_HIGHLIGHT, obj);
            c.putClientProperty(PREVIOUS_CARET, new Integer(c.getCaretPosition()));
            c.addCaretListener(caretListener);
            c.addMouseListener(mouseListener);
            c.addMouseMotionListener(mouseListener);
        } catch (BadLocationException ignore) {
            // ignore
        }
    }

    /**
     * Uninstall CurrentLineHighligher for the given JTextComponent.
     *
     * @param   c   Uninstall highlighter here.
     */
    public static void uninstall(final JTextComponent c) {
        c.putClientProperty(LINE_HIGHLIGHT, null);
        c.putClientProperty(PREVIOUS_CARET, null);
        c.removeCaretListener(caretListener);
        c.removeMouseListener(mouseListener);
        c.removeMouseMotionListener(mouseListener);
    }

    /**
     * Caret listener.
     */
    private static CaretListener caretListener = new CaretListener() {
        public void caretUpdate(final CaretEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            currentLineChanged(c);
        }
    };

    /**
     * Mouse listener.
     */
    private static MouseInputAdapter mouseListener = new MouseInputAdapter() {
        public void mousePressed(final MouseEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            currentLineChanged(c);
        }

        public void mouseDragged(final MouseEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            currentLineChanged(c);
        }
    };

    /**
     * Fetches the previous caret location, stores the current caret location. If the caret is on
     * another line, repaint the previous line and the current line
     *
     * @param c the text component
     */
    private static void currentLineChanged(final JTextComponent c) {
        if (null == c.getClientProperty(PREVIOUS_CARET)) {
            return;
        }
        try {
            final int previousCaret = ((Integer) c.getClientProperty(PREVIOUS_CARET)).intValue();
            final Rectangle prev = c.modelToView(previousCaret);
            final Rectangle r = c.modelToView(c.getCaretPosition());
            c.putClientProperty(PREVIOUS_CARET, new Integer(c.getCaretPosition()));

            if (prev.y != r.y) {
                c.repaint(0, prev.y, c.getWidth(), r.height);
                c.repaint(0, r.y, c.getWidth(), r.height);
            }
        } catch (final BadLocationException ignore) {
            // ignore
        }
    }

    /**
     * Painter for the highlighted area.
     */
    private static Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter() {
        public void paint(final Graphics g, final int p0, final int p1, final Shape bounds,
                final JTextComponent c) {
            GuiHelper.paintCurrentLineBackground(g, c,
                GuiHelper.getLineHighlighterBackgroundColor());
        }
    };
}
