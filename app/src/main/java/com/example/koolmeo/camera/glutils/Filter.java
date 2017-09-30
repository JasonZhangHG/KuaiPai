package com.example.koolmeo.camera.glutils;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class Filter {

    protected  float[] VERTICES = { 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f };
    protected  float[] TEXCOORD = { 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f };
    public void draw(final int tex_id, final float[] tex_matrix,float singlestep1,float singlestep2) {

    }

    public void setMatrix(final float[] matrix, final int offset) {
    }

    public Filter(){
    }

    protected   static int texid=-1;
    /**
     * create external texture
     * @return texture ID
     */
    public  static int initTex() {
            final int[] tex = new int[1];
            GLES20.glGenTextures(1, tex, 0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            texid=tex[0];
            return tex[0];
    }

    public  void setFlag(int flag1){

    }

    public void release(){

    }
    public static void deleteTex() {
        final int[] tex = new int[] {texid};
        GLES20.glDeleteTextures(1, tex, 0);
    }
}
