package de.dfki.embots.mocap.gui;

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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

import de.dfki.embots.mocap.JMocap;
import de.dfki.embots.mocap.figure.Bone;
import de.dfki.embots.mocap.figure.BoneGeom;
import de.dfki.embots.mocap.figure.JointGeom;
import de.dfki.embots.mocap.player.AnimClock;
import de.dfki.embots.mocap.reader.ASFReader;

/**
 * Camera is set for a meter system, looking at a 2m person.
 * 
 * @author Michael Kipp
 */
public class MocapGUI extends JFrame implements ActionListener
{

    private static final float CURSOR_RADIUS = .02f;

    private static final String PROPERTIES_FILE = ".jmocap";

    private static final String PROP_DIR = "dir";

    private static final String PROP_LAST_ASF = "file.asf";

    private static final String PROP_LAST_AMC = "file.amc";

    private static final String PROP_LAST_BVH = "file.bvh";

    public static final String MENU_RESET_CAM = "Reset camera";

    private static final String LOAD_ASF = "Load ASF..";

    private static final String LOAD_AMC = "Load AMC..";

    public static final Point3d CAMERA = new Point3d(1, 3, 15);

    public static final Point3d CAMERA_TARGET = new Point3d(0, 1, 0);

    private Properties _prop = new Properties();

    private JMocap _app;

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
            if (item.endsWith("translation"))
            {
                _app.getFigure().getSkeleton().setTranslationEnabled(
                                                                     ((CheckboxMenuItem) e.getSource()).getState());
            }
            else
                if (item.endsWith("orientation"))
                {
                    _app.getFigure().getSkeleton().setRotationEnabled(
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
            if (item.equals("bones off"))
            {
                _app.getFigure().getSkeleton().selectGeom(BoneGeom.NONE);
            }
            else
                if (item.equals("lines"))
                {
                    _app.getFigure().getSkeleton().selectGeom(BoneGeom.LINE);
                }
                else
                    if (item.equals("joints off"))
                    {
                        _app.getFigure().getSkeleton().selectJointGeom(
                                                                       JointGeom.NONE);
                    }
                    else
                        if (item.equals("crosses"))
                        {
                            _app.getFigure().getSkeleton().selectJointGeom(
                                                                           JointGeom.CROSS);
                        }
                        else
                            if (item.equals("small spheres"))
                            {
                                _app.getFigure().getSkeleton().selectJointGeom(
                                                                               JointGeom.SPHERE_SMALL);
                            }
                            else
                                if (item.equals("big spheres"))
                                {
                                    _app.getFigure().getSkeleton().selectJointGeom(
                                                                                   JointGeom.SPHERE_BIG);
                                }
                                else
                                    if (item.equals("bone name (small)"))
                                    {
                                        _app.getFigure().getSkeleton().displayName(
                                                                                   Bone.SMALL_NAME);
                                    }
                                    else
                                        if (item.equals("bone name (big)"))
                                        {
                                            _app.getFigure().getSkeleton().displayName(
                                                                                       Bone.BIG_NAME);
                                        }
                                        else
                                            if (item.equals("no bone names"))
                                            {
                                                _app.getFigure().getSkeleton().displayName(
                                                                                           Bone.NO_NAME);
                                            }
                                            else
                                                if (item.equals("bone cylinders"))
                                                {
                                                    _app.getFigure().getSkeleton().selectGeom(
                                                                                              BoneGeom.CYLINDER);
                                                }
                                                else
                                                    if (item.equals("show coord"))
                                                    {
                                                        _app.showCoordCross(e.getStateChange() == ItemEvent.SELECTED);
                                                    }
                                                    else
                                                        if (item.equals("show floor"))
                                                        {
                                                            _app.showFloor(e.getStateChange() == ItemEvent.SELECTED);
                                                        }
                                                        else
                                                            if (item.equals("z up"))
                                                            {
                                                                if (e.getStateChange() == ItemEvent.SELECTED)
                                                                {
                                                                    _app.getFigure().setZUpRotation();
                                                                }
                                                                else
                                                                {
                                                                    _app.getFigure().resetRotation();
                                                                }
                                                            }
        }
    }

    public MocapGUI()
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
        _app = new JMocap();
        _app.setCameraView(CAMERA, CAMERA_TARGET);
        createCursor();

        // layout GUI
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, _app.getViewComponent());
        add(BorderLayout.EAST, _control = new ControlPanel(_app, this));
        // add(BorderLayout.SOUTH, new AnimationPanel(_app, this));
        // add(BorderLayout.WEST, createLeftPane());
        setMenuBar(createMenubar());
        loadProp();
        // loadPreviousSkeleton();
        AnimClock ac = new AnimClock(_app.getFigureManager());
        ac.start();
        pack();
        setVisible(true);
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
        _app.getRootBG().addChild(bg);
    }

    public void moveCursor(float x, float z)
    {
        Transform3D t = new Transform3D();
        t.setTranslation(new Vector3f(x, 0, z));
        _cursorTG.setTransform(t);
        _cursorPos.x = x;
        _cursorPos.z = z;
    }

    /**
     * Loads skeleton and anim from previous session.
     */
    private void loadPreviousSkeleton()
    {
        String fnASF = _prop.getProperty(PROP_LAST_ASF);
        String fnAMC = _prop.getProperty(PROP_LAST_AMC);
        if (fnASF != null && fnAMC != null)
        {
            try
            {
                File f = new File(fnASF);
                ASFReader rd = new ASFReader();
                Bone skel = rd.getSkeleton(f);
                _app.initFigure(skel, f.getName());
                updateSkeletonInfo(f.getName());
                f = new File(fnAMC);
                _app.loadAMC(f);
                updateAnimInfo(f.getName());
            }
            catch (IOException e)
            {
                System.out.println("ERROR! loadpreviousskeleton");
                e.printStackTrace();
                _app.clearAll();
            }
        }
    }

    private void updateSkeletonInfo(String name)
    {
        _control.getInfo().updateSkeleton(name);
        if (_boneTree != null)
        {
            _boneTree.loadSkeleton(_app.getFigure().getSkeleton());
        }
    }

    private void updateAnimInfo(String name)
    {
        _control.getInfo().updateAnim(name);
        // _control.getInfo().updateTotalFrames(_app.getFigure().getSkeleton().getNumFrames());
    }

    private MenuBar createMenubar()
    {
        MenuBar mb = new MenuBar();

        // ************** FILE MENU
        Menu m = new Menu("File");
        MenuItem mi = new MenuItem(LOAD_ASF);
        m.add(mi);
        mi.addActionListener(this);
        mi = new MenuItem(LOAD_AMC);
        m.add(mi);
        mi.addActionListener(this);
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
                new JointInspector(_app.getFigure(),
                                   _app.getFigure().getSkeleton().findBone(
                                                                           "lhand"));
            }
        });

        // ************** VIEW MENU
        m = new Menu("View");
        ViewMenuListener li = new ViewMenuListener();
        mb.add(m);
        mi = new MenuItem(MENU_RESET_CAM);
        mi.addActionListener(this);
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
        
        return mb;
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

    private File getPropFile()
    {
        return new File(System.getProperty("user.dir"), PROPERTIES_FILE);
    }

    private void loadProp()
    {
        File f = getPropFile();
        if (f.exists())
        {
            try
            {
                _prop.load(new FileReader(f));
            }
            catch (IOException ex)
            {
                Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE,
                                                             null, ex);
            }
        }
    }

    private void saveProp()
    {
        FileWriter w = null;
        try
        {
            w = new FileWriter(getPropFile());
            _prop.store(w, "DFKI-EMBOTS");
            w.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                w.close();
            }
            catch (IOException ex)
            {
                Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE,
                                                             null, ex);
            }
        }
    }

    private File promptForFile(String dirProp, final String ext)
    {
        FileFilter ff = new FileFilter()
        {

            @Override
            public boolean accept(File f)
            {
                return f.getName().toLowerCase().endsWith(ext) || f.isDirectory();
            }

            @Override
            public String getDescription()
            {
                return "All ." + ext + " files";
            }
        };
        String d = _prop.getProperty(dirProp, System.getProperty("user.dir"));
        JFileChooser fc = new JFileChooser(d);
        fc.setFileFilter(ff);
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION)
        {
            _prop.put(dirProp, fc.getSelectedFile().getParent().toString());
            saveProp();
            return fc.getSelectedFile();
        }
        return null;
    }

    protected void loadASFAction()
    {
        File f = promptForFile(PROP_DIR, "asf");
        if (f != null)
        {
            try
            {
                _app.getFigureManager().pauseAll();
                ASFReader rd = new ASFReader();
                Bone skel = rd.getSkeleton(f);
                _app.initFigure(skel, f.getName(), _cursorPos);
                updateSkeletonInfo(f.getName());
                _prop.put(PROP_LAST_ASF, f.toString());
                saveProp();
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(
                                              null,
                                              "Couldn't load " + f.getName() + ": " + ex.getMessage(),
                                              "loading error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void loadAMCAction()
    {
        File f = promptForFile(PROP_DIR, "amc");
        if (f != null)
        {
            // String result = JOptionPane.showInputDialog(null,
            // "Please enter layer number (0-2):", 0);
            // if (result != null) {
            try
            {
                // int layer = Integer.parseInt(result);
                _app.loadAMC(f);
                _prop.put(PROP_LAST_AMC, f.toString());
                saveProp();
                updateAnimInfo(f.getName());
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(
                                              null,
                                              "Couldn't load " + f.getName() + ": " + ex.getMessage(),
                                              "loading error",
                                              JOptionPane.ERROR_MESSAGE);
                // }
            }
        }
    }

    protected void loadBVHAction()
    {
        File f = promptForFile(PROP_DIR, "bvh");
        if (f != null)
        {
            String scale = JOptionPane.showInputDialog(
                                                       null,
                                                       "Please enter target height (or -1)",
                                                       5);
            if (scale != null)
            {
                try
                {
                    float sc = Float.parseFloat(scale);
                    _app.loadBVH(f, sc, _cursorPos);

                    // GUI related stuff:
                    updateSkeletonInfo(f.getName());
                    updateAnimInfo(f.getName());
                    _prop.put(PROP_LAST_BVH, f.toString());
                    saveProp();
                    _control.setFps((int) _app.getFigure().getPlayer().getPlaybackFps());
                }
                catch (IOException ex)
                {
                    JOptionPane.showMessageDialog(
                                                  null,
                                                  "Couldn't load " + f.getName() + ": " + ex.getMessage(),
                                                  "loading error",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String c = e.getActionCommand();
        System.out.println("command=" + c);
        if (c.equals(LOAD_ASF))
        {
            loadASFAction();
        }
        else
            if (c.equals(LOAD_AMC))
            {
                loadAMCAction();
            }
            else
                if (c.equals(MocapGUI.MENU_RESET_CAM))
                {
                    _app.setCameraView(CAMERA, CAMERA_TARGET);
                }
    }

    private void showPathController()
    {
        new MotionTrailsControllerGUI(_app); 
    }

}
