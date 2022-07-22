package cn.xeblog.plugin.game.read.entity;

import cn.hutool.core.collection.CollUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.read.api.LegadoApi;
import cn.xeblog.plugin.game.read.util.CharsetConverter;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class Book {
    private String name;
    private String author;
    private String url;
    private Integer chapterIndex;
    private List<Chapter> chapters;
    private BookType type;
    @OptionTag(converter = CharsetConverter.class)
    private Charset charset;
    private String chapterPattern;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    @Transient
    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public BookType getType() {
        return type;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getChapterPattern() {
        return chapterPattern;
    }

    public void setChapterPattern(String chapterPattern) {
        this.chapterPattern = chapterPattern;
    }

    public Chapter getCurrentChapter() {
        if (CollUtil.isNotEmpty(chapters) && chapterIndex >= 0 && chapterIndex < chapters.size()) {
            return chapters.get(chapterIndex);
        }
        return null;
    }

    public List<Chapter> generateChapter() throws Exception {
        if (type == BookType.LOCAL) {
            chapters = Chapter.generateChapter(url, charset, chapterPattern);
        } else {
            LegadoApi api = new LegadoApi(DataCache.readConfig.getLegadoHost());
            chapters = api.getChapterList(url);
        }
        return chapters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(url, book.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
