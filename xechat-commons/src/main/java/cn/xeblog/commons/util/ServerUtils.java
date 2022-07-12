package cn.xeblog.commons.util;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.xeblog.commons.entity.OnlineSever;

import java.util.Collections;
import java.util.List;

/**
 * 服务器工具类
 *
 * @author nn200433
 * @date 2022-07-12 012 08:10:36
 */
public class ServerUtils {

    /**
     * github代理网址
     */
    private static final String GIT_HUB_PROXY_URL = "https://ghproxy.futils.com/";

    /**
     * 服务器url列表
     */
    private static final String SERVER_LIST_URL = GIT_HUB_PROXY_URL + "https://raw.githubusercontent.com/nn200433/xechat-idea/main/server_list.json";

    /**
     * 获取服务器列表
     *
     * @return {@link List }<{@link OnlineSever }>
     * @author nn200433
     */
    public static List<OnlineSever> getServerList() {
        String resp = HttpUtil.get(SERVER_LIST_URL);
        if (StrUtil.isBlank(resp)) {
            return Collections.emptyList();
        }
        return JSONUtil.toBean(resp, new TypeReference<List<OnlineSever>>() {
        }.getType(), Boolean.FALSE);
    }

}
