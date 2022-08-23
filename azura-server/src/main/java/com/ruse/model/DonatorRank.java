package com.ruse.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public enum DonatorRank {

    ONYX(-1, "@bla@<shad=0>", 1, 1), // The highest-privileged member of the server
    SAPPHIRE(60, "@blu@<shad=0>", 1, 1),
    EMERALD(45, "@gre@<shad=0>", 1, 1),
    RUBY(30, "@red@<shad=0>", 1, 1),
    DIAMOND(15, "@whi@<shad=0>", 1, 1),
    ZENYTE(-1, "<col=df9f04><shad=0>", 1, 1),

    YOUTUBER(-1, "<col=CD201F><shad=ffffff>", 1, 1),

    NONE(-1, null, 1, 1); // A regular muggle.
    private int yellDelay;
    private String yellHexColorPrefix;
    private double loyaltyPointsGainModifier;
    private double experienceGainModifier;


    DonatorRank(int yellDelaySeconds, String yellHexColorPrefix, double loyaltyPointsGainModifier,
                 double experienceGainModifier) {
        this.yellDelay = yellDelaySeconds;
        this.yellHexColorPrefix = yellHexColorPrefix;
        this.loyaltyPointsGainModifier = loyaltyPointsGainModifier;
        this.experienceGainModifier = experienceGainModifier;
    }

    private static final ImmutableSet<DonatorRank> donatorRanks = Sets.immutableEnumSet(ONYX, SAPPHIRE, EMERALD, RUBY, DIAMOND, ZENYTE, NONE);

}
