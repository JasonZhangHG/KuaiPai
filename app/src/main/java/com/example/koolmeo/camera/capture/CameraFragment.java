package com.example.koolmeo.camera.capture;
/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: CameraFragment.java
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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.example.koolmeo.R;
import com.example.koolmeo.camera.activities.ShareActivity;
import com.example.koolmeo.camera.custom.CameraConfig;
import com.example.koolmeo.camera.custom.CameraRecordTextView;
import com.example.koolmeo.camera.encoder.AudioEncoder;
import com.example.koolmeo.camera.encoder.MediaEncoder;
import com.example.koolmeo.camera.encoder.MuxerWrapper;
import com.example.koolmeo.camera.encoder.VideoEncoder;
import com.example.koolmeo.camera.glutils.CameraFile;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CameraFragment extends Fragment {
	private static final boolean DEBUG = false;	// TODO set false on release
	private static final String TAG = "CameraFragment";

	/**
	 * for camera preview display
	 */
	private CameraGLView mCameraView;
	/**
	 * button for start/stop recording
	 */
	private CameraRecordTextView capture;
	/**
	 * muxer for audio/video recording
	 */
	private MuxerWrapper mMuxer;

	public CameraFragment() {
		// need default constructor
	}

	private int flag=4;
	private int flash=0;
	private int camerid=0;
	private View rootView;
	private View tipStopRecord;
	private ImageView iv_beau;
	private ImageView iv_beau_filter;
	private ImageView buttonFlash;
	private ImageView switchCamera;
	private ImageView rl_focus;
	private ImageView buttonMenu;
	private ImageView buttonHome;
	private Bitmap bmppause;
	private Bitmap bmpnormal;
	private ImageView iv_record_del;
	private ArrayList<String> filelist=new ArrayList<String>();
	private String filename;
	private String parent;

	boolean recording = false;
	boolean isShowTip;
	Handler handler;
	LinearLayout layout_camera_toptitle;
	private RelativeLayout rlmergeshow;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	AlertDialog ad;
	public void showDialog(Context context) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.camera_droplayout,null);
		view.findViewById(R.id.camera_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
				if(ad!=null)
					ad.dismiss();
			}
		});
		view.findViewById(R.id.camera_cancal).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ad!=null)
					ad.dismiss();
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		ad=builder.show();
	}


	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.camera_drawerlayout, container, false);
		mCameraView = (CameraGLView)rootView.findViewById(R.id.cameraView);
		mCameraView.setVideoSize(1280, 720);
		mCameraView.setOnTouchListener(otl);
		capture = (CameraRecordTextView)rootView.findViewById(R.id.button_capture);
		capture.setOnClickListener(mOnClickListener);

		buttonMenu=(ImageView)rootView.findViewById(R.id.buttonMenu);
		buttonMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(getActivity());
			}
		});

		switchCamera=(ImageView)rootView.findViewById(R.id.button_ChangeCamera);
		switchCamera.setOnClickListener(mOnClickListener);

		rl_focus=(ImageView)rootView.findViewById(R.id.camera_rl_focus);

		buttonFlash=(ImageView)rootView.findViewById(R.id.buttonFlash);
		buttonFlash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(CameraFile.camerastaus==0){
					showToast(R.string.camera_permission);
					return;
				}
				if(mCameraView!=null){
					if(flash==0) {
						flash = 1;
						buttonFlash.setImageResource(R.drawable.camera_icon_lights_off);
					}
					else {
						flash = 0;
						buttonFlash.setImageResource(R.drawable.camera_icon_lights_on);
					}
					mCameraView.setPreviewFlash(flash);
				}
			}
		});

		iv_beau=(ImageView) rootView.findViewById(R.id.buttonFilter);

		iv_beau.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(CameraFile.camerastaus==0){
					showToast(R.string.camera_permission);
					return;
				}
				flag++;
				switchFilter(1);
			}
		});

		iv_beau_filter=(ImageView) rootView.findViewById(R.id.buttonBeau);
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what){
					case 1://持续录制，更新UI
						if(capture!=null){
							if(capture.getAngle()>360){
								buttonHome.setImageAlpha(255);
								stopRecording(true);
								finishtake();
								return;
							}
							capture.updateUi(bmppause);
						}
						break;
					case 2://停止录制
						if(capture!=null){
							capture.stopRecord();
						}
						break;
					case 4:
						showToast(R.string.camera_takevideo);
						break;

					case 5://mergevideo预览
						rlmergeshow.setVisibility(View.GONE);
						Bitmap bmp=CameraFile.getVideoThumbnail(video_path);
						if(bmp!=null){
							try {
								FileOutputStream fos=new FileOutputStream(pic_path);
								bmp.compress(Bitmap.CompressFormat.JPEG,100,fos);
								fos.flush();
								fos.close();
								bmp.recycle();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						Intent intent=new Intent(getActivity(), ShareActivity.class);
						intent.putExtra(ShareActivity.KEY_FILE_PATH, video_path);
						intent.putExtra(ShareActivity.KEY_PICTURE_PATH,pic_path);
						getActivity().startActivity(intent);
						break;
					case 6:
						if(rl_focus!=null)
							rl_focus.setVisibility(View.GONE);
						break;
					case 7:
						if(rl_focus!=null)
							rl_focus.setVisibility(View.GONE);
						Record();
						break;
				}
			}
		};
		bmppause= BitmapFactory.decodeResource(this.getResources(),R.drawable.camera_icon_pause);
		bmpnormal= BitmapFactory.decodeResource(this.getResources(),R.drawable.camera_icon_noral);
		capture.setAngle(0.0f);
		capture.setStep(360.0f/(CameraConfig.MAXRCORDTIME_SECOND/40.0f));
		capture.setPausebitmap(null);
		capture.setNormalbitmap(bmpnormal);
		capture.invalidate();
		layout_camera_toptitle=(LinearLayout)rootView.findViewById(R.id.camera_toptitle);

		iv_record_del=(ImageView)rootView.findViewById(R.id.btn_record_del);
		iv_record_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(recording ==true){
					showToast(R.string.camera_record);
					return;
				}
				int recordsize=filelist.size();
				if(recordsize>0){
					String fpath=filelist.get(recordsize-1);
					File file=new File(fpath);
					if(file.exists())
						file.delete();
					filelist.remove(recordsize-1);
					capture.delRecord();
					if(recordsize==1){
						iv_record_del.setVisibility(View.GONE);
						//buttonHome.setVisibility(View.GONE);
						buttonHome.setImageAlpha(70);
					}
				}
			}
		});

		rlmergeshow=(RelativeLayout)rootView.findViewById(R.id.camera_mergevideo_showprogress);
		buttonHome=(ImageView) rootView.findViewById(R.id.buttonHome);
		buttonHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishtake();
			}
		});
		buttonHome.setImageAlpha(70);

		CameraFile.audiostatus=1;
		return rootView;
	}

	public void finishtake(){
		final int filesizt=filelist.size();
		if(filesizt<=0){
			handler.sendEmptyMessage(4);
			return;
		}else{
			if(rlmergeshow.getVisibility()== View.GONE){
				rlmergeshow.setVisibility(View.VISIBLE);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						int fsize=filelist.size();
						ArrayList<String> aryt=new ArrayList<String>();
						aryt.clear();
						for(int i=0;i<fsize;i++){
							String fp=filelist.get(i);
							File file=new File(fp);
							if(file!=null && file.exists() && file.length()>1){
								aryt.add(fp);
							}
						}
						mergevideo(aryt);
						handler.sendEmptyMessage(5);
					}
				},3000);
			}

		}
	}

	public void startUpdateRecordUi(){
		Log.i(CameraConfig.TAG,"start");
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(recording ==true){
					handler.sendEmptyMessage(1);
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Log.i(CameraConfig.TAG,"stop");
			}
		}).start();

	}

	private String video_path=null;
	private long videotime;
	private String pic_path=null;

	public void mergevideo(ArrayList<String> fileList){
		List<Movie> moviesList = new LinkedList<Movie>();
		try
		{
			for (String file : fileList)
			{
				moviesList.add(MovieCreator.build(file));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();
		for (Movie m : moviesList)
		{
			for (Track t : m.getTracks())
			{
				if (t.getHandler().equals("soun"))
				{
					audioTracks.add(t);
				}
				if (t.getHandler().equals("vide"))
				{
					videoTracks.add(t);
				}
			}
		}

		Movie result = new Movie();

		try
		{
			if (audioTracks.size() > 0)
			{
				result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
			}
			if (videoTracks.size() > 0)
			{
				result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Container out = new DefaultMp4Builder().build(result);

		try
		{
			parent= CameraFile.getVideoPath();
			videotime= System.currentTimeMillis();
			String time= String.valueOf(videotime);
			video_path= parent+time+".mp4";
			pic_path=parent+time+".jpg";
			FileChannel fc = new RandomAccessFile(video_path, "rw").getChannel();
			out.writeContainer(fc);
			fc.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		moviesList.clear();
	}

	@Override
	public void onResume() {
		super.onResume();
		CameraFile.camerastaus=0;
		if (DEBUG) Log.v(TAG, "onResume:");
		CameraGLView.CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_BACK;
		camerid = CameraGLView.CAMERA_ID;
		mCameraView.onResume();
		capture.invalidate();
		selshow();
		buttonFlash.setImageResource(R.drawable.camera_icon_lights_on);
		flash=0;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (DEBUG) Log.v(TAG, "onPause:");
		stopRecording(true);
		mCameraView.onPause();
	}

	private final View.OnTouchListener otl=new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mDetector.onTouchEvent(event);
			return true;
		}
	};
	/**
	 * method when touch record button
	 */

	public void Foncus(final int msg){
		float cx=mCameraView.getWidth()/2;
		float cy=mCameraView.getHeight()/2;
		if(camerid== Camera.CameraInfo.CAMERA_FACING_BACK ) {
			rl_focus.setVisibility(View.VISIBLE);
			CameraGLView.Camera_Focus cf=new CameraGLView.Camera_Focus() {
				@Override
				public void onFocus() {
					if((msg==7 && capture.getAngle()<360) || msg==6)
						handler.sendEmptyMessage(msg);
					else if(msg==7 && capture.getAngle()>=360)
						handler.sendEmptyMessage(6);
				}
			};
			mCameraView.setPreviewFocus(cx,cy,cf);
		}else{
			if(msg==7 && capture.getAngle()<360){
				handler.sendEmptyMessage(msg);
			}
		}
	}

	private final OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(final View view) {
			switch(view.getId()){
				case R.id.buttonMenu:
					getActivity().finish();
					break;
				case R.id.button_capture:
					startRecord();
					break;
				case R.id.button_ChangeCamera:
					if(CameraFile.camerastaus==0){
						showToast(R.string.camera_permission);
						return;
					}
					if(mCameraView!=null){
						if(camerid== Camera.CameraInfo.CAMERA_FACING_BACK) {
							camerid = Camera.CameraInfo.CAMERA_FACING_FRONT;
						}else {
							camerid = Camera.CameraInfo.CAMERA_FACING_BACK;
						}
						selshow();
						buttonFlash.setImageResource(R.drawable.camera_icon_lights_on);
						flash=0;
						mCameraView.changeCamera(camerid);
					}
					break;
			}
		}
	};

	private void startRecord(){
		if(CameraFile.camerastaus==0){
			showToast(R.string.camera_permission);
			return;
		}
		if(CameraFile.audiostatus==0){
			showToast(R.string.audio_permission);
		}
		if (recording) {
			if(!stopRecording(false))
				return;
		} else {
			if(capture.getAngle()<360)
				Foncus(7);
		}
	}

	public void Record(){
		layout_camera_toptitle.setVisibility(View.GONE);
		switchCamera.setVisibility(View.GONE);
		iv_record_del.setVisibility(View.GONE);
		buttonHome.setImageAlpha(70);
		if (capture != null && capture.getAngle() < 360) {
			String filepath=startRecording();
			if(filepath!=null) {
				recording = true;
				startUpdateRecordUi();
			}
		}
		else if(capture.getAngle()>=360) {
			buttonHome.setImageAlpha(255);
		}
	}

	private void showToast(int resId){
		Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * start resorcing
	 * This is a sample project and call this on UI thread to avoid being complicated
	 * but basically this should be called on private thread because prepareing
	 * of encoder is heavy work
	 */
	private String startRecording() {
		String filepath=null;
		if (DEBUG) Log.v(TAG, "startRecording:");
		try {
			mMuxer = new MuxerWrapper(".mp4");
			float ww=mCameraView.getVideoWidth();
			float hh=mCameraView.getVideoHeight();
			float newh=ww;
			float neww=hh;
			new VideoEncoder(mMuxer, mMediaEncoderListener, (int)ww, (int)hh);
			new AudioEncoder(mMuxer, mMediaEncoderListener);
			mMuxer.prepare();
			mMuxer.startRecording();
			filepath=mMuxer.getOutputPath();

		} catch (final IOException e) {
			Log.e(TAG, "startCapture:", e);
		}
		return filepath;
	}

	/**
	 * request stop recording
	 */
	private boolean stopRecording(boolean flag) {
		if (mMuxer != null) {
			if(flag==false && mMuxer.gettdiff()<1200) {
				return false;
			}
			if(tipStopRecord != null && tipStopRecord.getVisibility() == View.VISIBLE){
				tipStopRecord.setVisibility(View.GONE);
			}
			mMuxer.stopRecording();
			filelist.add(mMuxer.getOutputPath());
			capture.stopRecord();
			mMuxer = null;
			recording=false;
			iv_record_del.setVisibility(View.VISIBLE);
			layout_camera_toptitle.setVisibility(View.VISIBLE);
			switchCamera.setVisibility(View.VISIBLE);
			iv_record_del.setVisibility(View.VISIBLE);
			buttonHome.setImageAlpha(255);
			selshow();
			return true;
		}
		return true;
	}

	public void selshow(){
		if(camerid== Camera.CameraInfo.CAMERA_FACING_FRONT){
			buttonFlash.setVisibility(View.GONE);
			iv_beau_filter.setVisibility(View.VISIBLE);
		}
		else{
			buttonFlash.setVisibility(View.VISIBLE);
			iv_beau_filter.setVisibility(View.GONE);
		}
	}

	/**
	 * callback methods from encoder
	 */
	private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
		@Override
		public void onPrepared(final MediaEncoder encoder) {
			if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
			if (encoder instanceof VideoEncoder)
				mCameraView.setVideoEncoder((VideoEncoder)encoder);
		}

		@Override
		public void onStopped(final MediaEncoder encoder) {
			if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
			if (encoder instanceof VideoEncoder)
				mCameraView.setVideoEncoder(null);
		}
	};

	public void switchFilter(int flag){
		if(CameraFile.camerastaus==0){
			showToast(R.string.camera_permission);
			return;
		}
		mCameraView.setFlag(flag);
	}


	GestureDetector mDetector = new GestureDetector(this.getActivity(), new OnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Foncus(6);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float dx=e1.getX()-e2.getX();
			if(dx<0)
				dx=-dx;
			if(dx>40 && velocityX>0 && recording==false) {
				switchFilter(1);
				return true;
			}
			if(dx>40 && velocityX<0 && recording==false){
				flag--;
				switchFilter(0);
				return true;
			}
			return false;
		}
	});

}
