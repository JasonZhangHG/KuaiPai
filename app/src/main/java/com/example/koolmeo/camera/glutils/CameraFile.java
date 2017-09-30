package com.example.koolmeo.camera.glutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class CameraFile {
    public static Context cxt=null;
    public static int filterindex=0;
    public static int camerastaus=0;
    public static int audiostatus=0;
    public static String getVideoPath(){
        String parent= Environment.getExternalStorageDirectory().getPath()+"/"+"DCIM/yivideo/";
        File file=new File(parent);
        if(!file.exists())
            file.mkdir();
        return parent;
    }

    public static String getVideoTemp(){
        String parent= Environment.getExternalStorageDirectory().getPath()+"/"+"DCIM/yivideotmp/";
        File file=new File(parent);
        if(!file.exists())
            file.mkdir();
        return parent;
    }
    public static void delFileVideo(){
        String dirfile=getVideoTemp();
        File file=new File(dirfile);
        if(file!=null){
            File[] files=file.listFiles();
            if(files!=null){
                int size=files.length;
                for(int i=0;i<size;i++){
                    files[i].delete();
                }
            }
        }
    }

    //filename  视频文件名
    public static void recordMeta(String filename, String record, String pos, String status){
        if(filename==null || (!filename.endsWith(".mp4")))
            return;
        String filetxt=filename.replaceFirst(".mp4",".txt");
        File file=new File(filetxt);
        if(file.exists())
            file.delete();
        try {
            file.createNewFile();
            PrintWriter pw=new PrintWriter(file);
            pw.print(record);
            pw.print(";");
            pw.print(pos);
            pw.print(";");
            pw.print(status);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(TimeUnit.MILLISECONDS.toMicros(1));
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static String getVideoDuration(String filePath){
        String duration="0";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return duration;
    }

    public static int bitmapw=1;
    public static int bitmaph=1;
}
