package cn.xeblog.plugin.game.novel;

import cn.hutool.core.io.FileUtil;
import cn.xeblog.plugin.util.BaseChapterUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author LYF
 * @date 2022-06-23
 */
@Slf4j
public class LocalChapterUtil extends BaseChapterUtil {
    // 小说文件路径
    private final String file;
    // 文件编码
    private final Charset charset;
    private final boolean isHard;

    public LocalChapterUtil(String file, Charset charset, long startPointer, long endPointer, boolean isHard) {
        this.file = file;
        this.charset = charset;
        this.pageIndex = 0;
        this.isHard = isHard;
        this.contentPage = pagination(startPointer, endPointer);
    }

    public String nextChapter(long startPointer, long endPointer) {
        this.contentPage = pagination(startPointer, endPointer);
        this.pageIndex = 0;
        return currentPage();
    }

    public String lastChapter(long startPointer, long endPointer) {
        this.contentPage = pagination(startPointer, endPointer);
        this.pageIndex = this.contentPage.size() - 1;
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
        return pagination(content, this.isHard);
    }
}
