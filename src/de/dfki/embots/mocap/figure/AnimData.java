package de.dfki.embots.mocap.figure;

import de.dfki.embots.mocap.player.MocapPlayer;
import java.util.List;

/**
 * Stores a single mocap animation.
 * 
 * @author Michael Kipp
 */
public class AnimData
{

    private float _data[][]; // first index: bones, second index: frames
    private int _numFrames;
    private float _fps = MocapPlayer.DEFAULT_FPS;

    public AnimData(int numBones)
    {
        _data = new float[numBones][];
    }

    public void putBoneData(int index, List<Float> data)
    {
        _data[index] = new float[data.size()];
        int j = 0;
        for (Float x : data) {
            _data[index][j++] = x;
        }
    }

    public void putBoneData(int index, float[] data)
    {
        _data[index] = data;
    }

    public float[] getBoneData(int index)
    {
        return _data[index];
    }

    public void setNumFrames(int n)
    {
        _numFrames = n;
    }

    public int getNumFrames()
    {
        return _numFrames;
    }

    public void setFps(float fps)
    {
        _fps = fps;
    }

    public float getFps()
    {
        return _fps;
    }

    @Override
    public String toString()
    {
        return "<AnimData frames:" + _numFrames + ">";
    }
}
