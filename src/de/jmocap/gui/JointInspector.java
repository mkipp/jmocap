package de.jmocap.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.vecmath.Point3d;

import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import de.jmocap.anim.FrameChangeListener;

/**
 *
 * @author Michael Kipp
 */
public class JointInspector extends JFrame implements FrameChangeListener
{

    private JLabel _rpos, _tpos, _hpos, _x, _y, _z;
    private DecimalFormat _fm = new DecimalFormat("##.##");
    private Point3d _p = new Point3d();
    private Bone _bone, _root, _thorax;

    public JointInspector(Figure figure, Bone bone)
    {
        _bone = bone;
        _root = figure.getSkeleton();
        _thorax = figure.getSkeleton().findBone("thorax");
        figure.getPlayer().addListener(this);
        setTitle("Inspect: " + bone.getName());
        JLabel head = new JLabel("Joint: " + _bone.getName(), SwingConstants.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        _rpos = new JLabel("root: --                         ");
        _tpos = new JLabel("thorax: --                         ");
        _hpos = new JLabel("hand: --                         ");
        _x = new JLabel("x: --");
        _y = new JLabel("y: --");
        _z = new JLabel("z: --");
        p.setLayout(new GridLayout(0,1));
        p.add(_rpos);
        p.add(_tpos);
        p.add(_hpos);
        p.add(_x);
        p.add(_y);
        p.add(_z);
        setLayout(new BorderLayout());
        add(head, BorderLayout.NORTH);
        add(p, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    public void frameUpdate(int framenumber)
    {
        _bone.getWorldPosition(_p);
        Point3d rootpos = new Point3d();
        Point3d rel = new Point3d();
        Point3d tho = new Point3d();
        Point3d thopos = new Point3d();
        Point3d hpos = new Point3d();
        _root.getWorldPosition(rootpos);
        _thorax.getWorldPosition(thopos);
        _bone.getRelativeTransPosition(_root, rel);
        _bone.getRelativeTransPosition(_thorax, tho);
        _bone.getWorldPosition(hpos);
        _rpos.setText("root: (" + _fm.format(rootpos.x) + ", " + _fm.format(rootpos.y) + ", " + _fm.format(rootpos.z) + ")");
        _tpos.setText("thorax: (" + _fm.format(thopos.x) + ", " + _fm.format(thopos.y) + ", " + _fm.format(thopos.z) + ")");
        _hpos.setText("hand: (" + _fm.format(hpos.x) + ", " + _fm.format(hpos.y) + ", " + _fm.format(hpos.z) + ")");
        _x.setText("x: " + _fm.format(_p.x) + "   rx: " + _fm.format(rel.x) + "   tx: " + _fm.format(tho.x));
        _y.setText("y: " + _fm.format(_p.y) + "   ry: " + _fm.format(rel.y) + "   ty: " + _fm.format(tho.y));
        _z.setText("z: " +_fm.format(_p.z) + "   rz: " + _fm.format(rel.z) + "   txz " + _fm.format(tho.z));
    }
}
