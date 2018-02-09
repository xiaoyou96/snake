package xiao.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 背景页面
 * @author Administrator
 *
 */
public class Yard extends Frame {
	
	PaintThread paintThread = new PaintThread();
	private boolean gameOver = false; //游戏是否结束
	
	public static final int ROWS = 50; // 行
	public static final int COLS = 50; // 列
	public static final int BLOCK_SIZE = 10; // 每格大小
	
	private Font fontGameOver = new Font("宋体", Font.BOLD, 50); // 定义 "游戏结束" 字体样式
	private int score = 0;
	
	Snake snake = new Snake(this);
	Egg egg = new Egg();
	
	Image offScreenImage = null; // 栓缓冲
	
	public void launch(){  // 画页面
		this.setLocation(200, 200);
		this.setSize(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setVisible(true);
		this.addKeyListener(new KeyMonitor());
		new Thread(paintThread).start();  // 开启线程
	}
	
	public static void main(String[] args) {
		new Yard().launch();
	}

	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		g.setColor(Color.DARK_GRAY);
		// 画出横线
		for(int i= 1; i< ROWS; i++){
			g.drawLine(0, BLOCK_SIZE * i, COLS * BLOCK_SIZE, BLOCK_SIZE * i);
		}
		for(int i= 1; i< COLS; i++){
			g.drawLine(BLOCK_SIZE * i, 0, BLOCK_SIZE * i, ROWS * BLOCK_SIZE);
		}
		
		g.setColor(Color.YELLOW);  // 显示得分
		g.drawString("score:" + score, 10, 60);
		
		if(gameOver) {  // 游戏结束
			g.setFont(fontGameOver);
			g.drawString("游戏结束", 150, 200);
			
			paintThread.pause();
		}
		g.setColor(c);
		snake.eat(egg); // 蛇吃蛋
		snake.draw(g); // 画蛇
		egg.draw(g); // 画蛋
	}
	
	public void stop(){ // 停止游戏
		gameOver = true;
	}
	
	@Override
	public void update(Graphics g) { // 栓缓冲
		if(offScreenImage == null){
			offScreenImage = this.createImage(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		}
		Graphics gOff = offScreenImage.getGraphics();
		paint(gOff);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	private class PaintThread implements Runnable{ // 起一个线程类
		private boolean running = true;
		private boolean pause = false;
		public void run() {
			while(running) {
				if(pause)
					continue; 
				else
					repaint();
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void pause() {
			this.pause = true;
		}
		
		public void reStart() {
			this.pause = false;
			snake = new Snake(Yard.this);
			gameOver = false;
		}
		
		public void gameOver() {
			running = false;
		}
	}
	
	private class KeyMonitor extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_F2) {
				paintThread.reStart();
			}
			snake.keyPressed(e);
		}
	}
	
	/**
	 * 拿到所得的分数
	 * @return 分数
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * 设置所得的分数
	 * @param score 分数
	 */
	public void setScore(int score) {
		this.score = score;
	}

}
