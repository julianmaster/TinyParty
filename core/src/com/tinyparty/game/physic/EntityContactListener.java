package com.tinyparty.game.physic;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.Bullet;
import com.tinyparty.game.model.Player;
import com.tinyparty.game.view.GameScreen;

public class EntityContactListener implements ContactListener {

	private final TinyParty game;

	public EntityContactListener(TinyParty game) {
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {
		Object objectA = contact.getFixtureA().getBody().getUserData();
		Object objectB = contact.getFixtureB().getBody().getUserData();

		GameScreen gameScreen = game.getGameScreen();

		gameScreen.getLock().lock();
		if(objectA instanceof Bullet) {
			// Bullet vs Player
			if(objectB instanceof Player) {
				System.out.println("coucou 1");
//				bulletPlayerContact(objectA, objectB);
			}
		}
		else if(objectB instanceof Bullet) {
			// Bullet vs Player
			if(objectA instanceof Player) {
				System.out.println("coucou 2");
//				bulletPlayerContact(objectB, objectA);
			}
		}
		gameScreen.getLock().unlock();
	}

//	private void bulletPillarArenaContact(Object bulletObject) {
//		Bullet bullet = (Bullet) bulletObject;
//		if (bullet.isMove()) {
//			bullet.drop();
//		}
//	}

//	private void bulletPlayerContact(Object bulletObject, Object playerObject) {
//		Bullet bullet = (Bullet) bulletObject;
//		Player player = (Player) playerObject;
//		if(bullet.isDropped() && player.getBullet() == null) {
//			bullet.pickUp();
//			player.pickUp(bullet);
//
//			RequestPickUpBulletJson requestPickUpBulletJson = new RequestPickUpBulletJson();
//			requestPickUpBulletJson.color = bullet.getColor();
//			game.getClient().send(requestPickUpBulletJson);
//		}
//		else if(!bullet.isDropped() && !bullet.isPlayerFire()) {
//			player.setLife(player.getLife()-1);
//			if(player.getLife() == 0) {
//				player.die();
//
//				RequestPlayerDieJson requestPlayerDieJson = new RequestPlayerDieJson();
//				requestPlayerDieJson.color = player.getColor();
//				game.getClient().send(requestPlayerDieJson);
//			}
//
//			bullet.drop();
//
//			RequestBulletTouchPlayerJson requestBulletTouchPlayerJson = new RequestBulletTouchPlayerJson();
//			requestBulletTouchPlayerJson.color = bullet.getColor();
//			requestBulletTouchPlayerJson.position = bullet.getPosition();
//			game.getClient().send(requestBulletTouchPlayerJson);
//		}
//	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
