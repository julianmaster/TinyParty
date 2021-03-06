package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.physic.PhysicManager;
import com.tinyparty.game.utils.AnimationManager;
import com.tinyparty.game.view.Asset;

public class Bullet extends Entity {

	private final TinyParty game;

	private int playerId;
	private boolean sourceOfFire;
	private Vector2 direction;
	private int size;
	private float speed;
	public float distance;

	// Box2D
	private Body body;

	public Bullet(int playerId, boolean sourceOfFire, Vector2 position, Vector2 direction, float angle, int size, float speed, float distance, TinyParty game) {
		super(-1);
		this.playerId = playerId;
		this.sourceOfFire = sourceOfFire;
		this.game = game;
		this.direction = direction;
		this.size = size;
		this.speed = speed;
		this.distance = distance;

		if(sourceOfFire) {
			this.body = PhysicManager.createBox(position.x, position.y, 4f*size, 4f*size, angle, Constants.BULLET_MOVE_CATEGORY, Constants.BULLET_MOVE_MASK, false, true, true, this, game.getGameScreen().getWorld());
		}
		else {
			this.body = PhysicManager.createBox(position.x, position.y, 4f*size, 4f*size, angle, Constants.OTHER_BULLET_CATEGORY, Constants.OTHER_BULLET_MOVE_MASK, false, true, true, this, game.getGameScreen().getWorld());
		}
		body.setTransform(position.x, position.y, angle);

		this.direction.scl(this.speed);
		game.getGameScreen().getEntitiesToAdd().add(this);
	}

	@Override
	public void update(float delta) {
		distance -= delta;
		if(distance <= 0f) {
			game.getGameScreen().getEntitiesToRemove().add(this);
			game.getGameScreen().getBodiesToRemove().add(body);
		}
		else {
			body.setLinearVelocity(direction);
		}
	}

	@Override
	public void render(Batch batch, AssetManager assetManager, AnimationManager animationManager) {
		batch.draw(assetManager.get(Asset.STANDARD_BULLET.filename, Texture.class), body.getPosition().x - Constants.BULLET_WIDTH/2f, body.getPosition().y - Constants.BULLET_HEIGHT/2f, 4f/2f, 4f/2f,
				4f, 4f, size, size, body.getAngle() * MathUtils.radiansToDegrees, 0, 0, 4, 4, false, false);
	}

	@Override
	public void renderShadow(Batch batch, AssetManager assetManager, AnimationManager animationManager) {

	}

	@Override
	public int compareTo(Object o) {
		return 1;
	}

	@Override
	public Vector2 getPosition() {
		return null;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public Vector2 getSize() {
		return null;
	}

	public int getPlayerId() {
		return playerId;
	}
}
