package com.tinyparty.game.view;

public enum Asset {

	// Assets
	GROUND("ground.jpg"),
	PLAYER("player.jpg"),
	STANDARD_BULLET("standard_bullet.jpg"),

	// Test
	TEST("badlogic.jpg");

	public String filename;

	Asset(String filename) {
		this.filename = filename;
	}
}
