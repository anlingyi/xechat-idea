package cn.xeblog.commons.entity.game.novel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LYF
 * @date 2022-06-22
 */
public class Chapter {
    private final Integer index;
    private final Long offset;
    private final String title;

    public Chapter(Integer index, Long offset, String title) {
        this.index = index;
        this.offset = offset;
        this.title = title;
    }

    public Integer getIndex() {
        return index;
    }

    public Long getOffset() {
        return offset;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    public static List<Chapter> generateChapter(String file, Charset charset, String pattern) throws Exception {
        List<Chapter> chapterList = new ArrayList<>();

        try (InputStream in = new FileInputStream(file)) {
            byte[] readBuff = new byte[1024];
            long pointer = 0;
            int readCount;
            boolean eol = false;
            int endLength = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((readCount = in.read(readBuff)) != -1) {
                for (int i = 0; i < readCount; i++) {
                    byte tmp;
                    //碰到换行符
                    switch (tmp = readBuff[i]) {
                        case '\n':
                            eol = true;
                            endLength++;
                            break;
                        case '\r':
                            endLength++;
                            eol = (i + 1) >= readCount || readBuff[i + 1] != '\n';
                            break;
                        default:
                            bos.write(tmp);
                            break;
                    }
                    if (eol) {
                        pointer += handleLine(chapterList, bos, charset, pointer, endLength, pattern);
                        endLength = 0;
                        eol = false;
                    }
                }
            }
        }
        return chapterList;
    }

    private static int handleLine(List<Chapter> chapterList, ByteArrayOutputStream bos,
                                  Charset charset, long startPointer, int endLength, String pattern) {
        byte[] data = bos.toByteArray();
        String line = new String(data, charset);
        if (Pattern.matches(pattern, line)) {
            int index = chapterList.size();
            if (index == 0 && startPointer != 0) {
                chapterList.add(0, new Chapter(index++, 0L, "前言"));
            }
            chapterList.add(new Chapter(index, startPointer, line));
        }
        bos.reset();

        return data.length + endLength;
    }
}
