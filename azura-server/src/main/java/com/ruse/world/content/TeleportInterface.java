package com.ruse.world.content;

import com.ruse.model.Position;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

public class TeleportInterface {

    public static void resetOldData(Player player) {
        player.setCurrentTeleportTab(0);
        player.setCurrentTeleportClickIndex(0);
    }


    private static void clearData(Player player) {
        for (int i = 122302; i < 122400; i += 3) {
            player.getPacketSender().sendString(i, "");
        }
    }

    public static boolean handleButton(Player player, int buttonID) {

        switch (buttonID) {
            case 1716:
                if (!player.isOpenedTeleports()) {
                    player.setOpenedTeleports(true);
                    TeleportInterface.sendMonsterData(player, TeleportInterface.ProgressionMonsters.values()[0]);
                    TeleportInterface.sendMonsterTab(player);
                } else {
                    player.getPacketSender().sendInterface(122000);
                }
                return true;
            case 122005:
            case 11004:
                TeleportInterface.sendMonsterTab(player);
                return true;
            case 122006:
            case 11008:
                TeleportInterface.sendBossTab(player);
                return true;
            case 122007:
            case 11011:
                TeleportInterface.sendMinigamesTab(player);
                return true;
            case 122008:
            case 11014:
                TeleportInterface.sendDungeonsTab(player);
                return true;
            case 122009:
            case 11017:
                TeleportInterface.sendMiscTab(player);
                return true;
            case 122017:
                TeleportInterface.handleTeleports(player);
                return true;
            case 122018:
            case 1717:
                Teleport data = player.getPreviousTeleport();
                if (data != null) {
                    if (data instanceof ProgressionMonsters) {
                        handleMonsterTeleport(player, (ProgressionMonsters) data);
                    } else if (data instanceof Bosses) {
                        handleBossTeleport(player, (Bosses) data);
                    } else if (data instanceof Minigames) {
                        handleMinigameTeleport(player, (Minigames) data);
                    } else if (data instanceof Dungeons) {
                        handleDungeonsTeleport(player, (Dungeons) data);
                    } else if (data instanceof Misc) {
                        handleMiscTeleport(player, (Misc) data);
                    }
                }
                return true;
        }

        if (buttonID >= 122202 && buttonID <= 122229) {
            int index = (buttonID - 122202) / 3;
            if ((buttonID - 122202) % 3 == 0) {
                if (index < player.getFavoriteTeleports().size()) {
                    Teleport data = player.getFavoriteTeleports().get(index);
                    if (data != null) {
                        if (data instanceof ProgressionMonsters) {
                            handleMonsterTeleport(player, (ProgressionMonsters) data);
                        } else if (data instanceof Bosses) {
                            handleBossTeleport(player, (Bosses) data);
                        } else if (data instanceof Minigames) {
                            handleMinigameTeleport(player, (Minigames) data);
                        } else if (data instanceof Dungeons) {
                            handleDungeonsTeleport(player, (Dungeons) data);
                        } else if (data instanceof Misc) {
                            handleMiscTeleport(player, (Misc) data);
                        }
                    }
                }
            } else if ((buttonID - 122202) % 3 == 1) {
                if (index < player.getFavoriteTeleports().size()) {
                    player.getFavoriteTeleports().remove(index);
                }
                showFavorites(player);
                updateTab(player);
            }
            return true;
        }

        if (buttonID >= 122302 && buttonID <= 122500) {
            int index = (buttonID - 122302) / 3;

            if ((buttonID - 122302) % 3 == 0) {
                if (player.getCurrentTeleportTab() == 0) {
                    if (index < ProgressionMonsters.values().length) {
                        ProgressionMonsters monsterData = ProgressionMonsters.values()[index];
                        player.setCurrentTeleportClickIndex(index);
                        sendMonsterData(player, monsterData);
                    }
                }
                if (player.getCurrentTeleportTab() == 1) {
                    if (index < Bosses.values().length) {
                        Bosses bossData = Bosses.values()[index];
                        player.setCurrentTeleportClickIndex(index);
                        sendBossData(player, bossData);
                    }
                }
                if (player.getCurrentTeleportTab() == 2) {
                    if (index < Minigames.values().length) {
                        Minigames minigamesData = Minigames.values()[index];
                        player.setCurrentTeleportClickIndex(index);
                        sendMinigameData(player, minigamesData);
                    }
                }
                if (player.getCurrentTeleportTab() == 3) {
                    if (index < Dungeons.values().length) {
                        Dungeons wildyData = Dungeons.values()[index];
                        player.setCurrentTeleportClickIndex(index);
                        sendDungeonsData(player, wildyData);
                    }
                }
                if (player.getCurrentTeleportTab() == 4) {
                    if (index < Misc.values().length) {
                        Misc miscData = Misc.values()[index];
                        player.setCurrentTeleportClickIndex(index);
                        sendMiscData(player, miscData);
                    }
                }
            } else if ((buttonID - 122302) % 3 == 1) {
                Teleport data = null;

                System.out.println("here " + index);
                if (player.getCurrentTeleportTab() == 0 && index < ProgressionMonsters.values().length) {
                    data = ProgressionMonsters.values()[index];
                } else if (player.getCurrentTeleportTab() == 1 && index < Bosses.values().length) {
                    data = Bosses.values()[index];
                } else if (player.getCurrentTeleportTab() == 2 && index < Minigames.values().length) {
                    data = Minigames.values()[index];
                } else if (player.getCurrentTeleportTab() == 3 && index < Dungeons.values().length) {
                    data = Dungeons.values()[index];
                } else if (player.getCurrentTeleportTab() == 4 && index < Misc.values().length) {
                    data = Misc.values()[index];
                }

                if (data != null) {
                    if (!player.getFavoriteTeleports().contains(data)) {
                        if (player.getFavoriteTeleports().size() >= 10) {
                            player.sendMessage("Your favorites section is full.");
                            updateTab(player);
                            return true;
                        }
                        player.getFavoriteTeleports().add(data);
                        player.sendMessage("Added favorite");
                    } else {
                        player.getFavoriteTeleports().remove(data);
                        player.sendMessage("Removed favorite");
                    }
                    showFavorites(player);
                }
                updateTab(player);


            }
            return true;
        }
        return false;
    }

    public static void updateTab(Player player) {
        switch (player.getCurrentTeleportTab()) {
            case 0:
                sendMonsterTab(player);
                break;
            case 1:
                sendBossTab(player);
                break;
            case 2:
                sendMinigamesTab(player);
                break;
            case 3:
                sendDungeonsTab(player);
                break;
            case 4:
                sendMiscTab(player);
                break;
        }
    }

    public static void handleTeleports(Player player) {
        switch (player.getCurrentTeleportTab()) {
            case 0:
                ProgressionMonsters monsterData = ProgressionMonsters.values()[player.getCurrentTeleportClickIndex()];
                handleMonsterTeleport(player, monsterData);
                break;
            case 1:
                Bosses bossData = Bosses.values()[player.getCurrentTeleportClickIndex()];
                handleBossTeleport(player, bossData);
                break;
            case 2:
                Minigames minigameData = Minigames.values()[player.getCurrentTeleportClickIndex()];
                handleMinigameTeleport(player, minigameData);
                break;
            case 3:
                Dungeons wildyData = Dungeons.values()[player.getCurrentTeleportClickIndex()];
                handleDungeonsTeleport(player, wildyData);
                break;
            case 4:
                Misc miscData = Misc.values()[player.getCurrentTeleportClickIndex()];
                handleMiscTeleport(player, miscData);
                break;
        }
    }

    public static void handleBossTeleport(Player player, Bosses bossData) {
        player.setPreviousTeleport(bossData);
        TeleportHandler.teleportPlayer(player,
                new Position(bossData.teleportCords[0], bossData.teleportCords[1], bossData.teleportCords[2]),
                player.getSpellbook().getTeleportType());
    }

    public static void handleMonsterTeleport(Player player, ProgressionMonsters monsterData) {
        player.setPreviousTeleport(monsterData);

        /*if (monsterData == Monsters.STARTER_ZONE) {
            ProgressionZone.teleport(player);
            return;
        }*/
        TeleportHandler.teleportPlayer(player,
                new Position(monsterData.teleportCords[0], monsterData.teleportCords[1], monsterData.teleportCords[2]),
                player.getSpellbook().getTeleportType());
    }

    public static void handleDungeonsTeleport(Player player, Dungeons wildyData) {
        player.setPreviousTeleport(wildyData);
        TeleportHandler.teleportPlayer(player,
                new Position(wildyData.teleportCords[0], wildyData.teleportCords[1], wildyData.teleportCords[2]),
                player.getSpellbook().getTeleportType());
    }

    public static void handleMiscTeleport(Player player, Misc miscData) {
        player.setPreviousTeleport(miscData);

        if (miscData == Misc.TRAIN_ZONE) {
            ProgressionZone.teleport(player);
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }

        TeleportHandler.teleportPlayer(player,
                new Position(miscData.teleportCords[0], miscData.teleportCords[1], miscData.teleportCords[2]),
                player.getSpellbook().getTeleportType());
    }

    public static void handleMinigameTeleport(Player player, Minigames minigameData) {
        player.setPreviousTeleport(minigameData);

        if (minigameData == Minigames.PlaceholderMini1) {
            player.hov.initArea();
            player.hov.start();
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        if (minigameData == Minigames.PlaceholderMini2) {
            player.vod.initArea();
            player.vod.start();
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        TeleportHandler.teleportPlayer(player, new Position(minigameData.teleportCords[0],
                minigameData.teleportCords[1], minigameData.teleportCords[2]), player.getSpellbook().getTeleportType());
    }

    public static void sendBossData(Player player, Bosses data) {
        player.getPacketSender().sendNpcOnInterface(122051, data.npcId, data.adjustedZoom);
        //player.getPacketSender().sendString(122202, NpcDefinition.forId(data.npcId).getHitpoints() + " - " + data.npcId);
        sendDrops(player, data.npcId);
    }

    public static void sendMonsterData(Player player, ProgressionMonsters data) {
        player.getPacketSender().sendNpcOnInterface(122051, data.npcId, data.adjustedZoom);
      //  player.getPacketSender().sendString(122202, NpcDefinition.forId(data.npcId).getHitpoints() + " - " + data.npcId);
        sendDrops(player, data.npcId);
    }

    public static void sendDungeonsData(Player player, Dungeons data) {
        player.getPacketSender().sendNpcOnInterface(122051, data.npcId, data.adjustedZoom);
        sendDrops(player, data.npcId);
    }

    public static void sendMiscData(Player player, Misc data) {
        player.getPacketSender().sendNpcOnInterface(122051, data.npcId, data.adjustedZoom);
        sendDrops(player, data.npcId);
    }

    public static void sendMinigameData(Player player, Minigames data) {
        player.getPacketSender().sendNpcOnInterface(122051, data.npcId, data.adjustedZoom);
        if (data.loot != null)
            sendDrops(player, data.loot);
        else
            sendDrops(player, data.npcId);
    }

    public static void showFavorites(Player player) {
        int id = 122202;
        for (int i = 0; i < 10; i++) {
            if (player.getFavoriteTeleports().size() > i) {
                player.getPacketSender().sendString(id, player.getFavoriteTeleports().get(i).getName());
            } else {
                player.getPacketSender().sendString(id, "---");
            }
            id += 3;
        }
    }


    public static void setUp(Player player, int index) {
        resetOldData(player);
        player.setCurrentTeleportTab(index);
        sendTitles(player);
        clearData(player);
        player.getPacketSender().sendConfig(2877, player.getCurrentTeleportTab());
        showFavorites(player);
    }

    public static void showList(Player player, Teleport[] list) {
        int id = 122302;
        int config = 5340;
        for (Teleport data : list) {
            player.getPacketSender().sendString(id, data.getName());
            if (player.getFavoriteTeleports().contains(data))
                player.getPacketSender().sendConfig(config++, 1);
            else
                player.getPacketSender().sendConfig(config++, 0);

            id += 3;
        }
        player.getPacketSender().setScrollBar(122300, ((id - 122302) / 3) * 20);
        player.getPacketSender().sendInterface(122000);
    }

    public static void sendMonsterTab(Player player) {
        setUp(player, 0);
        showList(player, ProgressionMonsters.values());
    }

    public static void sendBossTab(Player player) {
        setUp(player, 1);
        showList(player, Bosses.values());
    }

    public static void sendMinigamesTab(Player player) {
        setUp(player, 2);
        showList(player, Minigames.values());
    }

    public static void sendDungeonsTab(Player player) {
        setUp(player, 3);
        showList(player, Dungeons.values());
    }

    public static void sendMiscTab(Player player) {
        setUp(player, 4);
        showList(player, Misc.values());
    }

    public static void sendTitles(Player player) {
        String[] categories = new String[]{"Monsters", "Bosses", "Minigames", "Dungeons", "Misc"};
        for (int i = 0; i < 5; i++) {
            player.getPacketSender().sendString(122011 + i, (player.getCurrentTeleportTab() == i ? "@whi@" : "") + categories[i]);
        }
    }

    public static void sendDrops(Player player, int npcId) {
        if (npcId == -1) {
            int length = 10;

            for (int i = 0; i < length; i++) {
                player.getPacketSender().sendItemOnInterface1(35500, -1, i,
                        0);
            }
            int scroll = 43;
            player.getPacketSender().setScrollBar(122060, scroll);
        } else {
            try {
                NPCDrops drops = NPCDrops.getDrops().get(npcId);
                if (drops != null) {
                    int length = drops.getDropList().length;
                    if (length >= 160)
                        length = 160;


                    if (length >= 10 && length % 5 == 0){
                    }else{
                        length += 5 - (length % 5);
                    }

                    for (int i = 0; i < length + 5; i++) {
                        if (drops.getDropList().length > i) {
                            player.getPacketSender().sendItemOnInterface1(35500, drops.getDropList()[i].getId(), i,
                                    drops.getDropList()[i].getCount()[drops.getDropList()[i].getCount().length - 1] == -1 ? 25000
                                            : drops.getDropList()[i].getCount()[drops.getDropList()[i].getCount().length - 1]);
                        } else {
                            player.getPacketSender().sendItemOnInterface1(35500, -1, i,
                                    0);
                        }
                    }

                    int scroll = 7 + (length / 5) * 35;
                    if (scroll <= 43)
                        scroll = 43;

                    player.getPacketSender().setScrollBar(122060, scroll);
                }else{
                    sendDrops(player, -1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendDrops(Player player, Box[] drops) {
        try {
            if (drops != null) {
                int length = drops.length;
                if (length >= 160)
                    length = 160;

                length += 5 - (length % 5);
                for (int i = 0; i < length + 5; i++) {
                    if (drops.length > i)
                        player.getPacketSender().sendItemOnInterface1(35500, drops[i].getId(), i,
                                drops[i].getAmount());
                    else
                        player.getPacketSender().sendItemOnInterface1(35500, -1, i,
                                0);
                }
                int scroll = 7 + (length / 5) * 35;
                if (scroll <= 43)
                    scroll = 43;
                player.getPacketSender().setScrollBar(122060, scroll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public enum ProgressionMonsters implements Teleport {

        // STARTER_ZONE("Starter Zone", 9001, new int[]{1, 1, 0}, 600),

        Placeholder1("@gre@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder2("@gre@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder3("@gre@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder4("@gre@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder5("@gre@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder6("@yel@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder7("@yel@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder8("@yel@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder9("@yel@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder10("@yel@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder11("Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder12("Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder13("Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder14("Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder15("Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder16("@red@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder17("@red@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder18("@red@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder19("@red@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700),
        Placeholder20("@red@Vampyre hands", 1703, new int[]{1815, 4909, 0}, 700);

        private final String name;
        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;

        ProgressionMonsters(String name, int npcId, int[] teleportCords, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum Bosses implements Teleport {
        //TODO update with actual bosses
        PlaceHolderBoss1("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss2("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss3("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss4("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss5("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss6("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss7("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        PlaceHolderBoss8("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        ;

        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;
        private final String name;

        Bosses(String name, int npcId, int[] teleportCords, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;
        }

        public static boolean contains(int npcID) {
            for (Bosses d : Bosses.values()) {
                if (d.getNpcId() == npcID)
                    return true;
            }
            return false;
        }

        public int getNpcId() {
            return npcId;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum Minigames implements Teleport {
        //TODO update placehoolder minigame teleports with actual minigames
        PlaceholderMini1("Dungeoneering", 11226, new int[]{2251, 5040, 0}, DungeoneeringParty.loot, 700),
        PlaceholderMini2("Dungeoneering", 11226, new int[]{2251, 5040, 0}, DungeoneeringParty.loot, 700),
        PlaceholderMini3("Dungeoneering", 11226, new int[]{2251, 5040, 0}, DungeoneeringParty.loot, 700),
        PlaceholderMini4("Dungeoneering", 11226, new int[]{2251, 5040, 0}, DungeoneeringParty.loot, 700),
        ;

        private final String name;
        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;
        private final Box[] loot;

        Minigames(String name, int npcId, int[] teleportCords, Box[] loot, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;
            this.loot = loot;
        }

        Minigames(String name, int npcId, int[] teleportCords, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;
            this.loot = null;
        }

        @Override
        public String getName() {
            return name;
        }
    }


    public enum Dungeons implements Teleport {
        //TODO Update dungeon zones with actual dungeons
        PlaceholderDungeon1("Easy Dungeon", 1705, new int[]{1905, 4870, 0}, 700),
        PlaceholderDungeon2("Easy Dungeon", 1705, new int[]{1905, 4870, 0}, 700),
        PlaceholderDungeon3("Easy Dungeon", 1705, new int[]{1905, 4870, 0}, 700),
        ;

        private final String name;
        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;

        Dungeons(String name, int npcId, int[] teleportCords, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum Misc implements Teleport {
        //TODO Update misc zones with actual zones
        PlaceholderMisc1("Starter Zone", 9001, new int[]{1, 1, 0}, 600),
        PlaceholderMisc2("Starter Zone", 9001, new int[]{1, 1, 0}, 600),
        PlaceholderMisc3("Starter Zone", 9001, new int[]{1, 1, 0}, 600),
        TRAIN_ZONE("Training zone", 9001, new int[]{3472, 9484, 0}, 600),
        ;

        private final String name;
        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;

        Misc(String name, int npcId, int[] teleportCords, int adjustedZoom) {
            this.name = name;
            this.npcId = npcId;
            this.teleportCords = teleportCords;
            this.adjustedZoom = adjustedZoom;

        }

        @Override
        public String getName() {
            return name;
        }

    }

    public interface Teleport {
        String getName();
    }


}
