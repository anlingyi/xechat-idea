package cn.xeblog.plugin.action;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.plugin.client.XEChatClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

/**
 * @author anlingyi
 * @date 2021/8/22 9:02 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionAction {

    private String host;

    private int port;

    public void exec(Consumer<Boolean> consumer) {
        GlobalThreadPool.execute(() -> XEChatClient.run(host, port, consumer));
    }

}
