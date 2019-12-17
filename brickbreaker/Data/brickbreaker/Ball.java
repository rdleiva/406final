package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;

public class Ball extends GraphicObject
{
	//private float vx_, vy_;
	private float XMIN = -75.0f;
	private float XMAX = 60.0f;
	private float YMIN = -25.0f;
	private float YMAX = 25.0f;
	
	public Ball(float x, float y, float z, float width, float height)
	{
		super(x,y,z,width,height);
		boundingBox_ = new BoundingBox(x_ - width/2, x_ + width/2,
									   y_ - width/2, y_ + width/2,
									   z_ - width/2, z_+ width/2,
									   0);
		vx_ = 25.0f;
		vy_ = 25.0f;

	}

	protected void draw_(PApplet app) {
		app.noFill();
		app.fill(255);
		app.lights();
		app.sphere(width_);
	}
	
	public BoundingBox getBoundingBox()
	{
		return boundingBox_;
	}
	
	public void update_(float dt)
	{
		x_ += vx_ * dt;
		y_ += vy_ * dt;
		updateBoundingBoxes_();
		
		if(x_ < XMIN)
		{
			x_ = XMIN;
			vx_ = -vx_;
		}
		else if(x_ > XMAX)
		{
			x_ = XMAX;
			vx_ = -vx_;
		}
		if(y_ < YMIN)
		{
			y_ = YMIN;
			vy_ = -vy_;
		}
		else if(y_ > YMAX)
		{
			y_ = YMAX;
			vy_ = -vy_;
		}
		
	}

	protected void updateBoundingBoxes_() {
		boundingBox_ = new BoundingBox(x_ - width_/2, x_ + width_/2,
									   y_ - width_/2, y_ + width_/2,
									   z_ - width_/2, z_+ width_/2,
									   0);
	}
	
	public void setVY(float y)
	{
		vy_ = y;
	}
	
}
