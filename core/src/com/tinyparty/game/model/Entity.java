package com.tinyparty.game.model;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements Comparable {
	public abstract void update(float delta);
	public abstract void render(Batch batch, AssetManager assetManager);
	public abstract void renderShadow(Batch batch, AssetManager assetManager);
	public abstract Vector2 getPosition();
	public abstract Vector2 getSize();
}
