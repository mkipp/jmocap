package de.jmocap.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.vecmath.Vector3d;

import de.jmocap.JMocap;
import de.jmocap.figure.MotionTrailPoint;

/**
 * GUI for testing motion trail visualization.
 *
 * @author Quan Nguyen
 */
public class MotionTrailsControllerGUI extends JFrame
{

    private JMocap _jmocap;
    private JTextField _jftStartFrame;
    private JTextField _jftEndFrame;
    private JLabel _jlColor;
    private JTextField _jtfJoint;
    private JButton _jbShowPath;

    public MotionTrailsControllerGUI(JMocap jmocap)
    {
        super("Path Controller");
        _jmocap = jmocap;
        initComponents();
    }

    private void initComponents()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setLayout(new GridLayout(/* 3 */0, 2, 6, 3));

        this.add(new JLabel(" Start time in sec.:"));
        _jftStartFrame = new JTextField("1");
        this.add(_jftStartFrame);
        this.add(new JLabel(" End time:"));
        _jftEndFrame = new JTextField("4");
        this.add(_jftEndFrame);
        this.add(new JLabel(" Color: "));
        _jlColor = new JLabel();
        _jlColor.setBackground(Color.MAGENTA);
        _jlColor.setOpaque(true);
        this.add(_jlColor);
        this.add(new JLabel(" Joint: "));
        _jtfJoint = new JTextField("rhand");
        this.add(_jtfJoint);

        this.add(new JLabel());
        Action showPathAction = new AbstractAction("Show path")
        {

            public void actionPerformed(ActionEvent evt)
            {
                showPath();
//				moveJoint();
            }
        };
        _jbShowPath = new JButton(showPathAction);
        this.add(_jbShowPath);

        this.pack();
        this.setVisible(true);

    }

    private void showPath()
    {
        if (_jmocap != null) {
            _jmocap.addMotionTrail(showMotionTrails(
                    Double.parseDouble(_jftStartFrame.getText()),
                    Double.parseDouble(_jftEndFrame.getText()),
                    Color.magenta,
                    _jtfJoint.getText()));
        } else {
            System.out.println("No Mocap");
        }
    }

    private void moveJoint()
    {
        _jmocap.getFigure().getSkeleton().setBaseRotDeg(new Vector3d(10, 90, 10));
        _jmocap.getFigure().getSkeleton().setBaseTranslation(new Vector3d(1, 1, 1));
    }

    private Vector<MotionTrailPoint> showMotionTrails(double start, double end,
            Color color, String bone)
    {

        Vector<MotionTrailPoint> vMotionTrailPoints = new Vector<MotionTrailPoint>();

        double dDistance = 1 / (float) _jmocap.getFigure().getPlayer().getPlaybackFps();
        if (bone != null) {
            for (double i = start; i < end; i += dDistance) {
                System.out.println("showMotionTrails::time::" + i);
                vMotionTrailPoints.add(new MotionTrailPoint(bone, i, color));
            }
        }
        System.out.println("showMotionTrails::vMotionTrailPoints::" + vMotionTrailPoints.size());
        return vMotionTrailPoints;
    }
}
