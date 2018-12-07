package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.TinyParty;

public class Bullet extends Entity {

	private final TinyParty game;
	private Vector2 direction = null;
	private boolean sourceOfFire = false;
	private int size;
	private float speed;

	public float distance;

	private Body body;

	public Bullet(TinyParty game, Vector2 direction, boolean sourceOfFire, int size, float speed, float distance) {
		this.game = game;
		this.direction = direction;
		this.sourceOfFire = sourceOfFire;
		this.size = size;
		this.speed = speed;
		this.distance = distance;

		// TODO send information to server
	}

	@Override
	public void update(float delta) {
		distance -= delta;
		if(distance <= 0f) {
			game.getGameScreen().getBodiesToRemove().add(body);
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
		return null;
	}

	@Override
	public Vector2 getSize() {
		return null;
	}
}
