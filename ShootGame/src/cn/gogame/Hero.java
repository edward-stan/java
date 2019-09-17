package cn.gogame;

import java.awt.image.BufferedImage;

public class Hero extends FlyingObject{
	protected int life;//����ֵ
	protected int doublefire;//����ֵ
	//��ŷɻ�ͼƬ����
	BufferedImage[] images;
	//�л�ͼƬ����
	int index;
	
	public Hero() {
		// 
		imag=ShootGame.hero0;
		width=imag.getWidth();
		height=imag.getHeight();
		
		//��ʼ������ֵ������ֵ��ͼƬ����
		life=3;
		doublefire=0;
		images=new BufferedImage[] {ShootGame.hero0,ShootGame.hero1};
		//
		index=0;
		x=200;
		y=400;
	}
	//ͼƬ�л�
	@Override
	public void step() {
		imag=images[index++/10%images.length];		//0��1
	}
	public Bullet[] shoot() {
		//�ѷɻ���Ϊ4�ȷ�
		int xStep=this.width/4;
		int yStep=20;	//����
		
		//if (doublefire>0) {//˫������
			//Bullet[] bs=new Bullet[2];
		//	bs[0]=new Bullet(this.x+1*xStep,this.y+yStep);//---�������ӵ�����
			//bs[1]=new Bullet(this.x+3*xStep,this.y+yStep);//---�������ӵ�����
		//	doublefire--;//
		//	return bs;
	//	}else {
			Bullet[] bs=new Bullet[1];
			bs[0]=new Bullet(this.x+2*xStep,this.y-yStep);//---�����ӵ�����
			return bs;
	//	}
	}
	//
	public void moveTo(int x,int y) {
		this.x=x-this.width/2;
		this.y=y-this.height/2;
	}
	@Override
	public boolean outOfBounds() {
		return false;
	}
	public void addlife() {
		life++;
	}
	public void addDoubleFire() {
		doublefire=10;
	}
	public int getLife() {
		return life;
	}
	
	public boolean hit(FlyingObject obj) {
		int x1=obj.x-this.width/2;
		int y1=obj.y-this.height/2;
		int x2=obj.x+obj.width+this.width/2;
		int y2=obj.y+obj.height+this.height/2;
		
		int x=this.x+this.width/2;
		int y=this.y+this.height/2;
		return (x>x1&&x<x2)&&(y>y1&&y<y2);
	}
	public void clearDoubleFire() {
		doublefire=0;
	}
	public void subLife() {
		life--;	
	}
	
}
