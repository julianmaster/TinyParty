package com.tinyparty.game;

import com.badlogic.gdx.ApplicationAdapter;
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
import com.tinyparty.game.view.Asset;
import com.tinyparty.game.view.CustomColor;
import com.tinyparty.game.view.GameScreen;
import com.tinyparty.game.view.StartScreen;

public class TinyParty extends Game {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Stage stage;
	private AssetManager assetManager;
	private BitmapFont fontBig;
	private BitmapFont fontNormal;
	private BitmapFont fontSmall;
	private GlyphLayout layout;

	// Network
	private TinyPartyClient client;

	private StartScreen startScreen;
	private GameScreen gameScreen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT, camera);
		assetManager = new AssetManager();

		client = new TinyPartyClient(this);

		CustomColor.reset();
		load();

		stage = new Stage(viewport, batch);
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
		assetManager.load(Asset.PLAYER.filename, Texture.class);
		assetManager.load(Asset.PLAYER_2.filename, Texture.class);
		assetManager.load(Asset.STANDARD_BULLET.filename, Texture.class);

		assetManager.finishLoading();
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
		batch.dispose();
		assetManager.dispose();
		client.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Stage getStage() {
		return stage;
	}

	public AssetManager getAssetManager() {
		return assetManager;
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

	public StartScreen getStartScreen() {
		return startScreen;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}
}
