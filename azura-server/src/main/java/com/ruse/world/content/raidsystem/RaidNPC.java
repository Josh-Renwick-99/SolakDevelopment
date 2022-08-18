package com.ruse.world.content.raidsystem;

import com.ruse.model.Position;

public class RaidNPC {

	int id;
	Position pos;

	public RaidNPC(int id, Position pos) {
		this.id = id;
		this.pos = pos;
	}

	public int getID() {
		return id;
	}

	public Position getPosition() {
		return pos;
	}

}
