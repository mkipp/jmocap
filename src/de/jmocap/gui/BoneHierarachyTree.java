package de.jmocap.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.jmocap.figure.Bone;

/**
 * Shows the skeletal hierarchy in a tree.
 * 
 * @author Michael Kipp
 */
public class BoneHierarachyTree extends JPanel implements TreeSelectionListener {

    private JTree _tree;
    private JLabel _info;
    private BoneInfoPanel _boneInfo;
    private Bone _prevSelected;

    public BoneHierarachyTree(BoneInfoPanel bip) {
        setLayout(new BorderLayout());
        _boneInfo = bip;
        _info = new JLabel("--");
        _info.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        _tree = new JTree();
        _tree.addTreeSelectionListener(this);
        JScrollPane sp = new JScrollPane(_tree);
        add(BorderLayout.NORTH, _info);
        add(BorderLayout.CENTER, sp);
        sp.setPreferredSize(new Dimension(200, 800));
    }

    public void loadSkeleton(Bone skel) {
        _tree.removeAll();
        DefaultMutableTreeNode r = new DefaultMutableTreeNode("skeleton");
        r.add(createNodes(skel));
        _tree.setModel(new DefaultTreeModel(r));

        for (int i = 0; i < _tree.getRowCount(); i++) {
            _tree.expandRow(i);
        }
    }

    private DefaultMutableTreeNode createNodes(Bone b) {
        final Bone b1 = b;
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(b) {

            @Override
            public String toString() {
                return b1.getName();
            }
        };
        for (int i = 0; i < b.getChildren().length; i++) {
            n.add(createNodes(b.getChildren()[i]));
        }
        return n;
    }

    public Bone getSelectedBone() {
        TreePath p = _tree.getSelectionPath();
        if (p != null) {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) p.getLastPathComponent();
            return n.getUserObject() instanceof Bone ? (Bone) n.getUserObject() : null;
        }
        return null;
    }

    public void valueChanged(TreeSelectionEvent e) {
        Bone b = getSelectedBone();
        if (b != null) {
            _info.setText(b.getName());
            _boneInfo.setBone(b);
            b.setSelected(true);
            if (_prevSelected != null) {
                _prevSelected.setSelected(false);
            }
            _prevSelected = b;
        }
    }
}
