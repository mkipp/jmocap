package de.jmocap.vis.tangentialarrow;

import de.jmocap.anim.FrameChangeListener;

/**
 * @author Franziska Zamponi
 * @date 29.06.13
 */
public class TangentialArrowFrameListener implements FrameChangeListener{
    
    private TangentialArrowController.TaArMap _tac;
    
    public TangentialArrowFrameListener(TangentialArrowController.TaArMap tac){
        this._tac = tac;
    }
    
    @Override
    public void frameUpdate(int frame){
        _tac.updateSwitch(frame);
    }
}
