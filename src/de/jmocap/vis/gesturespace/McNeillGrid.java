package de.jmocap.vis.gesturespace;

import java.awt.Color;
import java.awt.Font;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Text2D;

/**
 * Places Gesture Space grid in front of the skeleton, highlighting
 * the sector that a hand passes through.
 * 
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */

public class McNeillGrid implements McNeillGridInterface {

    private BranchGroup gridBranchGroup = new BranchGroup();
    private Appearance appearanceCenterCenter;
    private Appearance appearanceCenter;
    private Appearance appearanceUp;
    private Appearance appearanceDown;
    private Appearance appearanceLeft;
    private Appearance appearanceRight;
    private Appearance appearanceUpLeft;
    private Appearance appearanceDownLeft;
    private Appearance appearanceUpRight;
    private Appearance appearanceDownRight;
    private Box centerCenterBox;
    private Box leftBox;
    private Box rightBox;
    public float centerCenterWidth;
    public float centerCenterHeight;
    public float centerWidth;
    public float upWidth;
    public float upHeight;
    public float downWidth;
    public float downHeight;
    public float leftWidth;
    public float leftHeight;
    public float rightWidth;
    public float rightHeight;
    private float xScale = 1.0f;
    private float yScale = 1.0f;

    @Override
    public BranchGroup getGrid() {
        return gridBranchGroup;
    }

    @Override
    public Vector3f getPosition() {
        Transform3D trans = new Transform3D();
        Vector3f position = new Vector3f();

        centerCenterBox.getLocalToVworld(trans);
        trans.get(position);

        return position;
    }

    @Override
    public Vector3f getVectorFromRightToLeftofTheGrid() {

        //Position of the left box
        Transform3D transLeftBox = new Transform3D();
        Vector3f positionLeftBox = new Vector3f();

        leftBox.getLocalToVworld(transLeftBox);
        transLeftBox.get(positionLeftBox);

        //Position of the right box
        Transform3D transRightBox = new Transform3D();
        Vector3f positionRightBox = new Vector3f();

        rightBox.getLocalToVworld(transRightBox);
        transRightBox.get(positionRightBox);

        Vector3f vectorFromRightToLeft = new Vector3f(positionLeftBox.x - positionRightBox.x,
                0.0f,
                positionLeftBox.z - positionRightBox.z);

        return vectorFromRightToLeft;
    }

    @Override
    public void increaseXScale() {
        xScale += 0.1f;
    }

    @Override
    public void decreaseXScale() {
        xScale -= 0.1f;
    }

    @Override
    public void increaseYScale() {
        yScale += 0.1f;
    }

    @Override
    public void decreaseYScale() {
        yScale -= 0.1f;
    }

    private Appearance getSimpleAppearance(Color3f color, int transparencyMode, float transparency) {

        Appearance app = new Appearance();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
        ca.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
        ca.setColor(color);
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setCapabilityIsFrequent(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapabilityIsFrequent(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setTransparencyMode(transparencyMode);
        ta.setTransparency(transparency);
        app.setColoringAttributes(ca);
        app.setTransparencyAttributes(ta);


        //###
        app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        //###

        return app;
    }

    @Override
    public void createGrid(float shoulderWidth) {
        //Beim erstellen der Box dims mit 0.5 mul 
        //fuer coordcomp. werte lassen!
        //ap von java3d suckzzzzz!!!einseinself
        gridBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
        gridBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        gridBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        BranchGroup centerCenterBranchGroup = new BranchGroup();
        BranchGroup centerBranchGroup = new BranchGroup();
        BranchGroup upBranchGroup = new BranchGroup();
        BranchGroup downBranchGroup = new BranchGroup();
        BranchGroup leftBranchGroup = new BranchGroup();
        BranchGroup rightBranchGroup = new BranchGroup();
        BranchGroup upLeftBranchGroup = new BranchGroup();
        BranchGroup downLeftBranchGroup = new BranchGroup();
        BranchGroup upRightBranchGroup = new BranchGroup();
        BranchGroup downRightBranchGroup = new BranchGroup();

        Appearance lineAppearance = new Appearance();
        ColoringAttributes lineColorAttributes = new ColoringAttributes(new Color3f(Color.orange), ColoringAttributes.SHADE_FLAT);
        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(1.0f);
        lineAppearance.setColoringAttributes(lineColorAttributes);
        lineAppearance.setLineAttributes(lineAttributes);

        ////CenterCenterBox
        appearanceCenterCenter = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        centerCenterHeight = shoulderWidth * 0.6f * yScale;
        centerCenterWidth = shoulderWidth * 0.6f * xScale;
        centerCenterBox = new Box(centerCenterWidth * 0.5f, centerCenterHeight * 0.5f, 0.05f, appearanceCenterCenter);

        //###
        centerCenterBox.setCapability(Box.ALLOW_LOCAL_TO_VWORLD_READ);
        //###

        Transform3D transform3DCenterCenter = new Transform3D();
        transform3DCenterCenter.setTranslation(new Vector3d(0.0f, 0.0f, 0.0f));
        TransformGroup transformGroupCenterCenter = new TransformGroup(transform3DCenterCenter);
        transformGroupCenterCenter.addChild(centerCenterBox);

        LineArray centerCenterLine = new LineArray(8, LineArray.COORDINATES);
        centerCenterLine.setCoordinate(0, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0f));
        centerCenterLine.setCoordinate(1, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0f));

        centerCenterLine.setCoordinate(2, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0f));
        centerCenterLine.setCoordinate(3, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0f));

        centerCenterLine.setCoordinate(4, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0f));
        centerCenterLine.setCoordinate(5, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0f));

        centerCenterLine.setCoordinate(6, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0f));
        centerCenterLine.setCoordinate(7, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0f));

        Shape3D centerCenterLines = new Shape3D(centerCenterLine, lineAppearance);

        centerCenterBranchGroup.addChild(transformGroupCenterCenter);
        centerCenterBranchGroup.addChild(centerCenterLines);
        ////CenterBox
        appearanceCenter = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        centerWidth = shoulderWidth * 0.2f;
        //CenterBox1
        Box centerBox1 = new Box((centerCenterWidth + 2 * centerWidth) * 0.5f, centerWidth * 0.5f, 0.05f, appearanceCenter);
        Transform3D transform3DCenter1 = new Transform3D();
        transform3DCenter1.setTranslation(new Vector3d(0.0f, shoulderWidth * 0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupCenter1 = new TransformGroup(transform3DCenter1);
        transformGroupCenter1.addChild(centerBox1);

        LineArray centerLine1 = new LineArray(2, LineArray.COORDINATES);
        centerLine1.setCoordinate(0, new Point3f(shoulderWidth * 0.5f, shoulderWidth * 0.2f * 0.5f, 0));
        centerLine1.setCoordinate(1, new Point3f(-shoulderWidth * 0.5f, shoulderWidth * 0.2f * 0.5f, 0));
        Shape3D centerLines1 = new Shape3D(centerLine1, lineAppearance);

        transformGroupCenter1.addChild(centerLines1);

        //CenterBox2
        Box centerBox2 = new Box((centerCenterWidth + 2 * centerWidth) * 0.5f, centerWidth * 0.5f, 0.05f, appearanceCenter);
        Transform3D transform3DCenter2 = new Transform3D();
        transform3DCenter2.setTranslation(new Vector3d(0.0f, shoulderWidth * -0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupCenter2 = new TransformGroup(transform3DCenter2);
        transformGroupCenter2.addChild(centerBox2);

        LineArray centerLine2 = new LineArray(2, LineArray.COORDINATES);
        centerLine2.setCoordinate(0, new Point3f(shoulderWidth * 0.5f, shoulderWidth * -0.2f * 0.5f, 0));
        centerLine2.setCoordinate(1, new Point3f(-shoulderWidth * 0.5f, shoulderWidth * -0.2f * 0.5f, 0));
        Shape3D centerLines2 = new Shape3D(centerLine2, lineAppearance);

        transformGroupCenter2.addChild(centerLines2);

        //CenterBox3
        Box centerBox3 = new Box(centerWidth * 0.5f, centerCenterHeight * 0.5f, 0.05f, appearanceCenter);
        Transform3D transform3DCenter3 = new Transform3D();
        transform3DCenter3.setTranslation(new Vector3d(shoulderWidth * 0.8f * 0.5f, 0.0f, 0.0f));
        TransformGroup transformGroupCenter3 = new TransformGroup(transform3DCenter3);
        transformGroupCenter3.addChild(centerBox3);

        LineArray centerLine3 = new LineArray(2, LineArray.COORDINATES);
        centerLine3.setCoordinate(0, new Point3f(shoulderWidth * 0.2f * 0.5f, shoulderWidth * 0.5f, 0));
        centerLine3.setCoordinate(1, new Point3f(shoulderWidth * 0.2f * 0.5f, -shoulderWidth * 0.5f, 0));
        Shape3D centerLines3 = new Shape3D(centerLine3, lineAppearance);

        transformGroupCenter3.addChild(centerLines3);

        //CenterBox4
        Box centerBox4 = new Box(centerWidth * 0.5f, centerCenterHeight * 0.5f, 0.05f, appearanceCenter);
        Transform3D transform3DCenter4 = new Transform3D();
        transform3DCenter4.setTranslation(new Vector3d(shoulderWidth * -0.8f * 0.5f, 0.0f, 0.0f));
        TransformGroup transformGroupCenter4 = new TransformGroup(transform3DCenter4);
        transformGroupCenter4.addChild(centerBox4);

        LineArray centerLine4 = new LineArray(2, LineArray.COORDINATES);
        centerLine4.setCoordinate(0, new Point3f(shoulderWidth * -0.2f * 0.5f, shoulderWidth * 0.5f, 0));
        centerLine4.setCoordinate(1, new Point3f(shoulderWidth * -0.2f * 0.5f, -shoulderWidth * 0.5f, 0));
        Shape3D centerLines4 = new Shape3D(centerLine4, lineAppearance);

        transformGroupCenter4.addChild(centerLines4);


        centerBranchGroup.addChild(transformGroupCenter1);
        centerBranchGroup.addChild(transformGroupCenter2);
        centerBranchGroup.addChild(transformGroupCenter3);
        centerBranchGroup.addChild(transformGroupCenter4);

        ////UpBox
        appearanceUp = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        upWidth = shoulderWidth * 0.6f * xScale;
        upHeight = shoulderWidth * 0.4f * yScale;
        Box upBox = new Box(upWidth * 0.5f, upHeight * 0.5f, 0.05f, appearanceUp);
        Transform3D transform3DUp = new Transform3D();
        transform3DUp.setTranslation(new Vector3d(0.0f, shoulderWidth * 1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupUp = new TransformGroup(transform3DUp);
        transformGroupUp.addChild(upBox);

        upBranchGroup.addChild(transformGroupUp);

        LineArray upLine = new LineArray(4, LineArray.COORDINATES);
        upLine.setCoordinate(0, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * -0.4f * 0.5f, 0));
        upLine.setCoordinate(1, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0));
        upLine.setCoordinate(2, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * -0.4f * 0.5f, 0));
        upLine.setCoordinate(3, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0));
        Shape3D upLines = new Shape3D(upLine, lineAppearance);

        transformGroupUp.addChild(upLines);

        ////DownBox
        appearanceDown = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        downWidth = shoulderWidth * 0.6f * xScale;
        downHeight = shoulderWidth * 0.4f * yScale;
        Box downBox = new Box(downWidth * 0.5f, downHeight * 0.5f, 0.05f, appearanceDown);
        Transform3D transform3DDown = new Transform3D();
        transform3DDown.setTranslation(new Vector3d(0.0f, shoulderWidth * -1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupDown = new TransformGroup(transform3DDown);
        transformGroupDown.addChild(downBox);

        downBranchGroup.addChild(transformGroupDown);

        LineArray downLine = new LineArray(4, LineArray.COORDINATES);
        downLine.setCoordinate(0, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * -0.4f * 0.5f, 0));
        downLine.setCoordinate(1, new Point3f(shoulderWidth * -0.6f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0));
        downLine.setCoordinate(2, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * -0.4f * 0.5f, 0));
        downLine.setCoordinate(3, new Point3f(shoulderWidth * 0.6f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0));
        Shape3D downLines = new Shape3D(downLine, lineAppearance);

        transformGroupDown.addChild(downLines);

        ////LeftBox
        appearanceLeft = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        leftWidth = shoulderWidth * 0.3f * xScale;
        leftHeight = shoulderWidth * 0.6f * yScale;
        leftBox = new Box(leftWidth * 0.5f, leftHeight * 0.5f, 0.05f, appearanceLeft);
        //###
        leftBox.setCapability(Box.ALLOW_LOCAL_TO_VWORLD_READ);
        //###

        Transform3D transform3DLeft = new Transform3D();
        transform3DLeft.setTranslation(new Vector3d(shoulderWidth * 1.3f * 0.5f, 0.0f, 0.0f));
        TransformGroup transformGroupLeft = new TransformGroup(transform3DLeft);
        transformGroupLeft.addChild(leftBox);

        leftBranchGroup.addChild(transformGroupLeft);

        LineArray leftLine = new LineArray(4, LineArray.COORDINATES);
        leftLine.setCoordinate(0, new Point3f(shoulderWidth * -0.3f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0));
        leftLine.setCoordinate(1, new Point3f(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0));
        leftLine.setCoordinate(2, new Point3f(shoulderWidth * -0.3f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0));
        leftLine.setCoordinate(3, new Point3f(shoulderWidth * 0.3f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0));

        Shape3D leftLines = new Shape3D(leftLine, lineAppearance);

        transformGroupLeft.addChild(leftLines);

        ////RightBox
        appearanceRight = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        rightWidth = shoulderWidth * 0.3f * xScale;
        rightHeight = shoulderWidth * 0.6f * yScale;
        rightBox = new Box(rightWidth * 0.5f, rightHeight * 0.5f, 0.05f, appearanceRight);

        //###
        rightBox.setCapability(Box.ALLOW_LOCAL_TO_VWORLD_READ);
        //###
        Transform3D transform3DRight = new Transform3D();
        transform3DRight.setTranslation(new Vector3d(shoulderWidth * -1.3f * 0.5f, 0.0f, 0.0f));
        TransformGroup transformGroupRight = new TransformGroup(transform3DRight);
        transformGroupRight.addChild(rightBox);

        rightBranchGroup.addChild(transformGroupRight);

        LineArray rightLine = new LineArray(4, LineArray.COORDINATES);
        rightLine.setCoordinate(0, new Point3f(shoulderWidth * -0.3f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0));
        rightLine.setCoordinate(1, new Point3f(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.6f * 0.5f, 0));
        rightLine.setCoordinate(2, new Point3f(shoulderWidth * -0.3f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0));
        rightLine.setCoordinate(3, new Point3f(shoulderWidth * 0.3f * 0.5f, shoulderWidth * -0.6f * 0.5f, 0));

        Shape3D rightLines = new Shape3D(rightLine, lineAppearance);

        transformGroupRight.addChild(rightLines);

        ////UpLeftBox
        appearanceUpLeft = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        //UpLeftBox1
        Box upLeftBox1 = new Box(shoulderWidth * 0.5f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0.05f, appearanceUpLeft);
        Transform3D transform3DUpLeft1 = new Transform3D();
        transform3DUpLeft1.setTranslation(new Vector3d(shoulderWidth * 1.1f * 0.5f, shoulderWidth * 1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupUpLeft1 = new TransformGroup(transform3DUpLeft1);
        transformGroupUpLeft1.addChild(upLeftBox1);

        //UpLeftBox2
        Box upLeftBox2 = new Box(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.2f * 0.5f, 0.05f, appearanceUpLeft);
        Transform3D transform3DUpLeft2 = new Transform3D();
        transform3DUpLeft2.setTranslation(new Vector3d(shoulderWidth * 1.3f * 0.5f, shoulderWidth * 0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupUpLeft2 = new TransformGroup(transform3DUpLeft2);
        transformGroupUpLeft2.addChild(upLeftBox2);

        upLeftBranchGroup.addChild(transformGroupUpLeft1);
        upLeftBranchGroup.addChild(transformGroupUpLeft2);

        ////UpRightBox
        appearanceUpRight = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        //UpRightBox1
        Box upRightBox1 = new Box(shoulderWidth * 0.5f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0.05f, appearanceUpRight);
        Transform3D transform3DUpRight1 = new Transform3D();
        transform3DUpRight1.setTranslation(new Vector3d(shoulderWidth * -1.1f * 0.5f, shoulderWidth * 1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupUpRight1 = new TransformGroup(transform3DUpRight1);
        transformGroupUpRight1.addChild(upRightBox1);

        //UpRightBox2
        Box upRightBox2 = new Box(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.2f * 0.5f, 0.05f, appearanceUpRight);
        Transform3D transform3DUpRight2 = new Transform3D();
        transform3DUpRight2.setTranslation(new Vector3d(shoulderWidth * -1.3f * 0.5f, shoulderWidth * 0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupUpRight2 = new TransformGroup(transform3DUpRight2);
        transformGroupUpRight2.addChild(upRightBox2);

        upRightBranchGroup.addChild(transformGroupUpRight1);
        upRightBranchGroup.addChild(transformGroupUpRight2);

        ////DownLeftBox
        appearanceDownLeft = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        //UpRightBox1
        Box downLeftBox1 = new Box(shoulderWidth * 0.5f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0.05f, appearanceDownLeft);
        Transform3D transform3DDownLeft1 = new Transform3D();
        transform3DDownLeft1.setTranslation(new Vector3d(shoulderWidth * 1.1f * 0.5f, shoulderWidth * -1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupDownLeft1 = new TransformGroup(transform3DDownLeft1);
        transformGroupDownLeft1.addChild(downLeftBox1);

        //UpRightBox2
        Box downLeftBox2 = new Box(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.2f * 0.5f, 0.05f, appearanceDownLeft);
        Transform3D transform3DDownLeft2 = new Transform3D();
        transform3DDownLeft2.setTranslation(new Vector3d(shoulderWidth * 1.3f * 0.5f, shoulderWidth * -0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupDownLeft2 = new TransformGroup(transform3DDownLeft2);
        transformGroupDownLeft2.addChild(downLeftBox2);

        downLeftBranchGroup.addChild(transformGroupDownLeft1);
        downLeftBranchGroup.addChild(transformGroupDownLeft2);

        ////DownRightBox
        appearanceDownRight = getSimpleAppearance(new Color3f(Color.orange), TransparencyAttributes.NICEST, 1.0f);

        //DownRightBox1
        Box downRightBox1 = new Box(shoulderWidth * 0.5f * 0.5f, shoulderWidth * 0.4f * 0.5f, 0.05f, appearanceDownRight);
        Transform3D transform3DDownRight1 = new Transform3D();
        transform3DDownRight1.setTranslation(new Vector3d(shoulderWidth * -1.1f * 0.5f, shoulderWidth * -1.4f * 0.5f, 0.0f));
        TransformGroup transformGroupDownRight1 = new TransformGroup(transform3DDownRight1);
        transformGroupDownRight1.addChild(downRightBox1);

        //DownRightBox2
        Box downRightBox2 = new Box(shoulderWidth * 0.3f * 0.5f, shoulderWidth * 0.2f * 0.5f, 0.05f, appearanceDownRight);
        Transform3D transform3DDownRight2 = new Transform3D();
        transform3DDownRight2.setTranslation(new Vector3d(shoulderWidth * -1.3f * 0.5f, shoulderWidth * -0.8f * 0.5f, 0.0f));
        TransformGroup transformGroupDownRight2 = new TransformGroup(transform3DDownRight2);
        transformGroupDownRight2.addChild(downRightBox2);

        downRightBranchGroup.addChild(transformGroupDownRight1);
        downRightBranchGroup.addChild(transformGroupDownRight2);

        LineArray outerLine = new LineArray(8, LineArray.COORDINATES);
        outerLine.setCoordinate(0, new Point3f(shoulderWidth * -1.6f * 0.5f, shoulderWidth * 1.8f * 0.5f, 0));
        outerLine.setCoordinate(1, new Point3f(shoulderWidth * 1.6f * 0.5f, shoulderWidth * 1.8f * 0.5f, 0));

        outerLine.setCoordinate(2, new Point3f(shoulderWidth * 1.6f * 0.5f, shoulderWidth * 1.8f * 0.5f, 0));
        outerLine.setCoordinate(3, new Point3f(shoulderWidth * 1.6f * 0.5f, shoulderWidth * -1.8f * 0.5f, 0));

        outerLine.setCoordinate(4, new Point3f(shoulderWidth * 1.6f * 0.5f, shoulderWidth * -1.8f * 0.5f, 0));
        outerLine.setCoordinate(5, new Point3f(shoulderWidth * -1.6f * 0.5f, shoulderWidth * -1.8f * 0.5f, 0));

        outerLine.setCoordinate(6, new Point3f(shoulderWidth * -1.6f * 0.5f, shoulderWidth * -1.8f * 0.5f, 0));
        outerLine.setCoordinate(7, new Point3f(shoulderWidth * -1.6f * 0.5f, shoulderWidth * 1.8f * 0.5f, 0));

        Shape3D outerLines = new Shape3D(outerLine, lineAppearance);
        gridBranchGroup.addChild(outerLines);

        gridBranchGroup.addChild(centerCenterBranchGroup);
        gridBranchGroup.addChild(centerBranchGroup);
        gridBranchGroup.addChild(upBranchGroup);
        gridBranchGroup.addChild(downBranchGroup);
        gridBranchGroup.addChild(leftBranchGroup);
        gridBranchGroup.addChild(rightBranchGroup);
        gridBranchGroup.addChild(upLeftBranchGroup);
        gridBranchGroup.addChild(upRightBranchGroup);
        gridBranchGroup.addChild(downLeftBranchGroup);
        gridBranchGroup.addChild(downRightBranchGroup);

        Text2D text = new Text2D("McNeill Gesture Space", new Color3f(1, 1, 0), "Helvetica", 20, Font.BOLD);
        // make text 2-sided
        Appearance app = text.getAppearance();
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        polyAttrib.setBackFaceNormalFlip(true);
        app.setPolygonAttributes(polyAttrib);
        Transform3D gridHeadText = new Transform3D();
        gridHeadText.setTranslation(new Vector3f(-shoulderWidth * 1.6f * 0.5f, shoulderWidth * 1.8f * 0.5f, 0));
        TransformGroup gridHeadtext = new TransformGroup(gridHeadText);
        gridHeadtext.addChild(text);

        gridBranchGroup.addChild(gridHeadtext);
    }

    public void activateAllSegments() {
        appearanceCenterCenter.getTransparencyAttributes().setTransparency(0.8f);
        appearanceCenterCenter.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceCenter.getTransparencyAttributes().setTransparency(0.8f);
        appearanceCenter.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUp.getTransparencyAttributes().setTransparency(0.8f);
        appearanceUp.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDown.getTransparencyAttributes().setTransparency(0.8f);
        appearanceDown.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceLeft.getTransparencyAttributes().setTransparency(0.8f);
        appearanceLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceRight.getTransparencyAttributes().setTransparency(0.8f);
        appearanceRight.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUpLeft.getTransparencyAttributes().setTransparency(0.8f);
        appearanceUpLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDownLeft.getTransparencyAttributes().setTransparency(0.8f);
        appearanceDownLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUpRight.getTransparencyAttributes().setTransparency(0.8f);
        appearanceUpRight.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDownRight.getTransparencyAttributes().setTransparency(0.8f);
        appearanceDownRight.getColoringAttributes().setColor(new Color3f(Color.red));
    }

    public void deactivateAllSegments() {
        appearanceCenterCenter.getTransparencyAttributes().setTransparency(1.0f);
        appearanceCenterCenter.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceCenter.getTransparencyAttributes().setTransparency(1.0f);
        appearanceCenter.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUp.getTransparencyAttributes().setTransparency(1.0f);
        appearanceUp.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDown.getTransparencyAttributes().setTransparency(1.0f);
        appearanceDown.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceLeft.getTransparencyAttributes().setTransparency(1.0f);
        appearanceLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceRight.getTransparencyAttributes().setTransparency(1.0f);
        appearanceRight.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUpLeft.getTransparencyAttributes().setTransparency(1.0f);
        appearanceUpLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDownLeft.getTransparencyAttributes().setTransparency(1.0f);
        appearanceDownLeft.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceUpRight.getTransparencyAttributes().setTransparency(1.0f);
        appearanceUpRight.getColoringAttributes().setColor(new Color3f(Color.red));
        appearanceDownRight.getTransparencyAttributes().setTransparency(1.0f);
        appearanceDownRight.getColoringAttributes().setColor(new Color3f(Color.red));
    }

    public void activateSegment(GestureSpaceSector segment, Color3f color) {
        Color3f colorCheck = new Color3f();
        Color3f checkcolor = new Color3f(Color.orange);
        switch (segment) {
            case CenterCenter:
                appearanceCenterCenter.getTransparencyAttributes().setTransparency(0.8f);
                appearanceCenterCenter.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceCenterCenter.getColoringAttributes().setColor(new Color3f(color));
                break;

            case Center:
                appearanceCenter.getTransparencyAttributes().setTransparency(0.8f);
                appearanceCenter.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceCenter.getColoringAttributes().setColor(new Color3f(color));
                break;

            case Up:
                appearanceUp.getTransparencyAttributes().setTransparency(0.8f);
                appearanceUp.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceUp.getColoringAttributes().setColor(new Color3f(color));

                break;

            case Down:
                appearanceDown.getTransparencyAttributes().setTransparency(0.8f);
                appearanceDown.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceDown.getColoringAttributes().setColor(new Color3f(color));
                break;

            case Left:
                appearanceLeft.getTransparencyAttributes().setTransparency(0.8f);
                appearanceLeft.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceLeft.getColoringAttributes().setColor(new Color3f(color));
                break;

            case Right:
                appearanceRight.getTransparencyAttributes().setTransparency(0.8f);
                appearanceRight.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceRight.getColoringAttributes().setColor(new Color3f(color));
                break;

            case UpLeft:
                appearanceUpLeft.getTransparencyAttributes().setTransparency(0.8f);
                appearanceUpLeft.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceUpLeft.getColoringAttributes().setColor(new Color3f(color));
                break;

            case UpRight:
                appearanceUpRight.getTransparencyAttributes().setTransparency(0.8f);
                appearanceUpRight.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceUpRight.getColoringAttributes().setColor(new Color3f(color));
                break;

            case DownRight:
                appearanceDownRight.getTransparencyAttributes().setTransparency(0.8f);
                appearanceDownRight.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceDownRight.getColoringAttributes().setColor(new Color3f(color));
                break;

            case DownLeft:
                appearanceDownLeft.getTransparencyAttributes().setTransparency(0.8f);
                appearanceDownLeft.getColoringAttributes().getColor(colorCheck);
                if (checkcolor.equals(colorCheck)) {
                    color = new Color3f(Color.red);
                }
                appearanceDownLeft.getColoringAttributes().setColor(new Color3f(color));
                break;
        }
    }

    public void deactivateSegment(GestureSpaceSector segment) {

        switch (segment) {
            case CenterCenter:
                appearanceCenterCenter.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case Center:
                appearanceCenter.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case Up:
                appearanceUp.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case Down:
                appearanceDown.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case Left:
                appearanceLeft.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case Right:
                appearanceRight.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case UpLeft:
                appearanceUpLeft.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case UpRight:
                appearanceUpRight.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case DownRight:
                appearanceDownRight.getTransparencyAttributes().setTransparency(1.0f);
                break;

            case DownLeft:
                appearanceDownLeft.getTransparencyAttributes().setTransparency(1.0f);
                break;
        }
    }
}
