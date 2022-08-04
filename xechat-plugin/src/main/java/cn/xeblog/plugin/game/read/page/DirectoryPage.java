package cn.xeblog.plugin.game.read.page;

import cn.xeblog.plugin.game.read.entity.Book;
import cn.xeblog.plugin.game.read.entity.Chapter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class DirectoryPage implements IPage {
    private JPanel directoryPanel;
    private JBList<Chapter> chapterList;

    @Override
    public void show() {
        if (directoryPanel == null) {
            initUI();
        }
        refreshChapterData();
        UIManager.showPage(directoryPanel, 320, 300);
    }

    @Override
    public void initUI() {
        directoryPanel = new JPanel(new BorderLayout());
        directoryPanel.setBounds(10, 10, 300, 300);

        JButton backButton = new JButton("返回");
        backButton.addActionListener(e -> UIManager.readPage.show());
        directoryPanel.add(backButton, BorderLayout.NORTH);

        // 目录
        chapterList = new JBList<>();
        chapterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chapterList.setVisibleRowCount(8);
        chapterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JBList theList = (JBList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        UIManager.readPage.updateProgress(index, 2);
                        UIManager.readPage.show();
                    }
                }
            }
        });
        directoryPanel.add(new JBScrollPane(chapterList), BorderLayout.CENTER);
    }

    private void refreshChapterData() {
        Book book = UIManager.readPage.getBook();
        chapterList.setListData(book.getChapters().toArray(Chapter[]::new));
    }
}
