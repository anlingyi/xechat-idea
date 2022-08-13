package cn.xeblog.plugin.game.game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameUI extends JPanel {
  private static final Color BG_COLOR = new Color(0xbbada0);
  private static final String FONT_NAME = "";
  // 行列数
  private static final int LINE = 4;
  // 每个方块的大小
  private static final int BLOCK_SIZE = 48;
  // 每个方块的间距
  private static final int BLOCK_MARGIN = 10;
  // 方块2生成的概率,剩下为方块4的概率
  private static final double PROBABILITY = 0.8;
  // 方块集合
  private Block[] blocks;
  private boolean isWin = false;
  private boolean isLose = false;
  private int score = 0;

  public GameUI() {
    setPreferredSize(new Dimension(200, 200));
    setFocusable(true);
    addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if ((isWin || isLose) && e.getKeyCode() == KeyEvent.VK_SPACE) {
              resetGame();
            }
            if (!canMove()) {
              isLose = true;
            }

            if (!isWin && !isLose) {
              switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                  left();
                  break;
                case KeyEvent.VK_RIGHT:
                  right();
                  break;
                case KeyEvent.VK_DOWN:
                  down();
                  break;
                case KeyEvent.VK_UP:
                  up();
                  break;
              }
            }
            if (!isWin && !canMove()) {
              isLose = true;
            }
            repaint();
          }
        });
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            requestFocusInWindow();
          }
        });
    resetGame();
  }

  // 重置游戏
  public void resetGame() {
    score = 0;
    isWin = false;
    isLose = false;
    blocks = new Block[LINE * LINE];
    for (int i = 0; i < blocks.length; i++) {
      blocks[i] = new Block();
    }
    addBlock();
    addBlock();
  }

  // 左移动
  public void left() {
    boolean addBlock = false;
    for (int i = 0; i < LINE; i++) {
      Block[] line = getLine(i);
      Block[] merged = mergeLine(moveLine(line));
      setLine(i, merged);
      if (!addBlock && !compare(line, merged)) {
        addBlock = true;
      }
    }
    if (addBlock) {
      addBlock();
    }
  }

  // 右移动
  public void right() {
    blocks = rotate(180);
    left();
    blocks = rotate(180);
  }

  // 上移动
  public void up() {
    blocks = rotate(270);
    left();
    blocks = rotate(90);
  }

  // 下移动
  public void down() {
    blocks = rotate(90);
    left();
    blocks = rotate(270);
  }

  // 获取一个方块
  private Block blockAt(int x, int y) {
    return blocks[x + y * LINE];
  }

  // 随机生成一个方块
  private void addBlock() {
    List<Block> list = availableSpace();
    if (!list.isEmpty()) {
      int index = (int) (Math.random() * list.size()) % list.size();
      Block emptyTime = list.get(index);
      emptyTime.value = Math.random() < PROBABILITY ? 2 : 4;
    }
  }

  // 寻找空白方块集合
  private List<Block> availableSpace() {
    final List<Block> list = new ArrayList<>(16);
    for (Block t : blocks) {
      if (t.isEmpty()) {
        list.add(t);
      }
    }
    return list;
  }

  // 是否铺满
  private boolean isFull() {
    return availableSpace().isEmpty();
  }

  // 是否可以移动
  boolean canMove() {
    if (!isFull()) {
      return true;
    }
    for (int x = 0; x < LINE; x++) {
      for (int y = 0; y < LINE; y++) {
        Block t = blockAt(x, y);
        if ((x < 3 && t.value == blockAt(x + 1, y).value)
            || ((y < 3) && t.value == blockAt(x, y + 1).value)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean compare(Block[] line1, Block[] line2) {
    if (line1 == line2) {
      return true;
    } else if (line1.length != line2.length) {
      return false;
    }

    for (int i = 0; i < line1.length; i++) {
      if (line1[i].value != line2[i].value) {
        return false;
      }
    }
    return true;
  }

  private Block[] rotate(int angle) {
    Block[] newBlocks = new Block[LINE * LINE];
    int offsetX = 3;
    int offsetY = 3;
    if (angle == 90) {
      offsetY = 0;
    } else if (angle == 270) {
      offsetX = 0;
    }

    double rad = Math.toRadians(angle);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);
    for (int x = 0; x < LINE; x++) {
      for (int y = 0; y < LINE; y++) {
        int newX = (x * cos) - (y * sin) + offsetX;
        int newY = (x * sin) + (y * cos) + offsetY;
        newBlocks[(newX) + (newY) * LINE] = blockAt(x, y);
      }
    }
    return newBlocks;
  }

  private Block[] moveLine(Block[] oldLine) {
    LinkedList<Block> l = new LinkedList<Block>();
    for (int i = 0; i < LINE; i++) {
      if (!oldLine[i].isEmpty()) {
        l.addLast(oldLine[i]);
      }
    }
    if (l.size() == 0) {
      return oldLine;
    } else {
      Block[] newLine = new Block[LINE];
      ensureSize(l, LINE);
      for (int i = 0; i < LINE; i++) {
        newLine[i] = l.removeFirst();
      }
      return newLine;
    }
  }

  private Block[] mergeLine(Block[] oldLine) {
    LinkedList<Block> list = new LinkedList<Block>();
    for (int i = 0; i < LINE && !oldLine[i].isEmpty(); i++) {
      int num = oldLine[i].value;
      if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
        num *= 2;
        score += num;
        int ourTarget = 2048;
        if (num == ourTarget) {
          isWin = true;
        }
        i++;
      }
      list.add(new Block(num));
    }
    if (list.isEmpty()) {
      return oldLine;
    } else {
      ensureSize(list, LINE);
      return list.toArray(new Block[LINE]);
    }
  }

  private static void ensureSize(java.util.List<Block> l, int s) {
    while (l.size() != s) {
      l.add(new Block());
    }
  }

  private Block[] getLine(int index) {
    Block[] result = new Block[LINE];
    for (int i = 0; i < LINE; i++) {
      result[i] = blockAt(i, index);
    }
    return result;
  }

  private void setLine(int index, Block[] re) {
    System.arraycopy(re, 0, blocks, index * LINE, LINE);
  }

  // 绘制
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width - 10, this.getSize().height);
    for (int y = 0; y < LINE; y++) {
      for (int x = 0; x < LINE; x++) {
        drawBlock(g, blocks[x + y * LINE], x, y);
      }
    }
  }

  // 绘制方块、得分、游戏结束
  private void drawBlock(Graphics g2, Block block, int x, int y) {
    Graphics2D g = (Graphics2D) g2;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    int value = block.value;
    int xOffset = offsetCoors(x);
    int yOffset = offsetCoors(y);
    g.setColor(block.getBackground());
    g.fillRoundRect(xOffset, yOffset, BLOCK_SIZE, BLOCK_SIZE, 14, 14);
    g.setColor(block.getForeground());
    final int size = value < 100 ? 32 : value < 1000 ? 26 : 18;
    final Font font = new Font(FONT_NAME, Font.BOLD, size);
    g.setFont(font);

    String s = String.valueOf(value);
    final FontMetrics fm = getFontMetrics(font);

    final int w = fm.stringWidth(s);
    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

    if (value != 0) {
      g.drawString(
          s, xOffset + (BLOCK_SIZE - w) / 2, yOffset + BLOCK_SIZE - (BLOCK_SIZE - h) / 2 - 2);
    }
    if (isWin || isLose) {
      g.setColor(new Color(255, 255, 255, 30));
      g.fillRect(0, 0, getWidth() - 10, getHeight());
      g.setColor(new Color(78, 139, 202));
      g.setFont(new Font(FONT_NAME, Font.BOLD, 36));
      if (isWin) {
        g.drawString("通关成功!", 50, 120);
      }
      if (isLose) {
        g.drawString("游戏结束!", 50, 120);
      }
      if (isWin || isLose) {
        g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        g.setColor(new Color(0x776e65));
        g.drawString("请按空格重新开始游戏", 40, getHeight() - 60);
      }
    }
    g.setColor(new Color(0x776e65));
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
    g.drawString("Score: " + score, getWidth() - 140, getHeight() - 10);
  }

  private static int offsetCoors(int arg) {
    return arg * (BLOCK_MARGIN + BLOCK_SIZE) + BLOCK_MARGIN;
  }
}
