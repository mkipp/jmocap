package de.jmocap.vis.relativemovement;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.jmocap.JMocap;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;

/* Displays the relative movement direction between up to 6 subjects
 * 
 * author: Levin von Hollen
 */
public class RelativeMovementPlate {

	private JMocap jmocap;

	private RMP[] rmps = new RMP[5];
	private TransformGroup[] rmpTGs = new TransformGroup[5];
	private double[][] subjectDirections =  new double[5][];
	private Vector3d[] subjectPosition = new Vector3d[5];
	
	private int figureAmount;

	private float hipsWidth;

	private TransformGroup globalTG = new TransformGroup();
	private BranchGroup root = new BranchGroup();



	public RelativeMovementPlate(JMocap jmocap) {
		this.jmocap = jmocap;
		initRMP();
		RelativeMovementPlateListener rMPL = new RelativeMovementPlateListener(this);
		jmocap.getFigure().addFrameChangeListener(rMPL);
	}

	public void initRMP() {
		figureAmount = jmocap.getFigureManager().getFigures().size();
		if(figureAmount > 6){
			figureAmount = 6;
		}
		globalTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		globalTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		globalTG.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		globalTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		globalTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_DETACH);

		Point3d l_Femur = new Point3d(0,0,0);
		Point3d r_Femur = new Point3d(0,0,0);

		jmocap.getFigure().getSkeleton().findBone("R_Femur").getWorldPosition(r_Femur);
		jmocap.getFigure().getSkeleton().findBone("L_Femur").getWorldPosition(l_Femur);

		Vector3d hipsvec = new Vector3d(r_Femur.x - l_Femur.x,r_Femur.y - l_Femur.y,r_Femur.z - l_Femur.z);
		hipsWidth = (float)hipsvec.length();

		for(int count = 0; count <= figureAmount; count++) {
			rmps[count] = new RMP();
			rmpTGs[count] = new TransformGroup();
			rmpTGs[count].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			rmpTGs[count].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			rmpTGs[count].addChild(rmps[count].getRoot());
			globalTG.addChild(rmpTGs[count]);
		}


		for(int i = 1; i <= figureAmount; i++) {
			Point3d hipJoint = new Point3d(0,0,0);
			jmocap.getFigureManager().getFigures().get(i-1).getSkeleton().findBone("Root").getWorldPosition(hipJoint);
			rmps[i-1].createRMP(hipsWidth, i);
			Transform3D t = new Transform3D();
			t.setTranslation(new Vector3d(hipJoint.x, hipJoint.y, hipJoint.z));
			rmpTGs[i-1].setTransform(t);
		}

		calculatePositionsAndDirections();
		
		root.addChild(globalTG);
		jmocap.getRootBG().addChild(root);
	}


	public void update() {

		for(int x = 0; x < figureAmount; x++) {
			Point3d hipJoint = new Point3d(0,0,0);
			jmocap.getFigureManager().getFigures().get(x).getSkeleton().findBone("Root").getWorldPosition(hipJoint);
			Vector3d vect = new Vector3d(hipJoint.x, hipJoint.y, hipJoint.z);
			subjectPosition[x] = vect;
		}

		for(int i = 1; i <= figureAmount; i++) {
			Point3d hipJoint = new Point3d(0,0,0);
			jmocap.getFigureManager().getFigures().get(i-1).getSkeleton().findBone("Root").getWorldPosition(hipJoint);
			Transform3D t = new Transform3D();
			Vector3d vec = new Vector3d(hipJoint.x, hipJoint.y, hipJoint.z);
			t.setTranslation(vec);
			Transform3D tRot = new Transform3D();
			tRot.rotY(subjectDirections[i-1][jmocap.getFigureManager().getFigures().get(i-1).getPlayer().getCurrentFrame()] - Math.PI/2);
			t.mul(tRot);
			rmpTGs[i-1].setTransform(t);
			
			Vector3d currentVec = subjectPosition[i-1];
			for(int angleCounter = 0; angleCounter < figureAmount; angleCounter++) {
				if(angleCounter == i-1)
					continue;
				double angle = 0;
				Vector3d vecToOpponent = subjectPosition[angleCounter];
				vecToOpponent.sub(currentVec);
//				angle = Math.atan2(vecToOpponent.x, vecToOpponent.z);
				angle = subjectDirections[i-1][jmocap.getFigureManager().getFigures().get(i-1).getPlayer().getCurrentFrame()];
				angle -= Math.PI/2;
				
				rmps[i-1].setDirectionBalls(angleCounter+1, angle, vecToOpponent);
			}
		}
	}

	private void calculatePositionsAndDirections() {

		for(int subjectNumber = 1; subjectNumber <= figureAmount; subjectNumber++) {
			
			Figure figure = jmocap.getFigureManager().getFigures().get(subjectNumber-1);
			Bone bone = figure.getSkeleton().findBone("Root");
			int maxFrame = figure.getPlayer().getNumFrames();
			float fps = figure.getPlayer().getPlaybackFps();
			Point3d allPos[] = new Point3d[maxFrame];
			
			for(int i = 0; i < maxFrame; i++) {
				figure.getPlayer().gotoTime(i/fps);
				Point3d rootPos = new Point3d();
				bone.getWorldPosition(rootPos);
				allPos[i] = rootPos;
			}

			int  	counterOfPreviousFrames, 
			counterForMaximumFrames;

			double 
			averageX = 0,
			averageZ = 0,
			angle;

			Vector3d vector;
			
			double[] angels = new double[allPos.length];
			
			for(int frameCounter = 0; frameCounter < allPos.length; frameCounter++){
				averageX = 0;
				averageZ = 0;
				Point3d currentPosition = allPos[frameCounter];

				counterOfPreviousFrames = frameCounter;
				counterForMaximumFrames = 0;

				// avoid bugs for the first entry
				if(frameCounter == 0){
					averageX = currentPosition.x;
					averageZ = currentPosition.z;
					counterForMaximumFrames ++;
				}
				// get the previous positions and sum their coordinates
				while(counterOfPreviousFrames > 0 && counterForMaximumFrames < 50){

					counterForMaximumFrames ++;
					counterOfPreviousFrames --;

					Point3d pastPosition = allPos[counterOfPreviousFrames];

					averageX = averageX + pastPosition.x;
					averageZ = averageZ + pastPosition.z;
				}
				// get the average
				averageX /= counterForMaximumFrames;
				averageZ /= counterForMaximumFrames;

				// vector between the current and the past position
				vector = new Vector3d(currentPosition.x-averageX, 0.0, currentPosition.z-averageZ);

				angle = Math.atan2(vector.x, vector.z);

				angels[frameCounter] = angle;
			}
			subjectDirections[subjectNumber-1] = angels;
			figure.getPlayer().gotoTime(0);
		}

	}
}
