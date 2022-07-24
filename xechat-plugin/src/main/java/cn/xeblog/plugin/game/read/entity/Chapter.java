package cn.xeblog.plugin.game.read.entity;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class Chapter {
    private String bookUrl;
    private Integer index;
    private String title;
    private Long startPointer;
    private Long endPointer;

    public Chapter() { }
    public Chapter(String title, Long startPointer, Long endPointer) {
        this.title = title;
        this.startPointer = startPointer;
        this.endPointer = endPointer;
        this.index = 0;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStartPointer() {
        return startPointer;
    }

    public void setStartPointer(Long startPointer) {
        this.startPointer = startPointer;
    }

    public Long getEndPointer() {
        return endPointer;
    }

    public void setEndPointer(Long endPointer) {
        this.endPointer = endPointer;
    }

    @Override
    public String toString() {
        return title.trim();
    }

    public static List<Chapter> generateChapter(String file, Charset charset, String pattern) throws Exception {
        List<Chapter> chapterList = new ArrayList<>();
        FileInputStream in = new FileInputStream(file);

        try {
            byte[] readBuff = new byte[1024];
            long pointer = 0L;
            boolean eol = false;
            int endLength = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int readCount;
            while((readCount = in.read(readBuff)) != -1) {
                for(int i = 0; i < readCount; ++i) {
                    byte tmp;
                    switch(tmp = readBuff[i]) {
                        case 10:
                            eol = true;
                            ++endLength;
                            break;
                        case 13:
                            ++endLength;
                            eol = i + 1 >= readCount || readBuff[i + 1] != 10;
                            break;
                        default:
                            bos.write(tmp);
                    }

                    if (eol) {
                        pointer += handleLine(chapterList, bos, charset, pointer, endLength, pattern);
                        endLength = 0;
                        eol = false;
                    }
                }
            }
        } catch (Throwable var15) {
            try {
                in.close();
            } catch (Throwable var14) {
                var15.addSuppressed(var14);
            }

            throw var15;
        }

        in.close();
        return chapterList;
    }

    private static int handleLine(List<Chapter> chapterList, ByteArrayOutputStream bos, Charset charset, long startPointer, int endLength, String pattern) {
        byte[] data = bos.toByteArray();
        String line = new String(data, charset);
        if (Pattern.matches(pattern, line)) {
            Chapter currentChapter = new Chapter(line, startPointer, 0L);

            int index = chapterList.size();
            if (index == 0 && startPointer != 0L) {
                Chapter chapter0 = new Chapter("前言", 0L, startPointer);
                chapterList.add(chapter0);
            } else if (index > 0) {
                Chapter lastChapter = chapterList.get(index - 1);
                lastChapter.setEndPointer(startPointer);
            }
            chapterList.add(currentChapter);
        }

        bos.reset();
        return data.length + endLength;
    }
}
