package com.china.snapshot.camera.glutils;
/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: GLFilter1.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Helper class to draw to whole view using specific texture and texture matrix
 */
public class GLFilter0 extends Filter {
	private static final boolean DEBUG = true; // TODO set false on release
	private static final String TAG = "GLFilter";

	private static final String vss
		= "uniform mat4 uMVPMatrix;\n"
		+ "uniform mat4 uTexMatrix;\n"
		+ "attribute highp vec4 aPosition;\n"
		+ "attribute highp vec4 aTextureCoord;\n"
		+ "varying highp vec2 textureCoordinate;\n"
		+ "\n"
		+ "void main() {\n"
		+ "	gl_Position = uMVPMatrix * aPosition;\n"
		+ "	textureCoordinate = (uTexMatrix * aTextureCoord).xy;\n"
		+ "}\n";

	public static final String FF= "" +
			"#extension GL_OES_EGL_image_external : require\n"+
			"precision highp float;"+
			"uniform samplerExternalOES inputImageTexture;"+
			"uniform highp float mparam;" +
			"varying highp vec2 textureCoordinate;"+
			"uniform highp int flag;\n"+
			"const vec3 warmFilter = vec3(0.93, 0.54, 0.0);\n" +
			"const mat3 RGBtoYIQ = mat3(0.299, 0.587, 0.114, 0.596, -0.274, -0.322, 0.212, -0.523, 0.311);\n" +
			"const mat3 YIQtoRGB = mat3(1.0, 0.956, 0.621, 1.0, -0.272, -0.647, 1.0, -1.105, 1.702);\n" +
			"const mediump vec3 luminanceWeighting = vec3(0.0, 0.0, 1.0);\n" +
			"void main()"+
			"{"+
				"if(flag==0){"+
					"gl_FragColor = texture2D(inputImageTexture, textureCoordinate);}" +
				"else if(flag==3){"+
						"vec3 rgb = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
						"float d = distance(textureCoordinate, vec2(0.5, 0.5));\n" +
						"float percent = smoothstep(0.3, 0.8, d);\n" +
						"vec3 vignetteColor=vec3(0.2,0.2,0.2);\n"+
						"gl_FragColor = vec4(mix(rgb.x, vignetteColor.x, percent), mix(rgb.y, vignetteColor.y, percent), mix(rgb.z, vignetteColor.z, percent), 1.0);\n" +
					"}"+
				"else if(flag==2){"+
						"vec3 rgb = texture2D(inputImageTexture, textureCoordinate).rgb;\n" +
						"float d = distance(textureCoordinate, vec2(0.5, 0.5));\n" +
						"float percent = smoothstep(0.3, 0.8, d);\n" +
						"vec3 vignetteColor=vec3(0.8,0.8,0.8);\n"+
						"gl_FragColor = vec4(mix(rgb.x, vignetteColor.x, percent), mix(rgb.y, vignetteColor.y, percent), mix(rgb.z, vignetteColor.z, percent), 1.0);\n" +
					"}"+
				"else if(flag==1){"+
						"float saturation=1.8;\n" +
						"vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
						"float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
						"vec3 greyScaleColor = vec3(luminance);\n" +
						"gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
					"}"+
				"else if(flag==5){"+
						"float temperature=-0.3;\n" +
						"float tint=0.0;\n" +
						"vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
						"vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint\n" +
						"yiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);\n" +
						"vec3 rgb = YIQtoRGB * yiq;\n" +
						"vec3 processed = vec3(\n" +
						"(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature\n" +
						"(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), \n" +
						"(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));\n" +
						"gl_FragColor = vec4(mix(rgb, processed, temperature), source.a);\n" +
					"}"+
				"else if(flag==4){"+
						"float temperature=0.3;\n" +
						"float tint=0.0;\n" +
						"vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
						"vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint\n" +
						"yiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);\n" +
						"vec3 rgb = YIQtoRGB * yiq;\n" +
						"vec3 processed = vec3(\n" +
						"(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature\n" +
						"(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), \n" +
						"(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));\n" +
						"gl_FragColor = vec4(mix(rgb, processed, temperature), source.a);\n" +
					"}"+
				"else if(flag==6){"+
						"float temperature=0.3;\n" +
						"float tint=0.5;\n" +
						"vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
						"vec3 yiq = RGBtoYIQ * source.rgb; //adjusting tint\n" +
						"yiq.b = clamp(yiq.b + tint*0.5226*0.1, -0.5226, 0.5226);\n" +
						"vec3 rgb = YIQtoRGB * yiq;\n" +
						"vec3 processed = vec3(\n" +
						"(rgb.r < 0.5 ? (2.0 * rgb.r * warmFilter.r) : (1.0 - 2.0 * (1.0 - rgb.r) * (1.0 - warmFilter.r))), //adjusting temperature\n" +
						"(rgb.g < 0.5 ? (2.0 * rgb.g * warmFilter.g) : (1.0 - 2.0 * (1.0 - rgb.g) * (1.0 - warmFilter.g))), \n" +
						"(rgb.b < 0.5 ? (2.0 * rgb.b * warmFilter.b) : (1.0 - 2.0 * (1.0 - rgb.b) * (1.0 - warmFilter.b))));\n" +
						"gl_FragColor = vec4(mix(rgb, processed, temperature), source.a);\n" +
					"}"+
				"else if(flag==7){"+
						"vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
						"float gamma=1.2;\n"+
						"vec3 vt=vec3(textureColor.r,textureColor.r,textureColor.r);\n"+
						"gl_FragColor = vec4(pow(vt, vec3(gamma)), textureColor.w);\n" +
					"}"+
			"}";



	private final FloatBuffer pVertex;
	private final FloatBuffer pTexCoord;
	private int hProgram;
	private int hProgram_beau;
	private int flagloc;
	private int singleStepOffsetloc;
    int maPositionLoc;
    int maTextureCoordLoc;
    int muMVPMatrixLoc;
    int muTexMatrixLoc;
	private int flag=0;
	private final float[] mMvpMatrix = new float[16];

	private static final int FLOAT_SZ = Float.SIZE / 8;
	private static final int VERTEX_NUM = 4;
	private static final int VERTEX_SZ = VERTEX_NUM * 2;

	private int uniformt[];
	private int bmpsize;

	public  void setFlag(int flag1){
		flag=flag1;
	}
	/**
	 * Constructor
	 * this should be called in GL context
	 */
	public GLFilter0() {
		pVertex = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		pVertex.put(VERTICES);
		pVertex.flip();
		pTexCoord = ByteBuffer.allocateDirect(VERTEX_SZ * FLOAT_SZ)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		pTexCoord.put(TEXCOORD);
		pTexCoord.flip();

		hProgram = loadShader(vss, FF);
		GLES20.glUseProgram(hProgram);
        maPositionLoc = GLES20.glGetAttribLocation(hProgram, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(hProgram, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(hProgram, "uTexMatrix");
		flagloc = GLES20.glGetUniformLocation(hProgram, "flag");


		Matrix.setIdentityM(mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mMvpMatrix, 0);
		GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, pVertex);
		GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, pTexCoord);
		GLES20.glEnableVertexAttribArray(maPositionLoc);
		GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
	}

	/**
	 * terminatinng, this should be called in GL context
	 */
	public void release() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		if (hProgram >= 0)
			GLES20.glDeleteProgram(hProgram);
		hProgram = -1;
	}

	/**
	 * draw specific texture with specific texture matrix
	 * @param tex_id texture ID
	 * @param tex_matrix texture matrix、if this is null, the last one use(we don't check size of this array and needs at least 16 of float)
	 */
	public void draw(final int tex_id, final float[] tex_matrix,float singlestep1,float singlestep2) {
		if(hProgram>=0){
			GLES20.glUseProgram(hProgram);
			GLES20.glUniform1i(flagloc,flag);
			if (tex_matrix != null)
				GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, tex_matrix, 0);
			GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mMvpMatrix, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex_id);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_NUM);
			GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
			GLES20.glUseProgram(0);
		}
	}

	/**
	 * Set model/view/projection transform matrix
	 * @param matrix
	 * @param offset
	 */
	public void setMatrix(final float[] matrix, final int offset) {
		if ((matrix != null) && (matrix.length >= offset + 16)) {
			System.arraycopy(matrix, offset, mMvpMatrix, 0, 16);
		} else {
			Matrix.setIdentityM(mMvpMatrix, 0);
		}
	}


	/**
	 * load, compile and link shader
	 * @param vss source of vertex shader
	 * @param fss source of fragment shader
	 * @return
	 */
	public static int loadShader(final String vss, final String fss) {
		if (DEBUG) Log.v(TAG, "loadShader:");
		int vs = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vs, vss);
		GLES20.glCompileShader(vs);
		final int[] compiled = new int[1];
		GLES20.glGetShaderiv(vs, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			if (DEBUG) Log.e(TAG, "Failed to compile vertex shader:"
					+ GLES20.glGetShaderInfoLog(vs));
			GLES20.glDeleteShader(vs);
			vs = 0;
		}

		int fs = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fs, fss);
		GLES20.glCompileShader(fs);
		GLES20.glGetShaderiv(fs, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			if (DEBUG) Log.w(TAG, "Failed to compile fragment shader:"
				+ GLES20.glGetShaderInfoLog(fs));
			GLES20.glDeleteShader(fs);
			fs = 0;
		}

		final int program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vs);
		GLES20.glAttachShader(program, fs);
		GLES20.glLinkProgram(program);
		return program;
	}

}
