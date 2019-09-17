package cn.gogame;

import java.util.Random;

class AirPlane extends FlyingObject implements Enemy{
	public int speed=2;//创建坐标增量
	
	public AirPlane() {
		imag=ShootGame.airplane;
		width=imag.getWidth();
		height=imag.getHeight();
		Random random=new Random();
		
		x=random.nextInt(ShootGame.WIDTH-this.width);
		y=-height;
	}
	
	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public void step() {
		this.y+=speed;
	}
	
	public boolean outOfBounds() {
		return this.y>=ShootGame.HEIGHT;
	}
}
