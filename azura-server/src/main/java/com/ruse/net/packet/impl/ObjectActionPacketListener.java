package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.*;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.DonateToWell;
import com.ruse.model.input.impl.EnterAmountOfLogsToAdd;
import com.ruse.model.movement.PathFinder;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.*;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.range.DwarfMultiCannon;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.holidayevents.easter2017data;
import com.ruse.world.content.minigames.impl.*;
import com.ruse.world.content.minigames.impl.Dueling.DuelRule;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.portal.portal;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.randomevents.EvilTree.EvilTreeDef;
import com.ruse.world.content.skill.impl.agility.Agility;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.content.skill.impl.construction.ConstructionActions;
import com.ruse.world.content.skill.impl.crafting.Flax;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.skill.impl.fishing.Fishing;
import com.ruse.world.content.skill.impl.fishing.Fishing.Spot;
import com.ruse.world.content.skill.impl.hunter.Hunter;
import com.ruse.world.content.skill.impl.hunter.PuroPuro;
import com.ruse.world.content.skill.impl.mining.Mining;
import com.ruse.world.content.skill.impl.mining.MiningData;
import com.ruse.world.content.skill.impl.mining.Prospecting;
import com.ruse.world.content.skill.impl.old_dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.runecrafting.Runecrafting;
import com.ruse.world.content.skill.impl.runecrafting.RunecraftingData;
import com.ruse.world.content.skill.impl.smithing.EquipmentMaking;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.content.skill.impl.thieving.Stalls;
import com.ruse.world.content.skill.impl.woodcutting.Woodcutting;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData;
import com.ruse.world.content.skill.impl.woodcutting.WoodcuttingData.Hatchet;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportLocations;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.route.RouteFinder;
import com.ruse.world.entity.impl.player.route.strategy.EntityStrategy;
import com.ruse.world.entity.impl.player.route.strategy.FixedTileStrategy;
import mysql.impl.Donation;

/**
 * This packet listener is called when a player clicked on a game object.
 *
 * @author relex lawl
 */

public class ObjectActionPacketListener implements PacketListener {

    public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234,
            FIFTH_CLICK = 228;

    public static final int OBJECT_INTERACTION_MAX_DISTANCE = 104;

    /**
     * The PacketListener logger to debug sendInformation and print out errors.
     */
    // private final static Logger logger =
    // Logger.getLogger(ObjectActionPacketListener.class);
    private static void firstClick(final Player player, Packet packet) {
        final int x = packet.readLEShortA();
        final int id = packet.readUnsignedShort();
        final int y = packet.readUnsignedShortA();
        final Position position = new Position(x, y, player.getPosition().getZ());
        final GameObject gameObject = new GameObject(id, position);

        if (id > 0 && id != 6 && (!RegionClipping.objectExists(gameObject) || !Locations.goodDistance(gameObject.getPosition(), player.getPosition(), OBJECT_INTERACTION_MAX_DISTANCE))) {
            if (player.getRights().OwnerDeveloperOnly()) {
                player.getPacketSender().sendMessage("A interaction error occured. Error code: " + id);
            } else {
                player.getPacketSender().sendMessage("Nothing interesting happens.");
            }
            return;
        }
        int distanceX = (player.getPosition().getX() - position.getX());
        int distanceY = (player.getPosition().getY() - position.getY());
        if (distanceX < 0)
            distanceX = -(distanceX);
        if (distanceY < 0)
            distanceY = -(distanceY);
        int size = distanceX > distanceY ? GameObjectDefinition.forId(id).getSizeX() : GameObjectDefinition.forId(id).getSizeY();
        if (size <= 0)
            size = 1;
        gameObject.setSize(size);
        if (player.getMovementQueue().isLockMovement())
            return;
        if (player.getRights() == PlayerRights.DEVELOPER)
            player.getPacketSender()
                    .sendMessage("First click object id; [id, position] : [" + id + ", " + position.toString() + "]");
        player.setInteractingObject(gameObject)
                .setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
                    @Override
                    public void execute() {
                        player.setPositionToFace(gameObject.getPosition());
                        if (WoodcuttingData.Trees.forId(id) != null) {
                            Woodcutting.cutWood(player, gameObject, false);
                            return;
                        }
                        if (EvilTreeDef.forId(id) != null) {
                            Woodcutting.cutWood(player, gameObject, false);
                            return;
                        }
                        if (MiningData.forRock(gameObject.getId()) != null) {
                            Mining.startMining(player, gameObject);
                            return;
                        }
                        if (!player.getControllerManager().processObjectClick1(gameObject)) {
                            return;
                        }

                        if (player.getFarming().click(player, x, y, 1))
                            return;
                        if (Runecrafting.runecraftingAltar(player, gameObject.getId())) {
                            RunecraftingData.RuneData rune = RunecraftingData.RuneData.forId(gameObject.getId());
                            if (rune == null)
                                return;
                            Runecrafting.craftRunes(player, rune);
                            return;
                        }
                        if (Agility.handleObject(player, gameObject)) {
                            return;
                        }
                        if (player.getPosition().getRegionId() == 7758) {//vod
                            player.vod.handleObject(gameObject);
                            return;
                        }
                        if (gameObject.getId() == HallsOfValor.CHEST_ID) {
                            HallsOfValor.handleObject(player);
                        }
                        if (Barrows.handleObject(player, gameObject)) {
                            return;
                        }
                        if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS
                                && WildernessObelisks.handleObelisk(gameObject.getId())) {
                            return;
                        }
                        if (ConstructionActions.handleFirstObjectClick(player, gameObject)) {
                            return;
                        }
                        if (gameObject.getDefinition() != null && gameObject.getDefinition().getName() != null
                                && gameObject.getDefinition().name.equalsIgnoreCase("door")
                                && player.getRights().OwnerDeveloperOnly()) {
                            player.getPacketSender().sendMessage("You just clicked a door. ID: " + id);
                        }
                        if (gameObject.getDefinition() != null && gameObject.getDefinition().getName() != null) {
                            if (gameObject.getDefinition().getName().toLowerCase().contains("bank")) {
                                if (player.getGameMode() == GameMode.GROUP_IRONMAN
                                        && player.getIronmanGroup() != null) {
                                    DialogueManager.start(player, 8002);
                                    player.setDialogueActionId(8002);
                                } else {
                                    player.getBank(player.getCurrentBankTab()).open();
                                }
                                return;
                            }
                        }
                        switch (id) {
                            case 13291:
                            case 22823:
                                player.sendMessage("Coming soon...");
                                // player.getUpgradeInterface().openInterface(Upgradeables.UpgradeType.WEAPON);
                                break;
                            case 41204:
                                player.setOpenedTeleports(true);
                                TeleportInterface.sendMinigameData(player, TeleportInterface.Minigames.values()[0]);
                                TeleportInterface.sendMinigamesTab(player);
                                break;
                            case 41205:
                                player.sendMessage("Coming soon...");
                                ///player.getRaidsInterface().openInterface(RaidsInterface.Raids.FURY_RAIDS);
                                break;

                            case 10014:
                                DungeoneeringParty party1 = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
                                if (party1 != null) {
                                    if (!party1.getOwner().equals(player)) {
                                        player.sendMessage("Only the party leader can start the dungeon.");
                                    } else {
                                        if (com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering.Companion.ready(party1)) {
                                            com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering dung = new com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering(party1);
                                            dung.startDungeon();
                                        } else {
                                            party1.sendMessage("Your party is not ready.");
                                        }
                                    }
                                } else {
                                    player.sendMessage("Please join a party before entering a dungeon.");
                                }
                                break;
                            case 5222:
                                trainZoneMoveHandler(player);
                                break;

                            case 41207:
                                if (player.getInventory().contains(TreasureHunter.MASTER_KEY.getId())) {
                                    player.getInventory().delete(TreasureHunter.MASTER_KEY);
                                    TreasureHunter.handleRewards(player);
                                } else {
                                    player.sendMessage("You don't have the Master Key.");
                                }
                                break;
                                //3472 9498
                                //3473 9487

                            case 22973:
                                player.setPoisonDamage(0);
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION), true);
                                player.getPacketSender().sendMessage("You have been rejuvinated.");
                                player.performGraphic(new Graphic(1310));
                                break;
                           /* case 166: // Zones gates
                            case 167:
                            case 2913:
                            case 2914:
                            case 2912:*/
                            case 2469:
                                ProgressionZone.handleGates(player, gameObject, false);
                                break;
                            case 7289:
                                if (player.getLocation() == Location.ZOMBIE) {
                                    if (player.getZombieParty() != null) {
                                        player.setDialogueActionId(71260);
                                        DialogueManager.start(player, 7126);
                                    }
                                }
                                break;


                            case 16958:
                                CurseHandler.deactivateAll(player);

                                if (player.getLocation() == Location.ZOMBIE_LOBBY) {
                                    if (player.getZombieParty() != null) {
                                        if (player.getZombieParty().getOwner().equals(player)) {
                                            player.setDialogueActionId(2012);
                                            DialogueManager.start(player, 2012);
                                        } else {
                                            player.sendMessage("Only the party leader can start the Raids [2]");
                                        }
                                    } else {
                                        player.sendMessage("You must be in a party to start the Raids [2]");
                                    }
                                }
                                break;
                           /* case 52601:
                                Stalls.stealFromAFKStall(player, id, 1);
                                break;
                            case 53654:
                                Stalls.stealFromAFKStall(player, id, 2);
                                break;
                            case 30035:
                                Stalls.stealFromAFKStall(player, id, 3);
                                break;*/
                            case 16047:
                                player.getPacketSender().sendMessage(
                                        "In order to unlock cradle of cursed you must use a @blu@Cursed@bla@ key on it.");
                                break;
                            case 16135:
                                player.getPacketSender().sendMessage(
                                        "In order to unlock gift of betrayed you must use a @blu@Betrayed@bla@ key on it.");
                                break;
                            case 16077:
                                player.getPacketSender().sendMessage(
                                        "In order to unlock grain of damned you must use a @blu@Damned@bla@ key on it.");
                                break;
                            case 16118:
                                player.getPacketSender().sendMessage(
                                        "In order to unlock box of hidden you must use a @blu@Hidden@bla@ key on it.");
                                break;
                            case 26945:
                                player.setDialogueActionId(215);
                                DialogueManager.start(player, 215);
                                break;
                            case 13615:
                                if (player.getSkillManager().getCombatLevel() < 100) {
                                    player.getPacketSender().sendMessage(
                                            "You need a combat level of @blu@100@bla@ and level @blu@50@bla@ on all non-combat skills.@red@no dungeoneering.");
                                    return;
                                }

                                for (int i = 7; i < Skill.values().length; i++) {
                                    if (i == 21 || i == 25 || i == 24 || i == 23 || i == 18)
                                        continue;
                                    if (player.getSkillManager().getMaxLevel(i) < 49) {

                                        player.getPacketSender().sendMessage(
                                                "You must be at least level 50 in every non-combat skill to do raids.");
                                        return;
                                    }
                                }

                                TeleportHandler.teleportPlayer(player, new Position(2722, 2737),
                                        player.getSpellbook().getTeleportType());
                                Dungeoneering.leave(player, false, true);
                                if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
                                    player.getMinigameAttributes().getDungeoneeringAttributes().getParty()
                                            .refreshInterface();
                                } else {
                                    player.getPacketSender().sendString(29053, "").sendString(29054, "");

                                    for (int i = 0; i < 10; i++) {
                                        player.getPacketSender().sendString(29095 + i, ""); // this should be the final
                                        // thing., added UI check for
                                        // every place now.
                                    }
                                }

                                break;
                            case 16150:
                                player.moveTo(new Position(1913, 5225, 0));
                                break;

                            // RAID CHEST REWARDS
                            // ARLO
                           /* case 59731:
                                if (player.getInventory().getFreeSlots() < 4) {
                                    player.getPacketSender()
                                            .sendMessage("You don't have enough inventory spaces. You need 4 spaces.");
                                    return;
                                }
                                Item[] common = Raids1.common;
                                Item[] uncommon = Raids1.uncommon;
                                Item[] rare = Raids1.rare;
                                if (player.getInventory().contains(13591, 1)) {
                                    player.getInventory().delete(13591, 1);
                                    int chance = RandomUtility.inclusiveRandom(0, 1000);
                                    String rarity = "";
                                    Item reward = null;
                                    if (chance < 600) {
                                        reward = common[RandomUtility.exclusiveRandom(0, common.length)];
                                        rarity = "Common";
                                    } else if (chance < 985) {
                                        reward = uncommon[RandomUtility.exclusiveRandom(0, uncommon.length)];
                                        rarity = "Uncommon";
                                    } else {
                                        reward = rare[RandomUtility.exclusiveRandom(0, rare.length)];
                                        rarity = "Very Rare";
                                    }
                                    player.getPacketSender().sendInterface(29130);
                                    player.getPacketSender().sendItemOnInterface(29132, reward.getId(), 0,
                                            reward.getAmount());
                                    int[] Items1 = new int[]{ItemDefinition.MILL_ID, 5022};
                                    int[] Items2 = new int[]{200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 2486,
                                            3052, 1624, 1622, 1620, 1618, 1632, 1516, 1514, 454, 448, 450, 452, 378, 372,
                                            7945, 384, 390, 15271, 533, 535, 537, 18830, 556, 558, 555, 554, 557, 559, 564,
                                            562, 566, 9075, 563, 561, 560, 565, 888, 890, 892, 11212, 9142, 9143, 9144,
                                            9341, 9244, 866, 867, 868, 2, 10589, 10564, 6809, 4132, 15126, 4153, 1704, 1149,
                                            4709, 4711, 4713, 4715, 4717, 4719, 4721, 4723, 4725, 4727, 4729, 4731, 4733,
                                            4735, 4737, 4739, 4746, 4748, 4750, 4752, 4754, 4756, 4758, 4760};
                                    int[] Items3 = new int[]{14733, 14732, 14734, 19111, 11137, 3907, 18351, 3140, 18353,
                                            18355, 18357, 15501, 15272, 2503, 10499, 3805, 6326, 861, 1163, 1201, 6111, 544,
                                            542, 5574, 5575, 5576, 1215, 3105, 13734, 7400, 11118, 4708, 4710, 4712, 4714,
                                            4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745,
                                            4747, 4749, 4751, 4753, 4755, 4757, 4759, 4708, 4710, 4712, 4714, 4716, 4718,
                                            4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749,
                                            4751, 4753, 4755, 4757, 4759, 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722,
                                            4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747, 4749, 4751, 4753,
                                            4755, 4757, 4759, 6199, 290};
                                    int[] LowItems = new int[]{50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 2, 4, 6,
                                            8, 10, 12, 14, 16, 20, 30, 40, 50, 60, 70, 80, 90, 100, 20, 30, 40, 50, 60, 70,
                                            80, 90, 100, 1000, 2000, 3000, 4000, 5000};
                                    int[] MedItems = new int[]{5, 10, 10, 6, 8, 4, 5, 3, 5, 7, 8, 10, 2, 3, 5, 10, 2, 3, 9, 11};
                                    int[] HighItems = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2};
                                    int Orbs = Items1[RandomUtility.exclusiveRandom(0, Items1.length)];
                                    int Skill = Items2[RandomUtility.exclusiveRandom(0, Items2.length)];
                                    int Keys = Items3[RandomUtility.exclusiveRandom(0, Items3.length)];

                                    int pickedAmt = LowItems[RandomUtility.exclusiveRandom(0, LowItems.length)];
                                    int pickedAmt2 = MedItems[RandomUtility.exclusiveRandom(0, MedItems.length)];
                                    int pickedAmt3 = HighItems[RandomUtility.exclusiveRandom(0, HighItems.length)];
                                    player.getInventory().add(Orbs, pickedAmt);
                                    player.getInventory().add(Skill, pickedAmt2);
                                    player.getInventory().add(Keys, pickedAmt3);
                                    player.getPacketSender().sendItemOnInterface(29134, Orbs, 0, pickedAmt);
                                    player.getPacketSender().sendItemOnInterface(29135, Skill, 0, pickedAmt2);
                                    player.getPacketSender().sendItemOnInterface(29136, Keys, 0, pickedAmt3);
                                    player.getInventory().add(reward);
                                    if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
                                        DungeoneeringParty party = player.getMinigameAttributes()
                                                .getDungeoneeringAttributes().getParty();
                                        party.sendMessage("<shad=1>[" + rarity + " Reward]" + "@blu@ "
                                                + player.getUsername() + " " + "opened the raids chest and got a @red@X"
                                                + reward.getAmount() + " " + reward.getDefinition().getName());
                                    }
                                } else {
                                    player.sendMessage("@red@You need a raid key to open the raid chest");
                                }
                                player.sendMessage("Clicked on the raid chest");
                                break;*/

                            case 16148:
                                player.moveTo(new Position(1863, 5239, 0));
                                break;
                            case 16149:
                                player.moveTo(new Position(2042, 5245, 0));
                                break;
                            case 16082:
                                player.moveTo(new Position(2017, 5211, 0));
                                break;
                            case 16114:

                                player.moveTo(new Position(2358, 5218, 0));
                                break;
                            case 16116:
                                player.moveTo(new Position(2149, 5278, 0));
                                break;
                            case 16115:
                                player.moveTo(new Position(2358, 5215, 0));
                                break;
                            case 16080:
                                player.moveTo(new Position(1863, 5239, 0));
                                break;
                            case 16078:
                                player.moveTo(new Position(1902, 5221, 0));
                                break;
                            case 16049:
                                player.moveTo(new Position(2120, 5257, 0));
                                break;
                            case 16081:
                                player.moveTo(new Position(2122, 5251, 0));
                                break;
                            case 16050:
                                player.moveTo(new Position(2350, 5214, 0));
                                break;
                            case 16112:
                                player.moveTo(new Position(2026, 5217, 0));
                                break;
                            case 16048:
                                player.moveTo(new Position(2147, 5284, 0));
                                break;
                            case 2305:
                                if (player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
                                    player.moveTo(new Position(3003, 10354, player.getPosition().getZ()));
                                    player.getPacketSender().sendMessage("You escape from the spikes.");
                                }
                                break;
                            case 4387://
                                player.setDialogueActionId(214);
                                DialogueManager.start(player, 214);
                                break;
                            case 4408:

                                /*
                                 * if(player.getPointsHandler().getTotalPrestiges() <= 49) {
                                 * player.getPacketSender().
                                 * sendMessage("You need 50 Total Prestiges. You only have "+
                                 * player.getPointsHandler().getTotalPrestiges()+"@bla@.");
                                 *
                                 *
                                 *
                                 * } else {
                                 * player.getPacketSender().sendMessage("You teleport to prestige portals area."
                                 * );
                                 *
                                 * player.moveTo(new Position(3191, 9825, player.getPosition().getZ())); }
                                 */
                                Position position = new Position(3168, 3490, 2);
                                player.getPacketSender().sendMessage("@red@This portal is disabled, please type ::Train!");
                                break;
                            case 4388:
                                resetInterface(player);
                                player.getPacketSender()
                                        .sendString(8144, "Tier Portal Teleports - Make your way up the tier")
                                        .sendInterface(8134);
                                player.getPacketSender().sendString(8147, "@gre@-@bla@ Tier I");
                                player.getPacketSender().sendString(8148,
                                        "@blu@-@whi@::devils   -@or2@ You need 500 NPC Kills");
                                player.getPacketSender().sendString(8150, "@gre@-@bla@ Tier II");
                                player.getPacketSender().sendString(8151,
                                        "@blu@-@whi@::lords   -@or2@ You need 100 Devil Kills");
                                player.getPacketSender().sendString(8153, "@gre@-@bla@ Tier IV");
                                player.getPacketSender().sendString(8154,
                                        "@blu@-@whi@::Demon   -@or2@ You need 200 Lord Kills");
                                player.getPacketSender().sendString(8156, "@gre@-@bla@ Tier V");
                                player.getPacketSender().sendString(8157,
                                        "@blu@-@whi@::Dragon   -@or2@ You need 300 Demon Kills");
                                player.getPacketSender().sendString(8159, "@gre@-@bla@ Tier VI");
                                player.getPacketSender().sendString(8160,
                                        "@blu@-@whi@::Beast   -@or2@ You need 400 Dragon Kills");
                                break;
                            case 589: // varrock ball
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("Just a wise old woman's ball.");
                                }
                                break;
                            case 11678:
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("Nope. Nothing special to it.");
                                }
                                break;
                            case 5595:
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("Just some toys.");
                                }
                                break;
                            case 2725:
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("Just regular fireplace things.");
                                }
                                break;
                            case 423:
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("I don't want to mess around with someone's bed.");
                                }
                                break;
                            case 11339:
                                if (Misc.easter(2017)) {
                                    if (player.getInventory().isFull()) {
                                        player.getPacketSender()
                                                .sendMessage("My inventory is too full, I should make room first.");
                                        return;
                                    }
                                    if (player.getEaster2017() == easter2017data.forObjectId(id).getRequiredProgress()) {
                                        player.getPacketSender()
                                                .sendMessage(easter2017data.forObjectId(id).getSearchMessage());
                                        player.setEaster2017(easter2017data.forObjectId(id).getRequiredProgress() + 1);
                                        player.getInventory().add(1961, 1);
                                    }
                                } else {
                                    player.getPacketSender().sendMessage("Just some gold, I can get enough on my own.");
                                }
                                break;
                            case 17953:
                                if (player.getLocation() == Location.ZULRAH_WAITING) {
                                    player.getPacketSender().sendMessage("You push the boat into the swamp...");
                                    // player.setPositionToFace(gameObject.getPosition());
                                    player.performAnimation(new Animation(923));
                                    TaskManager.submit(new Task(1, player, true) {
                                        int tick = 0;

                                        @Override
                                        public void execute() {
                                            if (tick >= 2) {
                                                // player.moveTo(new Position(player.getPosition().getX()-1,
                                                // player.getPosition().getY()));
                                                player.moveTo(new Position(3420, 2777, (player.getIndex() + 1) * 4));
                                                player.getPacketSender()
                                                        .sendMessage("...And arrive in Zulrah's territory.");
                                                stop();
                                            }
                                            tick++;
                                        }
                                    });
                                } else if (player.getLocation() == Location.ZULRAH) {
                                    if (!player.getRights().isMember()
                                            && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 85) {
                                        player.getPacketSender()
                                                .sendMessage("You need 85 Agility to navigate the boat back to camp!");
                                        return;
                                    }
                                    if (player.getRights().isMember()
                                            && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 85) {
                                        player.getPacketSender()
                                                .sendMessage("As a member you can navigate the swamp without 85 Agility.");
                                    }
                                    player.getPacketSender().sendMessage("You push the boat into the swamp...");
                                    // player.setPositionToFace(gameObject.getPosition());
                                    player.performAnimation(new Animation(923));
                                    TaskManager.submit(new Task(1, player, true) {
                                        int tick = 0;

                                        @Override
                                        public void execute() {
                                            if (tick >= 2) {
                                                // player.moveTo(new Position(player.getPosition().getX()-1,
                                                // player.getPosition().getY()));
                                                player.moveTo(new Position(3406, 2794, 0));
                                                player.getPacketSender()
                                                        .sendMessage("...And return to the pillar santuary.");
                                                stop();
                                            }
                                            tick++;
                                        }
                                    });
                                }
                                // TeleportHandler.teleportPlayer(player, new Position(3420, 2777,
                                // (player.getIndex()+1)*4), player.getSpellbook().getTeleportType()); //zulrah
                                // instance
                                break;
                            case 28295:
                                if (christmas2016.isChristmas()) {
                                    player.getPacketSender().sendMessage("Welcome to the Christmas 2016 event!");
                                    player.moveTo(christmas2016.eventStart);
                                }
                                break;
                            case 28296:
                                if (!player.getClickDelay().elapsed(1250)) {
                                    // player.getPacketSender().sendMessage("Your hands are getting cold, slow
                                    // down!");
                                    return;
                                }
                                player.getClickDelay().reset();
                                if (!player.getInventory().isFull() || (player.getInventory().getFreeSlots() == 0
                                        && player.getInventory().contains(10501))) {
                                    player.performAnimation(new Animation(827));
                                    player.getInventory().add(10501, Misc.getRandom(20));
                                    player.getPacketSender().sendMessage("You pack some of the snow together...");
                                } else {
                                    player.getPacketSender().sendMessage("You'll need some inventory space first!");
                                }
                                break;
                            case 7475:
                                if (!player.getClickDelay().elapsed(1250)) {
                                    // player.getPacketSender().sendMessage("Your hands are getting cold, slow
                                    // down!");
                                    return;
                                }
                                player.getClickDelay().reset();
                                if (player.getInventory().contains(20104) && player.getInventory().contains(20105)) {
                                    player.performAnimation(new Animation(810));
                                    player.getInventory().delete(20104, 1);
                                    player.getInventory().delete(20105, 1);
                                    player.moveTo(new Position(2584, 2575, player.getPosition().getZ()));

                                    player.getPacketSender()
                                            .sendMessage("<img=100>@cya@You make it to the next room! keep going :)");
                                } else {
                                    player.getPacketSender().sendMessage("you need Key 1 and key 2 to enter!");
                                }
                                break;
                            case 3378:
                                int[] commonsuper = new int[]{11846, 11848, 11850, 11852, 11854, 11856, 18686, 18799, 5095, 13996, 13913, 13919};
                                int[] uncommonsuper = new int[]{6927, 6928, 6929, 6930, 6931, 6932, 6933, 6935, 6936, 22077, 19136, 6936};
                                int[] raresuper = new int[]{13640, 19468, 19166, 19165, 19812, 20554, 19115, 20488,
                                        10946, 6769, 15288, 15290};
                                player.getMysteryBoxOpener().display(11795, "Custom chest key", commonsuper, uncommonsuper,
                                        raresuper);
                                break;
                            case 7476:
                                if (!player.getClickDelay().elapsed(1250)) {
                                    // player.getPacketSender().sendMessage("Your hands are getting cold, slow
                                    // down!");
                                    return;
                                }
                                player.getClickDelay().reset();
                                if (player.getInventory().contains(20106) && player.getInventory().contains(20107)) {
                                    player.performAnimation(new Animation(810));
                                    player.getInventory().delete(20106, 1);
                                    player.getInventory().delete(20107, 1);
                                    player.moveTo(new Position(2583, 2584, player.getPosition().getZ()));

                                    player.getPacketSender()
                                            .sendMessage("<img=100>@cya@You make it to the next room! keep going :)");
                                } else {
                                    player.getPacketSender().sendMessage("you need Key 3 and 4 to enter!");
                                }
                                break;
                            case 7477:
                                if (!player.getClickDelay().elapsed(1250)) {
                                    // player.getPacketSender().sendMessage("Your hands are getting cold, slow
                                    // down!");
                                    return;
                                }
                                player.getClickDelay().reset();
                                if (player.getInventory().contains(20108) && player.getInventory().contains(20109)) {
                                    player.performAnimation(new Animation(810));
                                    player.getInventory().delete(20108, 1);
                                    player.getInventory().delete(20109, 1);
                                    player.moveTo(new Position(2581, 2592, player.getPosition().getZ()));

                                    player.getPacketSender()
                                            .sendMessage("<img=100>@cya@You make it to the next room! keep going :)");
                                } else {
                                    player.getPacketSender().sendMessage("you need Key 5 and Key 6 to enter!");
                                }
                                break;
                            case 7478:
                                if (!player.getClickDelay().elapsed(1250)) {
                                    // player.getPacketSender().sendMessage("Your hands are getting cold, slow
                                    // down!");
                                    return;
                                }
                                player.getClickDelay().reset();// 20104
                                if (player.getInventory().contains(20103)) {
                                    player.performAnimation(new Animation(810));
                                    player.getInventory().delete(20103, 1);

                                    player.moveTo(new Position(2583, 2607, player.getPosition().getZ()));

                                    Doom.spawnWave1(player);

                                    player.getPacketSender()
                                            .sendMessage("<img=100>@cya@You make it to the next room! keep going :)");
                                    player.getPacketSender().sendMessage("@red@Kill the boss for a Chest key!");
                                } else {
                                    player.getPacketSender().sendMessage("you need boss room key to enter!");
                                }
                                break;
                            case 7479:
                                player.moveTo(new Position(2583, 2565, player.getPosition().getZ()));

                                player.getPacketSender().sendMessage("@red@Kill stormtrooper to get the key 1 and 2.");
                                break;

                            case 9975:
                            case 2804:
                            case 41900:
                                DialogueManager.start(player, 22);
                                player.setDialogueActionId(14);
                                break;

                            case 134:
                            case 135:
                                if (player.getPosition().getY() < 3354 && GameSettings.Halloween) {
                                    TeleportHandler.teleportPlayer(player, new Position(3109, 3354, 404),
                                            TeleportType.NORMAL);
                                }
                                boolean move = player.getPosition().getY() < 3354;
                                if (!move) {
                                    player.getPacketSender().sendMessage("Nope, it's not going to move.");
                                }

                                /*
                                 * TaskManager.submit(new Task(0, player, true) { int tick = 0;
                                 *
                                 * @Override public void execute() { tick++; if (player.getPosition().getX() >
                                 * 3106 && player.getPosition().getX() < 3111 && player.getPosition().getY() ==
                                 * 3353) { //player.getPosition().getY() > 3226 && player.getPosition().getY() <
                                 * 3229) { player.getMovementQueue().walkStep(0, 1);
                                 * player.getPacketSender().sendMessage("The heavy doors shut as you enter."); }
                                 * else if (player.getPosition().getY() > 3353) {
                                 * player.getPacketSender().sendMessage("That has no intention of moving..."); }
                                 * else { player.getPacketSender().sendMessage("You're too far away!"); }
                                 * if(tick == 1) stop(); }
                                 *
                                 * @Override public void stop() { setEventRunning(false);
                                 * //player.setCrossingObstacle(false); } }); }
                                 */
                                break;
                            case 2112:
                                if (!player.getRights().isMember()) {
                                    player.getPacketSender().sendMessage("You must be a member to access this area.");
                                    return;
                                }
                                TaskManager.submit(new Task(0, player, true) {
                                    int tick = 0;

                                    @Override
                                    public void execute() {
                                        tick++;
                                        if (player.getPosition().getX() == 3046 && player.getPosition().getY() == 9757) {
                                            player.getMovementQueue().walkStep(0, -1);
                                            player.getPacketSender()
                                                    .sendMessage("As a member, you can pass through the door.");
                                        } else if (player.getPosition().getX() == 3046
                                                && player.getPosition().getY() == 9756) {
                                            player.getMovementQueue().walkStep(0, 1);
                                            player.getPacketSender()
                                                    .sendMessage("As a member, you can pass through the door.");
                                        } else {
                                            player.getPacketSender().sendMessage("You must be in front of the door first.");
                                        }
                                        if (tick == 1)
                                            stop();
                                    }

                                    @Override
                                    public void stop() {
                                        setEventRunning(false);
                                        // player.setCrossingObstacle(false);
                                    }
                                });
                                break;
                            case 2882:
                            case 2883:
                                TaskManager.submit(new Task(0, player, true) {
                                    int tick = 0;

                                    @Override
                                    public void execute() {
                                        tick++;
                                        if (player.getPosition().getX() == 3268 && player.getPosition().getY() > 3226
                                                && player.getPosition().getY() < 3229) {
                                            player.getMovementQueue().walkStep(-1, 0);
                                            player.getPacketSender().sendMessage("You pass through the gate.");
                                        } else if (player.getPosition().getX() == 3267 && player.getPosition().getY() > 3226
                                                && player.getPosition().getY() < 3229) {
                                            player.getMovementQueue().walkStep(1, 0);
                                            player.getPacketSender().sendMessage("You pass through the gate.");
                                        } else {
                                            player.getPacketSender().sendMessage("You must be in front of the gate first.");
                                        }
                                        if (tick == 1)
                                            stop();
                                    }

                                    @Override
                                    public void stop() {
                                        setEventRunning(false);
                                        // player.setCrossingObstacle(false);
                                    }
                                });
                                break;
                            case 5262:
                                if (player.getLocation() == Location.KRAKEN) {
                                    player.getPacketSender().sendMessage("You leave the cave and end up at home.");
                                    player.moveTo(GameSettings.DEFAULT_POSITION.copy());
                                    // TeleportHandler.teleportPlayer(player, new Position(2524 +
                                    // Misc.getRandom(10), 2595 + Misc.getRandom(6)),
                                    // player.getSpellbook().getTeleportType());

                                }
                                break;

                            case 2273:
                                player.moveTo(new Position(3563, 3313, 0));
                                Location.THE_SIX.leave(player);
                                break;
                            case 5259:
                                if (player.getPosition().getX() >= 3653) { // :)
                                    if (player.getPosition().getY() != 3485 && player.getPosition().getY() != 3486) {
                                        player.getPacketSender()
                                                .sendMessage("You need to stand infront of the barrier to pass through.");
                                        return;
                                    }
                                    player.moveTo(new Position(3651, player.getPosition().getY()));
                                } else {
                                    player.setDialogueActionId(73);
                                    DialogueManager.start(player, 115);
                                }
                                break;
                            case 4470:
                                if (player.getPosition().getX() == 2459 && player.getPosition().getY() == 2862
                                        || player.getPosition().getY() == 2861) { // :)
                                    player.moveTo(new Position(2462, player.getPosition().getY()));
                                }
                                if (player.getPosition().getX() == 2462 && player.getPosition().getY() == 2862
                                        || player.getPosition().getY() == 2861) { // :)
                                    player.moveTo(new Position(2459, player.getPosition().getY()));
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("You need to stand infront of the barrier to pass through.");
                                }
                                break;
                            case 10805:
                            case 10806:
                                GrandExchange.open(player);
                                break;
                            case 38700:
                                if (gameObject.getPosition().getX() == 3668 && gameObject.getPosition().getY() == 2976) {
                                    player.getPacketSender().sendMessage(
                                            "<img=5> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
                                    player.moveTo(new Position(2815, 5511));
                                } else if (player.getLocation() == Location.FREE_FOR_ALL_WAIT) {
                                    player.moveTo(GameSettings.DEFAULT_POSITION.copy());
                                } else if (gameObject.getPosition().getX() == 2849
                                        && gameObject.getPosition().getY() == 3353) {
                                    player.getPacketSender().sendMessage(
                                            "<img=5> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
                                    player.moveTo(new Position(2815, 5511));
                                }
                                break;
                            case 2465:
                                if (player.getLocation() == Location.EDGEVILLE) {
                                    player.getPacketSender().sendMessage(
                                            "<img=5> @blu@Welcome to the free-for-all arena! You will not lose any items on death here.");
                                    player.moveTo(new Position(2815, 5511));
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("The portal does not seem to be functioning properly.");
                                }
                                break;
                            case 45803:
                            case 1767:
                                DialogueManager.start(player, 114);
                                player.setDialogueActionId(72);
                                break;
                            case 7352:
                                if (Dungeoneering.doingOldDungeoneering(player) && player.getMinigameAttributes()
                                        .getDungeoneeringAttributes().getParty().getGatestonePosition() != null) {
                                    player.moveTo(player.getMinigameAttributes().getDungeoneeringAttributes().getParty()
                                            .getGatestonePosition());
                                    player.setEntityInteraction(null);
                                    player.getPacketSender().sendMessage("You are teleported to your party's gatestone.");
                                    player.performGraphic(new Graphic(1310));
                                } else
                                    player.getPacketSender().sendMessage(
                                            "Your party must drop a Gatestone somewhere in the dungeon to use this portal.");
                                break;
                            case 7353:
                                player.moveTo(new Position(2439, 4956, player.getPosition().getZ()));
                                break;
                            case 7321:
                                player.moveTo(new Position(2452, 4944, player.getPosition().getZ()));
                                break;
                            case 7322:
                                player.moveTo(new Position(2455, 4964, player.getPosition().getZ()));
                                break;
                            case 7315:
                                player.moveTo(new Position(2447, 4956, player.getPosition().getZ()));
                                break;
                            case 7316:
                                player.moveTo(new Position(2471, 4956, player.getPosition().getZ()));
                                break;
                            case 7318:
                                player.moveTo(new Position(2464, 4963, player.getPosition().getZ()));
                                break;
                            // case 7319:
                            // player.moveTo(new Position(2467, 4940, player.getPosition().getZ()));
                            // break;
                            case 7324:
                                player.moveTo(new Position(2481, 4956, player.getPosition().getZ()));
                                break;

                            case 7319:
                                if (gameObject.getPosition().getX() == 2481 && gameObject.getPosition().getY() == 4956)
                                    player.moveTo(new Position(2467, 4940, player.getPosition().getZ()));
                                break;

                            case 11356:
                                if (gameObject.getPosition().getX() == 1764 && gameObject.getPosition().getY() == 5331) {
                                    VaultOfWar.enterEasternDungeon(player);
                                } else {
                                    player.moveTo(new Position(2860, 9741));
                                    player.getPacketSender().sendMessage("You step through the portal..");
                                }
                                break;
                            case 47180:
                                if (!player.getRights().isMember()) {
                                    player.getPacketSender().sendMessage("You must be a Member to use this.");
                                    return;
                                }
                                player.getPacketSender().sendMessage("You activate the device..");
                                player.moveTo(new Position(2586, 3912));
                                break;
                            case 10091:
                            case 8702:
                                if (gameObject.getId() == 8702) {
                                    if (!player.getRights().isMember()) {
                                        player.getPacketSender().sendMessage("You must be a Member to use this.");
                                        return;
                                    }
                                }
                                Fishing.setupFishing(player, Spot.ROCKTAIL);
                                break;
                            case 9319:
                                if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61) {
                                    player.getPacketSender().sendMessage(
                                            "You need an Agility level of at least 61 or higher to climb this");
                                    return;
                                }
                                if (player.getPosition().getZ() == 0)
                                    player.moveTo(new Position(3422, 3549, 1));
                                else if (player.getPosition().getZ() == 1) {
                                    if (gameObject.getPosition().getX() == 3447)
                                        player.moveTo(new Position(3447, 3575, 2));
                                    else
                                        player.moveTo(new Position(3447, 3575, 0));
                                }
                                break;

                            case 9320:
                                if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61) {
                                    player.getPacketSender().sendMessage(
                                            "You need an Agility level of at least 61 or higher to climb this");
                                    return;
                                }
                                if (player.getPosition().getZ() == 1)
                                    player.moveTo(new Position(3422, 3549, 0));
                                else if (player.getPosition().getZ() == 0)
                                    player.moveTo(new Position(3447, 3575, 1));
                                else if (player.getPosition().getZ() == 2)
                                    player.moveTo(new Position(3447, 3575, 1));
                                player.performAnimation(new Animation(828));
                                break;
                            case 2470:
                                if (player.getTeleblockTimer() > 0) {
                                    player.getPacketSender().sendMessage("You are teleblocked, don't die, noob.");
                                    return;
                                }
                                if (gameObject.getPosition().getX() == 2464 && gameObject.getPosition().getY() == 4782) {
                                    player.moveTo(GameSettings.DEFAULT_POSITION.copy());
                                    player.getPacketSender().sendMessage("The portal teleports you home.");
                                    return;
                                }
                                if (gameObject.getPosition().getX() == 3674 && gameObject.getPosition().getY() == 2981
                                        && GameSettings.FridayThe13th) {
                                    player.moveTo(new Position(2463, 4782));
                                    player.getPacketSender().sendMessage("Enjoy the Friday the 13th mini-event.");
                                }
                                if (gameObject.getPosition().getX() == 3674 && gameObject.getPosition().getY() == 2981
                                        && GameSettings.Halloween) {
                                    player.moveTo(new Position(3108, 3352, 4));
                                    player.getPacketSender().sendMessage("<img=5> You teleport to the event!");
                                    return;
                                }
                                break;
                            case 2274:
                                if (player.getTeleblockTimer() > 0) {
                                    player.getPacketSender().sendMessage("You are teleblocked, don't die, noob.");
                                    return;
                                }
                                if (gameObject.getPosition().getX() == 2912 && gameObject.getPosition().getY() == 5300) {
                                    player.moveTo(new Position(2914, 5300, 1));
                                } else if (gameObject.getPosition().getX() == 2914
                                        && gameObject.getPosition().getY() == 5300) {
                                    player.moveTo(new Position(2912, 5300, 2));
                                } else if (gameObject.getPosition().getX() == 3553
                                        && gameObject.getPosition().getY() == 9695) {
                                    player.moveTo(new Position(3565, 3313, 0));
                                } else if (gameObject.getPosition().getX() == 2919
                                        && gameObject.getPosition().getY() == 5276) {
                                    player.moveTo(new Position(2918, 5274));
                                } else if (gameObject.getPosition().getX() == 2918
                                        && gameObject.getPosition().getY() == 5274) {
                                    player.moveTo(new Position(2919, 5276, 1));
                                } else if (gameObject.getPosition().getX() == 3001
                                        && gameObject.getPosition().getY() == 3931
                                        || gameObject.getPosition().getX() == 3652
                                        && gameObject.getPosition().getY() == 3488) {
                                    player.moveTo(GameSettings.DEFAULT_POSITION.copy());
                                    player.getPacketSender().sendMessage("The portal teleports you home.");
                                    // } else if(gameObject.getPosition().getX() == 2914 &&
                                    // gameObject.getPosition().getY() == 5300 && (player.getAmountDonated() >= 5 ||
                                    // player.getSkillManager().getCurrentLevel(Skill.AGILITY) == 99)) {
                                    // player.getPacketSender().sendMessage("You would have access to the
                                    // shortcut.");
                                }
                                break;
                            case 28779:
                                if (player.getTeleblockTimer() > 0) {
                                    player.getPacketSender()
                                            .sendMessage("You are teleblocked, and cannot navigate the chaos tunnels.");
                                    return;
                                }
                                Position des = new Position(-1, -1);
                                for (int i = 0; i < portal.values().length; i++) {
                                    if (portal.values()[i].getLocation().getX() == gameObject.getPosition().getX()
                                            && portal.values()[i].getLocation().getY() == gameObject.getPosition().getY()) {
                                        des = new Position(portal.values()[i].getDestination().getX(),
                                                portal.values()[i].getDestination().getY(), player.getPosition().getZ());
                                        // // System.out.println("Matched on portal index "+i);
                                        break;
                                    }
                                }
                                if (des.getX() != -1 && des.getY() != -1) {
                                    player.moveTo(des);
                                } else {
                                    player.getPacketSender().sendMessage("ERROR 13754, no internals. Report on forums!");
                                }
                                /*
                                 * if(gameObject.getPosition().getX() == 3186 && gameObject.getPosition().getY()
                                 * == 5472) { player.moveTo(new Position(3192, 5471, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3192 && gameObject.getPosition().getY()
                                 * == 5472) { player.moveTo(new Position(3185, 5472, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3197 && gameObject.getPosition().getY()
                                 * == 5448) { player.moveTo(new Position(3205, 5445, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY()
                                 * == 5445) { player.moveTo(new Position(3196, 5448, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3189 && gameObject.getPosition().getY()
                                 * == 5444) { player.moveTo(new Position(3187, 5459, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3187 && gameObject.getPosition().getY()
                                 * == 5460) { player.moveTo(new Position(3190, 5444, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3178 && gameObject.getPosition().getY()
                                 * == 5460) { player.moveTo(new Position(3168, 5457, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3168 && gameObject.getPosition().getY()
                                 * == 5456) { player.moveTo(new Position(3178, 5459, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY()
                                 * == 5471) { player.moveTo(new Position(3172, 5473, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY()
                                 * == 5473) { player.moveTo(new Position(3167, 5470, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY()
                                 * == 5478) { player.moveTo(new Position(3166, 5478, 0)); } else
                                 * if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY()
                                 * == 5478) { player.moveTo(new Position(3172, 5478, 0)); // }
                                 */
                                break;
                            case 7836:
                            case 7808:
                                int amt = player.getInventory().getAmount(6055);
                                if (amt > 0) {
                                    player.getInventory().delete(6055, amt);
                                    player.getPacketSender().sendMessage("You put the weed in the compost bin.");
                                    player.getSkillManager().addExperience(Skill.FARMING, 1 * amt);
                                } else {
                                    player.getPacketSender().sendMessage("You do not have any weeds in your inventory.");
                                }
                                break;
                            case 5960: // Levers
                            case 5959:
                                if (player.getLocation() == Location.MAGEBANK_SAFE) {
                                    TeleportHandler.teleportPlayer(player, TeleportLocations.MAGEBANK_WILDY.getPos(),
                                            TeleportType.LEVER);
                                } else if (player.getWildernessLevel() >= 53
                                        && player.getLocation() == Location.WILDERNESS) {
                                    TeleportHandler.teleportPlayer(player, TeleportLocations.MAGEBANK_SAFE.getPos(),
                                            TeleportType.LEVER);
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("ERROR: 00512, P: [" + player.getPosition().getX() + ","
                                                    + player.getPosition().getY() + "," + player.getPosition().getZ()
                                                    + "] - please report this bug!");
                                }
                                break;
                            // player.setDirection(Direction.WEST);
                            // TeleportHandler.teleportPlayer(player, new Position(3090, 3475),
                            // TeleportType.LEVER);
                            // break;
                            case 5096:
                                if (gameObject.getPosition().getX() == 2644 && gameObject.getPosition().getY() == 9593)
                                    player.moveTo(new Position(2649, 9591));
                                break;

                            case 5094:
                                if (gameObject.getPosition().getX() == 2648 && gameObject.getPosition().getY() == 9592)
                                    player.moveTo(new Position(2643, 9594, 2));
                                break;

                            case 5098:
                                if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9511)
                                    player.moveTo(new Position(2637, 9517));
                                break;

                            case 5097:
                                if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9514)
                                    player.moveTo(new Position(2636, 9510, 2));
                                break;
                            case 26426:
                                // Saradomin Room
                                if (player.getPosition().getX() == 2533 && gameObject.getPosition().getX() == 2534)
                                    player.moveTo(new Position(2534, 2652, 0));
                                else if (player.getPosition().getX() == 2534 && gameObject.getPosition().getX() == 2534)
                                    player.moveTo(new Position(2533, 2652, 0));
                                    // Zamorak Room
                                else if (player.getPosition().getX() == 2521 && gameObject.getPosition().getX() == 2520)
                                    player.moveTo(new Position(2520, 2652, 0));
                                else if (player.getPosition().getX() == 2520 && gameObject.getPosition().getX() == 2520)
                                    player.moveTo(new Position(2521, 2652, 0));
                                    // Armadyl Room
                                else if (player.getPosition().getY() == 2643 && gameObject.getPosition().getY() == 2643)
                                    player.moveTo(new Position(2527, 2644, 0));
                                else if (player.getPosition().getY() == 2644 && gameObject.getPosition().getY() == 2643)
                                    player.moveTo(new Position(2527, 2643, 0));
                                    // Bandos Room
                                else if (player.getPosition().getY() == 2661 && gameObject.getPosition().getY() == 2661)
                                    player.moveTo(new Position(2527, 2660, 0));
                                else if (player.getPosition().getY() == 2660 && gameObject.getPosition().getY() == 2661)
                                    player.moveTo(new Position(2527, 2661, 0));

                                break;
                            case 26428:
                            case 26425:
                            case 26427:
                                String bossRoom = "Armadyl";
                                boolean leaveRoom = player.getPosition().getY() > 5295;
                                int index = 0;
                                Position movePos = new Position(2839, !leaveRoom ? 5296 : 5295, 2);
                                if (id == 26425) {
                                    bossRoom = "Bandos";
                                    leaveRoom = player.getPosition().getX() > 2863;
                                    index = 1;
                                    movePos = new Position(!leaveRoom ? 2864 : 2863, 5354, 2);
                                } else if (id == 26427) {
                                    bossRoom = "Saradomin";
                                    leaveRoom = player.getPosition().getX() < 2908;
                                    index = 2;
                                    movePos = new Position(leaveRoom ? 2908 : 2907, 5265);
                                } else if (id == 26428) {
                                    bossRoom = "Zamorak";
                                    leaveRoom = player.getPosition().getY() <= 5331;
                                    index = 3;
                                    movePos = new Position(2925, leaveRoom ? 5332 : 5331, 2);
                                }
                                if (!leaveRoom && (!player.getRights().isMember() && player.getMinigameAttributes()
                                        .getGodwarsDungeonAttributes().getKillcount()[index] < 20)) {
                                    if (player.getInventory().contains(22053)) {
                                        player.getInventory().delete(22053, 1);
                                        player.getPacketSender().sendMessage(
                                                "Your ecumenical key is consumed, and you pass through the door.");
                                    } else {
                                        player.getPacketSender().sendMessage("You need " + Misc.anOrA(bossRoom) + " "
                                                + bossRoom + " killcount of at least 20 to enter this room.");
                                        return;
                                    }
                                }
                                if (player.getRights().isMember()) {
                                    player.getPacketSender()
                                            .sendMessage("@red@As a member, you don't need to worry about kill count.");
                                    player.performGraphic(new Graphic(6, GraphicHeight.LOW));
                                }
                                player.moveTo(movePos);
                                player.getMinigameAttributes().getGodwarsDungeonAttributes()
                                        .setHasEnteredRoom(leaveRoom ? false : true);
                                player.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index] = 0;
                                player.getPacketSender().sendString(16216 + index, "0");
                                break;
                            case 26289:
                            case 26286:
                            case 26288:
                            case 26287:
                                if (System.currentTimeMillis() - player.getMinigameAttributes()
                                        .getGodwarsDungeonAttributes().getAltarDelay() < 600000) {
                                    player.getPacketSender().sendMessage("");
                                    player.getPacketSender()
                                            .sendMessage("You can only pray at a God's altar once every 10 minutes.");
                                    player.getPacketSender().sendMessage("You must wait another "
                                            + (int) ((600 - (System.currentTimeMillis() - player.getMinigameAttributes()
                                            .getGodwarsDungeonAttributes().getAltarDelay()) * 0.001))
                                            + " seconds before being able to do this again.");
                                    return;
                                }
                                int itemCount = id == 26289 ? Equipment.getItemCount(player, "Bandos", false)
                                        : id == 26286 ? Equipment.getItemCount(player, "Zamorak", false)
                                        : id == 26288 ? Equipment.getItemCount(player, "Armadyl", false)
                                        : id == 26287 ? Equipment.getItemCount(player, "Saradomin", false)
                                        : 0;
                                int toRestore = player.getSkillManager().getMaxLevel(Skill.PRAYER) + (itemCount * 10);
                                if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) >= toRestore) {
                                    player.getPacketSender()
                                            .sendMessage("You do not need to recharge your Prayer points at the moment.");
                                    return;
                                }
                                player.performAnimation(new Animation(645));
                                player.getSkillManager().setCurrentLevel(Skill.PRAYER, toRestore);
                                player.getMinigameAttributes().getGodwarsDungeonAttributes()
                                        .setAltarDelay(System.currentTimeMillis());
                                break;
                            case 23093:
                                if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
                                    player.getPacketSender().sendMessage(
                                            "You need an Agility level of at least 70 to go through this portal.");
                                    return;
                                }
                                if (!player.getClickDelay().elapsed(2000))
                                    return;
                                int plrHeight = player.getPosition().getZ();
                                if (plrHeight == 2)
                                    player.moveTo(new Position(2914, 5300, 1));
                                else if (plrHeight == 1) {
                                    int x = gameObject.getPosition().getX();
                                    int y = gameObject.getPosition().getY();
                                    if (x == 2914 && y == 5300)
                                        player.moveTo(new Position(2912, 5299, 2));
                                    else if (x == 2920 && y == 5276)
                                        player.moveTo(new Position(2920, 5274, 0));
                                } else if (plrHeight == 0)
                                    player.moveTo(new Position(2920, 5276, 1));
                                player.getClickDelay().reset();
                                break;
                            case 26439:
                                if (player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) <= 700
                                        && !(player.getRights().isMember())) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Constitution level of at least 70 to swim across, or be a member.");
                                    return;
                                }
                                if (player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) <= 700) {
                                    player.performGraphic(new Graphic(6, GraphicHeight.LOW));
                                    player.getPacketSender().sendMessage(
                                            "@red@You don't have 70 Constitution, but as a member you can cross anyway.");
                                }
                                if (!player.getClickDelay().elapsed(1000))
                                    return;
                                if (player.isCrossingObstacle())
                                    return;
                                final String startMessage = "You jump into the icy cold water..";
                                final String endMessage = "You climb out of the water safely.";
                                final int jumpGFX = 68;
                                final int jumpAnimation = 772;
                                player.setSkillAnimation(773);
                                player.setCrossingObstacle(true);
                                player.getUpdateFlag().flag(Flag.APPEARANCE);
                                player.performAnimation(new Animation(3067));
                                final boolean goBack2 = player.getPosition().getY() >= 5344;
                                player.getPacketSender().sendMessage(startMessage);
                                player.moveTo(new Position(2885, !goBack2 ? 5335 : 5342, 2));
                                player.setDirection(goBack2 ? Direction.SOUTH : Direction.NORTH);
                                player.performGraphic(new Graphic(jumpGFX));
                                player.performAnimation(new Animation(jumpAnimation));
                                TaskManager.submit(new Task(1, player, false) {
                                    int ticks = 0;

                                    @Override
                                    public void execute() {
                                        ticks++;
                                        player.getMovementQueue().walkStep(0, goBack2 ? -1 : 1);
                                        if (ticks >= 10)
                                            stop();
                                    }

                                    @Override
                                    public void stop() {
                                        player.setSkillAnimation(-1);
                                        player.setCrossingObstacle(false);
                                        player.getUpdateFlag().flag(Flag.APPEARANCE);
                                        player.getPacketSender().sendMessage(endMessage);
                                        player.moveTo(
                                                new Position(2885, player.getPosition().getY() < 5340 ? 5333 : 5345, 2));
                                        setEventRunning(false);
                                    }
                                });
                                player.getClickDelay().reset((System.currentTimeMillis() + 9000));
                                break;
                            case 26384:
                                if (player.isCrossingObstacle())
                                    return;
                                if (!player.getInventory().contains(2347) && !(player.getRights().isMember())) {
                                    player.getPacketSender()
                                            .sendMessage("You need to have a hammer to bang on the door with.");
                                    return;
                                }
                                if (!player.getInventory().contains(2347) && (player.getRights().isMember())) {
                                    player.getPacketSender().sendMessage(
                                            "@red@You don't have a hammer, but as a member you can enter anyway.");
                                    player.performGraphic(new Graphic(6, GraphicHeight.LOW));
                                }
                                if (player.getRights().isMember())
                                    player.setCrossingObstacle(true);
                                final boolean goBack = player.getPosition().getX() <= 2850;
                                player.performAnimation(new Animation(377));
                                TaskManager.submit(new Task(2, player, false) {
                                    @Override
                                    public void execute() {
                                        player.moveTo(new Position(goBack ? 2851 : 2850, 5333, 2));
                                        player.setCrossingObstacle(false);
                                        stop();
                                    }
                                });
                                break;
                            case 57211:
                                player.getPacketSender().sendMessage(
                                        "@red@Nobody is home. Please use the teleport under Modern Bosses to get to Nex.");
                                break;
                            case 26303:
                                if (!player.getClickDelay().elapsed(1200))
                                    return;
                                if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 70
                                        && !(player.getRights().isMember()))
                                    player.getPacketSender()
                                            .sendMessage("You need a Ranged level of at least 70 to swing across here.")
                                            .sendMessage(
                                                    "Or, you can get membership for $10 and pass without the requirement.");
                                else if (!player.getInventory().contains(9418) && !(player.getRights().isMember())) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Mithril grapple to swing across here. Explorer Jack might have one.")
                                            .sendMessage(
                                                    "Or, you can get membership for $10 and pass without the requirement.");
                                    return;
                                } else {
                                    if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 70) {
                                        player.getPacketSender().sendMessage(
                                                "@red@You don't have 70 Ranged, but as a member you can enter anyway.");
                                        player.performGraphic(new Graphic(6, GraphicHeight.LOW));
                                    }
                                    if (!(player.getInventory().contains(9418))) {
                                        player.performGraphic(new Graphic(6, GraphicHeight.LOW));
                                        player.getPacketSender().sendMessage(
                                                "@red@You don't have a Mith grapple, but as a member you can enter anyway.");
                                    }
                                    player.performAnimation(new Animation(789));
                                    TaskManager.submit(new Task(2, player, false) {
                                        @Override
                                        public void execute() {
                                            player.getPacketSender().sendMessage(
                                                    "You throw your Mithril grapple over the pillar and move across.");
                                            player.moveTo(new Position(2871,
                                                    player.getPosition().getY() <= 5270 ? 5279 : 5269, 2));
                                            stop();
                                        }
                                    });
                                    player.getClickDelay().reset();
                                }
                                break;
                            case 4493:
                                if (player.getPosition().getX() >= 3432) {
                                    player.moveTo(new Position(3433, 3538, 1));
                                }
                                break;
                            case 4494:
                                player.moveTo(new Position(3438, 3538, 0));
                                break;
                            case 4495:
                                player.moveTo(new Position(3417, 3541, 2));
                                break;
                            case 4496:
                                player.moveTo(new Position(3412, 3541, 1));
                                break;
                            case 2491:
                                player.setDialogueActionId(48);
                                DialogueManager.start(player, 87);
                                break;
                            case 25339:
                            case 25340:
                                player.moveTo(new Position(1778, 5346, player.getPosition().getZ() == 0 ? 1 : 0));
                                break;
                            case 10229:
                            case 10230:
                                boolean up = id == 10229;
                                player.performAnimation(new Animation(up ? 828 : 827));
                                player.getPacketSender().sendMessage("You climb " + (up ? "up" : "down") + " the ladder..");
                                TaskManager.submit(new Task(1, player, false) {
                                    @Override
                                    protected void execute() {
                                        player.moveTo(up ? new Position(1912, 4367) : new Position(2900, 4449));
                                        stop();
                                    }
                                });
                                break;
                            case 1568:
                                player.moveTo(new Position(3097, 9868));
                                break;
                            case 5103: // Brimhaven vines
                            case 5104:
                            case 5105:
                            case 5106:
                            case 5107:
                                if (!player.getClickDelay().elapsed(4000))
                                    return;
                                if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 30) {
                                    player.getPacketSender()
                                            .sendMessage("You need a Woodcutting level of at least 30 to do this.");
                                    return;
                                }
                                if (WoodcuttingData.getHatchet(player) < 0) {
                                    player.getPacketSender().sendMessage(
                                            "You do not have a hatchet which you have the required Woodcutting level to use.");
                                    return;
                                }
                                final Hatchet axe = Hatchet.forId(WoodcuttingData.getHatchet(player));
                                player.performAnimation(new Animation(axe.getAnim()));
                                gameObject.setFace(-1);
                                TaskManager.submit(new Task(3 + Misc.getRandom(4), player, false) {
                                    @Override
                                    protected void execute() {
                                        if (player.getMovementQueue().isMoving()) {
                                            stop();
                                            return;
                                        }
                                        int x = 0;
                                        int y = 0;
                                        if (player.getPosition().getX() == 2689 && player.getPosition().getY() == 9564) {
                                            x = 2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2691
                                                && player.getPosition().getY() == 9564) {
                                            x = -2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2683
                                                && player.getPosition().getY() == 9568) {
                                            x = 0;
                                            y = 2;
                                        } else if (player.getPosition().getX() == 2683
                                                && player.getPosition().getY() == 9570) {
                                            x = 0;
                                            y = -2;
                                        } else if (player.getPosition().getX() == 2674
                                                && player.getPosition().getY() == 9479) {
                                            x = 2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2676
                                                && player.getPosition().getY() == 9479) {
                                            x = -2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2693
                                                && player.getPosition().getY() == 9482) {
                                            x = 2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2672
                                                && player.getPosition().getY() == 9499) {
                                            x = 2;
                                            y = 0;
                                        } else if (player.getPosition().getX() == 2674
                                                && player.getPosition().getY() == 9499) {
                                            x = -2;
                                            y = 0;
                                        }
                                        CustomObjects.objectRespawnTask(player,
                                                new GameObject(-1, gameObject.getPosition().copy()), gameObject, 10);
                                        player.getPacketSender().sendMessage("You chop down the vines..");
                                        player.getSkillManager().addExperience(Skill.WOODCUTTING, 45);
                                        player.performAnimation(new Animation(65535));
                                        player.getMovementQueue().walkStep(x, y);
                                        stop();
                                    }
                                });
                                player.getClickDelay().reset();
                                break;
                            case 305:
                                if (player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT || player.getRights().equals(PlayerRights.YOUTUBER)) {
                                    boolean restore1 = player.getSpecialPercentage() < 100;
                                    if (restore1) {
                                        player.setSpecialPercentage(100);
                                        CombatSpecial.updateBar(player);
                                        player.getPacketSender().sendMessage("Your special attack energy has been restored.");
                                    }
                                    for (Skill skill : Skill.values()) {
                                        int increase = skill != Skill.PRAYER && skill != Skill.CONSTITUTION
                                                && skill != Skill.ATTACK && skill != Skill.STRENGTH && skill != Skill.SUMMONING
                                                ? 0
                                                : 0;
                                        if (player.getSkillManager().getCurrentLevel(
                                                skill) < (player.getSkillManager().getMaxLevel(skill) + increase))
                                            player.getSkillManager().setCurrentLevel(skill,
                                                    (player.getSkillManager().getMaxLevel(skill) + increase));
                                    }
                                    player.performAnimation(new Animation(2112));
                                    player.performGraphic(new Graphic(1302));
                                    player.getPacketSender().sendMessage("You have restored yourself.");
                                } else {
                                    player.getPacketSender().sendMessage("You do not have the required rank to do this.");
                                }
                                break;

                            case 29942:
                                if (player.getSkillManager().getCurrentLevel(Skill.SUMMONING) == player.getSkillManager()
                                        .getMaxLevel(Skill.SUMMONING)) {
                                    player.getPacketSender()
                                            .sendMessage("You do not need to recharge your Summoning points right now.");
                                    return;
                                }
                                player.performGraphic(new Graphic(1517));
                                player.getSkillManager().setCurrentLevel(Skill.SUMMONING,
                                        player.getSkillManager().getMaxLevel(Skill.SUMMONING), true);
                                player.getPacketSender().sendString(18045,
                                        " " + player.getSkillManager().getCurrentLevel(Skill.SUMMONING) + "/"
                                                + player.getSkillManager().getMaxLevel(Skill.SUMMONING));
                                player.getPacketSender().sendMessage("You recharge your Summoning points.");
                                break;
                            case 57225:
                                if (!player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
                                    player.setDialogueActionId(44);
                                    DialogueManager.start(player, 79);
                                } else {
                                    player.moveTo(new Position(2906, 5204));
                                    player.getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(false);
                                }
                                break;
                            // open dialogueopti
                            // Trying to make this object open a dialouge.
                            // and when clicking on the fi

                            case 9294:
                                if (player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 80) {
                                    player.getPacketSender()
                                            .sendMessage("You need an Agility level of at least 80 to use this shortcut.");
                                    return;
                                }
                                player.performAnimation(new Animation(769));
                                TaskManager.submit(new Task(1, player, false) {
                                    @Override
                                    protected void execute() {
                                        player.moveTo(
                                                new Position(player.getPosition().getX() >= 2880 ? 2878 : 2880, 9813));
                                        stop();
                                    }
                                });
                                break;
                            case 9293:
                                if (!player.getRights().isMember()
                                        && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
                                    player.getPacketSender()
                                            .sendMessage("You must have at least 70 Agility to use this shortcut.");
                                    return;
                                }
                                if (player.getRights().isMember()
                                        && player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
                                    player.getPacketSender().sendMessage(
                                            "You do not have 70 Agility, but as a member you can pass anyway.");
                                }
                                boolean back = player.getPosition().getX() > 2888;
                                player.moveTo(back ? new Position(2886, 9799) : new Position(2891, 9799));
                                break;
                            case 2320:
                                back = player.getPosition().getY() == 9969 || player.getPosition().getY() == 9970;
                                player.moveTo(back ? new Position(3120, 9963) : new Position(3120, 9969));
                                break;
                            case 1755:
                                player.performAnimation(new Animation(828));
                                player.getPacketSender().sendMessage("You climb the ladder..");
                                TaskManager.submit(new Task(1, player, false) {
                                    @Override
                                    protected void execute() {
                                        if (gameObject.getPosition().getX() == 2547
                                                && gameObject.getPosition().getY() == 9951) {
                                            player.moveTo(new Position(2548, 3551));
                                        } else if (gameObject.getPosition().getX() == 3005
                                                && gameObject.getPosition().getY() == 10363) {
                                            player.moveTo(new Position(3005, 3962));
                                        } else if (gameObject.getPosition().getX() == 3084
                                                && gameObject.getPosition().getY() == 9672) {
                                            player.moveTo(new Position(3117, 3244));
                                        } else if (gameObject.getPosition().getX() == 3097
                                                && gameObject.getPosition().getY() == 9867) {
                                            player.moveTo(new Position(3094, 3480));
                                        } else if (gameObject.getPosition().getX() == 3209
                                                && gameObject.getPosition().getY() == 9616) {
                                            player.moveTo(new Position(3210, 3216));
                                        }
                                        stop();
                                    }
                                });
                                break;
                            case 28742:
                                player.performAnimation(new Animation(827));
                                player.getPacketSender().sendMessage("You enter the trapdoor..");
                                TaskManager.submit(new Task(1, player, false) {
                                    @Override
                                    protected void execute() {
                                        player.moveTo(new Position(3209, 9617));
                                        stop();
                                    }
                                });
                                break;
                            case 5110:
                                player.moveTo(new Position(2647, 9557));
                                player.getPacketSender().sendMessage("You pass the stones..");
                                break;
                            case 5111:
                                player.moveTo(new Position(2649, 9562));
                                player.getPacketSender().sendMessage("You pass the stones..");
                                break;
                            case 6434:
                                player.performAnimation(new Animation(827));
                                player.getPacketSender().sendMessage("You enter the trapdoor..");
                                TaskManager.submit(new Task(1, player, false) {
                                    @Override
                                    protected void execute() {
                                        player.moveTo(new Position(3085, 9672));
                                        stop();
                                    }
                                });
                                break;
                            case 19187:
                            case 19175:
                                Hunter.dismantle(player, gameObject);
                                break;
                            case 25029:
                                PuroPuro.goThroughWheat(player, gameObject);
                                break;
                            case 47976:
                                Nomad.endFight(player, false);
                                break;
                            case 2182:
                                if (!player.getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
                                    player.getPacketSender()
                                            .sendMessage("You have no business with this chest. Talk to the Gypsy first!");
                                    return;
                                }
                                RecipeForDisaster.openRFDShop(player);
                                break;
                            case 12356:
                                if (!player.getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
                                    player.getPacketSender()
                                            .sendMessage("You have no business with this portal. Talk to the Gypsy first!");
                                    return;
                                }
                                if (player.getPosition().getZ() > 0) {
                                    RecipeForDisaster.leave(player);
                                } else {
                                    player.getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(1,
                                            true);
                                    RecipeForDisaster.enter(player);
                                }
                                break;
                            case 9369:
                                if (player.getPosition().getY() > 5175) {
                                    FightPit.addPlayer(player);
                                } else {
                                    FightPit.removePlayer(player, "leave room");
                                }
                                break;
                            case 9368:
                                if (player.getPosition().getY() < 5169) {
                                    FightPit.removePlayer(player, "leave game");
                                }
                                break;
                            case 9356:
                                FightCave.enterCave(player);
                                break;
                            case 6704:
                                player.moveTo(new Position(3577, 3282, 0));
                                break;
                            case 6706:
                                player.moveTo(new Position(3554, 3283, 0));
                                break;
                            case 6705:
                                player.moveTo(new Position(3566, 3275, 0));
                                break;
                            case 6702:
                                player.moveTo(new Position(3564, 3289, 0));
                                break;
                            case 6703:
                                player.moveTo(new Position(3574, 3298, 0));
                                break;
                            case 6707:
                                player.moveTo(new Position(3556, 3298, 0));
                                break;
                            case 3203:
                                if (player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 5) {
                                    if (Dueling.checkRule(player, DuelRule.NO_FORFEIT)) {
                                        player.getPacketSender().sendMessage("Forfeiting has been disabled in this duel.");
                                        return;
                                    }
                                    player.getCombatBuilder().reset(true);
                                    if (player.getDueling().duelingWith > -1) {
                                        Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
                                        if (duelEnemy == null)
                                            return;
                                        duelEnemy.getCombatBuilder().reset(true);
                                        duelEnemy.getMovementQueue().reset();
                                        duelEnemy.getDueling().duelVictory();
                                    }
                                    player.moveTo(new Position(3368 + Misc.getRandom(5), 3267 + Misc.getRandom(3), 0));
                                    player.getDueling().reset();
                                    player.getCombatBuilder().reset(true);
                                    player.restart();
                                }
                                break;
                            case 14315:
                                PestControl.boardBoat(player);
                                break;
                            case 14314:
                                if (player.getLocation() == Location.PEST_CONTROL_BOAT) {
                                    player.getLocation().leave(player);
                                }
                                break;
                            case 2145:
                                player.getPacketSender().sendMessage("There's no good reason to disturb that.");
                                break;
                            case 1738:
                                if (gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY() == 3207
                                        && player.getPosition().getZ() == 0) {
                                    player.moveTo(
                                            new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
                                } else if (player.getLocation() == Location.WARRIORS_GUILD) {
                                    player.moveTo(new Position(2840, 3539, 2));
                                }
                                break;
                            case 1739:
                                if (player.getLocation() == Location.LUMBRIDGE) {
                                    // player.moveTo(teleportTarget)
                                    // player.setDialogueActionId(154);
                                    // DialogueManager.start(player, 154);
                                    player.moveTo(
                                            new Position(player.getPosition().getX(), player.getPosition().getY(), 2));
                                }
                            case 15638:
                                if (player.getLocation() == Location.WARRIORS_GUILD) {
                                    player.moveTo(new Position(2840, 3539, 0));
                                }
                                break;
                            case 1740:
                                if (player.getLocation() == Location.LUMBRIDGE) {
                                    player.moveTo(
                                            new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
                                }
                                break;
                            case 15644:
                            case 15641:
                                switch (player.getPosition().getZ()) {
                                    case 0:
                                        player.moveTo(new Position(2855, player.getPosition().getY() >= 3546 ? 3545 : 3546));
                                        break;
                                    case 2:
                                        if (player.getPosition().getX() == 2846) {
                                            if (player.getInventory().getAmount(8851) < 70) {
                                                player.getPacketSender()
                                                        .sendMessage("You need at least 70 tokens to enter this area.");
                                                return;
                                            }
                                            DialogueManager.start(player, WarriorsGuild.warriorsGuildDialogue(player));
                                            player.moveTo(new Position(2847, player.getPosition().getY(), 2));
                                            WarriorsGuild.handleTokenRemoval(player);
                                        } else if (player.getPosition().getX() == 2847) {
                                            WarriorsGuild.resetCyclopsCombat(player);
                                            player.moveTo(new Position(2846, player.getPosition().getY(), 2));
                                            player.getMinigameAttributes().getWarriorsGuildAttributes()
                                                    .setEnteredTokenRoom(false);
                                        }
                                        break;
                                }
                                break;
                            case 28714:
                                player.performAnimation(new Animation(828));
                                player.delayedMoveTo(new Position(2655, 4017), 2);
                                break;
                            case 26933:
                                player.performAnimation(new Animation(827));
                                player.delayedMoveTo(new Position(3096, 9867), 2);
                                break;
                            case 1746:
                                player.performAnimation(new Animation(827));
                                player.delayedMoveTo(new Position(2209, 5348), 2);
                                break;
                            case 19191:
                            case 19189:
                            case 19180:
                            case 19184:
                            case 19182:
                            case 19178:
                                Hunter.lootTrap(player, gameObject);
                                break;
                            case 13493:
                                if (!player.getRights().isMember()) {
                                    player.getPacketSender().sendMessage("You must be a Member to use this.");
                                    return;
                                }
                                double c = Math.random() * 100;
                                int reward = c >= 70 ? 13003
                                        : c >= 45 ? 4131
                                        : c >= 35 ? 1113
                                        : c >= 25 ? 1147
                                        : c >= 18 ? 1163 : c >= 12 ? 1079 : c >= 5 ? 1201 : 1127;
                                Stalls.stealFromStall(player, gameObject, 95, 121, new Item(reward), "You stole some rune equipment.");
                                break;
                            case 22772:
                                Stalls.stealFromStall(player, gameObject, 1, 50, new Item(ItemDefinition.COIN_ID, 1000 + Misc.getRandom(4000)), "You stole some coins.");
                                break;
                            case 22774:
                                Stalls.stealFromStall(player, gameObject, 90, 250, new Item(ItemDefinition.COIN_ID, 3000 + Misc.getRandom(7000)), "You stole some coins.");
                                break;
                            case 30205:
                                player.setDialogueActionId(11);
                                DialogueManager.start(player, 20);
                                break;
                            case 28716:
                                if (!player.busy()) {
                                    player.getSkillManager().updateSkill(Skill.SUMMONING);
                                    player.getPacketSender().sendInterface(63471);
                                } else
                                    player.getPacketSender()
                                            .sendMessage("Please finish what you're doing before opening this.");
                                break;
                            case 6:
                                DwarfCannon cannon = player.getCannon();
                                if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
                                    player.getPacketSender().sendMessage("This is not your cannon!");
                                } else {
                                    DwarfMultiCannon.startFiringCannon(player, cannon);
                                }
                                break;
                            case 2:
                                player.moveTo(new Position(player.getPosition().getX() > 2690 ? 2687 : 2694, 3714));
                                player.getPacketSender().sendMessage("You walk through the entrance..");
                                break;
                            case 2026:
                            case 2028:
                            case 2029:
                            case 2030:
                            case 2031:
                                player.setEntityInteraction(gameObject);
                                Fishing.setupFishing(player, Fishing.forSpot(gameObject.getId(), false));
                                return;
                            case 12692:
                            case 2783:
                            case 2782:
                            case 4306:
                                player.setInteractingObject(gameObject);
                                EquipmentMaking.handleAnvil(player);
                                break;
                            case 2732:
                            case 11404:
                            case 11406:
                            case 11405:
                            case 20000:
                            case 20001:
                                EnterAmountOfLogsToAdd.openInterface(player);
                                break;
                            case 24343:
                            case 409:
                            case 27661:
                            case 2640:
                            case 36972:
                                player.performAnimation(new Animation(645));
                                if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                        .getMaxLevel(Skill.PRAYER)) {
                                    player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                            player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                    player.getPacketSender().sendMessage("You recharge your Prayer points.");
                                }
                                break;
                            case 13192:
                                player.performAnimation(new Animation(645));
                                if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                        .getMaxLevel(Skill.PRAYER)) {
                                    player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                            player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                    player.getPacketSender().sendMessage("You recharge your Prayer points.");
                                }

                                break;
                            case 8749:
                                boolean restore = player.getSpecialPercentage() < 100;
                                if (restore) {
                                    player.setSpecialPercentage(100);
                                    CombatSpecial.updateBar(player);
                                    player.getPacketSender().sendMessage("Your special attack energy has been restored.");
                                }
                                for (Skill skill : Skill.values()) {
                                    int increase = skill != Skill.PRAYER && skill != Skill.CONSTITUTION
                                            ? 19 : 0;
                                    if (player.getSkillManager().getCurrentLevel(
                                            skill) < (player.getSkillManager().getMaxLevel(skill) + increase))
                                        player.getSkillManager().setCurrentLevel(skill,
                                                (player.getSkillManager().getMaxLevel(skill) + increase));
                                }
                                player.performGraphic(new Graphic(1302));
                                player.getPacketSender().sendMessage("Your stats have received a major buff.");
                                break;
                            case 4859:
                                player.performAnimation(new Animation(645));
                                if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                        .getMaxLevel(Skill.PRAYER)) {
                                    player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                            player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                    player.getPacketSender().sendMessage("You recharge your Prayer points.");
                                }
                                break;
                            case 411:
                                if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 30) {
                                    player.getPacketSender()
                                            .sendMessage("You need a Defence level of at least 30 to use this altar.");
                                    return;
                                }
                                player.performAnimation(new Animation(645));
                                if (player.getPrayerbook() == Prayerbook.NORMAL) {
                                    player.getPacketSender()
                                            .sendMessage("You sense a surge of power flow through your body!");
                                    player.setPrayerbook(Prayerbook.CURSES);
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("You sense a surge of purity flow through your body!");
                                    player.setPrayerbook(Prayerbook.NORMAL);
                                }
                                player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB,
                                        player.getPrayerbook().getInterfaceId());
                                PrayerHandler.deactivateAll(player);
                                CurseHandler.deactivateAll(player);
                                CurseHandler.startDrain(player);
                                PrayerHandler.startDrain(player);
                                break;
                            case 6552:
                                player.performAnimation(new Animation(645));
                                player.setSpellbook(player.getSpellbook() == MagicSpellbook.ANCIENT ? MagicSpellbook.NORMAL
                                        : MagicSpellbook.ANCIENT);
                                player.getPacketSender()
                                        .sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                                        .sendMessage("Your magic spellbook is changed..");
                                Autocasting.resetAutocast(player, true);
                                break;
                            case 410:
                                if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 40) {
                                    player.getPacketSender()
                                            .sendMessage("You need a Defence level of at least 40 to use this altar.");
                                    return;
                                }
                                player.performAnimation(new Animation(645));
                                player.setSpellbook(player.getSpellbook() == MagicSpellbook.LUNAR ? MagicSpellbook.NORMAL
                                        : MagicSpellbook.LUNAR);
                                player.getPacketSender()
                                        .sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                                        .sendMessage("Your magic spellbook is changed..");
                                ;
                                Autocasting.resetAutocast(player, true);
                                break;
                            case 452:
                                player.getPacketSender().sendMessage("There's no ore in that rock.");
                                break;
                           /* case 172:
                                int[] commonpvm = new int[]{13734, 3140, 4087, 2631, 2643, 9470, 2570, 11710, 11712,
                                        11714, 15332, 6918, 6916, 6924, 6922, 6920, 7400, 7399, 7398};
                                int[] uncommonpvm = new int[]{6199, 18350, 18356, 18352, 18354, 7956, 290, 989};
                                int[] rarepvm = new int[]{6571, 11137, 3905, 20535, 17704, 11716, 455, 3321, 3319,
                                        3322, 3320, 3318, 3323, 10946};
                                player.getMysteryBoxOpener().display(989, "Crystal Key", commonpvm, uncommonpvm, rarepvm);
                                break;*/
                            // CrystalChest.handleChest(player, gameObject, false);
                            // break;

                            case 6910:
                            case 4483:
                            case 3193:
                            case 2213:
                            case 11758:
                            case 14367:
                                player.sendMessage(player.getGameMode().toString() + ", " + player.getIronmanGroup());
                                if (player.getGameMode() == GameMode.GROUP_IRONMAN
                                        && player.getIronmanGroup() != null) {
                                    DialogueManager.start(player, 8002);
                                    player.setDialogueActionId(8002);
                                } else {
                                    player.getBank(player.getCurrentBankTab()).open();
                                }
                                break;
                            case 42192:
                            case 75:
                            case 26972:
                            case 11338:
                            case 19230:
                            case 59732:
                            case 25808:
                            case 6084:
                            case 3194:
                                player.sendMessage(player.getGameMode().toString() + ", " + player.getIronmanGroup());
                                if (player.getGameMode() == GameMode.GROUP_IRONMAN
                                        && player.getIronmanGroup() != null) {
                                    DialogueManager.start(player, 8002);
                                    player.setDialogueActionId(8002);
                                } else {
                                    player.getBank(player.getCurrentBankTab()).open();
                                }
                                break;

                            case 11666:
                            case 23963:
                                Smelting.openInterface(player);
                                break;
                        }
                    }

                }));
    }

    private static void secondClick(final Player player, Packet packet) {
        final int id = packet.readLEShortA();
        final int y = packet.readLEShort();
        final int x = packet.readUnsignedShortA();
        final Position position = new Position(x, y, player.getPosition().getZ());
        final GameObject gameObject = new GameObject(id, position);
        if (id > 0 && id != 6 && (!RegionClipping.objectExists(gameObject) || !Locations.goodDistance(gameObject.getPosition(), player.getPosition(), OBJECT_INTERACTION_MAX_DISTANCE))) {
            // player.getPacketSender().sendMessage("An error occured. Error code:
            // "+id).sendMessage("Please report the error to a staff member.");
            return;
        }
        player.setPositionToFace(gameObject.getPosition());
        int distanceX = (player.getPosition().getX() - position.getX());
        int distanceY = (player.getPosition().getY() - position.getY());
        if (distanceX < 0)
            distanceX = -(distanceX);
        if (distanceY < 0)
            distanceY = -(distanceY);
        int size = distanceX > distanceY ? distanceX : distanceY;
        gameObject.setSize(size);
        if (player.getRights() == PlayerRights.DEVELOPER)
            player.getPacketSender()
                    .sendMessage("Second click object id; [id, position] : [" + id + ", " + position.toString() + "]");
        player.setInteractingObject(gameObject)
                .setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
                    public void execute() {
                        if (MiningData.forRock(gameObject.getId()) != null) {
                            Prospecting.prospectOre(player, id);
                            return;
                        }
                        if (!player.getControllerManager().processObjectClick2(gameObject)) {
                            return;
                        }
                        if (player.getFarming().click(player, x, y, 1))
                            return;
                        if (player.getPosition().getRegionId() == 7758) {//vod
                            player.vod.handleObject(gameObject);
                            return;
                        }
                        switch (gameObject.getId()) {
                            case 2469:
                                ProgressionZone.handleGates(player, gameObject, true);
                                break;
                            case 12100:
                                Smelting.openInterface(player);
                                break;
                            case 13192:
                                if (player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager()
                                        .getMaxLevel(Skill.PRAYER)) {
                                    player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                                            player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
                                    player.getPacketSender().sendMessage("You recharge your Prayer points.");
                                }

                                if (player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 30) {
                                    player.getPacketSender()
                                            .sendMessage("You need a Defence level of at least 30 to use this altar.");
                                    return;
                                }
                                player.performAnimation(new Animation(645));
                                if (player.getPrayerbook() == Prayerbook.NORMAL) {
                                    player.getPacketSender().sendMessage("You sense a surge of power flow through your body!");
                                    player.setPrayerbook(Prayerbook.CURSES);
                                    CurseHandler.deactivateAll(player);
                                    PrayerHandler.deactivateAll(player);
                                } else if (player.getPrayerbook() == Prayerbook.CURSES) {
                                    player.getPacketSender().sendMessage("You sense a surge of holiness flow through your body!");
                                    player.setPrayerbook(Prayerbook.HOLY);
                                    CurseHandler.deactivateAll(player);
                                    PrayerHandler.deactivateAll(player);
                                } else {
                                    player.getPacketSender()
                                            .sendMessage("You sense a surge of purity flow through your body!");
                                    player.setPrayerbook(Prayerbook.NORMAL);
                                    CurseHandler.deactivateAll(player);
                                    PrayerHandler.deactivateAll(player);
                                }
                                player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB,
                                        player.getPrayerbook().getInterfaceId());
                                PrayerHandler.deactivateAll(player);
                                CurseHandler.deactivateAll(player);
                                CurseHandler.startDrain(player);
                                PrayerHandler.startDrain(player);
                                player.switchedPrayerBooks = true;


                                break;
                            /*case 2562:
                                if (player.getAmountDonated() <= 5000) {
                                    player.sendMessage("You need $5k+ total claim to use this!");
                                    return;
                                }
                                if (player.getAmountDonated() >= 5000) {
                                    Stalls.stealFromAFKStall(player, id, 4);
                                }
                                break;*/
                            case 39644:
                            case -25892:
                            case 39643:
                            case -25893:
                                switch (Misc.getRandom(9)) {
                                    case 0:
                                        player.getPacketSender().sendInterface(28130);
                                        player.getPacketSender().sendMessage("You have been booty trapped!");
                                        break;
                                    case 1:
                                        player.moveTo(new Position(2439, 2845, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 2:
                                        player.moveTo(new Position(2448, 2841, 0));
                                        player.getPacketSender().sendMessage("oopsie!");
                                        break;
                                    case 3:
                                        player.moveTo(new Position(2448, 2843, 0));
                                        player.getPacketSender().sendMessage("you did it again!");
                                        break;
                                    case 4:
                                        player.moveTo(new Position(2439, 2845, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 5:
                                        player.getPacketSender().sendInterface(27130);
                                        player.getPacketSender().sendMessage("piccolo died!");
                                        break;
                                    case 6:
                                        Position[] locations = new Position[]{new Position(2434, 2820, 0),
                                                new Position(2436, 2818, 0), new Position(2491, 2818, 0),
                                                new Position(2493, 2820, 0), new Position(2491, 2877, 0),
                                                new Position(2493, 2875, 0), new Position(2434, 2875, 0),
                                                new Position(2436, 2877, 0)};
                                        Position teleportLocation = locations[RandomUtility.exclusiveRandom(0,
                                                locations.length)];

                                        TeleportHandler.teleportPlayer(player, teleportLocation,
                                                player.getSpellbook().getTeleportType());
                                        player.getPacketSender().sendMessage("EEEEK!");
                                        player.forceChat("eek!");
                                        break;
                                    case 7:
                                        player.moveTo(new Position(2438, 2839, 0));
                                        player.getPacketSender().sendMessage("almost there!");
                                        break;
                                    case 8:
                                        player.moveTo(new Position(2444, 2836, 0));
                                        player.forceChat("yayy!");
                                        player.getPacketSender().sendMessage("@gre@success!");
                                        break;
                                    case 9:
                                        player.getPacketSender().sendInterface(26130);
                                        player.getPacketSender().sendMessage("You are haunted!");
                                        break;
                                }

                                break;
                            //
                            case 39641:
                            case -25894:
                            case 39642:
                            case -25895:
                                switch (Misc.getRandom(9)) {
                                    case 0:
                                        player.getPacketSender().sendInterface(28130);
                                        player.getPacketSender().sendMessage("You have been booty trapped!");
                                        break;
                                    case 1:
                                        player.moveTo(new Position(2468, 2827, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 2:
                                        player.moveTo(new Position(2462, 2831, 0));
                                        player.getPacketSender().sendMessage("oopsie!");
                                        break;
                                    case 3:
                                        player.moveTo(new Position(2462, 2834, 0));
                                        player.getPacketSender().sendMessage("you did it again!");
                                        break;
                                    case 4:
                                        player.moveTo(new Position(2466, 2836, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 5:
                                        player.getPacketSender().sendInterface(27130);
                                        player.getPacketSender().sendMessage("piccolo died!");
                                        break;
                                    case 6:
                                        Position[] locations = new Position[]{new Position(2434, 2820, 0),
                                                new Position(2436, 2818, 0), new Position(2491, 2818, 0),
                                                new Position(2493, 2820, 0), new Position(2491, 2877, 0),
                                                new Position(2493, 2875, 0), new Position(2434, 2875, 0),
                                                new Position(2436, 2877, 0)};
                                        Position teleportLocation = locations[RandomUtility.exclusiveRandom(0,
                                                locations.length)];

                                        TeleportHandler.teleportPlayer(player, teleportLocation,
                                                player.getSpellbook().getTeleportType());
                                        player.getPacketSender().sendMessage("EEEEK!");
                                        player.forceChat("eek!");
                                        break;
                                    case 7:
                                        player.moveTo(new Position(2468, 2839, 0));
                                        player.getPacketSender().sendMessage("almost there!");
                                        break;
                                    case 8:
                                        player.moveTo(new Position(2471, 2833, 0));
                                        player.forceChat("yayy!");
                                        player.getPacketSender().sendMessage("@gre@success!");
                                        break;
                                    case 9:
                                        player.getPacketSender().sendInterface(26130);
                                        player.getPacketSender().sendMessage("You are haunted!");
                                        break;
                                }

                                break;
                            //
                            case 39640:
                            case -25896:
                            case 39639:
                            case -25897:
                                switch (Misc.getRandom(9)) {
                                    case 0:
                                        player.getPacketSender().sendInterface(28130);
                                        player.getPacketSender().sendMessage("You have been booty trapped!");
                                        break;
                                    case 1:
                                        player.moveTo(new Position(2462, 2869, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 2:
                                        player.moveTo(new Position(2456, 2855, 0));
                                        player.getPacketSender().sendMessage("oopsie!");
                                        break;
                                    case 3:
                                        player.moveTo(new Position(2459, 2853, 0));
                                        player.getPacketSender().sendMessage("you did it again!");
                                        break;
                                    case 4:
                                        player.moveTo(new Position(2461, 2851, 0));
                                        player.getPacketSender().sendMessage("wrong trigger!");
                                        break;
                                    case 5:
                                        player.getPacketSender().sendInterface(27130);
                                        player.getPacketSender().sendMessage("piccolo died!");
                                        break;
                                    case 6:
                                        Position[] locations = new Position[]{new Position(2434, 2820, 0),
                                                new Position(2436, 2818, 0), new Position(2491, 2818, 0),
                                                new Position(2493, 2820, 0), new Position(2491, 2877, 0),
                                                new Position(2493, 2875, 0), new Position(2434, 2875, 0),
                                                new Position(2436, 2877, 0)};
                                        Position teleportLocation = locations[RandomUtility.exclusiveRandom(0,
                                                locations.length)];

                                        TeleportHandler.teleportPlayer(player, teleportLocation,
                                                player.getSpellbook().getTeleportType());
                                        player.getPacketSender().sendMessage("EEEEK!");
                                        player.forceChat("eek!");
                                        break;
                                    case 7:
                                        player.moveTo(new Position(2461, 2867, 0));
                                        player.getPacketSender().sendMessage("almost there!");
                                        break;
                                    case 8:
                                        player.moveTo(new Position(2458, 2862, 0));
                                        player.forceChat("yayy!");

                                        player.getPacketSender().sendMessage("@gre@success!");
                                        break;
                                    case 9:
                                        player.getPacketSender().sendInterface(26130);
                                        player.getPacketSender().sendMessage("You are haunted!");
                                        break;
                                }

                                break;
                            case 2145:
                                player.getPacketSender().sendMessage("Eww. That's a terrible idea!");
                                break;
                            case 1739:
                                if (player.getLocation() == Location.LUMBRIDGE) {
                                    player.moveTo(
                                            new Position(player.getPosition().getX(), player.getPosition().getY(), 0));
                                }
                                break;
                            case 172:
                                CrystalChest.sendRewardInterface(player);
                                break;

                            case 9975:
                                DialogueManager.start(player, 22);
                                player.setDialogueActionId(14);
                                break;
                            case 4388:
                                resetInterface(player);
                                player.getPacketSender()
                                        .sendString(8144, "Tier Portal Teleports - Make your way up the tier")
                                        .sendInterface(8134);
                                player.getPacketSender().sendString(8147, "@gre@-@bla@ Tier I");
                                player.getPacketSender().sendString(8148,
                                        "@blu@-@whi@::devils   -@or2@ You need 500 NPC Kills");
                                player.getPacketSender().sendString(8140, "@gre@-@bla@ Tier II");
                                player.getPacketSender().sendString(8150,
                                        "@blu@-@whi@::lords   -@or2@ You need 100 Devil Kills");
                                player.getPacketSender().sendString(8151, "@gre@-@bla@ Tier IV");
                                player.getPacketSender().sendString(8152,
                                        "@blu@-@whi@::Demon   -@or2@ You need 200 Lord Kills");
                                player.getPacketSender().sendString(8153, "@gre@-@bla@ Tier V");
                                player.getPacketSender().sendString(8154,
                                        "@blu@-@whi@::Dragon   -@or2@ You need 300 Demon Kills");
                                player.getPacketSender().sendString(8155, "@gre@-@bla@ Tier VI");
                                player.getPacketSender().sendString(8156,
                                        "@blu@-@whi@::Beast   -@or2@ You need 400 Dragon Kills");
                                break;
                            case 6910:
                            case 4483:
                            case 25808:
                            case 3193:
                            case 2213:
                            case 11758:
                            case 14367:
                            case 42192:
                            case 75:
                            case 26972:
                            case 11338:
                            case 19230:
                                player.sendMessage(player.getGameMode().toString() + ", " + player.getIronmanGroup());

                                player.getBank(player.getCurrentBankTab()).open();
                                break;
                            case 26945:
                                player.setDialogueActionId(41);
                                player.setInputHandling(new DonateToWell());
                                player.getPacketSender().sendInterfaceRemoval()
                                        .sendEnterAmountPrompt("How much money would you like to contribute with?");
                                break;
                            case 2646:
                            case 312:
                                if (!player.getClickDelay().elapsed(1200))
                                    return;
                                if (player.getInventory().isFull()) {
                                    player.getPacketSender().sendMessage("You don't have enough free inventory space.");
                                    return;
                                }
                                String type = gameObject.getId() == 312 ? "Potato" : "Flax";
                                player.performAnimation(new Animation(827));
                                player.getInventory().add(gameObject.getId() == 312 ? 1942 : 1779, 1);
                                player.getPacketSender().sendMessage("You pick some " + type + "..");
                                gameObject.setPickAmount(gameObject.getPickAmount() + 1);
                                if (Misc.getRandom(3) == 1 && gameObject.getPickAmount() >= 1
                                        || gameObject.getPickAmount() >= 6) {
                                    player.getPacketSender().sendClientRightClickRemoval();
                                    gameObject.setPickAmount(0);
                                    CustomObjects.globalObjectRespawnTask(new GameObject(-1, gameObject.getPosition()),
                                            gameObject, 10);
                                }
                                player.getClickDelay().reset();
                                break;
                            case 2644:
                                Flax.showSpinInterface(player);
                                break;
                            case 6:
                                DwarfCannon cannon = player.getCannon();
                                if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
                                    player.getPacketSender().sendMessage("This is not your cannon!");
                                } else {
                                    DwarfMultiCannon.pickupCannon(player, cannon, false);
                                }
                                break;
                           /* case 5917: // friday the 13th event
                                Stalls.stealFromStall(player, gameObject, 1, 0, new Item(13150, 1),
                                        "You search the Plasma Vent... and find a Spooky Box!");
                                break;*/
                            //

                           /* case 4875:
                                if (!player.getClickDelay().elapsed(2500))
                                    return;
                                if (player.getInventory().getFreeSlots() < 1) {
                                    player.getPacketSender().sendMessage("You don't have enough inventory spaces.");
                                    return;
                                }
                                player.performAnimation(new Animation(881));
                                player.getPacketSender().sendInterfaceRemoval();
                                player.getSkillManager().addExperience(Skill.THIEVING, 15);
                                player.getClickDelay().reset();
                                player.getInventory().add(18199, 1);
                                int[] RewardId = new int[]{ItemDefinition.MILL_ID};
                                int[] LowItems = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2};
                                int pickedFood = RewardId[RandomUtility.exclusiveRandom(0, RewardId.length)];
                                int pickedAmt = LowItems[RandomUtility.exclusiveRandom(0, LowItems.length)];
                                player.getInventory().add(pickedFood, pickedAmt);
                                player.getPacketSender().sendMessage("You steal a banana");

                                break;

                            case 4874:
                                if (!player.getClickDelay().elapsed(2500))
                                    return;
                                if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < 30) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Thieving level of at least 30 to steal from this stall.");
                                    return;
                                }
                                if (player.getInventory().getFreeSlots() < 1) {
                                    player.getPacketSender().sendMessage("You don't have enough inventory spaces.");
                                    return;
                                }
                                player.performAnimation(new Animation(881));
                                player.getPacketSender().sendInterfaceRemoval();
                                player.getSkillManager().addExperience(Skill.THIEVING, 30);
                                player.getClickDelay().reset();
                                player.getInventory().add(15009, 1);
                                int[] RewardId1 = new int[]{ItemDefinition.MILL_ID};
                                int[] LowItems1 = new int[]{1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3};
                                int pickedFood1 = RewardId1[RandomUtility.exclusiveRandom(0, RewardId1.length)];
                                int pickedAmt1 = LowItems1[RandomUtility.exclusiveRandom(0, LowItems1.length)];
                                player.getInventory().add(pickedFood1, pickedAmt1);
                                player.getPacketSender().sendMessage("You steal a golden ring");
                                // Stalls.stealFromStall(player, 30, 34, 15009, "You steal a golden ring.");
                                break;
                            case 4876:

                                if (!player.getClickDelay().elapsed(2500))
                                    return;

                                if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < 55) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Thieving level of at least 55 to steal from this stall.");
                                    return;
                                }
                                if (player.getInventory().getFreeSlots() < 1) {
                                    player.getPacketSender().sendMessage("You don't have enough inventory spaces.");
                                    return;
                                }
                                player.performAnimation(new Animation(881));
                                player.getPacketSender().sendInterfaceRemoval();
                                player.getSkillManager().addExperience(Skill.THIEVING, 55);
                                player.getClickDelay().reset();
                                player.getInventory().add(17401, 1);
                                int[] RewardId11 = new int[]{ItemDefinition.MILL_ID};
                                int[] LowItems11 = new int[]{1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4};
                                int pickedFood11 = RewardId11[RandomUtility.exclusiveRandom(0, RewardId11.length)];
                                int pickedAmt11 = LowItems11[RandomUtility.exclusiveRandom(0, LowItems11.length)];
                                player.getInventory().add(pickedFood11, pickedAmt11);
                                player.getPacketSender().sendMessage("You steal a damaged hammer");
                                // Stalls.stealFromStall(player, 60, 57, 17401, "You steal a damaged hammer.");
                                break;
                            case 4877:
                                if (!player.getClickDelay().elapsed(2500))
                                    return;

                                if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < 78) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Thieving level of at least 78 to steal from this stall.");
                                    return;
                                }
                                if (player.getInventory().getFreeSlots() < 1) {
                                    player.getPacketSender().sendMessage("You don't have enough inventory spaces.");
                                    return;
                                }
                                player.performAnimation(new Animation(881));
                                player.getPacketSender().sendInterfaceRemoval();
                                player.getSkillManager().addExperience(Skill.THIEVING, 80);
                                player.getClickDelay().reset();
                                player.getInventory().add(1389, 1);
                                int[] RewardId111 = new int[]{ItemDefinition.MILL_ID};
                                int[] LowItems111 = new int[]{2, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 1};
                                int pickedFood111 = RewardId111[RandomUtility.exclusiveRandom(0, RewardId111.length)];
                                int pickedAmt111 = LowItems111[RandomUtility.exclusiveRandom(0, LowItems111.length)];
                                player.getInventory().add(pickedFood111, pickedAmt111);
                                player.getPacketSender().sendMessage("You steal a staff");
                                // Stalls.stealFromStall(player, 65, 80, 1389, "You steal a staff.");
                                break;
                            case 4878:
                                if (!player.getClickDelay().elapsed(2500))
                                    return;

                                if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < 95) {
                                    player.getPacketSender().sendMessage(
                                            "You need a Thieving level of at least 95 to steal from this stall.");
                                    return;
                                }
                                if (player.getInventory().getFreeSlots() < 1) {
                                    player.getPacketSender().sendMessage("You don't have enough inventory spaces.");
                                    return;
                                }
                                player.performAnimation(new Animation(881));
                                player.getPacketSender().sendInterfaceRemoval();
                                player.getSkillManager().addExperience(Skill.THIEVING, 100);
                                player.getClickDelay().reset();
                                player.getInventory().add(11998, 1);
                                int[] RewardId1111 = new int[]{ItemDefinition.MILL_ID};
                                int[] LowItems1111 = new int[]{2, 2, 3, 4, 4, 4, 5, 5, 6, 6, 6, 1, 1};
                                int pickedFood1111 = RewardId1111[RandomUtility.exclusiveRandom(0, RewardId1111.length)];
                                int pickedAmt1111 = LowItems1111[RandomUtility.exclusiveRandom(0, LowItems1111.length)];
                                player.getInventory().add(pickedFood1111, pickedAmt1111);
                                player.getPacketSender().sendMessage("You steal a scimitar");
                                // Stalls.stealFromStall(player, 80, 101, 11998, "You steal a scimitar.");
                                break;*/
                            case 3044:
                            case 6189:
                            case 26814:
                            case 11666:
                                Jewelry.jewelryInterface(player);
                                break;
                            case 2152:
                                player.performAnimation(new Animation(8502));
                                player.performGraphic(new Graphic(1308));
                                player.getSkillManager().setCurrentLevel(Skill.SUMMONING,
                                        player.getSkillManager().getMaxLevel(Skill.SUMMONING));
                                player.getPacketSender().sendMessage("You renew your Summoning points.");
                                break;
                        }
                    }
                }));
    }

    private static void trainZoneMoveHandler(Player p){
        Position pos = new Position(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ());
        //sonics
        if ((p.getPosition().getX() == 3472 || p.getPosition().getX() == 3473) && p.getPosition().getY() == 9491) {
            if (KillsTracker.getTotalKillsForNpc(9001, p) >= 10) {
                pos.setY(9493);
            } else {
                p.getPacketSender().sendMessage("You must have the required 10 killcount for sonic to pass into the next zone");
            }
        } else if ((p.getPosition().getX() == 3472 || p.getPosition().getX() == 3473) && p.getPosition().getY() > 9491 && p.getPosition().getY() < 9498) {
            if (KillsTracker.getTotalKillsForNpc(9001, p) >= 10) {
                pos.setY(9491);
            } else {
                p.getPacketSender().sendMessage("You must have the required 10 killcount for sonic to pass into the next zone");
            }
        }

        //patricks
        if ((p.getPosition().getX() == 3472 || p.getPosition().getX() == 3473) && p.getPosition().getY() == 9511) {
            if (KillsTracker.getTotalKillsForNpc(9002, p) >= 25) {
                pos.setY(9513);
            } else {
                p.getPacketSender().sendMessage("You must have the required 25 killcount for patrick to pass into the next zone");
            }
        } else if ((p.getPosition().getX() == 3472 || p.getPosition().getX() == 3473) && (p.getPosition().getY() >= 9513 && p.getPosition().getY() <= 9518)) {
            if (KillsTracker.getTotalKillsForNpc(9002, p) >= 25) {
                pos.setY(9510);
            } else {
                p.getPacketSender().sendMessage("You must have the required 25 killcount for patrick to pass into the next zone");
            }
        }

        //mew two
        if ((p.getPosition().getX() == 3480 || p.getPosition().getX() == 3479) && (p.getPosition().getY() >= 9523 || p.getPosition().getY() <= 9527)) {
            if (KillsTracker.getTotalKillsForNpc(9003, p) >= 50) {
                pos.setX(3481);
                pos.setY(9525);
            } else {
                p.getPacketSender().sendMessage("@red@You must have the required 50 killcount for luigi to pass into the next zone");
            }
        } else if ((p.getPosition().getX() >= 3481 ) && (p.getPosition().getY() == 9525 || p.getPosition().getY() == 9526)) {
            if (KillsTracker.getTotalKillsForNpc(9003, p) >= 50) {
                pos.setX(3479);
            } else {
                p.getPacketSender().sendMessage("@red@You must have the required 50 killcount for luigi to pass into the next zone");
            }
        }

        //4th zone
        if ((p.getPosition().getX() >= 3497 || p.getPosition().getX() <= 3500) && (p.getPosition().getY() <= 9520 && p.getPosition().getY() >= 9518)) {
            if (KillsTracker.getTotalKillsForNpc(9004, p) >= 100) {
                pos.setY(9516);
            } else {
                p.getPacketSender().sendMessage("@red@You must have the required 100 killcount for squirtle to pass into the next zone");
            }
        } else if ((p.getPosition().getX() == 3999 || p.getPosition().getX() == 3498) && (p.getPosition().getY() <= 9517 && p.getPosition().getY() >= 9510)) {
            if (KillsTracker.getTotalKillsForNpc(9004, p) >= 100) {
                pos.setY(p.getPosition().getY() + 2);
            } else {
                p.getPacketSender().sendMessage("@red@You must have the required 100 killcount for squirtle to pass into the next zone");
            }
        }

        //5th zone
        if ((p.getPosition().getX() >= 3497 || p.getPosition().getX() <= 3500) && (p.getPosition().getY() == 9497 || p.getPosition().getY() == 9498)) {
            if (KillsTracker.getTotalKillsForNpc(9005, p) >= 175) {
                pos.setY(9495);
            } else {
                p.getPacketSender().sendMessage("@red@You must have the required 175 killcount for mewtwo to pass into the next zone");
            }
        } else if ((p.getPosition().getX() == 3999 || p.getPosition().getX() == 3498) && (p.getPosition().getY() <= 9495 && p.getPosition().getY() >= 9490)) {
            if (KillsTracker.getTotalKillsForNpc(9005, p) >= 175) {
                pos.setY(p.getPosition().getY() + 2);
            } else {
                p.getPacketSender().sendMessage("You must have the required 50 killcount for mewtwo to pass into the next zone");
            }
        }
        p.getSession().clearMessages();
        p.moveTo(pos);
    }

    public static void resetInterface(Player player) {
        for (int i = 8145; i < 8196; i++)
            player.getPacketSender().sendString(i, "");
        for (int i = 12174; i < 12224; i++)
            player.getPacketSender().sendString(i, "");
        player.getPacketSender().sendString(8136, "Close window");
    }

    private static void thirdClick(Player player, Packet packet) {
        final int x = packet.readShort();
        final int y = packet.readShort();
        final int id = packet.readLEShortA();

        final Position position = new Position(x, y, player.getPosition().getZ());
        final GameObject gameObject = new GameObject(id, position);
        if (!Construction.buildingHouse(player)) {
            if (id > 0 && (!RegionClipping.objectExists(gameObject) || !Locations.goodDistance(gameObject.getPosition(), player.getPosition(), OBJECT_INTERACTION_MAX_DISTANCE))) {
                // player.getPacketSender().sendMessage("An error occured. Error code:
                // "+id).sendMessage("Please report the error to a staff member.");
                return;
            }
        }
        player.setPositionToFace(gameObject.getPosition());
        int distanceX = (player.getPosition().getX() - position.getX());
        int distanceY = (player.getPosition().getY() - position.getY());
        if (distanceX < 0)
            distanceX = -(distanceX);
        if (distanceY < 0)
            distanceY = -(distanceY);
        int size = distanceX > distanceY ? distanceX : distanceY;
        gameObject.setSize(size);
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender()
                    .sendMessage("Third click object id; [id, position] : [" + id + ", " + position.toString() + "]");
        }
        player.setInteractingObject(gameObject);
        player.setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
            @Override
            public void execute() {
                if (!player.getControllerManager().processObjectClick3(gameObject)) {
                    return;
                }
                switch (id) {
                    case 26945:
                        player.setDialogueActionId(1002);
                        DialogueManager.start(player, 216);
                        break;

                    case 13192:
                        player.performAnimation(new Animation(645));
                        player.setSpellbook(player.getSpellbook() == MagicSpellbook.ANCIENT ? MagicSpellbook.LUNAR
                                : player.getSpellbook() == MagicSpellbook.LUNAR ? MagicSpellbook.NORMAL
                                : MagicSpellbook.ANCIENT);
                        player.getPacketSender()
                                .sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                                .sendMessage("Your magic spellbook is changed..");
                        Autocasting.resetAutocast(player, true);
                        break;

                }
            }
        }));

    }

    private static void fourthClick(Player player, Packet packet) {
        final int x = packet.readShort();
        final int y = packet.readShort();
        final int id = packet.readLEShortA();

        final Position position = new Position(x, y, player.getPosition().getZ());
        final GameObject gameObject = new GameObject(id, position);
        //System.out.println("id:" + id + " x " + x + " y " + y);

        if (gameObject == null) {
            return;
        }
        if (!player.getControllerManager().processObjectClick4(gameObject)) {
            return;
        }
    }

    private static void fifthClick(final Player player, Packet packet) {
        final int id = packet.readUnsignedShortA();
        final int y = packet.readUnsignedShortA();
        final int x = packet.readShort();
        final Position position = new Position(x, y, player.getPosition().getZ());
        final GameObject gameObject = new GameObject(id, position);
        if (!Construction.buildingHouse(player)) {
            if (id > 0 && (!RegionClipping.objectExists(gameObject) || !Locations.goodDistance(gameObject.getPosition(), player.getPosition(), OBJECT_INTERACTION_MAX_DISTANCE))) {
                // player.getPacketSender().sendMessage("An error occured. Error code:
                // "+id).sendMessage("Please report the error to a staff member.");
                return;
            }
        }
        player.setPositionToFace(gameObject.getPosition());
        int distanceX = (player.getPosition().getX() - position.getX());
        int distanceY = (player.getPosition().getY() - position.getY());
        if (distanceX < 0)
            distanceX = -(distanceX);
        if (distanceY < 0)
            distanceY = -(distanceY);
        int size = distanceX > distanceY ? distanceX : distanceY;
        gameObject.setSize(size);
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender()
                    .sendMessage("Third click object id; [id, position] : [" + id + ", " + position.toString() + "]");
        }
        player.setInteractingObject(gameObject);
        player.setWalkToTask(new WalkToTask(player, position, gameObject.getSize(), new FinalizedMovementTask() {
            @Override
            public void execute() {
                if (!player.getControllerManager().processObjectClick5(gameObject)) {
                    return;
                }

                switch (id) {
                }
                Construction.handleFifthObjectClick(x, y, id, player);
            }
        }));
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.isTeleporting() || (player.isPlayerLocked() || player.isGroupIronmanLocked()) || player.getMovementQueue().isLockMovement())
            return;
        player.afkTicks = 0;
        player.afk = false;
        switch (packet.getOpcode()) {
            case FIRST_CLICK:
                firstClick(player, packet);
               player.getPacketSender().sendMessage("1st click obj");
                break;
            case SECOND_CLICK:
                secondClick(player, packet);
                    player.getPacketSender().sendMessage("2nd click obj");
                break;
            case THIRD_CLICK:
                thirdClick(player, packet);
                player.getPacketSender().sendMessage("3rd click obj");
                break;
            case FOURTH_CLICK:
                fourthClick(player, packet);
                player.getPacketSender().sendMessage("4th click obj. no handler");
                break;
            case FIFTH_CLICK:
                fifthClick(player, packet);
                player.getPacketSender().sendMessage("5th click obj");
                break;
        }
    }
}
