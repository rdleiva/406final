package brickbreaker.Data.brickbreaker;
import processing.core.*;

public abstract class GraphicObject {

	/**
	 * x location of any given graphic object
	 */
	protected float x_;
	/**
	 * y location of any given graphic object
	 */
	protected float y_;
	/**
	 * z location of any given graphic object
	 */
	protected float z_;
	/**
	 * x velocity of graphic object
	 */
	protected float vx_;
	/**
	 * y velocity of graphic object
	 */
	protected float vy_;
	/**
	 * z location of graphic object
	 */
	protected float vz_;
	/**
	 * width of the graphic object
	 */
	protected float width_;
	/**
	 * height of the graphic object
	 */
	protected float height_;
	/**
	 * color of graphic object
	 */
	protected int color_;
	/**
	 * every graphic object must have a cube-shaped bounding box in absolute FoR
	 */
	protected BoundingBox boundingBox_;
	
	/** Constructor for graphic object
	 * 
	 * @param x	initial x location of graphic object
	 * @param y	initial y location of graphic object
	 * @param z	initial z location of graphic object
	 * @param width	width of the graphic object
	 * @param height height of the graphic object
	 */
	public GraphicObject(float x, float y, float z, float width, float height) 
	{
		x_ = x;
		y_ = y;
		z_ = z;
		width_ = width;
		height_ = height;
	}
	
	/** the processing interface for drawing an object
	 * 
	 * @param app the processing app which is running
	 */
	public void draw(PApplet app)
	{
		app.pushMatrix();
		app.translate(x_,  y_, z_);
		draw_(app);
		app.popMatrix();
	}
	
	/**	every graphic object must have its own draw method
	 * 
	 * @param app the processing app which is running
	 */
	protected abstract void draw_(PApplet app);
	
	/** every graphic object must be able to return a copy of its bounding box
	 * 
	 * @return the object's bounding box
	 */
	public abstract BoundingBox getBoundingBox();
	
	/**	each object has a method to update its own location
	 * 
	 * @param dt the change in time between updates
	 */
	public abstract void update_(float dt);
	
	/** getter method for x
	 * 
	 * @return object's x location
	 */
	public float getX()
	{
		return x_;
	}
	
	/** getter method for y
	 * 
	 * @return object's y location
	 */
	public float getY()
	{
		return y_;
	}
	
	/** getter method for z
	 * 
	 * @return object's z location
	 */
	public float getZ()
	{
		return z_;
	}
	
	/** getter method for object's width
	 * 
	 * @return object's width
	 */
	public float getWidth()
	{
		return width_;
	}
	
	/** getter method for the object's height
	 * 
	 * @return object's height
	 */
	public float getHeight()
	{
		return height_;
	}
	
	/** getter method for object's x velocity
	 * 
	 * @return object's x velocity
	 */
	public float getvx()
	{
		return vx_;
	}
	
	/** getter method for object's y velocity
	 * 
	 * @return object's y velocity
	 */
	public float getvy()
	{
		return vy_;
	}
	
	/** getter method for object's z velocity
	 * 
	 * @return object's z velocity
	 */
	public float getvz()
	{
		return vz_;
	}
	
	/** setter method for object's x velocity
	 * 
	 * @param nvx new x velocity for the object
	 */
	public void setvx(float nvx)
	{
		vx_ = nvx;
	}
	
	/** setter method for object's y veclocity
	 * 
	 * @param nvy new y velocity for the object
	 */
	public void setvy(float nvy)
	{
		vy_ = nvy;
	}
	
	
	
}
