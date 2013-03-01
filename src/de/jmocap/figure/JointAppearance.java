package de.jmocap.figure;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;

/**
 * Singleton class to give out appearances for "selected" and "normal"
 * nodes.
 * 
 * @author Michael Kipp
 */
public class JointAppearance extends Appearance {

    private static final JointAppearance INSTANCE = new JointAppearance();
    private static final JointAppearance SELECTED_INSTANCE = new JointAppearance(true);
    
    private JointAppearance() {
        super();
        setMaterial(makeMaterial(.5f, .5f, .5f));
    }
    
    private JointAppearance(boolean sel) {
        super();
        if (sel) 
            setMaterial(makeMaterial(1f, .5f, .5f));
        else 
            setMaterial(makeMaterial(.5f, .5f, .5f));
    }
    
    public static JointAppearance getInstance() {
        return INSTANCE;
    }
    
    public static JointAppearance getSelectedInstance() {
        return SELECTED_INSTANCE;
    }
    
    private Material makeMaterial(float r, float g, float b) {
        Material mat = new Material();
        mat.setDiffuseColor(r, g, b);
        mat.setSpecularColor(1f,.3f,.3f);
        mat.setShininess(20);
        mat.setLightingEnable(true);
        return mat;
    }
}
