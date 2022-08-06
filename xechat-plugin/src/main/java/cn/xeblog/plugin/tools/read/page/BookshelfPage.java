package cn.xeblog.plugin.tools.read.page;

import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.read.entity.Book;
import cn.xeblog.plugin.tools.read.ui.AddBookDialog;
import cn.xeblog.plugin.tools.read.util.TableDataUtil;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class BookshelfPage implements IPage {

    private JPanel bookshelfPanel;
    private JBTable bookTable;

    @Override
    public void show() {
        if (bookshelfPanel == null) {
            initUI();
        }
        cn.xeblog.plugin.tools.read.page.UIManager.showPage(bookshelfPanel, 320, 300);
    }

    @Override
    public void initUI() {
        bookshelfPanel = new JPanel();
        bookshelfPanel.setLayout(new BorderLayout());
        bookshelfPanel.setBounds(10, 10, 300, 300);

        // 初始化书架
        initBookTable();
        bookshelfPanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        // 操作
        JPanel optPanel = new JPanel();
        optPanel.add(getDelButton());
        optPanel.add(getAddButton());
        optPanel.add(getBackButton());
        bookshelfPanel.add(optPanel, BorderLayout.NORTH);
    }

    /**
     * 初始化书架
     */
    private void initBookTable() {
        bookTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookDataRefresh();
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bookTable.getSelectedRow();
                    Book book = DataCache.readConfig.getBook(row);
                    cn.xeblog.plugin.tools.read.page.UIManager.readPage = ReadPage.getInstance(book);
                    cn.xeblog.plugin.tools.read.page.UIManager.readPage.show();
                }
            }
        });
    }

    /**
     * 删除书籍按钮
     */
    private JButton getDelButton() {
        JButton delButton = new JButton("删除");
        delButton.addActionListener(e -> {
            if (DataCache.readConfig.delBook(bookTable.getSelectedRow())) {
                bookDataRefresh();
            }
        });
        return delButton;
    }

    /**
     * 添加书籍按钮
     */
    private JButton getAddButton() {
        JButton addButton = new JButton("添加");
        addButton.addActionListener(e -> {
            AddBookDialog dialog = new AddBookDialog();
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    bookDataRefresh();
                }
            });
        });
        return addButton;
    }

    /**
     * 返回按钮
     */
    private JButton getBackButton() {
        JButton addButton = new JButton("返回");
        addButton.addActionListener(e -> UIManager.startPage.show());
        return addButton;
    }

    /**
     * 刷新书架数据
     */
    private void bookDataRefresh() {
        if (DataCache.readConfig != null && DataCache.readConfig.getBooks() != null) {
            bookTable.setModel(TableDataUtil.bookToTableModel(DataCache.readConfig.getBooks()));
        }
    }
}
