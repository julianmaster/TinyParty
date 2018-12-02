package com.tinyparty.game.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.physic.EntityContactListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameScreen extends ScreenAdapter {

	private final TinyParty game;

	private ReentrantLock lock = new ReentrantLock();

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
	}

	@Override
	public void render(float delta) {
		lock.lock();
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
}
