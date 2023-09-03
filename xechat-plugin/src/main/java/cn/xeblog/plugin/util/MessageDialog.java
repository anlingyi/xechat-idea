package cn.xeblog.plugin.util;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息对话框
 *
 * @author anlingyi
 * @date 2023/9/3 12:41 PM
 */
public class MessageDialog extends DialogWrapper {

    private String message;
    private String yesText;
    private String noText;

    public MessageDialog(String title, String message, String yesText, String noText) {
        super(true);
        this.message = message;
        this.yesText = yesText;
        this.noText = noText;

        setTitle(title);
        setResizable(true);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JLabel label = new JLabel("<html><body>" + message  +"</body></html>");
        label.setPreferredSize(new Dimension(180, 50));
        return label;
    }

    @Override
    protected Action @NotNull [] createActions() {
        List<Action> actionList = new ArrayList<>();
        if (StrUtil.isNotBlank(yesText)) {
            actionList.add(getOKAction());
            setOKButtonText(yesText);
        }
        if (StrUtil.isNotBlank(noText)) {
            actionList.add(getCancelAction());
            setCancelButtonText(noText);
        }
        return actionList.toArray(new Action[0]);
    }
}
