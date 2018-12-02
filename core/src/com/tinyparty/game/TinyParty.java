package com.tinyparty.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tinyparty.game.network.TinyPartyClient;
import com.tinyparty.game.view.Asset;
import com.tinyparty.game.view.CustomColor;
import com.tinyparty.game.view.GameScreen;

public class TinyParty extends Game {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Stage stage;
	private AssetManager assetManager;

	// Network
	private TinyPartyClient client;

	private GameScreen gameScreen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		assetManager = new AssetManager();

		client = new TinyPartyClient(this);

		CustomColor.reset();
		load();

		stage = new Stage(viewport, batch);
		Gdx.input.setInputProcessor(stage);

		gameScreen = new GameScreen(this);
		this.setScreen(gameScreen);
	}

	public void load() {
		assetManager.load(Asset.TEST.filename, Texture.class);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(CustomColor.BLUE2.r, CustomColor.BLUE2.g, CustomColor.BLUE2.b, CustomColor.BLUE2.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());
		camera.update();

		batch.setProjectionMatrix(camera.combined);

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

	public GameScreen getGameScreen() {
		return gameScreen;
	}
}
