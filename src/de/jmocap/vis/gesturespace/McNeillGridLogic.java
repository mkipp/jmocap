package de.jmocap.vis.gesturespace;

import java.awt.Color;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import de.jmocap.JMocap;

/**
 * @author Levin Freiherr von Hollen
 * @version 14-07-2013
 */
public class McNeillGridLogic {

    private JMocap jmocap;
    private McNeillGrid mng = new McNeillGrid();
    private BranchGroup mcNeillGrid;
    private TransformGroup globalTransformGroup = new TransformGroup();
    private Vector3f shoulderWidthVector;
    private BranchGroup root = new BranchGroup();

    public McNeillGridLogic(JMocap jmocap) {
        this.jmocap = jmocap;
        globalTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        globalTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        globalTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        globalTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        globalTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        root.setCapability(BranchGroup.ALLOW_DETACH);
        initMcNeillGrid();
        McNeillListener mngl = new McNeillListener(this);
        jmocap.getFigure().addFrameChangeListener(mngl);
    }

    public void initMcNeillGrid() {
        Point3d rightShoulderd = new Point3d(0, 0, 0);
        Point3d leftShoulderd = new Point3d(0, 0, 0);

        jmocap.getFigure().getSkeleton().findBone("R_Humerus").getWorldPosition(rightShoulderd);
        jmocap.getFigure().getSkeleton().findBone("L_Humerus").getWorldPosition(leftShoulderd);

        Point3f rightShoulder = new Point3f((float) rightShoulderd.x,
                (float) rightShoulderd.y,
                (float) rightShoulderd.z);

        Point3f leftShoulder = new Point3f((float) leftShoulderd.x,
                (float) leftShoulderd.y,
                (float) leftShoulderd.z);

        //Calculate the width of the McNeillGrid relative to the shoulder width
        shoulderWidthVector = new Vector3f(rightShoulder.x - leftShoulder.x,
                rightShoulder.y - leftShoulder.y,
                rightShoulder.z - leftShoulder.z);

        mng.createGrid(shoulderWidthVector.length());
        mcNeillGrid = mng.getGrid();
        globalTransformGroup.addChild(mcNeillGrid);

        root.addChild(globalTransformGroup);
        jmocap.getRootBG().addChild(root);
        updateMcNeillGridPosition();
    }

    public void updateMcNeillGridPosition() {

        //move McNeillGrid in front of the Thorax
        Point3d rightShoulderd = new Point3d();
        jmocap.getFigure().getSkeleton().findBone("R_Humerus").getWorldPosition(rightShoulderd);
        Point3f rightShoulder = new Point3f((float) rightShoulderd.x, (float) rightShoulderd.y, (float) rightShoulderd.z);

        Point3d leftShoulderd = new Point3d();
        jmocap.getFigure().getSkeleton().findBone("L_Humerus").getWorldPosition(leftShoulderd);
        Point3f leftShoulder = new Point3f((float) leftShoulderd.x, (float) leftShoulderd.y, (float) leftShoulderd.z);

        Vector3f shoulderVector = new Vector3f(leftShoulder.x - rightShoulder.x,
                0.0f,
                leftShoulder.z - rightShoulder.z);


        Vector3f positionVectorOfTheGrid = shoulderVector;
        //move McNeillGrid to the Thorax of the skeleton
        Transform3D transformGridToThorax = new Transform3D();

        Point3d thorax = new Point3d();
        jmocap.getFigure().getSkeleton().findBone("Thorax").getWorldPosition(thorax);

        Point3f positionOfThorax = new Point3f((float) thorax.x, (float) thorax.y, (float) thorax.z);

        //increase the y value so that it is a bit higher than the thorax
        Vector3f vectorToThorax = new Vector3f(positionOfThorax.x, (positionOfThorax.y + shoulderVector.length() * 0.4f), positionOfThorax.z);

        transformGridToThorax.setTranslation(vectorToThorax);

        //Multiplie the shoulder vector by 1.25
        positionVectorOfTheGrid.scale(1.25f);

        //rotate vec by 90 degree
        float oldX = positionVectorOfTheGrid.x;
        positionVectorOfTheGrid.x = (float) (positionVectorOfTheGrid.x * Math.cos(Math.PI / 2)
                - positionVectorOfTheGrid.z * Math.sin(Math.PI / 2));
        positionVectorOfTheGrid.z = (float) (oldX * Math.sin(Math.PI / 2)
                + positionVectorOfTheGrid.z * Math.cos(Math.PI / 2));

        Transform3D moveGridTwoTimesShoulderVector = new Transform3D();
        moveGridTwoTimesShoulderVector.setTranslation(positionVectorOfTheGrid);

        //Rotate the grid parallel to the shoulder vector
        Transform3D rotateGridParallelToShoulder = new Transform3D();
        shoulderVector.normalize();
        float angle = (float) Math.atan2(shoulderVector.x, shoulderVector.z);
        rotateGridParallelToShoulder.rotY(angle);

        //Multiplie all transformations together
        Transform3D finalTransformOfTheGrid = new Transform3D();
        finalTransformOfTheGrid.mul(transformGridToThorax);
        finalTransformOfTheGrid.mul(moveGridTwoTimesShoulderVector);
        finalTransformOfTheGrid.mul(rotateGridParallelToShoulder);
        //Grid stands in front of the Skeleton in the right Position
        globalTransformGroup.setTransform(finalTransformOfTheGrid);

        getActiveSegment();
    }

    public void getActiveSegment() {
        mng.deactivateAllSegments();
        // pre calc the grid position and angle of the grid to the x-axis
        Vector3f gridPositionAsVector = mng.getPosition();

        Vector3f tempGridVec = mng.getVectorFromRightToLeftofTheGrid();
        tempGridVec.normalize();
        float angle = -(float) Math.atan2(tempGridVec.x, tempGridVec.z);
        angle += Math.PI * 1.5;
        // Move the right hand 
        Point3d rightHand = new Point3d();
        jmocap.getFigure().getSkeleton().findBone("R_Wrist").getWorldPosition(rightHand);
        Vector3f rightHandPositionAsVector = new Vector3f((float) rightHand.x, (float) rightHand.y, (float) rightHand.z);
        rightHandPositionAsVector.sub(gridPositionAsVector);

        //Move the left hand
        Point3d leftHand = new Point3d();
        jmocap.getFigure().getSkeleton().findBone("L_Wrist").getWorldPosition(leftHand);
        Vector3f leftHandPositionAsVector = new Vector3f((float) leftHand.x, (float) leftHand.y, (float) leftHand.z);
        leftHandPositionAsVector.sub(gridPositionAsVector);

        //rotate the handpoints by the angle of the grid to the x-axis
        //rot hand r/l
        Matrix3f rotationMatrixr = new Matrix3f((float) Math.cos(angle), 0.0f, (float) Math.sin(angle),
                0.0f, 1.0f, 0.0f,
                (float) -Math.sin(angle), 0.0f, (float) Math.cos(angle));

        Matrix3f rotationMatrixl = new Matrix3f(rotationMatrixr);

        rotationMatrixr.transform(rightHandPositionAsVector);
        rotationMatrixl.transform(leftHandPositionAsVector);

        //check in wich segment the point is.
        coordinateComparesion(rightHandPositionAsVector.x, rightHandPositionAsVector.y, 0);
        coordinateComparesion(leftHandPositionAsVector.x, leftHandPositionAsVector.y, 1);
    }

    public void coordinateComparesion(float x, float y, int rightOrLeft) {
        float ccw = mng.centerCenterWidth / 2;
        float cch = mng.centerCenterHeight / 2;
        float cw = mng.centerWidth;
        float uw = mng.upWidth / 2;
        float uh = mng.upHeight;
        float dw = mng.downWidth / 2;
        float dh = mng.downHeight;
        float lw = mng.leftWidth;
        float lh = mng.leftHeight / 2;
        float rw = mng.rightWidth;
        float rh = mng.rightHeight / 2;
        Color3f color = new Color3f(Color.white);

        if (rightOrLeft == 0) {
            color = new Color3f(Color.orange);
        }

        if (rightOrLeft == 1) {
            color = new Color3f(Color.green);
        }

        //centerCenter Check
        if (-ccw < x && x < ccw) {
            if (-cch < y && y < cch) {
                mng.activateSegment(GestureSpaceSector.CenterCenter, color);
            }
        }

        //up Check
        if (-uw < x && x < uw) {
            if ((cch + cw) < y && y < (cch + cw + uh)) {
                mng.activateSegment(GestureSpaceSector.Up, color);
            }
        }

        //down Check
        if (-dw < x && x < dw) {
            if (-(cch + cw) > y && y > -(cch + cw + dh)) {
                mng.activateSegment(GestureSpaceSector.Down, color);
            }
        }

        //left check
        if (-(ccw + cw + lw) < x && x < -(ccw + cw)) {
            if (-lh < y && y < lh) {
                mng.activateSegment(GestureSpaceSector.Left, color);
            }
        }

        //right check
        if ((ccw + cw) < x && x < (ccw + cw + rw)) {
            if (-rh < y && y < rh) {
                mng.activateSegment(GestureSpaceSector.Right, color);
            }
        }

        //upRight n' downRight check
        if (ccw < x && x < (ccw + cw + rw)) {
            if ((cch + cw) < y && y < (cch + cw + uh)) {
                mng.activateSegment(GestureSpaceSector.UpRight, color);
            } else if (-(cch + cw + uh) < y && y < -(cch + cw)) {
                mng.activateSegment(GestureSpaceSector.DownRight, color);
            }
        }
        if ((ccw + cw) < x && x < (ccw + cw + rw)) {
            if (cch < y && y < (cch + cw)) {
                mng.activateSegment(GestureSpaceSector.UpRight, color);
            } else if (-(cch + cw) < y && y < -cch) {
                mng.activateSegment(GestureSpaceSector.DownRight, color);
            }
        }

        //upLeft n' downLeft check
        if (-(ccw + cw + lw) < x && x < -ccw) {
            if ((cch + cw) < y && y < (cch + cw + uh)) {
                mng.activateSegment(GestureSpaceSector.UpLeft, color);
            } else if (-(cch + cw + uh) < y && y < -(ccw + cw)) {
                mng.activateSegment(GestureSpaceSector.DownLeft, color);
            }
        }
        if (-(ccw + cw + lw) < x && x < -(ccw + cw)) {
            if (cch < y && y < (cch + cw)) {
                mng.activateSegment(GestureSpaceSector.UpLeft, color);
            } else if (-(cch + cw) < y && y < -cch) {
                mng.activateSegment(GestureSpaceSector.DownLeft, color);
            }
        }

        //center check
        if (-(ccw + cw) < x && x < (ccw + cw)) {
            if (cch < y && y < (cch + cw)) {
                mng.activateSegment(GestureSpaceSector.Center, color);
            } else if (-(cch + cw) < y && y < -cch) {
                mng.activateSegment(GestureSpaceSector.Center, color);
            }
        }
        if (-cch < y && y < cch) {
            if (-(ccw + cw) < x && x < -ccw) {
                mng.activateSegment(GestureSpaceSector.Center, color);
            } else if (ccw < x && x < (ccw + cw)) {
                mng.activateSegment(GestureSpaceSector.Center, color);
            }
        }
    }

    public void increaseXScale() {
        mng.increaseXScale();
        initMcNeillGrid();
    }

    public void decreaseXScale() {
        mng.decreaseXScale();
        initMcNeillGrid();
    }

    public void increaseYScale() {
        mng.increaseYScale();
        initMcNeillGrid();
    }

    public void decreaseYScale() {
        mng.decreaseYScale();
        initMcNeillGrid();
    }
}
