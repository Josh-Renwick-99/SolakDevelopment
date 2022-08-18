package com.ruse.model;

import com.ruse.GameSettings;
import com.ruse.model.RegionInstance.RegionInstanceType;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment.Jail;
import com.ruse.world.content.Zulrah;
import com.ruse.world.content.combat.strategy.impl.Scorpia;
import com.ruse.world.content.instanceMananger.InstanceManager;
import com.ruse.world.content.minigames.impl.*;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.skill.impl.old_dungeoneering.Dungeoneering;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.zombie.ZombieRaids;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.instance.MapInstance;

public class Locations {
	public static boolean inside(Position start, int startSize, Position target, int targetSize) {
		if (target == null) {
			return false;
		}
		int distanceX = start.getX() - target.getX();
		int distanceY = start.getY() - target.getY();
		if (distanceX < targetSize && distanceX > -startSize && distanceY < targetSize && distanceY > -startSize) {
			return true;
		}
		return false;
	}
	public static void login(Player player) {
		player.setLocation(Location.getLocation(player));
		player.getLocation().login(player);
		player.getLocation().enter(player);
	}

	public static void logout(Player player) {
		player.getLocation().logout(player);
		if (player.getRegionInstance() != null)
			player.getRegionInstance().destruct();
		if (player.getLocation() != Location.GODWARS_DUNGEON) {
			player.getLocation().leave(player);
		}
	}

	public static int PLAYERS_IN_WILD;
	public static int PLAYERS_IN_DUEL_ARENA;

	public enum Location {

		AFK(new int[] { 3024, 3056 }, new int[] { 4050, 4082 }, false, false, true, false, false, true) {
		},
		KEEPERS_OF_LIGHT_LOBBY(new int[] { 2304, 2344 }, new int[] { 4992, 5050 }, false, false, true, false, false, true) {

			@Override
			public void process(Player player) {
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {
				//TODO broken code remove?
				if (player.getLocation() != KEEPERS_OF_LIGHT_GAME) {
					KeepersOfLight.leave(player, true);
				}
			}

			@Override
			public void logout(Player player) {
				KeepersOfLight.leave(player, true);
			}
		},

		KEEPERS_OF_LIGHT_GAME(new int[] { 2345, 2431 }, new int[] { 4992, 5054 }, true, false, true, false, true, true) {

			/*@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender()
						.sendMessage("Teleport spells are blocked on this island. Win or die!");
				return false;
			}*/

			@Override
			public void leave(Player player) {
				KeepersOfLight.leave(player, true);
			}

			@Override
			public void logout(Player player) {
				KeepersOfLight.leave(player, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				KeepersOfLight.leave(player, true);
			}
		},
		EASTER(new int[] { 3123, 3138}, new int[] { 3392, 3419},
				true, true, true, false, false, true) {},
		SUMMER_EVENT(new int[] { 3076, 3133}, new int[] { 2886, 2938},
				true, true, true, false, false, true) {},
		PRESENTS(new int[] { 3017, 3061}, new int[] { 2824, 2870},
				true, true, true, false, false, true) {},
		HULK(new int[] { 3472, 3503}, new int[] { 3601, 3632},
				true, true, true, false, false, true) {},
		GOKU(new int[] { 2853, 2880}, new int[] { 2855, 2880},
				true, true, true, false, false, true) {},
		GARFIELD(new int[] { 1999, 2028}, new int[] { 4495, 4530},
				true, true, true, false, false, true) {},
		IRON(new int[] { 3776, 3839}, new int[] { 2816, 2879},
				true, true, true, false, false, true) {},
		ZENYTE(new int[] { 2778, 2792}, new int[] { 4834, 4848},
				true, true, true, false, false, true) {},
		EXODEN(new int[] { 2562, 2600}, new int[] { 4485, 4526},
				true, true, true, false, false, true) {},
		VBOSS(new int[] { 2959, 2998}, new int[] { 2762, 2800},
				true, true, true, false, false, true) {},
		PRIME(new int[] { 2437, 2492}, new int[] { 10113, 10171},
				true, true, true, false, false, true) {},


		LUCIFER(new int[] { 2301, 2367}, new int[] { 3970, 4024},
				false, true, true, false, false, true) {},

		PROGRESSION_ZONES(new int[] { 3016, 3071}, new int[] { 10244, 10297},
				false, true, true, false, false, true) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 112000)
					player.getPacketSender().sendWalkableInterface(112000, true);
			}


			@Override
			public void enter(Player player) {
				ProgressionZone.updateInterface(player);
			}

			@Override
			public void leave(Player player) {
				player.getPacketSender().sendWalkableInterface(112000, false);
			}
		},
		ZOMBIE(new int[]{2496, 2543}, new int[]{3721, 3771}, true, false, true, false, true, false) {
			@Override
			public void logout(Player player) {

				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstance.RegionInstanceType.ZOMBIE)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.ZOMBIE, player.getPosition().getZ(), player));

				}
				if (player.getRegionInstance() != null)
					player.getRegionInstance().destruct();

				if (player.getZombieParty() != null) {
					player.getZombieParty().remove(player, true);
				}

				player.moveTo(new Position(3832, 2821, 0));

				if (player.getZombieParty() != null)
					player.getZombieParty().getPlayers()
							.remove(player);

				player.getMovementQueue().setLockMovement(false);
				player.getPacketSender().sendCameraNeutrality();

			}

			@Override
			public void leave(Player player) {

				player.getPacketSender().sendCameraNeutrality();
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstance.RegionInstanceType.ZOMBIE)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.ZOMBIE, player.getPosition().getZ(), player));
				}

				if (player.getZombieParty() != null) {
					if (player.getZombieParty().getOwner().equals(player)) {
						World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.ZOMBIE, player.getIndex() * 4, player));
					}
				}

				/*if (player.getZombieParty() != null)
					player.getZombieParty().remove(player, true);

				if (player.getZombieParty() != null)
					player.getZombieParty().getPlayers()
							.remove(player);*/

				player.moveTo(new Position(2553, 3715, 0));

				player.getMovementQueue().setLockMovement(false);

			}

			@Override
			public void login(Player player) {
				player.getPacketSender().sendCameraNeutrality();
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstance.RegionInstanceType.ZOMBIE)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.ZOMBIE, player.getPosition().getZ(), player));
				}

				if (player.getZombieParty() != null) {
					if (player.getZombieParty().getOwner().equals(player)) {
						World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.ZOMBIE, player.getIndex() * 4, player));
					}
				}

				if (player.getZombieParty() != null)
					player.getZombieParty().remove(player, true);

				if (player.getZombieParty() != null)
					player.getZombieParty().getPlayers()
							.remove(player);

				player.moveTo(new Position(2553, 3715, 0));

				player.getMovementQueue().setLockMovement(false);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.sendMessage("You cannot teleport while in a raid");
				return false;
			}

			@Override
			public void enter(Player player) {
				player.setRegionInstance(new RegionInstance(player, RegionInstance.RegionInstanceType.ZOMBIE));
				player.getPacketSender().sendInteractionOption("null", 2, true);
			}

			@Override
			public void onDeath(Player player) {
				if (player.getZombieParty() != null) {
					ZombieRaids.handleDeath(player.getZombieParty(),
							player);
				}
				player.getPacketSender().sendCameraNeutrality();
				player.setInsideRaids(false);
				player.getMovementQueue().setLockMovement(false);
			}

			@Override
			public void process(Player player) {
				//if (player.getZombieParty() != null)
					player.getZombieParty().refreshInterface();
			}

		},

		ZOMBIE_LOBBY(new int[]{2546, 2561}, new int[]{3710, 3726}, true, false, true, false, true, false) {
			@Override
			public void leave(Player player) {
				player.getPacketSender().sendCameraNeutrality();

				if (player.getZombieParty() != null)
					player.getZombieParty().remove(player, true);

				if (player.getZombieParty() != null)
					player.getZombieParty().getPlayers()
							.remove(player);

				player.getMovementQueue().setLockMovement(false);
			}

			@Override
			public void enter(Player player) {
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.INVITE)
					player.getPacketSender().sendInteractionOption("Invite", 2, false);

				int id = 111716;
				for (int i = 111716; i < 111737; i++) {
					player.getPacketSender().sendString(id++, "---");
					player.getPacketSender().sendString(id++, "--");
					player.getPacketSender().sendString(id++, "-");
				}
				player.getPacketSender().sendString(111709, "Create");
				player.getPacketSender().sendString(111702, "Raiding Party: @whi@0");

				player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111700);
				player.getPacketSender().sendConfig(6000, 4);
				player.getPacketSender().sendTab(GameSettings.QUESTS_TAB);
			}

			@Override
			public void login(Player player) {
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.INVITE)
					player.getPacketSender().sendInteractionOption("Invite", 2, false);
			}

			@Override
			public void process(Player player) {
				if (player.getZombieParty() != null)
					player.getZombieParty().refreshInterface();
				else{
					int id = 111716;
					for (int i = 111716; i < 111737; i++) {
						player.getPacketSender().sendString(id++, "---");
						player.getPacketSender().sendString(id++, "--");
						player.getPacketSender().sendString(id++, "-");
					}
					player.getPacketSender().sendString(111709, "Create");
					player.getPacketSender().sendString(111702, "Raiding Party: @whi@0");
				}
			}
		},
		// Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean
		// followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean
		// aidingAllowed) {
		FREIZA(new int[] { 2433, 2494 }, new int[] { 2817, 2878 }, false, true, true, false, false, false) {
		},
		// xyyx
		AOE(new int[] { 2881, 2949 }, new int[] { 2820, 2877 }, true, true, true, false, false, false) {
		},
		CRYSTALQUEEN(new int[] { 2858, 2878 }, new int[] { 9933, 9959 }, true, true, true, false, false, false) {
		},

		// xyyx
		JOKER_LAND(new int[] { 1798, 1818 }, new int[] { 3201, 3221 }, true, true, true, false, false, false) {
		},
		// xyyx
		TRANSFORMER(new int[] { 3274, 3311 }, new int[] { 3013, 3036 }, true, true, true, false, false, false) {
		},
		MAGEBANK_SAFE(new int[] { 2525, 2550 }, new int[] { 4707, 4727 }, true, true, true, false, false, false) {

		},
		ZULRAH(new int[] { 3395, 3453 }, new int[] { 2751, 2785 }, false, false, true, false, false, false) {

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getPacketSender().sendCameraNeutrality();
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public void enter(Player player) {
				Zulrah.enter(player);
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public void onDeath(Player player) {
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				killer.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				return false;
			}

			@Override
			public void logout(Player player) {
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				player.moveTo(GameSettings.DEFAULT_POSITION);
			}

			@Override
			public void login(Player player) {
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstanceType.ZULRAH)) {
					player.getRegionInstance().destruct();
				}
				player.getMinigameAttributes().getZulrahAttributes().setRedFormDamage(0, false);
				player.moveTo(GameSettings.DEFAULT_POSITION);
			}

		},

		// xyyx
		LORDS(new int[] { 3349, 3379 }, new int[] { 9625, 9654 }, true, true, true, false, false, false) {
		},
		DOOM(new int[] { 2302, 2369 }, new int[] { 5182, 5250 }, true, true, true, false, false, false) {
		},
		// xyyx
		PENGUINZONE(new int[] { 3027, 3072 }, new int[] { 9533, 9558 }, true, true, true, false, false, false) {
		},

		// 3340 3351
		// 3364 3333
		GALVEKMULTI(new int[] { 3340, 3364 }, new int[] { 3333, 3351 }, true, true, true, false, false, false) {
		},
		BULWARK(new int[] { 2409, 2439 }, new int[] { 3512, 3532 }, true, true, true, false, false, false) {
		},
		INSTANCE1(new int[] { 2628, 2685 }, new int[] { 4750, 4790 }, true, true, true, false, false, false) {
			@Override
			public void logout(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
						new InstanceManager(player).onLogout();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE1, player.getIndex() * 4, player));
				}
			}

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE1, player.getIndex() * 4, player));
				}
			}

			@Override
			public void login(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE1, player.getIndex() * 4, player));
				}
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
			}

			@Override
			public void onDeath(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE1, player.getIndex() * 4, player));
				}
			}
		},
		INSTANCE2(new int[] { 2753, 2814 }, new int[] { 4736, 4800 }, true, true, true, false, false, false) {
			@Override
			public void logout(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
				}
				World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE2, player.getIndex() * 4, player));
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
			}

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
				}
				World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE2, player.getIndex() * 4, player));
			}

			@Override
			public void login(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
				}
				World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE2, player.getIndex() * 4, player));
				player.moveTo(GameSettings.DEFAULT_POSITION.copy());
			}

			@Override
			public void onDeath(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.INSTANCE)) {
					new InstanceManager(player).onLogout();
				}
				World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.INSTANCE2, player.getIndex() * 4, player));
			}
			},
		ZONES1(new int[] { 2948, 3007 }, new int[] { 9473, 9510 }, true, true, true, false, false, false) {
		},
		ZONES2(new int[] { 2295, 2410 }, new int[] { 3876, 3906 }, true, true, true, false, false, false) {
		},
		ZONES3(new int[] { 1665, 1725 }, new int[] { 5590, 5611 }, true, true, true, false, false, false) {
		},
		ZONES4(new int[] { 1597, 1662 }, new int[] { 5587, 5621 }, true, true, true, false, false, false) {
		},
		ZONES5(new int[] { 3278, 3334 }, new int[] { 3293, 3325 }, true, true, true, false, false, false) {
		},//2372, 5113, 2427, 5057
		SUPREME(new int[] { 2372, 2427 }, new int[] { 5057, 5113 }, true, true, true, false, false, false) {
		},
		ZONES6(new int[] { 3278, 3319 }, new int[] { 3270, 3325 }, true, true, true, false, false, false) {
		},
		ZONES7(new int[] { 2975, 3018 }, new int[] { 3100, 3135 }, true, true, true, false, false, false) {
		},
		SLAYERMULTI(new int[] { 1804, 2388 }, new int[] { 5188, 5274 }, true, true, true, false, false, false) {
		},
		ETERNAL(new int[] { 2063, 2098 }, new int[] { 3221, 3240 }, true, true, true, true, false, true) {
		},
		// Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean
		// followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean
		// aidingAllowed) {
		GODWARSPLATFORM(new int[] { 2506, 2550 }, new int[] { 2627, 2677 }, true, true, true, true, false, true) {
		},
		CUSTOMINI(new int[] { 2567, 2597 }, new int[] { 2564, 2600 }, true, true, true, true, false, false) {
		},
		// xyyx
		GENERAL(new int[] { 2319, 2351 }, new int[] { 9806, 9828 }, true, true, true, true, false, false) {
		},
		EVENTBOSS(new int[] { 2857, 2879 }, new int[] { 2858, 2879 }, true, true, true, true, false, false) {
		},
		TRAININZONEMULTI(new int[] { 2504, 2551 }, new int[] { 2502, 2562 }, true, true, true, false, false, false) {
		},
		SUPREMEE(new int[] { 2504, 2551 }, new int[] { 2502, 2562 }, true, true, true, false, false, false) {
		},
		TRAINING_MELEE(new int[] { 2389, 2407 }, new int[] { 2837, 2853 }, true, true, true, false, false, false) {
			@Override
			public void enter(Player player) {
				if (player.getPointsHandler().getNPCKILLCount() > 5000 && KillsTracker.getTotalKillsForNpc(1023, player) > 500) {
					player.moveTo(GameSettings.HOME_CORDS);
					player.sendMessage("This place is for new players with less than 5k npc kills.");
					return;
				}
			}
		},
		TRAINING_RANGED(new int[] { 2393, 2411 }, new int[] { 2907, 2923 }, true, true, true, false, false, false) {
			@Override
			public void enter(Player player) {
				if (player.getPointsHandler().getNPCKILLCount() > 5000&& KillsTracker.getTotalKillsForNpc(1233, player) > 500) {
					player.moveTo(GameSettings.HOME_CORDS);
					player.sendMessage("This place is for new players with less than 5k npc kills.");
					return;
				}
			}
		},
		TRAINING_MAGIC(new int[] { 2454, 2472 }, new int[] { 2906, 2922 }, true, true, true, false, false, false) {
			@Override
			public void enter(Player player) {
				if (player.getPointsHandler().getNPCKILLCount()> 5000&& KillsTracker.getTotalKillsForNpc(1234, player) > 500) {
					player.moveTo(GameSettings.HOME_CORDS);
					player.sendMessage("This place is for new players with less than 5k npc kills.");
					return;
				}
			}
		},
		TRAINING_ROCK_CRAB(new int[] { 2688, 2747 }, new int[] { 4719, 4798 }, true, true, true, false, false, false) {
			@Override
			public void enter(Player player) {
				if (player.getPointsHandler().getNPCKILLCount() > 5000&& KillsTracker.getTotalKillsForNpc(1265, player) > 500) {
					player.moveTo(GameSettings.HOME_CORDS);
					player.sendMessage("This place is for new players with less than 5k npc kills.");
					return;
				}
			}
		},
		XMASEVENT2016(new int[] { 2747, 2821 }, new int[] { 3707, 3877 }, false, true, true, false, true, true) {

			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 11877) {
					player.getPacketSender().sendWalkableInterface(11877, true);
				}
			}

		},

		CUSTOM_RAIDS_LOBBY(new int[] { 2715, 2735 }, new int[] { 2730, 2745 }, true, false, true, false, true, false) {
			@Override
			public void login(Player player) {

			}

			@Override
			public void logout(Player player) {
				Dungeoneering.leave(player, false, true);
			}

			@Override
			public void leave(Player player) {
				Dungeoneering.raidCount = 0;
				player.getPacketSender().sendWalkableInterface(29050, false);
				player.getPacketSender().sendInteractionOption("null", 7, false);
			}

			@Override
			public void enter(Player player) {
				player.getPacketSender().sendWalkableInterface(29050, true);
				player.getPacketSender().sendInteractionOption("Invite To Raid Party", 7, false);
			}

			@Override
			public void onDeath(Player player) {
				Dungeoneering.leave(player, false, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				return false;
			}

			@Override
			public void process(Player player) {
				// // System.out.println("In here");
			}
		},

		CUSTOM_RAIDS(new int[] { 2702, 2730 }, new int[] { 2702, 2730 }, true, false, true, false, true, false) {
			@Override
			public void login(Player player) {

			}

			@Override
			public void logout(Player player) {
				Dungeoneering.leave(player, false, true);
			}

			@Override
			public void leave(Player player) {

				// player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getPlayers().size());
				// player.getPacketSender().sendWalkableInterface(-1);
				// Dungeoneering.leave(player, false, true);
				// Dungeoneering.leave(player, true, true);
			}

			@Override
			public void enter(Player player) {
			}

			@Override
			public void onDeath(Player player) {
				Dungeoneering.handlePlayerDeath(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				return false;
			}

			@Override
			public void process(Player player) {
				if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
					TeleportHandler.teleportPlayer(player, new Position(2722, 2737),
							player.getSpellbook().getTeleportType());
				}
			}
		},



		DUNGEONEERING(new int[] { 2240, 2303 }, new int[] { 4992, 5053 }, true, false, true,
				false, true, false) {
			@Override
			public void login(Player player) {
				player.getPacketSender().sendDungeoneeringTabIcon(true).sendTabInterface(GameSettings.QUESTS_TAB, 27224).sendTab(GameSettings.QUESTS_TAB);

				DungeoneeringParty.clearInterface(player);
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.INVITE) {
					player.getPacketSender().sendInteractionOption("Invite", 2, true);
				}
			}

			@Override
			public void leave(Player player) {
				com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering.Companion.leaveLobby(player);
			}

			@Override
			public void enter(Player player) {
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.INVITE) {
					player.getPacketSender().sendInteractionOption("Invite", 2, true);
				}

				if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
					player.getPacketSender().sendDungeoneeringTabIcon(true).sendTabInterface(GameSettings.QUESTS_TAB, 27224).sendTab(GameSettings.QUESTS_TAB);
					DungeoneeringParty.clearInterface(player);
				}
			}

/*
			@Override
			public void leave(Player player) {
				if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
					if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().equals(player)) {
						World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.DUNGEONEERING, player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getHeight(), player));
					}
				}
				com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering.Companion.leaveLobby(player);
			}

			@Override
			public void enter(Player player) {
				player.getPacketSender().sendCameraNeutrality();
				player.setRegionInstance(new RegionInstance(player, RegionInstance.RegionInstanceType.DUNGEONEERING));
			}*/
		},


		DUNGEONEERING_ROOM(new int[] { 2240, 2303 }, new int[] { 4992, 5027 }, true, false, true,
				false, true, false) {
		},
		// Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean
		// followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean
		// aidingAllowed) {
		ZULRAH_WAITING(new int[] { 3401, 3414 }, new int[] { 2789, 2801 }, false, true, true, false, true, true) {
			@Override
			public void enter(Player player) {
				if (player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager()
						.getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
							player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("The astounding power of the old pillars heals you.");
				}
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
						.getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER,
							player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("The mystique aura of the pillars restores your prayer.");
				}
			}

			@Override
			public void leave(Player player) {
				if (player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager()
						.getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
							player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender().sendMessage("The astounding power of the old pillars heals you.");
				}
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
						.getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER,
							player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("The mystique aura of the pillars restores your prayer.");
				}
			}
		},
		JAIL(new int[] { 2505, 2535 }, new int[] { 9310, 9330 }, false, false, false, false, false, false) {
			@Override
			public boolean canTeleport(Player player) {
				if (player.getRights().isStaff()) {
					player.getPacketSender().sendMessage("Staff can leave at any time.");
					return true;
				}
				return !Jail.isJailed(player.getUsername());
			}
		},
		MEMBER_ZONE(new int[] { 3415, 3435 }, new int[] { 2900, 2926 }, false, true, true, false, false, true) {
		},
		HOME_BANK(new int[] { 2635, 2675 }, new int[] { 3965, 4031 }, false, true, true, false, true, true) {
			@Override
			public void enter(Player player) {
				if (player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager()
						.getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
							player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender()
							.sendMessage("As you enter the home bank, your health regenerates to full.");
				}
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
						.getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER,
							player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you enter the home bank, the gods restore your prayer.");
				}
			}

			@Override
			public void leave(Player player) {
				if (player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager()
						.getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
							player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender()
							.sendMessage("As you leave the home bank, your health regenerates to full.");
				}
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
						.getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER,
							player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you leave the home bank, the gods restore your prayer.");
				}
			}
		},
		NEW_MEMBER_ZONE(new int[] { 2792, 2877 }, new int[] { 3319, 3396 }, false, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if (!player.getRights().isMember() && !player.newPlayer()) {
					// player.getPacketSender().sendMessage("This area is for Members only.");
					// player.moveTo(GameSettings.HOME_CORDS);
				}
			}

			@Override
			public void enter(Player player) {
				if (player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager()
						.getMaxLevel(Skill.CONSTITUTION)) {
					player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
							player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
					player.getPacketSender()
							.sendMessage("As you enter the Member Zone, your health regenerates to full.");
				}
				if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
						.getMaxLevel(Skill.PRAYER)) {
					player.getSkillManager().setCurrentLevel(Skill.PRAYER,
							player.getSkillManager().getMaxLevel(Skill.PRAYER));
					player.getPacketSender().sendMessage("As you enter the Member Zone, the gods restore your prayer.");
				}
			}
		},
		TRIO_ZONE(new int[] { 3008, 3039 }, new int[] { 5216, 5247 }, false, false, false, false, false, false) {
		},
		// xyyx
		GAMBLE(new int[] { 2844, 2867 }, new int[] { 2696, 2720 }, false, true, true, false, true, true) {
		},
		VARROCK(new int[] { 3167, 3272 }, new int[] { 3263, 3504 }, false, true, true, true, true, true) {
		},
		/*
		 * BANK(new int[]{3090, 3099, 3089, 3090, 3248, 3258, 3179, 3191, 2944, 2948,
		 * 2942, 2948, 2944, 2950, 3008, 3019, 3017, 3022, 3203, 3213, 3212, 3215, 3215,
		 * 3220, 3220, 3227, 3227, 3230, 3226, 3228, 3227, 3229}, new int[]{3487, 3500,
		 * 3492, 3498, 3413, 3428, 3432, 3448, 3365, 3374, 3367, 3374, 3365, 3370, 3352,
		 * 3359, 3352, 3357, 3200, 3237, 3200, 3235, 3202, 3235, 3202, 3229, 3208, 3226,
		 * 3230, 3211, 3208, 3226}, false, true, true, false, false, true) { },
		 */
		// xyyx
		SKELETAL(new int[] { 3323, 3357 }, new int[] { 3162, 3188 }, true, true, true, false, false, false) {
		},


		VOID_OF_DARKNESS(new int[] { 1925, 1984 }, new int[] { 4999, 5055 }, true, true, true, false, true, true) {
			@Override
			public void enter(Player player) {
				/*VoidOfDarkness.resetBarrows(player);
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.VOID_OF_DARKNESS)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.VOID_OF_DARKNESS, player.getIndex() * 4));
				}
				player.vod.start();*/

				player.getPacketSender().sendWalkableInterface(126500, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {

				/*if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.VOID_OF_DARKNESS)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.VOID_OF_DARKNESS, player.getIndex() * 4));
				}*/
				VoidOfDarkness.resetBarrows(player);
				player.getPacketSender().sendCameraNeutrality();
				
				player.getPacketSender().sendWalkableInterface(126500, false);
				NPC tiedNpc = player.findSpawnedFor(7758);
				if(tiedNpc != null) {
					World.deregister(tiedNpc);
				}
				VoidOfDarkness.updateInterface(player);
			}

		},

		HALLS_OF_VALOR(new int[] { 2175, 2239}, new int[] { 4998, 5053 }, true, true, true, false, false, false) {
			@Override
			public void enter(Player player) {
				HallsOfValor.resetBarrows(player);
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.HALLS_OF_VALOR)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.HALLS_OF_VALOR, player.getIndex() * 4));
				}
				player.hov.start();
			}

			@Override
			public void logout(Player player) {
				leave(player);
			}

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.HALLS_OF_VALOR)) {
					player.getRegionInstance().destruct();
					World.getNpcs().forEach(n -> n.removeInstancedNpcs(Location.HALLS_OF_VALOR, player.getIndex() * 4));
				}
				HallsOfValor.resetBarrows(player);
				player.getPacketSender().sendCameraNeutrality();
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}

		},
		TREASURE_HUNTER(new int[] { 1986, 2045 }, new int[] { 4994, 5052 }, true, true, true, false, false, false) {
		},
		GLOBAL_BOSS(new int[] { 2128, 2161 }, new int[] { 5004, 5034 }, true, true, true, false, false, false) {

		},
		DIAMOND_ZONE(new int[] { 2571, 2619 }, new int[] { 2695, 2746 }, false, true, true, false, true, false) {

		},
		RUBY_ZONE(new int[] { 2503, 2557 }, new int[] { 2684, 2750 }, false, true, true, false, true, false) {

		},
		SAPPHIRE_ZONE(new int[] { 2496, 2556 }, new int[] { 2753, 2813 }, false, true, true, false, true, false) {

		},
		EMERALD_ZONE(new int[] { 2561, 2622 }, new int[] { 2752, 2810 }, false, true, true, false, true, false) {

		},
		ONYX_ZONE(new int[] { 2493, 2564 }, new int[] { 2625, 2680 }, false, true, true, false, true, false) {

		},
		ZENYTE_ZONE(new int[] { 2569, 2620 }, new int[] { 2631, 2687 }, false, true, true, false, true, false) {

		},
		VAULT_OF_WAR(new int[] { 1729, 1791 }, new int[] { 5313, 5375 }, false, true, true, false, false, false) {
			@Override
			public void logout(Player player) {
				if (player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
					player.moveTo(VaultOfWar.TELEPORT_AREA);
				}
			}

			@Override
			public void leave(Player player) {
				player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
			}
		},
		EZONE(new int[] { 2763, 2812 }, new int[] { 3072, 3123 }, true, true, true, false, false, false) {
		},
		SZONE(new int[] { 1630, 1691 }, new int[] { 5670, 5728 }, true, true, true, false, false, false) {
		},
		SPONSORZONE(new int[] { 2944, 3007 }, new int[] { 2816, 2879 }, true, true, true, false, false, false) {
		},
		HULK2(new int[] { 3295, 3316 }, new int[] { 4970, 4990 }, true, true, true, false, false, false) {
		},
		EDGEVILLE(new int[] { 3073, 3134 }, new int[] { 3457, 3518 }, false, true, true, false, false, true) {
		},
		LUMBRIDGE(new int[] { 3175, 3238 }, new int[] { 3179, 3302 }, false, true, true, true, true, true) {
		},
		KING_BLACK_DRAGON(new int[] { 2251, 2292 }, new int[] { 4673, 4717 }, true, true, true, true, true, true) {
		},
		SCORPIA(new int[] { 2845, 2864 }, new int[] { 9621, 9649 }, true, true, true, true, true, true) {
			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if (npc.getId() == 109) {
					Scorpia.killedBaby();
					return true;
				}
				return false;
			}
		},
		KRAKEN(new int[] { 3672, 3690 }, new int[] { 9875, 9899 }, true, true, true, true, true, true) {
			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null
						&& player.getRegionInstance().equals(RegionInstanceType.KRAKEN)) {
					player.getRegionInstance().destruct();
				}
				player.getPacketSender().sendCameraNeutrality();
			}
		},
		SLASH_BASH(new int[] { 2504, 2561 }, new int[] { 9401, 9473 }, true, true, true, true, true, true) {
		},
		BANDOS_AVATAR(new int[] { 2340, 2396 }, new int[] { 4929, 4985 }, true, true, true, true, true, true) {
		},
		KALPHITE_QUEEN(new int[] { 3464, 3500 }, new int[] { 9478, 9523 }, true, true, true, true, true, true) {
		},
		PHOENIX(new int[] { 2824, 2862 }, new int[] { 9545, 9594 }, true, true, true, true, true, true) {
		},
		// BANDIT_CAMP(new int[]{3020, 3150, 3055, 3195}, new int[]{3684, 3711, 2958,
		// 3003}, true, true, true, true, true, true) {
		// },
		ROCK_CRABS(new int[] { 2689, 2727 }, new int[] { 3691, 3730 }, true, true, true, true, true, true) {
		},
		ARMOURED_ZOMBIES(new int[] { 3077, 3132 }, new int[] { 9657, 9680 }, true, true, true, true, true, true) {
		},
		CORPOREAL_BEAST(new int[] { 2879, 2962 }, new int[] { 4368, 4413 }, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				int x1 = 2889;
				int x2 = 2908;
				int y1 = 4381;
				int y2 = 4403;
				int currentx = player.getPosition().getX();
				int currenty = player.getPosition().getY();

				boolean safe = currentx >= x1 && currentx <= x2 && currenty >= y1 && currenty <= y2;
				if (safe) {
					// player.getPacketSender().sendMessage("You are safe");
					player.getPacketSender().sendWalkableInterface(16152, false);// .sendMessage("sendwalkint-1");
					/*
					 * player.setWalkableInterfaceId(-1);
					 * player.getPacketSender().sendMessage("setwalkint-1");
					 * player.getPacketSender().sendInterfaceRemoval().sendMessage("sendintremoval")
					 * ; player.getPacketSender().sendInterfaceReset().sendMessage("sendintreset");
					 */
				} else {
					// player.getPacketSender().sendMessage("Get out of the gas!");
					player.dealDamage(new Hit(Misc.getRandom(15) * 10, Hitmask.DARK_PURPLE, CombatIcon.CANNON));
					if (player.getWalkableInterfaceId() != 16152) {
						player.getPacketSender().sendWalkableInterface(16152, true);
					}
					// player.setWalkableInterfaceId(16152);
				}
			}

		},
		DAGANNOTH_DUNGEON(new int[] { 2886, 2938 }, new int[] { 4431, 4477 }, true, true, true, false, true, true) {
		},
		WILDERNESS(new int[] { 2940, 3392, 2986, 3012, 3653, 3720, 3650, 3653, 3150, 3199, 2994, 3041 },
				new int[] { 3523, 3968, 10338, 10366, 3441, 3538, 3457, 3472, 3796, 3869, 3733, 3790 }, false, true,
				true, true, true, true) {


			// -- Contains Any requires something to be there, so if you currently do NOT HAVE an actually allowed item everywhere it will crash

			private final int[] ALLOWED_ITEMS = new int[] { 1157,1165,1161,1159,1163,5574,5575,5576,10828,1119,1125,1121,1123,1127,3140,4087,3751,1069,1077,1071,1079,1073,6128,6129,6130,3753,1193,1195,1197,1199,1201,10589,6809,10564,4131,1321,1323,1325,1327,1329,1331,1333,4587,1291,1293,1295,1297,1299,1301,1303,1422,1420,1424,1426,1428,1430,1432,6528,4153,1307,1309,1311,1313,1315,1318,1319,14017,1009,1796,1654,1656,1658,1712,1710,1708,1706,1731,1725,1727,1729,1478,1635,1637,1639,1641,1643,2550,2570,11118,11113,325,339,329,361,379,373,7946,385,391,2442,2436,2440,2444,3040,6685,2452,3024,2434,2446,882,884,886,888,890,892,11212,810,811,11230,877,9140,9144,9142,9143,9144,9240,9241,9243,9244,9242,9341,9245,864,865,866,867,868,550,551,552,553,554,555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,8007,8008,8009,8010,8011,8012,8013,8012,10499,841,843,845,847,849,851,853,855,857,859,861,1167,1129,1095,1063,9174,9177,9179,9181,9183,9185,10370,10372,10368,6328,3749,2499,2493,2487,2501,2495,2489,2503,2497,2491,1381,1383,1385,1387,4675,6914,6889,6918,6916,6924,6922,6920,4089,4091,4093,4095,4097,4099,4101,4103,4105,4107,4109,4111,4113,4115,4117,14499,14497,14501,7400,2412,2413,2414,7399,7398 };
			@Override
			public void process(Player player) {
				int x = player.getPosition().getX();
				int y = player.getPosition().getY();
				boolean ghostTown = x >= 3650 && y <= 3538;
				/*for (Item item : player.getEquipment().getItems()) {



					if(item.getId()!=-1&&!Arrays.stream(ALLOWED_ITEMS).anyMatch(i -> i == item.getId())) {
					player.sendMessage("<shad=1>@red@<img=18>Important Alert:");
					player.sendMessage("<shad=1>@red@<img=18>You are bringing one of the items that are not allowed!");
					player.sendMessage("<shad=1>@red@<img=18>Please type ::pvpitems to know what items are allowed!");
					Position[] locations = new Position[] { new Position(2375, 4021, 0), new Position(2375, 4022, 0) };
					Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
					TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
					return;
				}}

for (Item item : player.getInventory().getItems()) {



					if(item.getId()!=-1&&!Arrays.stream(ALLOWED_ITEMS).anyMatch(i -> i == item.getId())) {

					player.sendMessage("<shad=1>@red@<img=18>Important Alert:");
					player.sendMessage("<shad=1>@red@<img=18>You are bringing one of the items that are not allowed!");
					player.sendMessage("<shad=1>@red@<img=18>Please type ::pvpitems to know what items are allowed!");
					Position[] locations = new Position[] { new Position(2375, 4021, 0), new Position(2375, 4022, 0) };
					Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
					TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
					return;
				}}*/

				/*if (player.isFlying()) {
					player.getPacketSender().sendMessage("You cannot fly in the Wilderness.");
					player.setFlying(false);
					player.newStance();
				}
				if (player.isGhostWalking()) {
					player.getPacketSender().sendMessage("You cannot ghost walk in the Wilderness.");
					player.setGhostWalking(false);
					player.newStance();
				}*/
				/*
				 * boolean banditCampA = x >= 3020 && x <= 3150 && y >= 3684 && y <= 3711;
				 * boolean banditCampB = x >= 3055 && x <= 3195 && y >= 2958 && y <= 3003;
				 * if(banditCampA || banditCampB) { }
				 */
				/*if (ghostTown) {
					player.setWildernessLevel(60);

				} else {
					player.setWildernessLevel(((((y > 6400 ? y - 6400 : y) - 3520) / 8) + 1));
				}
				player.getPacketSender().sendString(42023, "" + player.getWildernessLevel());
				// player.getPacketSender().sendString(25355, "Levels:
				// "+CombatFactory.getLevelDifference(player, false) +" -
				// "+CombatFactory.getLevelDifference(player, true));
				BountyHunter.process(player);*/
			}

			@Override
			public void leave(Player player) {
				/*player.getPacketSender().sendWalkableInterface(42020, false);
				if (player.getLocation() != this) {
					player.getPacketSender().sendString(19000,
							"Combat level: " + player.getSkillManager().getCombatLevel());
					player.getUpdateFlag().flag(Flag.APPEARANCE);
				}
				PLAYERS_IN_WILD--;*/
			}

			@Override
			public void enter(Player player) {
				/*player.getPacketSender().sendInteractionOption("Attack", 2, true);
				player.getPacketSender().sendWalkableInterface(42020, true);
				player.getPacketSender().sendString(19000,
						"Combat level: " + player.getSkillManager().getCombatLevel());
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				PLAYERS_IN_WILD++;*/
			}

			@Override
			public boolean canTeleport(Player player) {
				/*if (Jail.isJailed(player.getUsername())) {
					player.getPacketSender().sendMessage("That'd be convenient.");
					return false;
				}
				if (player.getWildernessLevel() > 35) {
					if (player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR

							|| player.getRights() == PlayerRights.DEVELOPER) {
						player.getPacketSender()
								.sendMessage("@red@You've teleported out of deep Wilderness, logs have been written.");
						PlayerLogs.log(player.getUsername(), " teleported out of level " + player.getWildernessLevel()
								+ " wildy. Were in combat? " + player.getCombatBuilder().isBeingAttacked());
						return true;
					}
				//	player.getPacketSender().sendMessage("Teleport spells are blocked in this level of Wilderness.");
				//	player.getPacketSender()
				//			.sendMessage("You must be below level 20 of Wilderness to use teleportation spells.");
					return true;
				}*/
				return true;
			}

			@Override
			public void login(Player player) {
				//player.performGraphic(new Graphic(2000, 8));
			}

		/*	@Override
			public boolean canAttack(Player player, Player target) {
				int combatDifference = CombatFactory.combatLevelDifference(player.getSkillManager().getCombatLevel(),
						target.getSkillManager().getCombatLevel());
				if (combatDifference > player.getWildernessLevel() + 5
						|| combatDifference > target.getWildernessLevel() + 5) {
					player.getPacketSender()
							.sendMessage("Your combat level difference is too great to attack that player here.");
					player.getMovementQueue().reset();
					return false;
				}
				if (target.getLocation() != Location.WILDERNESS) {
					player.getPacketSender()
							.sendMessage("That player cannot be attacked, because they are not in the Wilderness.");
					player.getMovementQueue().reset();
					return false;
				}
				if (Jail.isJailed(player.getUsername())) {
					player.getPacketSender().sendMessage("You cannot do that right now.");
					return false;
				}
				if (Jail.isJailed(target.getUsername())) {
					player.getPacketSender().sendMessage("That player cannot be attacked right now.");
					return false;
				}
				return true;
			}*/
		},
		BARROWS(new int[] { 3520, 3598, 3543, 3584, 3543, 3560 }, new int[] { 9653, 9750, 3265, 3314, 9685, 9702 },
				false, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 37200)
					player.getPacketSender().sendWalkableInterface(37200, true);


			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {
				player.getPacketSender().sendWalkableInterface(37200, false);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				Barrows.killBarrowsNpc(killer, npc, true);
				return true;
			}
		},
		THE_SIX(new int[] { 2376, 2395 }, new int[] { 4711, 4731 }, true, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 37200)
					player.getPacketSender().sendWalkableInterface(37200, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {
				if (!player.doingClanBarrows()) {
					if (player.getRegionInstance() != null) {
						player.getRegionInstance().destruct();
					}
					new TheSix(player).leave(false);
				} else if (player.getCurrentClanChat() != null && player.getCurrentClanChat().doingClanBarrows()) {
					new TheSix(player).leave(false);
				}
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				boolean respawn = false;

				if (!killer.doingClanBarrows()) {
					Barrows.killBarrowsNpc(killer, npc, true);
					if (new TheSix(killer).allKilled()) {
						respawn = true;
					}
				} else if (killer.getCurrentClanChat() != null && killer.getCurrentClanChat().doingClanBarrows()) {
					for (Player p : killer.getCurrentClanChat().getMembers()) {
						if (p == null || !p.doingClanBarrows()) {
							continue;
						}
						Barrows.killBarrowsNpc(p, npc, true);
						if (new TheSix(p).allKilled()) {
							respawn = true;
						}
					}
				}

				if (respawn) {
					new TheSix(killer).spawn(killer.doingClanBarrows());
				}

				return true;
			}
		},

		INFERNAL_DEMON(new int[]{2337, 2370}, new int[]{9880, 9920}, true, true, true, false, false, true) {
			@Override
			public void enter(Player player) {

				player.sendMessage("You have entered the Infernal demon lair, proceed at your own risk!");
			}

		},

		INVADING_GAME(new int[] { 2216, 2223 }, new int[] { 4936, 4943 }, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				//if (player.getWalkableInterfaceId() != 21005)
				//	player.getPacketSender().sendWalkableInterface(21005, true);
			}
		},
		PEST_CONTROL_GAME(new int[] { 2624, 2690 }, new int[] { 2550, 2619 }, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 21100)
					player.getPacketSender().sendWalkableInterface(21100, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender()
						.sendMessage("Teleport spells are blocked on this island. Wait for the game to finish!");
				return false;
			}

			@Override
			public void leave(Player player) {
				PestControl.leave(player, true);
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2657, 2612, 0));
			}
		},
		PEST_CONTROL_BOAT(new int[] { 2660, 2663 }, new int[] { 2638, 2643 }, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 21005)
					player.getPacketSender().sendWalkableInterface(21005, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the boat before teleporting.");
				return false;
			}

			@Override
			public void leave(Player player) {
				if (player.getLocation() != PEST_CONTROL_GAME) {
					PestControl.leave(player, true);
				}
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player, true);
			}
		},
		SOULWARS(new int[] { -1, -1 }, new int[] { -1, -1 }, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {

			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender()
						.sendMessage("If you wish to leave, you must use the portal in your team's lobby.");
				return false;
			}

			@Override
			public void logout(Player player) {

			}

			@Override
			public void onDeath(Player player) {

			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {

				return false;
			}

		},
		SOULWARS_WAIT(new int[] { -1, -1 }, new int[] { -1, -1 }, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the waiting room before being able to teleport.");
				return false;
			}

			@Override
			public void logout(Player player) {
			}
		},

		FIGHT_PITS(new int[] { 2370, 2425 }, new int[] { 5133, 5167 }, true, true, true, false, false, true) {
			@Override
			public void process(Player player) {
				if (FightPit.inFightPits(player)) {
					FightPit.updateGame(player);
					if (player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
						player.getPacketSender().sendInteractionOption("Attack", 2, true);
				}
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage(
						"Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave game");
			}

			@Override
			public void leave(Player player) {
				onDeath(player);
			}

			@Override
			public void onDeath(Player player) {
				if (FightPit.getState(player) != null) {
					FightPit.removePlayer(player, "death");
				}
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				String state1 = FightPit.getState(player);
				String state2 = FightPit.getState(target);
				return state1 != null && state1.equals("PLAYING") && state2 != null && state2.equals("PLAYING");
			}
		},
		FIGHT_PITS_WAIT_ROOM(new int[] { 2393, 2404 }, new int[] { 5168, 5176 }, false, false, false, false, false,
				true) {
			@Override
			public void process(Player player) {
				FightPit.updateWaitingRoom(player);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage(
						"Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave room");
			}

			@Override
			public void leave(Player player) {
				if (player.getLocation() != FIGHT_PITS) {
					FightPit.removePlayer(player, "leave room");
				}
			}

		},
		GRAVEYARD(new int[] { 3485, 3517 }, new int[] { 3559, 3580 }, true, true, false, true, false, false) {
			@Override
			public boolean canTeleport(Player player) {
				if (player.getMinigameAttributes().getGraveyardAttributes().hasEntered()) {
					player.getPacketSender().sendInterfaceRemoval()
							.sendMessage("A spell teleports you out of the graveyard.");
					Graveyard.leave(player);
					return false;
				}
				return true;
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				return killer.getMinigameAttributes().getGraveyardAttributes().hasEntered()
						&& Graveyard.handleDeath(killer, npc);
			}

			@Override
			public void logout(Player player) {
				if (player.getMinigameAttributes().getGraveyardAttributes().hasEntered()) {
					Graveyard.leave(player);
				}
			}

			@Override
			public void onDeath(Player player) {
				Graveyard.leave(player);
			}

			@Override
			public void process(Player player) {
			}
		},
		DUEL_ARENA(new int[] { 3322, 3394, 3311, 3323, 3331, 3391 }, new int[] { 3195, 3291, 3223, 3248, 3242, 3260 },
				false, false, false, false, false, false) {
			@Override
			public void process(Player player) {
				if (player.getWalkableInterfaceId() != 201)
					player.getPacketSender().sendWalkableInterface(201, true);
				if (player.getDueling().duelingStatus == 0) {
					if (player.getPlayerInteractingOption() != PlayerInteractingOption.CHALLENGE)
						player.getPacketSender().sendInteractionOption("Challenge", 2, false);

				} else if (player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public void enter(Player player) {
				PLAYERS_IN_DUEL_ARENA++;
				player.getPacketSender().sendMessage(
						"<img=5> <col=996633>Warning! Do not stake items which you are not willing to lose.");
			}

			@Override
			public boolean canTeleport(Player player) {
				if (player.getDueling().duelingStatus == 5) {
					player.getPacketSender().sendMessage("To forfiet a duel, run to the west and use the trapdoor.");
					return false;
				}
				return true;
			}

			@Override
			public void logout(Player player) {
				boolean dc = false;
				if (player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5) {
					player.getDueling().declineDuel(player.getDueling().duelingWith > 0 ? true : false);
				} else if (player.getDueling().duelingStatus == 5) {
					if (player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						if (duelEnemy != null) {
							duelEnemy.getDueling().duelVictory();
						} else {
							dc = true;
						}
					}
				}
				player.moveTo(new Position(3368, 3268));
				if (dc) {
					World.removePlayer(player);
				}
			}

			@Override
			public void leave(Player player) {
				if (player.getDueling().duelingStatus == 5) {
					onDeath(player);
				}
				PLAYERS_IN_DUEL_ARENA--;
			}

			@Override
			public void onDeath(Player player) {
				if (player.getDueling().duelingStatus == 5) {
					if (player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						if (duelEnemy != null) {
							duelEnemy.getDueling().duelVictory();
							duelEnemy.getPacketSender().sendMessage("You won the duel! Congratulations!");
						}
					}
					PlayerLogs.log(player.getUsername(), "Has lost their duel.");
					player.getPacketSender().sendMessage("You've lost the duel.");
					player.getDueling().arenaStats[1]++;
					player.getDueling().reset();
				}
				player.moveTo(new Position(3368 + Misc.getRandom(5), 3267 + Misc.getRandom(3)));
				player.getDueling().reset();
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				if (target.getIndex() != player.getDueling().duelingWith) {
					player.getPacketSender().sendMessage("That player is not your opponent!");
					return false;
				}
				if (player.getDueling().timer != -1) {
					player.getPacketSender().sendMessage("You cannot attack yet!");
					return false;
				}
				return player.getDueling().duelingStatus == 5 && target.getDueling().duelingStatus == 5;
			}
		}, // 3340 3351
		// 3364 3333//ok continue sorry. uay
		// GALVEKMULTI(new int[]{3340, 3364
		GODWARS_DUNGEON(new int[] { 2800, 2950, 2858, 2943 }, new int[] { 5200, 5400, 5180, 5230 }, true, true, true,
				false, true, true) {
			@Override
			public void process(Player player) {

				if ((player.getPosition().getX() == 2842 && player.getPosition().getY() == 5308) // ARMADYL
						|| (player.getPosition().getX() == 2876 && player.getPosition().getY() == 5369) // BANDOS
						|| (player.getPosition().getX() == 2936 && player.getPosition().getY() == 5331) // ZAMMY
						|| (player.getPosition().getX() == 2907 && player.getPosition().getY() == 5272)) { // NORTH
					// EAST,
					// saradomin
					player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY() - 1,
							player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2842 && player.getPosition().getY() == 5296) // ARMADYL
						|| (player.getPosition().getX() == 2876 && player.getPosition().getY() == 5351) // BANDOS
						|| (player.getPosition().getX() == 2936 && player.getPosition().getY() == 5318) // ZAMMY
						|| (player.getPosition().getX() == 2907 && player.getPosition().getY() == 5258)) { // saradomin,
					// SOUTH
					// EAST
					player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY() + 1,
							player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2824 && player.getPosition().getY() == 5296) // ARMADYL
						|| (player.getPosition().getX() == 2864 && player.getPosition().getY() == 5351) // BANDOS
						|| (player.getPosition().getX() == 2918 && player.getPosition().getY() == 5318) // ZAMMY
						|| (player.getPosition().getX() == 2895 && player.getPosition().getY() == 5258)) { // saradomin,
					// SOUTH
					// WEST
					player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY() + 1,
							player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}
				if ((player.getPosition().getX() == 2824 && player.getPosition().getY() == 5308) // ARMADYL
						|| (player.getPosition().getX() == 2864 && player.getPosition().getY() == 5369) // BANDOS
						|| (player.getPosition().getX() == 2918 && player.getPosition().getY() == 5331) // ZAMMY
						|| (player.getPosition().getX() == 2895 && player.getPosition().getY() == 5272)) { // saradomin,
					// NORTH
					// WEST
					player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY() - 1,
							player.getPosition().getZ()));
					player.getMovementQueue().reset();
				}

				// if(player.getWalkableInterfaceId() != 16210)
				// player.getPacketSender().sendWalkableInterface(16210);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}

			@Override
			public void leave(Player p) {
				for (int i = 0; i < p.getMinigameAttributes().getGodwarsDungeonAttributes()
						.getKillcount().length; i++) {
					// p.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[i] =
					// 0;
					// p.getPacketSender().sendString((16216+i), "0");
				}
				// p.getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(0).setHasEnteredRoom(false);
				// p.getPacketSender().sendMessage("Your Godwars dungeon progress has been
				// reset.");
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				int index = -1;
				int npc = n.getId();
				if (npc == 6246 || npc == 6229 || npc == 6230 || npc == 6231) // Armadyl
					index = 0;
				else if (npc == 102 || npc == 3583 || npc == 115 || npc == 113 || npc == 6273 || npc == 6276
						|| npc == 6277 || npc == 6288) // Bandos
					index = 1;
				else if (npc == 6258 || npc == 6259 || npc == 6254 || npc == 6255 || npc == 6257 || npc == 6256) // Saradomin
					index = 2;
				else if (npc == 10216 || npc == 6216 || npc == 1220 || npc == 6007 || npc == 6219 || npc == 6220
						|| npc == 6221 || npc == 49 || npc == 4418) // Zamorak
					index = 3;
				if (index != -1) {
					// killer.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]++;
					// killer.getPacketSender().sendString((16216+index),
					// ""+killer.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]);
				}
				return false;
			}
		},
		NOMAD(new int[] { 3342, 3377 }, new int[] { 5839, 5877 }, true, true, false, true, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage(
						"Teleport spells are blocked here. If you'd like to leave, use the southern exit.");
				return false;
			}

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null)
					player.getRegionInstance().destruct();
				player.moveTo(new Position(1890, 3177));
				player.restart();
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if (npc.getId() == 8528) {
					Nomad.endFight(killer, true);
					return true;
				}
				return false;
			}
		},
		RECIPE_FOR_DISASTER(new int[] { 1885, 1913 }, new int[] { 5340, 5369 }, true, true, false, false, false,
				false) {

			/*
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender()
						.sendMessage("Teleport spells are blocked here. If you'd like to leave, use a portal.");
				return false;
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				RecipeForDisaster.handleNPCDeath(killer, npc);
				return true;
			}

			@Override
			public void leave(Player player) {
				if (player.getRegionInstance() != null)
					player.getRegionInstance().destruct();
				player.moveTo(new Position(3081, 3500));
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}*/
		},
		FREE_FOR_ALL_ARENA(new int[] { 2755, 2876 }, new int[] { 5512, 5627 }, true, true, true, false, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2375, 4021));
			}

			@Override
			public boolean canAttack(Player player, Player target) {
				if (target.getLocation() != FREE_FOR_ALL_ARENA) {
					player.getPacketSender().sendMessage("That player has not entered the dangerous zone yet.");
					player.getMovementQueue().reset();
					return false;
				}
				return true;
			}

			@Override
			public void enter(Player player) {
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.ATTACK) {
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
				}
			}

		},
		FREE_FOR_ALL_WAIT(new int[] { 2755, 2876 }, new int[] { 5507, 5627 }, false, false, true, false, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender()
						.sendMessage("Teleport spells are blocked here, if you wish to teleport, use the portal.");
				return false;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2815, 5511));
			}
		},
		WARRIORS_GUILD(new int[] { 2833, 2879 }, new int[] { 3531, 3559 }, false, true, true, false, false, true) {
			@Override
			public void logout(Player player) {
				if (player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
					player.moveTo(new Position(2844, 3540, 2));
				}
			}

			@Override
			public void leave(Player player) {
				player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
			}
		},
		PURO_PURO(new int[] { 2556, 2630 }, new int[] { 4281, 4354 }, false, true, true, false, false, true) {
		},
		FLESH_CRAWLERS(new int[] { 2033, 2049 }, new int[] { 5178, 5197 }, false, true, true, false, true, true) {
		},
		RUNESPAN(new int[] { 2122, 2159 }, new int[] { 5517, 5556 }, false, false, true, true, true, false) {
		},
		DICE_ZONE_MULTI(new int[] { 2844, 2867 }, new int[] { 2696, 2720 }, true, true, true, false, false, false) {
		},
		//// xyyx
		HOMEISLAND_MULTI(new int[] { 2058, 2972 }, new int[] { 2504, 2512 }, true, true, true, false, false, false) {
		},
		SKILLING_ISLAND(new int[] { 2765,2852 }, new int[] { 2569,2677 }, true, true, true, false, true, false) {
		},
		//// xyyx
		EARTHQUAKE(new int[] { 2883, 2942 }, new int[] { 5441, 5498 }, true, true, true, false, false, false) {
		},
		EXODENLOC(new int[] { 2816, 2879 }, new int[] { 2816, 2879 }, true, true, true, false, false, false) {
		},
		DEFAULT(null, null, false, true, true, true, true, true) {
		};

		Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed,
				 boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
			this.x = x;
			this.y = y;
			this.multi = multi;
			this.summonAllowed = summonAllowed;
			this.followingAllowed = followingAllowed;
			this.cannonAllowed = cannonAllowed;
			this.firemakingAllowed = firemakingAllowed;
			this.aidingAllowed = aidingAllowed;
		}

		private int[] x, y;
		private boolean multi;
		private boolean summonAllowed;
		private boolean followingAllowed;
		private boolean cannonAllowed;
		private boolean firemakingAllowed;
		private boolean aidingAllowed;

		public int[] getX() {
			return x;
		}

		public int[] getY() {
			return y;
		}

		/**
		 * MB_WYLDYWYRM(new int[]{3052, 3083}, new int[]{3929, 3963}, true, true, true,
		 * false, false, false) {}, RC_WYLDYWYRM(new int[]{3294, 3315}, new int[]{3919,
		 * 3961}, true, true, true, false, false, false) {}, CA_WYLDYWYRM(new
		 * int[]{3214, 3253}, new int[]{3594, 3639}, true, true, true, false, false,
		 * false) {}, DR_WYLDYWYRM(new int[]{3266, 3306}, new int[]{3868, 3903}, true,
		 * true, true, false, false, false) {},
		 */
		public static boolean inMulti(Character gc) {
			if (gc.getLocation() == SKILLING_ISLAND)
				return false;
			if (gc.isForceMultiArea()) {
				return true;
			}
			MapInstance mapInstance = gc.getMapInstance();
			if (mapInstance != null && mapInstance.isMultiArea(gc)) {
				return true;
			}

			int x = gc.getPosition().getX(), y = gc.getPosition().getY();
			if (x >= 2844 && x <= 2867 && y >= 2696 && y <= 2720) // merk boss
				return true;
			if (x >= 2434 && x <= 2496 && y >= 5378 && y <= 5440) // onyx
				return true;
			if (gc.getLocation() == WILDERNESS) {
				if (x >= 3250 && x <= 3302 && y >= 3905 && y <= 3925 || x >= 3020 && x <= 3055 && y >= 3684 && y <= 3711
						|| x >= 3150 && x <= 3195 && y >= 2958 && y <= 3003
						|| x >= 3645 && x <= 3715 && y >= 3454 && y <= 3550
						|| x >= 3150 && x <= 3199 && y >= 3796 && y <= 3869
						|| x >= 2994 && x <= 3041 && y >= 3733 && y <= 3790)
					return true;
				if (x >= 3336 && x <= 3371 && y >= 3792 && y <= 3819) // zulrah pinnensula
					return true;


				// wyrm multi handler
				if (x >= 3052 && x <= 3083 && y >= 3929 && y <= 3963 || x >= 3294 && x <= 3315 && y >= 3919 && y <= 3961
						|| x >= 3214 && x <= 3253 && y >= 3594 && y <= 3639
						|| x >= 3266 && x <= 3306 && y >= 3868 && y <= 3903
						|| x >= 3169 && x <= 3221 && y >= 3651 && y <= 3700
						|| x >= 3152 && x <= 3190 && y >= 3776 && y <= 3817)
					return true;
				// z x1: 3336, x2: 3371, y1: 3819, y2: 3792
			}
			if (x >= 3123 && x <= 3138 && y >= 3392 && y <= 3403 )
				return true;
			return gc.getLocation().multi;
		}

		public boolean isSummoningAllowed() {
			return summonAllowed;
		}

		public boolean isFollowingAllowed() {
			return followingAllowed;
		}

		public boolean isCannonAllowed() {
			return cannonAllowed;
		}

		public boolean isFiremakingAllowed() {
			return firemakingAllowed;
		}

		public boolean isAidingAllowed() {
			return aidingAllowed;
		}

		public static Location getLocation(Entity gc) {
			for (Location location : Location.values()) {
				if (location != Location.DEFAULT)
					if (inLocation(gc, location))
						return location;
			}
			return Location.DEFAULT;
		}

		public static boolean inLocation(Entity gc, Location location) {
			if (location == Location.DEFAULT) {
				if (getLocation(gc) == Location.DEFAULT)
					return true;
				else
					return false;
			}
			/*
			 * if(gc instanceof Player) { Player p = (Player)gc; if(location ==
			 * Location.TRAWLER_GAME) { String state = FishingTrawler.getState(p); return
			 * (state != null && state.equals("PLAYING")); } else if(location ==
			 * FIGHT_PITS_WAIT_ROOM || location == FIGHT_PITS) { String state =
			 * FightPit.getState(p), needed = (location == FIGHT_PITS_WAIT_ROOM) ? "WAITING"
			 * : "PLAYING"; return (state != null && state.equals(needed)); } else
			 * if(location == Location.SOULWARS) { return (SoulWars.redTeam.contains(p) ||
			 * SoulWars.blueTeam.contains(p) && SoulWars.gameRunning); } else if(location ==
			 * Location.SOULWARS_WAIT) { return SoulWars.isWithin(SoulWars.BLUE_LOBBY, p) ||
			 * SoulWars.isWithin(SoulWars.RED_LOBBY, p); } }
			 */
			return inLocation(gc.getPosition().getX(), gc.getPosition().getY(), location);
		}

		public static boolean inLocation(int absX, int absY, Location location) {
			int checks = location.getX().length - 1;
			for (int i = 0; i <= checks; i += 2) {
				if (absX >= location.getX()[i] && absX <= location.getX()[i + 1]) {
					if (absY >= location.getY()[i] && absY <= location.getY()[i + 1]) {
						return true;
					}
				}
			}
			return false;
		}

		public void process(Player player) {

		}

		public boolean canTeleport(Player player) {
			return true;
		}

		public void login(Player player) {

		}

		public void enter(Player player) {

		}

		public void leave(Player player) {

		}

		public void logout(Player player) {

		}

		public void onDeath(Player player) {

		}

		public boolean handleKilledNPC(Player killer, NPC npc) {
			return false;
		}

		public boolean canAttack(Player player, Player target) {
			return false;
		}

		/**
		 * SHOULD AN ENTITY FOLLOW ANOTHER ENTITY NO MATTER THE DISTANCE BETWEEN THEM?
		 **/
		public static boolean ignoreFollowDistance(Character character) {
			Location location = character.getLocation();
			return location == Location.RECIPE_FOR_DISASTER
					|| location == Location.NOMAD;
		}
	}

	public static void process(Character gc) {
		Location newLocation = Location.getLocation(gc);
		if (gc.getLocation() == newLocation) {
			if (gc.isPlayer()) {
				Player player = (Player) gc;
				gc.getLocation().process(player);
				if (Location.inMulti(player)) {
					if (player.getMultiIcon() != 1)
						player.getPacketSender().sendMultiIcon(1);
				} else if (player.getMultiIcon() == 1)
					player.getPacketSender().sendMultiIcon(0);
			}
		} else {
			Location prev = gc.getLocation();
			if (gc.isPlayer()) {
				Player player = (Player) gc;
				if (player.getMultiIcon() > 0)
					player.getPacketSender().sendMultiIcon(0);
				if (player.getWalkableInterfaceId() > 0 && player.getWalkableInterfaceId() != 37400
						&& player.getWalkableInterfaceId() != 50000)
					player.getPacketSender().sendWalkableInterface(50000, false);
				if (player.getPlayerInteractingOption() != PlayerInteractingOption.NONE)
					player.getPacketSender().sendInteractionOption("null", 2, true);
			}
			gc.setLocation(newLocation);
			if (gc.isPlayer()) {
				if ((prev == Location.ZOMBIE_LOBBY && newLocation == Location.ZOMBIE)
						|| (prev == Location.ZOMBIE && newLocation == Location.ZOMBIE_LOBBY)) {

				} else {
					prev.leave(((Player) gc));
				}
				gc.getLocation().enter(((Player) gc));
			}
		}
	}

	public static boolean goodDistance(Position start, int startSize, Position target, int targetSize, int distance) {
		if (target == null) {
			return false;
		}
		if (start.getZ() != target.getZ()) {
			return false;
		}
		final int deltaX1 = start.getX() - (target.getX() + targetSize - 1) - distance;
		final int deltaX2 = start.getX() + startSize - 1 - target.getX() + distance;
		final int deltaY1 = start.getY() + startSize - 1 - target.getY() + distance;
		final int deltaY2 = start.getY() - (target.getY() + targetSize - 1) - distance;

		boolean correctX = !(deltaX1 > 0) && !(deltaX2 < 0);
		boolean correctY = !(deltaY1 < 0) && !(deltaY2 > 0);
		return correctX && correctY;
	}
	public static boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		if (playerX == objectX && playerY == objectY)
			return true;
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
						&& ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean goodDistance(Position pos1, Position pos2, int distanceReq) {
		if (pos1.getZ() != pos2.getZ())
			return false;
		return goodDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), distanceReq);
	}

	public static boolean goodDistance(Character entity, Character entity2, int distance) {
		return goodDistance(entity.getPosition(), entity.getSize(), entity2.getPosition(), entity2.getSize(), distance);
	}


	public static int distanceTo(Position position, Position destination, int size) {
		final int x = position.getX();
		final int y = position.getY();
		final int otherX = destination.getX();
		final int otherY = destination.getY();
		int distX, distY;
		if (x < otherX)
			distX = otherX - x;
		else if (x > otherX + size)
			distX = x - (otherX + size);
		else
			distX = 0;
		if (y < otherY)
			distY = otherY - y;
		else if (y > otherY + size)
			distY = y - (otherY + size);
		else
			distY = 0;
		if (distX == distY)
			return distX + 1;
		return distX > distY ? distX : distY;
	}
}
