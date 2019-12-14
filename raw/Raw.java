package raw;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PMatrix;

public class Raw extends PApplet {

	static final int   WINDOW_WIDTH = 1200, WINDOW_HEIGHT = 900;
//	static final int   WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600;
	static final float PLANE_SIZE = sqrt(2)*400;
	static final float XMIN = -PLANE_SIZE/2, XMAX=PLANE_SIZE/2;
	static final float YMIN = -PLANE_SIZE/2, YMAX=PLANE_SIZE/2;
	float nearZ = 870, farZ = 1300;
	
	public void settings() {
		size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D);
	}
	
	public void setup() 
	{
		camSetup();
	}
	
	public void camSetup() {
//		ortho(-WINDOW_WIDTH/2, WINDOW_WIDTH/2, -WINDOW_HEIGHT/2, WINDOW_HEIGHT/2,
//				nearZ, farZ);

		float fov = (PI/3);
		perspective(fov, (float)width/(float)height, nearZ, farZ);

		printMatrix();
		println("60º Z at -" + ((height/2.f)/PApplet.tan(fov/2)));
	}
	
	
	public void draw() 
	{
		background(0);
		noFill();
		strokeWeight(1);
	

//println("-----------");		
//printMatrix();
		translate(width/2, height/2, 0);
//printMatrix();
//PMatrix pm = getMatrix();
//float []m = pm.get(null);
//println("ΔZ = " + m[11]);
		pushMatrix();
		translate(0, 0, -300);

		rotateX(PI/3);
		drawReferenceFrame(g);

//		rotateX(-PI/3);
//		translate(0, 0, -200);
//		rotateX(-PI/4);
		rotateZ(PI/6);
		noStroke();
		fill(127);
		beginShape(QUADS);
		vertex(XMIN, YMAX, 0);
		vertex(XMIN, YMIN, 0);
		vertex(XMAX, YMIN, 0);
		vertex(XMAX, YMAX, 0);

		endShape(CLOSE);   
		popMatrix();
	
	}

	public static void drawReferenceFrame(PGraphics g)
	{
		g.strokeWeight(5);
		g.stroke(255, 0, 0);
		g.line(0, 0, 0, 100, 0, 0);
		g.stroke(0, 255, 0);
		g.line(0, 0, 0, 0, 100, 0);
		g.stroke(0, 0, 255);
		g.line(0, 0, 0, 0, 0, 100);
	}
	
	public void keyReleased() 
	{
		switch (key)
		{
		case 'Q':
			nearZ += 5;
			break;
		case 'q':
			nearZ += 1;
			break;
			
		case 'A':
			nearZ -= 5;
			break;
		case 'a':
			nearZ -= 1;
			break;
			
		case 'W':
			farZ += 5;
			break;
		case 'w':
			farZ += 1;
			break;
			
		case 'S':
			farZ -= 5;
			break;
		case 's':
			farZ -= 1;
			break;
			
		}
		
		println("nearZ: " + nearZ + "   farZ: " + farZ + "\n");
		camSetup();
	}

	public static void main(String _args[]) {
		PApplet.main("raw.Raw");
	}
}
