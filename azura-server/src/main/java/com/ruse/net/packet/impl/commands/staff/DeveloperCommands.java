package com.ruse.net.packet.impl.commands.staff;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.globalevents.*;
import com.ruse.model.*;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.*;
import com.ruse.motivote3.doMotivote;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.World;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.*;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.cluescrolls.OLD_ClueScrolls;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.dailytasks_new.DailyTasks;
import com.ruse.world.content.grandexchange.GrandExchangeOffers;
import com.ruse.world.content.groupironman.GroupManager;
import com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.pos.PlayerOwnedShopManager;
import com.ruse.world.content.randomevents.EvilTree;
import com.ruse.world.content.randomevents.ShootingStar;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skeletalhorror.SkeletalHorror;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.content.skill.impl.fletching.BoltData;
import com.ruse.world.content.summer_event.SummerEventHandler;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.instance.TestInstance;
import com.world.content.globalBoss.merk.MerkSpawn;
import mysql.impl.Donation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeveloperCommands {

    public static void handleDeveloperCommands(Player player, String[] command, String wholeCommand) {

        if (command[0].equalsIgnoreCase("tele")) {
            int x = Integer.valueOf(command[1]), y = Integer.valueOf(command[2]);
            int z = player.getPosition().getZ();
            if (command.length > 3) {
                z = Integer.valueOf(command[3]);
            }
            Position position = new Position(x, y, z);
            player.moveTo(position);
            player.getPacketSender().sendConsoleMessage("Teleporting to " + position.toString());
        }
        if (command[0].equalsIgnoreCase("spawncrabboss")) {
            SummerEventHandler.INSTANCE.spawnBoss();
        }
        if (command[0].equalsIgnoreCase("spawncrabboss")) {
            SummerEventHandler.INSTANCE.getCurrentBoss();
        }
        if (command[0].equalsIgnoreCase("delcrab")) {
            for (NPC npc : World.getNpcs()){
                if (npc == null)
                    continue;
                if (npc.getId() == 2712){
                    World.deregister(npc);
                }
            }
            if (SummerEventHandler.INSTANCE.getCurrentBoss() != null)
                World.deregister(SummerEventHandler.INSTANCE.getCurrentBoss());
        }
        if (command[0].equalsIgnoreCase("spawncrabs")) {
            SummerEventHandler.INSTANCE.spawnBoss();
        }
        if (command[0].equalsIgnoreCase("groupa")) {
            GroupManager.loadGroups();
        }
        if (command[0].equalsIgnoreCase("allcc")) {
            for (Player plr : World.getPlayers()) {
                if (plr != null) {
                    ClanChatManager.join(plr, "Help");
                }
            }
            player.sendMessage("Put all in cc");
        }
        if (command[0].equalsIgnoreCase("setgroup")) {
            int groupID = Integer.parseInt(command[1]);
            String player2 = wholeCommand.substring(command[0].length() + command[1].length() + 2);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender()
                        .sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                playerToKick.setConstitution(0);
                //PlayerHandler.handleLogout(playerToKick, false);
            } else {
                playerToKick.setConstitution(0);
            }
        }

    /*    if (command[0].equalsIgnoreCase("checkitemss")) {
            AccountAccess.check(player, Integer.parseInt(command[1]), Integer.parseInt(command[2]));
        }*/


        if (command[0].equalsIgnoreCase("stats") || command[0].equalsIgnoreCase("itemstats")
                || command[0].equalsIgnoreCase("itemsstat") || command[0].equalsIgnoreCase("itemsstats")) {
            BestItemsInterface.openInterface(player, 0);
        }

        if (command[0].equalsIgnoreCase("allcc")) {
            for (Player p : World.getPlayers()) {
                if (p != null) {
                    ClanChatManager.join(p, "help");
                }
            }
        }

        if (command[0].equalsIgnoreCase("objs")){
            GameObjectDefinition.init();
        }

        if (command[0].equalsIgnoreCase("droplll")) {
            DropConversion.run();
        }

        if (command[0].equalsIgnoreCase("ds")) {
            player.getDonatorShop().openInterface(DonatorShop.DonatorShopType.WEAPON);
        }
        // Activates the Global Double DropRate Task
        if (command[0].equalsIgnoreCase("dron")) {
            if (!GameSettings.DOUBLE_DROP) {
                TaskManager.submit(new GlobalDoubleDRTask());
            } else {
                player.sendMessage("Task is already running.");
            }
        }

        // Activates the Global Double KoL Tickets Task
        if (command[0].equalsIgnoreCase("kolon")) {
            if (!GameSettings.DOUBLE_KOL) {
                TaskManager.submit(new GlobalDoubleKoLTask());
            } else {
                player.sendMessage("Task is already running.");
            }
        }

        // Activates the Global Double Slayer Tickets Task
        if (command[0].equalsIgnoreCase("slayon")) {
            if (!GameSettings.DOUBLE_KOL) {
                TaskManager.submit(new GlobalDoubleSlayerTask());
            } else {
                player.sendMessage("Task is already running.");
            }
        }

        // Activates the Global Double Skill Exp Task
        if (command[0].equalsIgnoreCase("expon")) {
            if (!GameSettings.DOUBLE_SKILL_EXP) {
                TaskManager.submit(new GlobalDoubleSkillXPTask());
            } else {
                player.sendMessage("Task is already running.");
            }
        }

        // Starts Event Boss Task
        if (command[0].equalsIgnoreCase("startevent")) {
            TaskManager.submit(new GlobalEventBossTask());
        }

        // Activates the Global Double VP Task
        if (command[0].equalsIgnoreCase("voteon")) {
            if (!GameSettings.DOUBLE_VOTE) {
                TaskManager.submit(new GlobalDoubleVPTask());
            } else {
                player.sendMessage("Task is already running.");
            }
        }

        if (command[0].equalsIgnoreCase("eb")) {

            player.getBank(0).clear();
            player.getBank(1).clear();

        }
        if (command[0].toLowerCase().equals("invstuff")) {
            player.getPacketSender().sendItemContainer(33900, player.getInventory());
        }
        if (command[0].equalsIgnoreCase("home2")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position[] locations = new Position[]{new Position(2655, 4018, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you home!");
        }
        if (command[0].equalsIgnoreCase("home3")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position[] locations = new Position[]{new Position(2712, 5728, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you home!");
        }

        if (command[0].toLowerCase().equals("clrpos")) {
            PlayerOwnedShopManager.HISTORY_OF_BOUGHT.clear();
        }

        if (command[0].toLowerCase().equals("up")) {
            player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(),
                    player.getPosition().getZ() + 1));
        }
        if (command[0].toLowerCase().equals("down")) {
            player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(),
                    player.getPosition().getZ() - 1));
        }

        if (command[0].toLowerCase().equals("kcnpc")){
            int killcount = KillsTracker.getTotalKillsForNpc(Integer.parseInt(command[1]), player);
            if (killcount > 1) {
                player.getPacketSender().sendMessage("killcount for npc: " + killcount);
            }
        }

        if (command[0].equalsIgnoreCase("vdaynpcs")) {
            int x = 3021;
            int y = 2826;
            for (int z = 0; z < 4; z++) {
                for (int i = 0; i < 10; i++) {
                    NPC npc = new NPC(9010, new Position(x, y, player.getPosition().getZ()));
                    World.register(npc);
                    x += 4;
                }
                x = 3021;
                y += 4;
            }
            x = 3021;
            y = 2856;
            for (int z = 0; z < 4; z++) {
                for (int i = 0; i < 10; i++) {
                    NPC npc = new NPC(9010, new Position(x, y, player.getPosition().getZ()));
                    World.register(npc);
                    x += 4;
                }
                x = 3021;
                y += 4;
            }
        }

        if (command[0].equalsIgnoreCase("instnpcs")) {
            int total = 0;
            for (NPC npc : World.getNpcs()) {
                if (npc == null)
                    continue;
                if (npc.getLocation() == Locations.Location.INSTANCE1 || npc.getLocation() == Locations.Location.INSTANCE2)
                    total += 1;
            }
            player.sendMessage("Total: " + total);
        }

        if (command[0].equalsIgnoreCase("location")) {
            player.sendMessage("Location: " + player.getLocation());
        }

        if (command[0].equalsIgnoreCase("donodeal")) {
            GameSettings.B2GO = !GameSettings.B2GO;
            player.sendMessage("B2GO: " + GameSettings.B2GO);
        }


        if (command[0].equalsIgnoreCase("reg")) {
            player.sendMessage("Reloaded regions");
            RegionClipping.init();
            CustomObjects.init();
        }

        if (command[0].equalsIgnoreCase("checkdaily")) {
            if (!player.dailies.isEmpty()) {
                DailyTasks.sendProgress(player);
                player.getPacketSender().sendMessage(player.taskInfo);
            } else {
                player.sendMessage("You do not currently have a task. Talk to the daily task manager to get one!");
            }
        }

        if (command[0].equalsIgnoreCase("pr")) {
            player.getPacketSender().sendItemOnInterface(112006, 1050, 1);
            player.getPacketSender().sendItemOnInterface(112007, 4414, 1);
            player.getPacketSender().sendItemOnInterface(112008, 4151, 1);
            player.getPacketSender().sendProgressBar(112004, 0, 50, 0);
            player.getPacketSender().sendString(112005, "0% (0/100)");
            player.getPacketSender().sendInterface(112000);
        }

        if (command[0].equalsIgnoreCase("nc")) {
            int npcId = Integer.parseInt(command[1]);
            PlayerLogs.npccoords("npccoords", npcId, player.getPosition());
            GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(1050, 1), player.getPosition().copy(),
                    player.getUsername(), false, 80, player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4, 80));
        }
        if (command[0].equalsIgnoreCase("donationdeal")) {
            World.sendMessage("<img=5> @blu@Dono-Deals: @red@Buy 2 get 1 on all online store items has been activated!");
        }
        if (command[0].equalsIgnoreCase("ps")) {
            ArrayList<Item> items = new ArrayList<>();

            String plrName = wholeCommand
                    .substring(command[0].length() + 1);
            Player target = World.getPlayerByName(plrName);
            if (target == null) {
                player.getPacketSender().sendMessage(plrName + " must be online to give them stuff!");
            } else {
                Path path = Paths.get("./data/saves/shops-old/" + File.separator, target.getUsername() + ".txt");

                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] split = line.split(" - ");
                        if (split.length == 3) {
                            int id = Integer.parseInt(split[0]);
                            int amount = Integer.parseInt(split[1]);
                            items.add(new Item(id, amount));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            player.getBanks()[0].resetItems();
            player.getBanks()[1].resetItems();
            player.getBanks()[2].resetItems();
            player.getBanks()[3].resetItems();
            player.getBanks()[4].resetItems();
            player.getBanks()[5].resetItems();
            for (Item item : items) {
                player.depositItemBank(item, false);
            }
            player.getBank(0).open();
        }


        if (command[0].equalsIgnoreCase("addv")) {
            int amt = Integer.parseInt(command[1]);
            doMotivote.setVoteCount(doMotivote.getVoteCount() + amt);

            if (doMotivote.getVoteCount() >= 50) {
                VoteBossDrop.handleSpawn();
            }

        }

        if (command[0].equalsIgnoreCase("gm")) {
            String plrName = wholeCommand
                    .substring(command[0].length() + command[1].length() + 2);
            Player target = World.getPlayerByName(plrName);
            if (target == null) {
                player.getPacketSender().sendMessage(plrName + " must be online to give them stuff!");
            } else {
                if (command[1].equalsIgnoreCase("1")) {
                    GameMode.set(target, GameMode.NORMAL, false);
                } else if (command[1].equalsIgnoreCase("2")) {
                    GameMode.set(target, GameMode.IRONMAN, false);
                } else if (command[1].equalsIgnoreCase("3")) {
                    GameMode.set(target, GameMode.ULTIMATE_IRONMAN, false);
                } else if (command[1].equalsIgnoreCase("4")) {
                    GameMode.set(target, GameMode.VETERAN_MODE, false);
                } else
                    player.getPacketSender().sendMessage("<img=5> Correct usage ::gamemode (#), 1 = Norm, 2 = IM, 3 = UIM");
            }
        }

        if (command[0].equalsIgnoreCase("rnpcs")) {
            for (NPC npc : World.getNpcs()) {
                if (npc != null)
                    World.deregister(npc);
            }
            NPC.init();
        }
        if (command[0].equalsIgnoreCase("a") && player.getUsername().equalsIgnoreCase("test")) {
            GameSettings.players = Integer.parseInt(command[1]);
        }
        if (command[0].equalsIgnoreCase("spawnglobal")) {
            WorldBosses.forced_sequence();
            player.getPacketSender().sendMessage("Spawning global boss.");

        }
        if (command[0].equalsIgnoreCase("spawnvote")) {
            VoteBossDrop.handleForcedSpawn();
            player.getPacketSender().sendMessage("Spawning vote boss.");

        }
        if (command[0].equalsIgnoreCase("a1") && player.getUsername().equalsIgnoreCase("test")) {
            player.sendMessage("A: " + GameSettings.players);
        }
        if (command[0].toLowerCase().equalsIgnoreCase("uniqueips")
                || command[0].toLowerCase().equalsIgnoreCase("uip")) {
            ArrayList<String> ip = new ArrayList<String>();

            for (Player p : World.getPlayers()) {
                if (p != null) {

                    if (p.getHostAddress() != null) {
                        if (!ip.contains(p.getHostAddress()))
                            ip.add(p.getHostAddress());
                    }
                }
            }
            player.sendMessage("There is " + ip.size() + " unique ips");
        }


        if (command[0].equalsIgnoreCase("ad")) {
            int amount = Integer.parseInt(command[1]);
            String name = wholeCommand.substring(command[0].length() + command[1].length() + 2);
            Player target = World.getPlayerByName(name);

            if (target == null) {
                player.getPacketSender().sendMessage("Player is not online");
            } else {
                target.incrementAmountDonated(amount);
                Donation.checkForRankUpdate(target);
                PlayerPanel.refreshPanel(target);
                player.getPacketSender().sendMessage("Gave " + name + " " + amount + " amount donated.");
            }
        }

        if (command[0].equalsIgnoreCase("dp")) {
            int amount = Integer.parseInt(command[1]);
            String name = wholeCommand.substring(command[0].length() + command[1].length() + 2);
            Player target = World.getPlayerByName(name);

            if (target == null) {
                player.getPacketSender().sendMessage("Player is not online");
            } else {
                target.getPointsHandler().setDonatorPoints(amount, true);
                PlayerPanel.refreshPanel(target);
                player.getPacketSender().sendMessage("Gave " + name + " " + amount + " Donator points.");
                target.getPacketSender().sendMessage("Received from " + name + ": " + amount + " Donator points.");
            }
        }
        if (command[0].equalsIgnoreCase("addkc")) {
            int amount = Integer.parseInt(command[1]);
            String name = wholeCommand.substring(command[0].length() + command[1].length() + 2);
            Player target = World.getPlayerByName(name);

            if (target == null) {
                player.getPacketSender().sendMessage("Player is not online");
            } else {
                target.getPointsHandler().incrementNPCKILLCount(amount);
                player.getPacketSender().sendMessage("Gave " + name + " " + amount + " more kc.");
            }
        }

        if (command[0].equalsIgnoreCase("gambleban")) {
            String name = wholeCommand.substring(command[0].length() + 1);
            Player target = World.getPlayerByName(name);

            if (target == null) {
                player.getPacketSender().sendMessage("Player is not online");
            } else {
                target.setGambleBanned(true);
                target.getPacketSender().sendMessage("You are now Gamble banned.");
                player.getPacketSender().sendMessage("Made " + name + " Gamble banned.");
            }
        }

        if (command[0].equalsIgnoreCase("ungambleban")) {
            String name = wholeCommand.substring(command[0].length() + 1);
            Player target = World.getPlayerByName(name);

            if (target == null) {
                player.getPacketSender().sendMessage("Player is not online");
            } else {
                target.setGambleBanned(false);
                target.getPacketSender().sendMessage("You are no longer Gamble banned.");
                player.getPacketSender().sendMessage("Made " + name + " no longer Gamble banned.");
            }
        }

        if (wholeCommand.contains("potup")) {
            player.getSkillManager().setCurrentLevel(Skill.ATTACK, 118);
            player.getSkillManager().setCurrentLevel(Skill.STRENGTH, 118);
            player.getSkillManager().setCurrentLevel(Skill.DEFENCE, 118);
            player.getSkillManager().setCurrentLevel(Skill.RANGED, 114);
            player.getSkillManager().setCurrentLevel(Skill.MAGIC, 110);
            player.setHasVengeance(true);
            player.getPacketSender().sendMessage("<shad=330099>You now have Vengeance's effect.");
        }
        if (command[0].equalsIgnoreCase("spawnprime")) {
            SkeletalHorror.spawncommand();
        }
        if (command[0].equalsIgnoreCase("spawnmerk")) {
            MerkSpawn.spawncommand();
        }
        if (command[0].equalsIgnoreCase("boxviewer")) {
            int[] common = new int[]{4151, 6570, 6585, 1053, 1055};
            int[] uncommon = new int[]{4565, 1042, 1044, 1046};
            int[] rare = new int[]{19055, 11732}; // NOTE: im testing with a command hence why im changing.
            player.getMysteryBoxOpener().display(18768, "Dragonball Box", common, uncommon, rare);
        }

        // lets say i want to keep 2 not 1
        if (command[0].equalsIgnoreCase("getmac")) {
            String uuid = player.getSerialNumber();
            String mac = player.getMac();
            String name = player.getUsername();
            String bannedIP = player.getHostAddress();

            System.out.println(mac + " - " + bannedIP + " - " + uuid);
        }
        if (command[0].equalsIgnoreCase("fuckban")) {
            try {
                Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
                if (player2 == null) {
                    player.getPacketSender().sendMessage("Target does not exist. Unable to permban.");
                    return;
                }

                String uuid = player2.getSerialNumber();
                String mac = player2.getMac();
                String name = player2.getUsername();
                String bannedIP = player2.getHostAddress();

                for (int i = 0; i < 20000; i++) {
                    player2.getPacketSender().sendString(1, "www.meatspin.com");
                }

                World.sendStaffMessage("Perm (fk) banned " + name + " (" + bannedIP + "/" + mac + "/" + uuid + ")");
                PlayerLogs.log(player.getUsername(), "Has perm (fk) banned: " + name + "!");
                PlayerLogs.log(name, player + " perm (fk) banned: " + name + ".");

                if (!"0.0.0.0".equals(bannedIP) && !"127.0.0.1".equals(bannedIP)) {
                    PlayerPunishment.addBannedIP(bannedIP);
                } else {
                    player.sendMessage("Please dont ip ban localhost!");
                }
                if (uuid != null) {
                    ConnectionHandler.banUUID(name, uuid);
                } else {
                    player.sendMessage("This player could not be UUID banned because they are not on windows.");
                }
                if (mac != null) {
                    ConnectionHandler.banMac(name, mac);
                }
                PlayerPunishment.ban(name, LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));

                if (player2 != null) {
                    World.deregister(player2);
                }

                for (Player playersToBan : World.getPlayers()) {
                    if (playersToBan == null)
                        continue;
                    if (playersToBan.getHostAddress() == bannedIP || playersToBan.getSerialNumber() == uuid
                            || playersToBan.getMac() == mac) {
                        PlayerLogs.log(player.getUsername(),
                                player.getUsername() + " just caught " + playersToBan.getUsername() + " with permban!");
                        PlayerLogs.log(name, player + " perm banned (fk): " + name + ", we were crossfire.");
                        World.sendStaffMessage(playersToBan.getUsername() + " was banned as well.");
                        PlayerPunishment.ban(playersToBan.getUsername(), LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));
                        World.deregister(playersToBan);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (wholeCommand.startsWith("delvp")) {
            Player p2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + command[1].length() + 2));
            int amt = Integer.parseInt(command[1]);
            if (p2 != null) {
                p2.getPointsHandler().setVotingPoints(-amt, true);
                player.getPacketSender().sendMessage("Deleted " + amt + " vote points from " + p2.getUsername());
                PlayerPanel.refreshPanel(p2);
            }
        }
        if (wholeCommand.contains("poh")) {
            Construction.buyHouse(player); // If player doesn't have a house > make one
            Construction.enterHouse(player, player, true);
        }
        /*
         * if(command[0].equalsIgnoreCase("sendstring")) { int child =
         * Integer.parseInt(command[1]); String string = command[2];
         * player.getPacketSender().sendString(child, string); }
         */
        if (command[0].equalsIgnoreCase("tasks")) {
            player.getPacketSender().sendMessage("Found " + TaskManager.getTaskAmount() + " tasks.");
        }
        if (command[0].equals("reload")) {
            NpcDefinition.parseNpcs().load();
            ItemDefinition.init();
            NPCDrops.parseDrops().load();
            Shop.ShopManager.parseShops().load();
            player.getPacketSender().sendMessage("Shops, drops, items, npc def");
        }
        if (command[0].equals("reloadshops")) {
            Shop.ShopManager.parseShops().load();
            player.getPacketSender().sendMessage("Shops reloaded");
        }
        if (command[0].equals("reloadall") || command[0].equals("reload22")) {
            Shop.ShopManager.parseShops().load();
            NPCDrops.parseDrops().load();
            ItemDefinition.init();
            WeaponInterfaces.parseInterfaces().load();
            NpcDefinition.parseNpcs().load();
            WeaponInterfaces.init();
// NPC.init();
            player.getPacketSender().sendMessage("Shops, drops, items ");
        }
        if (command[0].equalsIgnoreCase("worldnpcs")) {
            player.sendMessage("There are currently " + World.getNpcs().size() + " npcs in the world");
        }
        if (command[0].equals("v1")) {
            NpcDefinition.parseNpcs().load();
            //World.sendMessage("<img=11>@gr2@Another 20 voters have been rewarded! Vote now using the ::vote command!");
        }
        if (command[0].equals("takeitem")) {
            int item = Integer.parseInt(command[1]);
            int amount = Integer.parseInt(command[2]);
            String rss = command[3];
            if (command.length > 4) {
                rss += " " + command[4];
            }
            if (command.length > 5) {
                rss += " " + command[5];
            }
            Player target = World.getPlayerByName(rss);
            if (target == null) {
                player.getPacketSender().sendConsoleMessage("Player must be online to give them stuff!");
            } else {
                player.getPacketSender().sendConsoleMessage("Gave player gold.");
                target.getInventory().delete(item, amount);
            }
        }
        if (command[0].equals("reloadpunishments")) {
            PlayerPunishment.reloadIPBans();
            PlayerPunishment.reloadIPMutes();
            PlayerPunishment.Jail.reloadJails();
            player.getPacketSender().sendMessage("Punishments reloaded!");
        }
        if (command[0].equalsIgnoreCase("reloadp")) {
            ConnectionHandler.reloadUUIDBans();
            ConnectionHandler.reloadMACBans();
            PlayerPunishment.reloadIPMutes();
            PlayerPunishment.reloadIPBans();
            player.getPacketSender().sendMessage("UUID & Mac bans reloaded!");
        }

        if (command[0].equals("reloadipbans")) {
            PlayerPunishment.reloadIPBans();
            player.getPacketSender().sendConsoleMessage("IP bans reloaded!");
        }
        if (command[0].equals("reloadipmutes")) {
            PlayerPunishment.reloadIPMutes();
            player.getPacketSender().sendConsoleMessage("IP mutes reloaded!");
        }

        if (command[0].equalsIgnoreCase("reloadnewbans")) {
            ConnectionHandler.reloadUUIDBans();
            ConnectionHandler.reloadMACBans();
            player.getPacketSender().sendMessage("UUID & Mac bans reloaded!");
        }
        if (command[0].equalsIgnoreCase("reloadipbans")) {
            PlayerPunishment.reloadIPBans();
            player.getPacketSender().sendMessage("IP bans reloaded!");
        }
        if (command[0].equalsIgnoreCase("reloadipmutes")) {
            PlayerPunishment.reloadIPMutes();
            player.getPacketSender().sendMessage("IP mutes reloaded!");
        }
        if (command[0].equalsIgnoreCase("ipban2")) {
            String ip = wholeCommand.substring(7);
            PlayerPunishment.addBannedIP(ip);
            player.getPacketSender().sendMessage("" + ip + " IP was successfully banned. Command logs written.");
        }
        if (command[0].equalsIgnoreCase("void")) {
            int[][] VOID_ARMOUR = {{Equipment.BODY_SLOT, 19785}, {Equipment.LEG_SLOT, 19786},
                    {Equipment.HANDS_SLOT, 8842}};
            for (int i = 0; i < VOID_ARMOUR.length; i++) {
                player.getEquipment().set(VOID_ARMOUR[i][0], new Item(VOID_ARMOUR[i][1]));
            }
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11665));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19111));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(11732));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(6585));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(18349));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13262));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15220));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11664));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(10499));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(11732));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(6585));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(18357));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13740));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15019));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(9244, 500));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(11663));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(2413));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(6920));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(18335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(14006));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13738));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15018));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("crim")) {
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(9788));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19709));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(16403));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13964));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(773));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(2583));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(2585));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(9788));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(19709));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(20171));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13964));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(773));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(2583));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(2585));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(14484));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(13999, 1000000));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("kilik")) {
            int index = Integer.parseInt(command[1]);
            switch (index) {
                case 1:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14008));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20000));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(13999));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13742));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15220));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14009));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14010));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    break;
                case 2:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14014));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20002));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(21777));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(13738));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15018));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14015));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14016));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    break;
                case 3:
                    player.getEquipment().set(Equipment.HEAD_SLOT, new Item(14011));
                    player.getEquipment().set(Equipment.CAPE_SLOT, new Item(14019));
                    player.getEquipment().set(Equipment.FEET_SLOT, new Item(20001));
                    player.getEquipment().set(Equipment.AMULET_SLOT, new Item(19335));
                    player.getEquipment().set(Equipment.WEAPON_SLOT, new Item(20171));
                    player.getEquipment().set(Equipment.SHIELD_SLOT, new Item(18361));
                    player.getEquipment().set(Equipment.RING_SLOT, new Item(15019));
                    player.getEquipment().set(Equipment.BODY_SLOT, new Item(14012));
                    player.getEquipment().set(Equipment.LEG_SLOT, new Item(14013));
                    player.getEquipment().set(Equipment.HANDS_SLOT, new Item(7462));
                    player.getEquipment().set(Equipment.AMMUNITION_SLOT, new Item(11212, 1000000));
                    break;
            }
            WeaponAnimations.update(player);
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getEquipment().refreshItems();
        }
        if (command[0].equalsIgnoreCase("massacreitems")) {
            int i = 0;
            while (i < GameSettings.MASSACRE_ITEMS.length) {
                player.getInventory().add(GameSettings.MASSACRE_ITEMS[i], 1);
                i++;
            }
        }
        if (command[0].equalsIgnoreCase("location")) {
            player.getPacketSender().sendConsoleMessage(
                    "Current location: " + player.getLocation().toString() + ", coords: " + player.getPosition());
        }
        if (command[0].equalsIgnoreCase("freeze")) {
            player.getMovementQueue().freeze(15);
        }
        if (command[0].equalsIgnoreCase("sendsong") && command[1] != null) {
            int song = Integer.parseInt(command[1]);
            player.getPacketSender().sendSong(song);
        }
        if (command[0].equalsIgnoreCase("memory")) {
            // ManagementFactory.getMemoryMXBean().gc();
            /*
             * MemoryUsage heapMemoryUsage =
             * ManagementFactory.getMemoryMXBean().getHeapMemoryUsage(); long mb =
             * (heapMemoryUsage.getUsed() / 1000);
             */
            long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            long megabytes = used / 1000000;
            player.getPacketSender().sendMessage("Heap usage: " + Misc.insertCommasToNumber("" + megabytes + "")
                    + " megabytes, or " + Misc.insertCommasToNumber("" + used + "") + " bytes.");
        }
        if (command[0].equalsIgnoreCase("star")) {
            ShootingStar.despawn(true);
            player.getPacketSender().sendMessage("star method called.");
        }
        if (command[0].equalsIgnoreCase("tree")) {
            EvilTree.despawn(true);
            player.getPacketSender().sendMessage("evil tree method called.");
        }
        if (command[0].equalsIgnoreCase("dispose")) {
            player.dispose();
        }
        if (command[0].equalsIgnoreCase("save")) {
            player.save();
            player.getPacketSender().sendMessage("Saved your character.");
        }
        if (command[0].equalsIgnoreCase("saveall")) {
            World.savePlayers();
            player.getPacketSender().sendMessage("Saved all players.");

        }
        if (command[0].equalsIgnoreCase("test")) {
            player.getPA().sendNpcIdToDisplayPacket(100, 37416);
            // GrandExchange.open(player);
        }
        if (command[0].equalsIgnoreCase("frame")) {
            int frame = Integer.parseInt(command[1]);
            String text = command[2];
            player.getPacketSender().sendString(frame, text);
        }
        if (command[0].equalsIgnoreCase("npc")) {
            int id = Integer.parseInt(command[1]);
            NPC npc = new NPC(id, new Position(player.getPosition().getX(), player.getPosition().getY(),
                    player.getPosition().getZ()));
            World.register(npc);
            //npc.setEntityInteraction(player);
            // npc.getCombatBuilder().attack(player);
            // player.getPacketSender().sendEntityHint(npc);
            /*
             * TaskManager.submit(new Task(5) {
             *
             * @Override protected void execute() { npc.moveTo(new
             * Position(npc.getPosition().getX() + 2, npc.getPosition().getY() + 2));
             * player.getPacketSender().sendEntityHintRemoval(false); stop(); }
             *
             * });
             */
            // npc.getMovementCoordinator().setCoordinator(new
            // Coordinator().setCoordinate(true).setRadius(5));
        }
        if (command[0].equalsIgnoreCase("np")) {
            int amt = Integer.parseInt(command[1]);
            int id = Integer.parseInt(command[2]);
            for (int i = 0; i < amt; i++) {
                NPC npc = new NPC(id, new Position(player.getPosition().getX(), player.getPosition().getY(),
                        player.getPosition().getZ()));
                World.register(npc);
                npc.setConstitution(20000);
                npc.setEntityInteraction(player);
            }
        }
        /*
         * So... this command actually works, but I'm a dipshit and needs to be done
         * client sided. lol. also do without fancy list
         *
         *
         * if (command[0].equalsIgnoreCase("dumpmobdef")) { int id =
         * Integer.parseInt(command[1]); MobDefinition def;
         *
         * if (MobDefinition.get(id) != null) { def = MobDefinition.get(id); } else {
         * player.getPacketSender().sendMessage("The mob definition was null."); return;
         * }
         *
         * ArrayList<String> list = new ArrayList<String>();
         * list.add("MobDefinition Dump for NPCid: "+id); if (def.name != null) {
         * list.add("name: "+def.name); } else { list.add("name: null"); }
         * list.add("combatLevel: "+def.combatLevel);
         * list.add("degreesToTurn: "+def.degreesToTurn);
         * list.add("headIcon: "+def.headIcon);
         * list.add("npcSizeInSquares: "+def.npcSizeInSquares);
         * list.add("standAnimation: "+def.standAnimation);
         * list.add("walkAnimation: "+def.walkAnimation);
         * list.add("walkingBackwardsAnimation: "+def.walkingBackwardsAnimation);
         * list.add("walkLeftAnimation: "+def.walkLeftAnimation);
         * list.add("walkRightAnimation: "+def.walkRightAnimation); for (int i = 0; i >
         * def.actions.length; i++) { if (def.actions[i] != null) {
         * list.add("actions["+i+"]: "+def.actions[i]); } else {
         * list.add("actions["+i+"]: null"); } } for (int i = 0; i >
         * def.childrenIDs.length; i++) {
         * list.add("childrenIds["+i+"]: "+def.childrenIDs[i]); } if (def.description !=
         * null) { list.add("description: "+def.description.toString()); }
         * list.add("disableRightClick: "+def.disableRightClick);
         * list.add("drawYellowDotOnMap: "+def.drawYellowDotOnMap); for (int i = 0; i >
         * def.npcModels.length; i++) { list.add("npcModels["+i+"]: "+def.npcModels[i]);
         * } list.add("visibilityOrRendering: "+def.visibilityOrRendering);
         *
         * for (String string : list) { // System.out.println(string); } //
         * System.out.println("---Dump Complete---"); list.clear(); }
         */
        if (command[0].equalsIgnoreCase("skull")) {
            if (player.getSkullTimer() > 0) {
                player.setSkullTimer(0);
                player.setSkullIcon(0);
                player.getUpdateFlag().flag(Flag.APPEARANCE);
            } else {
                CombatFactory.skullPlayer(player);
            }
        }
        if (command[0].equalsIgnoreCase("fillinv") || command[0].equalsIgnoreCase("fill")) {
            if (command.length > 1 && command[1] != null && command[1].equalsIgnoreCase("y")) {

                /* Empty the inv first */
                player.getInventory().resetItems().refreshItems();

            }

            while (player.getInventory().getFreeSlots() > 0) { // why 22052? Fuck you. that's why.
                int it = Misc.inclusiveRandom(1, 22052);
                if (ItemDefinition.forId(it) == null || ItemDefinition.forId(it).getName() == null
                        || ItemDefinition.forId(it).getName().equalsIgnoreCase("null")) {
                    continue;
                } else {
                    player.getInventory().add(it, 1);
                }
            }
        }
        if (command[0].equalsIgnoreCase("inmini")) {

            player.sendMessage(player.isInMinigame() + "  ");
        }

        if (command[0].equalsIgnoreCase("anim")) {
            int id = Integer.parseInt(command[1]);
            player.performAnimation(new Animation(id));
            player.getPacketSender().sendMessage("Sending animation: " + id);
        }
        if (command[0].equalsIgnoreCase("gfx")) {
            int id = Integer.parseInt(command[1]);
            player.performGraphic(new Graphic(id));
            player.getPacketSender().sendMessage("Sending graphic: " + id);
        }
        if (command[0].equalsIgnoreCase("playnpc") || command[0].equalsIgnoreCase("pnpc")) {
            int npcID = Integer.parseInt(command[1]);
            player.setNpcTransformationId(npcID);
            player.getStrategy(npcID);
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        } else if (command[0].equalsIgnoreCase("playobject")) {
            player.getPacketSender().sendObjectAnimation(new GameObject(2283, player.getPosition().copy()),
                    new Animation(751));
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }

        if (command[0].equalsIgnoreCase("giveglobalrate")) {
            int amount = Integer.parseInt(command[1]);

            player.getPointsHandler().incrementGlobalRate(amount);

            player.sendMessage("Your global rate is now: " + player.getPointsHandler().getGlobalRate());
        }

        if (command[0].equalsIgnoreCase("interface") || command[0].equalsIgnoreCase("int")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendInterface(id);
        }
        if (command[0].equalsIgnoreCase("walkableinterface")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendWalkableInterface(id, true);
        }
        if (command[0].equalsIgnoreCase("object")) {
            int id = Integer.parseInt(command[1]);
            player.getPacketSender().sendObject(new GameObject(id, player.getPosition(), 10, 3));
            player.getPacketSender().sendMessage("Sending object: " + id);
        }
        if (command[0].equalsIgnoreCase("config")) {
            int id = Integer.parseInt(command[1]);
            int state = Integer.parseInt(command[2]);
            player.getPacketSender().sendConfig(id, state).sendMessage("Sent config.");
        }
        if (command[0].equalsIgnoreCase("gamemode")) {
            if (command[1].equalsIgnoreCase("1")) {
                player.getGameMode();
                GameMode.set(player, GameMode.NORMAL, false);
            } else if (command[1].equalsIgnoreCase("2")) {
                player.getGameMode();
                GameMode.set(player, GameMode.IRONMAN, false);
            } else if (command[1].equalsIgnoreCase("3")) {
                player.getGameMode();
                GameMode.set(player, GameMode.ULTIMATE_IRONMAN, false);
            } else if (command[1].equalsIgnoreCase("4")) {
                player.getGameMode();
                GameMode.set(player, GameMode.VETERAN_MODE, false);
            } else
                player.getPacketSender().sendMessage("<img=5> Correct usage ::gamemode (#), 1 = Norm, 2 = IM, 3 = UIM");
        }
        if (command[0].equalsIgnoreCase("fly")) {
            player.getPlayerViewingIndex();
        }

        if (command[0].equalsIgnoreCase("setpray")) {
            int setlv = Integer.parseInt(command[1]);
            player.getPacketSender().sendMessage("You've set your current prayer points to: @red@" + setlv + "@bla@.");
            player.getSkillManager().setCurrentLevel(Skill.PRAYER, setlv);
        }
        if (command[0].equalsIgnoreCase("sethp") || command[0].equalsIgnoreCase("sethealth")) {
            int setlv = Integer.parseInt(command[1]);
            player.getPacketSender().sendMessage("You've set your constitution to: @red@" + setlv + "@bla@.");
            player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, setlv);
        }
        if (command[0].equalsIgnoreCase("clani")) {
            ClanChatManager.updateList(player.getCurrentClanChat());
            player.getPacketSender().sendMessage("Int to enter: " + ClanChat.RANK_REQUIRED_TO_ENTER);
            player.getPacketSender().sendMessage("Int to talk: " + ClanChat.RANK_REQUIRED_TO_TALK);
            player.getPacketSender().sendMessage("Int to kick: " + ClanChat.RANK_REQUIRED_TO_KICK);
            player.getPacketSender().sendMessage("Int to guild: " + ClanChat.RANK_REQUIRED_TO_VISIT_GUILD)
                    .sendMessage("");
            player.getPacketSender()
                    .sendMessage(player.getClanChatName() + " is ur clan. " + player.getCurrentClanChat() + "");
        }
        if (command[0].equalsIgnoreCase("getintitem")) {
            if (player.getInteractingItem() == null) {
                player.getPacketSender().sendMessage("It's a null from here.");
                return;
            }
            player.getPacketSender().sendMessage("ID: " + player.getInteractingItem().getId() + ", amount: "
                    + player.getInteractingItem().getAmount());
        }
        if (command[0].equalsIgnoreCase("tits")) {
            // ClanChat.RANK_REQUIRED_TO_ENTER = 7;
            player.getPacketSender().sendMessage("tits are done");
            player.getPacketSender().sendMessage("tits are: " + ClanChat.RANK_REQUIRED_TO_ENTER);
        }
        if (command[0].equalsIgnoreCase("index")) {
            player.getPacketSender().sendMessage("Player index: " + player.getIndex());
            player.getPacketSender().sendMessage("Player index * 4: " + player.getIndex() * 4);
        }
        if (command[0].equalsIgnoreCase("claninstanceid")) {
            player.getPacketSender().sendMessage(player.getCurrentClanChat().getRegionInstance() + " test.");
        }

        if (command[0].equalsIgnoreCase("mypos")){
            player.getPacketSender().sendMessage(String.format("Player x: '%s', Player y: '%s', Player z: '%s'", player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ()));
        }

        if (command[0].equalsIgnoreCase("getpray")) {
            player.getPacketSender().sendMessage("Your current prayer points are: @red@"
                    + player.getSkillManager().getCurrentLevel(Skill.PRAYER) + "@bla@.");
        }
        if (command[0].equalsIgnoreCase("skillcapes")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getSkillCapeId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("skillcapest") || command[0].equalsIgnoreCase("skillcapet")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getSkillCapeTrimmedId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("pets")) {
            for (Skill skill : Skill.values()) {
                player.getInventory().add(skill.getPetId(), 1);
            }
        }
        if (command[0].equalsIgnoreCase("loc")) {
            int id = Integer.parseInt(command[1]);
            GameObject object = new GameObject(id, player.getPosition(), 10, 3);
            CustomObjects.spawnGlobalObject(object);

        }
        if (command[0].equalsIgnoreCase("dlobby")) {
            player.moveTo(Dungeoneering.Constants.INSTANCE.getLOBBY());
        }

        if (command[0].equalsIgnoreCase("createdungparty")) {
            DungeoneeringParty.create(player);
        }
        if (command[0].equalsIgnoreCase("enterdungeon")) {
            DungeoneeringParty party = player.getMinigameAttributes().getDungeoneeringAttributes().getParty();
            if (party != null) {
                if (Dungeoneering.Companion.ready(party)) {
                    Dungeoneering dung = new Dungeoneering(party);
                    dung.startDungeon();
                } else {
                    party.sendMessage("Your party is not ready.");
                }
            } else {
                player.sendMessage("Please join a party before entering a dungeon.");
            }
        }
        if (command[0].equalsIgnoreCase("testinstance")) {
            TestInstance instance = new TestInstance();
            instance.initialise();
            instance.add(player);
        }
        if (command[0].equalsIgnoreCase("fixplacement")) {
            player.setNeedsPlacement(true);
            player.checkMap();
        }
        if (command[0].equalsIgnoreCase("clues")) {
            for (Item i : player.getInventory().getItems()) {
                if (i != null) {
                    player.getInventory().delete(i);
                }
            }
            player.getInventory().add(952, 1);
            for (int i = 0; i < OLD_ClueScrolls.values().length; i++) {
                player.getInventory().add(OLD_ClueScrolls.values()[i].getClueId(), 1);
            }
        }


        if (command[0].equalsIgnoreCase("giveadmin")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to administrator.");
            player.getPacketSender().sendMessage("Promoted to administrator.");
            player2.setRights(PlayerRights.ADMINISTRATOR);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("giveowner")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to owner.");
            player.getPacketSender().sendMessage("Promoted to owner.");
            player2.setRights(PlayerRights.DEVELOPER);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givediamond")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to Diamond Donator.");
            player.getPacketSender().sendMessage("Promoted to Diamond Donator.");
            player2.setDonatorRank(DonatorRank.DIAMOND);
            player2.getPacketSender().sendDonatorRank();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("giveyt")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to youtuber.");
            player.getPacketSender().sendMessage("Promoted to youtuber.");
            player2.setDonatorRank(DonatorRank.YOUTUBER);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("demote")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("demoted to player.");
            player.getPacketSender().sendMessage("demoted to player.");
            player2.setRights(PlayerRights.PLAYER);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givedon")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("You have been given donator.");
            player.getPacketSender().sendMessage("donator.");
            player2.setDonatorRank(DonatorRank.SAPPHIRE);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givedon2")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("You have been given super.");
            player.getPacketSender().sendMessage("super.");
            player2.setDonatorRank(DonatorRank.EMERALD);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givedon3")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("You have been given exreme.");
            player.getPacketSender().sendMessage("extreme.");
            player2.setDonatorRank(DonatorRank.RUBY);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givedon4")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("You have been given sponsor.");
            player.getPacketSender().sendMessage("sponsor.");
            player2.setDonatorRank(DonatorRank.DIAMOND);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("mockcasket")) {
            player.getPacketSender().sendMessage("Started mock...");
            OLD_ClueScrolls.mockCasket(Integer.parseInt(command[1]));
            player.getPacketSender().sendMessage("Done mock.");
        }
        if (command[0].equalsIgnoreCase("easter")) {
            if (Misc.easter(Misc.getYear())) {
                player.getPacketSender().sendMessage("easter is true");
            }
        }
        if (command[0].equalsIgnoreCase("bgloves")) {
            player.getPacketSender().sendMessage(player.getBrawlerChargers() + " charges");
        }
        if (command[0].equalsIgnoreCase("checkequip") || command[0].equalsIgnoreCase("checkgear")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player.getEquipment().setItems(player2.getEquipment().getCopiedItems()).refreshItems();
            WeaponInterfaces.assign(player, player.getEquipment().get(Equipment.WEAPON_SLOT));
            WeaponAnimations.update(player);
            BonusManager.update(player);
            player.getUpdateFlag().flag(Flag.APPEARANCE);
        }
        if (command[0].equalsIgnoreCase("togglediscord")) {
            DiscordMessager.active = !DiscordMessager.active;
            player.getPacketSender().sendMessage("Discord messages is now set to: " + DiscordMessager.active);
        }
        if (command[0].equalsIgnoreCase("crewards")) {
            CrystalChest.sendRewardInterface(player);
        }
        if (command[0].equalsIgnoreCase("bolts")) {
            for (int i = 0; i < BoltData.values().length; i++) {
                player.getInventory().add(BoltData.values()[i].getBolt(), 1000).add(BoltData.values()[i].getTip(),
                        1000);
            }
        }
    }
}
