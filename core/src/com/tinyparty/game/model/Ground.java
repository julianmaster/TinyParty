package com.tinyparty.game.model;

import com.badlogic.gdx.math.MathUtils;
import com.tinyparty.game.view.Asset;

import java.util.ArrayList;
import java.util.List;

public class Ground {

	private final static List<Asset> grounds = new ArrayList<Asset>(){{
		add(Asset.GROUND1);
		add(Asset.GROUND2);
		add(Asset.GROUND3);
		add(Asset.GROUND4);
		add(Asset.GROUND5);
		add(Asset.GROUND6);
		add(Asset.GROUND7);
	}};

	private Asset[][] assets;

	public Ground() {
		assets = new Asset[15][15];

		for(int i = 0; i < assets.length; i++) {
			for (int j = 0; j < assets[i].length; j++) {
				assets[i][j] = grounds.get(MathUtils.random(grounds.size()-1));
			}
		}
	}

	public Asset[][] getAssets() {
		return assets;
	}
}
