package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.AdminReact;
import cn.xeblog.commons.entity.react.result.AdminReactResult;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoReact;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.server.config.GlobalConfig;

/**
 * @author anlingyi
 * @date 2023/2/18 8:16 PM
 */
@DoReact(React.ADMIN)
public class AdminReactHandler extends AbstractReactHandler<AdminReact, AdminReactResult> {

    @Override
    protected void process(User user, AdminReact body, ReactResult<AdminReactResult> result) {
        if (!user.isAdmin()) {
            result.setMsg("没有权限！");
            return;
        }

        String msg = null;
        switch (body.getOperate()) {
            case QUERY_PERMIT:
                break;
            case GLOBAL_MAX_FILE_SIZE:
                GlobalConfig.UPLOAD_FILE_MAX_SIZE = Integer.parseInt(body.getValue());
                ChannelAction.send(ResponseBuilder.system("管理员已将文件上传的大小限制为" + GlobalConfig.UPLOAD_FILE_MAX_SIZE + "KB!"));
                break;
            case GLOBAL_PERMIT_ADD:
                GlobalConfig.GLOBAL_PERMIT |= body.getPermissions().getValue();
                switch (body.getPermissions()) {
                    case SEND_FILE:
                        msg = "鱼塘已允许全员发送图片！";
                        break;
                    case SPEAK:
                        msg = "鱼塘已解除全员禁言！";
                        break;
                }
                break;
            case GLOBAL_PERMIT_REMOVE:
                if (body.getPermissions().hasPermit(GlobalConfig.GLOBAL_PERMIT)) {
                    GlobalConfig.GLOBAL_PERMIT ^= body.getPermissions().getValue();
                    switch (body.getPermissions()) {
                        case SEND_FILE:
                            msg = "鱼塘已禁止全员发送图片！";
                            break;
                        case SPEAK:
                            msg = "鱼塘已开启全员禁言！";
                            break;
                    }
                }
                break;
            case USER_PERMIT_ADD:
            case USER_PERMIT_REMOVE:
                User execUser = UserCache.get(body.getUid());
                if (execUser == null) {
                    result.setMsg("用户不存在！");
                    return;
                }

                if (body.getOperate() == AdminReact.Operate.USER_PERMIT_ADD) {
                    execUser.addPermit(body.getPermissions());
                    switch (body.getPermissions()) {
                        case SEND_FILE:
                            msg = "已允许[" + execUser.getUsername() + "]发送图片！";
                            break;
                        case SPEAK:
                            msg = "已允许[" + execUser.getUsername() + "]发言！";
                            break;
                    }
                } else {
                    execUser.removePermit(body.getPermissions());
                    switch (body.getPermissions()) {
                        case SEND_FILE:
                            msg = "已禁止[" + execUser.getUsername() + "]发送图片！";
                            break;
                        case SPEAK:
                            msg = "已禁止[" + execUser.getUsername() + "]发言！";
                            break;
                    }
                }

                GlobalConfig.addUserPermit(user, execUser.getPermit());
                ChannelAction.send(ResponseBuilder.build(execUser, null, MessageType.STATUS_UPDATE));
        }

        if (msg != null) {
            ChannelAction.send(ResponseBuilder.system(msg));
        }

        result.setSucceed(true);
        result.setData(new AdminReactResult(GlobalConfig.GLOBAL_PERMIT, GlobalConfig.UPLOAD_FILE_MAX_SIZE));
    }

}
