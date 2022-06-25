package cn.xeblog.plugin.game.novel;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-06-23
 */
@Slf4j
public class ChapterUtil {
    private static final int ROW = 8;
    private static final int COLUMNS = 27;

    // 小说文件路径
    private final String file;
    // 文件编码
    private final Charset charset;
    // 当前页索引
    private int index;
    // 当前章节分页内容
    private List<String> contentPage;

    public ChapterUtil(String file, Charset charset, long startPointer, long endPointer) {
        this.file = file;
        this.charset = charset;
        this.index = 0;
        this.contentPage = pagination(startPointer, endPointer);
    }

    public String currentPage() {
        return contentPage.get(index);
    }

    public String nextPage() {
        if (++index >= contentPage.size()) {
            return "";
        }
        return currentPage();
    }

    public String lastPage() {
        if (--index < 0) {
            return "";
        }
        return currentPage();
    }

    public String nextChapter(long startPointer, long endPointer) {
        this.contentPage = pagination(startPointer, endPointer);
        this.index = 0;
        return currentPage();
    }

    public String lastChapter(long startPointer, long endPointer) {
        this.contentPage = pagination(startPointer, endPointer);
        this.index = this.contentPage.size() - 1;
        return currentPage();
    }

    private String readText(long startPointer, long endPointer) {
        StringBuilder sb = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            String line;
            raf.seek(startPointer);
            while (true) {
                line = FileUtil.readLine(raf, charset);
                if (line == null || (endPointer != 0 && raf.getFilePointer() > endPointer)) {
                    break;
                }
                if (line.isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            log.error("小说读取错误：{}", e.getMessage());
        }
        return sb.toString();
    }

    private List<String> pagination(long startPointer, long endPointer) {
        String content = readText(startPointer, endPointer);
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
}
