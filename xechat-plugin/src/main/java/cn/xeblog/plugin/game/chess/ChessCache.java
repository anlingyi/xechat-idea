package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.game.chess.ChessDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 中国象棋缓存<p>
 *     用于缓存游戏数据
 * @author Hao.
 * @version 1.0
 * @since 2022/9/5 10:18
 */
public class ChessCache {

    /** 当前模式 */
    public Mode currentMode;

    /** 当前玩家 */
    public Player currentPlayer;

    /** 当前对战 */
    public Battle currentBattle;

    /** 当前界面 用途：后期可设置我方和对方不同界面 */
    public ChessDTO.UI currentUI;

    // 标记是否已下棋子
    public boolean put;

    public enum Mode {
        ONLINE, OFFLINE
    }

    @Getter
    @AllArgsConstructor
    public enum Player {
        RED("红方先走", 255), BLACK("黑方殿后", 0)

        ;

        private String name;

        private int value;
    }

    @Getter
    @AllArgsConstructor
    public enum Battle {
        PVP("人人对战", 1), PVC("人机对战", 0)

        ;

        private String name;

        private int value;
    }
}
