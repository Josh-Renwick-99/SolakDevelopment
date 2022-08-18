package com.ruse.world.content.skill.impl.summoning;

import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public class BossPets {

	@Getter
	public enum BossPet {

		//AENEAS(9020, 9021, 23018),
		//TLAHUICOLE(9020, 9021, 23018),
		//NEYTIRI(9020, 9021, 23018),
		//ATTICUS(9020, 9021, 23018),
		//ELEANOR(9020, 9021, 23018),
		//SOREN(9020, 9021, 23018),
		//CASSIUS(9020, 9021, 23018),
		//ZORA(9020, 9021, 23018),
		//ANAIS(9020, 9021, 23018),
		//ASTAIRE(9020, 9021, 23018),
		//GAEL(9020, 9021, 23018),
		//SKILLING(9020, 9021, 23018),
		//MONSTEROUS(9020, 9021, 23018),
		//SLAYER(9020, 9021, 23018),
		//COLLECTOR(9020, 9021, 23018),
		//MASTER(9020, 9021, 23018),

		CRAB_PET(2710, 16446, 1500, ""),
		MAGE_PET(9031, 23041, 500,"10% Magic damage boost"),
		MELEE_PET(9032, 23042,500, "10% Melee damage boost"),
		RANGED_PET(9033, 23043, 500,"10% Ranged damage boost"),
		KILJAEDEN_PET(9826, 23114, 500,"15% Overall damage boost"),

		SKREEG_PET(9827, 23115, 400,"10% Drop rate boost\\n10% Double Drop rate boost"),
		ORIX_PET(9828, 23116, 400,"20% Drop rate boost\\n20% Double Drop rate boost"),
		CRYSTAL_ORC_PET(9829, 23117, 500,"25% Drop rate boost\\n25% Double Drop rate boost"),
		RAMMUS_PET(9822, 23164, 400,"20% Upgrade boost"),

		DEMON_PET(9819, 23161, 300,"10% Overall damage boost\\n10% Drop rate boost\\n10% Double Drop rate boost"),
		GOLEM_PET(9821, 23163, 400,"15% Overall damage boost\\n15% Drop rate boost\\n15% Double Drop rate boost"),
		DRAGON_PET(9820, 23162, 1500,"25% Overall damage boost\\n25% Drop rate boost\\n25% Double Drop rate boost"),

		FENRIR_PET(9830, 23108, 300,"2X EXP Boost\\n25% off on skill island shops"),
		GREEN_FENRIR_PET(9831, 23109, 300,"10% Overall damage boost,\\n 10% Drop rate boost,\\n 10% Double Drop rate boost,\\n while doing minigames"),
		RED_FENRIR_PET(9832, 23110,300, "2X Slayer Tickets on Task \\n20% Overall dmg boost on task"),
		HEIMDALL_PET(9834, 23112, 400,"Collects all items on the\\nground to the bank."),
		ODIN_PET(9833, 23111, 400,
				"20% Overall damage boost\\n25% DR, DDR, Upgrade Boost\\n Collects items on ground to bank\\n 2X EXP Boost"),

		GORVEK_PET(9808, 23081),
		VINDICTA_PET(9809, 23082),
		AVARYSS_PET(9803, 23083),
		CONJKOINED_PET(9804,  23084),
		NYMORA_PET(9805, 23085),

		DEMON_LIGHT_PET(9840, 23151),
		BAT_LIGHT_PET(9841, 23152),
		GOBLIN_LIGHT_PET(9842, 23153),
		LAVA_HOUND_PET(9843,  23154),
		MUTATED_HOUND_PET(9844,  23155),

		SCORPION_PET(9847, 23156),
		ZOMBIE_PET(9848,  23157),
		;

		BossPet(int npcId, int itemId) {
			this.npcId = npcId;
			this.itemId = itemId;
		}
		BossPet(int npcId, int itemId, String boost) {
			this.npcId = npcId;
			this.itemId = itemId;
			this.boost = boost;
		}
		BossPet(int npcId, int itemId, int zoom, String boost) {
			this.npcId = npcId;
			this.itemId = itemId;
			this.zoom = zoom;
			this.boost = boost;
		}

		public int npcId, itemId, zoom = 800;
		public String boost = "None";

		public static BossPet forId(int itemId) {
			for (BossPet p : BossPet.values()) {
				if (p != null && p.itemId == itemId) {
					return p;
				}
			}
			return null;
		}

		public static BossPet forSpawnId(int spawnNpcId) {
			for (BossPet p : BossPet.values()) {
				if (p != null && p.npcId == spawnNpcId) {
					return p;
				}
			}
			return null;
		}
	}

	public static boolean pickup(Player player, NPC npc) {
		BossPet pet = BossPet.forSpawnId(npc.getId());
		if (pet != null) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("You need a free inventory space to pick up a pet.");
				return false;
			}
			if (player.getSummoning().getFamiliar() != null
					&& player.getSummoning().getFamiliar().getSummonNpc() != null
					&& player.getSummoning().getFamiliar().getSummonNpc().isRegistered()) {
				if (player.getSummoning().getFamiliar().getSummonNpc().getId() == pet.npcId
						&& player.getSummoning().getFamiliar().getSummonNpc().getIndex() == npc.getIndex()) {
					player.getSummoning().unsummon(true, true);
					player.getPacketSender().sendMessage("You pick up your pet.");
					return true;
				} else {
					player.getPacketSender().sendMessage("This is not your pet to pick up.");
				}
			} else {
				player.getPacketSender().sendMessage("This is not your pet to pick up.");
			}
		}
		return false;
	}

}
