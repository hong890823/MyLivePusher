package com.hong.mylivepusher.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

public abstract class HEGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Surface surface;
    private EGLContext eglContext;

    private HEGLThread eglThread;
    private HGLRender glRender;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    public HEGLSurfaceView(Context context) {
        this(context, null);
    }

    public HEGLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HEGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void setRender(HGLRender glRender) {
        this.glRender = glRender;
    }

    public void setRenderMode(int mRenderMode) {
        if(glRender == null){
            throw  new RuntimeException("must set render before");
        }
        this.mRenderMode = mRenderMode;
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext){
        this.surface = surface;
        this.eglContext = eglContext;
    }

    public EGLContext getEglContext(){
        if(eglThread != null){
            return eglThread.getEglContext();
        }
        return null;
    }

    public void requestRender(){
        if(eglThread != null){
            eglThread.requestRender();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(surface == null){
            surface = holder.getSurface();
        }
        eglThread = new HEGLThread(new WeakReference<>(this));
        eglThread.isCreate = true;
        eglThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        eglThread.width = width;
        eglThread.height = height;
        eglThread.isChange = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        eglThread.onDestory();
        eglThread = null;
        surface = null;
        eglContext = null;
    }

    public interface HGLRender{
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame();
    }


    static class HEGLThread extends Thread{
        final private Object object = new Object();
        private WeakReference<HEGLSurfaceView> eglSurfaceViewWeakReference;
        private EglHelper eglHelper = null;

        private boolean isExit = false;
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        private int width;
        private int height;

        HEGLThread(WeakReference<HEGLSurfaceView> eglSurfaceViewWeakReference) {
            this.eglSurfaceViewWeakReference = eglSurfaceViewWeakReference;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            isStart = false;
            eglHelper = new EglHelper();
            HEGLSurfaceView eglSurfaceView = eglSurfaceViewWeakReference.get();
            eglHelper.initEgl(eglSurfaceView.surface,eglSurfaceView.eglContext);

            while (true){
                if(isExit){
                    //释放资源
                    release();
                    break;
                }

                if(isStart){
                    if(eglSurfaceView.mRenderMode == RENDERMODE_WHEN_DIRTY){
                        synchronized (object){
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if(eglSurfaceView.mRenderMode == RENDERMODE_CONTINUOUSLY){
                        try {
                            Thread.sleep(1000 / 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        throw  new RuntimeException("mRenderMode is wrong value");
                    }
                }


                onCreate();
                onChange(width, height);
                onDraw();

                isStart = true;

            }

        }

        private void onCreate(){
            if(isCreate && eglSurfaceViewWeakReference.get().glRender != null){
                isCreate = false;
                eglSurfaceViewWeakReference.get().glRender.onSurfaceCreated();
            }
        }

        private void onChange(int width, int height){
            if(isChange && eglSurfaceViewWeakReference.get().glRender != null){
                isChange = false;
                eglSurfaceViewWeakReference.get().glRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw(){
            if(eglSurfaceViewWeakReference.get().glRender != null && eglHelper != null){
                eglSurfaceViewWeakReference.get().glRender.onDrawFrame();
                if(!isStart){//onDrawFrame最少执行两次才能画出图像
                    eglSurfaceViewWeakReference.get().glRender.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }

        private void requestRender(){
            synchronized (object){
                object.notifyAll();
            }
        }

        void onDestory(){
            isExit = true;
            requestRender();
        }


        void release(){
            if(eglHelper != null){
                eglHelper.destoryEgl();
                eglHelper = null;
                eglSurfaceViewWeakReference = null;
            }
        }

        EGLContext getEglContext(){
            if(eglHelper != null){
                return eglHelper.getmEglContext();
            }
            return null;
        }

    }


}
