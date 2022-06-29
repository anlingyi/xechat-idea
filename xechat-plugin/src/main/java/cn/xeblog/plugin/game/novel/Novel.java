package cn.xeblog.plugin.game.novel;

import cn.xeblog.commons.entity.game.novel.NovelDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.util.CharsetUtils;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;

/**
 * @author LYF
 * @date 2022-06-17
 */
@Slf4j
@DoGame(Game.NOVEL)
public class Novel extends AbstractGame<NovelDTO> {

    // 开始界面
    private JPanel startPanel;
    // 目录页面
    private JPanel directoryPanel;
    // 提示框
    private JLabel tipsLabel;
    // 章节标题
    private JLabel title;
    // 小说正文
    private JTextPane textJta;
    // 小说文件路径
    private String novelFilePath;
    // 小说文件编码
    private Charset charset = Charset.forName("GBK");
    // 小说目录
    private List<Chapter> novelChapters = new ArrayList<>();
    // 当前章节
    private Chapter currentChapter;
    private ChapterUtil chapterUtil;
    // 默认章节匹配正则
    private static final String DEFAULT_CHAPTER_PATTERN = "^.?第(.{1,5})[章部集卷节篇回].{0,24}";

    @Override
    protected void init() {
        initStartPanel();
    }

    @Override
    protected void start() {
        // 判断小说文件编码（有错误几率）
        charset = CharsetUtils.charset(novelFilePath);
        if (generateDirectory(DEFAULT_CHAPTER_PATTERN)) {
            initDirectoryPanel();
        } else {
            showStartPanel();
        }
    }

    @Override
    public void handle(NovelDTO body) { }

    // =================UI===================

    private void showSomePanel(JComponent panel, int width, int height) {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(width, height));

        mainPanel.add(panel);
        mainPanel.updateUI();
    }

    private void initStartPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 200));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("阅读!");
        title.setFont(new Font("", Font.BOLD, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getOpenFileButton());
        JButton exitButton = getExitButton();
        exitButton.setText("退出阅读");
        vBox.add(exitButton);

        tipsLabel = new JLabel();
        tipsLabel.setHorizontalAlignment(JLabel.CENTER);
        tipsLabel.setFont(new Font("", Font.BOLD, 13));
        tipsLabel.setForeground(JBColor.RED);
        startPanel.add(tipsLabel);

        mainPanel.updateUI();
    }

    private void showStartPanel() {
        showSomePanel(startPanel, 200, 200);
    }

    private void showTips(String tips) {
        tipsLabel.setText(tips);
        tipsLabel.updateUI();
        invoke(() -> {
            tipsLabel.setText("");
            tipsLabel.updateUI();
        }, 2000);
    }

    private void initDirectoryPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(320, 300));

        directoryPanel = new JPanel();
        directoryPanel.setLayout(new BorderLayout());
        directoryPanel.setBounds(10, 10, 300, 300);
        mainPanel.add(directoryPanel);

        JTextField chapterPatternJtf = new JTextField(24);
        chapterPatternJtf.setText(DEFAULT_CHAPTER_PATTERN);
        directoryPanel.add(chapterPatternJtf, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton reGenerateJb = new JButton("重新生成");
        buttonPanel.add(reGenerateJb);
        JButton backJb = new JButton("返回");
        backJb.addActionListener(e -> showStartPanel());
        buttonPanel.add(backJb);
        directoryPanel.add(buttonPanel, BorderLayout.CENTER);

        // 目录
        JScrollPane scrollPane = new JBScrollPane();
        scrollPane.setPreferredSize(new Dimension(0, 230));
        directoryPanel.add(scrollPane, BorderLayout.SOUTH);
        JBList<Chapter> chapterList = new JBList<>();
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setVisibleRowCount(8);
        chapterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JBList theList = (JBList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        currentChapter = (Chapter) theList.getModel().getElementAt(index);
                        chapterUtil = new ChapterUtil(
                                novelFilePath, charset, currentChapter.getOffset(), getChapterEnd());
                        initReadPanel();
                    }
                }
            }
        });
        scrollPane.setViewportView(chapterList);
        chapterList.setListData(novelChapters.toArray(Chapter[]::new));

        // 重新生成目录
        reGenerateJb.addActionListener(e -> {
            generateDirectory(chapterPatternJtf.getText());
            chapterList.setListData(novelChapters.toArray(Chapter[]::new));
        });

        mainPanel.updateUI();
    }

    private void showDirectoryPanel() {
        showSomePanel(directoryPanel, 320, 300);
    }

    private void initReadPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(370, 220));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBounds(10, 10, 350, 220);
        mainPanel.add(textPanel);

        title = new JLabel(currentChapter.getTitle());
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        textPanel.add(title, BorderLayout.NORTH);

        textJta = new AutoNewlineTextPane();
        textJta.setEditable(false);
        textJta.setFont(new Font("", Font.PLAIN, 12));
        textJta.setText(chapterUtil.currentPage());
        textJta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JBScrollPane textJsp = new JBScrollPane(textJta);
        textPanel.add(textJsp, BorderLayout.CENTER);

        JPanel optPanel = new JPanel();
        JButton lastChapterJb = new JButton("上一页");
        JButton nextChapterJb = new JButton("下一页");

        lastChapterJb.addActionListener(e -> {
            String text = chapterUtil.lastPage();
            if (text.isEmpty()) {
                if (lastChapter()) {
                    text = chapterUtil.lastChapter(currentChapter.getOffset(), getChapterEnd());
                } else {
                    lastChapterJb.setEnabled(false);
                    return;
                }
            }
            textJta.setText(text);
            textJta.updateUI();
            nextChapterJb.setEnabled(true);
        });
        optPanel.add(lastChapterJb);

        nextChapterJb.addActionListener(e -> {
            String text = chapterUtil.nextPage();
            if (text.isEmpty()) {
                if (nextChapter()) {
                    text = chapterUtil.nextChapter(currentChapter.getOffset(), getChapterEnd());
                } else {
                    nextChapterJb.setEnabled(false);
                    return;
                }
            }
            textJta.setText(text);
            textJta.updateUI();
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
                        title.setText(currentChapter.getTitle());
                        textJta.setText(chapterUtil.currentPage());
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

        JButton backJb = new JButton("返回");
        backJb.addActionListener(e -> showDirectoryPanel());
        optPanel.add(backJb);

        JButton exitJb = getExitButton();
        exitJb.setText("退出");
        optPanel.add(exitJb);

        textPanel.add(optPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    private JButton getOpenFileButton() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        JButton jb = new JButton("打开文件");
        jb.addActionListener(e -> {
            int val = jfc.showOpenDialog(null);
            if (val == JFileChooser.APPROVE_OPTION) {
                novelFilePath = jfc.getSelectedFile().toString();
                if (!novelFilePath.toLowerCase().endsWith(".txt")) {
                    showTips("非文本文件!");
                } else if (new File(novelFilePath).exists()) {
                    start();
                }
            }
        });
        return jb;
    }

    // =================method===================

    private boolean generateDirectory(String pattern) {
        try {
            novelChapters = Chapter.generateChapter(novelFilePath, charset, pattern);
            return true;
        } catch (Exception e) {
            log.error("小说文件读取错误：{}", e.getMessage());
            showTips("文件读取出错，请重新选择文件");
        }
        return false;
    }

    private Chapter getNextChapter() {
        int nextIndex = currentChapter.getIndex() + 1;
        if (nextIndex + 1 < novelChapters.size()) {
            return novelChapters.get(nextIndex);
        }
        return null;
    }

    private boolean lastChapter() {
        int lastIndex = currentChapter.getIndex() - 1;
        if (lastIndex >= 0) {
            currentChapter = novelChapters.get(lastIndex);
            title.setText(currentChapter.getTitle());
            return true;
        }
        return false;
    }

    private boolean nextChapter() {
        Chapter nextChapter = getNextChapter();
        if (nextChapter != null) {
            currentChapter = nextChapter;
            title.setText(currentChapter.getTitle());
            return true;
        }
        return false;
    }

    private long getChapterEnd() {
        Chapter nextCurrent = getNextChapter();
        return nextCurrent != null ? nextCurrent.getOffset() : 0L;
    }

}
