package com.tinyparty.game.network.json.client;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tinyparty.game.model.parameter.BulletDistanceAmountParameter;
import com.tinyparty.game.model.parameter.BulletSizeSpeedParameter;

public class RequestPlayerFireJson {
	public int idPlayer;
	public Vector2 position;
	public Vector3 worldClickCoords;
	public BulletSizeSpeedParameter bulletSizeSpeedParameter;
	public BulletDistanceAmountParameter bulletDistanceAmountParameter;
}
