package com.tinyparty.game.view;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.network.json.client.RequestJoinPartyJson;

import java.util.LinkedList;
import java.util.List;

public class StartScreen extends ScreenAdapter {

	private final TinyParty game;
	private int kill = 0;
	private int death = 0;

	private TextActionButton goButton;

	private final static List<String> colors = new LinkedList<String>() {{
		add("BLUE2");
		add("PINK2");
		add("BROWN3");
		add("YELLOW2");
		add("ORANGE1");
		add("PURPLE1");
		add("GREEN2");
		add("RED2");
	}};

	private int colorIndex = 0;
	private float timerColor = 0.1f;

	public StartScreen(TinyParty game) {
		this.game = game;
	}

	@Override
	public void show() {
		GlyphLayout layout = game.getLayout();

		goButton = new TextActionButton("Go !", game.getFontBig(), game);
		layout.setText(game.getFontBig(), "Go !");
		goButton.setPosition((Constants.CAMERA_WIDTH - layout.width)/2, Constants.CAMERA_WIDTH*2/6 - layout.height/2);
		goButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.getClient().send(new RequestJoinPartyJson());
				return false;
			}
		});
		game.getStage().addActor(goButton);
	}

	@Override
	public void render(float delta) {
		Batch batch = game.getBatch();
		Camera camera = game.getCamera();
		BitmapFont fontBig = game.getFontBig();
		BitmapFont fontNormal = game.getFontNormal();
		BitmapFont fontSmall = game.getFontSmall();
		GlyphLayout layout = game.getLayout();

		timerColor -= delta;
		if(timerColor <= 0f) {
			timerColor = 0.1f;
			colorIndex++;
			colorIndex %= colors.size();
		}

		game.getLock().lock();
		System.out.println("StartScreen");

		camera.position.set(0 + Constants.CAMERA_WIDTH/2f,0 + Constants.CAMERA_HEIGHT/2f,0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		String finalText = "";
		int i = colorIndex;
		for(char c : "tinyparty".toCharArray()) {
			finalText += "["+colors.get(i)+"]"+c;
			i++;
			i %= colors.size();
		}
		layout.setText(fontBig, finalText);
		fontBig.draw(batch, layout, (Constants.CAMERA_WIDTH - layout.width)/2, Constants.CAMERA_WIDTH*4/5 + layout.height/2);

		float ratio;
		if(death < 1) {
			ratio = kill;
		}
		else {
			ratio = (float)kill/(float)death;
		}

		finalText = "[GRIS1]RATIO: [YELLOW1]"+round(ratio, 2);
		layout.setText(fontNormal, finalText);
		fontNormal.draw(batch, layout, (Constants.CAMERA_WIDTH - layout.width)/2, Constants.CAMERA_WIDTH*3/5 + layout.height/2);
		finalText = " [GRIS1]K: [GREEN2]"+kill+"   [GRIS1]/D: [RED2]"+death+"[GRIS1]";
		layout.setText(fontNormal, finalText);
		fontNormal.draw(batch, layout, (Constants.CAMERA_WIDTH - layout.width)/2, Constants.CAMERA_WIDTH*3/5 + layout.height/2 - layout.height - 4);

		batch.end();

		game.getLock().unlock();
	}

	@Override
	public void hide() {
		goButton.remove();
	}

	@Override
	public void dispose() {
	}

	public static float round(float number, int scale) {
		int pow = 10;
		for (int i = 1; i < scale; i++)
			pow *= 10;
		float tmp = number * pow;
		return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}

	public int getKill() {
		return kill;
	}

	public void setKill(int kill) {
		this.kill = kill;
	}

	public int getDeath() {
		return death;
	}

	public void setDeath(int death) {
		this.death = death;
	}
}
