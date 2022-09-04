package com.ruse.world.content;

import com.ruse.model.Position;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.minigames.impl.*;
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
                    TeleportInterface.sendMonsterData(player, TeleportInterface.Monsters.values()[0]);
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
                    if (data instanceof Monsters) {
                        handleMonsterTeleport(player, (Monsters) data);
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
                        if (data instanceof Monsters) {
                            handleMonsterTeleport(player, (Monsters) data);
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
                    if (index < Monsters.values().length) {
                        Monsters monsterData = Monsters.values()[index];
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
                if (player.getCurrentTeleportTab() == 0 && index < Monsters.values().length) {
                    data = Monsters.values()[index];
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
                Monsters monsterData = Monsters.values()[player.getCurrentTeleportClickIndex()];
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

    public static void handleMonsterTeleport(Player player, Monsters monsterData) {
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

        if (miscData == Misc.STARTER_ZONE) {
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

        if (minigameData == Minigames.HOV) {
            player.hov.initArea();
            player.hov.start();
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        if (minigameData == Minigames.VOD) {
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

    public static void sendMonsterData(Player player, Monsters data) {
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
        showList(player, Monsters.values());
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

    public enum Monsters implements Teleport {

        // STARTER_ZONE("Starter Zone", 9001, new int[]{1, 1, 0}, 600),

        VAL("Valix", 1703, new int[]{1815, 4909, 0}, 1000),
        ZAL("Zalis", 1721, new int[]{1848, 4896, 0}, 1000),
        AXEL("Axel", 1729, new int[]{1878, 4908, 0}, 1000),
        GEL("Gelumonstrum", 1705, new int[]{1903, 4879, 0}, 1300),
        DUR("Durago", 1712, new int[]{1904, 4850, 0}, 700),
        DRAC("Dracon", 1711, new int[]{1880, 4822, 0}, 550),
        KLAX("Klaxor", 1739, new int[]{1846, 4819, 0}, 500),
        BLOODVELD("Dark bloodveld", 1710, new int[]{1809, 4834, 0}, 800),
        LAVANNOTH("Lavannoth", 1702, new int[]{1829, 4863, 0}, 1000),
        CRAB("Granite crab", 1700, new int[]{1869, 4863, 0}, 800),

        ANT("Ant worker", 1724, new int[]{2005, 4772, 0}, 700),
        MOSQUITO("Mosquito", 1713, new int[]{2025, 4772, 0}, 800),
        PLANT("War plant", 1737, new int[]{2026, 4759, 0}, 800),
        BIRD("Tycoons bird", 1730, new int[]{2004, 4754, 0}, 800),

        UNICORN("Nature unicorn", 1742, new int[]{2212, 4941, 0}, 1200),
        DRAGON("Bronze dragon", 1706, new int[]{2187, 4943, 0}, 800),
        Z_BIRD("Zamorak bird", 1725, new int[]{2214, 4960, 0}, 800),
        SYM("Symbiote", 1727, new int[]{2218, 4976, 0}, 800),
        GOUL("Ghoulord", 1708, new int[]{2195, 4975, 0}, 800),
        GROOTER("Grooter", 1744, new int[]{2166, 4946, 0}, 1200),
        MOSS("Elemental moss", 1740, new int[]{2166, 4946, 0}, 1000),
        FIRE("Elemental fire", 1741, new int[]{2162, 4972, 0}, 1000),
        PELICAN("Pelican bird", 1709, new int[]{2147, 4950, 0}, 1000),
        TURTLE("Runite turtle", 1745, new int[]{2129, 4977, 0}, 800),
        SABRE("Sabretooth", 1731, new int[]{2129, 4977, 0}, 800),
        MINOTAUR("Armoured minotaur", 1719, new int[]{2126, 4953, 0}, 800),

        DEMON("Native demon", 1715, new int[]{1633, 4844, 0}, 1200),
        GRAAHK("Wild graahk", 1734, new int[]{1669, 4843, 0}, 800),
        LEOPARD("Leopard", 1733, new int[]{1686, 4843, 0}, 800),
        SEA("Sea creature", 1735, new int[]{1705, 4840, 0}, 800),
        KREE("Kree devil", 1736, new int[]{1708, 4818, 0}, 900),
        HYNDRA("Hyndra", 1743, new int[]{1665, 4819, 0}, 800),
        CHIN("Evil chinchompa", 1723, new int[]{1640, 4819, 0}, 800),
        CHIN_DRAGON("Chinese dragon", 1716, new int[]{1623, 4816, 0}, 800),

        ;

        private final String name;
        private final int npcId;
        private final int[] teleportCords;
        private final int adjustedZoom;

        Monsters(String name, int npcId, int[] teleportCords, int adjustedZoom) {
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
        ELITE_DRAGON("Elite dragon", 8015, new int[]{2911, 3991, 0}, 1300),
        ETERNAL_DRAGON("Eternal dragon", 4972, new int[]{2975, 4000, 0}, 2200),
        SCARLET_FALCON("Scarlet falcon", 2949, new int[]{3036, 4003, 0}, 2200),

        CRYSTAL_QUEEN("Crystal queen", 6430, new int[]{1887, 5468, 0}, 1100),
        LUCIFER("Lucifer", 9012, new int[]{2335, 3998, 0}, 1000),
        MEGA_AVATAR("Mega avatar", 4540, new int[]{1884, 5334, 0}, 1600),

        CRAZY_WITCH("Crazy witch", 1234, new int[]{2784, 4445, 0}, 1000),
        LIGHT_SUPREME("Light supreme", 440, new int[]{2761, 4575, 0}, 1000),
        DARK_SUPREME("Dark supreme", 438, new int[]{2721, 4450, 0}, 1500),
        FRACTITE_DEMON("Fractite demon", 12843, new int[]{2785, 4525, 0}, 1000),
        INFERNAL_DEMON("Infernal demon", 12810, new int[]{2712, 4508, 0}, 1500),

        GLOBAL_BOSSES("Global bosses", 9014, new int[]{2139, 5019, 0}, 1000),


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
        DUNG("Dungeoneering", 11226, new int[]{2251, 5040, 0}, DungeoneeringParty.loot, 700),
        HOV("Halls of Valor", 9024, new int[]{2195, 5037, 0}, HallsOfValor.loot, 1400),
        VOD("Void of Darkness", 9028, new int[]{1954, 5010, 0}, VoidOfDarkness.loot, 600),
        TH("Treasure Hunter", 9816, new int[]{2015, 5022, 0}, TreasureHunter.loot, 3000),
        KOL("Keepers of Light", 9835, new int[]{2322, 5028, 0}, KeepersOfLight.loot, 1200),
        VOW("Vault of War", 9839, new int[]{1776, 5335, 0}, VaultOfWar.loot, 1200),
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

        EASY_DUNGEON("Easy Dungeon", 1705, new int[]{1905, 4870, 0}, 700),
        EASY_DUNGEON_1("Easy Dungeon 2", 1724, new int[]{2016, 4767, 0}, 700),
        MEDIUM_DUNGEON("Medium Dungeon", 1742, new int[]{2227, 4946, 0}, 1200),
        HARD_DUNGEON("Hard Dungeon", 1715, new int[]{1637, 4856, 0}, 1200),
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

        STARTER_ZONE("Starter Zone", 9001, new int[]{1, 1, 0}, 600),
        SKILL_ISLAND("Skill Island", 805, new int[]{2817, 2600, 0}, 700),
        GLOBAL_BOSSES("Global bosses", 9014, new int[]{2139, 5019, 0}, 1000),
        PURO_PURO_TELEPORT("Puro Puro", 6064, new int[]{2589, 4319, 0}, 700),
        TELE0("Gnome Course", 437, new int[]{2480, 3435, 0}, 700),
        TELE1("Barbarian Course", 437, new int[]{2552, 3556, 0}, 700),
        TELE2("Wilderness Course", 437, new int[]{3003, 3934, 0}, 700),


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
