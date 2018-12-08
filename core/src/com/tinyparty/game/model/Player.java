package com.tinyparty.game.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
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

public class Player extends Entity {

	private final TinyParty game;

	private int life = 3;
	private final Vector2 size;
	private Vector2 oldPosition;
	private Body body;

	private BulletSizeSpeedParameter bulletSizeSpeedParameter;
	private BulletDistanceAmountParameter bulletDistanceAmountParameter;
	private BulletFrenquecyDamageParameter bulletFrenquecyDamageParameter;

	private float waitFire = 0f;

	public Player(TinyParty game) {
		this.game = game;

		this.life = 3;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
		this.oldPosition = new Vector2(MathUtils.random()*100f, MathUtils.random()*100f);
		this.body = PhysicManager.createBox(oldPosition.x, oldPosition.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.PLAYER_CATEGORY, Constants.PLAYER_MASK, false, false, this, game.getGameScreen().getWorld());

		bulletSizeSpeedParameter = BulletSizeSpeedParameter.values()[MathUtils.random(BulletSizeSpeedParameter.values().length-1)];
//		bulletDistanceAmountParameter = BulletDistanceAmountParameter.values()[MathUtils.random(BulletDistanceAmountParameter.values().length-1)];
		bulletDistanceAmountParameter = BulletDistanceAmountParameter.HIGH;
		bulletFrenquecyDamageParameter = BulletFrenquecyDamageParameter.values()[MathUtils.random(BulletFrenquecyDamageParameter.values().length-1)];
	}

	@Override
	public void update(float delta) {
		waitFire -= delta;
		if(waitFire < 0f || MathUtils.isZero(waitFire)) {
			waitFire = -1f;
		}
		if(Gdx.input.justTouched() && waitFire < 0f) {
			waitFire = bulletFrenquecyDamageParameter.frenquecy;

			Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
			Vector3 worldCoords = game.getCamera().unproject(screenCoords);

			float angle = MathUtils.atan2(worldCoords.y - body.getPosition().y + 3f, worldCoords.x - body.getPosition().x);

			for(float offset : BulletAmountConfiguration.configuration.get(bulletDistanceAmountParameter.amount)) {
				Vector2 direction = new Vector2(MathUtils.cos(angle+offset), MathUtils.sin(angle+offset));

				Bullet bullet = new Bullet(game, new Vector2(body.getPosition().x, body.getPosition().y - 3f), direction, angle+offset,true, bulletSizeSpeedParameter.size, bulletSizeSpeedParameter.speed, bulletFrenquecyDamageParameter.damage, bulletDistanceAmountParameter.distance);
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
