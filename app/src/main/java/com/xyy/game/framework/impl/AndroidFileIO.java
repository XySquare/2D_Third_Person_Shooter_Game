package com.xyy.game.framework.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.xyy.game.framework.FileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Game接口的实现将保存该类的一个实例
 * 通过传入Context
 * Created by ${XYY} on ${2015/3/5}.
 */
public final class AndroidFileIO implements FileIO {
    private AssetManager assets;
    private Context applicationContext;
    //外部储存路径
    private final String externalStoragePath;
    //缓存文件路径
    private final File cacheFile;
    private final String cacheStoragePath;

    public AndroidFileIO(AssetManager assets, Context applicationContext) {
        this.assets = assets;
        this.applicationContext = applicationContext;
        this.externalStoragePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "AI" + File.separator;
        this.cacheFile = applicationContext.getCacheDir();
        this.cacheStoragePath = cacheFile.getAbsolutePath() + File.separator;

    }

    @Override
    public InputStream readAsset(String fileName) throws IOException {
        return assets.open(fileName);
    }

    @Override
    public InputStream readExternalStorage(String fileName) throws IOException {
        checkDir();
        return new FileInputStream(externalStoragePath + fileName);
    }

    @Override
    public OutputStream writeExternalStorage(String fileName) throws IOException {
        checkDir();
        return new FileOutputStream(externalStoragePath + fileName);
    }

    @Override
    public OutputStream writeExternalStorage(String fileName, boolean append) throws IOException {
        checkDir();
        return new FileOutputStream(externalStoragePath + fileName, append);
    }

    @Override
    public boolean deleteExternalStorage(File file){
        return file.exists() && file.delete();
    }

    @Override
    public File getExternalStorage(){
        return new File(externalStoragePath);
    }

    @Override
    public InputStream readInternalStorage(String fileName) throws IOException {
        return applicationContext.openFileInput(fileName);
    }

    @Override
    public OutputStream writeInternalStorage(String fileName) throws IOException {
        return applicationContext.openFileOutput(fileName,Context.MODE_PRIVATE);
    }

    @Override
    public InputStream readCacheStorage(String fileName) throws IOException {
        return new FileInputStream(cacheStoragePath + fileName);
    }

    @Override
    public OutputStream writeCacheStorage(String fileName) throws IOException {
        return new FileOutputStream(cacheStoragePath + fileName);
    }

    @Override
    public boolean deleteCacheStorage(){
        return deleteFolderFile(cacheFile,false);
    }

    @Override
    public float getCacheSize(){
        return getFolderSize(cacheFile)/1024f/1024f;
    }

    /**
     * @return 指定目录/文件的大小
     */
    private static long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (File aFileList : fileList) {
            // 如果下面还有文件  
            if (aFileList.isDirectory()) {
                size = size + getFolderSize(aFileList);
            } else {
                size = size + aFileList.length();
            }
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     * @return false=删除出错
     */
    private static boolean deleteFolderFile(File file, boolean deleteThisPath) {
        boolean success = true;
        if (file.isDirectory()) {// 如果是文件夹，下面还有文件
            File files[] = file.listFiles();
            for (File _file : files) {
                deleteFolderFile(_file, true);
            }
            if (deleteThisPath){
                if(!file.delete()) success = false;
            }
        }
        else{// 如果是文件，删除
            if(!file.delete()) success = false;
        }
        return success;
    }

    private void checkDir(){
        File destDir = new File(externalStoragePath);
        if (!destDir.exists()) {
            Log.e("FileIO","dir not exists.");
            if(!destDir.mkdirs()){
                Log.e("FileIO","failure or if the directory already existed.");
            }
        }
    }
}
