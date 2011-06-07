package de.dfki.embots.mocap.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.Transform3D;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Point3d;

import de.dfki.embots.mocap.JMocap;

/**
 * Pane on right hand side to load animations, change perspective,
 * playback etc.
 * 
 * @author Michael Kipp
 */
public class ControlPanel extends JPanel {

    private static final int DEFAULT_FPS = 120;
    private JMocap _app;
    private MocapGUI _gui;
    private int _lastRot;
    protected JButton _playButton;
    protected ImageIcon _playIcon,  _pauseIcon;
    private InfoPanel _info;
    private JLabel _fpsLabel;
    private JSlider _fpsSlider;

    protected ControlPanel(JMocap app, MocapGUI gui) {
        _app = app;
        _gui = gui;
        setLayout(new GridLayout(0, 1));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createLoadPane());
        add(createPlaybackPane());
        add(createFpsPane());
        add(create3DControls());
        add(createCursorControls());
        add(_info = new InfoPanel(app));
    }

    public InfoPanel getInfo() {
        return _info;
    }
    
    public void setFps(int n) {
        _fpsSlider.setValue(n);
    }

    private JPanel createLoadPane() {
        JPanel loadPane = new JPanel();
        loadPane.setBorder(BorderFactory.createEtchedBorder());
        JButton loadASF = new JButton("ASF");
        JButton loadAMC = new JButton("AMC");
        JButton loadBVH = new JButton("BVH");
        JButton clearAll = new JButton("Clear");
        loadPane.add(loadASF);
        loadPane.add(loadAMC);
        loadPane.add(loadBVH);
        loadPane.add(clearAll);
        loadASF.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                _gui.loadASFAction();
            }
        });
        loadAMC.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                _gui.loadAMCAction();
            }
        });
        loadBVH.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                _gui.loadBVHAction();
            }
        });
        clearAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                _app.clearAll();
            }
        });
        return loadPane;
    }

    private JPanel createPlaybackPane() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEtchedBorder());
        _playIcon = new ImageIcon("img/Play16.gif");
        _pauseIcon = new ImageIcon("img/Pause16.gif");
        _playButton = new JButton(_playIcon);
        JButton stop = new JButton(new ImageIcon("img/Stop16.gif"));
        JButton ffwd = new JButton(new ImageIcon("img/StepForward16.gif"));
        JButton fbwd = new JButton(new ImageIcon("img/StepBack16.gif"));
        JButton reset = new JButton("reset");
        _playButton.setFocusPainted(false);
        _playButton.setRolloverEnabled(false);
        stop.setFocusPainted(false);
        stop.setRolloverEnabled(false);
        _playButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (_playButton.getIcon() == _playIcon) {
                    if (_app.getFigureManager().playAll()) {
                        _playButton.setIcon(_pauseIcon);
                    }
                } else {
                    pause();
                    _playButton.setIcon(_playIcon);
                }
            }
        });
        stop.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (_playButton.getIcon() == _pauseIcon) {
                    _playButton.setIcon(_playIcon);
                }
                _app.getFigureManager().stopAll();
            }
        });
        ffwd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pause();
                _app.getFigureManager().frameForwardAll();
            }
        });
        fbwd.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                pause();
                _app.getFigureManager().frameBackwardAll();
            }
        });
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                _app.getFigure().getSkeleton().reset();
            }
        });
        p.add(_playButton);
        p.add(stop);
        p.add(fbwd);
        p.add(ffwd);
        p.add(reset);
        return p;
    }

    /**
     * Panel for 3D controls: rotation and zoom.
     */
    private JPanel create3DControls() {
        JPanel p = new JPanel();
        JPanel p1 = new JPanel();
        JLabel lrot = new JLabel("Rotate:");
        lrot.setHorizontalAlignment(JLabel.CENTER);
        JLabel lzoom = new JLabel("Zoom:");
        p.setBorder(BorderFactory.createEtchedBorder());
        final JSlider rotSlider = new JSlider(-180, 180);
        rotSlider.setMajorTickSpacing(180);
        rotSlider.setPaintTicks(true);
        rotSlider.setPaintLabels(true);
        p1.setLayout(new GridLayout(0, 1));
        p1.add(lrot);
        p1.add(rotSlider);
        final JSlider distSlider = new JSlider(JSlider.VERTICAL, 0, 50, (int) MocapGUI.CAMERA.z);
        distSlider.setMajorTickSpacing(25);
        distSlider.setPaintLabels(true);
        distSlider.setPaintTicks(true);
        distSlider.setPreferredSize(new Dimension(50, 90));
        rotSlider.setPreferredSize(new Dimension(120, 50));
        _lastRot = 0;
        rotSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                Transform3D tf = new Transform3D();
                Transform3D t2 = new Transform3D();
                t2.rotY(Math.toRadians(_lastRot - rotSlider.getValue()));
                _lastRot = rotSlider.getValue();
                _app.getUniverse().getViewingPlatform().getViewPlatformTransform().getTransform(tf);
                t2.mul(tf);
                _app.getUniverse().getViewingPlatform().getViewPlatformTransform().setTransform(t2);
            }
        });
        distSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                Transform3D tf = new Transform3D();
                _app.getUniverse().getViewingPlatform().getViewPlatformTransform().getTransform(tf);
                Point3d cam = new Point3d(0, 0, 0);
                tf.transform(cam);
                cam.z = distSlider.getValue();
                _app.setCameraView(cam, MocapGUI.CAMERA_TARGET);
            }
        });
        p.add(p1);
        p.add(lzoom);
        p.add(distSlider);
        return p;
    }

    /**
     * Panel for 3D controls: rotation and zoom.
     */
    private JPanel createCursorControls() {
        final int MAX = 300;
        JPanel p = new JPanel();
        JPanel p1 = new JPanel();
        JLabel lrot = new JLabel("x:");
        lrot.setHorizontalAlignment(JLabel.CENTER);
        JLabel lzoom = new JLabel("z:");
        p.setBorder(BorderFactory.createEtchedBorder());
        final JSlider xSlider = new JSlider(-MAX, MAX);
        xSlider.setMajorTickSpacing(100);
        xSlider.setPaintTicks(true);
        xSlider.setPaintLabels(true);
        p1.setLayout(new GridLayout(0, 1));
        p1.add(lrot);
        p1.add(xSlider);
        final JSlider zSlider = new JSlider(JSlider.VERTICAL, -MAX, MAX, 0);
        zSlider.setMajorTickSpacing(100);
        zSlider.setPaintLabels(true);
        zSlider.setPaintTicks(true);
        xSlider.setPreferredSize(new Dimension(150, 50));
        zSlider.setPreferredSize(new Dimension(50, 120));
        
        _lastRot = 0;
        ChangeListener ch = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                _gui.moveCursor(xSlider.getValue()/10f, -zSlider.getValue()/10f);
            }
        };
        xSlider.addChangeListener(ch);
        zSlider.addChangeListener(ch);
        p.add(p1);
        p.add(lzoom);
        p.add(zSlider);
        return p;
    }
    
    private JSlider createFpsSlider() {
        final JSlider s = new JSlider(JSlider.HORIZONTAL, 0, 200, DEFAULT_FPS);
        s.setPreferredSize(new Dimension(120, 50));
        s.setMinorTickSpacing(10);
        s.setMajorTickSpacing(50);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        s.setSnapToTicks(true);
        s.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int i = s.getValue();
                _fpsLabel.setText("fps: " + i);
                _app.getFigureManager().setFpsAll(s.getValue());
            }
        });
        _fpsSlider = s;
        return s;
    }

    private JPanel createFpsPane() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 1));
        p.setBorder(BorderFactory.createEtchedBorder());
        _fpsLabel = new JLabel("fps: " + DEFAULT_FPS);
        _fpsLabel.setHorizontalAlignment(JLabel.CENTER);
        p.add(_fpsLabel);
        p.add(createFpsSlider());
        p.setPreferredSize(new Dimension(200, 100));
        return p;
    }

    protected void pause() {
        if (_playButton.getIcon() == _pauseIcon) {
            _playButton.setIcon(_playIcon);
            _app.getFigureManager().pauseAll();
        }
    }
}
