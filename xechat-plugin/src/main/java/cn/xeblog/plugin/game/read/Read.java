package cn.xeblog.plugin.game.read;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.read.page.*;

/**
 * @author LYF
 * @date 2022-07-18
 */
@DoGame(Game.READ)
public class Read extends AbstractGame<GameDTO> {
    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);

        UIManager.mainPanel = mainPanel;
        UIManager.startPage = new StartPage();
        UIManager.settingPage = new SettingPage();
        UIManager.bookshelfPage = new BookshelfPage();
        UIManager.directoryPage = new DirectoryPage();

        UIManager.startPage.show();
    }

    @Override
    protected void start() {

    }

    @Override
    public void handle(GameDTO body) { }
}
