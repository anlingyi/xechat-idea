package cn.xeblog.plugin.game.box.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述: 地图初始化工具
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/21 11:34
 */
public class MapsUtils {

    private static final Map<Integer, int[][]> mapData = new HashMap<>(50);

    private MapsUtils() {
    }

    public static int[][] getLevel(int level) {
        return mapData.get(level) == null ? new int[1][1] : copy2dArrays(mapData.get(level));
    }

    public static int getTotal() {
        return mapData.size();
    }

    public static void initMapDataDefault() {
        URL url = MapsUtils.class.getResource("/map/map.json");
        if (url != null) {
            String string = FileUtil.readString(url, Charset.defaultCharset());
            Map<Integer, int[][]> data = JSONUtil.toBean(string, new TypeReference<Map<Integer, int[][]>>() {
            }.getType(), false);
            mapData.putAll(data);
        }
    }

    public static void initMapDataWithFile(String absolutPath) {
        File mapFile = new File(absolutPath);
        if (!mapFile.exists()) {
            return;
        }
        String string = FileUtil.readString(mapFile, Charset.defaultCharset());
        Map<Integer, int[][]> data = JSONUtil.toBean(string, new TypeReference<Map<Integer, int[][]>>() {
        }.getType(), false);
        mapData.putAll(data);
    }

    // 复制二维数组
    public static int[][] copy2dArrays(int[][] ints) {
        int[][] arrays = new int[ints.length][ints[0].length];
        for (int i = 0; i < ints.length; i++) {
            System.arraycopy(ints[i], 0, arrays[i], 0, ints[0].length);
        }
        return arrays;
    }

    // 控制台打印二维数组
    public static void print2dArrays(int level) {
        print2dArrays(getLevel(level));
    }

    // 控制台打印二维数组
    public static void print2dArrays(int[][] map) {
        for (int[] ints : map) {
            Console.log(ints);
        }
    }

}
