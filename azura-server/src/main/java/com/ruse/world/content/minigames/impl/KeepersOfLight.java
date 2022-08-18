package com.ruse.world.content.minigames.impl;

import com.ruse.GameSettings;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.dailytasks_new.DailyTask;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class KeepersOfLight {

    public static Box[] loot = { //
            new Box(23134, 1, 1D, true),
            new Box(23135, 1, 1D, true),
            new Box(23136, 1, 1D, true),
            new Box(23137, 1, 1D, true),
            new Box(23138, 1, 1D, true),
    };

    public static final Position BANKING_AREA = new Position(2322, 5028,0);
    public static final Position LOBBY_AREA = new Position(2322, 5031,0);
    public static final Position GAME_AREA = new Position(2399, 5040,0);

    public static final int LIGHT_TICKET = 621;

    public static final int WAIT_TIMER = 20;

    public static int waitTimer = WAIT_TIMER;

    public static int TOTAL_PLAYERS = 0;
    private static int PLAYERS_WAITING = 0;
    public static final int MIN_PLAYERS = 3;

    /**
     * @note States of minigames
     */
    public static final String PLAYING = "PLAYING";
    public static final String WAITING = "WAITING";

    /**
     * Is a game running?
     */
    private static boolean gameRunning;

    /**
     * @note Stores player and State
     */
    private static Map<Player, String> playerMap = new HashMap<Player, String>();

    /*
     * Stores npcs
     */
    private static CopyOnWriteArrayList<NPC> npcList = new CopyOnWriteArrayList<NPC>();

    private static NPC[] BOSSES = new NPC[3];

    /**
     * @return HashMap Value
     */
    public static String getState(Player player) {
        return playerMap.get(player);
    }

    //boardBoat
    public static void insertWaiting(Player p) {
        if (p.getSummoning().getFamiliar() != null) {
            p.getPacketSender().sendMessage("Familiars are not allowed on the boat.");
            return;
        }
        if (p.getSkillManager().getCombatLevel() < 30) {
            p.getPacketSender().sendMessage("You must have a combat level of at least 30 to play this minigame.");
            return;
        }
        if (getState(p) == null) {
            playerMap.put(p, WAITING);
            TOTAL_PLAYERS++;
            PLAYERS_WAITING++;
        }
        p.getSession().clearMessages();
        p.moveTo(LOBBY_AREA);
        p.getMovementQueue().setLockMovement(false).reset();
        p.getPacketSender().sendWalkableInterface(21005, true);
    }

    public static void removeWaiting(Player p, boolean fromList) {
        final String state = getState(p);
        if (state != null) {
            if (fromList) {
                playerMap.remove(p);
            }
            TOTAL_PLAYERS--;
            if (state == WAITING) {
                PLAYERS_WAITING--;
            }
        }
        p.getPacketSender().sendInterfaceRemoval();
        p.getPacketSender().sendWalkableInterface(21005, false);
        p.getSession().clearMessages();
        p.moveTo(BANKING_AREA);
        p.getMovementQueue().setLockMovement(false).reset();
    }

    public static void sequence() {
        if (TOTAL_PLAYERS == 0 && !gameRunning)
            return;
        updateWaitingInterface();
        if (waitTimer > 0)
            waitTimer--;
        if (waitTimer <= 0) {
            if (!gameRunning)
                startGame();
            else {
                for (Player p : playerMap.keySet()) {
                    if (p == null)
                        continue;
                    String state = getState(p);
                    if (state != null && state.equals(WAITING)) {
                        p.getPacketSender().sendMessage(
                                "A new Keepers of the light game will be started once the current one has finished.");
                    }
                }
            }
            waitTimer = WAIT_TIMER;
        } else if (waitTimer > 0 && (waitTimer % 15) == 0) {
            for (Player p : playerMap.keySet()) {
                if (p == null)
                    continue;
                String state = getState(p);
                if (state != null && state.equals(WAITING)) {
                    p.getPacketSender().sendMessage(
                            "A new Keepers of the light game will be starting in 10 seconds!");
                }
            }
        }
        if (gameRunning) {
            updateIngameInterface();
            processNPCs();
            if (allBossesDead() || allPlayersDead()) {
                endGame(true);
                waitTimer = WAIT_TIMER;
            }
        }
    }

    private static void updateWaitingInterface() {
        for (Player p : playerMap.keySet()) {
            if (p == null)
                continue;
            String state = getState(p);
            if (state != null && state.equals(WAITING)) {
                p.getPacketSender().sendString(21006, "Next Game: " + waitTimer + "");
                p.getPacketSender().sendString(21007, "Players Ready: " + PLAYERS_WAITING + "");
                p.getPacketSender().sendString(21008, "(Need at least " + MIN_PLAYERS + " players)");
                p.getPacketSender().sendString(21009, "");


            }
        }
    }

    private static boolean allBossesDead() {
        int count = 0;
        for (int i = 0; i < BOSSES.length; i++) {
            if (BOSSES[i] != null) {
                if (BOSSES[i].getConstitution() <= 0 || BOSSES[i].isDying()) {
                    count++;
                }
            }
        }
        return count >= 3;
    }

    private static boolean allPlayersDead() {
        for (Player p : playerMap.keySet()) {
            if (p == null)
                continue;
            String state = getState(p);
            if (state != null && state.equals(PLAYING)) {
                return false;
            }
        }

        return true;
    }

    private static void processNPCs() {
        for (NPC npc : npcList) {
            if (npc == null)
                continue;

            //TODO ? any npcs
        }
    }

    private static final int damageNeeded = 50000;

    private static void updateIngameInterface() {
        //600000+150000+98800=848800
        for (Player p : playerMap.keySet()) {
            if (p == null)
                continue;
            String state = getState(p);
            if (state != null && state.equals(PLAYING)) {
                String prefix = p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() == 0 ? "@red@"
                        : p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() < damageNeeded ? "@yel@"
                        : "@gre@";
                p.getPacketSender().sendString(21006, prefix + "Your damage : "
                        + p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() + "/" + damageNeeded);
                p.getPacketSender().sendString(21007, "");
                p.getPacketSender().sendString(21008, "");
                p.getPacketSender().sendString(21009, "");
            }
        }
    }

    private static void startGame() {
        boolean startGame = !gameRunning && PLAYERS_WAITING >= MIN_PLAYERS;
        if (startGame) {
            gameRunning = true;
            spawnMainNPCs();
        }
        for (Player player : playerMap.keySet()) {
            if (player != null) {
                System.out.println(player.getUsername());
                String state = getState(player);
                if (state != null && state.equals(WAITING)) {
                    if (startGame) {
                        movePlayerToIsland(player);
                        playerMap.put(player, PLAYING);
                    } else
                        player.getPacketSender()
                                .sendMessage("There must be at least 3 players waiting before a game can start.");
                }
            }
        }
    }

    private static void movePlayerToIsland(Player p) {
        p.getPacketSender().sendInterfaceRemoval();
        p.getSession().clearMessages();
        p.moveTo(GAME_AREA);
        p.getMovementQueue().setLockMovement(false).reset();
        PLAYERS_WAITING--;
    }

    private static void endGame(boolean won) {
        for (Iterator<Player> it = playerMap.keySet().iterator(); it.hasNext();) {
            Player p = it.next();
            if (p == null)
                continue;
            String state = getState(p);
            if (state != null && state.equals(PLAYING)) {
                long time = System.currentTimeMillis();
                leave(p, true);
                long timeTook = System.currentTimeMillis() - time;
                System.out.println("removing player took " + timeTook + "ms");

                if (won && p.getMinigameAttributes().getPestControlAttributes().getDamageDealt() >= damageNeeded) {
                    p.getPacketSender().sendMessage("The bosses were successfully defeated. You've been rewarded for your effort.");
                    time = System.currentTimeMillis();
                    PlayerPanel.refreshPanel(p);
                    timeTook = System.currentTimeMillis() - time;
                    System.out.println("player panel refresh took " + timeTook + "ms");
                    int bonus = p.getRights().getKeeperOfLightsBonusPoints(p);


                    time = System.currentTimeMillis();
                    int amount = 0;
                    if(GameSettings.DOUBLE_KOL) {
                        amount = 50 + (bonus * 2);
                      } else {
                        amount = 25 + bonus;
                    }
                    p.getInventory().add(LIGHT_TICKET, amount);
                    p.getPacketSender().sendMessage("You've received " + amount + " Light tickets.");
                    timeTook = System.currentTimeMillis() - time;
                    System.out.println("Light tickets took " + timeTook + "ms");

                    time = System.currentTimeMillis();
                    Achievements.doProgress(p, Achievements.Achievement.COMPLETE_5_KEEPERS_OF_LIGHT);
                    Achievements.doProgress(p, Achievements.Achievement.COMPLETE_15_KEEPERS_OF_LIGHT);
                    Achievements.doProgress(p, Achievements.Achievement.COMPLETE_50_KEEPERS_OF_LIGHT);
                    DailyTask.KEEPERS_OF_LIGHT.tryProgress(p);
                    timeTook = System.currentTimeMillis() - time;
                    System.out.println("The achievements took " + timeTook + "ms");

                    time = System.currentTimeMillis();
                    p.restart();
                    timeTook = System.currentTimeMillis() - time;
                    System.out.println("Player restart took " + timeTook + "ms");

                } else if (won)
                    p.getPacketSender().sendMessage("You didn't participate enough to receive a reward.");

                else {
                    p.getPacketSender().sendMessage("You failed to kill all the bosses.");
                }
                p.getMinigameAttributes().getPestControlAttributes().setDamageDealt(0);
            }
        }
        playerMap.entrySet().removeIf(entry -> (PLAYING.equalsIgnoreCase(entry.getValue())));

        for (NPC n : npcList) {
            if (n == null || !n.isRegistered())
                continue;
            if (n.getLocation() == Locations.Location.KEEPERS_OF_LIGHT_GAME) {
                World.deregister(n);
                //n = null;
            }
        }
        npcList.clear();
        for (int i = 0; i < BOSSES.length; i++)
            BOSSES[i] = null;
        gameRunning = false;
    }

    private static void spawnMainNPCs() {

        BOSSES[0] = spawnPCNPC(9835, new Position(2377, 5036, 0));
        BOSSES[1] = spawnPCNPC(9836, new Position(2398, 5017, 0));
        BOSSES[2] = spawnPCNPC(9837, new Position(2385, 5009, 0));
        for (NPC n : BOSSES) {
            int hp = n.getDefinition().getHitpoints();
            int multiple = (int) Math.ceil(playerMap.size() / 2D);
            int mHp = hp * (multiple);
            n.setConstitution(mHp);
            n.setDefaultConstitution(mHp);

            npcList.add(n);
        }
    }

    public static NPC spawnPCNPC(int id, Position pos) {
        NPC np = new NPC(id, pos);
        World.register(np);
        return np;
    }

    public static void leave(Player p, boolean fromList) {
        final String state = getState(p);
        if (state != null) {
            if (fromList) {
                playerMap.remove(p);
            }
            TOTAL_PLAYERS--;
            if (state == WAITING) {
                PLAYERS_WAITING--;
            }
            p.getPacketSender().sendInterfaceRemoval();
            p.getPacketSender().sendWalkableInterface(21005, false);
            p.getSession().clearMessages();
            p.moveTo(BANKING_AREA);
            p.getMovementQueue().setLockMovement(false).reset();
        }
    }
}
