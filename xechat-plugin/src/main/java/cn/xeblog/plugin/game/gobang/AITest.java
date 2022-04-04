package cn.xeblog.plugin.game.gobang;

/**
 * @author anlingyi
 * @date 2022/4/4 10:16 上午
 */
public class AITest {

    public static void main(String[] args) {
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
