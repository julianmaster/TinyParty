package com.tinyparty.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.*;
import com.tinyparty.game.network.json.client.RequestInfoOtherPlayerJson;
import com.tinyparty.game.network.json.client.RequestPlayerReadyJson;
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

	public GameScreen(TinyParty game) {
		this.game = game;
		game.getLock().lock();
		ground = new Ground();
		bulletManager = new BulletManager(game);
		game.getLock().unlock();
	}

	public void init(int id, PlayerColor playerColor, Vector2 position, boolean horizontalFlip) {
		loose = false;
		world = new World(new Vector2(), false);
		world.setContactListener(new EntityContactListener(game));

		player = new Player(id, playerColor, horizontalFlip, game);
		player.touched();
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
		Batch batch = game.getBatch();
		Camera camera = game.getCamera();
		AssetManager assetManager = game.getAssetManager();
		AnimationManager animationManager = game.getAnimationManager();

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			showDebugPhysics = !showDebugPhysics;
		}

		game.getLock().lock();

		/**
		 * Player loose the game
		 */
		if(loose) {
			// Switch screen
			game.setScreen(game.getStartScreen());
			// Unlock
			game.getLock().unlock();
			return;
		}

		// Add entities
		for(Entity entity : entitiesToAdd) {
			entities.add(entity);
		}
		entitiesToAdd.clear();

		// Update entities
		for(Entity entity : entities) {
			entity.update(delta);
		}

		// Remove entities
		for(Entity entity : entitiesToRemove) {
			entities.remove(entity);
		}
		entitiesToRemove.clear();

		camera.position.set(player.getPosition().x, player.getPosition().y,0);
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		Collections.sort(entities);

		batch.begin();

		Asset[][] assets = ground.getAssets();
		for(int i = 0; i < assets.length; i++) {
			for(int j = 0; j < assets[i].length; j++) {
				batch.draw(game.getAssetManager().get(assets[i][j].filename, Texture.class), 16*i, 16*j);
			}
		}

		// Render entities
		for(Entity entity : entities) {
			entity.render(batch, assetManager, animationManager);
		}

		// TODO show UI with life, player kill and death count and ratio

		for(int life = 0; life < 3; life++) {
			if(life < player.getLife()) {
				// Render heart
			}
			else {
				// Render empty heart
			}
		}

		batch.end();

		if(showDebugPhysics) {
			debugRenderer.render(world, game.getCamera().combined);
		}

		for(Body body : bodiesToRemove){
			world.destroyBody(body);
		}
		bodiesToRemove.clear();

		world.step(1/60f, 6, 2);
		game.getLock().unlock();
	}

	@Override
	public void hide() {
		game.getLock().lock();

		world.dispose();
		world = null;

		entities.clear();
		entitiesToAdd.clear();
		entitiesToRemove.clear();
		bodiesToRemove.clear();
		player = null;
		game.getLock().unlock();
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
