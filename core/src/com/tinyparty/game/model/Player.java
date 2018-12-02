package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.physic.PhysicManager;

public class Player extends Entity {

	private final TinyParty game;

	private int life = 3;
	private final Vector2 size;
	private Vector2 oldPosition;
	private Body body;

	public Player(TinyParty game) {
		this.game = game;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
	}

	public void init() {
		life = 3;
		this.oldPosition = new Vector2(MathUtils.random()*100f, MathUtils.random()*100f);
		this.body = PhysicManager.createBox(oldPosition.x, oldPosition.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.PLAYER_CATEGORY, Constants.PLAYER_MASK, false, false, this, game.getGameScreen().getWorld());
	}

	@Override
	public void update(float delta) {

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
