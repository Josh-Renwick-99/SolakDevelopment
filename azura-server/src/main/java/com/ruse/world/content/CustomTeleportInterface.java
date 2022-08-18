package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.Locations;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.world.World;
import com.ruse.world.content.minigames.impl.VoidOfDarkness;
import com.ruse.world.content.progressionzone.ProgressionZone;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class CustomTeleportInterface {

    private static final int ID_START = 60000 + 152;
    public int selectedTab = 0;
    private Player player;
    private int selectedIndex = -1;

    public CustomTeleportInterface(Player player) {
        this.player = player;
    }

    public static void sendDrops(Player player, int npcId) {
        player.getPacketSender().resetItemsOnInterface(60000 + 63, 100);
        try {
            NPCDrops drops = NPCDrops.forId(npcId);
            if (drops == null) {
                // System.out.println("Was null");
                return;
            }
            for (int i = 0; i < drops.getDropList().length; i++) {
                player.getPacketSender().sendItemOnInterface(60000 + 63, drops.getDropList()[i].getId(), i,
                        drops.getDropList()[i].getItem().getAmount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        player.getPacketSender().sendInterface(60000);
        selectedIndex = -1;
        updateInterface(false);
    }

    private void updateInterface(boolean onClick) {
        if (selectedIndex == -1 && onClick) {
            return;
        }

        if (!onClick) {

            for (int i = ID_START; i < ID_START + 16; i++) {
                player.getPacketSender().sendString(i, "");
            }

            if (selectedTab == 0) {
                for (Bosses npc : Bosses.values()) {
                    player.getPacketSender().sendString(npc.id, npc.name);
                }

            } else if (selectedTab == 1) {
                for (Monsters npc : Monsters.values()) {
                    player.getPacketSender().sendString(npc.id, npc.name);
                }
            } else if (selectedTab == 2) {
                for (Minigames npc : Minigames.values()) {
                    player.getPacketSender().sendString(npc.id, npc.name);
                }
            } else if (selectedTab == 3) {
                for (Dungeons npc : Dungeons.values()) {
                    player.getPacketSender().sendString(npc.id, npc.name);
                }
            } else if (selectedTab == 4) {
                for (Cities npc : Cities.values()) {
                    player.getPacketSender().sendString(npc.id, npc.name);
                }
            }
            return;
        } else {
            if (selectedTab == 0) {
                Bosses npc = Bosses.values()[selectedIndex];
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 51, "Attacks with: " + npc.attack);
                player.getPacketSender().sendString(60000 + 52, "Weakness: " + npc.weakness);
                player.getPacketSender().sendString(60000 + 53, "Health: " + NpcDefinition.forId(npc.npcId).getHitpoints());
                player.getPacketSender().sendString(60000 + 54, "Max hit " + NpcDefinition.forId(npc.npcId).getMaxHit());
                int playerCount = 0;
                for (Player p : World.getPlayers()) {
                    if (p == null || npc == null || p.getLocation() == null || npc.location == null) {
                        continue;
                    }
                    if (p.getLocation() == npc.location) {
                        playerCount++;
                    }
                }
                player.getPacketSender().sendString(60000 + 55, "Players here: N/A");
                player.getPacketSender().sendString(60000 + 47, npc.name);
                player.getPacketSender().sendNpcOnInterface(60000 + 62, npc.npcId, npc.adjustedZoom != 0 ? npc.adjustedZoom : 0);
                sendDrops(player, npc.npcId);

            } else if (selectedTab == 1) {
                Monsters npc = Monsters.values()[selectedIndex];
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 51, "Attacks with: " + npc.attack);
                player.getPacketSender().sendString(60000 + 52, "Weakness: " + npc.weakness);
                player.getPacketSender().sendString(60000 + 53, "Health: " + NpcDefinition.forId(npc.npcId).getHitpoints());
                player.getPacketSender().sendString(60000 + 54, "Max hit " + NpcDefinition.forId(npc.npcId).getMaxHit());
                int playerCount = 0;
                for (Player p : World.getPlayers()) {
                    if (p == null || npc == null || p.getLocation() == null || npc.location == null) {
                        continue;
                    }
                    if (p.getLocation() == npc.location) {
                        playerCount++;
                    }
                }
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 47, npc.name);
                player.getPacketSender().sendNpcOnInterface(60000 + 62, npc.npcId, npc.adjustedZoom != 0 ? npc.adjustedZoom : 0);
                player.getPacketSender().sendString(60000 + 55, "Players here: " + playerCount);
                sendDrops(player, npc.npcId);
            } else if (selectedTab == 2) {
                Minigames npc = Minigames.values()[selectedIndex];
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 51, "Attacks with: " + npc.attack);
                player.getPacketSender().sendString(60000 + 52, "Weakness: " + npc.weakness);
                player.getPacketSender().sendString(60000 + 53, "Health: " + NpcDefinition.forId(npc.npcId).getHitpoints());
                player.getPacketSender().sendString(60000 + 54, "Max hit " + NpcDefinition.forId(npc.npcId).getMaxHit());
                int playerCount = 0;
                for (Player p : World.getPlayers()) {
                    if (p == null || npc == null || p.getLocation() == null || npc.location == null) {
                        continue;
                    }
                    if (p.getLocation() == npc.location) {
                        playerCount++;
                    }
                }
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 47, npc.name);
                player.getPacketSender().sendNpcOnInterface(60000 + 62, npc.npcId, npc.adjustedZoom != 0 ? npc.adjustedZoom : 0);
                player.getPacketSender().sendString(60000 + 55, "Players here: " + playerCount);
                sendDrops(player, npc.npcId);
            } else if (selectedTab == 3) {
                Dungeons npc = Dungeons.values()[selectedIndex];
                player.getPacketSender().sendString(60000 + 50, "Tier: " + 0);
                player.getPacketSender().sendString(60000 + 51, "Attacks with: " + npc.attack);
                player.getPacketSender().sendString(60000 + 52, "Weakness: " + npc.weakness);
                player.getPacketSender().sendString(60000 + 53, "Health: " + NpcDefinition.forId(npc.npcId).getHitpoints());
                player.getPacketSender().sendString(60000 + 54, "Max hit " + NpcDefinition.forId(npc.npcId).getMaxHit());
                int playerCount = 0;
                for (Player p : World.getPlayers()) {
                    if (p == null || npc == null || p.getLocation() == null || npc.location == null) {
                        continue;
                    }
                    if (p.getLocation() == npc.location) {
                        playerCount++;
                    }
                }
                player.getPacketSender().sendString(60000 + 47, npc.name);
                player.getPacketSender().sendNpcOnInterface(60000 + 62, npc.npcId, npc.adjustedZoom != 0 ? npc.adjustedZoom : 0);
                player.getPacketSender().sendString(60000 + 55, "Players here: " + playerCount);

                sendDrops(player, npc.npcId);
            } else if (selectedTab == 4) {
                Cities npc = Cities.values()[selectedIndex];
                player.getPacketSender().sendString(60000 + 50, "Tier: " + npc.tier);
                player.getPacketSender().sendString(60000 + 51, "Attacks with: " + npc.attack);
                player.getPacketSender().sendString(60000 + 52, "Weakness: " + npc.weakness);
                player.getPacketSender().sendString(60000 + 53, "Health: " + NpcDefinition.forId(npc.npcId).getHitpoints());
                player.getPacketSender().sendString(60000 + 54, "Max hit " + NpcDefinition.forId(npc.npcId).getMaxHit());
                int playerCount = 0;
                for (Player p : World.getPlayers()) {
                    if (p == null || npc == null || p.getLocation() == null || npc.location == null) {
                        continue;
                    }
                    if (p.getLocation() == npc.location) {
                        playerCount++;
                    }
                }
                player.getPacketSender().sendString(60000 + 47, npc.name);
                player.getPacketSender().sendNpcOnInterface(60000 + 62, npc.npcId, npc.adjustedZoom != 0 ? npc.adjustedZoom : 0);
                player.getPacketSender().sendString(60000 + 55, "Players here: " + playerCount);
            }
        }

    }

    public void teleport() {
        if (selectedIndex == -1) {
            player.sendMessage("@red@You must choose a teleport first.");
            return;
        }
        if (selectedTab == 0) {
            Bosses npc = Bosses.values()[selectedIndex];

            if (npc.name.toLowerCase().contains("lucifer")) {
                if ((player.isUnlockedLucifers() &&
                        player.getPointsHandler().getMiniLuciferkillcount() >= 10_000 ) || player.getRights() == PlayerRights.DEVELOPER) {
                    TeleportHandler.teleportPlayer(player, npc.position.copy().setZ(player.getIndex() * 4), player.getSpellbook().getTeleportType());
                    boolean contains = false;
                    for (NPC others : World.getNpcs()) {
                        if (others == null) {
                            continue;
                        }

                        if (others.getId() == 9012 && others.getPosition().getRegionId() == 7760 &&
                                others.getPosition().getZ() == (player.getIndex() * 4)) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        NPC npc_ = new NPC(9012, new Position(1952, 5146, player.getIndex() * 4));
                        npc_.setSpawnedFor(player);
                        World.register(npc_);
                    }
                } else {
                    player.sendMessage("You need to have killed 10k Mini Lucifers to go here.");
                }
                return;
            } else if (npc.name().equalsIgnoreCase("dark_supreme")) {
                if (!player.isUnlockedDarkSupreme() && !player.getRights().isDeveloperOnly()) {
                    Item[] requirements = new Item[]{new Item(5011), new Item(12537), new Item(17013)};
                    if (player.getInventory().containsAll(requirements)) {
                        player.getInventory().deleteItemSet(requirements);
                        player.setUnlockedDarkSupreme(true);
                        player.sendMessage("@red@Congratulations, you have unlocked dark supreme's zone!");
                    } else {
                        player.sendMessage("You do not have the requirements to unlock dark supreme's!");
                        player.sendMessage("You need to sacrifice a Light twisted bow, vitur scythe and sang. staff!!");
                        player.sendMessage("@red@Try again with these items in your inventory!");
                    }
                } else {
                    TeleportHandler.teleportPlayer(player, npc.position.copy().setZ(player.getIndex() * 4), player.getSpellbook().getTeleportType());
                    boolean contains = false;
                    for (NPC others : World.getNpcs()) {
                        if (others == null) {
                            continue;
                        }

                        if (others.getId() == 438 && others.getPosition().getRegionId() == 12102 &&
                                others.getPosition().getZ() == (player.getIndex() * 4)) {
                            contains = true;
                        }
                    }
                    if (!contains) {
                        NPC npc_ = new NPC(438, new Position(3040, 4516, player.getIndex() * 4));
                        npc_.setSpawnedFor(player);
                        World.register(npc_);
                    }
                }
            } else {
                TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
            }
        } else if (selectedTab == 1) {
            Monsters npc = Monsters.values()[selectedIndex];
            if (npc.name.equalsIgnoreCase("Mini Lucifers")) {
                if (!player.isUnlockedLucifers()) {
                    Item[] requirements = new Item[]{new Item(ItemDefinition.TOKEN_ID, 25_000_000), new Item(20400, 1),
                            new Item(18823, 3), new Item(19888, 3)};
                    if (player.getInventory().containsAll(requirements)) {
                        player.getInventory().deleteItemSet(requirements);
                        player.setUnlockedLucifers(true);
                        player.sendMessage("@red@Congratulations, you have unlocked lucifer's zone!");
                    } else {
                        player.sendMessage("You do not have the requirements to unlock lucifer's!");
                        player.sendMessage("You need an enraged cape + 3 coll rings II, 3 colls necks II to sacrifice!!");
                        player.sendMessage("@red@Try again with these items in your inventory!");
                    }
                } else {
                    TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
                }
            } else {
                TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
            }
        } else if (selectedTab == 2) {
            Minigames npc = Minigames.values()[selectedIndex];
            if (npc == Minigames.PROGRESSION_ZONE) {
                ProgressionZone.teleport(player);
                return;
            }
            TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
        } else if (selectedTab == 3) {
            Dungeons npc = Dungeons.values()[selectedIndex];
            TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
        } else if (selectedTab == 4) {
            Cities npc = Cities.values()[selectedIndex];
            TeleportHandler.teleportPlayer(player, npc.position, player.getSpellbook().getTeleportType());
        }
    }

    public void switchTab(int index) {
        selectedTab = index;
        selectedIndex = 0;
        player.getPacketSender().sendString(60000 + 46, getTabName(index));
        updateInterface(false);
        updateInterface(true);
    }

    private String getTabName(int index) {

        if (index == 1) {
            return "Monsters";
        } else if (index == 2) {
            return "Minigames";
        } else if (index == 3) {
            return "Dungeons";
        } else if (index == 4) {
            return "Cities";
        } else {
            return "Bosses";
        }

    }

    public boolean handleButton(int buttonId) {
        // System.out.println("here");
        if (!(buttonId <= -5370 && buttonId >= -5384)) {
            return false;
        }
        int index = buttonId + 5384;


        selectedIndex = index;
        updateInterface(true);

        return true;
    }

    public enum Bosses {

        ABBADON(ID_START, 1000, 252, "Frieza", "Melee", "Range/Melee", new Position(2516, 3042, 0), Locations.Location.EXODENLOC, 6),
        INFERNAL_GROUDON(ID_START + 1, 1000, 449, "Perfect Cell", "Range", "Melee", new Position(3000, 2511, 0), Locations.Location.EXODENLOC, 6),
        BAPHOMET(ID_START + 2, 1100, 452, "Super Buu", "Magic", "Range", new Position(3342, 3022, 0), Locations.Location.EXODENLOC, 6),
        BROLY(ID_START + 3, 3500, 2949, "Scarlet Falcon", "Melee", "Magic", new Position(3869, 2776, 0), Locations.Location.EXODENLOC, 7),
        GOKU(ID_START + 4, 4200, 2950, "Herbal Rogue", "Range", "Magic", new Position(3044, 2969, 0), Locations.Location.EXODENLOC, 7),
        DARTH_VADER(ID_START + 5, 3700, 3831, "Azure Beast", "Magic", "Range", new Position(2924, 2842, 0), Locations.Location.EXODENLOC, 7),

        LUCIFER(ID_START + 6, 1000, 185, "Joker", "Melee", "Magic/Range", new Position(1807, 3211, 0), Locations.Location.EXODENLOC, 7),

        SEPHIROTH(ID_START + 7, 1250, 6430, "Crystal Queen", "Melee", "Magic/Range", new Position(2867, 9950, 0), Locations.Location.EXODENLOC, 7),

        CLOUD(ID_START + 8, 1250, 440, "Supreme Boss", "Melee", "Magic/Range", new Position(2403, 5082, 1000), Locations.Location.EXODENLOC, 9),

        THANOS(ID_START + 9, 3500, 1120, "Vasa Nistirio", "Magic", "Range", new Position(2910, 2593, 0), Locations.Location.EXODENLOC, 10),

        JAMESTHEBOSS(ID_START + 10, 2000, 8015, "Elite Dragon", "Magic", "Melee/Range", new Position(2719, 9442, 0), Locations.Location.EXODENLOC, 10),

        Galvik(ID_START + 11, 1850, 4540, "Mega Avatar", "Melee", "Range", new Position(2360, 4952, 0), Locations.Location.EXODENLOC, 11),

        InfernalDemon(ID_START + 12, 3000, 12810, "Infernal Demon", "Magic/Melee", "Range", new Position(2357, 9904, 0), Locations.Location.EXODENLOC, 11),

        Lucifer(ID_START + 13, 1000, 9012, "Lucifer", "Magic/Melee", "Range", new Position(1941, 5152, 0), Locations.Location.EXODENLOC, 12),
        DARK_SUPREME(ID_START + 14, 900, 438, "Dark Supreme @cya@[NEW]", "Magic/Melee/Range", "All", new Position(3036, 4503, 0), Locations.Location.EXODENLOC, 12);

        // Gundam(ID_START + 12, 1000, 2518, "Gundam", "Melee", "Melee/Magic", new Position(2792, 3791, 0), Locations.Location.EXODENLOC, 12);


        private final int id, npcId, tier, adjustedZoom;
        private final String name, attack, weakness;
        private final Position position;
        private final Locations.Location location;

        Bosses(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location, int tier) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
            this.tier = tier;
        }

    }

    public enum Monsters {


        PARAVanity(ID_START, 1250, 1265, "Solak Lion", "Melee", "Mage", new Position(2733, 4765, 0), Locations.Location.EXODENLOC, 1),
        MEWTWO(ID_START + 1, 1000, 1023, "Dark Bloodveld", "Melee", "Mage", new Position(2397, 2844, 0), Locations.Location.EXODENLOC, 1),
        GHOST(ID_START + 2, 2000, 1233, "Rusted Gargoyle", "Range", "Melee", new Position(2401, 2914, 0), Locations.Location.EXODENLOC, 1),
        SLIMER(ID_START + 3, 1300, 1234, "Crazy Witch", "Magic", "Range", new Position(2463, 2913, 0), Locations.Location.EXODENLOC, 1),
        TOXICVanity(ID_START + 4, 1500, 13479, "Revenants", "Melee", "Range/Mage", new Position(3652, 3486, 0), Locations.Location.EXODENLOC, 2),
        MARIO(ID_START + 5, 2500, 13747, "Ice Demon", "Melee", "Range/Mage", new Position(2764, 9191, 0), Locations.Location.EXODENLOC, 2),
        PIKACHU(ID_START + 6, 500, 12343, "Predator", "Magic", "Melee", new Position(2911, 3613, 0), Locations.Location.EXODENLOC, 3),
        RICK(ID_START + 7, 2700, 12886, "Cyantrix", "Melee", "Melee/Range", new Position(2397, 3487, 0), Locations.Location.EXODENLOC, 3),
        GRAND(ID_START + 8, 2700, 10103, "Bulwark", "Melee", "Range/Mage", new Position(2422, 3523, 0), Locations.Location.EXODENLOC, 4),
        GOLDEN(ID_START + 9, 1150, 606, "Prestige Warriors", "Melee", "Melee/Range", new Position(2455, 2863, 0), Locations.Location.EXODENLOC, 4),
        MINI_LUCIFER(ID_START + 10, 1150, 9011, "Mini Lucifers", "Melee", "Melee/Range", new Position(1941, 5152, 0), Locations.Location.EXODENLOC, 5);


        private final int id;
        private final int npcId;
        private final int tier;
        private final int adjustedZoom;
        private final String name, attack, weakness;
        private final Position position;
        private final Locations.Location location;

        Monsters(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location, int tier) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
            this.tier = tier;
        }
    }

    public enum Minigames {


        PROGRESSION_ZONE(ID_START, 1750, 9001, "Starter Progression", "Melee", "Any", new Position(2649, 4021, 0), Locations.Location.PROGRESSION_ZONES, 1),
        RAIDS(ID_START + 1, 1750, 2017, "Raids @or1@[1]", "Melee/Magic/Range", "Melee/Range", new Position(2649, 4020, 0), Locations.Location.EXODENLOC, 10),
        RAIDS2(ID_START + 2, 3500, 587, "Raids @or2@[2]", "Melee/Magic", "Magic/Range/Melee", new Position(2553, 3717, 0), Locations.Location.EXODENLOC, 10),
        CASHZONE(ID_START + 3, 1000, 1851, "Key Minigame", "Melee/Range/Magic", "Melee/Range", new Position(2579, 2566, 0), Locations.Location.EXODENLOC, 4),
        SLAYER(ID_START + 4, 1000, 8011, "Events Minigame", "Melee/Magic/Range", "Melee/Range", new Position(2732, 3465, 0), Locations.Location.EXODENLOC, 3),
        PEST_CONTROLL(ID_START + 5, 1000, 131, "Penguin Minigame", "Melee", "Magic/Range", new Position(3039, 9545, 0), Locations.Location.PEST_CONTROL_GAME, 3),
        DICEZONE(ID_START + 6, 2500, 1850, "Dice Zone", "N/A", "N/A", new Position(2623, 4010, 0), Locations.Location.EXODENLOC),
        VOID_OF_DARKNESS(ID_START + 7, 1000, 9030, "Void of Darkness", "Melee/Magic/Range", "Melee/Range",VoidOfDarkness.TELEPORT_AREA, Locations.Location.VOID_OF_DARKNESS, 10);
        //  BRUTAL_HANK(ID_START + 7, 1000, 8009, "Brutal Hulk", "Magic/Melee", "Range/Magic", new Position(2858, 2710, 0), Locations.Location.EXODENLOC, 13),
        //   FRIEZA(ID_START + 8, 1000, 2132, "Dragonball [Frieza]", "Melee", "Range", new Position(2523, 2703, 0), Locations.Location.EXODENLOC, 13);


        private int id, npcId, tier, adjustedZoom;
        private String name, attack, weakness;
        private Position position;
        private Locations.Location location;

        Minigames(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
        }

        Minigames(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location, int tier) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
            this.tier = tier;
        }
    }

    public enum Dungeons {

        Strykwyrm(ID_START, 2500, 9463, "Strykwyrm Cavern", "Melee", "Magic", new Position(2731, 5095, 0), Locations.Location.EXODENLOC),
        Ancient(ID_START + 1, 1000, 750, "Ancient Cavern", "Melee", "Magic", new Position(1746, 5325, 0), Locations.Location.EXODENLOC),
        Chaos(ID_START + 2, 1000, 90, "Chaos Tunnels", "Melee", "Magic", new Position(3184, 5471, 0), Locations.Location.EXODENLOC),
        EdgevilleDUNG(ID_START + 3, 1000, 1856, "Edgeville Dungeon", "Melee", "Magic", new Position(3097, 9870, 0), Locations.Location.EXODENLOC),
        Betrayed(ID_START + 4, 1000, 1855, "<img=15>Betrayed Dungeon", "Melee", "Magic", new Position(1859, 5240, 0), Locations.Location.EXODENLOC),
        Damned(ID_START + 5, 1000, 1853, "<img=15>Damned Dungeon", "Melee", "Magic", new Position(2041, 5243, 0), Locations.Location.EXODENLOC),
        Hidden(ID_START + 6, 1000, 1854, "<img=15>Hidden Dungeon", "Melee", "Magic", new Position(2125, 5255, 0), Locations.Location.EXODENLOC),
        Cursed(ID_START + 7, 1000, 1652, "<img=15>Cursed Dungeon", "Melee", "Magic", new Position(2360, 5212, 0), Locations.Location.EXODENLOC),
        Slayertower(ID_START + 8, 1750, 55, "Slayer Tower", "Melee", "Magic", new Position(3429, 3538, 0), Locations.Location.EXODENLOC),
        Taverly(ID_START + 9, 1000, 1593, "Taverly Dungeon", "Melee", "Magic", new Position(2884, 9797, 0), Locations.Location.EXODENLOC),
        Brimhaven(ID_START + 10, 1000, 5393, "Brimhaven Dungeon", "Melee", "Magic", new Position(2713, 9564, 0), Locations.Location.EXODENLOC);
        // Fishing(ID_START + 12, 1000, 1263, "Fishing", "Melee", "Magic", new Position(2345, 3698, 0), Locations.Location.EXODENLOC);

        private int id, npcId, adjustedZoom;
        private String name, attack, weakness;
        private Position position;
        private Locations.Location location;

        Dungeons(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
        }

    }

    public enum Cities {

        Varrock(ID_START, 1000, 1, "Varrock", "N/A", "N/A", new Position(3210, 3424, 0), Locations.Location.VARROCK),
        LUMBRIDGE(ID_START + 1, 1000, 1, "Lumbridge", "N/A", "N/A", new Position(3222, 3218, 0), Locations.Location.VARROCK),
        EDGEVILLE(ID_START + 2, 1000, 1, "Edgeville", "N/A", "N/A", new Position(3087, 3495, 0), Locations.Location.VARROCK),
        FALADOR(ID_START + 3, 1000, 1, "Falador", "N/A", "N/A", new Position(2964, 3378, 0), Locations.Location.VARROCK),
        CAMELOT(ID_START + 4, 1000, 1, "Camelot", "N/A", "N/A", new Position(2757, 3477, 0), Locations.Location.VARROCK),
        ARDOUGNE(ID_START + 5, 1000, 1, "Ardougne", "N/A", "N/A", new Position(2662, 3305, 0), Locations.Location.VARROCK),
        YANILLE(ID_START + 6, 1000, 1, "Yanille", "N/A", "N/A", new Position(2614, 3102, 0), Locations.Location.VARROCK);

        private int id, npcId, tier, adjustedZoom;
        private String name, attack, weakness;
        private Position position;
        private Locations.Location location;

        Cities(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
        }

        Cities(int id, int adjustedZoom, int npcId, String name, String attack, String weakness, Position position, Locations.Location location, int tier) {
            this.adjustedZoom = adjustedZoom;
            this.id = id;
            this.npcId = npcId;
            this.name = name;
            this.attack = attack;
            this.weakness = weakness;
            this.position = position;
            this.location = location;
            this.tier = tier;
        }
    }
}
