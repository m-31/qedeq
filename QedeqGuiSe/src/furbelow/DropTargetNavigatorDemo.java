/* Copyright (c) 2006-2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package furbelow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.*;
import java.awt.dnd.DnDConstants;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
/** Demonstrates automatic drop target navigation. */
public class DropTargetNavigatorDemo {
    private static class TestScrollable extends JComponent implements Scrollable {
        public TestScrollable() {
            setTransferHandler(new TransferHandler() {
                public boolean canImport(JComponent c, DataFlavor[] flavors) {
                    return Arrays.asList(flavors).contains(DataFlavor.stringFlavor);
                }
                public boolean importData(JComponent src, Transferable data) {
                    return true;
                }
            });
        }
        public Dimension getPreferredSize() {
            return new Dimension(10000, 10000);
        }
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(200, 200);
        }
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 55;
        }
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 1;
        }
        public void paint(Graphics g) {
            g.setColor(Color.black);
            for (int i=0;i < getWidth();i+=50) {
                g.drawLine(i, 0, i, getHeight()-1);
            }
            for (int i=0;i < getHeight();i+=50) {
                g.drawLine(0, i, getWidth()-1, i);
            }
        }
    }
    private static Component createTab(String name, int index) {
        JPanel p = new JPanel(new BorderLayout());
        if (index == 0) {
            p.add(new JScrollPane(new TestScrollable()));
        }
        else {
            Box box = Box.createVerticalBox();
            for (int i=0;i < index + 1;i++) {
                JPanel row = new JPanel(new BorderLayout());
                JLabel label = new JLabel(name + " Value " + i);
                label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
                row.add(label, BorderLayout.WEST);
                row.add(new JTextField(30));
                box.add(row);
                row.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            }
            box.setBorder(BorderFactory.createEmptyBorder(4,4,0,4));
            p.add(box, BorderLayout.NORTH);
        }
        return p;
    }
    private static JTabbedPane createTabs() {
        final JTabbedPane tabs = new JTabbedPane();
        tabs.setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent c, DataFlavor[] flavors) {
                return Arrays.asList(flavors).contains(DataFlavor.stringFlavor);
            }
            public boolean importData(JComponent c, Transferable data) {
                try {
                    String name = (String)data.getTransferData(DataFlavor.stringFlavor);
                    tabs.addTab(name, createTab(name, 1));
                    return true;
                }
                catch (Exception e) { 
                }
                return false;
                
            }
        });
        for (int i=0;i < 4;i++) {
            String name = "Tab " + i;
            tabs.addTab(name, createTab(name, i));
            // Java 6 only:
            //tabs.setTabComponentAt(i, new JLabel("*"+name+"*"));
        }
        return tabs;
    }
    private static JTree createTree() {
        final JTree tree = new JTree();
        tree.setDragEnabled(true);
        tree.setEditable(true);
        tree.expandRow(2);
        tree.setTransferHandler(new TransferHandler() {
            public int getSourceActions(JComponent c) {
                return DnDConstants.ACTION_COPY;
            }
            protected Transferable createTransferable(JComponent src) {
                TreePath path = tree.getSelectionPath();
                return new StringSelection(path.getLastPathComponent().toString());
            }
            public boolean canImport(JComponent c, DataFlavor[] flavors) {
                return Arrays.asList(flavors).contains(DataFlavor.stringFlavor);
            }
            public boolean importData(JComponent c, Transferable data) {
                TreePath path = tree.getSelectionPath();
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        path.getLastPathComponent();
                    DefaultTreeModel m = (DefaultTreeModel)tree.getModel();
                    try {
                        String string = (String)data.getTransferData(DataFlavor.stringFlavor);
                        int idx = m.getChildCount(node);
                        m.insertNodeInto(new DefaultMutableTreeNode(string), node, idx);
                        return true;
                    }
                    catch (Exception e) { 
                    }
                }
                return false;
            }
        });
        return tree;
    }
    private static JFrame createFrame() {
        JFrame frame = new JFrame("Drop Target Navigation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = (JPanel)frame.getContentPane();
        JLabel label = new JLabel("Drag an item from the tree or another program");
        Font font = label.getFont();
        label.setFont(font.deriveFont(font.getSize()*2f));
        label.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        p.add(label, BorderLayout.NORTH);
        label = new JLabel("Drag over other tabs and unexpanded tree nodes");
        label.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        p.add(label, BorderLayout.SOUTH);
        p.add(new JScrollPane(createTree()), BorderLayout.WEST);
        p.add(createTabs());
        return frame;
    }
    
    public static void main(String[] args) {
        DropTargetNavigator.enableDropTargetNavigation();
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            JFrame frame = createFrame();
            frame.pack();
            frame.setVisible(true);
        }});
    }
}
