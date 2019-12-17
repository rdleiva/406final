package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;

public class BoundingBox {
	private float xmin_, xmax_, ymin_, ymax_, zmin_, zmax_;
	private int color_;
	
	public BoundingBox(float xmin, float xmax, float ymin, float ymax, float zmin, float zmax, int color)
	{
		xmin_ = xmin;
		xmax_ = xmax;
		ymin_ = ymin;
		ymax_ = ymax;
		zmin_ = zmin;
		zmax_ = zmax;
		color_ = color;
	}
	
	public BoundingBox(int color)
	{
		color_ = color;
	}
	
	public BoundingBox()
	{
		xmin_ = 0.f;
		xmax_ = 0.f;
		ymin_ = 0.f;
		ymax_ = 0.f;
		zmin_ = 0.f;
		zmax_ = 0.f;
	}
	
	public float getXmin()
	{
		return xmin_;
	}

	public float getXmax()
	{
		return xmax_;
	}
	
	public float getYmin()
	{
		return ymin_;
	}

	public float getYmax()
	{
		return ymax_;
	}
	
	public float getZmin()
	{
		return zmin_;
	}

	public float getZmax()
	{
		return zmax_;
	}
	
	public boolean isInside(BoundingBox a)
	{
		return (a.xmin_ <= xmax_ && a.xmax_ >= xmin_) &&
        (a.ymin_ <= ymax_ && a.ymax_ >= ymin_);
	}
	
	
}
