package com.ruse.world.content.skill.impl.old_dungeoneering;

import com.ruse.GameSettings;
import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.content.CustomObjects;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.raidsystem.RaidNPC;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.instance.impl.DungeoneeringInstance;

/**
 * yeye
 * 
 * @author Gabriel Hannason
 */
public class Dungeoneering {

	public static void start(final Player p) {
			if (p.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
			p.sendMessage("You haven't created a raid party yet, or aren't in one");
			return;
			}
			final DungeoneeringParty party = p.getMinigameAttributes().getDungeoneeringAttributes().getParty();
			party.setHeight(p.getIndex() * 4);
		// System.out.println("Started raid for: " + p.getUsername() + " Party: with height of" + party.getHeight());
		startRaid(p, party);
	}

	public static int raidCount = 0;

	private static void startRaid(Player p, DungeoneeringParty party) {
		if (raidCount != 0) {
			p.sendMessage("There is already an active raid! - please wait for them to finish");
			return;
		}
		int height = party.getHeight();
		int[] XCoordinates = new int[] { 2704, 2706, 2708, 2710, 2712, 2714, 2716, 2718, 2720, 2722, 2725, 2725 };
		int[] YCroodinates = new int[] { 2703, 2705, 2707, 2709, 2711, 2713, 2715, 2717, 2719, 2721, 2723, 2725, 2726 };
		int[] RAIDNPCS = new int[] { 2882, 6260, 51, 2006, 2883, 6247, 408, 50, 2881, 6222, 6203, 2000, 8349, 8000,
				2060, 1122, 1121, 8133, 1124, 1123, 1125, 401, 8549, 8000, 408, 1742, 408, 1742 };
		int Raidspawns = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];

		int Raidspawns1 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns2 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns3 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns4 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns5 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns6 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns7 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns8 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns9 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns10 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns11 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns12 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns13 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns14 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns15 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns16 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns17 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns18 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns19 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns20 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns21 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns22 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns23 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns24 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns25 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns26 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns27 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns28 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns29 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns30 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns31 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns32 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns33 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns34 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns35 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns36 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns37 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns38 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns39 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns40 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns41 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int Raidspawns42 = RAIDNPCS[RandomUtility.exclusiveRandom(0, RAIDNPCS.length)];
		int xpos = XCoordinates[RandomUtility.exclusiveRandom(0, XCoordinates.length)];
		int ypos = YCroodinates[RandomUtility.exclusiveRandom(0, YCroodinates.length)];
		int[] XCoordinates1 = new int[] { 2704, 2706, 2708, 2710, 2712, 2714, 2716, 2718, 2720, 2722, 2725, 2725 };
		int[] YCroodinates1 = new int[] { 2703, 2705, 2707, 2709, 2711, 2713, 2715, 2717, 2719, 2721, 2723, 2725,
				2726 };
		int xpos1 = XCoordinates1[RandomUtility.exclusiveRandom(0, XCoordinates1.length)];
		int ypos1 = YCroodinates1[RandomUtility.exclusiveRandom(0, YCroodinates1.length)];
		int[] XCoordinates2 = new int[] { 2704, 2706, 2708, 2710, 2712, 2714, 2716, 2718, 2720, 2722, 2725, 2725 };
		int[] YCroodinates2 = new int[] { 2703, 2705, 2707, 2709, 2711, 2713, 2715, 2717, 2719, 2721, 2723, 2725,
				2726 };
		int xpos2 = XCoordinates2[RandomUtility.exclusiveRandom(0, XCoordinates2.length)];
		int ypos2 = YCroodinates2[RandomUtility.exclusiveRandom(0, YCroodinates2.length)];
		int[] XCoordinates3 = new int[] { 2704, 2706, 2708, 2710, 2712, 2714, 2716, 2718, 2720, 2722, 2725, 2725 };
		int[] YCroodinates3 = new int[] { 2703, 2705, 2707, 2709, 2711, 2713, 2715, 2717, 2719, 2721, 2723, 2725,
				2726 };
		int xpos3 = XCoordinates3[RandomUtility.exclusiveRandom(0, XCoordinates3.length)];
		int ypos3 = YCroodinates3[RandomUtility.exclusiveRandom(0, YCroodinates3.length)];
		int[] XCoordinates4 = new int[] { 2704, 2706, 2708, 2710, 2712, 2714, 2716, 2718, 2720, 2722, 2725, 2725 };
		int[] YCroodinates4 = new int[] { 2703, 2705, 2707, 2709, 2711, 2713, 2715, 2717, 2719, 2721, 2723, 2725,
				2726 };
		int xpos4 = XCoordinates4[RandomUtility.exclusiveRandom(0, XCoordinates4.length)];
		int ypos4 = YCroodinates4[RandomUtility.exclusiveRandom(0, YCroodinates4.length)];

		FIRST_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns1, new Position(xpos2, ypos1, height)),
				new RaidNPC(Raidspawns2, new Position(xpos3, ypos4, height)),
				new RaidNPC(Raidspawns3, new Position(xpos4, ypos1, height)) };

		SECOND_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns4, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns5, new Position(xpos2, ypos4, height)),
				new RaidNPC(Raidspawns6, new Position(xpos3, ypos3, height)),
				new RaidNPC(Raidspawns7, new Position(xpos4, ypos2, height)) };

		THIRD_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns8, new Position(xpos1, ypos1, height)),
				new RaidNPC(Raidspawns9, new Position(xpos2, ypos, height)),
				new RaidNPC(Raidspawns10, new Position(xpos3, ypos3, height)),
				new RaidNPC(Raidspawns11, new Position(xpos4, ypos, height)) };

		FOURTH_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns12, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns13, new Position(xpos2, ypos4, height)),
				new RaidNPC(Raidspawns14, new Position(xpos3, ypos3, height)),
				new RaidNPC(Raidspawns15, new Position(xpos4, ypos2, height)) };

		FIFTH_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns39, new Position(xpos1, ypos2, height)),
				new RaidNPC(Raidspawns40, new Position(xpos4, ypos1, height)),
				new RaidNPC(Raidspawns41, new Position(xpos, ypos3, height)),
				new RaidNPC(8549, new Position(xpos1, ypos3, height)), // BOSSS
				new RaidNPC(Raidspawns42, new Position(xpos3, ypos1, height)) };

		SIX_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns20, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns21, new Position(xpos2, ypos3, height)),
				new RaidNPC(Raidspawns22, new Position(xpos3, ypos1, height)),
				new RaidNPC(Raidspawns23, new Position(xpos, ypos4, height)),
				new RaidNPC(Raidspawns24, new Position(xpos1, ypos, height)) };

		SEVEN_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns25, new Position(xpos1, ypos2, height)),
				new RaidNPC(Raidspawns26, new Position(xpos2, ypos2, height)),
				new RaidNPC(Raidspawns27, new Position(xpos3, ypos2, height)),
				new RaidNPC(Raidspawns28, new Position(xpos4, ypos3, height)),
				new RaidNPC(Raidspawns29, new Position(xpos4, ypos1, height)) };

		EIGHT_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns30, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns31, new Position(xpos2, ypos1, height)),
				new RaidNPC(Raidspawns32, new Position(xpos3, ypos2, height)),
				new RaidNPC(Raidspawns33, new Position(xpos, ypos, height)),
				new RaidNPC(Raidspawns34, new Position(xpos4, ypos4, height)) };

		NINE_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns35, new Position(xpos1, ypos, height)),
				new RaidNPC(Raidspawns36, new Position(xpos2, ypos4, height)),
				new RaidNPC(Raidspawns37, new Position(xpos2, ypos3, height)),
				new RaidNPC(Raidspawns38, new Position(xpos3, ypos2, height)) };

		TEN_ROOM = new RaidNPC[] { new RaidNPC(Raidspawns39, new Position(xpos1, ypos2, height)),
				new RaidNPC(Raidspawns40, new Position(xpos4, ypos1, height)),
				new RaidNPC(Raidspawns41, new Position(xpos, ypos3, height)),
				new RaidNPC(8549, new Position(xpos1, ypos3, height)), // BOSSS
				new RaidNPC(Raidspawns42, new Position(xpos3, ypos1, height)) };

		p.getPacketSender().sendInterfaceRemoval();
		/*
		 * if(p.getRights() != PlayerRights.DEVELOPER) {
		 * p.getPacketSender().sendMessage("Dungeoneering isn't out yet."); return; }
		 */

		party.setWave(1);

		if (party.getOwner() == null) {
			party.sendMessage("Owner is not online");
			return;
		}
		if (party.getOwner() != p) {
			p.getPacketSender().sendMessage("Only the party leader can start the raid.");
			return;
		}
		for (Player member : party.getPlayers()) {
			if (member != null) {
				member.getPacketSender().sendInterfaceRemoval();
				if (member.getSummoning().getFamiliar() != null) {
					member.getPacketSender()
							.sendMessage("You must dismiss your familiar before being allowed to enter a dungeon.");
					p.getPacketSender().sendMessage(
							"" + p.getUsername() + " has to dismiss their familiar before you can enter the dungeon.");
					return;
				}
			}
		}
		raidCount++;
		party.getPlayers().forEach(x -> {
			x.moveTo(new Position(2716, 2715, height));
		});

		initiateRoom(p, 1);

	}

	public static void updateInterface(Player player) {
		final DungeoneeringParty party = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
		int index = 29095;
		for (Player member : party.getPlayers()) {
			member.getPacketSender().sendString(29053, party.getOwner().getUsername() + "'s Group");
			member.getPacketSender().sendString(29054, "Host: " + party.getOwner().getUsername());
			member.getPacketSender().sendString(index, member.getUsername());
			index++;

		}
	}

	private static RaidNPC[] FIRST_ROOM = null;
	private static RaidNPC[] SECOND_ROOM = null;
	private static RaidNPC[] THIRD_ROOM = null;
	private static RaidNPC[] FOURTH_ROOM = null;// 4
	private static RaidNPC[] FIFTH_ROOM = null;// 5
	private static RaidNPC[] SIX_ROOM = null;// 6
	private static RaidNPC[] SEVEN_ROOM = null;// 7
	private static RaidNPC[] EIGHT_ROOM = null;// 8
	private static RaidNPC[] NINE_ROOM = null;// 9
	private static RaidNPC[] TEN_ROOM = null;// 10

	private static void initiateRoom(Player player, int index) {
		resetKC(player);
		DungeoneeringParty party = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
		int height = party.getHeight();
		switch (index) {
		case 1:
			for (RaidNPC npc : FIRST_ROOM) {
				Position pos  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;

		case 2:
			for (RaidNPC npc : SECOND_ROOM) {
				Position pos1  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos1);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;

		case 3:
			for (RaidNPC npc : THIRD_ROOM) {
				Position pos2 = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos2);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;

		case 4:
			for (RaidNPC npc : FOURTH_ROOM) {
				Position pos3  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos3);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 5:
			for (RaidNPC npc : FIFTH_ROOM) {
				Position pos4  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos4);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 6:
			for (RaidNPC npc : SIX_ROOM) {
				Position pos5 = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos5);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 7:
			for (RaidNPC npc : SEVEN_ROOM) {
				Position pos6  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos6);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 8:
			for (RaidNPC npc : EIGHT_ROOM) {
				Position pos7  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos7);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 9:
			for (RaidNPC npc : NINE_ROOM) {
				Position pos8  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos8);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		case 10:
			for (RaidNPC npc : TEN_ROOM) {
				Position pos9  = npc.getPosition().copy().setZ(height);
				NPC toSpawn = new NPC(npc.getID(), pos9);
				toSpawn.getDefinition().setAggressive(true);
				Coordinator movementCoordinator = new Coordinator(true, 7);
				toSpawn.getMovementCoordinator().setCoordinator(movementCoordinator);
				World.register(toSpawn);
				player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getNpcs().add(toSpawn);
				if (player.getLocalPlayers().size() > 1) {
					Player chosenTarget = player.getLocalPlayers()
							.get(RandomUtility.exclusiveRandom(0, player.getLocalPlayers().size()));
					toSpawn.getCombatBuilder().attack(chosenTarget);
				}
			}
			break;
		}
	}

	public static void leave(Player p, boolean resetTab, boolean leaveParty) {
		if (p.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
			p.getMinigameAttributes().getDungeoneeringAttributes().getParty().remove(p, resetTab, leaveParty);
		} else if (resetTab) {
			p.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111000);//
			p.getPacketSender().sendDungeoneeringTabIcon(false);
			p.getPacketSender().sendTab(GameSettings.QUESTS_TAB);
		}
	}

	public static void setupFloor(DungeoneeringParty party) {
		int height = party.getHeight();
		/*
		 * Spawning npcs
		 */
		NPC smuggler = new NPC(11226, new Position(party.getDungeoneeringFloor().getSmugglerPosition().getX(),
				party.getDungeoneeringFloor().getSmugglerPosition().getY(), height));
		World.register(smuggler);
		party.getNpcs().add(smuggler);
		for (NPC n : party.getDungeoneeringFloor().getNpcs()[party.getComplexity() - 1]) {
			NPC npc = new NPC(n.getId(), n.getPosition().copy().setZ(height));
			World.register(npc);
			party.getNpcs().add(npc);
		}
		/*
		 * Spawning objects
		 */
		for (GameObject obj : party.getDungeoneeringFloor().getObjects()) {
			CustomObjects.spawnGlobalObjectWithinDistance(
					new GameObject(obj.getId(), obj.getPosition().copy().setZ(height)));
		}
	}

	public static boolean doingOldDungeoneering(Player p) {
		return p.getControllerManager().getController() == null && p.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null
				&& p.getMinigameAttributes().getDungeoneeringAttributes().getParty().hasEnteredDungeon();
	}

	public static void handlePlayerDeath(Player player) {
		DungeoneeringParty party = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
		if (party.getPlayers().contains(player)) {
			if (party.getOwner().getUsername().equalsIgnoreCase(player.getUsername())) {
				leave(player, false, true); // if owner dies, reset the whole raid
			}
		} else {
			player.moveTo(new Position(2716, 2715, party.getHeight()));
			party.sendMessage(player.getUsername() + " has died and respawned.");
		}
	}

	private static final Item[] misc = { new Item(555, 121), new Item(557, 87), new Item(554, 81), new Item(565, 63),
			new Item(5678), new Item(560, 97), new Item(861, 1), new Item(892, 127), new Item(18161, 2),
			new Item(18159, 2), new Item(139, 1) };

	public static void handleNpcDeath(Player p, NPC n) {

		if (n.getPosition().getZ() == p.getPosition().getZ()) {
			DungeoneeringParty party = p.getMinigameAttributes().getDungeoneeringAttributes().getParty();
			party.setKills(party.getKills() + 1);
			party.sendMessage(party.getOwner().getUsername() + " Party's kill count is now: @blu@ " + party.getKills());
			handleWave(p, party.getKills(), party);
		}

		/*
		 * if(n.getPosition().getZ() == p.getPosition().getZ()) { DungeoneeringParty
		 * party = p.getMinigameAttributes().getDungeoneeringAttributes().getParty();
		 * if(!party.getNpcs().contains(n)) return; party.getNpcs().remove(n);
		 * party.setKills(party.getKills()+1); boolean boss = n.getId() == 2060 ||
		 * n.getId() == 8549 || n.getId() == 1382 || n.getId() == 9939; if(boss) {
		 * party.setKilledBoss(true); } party.sendFrame(37509,
		 * "Party kills: "+party.getKills()); int random = Misc.getRandom(300);
		 * if(random >= 20 && random <= 45 || boss) {
		 * GroundItemManager.spawnGroundItem(p, new GroundItem(new
		 * Item(ItemBinding.getRandomBindableItem()), n.getPosition().copy(),
		 * "Dungeoneering", false, -1, false, -1)); if(boss) { party.
		 * sendMessage("@red@The boss has been slain! Feel free to exit the dungeon via tha northern ladder."
		 * ); } } else if(random >= 100 && random <= 150) { int amt = 3000 +
		 * Misc.getRandom(10000); GroundItemManager.spawnGroundItem(p, new
		 * GroundItem(new Item(18201, amt), n.getPosition().copy(), "Dungeoneering",
		 * false, -1, false, -1)); } else if(random > 150 && random < 250)
		 * GroundItemManager.spawnGroundItem(p, new
		 * GroundItem(misc[Misc.getRandom(misc.length-1)], n.getPosition().copy(),
		 * "Dungeoneering", false, -1, false, -1)); }
		 */
	}

	private static void handleWave(Player player, int killCount, DungeoneeringParty party) {
		if (party.getWave() == 1 && killCount >= FIRST_ROOM.length) {
			party.incrementWave(1);
			initiateRoom(player, party.getWave());
			party.sendMessage("Starting wave 2!");
			return;
		}

		if (party.getWave() == 2 && killCount >= SECOND_ROOM.length) {
			party.incrementWave(1);
			initiateRoom(player, party.getWave());
			party.sendMessage("Starting wave 3!");
			return;
		}

		if (party.getWave() == 3 && killCount >= THIRD_ROOM.length) {

			party.incrementWave(1);

			initiateRoom(player, party.getWave());
			party.sendMessage("Starting wave 4!");
			return;
		}

		if (party.getWave() == 4 && killCount >= FOURTH_ROOM.length) {
			party.incrementWave(1);

			initiateRoom(player, party.getWave());
			party.sendMessage("Starting wave 5!");
			return;
		}
		if (party.getWave() == 5 && killCount >= FIFTH_ROOM.length) {
			party.incrementWave(1);
			party.sendMessage("Custom raids has been completed");
			party.teleportAndGiveKey();
		}

	}

	private static void resetKC(Player player) {
		DungeoneeringParty party = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
		party.setKills(0);
	}

	public static final int FORM_PARTY_INTERFACE = 27224;
	public static final int PARTY_INTERFACE = 26224;
	public static final int DUNGEONEERING_GATESTONE_ID = 17489;
}
