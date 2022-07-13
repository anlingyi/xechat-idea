package cn.xeblog.server.util;

import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 百度翻譯
 *
 * @author nn200433
 * @date 2022年03月24日 0024 10:54:30
 */
@Slf4j
public class BaiDuFy {

    private static Pattern ENGLISH_REGEX = Pattern.compile("^[\\x00-\\xff]*$");

    private static LFUCache<String, String> cache = new LFUCache<String, String>(1000);

    private static final String APP_SALT = "xechat";

    private static final String url = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private String appId;

    private String appKey;

    public BaiDuFy(String appId, String appKey) {
        this.appId = appId;
        this.appKey = appKey;
    }

    /**
     * 翻译
     *
     * @param query 翻译的文本
     * @return {@link String }
     * @author nn200433
     */
    public String translate(String query) {
        // 无翻译配置返回
        if (StrUtil.isAllBlank(this.appId, this.appKey)) {
            return query;
        }

        // 非英文，返回
        final boolean isMatch = ReUtil.isMatch(ENGLISH_REGEX, query);
        if (StrUtil.isAllBlank(this.appId, this.appKey) || !isMatch) {
            return query;
        }

        // 准备进行翻译
        String result = cache.get(query);
        if (StrUtil.isNotBlank(result)) {
            log.info("======> [百度翻译] 缓存命中 单词 = {} | 结果 = {}", query, result);
            return result;
        }

        Map<String, Object> paramsMap = new HashMap<String, Object>(6);
        paramsMap.put("q", query);
        paramsMap.put("from", "en");
        paramsMap.put("to", "zh");
        paramsMap.put("appid", this.appId);
        paramsMap.put("salt", APP_SALT);
        paramsMap.put("sign", SecureUtil.md5(this.appId + query + APP_SALT + this.appKey));

        String post = HttpUtil.post(url, paramsMap);
        log.debug("---> [百度翻译] 单词 = {} | 结果 = {}", query, post);
        JSONObject resultObj = JSONUtil.parseObj(post);
        String eCode = resultObj.getStr("error_code");
        if (StrUtil.isNotBlank(eCode)) {
            return StrUtil.EMPTY;
        }
        String dst = UnicodeUtil.toString(resultObj.getJSONArray("trans_result").getJSONObject(0).getStr("dst"));
        log.debug("---> [百度翻译] 最终取值 = {}", dst);
        cache.put(query, dst);

        return StrUtil.format("{}（机翻：{}）", query, dst);
    }

}
