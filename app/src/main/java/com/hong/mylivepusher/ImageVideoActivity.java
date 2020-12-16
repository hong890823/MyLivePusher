package com.hong.mylivepusher;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hong.mylivepusher.encodec.HMediaEncodec;
import com.hong.mylivepusher.imgvideo.HImgVideoView;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class ImageVideoActivity extends AppCompatActivity{

    private HImgVideoView hImgVideoView;
    private HMediaEncodec hMediaEncodec;
    private WlMusic hMusic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video);
        hImgVideoView = findViewById(R.id.image_video_view);
        hImgVideoView.setCurrentImg(R.drawable.img_1);

        hMusic = WlMusic.getInstance();
        hMusic.setCallBackPcmData(true);

        hMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                hMusic.playCutAudio(0, 60);
            }
        });

        hMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                hMediaEncodec = new HMediaEncodec(ImageVideoActivity.this, hImgVideoView.getFboTextureId());
                hMediaEncodec.initEncodec(hImgVideoView.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_image_video.mp4",
                        720, 500, samplerate, channels);
                hMediaEncodec.startRecord();
                startImgs();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if(hMediaEncodec != null){
                    hMediaEncodec.putPCMData(pcmdata, size);
                }
            }
        });

    }

    public void start(View view) {
        hMusic.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/美丽的神话.flac");
        hMusic.prePared();

    }

    private void startImgs(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 1; i <= 257; i++){
                    int imgsrc = getResources().getIdentifier("img_" + i, "drawable", "com.hong.mylivepusher");
                    hImgVideoView.setCurrentImg(imgsrc);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(hMediaEncodec != null){
                    hMusic.stop();
                    hMediaEncodec.stopRecord();
                    hMediaEncodec = null;
                }
            }
        }).start();
    }

}
