package com.ruse.world.content.globalBoss;

import com.ruse.model.Position;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by Stan van der Bend on 16/12/2017. project: runeworld-game-server
 * package: runeworld.world.entity.combat.strategy.global.impl
 */
public class ShadowLord extends GlobalBoss {

	private final static int NPC_ID = 1265;
	private final static int KEY_ITEM_ID = 19933;

	private final static String RECEIVE_DROP_MESSAGE = "You received a key drop from the Wildywyrm.";

	public ShadowLord() {
		super(NPC_ID);
	}

	public boolean respawn = true;

	@Override
	public GlobalBoss reincarnate() {
		return new ShadowLord();
	}

	@Override
	protected void handleDrop(Player player) {
		player.giveItem(KEY_ITEM_ID, 1);
		player.sendMessage(RECEIVE_DROP_MESSAGE);
	}

	public boolean getRespawn() {
		return respawn;
	}

	public void setRespawn(boolean respawn) {
		this.respawn = respawn;
	}

	@Override
	protected SpawnLocation[] spawnLocations() {
		return new SpawnLocation[] { new SpawnLocation(new Position(2911, 3613, 0), "") };
	}

	@Override
	protected int minutesTillRespawn() {
		return 1;
	}

	@Override
	protected int minutesTillDespawn() {
		return 10;
	}

	@Override
	public int maximumDrops() {
		return 6;
	}
}
