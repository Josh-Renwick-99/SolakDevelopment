package com.ruse.world.instance;

import com.ruse.util.Stopwatch;
import com.ruse.world.entity.actor.player.controller.Controller;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.region.dynamic.DynamicMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Represents a boss map instance.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public abstract class MinigameInstance extends MapInstance {

    /**
     * The source map chunk-x position.
     */
    private final int chunkX;

    /**
     * The source map chunk-y position.
     */
    private final int chunkY;

    /**
     * The source map width.
     */
    private final int width;

    /**
     * The source map height.
     */
    private final int height;


    /**
     * The region object of the instance.
     */
    @Getter
    @Setter
    private DynamicMap map;

    @Getter
    @Setter
    private Controller minigameController;


    @Override
    public void process() {

    }

    protected void sendGroupMessage(String s) {
        for (Player p : getPlayers()) {
            p.sendMessage(s);
        }
    }

    /**
     * Constructs a new {@link MinigameInstance} type object instance.
     *
     * @param mode   the destroy mode of the instance.
     * @param chunkX the map chunk x position in chunks unit.
     * @param chunkY he map chunk y position in chunks unit.
     * @param width  the map width in chunks unit.
     * @param height the map height in chunks unit.
     */
    public MinigameInstance(@NonNull DestroyMode mode, int chunkX, int chunkY, int width, int height) {
        super(mode);
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.width = width;
        this.height = height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createMap() {
        if (map != null) {
            throw new IllegalStateException("The instance has already started");
        }
        map = DynamicMap.find((width + 7) / 8, (height + 7) / 8);
        if (map == null) {
            throw new IllegalStateException("Failed to find an empty map instance");
        }
        for (int level = 0; level < 4; level++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    map.copy(level, chunkX + x, chunkY + y, level, x, y, 0);
                }
            }
        }
        map.apply();
    }
}
