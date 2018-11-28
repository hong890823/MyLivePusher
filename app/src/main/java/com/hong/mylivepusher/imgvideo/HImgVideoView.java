package com.hong.mylivepusher.imgvideo;

import android.content.Context;
import android.util.AttributeSet;

import com.hong.mylivepusher.egl.HEGLSurfaceView;

public class HImgVideoView extends HEGLSurfaceView {

    private HImgVideoRender hImgVideoRender;
    private int fboTextureId;

    public HImgVideoView(Context context) {
        this(context, null);
    }

    public HImgVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HImgVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hImgVideoRender = new HImgVideoRender(context);
        setRender(hImgVideoRender);
        setRenderMode(HEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        hImgVideoRender.setOnRenderCreateListener(new HImgVideoRender.OnRenderCreateListener() {
            @Override
            public void onCreate(int textId) {
                fboTextureId = textId;
            }
        });
    }

    public void setCurrentImg(int imgsr){
        if(hImgVideoRender != null){
            hImgVideoRender.setCurrentImgSrc(imgsr);
            requestRender();
        }
    }

    public int getFboTextureId() {
        return fboTextureId;
    }
}
