package furbelow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.SimpleBeanInfo;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class AnimatedIconDemo extends JApplet {
    protected static Icon LOADING_ICON;
    protected static Icon SPINNING_ICON;
    
    private static WaitIndicator waiter;
    
    public void init() {
        SPINNING_ICON = new SpinningDial(16, 16);
        try {
            URL url = getClass().getResource("anim.gif");
            if (url == null) {
                url = getClass().getResource("/anim.gif");
            }
            if (url != null) {
                //System.out.println("found at " + url);
                LOADING_ICON = new ImageIcon(url);
            }
            if (LOADING_ICON == null) {
                Image image = new SimpleBeanInfo().loadImage("anim.gif");
                if (image == null) {
                    image = new SimpleBeanInfo().loadImage("/anim.gif");
                }
                if (image != null) {
                    //System.out.println("found at " + image);
                    LOADING_ICON = new ImageIcon(image);
                }
            }
            if (LOADING_ICON != null) {
                LOADING_ICON = new AnimatedIcon((ImageIcon)LOADING_ICON);
                setContentPane(createContentPane());
            }
            else {
                getContentPane().add(new JLabel("Image not found"));
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
            getContentPane().add(new JTextArea(t.toString()));
        }
    }
    
    public void start() {
        waiter = new SpinningDialWaitIndicator((JComponent)getContentPane());
        new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (waiter != null) {
                    waiter.dispose();
                    waiter = null;
                }
            }
        }).start();
    }
    
    private static Container createContentPane() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
        DefaultTreeModel model = new LazyTreeModel(root);
        JTree tree = new JTree(model);
        TreeCellRenderer r = new DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                          boolean selected, 
                                                          boolean expanded,
                                                          boolean isLeaf, 
                                                          int row, 
                                                          boolean focused) {
                Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
                TreePath path = tree.getPathForRow(row);
                if (path != null && path.getLastPathComponent() instanceof LoadingNode) {
                    setIcon(LOADING_ICON);
                }
                return c;
            }
        };
        tree.setCellRenderer(r);
        tree.setRootVisible(true);
        
        JLabel topLabel = new JLabel("Processing", LOADING_ICON, SwingConstants.LEFT);
        topLabel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        JLabel bottomLabel = new JLabel("Click Here");
        bottomLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        bottomLabel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        bottomLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JLabel label = (JLabel)e.getComponent();
                if (label.getIcon() == null)
                    label.setIcon(LOADING_ICON);
                else if (label.getIcon() == LOADING_ICON)
                    label.setIcon(SPINNING_ICON);
                else
                    label.setIcon(null);
            }
        });
        
        p.add(topLabel, BorderLayout.NORTH);
        p.add(new JScrollPane(tree));
        p.add(bottomLabel, BorderLayout.SOUTH);

        return p;
    }
    
    private static final class LazyTreeModel extends DefaultTreeModel {
        private Set loading = new HashSet();
        public LazyTreeModel(TreeNode root) {
            super(root);
            setAsksAllowsChildren(true);
        }
        private void populate(Object node) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
            n.removeAllChildren();
            for (int i=0;i < 5;i++) {
                n.add(new DefaultMutableTreeNode("child " + i, true));
            }
            fireTreeStructureChanged(this, n.getPath(), null, null);
        }
        private void loadChildren(final Object node) {
            if (!loading.contains(node)) {
                loading.add(node);
                new Thread("Load children for " + node) {
                    public void run() {
                        try { sleep(5000); } catch(InterruptedException e) { }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                populate(node);
                            }
                        });
                    }
                }.start();
            }
        }
        public int getChildCount(Object node) {
            int count = super.getChildCount(node);
            if (count == 0) {
                loadChildren(node);
                return 1;
            }
            return count;
        }
        public Object getChild(Object node, int idx) {
            if (super.getChildCount(node) == 0) {
                ((DefaultMutableTreeNode)node).add(new LoadingNode());
            }
            return super.getChild(node, idx);
        }
    }
    private static class LoadingNode extends DefaultMutableTreeNode {
        public LoadingNode() {
            super("Loading...", false);
        }
        public int getChildCount() { return 0; }
        
    }
    public static void main(String[] args) {
        URL url = AnimatedIconDemo.class.getResource("anim.gif");
        LOADING_ICON = new AnimatedIcon(new ImageIcon(url));
        SPINNING_ICON = new SpinningDial(16, 16);
        JFrame frame = new JFrame("Cell Animator Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(createContentPane());
        frame.pack();
        frame.setLocation(100, 100);
        frame.setSize(new Dimension(200, 300));
        frame.setVisible(true);
        
        waiter = new SpinningDialWaitIndicator(frame);
        new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (waiter != null) {
                    waiter.dispose();
                    waiter = null;
                }
            }
        }) {
            { setRepeats(false); }
        }.start();
    }
}
