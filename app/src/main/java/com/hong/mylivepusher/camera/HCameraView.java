package com.hong.mylivepusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.hong.mylivepusher.egl.HEGLSurfaceView;

public class HCameraView extends HEGLSurfaceView {
    private static final String TAG = "HCameraView";

    private HCameraRender hCameraRender;
    private HCamera hCamera;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int textureId = -1;

    public HCameraView(Context context) {
        this(context, null);
    }

    public HCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hCameraRender = new HCameraRender(context);
        hCamera = new HCamera(context);
        setRender(hCameraRender);
        previewAngle(context);
        hCameraRender.setOnSurfaceCreateListener(new HCameraRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture,int textureId) {
                hCamera.initCamera(surfaceTexture, cameraId);
                HCameraView.this.textureId = textureId;
            }
        });
    }

    public void onDestroy(){
        if(hCamera != null){
            hCamera.stopPreview();
        }
    }

    public void previewAngle(Context context){
        int angle = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        hCameraRender.resetMatrix();
        switch (angle){
            case Surface.ROTATION_0:
                Log.d(TAG, "0");
                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    hCameraRender.setAngle(90, 0, 0, 1);
                    hCameraRender.setAngle(180, 1, 0, 0);
                }else{
                    hCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_90:
                Log.d(TAG, "90");
                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    hCameraRender.setAngle(180, 0, 0, 1);
                    hCameraRender.setAngle(180, 0, 1, 0);
                }else{
                    hCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                Log.d(TAG, "180");
                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    hCameraRender.setAngle(90f, 0.0f, 0f, 1f);
                    hCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }else{
                    hCameraRender.setAngle(-90, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                Log.d(TAG, "270");
                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    hCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }else{
                    hCameraRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }

    public int getTextureId(){
        if(hCameraRender!=null){
            return textureId;
        }
        return -1;
    }

}
