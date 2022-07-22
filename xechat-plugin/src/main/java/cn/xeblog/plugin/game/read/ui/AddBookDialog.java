package cn.xeblog.plugin.game.read.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.read.api.LegadoApi;
import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.entity.BookType;
import cn.xeblog.plugin.game.read.entity.Chapter;
import cn.xeblog.plugin.game.read.entity.LegadoBook;
import cn.xeblog.plugin.game.read.util.TableDataUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import cn.xeblog.plugin.util.CharsetUtils;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-19
 */
public class AddBookDialog extends JDialog {
    /** 默认章节匹配正则 */
    private static final String DEFAULT_CHAPTER_PATTERN = "^.?第(.{1,5})[章部集卷节篇回].{0,24}";

    /** 主面板 */
    private JPanel mainPanel;
    /** 目录面板 */
    private JBLoadingPanel directoryCard;
    /** legado书架面板 */
    private JBLoadingPanel legadoBookAddCard;
    /** 书籍目录 */
    private JBList<Chapter> chapterList;
    /** 书籍章节 */
    private List<Chapter> chapters = new ArrayList<>();
    /** Legado书架 */
    private JBTable bookTable;
    /** Legado API */
    private LegadoApi api;
    /** Legado书籍 */
    private List<LegadoBook> bookshelf;

    public AddBookDialog() {
        initUI();
    }

    private void initUI() {
        this.pack();
        this.setSize(300, 300);
        this.setLocationRelativeTo(null);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBounds(10, 10, 300, 300);
        initStartCard();

        this.setContentPane(mainPanel);
        this.setModal(true);
        this.setTitle("添加书籍");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void initStartCard() {
        JPanel startCard = new JPanel();
        startCard.setBounds(0, 50, 300, 250);

        JButton addLocalBookButton = new JButton("添加本地书籍");
        addLocalBookButton.setPreferredSize(new Dimension(200, 30));
        addLocalBookButton.addActionListener(e -> initLocalBookAddCard());
        startCard.add(addLocalBookButton);

        JButton addLegadoBookButton = new JButton("添加Legado书籍");
        addLegadoBookButton.setPreferredSize(new Dimension(200, 30));
        addLegadoBookButton.addActionListener(e -> initLegadoBookAddCard());
        startCard.add(addLegadoBookButton);

        mainPanel.add(startCard, "StartCard");
        showCard("StartCard");
    }

    private void initLocalBookAddCard() {
        JPanel localBookAddCard = new JPanel();
        localBookAddCard.setBounds(0, 50, 300, 250);

        TextFieldWithBrowseButton filePath = new TextFieldWithBrowseButton();
        filePath.setPreferredSize(new Dimension(200, 30));
        filePath.addBrowseFolderListener("选择书籍", null, null, new FileChooserDescriptor(true, false, false, false, false, false));
        filePath.setEditable(false);
        localBookAddCard.add(filePath);

        JTextField name = new JTextField("书籍名");
        name.setPreferredSize(new Dimension(200, 30));
        localBookAddCard.add(name);
        JTextField author = new JTextField("作者");
        author.setPreferredSize(new Dimension(200, 30));
        localBookAddCard.add(author);

        JButton importButton = new JButton("生成目录");
        importButton.setPreferredSize(new Dimension(200, 30));
        importButton.addActionListener(e -> {
            String file = filePath.getText();
            if (StrUtil.isBlank(file) || !file.endsWith(".txt")) {
                AlertMessagesUtil.showInfoDialog("提示", "请选择正确的文件(必须为txt)");
                return;
            }
            Book book = new Book();
            book.setUrl(file);
            if (!"书籍名".equals(name.getText())) {
                book.setName(name.getText());
            }
            if (!"作者".equals(author.getText())) {
                book.setAuthor(author.getText());
            }
            book.setType(BookType.LOCAL);
            book.setChapterIndex(0);
            book.setCharset(CharsetUtils.charset(file));
            book.setChapterPattern(DEFAULT_CHAPTER_PATTERN);
            initDirectoryCard(book);
        });
        localBookAddCard.add(importButton);

        mainPanel.add(localBookAddCard, "LocalBookAddCard");
        showCard("LocalBookAddCard");
    }

    private void initLegadoBookAddCard() {
        api = new LegadoApi(DataCache.readConfig.getLegadoHost());
        legadoBookAddCard = new JBLoadingPanel(new BorderLayout(), DataCache.project);

        // 书籍列表
        bookTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bookTable.getSelectedRow();
                    LegadoBook book = bookshelf.get(row);
                    initDirectoryCard(book.toBook());
                }
            }
        });
        legadoBookAddCard.add(new JBScrollPane(bookTable), BorderLayout.CENTER);
        getLegadoBookshelf();

        mainPanel.add(legadoBookAddCard, "LegadoBookAddCard");
        showCard("LegadoBookAddCard");
    }

    private void initDirectoryCard(Book book) {
        directoryCard = new JBLoadingPanel(new BorderLayout(), DataCache.project);

        // 目录
        JScrollPane scrollPane = new JBScrollPane();
        directoryCard.add(scrollPane, BorderLayout.CENTER);
        chapterList = new JBList<>();
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setVisibleRowCount(8);
        scrollPane.setViewportView(chapterList);
        getDirectory(book);

        if (book.getType() == BookType.LOCAL) {
            JPanel optPanel = new JPanel();
            JTextField chapterPatternJtf = new JTextField(15);
            chapterPatternJtf.setText(DEFAULT_CHAPTER_PATTERN);
            optPanel.add(chapterPatternJtf);
            // 重新生成目录
            JButton reGenerateJb = new JButton("重新生成");
            reGenerateJb.addActionListener(e -> {
                book.setChapterPattern(chapterPatternJtf.getText());
                getDirectory(book);
            });
            optPanel.add(reGenerateJb);
            directoryCard.add(optPanel, BorderLayout.NORTH);
        }
        // 保存
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            if (CollUtil.isEmpty(chapters)) {
                AlertMessagesUtil.showWarningDialog("警告", "书籍章节目录为空");
                return;
            }
            book.setChapters(chapters);
            if (!DataCache.readConfig.addBook(book)) {
                AlertMessagesUtil.showInfoDialog("提示", "书籍已在书架中");
            }
            this.dispose();
        });
        directoryCard.add(saveButton, BorderLayout.SOUTH);

        mainPanel.add(directoryCard, "DirectoryCard");
        showCard("DirectoryCard");
    }

    private void showCard(String card) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, card);
    }

    private void getDirectory(Book book) {
        directoryCard.startLoading();
        new Thread(() -> {
            try {
                chapters = book.generateChapter();
                chapterList.setListData(chapters.toArray(Chapter[]::new));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    AlertMessagesUtil.showWarningDialog("警告", "章节目录解析失败！");
                });
            } finally {
                directoryCard.stopLoading();
            }
        }).start();
    }

    private void getLegadoBookshelf() {
        legadoBookAddCard.startLoading();
        new Thread(() -> {
            if (!DataCache.readConfig.verifyLegadoHost()) {
                SwingUtilities.invokeLater(() -> {
                    AlertMessagesUtil.showWarningDialog("警告", "请先配置正确的Legado服务HOST");
                });
                showCard("StartCard");
                return;
            }
            bookshelf = api.getBookshelf();
            bookTable.setModel(TableDataUtil.legadoBookToTableModel(bookshelf));
            legadoBookAddCard.stopLoading();
        }).start();
    }
}
