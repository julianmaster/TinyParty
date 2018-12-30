package com.tinyparty.game.physic;

import com.badlogic.gdx.physics.box2d.*;

public class PhysicManager {

	public static Body createBox(float x, float y, float width, float height, float angle, short categoryBits, int maskBits, boolean isStatic, boolean isBullet, boolean isSensor, Object object, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = isStatic ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.angle = angle;
		bodyDef.fixedRotation = true;
		bodyDef.bullet = isBullet;

		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2f, height / 2f);

		FixtureDef fixture = new FixtureDef();
		fixture.shape = shape;
		fixture.density = 1.0f;
		fixture.isSensor = isSensor;
		fixture.filter.categoryBits = categoryBits;
		fixture.filter.maskBits = (short)maskBits;

		body.createFixture(fixture);

		shape.dispose();

		body.setUserData(object);

		return body;
	}
}
