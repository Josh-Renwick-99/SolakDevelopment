package com.ruse.net.packet.impl.commands.staff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.*;
import com.ruse.util.AccessPlayer;
import com.ruse.util.Misc;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.content.*;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.grandexchange.GrandExchangeOffers;
import com.ruse.world.content.holidayevents.easter2017;
import com.ruse.world.content.pos.PlayerOwnedShopManager;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.crafting.Jewelry;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator;
import com.ruse.world.entity.impl.player.Player;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OwnerCommands {

    public static void handleOwnerCommands(final Player player, String[] command, String wholeCommand) {
        if (command[0].equals("dumpspawns")) {
            for (NPC npc : World.getNpcs()) {
                if (npc == null)
                    continue;
                if (npc.getPosition().getRegionId() == player.getPosition().getRegionId() &&
                        npc.getPosition().getZ() == player.getPosition().getZ()) {
                    int id = npc.getId();
                    Position position = npc.getDefaultPosition();
                    NPCMovementCoordinator.Coordinator coordinator = npc.getMovementCoordinator().getCoordinator();
                    Direction direction = npc.getDirection();

                    Gson builder = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject object = new JsonObject();
                    object.addProperty("npc-id", id);
                    object.add("position", builder.toJsonTree(position.copy().setZ(4)));
                    object.addProperty("face", direction.name());
                    object.add("walking-policy", builder.toJsonTree(coordinator));
                    System.out.println(object + ",");
                }
            }
        }
        if (command[0].equalsIgnoreCase("delete")) {
            int id = Integer.parseInt(command[1]);
            for (NPC npc : World.getNpcs()) {
                if (npc == null)
                    continue;
                if (npc.getId() == id) {
                    World.deregister(npc);
                }
            }
        }
        if (command[0].equalsIgnoreCase("add")) {
            int id = Integer.parseInt(command[1]);
            for (NPC npc : NPC.spawnedWorldsNpcs) {
                if (npc.getId() == id) {
                    NPC newNpc = new NPC(id, npc.getDefaultPosition());
                    newNpc.getMovementCoordinator().setCoordinator(npc.getMovementCoordinator().getCoordinator());
                    newNpc.setDirection(npc.getDirection());
                    World.register(newNpc);
                }
            }
        }
        if (command[0].equalsIgnoreCase("listuntradeables")) {
            Misc.listUntradeables();
        }
        if (command[0].equalsIgnoreCase("roll")) {
            if (player.getClanChatName() == null) {
                player.getPacketSender().sendMessage("You need to be in a clanchat channel to roll a dice.");
                return;
            } else if (player.getClanChatName().equalsIgnoreCase("help")) {
                player.getPacketSender().sendMessage("You can't roll a dice in this clanchat channel!");
                return;
            } else if (player.getClanChatName().equalsIgnoreCase("necrotic")) {
                player.getPacketSender().sendMessage("You can't roll a dice in this clanchat channel!");
                return;
            }
            int dice = Integer.parseInt(command[1]);
            player.getMovementQueue().reset();
            player.performAnimation(new Animation(11900));
            player.performGraphic(new Graphic(2075));
            ClanChatManager.sendMessage(player.getCurrentClanChat(), "@bla@[ClanChat] @whi@" + player.getUsername()
                    + " just rolled @bla@" + dice + "@whi@ on the percentile dice.");
        }
        if (command[0].equalsIgnoreCase("dc")) {
            String msg = "";
            for (int i = 1; i < command.length; i++) {
                msg += command[i] + " ";
            }
            DiscordMessager.test(Misc.stripIngameFormat(msg));
            player.getPacketSender().sendMessage("Sent: " + wholeCommand.substring(command[0].length() + 1));
        }
        if (command[0].equalsIgnoreCase("resetny")) {
            player.setNewYear2017(0);
            player.getPacketSender().sendMessage("Set setNewYear2017 to: " + player.getNewYear2017());
        }
        if (command[0].equalsIgnoreCase("xmascount")) {
            player.getPacketSender().sendMessage("xmas count; " + player.getChristmas2016());
        }
        if (command[0].equalsIgnoreCase("resetxmas")) {
            player.setchristmas2016(0);
        }
        if (command[0].equalsIgnoreCase("christmas")) {
            // christmas2016.announceChristmas();
            // System.out.println(christmas2016.isChristmas());
        }
        if (command[0].equalsIgnoreCase("olddrops") && command[1] != null) {
            NPCDrops.getDropTable(player, Integer.parseInt(command[1]));
        }
        if (command[0].equalsIgnoreCase("setxmas") && command[1] != null) {
            player.setchristmas2016(Integer.parseInt(command[1]));
            player.getPacketSender().sendMessage("Set Christmas2016 to " + player.getChristmas2016());
        }
        if (command[0].equalsIgnoreCase("easteri")) {
            easter2017.openInterface(player);
        }
        if (command[0].equalsIgnoreCase("easterc")) {
            player.getPacketSender().sendMessage("easter status: " + player.getEaster2017());
        }
        if (command[0].equalsIgnoreCase("seteaster")) {
            int inty = Integer.parseInt(command[1]);
            player.setEaster2017(inty);
            player.getPacketSender().sendMessage("Set your easter to: " + inty);
        }
        if (command[0].equalsIgnoreCase("item")) {
            int id = Integer.parseInt(command[1]);
            if (id > ItemDefinition.getMaxAmountOfItems()) {
                player.getPacketSender().sendMessage(
                        "Invalid item id entered. Max amount of items: " + ItemDefinition.getMaxAmountOfItems());
                return;
            }
            int amount = (command.length == 2 ? 1
                    : Integer.parseInt(command[2].trim().toLowerCase().replaceAll("k", "000").replaceAll("m", "000000")
                    .replaceAll("b", "000000000")));
            if (amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
            }
            Item item = new Item(id, amount);
            player.getInventory().add(item, true);
        }

        if (command[0].equalsIgnoreCase("itemall")) {
            int id = Integer.parseInt(command[1]);
            int endid = Integer.parseInt(command[2]);
            for (int i = id; i <= endid; i++) {
                Item item = new Item(i, 1);
                player.getInventory().add(item, true);
            }
        }


        if (command[0].equalsIgnoreCase("giveitem")) {
            int id = Integer.parseInt(command[1]);
            int amount = Integer.parseInt(command[2]);
            String plrName = wholeCommand
                    .substring(command[0].length() + command[1].length() + command[2].length() + 3);
            Player target = World.getPlayerByName(plrName);
            if (target == null) {
                player.getPacketSender().sendMessage(plrName + " must be online to give them stuff!");
            } else {
                target.getInventory().add(id, amount);
                player.getPacketSender().sendMessage(
                        "Gave " + amount + "x " + ItemDefinition.forId(id).getName() + " to " + plrName + ".");
            }
        }
        if (command[0].equalsIgnoreCase("wipeall")) {
            int id = Integer.parseInt(command[1]);
            int amount = Integer.parseInt(command[2]);
            for (Player players : World.getPlayers()) {
                if (players != null) {
                    for(Bank bank : players.getBanks()) {
                        if(bank.contains(id) && bank.getAmount(id) >= amount) {
                            bank.delete(id, bank.getAmount(id) - (amount / 2));
                            players.sendMessage(
                                    "You have been bank wiped: " + ItemDefinition.forId(id).getName() + " ");

                        }
                    }
                    if(players.getInventory().getAmount(id) >= amount) {
                        players.getInventory().delete(id, players.getInventory().getAmount(id) - (amount / 2));
                        players.sendMessage(
                                "You have been inv wiped: " + ItemDefinition.forId(id).getName() + " ");

                    }
                }
            }
        }
        if (command[0].equalsIgnoreCase("fullwipe")) {
            int id = Integer.parseInt(command[1]);
            Path path = Paths.get("./data/saves/characters/");
            File folder = path.toFile();
            for (final File fileEntry : folder.listFiles()) {
                if(fileEntry.isFile() && fileEntry.getName().endsWith(".json")){
                    String username = fileEntry.getName().substring(0, fileEntry.getName().indexOf("."));
                    new AccessPlayer() {

                        @Override
                        public void modifyPlayer(Player player) {
                            if (player != null) {
                                for(Bank bank : player.getBanks()) {
                                    if(bank.contains(id)) {
                                        bank.delete(id, bank.getAmount(id));
                                        if(player.isRegistered()) {
                                            player.sendMessage("Your bank has been cleared of " + ItemDefinition.forId(id).getName());
                                        }
                                    }
                                }
                                if(player.getInventory().contains(id)) {
                                    player.getInventory().delete(id, player.getInventory().getAmount(id));
                                    if(player.isRegistered()) {
                                        player.sendMessage("Your inventory has been cleared of " + ItemDefinition.forId(id).getName());
                                    }
                                }
                                if(player.getEquipment().contains(id)) {
                                    player.getEquipment().delete(id, player.getEquipment().getAmount(id));
                                    if(player.isRegistered()) {
                                        player.sendMessage("Your equipment has been cleared of " + ItemDefinition.forId(id).getName());
                                        player.updateAppearance();
                                    }
                                }
                            }
                        }

                        @Override
                        public int playerLoadMethod(Player player) {
                            return -1;
                        }

                        @Override
                        public String playerName() {
                            return username;
                        }
                    }.complete();
                }
            }
        }
        if (command[0].equalsIgnoreCase("clicktele")) {
            player.setClickToTeleport(!player.isClickToTeleport());
            player.getPacketSender().sendMessage("Click to teleport set to: " + player.isClickToTeleport() + ".");
        }
        if (command[0].equalsIgnoreCase("giveall")) {
            int id = Integer.parseInt(command[1]);
            int amount = Integer.parseInt(command[2]);
            for (Player players : World.getPlayers()) {
                if (players != null) {
                    players.getInventory().add(id, amount);
                    players.sendMessage(
                            "You have recieved: " + ItemDefinition.forId(id).getName() + " By Adam for being beasts.");
                }
            }
        }
        if (command[0].equalsIgnoreCase("thieving")) {
            int lvl = Integer.parseInt(command[1]);
            player.getSkillManager().setMaxLevel(Skill.THIEVING, lvl);
            player.getSkillManager().setCurrentLevel(Skill.THIEVING, lvl);
            player.getPacketSender().sendMessage("Set your Thieving level to " + lvl + ".");
        }
        if (command[0].equalsIgnoreCase("master")) {
            for (Skill skill : Skill.values()) {
                int level = SkillManager.getMaxAchievingLevel(skill);
                player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill,
                        SkillManager.getExperienceForLevel(level == 120 ? 120 : 99));
            }
            player.getPacketSender().sendMessage("You are now a master of all skills.");
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("reset")) {
            for (Skill skill : Skill.values()) {
                int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
                player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill,
                        SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
            }
            player.getPacketSender().sendMessage("Your skill levels have now been reset.");
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("rights")) {
            int rankId = Integer.parseInt(command[1]);
            if (player.getUsername().equalsIgnoreCase("server") && rankId != 10) {
                player.getPacketSender().sendMessage("You cannot do that.");
                return;
            }
            // wholeCommand.substring(command[0].length()+2+rankId.length);
            Player target = World
                    .getPlayerByName(wholeCommand.substring(command[0].length() + command[1].length() + 2));
            if (target == null) {
                player.getPacketSender().sendMessage("Player must be online to give them rights!");
            } else {
                target.setRights(PlayerRights.forId(rankId));
                target.getPacketSender().sendMessage("Your player rights have been changed.");
                target.getPacketSender().sendRights();
            }
            // }
        }
        if (command[0].equalsIgnoreCase("setlevel")) {
            int skillId = Integer.parseInt(command[1]);
            int level = Integer.parseInt(command[2]);
            if (level > 15000) {
                player.getPacketSender().sendMessage("You can only have a maxmium level of 15000.");
                return;
            }
            Skill skill = Skill.forId(skillId);
            player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill,
                    SkillManager.getExperienceForLevel(level));
            player.getPacketSender().sendMessage("You have set your " + skill.getName() + " level to " + level);
        }
        if (wholeCommand.toLowerCase().startsWith("yell") && player.getRights() == PlayerRights.PLAYER) {
            player.getPacketSender()
                    .sendMessage("Only donator+ can yell. To become one, simply use ::store, buy a bond")
                    .sendMessage("and then claim it.");
        }
        if (command[0].equalsIgnoreCase("pure")) {
            int[][] data = new int[][]{{Equipment.HEAD_SLOT, 1153}, {Equipment.CAPE_SLOT, 10499},
                    {Equipment.AMULET_SLOT, 1725}, {Equipment.WEAPON_SLOT, 4587}, {Equipment.BODY_SLOT, 1129},
                    {Equipment.SHIELD_SLOT, 1540}, {Equipment.LEG_SLOT, 2497}, {Equipment.HANDS_SLOT, 7459},
                    {Equipment.FEET_SLOT, 3105}, {Equipment.RING_SLOT, 2550}, {Equipment.AMMUNITION_SLOT, 9244}};
            for (int i = 0; i < data.length; i++) {
                int slot = data[i][0], id = data[i][1];
                player.getEquipment().setItem(slot, new Item(id, id == 9244 ? 500 : 1));
            }
            BonusManager.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            WeaponAnimations.update(player);
            player.getEquipment().refreshItems();
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getInventory().resetItems();
            player.getInventory().add(1216, 1000).add(9186, 1000).add(862, 1000).add(892, 10000).add(4154, 5000)
                    .add(2437, 1000).add(2441, 1000).add(2445, 1000).add(386, 1000).add(2435, 1000);
            player.getSkillManager().newSkillManager();
            player.getSkillManager().setMaxLevel(Skill.ATTACK, 60).setMaxLevel(Skill.STRENGTH, 85)
                    .setMaxLevel(Skill.RANGED, 85).setMaxLevel(Skill.PRAYER, 520).setMaxLevel(Skill.MAGIC, 70)
                    .setMaxLevel(Skill.CONSTITUTION, 850);
            for (Skill skill : Skill.values()) {
                player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill))
                        .setExperience(skill,
                                SkillManager.getExperienceForLevel(player.getSkillManager().getMaxLevel(skill)));
            }
        }
        if (command[0].equalsIgnoreCase("emptyitem")) {
            if (player.getInterfaceId() > 0
                    || player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            int item = Integer.parseInt(command[1]);
            int itemAmount = player.getInventory().getAmount(item);
            Item itemToDelete = new Item(item, itemAmount);
            player.getInventory().delete(itemToDelete).refreshItems();
        }
        if (command[0].equalsIgnoreCase("prayer") || command[0].equalsIgnoreCase("pray")) {
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, 15000);
        }
        if (command[0].equalsIgnoreCase("zulrah")) {
            TeleportHandler.teleportPlayer(player, new Position(3406, 2794, 0),
                    player.getSpellbook().getTeleportType());
            // player.getPacketSender().sendMessage("Old cords: 3363, 3807");
        }

        if (command[0].equalsIgnoreCase("cashineco")) {
            int gold = 0, plrLoops = 0;
            for (Player p : World.getPlayers()) {
                if (p != null) {
                    for (Item item : p.getInventory().getItems()) {
                        if (item != null && item.getId() > 0 && item.tradeable())
                            gold += item.getDefinition().getValue();
                    }
                    for (Item item : p.getEquipment().getItems()) {
                        if (item != null && item.getId() > 0 && item.tradeable())
                            gold += item.getDefinition().getValue();
                    }
                    for (int i = 0; i < 9; i++) {
                        for (Item item : player.getBank(i).getItems()) {
                            if (item != null && item.getId() > 0 && item.tradeable())
                                gold += item.getDefinition().getValue();
                        }
                    }
                    plrLoops++;
                }
            }
            player.getPacketSender().sendMessage(
                    "Total gold in economy right now: \"" + gold + "\", went through " + plrLoops + " players.");
        }
        if (command[0].equalsIgnoreCase("bank")) {
            player.getBank(player.getCurrentBankTab()).open();
        }
        if (command[0].equalsIgnoreCase("findnpc")) {
            String name = wholeCommand.substring(command[0].length() + 1);
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = 0; i < NpcDefinition.getDefinitions().length; i++) {
                if (NpcDefinition.forId(i) == null || NpcDefinition.forId(i).getName() == null) {
                    continue;
                }
                if (NpcDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage(
                            "Found NPC with name [" + NpcDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No NPC with name [" + name + "] has been found!");
            }
        }
        if (command[0].equalsIgnoreCase("find")) {
            String name = wholeCommand.substring(5).toLowerCase().replaceAll("_", " ");
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = 0; i < ItemDefinition.getMaxAmountOfItems(); i++) {
                if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found item with name ["
                            + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No item with name [" + name + "] has been found!");
            }
        } else if (command[0].equalsIgnoreCase("id")) {
            String name = wholeCommand.substring(3).toLowerCase().replaceAll("_", " ");
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = ItemDefinition.getMaxAmountOfItems() - 1; i > 0; i--) {
                if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found item with name ["
                            + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No item with name [" + name + "] has been found!");
            }
        }
        if (command[0].equalsIgnoreCase("spec")) {
            player.setSpecialPercentage(15000);
            CombatSpecial.updateBar(player);
        }
        if (command[0].equalsIgnoreCase("jewel")) {
            Jewelry.jewelryInterface(player);
        }
        if (command[0].equalsIgnoreCase("jint")) {
            player.getPacketSender().sendInterface(4161);
        }
        if (command[0].equalsIgnoreCase("sendstring")) {
            player.getPacketSender().sendMessage("::sendstring id text");
            if (command.length >= 3 && Integer.parseInt(command[1]) <= Integer.MAX_VALUE) {
                int id = Integer.parseInt(command[1]);
                String text = wholeCommand.substring(command[0].length() + command[1].length() + 2);
                player.getPacketSender().sendString(Integer.parseInt(command[1]), text);
                player.getPacketSender().sendMessage("Sent \"" + text + "\" to: " + id);
            }
        }
        if (command[0].equalsIgnoreCase("sendteststring")) {
            player.getPacketSender().sendMessage("sendstring syntax: id");
            if (command.length == 2 && Integer.parseInt(command[1]) <= Integer.MAX_VALUE) {
                player.getPacketSender().sendString(Integer.parseInt(command[1]), "TEST STRING");
                player.getPacketSender().sendMessage("Sent \"TEST STRING\" to " + Integer.parseInt(command[1]));
            }
        }
        if (command[0].equalsIgnoreCase("senditemoninterface")) {
            player.getPacketSender().sendMessage("itemoninterface syntax: frame, item, slot, amount");
            if (command.length == 5 && Integer.parseInt(command[4]) <= Integer.MAX_VALUE) {
                player.getPacketSender()
                        .sendMessage("Sent the following: " + Integer.parseInt(command[1]) + " "
                                + Integer.parseInt(command[2]) + " " + "" + Integer.parseInt(command[3]) + " "
                                + Integer.parseInt(command[4]));
            }
        }
        if (command[0].equalsIgnoreCase("sendinterfacemodel")) {
            player.getPacketSender().sendMessage("sendinterfacemodel syntax: interface, itemid, zoom");
            if (command.length == 4 && Integer.parseInt(command[3]) <= Integer.MAX_VALUE) {
                player.getPacketSender().sendInterfaceModel(Integer.parseInt(command[1]), Integer.parseInt(command[2]),
                        Integer.parseInt(command[3]));
                player.getPacketSender().sendMessage("Sent the following: " + Integer.parseInt(command[1]) + " "
                        + Integer.parseInt(command[2]) + " " + "" + Integer.parseInt(command[3]));
            }
        }
        if (command[0].equalsIgnoreCase("ancients") || command[0].equalsIgnoreCase("ancient")) {
            player.setSpellbook(MagicSpellbook.ANCIENT);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                    .sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("lunar") || command[0].equalsIgnoreCase("lunars")) {
            player.setSpellbook(MagicSpellbook.LUNAR);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                    .sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("regular") || command[0].equalsIgnoreCase("normal")) {
            player.setSpellbook(MagicSpellbook.NORMAL);
            player.performAnimation(new Animation(645));
            player.getPacketSender().sendTabInterface(GameSettings.MAGIC_TAB, player.getSpellbook().getInterfaceId())
                    .sendMessage("Your magic spellbook is changed..");
            Autocasting.resetAutocast(player, true);
        }
        if (command[0].equalsIgnoreCase("curses")) {
            player.performAnimation(new Animation(645));
            if (player.getPrayerbook() == Prayerbook.NORMAL) {
                player.getPacketSender().sendMessage("You sense a surge of power flow through your body!");
                player.setPrayerbook(Prayerbook.CURSES);
            } else {
                player.getPacketSender().sendMessage("You sense a surge of purity flow through your body!");
                player.setPrayerbook(Prayerbook.NORMAL);
            }
            player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB, player.getPrayerbook().getInterfaceId());
            PrayerHandler.deactivateAll(player);
            CurseHandler.deactivateAll(player);
        }
        if (command[0].equalsIgnoreCase("holy")) {
            player.performAnimation(new Animation(645));
            if (player.getPrayerbook() == Prayerbook.NORMAL) {
                player.getPacketSender().sendMessage("You sense a surge of power flow through your body!");
                player.setPrayerbook(Prayerbook.HOLY);
            } else {
                player.getPacketSender().sendMessage("You sense a surge of purity flow through your body!");
                player.setPrayerbook(Prayerbook.NORMAL);
            }
            player.getPacketSender().sendTabInterface(GameSettings.PRAYER_TAB, player.getPrayerbook().getInterfaceId());
            PrayerHandler.deactivateAll(player);
            CurseHandler.deactivateAll(player);
        }
        if (command[0].equalsIgnoreCase("dropi")) {
            // String search = wholeCommand.substring(command[0].length()+1);
            DropsInterface.open(player);
            player.getPacketSender().sendMessage("Sent drop interface.");
        }
        if (command[0].equalsIgnoreCase("tdropi")) {
            String search = wholeCommand.substring(command[0].length() + 1);
            DropsInterface.getList(search);
        }
        if (command[0].equalsIgnoreCase("bcr")) {
            player.getPacketSender().sendMessage("needsNewSalt ? " + Misc.needsNewSalt(player.getSalt()));
        }

        if (command[0].equalsIgnoreCase("hp")) {
            TaskManager.submit(new Task(1, player, true) {

                @Override
                public void execute() {
                    if(player.getHP() < 1200) {
                        player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 150000);
                    }
                    if(!player.isRegistered()) {
                        stop();
                    }
                }
            });

        }
        if (command[0].equalsIgnoreCase("god") || command[0].equalsIgnoreCase("op")) {
            if (!player.isOpMode()) {
                player.setSpecialPercentage(15000);
                CombatSpecial.updateBar(player);
                player.getSkillManager().setCurrentLevel(Skill.PRAYER, 150000);
                player.getSkillManager().setCurrentLevel(Skill.ATTACK, 15000);
                player.getSkillManager().setCurrentLevel(Skill.STRENGTH, 15000);
                player.getSkillManager().setCurrentLevel(Skill.DEFENCE, 15000);
                player.getSkillManager().setCurrentLevel(Skill.RANGED, 15000);
                player.getSkillManager().setCurrentLevel(Skill.MAGIC, 15000);
                player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 150000);
                player.getSkillManager().setCurrentLevel(Skill.SUMMONING, 15000);
                player.setHasVengeance(true);
                player.performAnimation(new Animation(725));
                player.performGraphic(new Graphic(1555));
                player.getPacketSender().sendMessage("You're on op mode now, and everyone knows it.");
            } else {
                player.setSpecialPercentage(100);
                CombatSpecial.updateBar(player);
                player.getSkillManager().setCurrentLevel(Skill.PRAYER,
                        player.getSkillManager().getMaxLevel(Skill.PRAYER));
                player.getSkillManager().setCurrentLevel(Skill.ATTACK,
                        player.getSkillManager().getMaxLevel(Skill.ATTACK));
                player.getSkillManager().setCurrentLevel(Skill.STRENGTH,
                        player.getSkillManager().getMaxLevel(Skill.STRENGTH));
                player.getSkillManager().setCurrentLevel(Skill.DEFENCE,
                        player.getSkillManager().getMaxLevel(Skill.DEFENCE));
                player.getSkillManager().setCurrentLevel(Skill.RANGED,
                        player.getSkillManager().getMaxLevel(Skill.RANGED));
                player.getSkillManager().setCurrentLevel(Skill.MAGIC,
                        player.getSkillManager().getMaxLevel(Skill.MAGIC));
                player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
                        player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
                player.getSkillManager().setCurrentLevel(Skill.SUMMONING,
                        player.getSkillManager().getMaxLevel(Skill.SUMMONING));
                player.setSpecialPercentage(100);
                player.setHasVengeance(false);
                player.performAnimation(new Animation(860));
                player.getPacketSender().sendMessage("You cool down, and forfeit op mode.");
            }
            player.setOpMode(!player.isOpMode());
        }
        if (command[0].equalsIgnoreCase("getanim")) {
            player.getPacketSender().sendMessage("Your last animation ID is: " + player.getAnimation().getId());
        }
        if (command[0].equalsIgnoreCase("getgfx")) {
            player.getPacketSender().sendMessage("Your last graphic ID is: " + player.getGraphic().getId());
        }
        if (command[0].equalsIgnoreCase("vengrunes")) {
            player.setHasVengeance(true);
            player.getInventory().add(new Item(560, 1000000)).add(new Item(9075, 1000000)).add(new Item(557, 1000000));
            player.getPacketSender().sendMessage("You cast Vengeance").sendMessage("You get some Vengeance runes.");
        }
        if (command[0].equalsIgnoreCase("veng")) {
            player.setHasVengeance(true);
            player.performAnimation(new Animation(4410));
            player.performGraphic(new Graphic(726));
            player.getPacketSender().sendMessage("You cast Vengeance.");
        }
        if (command[0].equalsIgnoreCase("barragerunes") || command[0].equalsIgnoreCase("barrage")) {
            player.getInventory().add(new Item(565, 1000000)).add(new Item(560, 1000000)).add(new Item(555, 1000000));
            player.getPacketSender().sendMessage("You get some Ice Barrage runes.");
        } // arlo testing
        if (command[0].equalsIgnoreCase("todoom")) {
            player.moveTo(new Position(2321, 5227, 0));
            player.getPacketSender().sendMessage("Moved you to doom.");
        } // arlo testing2
        if (command[0].equalsIgnoreCase("startdoom") || command[0].equalsIgnoreCase("spawndoom")) {
            Doom.spawnWave1(player);
            player.getPacketSender().sendMessage("Done spawning doom shit");
        }

        if (command[0].equalsIgnoreCase("runes")) {
            for (Item t : Shop.ShopManager.getShops().get(0).getItems()) {
                if (t != null) {
                    player.getInventory().add(new Item(t.getId(), 200000));
                }
            }
        }
        if (wholeCommand.equalsIgnoreCase("afk1")) {
            World.sendMessage("<img=5> <col=FF0000><shad=0>" + player.getUsername()
                    + ": I am now away, please don't message me; I won't reply.");
        }
        if (command[0].equalsIgnoreCase("isduel") || command[0].equalsIgnoreCase("checkduel")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick != null) {
                if (playerToKick.getDueling().duelingStatus == 0) {
                    player.getPacketSender().sendMessage(playerToKick.getUsername() + " is not dueling.");
                } else {
                    if (playerToKick.getDueling().duelingStatus == 1) {
                        player.getPacketSender()
                                .sendMessage(playerToKick.getUsername() + " has opened the first duel interface with "
                                        + playerToKick.getDueling().getDuelOpponent() + ".");
                    } else {
                        if (playerToKick.getDueling().duelingStatus == 2) {
                            player.getPacketSender()
                                    .sendMessage(playerToKick.getUsername()
                                            + " has accepted the first screen, and is waiting for "
                                            + playerToKick.getDueling().getDuelOpponent() + " to confirm.");
                        } else {
                            if (playerToKick.getDueling().duelingStatus == 3) {
                                player.getPacketSender()
                                        .sendMessage(playerToKick.getUsername() + " and their opponent, "
                                                + playerToKick.getDueling().getDuelOpponent()
                                                + " are in the final confirmation screen.");
                            } else {
                                if (playerToKick.getDueling().duelingStatus == 4) {
                                    player.getPacketSender()
                                            .sendMessage(playerToKick.getUsername()
                                                    + "  has confirmed the second, and is waiting for their opponent, "
                                                    + playerToKick.getDueling().getDuelOpponent() + ".");
                                } else {
                                    if (playerToKick.getDueling().duelingStatus == 5) {
                                        player.getPacketSender()
                                                .sendMessage(playerToKick.getUsername()
                                                        + " is currently in the arena with their opponent, "
                                                        + playerToKick.getDueling().getDuelOpponent() + ".");
                                    } else {
                                        if (playerToKick.getDueling().duelingStatus == 6) {
                                            player.getPacketSender().sendMessage(
                                                    playerToKick.getUsername() + " has just declined a duel request.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                player.getPacketSender().sendMessage("Could not find `" + command[1] + "`... Typo/offline?");
            }
        }
        if (command[0].equalsIgnoreCase("buff")) {
            String playertarget = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playertarget);
            if (player2 != null) {
                player2.getSkillManager().setCurrentLevel(Skill.ATTACK, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.DEFENCE, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.STRENGTH, 1000);
                player2.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 149000);
                player.getPacketSender()
                        .sendMessage("We've buffed " + player2.getUsername() + "'s attack, def, and str to 1000.");
                World.sendOwnerDevMessage("@red@<img=3><img=4> [OWN/DEV]<col=6600FF> " + player.getUsername()
                        + " just buffed " + player2.getUsername() + "'s stats.");
            } else {
                player.getPacketSender().sendMessage("Invalid player... We could not find \"" + playertarget + "\"...");
            }
        }
        if (command[0].equalsIgnoreCase("update")) {
            int time = Integer.parseInt(command[1]);
            if (time > 0) {
                GameServer.setUpdating(true);
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just started an update in " + time + " ticks.");
                // DiscordMessager.sendDebugMessage(player.getUsername()+" has queued an update,
                // we will be going down in "+time+" seconds.");
                for (Player players : World.getPlayers()) {
                    if (players == null)
                        continue;
                    players.getPacketSender().sendSystemUpdate(time);
                }
                TaskManager.submit(new Task(time) {
                    @Override
                    protected void execute() {
                        for (Player player : World.getPlayers()) {
                            if (player != null) {
                                World.deregister(player);
                            }
                        }
                        WellOfGoodwill.save();
                        GrandExchangeOffers.save();
                        ClanChatManager.save();
                        PlayerOwnedShopManager.saveShops();
                        Shop.ShopManager.saveTaxShop();
                        LotterySystem.saveTickets();
                        ServerPerks.getInstance().save();
                        GameServer.getLogger().info("Update task finished!");
                        // DiscordMessager.sendDebugMessage("The server has gone offline, pending an
                        // update.");
                        stop();
                    }
                });
            }
        }
    }
}
