package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;

/** Bounding box class to implement 3D collision
 * 
 *
 */
public class BoundingBox {
	/**
	 * private boundary variables for the bounding box cube
	 */
	private float xmin_, xmax_, ymin_, ymax_, zmin_, zmax_;
	/**
	 * bounding box color
	 */
	private int color_;
	
	/** BoundingBpx constructor
	 * 
	 * @param xmin x boundary
	 * @param xmax x boundary
	 * @param ymin y boundary
	 * @param ymax y boundary
	 * @param zmin z boundary
	 * @param zmax z boundary
	 * @param color cube color
	 */
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
	
	/** constructor which only takes a color
	 * 
	 * @param color
	 */
	public BoundingBox(int color)
	{
		color_ = color;
	}
	
	/** constructor which creates an empty bounding box
	 * 
	 */
	public BoundingBox()
	{
		xmin_ = 0.f;
		xmax_ = 0.f;
		ymin_ = 0.f;
		ymax_ = 0.f;
		zmin_ = 0.f;
		zmax_ = 0.f;
	}
	
	/** x min getter
	 * 
	 * @return x min
	 */
	public float getXmin()
	{
		return xmin_;
	}

	/** x max getter
	 * 
	 * @return x max
	 */
	public float getXmax()
	{
		return xmax_;
	}
	
	/** y min getter
	 * 
	 * @return y min
	 */
	public float getYmin()
	{
		return ymin_;
	}

	/** y max getter
	 * 
	 * @return y max
	 */
	public float getYmax()
	{
		return ymax_;
	}
	
	/** z min getter
	 * 
	 * @return z min
	 */
	public float getZmin()
	{
		return zmin_;
	}

	/** z max getter
	 * 
	 * @return z max
	 */
	public float getZmax()
	{
		return zmax_;
	}
	
	/** 3D collision detection
	 * 
	 * @param a a bounding box to check for collision with another graphic object
	 * @return is collision occurring
	 */
	public boolean isInside(BoundingBox a)
	{
		return (a.xmin_ <= xmax_ && a.xmax_ >= xmin_) &&
        (a.ymin_ <= ymax_ && a.ymax_ >= ymin_) &&
        (a.zmin_ <= zmax_ && a.zmax_ >= zmin_);
	}
	
	
}
