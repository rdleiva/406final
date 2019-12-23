package brickbreaker.Data.brickbreaker;
import processing.core.*;

/** Brick class for the breaker game
 * 
 *
 */
public class Brick extends GraphicObject{
	
	/** Brick constructor
	 * 
	 * @param x initial x location
	 * @param y initial y location
	 * @param z initial z location
	 * @param width brick width
	 * @param height brick height
	 */
	public Brick(float x, float y, float z, float width, float height)
	{
		super(x,y,z,width,height);
		boundingBox_ = new BoundingBox(x_ - width/2, x_ + width/2,
									   y_ - width/2, y_ + width/2,
									   z_ - width/2, z_+ width/2,
									   0);
		// rate at which bricks descend towards the player
		vx_ = 0.5f;
	}

	
	public void draw_(PApplet app)
	{
		// 0,0,0 is the center of this object in reletive frame of reference
		app.noFill();
		app.box(width_, width_, height_);
		app.translate(-width_/2, -width_/2, -height_/2);
		app.beginShape(app.QUADS);
		
		app.fill(255, 0, 0); 
		app.vertex(0,  0,  0);
		app.vertex(width_,  0,  0);
		app.vertex( width_, 0,  width_);
		app.vertex(0, 0,  width_);

		app.vertex(0,  0,  0);
		app.vertex(width_,  0,  0);
		app.vertex( width_, width_,  0);
		app.vertex(0, width_,  0);
		
		app.vertex(0,  0,  0);
		app.vertex(0,  0,  width_);
		app.vertex( 0, width_,  width_);
		app.vertex(0, width_,  0);
		
		app.vertex(width_,  0,  0);
		app.vertex(width_,  0,  width_);
		app.vertex(width_,  width_,  width_);
		app.vertex(width_,  width_,  0);

		app.vertex(width_,  width_,  width_);
		app.vertex(0,  width_,  width_);
		app.vertex(0,  width_,  0);
		app.vertex(width_,  width_,  0);
		
		app.vertex(width_,  width_,  width_);
		app.vertex(width_,  0,  width_);
		app.vertex(0,  0,  width_);
		app.vertex(0,  width_,  width_);
		
		app.endShape();
	}
	
	public BoundingBox getBoundingBox()
	{
		return boundingBox_;
	}

	public void update_(float dt) {
		
		x_ += vx_ * dt;
		
		boundingBox_ = new BoundingBox(x_ - width_/2, x_ + width_/2,
									   y_ - width_/2, y_ + width_/2,
									   z_ - width_/2, z_+ width_/2,
									   0);
		
	}
}
