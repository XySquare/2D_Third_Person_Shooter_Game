package com.xyy.game.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileI/O接口
 * Created by ${XYY} on ${2015/11/20}.
 */
public interface FileIO {
    /**
     * 从Assets文件夹读取资源文件
     * @param fileName 文件路径
     * @return 输入数据流
     * @throws IOException
     */
    InputStream readAsset(String fileName) throws IOException;

    /**
     * 读取外部储存的文件
     * @param fileName 文件路径
     * @return 输入数据流
     * @throws IOException
     */
    InputStream readExternalStorage(String fileName) throws IOException;

    /**
     * 写入外部储存的文件
     * @param fileName 文件路径
     * @return 输出数据流
     * @throws IOException
     */
    OutputStream writeExternalStorage(String fileName) throws IOException;

    /**
     * 写入外部储存的文件
     * @param fileName 文件路径
     * @param append true=追加模式
     * @return 输出数据流
     * @throws IOException
     */
    OutputStream writeExternalStorage(String fileName, boolean append) throws IOException;

    /**
     * 删除外部储存的文件
     * @param file 文件
     * @return true:删除成功
     *          false:文件不存在 或 删除失败
     */
    boolean deleteExternalStorage(File file);

    File getExternalStorage();

    /**
     * 读取内部储存文件
     * @param fileName 文件名，不能为路径
     * @return 输入数据流
     * @throws IOException
     */
    InputStream readInternalStorage(String fileName) throws IOException;

    /**
     * 写入内部储存文件
     * @param fileName 文件名，不能为路径
     * @return 输出数据流
     * @throws IOException
     */
    OutputStream writeInternalStorage(String fileName) throws IOException;

    InputStream readCacheStorage(String fileName) throws IOException;

    OutputStream writeCacheStorage(String fileName) throws IOException;

    /**
     * 删除缓存文件
     * @return false=删除出错
     */
    boolean deleteCacheStorage();

    /**
     * @return 缓存区文件大小（MB）
     */
    float getCacheSize();
}
