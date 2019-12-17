package skybox;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;

public class Skybox 
{
	PImage m_img = null;
	private ArrayList<VInfo> m_info = new ArrayList<VInfo>();
	
	public Skybox(PImage img)
	{		
		m_img = img;
		make_geometry();
	}
	
	public void make_geometry()
	{
		final float t1 = 1.0f/3.0f;
		final float t2 = 2.0f/3.0f;
		
		// Front Face
		m_info.add(new VInfo(-1.000000, 1.000000, 1.000000, 0.25, t2));
		m_info.add(new VInfo(-1.000000, -1.000000, 1.000000, 0.25, t1));
		m_info.add(new VInfo(1.000000, -1.000000, 1.000000, 0.0, t1));
		m_info.add(new VInfo(1.000000, 1.000000, 1.000000, 0.0, t2));

		// Left Face
		m_info.add(new VInfo(-1.000000, 1.000000, 1.000000, 0.25, t2));
		m_info.add(new VInfo(-1.000000, -1.000000, 1.000000, 0.25, t1));
		m_info.add(new VInfo(-1.000000, -1.000000, -1.000000, 0.5, t1));
		m_info.add(new VInfo(-1.000000, 1.000000, -1.000000, 0.5, t2));

		// Right Face
		m_info.add(new VInfo(1.000000, 1.000000, 1.000000, 1.0, t2));
		m_info.add(new VInfo(1.000000, -1.000000, 1.000000, 1.0, t1));
		m_info.add(new VInfo(1.000000, -1.000000, -1.000000, 0.75, t1));
		m_info.add(new VInfo(1.000000, 1.000000, -1.000000, 0.75, t2));

		// Back Face
		m_info.add(new VInfo(-1.000000, 1.000000, -1.000000, 0.5, t2));
		m_info.add(new VInfo(-1.000000, -1.000000, -1.000000, 0.5, t1));
		m_info.add(new VInfo(1.000000, -1.000000, -1.000000, 0.75, t1));
		m_info.add(new VInfo(1.000000, 1.000000, -1.000000, 0.75, t2));

		// Top Face
		m_info.add(new VInfo(-1.000000, 1.000000, -1.000000, 0.5, t2));
		m_info.add(new VInfo(-1.000000, 1.000000, 1.000000, 0.25, t2));
		m_info.add(new VInfo(1.000000, 1.000000, 1.000000, 0.25, 1.0));
		m_info.add(new VInfo(1.000000, 1.000000, -1.000000, 0.5, 1.0));

		// Bottom Face
		m_info.add(new VInfo(-1.000000, -1.000000, -1.000000, 0.5, t1));
		m_info.add(new VInfo(-1.000000, -1.000000, 1.000000, 0.25, t1));
		m_info.add(new VInfo(1.000000, -1.000000, 1.000000, 0.25, 0.0));
		m_info.add(new VInfo(1.000000, -1.000000, -1.000000, 0.5, 0.0));
	}
	
	private void triangle(PApplet app, int number_0, int number_1, int number_2)
	{
		VInfo info;
		info = m_info.get(number_0 - 1);
		app.vertex(info.vx, info.vy, info.vz, info.uvx, info.uvy);
		info = m_info.get(number_1 - 1);
		app.vertex(info.vx, info.vy, info.vz, info.uvx, info.uvy);
		info = m_info.get(number_2 - 1);
		app.vertex(info.vx, info.vy, info.vz, info.uvx, info.uvy);
	}
	public void drawSkybox(PApplet app)
	{
		app.pushMatrix();
		app.scale(100.0f);
		app.beginShape(PApplet.TRIANGLES);
		app.textureMode(PApplet.NORMAL);
		app.texture(m_img);
		
		// Front Face
		triangle(app, 3, 2, 1); 
		triangle(app, 3, 1, 4);

		// Left Face
		triangle(app, 5, 6, 7);
		triangle(app, 8, 5, 7);

		// Right Face
		triangle(app, 11, 10, 9);
		triangle(app, 11, 9, 12);

		// Back Face
		triangle(app, 13, 14, 15);
		triangle(app, 16, 13, 15);

		// Top Face
		triangle(app, 19, 18, 17);
		triangle(app, 19, 17, 20);

		// Bottom Face
		triangle(app, 21, 22, 23);
		triangle(app, 24, 21, 23);  
		
		app.endShape(PApplet.CLOSE);
		app.popMatrix();
	}
}
