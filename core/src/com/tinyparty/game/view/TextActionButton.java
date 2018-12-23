package com.tinyparty.game.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tinyparty.game.TinyParty;

import java.util.concurrent.locks.ReentrantLock;

public class TextActionButton extends Actor {

	private final TinyParty game;
	private String str;
	private BitmapFont font;
	private Color defaultColor;
	private Color mouseOverColor;
	private boolean activate = true;
	private boolean mouseOver = false;

	private ReentrantLock lock = new ReentrantLock();

	private GlyphLayout layout = new GlyphLayout();

	public TextActionButton(String str, BitmapFont font, TinyParty game) {
		this(str, font, CustomColor.WHITE, game);
	}

	public TextActionButton(String str, BitmapFont font, Color defaultColor, TinyParty game) {
		this(str, font, defaultColor, CustomColor.YELLOW2, game);
	}

	public TextActionButton(String str, BitmapFont font, Color defaultColor, Color mouseOverColor, TinyParty game) {
		this.str = str;
		this.font = font;
		this.defaultColor = defaultColor;
		this.mouseOverColor = mouseOverColor;
		this.game = game;
		sizeChanged();

		this.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				lock.lock();
				mouseOver = true;
				lock.unlock();
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				lock.lock();
				mouseOver = false;
				lock.unlock();
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		BitmapFontCache cache = font.getCache();

		lock.lock();
		Color color = defaultColor;
		if(mouseOver && activate) {
			color = mouseOverColor;
		}
		lock.unlock();

		cache.setColor(color);
		cache.setText(str, getX(), getY() + layout.height);
		cache.draw(batch);
		cache.clear();
	}

	public void setStr(String str) {
		this.str = str;
		sizeChanged();
	}

	public String getStr() {
		return str;
	}

	@Override
	protected void sizeChanged() {
		layout.setText(font, str);
		this.setSize(layout.width, layout.height);
	}

	public void setActivate(boolean activate) {
		lock.lock();
		this.activate = activate;
		lock.unlock();
	}

	public boolean isActivate() {
		return activate;
	}
}
