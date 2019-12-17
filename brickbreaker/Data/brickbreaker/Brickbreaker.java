package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Capture;

import java.util.*;

//	In 3D, a lot of the things we had to care about in 2D don't need to be
//	defined explicitly anymore, but rather are going to be defined implicitly
//	once you have defined your camera.
//		In particular, the notions of conversion between pixel and world units,
//	and even of "dimensions of the world," are implicitly defined by the choice
//	of camera.  The XMIN, XMAX, YMIN, YMAX that we had to define in our 2D applications
//	are now only relevant if you want object to bounce, or more generally have 
//	some specific behavior when they reach the "edge of the world."  
//		In this version, there are no constant that need to be available application-wide,
//	and so I got rid of the ApplicationConstants interface and defined in this class
//	all the constants that I needed.

public class Brickbreaker extends PApplet 
{
	
	private ArrayList<GraphicObject> brickList_;
	
	private GraphicObject ball_;
	
	private Bumper bump_;
	
	private enum CameraMode {
		ORTHOGRAPHIC_CAM,
		NORMAL_PERSPECTIVE_CAM,
		WIDE_PERSPECTIVE_CAM
	}
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	
	//	This is all that remains so far from our 2D ApplicationConstants interface
	public static final int WINDOW_WIDTH = 1200,
							WINDOW_HEIGHT = 900;
	

	//========================
	//	Camera parameters
	//========================
	//	I am going to implement three different cameras:
	//		o A camera with a vertical field of view of 60 degree (π/3)
	//		o A wide-angle camera with a vertical field of view of 120 degree (2π/3)
	//		o A camera implementing orthographic projection.
	//	This way to define cameras sticks to the way they are defined and used by
	//	processing.  We will see a more natural way to define cameras as a couple
	//	sensor + lens, which will let us implement in Processing regular DSLR or
	//	mirrorless (including point & shoot or cell phone) cameras.

	//-------------------------------------
	//	"Normal" camera (60 degree fov)
	//-------------------------------------
	private final float FOV_Y_1 = PI/3;
	private final float FOCAL_1 = (WINDOW_HEIGHT*0.5f)/tan(FOV_Y_1 / 2);

	//-------------------------------------
	//	Wide-angle camera (120 degree fov)
	//-------------------------------------
	private final float FOV_Y_2 = 2*PI/3;
	private final float FOCAL_2 = (WINDOW_HEIGHT*0.5f)/tan(FOV_Y_2 / 2);
	
	//-----------------------------------------------------------------------
	//	Orthographic camera
	//	Such a camera is defined by clipping values in the x and y directions
	//	Note that I compute the x range to preserve the window's aspect ratio
	//-----------------------------------------------------------------------
	static final float 	CAM_YMAX3 = +30,
				  		//
						CAM_YMIN3 = -CAM_YMAX3,
						CAM_XMIN3 = CAM_YMIN3*WINDOW_WIDTH/WINDOW_HEIGHT,
						CAM_XMAX3 = CAM_YMAX3*WINDOW_WIDTH/WINDOW_HEIGHT;
	static final float WORLD_TO_PIXEL3 = WINDOW_WIDTH/(CAM_XMAX3-CAM_XMIN3);
	static final float PIXEL_TO_WORLD3 = 1.f/WORLD_TO_PIXEL3;
	static final float DRAW_IN_WORLD_UNIT_SCALE3 = WORLD_TO_PIXEL3;
	static final float DRAW_IN_PIXEL_SCALE3 = PIXEL_TO_WORLD3;

	//============================
	//	Positioning of the camera
	//============================
	//	There are essentially 2 ways to proceed: Either you position the camera
	//	relative to the world, or the world relative to the camera.  By default,
	//	Processing uses the latter approach, which I think is the wrong way, 
	//	because it makes it *much* harder to move the camera if you want to.
	//	In this version, I am going to do everything the Processing "native" way,
	//	just I did with the fovy definitions.  In Version 2, we will do it
	//	in a "better way," or at least one that is more extensible.
	
	/**	Dimensions of textured plane
	 */								// 150
	private final float PLANE_WIDTH = 150.f, PLANE_HEIGHT = 50.f;
	
	private final float CAM_WIDTH = 50.f, CAM_HEIGHT = 50.f;
	
	private float XMIN = -PLANE_WIDTH/2, YMIN = -PLANE_HEIGHT/2, XMAX = PLANE_WIDTH/2, YMAX = PLANE_HEIGHT/2;
	
	/**	What camera are we using?
	 * 
	 */
	private CameraMode cameraMode_;

	/**	As the name indicates, texture to map on a plane.
	 * 
	 */
	private PImage planeTexture_;
	
	/**	Texture to map on the sphere
	 * 
	 */
	private PImage ballTexture_;
	
	/** Toggle for ball animation
	 * 
	 */
	private boolean animate_;
	
	private float ballTheta_;
	
	private int lastTime_;
	
	private float [][][]sphereVertex_;
	
	final int SPHERE_RES = 16;
	final float BALL_RADIUS = 3;
	final float BALL_ORBIT_RADIUS = 15;
	final float BALL_SPIN = PI/4;
	
	//		         Y            X            Z
	private float pan_ = -1.55f, tilt_, roll_ = 0.50f;
	
	private boolean transl_ = false;
	
	//webcam 
	Capture cam;
	
	public void settings() 
	{
		//Initial Scene configuration
		size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
	}

	public void setup() 
	{
		// web cam list
		String[] cameras = Capture.list();
		
		if (cameras.length == 0) {
			println("There are no cameras available for capture.");
			exit();
		} 
		
		println("Available cameras:");
		for (int i = 0; i < cameras.length; i++) {
			println("\t" + cameras[i]);
		}
		    
		// The camera can be initialized directly using an 
		// element from the array returned by list():
		cam = new Capture(this, cameras[0]);  
		cam.start();
		
//		cameraMode_ = CameraMode.ORTHOGRAPHIC_CAM;
		cameraMode_ = CameraMode.NORMAL_PERSPECTIVE_CAM;
//		cameraMode_ = CameraMode.WIDE_PERSPECTIVE_CAM;
		animate_ = false;
		ballTheta_ = 0;
		
		//Image loading section
		planeTexture_ = loadImage("space.jpg");
		ballTexture_ = loadImage("ballpattern3.jpg");
		textureMode(NORMAL);
		float azimuthStep = 2*PI/SPHERE_RES;
		float elevationStep = PI/(SPHERE_RES+1);
		//	I don't bother storing coordinates for the North and South poles.
		//	(0, 0, rad, 0.5, 1) and (0, 0, -rad, 0.5, 0).
		sphereVertex_ = new float[SPHERE_RES][SPHERE_RES][];

		for (int i=0; i<SPHERE_RES; i++)
		{
			float elevationAngle = PI/2 - (i+1)*elevationStep;
			float cosElev = cos(elevationAngle), sinElev = sin(elevationAngle);
			for (int j=0; j<SPHERE_RES; j++)
			{
				float azimuthAngle = j*azimuthStep;
				float cosAzim = cos(azimuthAngle), sinAzim = sin(azimuthAngle);
				float []XYZuv = {BALL_RADIUS*cosAzim*cosElev, BALL_RADIUS*sinAzim*cosElev, BALL_RADIUS*sinElev,
						1.f*j/SPHERE_RES, 1-(i+1.f)/(SPHERE_RES+1.f)};				
				sphereVertex_[i][j] = XYZuv;
			}			
		}

		// determine the number of bricks per row
		int numRows = 50;
		int blocksPerRow = 10;
		brickList_ = new ArrayList<GraphicObject>(numRows*blocksPerRow);
		int blockSize = (int) PLANE_HEIGHT/blocksPerRow;
		float rowStart = (float) blockSize/2; // starting location in each row
		// for each row
		for(int i = 0; i < numRows; i++) 
		{
			float xLoc = -(i*blockSize);
			for(int j = 0; j < blocksPerRow; j++)
			{
				float yLoc = (j*blockSize) - 25;
				brickList_.add(new Brick(xLoc+rowStart, yLoc+rowStart, (float)blockSize/2, blockSize, blockSize));
			}
		}

		ball_ = new Ball(0,0,blockSize/2, blockSize/2, blockSize/2);
		bump_ = new Bumper(PLANE_WIDTH/3,0f,blockSize/2, blockSize/2, blockSize);
		setupCamera_();
		lastTime_ = millis();
	}

	private void setupCamera_()
	{
		switch (cameraMode_)
		{
			case ORTHOGRAPHIC_CAM:
				ortho(-WINDOW_WIDTH/2, WINDOW_WIDTH/2, -WINDOW_HEIGHT/2, WINDOW_HEIGHT/2); // Same as ortho()
				break;
				
			case NORMAL_PERSPECTIVE_CAM:			
				perspective(FOV_Y_1, (float)width/(float)height, FOCAL_1/10, 50*FOCAL_1);
				break;
				
			case WIDE_PERSPECTIVE_CAM:
				perspective(FOV_Y_2, (float)width/(float)height, FOCAL_2 / 2, 50*FOCAL_2);
				break;
		}
	}
	
	public void draw() {

		background(0);
		
		//	Move to the center of the image plane
		translate(WINDOW_WIDTH/2, WINDOW_HEIGHT/2, 0);
		
		rotateY(pan_);
		rotateZ(roll_);

		//	I like my Z axis to indicate the vertical "up" direction
		rotateX(PI/2);
		
//		rotateX(tilt_);


		//	from now on we will be drawing in world units, but the lines
		//	we draw should remain 1 pixel wide on screen
		strokeWeight(DRAW_IN_PIXEL_SCALE3);

		scale(DRAW_IN_WORLD_UNIT_SCALE3, DRAW_IN_WORLD_UNIT_SCALE3, DRAW_IN_WORLD_UNIT_SCALE3);

		// put the camera where we want it
		translate(-50, 0, -20);


		drawSurfaceAndBall_();
		pushMatrix();
		translate(-PLANE_WIDTH, 0, 32);
		rotateY(PI/4);
		//rotateX(-PI/6);
		//rotateY(PI/3);

		beginShape(QUADS);
			texture(cam);
			vertex(-CAM_WIDTH/2, -CAM_HEIGHT/2, 0, 0, 0);
			vertex(CAM_WIDTH/2, -CAM_HEIGHT/2, 0, 0, 1);
			vertex(CAM_WIDTH/2, CAM_HEIGHT/2, 0, 1, 1);
			vertex(-CAM_WIDTH/2, CAM_HEIGHT/2, 0, 1, 0);
		endShape(CLOSE); 
		popMatrix();
		
		// show axes
		if(transl_)
		{
			pushMatrix();
			stroke(255,0,0);
			line(0,0,0  ,30,0,0);
			stroke(0,255,0);
			line(0,0,0  ,0,30,0);
			stroke(0,0,255);
			line(0,0,0  ,0,0,30);
			popMatrix();
		}

		stroke(255,255,255);
		strokeWeight(0.2f);

		// draw bricks, ball, and bumper
		for(int i = 0; i < brickList_.size(); i++)
		{
			brickList_.get(i).draw(this);
		}

		ball_.draw(this);
		bump_.draw(this);

		// update objects
		update_();
	}

	void drawSurfaceAndBall_(){
		
		if (cam.available()) {
			cam.read();
		}


		pushMatrix();
		noStroke();
		fill(127);
		translate(0, 0, 0);
		//rotateX(-PI/6);
		//rotateY(PI/3);

		beginShape(QUADS);
			texture(planeTexture_);
			vertex(-PLANE_WIDTH/2, -PLANE_HEIGHT/2, 0, 0, 0);
			vertex(PLANE_WIDTH/2, -PLANE_HEIGHT/2, 0, 0, 1);
			vertex(PLANE_WIDTH/2, PLANE_HEIGHT/2, 0, 1, 1);
			vertex(-PLANE_WIDTH/2, PLANE_HEIGHT/2, 0, 1, 0);
		endShape(CLOSE);   
		

		/*
		//	Move to the center of the ball
		translate(BALL_ORBIT_RADIUS*cos(ballTheta_), BALL_ORBIT_RADIUS*sin(ballTheta_), BALL_RADIUS*0.7f);
		rotateZ(ballTheta_);
		noFill();
		//stroke(255, 255, 0);
		//fill(255, 255, 0);
		//sphereDetail(32);
		//sphere(BALL_RADIUS);
		drawSphere_();
		*/
		popMatrix();
	}

	private void drawSphere_()
	{
		//	draw the central part
		for (int i=0; i<SPHERE_RES-1; i++)
		{
			beginShape(TRIANGLE_STRIP);
			texture(ballTexture_);
			for (int j=0; j<SPHERE_RES; j++)
			{
				vertex(sphereVertex_[i][j][0], sphereVertex_[i][j][1], sphereVertex_[i][j][2], 
						sphereVertex_[i][j][3], sphereVertex_[i][j][4]);
				vertex(sphereVertex_[i+1][j][0], sphereVertex_[i+1][j][1], sphereVertex_[i+1][j][2],
						sphereVertex_[i+1][j][3], sphereVertex_[i+1][j][4]);
			}
			//	close the strip (endShape(CLOSE) only works with a polygon
			//	or curve shape, not with strips of quads/triangles
			vertex(sphereVertex_[i][0][0], sphereVertex_[i][0][1], sphereVertex_[i][0][2],
					sphereVertex_[i][0][3], sphereVertex_[i][0][4]);
			vertex(sphereVertex_[i+1][0][0], sphereVertex_[i+1][0][1], sphereVertex_[i+1][0][2],
					sphereVertex_[i+1][0][3], sphereVertex_[i+1][0][4]);
			endShape();   			
		}

		//	draw the North cap
		beginShape(TRIANGLE_FAN);
		texture(ballTexture_);
		vertex(0, 0, BALL_RADIUS, 0.5f, 1f);
		for (int j=0; j<SPHERE_RES; j++)
			vertex(sphereVertex_[0][j][0], sphereVertex_[0][j][1], sphereVertex_[0][j][2], 
					sphereVertex_[0][j][3], sphereVertex_[0][j][4]);
		vertex(sphereVertex_[0][0][0], sphereVertex_[0][0][1], sphereVertex_[0][0][2], 
				sphereVertex_[0][0][3], sphereVertex_[0][0][4]);			
		endShape();   					

		//	draw the South cap
		beginShape(TRIANGLE_FAN);
		texture(ballTexture_);
		vertex(0, 0, -BALL_RADIUS, 0.5f, 0f);
		for (int j=SPHERE_RES-1; j>=0; j--)
			vertex(sphereVertex_[SPHERE_RES-1][j][0], sphereVertex_[SPHERE_RES-1][j][1], sphereVertex_[SPHERE_RES-1][j][2], 
					sphereVertex_[SPHERE_RES-1][j][3], sphereVertex_[SPHERE_RES-1][j][4]);
		vertex(sphereVertex_[SPHERE_RES-1][SPHERE_RES-1][0], sphereVertex_[SPHERE_RES-1][SPHERE_RES-1][1], sphereVertex_[SPHERE_RES-1][SPHERE_RES-1][2], 
				sphereVertex_[SPHERE_RES-1][SPHERE_RES-1][3], sphereVertex_[SPHERE_RES-1][SPHERE_RES-1][4]);			
		endShape();   					
	}
	
	private void update_() 
	{
		int time = millis();
		float dt = (time - lastTime_)*0.001f;
		// list of bricks which should be removed
		ArrayList<GraphicObject> toRemove_ = new ArrayList<GraphicObject>();
		// update ball location
		ball_.update_(dt);
		bump_.update_(dt);
		// check if ball is colliding with any bricks
		for(int i = 0; i < brickList_.size();i++){
			brickList_.get(i).update_(dt);
			// make list of bricks to remove
			if(brickList_.get(i).getBoundingBox().isInside(ball_.getBoundingBox()))
			{
				toRemove_.add(brickList_.get(i));
				// whichever axis difference is larger, is the one which caused the collision
				// if ball collides with two bricks at once, it continues without stopping
				// (fix for a later time)
				float yDiff = Math.abs(brickList_.get(i).getY() - ball_.getY());
				float xDiff = Math.abs(brickList_.get(i).getX() - ball_.getX());
				if(yDiff < xDiff)
				{
					ball_.setvx(-1.0f * ball_.getvx());
				}
				else
				{
					ball_.setvy(-1.0f * ball_.getvy());
				}
			}
		}
		
		// see if bumper is touching the ball
		if(ball_.getBoundingBox().isInside(bump_.getBoundingBox()))
		{
			ball_.setvx((float)-1.0*ball_.getvx());
		}
		
		// get rid of bricks which have been hit
		for(int j = 0; j < toRemove_.size(); j++)
		{
			brickList_.remove(toRemove_.get(j));
		}
		
		lastTime_ = time;
		
	}
	
	public void keyReleased()
	{
		switch(key)
		{
			case 27:
				System.exit(0);
				break;
				
			case ' ':
				animate_ = !animate_;
				lastTime_ = millis();
				break;
			
			case '0':
				cameraMode_ = CameraMode.ORTHOGRAPHIC_CAM;
				setupCamera_();
				break;
				
			case '1':
				cameraMode_ = CameraMode.NORMAL_PERSPECTIVE_CAM;
				setupCamera_();
				break;
				
			case '2':
				cameraMode_ = CameraMode.WIDE_PERSPECTIVE_CAM;
				setupCamera_();
				break;
				
			case 't':
				transl_  = !transl_;
				break;
				
			case 'q':
				tilt_ += 0.05f;
				break;
			case 'a':
				tilt_ -= 0.05f;
				break;
			case 'w':
				pan_ += 0.05f;
				break;
			case 's':
				pan_ -= 0.05f;
				break;
			case 'e':
				roll_ += 0.05f;
				break;
			case 'd':
				roll_ -= 0.05f;
				break;
			case ',':
				bump_.moveLeft();
				break;
			case '.':
				bump_.moveRight();
				break;

		}
	}
	
	public static void main(String _args[]) {
		PApplet.main("brickbreaker.Data.brickbreaker.Brickbreaker");
	}

}
