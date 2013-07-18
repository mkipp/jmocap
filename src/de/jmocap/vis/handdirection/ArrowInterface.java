package de.jmocap.vis.handdirection;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.TransformGroup;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public interface ArrowInterface {

    BranchGroup getRoot();

    TransformGroup getTransformGroupAngle();

    TransformGroup getTransformGroupPosition();

    Appearance getAppearance();

    void setColoringAttributes(ColoringAttributes colAttr);

    void setScale(double scale);
}
