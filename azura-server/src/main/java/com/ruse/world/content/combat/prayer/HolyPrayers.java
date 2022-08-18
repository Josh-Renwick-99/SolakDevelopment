/*package com.ruse.world.content.combat.prayer;

import com.ruse.model.GameMode;
import com.ruse.model.PlayerRights;
import com.ruse.model.Skill;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.entity.impl.player.Player;

import java.util.HashMap;

public class HolyPrayers {

    public enum PrayerData{
        //TODO: Set up configIDs for prayers
        DESTRUCTION("Destruction", 75, 1, 42503, 999),
        HUNTERS_EYE("Hunter's Eye", 80, 1, 42505, 999),
        FORTITUDE("Fortitude", 85, 1, 42507, 999),
        GNOMES_GREED("Gnome's Greed", 90, 1, 42509, 999),
        SOUL_LEECH("Soul Leech", 94, 1, 42511, 999),
        FURY_SWIPE("Fury Swipe", 99, 1, 42513, 999);

        public int requirement;
        public String prayerName;
        public double drainRate;
        public int buttonID;
        public int configID;
        private int hint = -1;
        private static HashMap<Integer, HolyPrayers.PrayerData> prayerData = new HashMap<Integer, HolyPrayers.PrayerData>();

        private PrayerData(String prayerName, int requirement, double drainRate,
                           int buttonID, int configID, int... hint) {
            this.prayerName = prayerName;
            this.requirement = requirement;
            this.drainRate =  drainRate;
            this.buttonID = buttonID;
            this.configID = configID;
            if (hint.length > 0)
               this.hint = hint[0];
        }

        public void HandlePrayer(Player player, final int buttonID) {
            for (HolyPrayers.PrayerData pd : HolyPrayers.PrayerData.values()) {
                if (buttonID == pd.buttonID) {
                    if (!player.getPrayerActive()[pd.ordinal()])
                        //TODO: Finish following method:
                        activatePrayer(player, pd.ordinal());
                    else
                        //TODO: Finish following method:
                        deactivatePrayer(player, pd.ordinal());
                }
                if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < requirement) {
                    player.getPacketSender().sendMessage("You must be " + requirement + "to use " + prayerName + ".");
                    deactivatePrayer(player, pd.ordinal());

                }
            }
        }
    }

    public static void activatePrayer(Player player, final int prayerID){
        if (player.getPrayerActive()[prayerID])
            return;
        if (Dueling.checkRule(player, Dueling.DuelRule.NO_PRAYER)) {
            player.getPacketSender().sendMessage("Prayer has been disabled in this duel.");
            CurseHandler.deactivateAll(player);
            PrayerHandler.deactivateAll(player);
            deactivateAll(player);
            return;
        }
        switch (prayerID) {
            case FURY_SWIPE:
                resetPrayers(player, DISABLED_WITH_FURY_SWIPE, prayerID);
                break;
            case HUNTERS_EYE:
                resetPrayers(player, DISABLED_WITH_HUNTERS_EYE, prayerID);
                break;
            case DESTRUCTION:
                resetPrayers(player, DISBALED_WITH_DESTRUCTION, prayerID);
                break;
        }
    }


    /**
     * Gets the amount of prayer to drain for <code>player</code>.
     *
     * @param player The player to get drain amount for.
     * @return The amount of prayer that will be drained from the player.

    private static final double getDrain(Player player) {
        double toRemove = 0.0;
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            if (player.getPrayerActive()[i]) {
                HolyPrayers.PrayerData prayerData = HolyPrayers.PrayerData.prayerData.get(i);
                toRemove += prayerData.drainRate / 10;
            }
        }
        if(player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
            toRemove *= 0.99;
        }
        if(player.getGameMode() == GameMode.VETERAN_MODE) {
            toRemove *= 0.97;
        }

        if(player.getEquipment().contains(23049)) { //Tier 6 Aura
            toRemove *= 0.95;
        }

        /**
         * Donator Rank Bonusses

        if(player.getRights().equals(PlayerRights.SAPPHIRE_DONATOR)) {
            toRemove *= 0.95;
        } else if(player.getRights().equals(PlayerRights.EMERALD_DONATOR)) {
            toRemove *= 0.93;
        } else if(player.getRights().equals(PlayerRights.RUBY_DONATOR)) {
            toRemove *= 0.90;
        } else if(player.getRights().equals(PlayerRights.DIAMOND_DONATOR)) {
            toRemove *= 0.88;
        } else if(player.getRights().equals(PlayerRights.ONYX_DONATOR)) {
            toRemove *= 0.85;
        } else if(player.getRights().equals(PlayerRights.ZENYTE_DONATOR)) {
            toRemove *= 0.80;
        }

        if (toRemove > 0) {
            toRemove /= (1 + (0.05 * player.getBonusManager().getOtherBonus()[2]));
        }
        return toRemove;
    }


    public static void deactivatePrayer(Player player, final int prayerID){

    }
    public static void deactivateAll(Player player) {
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            HolyPrayers.PrayerData pd = HolyPrayers.PrayerData.prayerData.get(i);
            if (pd == null)
                continue;
            player.getPrayerActive()[i] = false;
            player.getPacketSender().sendConfig(pd.configID, 0);
            if (pd.hint != -1) {
                int hintId = getHeadHint(player);
                player.getAppearance().setHeadHint(hintId);
            }
        }
    }

    public static void resetPrayers(Player player, int[] prayers, int prayerID) {
        for (int i = 0; i < prayers.length; i++) {
            if (prayers[i] != prayerID)
                deactivatePrayer(player, prayers[i]);
        }
    }

    public static final int
            DESTRUCTION = 60, HUNTERS_EYE = 61,
            FORTITUDE = 62, GNOMES_GREED = 63,
            SOUL_LEECH = 64, FURY_SWIPE = 65;

    private static final int[] DISABLED_WITH_FURY_SWIPE = {HUNTERS_EYE, DESTRUCTION};
    private static final int[] DISABLED_WITH_HUNTERS_EYE = {FURY_SWIPE, DESTRUCTION};
    private static final int[] DISBALED_WITH_DESTRUCTION = {HUNTERS_EYE, FURY_SWIPE};
    /**
     * Gets the player's current head hint if they activate or deactivate a head
     * prayer.
     *
     * @param player The player to fetch head hint index for.
     * @return The player's current head hint index.

    private static int getHeadHint(Player player) {
        //TODO: Ripped from Regular Prayers class. Convert to new prayers
        boolean[] prayers = player.getPrayerActive();
        if (prayers[DESTRUCTION])
            return 0;
        if (prayers[HUNTERS_EYE])
            return 1;
        if (prayers[FORTITUDE])
            return 2;
        if (prayers[GNOMES_GREED])
            return 3;
        if (prayers[SOUL_LEECH])
            return 4;
        if (prayers[FURY_SWIPE])
            return 5;
        return -1;
    }
}
*/