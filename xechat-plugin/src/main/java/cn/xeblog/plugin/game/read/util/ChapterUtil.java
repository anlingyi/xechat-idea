package cn.xeblog.plugin.game.read.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.read.api.LegadoApi;
import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.entity.BookType;
import cn.xeblog.plugin.game.read.entity.Chapter;
import cn.xeblog.plugin.game.read.entity.LegadoBook;
import cn.xeblog.plugin.util.NotifyUtils;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-20
 */
public class ChapterUtil {

    private static final int ROW = 8;
    private static final int COLUMNS = 26;

    /** 当前书籍 */
    private final Book book;
    /** 困难模式 */
    private final boolean isHard;
    /** 当前页索引 */
    private int pageIndex;
    /** 当前章节分页内容 */
    private final List<String> contentPage;
    /** Legado API */
    private LegadoApi legadoApi;

    public ChapterUtil(Book book, boolean isHard) {
        this(book, isHard, false);
    }

    public ChapterUtil(Book book, boolean isHard, boolean isLastPage) {
        this.book = book;
        this.isHard = isHard;
        if (book.getType() == BookType.LEGADO) {
            this.legadoApi = new LegadoApi(DataCache.readConfig.getLegadoHost());
        }

        this.contentPage = pagination();
        this.pageIndex = isLastPage ? this.contentPage.size() - 1 : 0;
        saveBookProgress();
    }

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

    /**
     * 保存进度
     */
    private void saveBookProgress() {
        if (this.book.getType() == BookType.LEGADO) {
            LegadoBook legadoBook = new LegadoBook();
            legadoBook.setDurChapterIndex(book.getChapterIndex());
            legadoBook.setDurChapterPos(0);
            legadoBook.setDurChapterTitle(this.book.getCurrentChapter().getTitle());
            legadoBook.setDurChapterTime(DateUtil.current());
            this.legadoApi.saveBookProgress(legadoBook);
        }
    }

    /**
     * 分页
     * @return 分页结果
     */
    private List<String> pagination() {
        Chapter chapter = this.book.getCurrentChapter();
        String content;
        if (this.book.getType() == BookType.LEGADO) {
            content = this.legadoApi.getBookContent(chapter.getBookUrl(), chapter.getIndex());
        } else {
            content = readText(chapter.getStartPointer(), chapter.getEndPointer());
        }
        if (isHard) {
            return hardPagination(content);
        } else {
            return easyPagination(content);
        }
    }

    private List<String> easyPagination(String content) {
        List<String> contentPage = new ArrayList<>();

        StringBuilder pageContent = new StringBuilder();
        int row = 1;
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
        return CollUtil.toList(StrUtil.split(content, DataCache.readConfig.getHardColumns()));
    }

    private String readText(long startPointer, long endPointer) {
        StringBuilder sb = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(this.book.getUrl(), "r")) {
            String line;
            raf.seek(startPointer);
            while (true) {
                line = FileUtil.readLine(raf, this.book.getCharset());
                if (line == null || (endPointer != 0 && raf.getFilePointer() > endPointer)) {
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            NotifyUtils.warn("", "小说读取错误：" + e.getMessage());
        }
        return sb.toString();
    }
}
