package cn.xeblog.plugin.game.read.error;

import cn.xeblog.plugin.util.AlertMessagesUtil;

import javax.swing.*;

/**
 * @author LYF
 * @date 2022-07-29
 */
public class LegadoApiException extends Exception {

    public LegadoApiException(String errorMsg) {
        super(errorMsg);
    }

    public static void throwException(String errorMsg) throws LegadoApiException {
        throw new LegadoApiException(errorMsg);
    }

    public void showErrorAlert() {
        SwingUtilities.invokeLater(() -> {
            AlertMessagesUtil.showErrorDialog("错误", this.getMessage());
        });
    }
}
