package com.hong.mylivepusher;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hong.mylivepusher.camera.HCameraView;
import com.hong.mylivepusher.encodec.HBaseMediaEncoder;
import com.hong.mylivepusher.encodec.HMediaEncodec;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class VideoActivity extends AppCompatActivity{
    private static final String TAG = "VideoActivity";

    private HCameraView hCameraView;
    private Button btnRecord;

    private HMediaEncodec hMediaEncodec;

    private WlMusic hMusicPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        hCameraView = findViewById(R.id.camera_view);
        btnRecord = findViewById(R.id.btn_record);

        hMusicPlayer = WlMusic.getInstance();
        hMusicPlayer.setCallBackPcmData(true);
        hMusicPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                hMusicPlayer.playCutAudio(75,105);
            }
        });

        hMusicPlayer.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                if(hMediaEncodec != null){
                    hMediaEncodec.stopRecord();
                    hMediaEncodec = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnRecord.setText("开始录制");
                        }
                    });
                }
            }
        });

        hMusicPlayer.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int sampleRate, int bit, int channels) {
                Log.d("Hong", "textureId is " + hCameraView.getTextureId());
                hMediaEncodec = new HMediaEncodec(VideoActivity.this, hCameraView.getTextureId());
                hMediaEncodec.initEncodec(hCameraView.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_live_pusher.mp4",720, 1280, sampleRate, channels);
                hMediaEncodec.setOnMediaInfoListener(new HBaseMediaEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        Log.d("Hong", "time is : " + times);
                    }
                });
                hMediaEncodec.startRecord();
            }

            @Override
            public void onPcmData(byte[] pcmData, int size,long clock) {
                if(hMediaEncodec != null){
                    hMediaEncodec.putPCMData(pcmData, size);
                }
            }
        });
    }

    /**
     * 只有视频，没有音频的时候的录制方法
     * */
    public void record_(View view) {
        if(hMediaEncodec == null){
            Log.d(TAG, "textureId is " + hCameraView.getTextureId());
            hMediaEncodec = new HMediaEncodec(this, hCameraView.getTextureId());
            hMediaEncodec.initEncodec(hCameraView.getEglContext(),
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_live_pusher.mp4",
                    720, 1280,
                    44100,2);
            hMediaEncodec.setOnMediaInfoListener(new HBaseMediaEncoder.OnMediaInfoListener() {
                @Override
                public void onMediaTime(int times) {
                    Log.d(TAG, "time is : " + times);
                }
            });

            hMediaEncodec.startRecord();
            btnRecord.setText("正在录制");
        }else{
            hMediaEncodec.stopRecord();
            btnRecord.setText("开始录制");
            hMediaEncodec = null;
        }

    }

    public void record(View view) {
        if(hMediaEncodec == null){
            hMusicPlayer.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/美丽的神话.flac");
            hMusicPlayer.prePared();
            btnRecord.setText("正在录制");
        }else{
            hMediaEncodec.stopRecord();
            btnRecord.setText("开始录制");
            hMediaEncodec = null;
            hMusicPlayer.stop();
        }
    }


}
