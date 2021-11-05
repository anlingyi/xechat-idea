package cn.xeblog.plugin.game.gobang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anlingyi
 * @date 2021/11/1 3:31 下午
 */
public class ZhiZhangAIService implements AIService {

    private int[][] chessData;
    private int size;

    @Override
    public Gobang.Point getPoint(int[][] chessData, Gobang.Point point, boolean started) {
        this.size = chessData.length;
        this.chessData = Arrays.copyOf(chessData, size);
        int type = 3 - point.type;

        if (started) {
            int center = Math.round(size / 2);
            return new Gobang.Point(center, center, type);
        }

        Gobang.Point curPoint = null;
        int maxScore = -1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.chessData[i][j] != 0) {
                    continue;
                }

                Gobang.Point p = new Gobang.Point(i, j, type);
                int score = evaluate(p);
                if (score > maxScore) {
                    maxScore = score;
                    curPoint = p;
                }
            }
        }

        return curPoint;
    }

    /**
     * 各棋型分值
     */
    private static final Map<String, Integer> SCORE = new HashMap<>();

    static {
        SCORE.put("11111", 999999);
        SCORE.put("011110", 10000);
        SCORE.put("01110", 3000);
        SCORE.put("010110", 3000);
        SCORE.put("011010", 3000);
        SCORE.put("0111010", 5000);
        SCORE.put("111010", 5000);
        SCORE.put("0101110", 5000);
        SCORE.put("0110110", 3000);
        SCORE.put("11110", 1000);
        SCORE.put("011112", 1000);
        SCORE.put("211110", 1000);
        SCORE.put("11011", 1000);
        SCORE.put("10111", 1000);
        SCORE.put("11101", 1000);
        SCORE.put("001100", 200);
        SCORE.put("0110", 150);
        SCORE.put("001010", 120);
        SCORE.put("010100", 120);
        SCORE.put("000100", 20);
        SCORE.put("001000", 20);
    }

    /**
     * 当前棋位分数评估
     *
     * @param point 当前棋位
     * @return
     */
    private int evaluate(Gobang.Point point) {
        int score = 0;
        for (int i = 1; i < 5; i++) {
            String str = getSituation(point, i);
            score += getScore(str);
            score += getScore(getSituation(new Gobang.Point(point.x, point.y, 3 - point.type), i));
        }
        return score;
    }

    private int getScore(String chess) {
        for (String s : SCORE.keySet()) {
            if (chess.contains(s)) {
                return SCORE.get(s);
            }
        }
        return 0;
    }

    /**
     * 获取棋位局势
     *
     * @param point     当前棋位
     * @param direction 方向 1.横 2.纵 3.左斜 4.右斜
     * @return
     */
    private String getSituation(Gobang.Point point, int direction) {
        direction = direction * 2 - 1;
        StringBuilder sb = new StringBuilder();
        appendChess(sb, point, direction, 4);
        appendChess(sb, point, direction, 3);
        appendChess(sb, point, direction, 2);
        appendChess(sb, point, direction, 1);
        sb.append(1);
        appendChess(sb, point, direction + 1, 1);
        appendChess(sb, point, direction + 1, 2);
        appendChess(sb, point, direction + 1, 3);
        appendChess(sb, point, direction + 1, 4);
        return sb.toString();
    }

    private void appendChess(StringBuilder sb, Gobang.Point point, int direction, int offset) {
        int chess = relativePoint(point, direction, offset);
        if (chess > -1) {
            if (point.type == 2) {
                if (chess > 0) {
                    chess = 3 - chess;
                }
            }
            sb.append(chess);
        }
    }

    /**
     * 获取相对点位棋子
     *
     * @param point     当前棋位
     * @param direction 方向 1.左横 2.右横 3.上纵 4.下纵  5.左斜上 6.左斜下 7.右斜上 8.右斜下
     * @param offset    偏移量
     * @return -1:越界 0:空位 1:黑棋 2:白棋
     */
    private int relativePoint(Gobang.Point point, int direction, int offset) {
        int x = point.x, y = point.y;
        switch (direction) {
            case 1:
                x -= offset;
                break;
            case 2:
                x += offset;
                break;
            case 3:
                y -= offset;
                break;
            case 4:
                y += offset;
                break;
            case 5:
                x += offset;
                y -= offset;
                break;
            case 6:
                x -= offset;
                y += offset;
                break;
            case 7:
                x -= offset;
                y -= offset;
                break;
            case 8:
                x += offset;
                y += offset;
                break;
        }

        if (x < 0 || y < 0 || x >= size || y >= size) {
            return -1;
        }

        return chessData[x][y];
    }

}
