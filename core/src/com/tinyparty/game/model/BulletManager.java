package com.tinyparty.game.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.parameter.BulletAmountConfiguration;
import com.tinyparty.game.model.parameter.BulletDistanceAmountParameter;
import com.tinyparty.game.model.parameter.BulletFrenquecyDamageParameter;
import com.tinyparty.game.model.parameter.BulletSizeSpeedParameter;

public class BulletManager {

	private final TinyParty game;

	public BulletManager(TinyParty game) {
		this.game = game;
	}

	public void fire(int playerId, boolean sourceOfFire, Vector2 source, Vector3 worldClickCoords, BulletSizeSpeedParameter bulletSizeSpeedParameter, BulletDistanceAmountParameter bulletDistanceAmountParameter, BulletFrenquecyDamageParameter bulletFrenquecyDamageParameter) {
		for(float offset : BulletAmountConfiguration.configuration.get(bulletDistanceAmountParameter.amount)) {
			float angleRad = MathUtils.atan2(worldClickCoords.y - source.y + 3f, worldClickCoords.x - source.x);
			float angleDeg = angleRad * MathUtils.radiansToDegrees + offset;

			Vector2 direction = new Vector2(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg));
			Vector2 position = new Vector2(source.x + 1f, source.y + 3f);
			new Bullet(playerId, sourceOfFire, position, direction, angleDeg*MathUtils.degreesToRadians, bulletSizeSpeedParameter.size, bulletSizeSpeedParameter.speed, bulletFrenquecyDamageParameter.damage, bulletDistanceAmountParameter.distance, game);
		}
	}
}
