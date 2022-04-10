package cn.xeblog.plugin.game.gobang;

/**
 * @author anlingyi
 * @date 2022/4/4 10:16 上午
 */
public class AITest {

    public static void main(String[] args) {
        // 冲四盘活局
        // 7,7,1;8,8,2;7,6,1;9,7,2;7,9,1;7,8,2;6,8,1;8,6,2;6,7,1;8,7,2;8,5,1;5,8,2;9,4,1;10,3,2;6,9,1;6,6,2;5,7,1;8,10,2;8,9,1;5,9,2;6,11,1;6,10,2;7,10,1;9,8,2;10,8,1;7,11,2;4,8,1;9,9,2;4,7,1;3,7,2;5,12,1;4,13,2;4,6,1;3,5,2;4,9,1;4,5,2;
        // 冲四局
        // 7,7,1;7,6,2;8,6,1;6,8,2;8,8,1;8,7,2;6,6,1;5,5,2;6,5,1;6,4,2;9,9,1;10,10,2;9,7,1;9,8,2;7,9,1;10,6,2;10,9,1;8,9,2;11,9,1;10,8,2;6,10,1;5,11,2;12,9,1;13,9,2;10,7,1;11,8,2;12,8,1;4,6,2;3,7,1;8,2,2;7,3,1;12,6,2;12,10,1;12,11,2;4,8,1;5,9,2;7,5,1;5,7,2;5,6,1;3,8,2;5,10,1;7,10,2;7,4,1;6,11,2;5,12,1;8,3,2;7,2,1;7,1,2;9,3,1;8,1,2;8,4,1;10,2,2;9,5,1;
        String str = "7,7,1;8,8,2;8,6,1;8,9,2;6,8,1;9,5,2;5,9,1;4,10,2;7,9,1;8,10,2;8,11,1;7,11,2;7,6,1;7,8,2;9,10,1;9,9,2;10,8,1;6,12,2;";
        Point lastPoint = null;
        int[][] chessData = new int[15][15];
        String[] chessRecords = str.split(";");
        int len = chessRecords.length;
        for (int i = 0; i < len; i++) {
            String[] point = chessRecords[i].split(",");
            int x = Integer.parseInt(point[0]);
            int y = Integer.parseInt(point[1]);
            int type = Integer.parseInt(point[2]);
            chessData[x][y] = type;
            if (i == len - 1) {
                lastPoint = new Point(x, y, type);
            }
        }

        AIService aiService = new ZhiZhangAIService();
        System.out.println("AI => " + aiService.getPoint(chessData, lastPoint, false));
    }

}
