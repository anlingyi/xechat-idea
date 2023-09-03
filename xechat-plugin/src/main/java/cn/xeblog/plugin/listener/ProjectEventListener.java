package cn.xeblog.plugin.listener;

import cn.hutool.core.io.IoUtil;
import cn.xeblog.plugin.factory.MainWindowFactory;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.wm.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.InputStream;

/**
 * @author anlingyi
 * @date 2021/9/4 10:31 下午
 */
public class ProjectEventListener implements ProjectManagerListener {

    private static final String WINDOW_ID = "XEChat";

    @Override
    public void projectOpened(@NotNull Project project) {
        MainWindow.getInstance();
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        IdeFrame[] allProjectFrames = WindowManager.getInstance().getAllProjectFrames();
        Project otherProject = allProjectFrames[0].getProject();
        if (otherProject != null) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(otherProject);
            toolWindowManager.invokeLater(() -> {
                ToolWindow toolWindow = toolWindowManager.getToolWindow(WINDOW_ID);
                if (toolWindow != null) {
                    try {
                        Icon icon = toolWindow.getIcon();
                        if (icon == null) {
                            InputStream inputStream = ProjectEventListener.class.getResourceAsStream("/images/logo.png");
                            icon = new ImageIcon(IoUtil.readBytes(inputStream));
                        }
                        RegisterToolWindowTask xeChat = RegisterToolWindowTask.notClosable(WINDOW_ID, icon);
                        toolWindow.remove();
                        // TODO can't calculate origin icon (too deep in hierarchy), src: com.intellij.openapi.wm.impl.ToolWindowIcon@6a8b6070
                        toolWindow = toolWindowManager.registerToolWindow(xeChat);
                        new MainWindowFactory().createToolWindowContent(otherProject, toolWindow);
                    } catch (Exception e) {
                    }
                }
            });
        }
    }
}
