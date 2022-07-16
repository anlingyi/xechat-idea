package cn.xeblog.plugin.game.legado;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.legado.BookInfo;
import cn.xeblog.commons.entity.game.legado.LegadoChapter;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.novel.AutoNewlineTextPane;
import cn.xeblog.plugin.util.NotifyUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LYF
 * @date 2022-07-15
 */
@DoGame(Game.LEGADO)
public class LegadoRead extends AbstractGame<GameDTO> {

    // 开始界面
    private JPanel startPanel;
    // 书架界面
    private JPanel bookshelfPanel;
    // 刷新
    private JButton refreshBtn;
    // 目录页面
    private JPanel directoryPanel;
    // 阅读界面
    private JPanel readPanel;
    // 章节标题
    private JLabel title;
    // 小说正文
    private JTextPane textJta;
    // 上一页
    private JButton lastChapterJb;
    // 下一页
    private JButton nextChapterJb;
    // 当前服务
    private LegadoApi legadoApi;
    // 书籍列表
    private List<BookInfo> bookshelf = new ArrayList<>();
    // 当前书籍
    private BookInfo currentBook;
    // 小说目录
    private List<LegadoChapter> chapters = new ArrayList<>();
    // 当前章节
    private LegadoChapter currentChapter;
    private LegadoChapterUtil chapterUtil;
    // 是否为困难模式
    private boolean isHard = false;
    // 状态栏
    private StatusBar statusBar;

    @Override
    protected void init() {
        initStartPanel();
        statusBar = WindowManager.getInstance().getStatusBar(DataCache.project);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventPostProcessor(new KeyEventPostProcessor() {
            @Override
            public boolean postProcessKeyEvent(KeyEvent event) {
                if (event.getID() != KeyEvent.KEY_PRESSED || !isHard) {
                    return false;
                }

                if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_UP && statusBar != null) {
                    lastChapterJb.doClick();
                    return true;
                } else if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_DOWN && statusBar != null) {
                    nextChapterJb.doClick();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void start() {
        initBookshelf();
    }

    @Override
    public void handle(GameDTO body) { }

    // ========================UI========================

    private void showSomePanel(JComponent panel, int width, int height) {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(width, height));

        mainPanel.add(panel);
        mainPanel.updateUI();
    }

    private void initStartPanel() {
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        cleanMainPanel(150, 200);
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("Legado阅读!");
        title.setFont(new Font("", Font.BOLD, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        vBox.add(legadoRead());
        JButton exitButton = getExitButton();
        exitButton.setText("退出阅读");
        vBox.add(exitButton);

        mainPanel.updateUI();
    }

    private void showStartPanel() {
        showSomePanel(startPanel, 200, 200);
    }

    public void initBookshelf() {
        cleanMainPanel(320, 300);

        bookshelfPanel = new JPanel();
        bookshelfPanel.setLayout(new BorderLayout());
        bookshelfPanel.setBounds(10, 10, 300, 300);
        mainPanel.add(bookshelfPanel);

        // 服务器地址
        JTextField serviceJtf = new JTextField(18);
        // serviceJtf.setText("http://192.168.110.220:1122");
        bookshelfPanel.add(serviceJtf, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        refreshBtn = new JButton("刷新");
        buttonPanel.add(refreshBtn);
        JButton backBtn = new JButton("返回");
        backBtn.addActionListener(e -> showStartPanel());
        buttonPanel.add(backBtn);
        bookshelfPanel.add(buttonPanel, BorderLayout.CENTER);

        // 书籍列表
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 230));
        bookshelfPanel.add(scrollPane, BorderLayout.SOUTH);
        JBList<BookInfo> bookList = new JBList<>();
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setVisibleRowCount(8);
        bookList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JBList theList = (JBList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        currentBook = (BookInfo) theList.getModel().getElementAt(index);
                        chapters = legadoApi.getChapterList(currentBook.getBookUrl());
                        currentChapter = chapters.get(currentBook.getDurChapterIndex());
                        chapterUtil = new LegadoChapterUtil(legadoApi, currentChapter, isHard);
                        chapterUtil.saveBookProgress(currentBook, currentChapter);
                        showReadPanel();
                    }
                }
            }
        });
        scrollPane.setViewportView(bookList);
        bookList.setListData(bookshelf.toArray(BookInfo[]::new));

        // 刷新
        refreshBtn.addActionListener(e -> {
            String server = StrUtil.trimToEmpty(serviceJtf.getText());
            legadoApi = new LegadoApi(server);
            bookshelf = legadoApi.getBookshelf();
            bookList.setListData(bookshelf.toArray(BookInfo[]::new));
            bookList.updateUI();
        });

        mainPanel.updateUI();
    }

    private void showBookshelf() {
        showSomePanel(bookshelfPanel, 320, 300);
        refreshBtn.doClick();
    }

    public void initDirectoryPanel() {
        cleanMainPanel(320, 300);

        directoryPanel = new JPanel();
        directoryPanel.setLayout(new BorderLayout());
        directoryPanel.setBounds(10, 10, 300, 300);
        mainPanel.add(directoryPanel);

        JButton backBtn = new JButton("返回");
        backBtn.addActionListener(e -> showReadPanel());
        directoryPanel.add(backBtn, BorderLayout.NORTH);

        // 章节目录
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 230));
        directoryPanel.add(scrollPane, BorderLayout.CENTER);
        JBList<LegadoChapter> chapterList = new JBList<>();
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setVisibleRowCount(8);
        chapterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JBList theList = (JBList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        currentChapter = (LegadoChapter) theList.getModel().getElementAt(index);
                        chapterUtil = new LegadoChapterUtil(legadoApi, currentChapter, isHard);
                        chapterUtil.saveBookProgress(currentBook, currentChapter);
                        showReadPanel();
                    }
                }
            }
        });
        scrollPane.setViewportView(chapterList);
        chapterList.setListData(chapters.toArray(LegadoChapter[]::new));
    }

    private void showDirectoryPanel() {
        if (directoryPanel == null) {
            initDirectoryPanel();
        } else {
            showSomePanel(directoryPanel, 320, 300);
        }
    }

    private void initReadPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(370, 220));

        readPanel = new JPanel(new BorderLayout());
        readPanel.setBounds(10, 10, 350, 220);
        mainPanel.add(readPanel);

        title = new JLabel(currentChapter.getTitle());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        readPanel.add(title, BorderLayout.NORTH);

        textJta = new AutoNewlineTextPane();
        textJta.setEditable(false);
        textJta.setFont(new Font("", Font.PLAIN, 12));
        textJta.setText(chapterUtil.currentPage());
        textJta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JBScrollPane textJsp = new JBScrollPane(textJta);
        readPanel.add(textJsp, BorderLayout.CENTER);

        JPanel optPanel = new JPanel();
        lastChapterJb = new JButton("上一页");
        lastChapterJb.setPreferredSize(new Dimension(50, 30));
        nextChapterJb = new JButton("下一页");
        nextChapterJb.setPreferredSize(new Dimension(50, 30));

        lastChapterJb.addActionListener(e -> {
            String text = chapterUtil.lastPage();
            if (text.isEmpty()) {
                if (lastChapter()) {
                    text = chapterUtil.lastChapter(currentBook, currentChapter);
                } else {
                    lastChapterJb.setEnabled(false);
                    return;
                }
            }
            if (isHard) {
                statusBar.setInfo(text);
            } else {
                textJta.setText(text);
                textJta.updateUI();
            }
            nextChapterJb.setEnabled(true);
        });
        optPanel.add(lastChapterJb);

        nextChapterJb.addActionListener(e -> {
            String text = chapterUtil.nextPage();
            if (text.isEmpty()) {
                if (nextChapter()) {
                    text = chapterUtil.nextChapter(currentBook, currentChapter);
                } else {
                    nextChapterJb.setEnabled(false);
                    return;
                }
            }
            if (isHard) {
                statusBar.setInfo(text);
            } else {
                textJta.setText(text);
                textJta.updateUI();
            }
            lastChapterJb.setEnabled(true);
        });
        optPanel.add(nextChapterJb);

        // 键盘翻页
        textJta.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 37: // 方向键左
                        title.setText("Debug");
                        textJta.setText("");
                        break;
                    case 38: // 方向键上
                        lastChapterJb.doClick();
                        break;
                    case 39: // 方向键右
                        if (!isHard) {
                            title.setText(currentChapter.getTitle());
                            textJta.setText(chapterUtil.currentPage());
                        }
                        break;
                    case 40: // 方向键下
                        nextChapterJb.doClick();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) { }
        });

        // 切换模式
        JButton modelBtn = new JButton("困难");
        modelBtn.setPreferredSize(new Dimension(50, 30));
        modelBtn.addActionListener(e -> {
            isHard = !isHard;
            chapterUtil = new LegadoChapterUtil(legadoApi, currentChapter, isHard);
            if (isHard) {
                title.setText("Debug");
                textJta.setText("");
                modelBtn.setText("简单");
                statusBar.setInfo(chapterUtil.currentPage());
            } else {
                statusBar.setInfo("");
                title.setText(currentChapter.getTitle());
                textJta.setText(chapterUtil.currentPage());
                modelBtn.setText("困难");
            }
        });
        optPanel.add(modelBtn);

        JButton directoryBtn = new JButton("目录");
        directoryBtn.setPreferredSize(new Dimension(50, 30));
        directoryBtn.addActionListener(e -> showDirectoryPanel());
        optPanel.add(directoryBtn);

        JButton backJb = new JButton("书架");
        backJb.setPreferredSize(new Dimension(50, 30));
        backJb.addActionListener(e -> showBookshelf());
        optPanel.add(backJb);

        JButton exitJb = getExitButton();
        exitJb.setPreferredSize(new Dimension(50, 30));
        exitJb.setText("退出");
        optPanel.add(exitJb);

        readPanel.add(optPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    private void showReadPanel() {
        if (readPanel == null) {
            initReadPanel();
        } else {
            title.setText(currentChapter.getTitle());
            textJta.setText(chapterUtil.currentPage());
            showSomePanel(readPanel, 370, 220);
        }
    }

    private void cleanMainPanel(int width, int height) {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(width, height));
    }

    private JButton legadoRead() {
        JButton btn = new JButton("开始阅读");
        btn.addActionListener(e -> {
            start();
        });
        return btn;
    }

    // =================method===================

    private LegadoChapter getNextChapter() {
        int nextIndex = currentChapter.getIndex() + 1;
        if (nextIndex + 1 < chapters.size()) {
            return chapters.get(nextIndex);
        }
        return null;
    }

    private boolean lastChapter() {
        int lastIndex = currentChapter.getIndex() - 1;
        if (lastIndex >= 0) {
            currentChapter = chapters.get(lastIndex);
            title.setText(currentChapter.getTitle());
            return true;
        }
        return false;
    }

    private boolean nextChapter() {
        LegadoChapter nextChapter = getNextChapter();
        if (nextChapter != null) {
            currentChapter = nextChapter;
            title.setText(currentChapter.getTitle());
            return true;
        }
        return false;
    }
}
