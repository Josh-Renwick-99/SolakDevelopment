package com.ruse.world.instance;

import com.ruse.GameSettings;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.EntityHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.instance.manager.InstanceManager;
import com.ruse.world.region.Region;
import com.ruse.world.region.dynamic.DynamicMap;
import com.ruse.world.region.dynamic.DynamicRegion;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a map instance in the world.
 *
 * @author Walied K. Yassen
 */
@Slf4j
@RequiredArgsConstructor
public abstract class MapInstance {

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * The players which are currently in the instance.
     */
    private final List<Player> players = new ArrayList<>();

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * The player name who started the party (groups)
     */
    private String owner;

    public DestroyMode getMode() {
        return mode;
    }

    public void setMode(DestroyMode mode) {
        this.mode = mode;
    }

    /**
     * The destroy mode of the map instance.
     */
    @NonNull
    private DestroyMode mode;

    /**
     * Whether or not this map instance has been destroyed.
     */
    private boolean destroyed;

    /**
     * Gets called when the instance is processed globally.
     */
    public void process() {
        // NOOP
    }

    /**
     * Initialises the map instance.
     */
    public void initialise() {
        createMap();
        createNpcs();
        InstanceManager.add(this);
    }

    /**
     * Gets called when the player has just entered the map instance.
     *
     * @param player the player which has just entered the map instance.
     */
    public void onEnter(Player player) {
        // NOOP
    }

    /**
     * Gets called when the player has just left the map instance.
     *
     * @param player the player which has just left the map instance.
     */
    public void onLeave(Player player) {
        // NOOP
    }

    /**
     * Fires the {@link #onLogout(Player)} event of the instance.
     *
     * @param player the player which was just logged out.
     */
    public void fireOnLogout(Player player) {
        if (!players.contains(player)) {
            return;
        }
        onLogout(player);
        remove(player, true);
    }

    /**
     * Gets called when the player has just logged out.
     *
     * @param player the player which was just logged out.
     */
    public void onLogout(Player player) {
        // NOOP
    }

    /**
     * Fires the {@link #onMove(Player)} event of the map instance.
     *
     * @param player the player who was just moved to another location.
     */
    public void fireOnMove(Player player) {
        if (!isWithin(player.getPosition())) {
            remove(player, false);
        } else {
            onMove(player);
        }
    }


    /**
     * Gets called when the player was forcibly moved to another location.
     *
     * @param player the player who was just moved.
     */
    public void onMove(Player player) {
        // NOOP
    }

    /**
     * Creates the region of the map instance
     */
    public abstract void createMap();

    /**
     * Spawn all of the map entities.
     */
    public abstract void createNpcs();

    /**
     * Returns the teleport position when the player is added to the instance.
     *
     * @param player the player which the teleport position is for.
     * @return the {@link Position} to teleport the player to.
     */
    public abstract Position getTeleportPosition(Player player);

    /**
     * Returns the position when the player is teleported outside the instance.
     *
     * @param player the player which the teleport position is for.
     * @return the {@link Position} to teleport the player to.
     */
    public Position getLobbyPosition(Player player) {
        return GameSettings.DEFAULT_POSITION;
    }

    /**
     * Returns the dynamic region of the map instance.
     *
     * @return the {@link DynamicMap} object.
     */
    public abstract DynamicMap getMap();

    /**
     * Adds a new player to the boss instance.
     *
     * @param player the player to add to the boss instance.
     */
    public boolean add(Player player) {
        if (mode == DestroyMode.GROUP && owner == null && players.size() == 0) {
            owner = player.getUsername();
        }
        if (players.contains(player)) {
            return false;
        }
        Position teleportPosition = getTeleportPosition(player);

        // This controller check breaks adding players to dungeoneering. We can add a check if we
        // actually need this for some reason later
        /*if (!player.getControllerManager().processTeleport(TeleportType.NORMAL, teleportPosition)) {
            return false;
        }*/
        players.add(player);
        player.moveTo(teleportPosition);
        onEnter(player);
        return true;
    }

    /**
     * Removes the specified {@link Player} from the map instance.
     *
     * @param player the player to remove from the map instance.
     */
    public void remove(Player player, boolean teleport) {
        if (!players.contains(player)) {
            // Onleave player from global instance..
            if (players.size() == 0) {
                onLeave(player);
            }
            return;
        }

        if (teleport) {
            player.moveTo(getLobbyPosition(player));
        }
        players.remove(player);
        onLeave(player);
        if (mode == DestroyMode.AUTO_ON_EMPTY && players.isEmpty()) {
            destroy();
        }
    }

    /**
     * Adds a new npc to the map instance.
     *
     * @param npc the npc to add to the map instance.
     */
    public void addNpc(NPC npc) {
        World.register(npc);
    }

    /**
     * Creates a new {@link NPC} object and adds it to the map instance.
     *
     * @param id    the id of the npc.
     * @param x     the local x position of the npc.
     * @param y     the local y position of the npc.
     * @param level the height level of the npc.
     * @return the created {@link NPC} object.
     */
    public NPC createNpc(int id, int x, int y, int level) {
        NPC npc = new NPC(id, toPosition(x, y, level));
        npc.respawn = false;
        npc.getMovementCoordinator().setCoordinator(new NPCMovementCoordinator.Coordinator(false, 1));
        addNpc(npc);
        return npc;
    }


    public NPC createNpc(int id, int x, int y, int level, int walkRadius) {
        NPC npc = new NPC(id, toPosition(x, y, level));
        npc.respawn = false;
        npc.getMovementCoordinator().setCoordinator(new NPCMovementCoordinator.Coordinator(true, walkRadius));
        addNpc(npc);
        return npc;
    }

        /**
         * Creates a new {@link NPC} object and adds it to the map instance.
         *
         * @param id    the id of the npc.
         * @param x     the local x position of the npc.
         * @param y     the local y position of the npc.
         * @param level the height level of the npc.
         * @return the created {@link NPC} object.
         */
    public NPC createNpcForceRespawn(int id, int x, int y, int level) {
        NPC npc = new NPC(id, toPosition(x, y, level));
        //npc.getDefinition().setRespawnTime(10);
        npc.respawn = true;
        npc.getMovementCoordinator().setCoordinator(new NPCMovementCoordinator.Coordinator(false, 1));
        addNpc(npc);
        return npc;
    }

    /**
     * Checks whether or not the player can teleport from the instance to the specified position.
     *
     * @param player   the player to check if they can teleport.
     * @param position the position which the player is teleporting to.
     * @return <code>true</code> if they can otherwise <code>false</code>.
     */
    public boolean canTeleport(Player player, Position position) {
        return true;
    }

    /**
     * Checks whether or not the area is considered a multi area for the specified {@link Character character}
     *
     * @param character the character we are checking for.
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isMultiArea(Character character) {
        return false;
    }

    /**
     * Destroys the map instance.
     */
    public void destroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;
        // Grab the dynamic region  associated with this instance.
        DynamicMap map = getMap();
        if (map != null) {
            // Removes all of the NPCs that are within this region.
            for (DynamicRegion region : map.getRegions()) {
                for (Integer npcIndex : region.getNpcs()) {
                    NPC npc = World.getNpcs().get(npcIndex);
                    if (npc == null) {
                        continue;
                    }
                    EntityHandler.deregister(npc);
                }
            }
            map.destroy();
        }
        // Remove all map instance from the instance manager.
        InstanceManager.remove(this);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            if (!destroyed) {
                log.warn("Please call BossInstance.destroy(). Auto destroying: {}", getClass().getSimpleName());
                destroy();
            }
        } finally {
            super.finalize();
        }
    }

    /**
     * Converts the specified local space coordinates into a world space coordinate.
     *
     * @param x the local x position.
     * @param y the local y position.
     * @return the {@link Position} object which contains the world space coordinate.
     */
    public Position toPosition(int x, int y) {
        return toPosition(x, y, 0);
    }

    /**
     * Converts the specified local space coordinates into a world space coordinate.
     *
     * @param x the local x position.
     * @param y the local y position.
     * @param z the local z position.
     * @return the {@link Position} object which contains the world space coordinate.
     */
    public Position toPosition(int x, int y, int z) {
        DynamicMap map = getMap();
        if (map == null) {
            throw new IllegalStateException("The map instance was not initialised yet");
        }
        return map.toWorldCoordinate(z, x, y);
    }

    /**
     * Checks whether or not the specified {@link Position} is within the map instance or not.
     *
     * @param position the position to check if it's within the map instance.
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isWithin(Position position) {
        DynamicMap map = getMap();
        if (map == null) {
            return false;
        }
        for (DynamicRegion region : map.getRegions()) {
            if (region != null && region.getId() == position.getRegionId()) {
                return true;
            }
        }
        return false;
    }
}
