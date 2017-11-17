package com.china.snapshot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Chuanlong on 2016/12/15.
 */

public class StorageUtil {

    private static final String TEMP_FOLDER = "temp";
    private static final String VIDEO_FOLDER = "video";
    private static final String RAW_FOLDER = "raw";
    private static final String RAW_CACHE_FOLDER = "raw_cache";

    public static String saveFile(byte[] data, String dir, String fileName) {
        BufferedOutputStream bos = null;
        try {
            File file = new File(dir, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String saveFile(Bitmap bitmap, String dir, String fileName) {
        BufferedOutputStream bos = null;
        try {
            File file = new File(dir, fileName);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            if (fileName != null && (fileName.contains(".png") || fileName.contains(".PNG"))) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            bos.flush();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取Internal的文件存储目录
     * /data/user/0/com.yi.moments/files
     */
    public static String getInternalFileDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取Internal的cache的存储目录
     * /data/user/0/com.yi.moments/cache
     */
    public static String getInternalCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }


    /**
     * 获取Internal的cache下的thumb存储目录
     * /data/user/0/com.yi.moments/cache/videoThumb/
     */
    public static String getInternalThumbCacheDir(Context context) {
        File dir = new File(getInternalCacheDir(context) + "/videoThumb/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 获取SD卡的根目录
     * /storage/emulated/0
     */
    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡公有目录的路径
     * /storage/emulated/0/[type]
     */
    public static String getSDCardPublicDir(String type) {
        if (isSDCardMounted()) {
            return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡私有Cache目录的路径
     * /storage/emulated/0/Android/data/com.yi.moments/cache
     */
    public static String getSDCardPrivateCacheDir(Context context) {
        if (isSDCardMounted() && context.getExternalCacheDir() != null) {
            return context.getExternalCacheDir().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡私有Files目录的路径
     * /storage/emulated/0/Android/data/com.yi.moments/files/[dir]
     */
    public static String getSDCardPrivateFilesDir(Context context, String dir) {
        if (isSDCardMounted() && context.getExternalFilesDir(dir) != null) {
            return context.getExternalFilesDir(dir).getAbsolutePath();
        }
        return null;
    }


    // 判断SD卡是否被挂载
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFormatPath(Context context, String folder, String suffix) {
        String dir = getSDCardPrivateCacheDir(context);
        if (dir == null) {
            dir = getInternalCacheDir(context);
        }
        String path = dir + File.separator + folder;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        path += File.separator + System.currentTimeMillis() + suffix;
        return path;
    }

    public static String getFormatFolder(Context context, String folder) {
        String dir = getSDCardPrivateCacheDir(context);
        if (dir == null) {
            dir = getInternalCacheDir(context);
        }
        String folderPath = dir + File.separator + folder;
        File folderFile = new File(folderPath);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        return folderPath;
    }

    public static String getFormatVideoPath(Context context) {
        return getFormatPath(context, VIDEO_FOLDER, ".mp4");
    }

    public static String getFormatVideoTempPath(Context context) {
        return getFormatPath(context, TEMP_FOLDER, ".mp4");
    }

    public static String getFormatRawPath(Context context) {
        return getFormatPath(context, RAW_FOLDER, ".yuv");
    }

    public static String getRawFolder(Context context) {
        return getFormatFolder(context, RAW_FOLDER);
    }

    public static String getRawCacheFolder(Context context) {
        return getFormatFolder(context, RAW_CACHE_FOLDER);
    }

    public static String getRawCachePath(Context context) {
        return getFormatPath(context, RAW_CACHE_FOLDER, ".dat");
    }

    public static String getVideoThumbnailTempPath(Context context) {
        return getFormatPath(context, VIDEO_FOLDER, ".jpg");
    }

    public static void clearRawCacheFolder(Context context) {
        String fileFolder = getRawCacheFolder(context);
        if (!TextUtils.isEmpty(fileFolder)) {
            File folder = new File(fileFolder);
            if (folder != null && folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    public static boolean saveBitmap(String picPath, Bitmap bitmap) {
        if (bitmap == null) {
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(picPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteFile(String... filePaths) {
        if (filePaths == null) {
            return;
        }
        for (String path : filePaths) {
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void createFile(String... filePaths) {
        if (filePaths == null) {
            return;
        }
        for (String path : filePaths) {
            if (TextUtils.isEmpty(path)) {
                continue;
            }
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getLrcContent(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } catch (Exception e) {

            }
        }
        return size;
    }

}
