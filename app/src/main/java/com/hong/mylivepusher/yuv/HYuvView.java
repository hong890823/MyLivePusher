package com.hong.mylivepusher.yuv;

import android.content.Context;
import android.util.AttributeSet;

import com.hong.mylivepusher.egl.HEGLSurfaceView;

public class HYuvView extends HEGLSurfaceView {

    private HYuvRender hYuvRender;

    public HYuvView(Context context) {
        this(context, null);
    }

    public HYuvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HYuvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hYuvRender = new HYuvRender(context);
        setRender(hYuvRender);
        setRenderMode(HEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv){
        if(hYuvRender != null){
            hYuvRender.setFrameData(w, h, by, bu, bv);
            requestRender();
        }
    }



}
