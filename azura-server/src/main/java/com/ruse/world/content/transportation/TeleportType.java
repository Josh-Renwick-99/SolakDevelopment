package com.ruse.world.content.transportation;

import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.util.Misc;

public enum TeleportType {

	NORMAL(3, new Animation(4381), new Animation(8941), new Graphic(1521), null),
	ANCIENT(5, new Animation(4381), new Animation(8941), new Graphic(1521, 0), null),
	LUNAR(4, new Animation(4381), new Animation(9013), new Graphic(1521), null),
	TELE_TAB(2, new Animation(4381), Animations.DEFAULT_RESET_ANIMATION, new Graphic(1521), null),
	RING_TELE(2, new Animation(4381), Animations.DEFAULT_RESET_ANIMATION, new Graphic(1521), null),
	LEVER(-1, null, null, null, null),
	PURO_PURO(9, new Animation(4381), Animations.DEFAULT_RESET_ANIMATION, new Graphic(1521), null),
	ZOOM(2, new Animation(4731), Animations.DEFAULT_RESET_ANIMATION, null, null);

	TeleportType(int startTick, Animation startAnim, Animation endAnim, Graphic startGraphic, Graphic endGraphic) {
		this.startTick = startTick;
		this.startAnim = startAnim;
		this.endAnim = endAnim;
		this.startGraphic = startGraphic;
		this.endGraphic = endGraphic;
	}

	private Animation startAnim, endAnim;
	private Graphic startGraphic, endGraphic;
	private int startTick;

	public Animation getStartAnimation() {
		return startAnim;
	}

	public Animation getEndAnimation() {
		return endAnim;
	}

	public Graphic getStartGraphic() {
		return this == NORMAL ? new Graphic(1521) : startGraphic;
	}

	public Graphic getEndGraphic() {
		return endGraphic;
	}

	public int getStartTick() {
		return startTick;
	}

	static class Animations {
		static Animation DEFAULT_RESET_ANIMATION = new Animation(65535);
	}
}
