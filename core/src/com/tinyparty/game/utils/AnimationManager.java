package com.tinyparty.game.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class AnimationManager {
	final ObjectMap<String, Animation> animations = new ObjectMap();

	public Animation load(String name, Texture texture, float frameDuration, int tileWidth, int tileHeight) {
		return animations.put(name, new Animation(frameDuration, TextureRegion.split(texture, tileWidth, tileHeight)[0]));
	}

	public Animation get(String name) {
		return animations.get(name);
	}
}
