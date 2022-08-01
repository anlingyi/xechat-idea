package cn.xeblog.commons.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.*;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 摸鱼提示工具类
 *
 * @author nn200433
 * @date 2022-07-22 022 18:15:47
 */
public class MoYuTipsUtil {

    private static final String TIPS_MSG = "【摸鱼总办】提醒您：\n" +
            "\n" +
            "{}月{}日 {}好，打工人！{}\n" +
            "\n" +
            "距离周末还有{}天\n" +
            "{}\n" +
            "{}\n" +
            "\n" +
            "欢迎加入摸鱼技术交流群：754126966";

    private static final List<String> hellos = new ArrayList<String>(3) {{
        add("摸鱼能增加工作动力，摸鱼能放松筋骨舒展神经。人都是被摸鱼摸大的，摸鱼是人的天性，无论是顺境还是逆境，工作摸鱼，才不会被工作抛弃。");
        add("工作再累，一定不要忘记摸鱼哦！有事没事起身去茶水间，去厕所，去廊道走走别老在工位上坐着，钱是老板的,但命是自己的。");
        add("生活需要摸鱼，而不是认真工作。有时候你需要摸鱼，让工作顺其自然，不要过分担心，也不要过于细致的规划。学会摸鱼，不要逼自己逼得太紧。深呼吸。尘埃落定时你会再次看见森林中的树木。");
    }};

    private static final List<String> end = new ArrayList<String>(3) {{
        add("认认真真上班，这根本就不叫赚钱，那是用劳动换取报酬。只有偷懒，在上班的时候摸鱼划水，你才是从老板手里赚到了钱。最后，祝愿天下所有摸鱼人都能愉快的渡过每一天！");
        // add("工作再累，一定不要忘记摸鱼哦！有事没事起身去茶水间，去厕所，去廊道走走别老在工位上坐着，钱是老板的,但命是自己的。");
        // add("生活需要摸鱼，而不是认真工作。有时候你需要摸鱼，让工作顺其自然，不要过分担心，也不要过于细致的规划。学会摸鱼，不要逼自己逼得太紧。深呼吸。尘埃落定时你会再次看见森林中的树木。");
    }};

    private static final List<String> FESTIVAL_FILTER = CollUtil.newArrayList("元旦节", "春节", "清明", "劳动节", "端午节", "中秋节", "国庆节");

    /**
     * 获取提示
     *
     * @return {@link String }
     * @author nn200433
     */
    public static String getTips() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.with(TemporalAdjusters.next(DayOfWeek.of(6)));

        Map<String, Long> holidayMap = computeHoliday();
        StringBuffer fStr = new StringBuffer();
        holidayMap.forEach((f, l) -> fStr.append(StrUtil.format("距离{}还有{}天\n", f, l)));

        return StrUtil.format(TIPS_MSG, now.getMonthValue(), now.getDayOfMonth(), amOrPm(now.getHour()),
                hellos.get(RandomUtil.randomInt(hellos.size())), Duration.between(now, nextWeek).toDays(), fStr, end.get(RandomUtil.randomInt(end.size())));

    }

    /**
     * 计算假期
     *
     * @return {@link Map }<{@link String }, {@link Long }>
     * @author nn200433
     */
    public static Map<String, Long> computeHoliday() {
        DateTime date = DateUtil.date();
        final DateTime today = DateUtil.date();

        DateTime dateTime = date.offset(DateField.YEAR, 1);
        DateTime end = DateUtil.endOfYear(dateTime);
        // 传入的时间如果是今年，日期生成从今天开始
        DateRange range = DateUtil.range(today, end, DateField.DAY_OF_YEAR);

        Map<String, Long> festivalMap = new LinkedHashMap<String, Long>(366);
        for (DateTime time : range) {
            // 1. 公历
            Solar solar = Solar.fromDate(time);
            List<String> festivals = Opt.ofNullable(solar.getFestivals()).orElse(Collections.emptyList());

            // 2. 农历
            Lunar lunar = Lunar.fromDate(time);
            festivals.addAll(lunar.getFestivals());

            // 3. 节气
            String jieQi = StrUtil.blankToDefault(lunar.getJieQi(), null);
            if (StrUtil.equals(jieQi, "清明")) {
                festivals.add("清明");
            }

            if (CollUtil.containsAny(festivals, FESTIVAL_FILTER)) {
                festivals.remove("教师节");
                final String festival = CollUtil.join(festivals, StrUtil.COMMA);
                if (null == festivalMap.get(festival)) {
                    festivalMap.put(festival, DateUtil.between(today, time, DateUnit.DAY));
                }
            }
        }

        return festivalMap;
    }

    /**
     * 上午或下午
     *
     * @param a 一个
     * @return {@link String }
     * @author nn200433
     */
    public static String amOrPm(int a) {
        if (a >= 0 && a <= 6) {
            return "凌晨";
        }
        if (a > 6 && a < 12) {
            return "上午";
        }
        if (a == 12) {
            return "中午";
        }
        if (a >= 13 && a <= 18) {
            return "下午";
        } else {
            return "晚上";
        }
    }

}
