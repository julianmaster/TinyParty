package com.tinyparty.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;
import com.tinyparty.game.network.json.client.RequestJoinPartyJson;
import com.tinyparty.game.network.json.client.RequestPlayerDieJson;
import com.tinyparty.game.network.json.client.RequestPlayerFireJson;
import com.tinyparty.game.network.json.client.RequestPositionPlayerJson;
import com.tinyparty.game.network.json.server.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class TinyPartyServer {

	private final Vertx vertx = Vertx.vertx();
	private final Serializer serializer = new JsonSerializer();
	private final ReentrantLock lock = new ReentrantLock();

//	private List<ServerWebSocket> webSockets = new ArrayList<>();

	private Map<Integer, MutablePair<ServerWebSocket, Vector2>> players = new HashMap<>();

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
		if(request instanceof RequestJoinPartyJson) {
			RequestJoinPartyJson requestJoinPartyJson = (RequestJoinPartyJson)request;

			// The id of player;
			int id = -1;

			boolean exist = false;
			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getValue().left == webSocket) {
					exist = true;
					id = player.getKey();
					break;
				}
			}

			if(!exist) {
				// Create new Id for new player
				do {
					id = MathUtils.random(100000);
				} while(players.containsKey(id));
			}

			if(id == -1) {
				System.err.println("Erreur de l'ID du joueur: "+id);
				return;
			}

			// Search informations of others players
			int[] otherIds = new int[players.size()];
			Vector2[] otherPositions = new Vector2[players.size()];
			int index = 0;
			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getKey() != id) {
					otherIds[index] = player.getKey();
					otherPositions[index] = player.getValue().right;
				}
			}

			// Adding new player
			// TODO fix the init player position
			Vector2 position = new Vector2(MathUtils.random()*100f, MathUtils.random()*100f);
			players.put(id, new MutablePair<>(webSocket, position));

			ResponseJoinPartyJson responseJoinPartyJson = new ResponseJoinPartyJson();
			responseJoinPartyJson.id = id;
			responseJoinPartyJson.position = position;
			responseJoinPartyJson.otherIds = otherIds;
			responseJoinPartyJson.otherPositions = otherPositions;
			webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseJoinPartyJson)));

			// Send new other player informations
			ResponseNewOtherPlayerJson responseNewOtherPlayerJson = new ResponseNewOtherPlayerJson();
			responseNewOtherPlayerJson.id = id;
			responseNewOtherPlayerJson.position = responseJoinPartyJson.position;
			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getKey() != id) {
					player.getValue().left.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseNewOtherPlayerJson)));
				}
			}
		}
		else if(request instanceof RequestPositionPlayerJson) {
			RequestPositionPlayerJson requestPositionPlayerJson = (RequestPositionPlayerJson)request;

			ResponsePositionPlayerJson responsePositionPlayerJson = new ResponsePositionPlayerJson();
			responsePositionPlayerJson.id = requestPositionPlayerJson.id;
			responsePositionPlayerJson.position = requestPositionPlayerJson.position;

			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getKey() != requestPositionPlayerJson.id) {
					player.getValue().left.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePositionPlayerJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerFireJson) {
			RequestPlayerFireJson requestPlayerFireJson = (RequestPlayerFireJson)request;

			ResponsePlayerFireJson responsePlayerFireJson = new ResponsePlayerFireJson();
			responsePlayerFireJson.idPlayer = requestPlayerFireJson.idPlayer;
			responsePlayerFireJson.position = requestPlayerFireJson.position;
			responsePlayerFireJson.worldClickCoords = requestPlayerFireJson.worldClickCoords;
			responsePlayerFireJson.bulletSizeSpeedParameter = requestPlayerFireJson.bulletSizeSpeedParameter;
			responsePlayerFireJson.bulletDistanceAmountParameter = requestPlayerFireJson.bulletDistanceAmountParameter;

			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getKey() != requestPlayerFireJson.idPlayer) {
					player.getValue().left.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerFireJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerDieJson) {
			RequestPlayerDieJson requestPlayerDieJson = (RequestPlayerDieJson)request;

			ResponsePlayerDieJson responsePlayerDieJson = new ResponsePlayerDieJson();
			responsePlayerDieJson.id = requestPlayerDieJson.id;
			responsePlayerDieJson.bulletIdPlayer = requestPlayerDieJson.bulletIdPlayer;

			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				if(player.getKey() != requestPlayerDieJson.id) {
					player.getValue().left.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerDieJson)));
				}
			}
		}
		lock.unlock();
	}

	private void handleSocketClosed(final ServerWebSocket webSocket, final Void frame) {
		lock.lock();

		Integer playerId = -1;

		for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
			if(webSocket == player.getValue().left) {
				playerId = player.getKey();
			}
		}

		if(playerId != -1) {
			players.remove(playerId);
			ResponseQuitPlayerJson responseQuitPlayerJson = new ResponseQuitPlayerJson();
			responseQuitPlayerJson.id = playerId;
			for(Map.Entry<Integer, MutablePair<ServerWebSocket, Vector2>> player : players.entrySet()) {
				player.getValue().left.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseQuitPlayerJson)));
			}
		}

		lock.unlock();
	}

	public static void main (String[] arg) {
		new TinyPartyServer().launch();
	}
}
