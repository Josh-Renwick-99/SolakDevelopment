package com.ruse.world.region.dynamic;

import com.google.common.base.Preconditions;
import com.ruse.model.Position;
import com.ruse.world.region.RegionManager;
import lombok.Getter;

import java.util.List;

/**
 * Represents a dynamic map of a variable size in width and length.
 *
 * @author Walied K. Yassen
 */
public final class DynamicMap {

    /**
     * The width of the map in regions unit.
     */
    @Getter
    private final int width;

    /**
     * The length of the map in regions unit.
     */
    @Getter
    private final int length;

    /**
     * The regions which belong to this map.
     */
    @Getter
    private final DynamicRegion[] regions;

    /**
     * Constructs a new {@link DynamicMap} type object instance.
     *
     * @param region the region this dynamic map should be for.
     */
    public DynamicMap(DynamicRegion region) {
        this(1, 1);
        regions[0] = region;
    }

    /**
     * Constructs a new {@link DynamicMap} type object instance.
     *
     * @param width  the width of the map in regions unit.
     * @param length the length of the map in regions unit.
     */
    private DynamicMap(int width, int length) {
        this.width = width;
        this.length = length;
        regions = new DynamicRegion[width * length];
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
        DynamicRegion region = getRegionByChunk(destinationX, destinationY);
        region.copy(sourceLevel, sourceX, sourceY, destinationLevel, destinationX & 0x7, destinationY & 0x7, rotation);
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
    public void copy(int sourceLevel, int sourceX, int sourceY, int destinationLevel, int destinationX, int destinationY, int width, int length, int rotation) {
        for (int xoff = 0; xoff < width; xoff++) {
            for (int yoff = 0; yoff < length; yoff++) {
                int rxoff = DynamicRegion.rotateX(xoff, yoff, 1, 1, 0, width, length, rotation);
                int ryoff = DynamicRegion.rotateY(xoff, yoff, 1, 1, 0, width, length, rotation);
                DynamicRegion region = getRegionByChunk(destinationX + rxoff, destinationY + ryoff);
                region.copy(sourceLevel, sourceX + xoff, sourceY + yoff, destinationLevel, destinationX + rxoff & 0x7, destinationY + ryoff & 0x7, rotation);
            }
        }
    }

    /**
     * Applies all of the pending changes to the dynamic map.
     */
    public void apply() {
        for (DynamicRegion region : regions) {
            region.apply();
        }
    }

    /**
     * Destroys the dynamic map.
     */
    public void destroy() {
        for (DynamicRegion region : regions) {
            region.destroy();
        }
    }

    /**
     * Returns a new {@link Position} object that contains the same coordinates as the specified {@code position} but
     * relative to base position of this map instance.
     *
     * @param position the position to relativize.
     * @return the created {@link Position} object instance.
     */
    public Position relativize(Position position) {
        return regions[0].relativize(position);
    }

    /**
     * Returns the {@link DynamicRegion} at the specified {@code index}.
     *
     * @param index the index of the region.
     * @return the {@link DynamicRegion} object instance.
     */
    public DynamicRegion getRegion(int index) {
        Preconditions.checkArgument(index >= 0 && index < regions.length, "The specified index is out of bounds");
        return regions[index];
    }

    /**
     * Returns the {@link DynamicRegion} at the specified local chunk coordinate.
     *
     * @param chunkX the local chunk x coordinate.
     * @param chunkY the local chunk y coordinate.
     * @return the {@link DynamicRegion} object instance.
     */
    public DynamicRegion getRegionByChunk(int chunkX, int chunkY) {
        return regions[getIndexByChunk(chunkX, chunkY)];
    }

    /**
     * Returns the {@code index} of the region which the specified local chunk coordinate belongs to.
     *
     * @param chunkX the local chunk x coordinate.
     * @param chunkY the local chunk y coordinate.
     * @return the {@code index} of the region.
     */
    private int getIndexByChunk(int chunkX, int chunkY) {
        return chunkY / 8 * width + chunkX / 8;
    }

    /**
     * Returns the world position of the specified local coordinate.
     *
     * @param level  the height level of the position.
     * @param localX the local coordinate x of the position.
     * @param localY the local coordinate y of the position.
     * @return the {@link Position} object holding world coordinates.
     */
    public Position toWorldPosition(int level, int localX, int localY) {
        DynamicRegion region = getRegionByChunk(localX / 8, localY / 8);
        return region.toWorldCoordinate(level, localX & 0x3f, localY & 0x3f);
    }

    /**
     * Returns the base position of the map.
     *
     * @return the base {@link Position} object.
     */
    public Position getBase() {
        return regions[0].getBase();
    }

    /**
     * Attempts to find an empty space in the world that has the specified {@code width} and {@code length} dimensions
     * in regions unit then creates a {@link DynamicMap} object based on this empty space.
     *
     * @param width  the width of the map in regions unit.
     * @param length the length of the map in regions unit.
     * @return the created {@link DynamicMap} or {@code null} if none is found.
     */
    public static DynamicMap find(int width, int length) {
        return find(width, length, RegionManager.DataType.TYPE_317);
    }

    /**
     * Attempts to find an empty space in the world that has the specified {@code width} and {@code length} dimensions
     * in regions unit then creates a {@link DynamicMap} object based on this empty space.
     *
     * @param width  the width of the map in regions unit.
     * @param length the length of the map in regions unit.
     * @return the created {@link DynamicMap} or {@code null} if none is found.
     */
    public static DynamicMap find(int width, int length, RegionManager.DataType dataType) {
        if (width == 1 && length == 1) {
            DynamicMap map = new DynamicMap(1, 1);
            map.regions[0] = RegionManager.createDynamic();
            return map;
        } else {
            // Cache the empty regions for easier access.
            List<Integer> emptyRegions = RegionManager.getEmptyRegions();
            // A helper array to store the region ids so far.
            int[] ids = new int[width * length];
            int index;
            // Loop through every empty region and perform the necessary checks.
            mainLoop:
            for (int regionId : emptyRegions) {
                // Calculate the base x and y of the region.
                int baseX = regionId >> 8;
                int baseY = regionId & 0xff;
                // Reset the ids and the index variables.
                index = 0;
                // Loop through every region and check if its available.
                for (int offx = 0; offx < width; offx++) {
                    for (int offy = 0; offy < length; offy++) {
                        int id = baseX + offx << 8 | baseY + offy;
                        if (!emptyRegions.contains(id)) {
                            continue mainLoop;
                        }
                        ids[index++] = id;
                    }
                }
                // Calculate and verify the found regions count.
                int count = index;
                if (count != ids.length) {
                    return null;
                }
                // Create the dynamic map object of the specified dimensions.
                DynamicMap map = new DynamicMap(width, length);
                // Loop through all of the found regions then create and store them.
                for (index = 0; index < count; index++) {
                    map.regions[index] = RegionManager.createDynamic(ids[index], dataType);
                }
                // Return the map.
                return map;
            }
            return null;
        }
    }

    /**
     * Checks whether or not the specified position is within this dynamic map.
     *
     * @param position the position to check if it is within.
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public boolean isWithin(Position position) {
        int regionId = position.getRegionId();
        for (DynamicRegion region : regions) {
            if (region.getId() == regionId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the specified local-space coordinate into a world-space coordinate.
     *
     * @param level
     *         the level of the local-space coordinate.
     * @param localX
     *         the x-position in the local-space coordinate.
     * @param localY
     *         the y-position in the local space coordinate.
     *
     * @return a {@link Position} object holding the world-space coordinate.
     */
    public Position toWorldCoordinate(int level, int localX, int localY) {
        return regions[0].toWorldCoordinate(level, localX, localY);
    }
}