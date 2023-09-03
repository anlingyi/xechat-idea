package cn.xeblog.plugin.tools.read.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.read.api.LegadoApi;
import cn.xeblog.plugin.tools.read.entity.Book;
import cn.xeblog.plugin.tools.read.entity.BookType;
import cn.xeblog.plugin.tools.read.entity.Chapter;
import cn.xeblog.plugin.tools.read.entity.LegadoBook;
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
    /** 当前页索引 */
    private int pageIndex;
    /** 当前章节分页内容 */
    private List<String> contentPage;
    /** Legado API */
    private LegadoApi legadoApi;

    public ChapterUtil(Book book) {
        this(book, 0);
    }

    /**
     * 章节工具类构造器
     * @param book 书籍
     * @param mode 模式，0：初始化；1：最后一页；2：第一页
     */
    public ChapterUtil(Book book, int mode) {
        this.book = book;
        if (book.getType() == BookType.LEGADO) {
            this.legadoApi = new LegadoApi(DataCache.readConfig.getLegadoHost());
        }

        this.contentPage = pagination();
        this.pageIndex = getPageIndex(mode);
        this.book.setChapterPos(this.pageIndex);
        saveBookProgress();
    }

    /**
     * 当前页
     */
    public String currentPage() {
        if (pageIndex < 0 || pageIndex >= CollUtil.size(contentPage)) {
            return "";
        }

        this.book.setChapterPos(this.pageIndex);
        return contentPage.get(pageIndex);
    }

    /**
     * 上一页
     */
    public String nextPage() {
        if (++pageIndex >= contentPage.size()) {
            return "";
        }
        saveBookProgress();
        return currentPage();
    }

    /**
     * 下一页
     */
    public String lastPage() {
        if (--pageIndex < 0) {
            return "";
        }
        saveBookProgress();
        return currentPage();
    }

    /**
     * 模式切换
     */
    public void changeMode() {
        int pos = pageIndexToPos();
        this.contentPage = pagination();
        this.pageIndex = posToPageIndex(pos);
    }

    /**
     * 保存进度
     */
    private void saveBookProgress() {
        if (this.book.getType() == BookType.LEGADO) {
            LegadoBook legadoBook = new LegadoBook();
            legadoBook.setBookUrl(book.getUrl());
            legadoBook.setName(book.getName());
            legadoBook.setAuthor(book.getAuthor());
            legadoBook.setDurChapterIndex(book.getChapterIndex());
            legadoBook.setDurChapterPos(pageIndexToPos());
            legadoBook.setDurChapterTitle(this.book.getCurrentChapter().getTitle());
            legadoBook.setDurChapterTime(DateUtil.current());
            ThreadUtil.execute(() -> legadoApi.saveBookProgress(legadoBook));
        }
    }

    /**
     * 分页
     * @return 分页结果
     */
    private List<String> pagination() {
        Chapter chapter = this.book.getCurrentChapter();
        String content = "";
        if (this.book.getType() == BookType.LEGADO) {
            content = this.legadoApi.getBookContent(chapter.getBookUrl(), chapter.getIndex());
        } else {
            content = readText(chapter.getStartPointer(), chapter.getEndPointer());
        }
        if (book.isHard()) {
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
        // content = StrUtil.cleanBlank(content);
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

    /**
     * 获取章节索引
     * @param mode 模式，0：初始化；1：最后一页；2：第一页
     * @return 章节索引
     */
    private int getPageIndex(int mode) {
        switch (mode) {
            // init
            case 0:
                Integer chapterPos = this.book.getChapterPos();
                if (this.book.getType() == BookType.LEGADO) {
                    chapterPos = posToPageIndex(chapterPos);
                }
                boolean valid = chapterPos != null && chapterPos >= 0 && chapterPos < this.contentPage.size();
                return valid ? chapterPos : 0;
            // last page
            case 1:
                return this.contentPage.size() - 1;
            case 2:
            default:
                return 0;
        }
    }

    private int pageIndexToPos() {
        int pos = 0;
        for (int i = 0; i < this.pageIndex; i++) {
            pos += this.contentPage.get(i).length();
        }
        return pos + 1;
    }

    private int posToPageIndex(int pos) {
        int tmpPos = 0;
        for (int i = 0; i < this.contentPage.size(); i++) {
            tmpPos += this.contentPage.get(i).length();
            if (tmpPos > pos) {
                return i;
            }
        }
        return 0;
    }
}
