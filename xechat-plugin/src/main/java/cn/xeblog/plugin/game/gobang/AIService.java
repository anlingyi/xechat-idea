package cn.xeblog.plugin.game.gobang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2021/11/1 3:26 下午
 */
public interface AIService {

    /**
     * 获取AI棋位
     *
     * @param chessData 已下棋子数据
     * @param point     对手棋位
     * @return
     */
    Point getPoint(int[][] chessData, Point point);

    /**
     * AI配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class AIConfig {
        /**
         * 搜索深度
         */
        private int depth;
        /**
         * 最大启发式节点数
         */
        private int maxNodes;
        /**
         * debug
         */
        private boolean debug;
    }

}
