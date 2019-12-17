package brickbreaker.Data.brickbreaker;
import processing.core.*;

public abstract class GraphicObject {

	// member functions
	protected float x_;
	protected float y_;
	protected float z_;
	protected float vx_;
	protected float vy_;
	protected float vz_;
	protected float width_;
	protected float height_;
	protected int color_;
	protected BoundingBox boundingBox_;
	
	public GraphicObject(float x, float y, float z, float width, float height) 
	{
		x_ = x;
		y_ = y;
		z_ = z;
		width_ = width;
		height_ = height;
	}
	
	public void draw(PApplet app)
	{
		app.pushMatrix();
		app.translate(x_,  y_, z_);
		draw_(app);
		app.popMatrix();
	}
	
	protected abstract void draw_(PApplet app);
	
	public abstract BoundingBox getBoundingBox();
	
	public abstract void update_(float dt);
	
	public float getX()
	{
		return x_;
	}
	public float getY()
	{
		return y_;
	}
	public float getZ()
	{
		return z_;
	}
	public float getWidth()
	{
		return width_;
	}
	public float getHeight()
	{
		return height_;
	}
	public float getvx()
	{
		return vx_;
	}
	public float getvy()
	{
		return vy_;
	}
	public float getvz()
	{
		return vz_;
	}
	public void setvx(float nvx)
	{
		vx_ = nvx;
	}
	public void setvy(float nvy)
	{
		vy_ = nvy;
	}
	
	
	
}
