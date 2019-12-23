package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;

public class Ball extends GraphicObject
{
	/** Some hard-coded bounds for the object based on the field size
	 * Should eventually be replaced to implement application constants instead of this
	 * 
	 */
	private float XMIN = -75.0f;
	private float XMAX = 60.0f;
	private float YMIN = -25.0f;
	private float YMAX = 25.0f;
	
	/** constructor for the ball object
	 * 
	 * @param x ball initial x location
	 * @param y ball initial y location
	 * @param z ball initial z location
	 * @param width ball initial width location
	 * @param height ball initial height location
	 */
	public Ball(float x, float y, float z, float width, float height)
	{
		super(x,y,z,width,height);
		// create object bounding box/cube
		boundingBox_ = new BoundingBox(x_ - width/2, x_ + width/2,
									   y_ - width/2, y_ + width/2,
									   z_ - width/2, z_+ width/2,
									   0);
		vx_ = 25.0f;
		vy_ = 25.0f;

	}

	/**
	 * Draw the ball
	 */
	protected void draw_(PApplet app) {
		app.noFill();
		app.fill(255);
		app.lights();
		app.sphere(width_);
	}
	
	/**
	 * get the bounding box
	 */
	public BoundingBox getBoundingBox()
	{
		return boundingBox_;
	}
	
	/**
	 * object update method
	 * Also implements collision with field boundaries
	 */
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

	/**
	 * update object bounding boxes
	 */
	protected void updateBoundingBoxes_() {
		boundingBox_ = new BoundingBox(x_ - width_/2, x_ + width_/2,
									   y_ - width_/2, y_ + width_/2,
									   z_ - width_/2, z_+ width_/2,
									   0);
	}
	
}
