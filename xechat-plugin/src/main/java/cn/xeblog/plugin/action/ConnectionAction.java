package cn.xeblog.plugin.action;

import cn.xeblog.plugin.client.XEChatClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public void exec() {
        new Thread(() -> XEChatClient.run(host, port)).start();
    }

}
