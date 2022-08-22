package com.ruse.net.packet.impl.commands;

import com.ruse.GameSettings;
import com.ruse.model.*;
import com.ruse.model.input.impl.ChangePassword;
import com.ruse.model.input.impl.EnterReferral;
import com.ruse.model.input.impl.SetPinPacketListener;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.content.*;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.Maxhits;
import com.ruse.world.content.dailyTask.DailyTaskHandler;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;
import mysql.impl.FoxVote;

public class PlayerCommands {

    public static void handlePlayerCommands(final Player player, String[] command, String wholeCommand) {

        if (GameSettings.BETA_ENABLED) {
            if (wholeCommand.toLowerCase().startsWith("ownerup")) {
                player.getPacketSender().sendMessage("Promoted to DEVELOPER.");
                player.setRights(PlayerRights.DEVELOPER);
                player.getPacketSender().sendRights();
                PlayerPanel.refreshPanel(player);
            }

            if (wholeCommand.toLowerCase().startsWith("ownerdown")) {
                player.getPacketSender().sendMessage("Promoted to PLAYER.");
                player.setRights(PlayerRights.PLAYER);
                player.getPacketSender().sendRights();
                PlayerPanel.refreshPanel(player);
            }
        }



        if (wholeCommand.toLowerCase().startsWith("yell")) {
            if (PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
                player.getPacketSender().sendMessage("You are muted and cannot yell.");
                return;
            }
            if (player.getAmountDonated() < Donation.SAPPHIRE_DONATION_AMOUNT && !(player.getRights().isStaff() || player.getRights() == PlayerRights.YOUTUBER)) {
                player.getPacketSender().sendMessage("You need to be a Donator to yell.");
                return;
            }
            int delay = player.getRights().isStaff() ? 0 : player.getRights().getYellDelay();
            if (!player.getLastYell().elapsed((delay * 1000))) {
                player.getPacketSender().sendMessage("You must wait at least " + delay + " seconds between every yell-message you send.");
                return;
            }
            String yellMessage = Misc.capitalizeJustFirst(wholeCommand.substring(5));

            String[] invalid = {"<img="};
            for (String s : invalid) {
                if (yellMessage.contains(s)) {
                    player.getPacketSender().sendMessage("Your message contains invalid characters.");
                    return;
                }
            }

            int image = player.getRights().ordinal();

            if (player.getRights() == PlayerRights.ZENYTE_DONATOR){
                image = 1508;
            }

            World.sendYellMessage("" + player.getRights().getYellPrefix()
                    + "<img=" + image
                    + "><col=" + player.getRights().getYellPrefix() +
                    " [" + Misc.ucFirst(player.getRights().name().replaceAll("_", " ")) + "]<shad=0><col=" + player.getYellHex() + "> " + player.getUsername() +
                    ": " + yellMessage);

            // World.sendMessage(":yell:" + yell);
            player.getLastYell().reset();
            return;
        }


        if (command[0].equalsIgnoreCase("gamble")) {
            TeleportHandler.teleportPlayer(player, new Position(2463, 5032, 0),
                    player.getSpellbook().getTeleportType());
        }

        if (command[0].equalsIgnoreCase("perks")) {
            ServerPerks.getInstance().open(player);
        }
        if (command[0].equalsIgnoreCase("junk") || command[0].equalsIgnoreCase("junks") || command[0].equalsIgnoreCase("junkshop")) {
//NUCKY HERE
            player.sendMessage("<shad=1>@yel@Sell your items to the junk shop!");
        }

        if (command[0].equalsIgnoreCase("claimdonation") || command[0].equalsIgnoreCase("claimdonate")
                || command[0].equalsIgnoreCase("claim") || command[0].equalsIgnoreCase("donated")) {
            player.claimDonation(player, false);
        }

        if (command[0].equalsIgnoreCase("kills")) {
            player.getPacketSender().sendInterfaceRemoval();
            KillTrackerInterface.open(player);
        }
        if (command[0].equalsIgnoreCase("dropmessage")) {
            player.dropMessageToggle = !player.dropMessageToggle;
            player.sendMessage("Show drop messages currently set to: " + player.dropMessageToggle);
            return;
        }

        if (command[0].equalsIgnoreCase("train")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position[] locations = new Position[]{new Position(3472, 9484, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to the starter zone.");
        }

        if (command[0].equalsIgnoreCase("donationdeals") || command[0].equalsIgnoreCase("deals")) {
            player.sendMessage(
                    "<shad=1>@yel@<img=14>Please check out the donation deals in our ::Discord - #Donation-deals");
        }

        if (wholeCommand.equalsIgnoreCase("droprate") || wholeCommand.equalsIgnoreCase("mydr") || wholeCommand.equalsIgnoreCase("dr")) {
            player.getPacketSender()
                    .sendMessage("Droprate  bonus is + @red@" + CustomDropUtils.drBonus(player, player.getSlayer().getSlayerTask().getNpcId())
                            + "@bla@%. / X2 Drop bonus is + <col=248f8f>" + CustomDropUtils.getDoubleDropChance(player, player.getSlayer().getSlayerTask().getNpcId())
                            + "@bla@%.");
        }

        if (wholeCommand.equalsIgnoreCase("ddr")) {
            player.getPacketSender().sendMessage(
                    "Your Double  bonus is + @red@" + CustomDropUtils.getDoubleDropChance(player, player.getSlayer().getSlayerTask().getNpcId()) + "@bla@%.");
        }
        if (command[0].equalsIgnoreCase("time") || command[0].equalsIgnoreCase("date")
                || command[0].equalsIgnoreCase("clock")) {
            int month = 1 + Misc.getMonth();
            String mo = Integer.toString(month);
            String dd = Integer.toString(Misc.getDayOfMonth());
            String weekend = "";

            if (Misc.getDayOfMonth() < 10) {
                dd = "0" + dd;
            }

            if (month < 10) {
                mo = "0" + mo;
            }

            if (Misc.isWeekend()) {
                weekend = ". It's the weekend";
            }

            player.getPacketSender().sendMessage("@cya@<shad=0>[Time] <shad=-1>@bla@[" + mo + "/" + dd + "] (MM/DD) @ "
                    + Misc.getCurrentServerTime() + " (24:00) in New York City, USA" + weekend + ".");
        }

        if (command[0].equalsIgnoreCase("rewards") || command[0].equalsIgnoreCase("loot")
                || command[0].equalsIgnoreCase("loots")) {
            PossibleLootInterface.openInterface(player, PossibleLootInterface.LootData.values()[0]);
        }


        if (command[0].startsWith("reward") || command[0].startsWith("voted") || command[0].startsWith("claimvote")) {
            new Thread(new FoxVote(player)).start();
        }

        if (command[0].equalsIgnoreCase("achievements") || command[0].equalsIgnoreCase("dailytasks")
                || command[0].equalsIgnoreCase("tasks")) {
            player.getAchievements().refreshInterface(player.getAchievements().currentInterface);
            player.getPacketSender().sendConfig(6000, 3);
        }

        if (command[0].equalsIgnoreCase("resetdaily")) {
            new DailyTaskHandler(player).resetTasks();
        }

        if (command[0].equalsIgnoreCase("collection") || command[0].equalsIgnoreCase("collectionlog")) {
            player.getCollectionLog().open();
        }

        if (command[0].equalsIgnoreCase("drops") || command[0].equalsIgnoreCase("drop")) {
            player.getPacketSender().sendMessage("Opening drops interface...");
            DropsInterface.open(player);
        }
        if (wholeCommand.equalsIgnoreCase("commands")) {

            for (int i = 8145; i < 8196; i++)
                player.getPacketSender().sendString(i, "");

            player.getPacketSender().sendInterface(8134);

            player.getPacketSender().sendString(8136, "Close window");
            player.getPacketSender().sendString(8144, "Commands");
            player.getPacketSender().sendString(8145, "");
            int index = 8147;
            String color = "@dre@";
            String color1 = "@red@";

            player.getPacketSender().sendString(index++, color1 + "Main Commands:");
            player.getPacketSender().sendString(index++, color + "::home - Teleports you home");
            player.getPacketSender().sendString(index++, color + "::perks - Opens up the server perks interface");
            // player.getPacketSender().sendString(index++, color + "::gamble - Teleports you to the gambling area");
            player.getPacketSender().sendString(index++, color + "::train - Teleports you to the starter zone");
            // player.getPacketSender().sendString(index++, color + "::checkdaily - Checks your daily dask");
            player.getPacketSender().sendString(index++, color + "::shops - Teleports you to all shops");
            player.getPacketSender().sendString(index++, color + "");
            player.getPacketSender().sendString(index++, color1 + "Interface Commands:");
            // player.getPacketSender().sendString(index++, color + "::kills - opens up your personal kill tracker list");
            // player.getPacketSender().sendString(index++, color + "::pos - opens the player owned shops interface");
            player.getPacketSender().sendString(index++, color + "::tasks - opens the achievements interface");
            // player.getPacketSender().sendString(index++, color + "::rewards - opens the possible loot interface");
            // player.getPacketSender().sendString(index++, color + "::drops - opens the loot viewer interface for npcs");
            // player.getPacketSender().sendString(index++, color + "::collection - opens the collection log interface");
            // player.getPacketSender().sendString(index++, color + "::itemstats - opens up best items interface");
            player.getPacketSender().sendString(index++, color + "");
            player.getPacketSender().sendString(index++, color1 + "Other Commands:");
            player.getPacketSender().sendString(index++, color + "::dr/ddr - shows you your current droprate");
            player.getPacketSender().sendString(index++, color + "::maxhit - shows you your current droprate");
            player.getPacketSender().sendString(index++, color + "::changepass - allows you to change your password");


            player.getPacketSender().sendString(index++,
                    color + "::global - teleports to global bosses");
            player.getPacketSender().sendString(index++,
                    color + "::bank - opens up your bank ($50 total claimed required)");
            player.getPacketSender().sendString(index++,
                    color + "::players - tells you how many players are currently online");
            player.getPacketSender().sendString(index++, color + "::forums - opens up our forums for Solak");
            player.getPacketSender().sendString(index++, color + "::client - downloads our client launcher");
            player.getPacketSender().sendString(index++, color + "::rules - opens up our rules");
            player.getPacketSender().sendString(index++, color + "::discord - opens up our discord for Solak");
            player.getPacketSender().sendString(index++, color + "::vote - opens up our site for voting");
            player.getPacketSender().sendString(index++, color + "::voted - claims your votes");
            player.getPacketSender().sendString(index++, color + "::donate - opens up our donation site");
            player.getPacketSender().sendString(index++, color + "::donated - claims your donation");
            player.getPacketSender().sendString(index++, color + "::donationdeals - see if there are any promotions");
            player.getPacketSender().sendString(index++,
                    color + "::whatdrops (item name) - tells you what drops the item");
            player.getPacketSender().sendString(index++,
                    color + "::dropmessage - removes messages of drops going to your inv/bank");
            player.getPacketSender().sendString(index++, color + "::help - requests assistance from a staff member");
            player.getPacketSender().sendString(index++, color + "::yell - sends a global message");
            player.getPacketSender().sendString(index++, color + "");
        }

        if (wholeCommand.equalsIgnoreCase("donate") || wholeCommand.equalsIgnoreCase("store")) {
            player.getPacketSender().sendString(1, GameSettings.StoreUrl);
            player.getPacketSender().sendMessage("Attempting to open the store");
        }

        if (wholeCommand.equalsIgnoreCase("client") || wholeCommand.equalsIgnoreCase("launcher")) {
            player.getPacketSender().sendString(1, "https://solak.io/play/");
            player.getPacketSender().sendMessage("Attempting to download the launcher");
        }
        if (wholeCommand.equalsIgnoreCase("discord") || wholeCommand.equalsIgnoreCase("chat")) {
            player.getPacketSender().sendString(1, GameSettings.DiscordUrl);
            player.getPacketSender().sendMessage("Attempting to open our Discord Server");
        }

        if (wholeCommand.equalsIgnoreCase("forums") || wholeCommand.equalsIgnoreCase("forum")) {
            player.getPacketSender().sendString(1, GameSettings.ForumUrl);
            player.getPacketSender().sendMessage("Attempting to open the forums");
        }
        if (wholeCommand.equalsIgnoreCase("updates") || wholeCommand.equalsIgnoreCase("whatsnew")) {
            player.getPacketSender().sendString(1, GameSettings.UpdateUrl);
            player.getPacketSender().sendMessage("Attempting to open the the update list");
        }

        if (wholeCommand.equalsIgnoreCase("rule") || wholeCommand.equalsIgnoreCase("rules")) {
            player.getPacketSender().sendString(1, GameSettings.RuleUrl);
            player.getPacketSender().sendMessage("Attempting to open the Rules.");
        }
        if (wholeCommand.equalsIgnoreCase("changepass") || wholeCommand.equalsIgnoreCase("changepassword")) {
            player.setInputHandling(new ChangePassword());
            player.getPacketSender().sendEnterInputPrompt("Enter a new password:");
        }

        if (command[0].equalsIgnoreCase("vote")) {
            player.getPacketSender().sendString(1, GameSettings.VoteUrl);// "http://Ruseps.com/vote/?user="+player.getUsername());
            player.getPacketSender().sendMessage("When you vote do ::claimvote to redeem votes");
        }

        if (command[0].equalsIgnoreCase("toggleglobalmessages") || command[0].equalsIgnoreCase("globalmessages")) {
            if (player.toggledGlobalMessages() == false) {
                player.getPacketSender().sendMessage("You have opted out from filterable global messages.");
                player.setToggledGlobalMessages(true);
            } else {
                player.getPacketSender().sendMessage("You have opted in to filterable global messages.");
                player.setToggledGlobalMessages(false);
            }
        }

        if (command[0].equalsIgnoreCase("setloginpin")) {
            if (player.getHasPin() == false) {

                player.setInputHandling(new SetPinPacketListener());
                player.getPacketSender().sendEnterInputPrompt("Enter the pin that you want to set$pin");
            }
        }
        if (command[0].equalsIgnoreCase("enterpin")) {
            if (player.getHasPin() == false) {
                player.setInputHandling(new SetPinPacketListener());
                player.getPacketSender().sendEnterInputPrompt("Enter the pin that you want to set$pin");
            }
        }
        if (command[0].equalsIgnoreCase("logout")) {
            World.getPlayers().remove(player);
        }
        if (command[0].equalsIgnoreCase("ref") ||command[0].equalsIgnoreCase("refer") || command[0].equalsIgnoreCase("referral")) {
            if (!player.hasReferral) {
                player.getPacketSender().sendEnterInputPrompt("Please type your refer code to receive a reward!");
                player.setInputHandling(new EnterReferral());
            } else {
                player.getPacketSender().sendMessage("You have already claimed a referral reward on this account!");
            }
        }

        if (command[0].equalsIgnoreCase("summer")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position pos = new Position(3103, 2915 );
            TeleportHandler.teleportPlayer(player, pos, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to Summer Global Boss!");
        }

        if (command[0].equalsIgnoreCase("home")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position pos = new Position(2072 + Misc.getRandom(9), 4454 + Misc.getRandom(6));
            TeleportHandler.teleportPlayer(player, pos, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you home!");
        }

        if (command[0].equalsIgnoreCase("global")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            TeleportHandler.teleportPlayer(player, new Position(2139, 5019, 0), player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to global!");
        }

        if (command[0].equalsIgnoreCase("vboss") || command[0].equalsIgnoreCase("voteboss")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = new Position(2980, 2771, 0);
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);

        }

        if (command[0].equalsIgnoreCase("afk")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = new Position(3037, 4062, 0);
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("<shad=1>@gre@Welcome to the afk zone!");

        }

        if ((command[0].equalsIgnoreCase("shop") && !player.getRights().OwnerDeveloperOnly())
                || command[0].equalsIgnoreCase("shops")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position position = new Position(2071, 4462, 0);
            TeleportHandler.teleportPlayer(player, position, TeleportType.NORMAL);
            player.getPacketSender().sendMessage("Teleporting you to our shops!");
        }

        if (command[0].equalsIgnoreCase("help")) {
            if (player.getLastYell().elapsed(30000)) {
                if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                        || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                    World.sendStaffMessage("<col=FF0066><img=5> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help, but is @red@*IN LEVEL " + player.getWildernessLevel()
                            + " WILDERNESS*<col=6600FF>. Be careful.");
                }
                if (PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAddress())) {
                    World.sendStaffMessage("<col=FF0066><img=5> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help, but is @red@*MUTED*<col=6600FF>. Be careful.");
                } else {
                    World.sendStaffMessage("<col=FF0066><img=5> [TICKET SYSTEM]<col=6600FF> " + player.getUsername()
                            + " has requested help. Please help them!");
                }
                player.getLastYell().reset();
                player.getPacketSender()
                        .sendMessage("<col=663300>Your help request has been received. Please be patient.");
            } else {
                player.getPacketSender().sendMessage("<col=663300>You need to wait 30 seconds before using this again.")
                        .sendMessage(
                                "<col=663300>If it's an emergency, please private message a staff member directly instead.");
            }
        }

        if (command[0].equalsIgnoreCase("empty")) {
            //   DialogueManager.editOptions(523, 1, "Click here to confirm empty");
            player.setDialogueActionId(523);
            DialogueManager.start(player, 523);
            return;
        }
        if (command[0].equalsIgnoreCase("players")) {
            int players = World.getPlayers().size() + GameSettings.players;
            player.getPacketSender().sendMessage(
                    "<shad=1>@or1@There are currently @whi@[ @gre@" + (players) + "@whi@ ] @or1@players online!");
        }

        if (command[0].equalsIgnoreCase("[cn]")) {
            if (player.getInterfaceId() == 40172) {
                ClanChatManager.setName(player, wholeCommand.substring(wholeCommand.indexOf(" ") + 1));
            }
        }
    }


}
