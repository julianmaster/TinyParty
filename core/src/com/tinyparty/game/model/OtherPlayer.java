package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.physic.PhysicManager;
import com.tinyparty.game.utils.AnimationManager;

public class OtherPlayer extends Entity {

	private final TinyParty game;

	private PlayerColor playerColor;
	private boolean horizontalFlip;
	private final Vector2 size;
	private Body body;

	private boolean invincible = false;
	private boolean white = false;
	private float changeColor = 0f;

	private float stateTime;

	public OtherPlayer(int id, PlayerColor playerColor, Vector2 position, boolean horizontalFlip, TinyParty game) {
		super(id);
		this.playerColor = playerColor;
		this.horizontalFlip = horizontalFlip;
		this.game = game;
		this.size = new Vector2(Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT);
		this.body = PhysicManager.createBox(position.x, position.y, Constants.PLAYER_COLLISION_WIDTH, Constants.PLAYER_COLLISION_HEIGHT, 0, Constants.OTHER_PLAYER_CATEGORY, Constants.OTHER_PLAYER_MASK, true, false, true,this, game.getGameScreen().getWorld());
		this.stateTime = 0f;

		invincible = true;
		white = true;
		changeColor = Constants.PLAYER_CHANGE_COLOR;
	}

	@Override
	public void update(float delta) {
		stateTime += delta;
		if(invincible) {
			changeColor -= delta;
			if(changeColor < 0f) {
				changeColor = Constants.PLAYER_CHANGE_COLOR;
				white = !white;
			}
		}
	}

	@Override
	public void render(Batch batch, AssetManager assetManager, AnimationManager animationManager) {
		TextureRegion currentFrame = (TextureRegion)animationManager.get(playerColor.player.filename).getKeyFrame(stateTime, true);

		if(invincible && white) {
			currentFrame = (TextureRegion)animationManager.get(playerColor.player2.filename).getKeyFrame(stateTime, true);
		}

		if(horizontalFlip && !currentFrame.isFlipX()) {
			currentFrame.flip(horizontalFlip == true, false);
		}
		else if(!horizontalFlip && currentFrame.isFlipX()) {
			currentFrame.flip(horizontalFlip != true, false);
		}

		batch.draw(currentFrame, body.getPosition().x - Constants.PLAYER_WIDTH/2f, body.getPosition().y - Constants.PLAYER_HEIGHT/2f);
	}

	@Override
	public void renderShadow(Batch batch, AssetManager assetManager, AnimationManager animationManager) {

	}

	public void touched() {
		invincible = true;
		white = true;
		changeColor = Constants.PLAYER_CHANGE_COLOR;
	}

	public void endInvicible() {
		invincible = false;
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
	public Body getBody() {
		return body;
	}

	@Override
	public Vector2 getSize() {
		return size;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public void setHorizontalFlip(boolean horizontalFlip) {
		this.horizontalFlip = horizontalFlip;
	}
}
