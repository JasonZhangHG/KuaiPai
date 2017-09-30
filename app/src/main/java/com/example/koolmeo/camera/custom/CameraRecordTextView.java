package com.example.koolmeo.camera.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

public class CameraRecordTextView extends ImageView {
    private Paint paint;
    private Paint paint_c;
    private float strokew=12;
    private Bitmap pausebitmap=null;
    private Bitmap normalbitmap=null;

    private ArrayList<Float> videointerval=new ArrayList<Float>();

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    private float step=0.1f;
    private RectF rectF = new RectF();
    private float lastangle=0.0f;

    public void setPausebitmap(Bitmap bmp){
        this.pausebitmap=bmp;
    }

    public void setNormalbitmap(Bitmap bmp){
        this.normalbitmap=bmp;
    }

    public void updateUi(Bitmap bmp){
        angle+=step;
        this.pausebitmap=bmp;
        invalidate();
        Log.i(CameraConfig.TAG,"angle:"+angle);
    }

    public boolean stopRecord(){
        float diff=angle-lastangle;
        float mint=CameraConfig.MIN_SECOND/CameraConfig.MAXRCORDTIME_SECOND*360;
//        if(angle<360 && (360-angle<mint ||  diff<mint))
//            return false;

        if(videointerval!=null) {
            videointerval.add(angle);
            lastangle=angle;
            this.pausebitmap=null;
            invalidate();
            return true;
        }
        return true;
    }

    public void delRecord(){
        if(videointerval!=null){
            int recordnum=videointerval.size();
            if(recordnum>0) {
                if(recordnum==1) {
                    angle = 0;
                }
                else {
                    angle = videointerval.get(recordnum - 2);
                }
                videointerval.remove(recordnum - 1);
                invalidate();
                lastangle=angle;
            }
        }
    }

    public float getAngle() {
        return angle;
    }
    public void setAngle(float angle) {
        this.angle = angle;
    }
    private float angle=0.0f;
    public CameraRecordTextView(Context context) {
        this(context, null);
    }
    public CameraRecordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraRecordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setGravity(Gravity.CENTER);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(strokew);
        paint.setStyle(Paint.Style.STROKE);

        paint_c=new Paint();
        paint_c.setColor(0xffffffff);
        paint_c.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectF.set(strokew/2, strokew/2, canvas.getWidth()-strokew/2, canvas.getHeight()-strokew/2);
        paint.setColor(0x88ffffff);
        canvas.drawArc(rectF,0.0f,360.0f,false,paint);

        paint.setColor(0xff212121);
        canvas.drawArc(rectF,-90.0f,angle,false,paint);

        if(videointerval!=null){
            paint.setColor(0x99ffffff);
            for(int i=0;i<videointerval.size();i++){
                float val=videointerval.get(i).floatValue();
                canvas.drawArc(rectF,val-1.0f-90.0f,2.0f,false,paint);
            }
        }
        int interval=20;
        int raidus=(int)strokew+interval;

        if(pausebitmap!=null){
            Bitmap bmp=Bitmap.createScaledBitmap(pausebitmap,canvas.getWidth()-raidus,canvas.getHeight()-raidus,false);
            canvas.drawBitmap(bmp,raidus/2,raidus/2,null);
        }
        else if(normalbitmap!=null)
        {
            Bitmap bmp=Bitmap.createScaledBitmap(normalbitmap,canvas.getWidth()-raidus,canvas.getHeight()-raidus,false);
            canvas.drawBitmap(bmp,raidus/2,raidus/2,null);
        }
    }
}
