package com.ruse.world.content.goldenscratch;

public enum ScratchCardData {

	Infernal_Minigun(19114), Infernal_M16_MH(19115), Infernal_M16_OH(19116),;

	private int displayId;

	ScratchCardData(int displayId) {
		this.displayId = displayId;
	}

	public int getDisplayId() {
		return displayId;
	}

	public void setDisplayId(int displayId) {
		this.displayId = displayId;
	}

}