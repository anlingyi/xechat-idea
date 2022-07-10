package cn.xeblog.plugin.persistence;

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
    public int msgNotify;

    /**
     * 历史命令列表
     */
    public List<String> historyCommandList;

}
