package com.tinyparty.game.model.parameter;

public enum BulletSizeSpeedParameter {
	FAST(1, 160f),
	SLOW(2, 60f);

	public int size;
	public float speed;

	BulletSizeSpeedParameter(int size, float speed) {
		this.size = size;
		this.speed = speed;
	}
}
