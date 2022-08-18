package com.ruse.world;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.MessageType;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.util.CharacterBackup;
import com.ruse.util.NameUtils;
import com.ruse.webhooks.discord.DiscordMessager;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.StaffList;
import com.ruse.world.content.WorldBosses;
import com.ruse.world.content.discordbot.Bot;
import com.ruse.world.content.minigames.impl.FightPit;
import com.ruse.world.content.minigames.impl.KeepersOfLight;
import com.ruse.world.content.minigames.impl.PestControl;
import com.ruse.world.content.randomevents.EvilTree;
import com.ruse.world.content.randomevents.ShootingStar;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.EntityHandler;
import com.ruse.world.entity.impl.CharacterList;
import com.ruse.world.entity.impl.GlobalItemSpawner;
import com.ruse.world.entity.impl.mini.MiniPlayer;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerHandler;
import com.ruse.world.entity.updating.NpcUpdateSequence;
import com.ruse.world.entity.updating.PlayerUpdateSequence;
import com.ruse.world.entity.updating.UpdateSequence;
import com.server.service.login.LoginService;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;

//import com.ruse.net.packet.impl.StaffList;

/**
 * @author Gabriel Hannason Thanks to lare96 for help with parallel updating
 * system
 */
public class World {

    /**
     * All of the registered players.
     */
    private static final CharacterList<Player> players = new CharacterList<>(GameSettings.playerCharacterListCapacity);
    private static final Map<String, Player> playersByUesrname = new ConcurrentHashMap<>();

    /**
     * All of the registered NPCs.
     */
    private static final CharacterList<NPC> npcs = new CharacterList<>(GameSettings.npcCharacterListCapacity);

    /**
     * Used to block the game thread until updating has completed.
     */
    private static final Phaser synchronizer = new Phaser(1);

    /**
     * A thread pool that will update players in parallel.
     */
    private static final ExecutorService updateExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(Thread.MAX_PRIORITY).build());

    /**
     * The queue of {@link Player}s waiting to be logged in.
     **/
    private static final Queue<Player> logins = new ConcurrentLinkedQueue<>();

    /**
     * The queue of {@link Player}s waiting to be logged out.
     **/
    private static final Queue<Player> logouts = new ConcurrentLinkedQueue<>();

    /**
     * The queue of {@link Player}s waiting to be given their vote reward.
     **/
    private static final Queue<Player> voteRewards = new ConcurrentLinkedQueue<>();


    public static void register(Entity entity) {
        EntityHandler.register(entity);
    }


    public static void register(Player c, NPC entity) {
        if (c != null && c.getRegionInstance() != null) {
            EntityHandler.register(entity);
            c.getRegionInstance().getNpcsList().add(entity);
        }
    }

    public static void deregister(Entity entity) {
        EntityHandler.deregister(entity);
    }

    public static Player getPlayerByName(String username) {
        Player p = playerByNames.get(NameUtils.stringToLong(username));

        // If the player is disconnected but the session is still in our list
        if (p instanceof MiniPlayer) {
            return null;
        }
        if(p != null && (!p.getSession().getChannel().isConnected() || !p.isRegistered())) {
            playerByNames.remove((long)p.getLongUsername(), p);
            removePlayer(p);
            return null;
        }


        return playerByNames.get(NameUtils.stringToLong(username));
    }

    public final static LoginService LOGIN_SERVICE = new LoginService();
    private final static ConcurrentLinkedQueue<Function0<Unit>> gameThreadJobs = new ConcurrentLinkedQueue<>();

    public static void sendMessage(String message) {
        if (message.contains("[Yell]")) {
            DiscordMessager.sendYellMessage(message);
        } else if (message.contains("10 more players have just voted")) {
            DiscordMessager.sendInGameMessage("10 more players have just voted.");
        } else {
            DiscordMessager.sendInGameMessage(message);
        }
        players.forEach(p -> p.getPacketSender().sendMessage(message));
    }


    public static void sendYellMessage(String message) {
        for (Player p : players) {
            if (p == null)
                continue;
                p.getPacketSender().sendMessage(message);
        }
    }

    public static void sendNewsMessage(String message) {
        players.forEach(p -> p.getPacketSender().sendMessage("<img=5> @blu@News: @or2@" + message));
    }

    public static void sendMessage1(String message) {
        players.forEach(p -> p.getPacketSender().sendMessage("<img=5> " + message));
    }


    public static void sendMessage(MessageType type, String message) {
        players.forEach(p -> p.getPacketSender().sendMessage(type, message));
        if (message.contains("[Yell]")) {
            DiscordMessager.sendYellMessage(message);
        } else if (message.contains("logged in for the first time")) {
            DiscordMessager.sendStaffMessage(message);
        } else {
            DiscordMessager.sendInGameMessage(message);
        }
    }

    public static void sendFilteredMessage(String message) {
        players.stream().filter(p -> p != null && (!p.toggledGlobalMessages()))
                .forEach(p -> p.getPacketSender().sendMessage(message));
    }

    public static void sendStaffMessage(String message) {
        players.stream().filter(p -> p != null && (p.getRights().isStaff()))
                .forEach(p -> p.getPacketSender().sendMessage(message));
        DiscordMessager.sendStaffMessage(message);
    }

    public static void sendOwnerDevMessage(String message) {
        players.stream().filter(
                p -> p != null && (p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.DEVELOPER))
                .forEach(p -> p.getPacketSender().sendMessage(message));
        DiscordMessager.sendDebugMessage("[Owner/Developer]\n" + message);
    }

    public static void sendGlobalGroundItems() {
        players.stream().filter(Objects::nonNull).forEach(GlobalItemSpawner::spawnGlobalGroundItems);
    }

    public static void updateStaffList() {
        TaskManager.submit(new Task(false) {
            @Override
            protected void execute() {
                players.forEach(StaffList::updateInterface);
                stop();
            }
        });
    }

    public static void updateServerTime() {
        // players.forEach(p -> p.getPacketSender().sendString(39161, "@or2@Server time:
        // @or2@[ @yel@"+Misc.getCurrentServerTime()+"@or2@ ]"));
    }

    public static void updatePlayersOnline() {
        updateStaffList();
    }

    public static void savePlayers() {
        players.forEach(Player::save);
    }

    public static CharacterList<Player> getPlayers() {
        return players;
    }

    public static CharacterList<NPC> getNpcs() {
        return npcs;
    }

    public static void sequence() {
        final boolean PRINT_TIMESTAMPS = true;
        long startTime = System.currentTimeMillis();
        long lastTime = startTime;

        gameThreadJobs.forEach(job -> {
            try {
                job.invoke();
            } catch (Exception e){
//                logger.severe("Error executing game-thread job. "+e.getMessage());
                e.printStackTrace();
            }
        });
        gameThreadJobs.clear();

        // Handle queued logins.
        for (int amount = 0; amount < GameSettings.LOGIN_THRESHOLD; amount++) {
            Player player = logins.poll();
            if (player == null)
                break;
            if (World.getPlayerByName(player.getUsername()) != null) {
                System.out.println("STOPPED MULTI LOG by " + player.getUsername());
                PlayerLogs.log(player.getUsername(), "Had a multilog attempt.");
                break;
            }
            if (playerByNames.containsKey(player.getLongUsername())) {
                player.dispose();
                continue;
            }
            PlayerHandler.handleLogin(player);
        }
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50)
                System.out.println("Login queues took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        // Handle queued logouts.
        int amount = 0;
        Iterator<Player> $it = logouts.iterator();
        while ($it.hasNext()) {
            Player player = $it.next();
            if (player == null || amount >= GameSettings.LOGOUT_THRESHOLD)
                break;
            if (PlayerHandler.handleLogout(player, false)) {
                $it.remove();
                amount++;
            }
        }
        // Handle queued vote rewards
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50)
                System.out.println("Logout queues took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        FightPit.sequence();

        WorldBosses.sequence();
        KeepersOfLight.sequence();
        PestControl.sequence();
        ShootingStar.sequence();
        EvilTree.sequence();
        Bot.updatePlayers();
        // Abyssector.initialize();
        //Wildywyrm.initialize();
        //SkeletalHorror.initialize();
        //TriviaSystem.tick();

        ServerPerks.getInstance().tick();
        CharacterBackup.sequence();

        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50)
                System.out.println("Content tickers took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        // First we construct the update sequences.
        UpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
        UpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();
        // Then we execute pre-updating code.
        players.forEach(playerUpdate::executePreUpdate);
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50)
                System.out.println("Player pre-updating took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        npcs.forEach(npcUpdate::executePreUpdate);
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime >= 50)
                System.out.println("Entities pre-updating took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        // Then we execute parallelized updating code.
        synchronizer.bulkRegister(players.size());
        players.forEach(playerUpdate::executeUpdate);
        synchronizer.arriveAndAwaitAdvance();
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50)
                System.out.println("Entities updating took: " + (System.currentTimeMillis() - lastTime) + " ms");
            lastTime = System.currentTimeMillis();
        }
        // Then we execute post-updating code.
        players.forEach(playerUpdate::executePostUpdate);
        npcs.forEach(npcUpdate::executePostUpdate);
        if (PRINT_TIMESTAMPS) {
            if (System.currentTimeMillis() - lastTime > 50) {
                System.out.println("Entities post-updating took: " + (System.currentTimeMillis() - lastTime) + " ms");
            }
            lastTime = System.currentTimeMillis();
            if (System.currentTimeMillis() - startTime > 50) {
                System.out.println("World ticking took: " + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    public static void submitGameThreadJob(@NotNull Function0<Unit> function) {
        gameThreadJobs.offer(function);
    }

    public static Queue<Player> getLoginQueue() {
        return logins;
    }

    public static Queue<Player> getLogoutQueue() {
        return logouts;
    }

    public static Queue<Player> getVoteRewardingQueue() {
        return voteRewards;
    }

    public static void addPlayer(Player player) {
        players.add(player);
        playersByUesrname.put(player.getUsername(), player);
    }

    public static void removePlayer(Player player) {
        players.remove(player);
        playersByUesrname.remove(player.getUsername());
    }

    private static Long2ObjectMap<Player> playerByNames = new Long2ObjectOpenHashMap<>();
    public static Long2ObjectMap<Player> playerMap() {
        return playerByNames;
    }

    public static ObjectArrayList<Entity> getNearbyEntities(final Position position, final int range) {
        ObjectArrayList<Entity> coll = new ObjectArrayList<>();
        coll.addAll(getNearbyPlayers(position,range));
        coll.addAll(getNearbyNPCs(position,range));
        return coll;
    }

    public static ObjectArrayList<Player> getNearbyPlayers(final Position position, final int range) {
        ObjectArrayList<Player> coll = new ObjectArrayList<>();
        for (Player p : getPlayers()) {
            if (p != null && p.getPosition().getZ() == position.getZ() && p.distanceToPoint(position.getX(), position.getY()) <= range) {
                coll.add(p);
            }
        }
        return coll;
    }

    public static ObjectArrayList<NPC> getNearbyNPCs(final Position position, final int range) {
        ObjectArrayList<NPC> coll = new ObjectArrayList<>();
        for (int i = 0; i < World.getNpcs().size(); i++) {
            NPC npc = World.getNpcs().get(i);
            if (npc != null && npc.getPosition().getZ() == position.getZ()&& npc.getPosition().withinDistance(position, range)) {
                coll.add(npc);
            }
        }
        return coll;
    }

    public static boolean npcIsRegistered(int id) {
        for (NPC n : getNpcs()) {
            if (n != null && n.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static void sendBroadcastMessage(String message) {
        for (Player p : players) {
            if (p == null)
                continue;
            p.getPacketSender().sendMessage("<col=ff0000>Server Message: <col=00ffff>" + message);
        }
    }
    public static void sendSummerMessage(String message) {
        for (Player p : players) {
            if (p == null)
                continue;
            p.getPacketSender().sendBroadCastMessage(message, 100);
            p.getPacketSender().sendMessage("<col=ffa412>Summer Event: <col=e00000>" + message);
        }
    }

}
