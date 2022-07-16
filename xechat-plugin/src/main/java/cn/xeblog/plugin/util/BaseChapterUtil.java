package cn.xeblog.plugin.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-15
 */
public abstract class BaseChapterUtil {
    private static final int ROW = 8;
    private static final int COLUMNS = 27;
    private static final int HARD_COLUMNS = 50;

    /**
     * 当前页索引
     */
    protected int pageIndex;
    /**
     * 当前章节分页内容
     */
    protected List<String> contentPage;

    /**
     * 当前页
     */
    public String currentPage() {
        return contentPage.get(pageIndex);
    }

    /**
     * 上一页
     */
    public String nextPage() {
        if (++pageIndex >= contentPage.size()) {
            return "";
        }
        return currentPage();
    }

    /**
     * 下一页
     */
    public String lastPage() {
        if (--pageIndex < 0) {
            return "";
        }
        return currentPage();
    }

    protected List<String> pagination(String content, boolean isHard) {
        if (isHard) {
            return hardPagination(content);
        } else {
            return easyPagination(content);
        }
    }

    private List<String> easyPagination(String content) {
        List<String> contentPage = new ArrayList<>();

        StringBuilder pageContent = new StringBuilder();
        int row = 0;
        int col = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            boolean newlineChar = (c == '\n');
            // 忽略开头第一个就是换行的字符
            if (newlineChar && col == 0) {
                continue;
            }
            pageContent.append(c);
            col++;

            boolean newline = (col == COLUMNS || newlineChar);
            if (newline) {
                col = 0;
                if (row++ == ROW) {
                    contentPage.add(pageContent.toString());
                    pageContent.setLength(0);
                    row = 0;
                }
            }
        }
        if (pageContent.length() > 0) {
            contentPage.add(pageContent.toString());
        }
        return contentPage;
    }

    private List<String> hardPagination(String content) {
        content = StrUtil.removeAny(content, "\r", "\n");
        return CollUtil.toList(StrUtil.split(content, HARD_COLUMNS));
    }
}
