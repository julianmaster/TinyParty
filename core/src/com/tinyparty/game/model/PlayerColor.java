package com.tinyparty.game.model;

import com.tinyparty.game.view.Asset;

public enum PlayerColor {
	RED(Asset.RED_PLAYER, Asset.RED_PLAYER_2, Asset.RED_HEART),
	BLUE(Asset.BLUE_PLAYER, Asset.BLUE_PLAYER_2, Asset.BLUE_HEART),
	GREEN(Asset.GREEN_PLAYER, Asset.GREEN_PLAYER_2, Asset.GREEN_HEART);

	public Asset player;
	public Asset player2;
	public Asset heart;

	PlayerColor(Asset player, Asset player2, Asset heart) {
		this.player = player;
		this.player2 = player2;
		this.heart = heart;
	}
}
