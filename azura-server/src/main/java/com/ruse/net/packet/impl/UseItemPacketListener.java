package com.ruse.net.packet.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.*;
import com.ruse.model.Locations.Location;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.CrystalChest;
import com.ruse.world.content.CurrencyPouch;
import com.ruse.world.content.ItemForging;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.cluescrolls.OLD_ClueScrolls;
import com.ruse.world.content.combat.range.ToxicBlowpipe;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.impl.DungPartyInvitation;
import com.ruse.world.content.holidayevents.christmas2016;
import com.ruse.world.content.minigames.impl.TreasureHunter;
import com.ruse.world.content.minigames.impl.VaultOfWar;
import com.ruse.world.content.minigames.impl.WarriorsGuild;
import com.ruse.world.content.skill.impl.cooking.Cooking;
import com.ruse.world.content.skill.impl.cooking.CookingData;
import com.ruse.world.content.skill.impl.crafting.Flax;
import com.ruse.world.content.skill.impl.crafting.Gems;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.skill.impl.crafting.LeatherMaking;
import com.ruse.world.content.skill.impl.firemaking.Firelighter;
import com.ruse.world.content.skill.impl.firemaking.Firemaking;
import com.ruse.world.content.skill.impl.firemaking.Logdata.logData;
import com.ruse.world.content.skill.impl.fletching.BoltData;
import com.ruse.world.content.skill.impl.fletching.Fletching;
import com.ruse.world.content.skill.impl.herblore.Crushing;
import com.ruse.world.content.skill.impl.herblore.Herblore;
import com.ruse.world.content.skill.impl.herblore.PotionCombinating;
import com.ruse.world.content.skill.impl.herblore.WeaponPoison;
import com.ruse.world.content.skill.impl.prayer.BonesOnAltar;
import com.ruse.world.content.skill.impl.prayer.Prayer;
import com.ruse.world.content.skill.impl.slayer.Slayer;
import com.ruse.world.content.skill.impl.slayer.SlayerDialogues;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.skill.impl.smithing.EquipmentMaking;
import com.ruse.world.content.skill.impl.smithing.Smelting;
import com.ruse.world.content.upgrades.Upgradable;
import com.ruse.world.entity.impl.mini.MiniPlayer;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This packet listener is called when a player 'uses' an item on another
 * entity.
 *
 * @author relex lawl
 */

public class UseItemPacketListener implements PacketListener {

    public final static int USE_ITEM = 122;
    public final static int ITEM_ON_NPC = 57;
    public final static int ITEM_ON_ITEM = 53;
    public final static int ITEM_ON_OBJECT = 192;
    public final static int ITEM_ON_GROUND_ITEM = 25;
    public static final int ITEM_ON_PLAYER = 14;

    /**
     * The PacketListener logger to debug sendInformation and print out errors.
     */
    // private final static Logger logger =
    // Logger.getLogger(UseItemPacketListener.class);
    private static void useItem(Player player, Packet packet) {
        if (player.isTeleporting() || player.getConstitution() <= 0)
            return;
        int interfaceId = packet.readLEShortA();
        int slot = packet.readShortA();
        int id = packet.readLEShort();
    }

    private static void itemOnItem(Player player, Packet packet) {
        int usedWithSlot = packet.readUnsignedShort();
        int itemUsedSlot = packet.readUnsignedShortA();
        if (usedWithSlot < 0 || itemUsedSlot < 0 || itemUsedSlot > player.getInventory().capacity()
                || usedWithSlot > player.getInventory().capacity())
            return;

        List<Integer> augmentItemIds = new ArrayList<Integer>(Arrays.asList(1, 3));

        Item usedOn = player.getInventory().getItems()[usedWithSlot];
        Item usedItem = player.getInventory().getItems()[itemUsedSlot];
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender()
                    .sendMessage("ItemOnItem - <shad=000000><col=ffffff>[<col=ff774a>"
                            + ItemDefinition.forId(usedItem.getId()).getName() + ":" + usedItem.getId() + ":"
                            + usedItem.getAmount() + " <col=ffffff>was used on <col=4AD2FF>"
                            + ItemDefinition.forId(usedOn.getId()).getName() + ":" + usedOn.getId() + ":"
                            + usedOn.getAmount() + "<col=ffffff>]");
        }

        if (!player.getControllerManager().canUseItemOnItem(usedOn, usedItem)) {
            return;
        }

        Upgradable upgradable = Upgradable.Companion.getUpgradable(usedOn.getId(), usedItem.getId());
        if(upgradable != null) {
            if (player.getInventory().contains(usedOn.getId()) && player.getInventory().contains(usedItem.getId())) {
                player.getInventory().delete(usedItem.getId(), 1);// Delete shard no matter what
                if(Misc.random(100) < 50) {// success
                    player.getInventory().delete(usedOn.getId(), 1);
                    player.getInventory().add(upgradable.getUpgradedItem(), 1);
                    player.sendMessage("Your upgrade was successful!");
                } else {
                    player.sendMessage("Your upgrade was a failure...");
                }
            }

        }

        for(int on = 16442; on <= 16444; on++) {
            for(int item = 16442; item <= 16444; item++) {
                if (usedOn.getId() == on && usedItem.getId() == item) {
                    if(player.getInventory().contains(16442) && player.getInventory().contains(16443) && player.getInventory().contains(16444)) {
                        // Consume shards
                        player.getInventory().delete(16442, 1);
                        player.getInventory().delete(16443, 1);
                        player.getInventory().delete(16444, 1);

                        if(Misc.random(100) < 50) {// success
                            player.getInventory().add(16445, 1);
                            player.sendMessage("You combine the three shards to form an ultimate shard.");
                        } else {
                            player.sendMessage("You combine the three shards and they shatter into pieces.");
                        }
                    } else {
                        player.sendMessage("You need all three shards to make an ultimate shard.");
                    }

                }
            }
        }

        if (augmentItemIds.contains(usedItem.getId())){
            if (ItemDefinition.forId(usedOn.getId()).isEquitable) {
                player.getAugmentHandler().add(usedOn.getId(), usedItem.getId(), player);
                player.setCurrentAugment(usedOn.getId());
                player.getAugmentHandler().updateInterface(player.getCurrentAugment(), player);
            } else {
                player.getPacketSender().sendMessage("You can only augment gear pieces");
            }
        }

        if (usedOn.getId() == 9003 && usedItem.getId() == 989) {
            CrystalChest.sendRewardInterface(player);
            return;
        }

        if (usedOn.getId() == 22108) {
            CurrencyPouch.handleItemOnItem(player, usedItem);
            return;
        }
        if (usedOn.getId() == TreasureHunter.KEY_1 || usedOn.getId() == TreasureHunter.KEY_2 | usedOn.getId() == TreasureHunter.KEY_3 || usedOn.getId() == TreasureHunter.KEY_4) {
            TreasureHunter.combineKeys(player, usedOn, usedItem);
            return;
        }

        if (usedOn.getId() == 22110) {
            Item[] requirements = new Item[]{new Item(22110, 1), new Item(7995, 1)};
            if (player.getInventory().containsAll(requirements) && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22109, 1);
                player.sendMessage("@red@Congratulations, you've made a Upgraded Owner's Cape!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created an Upgraded Owner's Cape!";
                World.sendMessage(msg);
                return;
            }
            requirements = new Item[]{new Item(22110, 1), new Item(12630, 1)};
            if (player.getInventory().containsAll(requirements) && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22111, 1);
                player.sendMessage("@red@Congratulations, you've made a Owner's Aura!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created an Owner's Aura!";
                World.sendMessage(msg);
                return;
            }
        }

        if (usedOn.getId() == 22106) {
            Item[] requirements = new Item[]{new Item(22106, 1),
                    new Item(19000, 10_000), new Item(5022, 10_000_000),
                    new Item(ItemDefinition.TOKEN_ID, 10_000_000)};
            if (player.getInventory().containsAll(requirements) && usedItem.getId() == 19_000) {
                player.getInventory().deleteItemSet(requirements);
                if (Misc.exclusiveRandom(1, 100) <= 25) {
                    player.getInventory().add(22107, 1);
                    player.sendMessage("@red@Congratulations, you've made Lucifer's pet!");
                    String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's pet!";
                    World.sendMessage(msg);
                }
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 2), new Item(18885, 1)})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22104, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's gloves!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's gloves!";
                World.sendMessage(msg);
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 2), new Item(18887, 1),})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22103, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's boots!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's boots!";
                World.sendMessage(msg);
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 3), new Item(4684, 1),})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22100, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's head!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's head!";
                World.sendMessage(msg);
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 3), new Item(4685, 1),})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22101, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's body!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's body!";
                World.sendMessage(msg);
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 3), new Item(4686, 1),})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22102, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's legs!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's legs!";
                World.sendMessage(msg);
                return;
            } else if (player.getInventory().containsAll(requirements = new Item[]{new Item(22106, 3), new Item(20400, 1),})
                    && usedItem.getId() == requirements[1].getId()) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(22105, 1);
                player.sendMessage("@red@Congratulations, you've made Lucifer's wings!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created Lucifer's wings!";
                World.sendMessage(msg);
                return;
            }
        }

        if ((usedOn.getId() == 23004 || usedOn.getId() == 23005 || usedOn.getId() == 23006 || usedOn.getId() == 23007)
                && (usedItem.getId() == 23004 || usedItem.getId() == 23005 || usedItem.getId() == 23006 || usedItem.getId() == 23007)) {
            Item[] requirements = new Item[]{new Item(23004, 100),
                    new Item(23005, 100), new Item(23006, 100),
                    new Item(23007, 100)};
            if (player.getInventory().containsAll(requirements)) {
                player.getInventory().deleteItemSet(requirements);
                player.getInventory().add(23018, 1);
                player.sendMessage("@red@Congratulations, you've made an Armoured Bunny pet!");
                String msg = "@blu@<img=5>[CREATION]<img=5>@red@ " + player.getUsername() + " has created a Armoured Bunny Pet!";
                World.sendMessage(msg);
                return;
            }
        }


        if (usedOn.getId() == 28 && usedItem.getId() == 29 || usedOn.getId() == 29 && usedItem.getId() == 28) {
            int rewards2[][] = {
                    {14733, 14732, 14734, 19111, 11137, 3907, 18351, 3140, 18353, 18355, 18357, 15501, 15272, 2503,
                            10499, 3805, 6326, 861, 1163, 1201, 6111, 544, 542, 5574, 5575, 5576, 1215, 3105, 13734,
                            7400, 11118}, // Common, 0
                    {18686, 13996, 13913, 13919, 18799, 18834, 18801, 18800, 5095, 19140, 19139, 19138, 4411, 19887,
                            22078, 19123, 11617, 3909, 3318, 15501, 11133, 15126, 16043, 6500, 10828, 3751, 3753, 10589,
                            10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914,
                            6889}, // Uncommon, 1
                    {6739, 11728, 6585, 15259, 15332, 2579, 6920, 7630, 6922, 13879, 13883, 15241, 15243, 3321, 3391,
                            3322, 3320, 3318, 18360} // Rare, 2
            };
            double numGen = Math.random();
            /**
             * Chances 50% chance of Common Items - cheap gear, high-end consumables 40%
             * chance of Uncommon Items - various high-end coin-bought gear 10% chance of
             * Rare Items - Highest-end coin-bought gear, some voting-point/pk-point
             * equipment
             */

            int rewardGrade = numGen >= 0.5 ? 0 : numGen >= 0.20 ? 1 : 2;
            int rewardPos = Misc.getRandom(rewards2[rewardGrade].length - 1);
            player.getInventory().delete(28, 1);
            player.getInventory().delete(29, 1);
            player.getInventory().add(rewards2[rewardGrade][rewardPos], 1).refreshItems();
            player.getPacketSender().sendMessage("You unlocked the slayer chest");


            return;

        }

        /* Clue Handler */
        if (usedItem.getId() == 9003
                && (usedOn.getId() == 2724 || ItemDefinition.forId(usedOn.getId()).getName().contains("Clue"))) {
            OLD_ClueScrolls.sendDropTableInterface(player);
        }
        for (int i = 0; i < Firelighter.values().length; i++) {
            if (usedOn.getId() == Firelighter.values()[i].getLighterId()
                    || usedItem.getId() == Firelighter.values()[i].getLighterId()) {
                Firelighter.handleFirelighter(player, i);
                break;
            }
        }

        for (int i = 0; i < Crushing.values().length; i++) {
            if (usedOn.getId() == Crushing.values()[i].getInput()
                    || usedItem.getId() == Crushing.values()[i].getInput()) {
                Crushing.handleCrushing(player, i);
                break;
            }
        }

        for (int i = 0; i < BoltData.values().length; i++) {
            if (usedOn.getId() == BoltData.values()[i].getTip()
                    || usedItem.getId() == BoltData.values()[i].getTip()) {
                Fletching.tipBolt(player, BoltData.values()[i].getTip());
                break;
            }
        }

        WeaponPoison.execute(player, usedItem.getId(), usedOn.getId());
        if (usedItem.getId() == 590 || usedOn.getId() == 590)
            Firemaking.lightFire(player, usedItem.getId() == 590 ? usedOn.getId() : usedItem.getId(), false,
                    1);
        if (usedItem.getDefinition().getName().contains("(") && usedOn.getDefinition().getName().contains("("))
            PotionCombinating.combinePotion(player, usedOn.getId(), usedItem.getId());
        if (usedOn.getId() == Herblore.VIAL || usedItem.getId() == Herblore.VIAL) {
            if (Herblore.makeUnfinishedPotion(player, usedOn.getId())
                    || Herblore.makeUnfinishedPotion(player, usedItem.getId()))
                return;
        }
        if (Herblore.finishPotion(player, usedOn.getId(), usedItem.getId())
                || Herblore.finishPotion(player, usedItem.getId(), usedOn.getId()))
            return;
        if (usedOn.getId() == 12934 || usedItem.getId() == 12926 || usedOn.getId() == 12926
                || usedItem.getId() == 12934) {
            ToxicBlowpipe.loadPipe(player);
        }
        if (usedOn.getId() == 5012 && usedItem.getId() == 10949 && usedItem.getAmount() >= 3)
            World.sendMessage("<img=16> <shad=1>[" + player.getUsername() + "] @cya@Turned his Supreme twisted bow into a Light Twisted Bow!");

        if (usedOn.getId() == 10949 && usedItem.getId() == 5012 && usedOn.getAmount() >= 3)
            World.sendMessage("<img=16> <shad=1>[" + player.getUsername() + "] @cya@Turned his Supreme twisted bow into a Light Twisted Bow!");

        if (usedOn.getId() == 946 || usedItem.getId() == 946)
            Fletching.openSelection(player, usedOn.getId() == 946 ? usedItem.getId() : usedOn.getId());
        if (usedOn.getId() == 1777 || usedItem.getId() == 1777)
            Fletching.openBowStringSelection(player,
                    usedOn.getId() == 1777 ? usedItem.getId() : usedOn.getId());
        if (usedOn.getId() == 53 || usedItem.getId() == 53 || usedOn.getId() == 52
                || usedItem.getId() == 52)
            Fletching.makeArrows(player, usedOn.getId(), usedItem.getId());
        if (usedItem.getId() == 1755 || usedOn.getId() == 1755)
            Gems.selectionInterface(player, usedOn.getId() == 1755 ? usedItem.getId() : usedOn.getId());
        if (usedItem.getId() == 1755 || usedOn.getId() == 1755)
            Fletching.openGemCrushingInterface(player,
                    usedOn.getId() == 1755 ? usedItem.getId() : usedOn.getId());

        if (usedOn.getId() == 1733 || usedItem.getId() == 1733)
            LeatherMaking.craftLeatherDialogue(player, usedOn.getId(), usedItem.getId());
        Herblore.handleSpecialPotion(player, usedItem.getId(), usedOn.getId());
        if (usedItem.getId() == 1759 || usedOn.getId() == 1759) {
            Jewelry.stringAmulet(player, usedItem.getId(), usedOn.getId());
        }
        ItemForging.forgeItem(player, usedItem.getId(), usedOn.getId());
    }

    @SuppressWarnings("unused")
    private static void itemOnObject(Player player, Packet packet) {
        @SuppressWarnings("unused")
        int interfaceType = packet.readShort();
        final int objectId = packet.readShort();
        final int objectY = packet.readLEShortA();
        final int itemSlot = packet.readLEShort();
        final int objectX = packet.readLEShortA();
        final int itemId = packet.readShort();

        if (itemSlot < 0 || itemSlot > player.getInventory().capacity())
            return;
        final Item item = player.getInventory().getItems()[itemSlot];
        if (item == null)
            return;
        final GameObject gameObject = new GameObject(objectId,
                new Position(objectX, objectY, player.getPosition().getZ()));
        if (objectId > 0 && objectId != 6 && !RegionClipping.objectExists(gameObject)) {
            // player.getPacketSender().sendMessage("An error occured. Error code:
            // "+id).sendMessage("Please report the error to a staff member.");
            return;
        }
        player.setInteractingObject(gameObject);
        if (player.getRights() == PlayerRights.DEVELOPER) {
            if (GameObjectDefinition.forId(gameObject.getId()) != null
                    && GameObjectDefinition.forId(gameObject.getId()).getName() != null) {
                player.getPacketSender()
                        .sendMessage("ItemOnObject - <shad=000000><col=ffffff>[<col=ff774a>"
                                + ItemDefinition.forId(itemId).getName() + ":" + itemId
                                + " <col=ffffff>was used on <col=4AD2FF>"
                                + GameObjectDefinition.forId(gameObject.getId()).getName() + ":" + gameObject.getId()
                                + "<col=ffffff>]");
            } else {
                player.getPacketSender()
                        .sendMessage("ItemOnObject - <shad=000000><col=ffffff>[<col=ff774a>"
                                + ItemDefinition.forId(itemId).getName() + ":" + itemId
                                + " <col=ffffff>was used on <col=4AD2FF>" + gameObject.getId()
                                + "<col=ffffff>] @red@(null obj. def)");
            }
        }
        player.setWalkToTask(new WalkToTask(player, gameObject.getPosition().copy(), gameObject.getSize(),
                new FinalizedMovementTask() {
                    @Override
                    public void execute() {


                        if (!player.getControllerManager().handleItemOnObject(gameObject, item)) {
                            return;
                        }
                        if (gameObject.getId() == TreasureHunter.CHEST_ID) {
                            TreasureHunter.useKeyOnChest(player, item);
                        }
                        if (CookingData.forFish(item.getId()) != null && CookingData.isRange(objectId)) {
                            player.setPositionToFace(gameObject.getPosition());
                            Cooking.selectionInterface(player, CookingData.forFish(item.getId()));
                            return;
                        }
                        if (Prayer.isBone(itemId) && (objectId == 409 || objectId == 24343 || objectId == 13192)) {
                            BonesOnAltar.openInterface(player, itemId);
                            return;
                        }
                        if (player.getFarming().plant(itemId, objectId, objectX, objectY))
                            return;
                        if (player.getFarming().useItemOnPlant(itemId, objectX, objectY))
                            return;
                        if (objectId == 31429) {
                            VaultOfWar.useGlovesOnObject(player);
                            return;
                        }
                        if (objectId == 15621) { // Warriors guild
                            // animator
                            if (!WarriorsGuild.itemOnAnimator(player, item, gameObject))
                                player.getPacketSender().sendMessage("Nothing interesting happens..");
                            return;
                        }
                        if (GameObjectDefinition.forId(objectId) != null
                                && GameObjectDefinition.forId(objectId).getName() != null
                                && GameObjectDefinition.forId(objectId).getName().equalsIgnoreCase("furnace")
                                && ItemDefinition.forId(itemId) != null
                                && ItemDefinition.forId(itemId).getName() != null
                                && ItemDefinition.forId(itemId).getName().contains("ore")) {
                            Smelting.openInterface(player);
                            return;
                        }
                        if (GameObjectDefinition.forId(objectId) != null
                                && GameObjectDefinition.forId(objectId).getName() != null
                                && GameObjectDefinition.forId(objectId).getName().equalsIgnoreCase("furnace")
                                && itemId == 2357) {
                            Jewelry.jewelryInterface(player);
                        }
                        if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) { // UIM can use any noted item on a bank
                            // booth to instantly unnote it to
                            // fill all your free inventory
                            // spaces
                            if (GameObjectDefinition.forId(objectId) != null) {
                                GameObjectDefinition def = GameObjectDefinition.forId(objectId);
                                if (def.name != null && def.name.toLowerCase().contains("bank") && def.actions != null
                                        && def.actions[0] != null && def.actions[0].toLowerCase().contains("use")) {
                                    ItemDefinition def1 = ItemDefinition.forId(itemId);
                                    ItemDefinition def2;
                                    int newId = def1.isNoted() ? itemId - 1 : itemId + 1;
                                    def2 = ItemDefinition.forId(newId);
                                    if (def2 != null && def1.getName().equals(def2.getName())) {
                                        int amt = player.getInventory().getAmount(itemId);
                                        if (!def2.isNoted()) {
                                            if (amt > player.getInventory().getFreeSlots())
                                                amt = player.getInventory().getFreeSlots();
                                        }
                                        if (amt == 0) {
                                            player.getPacketSender().sendMessage(
                                                    "You do not have enough space in your inventory to do that.");
                                            return;
                                        }
                                        player.getInventory().delete(itemId, amt).add(newId, amt);

                                    } else {
                                        player.getPacketSender().sendMessage("You cannot do this with that item.");
                                    }
                                    return;
                                }
                            }
                        }
                        switch (objectId) {
                            case 172:
                            case 173:
                                if (itemId == 9003) {
                                    CrystalChest.sendRewardInterface(player);
                                }
                                break;
                            case 16135://betrayed

                                if (itemId == 8868 || itemId == 21201 || itemId == 21202 || itemId == 21203 || itemId == 21204) {
                                    player.getPacketSender().sendMessage("Rewards for gift of betrayed @blu@coming soon@bla@. collect your keys.");

                                }
                                break;
                            case 16077://dammned

                                if (itemId == 9962 || itemId == 21205 || itemId == 21206 || itemId == 21207 || itemId == 21208) {
                                    player.getPacketSender().sendMessage("Rewards for grain of damned @blu@coming soon@bla@. collect your keys.");

                                }
                                break;
                            case 16118://hidden

                                if (itemId == 14471 || itemId == 21209 || itemId == 21210 || itemId == 21211 || itemId == 21212) {
                                    player.getPacketSender().sendMessage("Rewards for box of hidden @blu@coming soon@bla@. collect your keys.");

                                }
                                break;
                            case 16047://cursed

                                if (itemId == 3468 || itemId == 21213 || itemId == 21214 || itemId == 21215 || itemId == 21216) {
                                    player.getPacketSender().sendMessage("Rewards for cradle of cursed @blu@coming soon@bla@. collect your keys.");

                                }
                                break;

                            case -23813:
                            case 41723:
                                if (!christmas2016.isChristmas() || player.getChristmas2016() < 2) {
                                    return;
                                } else {
                                    christmas2016.handleItemOnReindeer(player, itemId);
                                }
                                break;
                            case 2644:
                                if (itemId == 1779) {
                                    Flax.showSpinInterface(player);
                                }
                                break;
                            case 6189:
                            case 11666:
                                Jewelry.jewelryInterface(player);
                                break;
                            case 7836:
                            case 7808:
                                if (itemId == 6055) {
                                    int amt = player.getInventory().getAmount(6055);
                                    if (amt > 0) {
                                        player.getInventory().delete(6055, amt);
                                        player.getPacketSender().sendMessage("You put the weed in the compost bin.");
                                        player.getSkillManager().addExperience(Skill.FARMING, 1 * amt);
                                    }
                                }
                                break;
                            case 4306:
                                EquipmentMaking.handleAnvil(player);
                                break;
                        }
                    }
                }));
    }

    private static void itemOnNpc(final Player player, Packet packet) {
        final int id = packet.readShortA();
        final int index = packet.readShortA();
        final int slot = packet.readLEShort();
        if (index < 0 || index > World.getNpcs().capacity()) {
            return;
        }
        if (slot < 0 || slot > player.getInventory().getItems().length) {
            return;
        }
        NPC npc = World.getNpcs().get(index);
        if (npc == null) {
            return;
        }
        Item usedItem = player.getInventory().forSlot(slot);
        if (usedItem == null) {
            return;
        }
        if (player.getInventory().getItems()[slot].getId() != id) {
            return;
        }
        if (player.getRights() == PlayerRights.DEVELOPER) {
            player.getPacketSender()
                    .sendMessage("ItemOnNPC - <shad=000000><col=ffffff>[<col=ff774a>"
                            + ItemDefinition.forId(id).getName() + ":" + id + " <col=ffffff>was used on <col=4AD2FF>"
                            + npc.getDefinition().getName() + ":" + npc.getId() + "<col=ffffff>]");
        }
        if (!player.getControllerManager().processItemOnNPC(npc, usedItem)) {
            return;
        }

        if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) { // UIM can use any noted item on a bank booth to
            // instantly unnote it to fill all your free
            // inventory spaces
            if (npc.getDefinition() != null) {
                NpcDefinition def = npc.getDefinition();
                if (def.getName() != null && def.getName().toLowerCase().contains("banker")) {
                    ItemDefinition def1 = ItemDefinition.forId(id);
                    ItemDefinition def2;
                    int newId = def1.isNoted() ? id - 1 : id + 1;
                    def2 = ItemDefinition.forId(newId);
                    if (def2 != null && def1.getName().equals(def2.getName())) {
                        int amt = player.getInventory().getAmount(id);
                        if (!def2.isNoted()) {
                            if (amt > player.getInventory().getFreeSlots())
                                amt = player.getInventory().getFreeSlots();
                        }
                        if (amt == 0) {
                            player.getPacketSender()
                                    .sendMessage("You do not have enough space in your inventory to do that.");
                            return;
                        }
                        player.getInventory().delete(id, amt).add(newId, amt);

                    } else {
                        player.getPacketSender().sendMessage("You cannot do this with that item.");
                    }
                    return;
                }
            }
        }
        switch (npc.getId()) {
            case VaultOfWar.GLOVES_NPC:
                VaultOfWar.useGlovesOnNPC(player, new Item(id));
                break;
            case 1597:
            case 8275:
            case 9085:
            case 9000:
                if(player.getSlayer() != null && player.getInventory().contains(Slayer.SKIP_TOKEN)) {
                    player.getSlayer().resetSlayerTask();
                }

                break;

        }
        switch (id) {


            case 9003:
                if (player.getLastTomed() != npc.getId()) {
                    player.getPacketSender()
                            .sendMessage(NpcDefinition.forId(npc.getId()).getName() + " has been scanned to your tome.");
                    if (npc.getId() == 1158) {
                        player.setLastTomed(1160);
                        break;
                    }
                }
                player.setLastTomed(npc.getId());
                break;
            case 1907:
                if (npc.getId() == 1552 && christmas2016.isChristmas()) {
                    if (player.getChristmas2016() < 5) {
                        player.getPacketSender().sendMessage("I should do this after giving the Reindeer their runes.");
                        return;
                    } else if (player.getInventory().getAmount(1907) >= 1) {
                        player.setPositionToFace(npc.getPosition());
                        player.performAnimation(new Animation(4540));
                        player.getInventory().delete(1907, 100);
                        player.setchristmas2016(6);
                        player.getPacketSender().sendMessage("You give Santa the " + ItemDefinition.forId(1907).getName()
                                + ". I should speak with him.");
                    }

                }
                break;
            case 3550:// clue
                boolean clue = OLD_ClueScrolls.handleNpcUse(player, npc.getId());
                if (!clue) {
                    player.getPacketSender().sendMessage("Nothing interesting happens.");
                } else {
                    player.getPacketSender().sendMessage("You manage to continue your clue..");
                }
                break;
            case 4837:
                if (NpcDefinition.forId(npc.getId()).getName().contains("ark wizar")) {
                    TaskManager.submit(new Task(1, player, true) {
                        int tick = 0;

                        @Override
                        public void execute() {
                            if (tick >= 8) {
                                stop();
                            }
                            switch (tick) {
                                case 0:
                                    player.getInventory().delete(new Item(4837, 1));
                                    break;
                                case 1:
                                    player.forceChat("Can I have your autograph?");
                                    break;
                                case 3:
                                    npc.forceChat("Yea noob lol here u go");
                                    break;
                                case 4:
                                    npc.performAnimation(new Animation(1249));
                                    break;
                                case 7:
                                    player.getInventory().add(new Item(22040, 1));
                                    player.getPacketSender()
                                            .sendMessage("The Dark Wizard signs your Necromancy Book, transforming it.");
                                    break;
                            }
                            tick++;
                        }
                    });
                }
                break;
        }
        for (int i = 0; i < logData.values().length; i++) {
            if (logData.values()[i].getLogId() == id) {
                if (npc.getId() == 7377) {
                    if (player.getSummoning().getFamiliar() == null) {
                        player.getPacketSender().sendMessage("That isn't your familiar!");
                        return;
                    }
                    if (player.getSummoning().getFamiliar().getSummonNpc().getId() != 7377) {
                        player.getPacketSender().sendMessage("You must have your own Pyrefiend to use that effect.");
                        return;
                    }
                    Firemaking.lightFire(player, id, false, 1);
                }
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    private static void itemOnGroundItem(Player player) {
        player.getPacketSender().sendMessage("Nothing interesting happens.");
        // // System.out.println("itemongrounditem");
    }

    @SuppressWarnings("unused")
    private static void itemOnPlayer(Player player, Packet packet) {
        int interfaceId = packet.readUnsignedShortA();
        int targetIndex = packet.readUnsignedShort();
        int itemId = packet.readUnsignedShort();
        int slot = packet.readLEShort();
        if (slot < 0 || slot > player.getInventory().capacity() || targetIndex > World.getPlayers().capacity())
            return;
        Player target = World.getPlayers().get(targetIndex);
        if (target == null)
            return;

        if (!player.getControllerManager().processItemOnPlayer(target, itemId, slot)) {
            return;
        }
        System.out.println("HER");
        if (target instanceof MiniPlayer) {
            if (player.getMiniPManager().getMiniPlayer() == null) {
                player.sendMessage("You do not have a mini pet.");
                return;
            }
            if (!player.getMiniPManager().getMiniPlayer().equals(target)) {
                player.sendMessage("This is not your pet.");
                return;
            }
            player.getMiniPManager().useItemWith(itemId, 1, slot);
        }
        switch (itemId) {
            case 23057:
            case 23058:
            case 23059:
            case 23060:
                System.out.println("1");
                boolean ironman = player.getGameMode().isIronman();
                boolean targetIronman = target.getGameMode().isIronman();
                System.out.println("2");
               /* if (!ironman && !targetIronman) { //if same ip
                    if (!player.getHostAddress().equals(target.getHostAddress())) {
                        player.sendMessage("You cannot give bonds to other mains unless its your own!");
                        return;
                    }
                }*/
                System.out.println("3");
                //if (ironman && !targetIronman || !ironman && targetIronman) {
                    if (!target.getInventory().isFull()) {
                        System.out.println("f");
                        if (player.getInventory().contains(itemId)) {
                            player.getInventory().delete(itemId, 1);
                            target.getInventory().add(itemId, 1);

                            player.sendMessage("You gave " + target.getUsername() + " a " + ItemDefinition.forId(itemId).getName());
                            target.sendMessage("You received a " + ItemDefinition.forId(itemId).getName()  + " from " + player.getUsername());
                            PlayerLogs.logPlayerDonations(player.getUsername(), "used a "
                                    + ", BOND: " + ItemDefinition.forId(itemId).getName() + ", on " + target.getUsername());
                            PlayerLogs.log(player.getUsername(), "used a "
                                    + ", BOND: " + ItemDefinition.forId(itemId).getName() + ", on " + target.getUsername());

                        } else {
                            player.sendMessage("You need a bond to perform this action!");
                        }
                    } else {
                        player.sendMessage("This player's inventory is full.");
                    }
                //}
                break;
            case 962:
                if (!player.getInventory().contains(962) || player.getRights() == PlayerRights.ADMINISTRATOR)
                    return;
                player.setPositionToFace(target.getPosition());
                player.performGraphic(new Graphic(1006));
                player.performAnimation(new Animation(451));
                player.getPacketSender().sendMessage("You pull the Christmas cracker...");
                target.getPacketSender().sendMessage("" + player.getUsername() + " pulls a Christmas cracker on you..");
                player.getInventory().delete(962, 1);
                player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
                int phat = 1038 + Misc.getRandom(10);
                player.getInventory().add(phat, 1);
                target.getPacketSender().sendMessage("" + player.getUsername() + " has received a Party hat!");
                PlayerLogs.log(player.getUsername(), "Opened a cracker containing a " + ItemDefinition.forId(phat).getName()
                        + " on " + target.getUsername());
                break;
            case 15707:
                Player partyDung = World.getPlayers().get(targetIndex);
                if (player.getLocation() != Location.DUNGEONEERING || player.isTeleporting()) {
                    player.getPacketSender().sendMessage("You're not in Daemonheim");
                    return;
                }
                if (partyDung.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
                    player.getPacketSender().sendMessage("That player is already in a party.");
                    return;
                }
                if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
                    player.getPacketSender().sendMessage("You're not in a party!");
                    return;
                }
                if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner() != player) {
                    player.getPacketSender().sendMessage("Only the party leader can invite other players.");
                    return;
                }
                if (player.busy()) {
                    player.getPacketSender().sendMessage("You're busy and can't invite anyone.");
                    return;
                }
                if (partyDung.busy()) {
                    player.getPacketSender().sendMessage(partyDung.getUsername() + " is too busy to get your invite.");
                    return;
                }
                DialogueManager.start(partyDung, new DungPartyInvitation(player, partyDung));
                player.getPacketSender().sendMessage("An invitation has been sent to " + partyDung.getUsername());
                break;
            case 4566:
                player.performAnimation(new Animation(451));
                break;
            case 4155:
                if (player.getSlayer().getDuoPartner() != null) {
                    player.getPacketSender().sendMessage("You already have a duo partner.");
                    return;
                }
                if (player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
                    player.getPacketSender().sendMessage("You already have a Slayer task. You must reset it first.");
                    return;
                }
                Player duoPartner = World.getPlayers().get(targetIndex);
                if (duoPartner != null) {
                    if (duoPartner.getSlayer().getDuoPartner() != null) {
                        player.getPacketSender().sendMessage("This player already has a duo partner.");
                        return;
                    }
                    if (duoPartner.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
                        player.getPacketSender().sendMessage("This player already has a Slayer task.");
                        return;
                    }
                    if (duoPartner.getSlayer().getSlayerMaster() != player.getSlayer().getSlayerMaster()) {
                        player.getPacketSender().sendMessage("You do not have the same Slayer master as that player.");
                        return;
                    }
                    if (duoPartner.busy() || duoPartner.getLocation() == Location.WILDERNESS) {
                        player.getPacketSender().sendMessage("This player is currently busy.");
                        return;
                    }
                    DialogueManager.start(duoPartner, SlayerDialogues.inviteDuo(duoPartner, player));
                    player.getPacketSender()
                            .sendMessage("You have invited " + duoPartner.getUsername() + " to join your Slayer duo team.");
                }
                break;
        }
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.getConstitution() <= 0)
            return;
        switch (packet.getOpcode()) {
            case ITEM_ON_ITEM:
                itemOnItem(player, packet);
                break;
            case USE_ITEM:
                useItem(player, packet);
                break;
            case ITEM_ON_OBJECT:
                itemOnObject(player, packet);
                break;
            case ITEM_ON_GROUND_ITEM:
                itemOnGroundItem(player);
                break;
            case ITEM_ON_NPC:
                itemOnNpc(player, packet);
                break;
            case ITEM_ON_PLAYER:
                itemOnPlayer(player, packet);
                break;
        }
    }
}
