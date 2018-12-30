package com.tinyparty.game.network;

import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.model.Player;
import com.tinyparty.game.network.json.server.ResponseJoinPartyJson;
import com.tinyparty.game.network.json.server.ResponseNewOtherPlayerJson;
import com.tinyparty.game.network.json.server.ResponsePlayerFireJson;
import com.tinyparty.game.network.json.server.ResponsePositionPlayerJson;

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
			protected boolean onMessage(com.github.czyzby.websocket.WebSocket webSocket, Object response) throws WebSocketException {
				if(response instanceof ResponseJoinPartyJson) {
					ResponseJoinPartyJson responseJoinPartyJson = (ResponseJoinPartyJson)response;

					game.getStartScreen().getLock().lock();
					game.getGameScreen().getLock().lock();

					game.getGameScreen().init(responseJoinPartyJson.id, responseJoinPartyJson.position);
					for(int i = 0; i < responseJoinPartyJson.otherIds.length; i++) {
						game.getGameScreen().addNewOtherPlayer(responseJoinPartyJson.otherIds[i], responseJoinPartyJson.otherPositions[i]);
					}
					game.setScreen(game.getGameScreen());

					game.getGameScreen().getLock().unlock();
					game.getStartScreen().getLock().unlock();
				}
				else if(response instanceof ResponseNewOtherPlayerJson) {
					ResponseNewOtherPlayerJson responseNewOtherPlayerJson = (ResponseNewOtherPlayerJson)response;

					game.getGameScreen().getLock().lock();
					game.getGameScreen().addNewOtherPlayer(responseNewOtherPlayerJson.id, responseNewOtherPlayerJson.position);
					game.getGameScreen().getLock().unlock();
				}
				else if(response instanceof ResponsePositionPlayerJson) {
					ResponsePositionPlayerJson responsePositionPlayerJson = (ResponsePositionPlayerJson)response;

					game.getGameScreen().getLock().lock();
					game.getGameScreen().changeOtherPlayerPosition(responsePositionPlayerJson.id, responsePositionPlayerJson.position);
					game.getGameScreen().getLock().unlock();
				}
				else if(response instanceof ResponsePlayerFireJson) {
					ResponsePlayerFireJson responsePlayerFireJson = (ResponsePlayerFireJson)response;

					game.getGameScreen().getLock().lock();
					game.getGameScreen().getBulletManager().fire(responsePlayerFireJson.idPlayer, false, responsePlayerFireJson.position, responsePlayerFireJson.worldClickCoords, responsePlayerFireJson.bulletSizeSpeedParameter, responsePlayerFireJson.bulletDistanceAmountParameter, responsePlayerFireJson.bulletFrenquecyDamageParameter);
					game.getGameScreen().getLock().unlock();
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
