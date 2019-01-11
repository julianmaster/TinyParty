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
import com.tinyparty.game.model.parameter.BulletDistanceAmountParameter;
import com.tinyparty.game.model.parameter.BulletSizeSpeedParameter;
import com.tinyparty.game.network.json.client.RequestPlayerFireJson;
import com.tinyparty.game.network.json.client.RequestPositionPlayerJson;
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

	private boolean invinsible = false;
	private float invinsibleDuration = 0f;
	private boolean white = false;
	private float changeColor = 0f;
	private float waitFire = 0f;

	public Player(int id, TinyParty game) {
		super(id);
		this.game = game;

		this.life = 3;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
		this.oldPosition = new Vector2();
		this.body = PhysicManager.createBox(oldPosition.x, oldPosition.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.PLAYER_CATEGORY, Constants.PLAYER_MASK, false, false, true,this, game.getGameScreen().getWorld());

		bulletSizeSpeedParameter = BulletSizeSpeedParameter.values()[MathUtils.random(BulletSizeSpeedParameter.values().length-1)];
//		bulletSizeSpeedParameter = BulletSizeSpeedParameter.SLOW;
		bulletDistanceAmountParameter = BulletDistanceAmountParameter.values()[MathUtils.random(BulletDistanceAmountParameter.values().length-1)];

		invinsible = true;
		invinsibleDuration = Constants.PLAYER_INVINCIBLE_DURATION;
	}

	@Override
	public void update(float delta) {
		waitFire -= delta;
		if(waitFire < 0f || MathUtils.isZero(waitFire)) {
			waitFire = -1f;
		}

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

		if(Gdx.input.justTouched() && waitFire < 0f) {
			waitFire = Constants.BULLET_FREQUENCY;

			Vector3 screenClickCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
			Vector3 worldClickCoords = game.getCamera().unproject(screenClickCoords);

			game.getGameScreen().getBulletManager().fire(getId(), true, body.getPosition(), worldClickCoords, bulletSizeSpeedParameter, bulletDistanceAmountParameter);

			RequestPlayerFireJson requestPlayerFireJson = new RequestPlayerFireJson();
			requestPlayerFireJson.idPlayer = getId();
			requestPlayerFireJson.position = body.getPosition();
			requestPlayerFireJson.worldClickCoords = worldClickCoords;
			requestPlayerFireJson.bulletSizeSpeedParameter = bulletSizeSpeedParameter;
			requestPlayerFireJson.bulletDistanceAmountParameter = bulletDistanceAmountParameter;
			game.getClient().send(requestPlayerFireJson);
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
			RequestPositionPlayerJson requestPositionPlayerJson = new RequestPositionPlayerJson();
			requestPositionPlayerJson.id = getId();
			requestPositionPlayerJson.position = body.getPosition();
			game.getClient().send(requestPositionPlayerJson);
		}
	}

	@Override
	public void render(Batch batch, AssetManager assetManager) {
		Asset asset = Asset.PLAYER;

		if(invinsible && white) {
			asset = Asset.PLAYER_2;
		}

		batch.draw(game.getAssetManager().get(asset.filename, Texture.class), body.getPosition().x - Constants.PLAYER_WIDTH/2f, body.getPosition().y - Constants.PLAYER_HEIGHT/2f);
	}

	@Override
	public void renderShadow(Batch batch, AssetManager assetManager) {

	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public void touched() {
		life -= 1;
		invinsible = true;
		invinsibleDuration = Constants.PLAYER_INVINCIBLE_DURATION;
		changeColor = Constants.PLAYER_CHANGE_COLOR;
		white = true;
	}

	public void die() {
		game.getGameScreen().getBodiesToRemove().add(body);
		game.getGameScreen().getEntitiesToRemove().remove(this);
	}

	@Override
	public int getId() {
		return super.getId();
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

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isInvinsible() {
		return invinsible;
	}
}
