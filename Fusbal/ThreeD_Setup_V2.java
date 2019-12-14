package Fusbal;

import processing.core.PApplet;
import processing.core.PImage;

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

public class ThreeD_Setup_V2 extends PApplet 
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
	 */
	private final float PLANE_WIDTH = 30.f, PLANE_HEIGHT = 50.f;
	
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
	
	//		       Y     X       Z
	private float pan_ = 1.05f, tilt_, roll_ = -1.5999995f;
	
	private boolean transl_ = false;
	
	public void settings() 
	{
		//Initial Scene configuration
		size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
	}

	public void setup() 
	{
//		cameraMode_ = CameraMode.ORTHOGRAPHIC_CAM;
		cameraMode_ = CameraMode.NORMAL_PERSPECTIVE_CAM;
//		cameraMode_ = CameraMode.WIDE_PERSPECTIVE_CAM;
		animate_ = false;
		ballTheta_ = 0;
		
		//Image loading section
		planeTexture_ = loadImage("field.jpg");
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

if (transl_)
	translate(0, -20, 0);

		//	Draw a rotated cube on the Z axis in front of the camera
		pushMatrix();
		translate(0, 0, 0);
		rotateX(-PI/6);
		rotateY(PI/3);
		stroke(255, 0, 0);
		noFill();
		box(5f);
		popMatrix();

		drawSurfaceAndBall_();
		
		update_();
	}

	void drawSurfaceAndBall_(){

		pushMatrix();
		noStroke();
		fill(127);
		translate(0, 0, 0);
		rotateX(-PI/6);
		rotateY(PI/3);

		beginShape(QUADS);
			texture(planeTexture_);
			vertex(-PLANE_WIDTH/2, -PLANE_HEIGHT/2, 0, 0, 0);
			vertex(PLANE_WIDTH/2, -PLANE_HEIGHT/2, 0, 0, 1);
			vertex(PLANE_WIDTH/2, PLANE_HEIGHT/2, 0, 1, 1);
			vertex(-PLANE_WIDTH/2, PLANE_HEIGHT/2, 0, 1, 0);
		endShape(CLOSE);   

		//	Move to the center of the ball
		translate(BALL_ORBIT_RADIUS*cos(ballTheta_), BALL_ORBIT_RADIUS*sin(ballTheta_), BALL_RADIUS*0.7f);
		rotateZ(ballTheta_);
		noFill();
		//stroke(255, 255, 0);
		//fill(255, 255, 0);
		//sphereDetail(32);
		//sphere(BALL_RADIUS);
		drawSphere_();
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
		if (animate_)
		{
			int time = millis();
			float dt = (time - lastTime_)*0.001f;
			ballTheta_ += BALL_SPIN*dt;
			//	Keep ballTheta_ in range [0, 2π[
			if (ballTheta_ > 2*PI)
				ballTheta_ -= 2*PI;
			if (ballTheta_ < 0)
				ballTheta_ += 2*PI;				
			lastTime_ = time;
		}
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
				System.out.println(pan_ + " " + roll_);
				break;

		}
	}
	
	public static void main(String _args[]) {
		PApplet.main("Fusbal.ThreeD_Setup_V2");
	}

}
