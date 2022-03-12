package cn.xeblog.plugin.game;

import cn.xeblog.commons.entity.Response;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public abstract class AbstractGame<T> {

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
    }

    public void over() {
        mainPanel.setVisible(false);
        ApplicationManager.getApplication().invokeLater(() -> mainPanel.removeAll());
    }
}
