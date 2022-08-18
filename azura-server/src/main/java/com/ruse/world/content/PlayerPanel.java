package com.ruse.world.content;

import com.ruse.GameSettings;
import com.ruse.motivote3.doMotivote;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.summer_event.SummerEventHandler;
import com.ruse.world.entity.impl.player.Player;

public class PlayerPanel {

    public static void refreshCurrentTab(Player player) {
        refreshPanel(player);
    }

    public static void refreshPanel(Player player) {
        int interfaceID = 111201;
        int players = (int) World.getPlayers().size() + GameSettings.players;
        String[] Messages = new String[]{
                //
                "Main",
                "Players Online: @whi@" + ((players)),
                "Server Time: @whi@" + Misc.getCurrentServerTime(),
                //
                "Events",
                "Current Event: @whi@" +
                        ( ServerPerks.getInstance().getActivePerk() != null ?
                                ServerPerks.getInstance().getActivePerk().getName() :
                                "N/A"),
                (WellOfGoodwill.isActive() ? "Well of Goodwill: @whi@On" : "Well of Goodwill: @whi@Off"),
                "Bonus Xp: @whi@" + (Misc.format(player.getMinutesBonusExp()) == null ? "-1" : Misc.format(player.getMinutesBonusExp())) + " minutes left",
                (VoteBossDrop.currentSpawn == null
                        ? "Vote Boss: @whi@" + doMotivote.getVoteCount() + "/50 please vote!"
                        : "Vote Boss: @whi@::Vboss"),
                "Global boss: @whi@" + WorldBosses.timeLeft(),
                "Summer boss: @whi@" + SummerEventHandler.INSTANCE.getTimeUntilNextBoss(),
                /* (WorldBosses.currentBoss == null
                         ? "Global bosses: @whi@" + WorldBosses.timeLeft()
                         : "Global bosses: @whi@::global"),*/
                //"Next Prime Respawn:",
                //"@whi@" + (!SkeletalHorror.wyrmAlive ? SkeletalHorror.getTimeLeft() : "Currently Alive @::prime"),
                //
                "Useful links",
                "@whi@Open @yel@Homepage",
                "@whi@Open @yel@Forums",
                "@whi@Open @yel@Vote",
                "@whi@Open @yel@Store",
                "@whi@Open @yel@Discord",
        };

        for (int i = 0; i < Messages.length; i++) {
            player.getPacketSender().sendString(interfaceID++, Messages[i]);
        }


        interfaceID = 111401;

        Messages = new String[]{"Main",
                "Time Played: @whi@"
                        + Misc.getHoursPlayed((player.getTotalPlayTime() + player.getRecordedLogin().elapsed())),
                "Username: @whi@" + player.getUsername(),
                "Total Donated: @whi@$" + player.getAmountDonated(),
                "Mode: @whi@"
                        + Misc.capitalizeString(player.getGameMode().toString().toLowerCase().replace("_", " ")),
                "Rank: @whi@" + Misc.formatText(player.getRights().toString().toLowerCase()),

                "Droprate bonus: @whi@" + CustomDropUtils.drBonus(player, player.getSlayer().getSlayerTask().getNpcId()) + "%",
                "Double drop bonus: @whi@" + CustomDropUtils.getDoubleDropChance(player, player.getSlayer().getSlayerTask().getNpcId()) + "%",
                //"Triple drop bonus: @whi@" + CustomDropUtils.getTripleDropChance(player) + "%",
                //"Exp Lock: " + (player.experienceLocked() ? "Locked" : "@gre@Unlocked") + "",
                //"Voting x2 DR Boost: @whi@" + player.getMinutesVotingDR() + "min",
                //"Voting x2 DMG Boost: @whi@" + player.getMinutesVotingDMG() + "min",

                //
                "Points & Statistics",
                "NPC kill Count: @whi@ " + player.getPointsHandler().getNPCKILLCount(),
                //"Boss Points: @whi@ " + player.getPointsHandler().getBossPoints(),
                "Donator Points: @whi@" + player.getPointsHandler().getDonatorPoints(),
                "Voting Points: @whi@ " + player.getPointsHandler().getVotingPoints(),
                //"Slayer Points: @whi@" + player.getPointsHandler().getSlayerPoints(),
                //"Loyalty Points: @whi@" + (int) player.getPointsHandler().getLoyaltyPoints(),
                //  "Prestige Points: @whi@" + player.getPointsHandler().getPrestigePoints(),
                //  "Total Prestige: @whi@" + player.getPointsHandler().getTotalPrestiges(),
                //  "Event Points: @whi@ " + player.getPointsHandler().getEventPoints(),
                //   "Commendations: @whi@ " + player.getPointsHandler().getCommendations(),
                //  "Dung. Tokens: @whi@ " + player.getPointsHandler().getDungeoneeringTokens(),
                //  "Penguin Multiplier: @whi@ " + player.getPointsHandler().getPengRate() + "%",
                // "Barrows Points: @whi@" + player.getPointsHandler().getBarrowsPoints(),
                // "Pk Points: @whi@" + player.getPointsHandler().getPkPoints(),

                "Slayer Information",
                "Master: @whi@" + Misc
                        .formatText(player.getSlayer().getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")),
                (player.getSlayer().getSlayerTask() == SlayerTasks.NO_TASK
                        ? "Task: " + player.getSlayer().getSlayerTask().getName()
                        : "Task: @whi@" + player.getSlayer().getSlayerTask().getName()
                        + "s"),
                "Amount: @whi@" + player.getSlayer().getAmountToSlay(),
                "Streak: @whi@" + player.getSlayer().getTaskStreak(),
                "Slayer Multiplier: @whi@ " + player.getPointsHandler().getSlayerRate() + "%",
                // (player.getSlayer().getDuoPartner() != null
                //        ? "Duo Partner: @whi@" + player.getSlayer().getDuoPartner()
                //      : "Duo Partner: @whi@N/A"),
        };

        for (int i = 0; i < Messages.length; i++) {
            player.getPacketSender().sendString(interfaceID++, Messages[i]);
        }

        interfaceID = 111601;
        player.getPacketSender().sendString(interfaceID++, "View @whi@Achievements");
        player.getPacketSender().sendString(interfaceID++, "View Drop Tables");
        //player.getPacketSender().sendString(interfaceID++, "View Item List");
        player.getPacketSender().sendString(interfaceID++, "View Collection Log");
        player.getPacketSender().sendString(interfaceID++, "View Possible Loot");
        player.getPacketSender().sendString(interfaceID++, "Change Password");
        player.getPacketSender().sendString(interfaceID++, "Edit Pin");
        //player.getPacketSender().sendString(interfaceID++, "@whi@View @yel@Kill Tracker");


    }

}