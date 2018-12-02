package com.tinyparty.game;

public class Constants {
	// Network
	public static final int PORT = 8465;
	public static final String HOST = "localhost";

	public static final float PLAYER_COLLISION_WIDTH = 2f;
	public static final float PLAYER_COLLISION_HEIGHT = 4f;

	public static final short PLAYER_CATEGORY = 1;
	public static final short OTHER_PLAYER_CATEGORY = 2;
	public static final short BULLET_MOVE_CATEGORY = 4;
	public static final short OTHER_BULLET_CATEGORY = 8;

	public static final short PLAYER_MASK = OTHER_PLAYER_CATEGORY | OTHER_BULLET_CATEGORY;
	public static final short OTHER_PLAYER_MASK = PLAYER_CATEGORY;
	public static final short BULLET_MOVE_MASK = 0;
	public static final short OTHER_BULLET_MOVE_MASK = PLAYER_CATEGORY;
}
