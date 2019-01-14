package com.tinyparty.game.physic;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.Bullet;
import com.tinyparty.game.model.Player;
import com.tinyparty.game.network.json.client.RequestPlayerDieJson;
import com.tinyparty.game.network.json.client.RequestPlayerInvinsibleJson;
import com.tinyparty.game.view.GameScreen;
import com.tinyparty.game.view.StartScreen;

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

		game.getLock().lock();
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
		game.getLock().unlock();
	}

	private void bulletPlayerContact(Object bulletObject, Object playerObject) {
		Bullet bullet = (Bullet) bulletObject;
		Player player = (Player) playerObject;

		if(player.getLife() > 0 && !player.isInvinsible()) {
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
				RequestPlayerInvinsibleJson requestPlayerInvinsibleJson = new RequestPlayerInvinsibleJson();
				requestPlayerInvinsibleJson.idPlayer = player.getId();
				game.getClient().send(requestPlayerInvinsibleJson);
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
