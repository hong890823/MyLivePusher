package com.hong.mylivepusher.encodec;

import android.content.Context;

public class HMediaEncodec extends HBaseMediaEncoder{

    private HEncodecRender hEncodecRender;

    public HMediaEncodec(Context context, int textureId) {
        super(context);
        hEncodecRender = new HEncodecRender(context, textureId);
        setRender(hEncodecRender);
        setmRenderMode(HBaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
