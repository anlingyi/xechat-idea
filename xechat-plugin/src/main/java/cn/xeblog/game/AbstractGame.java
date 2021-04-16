package cn.xeblog.game;

import cn.xeblog.entity.Response;

import javax.swing.*;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public abstract class AbstractGame<T> extends JPanel {

    protected static JPanel mainPanel;

    public static void setMainPanel(JPanel mainPanel) {
        AbstractGame.mainPanel = mainPanel;
    }

    public AbstractGame() {
        init();
        start();
    }

    public abstract void handle(Response<T> response);

    protected void init() {
        mainPanel.setLayout(new FlowLayout());
    }

    public void start() {
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        setEnabled(true);
        setVisible(true);
    }

    public void over() {
        mainPanel.setVisible(false);
        mainPanel.removeAll();
    }
}
