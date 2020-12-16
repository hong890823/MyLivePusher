package com.hong.mylivepusher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hong.mylivepusher.push.HPushVideo;

public class LivePushActivity extends AppCompatActivity{

    private HPushVideo hPushVideol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);
        hPushVideol = new HPushVideo();
    }

    public void startPush(View view){
        hPushVideol.initLivePush("rtmp://192.168.199.128/live");
    }

}
