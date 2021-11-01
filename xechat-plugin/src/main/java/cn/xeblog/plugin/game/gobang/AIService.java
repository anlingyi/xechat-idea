package cn.xeblog.plugin.game.gobang;

/**
 * @author anlingyi
 * @date 2021/11/1 3:26 下午
 */
public interface AIService {

    Gobang.Point getPoint(int[][] chessData, int type);

}
