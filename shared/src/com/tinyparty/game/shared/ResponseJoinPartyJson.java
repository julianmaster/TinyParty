package com.tinyparty.game.shared;

public class ResponseJoinPartyJson {
	public int id;

	public String playerColor;
	public float x;
	public float y;
	public boolean horizontalFlip;

	public int[] otherIds;
	public String[] otherPlayerColors;
	public float[] otherPositionsX;
	public float[] otherPositionsY;
	public boolean[] otherHorizontalFlips;
	public boolean[] otherInvincibles;
}
