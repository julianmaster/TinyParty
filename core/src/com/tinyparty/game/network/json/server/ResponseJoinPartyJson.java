package com.tinyparty.game.network.json.server;

import com.badlogic.gdx.math.Vector2;
import com.tinyparty.game.model.PlayerColor;

public class ResponseJoinPartyJson {
	public int id;

	public PlayerColor playerColor;
	public Vector2 position;
	public boolean horizontalFlip;

	public int[] otherIds;
	public PlayerColor[] otherPlayerColors;
	public Vector2[] otherPositions;
	public boolean[] otherHorizontalFlips;
}
