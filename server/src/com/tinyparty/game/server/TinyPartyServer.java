package com.tinyparty.game.server;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.czyzby.websocket.serialization.Serializer;
import com.github.czyzby.websocket.serialization.impl.JsonSerializer;
import com.tinyparty.game.Constants;
import com.tinyparty.game.model.PlayerColor;
import com.tinyparty.game.shared.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemKeyCertOptions;

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

		HttpServerOptions options = new HttpServerOptions();
		options.setUseAlpn(true);
		options.setSsl(true);
		options.setPemKeyCertOptions(new PemKeyCertOptions().setKeyPath("/path/to/keyfile").setCertPath("/path/to/Certfile"));

		final HttpServer server = vertx.createHttpServer(options);
		server.websocketHandler(webSocket -> {
			webSocket.frameHandler(frame -> handleFrame(webSocket, frame));
			webSocket.endHandler(frame -> handleSocketClosed(webSocket, frame));
		}).listen(Constants.PORT);
		System.out.println("Go!");
	}

	private void handleFrame(final ServerWebSocket webSocket, final WebSocketFrame frame) {
		// Deserializing received message
		final Object request = serializer.deserialize(frame.binaryData().getBytes());

		lock.lock();
		if(request instanceof RequestJoinPartyJson) {
			RequestJoinPartyJson requestJoinPartyJson = (RequestJoinPartyJson)request;

			// The idPlayer of player;
			int id = -1;
			PlayerColor playerColor = null;
			int playerSize = players.size();

			boolean exist = false;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getValue().webSocket == webSocket) {
					exist = true;
					id = player.getKey();
					playerColor = player.getValue().playerColor;
					playerSize--;
					break;
				}
			}

			if(!exist) {
				// Create new Id for new player
				do {
					id = MathUtils.random(100000);
				} while(players.containsKey(id));
				playerColor = PlayerColor.values()[MathUtils.random(PlayerColor.values().length-1)];
			}

			if(id == -1) {
				System.err.println("Erreur de l'ID du joueur: "+id);
				return;
			}

			// Search informations of others players
			int[] otherIds = new int[playerSize];
			String[] otherPlayerColors = new String[playerSize];
			float[] otherPositionsX = new float[playerSize];
			float[] otherPositionsY = new float[playerSize];
			boolean[] otherHorizontalFlips = new boolean[playerSize];
			boolean[] otherInvincibles = new boolean[playerSize];
			int index = 0;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != id) {
					otherIds[index] = player.getKey();
					otherPlayerColors[index] = player.getValue().playerColor.name();
					otherPositionsX[index] = player.getValue().position.x;
					otherPositionsY[index] = player.getValue().position.y;
					otherHorizontalFlips[index] = player.getValue().horizontalFlip;
					otherInvincibles[index] = player.getValue().invincible;
				}
			}

			// Adding new player
			Vector2 position = new Vector2(MathUtils.random()*15*16, MathUtils.random()*15*16);
			if(!exist) {
				Data data = new Data();
				data.webSocket = webSocket;
				data.playerColor = playerColor;
				data.ready = false;
				data.position = position;
				data.horizontalFlip = false;
				data.invincible = true;
				players.put(id, data);
			}
			else {
				players.get(id).position = position;
				players.get(id).horizontalFlip = false;
				players.get(id).invincible = true;
			}

			ResponseJoinPartyJson responseJoinPartyJson = new ResponseJoinPartyJson();
			responseJoinPartyJson.id = id;
			responseJoinPartyJson.playerColor = playerColor.name();
			responseJoinPartyJson.x = position.x;
			responseJoinPartyJson.y = position.y;
			responseJoinPartyJson.horizontalFlip = false;
			responseJoinPartyJson.otherIds = otherIds;
			responseJoinPartyJson.otherPlayerColors = otherPlayerColors;
			responseJoinPartyJson.otherPositionsX = otherPositionsX;
			responseJoinPartyJson.otherPositionsY = otherPositionsY;
			responseJoinPartyJson.otherHorizontalFlips = otherHorizontalFlips;
			responseJoinPartyJson.otherInvincibles = otherInvincibles;
			webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseJoinPartyJson)));

			// Send new other player informations
			ResponseNewOtherPlayerJson responseNewOtherPlayerJson = new ResponseNewOtherPlayerJson();
			responseNewOtherPlayerJson.id = id;
			responseNewOtherPlayerJson.playerColor = playerColor.name();
			responseNewOtherPlayerJson.x = responseJoinPartyJson.x;
			responseNewOtherPlayerJson.y = responseJoinPartyJson.y;
			responseNewOtherPlayerJson.horizontalFlip = false;
			responseNewOtherPlayerJson.invincible = true;
			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != id && player.getValue().ready) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseNewOtherPlayerJson)));
				}
			}
		}
		else if(request instanceof RequestInfoOtherPlayerJson) {
			RequestInfoOtherPlayerJson requestInfoOtherPlayerJson = (RequestInfoOtherPlayerJson)request;

			Data data = players.get(requestInfoOtherPlayerJson);

			if(data != null) {
				ResponseNewOtherPlayerJson responseNewOtherPlayerJson = new ResponseNewOtherPlayerJson();
				responseNewOtherPlayerJson.id = requestInfoOtherPlayerJson.otherPlayer;
				responseNewOtherPlayerJson.playerColor = data.playerColor.name();
				responseNewOtherPlayerJson.x = data.position.x;
				responseNewOtherPlayerJson.y = data.position.y;
				responseNewOtherPlayerJson.horizontalFlip = data.horizontalFlip;
				responseNewOtherPlayerJson.invincible = data.invincible;

				players.get(requestInfoOtherPlayerJson.idPlayer).webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responseNewOtherPlayerJson)));
			}
		}
		else if(request instanceof RequestPlayerReadyJson) {
			RequestPlayerReadyJson requestPlayerReadyJson = (RequestPlayerReadyJson)request;

			players.get(requestPlayerReadyJson.idPlayer).ready = true;
		}
		else if(request instanceof RequestPositionPlayerJson) {
			RequestPositionPlayerJson requestPositionPlayerJson = (RequestPositionPlayerJson)request;

			players.get(requestPositionPlayerJson.idPlayer).position.x = requestPositionPlayerJson.x;
			players.get(requestPositionPlayerJson.idPlayer).position.y = requestPositionPlayerJson.y;
			players.get(requestPositionPlayerJson.idPlayer).horizontalFlip = requestPositionPlayerJson.horizontalFlip;

			ResponsePositionPlayerJson responsePositionPlayerJson = new ResponsePositionPlayerJson();
			responsePositionPlayerJson.id = requestPositionPlayerJson.idPlayer;
			responsePositionPlayerJson.x = requestPositionPlayerJson.x;
			responsePositionPlayerJson.y = requestPositionPlayerJson.y;
			responsePositionPlayerJson.horizontalFlip = requestPositionPlayerJson.horizontalFlip;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPositionPlayerJson.idPlayer && player.getValue().ready) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePositionPlayerJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerFireJson) {
			RequestPlayerFireJson requestPlayerFireJson = (RequestPlayerFireJson)request;

			ResponsePlayerFireJson responsePlayerFireJson = new ResponsePlayerFireJson();
			responsePlayerFireJson.idPlayer = requestPlayerFireJson.idPlayer;
			responsePlayerFireJson.x = requestPlayerFireJson.x;
			responsePlayerFireJson.y = requestPlayerFireJson.y;
			responsePlayerFireJson.worldClickCoordsX = requestPlayerFireJson.worldClickCoordsX;
			responsePlayerFireJson.worldClickCoordsY = requestPlayerFireJson.worldClickCoordsY;
			responsePlayerFireJson.worldClickCoordsZ = requestPlayerFireJson.worldClickCoordsZ;
			responsePlayerFireJson.bulletSizeSpeedParameter = requestPlayerFireJson.bulletSizeSpeedParameter;
			responsePlayerFireJson.bulletDistanceAmountParameter = requestPlayerFireJson.bulletDistanceAmountParameter;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerFireJson.idPlayer && player.getValue().ready) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerFireJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerInvincibleJson) {
			RequestPlayerInvincibleJson requestPlayerInvincibleJson = (RequestPlayerInvincibleJson)request;

			players.get(requestPlayerInvincibleJson.idPlayer).invincible = requestPlayerInvincibleJson.invincible;

			ResponsePlayerInvincibleJson responsePlayerInvincibleJson = new ResponsePlayerInvincibleJson();
			responsePlayerInvincibleJson.idPlayer = requestPlayerInvincibleJson.idPlayer;
			responsePlayerInvincibleJson.invincible = requestPlayerInvincibleJson.invincible;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerInvincibleJson.idPlayer && player.getValue().ready) {
					player.getValue().webSocket.writeBinaryMessage(Buffer.buffer(serializer.serialize(responsePlayerInvincibleJson)));
				}
			}
		}
		else if(request instanceof RequestPlayerDieJson) {
			RequestPlayerDieJson requestPlayerDieJson = (RequestPlayerDieJson)request;

			ResponsePlayerDieJson responsePlayerDieJson = new ResponsePlayerDieJson();
			responsePlayerDieJson.id = requestPlayerDieJson.id;
			responsePlayerDieJson.bulletIdPlayer = requestPlayerDieJson.bulletIdPlayer;

			players.get(requestPlayerDieJson.id).ready = false;

			for(Map.Entry<Integer, Data> player : players.entrySet()) {
				if(player.getKey() != requestPlayerDieJson.id && player.getValue().ready) {
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
