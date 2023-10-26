package cn.xeblog.server.util;

import cn.hutool.core.lang.ConsoleTable;
import cn.xeblog.commons.entity.IpRegion;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.CountEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 鼓励师
 * @date 2023/8/29 16:52
 */
public class CountOnlineUserUtil {

    /**
     * 统计在线人数 控制台打印
     *
     * @author 鼓励师
     * @date 2023/8/29 17:11
     */
    public static String getConsoleTableStr(List<User> onlineUsers, CountEnum countEnum) {
        return getConsoleTableStr(groupUsers(onlineUsers, countEnum), countEnum);
    }

    /**
     * 统计在线人数 控制台打印
     *
     * @author 鼓励师
     * @date 2023/8/29 17:11
     */
    public static String getConsoleTableStr(Map<String, List<User>> groupUserMap, CountEnum countEnum) {
        Map<String, Integer> countMap = groupUserMap.keySet().stream().collect(Collectors.toMap(k -> k, k -> groupUserMap.get(k).size()));
        ConsoleTable consoleTable = ConsoleTable.create();
        consoleTable.addHeader(countEnum.getDesc(), "在线人数");
        countMap.forEach((key, value) -> consoleTable.addBody(key, value.toString()));
        return consoleTable.toString();
    }

    /**
     * 统计在线人数分类
     *
     * @author 鼓励师
     * @date 2023/8/31 17:18
     */
    public static Map<String, List<User>> groupUsers(List<User> onlineUsers, CountEnum countEnum) {
        Map<String, List<User>> countMap = new HashMap<>();
        onlineUsers.forEach(user -> {
            String type = null;

            IpRegion region = user.getRegion();
            if (region != null) {
                switch (countEnum) {
                    case COUNTRY:
                        type = region.getCountry();
                        break;
                    case PROVINCE:
                        type = region.getProvince();
                        break;
                    case CITY:
                        type = region.getCity();
                        break;
                    case ISP:
                        type = region.getIsp();
                        break;
                    case USER_STATUS:
                        type = user.getStatus().alias();
                        break;
                }
            }

            if (type == null || "0".equals(type.trim())) {
                type = "未知";
            }

            List<User> userList = countMap.computeIfAbsent(type, k -> new ArrayList<>());
            userList.add(user);
        });

        return countMap;
    }

    /**
     * 分类展示所有人
     *
     * @author 鼓励师
     * @date 2023/9/1 10:20
     */
    public static String showAllUsers(List<User> onlineUsers, CountEnum countEnum, int size) {
        Map<String, List<User>> groupUsers = groupUsers(onlineUsers, countEnum);
        ConsoleTable consoleTable = ConsoleTable.create();

        String[] headers = new String[size + 1];
        headers[0] = countEnum.getDesc();
        for (int i = 1; i < size + 1; i++) {
            headers[i] = "用户";
        }
        consoleTable.addHeader(headers);

        groupUsers.forEach((key, userList) -> {
            List<String[]> stringsList = queryTableString(userList, key + "(" + userList.size() + ")", size);
            stringsList.forEach(consoleTable::addBody);
        });
        return consoleTable.toString();
    }

    /**
     * 统计在线人数分类
     *
     * @author 鼓励师
     * @date 2023/8/31 17:18
     */
    private static List<String[]> queryTableString(List<User> onlineUsers, String title, int size) {
        List<String[]> list = new ArrayList<>();

        int merchant = onlineUsers.size() / size;   // 商
        int remainder = onlineUsers.size() % size;  // 余数

        int f = 0;
        if (merchant == 0 && remainder > 0) {
            f = 1;
        } else if (merchant > 0 && remainder == 0) {
            f = merchant;
        } else if (remainder > 0) {
            f = merchant + 1;
        }

        for (int i = 0; i < f; i++) {
            String[] names = new String[size + 1];
            names[0] = title;
            for (int j = i * size; j < i * size + size; j++) {
                int index = j % size + 1;
                if (onlineUsers.size() > j) {
                    String username = onlineUsers.get(j).getUsername();
                    names[index] = username;
                } else {
                    names[index] = "  ";
                }
            }
            list.add(names);
        }

        return list;
    }
}
