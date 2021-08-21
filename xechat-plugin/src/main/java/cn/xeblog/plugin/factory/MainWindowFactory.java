package cn.xeblog.plugin.factory;

import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author anlingyi
 * @date 2020/5/26
 */
public class MainWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(MainWindow.getInstance().getComponent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
