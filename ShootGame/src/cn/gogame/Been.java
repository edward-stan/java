package cn.gogame;

import java.util.Random;

public class Been extends FlyingObject implements Award{
	protected int xSpeed=1;
	protected int ySpeed=1;
	protected int awardType;
	
	public Been() {
		imag=ShootGame.been;
		width=imag.getWidth();
		height=imag.getHeight();
		Random random=new Random();
		x=random.nextInt(ShootGame.WIDTH-this.width);
		y=-height;
		awardType=random.nextInt(2);
	}
	@Override
	public int awardType() {
		return awardType;
	}

	@Override
	public void step() {
		this.x+=xSpeed;
		this.y+=ySpeed;
		if (this.x>=ShootGame.WIDTH-this.width) {
			this.xSpeed-=1;
		}
		if (this.x<=0) {
			this.xSpeed=1;
		}
	}
	@Override
	public boolean outOfBounds() {
		
		return this.y>=ShootGame.HEIGHT;
	}
}
