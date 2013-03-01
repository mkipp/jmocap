/**
 * JMOCAP
 *
 * Developed by Michael Kipp, 2008-2011, DFKI Saarbrücken, Germany E-Mail:
 * mich.kipp@googlemail.com
 *
 * This software has been released under the GNU LESSER GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 */
package de.jmocap.gui;

import de.jmocap.JMocap;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import de.jmocap.reader.ASFReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.vecmath.Point3d;

/**
 *
 * @author Michael Kipp
 */
public class JMocapController implements ActionListener {

    private static final String PROP_DIR = "dir";
    public static final Point3d CAMERA = new Point3d(1, 3, 15);
    public static final Point3d CAMERA_TARGET = new Point3d(0, 1, 0);
    private static final String PROP_LAST_ASF = "file.asf";
    private static final String PROP_LAST_AMC = "file.amc";
    private static final String PROP_LAST_BVH = "file.bvh";
    private static final String PROPERTIES_FILE = ".jmocap";
    private JMocap _jmocap;
    private JMocapGUI _view;
    private Properties _prop = new Properties();

    public JMocapController() {
        _jmocap = new JMocap();
        _jmocap.setCameraView(CAMERA, CAMERA_TARGET);
        loadProp();
        _view = new JMocapGUI(this, _jmocap);
    }

    /**
     * Loads skeleton and anim from previous session.
     */
    private void loadPreviousSkeleton() {
        String fnASF = _prop.getProperty(PROP_LAST_ASF);
        String fnAMC = _prop.getProperty(PROP_LAST_AMC);
        if (fnASF != null && fnAMC != null) {
            try {
                File f = new File(fnASF);
                ASFReader rd = new ASFReader();
                Bone skel = rd.readSkeleton(f);
                _jmocap.initFigure(skel, f.getName());
                _view.updateSkeletonInfo(f.getName());
                f = new File(fnAMC);
                _jmocap.loadAMC(f);
                _view.updateAnimInfo(f.getName());
            } catch (IOException e) {
                System.out.println("ERROR! loadpreviousskeleton");
                e.printStackTrace();
                _jmocap.clearAll();
            }
        }
    }

    private File getPropFile() {
        return new File(System.getProperty("user.dir"), PROPERTIES_FILE);
    }

    private void loadProp() {
        File f = getPropFile();
        if (f.exists()) {
            try {
                _prop.load(new FileReader(f));
            } catch (IOException ex) {
                Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    private void saveProp() {
        FileWriter w = null;
        try {
            w = new FileWriter(getPropFile());
            _prop.store(w, "DFKI-EMBOTS");
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                w.close();
            } catch (IOException ex) {
                Logger.getLogger(JMocap.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }

    private File promptForFile(String dirProp, final String ext) {
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(ext) || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "All ." + ext + " files";
            }
        };
        String d = _prop.getProperty(dirProp, System.getProperty("user.dir"));
        JFileChooser fc = new JFileChooser(d);
        fc.setFileFilter(ff);
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            _prop.put(dirProp, fc.getSelectedFile().getParent().toString());
            saveProp();
            return fc.getSelectedFile();
        }
        return null;
    }

    /**
     * Loads definition file for a skeleton (ASF format).
     */
    protected void loadASFAction() {
        File f = promptForFile(PROP_DIR, "asf");
        if (f != null) {
            try {
                _jmocap.getFigureManager().pauseAll();
                
                _jmocap.loadASF(f, _view.getCursorPos());
                
//                ASFReader rd = new ASFReader();
//                Bone skel = rd.getSkeleton(f);
//                _jMocap.initFigure(skel, f.getName(), _view.getCursorPos());
                
                
                _view.updateSkeletonInfo(f.getName());
                _prop.put(PROP_LAST_ASF, f.toString());
                saveProp();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't load " + f.getName() + ": " + ex.getMessage(),
                        "loading error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads a motion file for a skeleton.
     */
    protected void loadAMCAction() {
        File f = promptForFile(PROP_DIR, "amc");
        if (f != null) {
            // String result = JOptionPane.showInputDialog(null,
            // "Please enter layer number (0-2):", 0);
            // if (result != null) {
            try {
                // int layer = Integer.parseInt(result);

                List<Figure> figures = _jmocap.getFigureManager().getFigures();
                Figure selectedFigure = null;
                if (figures.size() > 0) {
                    if (figures.size() == 1) {
                        selectedFigure = figures.get(0);
                    } else {
                        String[] names = new String[figures.size()];
                        for (int i = 0; i < names.length; i++) {
                            names[i] = figures.get(i).getName();
                        }
                        int selection =
                                JOptionPane.
                                showOptionDialog(null,
                                "Select the figure for this motion data",
                                "Figure", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null, names, names[0]);
                        if (selection != JOptionPane.CLOSED_OPTION) {
                            selectedFigure = figures.get(selection);
                        }
                    }
                    if (selectedFigure != null) {
                        _jmocap.loadAMC(f, selectedFigure);
                        _prop.put(PROP_LAST_AMC, f.toString());
                        saveProp();
                        _view.updateAnimInfo(f.getName());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please load an ASF file first.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Couldn't load " + f.getName() + ": " + ex.getMessage(),
                        "loading error",
                        JOptionPane.ERROR_MESSAGE);
                // }
            }
        }
    }

    protected void loadBVHAction() {
        File f = promptForFile(PROP_DIR, "bvh");
        if (f != null) {
            String scale = JOptionPane.showInputDialog(
                    null,
                    "Please enter target height (or -1)",
                    5);
            if (scale != null) {
                try {
                    float sc = Float.parseFloat(scale);
                    _jmocap.loadBVH(f, sc, _view.getCursorPos());

                    // GUI related stuff:
                    _view.updateSkeletonInfo(f.getName());
                    _view.updateAnimInfo(f.getName());
                    _prop.put(PROP_LAST_BVH, f.toString());
                    saveProp();
                    _view.setFps((int) _jmocap.getFigure().getPlayer().getPlaybackFps());
                } catch (IOException ex) {
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
    public void actionPerformed(ActionEvent e) {
        String c = e.getActionCommand();
        System.out.println("command=" + c);
        if (c.equals(JMocapGUI.LOAD_ASF)) {
            loadASFAction();
        } else if (c.equals(JMocapGUI.LOAD_AMC)) {
            loadAMCAction();
        } else if (c.equals(JMocapGUI.MENU_RESET_CAM)) {
            _jmocap.setCameraView(CAMERA, CAMERA_TARGET);
        } else if (c.equals(JMocapGUI.LOAD_BVH)) {
            loadBVHAction();
        }
    }
}
