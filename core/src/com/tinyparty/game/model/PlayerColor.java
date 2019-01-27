package com.tinyparty.game.model;

import com.tinyparty.game.view.Asset;

public enum PlayerColor {
	RED(Asset.RED_PLAYER, Asset.RED_PLAYER_2, Asset.RED_HEART, Asset.EMPTY_RED_HEART),
	BLUE(Asset.BLUE_PLAYER, Asset.BLUE_PLAYER_2, Asset.BLUE_HEART, Asset.EMPTY_BLUE_HEART),
	GREEN(Asset.GREEN_PLAYER, Asset.GREEN_PLAYER_2, Asset.GREEN_HEART, Asset.EMPTY_GREEN_HEART);

	public Asset player;
	public Asset player2;
	public Asset heart;
	public Asset empty_heart;

	PlayerColor(Asset player, Asset player2, Asset heart, Asset empty_heart) {
		this.player = player;
		this.player2 = player2;
		this.heart = heart;
		this.empty_heart = empty_heart;
	}
}
