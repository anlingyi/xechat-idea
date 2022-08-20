package cn.xeblog.plugin.game.ngsnake.ui;

import cn.xeblog.plugin.game.ngsnake.model.*;
import cn.xeblog.plugin.game.ngsnake.model.Point;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * @author anlingyi
 * @date 2022/8/2 3:51 PM
 */
public class SnakeGameUI extends JPanel implements ActionListener {

    /**
     * 是否允许穿墙
     */
    @Setter
    private boolean pierced;

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    /**
     * 游戏模式
     */
    private GameMode gameMode;


    /**
     * 蛇
     */
    private Snake snake;

    /**
     * 药丸
     */
    private Pill pill;

    /**
     * 移动方向
     */
    private Direction direction;

    /**
     * 停止游戏标记
     */
    private boolean stop;

    /**
     * 游戏状态 0.初始化 1.游戏胜利 2.游戏失败
     */
    private int state = -1;

    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 移动速度
     */
    private int speed = 100;

    /**
     * 分数
     */
    private int score;

    /**
     * 特殊药丸列表
     */
    private ArrayList<Pill.PillType> specialPill;

    public SnakeGameUI(int width, int height, GameMode gameMode) {
        this.width = width;
        this.height = height;
        this.gameMode = gameMode;
        this.timer = new Timer(speed, this);
        this.stop = true;

        initPanel();
    }

    /**
     * 初始化
     */
    private void init() {
        this.score = 0;
        this.state = 0;
        this.stop = true;
        this.timer.setDelay(speed);

        initSnake();
        initPill();
        generatePill();
        repaint();
    }

    /**
     * 初始化游戏面板
     */
    private void initPanel() {
        SnakeGameUI snakeGameUI = this;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.addKeyListener(getKeyListener());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                snakeGameUI.requestFocusInWindow();
            }
        });
    }

    /**
     * 获取按键事件监听
     *
     * @return
     */
    private KeyListener getKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (stop && e.getKeyCode() != KeyEvent.VK_SPACE) {
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (direction == Direction.DOWN) {
                            break;
                        }

                        direction = Direction.UP;
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction == Direction.UP) {
                            break;
                        }

                        direction = Direction.DOWN;
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction == Direction.RIGHT) {
                            break;
                        }

                        direction = Direction.LEFT;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction == Direction.LEFT) {
                            break;
                        }

                        direction = Direction.RIGHT;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (state != 0) {
                            init();
                        }

                        stop = !stop;
                        if (!stop) {
                            timer.start();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 初始化蛇
     */
    private void initSnake() {
        this.direction = Direction.LEFT;
        int maxX = this.width / Snake.width;
        int maxY = this.height / Snake.height;

        this.snake = new Snake(new ArrayList<>());
        this.snake.add(maxX - 2, 3);
        this.snake.add(maxX - 1, 3);
        this.snake.add(maxX - 1, 2);
        this.snake.add(maxX - 1, 1);

        if (gameMode == GameMode.NON_GLUTTONOUS) {
            for (int i = maxX - 1; i > 0; i--) {
                this.snake.add(i, 1);
            }
            for (int i = 1; i < maxY - 1; i++) {
                this.snake.add(1, i);
            }
            for (int i = 1; i < maxX - 1; i++) {
                this.snake.add(i, maxY - 2);
            }
        }
    }

    /**
     * 初始化药丸
     */
    private void initPill() {
        this.specialPill = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            this.specialPill.add(Pill.PillType.RED);
        }
        for (int i = 0; i < 10; i++) {
            this.specialPill.add(Pill.PillType.BLUE);
        }

        Collections.shuffle(specialPill);
    }

    /**
     * 生成药丸
     */
    private void generatePill() {
        // 是否获取特殊药丸
        boolean getSpecialPill = new Random().nextInt(6) == 3;
        Pill.PillType pillType;
        if (getSpecialPill && this.specialPill.size() > 0) {
            // 生成特殊药丸
            int index = new Random().nextInt(this.specialPill.size());
            pillType = this.specialPill.get(index);
            this.specialPill.remove(index);
        } else {
            // 生成绿色药丸
            pillType = Pill.PillType.GREEN;
        }

        int x, y;

        boolean inSnakeBody;
        do {
            inSnakeBody = false;
            // 随机坐标
            x = new Random().nextInt(this.width / Pill.width);
            y = new Random().nextInt(this.height / Pill.height);

            // 需要跳过蛇身坐标
            int sx = x * Snake.width;
            int sy = y * Snake.height;
            for (Point point : this.snake.body) {
                if (point.x == sx && point.y == sy) {
                    inSnakeBody = true;
                    break;
                }
            }
        } while (inSnakeBody);


        this.pill = new Pill(x, y, pillType);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(66, 66, 66));
        g2.fillRect(0, 0, this.width, this.height);

        if (this.snake != null) {
            // 画蛇
            g2.setColor(new Color(255, 255, 255));
            for (int i = this.snake.size() - 1; i >= 0; i--) {
                cn.xeblog.plugin.game.ngsnake.model.Point point = this.snake.body.get(i);
                if (i == 0) {
                    // 蛇头
                    g2.setColor(new Color(255, 92, 92));
                } else {
                    g2.setColor(new Color(215, 173, 173));
                }

                g2.fillRect(point.x, point.y, Snake.width, Snake.height);
            }
        }

        if (this.pill != null) {
            // 画药丸
            Color pillColor;
            switch (this.pill.pillType) {
                case RED:
                    pillColor = new Color(255, 41, 41);
                    break;
                case BLUE:
                    pillColor = new Color(20, 250, 243);
                    break;
                default:
                    pillColor = new Color(97, 255, 113);
                    break;
            }

            g2.setColor(pillColor);
            g2.fillOval(pill.point.x, pill.point.y, Pill.width, Pill.height);
        }

        if (state > 0) {
            // 显示游戏结果
            String tips = "游戏失败！";
            if (state == 1) {
                tips = "游戏胜利！";
            }
            g2.setFont(new Font("", Font.BOLD, 20));
            g2.setColor(new Color(208, 74, 74));
            g2.drawString(tips, this.width / 3, this.height / 3);

            g2.setFont(new Font("", Font.PLAIN, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("（" + gameMode.getName() + "）得分：" + this.score, this.width / 4, this.height / 3 + 50);
        }

        if (stop) {
            g2.setFont(new Font("", Font.PLAIN, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("按空格键开始/暂停游戏！", this.width / 4, this.height - 50);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 是否吃药
        boolean isAte = false;
        if (!this.stop) {
            // 移动蛇
            this.snake.move(this.direction);
            Point head = this.snake.getHead();
            if (head.equals(this.pill.point)) {
                // 吃药了
                isAte = true;
                // 药丸分数
                int getScore = this.pill.pillType.score;
                // 累计分数
                this.score += getScore;
                if (gameMode == GameMode.NON_GLUTTONOUS) {
                    for (int i = 0; i < getScore; i++) {
                        // 移除蛇尾
                        this.snake.removeLast();
                        if (this.snake.size() == 0) {
                            // 游戏胜利
                            this.state = 1;
                            this.stop = true;
                            break;
                        }
                    }
                } else {
                    // 蛇身长度+1
                    Point tail = this.snake.getTail();
                    this.snake.add(tail.x, tail.y);
                }

                pill = null;
                if (this.score % 10 == 0) {
                    int curSpeed = this.timer.getDelay();
                    if (curSpeed > 30) {
                        // 加速
                        this.timer.setDelay(curSpeed - 10);
                    }
                }
            }

            if (state == 0) {
                // 判断蛇有没有咬到自己或是撞墙
                int maxWidth = this.width - this.snake.width;
                int maxHeight = this.height - this.snake.height;
                boolean isHitWall = head.x > maxWidth || head.x < 0 || head.y > maxHeight || head.y < 0;
                boolean isBiting = false;
                for (int i = this.snake.size() - 1; i > 0; i--) {
                    if (head.equals(this.snake.body.get(i))) {
                        isBiting = true;
                        break;
                    }
                }

                if (pierced) {
                    // 穿墙
                    if (head.x > maxWidth) {
                        head.x = 0;
                    }
                    if (head.x < 0) {
                        head.x = maxWidth;
                    }
                    if (head.y > maxHeight) {
                        head.y = 0;
                    }
                    if (head.y < 0) {
                        head.y = maxHeight;
                    }
                }

                if (isBiting || !pierced && isHitWall) {
                    // 游戏失败
                    this.state = 2;
                    this.stop = true;
                }
            }
        }

        if (this.stop) {
            this.timer.stop();
        } else if (isAte) {
            // 重新生成药丸
            generatePill();
        }

        repaint();
    }

}
