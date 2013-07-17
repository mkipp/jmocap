/**
 * JMOCAP
 * 
 * Developed by Michael Kipp, 2008-2011, DFKI Saarbrücken, Germany
 * E-Mail: mich.kipp@googlemail.com
 * 
 * This software has been released under the
 * GNU LESSER GENERAL PUBLIC LICENSE Version 3, 29 June 2007
 */
package de.jmocap.gui;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

import de.jmocap.JMocap;
import de.jmocap.figure.Bone;
import de.jmocap.figure.BoneGeom;
import de.jmocap.figure.JointGeom;
import de.jmocap.anim.AnimDriver;
import de.jmocap.vis.orientation.FacingAngleGUI;
import de.jmocap.vis.tangentialarrow.TangentialArrowGUI;
import javax.swing.JOptionPane;

/**
 * Camera is set for a meter system, looking at a 2m person.
 * 
 * @author Michael Kipp
 */
public class JMocapGUI extends JFrame
{

    private static final float CURSOR_RADIUS = .02f;
    public static final String MENU_RESET_CAM = "Reset camera";
    static final String LOAD_ASF = "Load ASF..";
    static final String LOAD_AMC = "Load AMC..";
    static final String LOAD_BVH = "Load BVH..";
    private JMocap _jMocap;
    private BoneHierarachyTree _boneTree;
    private ControlPanel _control;
    private TransformGroup _cursorTG;
    private Point3d _cursorPos = new Point3d();

    class PlaybackMenuListener implements ItemListener
    {

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            String item = e.getItem().toString();
            if (item.endsWith("translation")) {
                _jMocap.getFigure().getSkeleton().setTranslationEnabled(
                        ((CheckboxMenuItem) e.getSource()).getState());
            } else if (item.endsWith("orientation")) {
                _jMocap.getFigure().getSkeleton().setRotationEnabled(
                        ((CheckboxMenuItem) e.getSource()).getState());
            }
        }
    }

    class ViewMenuListener implements ItemListener
    {

        @Override
        public void itemStateChanged(ItemEvent e)
        {
            System.out.println("item= " + e.getItem());
            String item = e.getItem().toString();
            if (item.equals("bones off")) {
                _jMocap.getFigure().getSkeleton().selectGeom(BoneGeom.NONE);
            } else if (item.equals("lines")) {
                _jMocap.getFigure().getSkeleton().selectGeom(BoneGeom.LINE);
            } else if (item.equals("joints off")) {
                _jMocap.getFigure().getSkeleton().selectJointGeom(
                        JointGeom.NONE);
            } else if (item.equals("crosses")) {
                _jMocap.getFigure().getSkeleton().selectJointGeom(
                        JointGeom.CROSS);
            } else if (item.equals("small spheres")) {
                _jMocap.getFigure().getSkeleton().selectJointGeom(
                        JointGeom.SPHERE_SMALL);
            } else if (item.equals("big spheres")) {
                _jMocap.getFigure().getSkeleton().selectJointGeom(
                        JointGeom.SPHERE_BIG);
            } else if (item.equals("bone name (small)")) {
                _jMocap.getFigure().getSkeleton().displayName(
                        Bone.SMALL_NAME);
            } else if (item.equals("bone name (big)")) {
                _jMocap.getFigure().getSkeleton().displayName(
                        Bone.BIG_NAME);
            } else if (item.equals("no bone names")) {
                _jMocap.getFigure().getSkeleton().displayName(
                        Bone.NO_NAME);
            } else if (item.equals("bone cylinders")) {
                _jMocap.getFigure().getSkeleton().selectGeom(
                        BoneGeom.CYLINDER);
            } else if (item.equals("show coord")) {
                _jMocap.showCoordCross(e.getStateChange() == ItemEvent.SELECTED);
            } else if (item.equals("show floor")) {
                _jMocap.showFloor(e.getStateChange() == ItemEvent.SELECTED);
            } else if (item.equals("z up")) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    _jMocap.getFigure().setZUpRotation();
                } else {
                    _jMocap.getFigure().resetRotation();
                }
            }
        }
    }

    public JMocapGUI(ActionListener actionListener, JMocap jmocap)
    {
        super();
        setTitle("Mocap Viewer");
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });

        _jMocap = jmocap;

        createCursor();

        // layout GUI
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, _jMocap.getViewComponent());
        add(BorderLayout.EAST, _control = new ControlPanel(_jMocap, this, actionListener));
        // add(BorderLayout.SOUTH, new AnimationPanel(_app, this));
        // add(BorderLayout.WEST, createLeftPane());
        setMenuBar(createMenubar(actionListener));

        // loadPreviousSkeleton();
        AnimDriver ac = new AnimDriver(_jMocap.getFigureManager());
        ac.start();
        pack();
        setVisible(true);
    }

    public Point3d getCursorPos() {
        return _cursorPos;
    }
    
    private void createCursor()
    {
        _cursorTG = new TransformGroup();
        _cursorTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Sphere s = new Sphere(CURSOR_RADIUS);

        // wiring up
        BranchGroup bg = new BranchGroup();
        bg.addChild(_cursorTG);
        _cursorTG.addChild(s);
        _jMocap.getRootBG().addChild(bg);
    }

    public void moveCursor(float x, float z)
    {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(x, 0, z));
        _cursorTG.setTransform(t);
        _cursorPos.x = x;
        _cursorPos.z = z;
    }

    void updateSkeletonInfo(String name)
    {
        _control.getInfo().updateSkeleton(name);
        if (_boneTree != null) {
            _boneTree.loadSkeleton(_jMocap.getFigure().getSkeleton());
        }
    }

    void updateAnimInfo(String name)
    {
        _control.getInfo().updateAnim(name);
        // _control.getInfo().updateTotalFrames(_app.getFigure().getSkeleton().getNumFrames());
    }

    private MenuBar createMenubar(ActionListener actionListener)
    {
        MenuBar mb = new MenuBar();

        // ************** FILE MENU
        Menu m = new Menu("File");
        MenuItem mi = new MenuItem(LOAD_ASF);
        m.add(mi);
        mi.addActionListener(actionListener);
        mi = new MenuItem(LOAD_AMC);
        m.add(mi);
        mi.addActionListener(actionListener);
        mb.add(m);

        // ************** PLAYBACK MENU
        m = new Menu("Playback");
        PlaybackMenuListener pli = new PlaybackMenuListener();
        mb.add(m);
        CheckboxMenuItem cb = new CheckboxMenuItem("global translation");
        cb.setState(true);
        cb.addItemListener(pli);
        m.add(cb);
        cb = new CheckboxMenuItem("global orientation");
        cb.setState(true);
        cb.addItemListener(pli);
        m.add(cb);

        // ************** PLAYBACK MENU
        m = new Menu("Inspect");
        mb.add(m);
        mi = new MenuItem("Inspect bone");
        m.add(mi);
        mi.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                new JointInspector(_jMocap.getFigure(),
                        _jMocap.getFigure().getSkeleton().findBone(
                        "lhand"));
            }
        });

        // ************** VIEW MENU
        m = new Menu("View");
        ViewMenuListener li = new ViewMenuListener();
        mb.add(m);
        mi = new MenuItem(MENU_RESET_CAM);
        mi.addActionListener(actionListener);
        m.add(mi);
        cb = new CheckboxMenuItem("show coord", true);
        cb.addItemListener(li);
        m.add(cb);
        cb = new CheckboxMenuItem("show floor", true);
        cb.addItemListener(li);
        m.add(cb);
        cb = new CheckboxMenuItem("z up");
        cb.addItemListener(li);
        m.add(cb);
        Menu sm = new Menu("Bones");
        m.add(sm);
        cb = new CheckboxMenuItem("bones off");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("lines");
        cb.addItemListener(li);
        sm.add(cb);
        sm = new Menu("Joints");
        m.add(sm);
        cb = new CheckboxMenuItem("joints off");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("crosses");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("small spheres");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("big spheres");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("no bone names");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("bone name (small)");
        cb.addItemListener(li);
        sm.add(cb);
        cb = new CheckboxMenuItem("bone name (big)");
        cb.addItemListener(li);
        sm.add(cb);

        m = new Menu("Debug");
        mb.add(m);
        mi = new MenuItem("Path controller");
        m.add(mi);
        mi.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                showPathController();
            }
        });

        // Levin McNeillGrid
        
        mi = new MenuItem("Gesture Space (McNeill)");
        m.add(mi);
        mi.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
        	if(_jMocap.getFigure() == null){
				JOptionPane.showMessageDialog(new JFrame(), "BVH file required! ", "Warning", JOptionPane.WARNING_MESSAGE, null);
			}else  _jMocap.addMcNeillGrid();
        }
        });
        
        mi = new MenuItem("Relative Movement Plate");
        m.add(mi);
        mi.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
        	if(_jMocap.getFigure() == null){
			}else  _jMocap.addRelativeMovingPlates();
        }
        });
        
        // Michi JMocapDisk
        mi = new MenuItem("Speed Disk");
        m.add(mi);
        mi.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_jMocap.getFigure() == null){
				JOptionPane.showMessageDialog(new JFrame(), "BVH file required! ", "Warning", JOptionPane.WARNING_MESSAGE, null);
			}else  _jMocap.addDisk();
		}
            });
        
        // Michi JMocapDistancePlate
        mi = new MenuItem("Interpersonal Distance");
        m.add(mi);
        mi.addActionListener(new ActionListener() {
			
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_jMocap.getFigureManager().getFigures().size() < 2){
				JOptionPane.showMessageDialog(new JFrame(), "Two BVH files required! ", "Warning", JOptionPane.WARNING_MESSAGE, null);
			}else  _jMocap.addDistancePlate();
		}
            });
        
        // Franziska: TangentialArrowGUI
        mi = new MenuItem("Tangential Arrow");
        m.add(mi);
        mi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                TangentialArrowGUI newGUI = new TangentialArrowGUI(_jMocap.getTangentialArrowController());
            }
        });
        
        // Franziska: FacingAngleGUI
        mi = new MenuItem("Interpersonal Orientation");
        m.add(mi);
        mi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                FacingAngleGUI newGUI = new FacingAngleGUI(_jMocap.getFacingAngleController(),
                        _jMocap.getFigureManager().getFigures());
            }
        });
        
        return mb;
    }

    void setFps(int fps)
    {
        _control.setFps(fps);
    }

    private JPanel createLeftPane()
    {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        BoneInfoPanel bip = new BoneInfoPanel();
        _boneTree = new BoneHierarachyTree(bip);
        p.add(BorderLayout.CENTER, _boneTree);
        p.add(BorderLayout.SOUTH, bip);
        return p;
    }

    /**
     * Enables/disables only th ehierarchy with the given bone at its root.
     * 
     * Disables/enables the rest.
     * 
     * @param layer
     * @param val
     */
    // public void setAnimEnable(int layer, boolean val) {
    // Bone b = _boneTree.getSelectedBone();
    // if (b != null) {
    // _app.getFigure().getSkeleton().setLayerEnabled(layer, !val);
    // b.setLayerEnabled(layer, val);
    // }
    // }
    public void exit()
    {
        System.out.println("*** CLEAN EXIT ***");
        dispose();
        System.exit(0);
    }

    private void showPathController()
    {
        new MotionTrailsControllerGUI(_jMocap);
    }
}
