package com.ruse.world.region.dynamic;


import com.ruse.model.GameObject;
import com.ruse.model.GroundItem;
import com.ruse.model.Position;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.world.World;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.region.Region;
import com.ruse.world.region.RegionManager;
import com.ruse.world.region.collision.CollisionMap;

import java.util.Arrays;
import java.util.Collection;

/**
 * A dynamically generated {@link Region} with chunks that are copied from other {@link Region region}s.
 *
 * @author Walied K. Yassen
 */
public final class DynamicRegion extends Region {

    /**
     * The currently pending chunks in this region.
     */
    private final int[][][] pendingChunks = new int[4][8][8];

    /**
     * The currently applied chunks in this region.
     */
    private final int[][][] appliedChunks = new int[4][8][8];

    /**
     * Constructs a new {@link DynamicRegion} type object instance.
     *
     * @param id the id of the region.
     * @Parma dataType the data type of the region.
     */
    public DynamicRegion(int id) {
        super(id);
        for (int level = 0; level < 4; level++) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    pendingChunks[level][x][y] = -1;
                    appliedChunks[level][x][y] = -1;
                }
            }
        }
    }

    /**
     * Copies a chunk from the specified source location and place it in the specified destination location (relative to
     * the region).
     *
     * @param sourceLevel      the source chunk level.
     * @param sourceX          teh source chunk x position.
     * @param sourceY          the source chunk y position.
     * @param destinationLevel the destination chunk level.
     * @param destinationX     the destination chunk x position.
     * @param destinationY     the destination chunk y position.
     * @param rotation         the rotation of the chunk.
     */
    public void copy(int sourceLevel, int sourceX, int sourceY, int destinationLevel, int destinationX, int destinationY, int rotation) {
        pendingChunks[destinationLevel][destinationX][destinationY] = pack(sourceLevel, sourceX, sourceY, rotation);
    }

    /**
     * Applies the current pending changes to the region.
     */
    public void apply() {
        for (int level = 0; level < 4; level++) {
            for (int chunkX = 0; chunkX < 8; chunkX++) {
                for (int chunkY = 0; chunkY < 8; chunkY++) {
                    int pendingChunk = pendingChunks[level][chunkX][chunkY];
                    if (appliedChunks[level][chunkX][chunkY] == pendingChunk) {
                        continue;
                    }
                    appliedChunks[level][chunkX][chunkY] = pendingChunk;
                    int baseInRegionX = chunkX << 3;
                    int baseInRegionY = chunkY << 3;
                    if (pendingChunk == -1) {
                        for (int inChunkX = 0; inChunkX < 8; inChunkX++) {
                            for (int inChunkY = 0; inChunkY < 8; inChunkY++) {
                                int localX = baseInRegionX + inChunkX;
                                int localY = baseInRegionY + inChunkY;
                                setOriginalObjects(level, localX, localY, null);
                                getCollisionMap().flag(level, localX, localY, CollisionMap.FLAG_BLOCKED);
                                for (int type = 0; type < 4; type++) {
                                    int packedProperties = toPackedProperties(level, localX, localY, type);
                                    getSpawnedObjects().remove(packedProperties);
                                    getRemovedObjects().remove(packedProperties);
                                }
                            }
                        }
                    } else {
                        int sourceLevel = pendingChunk >> 24 & 0x3;
                        int sourceChunkX = pendingChunk >> 14 & 0x3ff;
                        int sourceChunkY = pendingChunk >> 3 & 0x7ff;
                        int mapRotation = pendingChunk >> 1 & 0x3;
                        for (int inChunkX = 0; inChunkX < 8; inChunkX++) {
                            for (int inChunkY = 0; inChunkY < 8; inChunkY++) {
                                int sourceAbsoluteX = (sourceChunkX << 3) + inChunkX;
                                int sourceAbsoluteY = (sourceChunkY << 3) + inChunkY;
                                GameObject[] sourceObjects = RegionManager.getOriginalObjects(new Position(sourceAbsoluteX, sourceAbsoluteY, sourceLevel));
                                if (sourceObjects != null) {
                                    for (GameObject sourceObject : sourceObjects) {
                                        if (sourceObject == null) {
                                            continue;
                                        }
                                        GameObjectDefinition definition = sourceObject.getDefinition();

                                        int rotatedX = rotateX(inChunkX, inChunkY, definition.tileSizeX, definition.tileSizeY, sourceObject.getFace(), 8, 8, mapRotation);
                                        int rotatedY = rotateY(inChunkX, inChunkY, definition.tileSizeX, definition.tileSizeY, sourceObject.getFace(), 8, 8, mapRotation);
                                        Position position = getBase().transform(baseInRegionX + rotatedX, baseInRegionY + rotatedY, level);
                                        GameObject object = new GameObject(sourceObject.getId(), position, sourceObject.getType(), (sourceObject.getFace() + mapRotation) & 0x3);
                                        addOriginalObject(object);
                                    }
                                }
                                int clippingX = rotateX(inChunkX, inChunkY, 1, 1, 0, 8, 8, mapRotation);
                                int clippingY = rotateY(inChunkX, inChunkY, 1, 1, 0, 8, 8, mapRotation);
                                int mask = RegionManager.getCollisionMask(level, sourceAbsoluteX, sourceAbsoluteY);
                                if ((mask & CollisionMap.FLAG_BLOCKED) == CollisionMap.FLAG_BLOCKED) {
                                    getCollisionMap().flagBlocked(level, baseInRegionX | clippingX, baseInRegionY | clippingY);
                                }
                            }
                        }
                    }
                }
            }
        }
        // Reload the map for all of the active players.
        for (int index : getVisiblePlayers()) {
            Player player = World.getPlayers().get(index);
            if (player == null) {
                continue;
            }
            player.loadMap();
        }

    }

    /**
     * Destroys the dynamic region.
     */
    public void destroy() {
        GroundItemManager.getGroundTilesByTile().values().stream().flatMap(Collection::stream).forEach( item -> {
            if (item.getPosition().getRegionId() == id) {
                GroundItemManager.remove(item, true);
            }
        });
        RegionManager.getEmptyRegions().add(id);
    }

    /**
     * Returns the chunk packed properties for the chunk at the specified local coordinate.
     *
     * @param level the level of the chunk.
     * @param x     the x position of the chunk.It'
     * @param y     the y position of the chunk.
     * @return the packed properties of the chunk.
     */
    public int getChunk(int level, int x, int y) {
        return appliedChunks[level][x][y];
    }

    /**
     * Rotates the specified local chunk y coordinate.
     *
     * @param x           the chunk local x coordinate.
     * @param y           the chunk local y coordinate.
     * @param width       the width of the entity.
     * @param length      the length of the entity.
     * @param rotation    the rotation to rotate by.
     * @param mapRotation the rotation to rotate by.
     * @return the rotated chunk local y position.
     */
    public static int rotateX(int x, int y, int width, int length, int rotation, int mapWidth, int mapLength, int mapRotation) {
        if ((rotation & 0x1) == 1) {
            int backup = width;
            width = length;
            length = backup;
        }
        mapRotation &= 0x3;
        if (mapRotation == 0) {
            return x;
        }
        if (mapRotation == 1) {
            return y;
        }
        if (mapRotation == 2) {
            return mapWidth - 1 - x - (width - 1);
        }
        return mapLength - 1 - y - (length - 1);
    }

    /**
     * Rotates the specified local chunk x coordinate.
     *
     * @param x           the chunk local x coordinate.
     * @param y           the chunk local y coordinate.
     * @param width       the width of the entity.
     * @param length      the length of the entity.
     * @param rotation    the rotation to rotate by.
     * @param mapRotation the rotation to rotate by.
     * @return the rotated chunk local x position.
     */
    public static int rotateY(int x, int y, int width, int length, int rotation, int mapWidth, int mapLength, int mapRotation) {
        if ((rotation & 0x1) == 1) {
            int backup = width;
            width = length;
            length = backup;
        }
        mapRotation &= 0x3;
        if (mapRotation == 0) {
            return y;
        }
        if (mapRotation == 1) {
            return mapWidth - 1 - x - (width - 1);
        }
        if (mapRotation == 2) {
            return mapLength - 1 - y - (length - 1);
        }
        return x;
    }

    /**
     * Packs the specified chunk properties into a single 32-bit integer.
     *
     * @param level    the level of the chunk.
     * @param chunkX   the x-coordinate of the chunk.
     * @param chunkY   the y-coordinate of the chunk.
     * @param rotation the rotation of the chunk.
     * @return the packed properties integer.
     */
    public static int pack(int level, int chunkX, int chunkY, int rotation) {
        return level << 24 | chunkX << 14 | chunkY << 3 | rotation << 1;
    }
}
