package cn.xeblog.plugin.setting;

import cn.xeblog.commons.constants.Commons;
import cn.xeblog.plugin.persistence.PersistenceService;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author anlingyi
 * @date 2022/7/23 5:58 PM
 */
public class PluginConfigSetting implements SearchableConfigurable {

    private JPasswordField tokenInput;

    @Override
    public @NotNull
    @NonNls String getId() {
        return Commons.KEY_PREFIX + "setting";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return null;
    }

    @Override
    public @Nullable JComponent createComponent() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(null);

        JLabel tokenLabel = new JLabel("Token:");
        tokenLabel.setBounds(10, 0, 50, 30);
        configPanel.add(tokenLabel);

        tokenInput = new JPasswordField();
        tokenInput.setBounds(60, 0, 300, 30);
        tokenInput.setText(PersistenceService.getData().getToken());
        configPanel.add(tokenInput);

        return configPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (tokenInput != null) {
            PersistenceService.getData().setToken(tokenInput.getText());
        }
        System.out.println(PersistenceService.getData().getToken());
    }

}
