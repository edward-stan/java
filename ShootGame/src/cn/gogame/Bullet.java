package cn.gogame;

class Bullet extends FlyingObject{
	protected int speed=-2;
	public Bullet(int x,int y) {	//构造方法
		imag =ShootGame.bullet;
		width=imag.getWidth();
		height=imag.getHeight();
		this.x=x;
		this.y=y;
	}
	@Override
	public void step() {
		this.y+=speed;
	}
	@Override
	public boolean outOfBounds() {
		
		return this.y<=-this.height;
	}
}
