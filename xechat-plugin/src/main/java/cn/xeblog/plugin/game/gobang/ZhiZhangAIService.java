package cn.xeblog.plugin.game.gobang;

import cn.hutool.core.util.RandomUtil;

/**
 * @author anlingyi
 * @date 2021/11/1 3:31 下午
 */
public class ZhiZhangAIService implements AIService {

    @Override
    public Gobang.Point getPoint(int[][] chessData, Gobang.Point point) {
        int type = 3 - point.type;
        int size = chessData.length;
        int x;
        int y;

        do {
            x = RandomUtil.randomInt(size);
            y = RandomUtil.randomInt(size);
        } while (chessData[x][y] != 0);

        return new Gobang.Point(x, y, type);
    }

}
