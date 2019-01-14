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
	PLAYER("player.png"), // TODO change sprite to new one
	PLAYER_2("player_2.png"),
	STANDARD_BULLET("standard_bullet.png"),

	// Test
	TEST("badlogic.jpg");

	public String filename;

	Asset(String filename) {
		this.filename = filename;
	}
}
