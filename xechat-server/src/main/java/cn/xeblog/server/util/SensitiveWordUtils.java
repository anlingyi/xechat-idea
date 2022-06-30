package cn.xeblog.server.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author anlingyi
 * @date 2022/6/30 3:58 下午
 */
@Slf4j
public class SensitiveWordUtils {

    private final static String[] LOVE_CHINA = {"富强", "民主", "文明", "和谐", "自由", "平等", "公正", "法治", "爱国",
            "敬业", "诚信", "友善"};

    private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5]");

    /**
     * 敏感词文件路径
     */
    private static String sensitiveWordFilePath;

    /**
     * 敏感词根节点
     */
    private static SensitiveWordNode rootNode;

    public static void setSensitiveWordFilePath(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }

        sensitiveWordFilePath = filePath;
        init();
    }

    /**
     * 初始化敏感词库
     */
    private static void init() {
        log.info("敏感词文件地址：{}", sensitiveWordFilePath);

        Set<String> keyWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(sensitiveWordFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                keyWords.add(line.trim());
            }
        } catch (Exception e) {
            log.error("读取敏感词库出现异常！Error -> {}", e);
        }

        if (keyWords.isEmpty()) {
            return;
        }

        log.info("初始化敏感词库，共有{}个敏感词", keyWords.size());

        // 初始化根节点
        rootNode = new SensitiveWordNode(' ');
        // 构建敏感词节点
        for (String keyWord : keyWords) {
            buildSensitiveWordNode(keyWord);
        }
    }

    /**
     * 构建敏感词节点
     *
     * @param keyWord 敏感词
     */
    private static void buildSensitiveWordNode(String keyWord) {
        SensitiveWordNode nowNode = rootNode;
        for (Character c : keyWord.toCharArray()) {
            SensitiveWordNode nextNode = nowNode.getNextNode(c);
            if (nextNode == null) {
                nextNode = new SensitiveWordNode(c);
                nowNode.putNextNode(nextNode);
            }
            nowNode = nextNode;
        }
        nowNode.setEnd(true);
    }

    /**
     * 判断是否存在敏感词
     *
     * @param text 待检测文本
     * @return true:存在敏感词 false:未存在敏感词
     */
    public static boolean hasSensitiveWord(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }

        if (rootNode == null) {
            log.debug("敏感词节点未被初始化！");
            return false;
        }

        // 清除非法字符
        text = invalidClear(text);
        StringBuilder sb = new StringBuilder();
        SensitiveWordNode nowNode;

        for (int i = 0; i < text.length(); i++) {
            nowNode = rootNode;
            for (int j = i; j < text.length(); j++) {
                nowNode = nowNode.getNextNode(text.charAt(j));
                if (nowNode == null) {
                    sb.setLength(0);
                    break;
                }

                sb.append(nowNode.getKey());

                if (nowNode.isEnd()) {
                    log.debug("[{}] => 存在敏感词 -> {}", text, sb);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 热爱祖国，热爱人民
     *
     * @param text 待检测文本
     * @return 如果存在敏感词则返回处理后的结果，否则直接返回原内容
     */
    public static String loveChina(String text) {
        if (hasSensitiveWord(text)) {
            return LOVE_CHINA[RandomUtil.randomInt(LOVE_CHINA.length)];
        }

        return text;
    }

    /**
     * 清除非法字符
     *
     * @param str 字符
     * @return 返回清除非法字符后的字符
     */
    private static String invalidClear(String str) {
        Matcher matcher = PATTERN.matcher(str.trim());
        return matcher.replaceAll("");
    }

}
