package com.tinyparty.game.network.json.server;

import com.badlogic.gdx.math.Vector2;

public class ResponseJoinPartyJson {
	public int id;
	public Vector2 position;
	public int[] otherIds;
	public Vector2[] otherPositions;
}
