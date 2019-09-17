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
	//���ڵĿ��
	public static final int WIDTH=500;
	public static final int HEIGHT=900;
	//׼��ͼƬ����
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
	
	//��ʼ��Ӣ�۶���
	private Hero hero =new Hero();
	//��ʼ�����˶���
	private FlyingObject[] flyings= {};
	//��ʼ���ӵ�����
	private Bullet[] bullets= {};			
	//���Ƶ��˶����������
	int flyEnter=0;
	//�����ӵ�
	int shootIndex=0;
	
	int score=0;
	//����ͼƬ��Դ
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
			
			//����new file()��getResource			����ֻ�ܶ�class����Ŀ¼���ļ����в���
		} catch (Exception e) {
			System.out.print("ͼƬ·������");
			e.printStackTrace();
		}
	}//end---------------------------------------------------------------------------------------
	//�������ණ��
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
	//���ӵ�
	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b=bullets[i];
			g.drawImage(b.imag, b.x, b.y, null);
		}
	}
	//���л�
	public void  paintFlyobject(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f= flyings[i];
			g.drawImage(f.imag, f.x, f.y, null);
		}
	}
	//��Ӣ�ۻ�
	public void paintHero(Graphics g) {
		g.drawImage(hero.imag, hero.x, hero.y, null);
	}//end---------------------------------------------------------------------------------
	//�ƶ�
	public void action() {
		//�����������¼�
		//MouseListener, MouseWheelListener, MouseMotionListener ���ӽӿ�
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
		//��ʱ����
		Timer timer =new Timer();
		//��ʱ������
		int interVal=10;	//10ms
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				if (state==RUNNING) {
					// 1.�õ��˶����볡
					entryAction();
					//2 ��
					stepAction();
					//3 �ӵ�
					shootAction();
					//
					outOfBoundsAction();
					//
					bangAction();
					
					isCheckGameOver();
				}
				//�ػ�
				repaint();
			}
		};
	//ִ��timer.schedule(�������ڶ���,���ʱ��,����ִ��)
		timer.schedule(task, interVal, interVal);//10msһ��
	}//end-------------------------------------------------------------------------------------
	//�����������
	public FlyingObject nextOne() {
		Random random=new Random();
		int typenum=random.nextInt(20);//0~19
		if (typenum<3) {	//С�۷��������
			return new Been();//--------------------�����۷����
		}else {
			return new AirPlane();//----------------�����л�����
		}	
	}//end----------------------------------------------------------------------------------
	//���˶����볡
	public void entryAction() {
		flyEnter++;//0~100
		if(flyEnter%10==0) {		//40  80  120 һ���������
			FlyingObject one=nextOne();//---------------�������ĵ��˶���С�۷䡢�л�����ֵ��һ��FlyingObject�����
			//�����������			����������()
			flyings=Arrays.copyOf(flyings, flyings.length+1);
			//���������
			flyings[flyings.length-1]=one;//----���¶�������������һλ
		}
	}
	//�����ӵ�
	public void shootAction() {
		//һ�����100���ӵ�
		shootIndex++;
		if (shootIndex%40==0) {	//ÿ��3�����ӵ�����
			Bullet[] bs=hero.shoot();
			bullets=Arrays.copyOf(bullets, bullets.length+bs.length);
			//��������
			//System.arraycopy(Ҫ�������飬���Ŀ�ʼ������������Ŀ�����飬������Ŀ���������һ���ط��������ĳ���)
			System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);
		}
	}
	//ʵ��ÿ�������ƶ�
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
		//ѭ���ж϶����Ƿ�Խ��
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
		JFrame jFrame=new JFrame("�ɻ���ս");
		
		JPanel jPanel=new JPanel();
		ShootGame game=new ShootGame();
		jPanel.setOpaque(true);
		
		jFrame.add(game);
		jFrame.setSize(WIDTH,HEIGHT);
		jFrame.setResizable(false);
		
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		//����Ϊ����ͼ��
		jFrame.setAlwaysOnTop(true);
		jFrame.setVisible(true);
		
		game.action();
		
	}


}
