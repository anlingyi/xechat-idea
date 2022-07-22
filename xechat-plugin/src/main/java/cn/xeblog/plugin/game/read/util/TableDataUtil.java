package cn.xeblog.plugin.game.read.util;

import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.entity.LegadoBook;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-19
 */
public class TableDataUtil {

    public static DefaultTableModel bookToTableModel(List<Book> books){
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"书名", "作者", "类型"});
        for (Book book : books) {
            String[] row = new String[]{book.getName(), book.getAuthor(), book.getType().getName()};
            model.addRow(row);
        }
        return model;
    }

    public static DefaultTableModel legadoBookToTableModel(List<LegadoBook> books){
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"书名", "作者"});
        for (LegadoBook book : books) {
            String[] row = new String[]{book.getName(), book.getAuthor()};
            model.addRow(row);
        }
        return model;
    }
}
