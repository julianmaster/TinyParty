package com.tinyparty.game.model.parameter;

public enum BulletDistanceAmountParameter {
	HIGH(3f, 1),
	MEDIUM(1.5f, 3),
	LOW(0.6f, 7);

	public float distance;
	public int amount;

	BulletDistanceAmountParameter(float distance, int amount) {
		this.distance = distance;
		this.amount = amount;
	}
}
