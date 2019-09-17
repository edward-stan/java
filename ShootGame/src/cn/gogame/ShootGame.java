package cn.gogame;
import java.applet.Applet;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


import java.io.*;
import java.applet.Applet;
import java.net.MalformedURLException;

public class ShootGame extends JPanel{
	//窗口的宽高
	public static final int WIDTH=500;
	public static final int HEIGHT=900;
	//准备图片变量
	public static BufferedImage backgroud;
	public static BufferedImage airplane;
	public static BufferedImage been;
	public static BufferedImage bullet;
	public static BufferedImage start;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	
	public static final int START=1;
	public static final int RUNNING=2;
	public static final	int PAUSE=3;
	public static final int GAMEOVER=4;
	//
	int state=1;
	
	//初始化英雄对象
	private Hero hero =new Hero();
	//初始化敌人对象
	private FlyingObject[] flyings= {};
	//初始化子弹对象
	private Bullet[] bullets= {};			
	//控制敌人对象产生数量
	int flyEnter=0;
	//控制子弹
	int shootIndex=0;
	
	int score=0;
	//加载图片资源
	static {
		try {
			backgroud=ImageIO.read(ShootGame.class.getResource("/img/background.png"));
			airplane=ImageIO.read(ShootGame.class.getResource("/img/cxk.png"));
			been=ImageIO.read(ShootGame.class.getResource("/img/cxk2.png"));
			bullet=ImageIO.read(ShootGame.class.getResource("/img/ball.png"));
			start=ImageIO.read(ShootGame.class.getResource("/img/start.png"));
			pause=ImageIO.read(ShootGame.class.getResource("/img/pause.png"));
			gameover=ImageIO.read(ShootGame.class.getResource("/img/gameover.png"));
			hero0=ImageIO.read(ShootGame.class.getResource("/img/man0.png"));
			hero1=ImageIO.read(ShootGame.class.getResource("/img/man1.png"));
			
			//区分new file()与getResource			后者只能对class所在目录下文件进行操作
		} catch (Exception e) {
			System.out.print("图片路径错误");
			e.printStackTrace();
		}
	}//end---------------------------------------------------------------------------------------
	//画出各类东西
	@Override
	public void paint(Graphics g) {
		g.drawImage(backgroud, 0, 0, null);
		paintHero(g);
		paintFlyobject(g);
		paintBullets(g);
		paintLifeAndScore(g);
		paintState(g);
	}
	public void paintState(Graphics g) {
		switch (state) {
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAMEOVER:
			g.drawImage(gameover, 0, 0, null);
		default:
			break;
		}
		
	}
	public void paintLifeAndScore(Graphics g) {
		g.setColor(Color.PINK);
		Font font=new Font(Font.SANS_SERIF,Font.BOLD,25);
		g.drawString("SCORE:"+score,10,25);
		g.drawString("LIFE:"+hero.getLife(),10,50);
	}
	//画子弹
	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b=bullets[i];
			g.drawImage(b.imag, b.x, b.y, null);
		}
	}
	//画敌机
	public void  paintFlyobject(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f= flyings[i];
			g.drawImage(f.imag, f.x, f.y, null);
		}
	}
	//画英雄机
	public void paintHero(Graphics g) {
		g.drawImage(hero.imag, hero.x, hero.y, null);
	}//end---------------------------------------------------------------------------------
	//移动
	public void action() {
		//创建鼠标监听事件
		//MouseListener, MouseWheelListener, MouseMotionListener 连接接口
		MouseAdapter adapter =new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (state==RUNNING) {
					int x=e.getX();
					int y=e.getY();
					hero.moveTo(x, y);
				}
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if(state==START) {
					state=RUNNING;
				}else if (state==GAMEOVER) {
					hero=new Hero();
					score=0;
					flyings=new FlyingObject[0];
					bullets=new Bullet[0];
					
					state=START;
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				if (state==RUNNING) {
					state=PAUSE;
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if (state==PAUSE) {
					state=RUNNING;
				}
			}			
		};		
		
		this.addMouseListener(adapter);
		this.addMouseMotionListener(adapter);
		//定时任务
		Timer timer =new Timer();
		//定时器变量
		int interVal=10;	//10ms
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				if (state==RUNNING) {
					// 1.让敌人对象入场
					entryAction();
					//2 动
					stepAction();
					//3 子弹
					shootAction();
					//
					outOfBoundsAction();
					//
					bangAction();
					
					isCheckGameOver();
				}
				//重绘
				repaint();
			}
		};
	//执行timer.schedule(方法所在对象,间隔时间,连续执行)
		timer.schedule(task, interVal, interVal);//10ms一次
	}//end-------------------------------------------------------------------------------------
	//随机产生敌人
	public FlyingObject nextOne() {
		Random random=new Random();
		int typenum=random.nextInt(20);//0~19
		if (typenum<3) {	//小蜜蜂产生概率
			return new Been();//--------------------创建蜜蜂对象
		}else {
			return new AirPlane();//----------------创建敌机对象
		}	
	}//end----------------------------------------------------------------------------------
	//敌人对象入场
	public void entryAction() {
		flyEnter++;//0~100
		if(flyEnter%10==0) {		//40  80  120 一秒产生两个
			FlyingObject one=nextOne();//---------------将产生的敌人对象（小蜜蜂、敌机）赋值给一个FlyingObject类对象
			//对象放入数组			对数组扩容()
			flyings=Arrays.copyOf(flyings, flyings.length+1);
			//对象放数组
			flyings[flyings.length-1]=one;//----将新对象放入数组最后一位
		}
	}
	//产生子弹
	public void shootAction() {
		//一秒产生100个子弹
		shootIndex++;
		if (shootIndex%40==0) {	//每秒3个多子弹数组
			Bullet[] bs=hero.shoot();
			bullets=Arrays.copyOf(bullets, bullets.length+bs.length);
			//拷贝数组
			//System.arraycopy(要拷贝数组，从哪开始拷贝，拷贝至目标数组，拷贝到目标数组的哪一个地方，拷贝的长度)
			System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);
		}
	}
	//实现每个对象移动
	public void stepAction() {
		hero.step();
		for (int i = 0; i < flyings.length; i++) {
			flyings[i].step();
		}
		//
		for (int i = 0; i < bullets.length; i++) {
			bullets[i].step();
		}
	}
	
	public void outOfBoundsAction() {
		//
		int index=0;
		//
		FlyingObject[] flyingLives=new FlyingObject[flyings.length];
		//循环判断对象是否越界
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f=flyings[i];
			if (!f.outOfBounds()) {//
				flyingLives[index]=f;
				index++;
			}
		}
		//
		flyings=Arrays.copyOf(flyingLives, index);
		//
		index=0;
		Bullet[] bsLives=new Bullet[bullets.length];
		for (int i = 0; i < bullets.length; i++) {
			Bullet b=bullets[i];
			if (!b.outOfBounds()) {
				bsLives[index]=b;
				index++;
			}
		}
		bullets=Arrays.copyOf(bsLives,index);
	}
	
	public void bangAction() {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b=bullets[i];
			bang(b);
		}
	}
	public void bang(Bullet b) {
		int index=-1;
		for (int i = 0;i < flyings.length;i++) {
			FlyingObject f=flyings[i];
			if (f.shootBy(b)) {
				index=i;
				break;
			}
		}
		if (index!=-1) {
			FlyingObject f=flyings[index];
			if (f instanceof Enemy) {
				Enemy e=(Enemy)f;
				score+=e.getScore();
			}
			if (f instanceof Award) {
				Award a=(Award)f;
				int type=a.awardType();
				switch (type) {
				case Award.AWARD_LIFE:
					hero.addlife();
					break;
				case Award.AWARD_FIRE:
					hero.addDoubleFire();
					break;
				}
				
			}
			FlyingObject one=flyings[index];
			flyings[index]=flyings[flyings.length-1];
			flyings[flyings.length-1]=one;
			flyings=Arrays.copyOf(flyings,flyings.length-1);
		}		
}

	public void isCheckGameOver() {
		if(isGameOver()) {
			state=GAMEOVER;
		}
	}
	
	public boolean isGameOver() {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f=flyings[i];
			if (hero.hit(f)) {
				FlyingObject one=flyings[i];
				hero.clearDoubleFire();
				hero.subLife();
				flyings[i]=flyings[flyings.length-1];
				flyings[flyings.length-1]=one;
				flyings=Arrays.copyOf(flyings,flyings.length-1);
			}
		}
		return hero.getLife()<=0;
	}
	
	
	public static void main(String[] args) {
		new MuSic();
		JFrame jFrame=new JFrame("飞机大战");
		
		JPanel jPanel=new JPanel();
		ShootGame game=new ShootGame();
		jPanel.setOpaque(true);
		
		jFrame.add(game);
		jFrame.setSize(WIDTH,HEIGHT);
		jFrame.setResizable(false);
		
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		//设置为最上图层
		jFrame.setAlwaysOnTop(true);
		jFrame.setVisible(true);
		
		game.action();
		
	}


}
