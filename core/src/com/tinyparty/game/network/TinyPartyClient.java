package com.tinyparty.game.network;

import com.badlogic.gdx.utils.Disposable;
import com.github.czyzby.websocket.AbstractWebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.net.ExtendedNet;
import com.tinyparty.game.Constants;
import com.tinyparty.game.TinyParty;
import com.tinyparty.game.network.json.server.ResponseJoinPartyJson;

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

					// TODO change to gameScreen and init info from response
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
