package cn.xeblog.plugin.game.read.page;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.read.api.LegadoApi;
import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.entity.BookType;
import cn.xeblog.plugin.game.read.entity.LegadoBook;
import cn.xeblog.plugin.game.read.error.LegadoApiException;
import cn.xeblog.plugin.game.read.ui.AutoNewlineTextPane;
import cn.xeblog.plugin.game.read.ui.HardReadWidget;
import cn.xeblog.plugin.game.read.util.ChapterUtil;
import cn.xeblog.plugin.game.read.util.KeyFormatUtil;
import cn.xeblog.plugin.util.NotifyUtils;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final HardReadWidget hardReadWidget = HardReadWidget.create();
    /** 上一页 */
    private JButton lastPageButton;
    /** 下一页 */
    private JButton nextPageButton;
    /** 当前阅读的书籍 */
    private final Book book;
    /** 章节阅读工具 */
    private ChapterUtil chapterUtil;
    /** 本页面是否显示 */
    private boolean isShow = false;
    /** 热键 */
    private KeyEventPostProcessor processor;
    /** 翻页定时器 */
    private ScheduledThreadPoolExecutor scheduler;

    private ReadPage(Book book) {
        this.book = book;
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
        boolean isInit = (readPanel == null);
        if (isInit) {
            readPanel = new JBLoadingPanel(new BorderLayout(), DataCache.project);
            readPanel.setBounds(10, 10, 350, 220);
        }
        UIManager.showPage(readPanel, 370, 220);
        readPanel.startLoading();
        ThreadUtil.execute(() -> {
            // 更新书籍进度
            if (book.getType() == BookType.LEGADO && !updateBookProgress()) {
                SwingUtilities.invokeLater(() -> {
                    backBookshelf();
                    readPanel = null;
                });
                return;
            }
            SwingUtilities.invokeLater(() -> {
                if (isInit) {
                    initUI();
                } else {
                    chapterUtil = new ChapterUtil(book);
                    setText();
                }
                if (readPanel != null) {
                    readPanel.stopLoading();
                    isShow = true;
                }
            });
        });
    }

    @Override
    public void initUI() {
        try {
            book.generateChapter();
            chapterUtil = new ChapterUtil(book);
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
                setText();
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

            // 添加热键监听
            addKeyEventPostProcessor();
        } catch (Exception e) {
            NotifyUtils.warn("", "章节目录解析失败！");
            readPanel = null;
            backBookshelf();
        }
    }

    public Book getBook() {
        return this.book;
    }

    /**
     * 更新读书进度
     * @param index 章节索引
     * @param mode mode 模式，0：初始化；1：最后一页；2：第一页
     */
    public void updateProgress(int index, int mode) {
        if (index >= 0 && index < book.getChapters().size()) {
            book.setChapterIndex(index);
            chapterUtil = new ChapterUtil(book, mode);
        }
    }

    /**
     * @return 上一页
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
            setText(text);
            nextPageButton.setEnabled(true);
        });
        return lastPageButton;
    }

    /**
     * @return 下一页
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
            setText(text);
            lastPageButton.setEnabled(true);
        });
        return nextPageButton;
    }

    /**
     * @return 模式切换
     */
    private JButton getModelButton() {
        JButton modelButton = new JButton();
        modelButton.setText(book.isHard() ? "简单" : "困难");
        modelButton.setPreferredSize(new Dimension(50, 30));
        modelButton.addActionListener(e -> {
            book.setHard(!book.isHard());
            chapterUtil.changeMode();
            if (book.isHard()) {
                title.setText("Debug");
                textJta.setText("");
                modelButton.setText("简单");
            } else {
                hardReadWidget.dispose();
                modelButton.setText("困难");
            }
            setText();
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
            setText("");
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
        bookshelfButton.addActionListener(e -> backBookshelf());
        return bookshelfButton;
    }

    /**
     * @return 首页
     */
    private JButton getHomeButton() {
        JButton exitButton = new JButton("首页");
        exitButton.setPreferredSize(new Dimension(50, 30));
        exitButton.addActionListener(e -> {
            hardReadWidget.dispose();
            UIManager.startPage.show();
            isShow = false;
        });
        return exitButton;
    }

    private boolean lastChapter() {
        int lastIndex = book.getChapterIndex() - 1;
        if (lastIndex >= 0) {
            updateProgress(lastIndex, 1);
            return true;
        }
        return false;
    }

    private boolean nextChapter() {
        int nextIndex = book.getChapterIndex() + 1;
        if (nextIndex < book.getChapters().size()) {
            updateProgress(nextIndex, 2);
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

            if (key.equals(keys[0])) {
                lastPageButton.doClick();
                return true;
            } else if (key.equals(keys[1])) {
                nextPageButton.doClick();
                return true;
            } else if (key.equals(keys[2])) {
                if (book.isHard()) {
                    hardReadWidget.setText("");
                } else {
                    title.setText("Debug");
                    textJta.setText("");
                }
                return true;
            } else if (key.equals(keys[3])) {
                setText();
                return true;
            } else if (key.equals(keys[4]) && scheduler == null) {
                scheduler = ThreadUtil.schedule(ThreadUtil.createScheduledExecutor(1), () -> {
                    if (nextPageButton.isEnabled()) {
                        nextPageButton.doClick();
                    }
                }, 1, DataCache.readConfig.getPageTurningSpeed(), TimeUnit.SECONDS, false);
                return true;
            } else if (key.equals(keys[5]) && scheduler != null) {
                scheduler.shutdownNow();
                scheduler = null;
                return true;
            }

            return false;
        };
        manager.addKeyEventPostProcessor(processor);
    }

    private void setText() {
        setText(chapterUtil.currentPage());
    }

    private void setText(String text) {
        if (book.isHard()) {
            if (!hardReadWidget.isInstalled()) {
                hardReadWidget.register();
            }
            hardReadWidget.setText(text);
        } else {
            title.setText(book.getCurrentChapter().getTitle());
            textJta.setText(text);
        }
    }

    private boolean updateBookProgress() {
        try {
            LegadoApi legadoApi = new LegadoApi(DataCache.readConfig.getLegadoHost());
            Map<String, LegadoBook> legadoBookMap = legadoApi.getBookshelf()
                    .stream().collect(Collectors.toMap(LegadoBook::getBookUrl, b -> b));
            LegadoBook legadoBook = legadoBookMap.get(book.getUrl());
            if (legadoBook != null) {
                book.setUrl(legadoBook.getBookUrl());
                book.setChapterIndex(legadoBook.getDurChapterIndex());
                book.setChapterPos(legadoBook.getDurChapterPos());
            }
            return true;
        } catch (LegadoApiException e) {
            new LegadoApiException("书籍进度同步失败！").showErrorAlert();
            return false;
        }
    }

    private void backBookshelf() {
        hardReadWidget.dispose();
        UIManager.bookshelfPage.show();
        isShow = false;
    }
}
