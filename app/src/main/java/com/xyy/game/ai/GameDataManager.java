package com.xyy.game.ai;

import android.util.Log;

import com.xyy.game.ANN.GenPool;
import com.xyy.game.framework.FileIO;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 游戏数据管理
 * Created by ${XYY} on ${2016/9/25}.
 */
public class GameDataManager {

    public static boolean AntiAlias = true;

    public static boolean FilterBitmap = true;

    public static boolean AdvancedFilterBitmap = false;

    public static boolean LowQuality = false;

    private static String[] data;

    private static GenPool[] genPools;

    public static GenPool getGenPool(String uid){
        for(int i=0;i<data.length;i++)
            if(data[i].equals(uid)) return genPools[i];
        throw new RuntimeException("Unable to Associate with the GenPool with uid = \""+uid+"\"");
    }

    public static void load(String[] dataToLoad, FileIO fileIO){

        data = dataToLoad;

        GenPool[] genPoolsTemp = new GenPool[dataToLoad.length];

        for(int i=0;i<dataToLoad.length;i++){
            String name = dataToLoad[i];

            double[][] nativeWeights = readDataFromAssets(fileIO,name);

            double[][] userWeights = readDataGeneratedFromUser(fileIO,name);

            if(nativeWeights != null && userWeights != null){
                genPoolsTemp[i] = new GenPool(userWeights,nativeWeights[0]);
                Log.e("GameDataManager","nativeWeights, userWeights loaded !");
            }
            else if(nativeWeights!=null){
                genPoolsTemp[i] = new GenPool(nativeWeights[0]);
                Log.e("GameDataManager","Only nativeWeights loaded !");
            }
            else{
                Log.e("GameDataManager","loading Failed !");
            }
        }

        genPools = genPoolsTemp;
    }

    public static void save(FileIO fileIO){
        for(int i=0;i<data.length;i++) {
            String name = data[i];

            writeDataGeneratedFromUser(fileIO,name,genPools[i].getData());
        }
    }

    private static double[][] readDataFromAssets(FileIO fileIO, String fileName){
        double[][] data = null;

        DataInputStream in = null;
        try {
            //注意保持读取和写入文件名相同
            in = new DataInputStream(fileIO.readAsset(fileName));
            // 读取数组长度
            short len = in.readShort();
            double[][] dataTemp = new double[len][];
            // 对每个子数组...
            for(short i=0;i<len;i++){
                // 读取子数组长度
                short l = in.readShort();
                double[] sub_data = new double[l];
                // 读取子数组数据
                for(short j=0;j<l;j++){
                    sub_data[j] = in.readDouble();
                }
                dataTemp[i] = sub_data;
            }
            data = dataTemp;
            // 读取完毕
        } catch (IOException ignored) {
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
            }
        }

        return data;
    }

    private static double[][] readDataGeneratedFromUser(FileIO fileIO, String fileName){
        double[][] data = null;

        //尝试读取外部文件
        DataInputStream in_bak = null;
        boolean success = false;
        try {
            //注意保持读取和写入文件名相同
            in_bak = new DataInputStream(fileIO.readExternalStorage(fileName));
            // 读取数组长度
            short len = in_bak.readShort();
            double[][] dataTemp = new double[len][];
            // 对每个子数组...
            for(short i=0;i<len;i++){
                // 读取子数组长度
                short l = in_bak.readShort();
                double[] sub_data = new double[l];
                // 读取子数组数据
                for(short j=0;j<l;j++){
                    sub_data[j] = in_bak.readDouble();
                }
                dataTemp[i] = sub_data;
            }
            data = dataTemp;
            // 读取完毕
            success = true;
            Log.i("GameDataManager","External Data read successfully!");
        } catch (IOException ignored) {
            Log.e("GameDataManager","read External Data GeneratedFromUser Reading Error !");
        } finally {
            try {
                if (in_bak != null)
                    in_bak.close();
            } catch (IOException ignored) {
                Log.e("GameDataManager","read External Data GeneratedFromUser Closing Error !");
            }
        }
        if(!success) {
            DataInputStream in = null;
            try {

                //注意保持读取和写入文件名相同
                in = new DataInputStream(fileIO.readInternalStorage(fileName));
                // 读取数组长度
                short len = in.readShort();
                double[][] dataTemp = new double[len][];
                // 对每个子数组...
                for (short i = 0; i < len; i++) {
                    // 读取子数组长度
                    short l = in.readShort();
                    double[] sub_data = new double[l];
                    // 读取子数组数据
                    for (short j = 0; j < l; j++) {
                        sub_data[j] = in.readDouble();
                    }
                    dataTemp[i] = sub_data;
                }
                data = dataTemp;
                // 读取完毕
                Log.i("GameDataManager", "Data read successfully!");
            } catch (IOException ignored) {
                Log.e("GameDataManager", "readDataGeneratedFromUser Reading Error !");
            } finally {
                try {
                    if (in != null)
                        in.close();
                } catch (IOException ignored) {
                    Log.e("GameDataManager", "readDataGeneratedFromUser Closing Error !");
                }
            }
        }
        return data;
    }

    private static void writeDataGeneratedFromUser(FileIO files, String fileName, double[][] data) {
        DataOutputStream out = null;

        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(files.writeInternalStorage(fileName)));

            // 写入数组长度
            if(data.length>Short.MAX_VALUE) Log.e("GameDataManager","Array Size Overflow may Lost of Data! (0x00)");
            short len = (short) data.length;
            out.writeShort(len);
            // 对每个子数组...
            for(short i=0;i<len;i++){
                // 写入子数组长度
                if(data[i].length>Short.MAX_VALUE) Log.e("GameDataManager","Array Size Overflow may Lost of Data!(0x01)("+i+")");
                short l = (short) data[i].length;
                out.writeShort(l);
                // 写入子数组数据
                for(short j=0;j<l;j++){
                    out.writeDouble(data[i][j]);
                }
            }
            // 写入完毕
            Log.i("GameDataManager","Data write successfully!");
        } catch (IOException ignored) {
            Log.e("GameDataManager","writeDataGeneratedFromUser Writing Error !");
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
                Log.e("GameDataManager","writeDataGeneratedFromUser Closing Error !");
            }
        }

        DataOutputStream out_bak = null;
        try {
            out_bak = new DataOutputStream(new BufferedOutputStream(files.writeExternalStorage(fileName)));
            // 写入数组长度
            if(data.length>Short.MAX_VALUE) Log.e("GameDataManager","Array Size Overflow may Lost of Data! (0x02)");
            short len = (short) data.length;
            out_bak.writeShort(len);
            // 对每个子数组...
            for(short i=0;i<len;i++){
                // 写入子数组长度
                if(data[i].length>Short.MAX_VALUE) Log.e("GameDataManager","Array Size Overflow may Lost of Data!(0x03)("+i+")");
                short l = (short) data[i].length;
                out_bak.writeShort(l);
                // 写入子数组数据
                for(short j=0;j<l;j++){
                    out_bak.writeDouble(data[i][j]);
                }
            }
            // 写入完毕
            Log.i("GameDataManager","External Data write successfully!");
        } catch (IOException ignored) {
            Log.e("GameDataManager","write External Data GeneratedFromUser Writing Error !");
        } finally {
            try {
                if (out_bak != null)
                    out_bak.close();
            } catch (IOException ignored) {
                Log.e("GameDataManager","write External Data GeneratedFromUser Closing Error !");
            }
        }
    }

    /**
     * 读取设置
     *
     */
    public static void loadSettings(FileIO fileIO){
        final String fileName = "settings";

        DataInputStream in = null;
        try {
            //注意保持读取和写入文件名相同
            in = new DataInputStream(fileIO.readInternalStorage(fileName));
            // 读取
            boolean aa = in.readBoolean();
            boolean fb = in.readBoolean();
            boolean advFb = in.readBoolean();
            boolean lq = in.readBoolean();
            AntiAlias = aa;
            FilterBitmap = fb;
            AdvancedFilterBitmap = advFb;
            LowQuality = lq;
            // 读取完毕
        } catch (IOException ignored) {
            Log.e("GameDataManager","load Settings failed, use default values.");
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException ignored) {
                Log.e("GameDataManager","load Settings Closing Error !");
            }
        }
    }

    /**
     * 保存设置
     *
     */
    public static void saveSettings(FileIO files) {
        final String fileName = "settings";
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(files.writeInternalStorage(fileName)));
            // 写入
            out.writeBoolean(AntiAlias);
            out.writeBoolean(FilterBitmap);
            out.writeBoolean(AdvancedFilterBitmap);
            out.writeBoolean(LowQuality);
            // 写入完毕
        } catch (IOException ignored) {
            Log.e("GameDataManager","save Settings failed!.");
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException ignored) {
                Log.e("GameDataManager","save Settings Closing Error !");
            }
        }
    }

}
