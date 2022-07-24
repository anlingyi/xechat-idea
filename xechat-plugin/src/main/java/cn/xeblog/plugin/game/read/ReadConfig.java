package cn.xeblog.plugin.game.read;

import cn.hutool.core.lang.Validator;
import cn.xeblog.plugin.game.read.api.LegadoApi;
import cn.xeblog.plugin.game.read.entity.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class ReadConfig {
    /** 书架 */
    private List<Book> books;
    /** 热键 */
    private String[] key = new String[]{"Ctrl+↑", "Ctrl+↓", "Shift+↓", "Shift+↑"};
    /** legado阅读web服务的Host */
    private String legadoHost;
    /** 困难模式一次显示的字符数 */
    private int hardColumns = 50;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public String getLegadoHost() {
        return legadoHost;
    }

    public void setLegadoHost(String legadoHost) {
        this.legadoHost = legadoHost;
    }

    public int getHardColumns() {
        return hardColumns;
    }

    public void setHardColumns(int hardColumns) {
        this.hardColumns = hardColumns;
    }

    /**
     * 添加书籍
     * @param book 书籍
     * @return 是否添加成功
     */
    public boolean addBook(Book book) {
        if (this.books == null) {
            this.books = new ArrayList<>();
        }
        boolean bookExists = this.books.stream().anyMatch(b -> b.equals(book));
        if (!bookExists) {
            this.books.add(book);
        }
        return !bookExists;
    }

    /**
     * 删除书籍
     * @param index 索引
     * @return 是否删除成功
     */
    public boolean delBook(int index) {
        if (index >= 0 && index < this.books.size()) {
            this.books.remove(index);
            return true;
        }
        return false;
    }

    /**
     * 根据索引获取书籍
     * @param index 索引
     * @return 书籍
     */
    public Book getBook(int index) {
        if (index >= 0 && index < this.books.size()) {
            return this.books.get(index);
        }
        return null;
    }

    /**
     * 更新热键
     * @param key 热键
     * @param index 索引
     */
    public void setKeyMap(String key, int index){
        this.key[index] = key;
    }

    public boolean verifyLegadoHost() {
        return verifyLegadoHost(legadoHost);
    }

    public boolean verifyLegadoHost(String host) {
        if (Validator.isIpv4(host)) {
            LegadoApi api = new LegadoApi(host);
            return api.testConnect();
        }
        return false;
    }
}
