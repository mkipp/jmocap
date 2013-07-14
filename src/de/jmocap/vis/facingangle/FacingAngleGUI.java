package de.jmocap.vis.facingangle;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class FacingAngleGUI {

    private FacingAngleController _fac;
    private JTextField _textFigure1;
    private JTextField _textFigure2;
    private JTextField _text1ShoulderLeft;
    private JTextField _text1ShoulderRight;
    private JTextField _text2ShoulderLeft;
    private JTextField _text2ShoulderRight;
    private JButton _buttonSetFacingAngle;
    private JButton _buttonSetShoulders;
    private JTextField _textHeightA;
    private JTextField _textHeightB;
    private JTextField _textHeightMiddle;
    private JTextField _textRotation;

    public FacingAngleGUI(FacingAngleController facingAngleController) {
        _fac = facingAngleController;

        JFrame frame = new JFrame("Facing Angle");
        JPanel panel = new JPanel(new FlowLayout());

        JLabel labelFigure1 = new JLabel("Figure 1 Name: ");
        _textFigure1 = new JTextField(15);
        panel.add(labelFigure1);
        panel.add(_textFigure1);
        JLabel labelFigure2 = new JLabel("Figure 2 Name: ");
        _textFigure2 = new JTextField(15);
        panel.add(labelFigure2);
        panel.add(_textFigure2);

        JLabel label1ShoulderLeft = new JLabel("Figure 1 Left Shoulder: ");
//        _text1ShoulderLeft = new JTextField(10);
        _text1ShoulderLeft = new JTextField("L_Humerus"); //***
        panel.add(label1ShoulderLeft);
        panel.add(_text1ShoulderLeft);
        JLabel label1ShoulderRight = new JLabel("Figure 1 Right Shoulder: ");
//        _text1ShoulderRight = new JTextField(10);
        _text1ShoulderRight = new JTextField("R_Humerus"); //***
        panel.add(label1ShoulderRight);
        panel.add(_text1ShoulderRight);

        JLabel label2ShoulderLeft = new JLabel("Figure 2 Left Shoulder: ");
//        _text2ShoulderLeft = new JTextField(10);
        _text2ShoulderLeft = new JTextField("L_Humerus"); //***
        panel.add(label2ShoulderLeft);
        panel.add(_text2ShoulderLeft);
        JLabel label2ShoulderRight = new JLabel("Figure 2 Right Shoulder: ");
//        _text2ShoulderRight = new JTextField(10);
        _text2ShoulderRight = new JTextField("R_Humerus"); //***
        panel.add(label2ShoulderRight);
        panel.add(_text2ShoulderRight);

        _buttonSetFacingAngle = new JButton("Set Facing Angle");
        panel.add(_buttonSetFacingAngle);
        _buttonSetShoulders = new JButton("Set Soulder Bones");
        panel.add(_buttonSetShoulders);

        JButton buttonInvisible = new JButton("Make Invisible");
        buttonInvisible.addActionListener(new ButtonInvisibleListener());
        panel.add(buttonInvisible);
        JButton buttonVisible = new JButton("Make Visible");
        buttonVisible.addActionListener(new ButtonVisibleListener());
        panel.add(buttonVisible);

        JLabel labelHeightA = new JLabel("Height A: ");
        _textHeightA = new JTextField("0.0000");
        panel.add(labelHeightA);
        panel.add(_textHeightA);
        JLabel labelHeightB = new JLabel("Height B: ");
        _textHeightB = new JTextField("0.0000");
        panel.add(labelHeightB);
        panel.add(_textHeightB);
        JLabel labelHeightMiddle = new JLabel("Height Middle: ");
        _textHeightMiddle = new JTextField("0.0000");
        panel.add(labelHeightMiddle);
        panel.add(_textHeightMiddle);
        JButton buttonSetHeight = new JButton("Set Height");
        buttonSetHeight.addActionListener(new ButtonSetHeightListener());
        panel.add(buttonSetHeight);

        _buttonSetFacingAngle.addActionListener(new ButtonSetFacingAngleListener());
        _buttonSetShoulders.addActionListener(new ButtonSetShouldersListener());

        frame.add(panel);
        frame.setSize(250, 400);
        frame.setVisible(true);
    }

    class ButtonSetFacingAngleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            _fac.createFacingAngle(_textFigure1.getText(), _textFigure2.getText(),
                    _text1ShoulderLeft.getText(), _text1ShoulderRight.getText(),
                    _text2ShoulderLeft.getText(), _text2ShoulderRight.getText());
        }
    }

    class ButtonSetShouldersListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            _fac.setShoulderBones(_text1ShoulderLeft.getText(), _text1ShoulderRight.getText(), _text2ShoulderLeft.getText(), _text2ShoulderRight.getText());
        }
    }

    class ButtonInvisibleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            _fac.setInvisible();
        }
    }

    class ButtonVisibleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            _fac.setVisible();
        }
    }

    class ButtonSetHeightListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            double a = Double.valueOf(_textHeightA.getText());
            double b = Double.valueOf(_textHeightB.getText());
            double middle = Double.valueOf(_textHeightMiddle.getText());
            _fac.setHeightA(a);
            _fac.setHeightB(b);
            _fac.setHeightMiddle(middle);
        }
    }
}