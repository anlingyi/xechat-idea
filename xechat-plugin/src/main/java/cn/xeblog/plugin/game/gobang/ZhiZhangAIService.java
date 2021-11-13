package cn.xeblog.plugin.game.gobang;

import lombok.AllArgsConstructor;

import java.util.*;

/**
 * @author anlingyi
 * @date 2021/11/1 3:31 下午
 */
public class ZhiZhangAIService implements AIService {

    /**
     * 已下棋子数据
     */
    private int[][] chessData;
    /**
     * 棋盘行数
     */
    private int rows;
    /**
     * 棋盘列数
     */
    private int cols;
    /**
     * AI棋子类型
     */
    private int ai;
    /**
     * 进攻系数
     */
    private int attack;
    /**
     * AI最佳下棋点位
     */
    private Point bestPoint;

    /**
     * 声明一个最大值
     */
    private static final int INFINITY = 999999999;

    /**
     * 棋型分数表
     */
    private static final Map<String, Integer> SCORE = new LinkedHashMap<>();

    static {
        // 初始化棋型分数表
        for (ChessModel chessScore : ChessModel.values()) {
            for (String value : chessScore.values) {
                SCORE.put(value, chessScore.score);
            }
        }
    }

    @AllArgsConstructor
    private enum ChessModel {
        /**
         * 连五
         */
        LIANWU(10000000, new String[]{"11111"}),
        /**
         * 活四
         */
        HUOSI(100000, new String[]{"011110"}),
        /**
         * 活三
         */
        HUOSAN(10000, new String[]{"001110", "011100", "010110", "011010"}),
        /**
         * 冲四
         */
        CHONGSI(8000, new String[]{"0111010", "0101110", "0110110", "110111", "101111", "111011", "110111",
                "011112", "211110", "10111", "11011", "11101", "111012", "211101", "110112", "101112", "211011"}),
        /**
         * 眠三
         */
        MIANSAN(1000, new String[]{"001112", "010112", "011012", "211100", "211010"}),
        /**
         * 活二
         */
        HUOER(800, new String[]{"001100", "011000", "000110"}),
        /**
         * 眠二
         */
        MIANER(50, new String[]{"011200", "001120", "002110", "021100", "001010", "010100"}),
        /**
         * 眠一
         */
        MIANYI(10, new String[]{"001200", "002100", "020100", "000210", "000120"});

        /**
         * 分数
         */
        int score;
        /**
         * 局势数组
         */
        String[] values;
    }

    @Override
    public Point getPoint(int[][] chessData, Point point, boolean started) {
        initChessData(chessData);
        this.ai = 3 - point.type;
        this.bestPoint = null;
        this.attack = 2;

        if (started) {
            // AI先下，首子天元
            int centerX = this.cols / 2;
            int centerY = this.rows / 2;
            return new Point(centerX, centerY, this.ai);
        }

        // 基于极大极小值搜索获取最佳棋位
        minimax(0, 2, -INFINITY, INFINITY);

        return this.bestPoint;
    }

    /**
     * 初始化棋盘数据
     *
     * @param chessData 当前棋盘数据
     */
    private void initChessData(int[][] chessData) {
        this.rows = chessData.length;
        this.cols = chessData[0].length;
        this.chessData = new int[this.cols][this.rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                this.chessData[i][j] = chessData[i][j];
            }
        }
    }

    /**
     * 下棋子
     *
     * @param point 棋子
     */
    private void putChess(Point point) {
        this.chessData[point.x][point.y] = point.type;
    }

    /**
     * 撤销下的棋子
     *
     * @param point 棋子
     */
    private void revokeChess(Point point) {
        this.chessData[point.x][point.y] = 0;
    }

    /**
     * 获取最佳下棋点位（只考虑一步）
     *
     * @param point 对手下棋点位
     * @return
     */
    private Point getBestPoint(Point point) {
        Point best = null;
        // 初始分值为最小
        int score = -INFINITY;

        /* 遍历所有能下棋的点位，评估各个点位的分值，选择分值最大的点位 */
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (this.chessData[i][j] != 0) {
                    // 该点已有棋子，跳过
                    continue;
                }

                Point p = new Point(i, j, this.ai);
                // 该点得分 = AI落子得分 + 对手落子得分
                int val = evaluate(p) + evaluate(new Point(i, j, 3 - this.ai));
                // 选择得分最高的点位
                if (val > score) {
                    // 最高分被刷新
                    score = val;
                    // 更新最佳点位
                    best = p;
                }
            }
        }

        return best;
    }

    /**
     * 极大极小值搜索
     *
     * @param type  当前走棋方 0.根节点表示AI走棋 1.AI 2.玩家
     * @param depth 搜索深度
     * @return
     */
    private int minimax(int type, int depth) {
        // 是否是根节点
        boolean isRoot = type == 0;
        if (isRoot) {
            // 根节点是AI走棋
            type = this.ai;
        }

        // 当前是否是AI走棋
        boolean isAI = type == this.ai;
        // 当前分值，
        int score;
        if (isAI) {
            // AI因为要选择最高分，所以初始化一个难以到达的低分
            score = -INFINITY;
        } else {
            // 对手要选择最低分，所以初始化一个难以到达的高分
            score = INFINITY;
        }

        // 到达叶子结点
        if (depth == 0) {
            /**
             * 评估每棵博弈树的叶子结点的局势
             * 比如：depth=2时，表示从AI开始走两步棋之后的局势评估，AI(走第一步) -> 玩家(走第二步)，然后对局势进行评估
             * 注意：局势评估是以AI角度进行的，分值越大对AI越有利，对玩家越不利
             */
            return evaluateAll();
        }

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (this.chessData[i][j] != 0) {
                    // 该处已有棋子，跳过
                    continue;
                }

                /* 模拟 AI -> 玩家 交替落子 */
                Point p = new Point(i, j, type);
                // 落子
                putChess(p);
                // 递归生成博弈树，并评估叶子结点的局势获取分值
                int curScore = minimax(3 - type, depth - 1);
                // 撤销落子
                revokeChess(p);

                if (isAI) {
                    // AI要选对自己最有利的节点（分最高的）
                    if (curScore > score) {
                        // 最高值被刷新
                        score = curScore;
                        if (isRoot) {
                            // 根节点处更新AI最好的棋位
                            this.bestPoint = p;
                        }
                    }
                } else {
                    // 对手要选对AI最不利的节点（分最低的）
                    if (curScore < score) {
                        // 最低值被刷新
                        score = curScore;
                    }
                }
            }
        }

        return score;
    }

    /**
     * 极大极小值搜索、AlphaBeta剪枝
     *
     * @param type  当前走棋方 0.根节点表示AI走棋 1.AI 2.玩家
     * @param depth 搜索深度
     * @param alpha 极大值
     * @param beta  极小值
     * @return
     */
    private int minimax(int type, int depth, int alpha, int beta) {
        // 是否是根节点
        boolean isRoot = type == 0;
        if (isRoot) {
            // 根节点是AI走棋
            type = this.ai;
        }

        // 当前是否是AI走棋
        boolean isAI = type == this.ai;

        // 到达叶子结点
        if (depth == 0) {
            /**
             * 评估每棵博弈树的叶子结点的局势
             * 比如：depth=2时，表示从AI开始走两步棋之后的局势评估，AI(走第一步) -> 玩家(走第二步)，然后对局势进行评估
             * 注意：局势评估是以AI角度进行的，分值越大对AI越有利，对玩家越不利
             */
            return evaluateAll();
        }

        for (int i = 0; i < this.cols; i++) {
            if (alpha >= beta) {
                /*
                 AlphaBeta剪枝

                 解释：
                 AI当前最大分数为：alpha 搜索区间 (alpha, +∞]
                 对手当前最小分数为：beta 搜索区间 [-∞, beta)

                 因为对手要选择分数小于beta的分支，AI要从对手给的分支里面选最大的分支，这个最大的分支要和当前的分支(alpha)做比较，
                 现在alpha都比beta大了，下面搜索给出的分支也都是小于alpha的，所以搜索下去没有意义，剪掉提高搜索效率。
                 */
                break;
            }

            for (int j = 0; j < this.rows; j++) {
                if (this.chessData[i][j] != 0) {
                    // 该处已有棋子，跳过
                    continue;
                }

                /* 模拟 AI -> 玩家 交替落子 */
                Point p = new Point(i, j, type);
                // 落子
                putChess(p);
                // 递归生成博弈树，并评估叶子结点的局势
                int score = minimax(3 - type, depth - 1, alpha, beta);
                // 撤销落子
                revokeChess(p);

                if (isAI) {
                    // AI要选对自己最有利的节点（分最高的）
                    if (score > alpha) {
                        // 最高值被刷新，更新alpha值
                        alpha = score;
                        if (isRoot) {
                            // 根节点处更新AI最好的棋位
                            this.bestPoint = p;
                        }
                    }
                } else {
                    // 对手要选对AI最不利的节点（分最低的）
                    if (score < beta) {
                        // 最低值被刷新，更新beta值
                        beta = score;
                    }
                }

                if (alpha >= beta) {
                    // 剪枝
                    break;
                }
            }
        }

        return isAI ? alpha : beta;
    }

    /**
     * 对当前棋位进行评估
     *
     * @param point 当前棋位
     * @return
     */
    private int evaluate(Point point) {
        // 分值
        int score = 0;
        // 活三数
        int huosanTotal = 0;
        // 冲四数
        int chongsiTotal = 0;
        // 活二数
        int huoerTotal = 0;

        for (int i = 1; i < 5; i++) {
            String situation = getSituation(point, i);
            if (checkSituation(situation, ChessModel.HUOSAN)) {
                // 活三+1
                huosanTotal++;
            } else if (checkSituation(situation, ChessModel.CHONGSI)) {
                // 冲四+1
                chongsiTotal++;
            } else if (checkSituation(situation, ChessModel.HUOER)) {
                // 活二+1
                huoerTotal++;
            }

            // 下此步的得分
            score += getScore(situation);
        }

        if (huosanTotal > 0 && huoerTotal > 0) {
            // 活三又活二
            score *= 2;
        }
        if (chongsiTotal > 0 && huoerTotal > 0) {
            // 冲四又活二
            score *= 4;
        }
        if (huosanTotal > 1) {
            // 活三数大于1
            score *= 6;
        }
        if (chongsiTotal > 0 && huosanTotal > 0) {
            // 冲四又活三
            score *= 8;
        }

        return score;
    }

    /**
     * 以AI角度对当前局势进行评估，分数越大对AI越有利
     *
     * @return
     */
    private int evaluateAll() {
        // AI得分
        int aiScore = 0;
        // 对手得分
        int foeScore = 0;

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                int type = this.chessData[i][j];
                if (type == 0) {
                    // 该点没有棋子，跳过
                    continue;
                }

                // 评估该棋位分值
                int val = evaluate(new Point(i, j, type));
                if (type == this.ai) {
                    // 累积AI得分
                    aiScore += val;
                } else {
                    // 累积对手得分
                    foeScore += val;
                }
            }
        }

        // 该局AI最终得分 = AI得分 * 进攻系数 - 对手得分
        return aiScore * this.attack - foeScore;
    }

    /**
     * 检查当前落子是否处于某一局势
     *
     * @param point      当前棋位
     * @param chessScore 检查的局势
     * @return
     */
    private boolean checkSituation(Point point, ChessModel chessScore) {
        // 要检查4个大方向
        for (int i = 1; i < 5; i++) {
            if (checkSituation(getSituation(point, i), chessScore)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前局势是否处于某个局势
     *
     * @param situation  当前局势
     * @param chessModel 检查的局势
     * @return
     */
    private boolean checkSituation(String situation, ChessModel chessModel) {
        for (String value : chessModel.values) {
            if (situation.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取局势分数
     *
     * @param situation 局势
     * @return
     */
    private int getScore(String situation) {
        for (String key : SCORE.keySet()) {
            if (situation.contains(key)) {
                return SCORE.get(key);
            }
        }
        return 0;
    }

    /**
     * 获取棋位局势
     *
     * @param point     当前棋位
     * @param direction 大方向 1.横 2.纵 3.左斜 4.右斜
     * @return
     */
    private String getSituation(Point point, int direction) {
        // 下面用到了relativePoint函数，根据传入的四个大方向做转换
        direction = direction * 2 - 1;
        // 以下是将各个方向的棋子拼接成字符串返回
        StringBuilder sb = new StringBuilder();
        appendChess(sb, point, direction, 4);
        appendChess(sb, point, direction, 3);
        appendChess(sb, point, direction, 2);
        appendChess(sb, point, direction, 1);
        sb.append(1); // 当前棋子统一标记为1(黑)
        appendChess(sb, point, direction + 1, 1);
        appendChess(sb, point, direction + 1, 2);
        appendChess(sb, point, direction + 1, 3);
        appendChess(sb, point, direction + 1, 4);
        return sb.toString();
    }

    /**
     * 拼接各个方向的棋子
     * <p>
     * 由于现有评估模型是对黑棋进行评估
     * 所以，为了方便对局势进行评估，如果当前是白棋方，需要将扫描到的白棋转换为黑棋，黑棋转换为白棋
     * 如：point(x=0,y=0,type=2) 即当前为白棋方
     * 扫描到的某个方向局势为：20212 -> 转换后 -> 10121
     *
     * @param sb        字符串容器
     * @param point     当前棋子
     * @param direction 方向 1.左横 2.右横 3.上纵 4.下纵  5.左斜上 6.左斜下 7.右斜上 8.右斜下
     * @param offset    偏移量
     */
    private void appendChess(StringBuilder sb, Point point, int direction, int offset) {
        int chess = relativePoint(point, direction, offset);
        if (chess > -1) {
            if (point.type == 2) {
                // 对白棋进行转换
                if (chess > 0) {
                    // 对棋子颜色进行转换，2->1，1->2
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
    private int relativePoint(Point point, int direction, int offset) {
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

        if (x < 0 || y < 0 || x >= this.cols || y >= this.rows) {
            // 越界
            return -1;
        }

        // 返回该位置的棋子
        return this.chessData[x][y];
    }

}
