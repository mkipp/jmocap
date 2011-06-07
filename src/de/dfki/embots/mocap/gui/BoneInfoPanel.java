package de.dfki.embots.mocap.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.dfki.embots.mocap.figure.Bone;

/**
 *
 * @author Michael Kipp
 */
public class BoneInfoPanel extends JPanel implements ChangeListener {

    private JLabel _bone,  _dof,  _x,  _y,  _z;
    private JSlider _xs,  _ys,  _zs;
    private DecimalFormat _fm = new DecimalFormat("##.##");
    private Bone _currentBone;

    public BoneInfoPanel() {
        setBorder(BorderFactory.createTitledBorder("Joint"));
        _bone = new JLabel("name: ---");
        _dof = new JLabel("dof: ---");
        JPanel px = new JPanel();
        JPanel py = new JPanel();
        JPanel pz = new JPanel();
        _x = new JLabel("x: ---");
        _y = new JLabel("y: ---");
        _z = new JLabel("z: ---");
        _xs = createSlider();
        _ys = createSlider();
        _zs = createSlider();
        JButton apply = new JButton("apply");
        px.add(_x);
        px.add(_xs);
        py.add(_y);
        py.add(_ys);
        pz.add(_z);
        pz.add(_zs);
        setLayout(new GridLayout(0, 1));
        add(_bone);
        add(_dof);
        add(px);
        add(py);
        add(pz);
        add(apply);
        apply.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setBoneRotation();
            }
        });
    }

    private JSlider createSlider() {
        JSlider s = new JSlider(-60, 60);
        s.addChangeListener(this);
        s.setPaintLabels(true);
        s.setMajorTickSpacing(60);
        return s;
    }

    private void setBoneRotation() {
        if (_currentBone != null) {
            _currentBone.setRot((float) Math.toRadians(_xs.getValue()),
                    (float) Math.toRadians(_ys.getValue()),
                    (float) Math.toRadians(_zs.getValue()));
        }
    }

    public void setBone(Bone b) {
        _currentBone = b;
        _bone.setText("name: " + b.getName());
        _dof.setText(b.getDOFString());
//        setRot(b.getCurrentRotation());
    }

    private void setRot(float[] r) {
        if (r.length > 2) {
            double x = Math.toDegrees(r[0]);
            double y = Math.toDegrees(r[1]);
            double z = Math.toDegrees(r[2]);
            _x.setText("x: " + _fm.format(x));
            _y.setText("y: " + _fm.format(y));
            _z.setText("z: " + _fm.format(z));
            _xs.setValue((int) x);
            _ys.setValue((int) y);
            _zs.setValue((int) z);
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(_xs)) {
            _x.setText("x: " + _xs.getValue());
        }
        if (e.getSource().equals(_ys)) {
            _y.setText("y: " + _ys.getValue());
        }
        if (e.getSource().equals(_zs)) {
            _z.setText("z: " + _zs.getValue());
        }
    }
}
