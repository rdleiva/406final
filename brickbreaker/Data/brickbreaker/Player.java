package brickbreaker;

import processing.core.PShape;

public class Player {
	/**
	 * player object x location
	 */
	private float x_;
	/**
	 * player object y location
	 */
	private float y_;
	/**
	 * player object z location
	 */
	private float z_;
	/**
	 * player object width
	 */
	private float width_;
	/**
	 * player object height
	 */
	private float height_;
	
	private PShape head_;
	private PShape body_;
	
	public Player(float x, float y, float z, float width, float height) {
		x_ = x;
		y_ = y;
		z_ = z;
		width_ = width;
		height_ = height;
		
	};
}
