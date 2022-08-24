package com.ruse.world.content;

public enum NpcRequirements {
/*

    MEGAAVATAR(4540, 500000),


    SPAWN(1614, 50),
    LORD(603, 1614, 100),
    DEMON(12843, 603, 200),
    DRAGON(53, 12843, 300),
    BEAST(8018, 53, 400),
    TROLLKING(13635, 8018, 500),
    AVATAR(8008, 13635, 1000),
    ANGEL(3308, 8008, 1200),
    LUCIEN(3117, 3308, 1500),
    HERCULES(201, 3117, 2500),
    SATAN(202, 201, 3500),
    ZEUS(203, 202, 5000),

    VASA(1120, 202, 50000),
    GROUDON(8010, 203, 15000),*/
    SONIC(9001, 10),
    PATRICK(9002, 25),
    LUIGI(9003, 50),
    SQUIRTLE(9004, 75),
    MEWTWO(9005, 100),
    BOWSER(9006, 150);

    private int npcId;
    private int requireNpcId;
    private int amountRequired;
    private int killCount;

    NpcRequirements(int npcId, int requireNpcId, int amountRequired) {
        this.npcId = npcId;
        this.requireNpcId = requireNpcId;
        this.amountRequired = amountRequired;
        this.killCount = 0;
    }

    NpcRequirements(int npcId, int killCount) {
        this.npcId = npcId;
        this.killCount = killCount;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getRequireNpcId() {
        return requireNpcId;
    }

    public int getAmountRequired() {
        return amountRequired;
    }

}