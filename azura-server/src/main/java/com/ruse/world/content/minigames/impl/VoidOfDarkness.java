package com.ruse.world.content.minigames.impl;

import com.ruse.model.GameObject;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.casketopening.BoxLoot;
import com.ruse.world.content.dailytasks_new.DailyTask;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class VoidOfDarkness {

    public static final Position TELEPORT_AREA = new Position(1954, 5010, 0);
    public static final int CHEST_ID = 41203;
    public static final Object[][] data = {{"Tectonic skeleton lord", 126507}, {"Skinhead the Destroyer", 126508},
            {"Great elf of Solak", 126509}};
    public static Box[] loot = { //
            new Box(995, 50, 100000, 100D),

            //magic
            new Box(23021, 1, 0.25D, true),
            new Box(23022, 1, 0.25D, true),
            new Box(23023, 1, 0.25D, true),
            new Box(23027, 1, 0.5D, true),
            new Box(23024, 1, 0.5D, true),
            new Box(23025, 1, 0.5D, true),

            //melee
            new Box(23028, 1, 0.25D, true),
            new Box(23029, 1, 0.25D, true),
            new Box(23030, 1, 0.25D, true),
            new Box(23031, 1, 0.5D, true),
            new Box(23032, 1, 0.5D, true),

            //ranged
            new Box(23034, 1, 0.25D, true),
            new Box(23035, 1, 0.25D, true),
            new Box(23036, 1, 0.25D, true),
            new Box(23037, 1, 0.5D, true),
            new Box(23038, 1, 0.5D, true),

            //weps
            new Box(23026, 1, 0.28D, true),
            new Box(23033, 1, 0.28D, true),
            new Box(23039, 1, 0.33D, true),
    };
    private final Player player;

    public VoidOfDarkness(Player player) {
        this.player = player;
    }


    public void initArea() {
        TeleportHandler.teleportPlayer(player, VoidOfDarkness.TELEPORT_AREA.copy().setZ(0/*player.getIndex() * 4*/), TeleportType.NORMAL);
        /*if (player.getRegionInstance() != null && player.getRegionInstance().equals(RegionInstance.RegionInstanceType.VOID_OF_DARKNESS)) {
            player.getRegionInstance().destruct();
            World.getNpcs().forEach(n -> n.removeInstancedNpcs(Locations.Location.VOID_OF_DARKNESS, player.getIndex() * 4));
        }*/
        resetBarrows(player);
    }

    public void start() {
       // player.setRegionInstance(new RegionInstance(player, RegionInstance.RegionInstanceType.VOID_OF_DARKNESS));
    }


    public static void updateInterface(Player player) {
        for (int i = 0; i < data.length; i++) {
            boolean killed = player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[i][1] == 2;
            String s = killed ? "@gre@" : "@red@";
            player.getPacketSender().sendString((int) data[i][1], "" + s + ""
                    + NpcDefinition.forId(player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[i][0]).getName());
        }

        player.getPacketSender().sendString(126506, "Void of Darkness");
        player.getPacketSender().sendString(126510,
                "Killcount: " + player.getMinigameAttributes().getVoidOfDarknessAttributes().getKillcount());
    }

    public static void resetBarrows(Player player) {
        player.getMinigameAttributes().getVoidOfDarknessAttributes().setKillcount(0);
        for (int i = 0; i < player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData().length; i++)
            player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[i][1] = 0;
    }

    public static void handleLogin(Player player) {
        updateInterface(player);
    }

    public void handleObject(GameObject object) {
        if (object.getId() == CHEST_ID) {
            if (player.getMinigameAttributes().getVoidOfDarknessAttributes().getKillcount() < 3) {
                player.getPacketSender().sendMessage("You've not searched all the coffins yet.");
                return;
            }
            if (player.getInventory().getFreeSlots() < 1) {
                player.getPacketSender()
                        .sendMessage("You need at least 1 free inventory slot to loot this chest.");
                return;
            }
            resetBarrows(player);
            handleRewards(player);
            updateInterface(player);
            return;
        }
        switch (object.getId()) {
            case 41202:
                searchCoffin(object.getId(), 2, 9030, new Position(1933 + Misc.getRandom(6), 5023 + Misc.getRandom(3), player.getPosition().getZ()));
                return;
            case 41201:
                searchCoffin(object.getId(), 1, 9028, new Position(1951 + Misc.getRandom(6), 5032 + Misc.getRandom(3), player.getPosition().getZ()));
                return;
            case 41200:
                searchCoffin(object.getId(), 0, 9029, new Position(1968 + Misc.getRandom(6), 5021 + Misc.getRandom(3), player.getPosition().getZ()));
        }
    }

    public void searchCoffin(final int obj, final int coffinId, int npcId,
                             Position spawnPos) {
        player.getPacketSender().sendInterfaceRemoval();
        //player.sendMessage("COFFIN STATE = " + player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[coffinId][1]);


        NPC currentSpawn = player.findSpawnedFor();

        int coffinState = player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[coffinId][1];
        if (coffinState == 0) {
            if (player.getPosition().getRegionId() == 7758) {
                NPC npc_ = new NPC(npcId, spawnPos);
                npc_.forceChat(
                        player.getPosition().getZ() == -1 ? "You dare disturb my rest!" : "You dare steal from us!");
                npc_.getCombatBuilder().setAttackTimer(3);
                npc_.setSpawnedFor(player);
                npc_.getCombatBuilder().attack(player);
                World.register(npc_);
                player.getPacketSender().sendEntityHint(npc_);
                if (player.getRegionInstance() != null && player.getRegionInstance().getNpcsList() != null) {
                    player.getRegionInstance().getNpcsList().add(npc_);
                }
                player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[coffinId][1] = 1;
            }
        } else if (coffinState == 1 && currentSpawn == null) {// Reset coffin when player has left
            player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[coffinId][1] = 0;
            player.getPacketSender().sendMessage("The entity within slumbers deeply...");
        } else {
            player.getPacketSender().sendMessage("You have already searched this sarcophagus.");
        }
    }

    public void killBarrowsNpc(Player player, NPC n, boolean killed) {
        int arrayIndex = getBarrowsIndex(n.getId());
        if (killed) {
            player.getMinigameAttributes().getVoidOfDarknessAttributes()
                    .setKillcount(player.getMinigameAttributes().getVoidOfDarknessAttributes().getKillcount() + 1);
            if (player.getRegionInstance() != null) {
                player.getRegionInstance().getNpcsList().remove(n);
            }

            player.setVodKilled(player.getVodKilled() + 1);
            player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[arrayIndex][1] = 2;
        } else player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[arrayIndex][1] = 0;

        updateInterface(player);
    }

    /**
     * Bunch of Arrays for the VoD Loot Table.
     */

    public int getBarrowsIndex(int id) {
        int index = -1;
        for (int i = 0; i < player.getMinigameAttributes().getVoidOfDarknessAttributes()
                .getBarrowsData().length; i++) {
            if (player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()[i][0] == id) {
                index = i;
            }
        }

        return index;
    }

    /**
     * Handles VoD Chest Rewards
     *
     * @param player, the player the reward is sent to.
     */
    private void handleRewards(Player player) {

        Box box = BoxLoot.getLoot(loot);
        int amount = box.getAmount();
        player.getInventory().add(box.getId(), amount);
        player.getPacketSender().sendMessage("You've received x" + amount + " " + ItemDefinition.forId(box.getId()).getName());

        //Reset the Coffin Instance
        resetBarrows(player);

        Achievements.doProgress(player, Achievements.Achievement.OPEN_5_CHESTS_IN_VOID_OF_DARKNESS);
        Achievements.doProgress(player, Achievements.Achievement.OPEN_25_CHESTS_IN_VOID_OF_DARKNESS);
        Achievements.doProgress(player, Achievements.Achievement.OPEN_200_CHESTS_IN_VOID_OF_DARKNESS);
        DailyTask.VOID_OF_DARKNESS.tryProgress(player);

    }
}
