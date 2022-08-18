package com.ruse.world.content.cluescrolls;

import com.ruse.model.definitions.ItemDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ClueScrollReward {
    //TODO: missing item IDs
    //BRONZE_ARMOUR_SET_T(x, 100, ClueScroll.EASY),
    //IRON_ARMOUR_SET_T(x, 125, ClueScroll.EASY),
    //STEEL_ARMOUR_SET_T(x, 150, ClueScroll.EASY),
    BLACK_ARMOUR_SET_T(11878, 200, ClueScroll.EASY),
    //MITHRIL_ARMOUR_SET_T(x, 225, ClueScroll.EASY),
    ADAMANT_ARMOUR_SET_T(11886, 250, ClueScroll.EASY),
    RUNE_ARMOUR_SET_T(11894, 300, ClueScroll.EASY),
    BLUE_WIZARD_SET_T(11904, 350, ClueScroll.EASY),
    BLUE_PHAT_E(1042, 10000, ClueScroll.EASY),
    RED_PHAT_E(1038, 10000, ClueScroll.EASY),
    GREEN_PHAT_E(1044, 10000, ClueScroll.EASY),
    WHITE_PHAT_E(1048, 10000, ClueScroll.EASY),
    PURPLE_PHAT_E(1046, 10000, ClueScroll.EASY),
    YELLOW_PHAT_E(1040, 10000, ClueScroll.EASY),
    CASH_E(ItemDefinition.COIN_ID, 100000, 1, ClueScroll.EASY),

    //BRONZE_ARMOUR_SET_G(x, 100, ClueScroll.MEDIUM),
    //IRON_ARMOUR_SET_G(x, 125, ClueScroll.MEDIUM),
    //STEEL_ARMOUR_SET_G(x, 150, ClueScroll.MEDIUM),
    BLACK_ARMOUR_SET_G(11882, 175, ClueScroll.MEDIUM),
    //MITHRIL_ARMOUR_SET_G(x, 200, ClueScroll.MEDIUM),
    ADAMANT_ARMOUR_SET_G(11890, 225, ClueScroll.MEDIUM),
    RUNE_ARMOUR_SET_G(11898, 250, ClueScroll.MEDIUM),
    BLUE_WIZARD_SET_G(11906, 275, ClueScroll.MEDIUM),
    BLUE_PHAT_M(1042, 7000, ClueScroll.MEDIUM),
    RED_PHAT_M(1038, 7000, ClueScroll.MEDIUM),
    GREEN_PHAT_M(1044, 7000, ClueScroll.MEDIUM),
    WHITE_PHAT_M(1048, 7000, ClueScroll.MEDIUM),
    PURPLE_PHAT_M(1046, 7000, ClueScroll.MEDIUM),
    YELLOW_PHAT_M(1040, 7000, ClueScroll.MEDIUM),
    CASH_M(ItemDefinition.COIN_ID, 250000, 1, ClueScroll.MEDIUM),

    THIRD_AGE_RANGE_TOP(10330, 2000, ClueScroll.HARD),
    THIRD_AGE_RANGE_LEGS(10332, 2000, ClueScroll.HARD),
    THIRD_AGE_RANGE_COIF(10334, 2000, ClueScroll.HARD),
    THIRD_AGE_RANGE_VAMBS(10336, 2000, ClueScroll.HARD),
    THIRD_AGE_MAGE_TOP(10338, 2000, ClueScroll.HARD),
    THIRD_AGE_MAGE_BOTTOM(10340, 2000, ClueScroll.HARD),
    THIRD_AGE_MAGE_HAT(10342, 2000, ClueScroll.HARD),
    THIRD_AGE_MAGE_AMMY(10344, 2000, ClueScroll.HARD),
    THIRD_AGE_MELEE_LEGS(10346, 2000, ClueScroll.HARD),
    THIRD_AGE_MELEE_PLATE(10348, 2000, ClueScroll.HARD),
    THIRD_AGE_MELEE_HELM(10350, 2000, ClueScroll.HARD),
    THIRD_AGE_MELEE_SHIELD(10352, 2000, ClueScroll.HARD),
    SANTA_HAT(1050, 3000, ClueScroll.HARD),
    RED_HWEEN(1057, 3000, ClueScroll.HARD),
    GREEN_HWEEN(1053, 3000, ClueScroll.HARD),
    BLUE_HWEEN(1055, 3000, ClueScroll.HARD),
    BLUE_PHAT_H(1042, 5000, ClueScroll.HARD),
    RED_PHAT_H(1038, 5000, ClueScroll.HARD),
    GREEN_PHAT_H(1044, 5000, ClueScroll.HARD),
    WHITE_PHAT_H(1048, 5000, ClueScroll.HARD),
    PURPLE_PHAT_H(1046, 5000, ClueScroll.HARD),
    YELLOW_PHAT_H(1040, 5000, ClueScroll.HARD),
    CASH_H(ItemDefinition.COIN_ID, 5000000, 1, ClueScroll.HARD),
    ;

    public int getMinAmt() {
        return minAmt;
    }

    public int getMaxAmt() {
        return maxAmt;
    }

    public int getItemId() {
        return itemId;
    }

    int minAmt;
    int maxAmt;
    int itemId;
    int rate;
    ClueScroll type;
    public static final List<ClueScrollReward> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();

    ClueScrollReward(int itemId, int amt, int rate, ClueScroll type) {
        this.itemId = itemId;
        this.minAmt = amt;
        this.maxAmt = amt;
        this.rate = rate;
        this.type = type;
    }
    ClueScrollReward(int itemId, int minAmt, int maxAmt, int rate, ClueScroll type) {
        this.itemId = itemId;
        this.minAmt = minAmt;
        this.maxAmt = maxAmt;
        this.rate = rate;
        this.type = type;
    }
    ClueScrollReward(int itemId, int rate, ClueScroll type) {
        this.itemId = itemId;
        this.minAmt = 1;
        this.maxAmt = 1;
        this.rate = rate;
        this.type = type;
    }

    public static ClueScrollReward getWeightedReward(ClueScroll difficulty) {
        double totalWeight = 0;
        double[] pointWeight = new double[SIZE];
        for(int i = 0; i < pointWeight.length; i++) {
            ClueScrollReward current = VALUES.get(i);
            if(current.type == difficulty) {
                pointWeight[i] = 1D / current.rate;
            } else {
                pointWeight[i] = 0;
            }
            totalWeight += pointWeight[i];
        }

        double r = Math.random() * totalWeight;
        double runningWeight = 0.0;
        int winner = 0;
        for(int i = 0; i < pointWeight.length; i++) {
            runningWeight += pointWeight[i];
            if (runningWeight >= r) {
                winner = i;
                break;
            }
        }
        return VALUES.get(winner);
    }
}
