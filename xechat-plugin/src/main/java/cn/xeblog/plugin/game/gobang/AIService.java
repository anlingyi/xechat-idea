package cn.xeblog.plugin.game.gobang;

/**
 * @author anlingyi
 * @date 2021/11/1 3:26 下午
 */
public interface AIService {

    /**
     * 获取AI棋位
     *
     * @param chessData 当前棋子数据
     * @param point     对手棋位
     * @return
     */
    Gobang.Point getPoint(int[][] chessData, Gobang.Point point);

}
