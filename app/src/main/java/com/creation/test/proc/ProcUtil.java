package com.creation.test.proc;

import android.content.Context;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
    MemTotal: 所有可用RAM大小。
    MemFree: LowFree与HighFree的总和，被系统留着未使用的内存。
    Buffers: 用来给文件做缓冲大小。
    Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于diskcache minus SwapCache）。
    SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小。已经被交换出来的内存，仍然被存放在swapfile中，用来在需要的时候很快的被替换而不需要再次打开I/O端口。
    Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要，否则不会被移作他用。
    Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径。
    SwapTotal: 交换空间的总大小。
    SwapFree: 未被使用交换空间的大小。
    Dirty: 等待被写回到磁盘的内存大小。
    Writeback: 正在被写回到磁盘的内存大小。
    AnonPages：未映射页的内存大小。
    Mapped: 设备和文件等映射的大小。
    Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
    SReclaimable:可收回Slab的大小。
    SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）。
    PageTables：管理内存分页页面的索引表的大小。
    NFS_Unstable:不稳定页表的大小。
    要获取android手机总内存大小，只需读取”/proc/meminfo”文件的第1行，并进行简单的字符串处理即可。
*/

public class ProcUtil {
    private static final String MEM_INFO_PATH = "/proc/meminfo";
    private static final String MEM_TOTAL = "MemTotal";
    private static final String MEM_FREE = "MemFree";

    private static final String VERSION_PATH = "/proc/version";

    private static final String CPU_INFO_PATH = "/proc/cpuinfo";

    /**
     * 得到中内存大小
     */
    public static String getTotalMemory(Context context) {
        return getMemInfoByType(context, MEM_TOTAL);
    }

    /**
     * 得到可用内存大小
     */
    public static String getMemoryFree(Context context) {
        return getMemInfoByType(context, MEM_FREE);
    }

    /**
     * 得到mem info
     */
    public static String getMemInfoByType(Context context, String type) {
        String line = findLineByType(MEM_INFO_PATH, type);
        if (line == null) {
            return null;
        }
        String[] array = line.split("\\s+"); // \\s表示   空格,回车,换行等空白符, +号表示一个或多个的意思
        int length = Integer.valueOf(array[1]) * 1024; // 获得系统总内存，单位是KB，乘以1024转换为Byte
        return Formatter.formatFileSize(context, length);
    }

    /**
     * 得到version
     */
    public static String getVersionByType(Context context, String type) {
        String line = findLineByType(VERSION_PATH, type);
        if (line == null) {
            return null;
        }
        String[] array = line.split("\\s+"); // \\s表示   空格,回车,换行等空白符, +号表示一个或多个的意思
        int length = Integer.valueOf(array[1]) * 1024; // 获得系统总内存，单位是KB，乘以1024转换为Byte
        return Formatter.formatFileSize(context, length);
    }

    /**
     * 得到cpu info
     */
    public static String getCpuInfoByType(Context context, String type) {
        String line = findLineByType(CPU_INFO_PATH, type);
        if (line == null) {
            return null;
        }
        String[] array = line.split("\\s+"); // \\s表示   空格,回车,换行等空白符, +号表示一个或多个的意思
        int length = Integer.valueOf(array[1]) * 1024; // 获得系统总内存，单位是KB，乘以1024转换为Byte
        return Formatter.formatFileSize(context, length);
    }

    private static String findLineByType(String path, String type) {
        try (
                FileReader fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024)
        ) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.e("ProcUtil", line);
                if (line.contains(type)) {
                    break;
                }
            }
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}