package com.example.koolmeo.manager;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.example.koolmeo.http.IHttpFileCallback;
import com.example.koolmeo.util.StorageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StorageManager {

    private static StorageManager instance;

    public static StorageManager getInstance(){
        return instance;
    }

    public static void initInstance(Context context){
        instance = new StorageManager(context);
    }

    public final static String VIDEO_FOLDER = "video";
    public final static String IMAGE_FOLDER = "image";
    public final static String TEMP_FOLDER = "temp";

    public String videoDir;
    public String imageDir;
    public String tempDir;

    private StorageManager(Context context){
        String dir = StorageUtil.getSDCardPrivateCacheDir(context);
        if(dir == null){
            dir = StorageUtil.getInternalCacheDir(context);
        }

        videoDir = dir + File.separator + VIDEO_FOLDER;
        imageDir = dir + File.separator + IMAGE_FOLDER;
        tempDir = dir + File.separator + TEMP_FOLDER;

        File videoFileDir = new File(videoDir);
        if(!videoFileDir.exists()){
            videoFileDir.mkdir();
        }

        File imageFileDir = new File(imageDir);
        if(!imageFileDir.exists()){
            imageFileDir.mkdir();
        }

        File tempFileDir = new File(tempDir);
        if(tempFileDir.exists()){
            tempFileDir.delete();
        }
        tempFileDir.mkdir();

        map = new HashMap<>();
    }

    private Map<String, List<IHttpFileCallback>> map = new ConcurrentHashMap<>();

    public String getVideoFilepath(String url){
        String filename = getFilename(url);
        if(filename != null){
            return videoDir + File.separator + filename;
        }
        return null;
    }

    public boolean copy(String oldPath, String newPath) {
        boolean isDone = false;
        InputStream is = null;
        FileOutputStream fs = null;
        try {
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                is = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fs.write(buffer, 0, len);
                }
                fs.flush();
            }
            isDone = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDone = false;
        } finally {
            try {
                if (is != null){
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (fs != null){
                    fs.close();
                }
            } catch (IOException e) {
            }
        }
        return isDone;
    }

    public String getFilename(String url) {
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        String filename = null;
        while (mc.find()) {
            filename = mc.group();//截取文件名后缀名
        }
        return filename;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }
}
