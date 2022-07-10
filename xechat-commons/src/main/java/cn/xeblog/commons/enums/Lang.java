package cn.xeblog.commons.enums;

import lombok.Getter;

/**
 * 语言
 *
 * @author nn200433
 * @date 2022-04-19 11:41:48
 */
public enum Lang {

    /**
     * 简体中文
     */
    ZH_HANS("zh-hans"),
    ZH("zh"),

    /**
     * 繁体中文
     */
    ZH_HANT("zh-hant"),

    /**
     * 英文
     */
    EN("en"),

    /**
     * 德语
     */
    DE("de"),

    /**
     * 西班牙语
     */
    ES("es"),

    /**
     * 法语
     */
    FR("fr"),

    /**
     * 意大利语
     */
    IT("it"),

    /**
     * 日语
     */
    JA("ja"),

    /**
     * 韩语
     */
    KO("ko"),

    /**
     * 俄语
     */
    RU("ru"),

    /**
     * 印地语
     */
    HI("hi"),

    /**
     * 泰语
     */
    TH("th"),

    /**
     * 阿拉伯语
     */
    AR("ar"),

    /**
     * 葡萄牙语
     */
    PT("pt"),

    /**
     * 孟加拉语
     */
    BN("bn"),

    /**
     * 马来语
     */
    MS("ms"),

    /**
     * 荷兰语
     */
    NL("nl"),

    /**
     * 希腊语
     */
    EL("el"),

    /**
     * 拉丁语
     */
    LA("la"),

    /**
     * 瑞典语
     */
    SV("sv"),

    /**
     * 印尼语
     */
    ID("id"),

    /**
     * 波兰语
     */
    PL("pl"),

    /**
     * 土耳其语
     */
    TR("tr"),

    /**
     * 捷克语
     */
    CS("cs"),

    /**
     * 爱沙尼亚语
     */
    ET("et"),

    /**
     * 越南语
     */
    VI("vi"),

    /**
     * 菲律宾语
     */
    FIL("fil"),

    /**
     * 芬兰语
     */
    FI("fi"),

    /**
     * 希伯来语
     */
    HE("he"),

    /**
     * 冰岛语
     */
    IS("is"),

    /**
     * 挪威语
     */
    NB("nb");

    @Getter
    private String value;

    private Lang(String value) {
        this.value = value;
    }

}
