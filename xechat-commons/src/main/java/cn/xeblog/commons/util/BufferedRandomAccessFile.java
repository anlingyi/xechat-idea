package cn.xeblog.commons.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

/**
 * @author LYF
 * @date 2022-06-21
 */
public class BufferedRandomAccessFile extends RandomAccessFile {

    /**
     * 默认缓冲区大小的位长，即缓冲区默认大小为1024
     */
    private static final int DEFAULT_BUFFER_BIT_LEN = 10;

    /**
     * 缓冲区
     */
    protected byte[] buf;
    /**
     * 缓冲区大小的"指数"/ "位长"
     */
    protected int bufBitLen;
    /**
     * 缓冲区大小
     */
    protected int bufSize;
    /**
     * 用于优化计算缓冲区开始位置指针的变量
     */
    protected long bufMask;
    /**
     * 数据是否写入磁盘
     */
    protected boolean bufDirty;
    /**
     * 缓冲区使用的大小
     */
    protected int bufUsedSize;
    /**
     * 当前的文件指针
     */
    protected long curPos;
    /**
     * 缓冲区开始位置对应文件中的指针
     */
    protected long bufStartPos;
    /**
     * 缓冲区结束为止对应文件中的指针
     */
    protected long bufEndPos;
    /**
     * 文件结束位置的指针偏移量
     */
    protected long fileEndPos;
    /**
     * 文件名
     */
    protected String filename;
    /**
     * 初始文件长度
     */
    protected long initFileLen;

    public BufferedRandomAccessFile(String name) throws IOException {
        this(name, DEFAULT_BUFFER_BIT_LEN);
    }

    /**
     * @param name      文件名
     * @param bufBitLen 缓存区大小, 默认bufBitLen = 10 ,就是缓存区大小为1024 ; 为9,则是,512, 依次类推
     */
    public BufferedRandomAccessFile(String name, int bufBitLen) throws IOException {
        super(name, "r");
        this.init(name, bufBitLen);
    }

    /**
     * 构造方法调用的初始化
     */
    private void init(String name, int bufBitLen) throws IOException {
        this.filename = name;
        this.initFileLen = super.length();
        this.fileEndPos = this.initFileLen - 1;
        this.curPos = super.getFilePointer();

        if (bufBitLen < 0) {
            throw new IllegalArgumentException("bufBitLen size must >= 0");
        }

        this.bufBitLen = bufBitLen;
        this.bufSize = 1 << bufBitLen;
        this.buf = new byte[this.bufSize];
        this.bufMask = ~((long) this.bufSize - 1L);
        this.bufDirty = false;
        this.bufUsedSize = 0;
        this.bufStartPos = -1;
        this.bufEndPos = -1;
    }

    /**
     * bufDirty为真,把 buf[]中尚未写入磁盘的数据,写入磁盘
     */
    private void flushBuf() throws IOException {
        if (this.bufDirty) {
            if (super.getFilePointer() != this.bufStartPos) {
                super.seek(this.bufStartPos);
            }
            super.write(this.buf, 0, this.bufUsedSize);
            this.bufDirty = false;
        }
    }

    /**
     * 根据bufStartPos,填充buf[]
     */
    private int fillBuf() throws IOException {
        super.seek(this.bufStartPos);
        this.bufDirty = false;
        return super.read(this.buf);
    }

    /**
     * 移动文件指针到pos位置, 并且把buf[]映射填充至pos所在的文件块
     */
    @Override
    public void seek(long pos) throws IOException {
        if ((pos < this.bufStartPos) || (pos > this.bufEndPos)) {
            this.flushBuf();
            if ((pos >= 0) && (pos <= this.fileEndPos) && (this.fileEndPos != 0)) {
                this.bufStartPos = pos & this.bufMask;
                this.bufUsedSize = this.fillBuf();
            } else if (((pos == 0) && (this.fileEndPos == 0)) || (pos == this.fileEndPos + 1)) {
                this.bufStartPos = pos;
                this.bufUsedSize = 0;
            }
            this.bufEndPos = this.bufStartPos + this.bufSize - 1;
        }
        this.curPos = pos;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        long readEndPos = this.curPos + len - 1;  //读入字节最远的位置
        if (readEndPos <= this.bufEndPos && readEndPos <= this.fileEndPos) { // 从缓冲中读取
            System.arraycopy(this.buf, (int) (this.curPos - this.bufStartPos), b, off, len);
        } else {
            if (readEndPos > this.fileEndPos) { // 如果输出的内容大于文件的长度
                len = (int) (this.length() - this.curPos + 1);        //从文件当前位置开始的文件所有的字节数
            }
            super.seek(this.curPos);         //移动文件指针到当前位置
            len = super.read(b, off, len);         //读取知道文件结尾的所有字节,保存到b中
            readEndPos = this.curPos + len - 1;
        }
        this.seek(readEndPos + 1);
        return len;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        long writeEndPos = this.curPos + len - 1;
        if (writeEndPos <= this.bufEndPos) { // b[] in cur buf
            System.arraycopy(b, off, this.buf, (int)(this.curPos - this.bufStartPos), len);
            this.bufDirty = true;
            this.bufUsedSize = (int)(writeEndPos - this.bufStartPos + 1);
        } else { // b[] not in cur buf
            super.seek(this.curPos);
            super.write(b, off, len);
        }
        if (writeEndPos > this.fileEndPos) {
            this.fileEndPos = writeEndPos;
        }
        this.seek(writeEndPos + 1);
    }

    @Override
    public long length() throws IOException {
        return Math.max(this.fileEndPos + 1, this.initFileLen);
    }

    @Override
    public void setLength(long newLength) throws IOException {
        if (newLength > 0) {
            this.fileEndPos = newLength - 1;
        } else {
            this.fileEndPos = 0;
        }
        super.setLength(newLength);
    }

    @Override
    public long getFilePointer() throws IOException {
        return this.curPos;
    }

    @Override
    public void close() throws IOException {
        this.flushBuf();
        super.close();
    }

    /**
     * 测试用例
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        long readfilelen = 0;
        BufferedRandomAccessFile brafReadFile, brafWriteFile;

        String path = "C:\\Windows\\Fonts\\simsun.ttc";
        brafReadFile = new BufferedRandomAccessFile(path);
        readfilelen = brafReadFile.initFileLen;
        brafWriteFile = new BufferedRandomAccessFile(".\\STKAITI.001", 10);

        byte[] buf = new byte[1024];
        int readcount;

        long start = System.currentTimeMillis();

        while ((readcount = brafReadFile.read(buf)) != -1) {
            brafWriteFile.write(buf, 0, readcount);
        }

        brafWriteFile.close();
        brafReadFile.close();

        System.out.println("BufferedRandomAccessFile Copy & Write File: "
                + brafReadFile.filename
                + "    FileSize: "
                + java.lang.Integer.toString((int) readfilelen >> 1024)
                + " (KB)    "
                + "Spend: "
                + (double) (System.currentTimeMillis() - start) / 1000
                + "(s)");

        java.io.FileInputStream fdin = new java.io.FileInputStream(path);
        java.io.BufferedInputStream bis = new java.io.BufferedInputStream(fdin, 1024);
        java.io.DataInputStream dis = new java.io.DataInputStream(bis);

        java.io.FileOutputStream fdout = new java.io.FileOutputStream(".\\STKAITI.002");
        java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(fdout, 1024);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(bos);

        start = System.currentTimeMillis();

        for (int i = 0; i < readfilelen; i++) {
            dos.write(dis.readByte());
        }

        dos.close();
        dis.close();

        System.out.println("DataBufferedios Copy & Write File: "
                + brafReadFile.filename
                + "    FileSize: "
                + java.lang.Integer.toString((int) readfilelen >> 1024)
                + " (KB)    "
                + "Spend: "
                + (double) (System.currentTimeMillis() - start) / 1000
                + "(s)");
    }
}
