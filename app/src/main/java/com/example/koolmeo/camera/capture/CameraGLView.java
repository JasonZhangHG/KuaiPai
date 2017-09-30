package com.example.koolmeo.camera.capture;
/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: CameraGLView.java
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

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.example.koolmeo.camera.encoder.VideoEncoder;
import com.example.koolmeo.camera.glutils.CameraFile;
import com.example.koolmeo.camera.glutils.GLFilter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Sub class of GLSurfaceView to display camera preview and write video frame to capturing surface
 */
public final class CameraGLView extends GLSurfaceView {

	private static final boolean DEBUG = false; // TODO set false on release
	private static final String TAG = "CameraGLView";

	public static  int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;

	private static final int SCALE_STRETCH_FIT = 0;
	private static final int SCALE_KEEP_ASPECT_VIEWPORT = 1;
	private static final int SCALE_KEEP_ASPECT = 2;
	private static final int SCALE_CROP_CENTER = 3;

	private final CameraSurfaceRenderer mRenderer;
	private boolean mHasSurface;
	private int mVideoWidth, mVideoHeight;
	private int mRotation;
	private int mScaleMode = SCALE_STRETCH_FIT;

	private CameraThread ct=null;

	public void setFlag(int flag) {
		if(mRenderer!=null){
			mRenderer.setFlag(flag);
		}
	}

	public CameraGLView(final Context context) {
		this(context, null, 0);
	}

	public CameraGLView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CameraGLView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs);
		if (DEBUG) Log.v(TAG, "CameraGLView:");
		ct=new CameraThread(this);
		mRenderer = new CameraSurfaceRenderer(this);
		setEGLContextClientVersion(2);	// GLES 2.0, API >= 8
		setRenderer(mRenderer);
	}

	@Override
	public void onResume() {
		if (DEBUG) Log.v(TAG, "onResume:");
		super.onResume();
//		if (mHasSurface) {
//			ct.startPreview(getWidth(),getHeight());
//		}
	}

	@Override
	public void onPause() {
		if (DEBUG) Log.v(TAG, "onPause:");
		super.onPause();
		//ct.stopPreview();
	}

	public void setScaleMode(final int mode) {
		if (mScaleMode != mode) {
			mScaleMode = mode;
			queueEvent(new Runnable() {
				@Override
				public void run() {
					mRenderer.updateViewport();
				}
			});
		}
	}

	public int getScaleMode() {
		return mScaleMode;
	}

	public void setVideoSize(final int width, final int height) {
		if ((mRotation % 180) == 0) {
			mVideoWidth = width;
			mVideoHeight = height;
		} else {
			mVideoWidth = height;
			mVideoHeight = width;
		}
		queueEvent(new Runnable() {
			@Override
			public void run() {
				mRenderer.updateViewport();
			}
		});
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public SurfaceTexture getSurfaceTexture() {
		if (DEBUG) Log.v(TAG, "getSurfaceTexture:");
		return mRenderer != null ? mRenderer.mSTexture : null;
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		if (DEBUG) Log.v(TAG, "surfaceDestroyed:");
		super.surfaceDestroyed(holder);
		//ct.stopPreview();
		mHasSurface = false;
		mRenderer.onSurfaceDestroyed();

	}

	public void setVideoEncoder(final VideoEncoder encoder) {
		if (DEBUG) Log.v(TAG, "setVideoEncoder:tex_id=" + mRenderer.hTex + ",encoder=" + encoder);
		queueEvent(new Runnable() {
			@Override
			public void run() {
				synchronized (mRenderer) {
					if (encoder != null) {
						encoder.setEglContext(EGL14.eglGetCurrentContext(), mRenderer.hTex);
					}
					mRenderer.mVideoEncoder = encoder;
				}
			}
		});
	}

//********************************************************************************

	public synchronized void setPreviewFlash(int flag){
		ct.setCameraFlash(flag);
	}
	public void changeCamera(int id){
		CAMERA_ID=id;
		ct.startPreview(1280,720);

	}

	public static Camera_Focus cf=null;

	public synchronized void setPreviewFocus(float x,float y,Camera_Focus cfi){
		ct.focusOnTouch(x,y);
		cf=cfi;
	}

	public interface Camera_Focus{
		public void onFocus();
	}

	/**
	 * GLSurfaceViewのRenderer
	 */
	private  final class CameraSurfaceRenderer
			implements GLSurfaceView.Renderer,
			SurfaceTexture.OnFrameAvailableListener {	// API >= 11

		private final WeakReference<CameraGLView> mWeakParent;
		private SurfaceTexture mSTexture;	// API >= 11
		private int hTex;
		private GLFilter mDrawer;
		private final float[] mStMatrix = new float[16];
		private final float[] mMvpMatrix = new float[16];
		private VideoEncoder mVideoEncoder;

		public CameraSurfaceRenderer(final CameraGLView parent) {
			if (DEBUG) Log.v(TAG, "CameraSurfaceRenderer:");
			mWeakParent = new WeakReference<CameraGLView>(parent);
			Matrix.setIdentityM(mMvpMatrix, 0);
		}

		public void setFlag(int flag){
			if(mDrawer!=null)
				mDrawer.setFlag(flag);
		}

		@Override
		public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
			if (DEBUG) Log.v(TAG, "onSurfaceCreated:");
			// This renderer required OES_EGL_image_external extension
			final String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);	// API >= 8
//			if (DEBUG) Log.i(TAG, "onSurfaceCreated:Gl extensions: " + extensions);
			if (!extensions.contains("OES_EGL_image_external"))
				throw new RuntimeException("This system does not support OES_EGL_image_external.");
			// create textur ID
			hTex = GLFilter.initTex();
			// create SurfaceTexture with texture ID.
			mSTexture = new SurfaceTexture(hTex);
			mSTexture.setOnFrameAvailableListener(this);
			// clear screen with yellow color so that you can see rendering rectangle
			GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			final CameraGLView parent = mWeakParent.get();
			if (parent != null) {
				parent.mHasSurface = true;
			}
			// create object for preview display
			mDrawer = new GLFilter();
			mDrawer.setMatrix(mMvpMatrix, 0);
			//mDrawer.setFlag(0);
			CameraFile.filterindex=0;
		}

		@Override
		public void onSurfaceChanged(final GL10 unused, final int width, final int height) {
			if (DEBUG) Log.v(TAG, String.format("onSurfaceChanged:(%d,%d)", width, height));
			// if at least with or height is zero, initialization of this view is still progress.
			if ((width == 0) || (height == 0)) return;
			updateViewport();
			CameraFile.bitmapw=width;
			CameraFile.bitmaph=height;
			final CameraGLView parent = mWeakParent.get();
			if(parent!=null)
				ct.startPreview(1280,720);
		}

		/**
		 * when GLSurface context is soon destroyed
		 */
		public void onSurfaceDestroyed() {
			if (DEBUG) Log.v(TAG, "onSurfaceDestroyed:");
			ct.stopPreview();
			if (mDrawer != null) {
				mDrawer.release();
				mDrawer = null;
			}
			if (mSTexture != null) {
				mSTexture.release();
				mSTexture = null;
			}
			GLFilter.deleteTex();
		}

		private final void updateViewport() {
			final CameraGLView parent = mWeakParent.get();
			if (parent != null) {
				final int view_width = parent.getWidth();
				final int view_height = parent.getHeight();
				GLES20.glViewport(0, 0, view_width, view_height);
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
				final double video_width = parent.mVideoWidth;
				final double video_height = parent.mVideoHeight;
				if (video_width == 0 || video_height == 0) return;
				Matrix.setIdentityM(mMvpMatrix, 0);
				final double view_aspect = view_width / (double)view_height;
				Log.i(TAG, String.format("view(%d,%d)%f,video(%1.0f,%1.0f)", view_width, view_height, view_aspect, video_width, video_height));
				switch (parent.mScaleMode) {
					case SCALE_STRETCH_FIT:
						break;
					case SCALE_KEEP_ASPECT_VIEWPORT:
					{
						final double req = video_width / video_height;
						int x, y;
						int width, height;
						if (view_aspect > req) {
							// if view is wider than camera image, calc width of drawing area based on view height
							y = 0;
							height = view_height;
							width = (int)(req * view_height);
							x = (view_width - width) / 2;
						} else {
							// if view is higher than camera image, calc height of drawing area based on view width
							x = 0;
							width = view_width;
							height = (int)(view_width / req);
							y = (view_height - height) / 2;
						}
						// set viewport to draw keeping aspect ration of camera image
						if (DEBUG) Log.v(TAG, String.format("xy(%d,%d),size(%d,%d)", x, y, width, height));
						GLES20.glViewport(x, y, width, height);
						break;
					}
					case SCALE_KEEP_ASPECT:
					case SCALE_CROP_CENTER:
					{
						final double scale_x = view_width / video_width;
						final double scale_y = view_height / video_height;
						final double scale = (parent.mScaleMode == SCALE_CROP_CENTER
								? Math.max(scale_x,  scale_y) : Math.min(scale_x, scale_y));
						final double width = scale * video_width;
						final double height = scale * video_height;
						Log.v(TAG, String.format("size(%1.0f,%1.0f),scale(%f,%f),mat(%f,%f)",
								width, height, scale_x, scale_y, width / view_width, height / view_height));
						Matrix.scaleM(mMvpMatrix, 0, (float)(width / view_width), (float)(height / view_height), 1.0f);
						break;
					}
				}
				if (mDrawer != null)
					mDrawer.setMatrix(mMvpMatrix, 0);
			}
		}

		private volatile boolean requesrUpdateTex = false;
		private boolean flip = true;
		/**
		 * drawing to GLSurface
		 * we set renderMode to GLSurfaceView.RENDERMODE_WHEN_DIRTY,
		 * this method is only called when #requestRender is called(= when texture is required to update)
		 * if you don't set RENDERMODE_WHEN_DIRTY, this method is called at maximum 60fps
		 */
		@Override
		public void onDrawFrame(final GL10 unused) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			if (requesrUpdateTex) {
				requesrUpdateTex = false;
				mSTexture.updateTexImage();
				mSTexture.getTransformMatrix(mStMatrix);
				Log.i("kkk","update");
			}
			Log.i("kkk","draw");
			// draw to preview screen
			mDrawer.draw(hTex, mStMatrix,2.0f/CameraFile.bitmapw,2.0f/CameraFile.bitmaph);
			flip = !flip;
			if (flip) {	// ~30fps
				synchronized (this) {
					if (mVideoEncoder != null) {
						mVideoEncoder.frameAvailableSoon(mStMatrix, mMvpMatrix);
					}
				}
			}
		}

		@Override
		public void onFrameAvailable(final SurfaceTexture st) {
			requesrUpdateTex = true;
			Log.i("kkk","avaible");
//			final CameraGLView parent = mWeakParent.get();
//			if (parent != null)
//				parent.requestRender();
		}
	}

	/**
	 * Thread for asynchronous operation of camera preview
	 */
	private final class CameraThread  {
		private final Object mReadyFence = new Object();
		private final WeakReference<CameraGLView> mWeakParent;
		private volatile boolean mIsRunning = false;
		private Camera mCamera=null;
		private boolean mIsFrontFace;
		private static  final int FOCUS_AREA_SIZE= 500;

		public CameraThread(final CameraGLView parent) {
			mWeakParent = new WeakReference<CameraGLView>(parent);
		}

		private Rect calculateFocusArea(float x, float y) {
			final CameraGLView parent = mWeakParent.get();
			int left = clamp(Float.valueOf((x / parent.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
			int top = clamp(Float.valueOf((y / parent.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
			return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
		}

		private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
			int result;
			if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
				if (touchCoordinateInCameraReper>0){
					result = 1000 - focusAreaSize/2;
				} else {
					result = -1000 + focusAreaSize/2;
				}
			} else{
				result = touchCoordinateInCameraReper - focusAreaSize/2;
			}
			return result;
		}

		public void focusOnTouch(float x,float y) {
			if (mCamera != null ) {
				Camera.Parameters parameters = mCamera.getParameters();
				if (parameters.getMaxNumMeteringAreas() > 0){
					Rect rect = calculateFocusArea(x,y);

					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
					meteringAreas.add(new Camera.Area(rect, 800));
					parameters.setFocusAreas(meteringAreas);

					mCamera.setParameters(parameters);
					mCamera.autoFocus(mAutoFocusTakePictureCallback);
				}else {
					mCamera.autoFocus(mAutoFocusTakePictureCallback);
				}
			}
		}

		private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					if(cf!=null){
						cf.onFocus();
					}
				} else {
					if(cf!=null){
						cf.onFocus();
					}
				}
			}
		};

		/**
		 * start camera preview
		 * @param width
		 * @param height
		 */
		private final void startPreview(final int width, final int height) {
			if (DEBUG) Log.v(TAG, "startPreview:");
			final CameraGLView parent = mWeakParent.get();
			if ((parent != null)) {
				try {
					CameraFile.camerastaus=1;
					transepre();
					mCamera = Camera.open(CAMERA_ID);
					if(mCamera==null)
						return;
					final Camera.Parameters params = mCamera.getParameters();
					final List<String> focusModes = params.getSupportedFocusModes();
					if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
						params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
					} else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
						params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					} else {
						if (DEBUG) Log.i(TAG, "Camera does not support autofocus");
					}
					// let's try fastest frame rate. You will get near 60fps, but your device become hot.
					final List<int[]> supportedFpsRange = params.getSupportedPreviewFpsRange();
					final int[] max_fps = supportedFpsRange.get(supportedFpsRange.size() - 1);
					Log.i(TAG, String.format("fps:%d-%d", max_fps[0], max_fps[1]));
					params.setPreviewFpsRange(max_fps[0], max_fps[1]);
					params.setRecordingHint(true);
					// request closest supported preview size
					final Camera.Size closestSize = getClosestSupportedSize(
							params.getSupportedPreviewSizes(), width, height);
					params.setPreviewSize(closestSize.width, closestSize.height);
					// request closest picture size for an aspect ratio issue on Nexus7
					final Camera.Size pictureSize = getClosestSupportedSize(
							params.getSupportedPictureSizes(), width, height);
					params.setPictureSize(pictureSize.width, pictureSize.height);
					setRotation(params);

					mCamera.setParameters(params);
					// get the actual preview size
					final Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
					Log.i(TAG, String.format("previewSize(%d, %d)", previewSize.width, previewSize.height));
					// adjust view size with keeping the aspect ration of camera preview.
					// here is not a UI thread and we should request parent view to execute.
					parent.post(new Runnable() {
						@Override
						public void run() {
							parent.setVideoSize(previewSize.width, previewSize.height);
						}
					});
					final SurfaceTexture st = parent.getSurfaceTexture();
					st.setDefaultBufferSize(previewSize.width, previewSize.height);
					mCamera.setPreviewTexture(st);
					if (mCamera != null) {
						mCamera.startPreview();
					}
				} catch (final IOException e) {
					Log.e(TAG, "startPreview:", e);
					CameraFile.camerastaus=0;
					if (mCamera != null) {
						mCamera.release();
						mCamera = null;
					}
				} catch (final RuntimeException e) {
					Log.e(TAG, "startPreview:", e);
					CameraFile.camerastaus=0;
					if (mCamera != null) {
						mCamera.release();
						mCamera = null;
					}
				}
			}
		}

		private Camera.Size getClosestSupportedSize(List<Camera.Size> supportedSizes, final int requestedWidth, final int requestedHeight) {
			return (Camera.Size) Collections.min(supportedSizes, new Comparator<Camera.Size>() {

				private int diff(final Camera.Size size) {
					return Math.abs(requestedWidth - size.width) + Math.abs(requestedHeight - size.height);
				}

				@Override
				public int compare(final Camera.Size lhs, final Camera.Size rhs) {
					return diff(lhs) - diff(rhs);
				}
			});
		}

		/**
		 * set camera flash
		 * flag  1:open  other:close
		 */

		private void setCameraFlash(int flag){
			if(mCamera==null)
				return;
			if(flag==1){
				Camera.Parameters params = mCamera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(params);
			}else{
				Camera.Parameters params = mCamera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(params);
			}
			Log.i("ghlg","flash:"+flag);
		}

		private void transepre(){
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}

		/**
		 * stop camera preview
		 */
		private void stopPreview() {
			if (DEBUG) Log.v(TAG, "stopPreview:");
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}

		/**
		 * rotate preview screen according to the device orientation
		 * @param params
		 */
		private final void setRotation(final Camera.Parameters params) {
			if (DEBUG) Log.v(TAG, "setRotation:");
			final CameraGLView parent = mWeakParent.get();
			if (parent == null) return;

			final Display display = ((WindowManager)parent.getContext()
					.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			final int rotation = display.getRotation();
			int degrees = 0;
			switch (rotation) {
				case Surface.ROTATION_0: degrees = 0; break;
				case Surface.ROTATION_90: degrees = 90; break;
				case Surface.ROTATION_180: degrees = 180; break;
				case Surface.ROTATION_270: degrees = 270; break;
			}
			// get whether the camera is front camera or back camera
			final Camera.CameraInfo info =
					new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(CAMERA_ID, info);
			mIsFrontFace = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
			if (mIsFrontFace) {	// front camera
				degrees = (info.orientation + degrees) % 360;
				degrees = (360 - degrees) % 360;  // reverse
			} else {  // back camera
				degrees = (info.orientation - degrees + 360) % 360;
			}
			// apply rotation setting
			mCamera.setDisplayOrientation(degrees);
			parent.mRotation = degrees;
			// XXX This method fails to call and camera stops working on some devices.
//			params.setRotation(degrees);
		}

	}
}
