package com.tinyparty.game.network.json.server;

import com.badlogic.gdx.math.Vector2;
import com.tinyparty.game.model.PlayerColor;

public class ResponseNewOtherPlayerJson {
	public int id;
	public PlayerColor playerColor;
	public Vector2 position;
	public boolean horizontalFlip;
	public boolean invincible;
}
