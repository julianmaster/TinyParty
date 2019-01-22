package com.tinyparty.game;

import com.badlogic.gdx.math.Vector2;
import com.tinyparty.game.model.PlayerColor;
import io.vertx.core.http.ServerWebSocket;

public class Data {
	public ServerWebSocket webSocket;
	public PlayerColor playerColor;
	public boolean ready;
	public Vector2 position;
	public boolean horizontalFlip;
	public boolean invincible;
}
