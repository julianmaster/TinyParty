package com.tinyparty.game.model.parameter;

public enum BulletSizeSpeedParameter {
	FAST(1, 180f),
	SLOW(3, 80f),
	STATIC(6, 0f);

	public int size;
	public float speed;

	BulletSizeSpeedParameter(int size, float speed) {
		this.size = size;
		this.speed = speed;
	}
}
