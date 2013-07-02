
package de.jmocap.vis.tangentialarrow;

import java.util.Map;
import java.util.TreeMap;
import javax.vecmath.Point3d;
import java.util.Vector;
import de.jmocap.JMocap;
import de.jmocap.scene.MotionTrailPoint;
import de.jmocap.figure.Bone;
import de.jmocap.figure.Figure;
import java.util.ArrayList;
import java.util.List;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Switch;
import javax.vecmath.Vector3d;


/**
 *
 * @author Franziska
 * @date 29.06.13
 * 
 * creates, removes and controlls all "arrow trails for different bones and figures" 
 * or maps of TangentialArrows
 */
public class TangentialArrowController {
    
    private JMocap _jmocap;
    private List<TaArMap> _taArMaps;
    private BranchGroup _rootController;
    
    public TangentialArrowController(JMocap jmocap){
        _jmocap = jmocap;
        _rootController = new BranchGroup();
        _rootController.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        _rootController.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        _rootController.setCapability(BranchGroup.ALLOW_DETACH);
        _jmocap.getRootBG().addChild(_rootController);
        _taArMaps = new ArrayList<TaArMap>();
    }
    
    /**
     * 
     * returns a map of TangentialArrows,
     * but their angles are not jet computed
     */
    private Map<Integer, TangentialArrow> getNewTangentialArrows(
            double startSec, double endSec, String figureName, String boneName)
    {
        Map<Integer, TangentialArrow> mapTanArrow = new TreeMap<Integer, TangentialArrow>();
        Figure figure = getFigure(figureName);//find the right figure
        double dDistance = 1 / (float) figure.getPlayer().getPlaybackFps();
        
        Bone bone = figure.getSkeleton().findBone(boneName);
        double scale = bone.getLength();
        int nCurrentFrame = figure.getPlayer().getCurrentFrame(); //save figure position for later
        if (bone != null) {
            for (double time = startSec; time < endSec; time += dDistance) {
                figure.getPlayer().gotoTime(time);
                int frame = figure.getPlayer().getCurrentFrame();
                Point3d p3dPositionInWorld = new Point3d();
                bone.getWorldPosition(p3dPositionInWorld);
                mapTanArrow.put(frame, new TangentialArrow(
                        frame, time, boneName, scale, p3dPositionInWorld));
            }
        }
        // go to old figure position:
        figure.getPlayer().gotoTime(
                    nCurrentFrame / figure.getPlayer().getPlaybackFps());
        
        return mapTanArrow;
    }
    
    private Figure getFigure(String figureName){
        List<Figure> figures= _jmocap.getFigureManager().getFigures();
        for (Figure figure : figures){
            if ( figure.getName().equals(figureName)==true ){
                return figure;
            }
        }
        return null;
    }
    
    /**
     * adds a new GranchGroup with Switch controlling TangentialArrows,
     * stores the arrows as a new TaArMap object in the list _taArMap
     */
    public void addTaArMap(
            double startSec, double endSec, String figureName, String boneName)
    {   
        float currentFPS = getFigure(figureName).getPlayer().getPlaybackFps();
        Map<Integer, TangentialArrow> mapTA = getNewTangentialArrows(startSec, 
                endSec, figureName, boneName);
        mapTA = addAngles(mapTA, currentFPS);
        TaArMap newTaArMap = new TaArMap(figureName, boneName, currentFPS, mapTA);
        _rootController.addChild(newTaArMap.getRoot());
        getFigure(figureName).getPlayer().addListener(newTaArMap.getListener());
        _taArMaps.add(newTaArMap);
        // store some information for GUI:
        newTaArMap.setStartAndEndSec(startSec, endSec);
    }
    
    /**
     * returns a vector between two points
     */
    private Vector3d getNewVector(Point3d past, Point3d future){
        double x = future.x - past.x;
        double y = future.y - past.y;
        double z = future.z - past.z;
        Vector3d vector = new Vector3d(x, y, z);
        return vector;
    }
    
    /**
     * old version! no longer in use!
     * 
     * returns the average vector of two vecors
     */
    private Vector3d getAverageVector(Vector3d v1, Vector3d v2){
        //berechnet den Durchschnitts-Vektor aus zwei Vektoren
        //silmpelst! könnte man sicher besser machen, mit gewichtung und so
        Vector3d averageVector = new Vector3d(
                (v1.x + v2.x)/2,
                (v1.y + v2.y)/2,
                (v1.z + v2.z)/2);
        return averageVector;
    }
    
    /**
     * new version!
     * 
     * returns the average vector of all vectors in the list 
     */ 
    private Vector3d getAverageVector(List<Vector3d> vectors){ 
        double x=0, y=0, z=0; 
        int i=0;
        for (Vector3d v : vectors){
            i++;
            x = x + v.x;
            y = y + v.y;
            z = z + v.z;
        }
        x = x/i;
        y = y/i;
        z = z/i;
        return new Vector3d( x, y, z );
    }
    
    /**
     * old version! no longer in use!
     * 
     * rotates all TangentialArrows in the map
     */
    private Map<Integer, TangentialArrow> addAngles(
            Map<Integer, TangentialArrow> tangentialArrows)
    {
        // extremely simple way!                
        for (TangentialArrow currentTA : tangentialArrows.values()){
            int current = currentTA.getFrame();
            Vector3d v1 = new Vector3d();
            Vector3d v2 = new Vector3d();
            for (int i = 3; i>0; i--){
                if( tangentialArrows.containsKey(current-i)==true &
                        tangentialArrows.containsKey(current+i)==true)
                {
                    TangentialArrow pastTA = tangentialArrows.get(current-i);
                    TangentialArrow futureTA = tangentialArrows.get(current+i);
                    v2 = new Vector3d(v1);
                    v1 = getNewVector(pastTA.getPositionPoint(), futureTA.getPositionPoint());
                    if(v1!=null & v2!=null) v1 = getAverageVector(v1, v2);
                }
                
                else if (tangentialArrows.containsKey(current-1)==false &
                        tangentialArrows.containsKey(current+1)==true) // first arrow
                {
                    TangentialArrow futureTA = tangentialArrows.get(current+1);
                    v1 = getNewVector(currentTA.getPositionPoint(), futureTA.getPositionPoint());
                }
                else if (tangentialArrows.containsKey(current-1)==true &
                        tangentialArrows.containsKey(current+1)==false) // last arrow
                {
                    TangentialArrow pastTA = tangentialArrows.get(current-1);
                    v1 = getNewVector(pastTA.getPositionPoint(), currentTA.getPositionPoint());
                }
            }
            currentTA.setRotation(v1);
        }
        return tangentialArrows;
    }
    
    /**
     * rotates all TangentialArrows in the map
     */
    private Map<Integer, TangentialArrow> addAngles( 
            Map<Integer, TangentialArrow> tangentialArrows, float fps)
    {
        int rate = (int) (fps/4); // könnte man evtl auch anders berechnen, sollte irwie von fps abhängen            
        for (TangentialArrow currentTA : tangentialArrows.values()){
            int current = currentTA.getFrame();
            List<Vector3d> vectorsForComputing = new ArrayList<Vector3d>();
            for (int i = rate; i>0; i--){
                Vector3d v1 = new Vector3d();
                if( tangentialArrows.containsKey(current-i)==true &
                        tangentialArrows.containsKey(current+i)==true)
                {
                    TangentialArrow pastTA = tangentialArrows.get(current-i);
                    TangentialArrow futureTA = tangentialArrows.get(current+i);
                    v1 = getNewVector(pastTA.getPositionPoint(), futureTA.getPositionPoint());
                }
                
                else if (tangentialArrows.containsKey(current-1)==false &
                        tangentialArrows.containsKey(current+1)==true) // first arrow
                {
                    TangentialArrow futureTA = tangentialArrows.get(current+1);
                    v1 = getNewVector(currentTA.getPositionPoint(), futureTA.getPositionPoint());
                }
                else if (tangentialArrows.containsKey(current-1)==true &
                        tangentialArrows.containsKey(current+1)==false) // last arrow
                {
                    TangentialArrow pastTA = tangentialArrows.get(current-1);
                    v1 = getNewVector(pastTA.getPositionPoint(), currentTA.getPositionPoint());
                }
                vectorsForComputing.add(v1);
            }
            Vector3d vFinal = getAverageVector(vectorsForComputing);
            currentTA.setRotation(vFinal);
        }
        return tangentialArrows;
    }
    
    public void removeTaArMap(String string){
        for (TaArMap map : _taArMaps){
            if(map.toString().equals(string)){
                removeTaArMap(map);
            }
        }
    }
    
    /**
     * removes the specific savely from the list
     */
    public void removeTaArMap(TaArMap taArMap){
        // remove Listener:
        getFigure( taArMap.getFigureName() ).getPlayer().removeListener( taArMap.getListener() );
        // remove BranchGroup:
        _rootController.removeChild(taArMap.getRoot()); 
        // remove from list:
        _taArMaps.remove(taArMap);
    }
    
    public BranchGroup getRoot(){
        return _rootController;
    }
    
    public List<TaArMap> getTaArMaps(){
        return _taArMaps;
    }
    
    /*
     * contains a certain trail of arrows for a specific bone in a map
     * and what is needed to operate on this certain trail
     */
    public class TaArMap{
        
        private String _figureName;
        private String _boneName;
        private BranchGroup _rootTaArMap;
        private Switch _switch;
        private boolean _active; // means ready to work with _switch
        private float _fps; // if the fps in AnimController is not equal this Object gets useless! 
        private TangentialArrowFrameListener _listener;
        private Map<Integer, TangentialArrow> _tangentialArrows; //trail of arrows for a specific bone
        //information needed for the GUI:
        private double _startSec;
        private double _endSec;
        
        private TaArMap(String figureName, String boneName, float fps, 
                Map<Integer, TangentialArrow> tangentialArrows)
        {
            _figureName = figureName;
            _boneName = boneName;
            _fps = fps;
            _tangentialArrows = tangentialArrows;
            _listener = new TangentialArrowFrameListener(this);
            
            // add Arrows to Switch:
            _switch = new Switch();
            _switch.setCapability(Switch.ALLOW_SWITCH_WRITE);
            int i = 0;
            for(TangentialArrow ta : _tangentialArrows.values()){
                _switch.addChild( ta.getTgForSwitch() );
                ta.setSwitchIndex(i);
                i++;
            }
            _rootTaArMap = new BranchGroup();
            _rootTaArMap.setCapability(BranchGroup.ALLOW_DETACH);
            _rootTaArMap.addChild(_switch);
            _active = true;
        }
        
        public void updateSwitch(int frame){
            if (_active == true){
                if( _tangentialArrows.containsKey(frame)==true ){
                    int makeVisible = _tangentialArrows.get(frame).getSwitchIndex();
                    _switch.setWhichChild(makeVisible);
                }
                else{
                    _switch.setWhichChild(Switch.CHILD_NONE);
                }
            }
        }
        
        /**
         * adds some information nedded for the GUI
         */
        private void setStartAndEndSec(double startSec, double endSec){
            _startSec = startSec;
            _endSec = endSec;
        }
        
        private BranchGroup getRoot(){
            return _rootTaArMap;
        }
        
        private TangentialArrowFrameListener getListener(){
            return _listener;
        }
        
        private String getFigureName(){
            return _figureName;
        }
        
        private String getBoneName(){
            return _boneName;
        }
        
        private float getFPS(){
            return _fps;
        }
       
        public String toString(){
            return (_figureName + " - " + _boneName + " - " + _startSec + " - " + _endSec);
        }
    } 
} 