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

	public void fire(int playerId, boolean sourceOfFire, Vector2 position, Vector3 worldClickCoords, BulletSizeSpeedParameter bulletSizeSpeedParameter, BulletDistanceAmountParameter bulletDistanceAmountParameter, BulletFrenquecyDamageParameter bulletFrenquecyDamageParameter) {
		for(float offset : BulletAmountConfiguration.configuration.get(bulletDistanceAmountParameter.amount)) {
			float angleRad = MathUtils.atan2(worldClickCoords.y - position.y + 3f, worldClickCoords.x - position.x);
			float angleDeg = angleRad * MathUtils.radiansToDegrees + offset;

			Vector2 direction = new Vector2(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg));

			if(bulletSizeSpeedParameter == BulletSizeSpeedParameter.STATIC &&
					(bulletDistanceAmountParameter == BulletDistanceAmountParameter.MEDIUM || bulletDistanceAmountParameter == BulletDistanceAmountParameter.LOW)) {
				position.x += direction.x * BulletDistanceAmountParameter.LOW.distance * BulletSizeSpeedParameter.SLOW.speed;
				position.y += direction.y * BulletDistanceAmountParameter.LOW.distance * BulletSizeSpeedParameter.SLOW.speed;
			}

			new Bullet(playerId, sourceOfFire, position, direction, angleDeg*MathUtils.degreesToRadians, bulletSizeSpeedParameter.size, bulletSizeSpeedParameter.speed, bulletFrenquecyDamageParameter.damage, bulletDistanceAmountParameter.distance, game);
		}
	}
}
