package com.tinyparty.game;

import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TinyPartyServer {

	private final Vertx vertx = Vertx.vertx();
	private final Serializer serializer = new JsonSerializer();
	private final ReentrantLock lock = new ReentrantLock();

	private List<ServerWebSocket> webSockets = new ArrayList<>();

	private void launch() {
		System.out.println("Launching web socket server...");
		final HttpServer server = vertx.createHttpServer();
		server.websocketHandler(webSocket -> {
			webSocket.frameHandler(frame -> handleFrame(webSocket, frame));
			webSocket.endHandler(frame -> handleSocketClosed(webSocket, frame));
		}).listen(Constants.PORT);
		System.out.println("Go!");
	}

	private void handleFrame(final ServerWebSocket webSocket, final WebSocketFrame frame) {
		final Object request = serializer.deserialize(frame.binaryData().getBytes());

		lock.lock();
//		if(request instanceof RequestJoinPartyJson) {
//			RequestJoinPartyJson requestJoinPartyJson = (RequestJoinPartyJson)request;
//			Party party = parties.get(requestJoinPartyJson.party);
//
//			boolean playerAdded = party != null && party.addPlayer(webSocket);
//			System.out.println("join: "+playerAdded);
//			if(playerAdded) {
//				sendResponsePlayerStatusPartyJson(party);
//				ResponsePartyStateJson responsePartyStateJson = new ResponsePartyStateJson();
//				responsePartyStateJson.state = party.getState();
//				webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePartyStateJson)));
//			}
//			else {
//				ResponsePlayerStatusPartyJson responsePlayerStatusPartyJson = new ResponsePlayerStatusPartyJson();
//				responsePlayerStatusPartyJson.join = false;
//				webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerStatusPartyJson)));
//			}
//		}
		
		lock.unlock();
	}

	private void handleSocketClosed(final ServerWebSocket webSocket, final Void frame) {
		lock.lock();
		webSockets.remove(webSocket);
//		List<Integer> nums = new ArrayList<>();
//		for(Map.Entry<Integer, Party> party : parties.entrySet()) {
//			party.getValue().removePlayer(webSocket);
//			if(party.getValue().getPlayers().isEmpty()) {
//				nums.add(party.getKey());
//			}
//		}
//		for(Integer num : nums) {
//			parties.remove(num);
//		}

		// TODO manage player quit during party
		lock.unlock();
	}

	public static void main (String[] arg) {
		new TinyPartyServer().launch();
	}
}
