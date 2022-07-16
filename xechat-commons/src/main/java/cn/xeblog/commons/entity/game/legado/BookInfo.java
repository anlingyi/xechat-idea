package cn.xeblog.commons.entity.game.legado;

import cn.hutool.core.util.StrUtil;

/**
 * @author LYF
 * @date 2022-07-14
 */
public class BookInfo {
    private String name;
    private String author;
    private String bookUrl;
    private Integer durChapterIndex;
    private Integer durChapterPos;
    private Long durChapterTime;
    private String durChapterTitle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public Integer getDurChapterIndex() {
        return durChapterIndex;
    }

    public void setDurChapterIndex(Integer durChapterIndex) {
        this.durChapterIndex = durChapterIndex;
    }

    public Integer getDurChapterPos() {
        return durChapterPos;
    }

    public void setDurChapterPos(Integer durChapterPos) {
        this.durChapterPos = durChapterPos;
    }

    public Long getDurChapterTime() {
        return durChapterTime;
    }

    public void setDurChapterTime(Long durChapterTime) {
        this.durChapterTime = durChapterTime;
    }

    public String getDurChapterTitle() {
        return durChapterTitle;
    }

    public void setDurChapterTitle(String durChapterTitle) {
        this.durChapterTitle = durChapterTitle;
    }

    @Override
    public String toString() {
        return StrUtil.format("{} - {}", this.name, this.author);
    }
}
