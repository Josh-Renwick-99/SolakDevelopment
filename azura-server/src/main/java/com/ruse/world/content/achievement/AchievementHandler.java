package com.ruse.world.content.achievement;

import com.ruse.util.Misc;
import com.ruse.world.content.achievement.Achievements.Achievement;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AchievementHandler {
    private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
    private static final int MAXIMUM_TIERS = 4;
    private static final long numEasy = Achievement.ACHIEVEMENTS.stream().filter(n -> n.getDifficulty().ordinal() == 0).count();
    private static final long numMed = Achievement.ACHIEVEMENTS.stream().filter(n -> n.getDifficulty().ordinal() == 1).count();
    private static final long numHard = Achievement.ACHIEVEMENTS.stream().filter(n -> n.getDifficulty().ordinal() == 2).count();
    private final Player player;
    /**
     * WARNING: ADD TO THE END OF THE LIST.
     */
    private final int[][] boughtItems = {{7409, -1}, {13659, -1}, {20120, -1}, {88, -1}, {13281, -1}, {2379, -1}, {20235, -1}, {13845, -1}, {13846, -1}, {13847, -1},
            {13848, -1}, {13849, -1}, {13850, -1}, {13851, -1}, {13852, -1}, {13853, -1}, {13854, -1}, {13855, -1}, {13856, -1}, {13857, -1}, {20220, -1},
            {20221, -1}, {20222, -1},};
    public int currentInterface = 0;
    public boolean clickedAchievement = false;
    private int toView;
    private int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private int points;
    //	private static final long numDaily = Achievement.ACHIEVEMENTS.stream().filter(n -> n.getDifficulty().ordinal() == 3).count();
    private long dailyAchievementsDate;
    private int previousAchievementIndex = 0;
    private int selectedAchievementIndex = 0;
    @Getter
    private ArrayList<Achievement> achievementsShown = new ArrayList<>();


    public AchievementHandler(Player player) {
        this.player = player;
    }

    /**
     * Gets tomorrow's date
     */
    public static String getTimeLeft() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("EST"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);

        final long millis = cal.getTimeInMillis() - System.currentTimeMillis();

        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private int getIndex(int tier, int value) {
        if (tier == 0) {
            return value;
        } else if (tier == 1) {
            return (int) (value - numEasy);
        } else if (tier == 2) {
            return (int) (value - numMed - numEasy);
        } else {
            return (int) (value - numHard - numMed - numEasy);
        }
    }

    /**
     * Draws the achievement interface
     *
     * @param tier
     */
    public void drawInterface(int tier) {
        refreshInterface(tier);
    }

    public void refreshInterface(int tier) {
        String tab = "";
        switch (tier) {
            case 0://Easy
                tab = "Easy";
                selectedAchievementIndex = 0;
                break;
            case 1://Medium
                tab = "Med.";
                selectedAchievementIndex = (int) (numEasy);
                break;
            case 2://Hard
                tab = "Hard";
                selectedAchievementIndex = (int) (numEasy + numMed);
                break;
            case 3://Daily
                tab = "Daily";
                selectedAchievementIndex = (int) (numEasy + numMed + numHard);
                break;
        }
        previousAchievementIndex = selectedAchievementIndex;

        int count = 117101;
        int c = 0;
        int completed = 0;

        achievementsShown.clear();
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getDifficulty().ordinal() == tier) {
                int amount = getAmountRemaining(achievement.getDifficulty().ordinal(), achievement.getId());
                String text = achievement.name().toUpperCase().replaceAll("_", " ");

                boolean done = false;

                int percentage = (int) ((100 * (double) amount) / (double) achievement.getAmount());//percentage calculator
                if (amount >= achievement.getAmount()) {
                    amount = achievement.getAmount();
                    percentage = 100;
                    completed++;
                    done = true;
                }
                count++;
                player.getPacketSender().sendSpriteChange(count++, achievement.getSpriteID());

                player.getPacketSender().sendString(count++,(done ? "@str@" : "") +Misc.ucFirst(text));


                player.getPacketSender().sendProgressBar(count++, 0, percentage, 0);
                player.getPacketSender().sendString(count++, "@whi@" + amount + "/" + achievement.getAmount());//Progress

                for (int i = 0; i < 3; i++) {
                    if (achievement.getRewards().length > i) {
                        player.getPacketSender().sendItemOnInterface(count++, achievement.getRewards()[i].getId(), achievement.getRewards()[i].getAmount());
                    } else {
                        player.getPacketSender().sendItemOnInterface(count++, -1, 1);
                    }
                }
                achievementsShown.add(achievement);
                count += 2;
                c++;
            }
        }

        player.getPacketSender().sendString(117013, "Achievements completed: " + completed + "/" + c);

        player.getPacketSender().setScrollBar(117100, 39 * c);

        player.getPacketSender().sendInterface(117000);
    }


    public void sendInformation(int index) {

        if (getAchievementsShown() != null)
            player.sendMessage(getAchievementsShown().get(index).getDescription());

    }

    private void claimReward(int index) {
        if (getAchievementsShown() != null) {
            Achievements.claimReward(player, getAchievementsShown().get(index));
        }
    }


    public boolean hasCompletedAll() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getDifficulty().ordinal(), achievement.getId()))
                amount++;
        }
        return amount == Achievements.getMaximumAchievements();
    }

    public boolean completedTier(AchievementDifficulty tier) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS)
            if (achievement.getDifficulty() == tier)
                if (!isComplete(achievement.getDifficulty().ordinal(), achievement.getId()))
                    return false;
        return true;
    }

    public boolean isComplete(int tier, int index) {
        return completed[tier][index];
    }

    public void setComplete(int tier, int index, boolean state) {
        this.completed[tier][index] = state;
    }

    public boolean[][] getCompleted() {
        return completed;
    }

    public void setCompleted(boolean[][] completed) {
        this.completed = completed;
    }

    public int getAmountRemaining(int tier, int index) {
        return amountRemaining[tier][index];
    }

    public int[][] getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(int[][] amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public void setAmountRemaining(int tier, int index, int amountRemaining) {
        this.amountRemaining[tier][index] = amountRemaining;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isAchievementItem(int itemId) {
        for (int[] boughtItem : boughtItems)
            if (boughtItem[0] == itemId)
                return true;
        return false;
    }

    public boolean hasBoughtItem(int itemId) {
        for (int[] boughtItem : boughtItems)
            if (boughtItem[0] == itemId)
                if (boughtItem[1] != -1)
                    return true;
        return false;
    }

    public void setBoughtItem(int itemId) {
        for (int i = 0; i < boughtItems.length; i++)
            if (boughtItems[i][0] == itemId)
                boughtItems[i][1] = 1;
    }

    public int[][] getBoughtItems() {
        return this.boughtItems;
    }

    public void setBoughtItem(int index, int value) {
        if (index > this.boughtItems.length - 1)
            return;
        this.boughtItems[index][1] = value;
    }

    /**
     * Gets today's date
     *
     * @return
     */
    public int getTodayDate() {
        Calendar cal = new GregorianCalendar();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        return (month * 100 + day);
    }

    public long getDailyAchievementsDate() {
        return dailyAchievementsDate;
    }

    public void setDailyTaskDate(long dailyTaskDate) {
        this.dailyAchievementsDate = dailyTaskDate;
    }

    public boolean handleButtonClick(int buttonId) {
        /**Achievement Buttons**/
        if (buttonId >= 117109 && buttonId <= 117400) {
            if ((buttonId - 117109) % 10 == 0) {
                sendInformation((buttonId - 117109) / 10);
            } else if ((buttonId - 117110) % 10 == 0) {
                claimReward((buttonId - 117110) / 10);
            }
            return true;
        }

        switch (buttonId) {
            case 117005:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().currentInterface = 0;
                player.getAchievements().drawInterface(0);
                return true;
            case 117006:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().currentInterface = 1;
                player.getAchievements().drawInterface(1);
                return true;
            case 117007:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().currentInterface = 2;
                player.getAchievements().drawInterface(2);
                return true;
            case 117008:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().currentInterface = 3;
                player.getAchievements().drawInterface(3);
                return true;
        }
        return false;
    }

}
