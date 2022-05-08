package cn.xeblog.plugin.game;

import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public abstract class AbstractGame<T> {

    protected final JPanel mainPanel;

    public AbstractGame() {
        this.mainPanel = MainWindow.getInstance().getRightPanel();
        init();
        start();
    }

    public abstract void handle(Response<T> response);

    public void sendMsg(GameDTO body) {
        MessageAction.send(body, Action.GAME);
    }

    protected void init() {
        mainPanel.setLayout(new FlowLayout());
    }

    protected void start() {
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    public void over() {
        mainPanel.setVisible(false);
        ApplicationManager.getApplication().invokeLater(() -> mainPanel.removeAll());
    }
}
