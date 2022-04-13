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
    private float attack;
    /**
     * AI最佳下棋点位
     */
    private Point bestPoint;

    /**
     * 当前回合数
     */
    private int rounds;

    /**
     * AI配置
     */
    private AIConfig aiConfig;

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
        HUOSI(1000000, new String[]{"011110"}),
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
        MIANYI(10, new String[]{"001200", "002100", "020100", "000210", "000120", "210000", "000012"});

        /**
         * 分数
         */
        int score;
        /**
         * 局势数组
         */
        String[] values;
    }

    public ZhiZhangAIService() {
        this(new AIConfig(6, 10));
    }

    public ZhiZhangAIService(AIConfig aiConfig) {
        this.aiConfig = aiConfig;
    }

    @Override
    public Point getPoint(int[][] chessData, Point point) {
        initChessData(chessData);
        this.ai = 3 - point.type;
        this.bestPoint = null;
        // AI是黑棋则偏进攻，是白棋则偏防守
        this.attack = this.ai == 1 ? 1.5f : 1.0f;
        int depth = this.aiConfig.getDepth();

        if (this.rounds == 1 && this.ai == 1) {
            // AI先下，首子天元
            int centerX = this.cols / 2;
            int centerY = this.rows / 2;
            return new Point(centerX, centerY, this.ai);
        }

        // 基于普通方式获取最佳棋位
        if (this.aiConfig.getDepth() < 2) {
            return getBestPoint(point);
        }

        if (this.aiConfig.getDepth() > 4 && this.rounds < 4) {
            // 当AI级别大于4时，将前三个回合的搜索深度设置为4
            depth = 4;
        }

        // 基于极大极小值搜索获取最佳棋位
        minimax(0, depth, -INFINITY, INFINITY);

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
        int chessTotal = 0;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int type = chessData[i][j];
                this.chessData[i][j] = type;
                if (type != 0) {
                    chessTotal++;
                }
            }
        }

        this.rounds = chessTotal / 2 + 1;
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
                // 该点得分 = AI落子得分 * 进攻系数 + 对手落子得分
                int val = Math.round(evaluate(p) * this.attack) + evaluate(new Point(i, j, 3 - this.ai));
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

        // 启发式搜索
        List<Point> pointList = getHeuristicPoints(type);
        // 记录选择的最好落子点
        List<Point> bestPointList = new ArrayList<>();
        for (Point point : pointList) {
            int score;
            if (checkSituation(point, ChessModel.LIANWU)) {
                // 落子到这里就赢了，后面的节点不用再评估了
                depth = 1;
            }

            /* 模拟 AI -> 玩家 交替落子 */
            // 落子
            putChess(point);
            // 递归生成博弈树，并评估叶子结点的局势
            score = minimax(3 - type, depth - 1, alpha, beta);
            // 撤销落子
            revokeChess(point);

            if (isAI) {
                // AI要选对自己最有利的节点（分最高的）
                if (score >= alpha) {
                    if (isRoot) {
                        if (score > alpha) {
                            // 找到了更好的落子点，将之前选择的落子点清空
                            bestPointList.clear();
                        }
                        // 记录该落子点
                        bestPointList.add(point);
                    }

                    // 最高值被刷新，更新alpha值
                    alpha = score;
                }
            } else {
                // 对手要选对AI最不利的节点（分最低的）
                if (score < beta) {
                    // 最低值被刷新，更新beta值
                    beta = score;
                }
            }

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
        }

        if (isRoot) {
            // 如果有多个落子点，则通过getBestPoint方法选择一个最好的
            this.bestPoint = bestPointList.size() > 1 ? getBestPoint(bestPointList) : bestPointList.get(0);
        }

        return isAI ? alpha : beta;
    }

    /**
     * 启发式获取落子点位
     *
     * @param type 当前走棋方 1.黑棋 2.白棋
     * @return
     */
    private List<Point> getHeuristicPoints(int type) {
        // 落子点上限
        int max = this.aiConfig.getMaxNodes();
        // 高优先级落子点
        List<Point> highPriorityPointList = new ArrayList<>();
        // 低优先级落子点
        List<Point> lowPriorityPointList = new ArrayList<>();
        // 候补落子点
        List<Point> alternatePointList = new ArrayList<>();
        // 杀棋点
        List<Point> killPointList = new ArrayList<>();

        boolean isEnd = false;
        // 局势危险等级 0.不危险 1.有危险 2.很危险
        int dangerLevel = 0;
        for (int i = 0; i < this.cols; i++) {
            if (isEnd) {
                break;
            }

            for (int j = 0; j < this.rows; j++) {
                if (this.chessData[i][j] != 0) {
                    // 该处已有棋子，跳过
                    continue;
                }

                // 考虑自己的落子情况
                Point point = new Point(i, j, type);
                if (checkSituation(point, ChessModel.LIANWU)) {
                    // 优先检查自己连五的情况，如果该落子点可以形成连五，则结束循环，直接返回
                    highPriorityPointList.clear();
                    highPriorityPointList.add(point);
                    isEnd = true;
                    break;
                }

                if (dangerLevel == 2) {
                    // 局势很危险，只检查自己可以连五的落子点
                    continue;
                }

                if (checkSituation(point, ChessModel.CHONGSI, ChessModel.HUOSI)) {
                    // 将自己的冲四、活四落子点加入到杀棋点队列
                    killPointList.add(point);
                }

                // 考虑对手的落子情况
                Point foePoint = new Point(i, j, 3 - type);
                // 当前局势危险等级
                int level = 0;
                if (checkSituation(foePoint, ChessModel.LIANWU)) {
                    // 对手连五了，局势很危险！！
                    level = 2;
                } else if (checkSituation(foePoint, ChessModel.HUOSI)) {
                    // 对手活四了，局势有危险！
                    level = 1;
                }

                if (level > 0) {
                    // 当前局势存在危险
                    if (dangerLevel < level) {
                        // 危险升级
                        dangerLevel = level;
                        // 局势危险等级如果上升，则清空之前选择的高优先级节点，防止AI误入歧途
                        highPriorityPointList.clear();
                    }

                    // 将此节点加入到高优先级队列
                    highPriorityPointList.add(point);
                }

                if (dangerLevel > 0) {
                    // 局势有危险，下面的检查逻辑不用走了
                    continue;
                }

                if (checkHighPriorityPoint(point) || checkHighPriorityPoint(foePoint)) {
                    // 自己的高优先级落子点 ｜ 对手的高优先级落子点
                    highPriorityPointList.add(point);
                } else if (checkSituation(point, ChessModel.HUOSAN, ChessModel.CHONGSI)) {
                    // 低优先级落子点：活三、冲四
                    lowPriorityPointList.add(point);
                } else if (checkSituation(point, ChessModel.HUOER, ChessModel.MIANSAN, ChessModel.MIANER, ChessModel.MIANYI)) {
                    // 候补落子点：活二、眠三、眠二、眠一
                    alternatePointList.add(point);
                }
            }
        }

        if (dangerLevel == 1 && !killPointList.isEmpty()) {
            // 局势有危险且杀棋队列不为空，则将所有的杀棋点加入到高优先级队列
            highPriorityPointList.addAll(killPointList);
        }

        if (highPriorityPointList.isEmpty()) {
            // 无高优先级落子点，则判断是否有低优先级落子点
            if (lowPriorityPointList.isEmpty()) {
                // 低优先级落子点也没有，就返回候补落子点
                if (alternatePointList.isEmpty()) {
                    // 候补落子点也没有，就随机取
                    return randomPoint(type, 1);
                }

                if (alternatePointList.size() > max) {
                    // 候补落子点个数超过上限，随机取 {max} 个
                    Collections.shuffle(alternatePointList);
                    return alternatePointList.subList(0, max);
                }

                // 返回候补落子点
                return alternatePointList;
            }

            if (lowPriorityPointList.size() > max) {
                // 低优先级落子点个数超过上限，随机取 {max} 个
                Collections.shuffle(lowPriorityPointList);
                return lowPriorityPointList.subList(0, max);
            }

            // 返回低优先级落子点
            return lowPriorityPointList;
        }

        // 返回高优先级落子点
        return highPriorityPointList;
    }

    /**
     * 检查高优先级落子点
     *
     * @param point 检查的点位
     * @return
     */
    private boolean checkHighPriorityPoint(Point point) {
        // 活三数
        int huosanTotal = 0;
        // 冲四数
        int chongsiTotal = 0;
        // 活二数
        int huoerTotal = 0;

        for (int i = 1; i < 5; i++) {
            // 获取当前局势
            String situation = getSituation(point, i);
            // 获取当前局势的棋型
            ChessModel chessModel = getChessModel(situation);

            // 棋型统计
            if (chessModel != null) {
                switch (chessModel) {
                    case HUOSI:
                        return true;
                    case HUOSAN:
                        // 活三+1
                        huosanTotal++;
                        break;
                    case CHONGSI:
                        // 冲四+1
                        chongsiTotal++;
                        break;
                    case HUOER:
                        // 活二+1
                        huoerTotal++;
                        break;
                }
            }
        }

        if (chongsiTotal > 1 || (chongsiTotal > 0 && huosanTotal > 0)) {
            // 冲四数大于1、冲四又活三
            return true;
        }
        if (huosanTotal > 1) {
            // 活三数大于1
            return true;
        }
        if (huosanTotal > 0 && huoerTotal > 0) {
            // 活三又活二
            return true;
        }

        return false;
    }

    /**
     * 从给定的点位列表中获取最佳点位
     *
     * @param pointList 点位列表
     * @return
     */
    private Point getBestPoint(List<Point> pointList) {
        Point bestPoint = null;
        int bestScore = -INFINITY;

        for (int i = 0; i < pointList.size(); i++) {
            Point point = pointList.get(i);
            int score = Math.round(evaluate(point) * this.attack) + evaluate(new Point(point.x, point.y, 3 - point.type));
            if (score > bestScore) {
                bestScore = score;
                bestPoint = point;
            }
        }

        return bestPoint;
    }

    /**
     * 随机获取落子点
     *
     * @param type 棋子类型
     * @param num  数量
     * @return
     */
    private List<Point> randomPoint(int type, int num) {
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < this.rows; j++) {
                if (this.chessData[i][j] == 0) {
                    pointList.add(new Point(i, j, type));
                }
            }
        }

        Collections.shuffle(pointList);
        return pointList.subList(0, Math.min(num, pointList.size()));
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
            // 获取当前局势
            String situation = getSituation(point, i);
            // 获取当前局势的棋型
            ChessModel chessModel = getChessModel(situation);

            // 棋型统计
            if (chessModel != null) {
                switch (chessModel) {
                    case HUOSAN:
                        // 活三+1
                        huosanTotal++;
                        break;
                    case CHONGSI:
                        // 冲四+1
                        chongsiTotal++;
                        break;
                    case HUOER:
                        // 活二+1
                        huoerTotal++;
                        break;
                }

                // 下此步的得分
                score += chessModel.score;
            }
        }

        if (chongsiTotal > 0 && huoerTotal > 0) {
            // 冲四又活二
            score *= 2;
        }
        if (huosanTotal > 0 && huoerTotal > 0) {
            // 活三又活二
            score *= 3;
        }
        if (huosanTotal > 1) {
            // 活三数大于1
            score *= 5;
        }
        if (chongsiTotal > 1 || (chongsiTotal > 0 && huosanTotal > 0)) {
            // 冲四数大于1、冲四又活三
            score *= 6;
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
        return Math.round(aiScore * this.attack) - foeScore;
    }

    /**
     * 检查当前落子是否处于某一局势
     *
     * @param point       当前棋位
     * @param chessModels 检查的局势
     * @return
     */
    private boolean checkSituation(Point point, ChessModel... chessModels) {
        // 要检查4个大方向
        for (int i = 1; i < 5; i++) {
            ChessModel chessModel = getChessModel(getSituation(point, i));
            for (ChessModel model : chessModels) {
                if (model == chessModel) {
                    return true;
                }
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
     * 获取当前局势的棋型
     *
     * @param situation 当前局势
     * @return
     */
    private ChessModel getChessModel(String situation) {
        for (ChessModel chessModel : ChessModel.values()) {
            for (String value : chessModel.values) {
                if (situation.contains(value)) {
                    return chessModel;
                }
            }
        }

        return null;
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
