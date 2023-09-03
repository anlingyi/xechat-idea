package cn.xeblog.plugin.persistence;

import cn.xeblog.plugin.tools.browser.config.BrowserConfig;
import cn.xeblog.plugin.tools.read.ReadConfig;
import lombok.Data;

import java.util.List;

/**
 * 数据持久化
 *
 * @author anlingyi
 * @date 2022/6/27 5:39 上午
 */
@Data
public class PersistenceData {

    /**
     * 用户名
     */
    private String username;

    /**
     * 消息通知 1.正常通知 2.隐晦通知 3.关闭通知
     */
    private int msgNotify;

    /**
     * 历史命令列表
     */
    private List<String> historyCommandList;

    /**
     * 阅读持久化数据
     */
    private ReadConfig readConfig;

    /**
     * token
     */
    private String token;

    /**
     * 浏览器配置
     */
    private BrowserConfig browserConfig;

}
