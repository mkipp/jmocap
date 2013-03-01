/**
 * JMOCAP
 *
 * Developed by Michael Kipp, 2008-2011, DFKI Saarbrücken, Germany Extended by
 * Quan Nguyen, DFKI
 *
 * Contact: mich.kipp@gmail.com
 *
 * This software has been released under the GNU LESSER GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 */
package de.jmocap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.J3DGraphics2D;
import javax.media.j3d.Material;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import de.jmocap.anim.AnimData;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import de.jmocap.figure.FigureManager;
import de.jmocap.figure.MotionTrailPoint;
import de.jmocap.gui.CameraChangeListener;
import de.jmocap.reader.AMCReader;
import de.jmocap.reader.ASFReader;
import de.jmocap.reader.BVHReader;
import de.jmocap.scene.CoordCross;
import de.jmocap.scene.Floor;
import java.util.List;

/**
 * Provides a Java3D world for viewing mocap files (ASF/AMC and BVH).
 *
 * FLICKER PROBLEM: One solution appears to be switching off "Use unified
 * back/depth buffer" for NVIDIA cards
 *
 * @author Michael Kipp
 */
public class JMocap
        implements
        MouseMotionListener,
        MouseListener,
        MouseWheelListener {

    private static final String VERSION = "1.0";
    private static final boolean HAS_ORBIT_CONTROL = false;
    private static final int VIEW_ACTIVATION_RADIUS = 250;
    private static final double BACK_CLIP_DISTANCE = 500;
    private static final float LIGHT_REACH = 500;
    private Figure _figure;
    private FigureManager _figureManager = new FigureManager();
    private static final int W = 1100;
    private static final int H = 800;
    protected BranchGroup _root;
    protected SimpleUniverse _su;
    protected JMocapCanvas3D _canvas;
    private Switch _coordCrossSwitch;
    private Switch _floorSwitch;
    private boolean _dirty = false;
    private BranchGroup _bgMotionTrails;
    private boolean _bMouseButtonPressed;
    private int _nBeginMouseCoord_Y;
    private int _nLastMouseCoord_Y;
    private int _nLastMouseCoord_X;
    private int _nBeginMouseCoord_X;
    private Point3d _p3dCameraPosition = new Point3d(1, 3, 15);
    public static final int _nCAMERA_YAW_MAX = 180;
    public static final int _nCAMERA_YAW_MIN = -180;
    private int _nCameraYaw = 0;
    private int _nCameraPitch = 0;
    public static final int _nCAMERA_PITCH_MAX = 90;
    public static final int _nCAMERA_PITCH_MIN = -90;
    private int _nCameraRoll;
    private Point3d _p3dCameraTarget;
    private boolean _bLookAtPosition = false;
    private Point3d _p3dCameraPositionInit;
    private float _fGainer_Y = 0.08f;
    private float _fGainer_X = 0.08f;
    private float _fGainer_Z = 0.2f;
    private double _dScale = 1.0;
    private int nReverseX = 1;
    private int nReverseY = 1;
    private boolean _bClearTrails;
    private List<MotionTrailPoint> _motionTrailPoints = null;
    private CameraChangeListener _cameraChangeListener = null;
    private boolean _bShowMotionTrailVelocity = false;

    public JMocap() {
        super();
        _root = createScenegraph();
        _root.compile();
        _canvas = new JMocapCanvas3D(SimpleUniverse.getPreferredConfiguration()) {
            @Override
            public void postSwap() {
                super.postSwap();
                synchronized (this) {
                    _dirty = false;
                }
            }
        };
        // _canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        // _canvas = new
        // Canvas3D(GraphicsEnvironment.getLocalGraphicsEnvironment().
        // getDefaultScreenDevice().getDefaultConfiguration());
        _canvas.setPreferredSize(new Dimension(W, H));
        _canvas.setSize(new Dimension(W, H));
        _su = new SimpleUniverse(_canvas);
        //		_su.getViewingPlatform().getViewPlatform().setCapability(ViewPlatform.ALLOW_BOUNDS_WRITE);

        _su.getViewer().getView().setBackClipDistance(BACK_CLIP_DISTANCE);
        _su.getViewingPlatform().getViewPlatform().setActivationRadius(
                VIEW_ACTIVATION_RADIUS);
        if (HAS_ORBIT_CONTROL) {
            OrbitBehavior orbit = new OrbitBehavior(_canvas,
                    OrbitBehavior.REVERSE_ALL);
            orbit.setSchedulingBounds(new BoundingSphere());
            _su.getViewingPlatform().setViewPlatformBehavior(orbit);
        } else {
            //			_su.getViewingPlatform().getViewPlatform().setBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0));
        }
        //		_su.getViewer().getView().addCanvas3D();
        _su.addBranchGraph(_root);

//        printInfo();
        _canvas.addMouseListener(this);
        _canvas.addMouseMotionListener(this);
        _canvas.addMouseWheelListener(this);
    }

    /**
     * Starts playback of all animations.
     *
     * @return false if no figures are available
     */
    public boolean play() {
        return getFigureManager().playAll();
    }

    /**
     * Pauses all playback.
     */
    public void pause() {
        getFigureManager().pauseAll();
    }

    public void initCanvasComponents() {
        _canvas.initComponents();
    }

    public synchronized void setDirty() {
        _dirty = true;
    }

    public synchronized boolean isDirty() {
        return _dirty;
    }

    /**
     * @return String with infos on current Java and J3D versions, used pipeline
     * and renderer.
     */
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        Map props = SimpleUniverse.getProperties();
        sb.append("JMocap version:    " + VERSION);
        sb.append("\nJava version:      "
                + System.getProperty("java.version"));
        sb.append("\nJ3D Version:       " + props.get("j3d.version"));
        sb.append("\nJ3D Specification: "
                + props.get("j3d.specification.version"));
        sb.append("\nPipeline:          " + props.get("j3d.pipeline"));
        sb.append("\nRenderer:          " + props.get("j3d.renderer"));
        return sb.toString();
    }

    private void createFloor(BranchGroup bg) {
        _floorSwitch = new Switch();
        _floorSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        _floorSwitch.addChild(new Floor().getBG()); // add the floor
        bg.addChild(_floorSwitch);
        _floorSwitch.setWhichChild(0);
    }

    public FigureManager getFigureManager() {
        return _figureManager;
    }

    /**
     * Removes all figures.
     */
    public void clearAll() {
        for (Figure f : _figureManager.getFigures()) {
            _root.removeChild(f.getBG());
        }
        _figureManager.getFigures().clear();
        if (_bgMotionTrails != null) {
            _bgMotionTrails.removeAllChildren();
            _bgMotionTrails = null;
        }

    }

    public void dispose() {
        _canvas.dispose();
    }

    /**
     * @return 3D viewing component.
     */
    public Canvas3D getViewComponent() {
        return _canvas;
    }

    public void setCameraView(Point3d pos, Point3d target) {
        _p3dCameraPosition = pos;
        _p3dCameraPositionInit = new Point3d(pos);
        _p3dCameraTarget = target;
        Transform3D t3d = new Transform3D();
        t3d.setTranslation(new Vector3d(_p3dCameraPosition));
        // _su.getViewingPlatform().getViewPlatformTransform().getTransform(t3d);
        // t3d.lookAt(pos, target, new Vector3d(0, 1, 0));
        // t3d.invert();
        _su.getViewingPlatform().getViewPlatformTransform().setTransform(t3d);
        t3d = null;
    }

    public void resetCamera() {
        _p3dCameraPosition = new Point3d(_p3dCameraPositionInit);
        moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0, false);

    }

    public SimpleUniverse getUniverse() {
        return _su;
    }

    public BranchGroup getRootBG() {
        return _root;
    }

    /**
     * @return Most recently loaded figure.
     */
    public Figure getFigure() {
        return _figure;
    }

    public void initFigure(Bone skel, String name) {
        initFigure(skel, name, new Point3d());
    }

    public void initFigure(Bone skel, String name, Point3d offset) {
        _figure = _figureManager.addFigure(name, skel, offset);
        _root.addChild(_figure.getBG());
    }

    /**
     * Attaches animation to current figure.
     */
    public void initAnim(AnimData data, String name, Figure figure) {
        figure.setAnimation(data);
        figure.getPlayer().reset();
    }

    /**
     * Loads motion file in AMC format.
     *
     * @throws IOException
     */
    public void loadAMC(File file) throws IOException {
        loadAMC(file, _figure);
    }

    /**
     * Loads motion file in AMC format for the given figure.
     *
     * @throws IOException
     */
    public void loadAMC(File file, Figure figure) throws IOException {
        AMCReader r = new AMCReader();
        AnimData d = r.readAMC(file, figure.getSkeleton());
        initAnim(d, file.getName(), figure);
    }

    public void loadBVH(File f, float targetHeight, Point3d offset)
            throws IOException {
        _figureManager.pauseAll();
        BVHReader rd = new BVHReader(targetHeight);
        BVHReader.BVHResult bvh = rd.readFile(f);
        initFigure(bvh.skeleton, f.getName(), offset);
        initAnim(bvh.animation, f.getName(), _figure);

        _dScale = rd.getScale();
    }

    public void loadASF(File file, Point3d offset) throws IOException
    {
        ASFReader rd = new ASFReader();
        Bone skel = rd.getSkeleton(file);
        initFigure(skel, file.getName(), offset);
    }

    protected Sphere createSphere(Color c, float radius) {
        Sphere s = new Sphere(radius);
        Color3f c3 = new Color3f(c);
        Appearance app = new Appearance();
        app.setMaterial(new Material(c3, c3, new Color3f(Color.WHITE), c3,
                .3f));
        s.setAppearance(app);
        return s;
    }

    protected Switch createCoordCross(BranchGroup r, float diameter, float thick) {
        Switch s = new Switch();
        s.setCapability(Switch.ALLOW_SWITCH_WRITE);
        CoordCross cc = new CoordCross(1.2f);
        s.addChild(cc.getRoot());
        s.setWhichChild(Switch.CHILD_ALL);
        return s;
    }

    /**
     * One ambient light. 2 directional lights: 1) from up-right-front 2) from
     * up-left-back
     */
    protected void lightScene(BranchGroup bg) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(), LIGHT_REACH);
        Color3f white = new Color3f(Color.YELLOW);
        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        bg.addChild(ambientLightNode);

        // Directional lights:
        // comes from up-right-front
        Vector3f light1Direction = new Vector3f(-5f, -5f, -5f); // left, down,
        // backwards
        // comes from up-left-back
        Vector3f light2Direction = new Vector3f(5f, -5f, 5f); // right, down,
        // forwards

        DirectionalLight light1 = new DirectionalLight(white, light1Direction);
        light1.setInfluencingBounds(bounds);
        bg.addChild(light1);

        DirectionalLight light2 = new DirectionalLight(white, light2Direction);
        light2.setInfluencingBounds(bounds);
        bg.addChild(light2);
    }

    public void showCoordCross(boolean val) {
        _coordCrossSwitch.setWhichChild(val
                ? Switch.CHILD_ALL
                : Switch.CHILD_NONE);
    }

    public void showFloor(boolean val) {
        _floorSwitch.setWhichChild(val ? Switch.CHILD_ALL : Switch.CHILD_NONE);
    }

    public BranchGroup createScenegraph() {
        BranchGroup r = new BranchGroup();
        r.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        lightScene(r);
        r.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _coordCrossSwitch = createCoordCross(r, 15f, .01f);
        r.addChild(_coordCrossSwitch);
        r.setBounds(new BoundingSphere(new Point3d(0, 0, 0), 10d));
        createFloor(r);
        return r;
    }

    /**
     * Shows the path of the special joint.
     *
     * @param startFrame
     * @param endFrame
     * @param color
     * @param jointName
     */
    public void addMotionTrail(List<MotionTrailPoint> motionTrailPoints) {
        _bClearTrails = true;
        _motionTrailPoints = motionTrailPoints;
        _canvas.addPositionsToMotionTrail();
        _canvas.repaint();
    }

    /**
     * Add the path of the special joint to the scene
     *
     * @param startFrame
     * @param endFrame
     * @param color
     * @param jointName
     */
    private void addPositionsToMotionTrailPoints(
            List<MotionTrailPoint> motionTrailPoints) {

        if (_figure.getPlayer() != null) {
            int nCurrentFrame = _figure.getPlayer().getCurrentFrame();
            for (MotionTrailPoint motionTrailPoint : motionTrailPoints) {
                Bone bone = _figure.getSkeleton().findBone(
                        motionTrailPoint.getBone());

                if (bone != null) {
                    _figure.getPlayer().gotoTime(
                            motionTrailPoint.getTimePointInSeconds());
                    motionTrailPoint.setScale(_dScale);
                    Point3d p3dPositionInWorld = new Point3d();
                    bone.getWorldPosition(p3dPositionInWorld);

                    // NEW: position is relative to root joint
//                    bone.getRelativePosition(_figure.getSkeleton(), p3dPositionInWorld);

                    motionTrailPoint.setPosition(p3dPositionInWorld);
                    p3dPositionInWorld = null;

                } else {
                    System.err.println("Bone " + motionTrailPoint.getBone()
                            + " not found");

                }
                bone = null;
            }
            // go to old position
            _figure.getPlayer().gotoTime(
                    nCurrentFrame / _figure.getPlayer().getPlaybackFps());
        } else {
            System.err.println("No player Mocap-Player found");

        }

    }

    /**
     * Add the given MotionTrailPoints to scene
     *
     * @param motionTrailPoints Vector of MotionTrailPoints
     */
    private void addMotionTrailsToScene(
            List<MotionTrailPoint> motionTrailPoints) {
        //		Point3d p = new Point3d();
        //		_figure.getSkeleton().getWorldPosition(p);
        //		System.out.println("Figure position in World: " + p);

        if (_bgMotionTrails == null) {
            _bgMotionTrails = new BranchGroup();
            _bgMotionTrails.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            _bgMotionTrails.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
            _bgMotionTrails.setCapability(BranchGroup.ALLOW_DETACH);
            _root.addChild(_bgMotionTrails);
        } else if (_bClearTrails) {
            removeMotionTrails();
        }

        for (int i = 0; i + 1 < motionTrailPoints.size() - 1; i++) {
            motionTrailPoints.get(i).showMotionTrailVelocity(
                    _bShowMotionTrailVelocity);
            _bgMotionTrails.addChild(motionTrailPoints.get(i).getObject());

            Vector3d v3dNextPositionInWorld = new Vector3d();
            motionTrailPoints.get(i + 1).getPosition(v3dNextPositionInWorld);
            motionTrailPoints.get(i).angleVelocityVisualisation(
                    new Point3d(v3dNextPositionInWorld));
            if (i + 1 == motionTrailPoints.size() - 1) {
                _bgMotionTrails.addChild(motionTrailPoints.get(i + 1).getObject());
            }

        }

        // write the positions into a file
        //		PathsFileWriter checkpointPoseReader = new PathsFileWriter();
        //		checkpointPoseReader.writeDocument("checkpoints", motionTrailPoints);
        //		System.out.println("Figure Scale: " + _figure.getScale());

    }

    public void showMotionTrailVelocity(boolean showMotionTrailVelocity) {
        if (showMotionTrailVelocity != _bShowMotionTrailVelocity
                && _motionTrailPoints != null) {
            for (MotionTrailPoint mtPoint : _motionTrailPoints) {
                mtPoint.showMotionTrailVelocity(showMotionTrailVelocity);
            }
        }
        _bShowMotionTrailVelocity = showMotionTrailVelocity;
    }

    public void removeMotionTrails() {
        if (_bgMotionTrails != null) {
            _bgMotionTrails.removeAllChildren();
        }
    }

    public void initCameraToSkeleton(Point3d cameraTarget) {

//		_p3dCameraTarget = cameraTarget;
        double dSkeletonSize = 1.5d * _figure.getSkeleton().getMaxDistance();
//		System.out.println("Max Distance: " + dSkeletonSize);

        // get the field of view

        double dFieldOfView = _su.getViewer().getView().getFieldOfView();
        double dCameraZPos = Math.atan(dFieldOfView / 2d) / dSkeletonSize;
//		System.out.println("Max Distance:dCameraZPos: " + dCameraZPos);
        //		double dFieldOfViewInRadians = Math
        //				.atan((dSkeletonSize / (2 * _cameraConfiguration.getFocal())))
        //				* factor;
        //		// System.out.println("\n\n\n\nCameraController::" + this +
        //		// "getObjectInFieldOfView::_dFieldOfViewInRadians::" +
        //		// dFieldOfViewInRadians + "\n\n\n\n");
        //		_cameraConfiguration.setFieldOfView(dFieldOfViewInRadians);
        //		return dFieldOfViewInRadians;
        // update();
        _p3dCameraPosition.z = dCameraZPos;
        _p3dCameraPosition.x = cameraTarget.x;
        _p3dCameraPosition.y = cameraTarget.y;
        moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0, false);
        onCameraChanged("z_coord", _p3dCameraPosition.z);
        onCameraChanged("x_coord", _p3dCameraPosition.x);
        onCameraChanged("y_coord", _p3dCameraPosition.y);
//		System.out.println("Max Distance:_p3dCameraPosition: " + _p3dCameraPosition);
//		System.out.println("Max Distance:_p3dCameraTarget: " + _p3dCameraTarget);
//		System.out.println("Max Distance:getCameraYaw(): " + getCameraYaw());
//		System.out.println("Max Distance:getCameraPitch: " + getCameraPitch());


    }

    private Transform3D moveCamera(double x, double y, double z, double yaw,
            double pitch, double roll, boolean holdLookAt) {
        Transform3D ret = new Transform3D();// return transform
        Transform3D xrot = new Transform3D();
        Transform3D yrot = new Transform3D();
        Transform3D zrot = new Transform3D();
        Transform3D pos = new Transform3D();

        // roll pitch yaw rotations by Degrees
        yrot.rotY(yaw / 360 * (2 * Math.PI));
        xrot.rotX(pitch / 360 * (2 * Math.PI));
        zrot.rotZ(roll / 360 * (2 * Math.PI));
        // multiply return values by rotation
        // (order of multiplication is important dependent on coordinate system
        // being used)
        ret.mul(zrot);
        ret.mul(yrot);
        ret.mul(xrot);

        // multiply return values by translation
        pos.setTranslation(new Vector3d(x, y, z));
        ret.mul(pos);

        if (holdLookAt) {
            Vector3d v3dPos = new Vector3d();
            ret.get(v3dPos);
            Transform3D t3d = new Transform3D();
            t3d.lookAt(new Point3d(v3dPos), _p3dCameraTarget, new Vector3d(0,
                    1, 0));
            t3d.invert();

            _su.getViewingPlatform().getViewPlatformTransform().setTransform(
                    t3d);
        } else {
            _su.getViewingPlatform().getViewPlatformTransform().setTransform(
                    ret);
        }
        return ret;
    }

    /**
     * Zooming by changing the field of view
     *
     * @param fieldOfView
     */
    private void zoomCamera(double fieldOfView) {
        double dFieldOfView = _su.getViewingPlatform().getViewers()[0].getView().getFieldOfView();

        _su.getViewingPlatform().getViewers()[0].getView().setFieldOfView(
                dFieldOfView + fieldOfView * 0.05);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // camera rotating
        if (e.getButton() == MouseEvent.BUTTON1) {

            if (!_bMouseButtonPressed) {

                _nCameraYaw = _nCameraYaw + e.getX() - _nLastMouseCoord_X;

            } else {
                _nCameraYaw = _nCameraYaw + e.getX() - _nBeginMouseCoord_X;

            }
            if (_nCameraYaw >= _nCAMERA_YAW_MAX) {
                _nCameraYaw = _nCAMERA_YAW_MAX;
            } else if (_nCameraYaw <= _nCAMERA_YAW_MIN) {
                _nCameraYaw = _nCAMERA_YAW_MIN;
            }
            // negate _fYaw to get an intuitive behavior. Move mouse to right,
            moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                    _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0,
                    _bLookAtPosition);
            onCameraChanged("yaw", _nCameraYaw);
            _bMouseButtonPressed = false;

        } else if (e.getButton() == MouseEvent.BUTTON3) // camera moving
        {
            if (!_bMouseButtonPressed) {

                _p3dCameraPosition.x = _p3dCameraPosition.x
                        + (nReverseX * ((e.getX() - _nLastMouseCoord_X))
                        * _fGainer_X * _dScale);
                _p3dCameraPosition.y = _p3dCameraPosition.y
                        + (nReverseY * ((e.getY() - _nLastMouseCoord_Y))
                        * _fGainer_Y * _dScale);

            } else {
                _p3dCameraPosition.x = _p3dCameraPosition.x
                        + (nReverseX * ((e.getX() - _nBeginMouseCoord_X))
                        * _fGainer_X * _dScale);
                _p3dCameraPosition.y = _p3dCameraPosition.y
                        + (nReverseY * ((e.getY() - _nBeginMouseCoord_Y))
                        * _fGainer_Y * _dScale);
            }

            moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                    _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0, false);
            onCameraChanged("x_coord", _p3dCameraPosition.x);
            onCameraChanged("y_coord", _p3dCameraPosition.y);
            _bMouseButtonPressed = false;

        } else if (e.getButton() == MouseEvent.BUTTON2) // camera moving
        {
            if (!_bMouseButtonPressed) {

                _nCameraPitch = _nCameraPitch + e.getY() - _nLastMouseCoord_Y;

            } else {

                _nCameraPitch = _nCameraPitch + e.getY() - _nBeginMouseCoord_Y;
            }
            if (_nCameraPitch >= _nCAMERA_PITCH_MAX) {
                _nCameraPitch = _nCAMERA_PITCH_MAX;
            } else if (_nCameraPitch <= _nCAMERA_PITCH_MIN) {
                _nCameraPitch = _nCAMERA_PITCH_MIN;
            }
            // negate _fYaw to get an intuitive behavior. Move mouse to
            // right,
            moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                    _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0,
                    _bLookAtPosition);
            onCameraChanged("pitch", _nCameraPitch);
            _bMouseButtonPressed = false;

        }
        _nLastMouseCoord_X = e.getX();
        _nLastMouseCoord_Y = e.getY();
    }

    private void onCameraChanged(String command, double value) {
        if (_cameraChangeListener != null) {
            _cameraChangeListener.onCameraChanged(command, value);// TODO Auto-generated method stub
        }
    }

    public Point3d getCameraPosition() {
        return _p3dCameraPosition;
    }

    public int getCameraPitch() {
        return _nCameraPitch;
    }

    public int getCameraYaw() {
        return _nCameraYaw;
    }

    public void reverseXAxis(boolean reverse) {
        if (reverse) {
            nReverseX = -1;
        } else {
            nReverseX = 1;
        }
    }

    public void reverseYAxis(boolean reverse) {
        if (reverse) {
            nReverseY = -1;
        } else {
            nReverseY = 1;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        _nBeginMouseCoord_X = e.getX();
        _nLastMouseCoord_X = e.getX();
        _nBeginMouseCoord_Y = e.getY();
        _nLastMouseCoord_Y = e.getY();
        _bMouseButtonPressed = true;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        _p3dCameraPosition.z = _p3dCameraPosition.z + notches * _fGainer_Z;
        moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch, 0, false);
        onCameraChanged("z_coord", _p3dCameraPosition.z);
        // zoomCamera(notches);

    }

    public void setCameraChangeListener(
            CameraChangeListener cameraChangeListener) {
        _cameraChangeListener = cameraChangeListener;

    }

    public void setCamera(double x, double y, double z, int yaw, int pitch,
            int roll, boolean lookAtPos) {
        _p3dCameraPosition.x = x;
        _p3dCameraPosition.y = y;
        _p3dCameraPosition.z = z;
        _nCameraYaw = yaw;
        _nCameraPitch = pitch;
        _nCameraRoll = roll;
        _bLookAtPosition = lookAtPos;
        //		System.out.println("Set Camera values (" + x + ", " + y + ", " + z
        //				+ ", " + yaw + ", " + pitch + ")");
        moveCamera(_p3dCameraPosition.x, _p3dCameraPosition.y,
                _p3dCameraPosition.z, -_nCameraYaw, _nCameraPitch,
                _nCameraRoll, _bLookAtPosition);

    }

    class JMocapCanvas3D extends Canvas3D {

        private boolean bAddMotionTrail;
//        private double pathStartTime;
//        private double pathEndTime;
//        private float fps;
//        private Color pathColor;
//        private String jointName1;
//        private String jointName2;
        private J3DGraphics2D graphics2D;

        public JMocapCanvas3D(GraphicsConfiguration gconfig,
                boolean offscreenflag) {
            super(gconfig, offscreenflag);
            initComponents();
        }

        public JMocapCanvas3D(GraphicsConfiguration preferredConfiguration) {
            super(preferredConfiguration);
            initComponents();

        }

        public void initComponents() {
            graphics2D = this.getGraphics2D();
            graphics2D.setColor(Color.LIGHT_GRAY);
            graphics2D.setFont(new Font("Serif", Font.BOLD, 15));
        }

        @Override
        public void preRender() {
            super.preRender();
            if (bAddMotionTrail) {
                addPositionsToMotionTrailPoints(_motionTrailPoints);
                addMotionTrailsToScene(_motionTrailPoints);
                bAddMotionTrail = false;
            }
        }

        @Override
        public void postRender() {
            super.postRender();

            showFrameInformation();

            graphics2D.flush(true);
        }

        public void dispose() {
            graphics2D.dispose();
        }

//        public void showPath(double startTime, double endTime, Color color,
//                String name1, String name2, float fps)
//        {
//            bAddMotionTrail = true;
//            pathStartTime = startTime;
//            pathEndTime = endTime;
//            pathColor = color;
//            jointName1 = name1;
//            jointName2 = name2;
//            this.fps = fps;
//
//        }
        public void addPositionsToMotionTrail() {
            bAddMotionTrail = true;
        }

        public void showFrameInformation() {
            if (_figure != null) {
                graphics2D.drawString("frame: "
                        + _figure.getPlayer().getCurrentFrame(),
                        getSize().width / 2, getSize().height - 20);
            }

        }
    }
}
