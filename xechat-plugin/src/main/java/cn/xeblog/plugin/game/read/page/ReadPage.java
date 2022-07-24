package cn.xeblog.plugin.game.read.page;

import cn.hutool.core.collection.CollUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.ui.AutoNewlineTextPane;
import cn.xeblog.plugin.game.read.util.ChapterUtil;
import cn.xeblog.plugin.game.read.util.KeyFormatUtil;
import cn.xeblog.plugin.util.NotifyUtils;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class ReadPage implements IPage {
    /** 阅读面板 */
    private JBLoadingPanel readPanel;
    /** 章节标题 */
    private JLabel title;
    /** 小说正文 */
    private JTextPane textJta;
    /** 上一页 */
    private JButton lastPageButton;
    /** 下一页 */
    private JButton nextPageButton;
    /** 当前阅读的书籍 */
    private final Book book;
    /** 章节阅读工具 */
    private ChapterUtil chapterUtil;
    /** 困难模式 */
    private boolean isHard = false;
    /** 状态栏 */
    private final StatusBar statusBar;
    /** 本页面是否显示 */
    private boolean isShow = false;
    /** 热键 */
    private KeyEventPostProcessor processor;

    private ReadPage(Book book) {
        this.book = book;
        this.statusBar = WindowManager.getInstance().getStatusBar(DataCache.project);
    }

    public static ReadPage getInstance(Book book) {
        ReadPage instance = UIManager.readPage;
        if (instance != null) {
            if (book.equals(UIManager.readPage.getBook())) {
                return instance;
            }

            instance.cleanProcessor();
        }

        return new ReadPage(book);
    }

    private void cleanProcessor() {
        if (processor == null) {
            return;
        }

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removeKeyEventPostProcessor(processor);
    }

    @Override
    public void show() {
        if (readPanel == null) {
            initUI();
        } else {
            // 添加热键监听
            addKeyEventPostProcessor();
        }

        UIManager.showPage(readPanel, 370, 220);
        this.isShow = true;
    }

    @Override
    public void initUI() {
        readPanel = new JBLoadingPanel(new BorderLayout(), DataCache.project);
        readPanel.setBounds(10, 10, 350, 220);

        readPanel.startLoading();
        new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                try {
                    book.generateChapter();
                    chapterUtil = new ChapterUtil(book, isHard);
                    createUIContent();
                    readPanel.stopLoading();
                } catch (Exception e) {
                    NotifyUtils.warn("", "章节目录解析失败！");
                    readPanel = null;
                    UIManager.bookshelfPage.show();
                }
                return null;
            }
        }.execute();
    }

    public Book getBook() {
        return this.book;
    }

    public void updateProgress(int index, boolean isLastPage) {
        if (index >= 0 && index < book.getChapters().size()) {
            book.setChapterIndex(index);
            if (!isHard) {
                title.setText(book.getCurrentChapter().getTitle());
            }
            chapterUtil = new ChapterUtil(book, isHard, isLastPage);
        }
    }

    private void createUIContent() {
        // 章节标题
        title = new JLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        readPanel.add(title, BorderLayout.NORTH);
        // 正文
        textJta = new AutoNewlineTextPane();
        textJta.setEditable(false);
        textJta.setFont(new Font("", Font.PLAIN, 12));
        if (chapterUtil != null && CollUtil.isNotEmpty(book.getChapters())) {
            title.setText(book.getCurrentChapter().getTitle());
            textJta.setText(chapterUtil.currentPage());
        }
        textJta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        readPanel.add(new JBScrollPane(textJta), BorderLayout.CENTER);

        // 操作面板
        JPanel optPanel = new JPanel();
        optPanel.add(getLastPageButton());
        optPanel.add(getNextPageButton());
        optPanel.add(getModelButton());
        optPanel.add(getDirectoryButton());
        optPanel.add(getBookshelfButton());
        optPanel.add(getHomeButton());
        readPanel.add(optPanel, BorderLayout.SOUTH);
        readPanel.updateUI();

        addKeyEventPostProcessor();
    }

    /**
     * @return 下一页
     */
    private JButton getLastPageButton() {
        lastPageButton = new JButton("上一页");
        lastPageButton.setPreferredSize(new Dimension(50, 30));
        lastPageButton.addActionListener(e -> {
            String text = chapterUtil.lastPage();
            if (text.isEmpty()) {
                if (lastChapter()) {
                    text = chapterUtil.currentPage();
                } else {
                    lastPageButton.setEnabled(false);
                    return;
                }
            }
            if (isHard) {
                statusBar.setInfo(text);
            } else {
                textJta.setText(text);
                textJta.updateUI();
            }
            nextPageButton.setEnabled(true);
        });
        return lastPageButton;
    }

    /**
     * @return 上一页
     */
    private JButton getNextPageButton() {
        nextPageButton = new JButton("下一页");
        nextPageButton.setPreferredSize(new Dimension(50, 30));
        nextPageButton.addActionListener(e -> {
            String text = chapterUtil.nextPage();
            if (text.isEmpty()) {
                if (nextChapter()) {
                    text = chapterUtil.currentPage();
                } else {
                    nextPageButton.setEnabled(false);
                    return;
                }
            }
            if (isHard) {
                statusBar.setInfo(text);
            } else {
                textJta.setText(text);
                textJta.updateUI();
            }
            lastPageButton.setEnabled(true);
        });
        return nextPageButton;
    }

    /**
     * @return 模式切换
     */
    private JButton getModelButton() {
        JButton modelButton = new JButton("困难");
        modelButton.setPreferredSize(new Dimension(50, 30));
        modelButton.addActionListener(e -> {
            isHard = !isHard;
            chapterUtil = new ChapterUtil(book, isHard);
            if (isHard) {
                title.setText("Debug");
                textJta.setText("");
                modelButton.setText("简单");
                statusBar.setInfo(chapterUtil.currentPage());
            } else {
                statusBar.setInfo("");
                title.setText(this.book.getCurrentChapter().getTitle());
                textJta.setText(chapterUtil.currentPage());
                modelButton.setText("困难");
            }
        });
        return modelButton;
    }

    /**
     * @return 目录
     */
    private JButton getDirectoryButton() {
        JButton directoryButton = new JButton("目录");
        directoryButton.setPreferredSize(new Dimension(50, 30));
        directoryButton.addActionListener(e -> {
            statusBar.setInfo("");
            UIManager.directoryPage.show();
            isShow = false;
        });
        return directoryButton;
    }

    /**
     * @return 书架
     */
    private JButton getBookshelfButton() {
        JButton bookshelfButton = new JButton("书架");
        bookshelfButton.setPreferredSize(new Dimension(50, 30));
        bookshelfButton.addActionListener(e -> {
            statusBar.setInfo("");
            UIManager.bookshelfPage.show();
            isShow = false;
        });
        return bookshelfButton;
    }

    /**
     * @return 首页
     */
    private JButton getHomeButton() {
        JButton exitButton = new JButton("首页");
        exitButton.setPreferredSize(new Dimension(50, 30));
        exitButton.addActionListener(e -> {
            statusBar.setInfo("");
            UIManager.startPage.show();
            isShow = false;
        });
        return exitButton;
    }

    private boolean lastChapter() {
        int lastIndex = book.getChapterIndex() - 1;
        if (lastIndex >= 0) {
            updateProgress(lastIndex, true);
            return true;
        }
        return false;
    }

    private boolean nextChapter() {
        int nextIndex = book.getChapterIndex() + 1;
        if (nextIndex < book.getChapters().size()) {
            updateProgress(nextIndex, false);
            return true;
        }
        return false;
    }

    private void addKeyEventPostProcessor() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        if (processor != null) {
            manager.removeKeyEventPostProcessor(processor);
        }

        processor = event -> {
            if (event.getID() != KeyEvent.KEY_PRESSED || !isShow) {
                return false;
            }

            String key = KeyFormatUtil.format(event);
            String[] keys = DataCache.readConfig.getKey();
            if (event.isControlDown()) {
                if (key.equals(keys[0])) {
                    lastPageButton.doClick();
                } else if (key.equals(keys[1])) {
                    nextPageButton.doClick();
                } else if (key.equals(keys[2])) {
                    if (isHard) {
                        statusBar.setInfo("");
                    } else {
                        title.setText("Debug");
                        textJta.setText("");
                    }
                } else if (key.equals(keys[3])) {
                    if (isHard) {
                        statusBar.setInfo(chapterUtil.currentPage());
                    } else {
                        title.setText(book.getCurrentChapter().getTitle());
                        textJta.setText(chapterUtil.currentPage());
                    }
                } else {
                    return false;
                }
                return true;
            }
            return false;
        };
        manager.addKeyEventPostProcessor(processor);
    }
}
