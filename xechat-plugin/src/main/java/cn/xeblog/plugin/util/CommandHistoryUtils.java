package cn.xeblog.plugin.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.GlobalThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anlingyi
 * @date 2022/7/10 9:04 PM
 */
public class CommandHistoryUtils {

    private static final int MAX_SIZE = 15;

    private static List<String> historyList = new ArrayList<>(MAX_SIZE);

    private static int nextIndex;

    public static void addCommand(String command) {
        GlobalThreadPool.execute(() -> {
            nextIndex = 0;
            int size = CollectionUtil.size(historyList);
            int lastIndex = size - 1;
            if (lastIndex > -1 && historyList.get(lastIndex).equals(command)) {
                return;
            }

            historyList.add(command);
            if (size > MAX_SIZE) {
                historyList.remove(0);
            }
        });
    }

    public static String getNextCommand() {
        nextIndex++;
        return getCurrentCommand();
    }

    public static String getPrevCommand() {
        nextIndex--;
        return getCurrentCommand();
    }

    public static String getCurrentCommand() {
        int size = CollectionUtil.size(historyList);
        if (size == 0) {
            return "";
        }

        if (nextIndex < 0) {
            nextIndex = size - 1;
        }
        if (nextIndex >= size) {
            nextIndex = 0;
        }

        return historyList.get(nextIndex);
    }

    public static void setHistoryList(List<String> list) {
        if (list == null) {
            return;
        }

        historyList = list;
        nextIndex = 0;
    }

    public static List<String> getHistoryList() {
        return historyList;
    }

}
