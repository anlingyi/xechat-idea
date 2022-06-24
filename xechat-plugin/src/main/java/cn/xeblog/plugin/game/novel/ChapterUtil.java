package cn.xeblog.plugin.game.novel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author LYF
 * @date 2022-06-23
 */
@Slf4j
public class ChapterUtil {
    private static final int PAGE_SIZE = 120;

    // 小说文件路径
    private final String file;
    // 文件编码
    private final Charset charset;
    // 当前页起始索引
    private int pageStartIndex;
    // 当前页结束索引
    private int pageEndIndex;
    // 当前章节内容
    private String content;

    public ChapterUtil(String file, Charset charset, long startPointer, long endPointer) {
        this.file = file;
        this.charset = charset;
        this.content = readText(startPointer, endPointer);
        this.pageStartIndex = 0;
        this.pageEndIndex = Math.min(this.content.length(), PAGE_SIZE);
    }

    public String currentPage() {
        String pageContent = content.substring(pageStartIndex, pageEndIndex);
        return handleHeader(pageContent);
    }

    public String nextPage() {
        if (pageEndIndex == content.length()) {
            return "";
        }
        pageStartIndex = pageEndIndex;
        pageEndIndex = Math.min(content.length(), pageEndIndex + PAGE_SIZE);
        return currentPage();
    }

    public String lastPage() {
        if (pageStartIndex == 0) {
            return "";
        }
        pageStartIndex = Math.max(0, pageStartIndex - PAGE_SIZE);
        pageEndIndex = Math.min(pageStartIndex + PAGE_SIZE, this.content.length());
        return currentPage();
    }

    public String nextChapter(long startPointer, long endPointer) {
        this.content = readText(startPointer, endPointer);
        this.pageStartIndex = 0;
        this.pageEndIndex = Math.min(this.content.length(), PAGE_SIZE);
        return currentPage();
    }

    public String lastChapter(long startPointer, long endPointer) {
        this.content = readText(startPointer, endPointer);
        this.pageStartIndex = Math.max(0, this.content.length() - PAGE_SIZE);
        this.pageEndIndex = this.content.length();
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

    private String handleHeader(String pageContent) {
        char c = pageContent.charAt(0);
        if (c == '\n') {
            pageContent = pageContent.substring(1);
        }
        return pageContent;
    }
}
