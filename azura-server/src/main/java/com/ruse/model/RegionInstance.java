package com.ruse.model;

import com.ruse.GameSettings;
import com.ruse.world.World;
import com.ruse.world.content.minigames.impl.Barrows;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles a custom region instance for a player
 * 
 * @author Gabriel
 */
public class RegionInstance {

	public enum RegionInstanceType {
		HALLS_OF_VALOR, BARROWS, THE_SIX, GRAVEYARD, RAID1, FIGHT_CAVE, WARRIORS_GUILD, NOMAD, RECIPE_FOR_DISASTER, CONSTRUCTION_HOUSE,
		CONSTRUCTION_DUNGEON, TRIO, KRAKEN, CORPOREAL_BEAST, KRIL_TSUTSAROTH, GENERAL_GRAARDOR, COMMANDER_ZILYANA,
		KREE_ARRA, ETERNAL, TORMENTED_DEMON, KING_BLACK_DRAGON, CHAOS_ELEMENTAL, SLASH_BASH, FLOREOX_BIRD,
		BANDOS_AVATAR, DAG_PRIME, DAG_SUPREME, DAG_REX, CALLISTO, VETION, VENENATIS, GALVEK, REV_TARRAGON, NEX, KINGS,
		ZULRAH, ZOMBIE, INSTANCE, DUNGEONEERING, VOID_OF_DARKNESS;
	}

	private Player owner;
	private RegionInstanceType type;
	private CopyOnWriteArrayList<NPC> npcsList;
	private CopyOnWriteArrayList<Player> playersList;

	public RegionInstance(Player p, RegionInstanceType type) {
		this.owner = p;
		this.type = type;
		this.npcsList = new CopyOnWriteArrayList<NPC>();
		if (type == RegionInstanceType.CONSTRUCTION_HOUSE || type == RegionInstanceType.THE_SIX) {
			this.playersList = new CopyOnWriteArrayList<Player>();
		}
	}

	public void destruct() {
		for (NPC n : npcsList) {
			if (n != null) { //&& n.getConstitution() > 0 && World.getNpcs().get(n.getIndex()) != null && !n.isDying()) {
			//	// System.out.println("okkk");
				if (type == RegionInstanceType.WARRIORS_GUILD) {

					if (n.getId() >= 4278 && n.getId() <= 4284) {
						owner.getMinigameAttributes().getWarriorsGuildAttributes().setSpawnedArmour(false);
					}

				} else if (type == RegionInstanceType.BARROWS) {

					if (n.getId() >= 2024 && n.getId() <= 2034) {
						Barrows.killBarrowsNpc(owner, n, false);
					}

				} else if (type == RegionInstanceType.HALLS_OF_VALOR) {
					owner.hov.killBarrowsNpc(n, false);
				}
				World.deregister(n);
				// // System.out.println("Is this running?");
			}
		}
		npcsList.clear();
		owner.setRegionInstance(null);
		// // System.out.println("Is this ru222nning?");
	}

	public void onLogout(Player player) {
		if (player != owner)
			return;
		player.getPA().sendMessage("You have been kicked from instance");
		player.getRegionInstance().destruct();
		player.setData(null);
		player.setCurrentInstanceAmount(-1);
		player.setCurrentInstanceNpcId(-1);
		player.setCurrentInstanceNpcName("");
		player.moveTo(GameSettings.HOME_CORDS);

	}
	
	public void add(Character c) {
		if (type == RegionInstanceType.CONSTRUCTION_HOUSE) {
			if (c.isPlayer()) {
				playersList.add((Player) c);
			} else if (c.isNpc()) {
				npcsList.add((NPC) c);
			}

			if (c.getRegionInstance() == null || c.getRegionInstance() != this) {
				c.setRegionInstance(this);
			}
		}
	}

	public void remove(Character c) {
		if (type == RegionInstanceType.CONSTRUCTION_HOUSE) {
			if (c.isPlayer()) {
				playersList.remove((Player) c);
				if (owner == ((Player) c)) {
					destruct();
				}
			} else if (c.isNpc()) {
				npcsList.remove((NPC) c);
			}

			if (c.getRegionInstance() != null && c.getRegionInstance() == this) {
				c.setRegionInstance(null);
			}
		}
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public RegionInstanceType getType() {
		return type;
	}

	public CopyOnWriteArrayList<NPC> getNpcsList() {
		return npcsList;
	}

	public CopyOnWriteArrayList<Player> getPlayersList() {
		return playersList;
	}

	@Override
	public boolean equals(Object other) {
		return (RegionInstanceType) other == type;
	}
}
