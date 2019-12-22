package brickbreaker.Data.brickbreaker;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.Capture;
import skybox.Skybox;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;
import processing.core.PGraphics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

//Team: 			Eben Aceto
//					Fehmina Hasan
//					Rotman Daniel Leiva
//					John Motta
//		
//Professor:		Hervé
//
//CSC406:			Computer Graphics
//
//Final Project: 	BrickBreaked3D
	

public class Brickbreaker extends PApplet 
{

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
	
	/**	Dimensions of camera plane
	 */		
	private final float CAM_WIDTH = 50.f, CAM_HEIGHT = 50.f;
	
//	private float XMIN = -PLANE_WIDTH/2, YMIN = -PLANE_HEIGHT/2, XMAX = PLANE_WIDTH/2, YMAX = PLANE_HEIGHT/2;
	
	/**	What camera are we using?
	 * 
	 */
	private CameraMode cameraMode_;

	/**	As the name indicates, texture to map on a plane.
	 * 
	 */
	private PImage planeTexture_;
	
	/** Toggle for ball animation
	 * 
	 */
	private boolean animate_;
	
	/**	array to hold list of bricks
	 * 
	 */
	private ArrayList<GraphicObject> brickList_;
	
	/**	ball graphic object
	 * 
	 */
	private GraphicObject ball_;
	
	/**	bumper object to bounce ball
	 * 
	 */
	private Bumper bump_;
	
	/**	
	 * 
	 */
	private int lastTime_;
	

	/**	fixed start view y and z
	 * 
	 */
	private float pan_ = -1.55f, tilt_, roll_ = 0.50f;
	
	/**	
	 * 
	 */
	private boolean transl_ = false;
	
	/**	webcam variable
	 * 
	 */
	Capture cam;
	
	/**	skyboxs boolean
	 * 
	 */
	private final boolean skyboxyes_ = true;

	/** Skybox variable
	 * 
	 */
	private Skybox skybox = null;
	
	/** Game over Image variable
	 * 
	 */
	private PImage gameOver_;
	
	/**	game over boolean
	 * 
	 */
	private boolean gameOver = false;
	
	/**	number of rows and blocks per row variables
	 * 
	 */
	int numRows = 50, blocksPerRow = 10;
	
	/**	size of block
	 * 
	 */
	int blockSize = (int) PLANE_HEIGHT/blocksPerRow;
	
	/**	starting location in each row
	 * 
	 */
	float rowStart = (float) blockSize/2;
	
	/**	application settings
	 * 
	 */
	public void settings() 
	{
		//Initial Scene configuration
		size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
	}

	/**	application setup
	 * 
	 */
	public void setup() 
	{
		// web cam list
		String[] cameras = Capture.list();
		
		music();
		    
		// The camera can be initialized directly using an 
		// element from the array returned by list():
		cam = new Capture(this, cameras[0]);  
		cam.start();
		
		// cameraMode_ = CameraMode.ORTHOGRAPHIC_CAM;
		cameraMode_ = CameraMode.NORMAL_PERSPECTIVE_CAM;
		// cameraMode_ = CameraMode.WIDE_PERSPECTIVE_CAM;
		animate_ = false;
	
		//Image loading section
		skybox = new Skybox(loadImage("space.jpg"));
		planeTexture_ = loadImage("space.jpg");
		gameOver_ = loadImage("gameover.jpg");
		textureMode(NORMAL);

		brickList_ = new ArrayList<GraphicObject>(numRows*blocksPerRow);

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
		bump_ = new Bumper(PLANE_WIDTH/3,0f,blockSize/2, blockSize/2, blockSize*2f);

		setupCamera_();
		lastTime_ = millis();
	}

	/**	camera setup
	 * 
	 */
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
	
	/**	drawing
	 * 
	 */
	public void draw() 
	{		
		background(0);
		
		if (skybox != null)
			hint(DISABLE_DEPTH_TEST);
		
		//	Move to the center of the image plane
		translate(WINDOW_WIDTH/2, WINDOW_HEIGHT/2, 0);
		
		rotateY(pan_);
		rotateZ(roll_);
		rotateX(tilt_);

		//	I like my Z axis to indicate the vertical "up" direction
		rotateX(PI/2);

		//	from now on we will be drawing in world units, but the lines
		//	we draw should remain 1 pixel wide on screen
		strokeWeight(DRAW_IN_PIXEL_SCALE3);

		scale(DRAW_IN_WORLD_UNIT_SCALE3, DRAW_IN_WORLD_UNIT_SCALE3, DRAW_IN_WORLD_UNIT_SCALE3);

		// put the camera where we want it
		translate(-50, 0, -20);
		
		if (skybox != null)
		{
			skybox.drawSkybox(this);
			hint(ENABLE_DEPTH_TEST);
		}

		drawSurvaceAndCam_();
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
		
		
		// game over
		if(gameOver) 
		{
			pushMatrix();
			translate(PLANE_WIDTH/3, 0, PLANE_HEIGHT/2);
			rotateY(PI);
			rotateZ(PI);
			rotateY(-PI/4);

			beginShape(QUADS);
			texture(gameOver_);
			vertex(-CAM_WIDTH, -CAM_HEIGHT, 0, 0, 0);
			vertex(CAM_WIDTH, -CAM_HEIGHT, 0, 0, 1);
			vertex(CAM_WIDTH, CAM_HEIGHT, 0, 1, 1);
			vertex(-CAM_WIDTH, CAM_HEIGHT, 0, 1, 0);
			endShape(CLOSE); 
			popMatrix();
		}
		
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

	/**	draw surface and ball
	 * 
	 */
	void drawSurvaceAndCam_(){
		
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
		
		popMatrix();
	}
	
	/**	update
	 * 
	 */
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
			
			if(brickList_.get(i).getX() >= bump_.getX()) 
			{
				gameOver = true;
//				System.exit(0);
			}
			// brick collides with ball, if statement happens
			// make list of bricks to remove
			if(brickList_.get(i).getBoundingBox().isInside(ball_.getBoundingBox()))
			{
				blockHit();
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
		
		if(ball_.getX() >= bump_.getX()+10) gameOver = true; 
	}
	
	/**	keyboard 
	 * 
	 */
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
			case ',':	// left move
				if(bump_.getY() <= PLANE_HEIGHT/3)
					bump_.moveLeft();
				break;
			case '.':	// right move
				if(bump_.getY() >= -PLANE_HEIGHT/3)
					bump_.moveRight();
				break;
		}
	}
	
	/**	music
	 * 
	 */
	public void music() 
    {       
		
		//Initializes all sound players/streamers/and data managers
        AudioPlayer AP = AudioPlayer.player;
        AudioStream AS;
        AudioData AD;
        // loop for continuous audio
        ContinuousAudioDataStream loop = null;

        try
        {
        	//tries to play selected music
            InputStream test = new FileInputStream("menu.wav");
            AS = new AudioStream(test);
            AudioPlayer.player.start(AS);
            AD = AS.getData();
            loop = new ContinuousAudioDataStream(AD);

        }
        
        catch(FileNotFoundException e)
        {
        	//if music file isnt found
            System.out.print(e.toString());
        }
        
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        //activates loop
        AP.start(loop);
    }
	
	/**	when block gets hit
	 * 
	 */
	public static void blockHit() 
    {       
    	//Initializes all sound players/streamers/and data managers
        AudioPlayer AP = AudioPlayer.player;
        AudioStream AS;
        AudioData AD;
        // loop for continuous audio
        //ContinuousAudioDataStream loop = null;

        try
        {
        	//tries to play selected music
            InputStream test = new FileInputStream("blockHit.wav");
            AS = new AudioStream(test);
            AudioPlayer.player.start(AS);
            AD = AS.getData();
            //loop = new ContinuousAudioDataStream(AD);

        }
        
        catch(FileNotFoundException e)
        {
        	//if music file isnt found
            System.out.print(e.toString());
        }
        
        catch(IOException error)
        {
            System.out.print(error.toString());
        }
        //activates loop
        //AP.start(loop);
    }
	
	/**	main app
	 * 
	 */
	public static void main(String _args[]) {
		PApplet.main("brickbreaker.Data.brickbreaker.Brickbreaker");
	}

}
