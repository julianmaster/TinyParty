package com.tinyparty.game;

public class Constants {
	public static final int CAMERA_WIDTH = 200;
	public static final int CAMERA_HEIGHT = 200;
	public static final int WINDOM_ZOOM = 4;
	public static final int WINDOW_WIDTH = CAMERA_WIDTH * WINDOM_ZOOM;
	public static final int WINDOW_HEIGHT = CAMERA_HEIGHT * WINDOM_ZOOM;


	// Network
	public static final int PORT = 8465;
	public static final String HOST = "localhost";

	public static final float PLAYER_INVINCIBLE_DURATION = 15f;
	public static final float PLAYER_WIDTH = 4f;
	public static final float PLAYER_COLLISION_WIDTH = 2f;
	public static final float PLAYER_HEIGHT = 5f;
	public static final float PLAYER_COLLISION_HEIGHT = 5f;
	public static final float BULLET_WIDTH = 4f;
	public static final float BULLET_HEIGHT = 4f;

	public static final float PLAYER_SPEED = 80f;

	public static final short PLAYER_CATEGORY = 1;
	public static final short OTHER_PLAYER_CATEGORY = 2;
	public static final short BULLET_MOVE_CATEGORY = 4;
	public static final short OTHER_BULLET_CATEGORY = 8;

	public static final short PLAYER_MASK = OTHER_PLAYER_CATEGORY | OTHER_BULLET_CATEGORY;
	public static final short OTHER_PLAYER_MASK = PLAYER_CATEGORY;
	public static final short BULLET_MOVE_MASK = 0;
	public static final short OTHER_BULLET_MOVE_MASK = PLAYER_CATEGORY;
}
