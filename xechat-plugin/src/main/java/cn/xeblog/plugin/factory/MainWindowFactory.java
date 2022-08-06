package cn.xeblog.plugin.factory;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.plugin.action.InputAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

/**
 * @author anlingyi
 * @date 2020/5/26
 */
public class MainWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DataCache.project = project;

        JPanel mainPanel = MainWindow.getInstance().getMainPanel();
        mainPanel.addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                GlobalThreadPool.execute(() -> {
                    while (!InputAction.restCursor()) {
                    }
                });
            }
        });

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
