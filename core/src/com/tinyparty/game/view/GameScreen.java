package com.tinyparty.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.Entity;
import com.tinyparty.game.model.Ground;
import com.tinyparty.game.model.Player;
import com.tinyparty.game.physic.EntityContactListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameScreen extends ScreenAdapter {

	private final TinyParty game;

	private ReentrantLock lock = new ReentrantLock();

	private Ground ground;
	private Player player;
	private List<Entity> entities = new ArrayList<>();
	private List<Entity> entitiesToAdd = new ArrayList<>();

	// Box2D
	private World world;
	private List<Body> bodiesToRemove = new ArrayList<>();
	private boolean showDebugPhysics = false;
	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public GameScreen(TinyParty game) {
		this.game = game;
		lock.lock();
		world = new World(new Vector2(), false);
		world.setContactListener(new EntityContactListener(game));
		lock.unlock();
	}

	@Override
	public void show() {
		ground = new Ground();
		player = new Player(game);
		entitiesToAdd.add(player);
	}

	@Override
	public void render(float delta) {
		Batch batch = game.getBatch();
		Camera camera = game.getCamera();

		if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
			showDebugPhysics = !showDebugPhysics;
		}

		lock.lock();

		for(Entity entity : entitiesToAdd) {
			entities.add(entity);
		}
		entitiesToAdd.clear();

		for(Entity entity : entities) {
			entity.update(delta);
		}

		camera.position.set(player.getPosition().x, player.getPosition().y,0);
		camera.update();

		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		Asset[][] assets = ground.getAssets();
		for(int i = 0; i < assets.length; i++) {
			for(int j = 0; j < assets[i].length; j++) {
				batch.draw(game.getAssetManager().get(assets[i][j].filename, Texture.class), 16*i, 16*j);
			}
		}
		batch.draw(game.getAssetManager().get(Asset.PLAYER.filename, Texture.class), player.getPosition().x, player.getPosition().y);

		batch.end();

		if(showDebugPhysics) {
			debugRenderer.render(world, game.getCamera().combined);
		}

		for(Body body : bodiesToRemove){
			world.destroyBody(body);
		}
		bodiesToRemove.clear();

		world.step(1/60f, 6, 2);
		lock.unlock();
	}

	@Override
	public void dispose() {
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public World getWorld() {
		return world;
	}

	public List<Body> getBodiesToRemove() {
		return bodiesToRemove;
	}
}
