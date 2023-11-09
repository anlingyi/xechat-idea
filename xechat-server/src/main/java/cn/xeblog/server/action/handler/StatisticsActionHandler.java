package cn.xeblog.server.action.handler;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.StatisticsMsgDTO;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.CountEnum;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.server.util.CountOnlineUserUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 在线用户统计
 *
 * @author 鼓励师
 * @date 2023/11/8 17:20
 */
@DoAction(Action.STATISTICS)
@Slf4j
public class StatisticsActionHandler extends AbstractActionHandler<StatisticsMsgDTO> {

    @Override
    protected void process(User user, StatisticsMsgDTO dto) {

        if (!user.isAdmin()) {
            user.send(ResponseBuilder.build(user, "你不是管理员，无权操作！", MessageType.USER));
            return;
        }

        CountEnum countEnum = dto.getCountEnum();
        if (countEnum == null) {
            user.send(ResponseBuilder.system("统计类型有误！"));
            return;
        }

        List<User> users = UserCache.listUser();
        String consoleTableStr = dto.isShowDetail() ? CountOnlineUserUtil.showAllUsers(users, countEnum, dto.getShowSize())
                : CountOnlineUserUtil.getConsoleTableStr(users, countEnum);

        // 表格消息
        String dateMsg = StrUtil.format("现在时间是:{}\n{}", dateFormat.format(new Date()), consoleTableStr);
        if (dto.isToAll()) {
            ChannelAction.send(ResponseBuilder.system(dateMsg));
        } else {
            user.send(ResponseBuilder.system(dateMsg));
        }
    }

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static final Timer timer = new Timer();

    public static void startTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Date now = new Date();
                Calendar instance = Calendar.getInstance();
                instance.setTime(now);
                int hour = instance.get(Calendar.HOUR_OF_DAY);
                if (hour >= 7 && hour <= 22) {
                    String consoleTableStr = CountOnlineUserUtil.getConsoleTableStr(UserCache.listUser(), CountEnum.PROVINCE);
                    String dateMsg = StrUtil.format("现在时间是:{}\n{}", dateFormat.format(now), consoleTableStr);
                    ChannelAction.send(ResponseBuilder.system(dateMsg));
                }
            }
        };
        Date firstTime = getNextHour();                // 设置定时器第1次执行的开始时间
        long period = TimeUnit.HOURS.toMillis(1);    // 设置每隔1小时执行1次
        timer.scheduleAtFixedRate(task, firstTime, period);
    }

    /**
     * 获取当前时间的下一个小时的整点时间
     */
    public static Date getNextHour() {
        Calendar calendar = Calendar.getInstance();
        log.info("==> 计时器：当前时间 = " + dateFormat.format(calendar.getTime()));
        calendar.set(Calendar.MILLISECOND, 0);  // 清空毫秒
        calendar.set(Calendar.SECOND, 0);       // 清空秒
        calendar.set(Calendar.MINUTE, 0);       // 清空分
        calendar.add(Calendar.HOUR, 1);     // 添加1小时
        Date nextHour = calendar.getTime();
        log.info("==> 计时器：开始时间 = " + dateFormat.format(nextHour));
        return nextHour;
    }

}
