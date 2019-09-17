package cn.gogame;

import java.awt.image.BufferedImage;

abstract class FlyingObject {
	protected BufferedImage imag;
	protected int width;
	protected int height;
	protected int x;
	protected int y;
	abstract public void step();
	
	abstract public boolean outOfBounds();
	
	public boolean shootBy(Bullet bullet) {
		int x1=this.x;
		int y1=this.y;
		
		int x2=this.x+this.width;
		int y2=this.y+this.height;
		
		int x=bullet.x;
		int y=bullet.y;
		
		return(x>=x1&&x<=x2)&&(y>=y1&&y<=y2);
	}
}
