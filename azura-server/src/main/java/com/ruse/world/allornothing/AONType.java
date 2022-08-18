package com.ruse.world.allornothing;

/**
 * Created by Grant_ | Rune-server.ee | on 2/22/2018.
 */
public enum AONType {
	COMMON("@gre@", 1), UNCOMMON("@red@", 50), ULTIMATE("@blu@", 45), LEGENDARY("@yel@", 40), DREAM("@pur@", 35);

	private String color;
	private int gamblePercentage;

	AONType(String color, int gamblePercentage) {
		this.color = color;
		this.gamblePercentage = gamblePercentage;
	}

	public String getColor() {
		return color;
	}

	public int getGamblePercentage() {
		return gamblePercentage;
	}
}