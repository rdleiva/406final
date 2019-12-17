package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;

// simple bumper class just to have somthing working for the presentation today :D

public class Bumper extends GraphicObject{
	// how much does the bumper move left and right from a single button press
	private float moveDist_ = 5.0f;
	
	
	public Bumper(float x, float y, float z, float width, float height)
	{
		super(x,y,z,width,height);
		boundingBox_ = new BoundingBox(x_ - width/2, x_ + width/2,
									   y_ - height/2, y_ + height/2,
									   z_ - width/2, z_+ width/2,
									   0);
	}

	protected void draw_(PApplet app) {
		// for now we'll just use a rectangle for the bumper
		app.noFill();
		app.box(width_, height_, width_);
	}

	public BoundingBox getBoundingBox() {
		return boundingBox_;
	}

	public void update_(float dt) {
		updateBoundingBoxes_();
	}
	
	// move methods should shift the location of the bumper along the y axis
	public void moveRight()
	{
		y_ -= moveDist_;
	}
	public void moveLeft()
	{
		y_ += moveDist_;
	}
	protected void updateBoundingBoxes_() {
		boundingBox_ = new BoundingBox(x_ - width_/2, x_ + width_/2,
									   y_ - height_/2, y_ + height_/2,
									   z_ - width_/2, z_+ width_/2,
									   0);
	}
}
