package com.tinyparty.game.view;

public enum Asset {

	// Assets
	GROUND1("ground1.png"),
	GROUND2("ground2.png"),
	GROUND3("ground3.png"),
	GROUND4("ground4.png"),
	GROUND5("ground5.png"),
	GROUND6("ground6.png"),
	GROUND7("ground7.png"),
	PLAYER("player.png"),
	STANDARD_BULLET("standard_bullet.png"),

	// Test
	TEST("badlogic.jpg");

	public String filename;

	Asset(String filename) {
		this.filename = filename;
	}
}
