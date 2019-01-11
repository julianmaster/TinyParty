package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.physic.PhysicManager;
import com.tinyparty.game.view.Asset;

public class OtherPlayer extends Entity {

	private final TinyParty game;

	private final Vector2 size;
	private Body body;

	private boolean invinsible = false;
	private float invinsibleDuration = 0f;
	private boolean white = false;
	private float changeColor = 0f;

	public OtherPlayer(int id, Vector2 position, TinyParty game) {
		super(id);
		this.game = game;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
		this.body = PhysicManager.createBox(position.x, position.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.OTHER_PLAYER_CATEGORY, Constants.OTHER_PLAYER_MASK, true, false, true,this, game.getGameScreen().getWorld());
	}

	@Override
	public void update(float delta) {
		if(invinsible) {
			invinsibleDuration -= delta;
			changeColor -= delta;
			if(invinsibleDuration < 0f) {
				invinsible = false;
			}
			else {
				if(changeColor < 0f) {
					changeColor = Constants.PLAYER_CHANGE_COLOR;
					white = !white;
				}
			}
		}
	}

	@Override
	public void render(Batch batch, AssetManager assetManager) {
		Asset asset = Asset.PLAYER;

		if(invinsible && white) {
			asset = Asset.PLAYER_2;
		}

		batch.draw(assetManager.get(asset.filename, Texture.class), body.getPosition().x - Constants.PLAYER_WIDTH/2f, body.getPosition().y - Constants.PLAYER_HEIGHT/2f);
	}

	@Override
	public void renderShadow(Batch batch, AssetManager assetManager) {

	}

	public void touched() {
		invinsible = true;
		invinsibleDuration = Constants.PLAYER_INVINCIBLE_DURATION;
		changeColor = Constants.PLAYER_CHANGE_COLOR;
		white = true;
	}

	public void die() {
		game.getGameScreen().getBodiesToRemove().add(body);
		game.getGameScreen().getEntitiesToRemove().add(this);
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	public void setPosition(Vector2 position) {
		body.setTransform(position.x, position.y, 0f);
	}

	@Override
	public Vector2 getSize() {
		return size;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
