package cn.xeblog.plugin.tools;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;

/**
 * @author anlingyi
 * @date 2022/8/5 6:18 上午
 */
public abstract class AbstractPanelComponent {

    protected final JPanel mainPanel;

    public AbstractPanelComponent(boolean initialized) {
        this.mainPanel = MainWindow.getInstance().getRightPanel();
        if (initialized) {
            initialized();
        }
    }

    private void initialized() {
        init();
        ConsoleAction.gotoConsoleLow(true);
    }

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 结束
     */
    public void over() {
        invoke(() -> {
            mainPanel.setVisible(false);
            mainPanel.removeAll();
            mainPanel.updateUI();
        });
    }

    protected JButton getExitButton() {
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> Command.OVER.exec());
        return exitButton;
    }

    protected final void invoke(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable);
    }

    protected final void invoke(Runnable runnable, long millis) {
        GlobalThreadPool.execute(() -> {
            spinMoment(millis);
            invoke(runnable);
        });
    }

    protected void spinMoment(long millis) {
        ThreadUtils.spinMoment(millis);
    }

}
