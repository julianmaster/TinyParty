package com.tinyparty.game.model.parameter;

public enum BulletDistanceAmountParameter {
	HIGH(6f, 1),
	MEDIUM(4f, 3),
	LOW(2f, 8);

	public float distance;
	public int amount;

	BulletDistanceAmountParameter(float distance, int amount) {
		this.distance = distance;
		this.amount = amount;
	}
}
