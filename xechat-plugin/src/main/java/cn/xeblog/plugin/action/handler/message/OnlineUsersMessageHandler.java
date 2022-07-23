package cn.xeblog.plugin.action.handler.message;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserListMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.ONLINE_USERS)
public class OnlineUsersMessageHandler extends AbstractMessageHandler<UserListMsgDTO> {

    @Override
    protected void process(Response<UserListMsgDTO> response) {
        Map<String, User> userMap = new ConcurrentHashMap<>();
        List<User> userList = response.getBody().getUserList();
        userList.forEach(user -> userMap.put(user.getUsername(), user));
        DataCache.userMap = userMap;

        GlobalThreadPool.execute(() -> {
            ThreadUtils.spinMoment(3000);
            String msg = "系统公告：亲爱的鱼友，欢迎你来到鱼塘~ 倡导文明摸鱼、理性摸鱼，做个德才兼备的顶级摸鱼选手！" +
                    "本项目为开源项目，开源地址：https://github.com/anlingyi/xechat-idea" +
                    " \n插件使用有问题请进群反馈或是直接去GitHub提交issues，摸鱼技术交流群：754126966。";
            ConsoleAction.showSimpleMsg(msg);
        });
    }

}
