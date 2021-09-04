package cn.xeblog.plugin.listener;

import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author anlingyi
 * @date 2021/9/4 10:31 下午
 */
public class ProjectEventListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        MainWindow.getInstance();
    }

}
