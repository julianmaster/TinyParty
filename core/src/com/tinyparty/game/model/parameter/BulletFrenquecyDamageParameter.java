package com.tinyparty.game.model.parameter;

public enum BulletFrenquecyDamageParameter {
	HIGH(0.2f, 1),
	LOW(1f, 2);

	public float frenquecy;
	public int damage;

	BulletFrenquecyDamageParameter(float frenquecy, int damage) {
		this.frenquecy = frenquecy;
		this.damage = damage;
	}
}
