package com.tinyparty.game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.network.json.server.*;

public class TinyPartyClient implements Disposable {

	private final TinyParty game;
	private final Object lock = new Object();
	private WebSocket socket;

	public TinyPartyClient(TinyParty game) {
		this.game = game;

		socket = ExtendedNet.getNet().newWebSocket(Constants.HOST, Constants.PORT);

		synchronized (lock) {
			socket.addListener(getListener());
			Gdx.app.log("Server", "connect...");
			socket.connect();
			Gdx.app.log("Server", "connected");
		}
	}

	private WebSocketListener getListener() {
		return new AbstractWebSocketListener() {
			@Override
			protected boolean onMessage(WebSocket webSocket, Object response) throws WebSocketException {
				try {
					synchronized (game.getLock()) {
						if(response instanceof ResponseJoinPartyJson) {
							ResponseJoinPartyJson responseJoinPartyJson = (ResponseJoinPartyJson)response;

							game.getGameScreen().init(responseJoinPartyJson.id, responseJoinPartyJson.playerColor, responseJoinPartyJson.position, responseJoinPartyJson.horizontalFlip);
							for(int i = 0; i < responseJoinPartyJson.otherIds.length; i++) {
								game.getGameScreen().addNewOtherPlayer(responseJoinPartyJson.otherIds[i], responseJoinPartyJson.otherPlayerColors[i], responseJoinPartyJson.otherPositions[i], responseJoinPartyJson.otherHorizontalFlips[i], responseJoinPartyJson.otherInvincibles[i]);
							}
							Gdx.app.log("Client", "Join Party");
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
								if(game.getGameScreen().getPlayer() != null && game.getGameScreen().getPlayer().getLife() < 3) {
									game.getGameScreen().getPlayer().setLife(game.getGameScreen().getPlayer().getLife()+1);
								}
							}
							game.getGameScreen().otherPlayerDie(responsePlayerDieJson.id);
						}
						else if(response instanceof ResponseQuitPlayerJson) {
							ResponseQuitPlayerJson responseQuitPlayerJson = (ResponseQuitPlayerJson)response;
							game.getGameScreen().removeOtherPlayer(responseQuitPlayerJson.id);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				return FULLY_HANDLED;
			}
		};
	}

	public void send(Object packet) {
		synchronized (lock) {
			if(socket.isOpen()) {
				socket.send(packet);
			}
		}
	}

	@Override
	public void dispose() {

	}
}
