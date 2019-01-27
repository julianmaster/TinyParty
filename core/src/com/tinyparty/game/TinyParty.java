package com.tinyparty.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tinyparty.game.network.TinyPartyClient;
import com.tinyparty.game.utils.AnimationManager;
import com.tinyparty.game.view.Asset;
import com.tinyparty.game.view.CustomColor;
import com.tinyparty.game.view.GameScreen;
import com.tinyparty.game.view.StartScreen;

public class TinyParty extends Game {
	private SpriteBatch spriteBatch;
	private SpriteBatch hudBatch;
	private OrthographicCamera camera;
	private OrthographicCamera hudCamera;
	private Viewport viewport;
	private Stage stage;
	private AssetManager assetManager;
	private AnimationManager animationManager;
	private BitmapFont fontBig;
	private BitmapFont fontNormal;
	private BitmapFont fontSmall;
	private GlyphLayout layout;

	// Network
	private TinyPartyClient client;

	private StartScreen startScreen;
	private GameScreen gameScreen;

	private Object lock = new Object();

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		hudBatch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, camera);
		hudCamera = new OrthographicCamera(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT);
		hudCamera.position.set(Constants.CAMERA_WIDTH/2, Constants.CAMERA_HEIGHT/2, 0);
		assetManager = new AssetManager();
		animationManager = new AnimationManager();

		client = new TinyPartyClient(this);

		CustomColor.reset();
		load();

		stage = new Stage(viewport, spriteBatch);
		Gdx.input.setInputProcessor(stage);

		// Define Font
		fontBig = assetManager.get(Asset.FONT_BIG.filename, BitmapFont.class);
		fontBig.getData().markupEnabled = true;
		fontNormal = assetManager.get(Asset.FONT_NORMAL.filename, BitmapFont.class);
		fontNormal.getData().markupEnabled = true;
		fontSmall = assetManager.get(Asset.FONT_SMALL.filename, BitmapFont.class);
		fontSmall.getData().markupEnabled = true;
		layout = new GlyphLayout();

		startScreen = new StartScreen(this);
		gameScreen = new GameScreen(this);
		this.setScreen(startScreen);
	}

	public void load() {
		assetManager.load(Asset.TEST.filename, Texture.class);
		assetManager.load(Asset.FONT_BIG.filename, BitmapFont.class);
		assetManager.load(Asset.FONT_NORMAL.filename, BitmapFont.class);
		assetManager.load(Asset.FONT_SMALL.filename, BitmapFont.class);
		assetManager.load(Asset.GROUND1.filename, Texture.class);
		assetManager.load(Asset.GROUND2.filename, Texture.class);
		assetManager.load(Asset.GROUND3.filename, Texture.class);
		assetManager.load(Asset.GROUND4.filename, Texture.class);
		assetManager.load(Asset.GROUND5.filename, Texture.class);
		assetManager.load(Asset.GROUND6.filename, Texture.class);
		assetManager.load(Asset.GROUND7.filename, Texture.class);
		assetManager.load(Asset.STANDARD_BULLET.filename, Texture.class);
		assetManager.load(Asset.RED_PLAYER.filename, Texture.class);
		assetManager.load(Asset.RED_PLAYER_2.filename, Texture.class);
		assetManager.load(Asset.RED_HEART.filename, Texture.class);
		assetManager.load(Asset.EMPTY_RED_HEART.filename, Texture.class);
		assetManager.load(Asset.BLUE_PLAYER.filename, Texture.class);
		assetManager.load(Asset.BLUE_PLAYER_2.filename, Texture.class);
		assetManager.load(Asset.BLUE_HEART.filename, Texture.class);
		assetManager.load(Asset.EMPTY_BLUE_HEART.filename, Texture.class);
		assetManager.load(Asset.GREEN_PLAYER.filename, Texture.class);
		assetManager.load(Asset.GREEN_PLAYER_2.filename, Texture.class);
		assetManager.load(Asset.GREEN_HEART.filename, Texture.class);
		assetManager.load(Asset.EMPTY_GREEN_HEART.filename, Texture.class);
		assetManager.load(Asset.BACKGROUND_UI_TOP.filename, Texture.class);
		assetManager.load(Asset.BACKGROUND_UI_BOTTOM.filename, Texture.class);
		assetManager.finishLoading();

		animationManager.load(Asset.RED_PLAYER.filename, assetManager.get(Asset.RED_PLAYER.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
		animationManager.load(Asset.RED_PLAYER_2.filename, assetManager.get(Asset.RED_PLAYER_2.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
		animationManager.load(Asset.BLUE_PLAYER.filename, assetManager.get(Asset.BLUE_PLAYER.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
		animationManager.load(Asset.BLUE_PLAYER_2.filename, assetManager.get(Asset.BLUE_PLAYER_2.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
		animationManager.load(Asset.GREEN_PLAYER.filename, assetManager.get(Asset.GREEN_PLAYER.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
		animationManager.load(Asset.GREEN_PLAYER_2.filename, assetManager.get(Asset.GREEN_PLAYER_2.filename), 0.120f, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);

		animationManager.load(Asset.RED_HEART.filename, assetManager.get(Asset.RED_HEART.filename), 0.120f, Constants.HEART_WIDTH, Constants.HEART_HEIGHT);
		animationManager.load(Asset.BLUE_HEART.filename, assetManager.get(Asset.BLUE_HEART.filename), 0.120f, Constants.HEART_WIDTH, Constants.HEART_HEIGHT);
		animationManager.load(Asset.GREEN_HEART.filename, assetManager.get(Asset.GREEN_HEART.filename), 0.120f, Constants.HEART_WIDTH, Constants.HEART_HEIGHT);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(CustomColor.BLACK.r, CustomColor.BLACK.g, CustomColor.BLACK.b, CustomColor.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());

		super.render();
		stage.draw();
	}

	@Override
	public void dispose () {
		stage.dispose();
		spriteBatch.dispose();
		assetManager.dispose();
		client.dispose();
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public SpriteBatch getHudBatch() {
		return hudBatch;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}

	public Stage getStage() {
		return stage;
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}

	public BitmapFont getFontBig() {
		return fontBig;
	}

	public BitmapFont getFontNormal() {
		return fontNormal;
	}

	public BitmapFont getFontSmall() {
		return fontSmall;
	}

	public GlyphLayout getLayout() {
		return layout;
	}

	public TinyPartyClient getClient() {
		return client;
	}

	public Object getLock() {
		return lock;
	}

	public StartScreen getStartScreen() {
		return startScreen;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}
}
