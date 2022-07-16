package cn.xeblog.plugin.game.legado;

import cn.hutool.core.date.DateUtil;
import cn.xeblog.commons.entity.game.legado.BookInfo;
import cn.xeblog.commons.entity.game.legado.LegadoChapter;
import cn.xeblog.plugin.util.BaseChapterUtil;

import java.util.List;

/**
 * @author LYF
 * @date 2022-07-15
 */
public class LegadoChapterUtil extends BaseChapterUtil {
    // 当前服务
    private final LegadoApi legadoApi;
    private final boolean isHard;

    public LegadoChapterUtil(LegadoApi legadoApi, LegadoChapter chapter, boolean isHard) {
        this.legadoApi = legadoApi;
        this.pageIndex = 0;
        this.isHard = isHard;
        this.contentPage = pagination(chapter);
    }

    public String nextChapter(BookInfo book, LegadoChapter chapter) {
        this.contentPage = pagination(chapter);
        this.pageIndex = 0;
        saveBookProgress(book, chapter);
        return currentPage();
    }

    public String lastChapter(BookInfo book, LegadoChapter chapter) {
        this.contentPage = pagination(chapter);
        this.pageIndex = this.contentPage.size() - 1;
        saveBookProgress(book, chapter);
        return currentPage();
    }

    private List<String> pagination(LegadoChapter chapter) {
        String content = this.legadoApi.getBookContent(chapter.getBookUrl(), chapter.getIndex());
        return pagination(content, this.isHard);
    }

    public void saveBookProgress(BookInfo book, LegadoChapter chapter) {
        book.setDurChapterIndex(chapter.getIndex());
        book.setDurChapterPos(0);
        book.setDurChapterTitle(chapter.getTitle());
        book.setDurChapterTime(DateUtil.current());
        this.legadoApi.saveBookProgress(book);
    }
}
