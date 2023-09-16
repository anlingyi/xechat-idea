package cn.xeblog.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏信息
 *
 * @author anlingyi
 * @date 2020/8/17
 */
@Getter
@AllArgsConstructor
public enum Game {
    GOBANG("五子棋", false),
    LANDLORDS("斗地主", false),
    NON_GLUTTONOUS_SNAKE("不贪吃蛇", false),
    GAME_2048("2048", false),
    SUDOKU("数独", false),
    PUSH_BOX("推箱子", false),
    CHINESE_CHESS("中国象棋", false),
    TETRIS("俄罗斯方块", false),
    MINESWEEPER("扫雷", false),
    ;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 是否必须要登录
     */
    private boolean requiredLogin;

    public static Game getGame(int index) {
        Game[] games = values();
        if (index < 0 || index >= games.length) {
            return null;
        }

        return games[index];
    }

}
