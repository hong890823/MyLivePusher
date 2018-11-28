package com.hong.mylivepusher;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hong.mylivepusher.yuv.HYuvView;

import java.io.File;
import java.io.FileInputStream;

public class YuvActivity extends AppCompatActivity{

    private HYuvView hYuvView;

    private FileInputStream fis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        hYuvView = findViewById(R.id.yuv_view);
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int w = 640;
                    int h = 360;
//                    String path = "/mnt/shared/Other/sintel_640_360.yuv";
                    String path = Environment.getExternalStorageDirectory().getPath()+"/sintel_640_360.yuv";
                    fis = new FileInputStream(new File(path));
                    byte []y = new byte[w * h];
                    byte []u = new byte[w * h / 4];
                    byte []v = new byte[w * h / 4];

                    while (true){
                        int ry = fis.read(y);
                        int ru = fis.read(u);
                        int rv = fis.read(v);
                        if(ry > 0 && ru > 0 && rv > 0) {
                            hYuvView.setFrameData(w, h, y, u, v);
                            Thread.sleep(40);
                        } else {
                            Log.d("Hong", "完成");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
