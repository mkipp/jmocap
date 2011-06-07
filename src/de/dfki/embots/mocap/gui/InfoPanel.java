package de.dfki.embots.mocap.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.dfki.embots.mocap.JMocap;
import de.dfki.embots.mocap.player.PlayerFrameListener;

/**
 *
 * @author Michael Kipp
 */
public class InfoPanel extends JPanel implements PlayerFrameListener {

    private static final Font FONT_INFO = new Font("Arial", Font.BOLD, 14);
    private static final Color COLOR_INFO = Color.BLUE;
    private JLabel _infoASF, _infoAMC, _infoTotalFrames, _infoFrame;

    public InfoPanel(JMocap app) {
        setLayout(new GridLayout(0, 1));
        _infoASF = createLabel("skeleton: ---");
        _infoAMC = createLabel("motion: ---");
        _infoTotalFrames = createLabel("total frames: ---");
        _infoFrame = createLabel("frame: ---");
//        app.getPlayer().addListener(this);
    }

    public void updateSkeleton(String name) {
        _infoASF.setText("skeleton: " + name);
    }

    public void updateAnim(String name) {
        _infoAMC.setText("motion: " + name);
    }
    
    public void updateTotalFrames(int n) {
        _infoTotalFrames.setText("total frames: " + n);
    }

    public void updateFrame(int n) {
        _infoFrame.setText("frame: " + n);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_INFO);
        l.setForeground(COLOR_INFO);
        add(l);
        return l;
    }

    public void frameUpdate(int framenumber) {
        updateFrame(framenumber);
    }
}
