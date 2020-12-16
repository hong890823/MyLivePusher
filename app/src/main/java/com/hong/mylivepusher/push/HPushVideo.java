package com.hong.mylivepusher.push;

import android.text.TextUtils;

public class HPushVideo {

    static {
        System.loadLibrary("hpush");
    }

    public void initLivePush(String url){
        if(TextUtils.isEmpty(url)){
            initPush(url);
        }
    }

    private native void initPush(String url);
}
