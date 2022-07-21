package cn.xeblog.commons.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * ip常量
 *
 * @author nn200433
 * @date 2022年01月10日 0010 15:03:02
 */
public interface IpConstants {

    /**
     * ip未知默认展示
     */
    public static final String IP_UN_KNOW_DEFAULT_REGION = "未知";

    /**
     * 省份简称
     */
    public static final Map<String, String> SHORT_PROVINCE = new HashMap<String, String>() {{
        put("北京市", "京");
        put("北京", "京");
        put("天津市", "津");
        put("天津", "津");
        put("河北省", "冀");
        put("山西省", "晋");
        put("内蒙古自治区", "蒙");
        put("辽宁省", "辽");
        put("吉林省", "吉");
        put("黑龙江省", "黑");
        put("上海市", "沪");
        put("上海", "沪");
        put("江苏省", "苏");
        put("浙江省", "浙");
        put("安徽省", "皖");
        put("福建省", "闽");
        put("江西省", "赣");
        put("山东省", "鲁");
        put("河南省", "豫");
        put("湖北省", "鄂");
        put("湖南省", "湘");
        put("广东省", "粤");
        put("广西壮族自治区", "桂");
        put("海南省", "琼");
        put("四川省", "川");
        put("贵州省", "贵");
        put("云南省", "滇");
        put("重庆市", "渝");
        put("重庆", "渝");
        put("西藏自治区", "藏");
        put("陕西省", "陕");
        put("甘肃省", "甘");
        put("青海省", "青");
        put("宁夏回族自治区", "宁");
        put("新疆維吾尔自治区", "新");
        put("香港特别行政区", "港");
        put("澳门特别行政区", "澳");
        put("台湾省", "台");
        put("未知", "-");
    }};

}
