package cn.xeblog.plugin.game.uno;


import cn.xeblog.plugin.game.uno.application.GameAppService;
import cn.xeblog.plugin.game.uno.ui.AppFrame;

import javax.swing.*;

public class UnoApp {

    public static void main(String[] args) {
        var appService = new GameAppService();

        SwingUtilities.invokeLater(() -> {
            new AppFrame(appService);
            System.out.println("UNO app is launched");
        });
    }
}
