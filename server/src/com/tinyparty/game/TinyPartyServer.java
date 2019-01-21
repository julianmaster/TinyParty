package com.tinyparty.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;
import com.tinyparty.game.model.PlayerColor;
import com.tinyparty.game.network.json.client.*;
import com.tinyparty.game.network.json.server.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class TinyPartyServer {

	private final Vertx vertx = Vertx.vertx();
	private final Serializer serializer = new JsonSerializer();
	private final ReentrantLock lock = new ReentrantLock();

	private Map<Integer, Data> players = new HashMap<>();

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
			int playerSize = players.size();

			boolean exist = false;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getValue().webSocket == webSocket) {
					exist = true;
					id = player.getKey();
					playerSize--;
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

			PlayerColor playerColor = PlayerColor.values()[MathUtils.random(PlayerColor.values().length-1)];

			// Search informations of others players
			int[] otherIds = new int[playerSize];
			PlayerColor[] otherPlayerColors = new PlayerColor[playerSize];
			Vector2[] otherPositions = new Vector2[playerSize];
			boolean[] otherHorizontalFlips = new boolean[playerSize];
			int index = 0;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != id) {
					otherIds[index] = player.getKey();
					otherPlayerColors[index] = player.getValue().playerColor;
					otherPositions[index] = player.getValue().position;
					otherHorizontalFlips[index] = player.getValue().horizontalFlip;
				}
			}

			// Adding new player
			Vector2 position = new Vector2(MathUtils.random()*100f, MathUtils.random()*100f); // TODO fix player start location
			if(!exist) {
				Data data = new Data();
				data.webSocket = webSocket;
				data.playerColor = playerColor;
				data.position = position;
				data.horizontalFlip = false;
				players.put(id, data);
			}
			else {
				players.get(id).position = position;
				players.get(id).horizontalFlip = false;
			}

			ResponseJoinPartyJson responseJoinPartyJson = new ResponseJoinPartyJson();
			responseJoinPartyJson.id = id;
			responseJoinPartyJson.playerColor = playerColor;
			responseJoinPartyJson.position = position;
			responseJoinPartyJson.horizontalFlip = false;
			responseJoinPartyJson.otherIds = otherIds;
			responseJoinPartyJson.otherPlayerColors = otherPlayerColors;
			responseJoinPartyJson.otherPositions = otherPositions;
			responseJoinPartyJson.otherHorizontalFlips = otherHorizontalFlips;
			webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseJoinPartyJson)));

			// Send new other player informations
			ResponseNewOtherPlayerJson responseNewOtherPlayerJson = new ResponseNewOtherPlayerJson();
			responseNewOtherPlayerJson.id = id;
			responseNewOtherPlayerJson.playerColor = playerColor;
			responseNewOtherPlayerJson.position = responseJoinPartyJson.position;
			responseNewOtherPlayerJson.horizontalFlip = false;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != id) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseNewOtherPlayerJson)));
				}
			}
		}
		else if(request instanceof RequestPositionPlayerJson) {
			RequestPositionPlayerJson requestPositionPlayerJson = (RequestPositionPlayerJson)request;

			players.get(requestPositionPlayerJson.id).position = requestPositionPlayerJson.position;
			players.get(requestPositionPlayerJson.id).horizontalFlip = requestPositionPlayerJson.horizontalFlip;

			ResponsePositionPlayerJson responsePositionPlayerJson = new ResponsePositionPlayerJson();
			responsePositionPlayerJson.id = requestPositionPlayerJson.id;
			responsePositionPlayerJson.position = requestPositionPlayerJson.position;
			responsePositionPlayerJson.horizontalFlip = requestPositionPlayerJson.horizontalFlip;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPositionPlayerJson.id) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePositionPlayerJson)));
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

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerFireJson.idPlayer) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerFireJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerInvinsibleJson) {
			RequestPlayerInvinsibleJson requestPlayerInvinsibleJson = (RequestPlayerInvinsibleJson)request;

			ResponsePlayerInvinsibleJson responsePlayerInvinsibleJson = new ResponsePlayerInvinsibleJson();
			responsePlayerInvinsibleJson.idPlayer = requestPlayerInvinsibleJson.idPlayer;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerInvinsibleJson.idPlayer) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerInvinsibleJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerDieJson) {
			RequestPlayerDieJson requestPlayerDieJson = (RequestPlayerDieJson)request;

			ResponsePlayerDieJson responsePlayerDieJson = new ResponsePlayerDieJson();
			responsePlayerDieJson.id = requestPlayerDieJson.id;
			responsePlayerDieJson.bulletIdPlayer = requestPlayerDieJson.bulletIdPlayer;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerDieJson.id) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerDieJson)));
				}
			}
		}
		lock.unlock();
	}

	private void handleSocketClosed(final ServerWebSocket webSocket, final Void frame) {
		lock.lock();

		Integer playerId = -1;

		for(Map.Entry<Integer, Data> player : players.entrySet()) {
			if(webSocket == player.getValue().webSocket) {
				playerId = player.getKey();
			}
		}

		if(playerId != -1) {
			players.remove(playerId);
			ResponseQuitPlayerJson responseQuitPlayerJson = new ResponseQuitPlayerJson();
			responseQuitPlayerJson.id = playerId;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseQuitPlayerJson)));
			}
		}

		lock.unlock();
	}

	public static void main (String[] arg) {
		new TinyPartyServer().launch();
	}
}
