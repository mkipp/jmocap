package de.jmocap.vis.relativemovement;

import java.awt.Color;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

/**
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */
public class RMP {

    private BranchGroup root = new BranchGroup();
    private BranchGroup ballBG = new BranchGroup();
    private float hipsWidth;
    private Color3f subject1 = new Color3f(Color.blue);
    private Color3f subject2 = new Color3f(Color.yellow);
    private Color3f subject3 = new Color3f(Color.green);
    private Color3f subject4 = new Color3f(Color.orange);
    private Color3f subject5 = new Color3f(Color.cyan);
    private Color3f subject6 = new Color3f(Color.magenta);
    private TransformGroup ball1tg = new TransformGroup();
    private TransformGroup ball2tg = new TransformGroup();
    private TransformGroup ball3tg = new TransformGroup();
    private TransformGroup ball4tg = new TransformGroup();
    private TransformGroup ball5tg = new TransformGroup();
    private TransformGroup ball6tg = new TransformGroup();
    private Appearance ball1app;
    private Appearance ball2app;
    private Appearance ball3app;
    private Appearance ball4app;
    private Appearance ball5app;
    private Appearance ball6app;

    public BranchGroup createRMP(float hipsWidth, int subjectNumber) {
        this.hipsWidth = hipsWidth;

        Appearance appPlate = new Appearance();
        ColoringAttributes colorAttPlate = new ColoringAttributes();
        Color3f color = getColorFromSubjectNumber(subjectNumber);
        colorAttPlate.setColor(color);
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(TransparencyAttributes.FASTEST);
        ta.setTransparency(0.5f);
        appPlate.setColoringAttributes(colorAttPlate);
        appPlate.setTransparencyAttributes(ta);

        //Plate
        Cylinder plate = new Cylinder(hipsWidth / 2, 0.01f, appPlate);


        //Arrow Body
        Appearance appArrow = new Appearance();
        ColoringAttributes colorArrow = new ColoringAttributes();
        colorArrow.setColor(new Color3f(Color.red));
        appArrow.setColoringAttributes(colorArrow);

        Cylinder arrowBody = new Cylinder(0.02f, hipsWidth * 0.55f, appArrow);

        Transform3D rotTheBody = new Transform3D();
        rotTheBody.rotZ(Math.toRadians(-90));

        TransformGroup arrowBodyRot = new TransformGroup(rotTheBody);
        arrowBodyRot.addChild(arrowBody);

        //Arrow Piek
        Cone arrowPiek = new Cone(0.04f, hipsWidth * 0.2f, appArrow);

        Transform3D piekOnTop = new Transform3D();
        piekOnTop.setTranslation(new Vector3f(hipsWidth * 0.375f, 0.0f, 0.0f));

        Transform3D rotThePiek = new Transform3D();
        rotThePiek.rotZ(Math.toRadians(-90));
        piekOnTop.mul(rotThePiek);

        TransformGroup arrowPiekToTop = new TransformGroup(piekOnTop);
        arrowPiekToTop.addChild(arrowPiek);

        initDirectionBalls();

        root.addChild(plate);
        root.addChild(arrowBodyRot);
        root.addChild(arrowPiekToTop);
        root.addChild(ballBG);

        return root;
    }

    private Appearance getBallApp(Color3f color) {

        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setCapabilityIsFrequent(TransparencyAttributes.ALLOW_VALUE_READ);
        ta.setCapabilityIsFrequent(TransparencyAttributes.ALLOW_VALUE_WRITE);
        ta.setTransparencyMode(TransparencyAttributes.NICEST);
        ta.setTransparency(1.0f);
        ColoringAttributes ball1ca = new ColoringAttributes();
        ball1ca.setColor(color);
        Appearance ball1App = new Appearance();
        ball1App.setColoringAttributes(ball1ca);
        ball1App.setTransparencyAttributes(ta);

        return ball1App;
    }

    private void initDirectionBalls() {

        ball1tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ball2tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ball3tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ball4tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ball5tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ball6tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);


        ball1app = getBallApp(subject1);
        Sphere balls1 = new Sphere(hipsWidth * 0.05f, ball1app);

        Transform3D t3Db1 = new Transform3D();
        t3Db1.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball1tg.setTransform(t3Db1);
        ball1tg.addChild(balls1);


        ball2app = getBallApp(subject2);
        Sphere balls2 = new Sphere(hipsWidth * 0.05f, ball2app);

        Transform3D t3Db2 = new Transform3D();
        t3Db2.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball2tg.setTransform(t3Db2);
        ball2tg.addChild(balls2);

        ball3app = getBallApp(subject3);
        Sphere balls3 = new Sphere(hipsWidth * 0.05f, ball3app);

        Transform3D t3Db3 = new Transform3D();
        t3Db3.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball3tg.setTransform(t3Db3);
        ball3tg.addChild(balls3);

        ball4app = getBallApp(subject4);
        Sphere balls4 = new Sphere(hipsWidth * 0.05f, ball4app);

        Transform3D t3Db4 = new Transform3D();
        t3Db4.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball4tg.setTransform(t3Db4);
        ball4tg.addChild(balls4);

        ball5app = getBallApp(subject5);
        Sphere balls5 = new Sphere(hipsWidth * 0.05f, ball5app);

        Transform3D t3Db5 = new Transform3D();
        t3Db5.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball5tg.setTransform(t3Db5);
        ball5tg.addChild(balls5);

        ball6app = getBallApp(subject6);
        Sphere balls6 = new Sphere(hipsWidth * 0.05f, ball6app);

        Transform3D t3Db6 = new Transform3D();
        t3Db6.setTranslation(new Vector3f(hipsWidth / 2, 0.0f, 0.0f));
        ball6tg.setTransform(t3Db6);
        ball6tg.addChild(balls6);

        ballBG.addChild(ball1tg);
        ballBG.addChild(ball2tg);
        ballBG.addChild(ball3tg);
        ballBG.addChild(ball4tg);
        ballBG.addChild(ball5tg);
        ballBG.addChild(ball6tg);
    }

    private Color3f getColorFromSubjectNumber(int subjectNumber) {
        Color3f color = null;

        switch (subjectNumber) {
            case 1:
                color = subject1;
                break;
            case 2:
                color = subject2;
                break;
            case 3:
                color = subject3;
                break;
            case 4:
                color = subject4;
                break;
            case 5:
                color = subject5;
                break;
            case 6:
                color = subject6;
                break;
        }

        return color;
    }

    public void setDirectionBalls(int subjectNumber, double angle, Vector3d vec) {

        Vector3d vector = new Vector3d(vec.x, 0, vec.z);
        vector.normalize();
        vector.scale(hipsWidth / 2);
        Transform3D tmove = new Transform3D();
        tmove.setTranslation(vector);
        Transform3D tRot = new Transform3D();
        tRot.rotY(-angle);
        tRot.mul(tmove);

        switch (subjectNumber) {
            case 1:
                ball1tg.setTransform(tRot);
                ball1app.getTransparencyAttributes().setTransparency(0.0f);
                break;
            case 2:
                ball2tg.setTransform(tRot);
                ball2app.getTransparencyAttributes().setTransparency(0.0f);
                break;
            case 3:
                ball3tg.setTransform(tRot);
                ball3app.getTransparencyAttributes().setTransparency(0.0f);
                break;
            case 4:
                ball4tg.setTransform(tRot);
                ball4app.getTransparencyAttributes().setTransparency(0.0f);
                break;
            case 5:
                ball5tg.setTransform(tRot);
                ball5app.getTransparencyAttributes().setTransparency(0.0f);
                break;
            case 6:
                ball6tg.setTransform(tRot);
                ball6app.getTransparencyAttributes().setTransparency(0.0f);
                break;

        }

    }

    public BranchGroup getRoot() {
        return root;
    }
}
