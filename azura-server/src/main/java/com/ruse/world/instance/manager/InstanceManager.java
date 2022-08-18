package com.ruse.world.instance.manager;

import com.ruse.world.instance.BossInstance;
import com.ruse.world.instance.MapInstance;
import com.ruse.world.region.dynamic.DynamicMap;
import com.ruse.world.region.dynamic.DynamicRegion;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Represents the map instance manager.
 *
 * @author Walied K. Yassen
 */
public final class InstanceManager {

    /**
     * All of the registered map instances of the world.
     */
    private static final Map<Integer, MapInstance> instances = new ConcurrentHashMap<>();

    /**
     * Processes all of the active map instances.
     */
    public static void process() {
        instances.values().forEach(MapInstance::process);
    }

    /**
     * Adds a new {@link MapInstance instance} to the manager.
     *
     * @param instance the instance to add to the manager.
     */
    public static void add(MapInstance instance) {
        DynamicMap map = instance.getMap();
        if (map == null) {
            throw new IllegalStateException("The map instance region was not created yet.");
        }
        for (DynamicRegion region : map.getRegions()) {
            instances.put(region.getId(), instance);
        }
    }

    /**
     * Removes the specified {@link MapInstance instance} from the manager.
     *
     * @param instance the instance to remove from the manager.
     */
    public static void remove(MapInstance instance) {
        DynamicMap map = instance.getMap();
        if (map == null) {
            throw new IllegalStateException("The map instance region was not created yet.");
        }
        for (DynamicRegion region : map.getRegions()) {
            instances.remove(region.getId());
        }
    }

    /**
     * Returns the {@link MapInstance} that is at the specified region {@code id}.
     *
     * @param id the region id of the map instance.
     * @return {@link MapInstance} at the specified region id.
     */
    public static MapInstance getInstance(int id) {
        return instances.get(id);
    }


    public static Optional<MapInstance> findPlayerInstance(String playerName, Class<? extends BossInstance> instance) {
        Predicate<MapInstance> hasOwner = p -> playerName.equalsIgnoreCase(p.getOwner());
        Predicate<MapInstance> containsPlayer = v -> v.getPlayers().stream().anyMatch(p -> p.getUsername().equalsIgnoreCase(playerName));
        return instances.values().stream().filter(instance::isInstance).filter(hasOwner.or(containsPlayer)).findFirst();
    }
}
