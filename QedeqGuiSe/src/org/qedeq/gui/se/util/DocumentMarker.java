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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Position;

/**
 * Mark text areas (aka blocks) in another background color. Precondition is an installed
 * {@link org.qedeq.gui.se.util.CurrentLineHighlighterUtility}.
 *
 * @author  Santhosh Kumar T
 * @author  Michael Meyling
 */
public class DocumentMarker {

    /** We mark areas in this text component. */
    private JTextArea textComp;

    /** Our highlighter for the text areas. */
    private Highlighter.HighlightPainter highlightPainter;


    /** Contains all positions. Each entry is of type {@link Position}[3]. The first position is
     * the starting offset, second is the end offset and third is the landmark position. It is
     * also possible that we have an empty block, that situation is represented by a
     * <code>null</code> entry in the list. */
    private final List pos = new ArrayList();

    /**
     * Constructor.
     *
     * @param   textComp Text       Component with marked areas.
     * @param   highlightPainter    Our highlighter.
     */
    public DocumentMarker(final JTextArea textComp, final Highlighter.HighlightPainter highlightPainter) {
        this.textComp = textComp;
        this.highlightPainter = highlightPainter;
    }

    public int getOffsetOfFistLineFromBlock(final int blockNumber) {
        int offset = 0;
        if (0 <= blockNumber && blockNumber < pos.size()) {
            Position[] p = (Position[]) pos.get(blockNumber);
            if (p != null) {    // no empty block?
                offset = p[0].getOffset();
            }
        }
        return offset;
    }

    /**
     * Get offset for landmark position of block.
     *
     * @param   blockNumber Get landmark for this block.
     * @return  Offset for this landmark.
     */
    public int getLandmarkOffsetForBlock(final int blockNumber) {
        int offset = 0;
        if (0 <= blockNumber && blockNumber < pos.size()) {
            Position[] p = (Position[]) pos.get(blockNumber);
            if (p != null) {    // no empty block?
                offset = p[2].getOffset();
            }
        }
        return offset;
    }

    public List getBlockNumbersForOffset(final int offset) {
        List list = new ArrayList();
        for (int i = 0; i < pos.size(); i++) {
            Position[] p = (Position[]) pos.get(i);
            if (p == null) {    // empty block?
                continue;
            }
            int g1 = p[0].getOffset() - 1, g2 = p[1].getOffset() + 1;
            if (g1 < offset && offset < g2) {
                list.add(new Integer(i));
            }
        }
        return list;
    }

    /**
     * Add an empty block. This is useful for representing errors that have no specific location.
     * (Another solution might be a block from offset 0 to 0.)
     */
    public void addEmptyBlock() {
        pos.add(null);
    }

    public void addMarkedLines(final int fromLine, final int toLine, final int off)
            throws BadLocationException {
        int fromOffset = textComp.getLineStartOffset(fromLine);
        int toOffset = textComp.getLineEndOffset(toLine);
        addMarkedBlock(fromOffset, toOffset, fromOffset + off);
    }

    public void addMarkedBlock(final int startLine, final int startLineOffset, final int endLine,
            final int endLineOffset) throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) textComp.getDocument();
        int fromOffset = textComp.getLineStartOffset(startLine) + startLineOffset;
        int toOffset = textComp.getLineStartOffset(endLine) + endLineOffset;
        addMarkedBlock(doc.createPosition(fromOffset), doc.createPosition(toOffset - 1), doc
            .createPosition(fromOffset));
        textComp.getHighlighter().addHighlight(fromOffset + 1, toOffset - 1, highlightPainter);
    }

    private void addMarkedBlock(final int fromOffset, final int toOffset, final int pos)
            throws BadLocationException {
        AbstractDocument doc = (AbstractDocument) textComp.getDocument();
        addMarkedBlock(doc.createPosition(fromOffset), doc.createPosition(toOffset - 1), doc
            .createPosition(pos));
        textComp.getHighlighter().addHighlight(fromOffset + 1, toOffset - 1, highlightPainter);
    }

    private void addMarkedBlock(final Position start, final Position end, final Position pos)
            throws BadLocationException {
        this.pos.add(new Position[] {start, end, pos});
    }

}
