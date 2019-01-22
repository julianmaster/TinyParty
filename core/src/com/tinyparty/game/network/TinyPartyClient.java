package com.tinyparty.game.network;

import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.network.json.server.*;

import java.util.concurrent.locks.ReentrantLock;

public class TinyPartyClient implements Disposable {

	private final TinyParty game;
	private final ReentrantLock lock = new ReentrantLock();
	private WebSocket socket;

	public TinyPartyClient(TinyParty game) {
		this.game = game;

		socket = ExtendedNet.getNet().newWebSocket(Constants.HOST, Constants.PORT);

		lock.lock();
		socket.addListener(getListener());
		socket.connect();
		lock.unlock();
	}

	private WebSocketListener getListener() {
		return new AbstractWebSocketListener() {
			@Override
			protected boolean onMessage(WebSocket webSocket, Object response) throws WebSocketException {
				try {
					game.getLock().lock();
					if(response instanceof ResponseJoinPartyJson) {
						ResponseJoinPartyJson responseJoinPartyJson = (ResponseJoinPartyJson)response;

						game.getGameScreen().init(responseJoinPartyJson.id, responseJoinPartyJson.playerColor, responseJoinPartyJson.position, responseJoinPartyJson.horizontalFlip);
						for(int i = 0; i < responseJoinPartyJson.otherIds.length; i++) {
							game.getGameScreen().addNewOtherPlayer(responseJoinPartyJson.otherIds[i], responseJoinPartyJson.otherPlayerColors[i], responseJoinPartyJson.otherPositions[i], responseJoinPartyJson.otherHorizontalFlips[i], responseJoinPartyJson.otherInvincibles[i]);
						}
						game.setScreen(game.getGameScreen());
					}
					else if(response instanceof ResponseNewOtherPlayerJson) {
						ResponseNewOtherPlayerJson responseNewOtherPlayerJson = (ResponseNewOtherPlayerJson)response;

						game.getGameScreen().addNewOtherPlayer(responseNewOtherPlayerJson.id, responseNewOtherPlayerJson.playerColor, responseNewOtherPlayerJson.position, responseNewOtherPlayerJson.horizontalFlip, responseNewOtherPlayerJson.invincible);
					}
					else if(response instanceof ResponsePositionPlayerJson) {
						ResponsePositionPlayerJson responsePositionPlayerJson = (ResponsePositionPlayerJson)response;

						game.getGameScreen().changeOtherPlayerPosition(responsePositionPlayerJson.id, responsePositionPlayerJson.position, responsePositionPlayerJson.horizontalFlip);
					}
					else if(response instanceof ResponsePlayerFireJson) {
						ResponsePlayerFireJson responsePlayerFireJson = (ResponsePlayerFireJson)response;

						game.getGameScreen().getBulletManager().fire(responsePlayerFireJson.idPlayer, false, responsePlayerFireJson.position, responsePlayerFireJson.worldClickCoords, responsePlayerFireJson.bulletSizeSpeedParameter, responsePlayerFireJson.bulletDistanceAmountParameter);
					}
					else if(response instanceof ResponsePlayerInvincibleJson) {
						ResponsePlayerInvincibleJson responsePlayerInvincibleJson = (ResponsePlayerInvincibleJson)response;

						game.getGameScreen().changeOtherPlayerInvincible(responsePlayerInvincibleJson.idPlayer, responsePlayerInvincibleJson.invincible);
					}
					else if(response instanceof ResponsePlayerDieJson) {
						ResponsePlayerDieJson responsePlayerDieJson = (ResponsePlayerDieJson)response;

						if(game.getGameScreen().getPlayer().getId() == responsePlayerDieJson.bulletIdPlayer) {
							game.getStartScreen().setKill(game.getStartScreen().getKill()+1);
						}
						game.getGameScreen().otherPlayerDie(responsePlayerDieJson.id);
					}
					else if(response instanceof ResponseQuitPlayerJson) {
						ResponseQuitPlayerJson responseQuitPlayerJson = (ResponseQuitPlayerJson)response;
						game.getGameScreen().removeOtherPlayer(responseQuitPlayerJson.id);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					game.getLock().unlock();
				}

				return FULLY_HANDLED;
			}
		};
	}

	public void send(Object packet) {
		lock.lock();
		if(socket.isOpen()) {
			socket.send(packet);
		}
		lock.unlock();
	}

	@Override
	public void dispose() {

	}
}
