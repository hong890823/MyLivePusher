package com.hong.mylivepusher.encodec;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.hong.mylivepusher.R;
import com.hong.mylivepusher.egl.HEGLSurfaceView;
import com.hong.mylivepusher.egl.HShaderUtil;
import com.hong.mylivepusher.util.WatermarkUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 用于录制的Render，这个Render是看不见的
 * */
public class HEncodecRender implements HEGLSurfaceView.HGLRender{

    private Context context;

    private float[] vertexData = {
            -1f, -1f,//左下
            1f, -1f,//右下
            -1f, 1f,//左上
            1f, 1f,//右上

            //水印
            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,//左下
            1f, 1f,//右下
            0f, 0f,//左上
            1f, 0f//右上
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int textureid;

    private int vboId;

    private Bitmap watermarkBitmap;
    private int watermarkTexureId;

    HEncodecRender(Context context, int textureid) {
        this.context = context;
        this.textureid = textureid;

        //水印
        watermarkBitmap = WatermarkUtil.createTextImage("我是水印",50,"#ff0000","#00000000",0);
        float r = 1.0f * watermarkBitmap.getWidth() / watermarkBitmap.getHeight();
        float w = r * 0.1f;//默认高度为0.1f

        vertexData[8] = 0.8f - w;
        vertexData[9] = -0.8f;
        //先确定的右下角
        vertexData[10] = 0.8f;
        vertexData[11] = -0.8f;

        vertexData[12] = 0.8f - w;
        vertexData[13] = -0.7f;

        vertexData[14] = 0.8f;
        vertexData[15] = -0.7f;



        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);

    }

    @Override
    public void onSurfaceCreated() {
        //开启ARGB的透明
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        String vertexSource = HShaderUtil.getRawResource(context, R.raw.vertex_shader_screen);
        String fragmentSource = HShaderUtil.getRawResource(context, R.raw.fragment_shader_screen);

        program = HShaderUtil.createProgram(vertexSource, fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");

        int [] vbos = new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        vboId = vbos[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20. GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        watermarkTexureId = WatermarkUtil.loadBitmapTexture(watermarkBitmap);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f,0f, 0f, 1f);

        GLES20.glUseProgram(program);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //绘制水印纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, watermarkTexureId);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                32);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
