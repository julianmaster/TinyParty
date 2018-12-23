package com.tinyparty.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.parameter.BulletAmountConfiguration;
import com.tinyparty.game.model.parameter.BulletDistanceAmountParameter;
import com.tinyparty.game.model.parameter.BulletFrenquecyDamageParameter;
import com.tinyparty.game.model.parameter.BulletSizeSpeedParameter;
import com.tinyparty.game.physic.PhysicManager;
import com.tinyparty.game.view.Asset;

public class Player extends Entity {

	private final TinyParty game;

	private int life = 3;
	private final Vector2 size;
	private Vector2 oldPosition;
	private Body body;

	private BulletSizeSpeedParameter bulletSizeSpeedParameter;
	private BulletDistanceAmountParameter bulletDistanceAmountParameter;
	private BulletFrenquecyDamageParameter bulletFrenquecyDamageParameter;

	private float invinsibleDuration = 0f;
	private float waitFire = 0f;

	public Player(int id, TinyParty game) {
		super(id);
		this.game = game;

		this.life = 3;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
		this.oldPosition = new Vector2(MathUtils.random()*100f, MathUtils.random()*100f);
		this.body = PhysicManager.createBox(oldPosition.x, oldPosition.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.PLAYER_CATEGORY, Constants.PLAYER_MASK, false, false, this, game.getGameScreen().getWorld());

		bulletSizeSpeedParameter = BulletSizeSpeedParameter.values()[MathUtils.random(BulletSizeSpeedParameter.values().length-1)];
		bulletDistanceAmountParameter = BulletDistanceAmountParameter.values()[MathUtils.random(BulletDistanceAmountParameter.values().length-1)];
		bulletFrenquecyDamageParameter = BulletFrenquecyDamageParameter.values()[MathUtils.random(BulletFrenquecyDamageParameter.values().length-1)];

		invinsibleDuration = Constants.PLAYER_INVINSIBLE_DURATION;

		// TODO send position
//		bulletSizeSpeedParameter = BulletSizeSpeedParameter.STATIC;
//		bulletDistanceAmountParameter = BulletDistanceAmountParameter.LOW;
//		bulletFrenquecyDamageParameter = BulletFrenquecyDamageParameter.HIGH;
	}

	@Override
	public void update(float delta) {
		waitFire -= delta;
		if(waitFire < 0f || MathUtils.isZero(waitFire)) {
			waitFire = -1f;
		}

		invinsibleDuration -= delta;
		if(invinsibleDuration < 0f) {
			invinsibleDuration = 0f;
		}

		if(Gdx.input.justTouched() && waitFire < 0f) {
			waitFire = bulletFrenquecyDamageParameter.frenquecy;

			Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
			Vector3 worldCoords = game.getCamera().unproject(screenCoords);

			for(float offset : BulletAmountConfiguration.configuration.get(bulletDistanceAmountParameter.amount)) {
				float angleRad = MathUtils.atan2(worldCoords.y - body.getPosition().y + 3f, worldCoords.x - body.getPosition().x);
				float angleDeg = angleRad * MathUtils.radiansToDegrees + offset;

				Vector2 direction = new Vector2(MathUtils.cosDeg(angleDeg), MathUtils.sinDeg(angleDeg));

				Vector2 position = new Vector2(body.getPosition().x + 1f, body.getPosition().y + 3f);
				if(bulletSizeSpeedParameter == BulletSizeSpeedParameter.STATIC &&
						(bulletDistanceAmountParameter == BulletDistanceAmountParameter.MEDIUM || bulletDistanceAmountParameter == BulletDistanceAmountParameter.LOW)) {
					position.x += direction.x * BulletDistanceAmountParameter.LOW.distance * BulletSizeSpeedParameter.SLOW.speed;
					position.y += direction.y * BulletDistanceAmountParameter.LOW.distance * BulletSizeSpeedParameter.SLOW.speed;
				}

				new Bullet(game, position, direction, angleDeg*MathUtils.degreesToRadians,true, bulletSizeSpeedParameter.size, bulletSizeSpeedParameter.speed, bulletFrenquecyDamageParameter.damage, bulletDistanceAmountParameter.distance);
			}
		}

		float y = 0f, x = 0f;
		if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
			y += 1f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			y -= 1f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			x -= 1f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			x += 1f;
		}

		if(x != 0f && y != 0f) {
			x *= 0.7f;
			y *= 0.7f;
		}
		body.setLinearVelocity(x * Constants.PLAYER_SPEED, y * Constants.PLAYER_SPEED);
		if(!body.getPosition().epsilonEquals(oldPosition)) {
			oldPosition.set(body.getPosition());
			// TODO send position to server
		}
	}

	@Override
	public void render(Batch batch, AssetManager assetManager) {
		if((int)invinsibleDuration % 2 == 0) {
			batch.draw(game.getAssetManager().get(Asset.PLAYER.filename, Texture.class), body.getPosition().x, body.getPosition().y);
		}
		else {
			batch.draw(game.getAssetManager().get(Asset.PLAYER_2.filename, Texture.class), body.getPosition().x, body.getPosition().y);
		}
	}

	@Override
	public void renderShadow(Batch batch, AssetManager assetManager) {

	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public Vector2 getSize() {
		return size;
	}
}
