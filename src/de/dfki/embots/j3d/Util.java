package de.dfki.embots.j3d;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/**
 *
 * @author Michael Kipp
 */
public class Util
{

    private Util()
    {
    }

    /**
     * Computes the full transform from root to the given transform group,
     * including the transform *in* the TG.
     *
     * The resulting transform can be used to find the origin of the TG by
     * applying the transform to (0,0,0).
     *
     * @param tg The TG in question
     * @param tf Resulting transform is returned here (old transform is overwritten)
     */
    public static void getFullTransform(TransformGroup tg, Transform3D tf)
    {
        Transform3D tf2 = new Transform3D();
        tf.setIdentity();
        tg.getTransform(tf2);
        tg.getLocalToVworld(tf);
        tf.mul(tf2);
    }

    /**
     * Computes the transform that takes a point that is *local* in locationTG
     * to the frame of reference of frameTG.
     *
     * If one applies the resulting transform to (0,0,0) one obtains the location
     * of the origin of locationTG in the frame of reference of frameTG.
     *
     * @param locationTG The TG in question, must be (grand)child of frameTG
     * @param frameTG The new frame of reference, must be parent of locationTG
     * @param tf Resulting transform is returned here (old transform is overwritten)
     */
    public static void getRelativeTransform(TransformGroup locationTG, TransformGroup frameTG, Transform3D tf)
    {
        Transform3D tf2 = new Transform3D();
        getFullTransform(locationTG, tf);
        getFullTransform(frameTG, tf2);
        tf2.invert();
        tf.mul(tf2, tf);
    }
}
