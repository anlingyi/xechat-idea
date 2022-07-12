package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.ConsoleTable;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.OnlineSever;
import cn.xeblog.commons.util.ParamsUtils;
import cn.xeblog.commons.util.ServerUtils;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author anlingyi
 * @date 2020/9/11
 */
@DoCommand(Command.SHOW_SERVER)
public class ShowServerCommandHandler extends AbstractCommandHandler {

    private static List<OnlineSever> SERVER = null;

    @Getter
    @AllArgsConstructor
    private enum Config {
        /**
         * 清理缓存
         */
        CLEAN("-c");

        private String key;
    }

    @Override
    public void process(String[] args) {
        List<OnlineSever> serverList = null;

        // 存在-c参数，清除缓存
        if (ParamsUtils.hasKey(args, Config.CLEAN.getKey())) {
            SERVER = null;
        }

        if (CollUtil.isEmpty(SERVER)) {
            // 查询服务列表,并缓存
            serverList = ServerUtils.getServerList();
            SERVER = serverList;
        } else {
            // 使用缓存数据
            serverList = SERVER;
        }
        
        ConsoleTable consoleTable = new ConsoleTable();
        consoleTable.setSBCMode(Boolean.FALSE);
        consoleTable.addHeader("#", "鱼塘", "命令");
        for (int i = 0; i < serverList.size(); i++) {
            OnlineSever server = serverList.get(i);
            consoleTable.addBody(Convert.toStr(i + 1), server.getName(),
                    StrUtil.format("#login {名字} -h {} -p {}", server.getIp(), server.getPort()));
        }
        ConsoleAction.showSimpleMsg(consoleTable.toString());
    }

    @Override
    protected boolean check(String[] args) {
        return true;
    }
}
