package com.tinyparty.game.physic;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.Bullet;
import com.tinyparty.game.model.Player;
import com.tinyparty.game.shared.RequestPlayerDieJson;
import com.tinyparty.game.shared.RequestPlayerInvincibleJson;

public class EntityContactListener implements ContactListener {

	private final TinyParty game;

	public EntityContactListener(TinyParty game) {
		this.game = game;
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void beginContact(Contact contact) {
		Object objectA = contact.getFixtureA().getBody().getUserData();
		Object objectB = contact.getFixtureB().getBody().getUserData();

		synchronized (game.getLock()) {
			if(objectA instanceof Bullet) {
				// Bullet vs Player
				if(objectB instanceof Player) {
					bulletPlayerContact(objectA, objectB);
				}
			}
			else if(objectB instanceof Bullet) {
				// Bullet vs Player
				if(objectA instanceof Player) {
					bulletPlayerContact(objectB, objectA);
				}
			}
		}
	}

	private void bulletPlayerContact(Object bulletObject, Object playerObject) {
		Bullet bullet = (Bullet) bulletObject;
		Player player = (Player) playerObject;

		if(player.getLife() > 0 && !player.isInvincible()) {
			player.touched();

			if(player.getLife() == 0) {
				player.die();

				RequestPlayerDieJson requestPlayerDieJson = new RequestPlayerDieJson();
				requestPlayerDieJson.id = player.getId();
				requestPlayerDieJson.bulletIdPlayer = bullet.getPlayerId();
				game.getClient().send(requestPlayerDieJson);

				game.getStartScreen().setDeath(game.getStartScreen().getDeath()+1);
				game.getGameScreen().setLoose(true);
			}
			else {
				RequestPlayerInvincibleJson requestPlayerInvincibleJson = new RequestPlayerInvincibleJson();
				requestPlayerInvincibleJson.idPlayer = player.getId();
				requestPlayerInvincibleJson.invincible = true;
				game.getClient().send(requestPlayerInvincibleJson);
			}
		}
	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
