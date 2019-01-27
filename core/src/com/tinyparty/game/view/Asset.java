package com.tinyparty.game.view;

public enum Asset {

	// Font
	FONT_BIG("kenney_blocks_big.fnt"),
	FONT_NORMAL("kenney_blocks_normal.fnt"),
	FONT_SMALL("kenney_blocks_small.fnt"),

	// Assets
	GROUND1("ground1.png"),
	GROUND2("ground2.png"),
	GROUND3("ground3.png"),
	GROUND4("ground4.png"),
	GROUND5("ground5.png"),
	GROUND6("ground6.png"),
	GROUND7("ground7.png"),
	STANDARD_BULLET("standard_bullet.png"),
	RED_PLAYER("red_player_animation.png"),
	RED_PLAYER_2("red_player_animation_2.png"),
	RED_HEART("red_heart_animation.png"),
	EMPTY_RED_HEART("empty_red_heart.png"),
	BLUE_PLAYER("blue_player_animation.png"),
	BLUE_PLAYER_2("blue_player_animation_2.png"),
	BLUE_HEART("blue_heart_animation.png"),
	EMPTY_BLUE_HEART("empty_blue_heart.png"),
	GREEN_PLAYER("green_player_animation.png"),
	GREEN_PLAYER_2("green_player_animation_2.png"),
	GREEN_HEART("green_heart_animation.png"),
	EMPTY_GREEN_HEART("empty_green_heart.png"),
	BACKGROUND_UI_TOP("background_UI_top.png"),
	BACKGROUND_UI_BOTTOM("background_UI_bottom.png"),

	// Test
	TEST("badlogic.jpg");

	public String filename;

	Asset(String filename) {
		this.filename = filename;
	}
}
