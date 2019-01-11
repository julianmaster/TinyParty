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
import com.tinyparty.game.physic.EntityContactListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameScreen extends ScreenAdapter {

	private final TinyParty game;

	private Ground ground;
	private Player player;
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

	public void init(int id, Vector2 position) {
		if(world != null) {
			world.dispose();
		}

		world = new World(new Vector2(), false);
		if(player == null) {
			player = new Player(id, game);
		}
		world.setContactListener(new EntityContactListener(game));
		player.setPosition(position);
		entitiesToAdd.add(player);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Batch batch = game.getBatch();
		Camera camera = game.getCamera();
		AssetManager assetManager = game.getAssetManager();

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			showDebugPhysics = !showDebugPhysics;
		}

		game.getLock().lock();

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
			entity.render(batch, assetManager);
		}

		// TODO show UI with life, player kill and death count and ratio

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
		entities.clear();
		entitiesToAdd.clear();
		entitiesToRemove.clear();
		player = null;
	}

	@Override
	public void dispose() {
		debugRenderer.dispose();
		world.dispose();
	}

	public void addNewOtherPlayer(int id, Vector2 position) {
		OtherPlayer otherPlayer = new OtherPlayer(id, position, game);
		entitiesToAdd.add(otherPlayer);
	}

	public void changeOtherPlayerPosition(int id, Vector2 position) {
		boolean found = false;
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					otherPlayer.setPosition(position);
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
						found = true;
					}
				}
			}
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

	public void changeOtherPlayerInvinsible(int id) {
		for(Entity entity : entities) {
			if(entity instanceof OtherPlayer) {
				OtherPlayer otherPlayer = (OtherPlayer)entity;
				if(otherPlayer.getId() == id) {
					otherPlayer.touched();
				}
			}
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
