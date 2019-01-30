package com.tinyparty.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.*;
import com.tinyparty.game.shared.RequestInfoOtherPlayerJson;
import com.tinyparty.game.shared.RequestPlayerReadyJson;
import com.tinyparty.game.physic.EntityContactListener;
import com.tinyparty.game.utils.AnimationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen extends ScreenAdapter {

	private final TinyParty game;

	private Ground ground;
	private Player player;
	private boolean loose = false;
	private List<Entity> entities = new ArrayList<>();
	private List<Entity> entitiesToAdd = new ArrayList<>();
	private List<Entity> entitiesToRemove = new ArrayList<>();

	// Box2D
	private World world;
	private BulletManager bulletManager;
	private List<Body> bodiesToRemove = new ArrayList<>();
	private boolean showDebugPhysics = false;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	private float stateTime;

	public GameScreen(TinyParty game) {
		this.game = game;
		synchronized (game.getLock()) {
			ground = new Ground();
			bulletManager = new BulletManager(game);
		}
	}

	public void init(int id, PlayerColor playerColor, Vector2 position, boolean horizontalFlip) {
		stateTime = 0f;
		loose = false;
		world = new World(new Vector2(), false);
		world.setContactListener(new EntityContactListener(game));

		player = new Player(id, playerColor, horizontalFlip, game);
		player.invincible();
		player.setPosition(position);
		entitiesToAdd.add(player);

		RequestPlayerReadyJson requestPlayerReadyJson = new RequestPlayerReadyJson();
		requestPlayerReadyJson.idPlayer = id;
		game.getClient().send(requestPlayerReadyJson);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		stateTime += delta;
		Batch spriteBatch = game.getSpriteBatch();
		Batch hudBatch = game.getHudBatch();
		Camera camera = game.getCamera();
		Camera hudCamera = game.getHudCamera();
		AssetManager assetManager = game.getAssetManager();
		AnimationManager animationManager = game.getAnimationManager();
		BitmapFont fontSmall = game.getFontSmall();
		GlyphLayout layout = game.getLayout();

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			showDebugPhysics = !showDebugPhysics;
		}

		synchronized (game.getLock()) {
			/**
			 * Player loose the game
			 */
			if (loose) {
				// Switch screen
				game.setScreen(game.getStartScreen());
			}
			else {
				// Add entities
				for (Entity entity : entitiesToAdd) {
					entities.add(entity);
				}
				entitiesToAdd.clear();

				// Update entities
				for (Entity entity : entities) {
					entity.update(delta);
				}

				// Remove entities
				for (Entity entity : entitiesToRemove) {
					entities.remove(entity);
				}
				entitiesToRemove.clear();

				Collections.sort(entities);


				/**
				 * Sprite batch
				 */
				camera.position.set(player.getPosition().x, player.getPosition().y, 0);
				camera.update();
				spriteBatch.setProjectionMatrix(camera.combined);
				spriteBatch.begin();

				Asset[][] assets = ground.getAssets();
				for (int i = 0; i < assets.length; i++) {
					for (int j = 0; j < assets[i].length; j++) {
						spriteBatch.draw(assetManager.get(assets[i][j].filename, Texture.class), 16 * i, 16 * j);
					}
				}

				// Render entities
				for (Entity entity : entities) {
					entity.render(spriteBatch, assetManager, animationManager);
				}

				spriteBatch.end();


				/**
				 * HUD batch
				 */
				hudCamera.update();
				hudBatch.setProjectionMatrix(hudCamera.combined);
				hudBatch.begin();

				Texture backgroundUITop = assetManager.get(Asset.BACKGROUND_UI_TOP.filename, Texture.class);
				hudBatch.draw(backgroundUITop, 0, Constants.CAMERA_HEIGHT - backgroundUITop.getHeight());

				for (int life = 0; life < 3; life++) {
					if (life < player.getLife()) {
						// Render heart
						TextureRegion currentFrame = (TextureRegion) animationManager.get(player.getPlayerColor().heart.filename).getKeyFrame(stateTime, true);

						hudBatch.draw(currentFrame, (Constants.HEART_WIDTH + 2) * life + 2, Constants.CAMERA_HEIGHT - 2 - Constants.HEART_HEIGHT);
					} else {
						// Render empty heart
						hudBatch.draw(assetManager.get(player.getPlayerColor().empty_heart.filename, Texture.class), (Constants.HEART_WIDTH + 2) * life + 2, Constants.CAMERA_HEIGHT - 2 - Constants.HEART_HEIGHT);
					}
				}

				int kill = game.getStartScreen().getKill();
				int death = game.getStartScreen().getDeath();
				float ratio;
				if (death < 1) {
					ratio = kill;
				} else {
					ratio = (float) kill / (float) death;
				}

				String ratioText = "[WHITE]RATIO: [YELLOW1]" + StartScreen.round(ratio, 2);
				String killDeathText = " [WHITE]K: [GREEN1]" + kill + "   [WHITE]/D: [RED2]" + death + "[GRIS1]";
				;

				layout.setText(fontSmall, ratioText);
				float maxLength = layout.width;
				layout.setText(fontSmall, killDeathText);
				maxLength = maxLength > layout.width ? maxLength : layout.width;

				Texture backgroundUIBottom = assetManager.get(Asset.BACKGROUND_UI_BOTTOM.filename, Texture.class);
				hudBatch.draw(backgroundUIBottom, Constants.CAMERA_WIDTH - (maxLength + 22), 0);

				layout.setText(fontSmall, ratioText);
				fontSmall.draw(hudBatch, layout, Constants.CAMERA_WIDTH - layout.width - 2, layout.height * 2 + 2 * 2);
				layout.setText(fontSmall, killDeathText);
				fontSmall.draw(hudBatch, layout, Constants.CAMERA_WIDTH - layout.width - 2, layout.height + 2);

				hudBatch.end();

				if (showDebugPhysics) {
					debugRenderer.render(world, game.getCamera().combined);
				}

				for (Body body : bodiesToRemove) {
					world.destroyBody(body);
				}
				bodiesToRemove.clear();

				world.step(1 / 60f, 6, 2);
			}
		}
	}

	@Override
	public void hide() {
		synchronized (game.getLock()) {
			world.dispose();
			world = null;

			entities.clear();
			entitiesToAdd.clear();
			entitiesToRemove.clear();
			bodiesToRemove.clear();
			player = null;
		}
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		world.dispose();
	}

	public void addNewOtherPlayer(int id, PlayerColor playerColor, Vector2 position, boolean horizontalFlip, boolean invincible) {
		OtherPlayer otherPlayer = new OtherPlayer(id, playerColor, position, horizontalFlip, game);
		entitiesToAdd.add(otherPlayer);
		if(invincible) {
			otherPlayer.touched();
		}
	}

	public void changeOtherPlayerPosition(int id, Vector2 position, boolean horizontalFlip) {
		boolean found = false;
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					otherPlayer.setPosition(position);
					otherPlayer.setHorizontalFlip(horizontalFlip);
					found = true;
				}
			}
		}

		if(!found) {
			for(Entity entity : entitiesToAdd) {
				if(entity instanceof OtherPlayer) {
					OtherPlayer otherPlayer = (OtherPlayer)entity;
					if(otherPlayer.getId() == id) {
						otherPlayer.setPosition(position);
						otherPlayer.setHorizontalFlip(horizontalFlip);
						found = true;
					}
				}
			}
		}

		if(!found) {
			RequestInfoOtherPlayerJson requestInfoOtherPlayerJson = new RequestInfoOtherPlayerJson();
			requestInfoOtherPlayerJson.idPlayer = player.getId();
			requestInfoOtherPlayerJson.otherPlayer = id;

			game.getClient().send(requestInfoOtherPlayerJson);
		}
	}

	public void removeOtherPlayer(int id) {
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					otherPlayer.die();
				}
			}
		}
	}

	public void changeOtherPlayerInvincible(int id, boolean start) {
		boolean found = false;
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					if(start) {
						otherPlayer.touched();
					}
					else {
						otherPlayer.endInvicible();
					}
					found = true;
				}
			}
		}

		if(!found) {
			for(Entity entity : entitiesToAdd) {
				if(entity instanceof OtherPlayer) {
					OtherPlayer otherPlayer = (OtherPlayer)entity;
					if(otherPlayer.getId() == id) {
						if(start) {
							otherPlayer.touched();
						}
						else {
							otherPlayer.endInvicible();
						}
						found = true;
					}
				}
			}
		}

		if(!found) {
			RequestInfoOtherPlayerJson requestInfoOtherPlayerJson = new RequestInfoOtherPlayerJson();
			requestInfoOtherPlayerJson.idPlayer = player.getId();
			requestInfoOtherPlayerJson.otherPlayer = id;

			game.getClient().send(requestInfoOtherPlayerJson);
		}
	}

	public void otherPlayerDie(int id) {
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					otherPlayer.die();
				}
			}
		}
	}

	public void setLoose(boolean loose) {
		this.loose = loose;
	}

	public Player getPlayer() {
		return player;
	}

	public World getWorld() {
		return world;
	}

	public BulletManager getBulletManager() {
		return bulletManager;
	}

	public List<Entity> getEntitiesToRemove() {
		return entitiesToRemove;
	}

	public List<Entity> getEntitiesToAdd() {
		return entitiesToAdd;
	}

	public List<Body> getBodiesToRemove() {
		return bodiesToRemove;
	}
}
