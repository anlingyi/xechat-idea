package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.game.chess.ChessDTO;
import cn.xeblog.plugin.util.NotifyUtils;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：游戏面板<br>
 * 作者：我是小木鱼（Lag） 原文地址：https://blog.csdn.net/lag_csdn/article/details/122324799<br>
 * 作者：Hao.<br>
 */
public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener
{
	private static final long serialVersionUID = 1353029267562430095L;

	/** 游戏逻辑 */
	GameLogic gameLogic;

	ChineseChess chineseChess;

	/** 网格行数 */
	static final int GRID_ROWS = 10;

	/** 网格列数 */
	static final int GRID_COLUMNS = 9;

	/** 网格尺寸 */
	final int gridSize = 33;

	/** 网格左上角X坐标 */
	final int gridsLeftX = 28;

	/** 网格左上角Y坐标 */
	final int gridsTopY = 28;

	/** 象棋面板（要分层，否则棋盘标签压棋子标签） */
	private JLayeredPane panelChess;

	/** 棋盘标签 */
	JLabel labelChessBorad;

	/** 棋盘图片 */
	public ImageIcon imageIconChessBoard;

	/** 棋盘状态信息（-1->无棋，其他数字->棋子信息数组的下标） */
	int[][] chessBoradState = new int[GRID_ROWS][GRID_COLUMNS];

	/** 棋子尺寸 */
	final int chessSize = 25;

	/** 棋子标签 */
	JLabel[] labelChess = new JLabel[32];

	/**
	 * 棋子信息数组<br>
	 * index -> 棋子索引<br>
	 * color -> 棋子颜色（0-黑棋，255-红棋）<br>
	 * type -> 棋子类型（rook、horse、elephant、guard、king、cannon、soldier）<br>
	 * name -> 棋子名字（黑车、黑马、黑象、黑士、黑将、黑炮、黑卒、红兵、红炮、红车、红马、红相、红仕、红帅）<br>
	 * number -> 棋子小标号（如卒1卒2中的1与2）<br>
	 * direction -> 棋子方向（T-上方，B-下方）<br>
	 * oldOldRow -> 棋子大上次行位置<br>
	 * oldOldColumn -> 棋子大上次列位置<br>
	 * oldRow -> 棋子上次行位置<br>
	 * oldColumn -> 棋子上次列位置<br>
	 * newRow -> 棋子本次行位置<br>
	 * newColumn -> 棋子本次列位置<br>
	 * dead -> 棋子是否处于死亡状态（T-死亡，F-活着）<br>
	 * oldEatIndex -> 上次被其吃棋子下标<br>
	 * eatIndex -> 本次被其吃棋子下标<br>
	 */
	@SuppressWarnings("unchecked")	//数组不支持泛型
	Map<String, String>[] mapChess = new Map[32];

	/** 棋子图片 */
	private ImageIcon[] imageIconChess = new ImageIcon[14];

	/** 电脑棋子颜色 */
	int computerChess = -1;

	/** 全部下棋信息 */
	List<Map<String, String>> listChess = new ArrayList<>();

	/** 移动线路图信息 */
	List<Map<String, Integer>> listMove = new ArrayList<>();

	/** 按钮控件 */
	JButton jb_undo,jb_surrender;

	/** 标签控件 */
	JLabel jlb_blackStateText,jlb_redStateText;

	/** 是否第一次点击 */
	boolean isFirstClick = true;

	/** 第一次点击棋子 */
	Map<String, String> firstClickChess = null;

	/**
	 * 落子指示器<br>
	 * row -> 行坐标<br>
	 * column -> 列坐标<br>
	 * show -> 是否显示（0-不显示，1-显示）<br>
	 * color -> 颜色（0-黑，255-红）<br>
	 */
	Map<String, Integer> mapPointerChess = new HashMap<>();

	/**
	 * 移动指示器<br>
	 * row -> 行坐标<br>
	 * column -> 列坐标<br>
	 * show -> 是否显示（0-不显示，1-显示）<br>
	 * color -> 颜色（-1-默认，0-黑，255-红）<br>
	 */
	Map<String, Integer> mapPointerMove = new HashMap<>();

	/** 判断游戏是否结束（true-结束，false-未结束） */
	boolean isGameOver = false;

	/**
	 * 功能：构造函数<br>
	 */
	public GamePanel(ChineseChess chineseChess) {

		this.chineseChess = chineseChess;

		//设置象棋面板
		this.setLayout(null);
		// 设置棋盘宽高
		this.setPreferredSize(new Dimension(320,360));
		this.panelChess = new JLayeredPane();
		this.panelChess.setBounds(0,0,320,358);
		this.panelChess.setLayout(null);
		this.add(this.panelChess);

		//加载图片
		this.loadImage();

		//设置棋盘背景图片
		this.labelChessBorad = new JLabel();
		this.labelChessBorad.setBounds(0,0,this.panelChess.getWidth(),this.panelChess.getHeight());
		this.labelChessBorad.setIcon(this.imageIconChessBoard);
		this.labelChessBorad.addMouseListener(this);
		this.labelChessBorad.addMouseMotionListener(this);
		this.panelChess.add(this.labelChessBorad, JLayeredPane.DEFAULT_LAYER);	//最底层

		//建立棋子标签
		this.createChess();

		// 底部功能区布局
		this.option();

		//游戏逻辑
		this.gameLogic = new GameLogic(this);

		// 直接开始游戏
		this.newGame();
	}

	/**
	 * 功能：加载图片<br>
	 * 备注：考虑Jar包问题，所以图片路径用URL格式<br>
	 */
	private void loadImage()
	{
		try
		{
			URL resource = this.getClass().getResource("/games/chinese-chess/chessboard3.png");
			ImageIcon chessboard = new ImageIcon(resource);
			//棋盘图片
			this.imageIconChessBoard = new ImageIcon(chessboard.getImage().getScaledInstance(this.panelChess.getWidth(),this.panelChess.getHeight(), Image.SCALE_SMOOTH));	//缩放图片来适应标签大小
			//棋子图片
			for(int i=0;i<this.imageIconChess.length;i++)
			{
				String imgPath = "/games/chinese-chess/chess" + i + ".png";
				this.imageIconChess[i] = new ImageIcon(new ImageIcon(this.getClass().getResource(imgPath)).getImage().getScaledInstance(this.chessSize,this.chessSize, Image.SCALE_SMOOTH));	//缩放图片来适应标签大小
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * 功能：初始化游戏<br>
	 */
	private void initGame()
	{
		//重新设置参数
		this.isGameOver = true;

		//清空下棋与移动线路图列表
		this.listChess.clear();
		this.listMove.clear();

		//指示器初始化
		this.mapPointerChess.put("row",-1);
		this.mapPointerChess.put("column",-1);
		this.mapPointerChess.put("show",0);
		this.mapPointerChess.put("color",-1);
		this.mapPointerMove.put("row",-1);
		this.mapPointerMove.put("column",-1);
		this.mapPointerMove.put("show",0);
		this.mapPointerMove.put("color",-1);

		//电脑与玩家棋子颜色
		if(chineseChess.chessCache.currentBattle == ChessCache.Battle.PVC)
		{
			if(chineseChess.chessCache.currentPlayer == ChessCache.Player.BLACK)
			{
				this.computerChess = ChessCache.Player.RED.getValue();
			}
			else
			{
				this.computerChess = ChessCache.Player.BLACK.getValue();
			}
		}

		//设置控件状态
		this.setComponentState(false);

		//初始化棋子（默认我方棋子在下方）
		this.initChess();
	}

	/**
	 * 功能：布局棋子<br>
	 */
	void initChess()
	{
		//先按默认设置棋子信息（玩家执红：红方在下，黑方在上）
		for(int index=0;index<this.mapChess.length;index++)
		{
			this.mapChess[index].put("index", Integer.toString(index));
			this.mapChess[index].put("oldOldRow","-1");
			this.mapChess[index].put("oldOldColumn","-1");
			this.mapChess[index].put("oldRow","-1");
			this.mapChess[index].put("oldColumn","-1");
			this.mapChess[index].put("dead","F");
			this.mapChess[index].put("oldEatIndex","-1");
			this.mapChess[index].put("eatIndex","-1");
			if(index < 9)			//黑车马象士将士象马车
			{
				this.mapChess[index].put("direction","T");	//上方
				this.mapChess[index].put("newRow","0");
				this.mapChess[index].put("newColumn", Integer.toString(index));
			}
			else if(index == 9)		//黑炮1
			{
				this.mapChess[index].put("direction","T");
				this.mapChess[index].put("newRow","2");
				this.mapChess[index].put("newColumn","1");
			}
			else if(index == 10)	//黑炮2
			{
				this.mapChess[index].put("direction","T");
				this.mapChess[index].put("newRow","2");
				this.mapChess[index].put("newColumn","7");
			}
			else if(index > 10 && index < 16)	//黑卒n
			{
				this.mapChess[index].put("direction","T");
				this.mapChess[index].put("newRow","3");
				this.mapChess[index].put("newColumn", Integer.toString(2 * index - 22));
			}
			else if(index >= 16 && index < 21)	//红兵n
			{
				this.mapChess[index].put("direction","B");	//下方
				this.mapChess[index].put("newRow","6");
				this.mapChess[index].put("newColumn", Integer.toString(2 * index - 32));
			}
			else if(index == 21)		//红炮1
			{
				this.mapChess[index].put("direction","B");
				this.mapChess[index].put("newRow","7");
				this.mapChess[index].put("newColumn","1");
			}
			else if(index == 22)		//红炮2
			{
				this.mapChess[index].put("direction","B");
				this.mapChess[index].put("newRow","7");
				this.mapChess[index].put("newColumn","7");
			}
			else if(index > 22 && index < 32)	//红车马相仕帅仕相马车
			{
				this.mapChess[index].put("direction","B");
				this.mapChess[index].put("newRow","9");
				this.mapChess[index].put("newColumn", Integer.toString(index - 23));
			}
		}

		//如果玩家执黑则坐标反过来
		if(chineseChess.chessCache.currentPlayer == ChessCache.Player.BLACK)
		{
			//棋子信息对调（行变abs(9-行)，列不变）
			for (Map<String, String> chess : this.mapChess) {
				int row = Integer.parseInt(chess.get("newRow"));
				int column = Integer.parseInt(chess.get("newColumn"));
				chess.put("newRow", Integer.toString(Math.abs(9 - row)));
				chess.put("newColumn", Integer.toString(Math.abs(8 - column)));
				if ("T".equals(chess.get("direction"))) {
					chess.put("direction", "B");
				} else {
					chess.put("direction", "T");
				}
			}
		}

		//清空棋盘状态信息
		for(int row=0;row<this.chessBoradState.length;row++)
		{
			for(int column=0;column<this.chessBoradState[0].length;column++)
			{
				this.chessBoradState[row][column] = -1;
			}
		}
		//再根据棋子状态信息设置棋盘状态信息
		for(int index=0;index<this.mapChess.length;index++)
		{
			int row = Integer.parseInt(this.mapChess[index].get("newRow"));
			int column = Integer.parseInt(this.mapChess[index].get("newColumn"));
			this.chessBoradState[row][column] = index;
		}

		//重新布局棋子（X->列，Y->行）
		for(int index=0;index<this.mapChess.length;index++)
		{
			int row = Integer.parseInt(this.mapChess[index].get("newRow"));
			int column = Integer.parseInt(this.mapChess[index].get("newColumn"));
			this.labelChess[index].setBounds(this.gridsLeftX + column * this.gridSize - this.chessSize/2,this.gridsTopY + row * this.gridSize  - this.chessSize/2,this.chessSize,this.chessSize);
		}

	}

	/**
	 * 功能：设置控件状态<br>
	 * 参数：true-新开局；false-未开局<br>
	 */
	public void setComponentState(boolean _flag)
	{
		if(_flag)	//新游戏已经开始了
		{
			this.jb_undo.setEnabled(true);
			this.jb_surrender.setEnabled(true);
		}
		else	//新游戏还未开始
		{
			this.jb_undo.setEnabled(false);
			this.jb_surrender.setEnabled(false);
		}
	}

	/**
	 * 功能：建立棋子标签<br>
	 */
	private void createChess()
	{
		for(int index=0;index<this.labelChess.length;index++)
		{
			this.labelChess[index] = new JLabel();
			this.labelChess[index].setName(Integer.toString(index));
			this.mapChess[index] = new HashMap<>();
			if(index == 0)			//黑车1
			{
				this.labelChess[index].setIcon(this.imageIconChess[4]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","rook");
				this.mapChess[index].put("name","黑车");
				this.mapChess[index].put("number","1");
			}
			else if(index == 8)		//黑车2
			{
				this.labelChess[index].setIcon(this.imageIconChess[4]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","rook");
				this.mapChess[index].put("name","黑车");
				this.mapChess[index].put("number","2");
			}
			else if(index == 1)		//黑马1
			{
				this.labelChess[index].setIcon(this.imageIconChess[3]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","horse");
				this.mapChess[index].put("name","黑马");
				this.mapChess[index].put("number","1");
			}
			else if(index == 7)		//黑马2
			{
				this.labelChess[index].setIcon(this.imageIconChess[3]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","horse");
				this.mapChess[index].put("name","黑马");
				this.mapChess[index].put("number","2");
			}
			else if(index == 2)		//黑象1
			{
				this.labelChess[index].setIcon(this.imageIconChess[2]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","elephant");
				this.mapChess[index].put("name","黑象");
				this.mapChess[index].put("number","1");
			}
			else if(index == 6)		//黑象2
			{
				this.labelChess[index].setIcon(this.imageIconChess[2]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","elephant");
				this.mapChess[index].put("name","黑象");
				this.mapChess[index].put("number","2");
			}
			else if(index == 3)		//黑士1
			{
				this.labelChess[index].setIcon(this.imageIconChess[1]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","guard");
				this.mapChess[index].put("name","黑士");
				this.mapChess[index].put("number","1");
			}
			else if(index == 5)		//黑士2
			{
				this.labelChess[index].setIcon(this.imageIconChess[1]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","guard");
				this.mapChess[index].put("name","黑士");
				this.mapChess[index].put("number","2");
			}
			else if(index == 4)		//黑将
			{
				this.labelChess[index].setIcon(this.imageIconChess[0]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","king");
				this.mapChess[index].put("name","黑将");
				this.mapChess[index].put("number","");
			}
			else if(index == 9 || index == 10)		//黑炮n
			{
				this.labelChess[index].setIcon(this.imageIconChess[5]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","cannon");
				this.mapChess[index].put("name","黑炮");
				this.mapChess[index].put("number", Integer.toString(index - 8));
			}
			else if(index > 10 && index < 16)	//黑卒n
			{
				this.labelChess[index].setIcon(this.imageIconChess[6]);
				this.mapChess[index].put("color","0");
				this.mapChess[index].put("type","soldier");
				this.mapChess[index].put("name","黑卒");
				this.mapChess[index].put("number", Integer.toString(index - 10));
			}
			else if(index >= 16 && index < 21)	//红兵n
			{
				this.labelChess[index].setIcon(this.imageIconChess[13]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","soldier");
				this.mapChess[index].put("name","红兵");
				this.mapChess[index].put("number", Integer.toString(index - 15));
			}
			else if(index == 21 || index == 22)		//红炮n
			{
				this.labelChess[index].setIcon(this.imageIconChess[12]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","cannon");
				this.mapChess[index].put("name","红炮");
				this.mapChess[index].put("number", Integer.toString(index - 20));
			}
			else if(index == 23)		//红车1
			{
				this.labelChess[index].setIcon(this.imageIconChess[11]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","rook");
				this.mapChess[index].put("name","红车");
				this.mapChess[index].put("number","1");
			}
			else if(index == 31)		//红车2
			{
				this.labelChess[index].setIcon(this.imageIconChess[11]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","rook");
				this.mapChess[index].put("name","红车");
				this.mapChess[index].put("number","2");
			}
			else if(index == 24)		//红马1
			{
				this.labelChess[index].setIcon(this.imageIconChess[10]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","horse");
				this.mapChess[index].put("name","红马");
				this.mapChess[index].put("number","1");
			}
			else if(index == 30)		//红马2
			{
				this.labelChess[index].setIcon(this.imageIconChess[10]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","horse");
				this.mapChess[index].put("name","红马");
				this.mapChess[index].put("number","2");
			}
			else if(index == 25)		//红相1
			{
				this.labelChess[index].setIcon(this.imageIconChess[9]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","elephant");
				this.mapChess[index].put("name","红相");
				this.mapChess[index].put("number","1");
			}
			else if(index == 29)		//红相2
			{
				this.labelChess[index].setIcon(this.imageIconChess[9]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","elephant");
				this.mapChess[index].put("name","红相");
				this.mapChess[index].put("number","2");
			}
			else if(index == 26)		//红仕1
			{
				this.labelChess[index].setIcon(this.imageIconChess[8]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","guard");
				this.mapChess[index].put("name","红仕");
				this.mapChess[index].put("number","1");
			}
			else if(index == 28)		//红仕2
			{
				this.labelChess[index].setIcon(this.imageIconChess[8]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","guard");
				this.mapChess[index].put("name","红仕");
				this.mapChess[index].put("number","2");
			}
			else if(index == 27)		//红帅
			{
				this.labelChess[index].setIcon(this.imageIconChess[7]);
				this.mapChess[index].put("color","255");
				this.mapChess[index].put("type","king");
				this.mapChess[index].put("name","红帅");
				this.mapChess[index].put("number","");
			}
			this.labelChess[index].addMouseListener(this);
			this.labelChess[index].addMouseMotionListener(this);
			this.panelChess.add(this.labelChess[index], JLayeredPane.DRAG_LAYER);	//最高层
		}

	}

	/**
	 * 功能：底部功能区布局<br>
	 */
	private void option()
	{

		//红棋提示
		JPanel groupBoxRed = new JPanel();
		groupBoxRed.setLayout(null);
		groupBoxRed.setBackground(this.getBackground());
		groupBoxRed.setBounds((int) (this.panelChess.getWidth() * 0.01), this.panelChess.getHeight(),90,50);
		groupBoxRed.setBorder(BorderFactory.createTitledBorder("红棋"));
		this.add(groupBoxRed);
		JLabel jlb_whiteState = new JLabel("状态：");
		jlb_whiteState.setFont(new Font("微软雅黑",Font.PLAIN,12));
		jlb_whiteState.setBounds(10,16,40,30);
		groupBoxRed.add(jlb_whiteState);
		this.jlb_redStateText = new JLabel("未开始");
		this.jlb_redStateText.setFont(new Font("微软雅黑",Font.BOLD,12));
		this.jlb_redStateText.setForeground(JBColor.darkGray);
		this.jlb_redStateText.setBounds(44,16,40,30);
		groupBoxRed.add(this.jlb_redStateText);


		//按钮
		this.jb_undo = new JButton("悔棋");
		this.jb_undo.setFont(new Font("微软雅黑",Font.PLAIN,12));
		this.jb_undo.setBounds((int) (this.panelChess.getWidth() * 0.3), this.panelChess.getHeight() + 15,60,30);
		this.jb_undo.setActionCommand("undo");
		this.jb_undo.addActionListener(this);
		this.jb_undo.setEnabled(false);
		this.add(this.jb_undo);

		this.jb_surrender = new JButton("投降");
		this.jb_surrender.setFont(new Font("微软雅黑",Font.PLAIN,12));
		this.jb_surrender.setBounds((int) (this.panelChess.getWidth() * 0.5), this.panelChess.getHeight() + 15,60,30);
		this.jb_surrender.setActionCommand("surrender");
		this.jb_surrender.addActionListener(this);
		this.jb_surrender.setEnabled(false);
		this.add(this.jb_surrender);


		//黑棋提示
		JPanel groupBoxBlack = new JPanel();
		groupBoxBlack.setLayout(null);
		groupBoxBlack.setBackground(this.getBackground());
		groupBoxBlack.setBounds((int) (this.panelChess.getWidth() * 0.7), this.panelChess.getHeight(),90,50);
		groupBoxBlack.setBorder(BorderFactory.createTitledBorder("黑棋"));
		this.add(groupBoxBlack);
		JLabel jlb_blackState = new JLabel("状态：");
		jlb_blackState.setFont(new Font("微软雅黑",Font.PLAIN,12));
		jlb_blackState.setBounds(10,16,40,30);
		groupBoxBlack.add(jlb_blackState);
		this.jlb_blackStateText = new JLabel("未开始");
		this.jlb_blackStateText.setFont(new Font("微软雅黑",Font.BOLD,12));
		this.jlb_blackStateText.setForeground(JBColor.darkGray);
		this.jlb_blackStateText.setBounds(44,16,40,30);
		groupBoxBlack.add(this.jlb_blackStateText);
	}

	/**
	 * 功能：绘图<br>
	 */
	@Override
	public void paint(Graphics g)
	{
		//调用父类,让其做一些事前的工作，如刷新屏幕等
		super.paint(g);

		//因为要画一些特殊效果，所以要用Graphics2D
		Graphics2D g2D = (Graphics2D)g;

		//画移动指示器
		if(this.mapPointerMove.get("show") == 1)
		{
			if(this.mapPointerMove.get("color") == ChessCache.Player.BLACK.getValue())
			{
				g2D.setColor(JBColor.BLACK);
			}
			else if(this.mapPointerMove.get("color") == ChessCache.Player.RED.getValue())
			{
				g2D.setColor(JBColor.RED);
			}
			else
			{
				g2D.setColor(JBColor.GREEN);
			}
			g2D.setStroke(new BasicStroke(3.5f));
			//先以交叉点为中心取到指示器周围的4个角坐标
			//中心点坐标
			int x = this.gridsLeftX + this.mapPointerMove.get("column") * this.gridSize;
			int y = this.gridsTopY + this.mapPointerMove.get("row") * this.gridSize;
			//左上角坐标，并向下向右画线
			int x1 = x - this.chessSize / 2;
			int y1 = y - this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
			//右上角坐标，并向下向左画线
			x1 = x + this.chessSize / 2;
			y1 = y - this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
			//左下角坐标，并向上向右画线
			x1 = x - this.chessSize / 2;
			y1 = y + this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
			//右下角坐标，并向上向左画线
			x1 = x + this.chessSize / 2;
			y1 = y + this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
			//System.out.println("("+this.mapPointerChess.get("x")+","+this.mapPointerChess.get("y")+")");
		}

		//画落子指示器
		if(this.mapPointerChess.get("show") == 1)
		{
			if(this.mapPointerChess.get("color") == ChessCache.Player.BLACK.getValue())
			{
				g2D.setColor(JBColor.BLACK);
			}
			else
			{
				g2D.setColor(JBColor.RED);
			}
			g2D.setStroke(new BasicStroke(3.5f));
			//先以交叉点为中心取到指示器周围的4个角坐标
			//中心点坐标
			int x = this.gridsLeftX + this.mapPointerChess.get("column") * this.gridSize;
			int y = this.gridsTopY + this.mapPointerChess.get("row") * this.gridSize;
			//左上角坐标，并向下向右画线
			int x1 = x - this.chessSize / 2;
			int y1 = y - this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
			//右上角坐标，并向下向左画线
			x1 = x + this.chessSize / 2;
			y1 = y - this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 + this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
			//左下角坐标，并向上向右画线
			x1 = x - this.chessSize / 2;
			y1 = y + this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 + this.chessSize / 4,y1);
			//右下角坐标，并向上向左画线
			x1 = x + this.chessSize / 2;
			y1 = y + this.chessSize / 2;
			g2D.drawLine(x1,y1,x1,y1 - this.chessSize / 4);
			g2D.drawLine(x1,y1,x1 - this.chessSize / 4,y1);
			//System.out.println("("+this.mapPointerChess.get("x")+","+this.mapPointerChess.get("y")+")");
		}

		// TODO 摸鱼简化版：画可移动线路图
		if(this.listMove.size() > 0) {
			g2D.setColor(JBColor.BLUE);
			new ArrayList<>(this.listMove).forEach(map -> {
				int row = map.get("row");
				int column = map.get("column");
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    //消除画图锯齿
				g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);        //追求速度或质量
				g2D.fillArc(this.gridsLeftX + column * this.gridSize - 6, this.gridsTopY + row * this.gridSize - 4, 10, 10, 0, 360);
			});
		}
	}

	/**
	 * 功能：开始新游戏<br>
	 */
	public void newGame()
	{
		//初始化游戏
		this.initGame();
		//设置控件状态
		this.setComponentState(true);

		this.jb_undo.setEnabled(false);

		//设置游戏结束标识
		this.isGameOver = false;

		if(chineseChess.chessCache.currentBattle == ChessCache.Battle.PVC && chineseChess.chessCache.currentPlayer == ChessCache.Player.BLACK){
			this.gameLogic.computerPlay();
		}
	}

	boolean canRepent() {
		// 前三步不允许悔棋
		boolean hasNotStarted = this.listChess.size() <= 6;
		return !hasNotStarted;
	}

	/**
	 * 功能：悔棋<br>
	 */
	public void undoConfirm()
	{
		if(this.isGameOver){return;}

		if(chineseChess.chessCache.currentMode == ChessCache.Mode.ONLINE){
			jb_undo.setEnabled(false);
			jb_surrender.setEnabled(false);

			chineseChess.send(new Point(ChessDTO.Option.UNDO));

			// 通知自己
			NotifyUtils.info("提示", "等待对方同意");
		}else{
			gameLogic.undo();
		}
	}

	/**
	 * 功能：对方悔棋<br>
	 */
	public void otherSideUndo() {
		Map<String, String> mapLast = listChess.get(listChess.size() - 1);
		int index = Integer.parseInt(mapLast.get("index"));
		int color = Integer.parseInt(mapLast.get("color"));
		int oldRow = Integer.parseInt(mapLast.get("oldRow"));
		int oldColumn = Integer.parseInt(mapLast.get("oldColumn"));

		gameLogic.undoStep();

		//重新生成落子指示器
		mapPointerChess.put("row",oldRow);
		mapPointerChess.put("column",oldColumn);
		mapPointerChess.put("color",color);
		mapPointerChess.put("show",1);
		isFirstClick = false;
		firstClickChess = mapChess[index];

		//清除移动路线图
		listMove.clear();

		if(color == ChessCache.Player.RED.getValue())
		{
			jlb_redStateText.setText("悔棋中");
			jlb_blackStateText.setText(gameLogic.getPrompt(this.listChess.get(this.listChess.size() - 1)));
		}
		else
		{
			jlb_redStateText.setText(gameLogic.getPrompt(this.listChess.get(this.listChess.size() - 1)));
			jlb_blackStateText.setText("悔棋中");
		}

		//刷新
		repaint();
	}

	/**
	 * 功能：投降<br>
	 */
	public void surrender()
	{
		if(chineseChess.chessCache.currentMode == ChessCache.Mode.ONLINE){
			this.chineseChess.send(new Point(ChessDTO.Option.SURRENDER));
		}

		JOptionPane.showMessageDialog(this,"OK，loser!");

		gameOver();
	}

	public void gameOver() {
		this.isGameOver = true;
		this.setComponentState(false);
		this.jlb_blackStateText.setText("已结束");
		this.jlb_redStateText.setText("已结束");

		if (chineseChess.chessCache.currentMode == ChessCache.Mode.ONLINE) {
			JButton jb_out = chineseChess.gameOverButton();
			jb_out.setFont(new Font("微软雅黑",Font.PLAIN,12));
			jb_out.setBounds((int) (panelChess.getWidth() * 0.38), (int) (panelChess.getHeight() + 10),70,40);
			this.remove(jb_undo);
			this.remove(jb_surrender);
			this.add(jb_out);
		}
	}

	/**
	 * 功能：离开<br>
	 */
	public void exit()
	{
		this.chineseChess.init();
	}

	/**
	 * 功能：功能监听<br>
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if("undo".equals(command))
		{
			this.undoConfirm();
		}
		else if("surrender".equals(command))
		{
			this.surrender();
		}
		else if("exit".equals(command))
		{
			this.exit();
		}
	}

	/**
	 * 功能：鼠标点击事件监听<br>
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		this.gameLogic.mouseClicked(e);
	}

	/**
	 * 功能：鼠标移动事件监听<br>
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (chineseChess.chessCache.currentUI == ChessDTO.UI.CLASSIC) {
			this.gameLogic.mouseMoved(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e){}

	@Override
	public void mouseReleased(MouseEvent e){}

	@Override
	public void mouseEntered(MouseEvent e){}

	@Override
	public void mouseExited(MouseEvent e){}

	@Override
	public void mouseDragged(MouseEvent e){}

	/**
	 * 设置对方的棋子
	 * @author Hao.
	 * @date 2022/9/22 8:42
	 * @return
	 */
	void setChess(Point point) {
		int index = point.index;
		Map<String, String> chess = this.mapChess[index];
		int row = point.x;
		int column = point.y;

		this.gameLogic.moveTo(chess, row, column);
		//取消移动路线图
		this.listMove.clear();
		//落子指示器
		this.mapPointerChess.put("row", row);
		this.mapPointerChess.put("column", column);
		this.mapPointerChess.put("show",1);
		this.mapPointerChess.put("color", Integer.parseInt(chess.get("color")));
		//更新提示
		if(Integer.parseInt(chess.get("color")) == ChessCache.Player.BLACK.getValue())
		{
			this.jlb_redStateText.setText("思考中");
			this.jlb_blackStateText.setText(gameLogic.getOtherSidePrompt(chess));
		}
		else
		{
			this.jlb_redStateText.setText(gameLogic.getOtherSidePrompt(chess));
			this.jlb_blackStateText.setText("思考中");
		}

		this.repaint();

		//判断是否将军
		this.gameLogic.check(Integer.parseInt(chess.get("color")));
	}

	void otherSideCheck()
	{
		this.jlb_redStateText.setText("将军");
		this.jlb_blackStateText.setText("将军");
		this.listMove.clear();
		this.repaint();
	}

	/**
	 * 功能：判断下一步是红棋下还是黑棋下<br>
	 */
	int getNextChessColor()
	{
		int chessColor;

		//得到上一步信息
		if(listChess.size() > 0)
		{
			Map<String, String> mapLast = listChess.get(listChess.size() - 1);
			if(Integer.parseInt(mapLast.get("color")) == ChessCache.Player.BLACK.getValue())
			{
				chessColor = ChessCache.Player.RED.getValue();
			}
			else
			{
				chessColor = ChessCache.Player.BLACK.getValue();
			}
		}
		else
		{
			// 第一步棋子：必须是红方
			chessColor = ChessCache.Player.RED.getValue();
		}

		return chessColor;
	}

	public JButton exitButton() {
		JButton exit = new JButton("离开");
		exit.setFont(new Font("微软雅黑",Font.PLAIN,12));
		exit.setBounds((int) (panelChess.getWidth() * 0.5), this.panelChess.getHeight() + 15,60,30);
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		exit.setEnabled(true);
		return exit;
	}
}
