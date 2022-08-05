package cn.xeblog.plugin.tools.read.entity;

/**
 * @author LYF
 * @date 2022-07-18
 */
public enum BookType {
    /**
     * 本地书籍
     */
    LOCAL("本地"),
    /**
     * legado书籍
     */
    LEGADO("legado");

    private final String name;
    BookType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
