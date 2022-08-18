package com.ruse.world.content.achievement;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

public class Achievements {

    public static void doProgress(Player player, Achievement achievement) {
        doProgress(player, achievement, 1);
    }

    public static void doProgress(Player player, Achievement achievement, int amount) {
        if (achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
            int currentAmount = player.getAchievements().getAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId());
            int tier = achievement.getDifficulty().ordinal();
            if (currentAmount < achievement.getAmount() && !player.getAchievements().isComplete(achievement.getDifficulty().ordinal(), achievement.getId())) {
                player.getAchievements().setAmountRemaining(tier, achievement.getId(), currentAmount + amount);
                if ((currentAmount + amount) >= achievement.getAmount()) {
                    String name = achievement.name().replaceAll("_", " ");
                    player.getPacketSender().sendMessage(
                            "[ACHIEVEMENT] @blu@" + Misc.capitalizeJustFirst(achievement.name().replaceAll("_", " ") + " @bla@completed, claim your reward!"));

                    if (achievement.getDifficulty().ordinal() == 2) {
                        World.sendNewsMessage("<col=ff0000>" + Misc.capitalizeJustFirst(player.getUsername())
                                + " <col=a72800>completed the achievement <col=ff0000>" + Misc.capitalizeJustFirst(achievement.name().replaceAll("_", " ") + "!"));
                    }
                }
            }
        }
    }

    public static void claimReward(Player player, Achievement achievement) {
        if (achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
            if (!player.getAchievements().isComplete(achievement.getDifficulty().ordinal(), achievement.getId())) {
                int currentAmount = player.getAchievements().getAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId());
                int tier = achievement.getDifficulty().ordinal();
                if (currentAmount >= achievement.getAmount()) {
                    if (achievement.getRewards() != null) {
                        player.getPacketSender().sendMessage("Your achievement reward(s) has been added to your account.");
                        player.getAchievements().setComplete(tier, achievement.getId(), true);
                        for (Item item : achievement.getRewards())
                            player.getInventory().add(item.getId(), item.getAmount());

                        for (int x = 0; x < achievement.getPoints().length; x++) {
                            if (achievement.getPoints()[x][0].equalsIgnoreCase("achievement")) {
                                player.getPointsHandler().setAchievementPoints(Integer.parseInt(achievement.getPoints()[x][1]), true);
                            } else if (achievement.getPoints()[x][0].equalsIgnoreCase("skill")) {
                                player.getPointsHandler().setSkillingPoints(Integer.parseInt(achievement.getPoints()[x][1]), true);
                            } else if (achievement.getPoints()[x][0].equalsIgnoreCase("voting")) {
                                player.getPointsHandler().setVotingPoints(Integer.parseInt(achievement.getPoints()[x][1]), true);
                            } else if (achievement.getPoints()[x][0].equalsIgnoreCase("slayer")) {
                                player.getPointsHandler().setSlayerPoints(Integer.parseInt(achievement.getPoints()[x][1]), true);
                            }
                        }
                    }
                } else {
                    player.getPacketSender().sendMessage("You have yet to complete this achievement!");
                }
            } else {
                player.getPacketSender().sendMessage("You have already claimed this reward.");
            }
        }
    }

    public static void reset(Player player, Achievement achievement) {
        if (achievement.getRequirement() == null || achievement.getRequirement().isAble(player)) {
            if (!player.getAchievements().isComplete(achievement.getDifficulty().ordinal(), achievement.getId())) {
                player.getAchievements().setAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId(), 0);
            }
        }
    }

    public static void resetDailys(Player player) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getDifficulty().equals(AchievementDifficulty.DAILY)) {
                player.getAchievements().setAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId(), 0);
                player.getAchievements().setComplete(3, achievement.getId(), false);
            }
        }
        player.getAchievements().setDailyTaskDate(player.getAchievements().getTodayDate());
    }

    public static void resetEasys(Player player) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getDifficulty().equals(AchievementDifficulty.EASY)) {
                player.getAchievements().setAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId(), 0);
                player.getAchievements().setComplete(0, achievement.getId(), false);
            }
        }
    }

    public static void resetMediums(Player player) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getDifficulty().equals(AchievementDifficulty.MEDIUM)) {
                player.getAchievements().setAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId(), 0);
                player.getAchievements().setComplete(1, achievement.getId(), false);
            }
        }
    }

    public static void resetHards(Player player) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getDifficulty().equals(AchievementDifficulty.HARD)) {
                player.getAchievements().setAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId(), 0);
                player.getAchievements().setComplete(2, achievement.getId(), false);
            }
        }
    }

    public static int getMaximumAchievements() {
        return Achievement.ACHIEVEMENTS.size();
    }

    public enum Achievement {

        // EASY
        VOTE_10_TIMES(1, 625, AchievementDifficulty.EASY, "Vote for Solak 10 times by using the ::vote command!", 10, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        FISH_50_LOBSTERS(2, 633, AchievementDifficulty.EASY, "Fish 50 Lobsters", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        BURN_50_OAK_LOGS(3, 632, AchievementDifficulty.EASY, "Burn 50 Oak Logs", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        CHOP_50_OAK_LOGS(4, 648, AchievementDifficulty.EASY, "Chop 50 Oak Logs", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        MINE_15_COPPER(5, 639, AchievementDifficulty.EASY, "Mine 15 Copper Ore", 15, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        CRAFT_20_LEATHER_BODIES(6, 628, AchievementDifficulty.EASY, "Craft 20 Leather Bodies", 20, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        FLETCH_20_OAK_LONGBOWS(7, 634, AchievementDifficulty.EASY, "Fletch 20 Oak longbows", 20, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        SMITH_30_IRON_PLATEBODIES(8, 644, AchievementDifficulty.EASY, "Smith 30 Iron platebodies", 30, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        COMPLETE_20_SLAYER_TASKS(9, 643, AchievementDifficulty.EASY, "Complet 20 Slayer tasks", 20, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        CATCH_30_IMPLINGS(10, 637, AchievementDifficulty.EASY, "Catch 30 Implings", 30, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        CLEAN_20_GUAMS(11, 635, AchievementDifficulty.EASY, "Clean 20 Guams", 20, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        STEAL_100_LAMP_STALLS(12, 647, AchievementDifficulty.EASY, "Steal from 100 lamp stalls", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 2_500_000)),
        COMPLETE_30_LAPS(13, 624, AchievementDifficulty.EASY, "Complete 30 laps of any obstacle course", 30, new String[][]{}, new Item(ItemDefinition.COIN_ID, 3_000_000)),
        COOK_50_FOOD(14, 627, AchievementDifficulty.EASY, "Cook 50 food", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 3_000_000)),
        BURY_100_BONES(15, 640, AchievementDifficulty.EASY, "Bury 100 bones", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        KILL_5000_NPCS(16, 625, AchievementDifficulty.EASY, "Kill 5,000 Npcs", 5000, new String[][]{}, new Item(ItemDefinition.COIN_ID, 50_000_000)),
        KILL_45_GLOBAL_BOSSES(17, 625, AchievementDifficulty.EASY, "Kill 45 Global bosses", 45, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        REACH_1500_TOTAL_LEVEL(18, 529, AchievementDifficulty.EASY, "Reach 1500 total level", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 50_000_000)),
        COLLECT_T2_GLOVES(19, 738, AchievementDifficulty.EASY, "Collect t2 gloves from Vault of war", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        COMPLETE_5_KEEPERS_OF_LIGHT(20, 738, AchievementDifficulty.EASY, "Complete 5 Keepers of the Light runs", 5, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        OPEN_5_CHESTS_IN_VOID_OF_DARKNESS(21, 738, AchievementDifficulty.EASY, "Open 5 chests in Void of Darkness", 5, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        OPEN_5_CHESTS_IN_HALLS_OF_VALOR(22, 738, AchievementDifficulty.EASY, "Open 5 chests in Halls of Valor", 5, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        OPEN_5_CHESTS_IN_TREASURE_HUNTER(23, 738, AchievementDifficulty.EASY, "Open 5 chests in Treasure hunter", 5, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        //COMPLETE_15_RAIDS(32, 625, AchievementDifficulty.EASY, "Complete 15 raids", 15, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100000)),


        // MEDIUM
        VOTE_50_TIMES(1, 625, AchievementDifficulty.MEDIUM, "Vote for Solak 50 times by using the ::vote command!", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 50_000_000)),
        FISH_100_SHARKS(2, 633, AchievementDifficulty.MEDIUM, "Fish 100 Sharks", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        BURN_100_YEW_LOGS(3, 632, AchievementDifficulty.MEDIUM, "Burn 100 Yew Logs", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        CHOP_100_YEW_LOGS(4, 648, AchievementDifficulty.MEDIUM, "Chop 100 Yew Logs", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        MINE_100_MITHRIL(5, 639, AchievementDifficulty.MEDIUM, "Mine 100 Mithril Ore", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        CUT_100_DIAMOND_GEMS(6, 628, AchievementDifficulty.MEDIUM, "Cut 100 Diamond gems", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        FLETCH_150_YEW_LONGBOWS(7, 634, AchievementDifficulty.MEDIUM, "Fletch 150 Yew longbows", 150, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        SMITH_50_ADAMANT_PLATEBODIES(8, 644, AchievementDifficulty.MEDIUM, "Smith 50 Adamant platebodies", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        COMPLETE_50_SLAYER_TASKS(9, 643, AchievementDifficulty.MEDIUM, "Complet 50 Slayer tasks", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        CATCH_100_IMPLINGS(10, 637, AchievementDifficulty.MEDIUM, "Catch 100 Implings", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        CLEAN_100_AVANTOES(11, 635, AchievementDifficulty.MEDIUM, "Clean 100 Avantoes", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 5_000_000)),
        STEAL_250_ARMOUR_STALLS(12, 647, AchievementDifficulty.MEDIUM, "Steal from 250 armour stalls", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        COMPLETE_75_LAPS(13, 624, AchievementDifficulty.MEDIUM, "Complete 75 laps of any obstacle course", 75, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        COOK_100_SHARKS(14, 627, AchievementDifficulty.MEDIUM, "Cook 100 sharks", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        BURY_250_BONES(15, 640, AchievementDifficulty.MEDIUM, "Bury 250 bones", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 25_000_000)),
        KILL_10000_NPCS(16, 625, AchievementDifficulty.MEDIUM, "Kill 10,000 Npcs", 10000, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        REACH_2000_TOTAL_LEVEL(17, 529, AchievementDifficulty.MEDIUM, "Reach 2000 total level", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        COLLECT_T4_GLOVES(18, 738, AchievementDifficulty.MEDIUM, "Collect t4 gloves from Vault of war", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        COMPLETE_15_KEEPERS_OF_LIGHT(19, 738, AchievementDifficulty.MEDIUM, "Complete 15 Keepers of the Light runs", 15, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        OPEN_25_CHESTS_IN_VOID_OF_DARKNESS(20, 738, AchievementDifficulty.MEDIUM, "Open 25 chests in Void of Darkness", 25, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        OPEN_25_CHESTS_IN_HALLS_OF_VALOR(21, 738, AchievementDifficulty.MEDIUM, "Open 25 chests in Halls of Valor", 25, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        OPEN_25_CHESTS_IN_TREASURE_HUNTER(22, 738, AchievementDifficulty.MEDIUM, "Open 25 chests in Treasure hunter", 25, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        //COMPLETE_50_RAIDS(32, 625, AchievementDifficulty.MEDIUM, "Complete 50 raids", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100000)),

        // HARD
        VOTE_100_TIMES(1, 625, AchievementDifficulty.HARD, "Vote 100 Times", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        FISH_250_ROCKTAILS(2, 633, AchievementDifficulty.HARD, "Fish 250 Rocktails", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        BURN_250_MAGIC_LOGS(3, 632, AchievementDifficulty.HARD, "Burn 250 Magic Logs", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        CHOP_300_MAGIC_LOGS(4, 648, AchievementDifficulty.HARD, "Chop 300 Magic Logs", 300, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        MINE_250_RUNE(5, 639, AchievementDifficulty.HARD, "Mine 250 Runite ore", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        CUT_150_DRAGONSTONE_GEMS(6, 628, AchievementDifficulty.HARD, "Cut 150 Dragonstone gems", 150, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        FLETCH_250_MAGIC_LONGBOWS(7, 634, AchievementDifficulty.HARD, "Fletch 250 Magic longbows", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        SMITH_200_RUNE_PLATEBODIES(8, 644, AchievementDifficulty.HARD, "Smith 200 Rune platebodies", 200, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        COMPLETE_150_SLAYER_TASKS(9, 643, AchievementDifficulty.HARD, "Complet 150 Slayer tasks", 150, new String[][]{}, new Item(ItemDefinition.COIN_ID, 200_000_000)),
        CATCH_250_IMPLINGS(10, 637, AchievementDifficulty.HARD, "Catch 250 Implings", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        HARVEST_250_TORSTOLS(11, 631, AchievementDifficulty.HARD, "Harvest 250 Torstols", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        CLEAN_250_TORSTOLS(12, 635, AchievementDifficulty.HARD, "Clean 250 Torstols", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        STEAL_750_ARMOUR_STALLS(13, 647, AchievementDifficulty.HARD, "Steal from 750 armour stalls", 750, new String[][]{}, new Item(ItemDefinition.COIN_ID, 10_000_000)),
        COMPLETE_150_LAPS(14, 624, AchievementDifficulty.HARD, "Complete 150 laps of any obstacle course", 150, new String[][]{}, new Item(ItemDefinition.COIN_ID, 35_000_000)),
        COOK_250_ROCKTAILS(15, 627, AchievementDifficulty.HARD, "Cook 250 rocktails", 250, new String[][]{}, new Item(ItemDefinition.COIN_ID, 20_000_000)),
        BURY_1000_BONES(16, 640, AchievementDifficulty.HARD, "Bury 1000 bones", 1000, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        KILL_25000_NPCS(17, 625, AchievementDifficulty.HARD, "Kill 25,000 Npcs", 25000, new String[][]{}, new Item(ItemDefinition.COIN_ID, 250_000_000)),
        MAX_OUT_ALL_SKILLS(18, 529, AchievementDifficulty.HARD, "Max out all skills", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 250_000_000)),
        COLLECT_MERCENARY_GLOVES(19, 738, AchievementDifficulty.HARD, "Collect Mercenary gloves from Vault of war", 1, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        COMPLETE_50_KEEPERS_OF_LIGHT(20, 738, AchievementDifficulty.HARD, "Complete 50 Keepers of the Light runs", 50, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        OPEN_200_CHESTS_IN_VOID_OF_DARKNESS(21, 738, AchievementDifficulty.HARD, "Open 200 chests in Void of Darkness", 200, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        OPEN_200_CHESTS_IN_HALLS_OF_VALOR(22, 738, AchievementDifficulty.HARD, "Open 200 chests in Halls of Valor", 200, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        OPEN_200_CHESTS_IN_TREASURE_HUNTER(23, 738, AchievementDifficulty.HARD, "Open 200 chests in Treasure hunter", 200, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100_000_000)),
        //COMPLETE_100_RAIDS(32, 625, AchievementDifficulty.HARD, "Complete 100 raids", 100, new String[][]{}, new Item(ItemDefinition.COIN_ID, 100000)),
        ;

        public static final Set<Achievement> ACHIEVEMENTS = EnumSet.allOf(Achievement.class);
        final String[][] points;
        private final AchievementDifficulty difficulty;
        private final AchievementRequirement requirement;
        private final String description;
        private final int amount;
        private final int npcId;
        private final int identification;
        @Getter
        private final int spriteID;
        private final Item[] rewards;

        Achievement(int identification, int spriteID, AchievementDifficulty difficulty, int npcId, String description, int amount, String[][] points,
                    Item... rewards) {
            this.identification = identification;
            this.spriteID = spriteID;
            this.difficulty = difficulty;
            this.requirement = null;
            this.npcId = npcId;
            this.description = description;
            this.amount = amount;
            this.points = points;
            this.rewards = rewards;

            for (Item b : rewards)
                if (b.getAmount() == 0)
                    b.setAmount(1);

        }

        Achievement(int identification, int spriteID, AchievementDifficulty difficulty, String description, int amount, String[][] points,
                    Item... rewards) {
            this.identification = identification;
            this.spriteID = spriteID;
            this.difficulty = difficulty;
            this.requirement = null;
            this.npcId = -1;
            this.description = description;
            this.amount = amount;
            this.points = points;
            this.rewards = rewards;

            for (Item b : rewards)
                if (b.getAmount() == 0)
                    b.setAmount(1);

        }

        public static Achievement getAchievement(AchievementDifficulty tier, int ordinal) {
            for (Achievement achievement : ACHIEVEMENTS)
                if (achievement.getDifficulty() == tier && achievement.ordinal() == ordinal)
                    return achievement;
            return null;
        }

        public static boolean hasRequirement(Player player, AchievementDifficulty tier, int ordinal) {
            for (Achievement achievement : ACHIEVEMENTS) {
                if (achievement.getDifficulty() == tier && achievement.ordinal() == ordinal) {
                    if (achievement.getRequirement() == null)
                        return true;
                    if (achievement.getRequirement().isAble(player))
                        return true;
                }
            }
            return false;
        }

        public int getNpcId() {
            return npcId;
        }

        public int getId() {
            return identification;
        }

        public AchievementDifficulty getDifficulty() {
            return difficulty;
        }

        AchievementRequirement getRequirement() {
            return requirement;
        }

        public String getDescription() {
            return description;
        }

        public int getAmount() {
            return amount;
        }

        public String[][] getPoints() {
            return points;
        }

        public Item[] getRewards() {
            return rewards;
        }
    }
}
