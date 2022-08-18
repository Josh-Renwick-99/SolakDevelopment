package com.ruse.world.region;


import com.google.common.collect.ImmutableSet;
import com.ruse.model.Direction;
import com.ruse.model.GameObject;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.clip.region.Flags;
import com.ruse.world.clip.stream.ByteStream;
import com.ruse.world.entity.object.ObjectType;
import com.ruse.world.region.dynamic.DynamicRegion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The region manager of the world.
 *
 * @author Walied K. Yassen
 */
@Slf4j
public final class RegionManager {

    /**
     * The search minimum x grid position.
     */
    public static final int SEARCH_MIN_X = 75;

    /**
     * The search maximum x grid position.
     */
    public static final int SEARCH_MAX_X = 127;

    /**
     * The search minimum y grid position.
     */
    public static final int SEARCH_MIN_Y = 15;

    /**
     * The search minimum y grid position.
     */
    public static final int SEARCH_MAX_Y = 255;

    /**
     * A {@link Set} of region id(s) that will be forced to load from the 667 data.
     */
    private static final Set<Integer> REGIONS_667 = ImmutableSet.of(11601, 5961, 13656, 9804, 5190, 8787, 12633, 14672, 14671, 14416, 14415, 13626, 13625, 13882, 13881, 10074, 10073, 10329, 10330, 8787, 6961, 6962, 6963, 7216, 7217, 7218, 7219, 7472, 7473, 7474, 7475, 7728, 7729, 7730, 7731, 7985, 7986, 7987, 23372, 23373, 23628, 23629, 12176, 16279, 13403, 16729, 19260, 19261, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 542, 543, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 577, 578, 579, 580, 581, 582, 583, 584, 585, 586, 587, 588, 589, 590, 591, 592, 593, 594, 595, 596, 597, 598, 599, 798, 799, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812, 813, 814, 815, 816, 817, 818, 819, 820, 821, 822, 823, 824, 825, 826, 827, 828, 829, 830, 831, 832, 833, 834, 835, 836, 837, 838, 839, 840, 841, 842, 843, 844, 845, 846, 847, 848, 849, 850, 851, 852, 853, 854, 855, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1071, 1072, 1073, 1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083, 1084, 1085, 1086, 1087, 1088, 1089, 1090, 1091, 1092, 1093, 1094, 1095, 1096, 1097, 1098, 1099, 1100, 1101, 1102, 1103, 1104, 1105, 1106, 1107, 1108, 1109, 1110, 1111, 1310, 1311, 1312, 1313, 1314, 1315, 1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 1324, 1325, 1326, 1327, 1328, 1329, 1330, 1331, 1332, 1333, 1334, 1335, 1336, 1337, 1338, 1339, 1340, 1341, 1342, 1343, 1344, 1345, 1346, 1347, 1348, 1349, 1350, 1351, 1352, 1353, 1354, 1355, 1356, 1357, 1358, 1359, 1360, 1361, 1362, 1363, 1364, 1365, 1366, 1367, 1566, 1567, 1568, 1569, 1570, 1571, 1572, 1573, 1574, 1575, 1576, 1577, 1578, 1579, 1580, 1581, 1582, 1583, 1584, 1585, 1586, 1587, 1588, 1589, 1590, 1591, 1592, 1593, 1594, 1595, 1596, 1597, 1598, 1599, 1600, 1601, 1602, 1603, 1604, 1605, 1606, 1607, 1608, 1609, 1610, 1611, 1612, 1613, 1614, 1615, 1616, 1617, 1618, 1619, 1620, 1621, 1622, 1623, 1822, 1823, 1824, 1825, 1826, 1827, 1828, 1829, 1830, 1831, 1832, 1833, 1834, 1835, 1836, 1837, 1838, 1839, 1840, 1841, 1842, 1843, 1844, 1845, 1846, 1847, 1848, 1849, 1850, 1851, 1852, 1853, 1854, 1855, 1856, 1857, 1858, 1859, 1860, 1861, 1862, 1863, 1864, 1865, 1866, 1867, 1868, 1869, 1870, 1871, 1872, 1873, 1874, 1875, 1876, 1877, 1878, 1879, 2078, 2079, 2080, 2081, 2082, 2083, 2084, 2085, 2086, 2087, 2088, 2089, 2090, 2091, 2092, 2093, 2094, 2095, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2111, 2112, 2113, 2114, 2115, 2116, 2117, 2118, 2119, 2120, 2121, 2122, 2123, 2124, 2125, 2126, 2127, 2128, 2129, 2130, 2131, 2132, 2133, 2134, 2135, 2334, 2335, 2336, 2337, 2338, 2339, 2340, 2341, 2342, 2343, 2344, 2345, 2346, 2347, 2348, 2349, 2350, 2351, 2352, 2353, 2354, 2355, 2356, 2357, 2358, 2359, 2360, 2361, 2362, 2363, 2364, 2365, 2366, 2367, 2368, 2369, 2370, 2371, 2372, 2373, 2374, 2375, 2376, 2377, 2378, 2379, 2380, 2381, 2382, 2383, 2384, 2385, 2386, 2387, 2388, 2389, 2390, 2391, 2590, 2591, 2592, 2593, 2594, 2595, 2596, 2597, 2598, 2599, 2600, 2601, 2602, 2603, 2604, 2605, 2606, 2607, 2608, 2609, 2610, 2611, 2612, 2613, 2614, 2615, 2616, 2617, 2618, 2619, 2620, 2621, 2622, 2623, 2624, 2625, 2626, 2627, 2628, 2629, 2630, 2631, 2632, 2633, 2634, 2635, 2636, 2637, 2638, 2639, 2640, 2641, 2642, 2643, 2644, 2645, 2646, 2647);

    /**
     * A {@link Set} of region id(s) that wil be forced to load from the 742 data.
     */
    private static final Set<Integer> REGIONS_742 = ImmutableSet.of(18517, 18516, 18515, 18518, 5475, 5476, 5731, 19024, 19025, 19023, 18768, 19538, 18769, 3663);

    /**
     * The regions that are currently loaded in the world.
     */
    private static final Map<Integer, Region> regions = new ConcurrentHashMap<>();

    /**
     * A list of all the currently empty regions in the world.
     */
    @Getter
    private static final List<Integer> emptyRegions = new CopyOnWriteArrayList<>();

    /**
     * Initialises the region system.
     */
    public static void initialise() {
        // Load all of the map indices from the cache.
        MapIndex map_index = MapIndex.load();
        //indices.put(DataType.TYPE_667, MapIndex.load(Cache.getStore().getIndexes()[16].getFile("map_index_667.dat", 0)));
        //indices.put(DataType.TYPE_742, MapIndex.load(Cache.getStore().getIndexes()[16].getFile("map_index_742.dat", 0)));
        // Loop through all regions and grab the valid ones.
        Set<Integer> regionIds = new HashSet<>();
        for (int regionId = 0; regionId < 32767; regionId++) {
            if (map_index.maps.containsKey(regionId)) {
                regionIds.add(regionId);
                regions.computeIfAbsent(regionId, $ -> new Region($));
            }
        }
        int count = 0;
        // Loop through all the valid regions and load them.
        for (int regionId : regionIds) {
            Pair<Integer, Integer> ids = map_index.maps.get(regionId);
            if (ids == null) {
                continue;
            }
            try {
                byte[] locData = Misc.getBuffer(new File("./data/clipping/maps/" + ids.getRight() + ".gz"));
                byte[] mapData = Misc.getBuffer(new File("./data/clipping/maps/" + ids.getLeft() + ".gz"));
                if (mapData == null || locData == null) {
                    continue;
                }
                loadRegion(regionId, mapData, locData);
                count++;
            } catch (Throwable e) {
                log.error("Failed to load region {} reason: {}", regionId, e.getMessage());
            }
        }
        System.out.println("Loaded a total of " + count + " region(s)");
        // Pre-find all of the empty region(s) in the world.
        for (int x = SEARCH_MIN_X; x <= SEARCH_MAX_X; x++) {
            for (int y = SEARCH_MIN_Y; y <= SEARCH_MAX_Y; y++) {
                int id = x << 8 | y;
                if (regionIds.contains(id)) {
                    continue;
                }
                emptyRegions.add(id);
            }
        }
    }

    /**
     * Attempt to load the specified {@link Region} from the specified map and loc data.
     *
     * @param id      the id of the region.
     * @param mapData the map data of the region.
     * @param locData the loc data of the region.
     */
    private static void loadRegion(int id, byte[] mapData, byte[] locData) {
        Region region = regions.get(id);
        if (region == null) {
            throw new IllegalArgumentException("The specified region id does not have an associated initialised Region object");
        }
        region.load(mapData, locData);
    }

    /**
     * Returns the {@link GameObject} at the specified {@link Position} and has the specified {@code id}.
     *
     * @param position the position of the object.
     * @param id       the id of the object.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getObjectWithId(Position position, int id) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return null;
        }
        for (int type = 0; type < 4; type++) {
            GameObject object = region.getObject(position.getZ(), position.getInRegionX(), position.getInRegionY(), type);
            if (object != null && object.getId() == id) {
                return object;
            }
        }
        return null;
    }

    /**
     * Returns the {@link GameObject} at the specified {@link Position} and has the specified {@code type}.
     *
     * @param position the position of the object.
     * @param type     the type of the object.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getObjectWithType(Position position, ObjectType type) {
        return getObjectWithType(position, type.getId());
    }

    /**
     * Returns the {@link GameObject} at the specified {@link Position} and has the specified {@code type}.
     *
     * @param position the position of the object.
     * @param type     the type of the object.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getObjectWithType(Position position, int type) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return null;
        }
        return region.getObject(position.getZ(), position.getInRegionX(), position.getInRegionY(), type);
    }

    /**
     * Adds a new {@link GameObject object} to the world at the specified position and with the specified properties.
     *
     * @param id       the id of the object to add to the world.
     * @param level    the height level of the object to add to the world.
     * @param x        the x position of the object to add to the world.
     * @param y        the y position of the object to add to the world.
     * @param shape    the shape of the object to add to the world.
     * @param rotation the rotation of the object to add to the world.
     */
    public static void addObject(int id, int level, int x, int y, int shape, int rotation) {
        addObject(id, new Position(x, y, level), shape, rotation);
    }

    /**
     * Adds a new {@link GameObject object} to the world at the specified position and with the specified properties.
     *
     * @param id       the id of the object to add to the world.
     * @param position the position of the object to add to the world.
     * @param shape    the shape of the object to add to the world.
     * @param rotation the rotation of the object to add to the world.
     */
    public static void addObject(int id, Position position, int shape, int rotation) {
        addObject(new GameObject(id, position, shape, rotation));
    }

    /**
     * Adds a new {@link GameObject object} to the world at the specified position and with the specified properties.
     *
     * @param object the object to add to the world.
     */
    public static void addObject(GameObject object) {
        Region region = getRegion(object.getPosition().getRegionId());
        if (region == null) {
            return;
        }
        region.addObject(object);
    }

    /**
     * Removes an existing {@link GameObject} from the world.
     *
     * @param object the game object to remove from the world.
     */
    public static void removeObject(GameObject object) {
        Region region = getRegion(object.getPosition().getRegionId());
        if (region == null) {
            return;
        }
        region.removeObject(object);
    }

    /**
     * Removes the existing ground {@link GameObject} that is currently at the specified {@link Position}.
     *
     * @param level the height level of the game object to remove.
     * @param x     the x position of the game object to remove.
     * @param y     the y position of the game object to remove.
     */
    public static void removeObject(int level, int x, int y) {
        removeObject(level, x, y, ObjectType.INTERACTABLE_OBJECT);
    }

    /**
     * Removes the existing ground {@link GameObject} that is currently at the specified {@link Position}.
     *
     * @param position the position of the game object to remove.
     */
    public static void removeObject(Position position) {
        removeObject(position, ObjectType.INTERACTABLE_OBJECT);
    }

    /**
     * Removes the existing {@link GameObject} that is currently at the specified {@link Position} and has the specified
     * {@code type}.
     *
     * @param position the position of the game object to remove.
     * @param type     the type of the game object to remove.
     */
    public static void removeObject(Position position, ObjectType type) {
        removeObject(position, type.getId());
    }

    /**
     * Removes the existing {@link GameObject} that is currently at the specified {@link Position} and has the specified
     * {@code type}.
     *
     * @param level the height level of the game object to remove.
     * @param x     the x position of the game object to remove.
     * @param y     the y position of the game object to remove.
     * @param type  the type of the game object to remove.
     */
    public static void removeObject(int level, int x, int y, ObjectType type) {
        removeObject(level, x, y, type.getId());
    }

    /**
     * Removes the existing {@link GameObject} that is currently at the specified {@link Position} and has the specified
     * {@code type}.
     *
     * @param level the height level of the game object to remove.
     * @param x     the x position of the game object to remove.
     * @param y     the y position of the game object to remove.
     * @param type  the type of the game object to remove.
     */
    public static void removeObject(int level, int x, int y, int type) {
        removeObject(new Position(x, y, level), type);
    }

    /**
     * Removes the existing {@link GameObject} that is currently at the specified {@link Position} and has the specified
     * {@code type}.
     *
     * @param position the position of the game object to remove.
     * @param type     the type of the game object to remove.
     */
    public static void removeObject(Position position, int type) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return;
        }
        region.removeObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), type);
    }


    /**
     * Returns the {@link GameObject} that is at the specified {@link Position} and has the specified {@code id}.
     * <br>
     * Please bare in mind, that this method does not care if the object is currently removed or not, so use it with
     * caution
     *
     * @param position the position which the game object is at.
     * @param id       the id of the game object we are trying to get.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getOriginalObjectWithId(Position position, int id) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return null;
        }
        GameObject[] objects = region.getOriginalObjects(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY());
        if (objects == null) {
            return null;
        }
        for (GameObject object : objects) {
            if (object == null) {
                continue;
            }
            if (object.getId() == id) {
                return object;
            }
        }
        return null;
    }

    /**
     * Returns all the original {@link GameObject} at the specified {@link Position} in the world.
     * <br>
     * Please bare in mind, that this method does not care if the object is currently removed or not, so use it with
     * caution
     *
     * @param position the position to grab the original game objects from.
     * @param type     the type of the object we are trying to get.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getOriginalObjectWithType(Position position, ObjectType type) {
        return getOriginalObjectWithType(position, type.getId());
    }

    /**
     * Returns all the original {@link GameObject} at the specified {@link Position} in the world.
     * <br>
     * Please bare in mind, that this method does not care if the object is currently removed or not, so use it with
     * caution
     *
     * @param position the position to grab the original game objects from.
     * @param type     the type of the object we are trying to get.
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public static GameObject getOriginalObjectWithType(Position position, int type) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return null;
        }
        GameObject[] objects = region.getOriginalObjects(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY());
        if (objects == null) {
            return null;
        }
        for (GameObject object : objects) {
            if (object == null) {
                continue;
            }
            if (object.getType() == type) {
                return object;
            }
        }
        return null;
    }

    /**
     * Returns all the original {@link GameObject} at the specified {@link Position} in the world.
     * <br>
     * Please bare in mind, that this method does not care if the object is currently removed or not, so use it with
     * caution
     *
     * @param position the position to grab the original game objects from.
     * @return an array of all {@link GameObject} if they were present otherwise {@code null}.
     */
    public static GameObject[] getOriginalObjects(Position position) {
        Region region = getRegion(position.getRegionId());
        if (region == null) {
            return null;
        }
        return region.getOriginalObjects(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY());
    }

    /**
     * Returns the {@link Region} object that is located the specified absolute position.
     *
     * @param absoluteX the absolute x position.
     * @param absoluteY the absolute y position.
     * @return the {@link Region} object if it was present otherwise {@code null}.
     */
    public static Region getRegion(int absoluteX, int absoluteY) {
        return regions.get(absoluteX >> 6 << 8 | absoluteY >> 6);
    }

    /**
     * Returns the {@link Region} object that is located the specified {@link Position position}.
     *
     * @param position the position which we want it's region.
     * @return the {@link Region} object if it was present otherwise {@code null}.
     */
    public static Region getRegion(Position position) {
        return regions.get(position.getRegionId());
    }

    /**
     * Returns the {@link Region} object with the specified {@code id}.
     *
     * @param id the id of the region.
     * @return the {@link Region} object if it was present otherwise {@code null}.
     */
    public static Region getRegion(int id) {
        return regions.get(id);
    }

    /**
     * Finds an empty map region and creates a dynamic region on that region.
     *
     * @return the created {@link DynamicRegion} object.
     * @throws IllegalStateException if we could not find any empty map regions to use.
     */
    public static DynamicRegion createDynamic() {
        if (emptyRegions.isEmpty()) {
            throw new IllegalStateException("Could not find an empty map square to build the region on.");
        }
        return createDynamic(emptyRegions.remove(Misc.random(emptyRegions.size())));
    }

    /**
     * Finds an empty map region and creates a dynamic region on that region.
     *
     * @return the created {@link DynamicRegion} object.
     * @throws IllegalStateException if we could not find any empty map regions to use.
     */
    public static DynamicRegion createDynamic(DataType type) {
        if (emptyRegions.isEmpty()) {
            throw new IllegalStateException("Could not find an empty map square to build the region on.");
        }
        return createDynamic(emptyRegions.remove(Misc.random(emptyRegions.size())), type);
    }

    /**
     * Attempts to create a {@link DynamicRegion} at the specified region id.
     *
     * @param regionId the region id to create the dynamic region at.
     * @return the created {@link DynamicRegion} object.
     * @throws IllegalArgumentException if the specified region id is not available.
     */
    public static DynamicRegion createDynamic(int regionId) {
        return createDynamic(regionId, DataType.TYPE_317);
    }

    /**
     * Attempts to create a {@link DynamicRegion} at the specified region id.
     *
     * @param regionId the region id to create the dynamic region at.
     * @param dataType the data type of the region.
     * @return the created {@link DynamicRegion} object.
     * @throws IllegalArgumentException if the specified region id is not available.
     */
    public static DynamicRegion createDynamic(int regionId, DataType dataType) {
        if (regions.containsKey(regionId)) {
            throw new IllegalArgumentException("The specified region id is already parked for another region: " + regionId);
        }
        DynamicRegion region = new DynamicRegion(regionId);
        regions.put(regionId, region);
        emptyRegions.remove((Integer) regionId);
        return region;
    }

    /**
     * Checks whether or not we can throw a projectile from the specified position towards the specified position.
     *
     * @param current the position to throw the projectile from.
     * @param dest    the position  to throw the projectile to.
     * @param size    the size of the destination.
     * @return <code>true</code> if the projectile path is possible otherwise <code>false</code>.
     */
    public static boolean canProjectile(Position current, Position dest, int size) {
        if (current.getZ() != dest.getZ()) {
            return false;
        }
        if (Locations.inside(current, 1, dest, size)) {
            return true;
        }
        int currentX = current.getX(), currentY = current.getY();
        int destX = dest.getX(), destY = dest.getY(), destEndX = destX + size - 1, destEndY = destY + size - 1;
        int lastX = currentX, lastY = currentY;
        if (lastX >= destX && lastY >= destY && lastX <= destEndX && lastY <= destEndY) {
            return true;
        }
        int level = current.getZ();
        while (true) {
            if (currentX < destX) {
                currentX++;
            } else if (currentX > destX) {
                currentX--;
            }
            if (currentY < destY) {
                currentY++;
            } else if (currentY > destY) {
                currentY--;
            }
            Direction direction = Direction.fromDeltas(currentX - lastX, currentY - lastY);
            if (direction == Direction.NONE) {
                return false;
            }
            if (!canProjectileStep(level, lastX, lastY, direction)) {
                return false;
            }
            lastX = currentX;
            lastY = currentY;
            if (lastX >= destX && lastY >= destY && lastX <= destEndX && lastY <= destEndY) {
                return true;
            }
        }
    }

    /**
     * Checks whether or not a projectile can step from the specified position towards the specified direction.
     *
     * @param level     the level of the position.
     * @param x         the x of the position.
     * @param y         the y of the position.
     * @param direction the direction to step towards.
     * @param size      the size of the entity to step towards.
     * @return <code>true</code> if we can step towards otherwise <code>false</code>.
     */
    private static boolean canProjectileStep(int level, int x, int y, Direction direction) {
        int dx = direction.deltaX();
        int dy = direction.deltaY();
        int blockMask = 0x20000;
        if (dx <= 0) {
            blockMask |= 0x1000;
        } else {
            blockMask |= 0x10000;
        }
        if (dy <= 0) {
            blockMask |= 0x400;
        } else {
            blockMask |= 0x4000;
        }
        int mask = getProjectileMask(level, x + dx, y + dy);
        return (mask & blockMask) == 0;
    }

    /**
     * Checks whether or not an entity can walk from the tile at the specified position towards the specified
     * direction.
     *
     * @param position  the position of the entity.
     * @param direction the direction to walk towards.
     * @param size      the size of the entity we are checking the walking for.
     * @return <code>true</code> if the entity can walk towards the specified direction otherwise <code>false</code>.
     */
    public static boolean canWalk(Position position, Direction direction, int size) {
        return canWalk(position.getZ(), position.getX(), position.getY(), direction.deltaX(), direction.deltaY(), size);
    }

    /**
     * Checks whether or not an entity can walk from the tile at the specified position towards the specified
     * direction.
     *
     * @param level     the level position of the tile.
     * @param x         the x position of the tile.
     * @param y         the y position of the tile.
     * @param direction the direction to walk towards.
     * @param size      the size of the entity we are checking the walking for.
     * @return <code>true</code> if the entity can walk towards the specified direction otherwise <code>false</code>.
     */
    public static boolean canWalk(int level, int x, int y, Direction direction, int size) {
        return canWalk(level, x, y, direction.deltaX(), direction.deltaY(), size);
    }

    /**
     * Checks whether or not an entity can walk from the tile at the specified position towards the specified delta.
     *
     * @param level  the level position of the tile.
     * @param x      the x position of the tile.
     * @param y      the y position of the tile.
     * @param deltaX the delta of the x-position of the direction to walk towards.
     * @param deltaY the delta of the y-position of the direction to walk towards.
     * @param size   the size of the entity we are checking the walking for.
     * @return <code>true</code> if the entity can walk towards the specified delta otherwise <code>false</code>
     */
    public static boolean canWalk(int level, int x, int y, int deltaX, int deltaY, int size) {
        if (size == 1) {
            int mask = getCollisionMask(level, x + deltaX, y + deltaY);
            if (deltaX == -1 && deltaY == 0) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
            }
            if (deltaX == 1 && deltaY == 0) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
            }
            if (deltaX == 0 && deltaY == -1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
            }
            if (deltaX == 0 && deltaY == 1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
            }
            if (deltaX == -1 && deltaY == -1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getCollisionMask(level, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getCollisionMask(level, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
            }
            if (deltaX == 1 && deltaY == -1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getCollisionMask(level, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getCollisionMask(level, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
            }
            if (deltaX == -1 && deltaY == 1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getCollisionMask(level, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0 && (getCollisionMask(level, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
            }
            if (deltaX == 1 && deltaY == 1) {
                return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getCollisionMask(level, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0 && (getCollisionMask(level, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
            }
        } else if (size == 2) {
            if (deltaX == -1 && deltaY == 0) {
                return (getCollisionMask(level, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getCollisionMask(level, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
            }
            if (deltaX == 1 && deltaY == 0) {
                return (getCollisionMask(level, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0 && (getCollisionMask(level, x + 2, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            }
            if (deltaX == 0 && deltaY == -1) {
                return (getCollisionMask(level, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getCollisionMask(level, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
            }
            if (deltaX == 0 && deltaY == 1) {
                return (getCollisionMask(level, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getCollisionMask(level, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            }
            if (deltaX == -1 && deltaY == -1) {
                return (getCollisionMask(level, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getCollisionMask(level, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
                        && (getCollisionMask(level, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
            }
            if (deltaX == 1 && deltaY == -1) {
                return (getCollisionMask(level, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0 && (getCollisionMask(level, x + 2, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
                        && (getCollisionMask(level, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            }
            if (deltaX == -1 && deltaY == 1) {
                return (getCollisionMask(level, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) == 0 && (getCollisionMask(level, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
                        && (getCollisionMask(level, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            }
            if (deltaX == 1 && deltaY == 1) {
                return (getCollisionMask(level, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0 && (getCollisionMask(level, x + 2, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
                        && (getCollisionMask(level, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
            }
        } else {
            if (deltaX == -1 && deltaY == 0) {
                if ((getCollisionMask(level, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getCollisionMask(level, x - 1, -1 + y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
                    if ((getCollisionMask(level, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == 1 && deltaY == 0) {
                if ((getCollisionMask(level, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0 || (getCollisionMask(level, x + size, y - (-size + 1)) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
                    if ((getCollisionMask(level, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == 0 && deltaY == -1) {
                if ((getCollisionMask(level, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0 || (getCollisionMask(level, x + size - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
                    if ((getCollisionMask(level, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == 0 && deltaY == 1) {
                if ((getCollisionMask(level, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0 || (getCollisionMask(level, x + size - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++) {
                    if ((getCollisionMask(level, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == -1 && deltaY == -1) {
                if ((getCollisionMask(level, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
                    if ((getCollisionMask(level, x - 1, y + -1 + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
                            || (getCollisionMask(level, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == 1 && deltaY == -1) {
                if ((getCollisionMask(level, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
                    if ((getCollisionMask(level, x + size, sizeOffset + -1 + y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0
                            || (getCollisionMask(level, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == -1 && deltaY == 1) {
                if ((getCollisionMask(level, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
                    if ((getCollisionMask(level, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
                            || (getCollisionMask(level, -1 + x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                        return false;
                    }
                }
            } else if (deltaX == 1 && deltaY == 1) {
                if ((getCollisionMask(level, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                    return false;
                }
                for (int sizeOffset = 1; sizeOffset < size; sizeOffset++) {
                    if ((getCollisionMask(level, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0
                            || (getCollisionMask(level, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the collision mask of the tile at the specified position.
     *
     * @param level the position level of the specified tile.
     * @param x     the the position x of the specified tile.
     * @param y     the position y of the specified tile.
     * @return the collision mask if it was present otherwise {@code -1}.
     */
    public static int getCollisionMask(int level, int x, int y) {
        Region region = getRegion(x >> 6 << 8 | y >> 6);
        if (region == null) {
            return -1;
        }
        return region.getCollisionMap().getMasks()[level & 0x3][x & 0x3f][y & 0x3f];
    }

    /**
     * Returns the projectile mask of the tile at the specified position.
     *
     * @param level the position level of the specified tile.
     * @param x     the the position x of the specified tile.
     * @param y     the position y of the specified tile.
     * @return the projectile mask if it was present otherwise {@code -1}.
     */
    public static int getProjectileMask(int level, int x, int y) {
        Region region = getRegion(x >> 6 << 8 | y >> 6);
        if (region == null) {
            return -1;
        }
        return region.getProjectileCollisionMap().getMasks()[level & 0x3][x & 0x3f][y & 0x3f];
    }

    /**
     * Checks whether or not the tile at the specified position is currently empty from any sort of objects.
     *
     * @param position the position to check.
     * @return <code>true</code> if the tile is empty otherwise <code>false</code>.
     */
    public static boolean isTileEmpty(Position position) {
        return isTileEmpty(position.getZ(), position.getX(), position.getY());
    }

    /**
     * Checks whether or not the tile at the specified position is currently empty from any sort of objects.
     *
     * @param level the level of the position to check.
     * @param x     the x of the position to  check.
     * @param y     the y of the position to check.
     * @return <code>true</code> if the tile is empty otherwise <code>false</code>.
     */
    public static boolean isTileEmpty(int level, int x, int y) {
        int mask = getCollisionMask(level, x, y);
        return (mask & Flags.FLOOR_BLOCKANY) == 0 && (mask & Flags.WALL_BLOCKANY) == 0;
    }

    /**
     * Checks whether or not the specified tile is blocked.
     *
     * @param level the level of the tile.
     * @param x     the x position of the tile.
     * @param y     the y position of the tile.
     * @return <code>true</code> if it is otherwise <code>false</code>.
     */
    public static boolean isBlocked(int level, int x, int y) {
        int mask = getCollisionMask(level, x, y);
        return (mask & 0x1280120) != 0;
    }

    /**
     * Checks whether or not the tile at the specified position is currently empty from any sort of objects.
     *
     * @param level the level of the position to check.
     * @param x     the x of the position to  check.
     * @param y     the y of the position to check.
     * @param size  the size of of the entity we are checking for.
     * @return <code>true</code> if the tile is empty otherwise <code>false</code>.
     */
    public static boolean isTileEmpty(int level, int x, int y, int size) {
        int currentX = x;
        int currentY = y;
        for (; currentX < x + size; currentX++) {
            for (; currentY < y + size; currentY++) {
                int mask = getCollisionMask(level, currentX, currentY);
                if ((mask & Flags.FLOOR_BLOCKANY) != 0 || (mask & Flags.WALL_BLOCKANY) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Position getFreeTile(Position center, int distance) {
        Position tile = center;
        for (int i = 0; i < 10; i++) {
            tile = new Position(center, distance);
            if (isTileEmpty(tile.getZ(), tile.getX(), tile.getY(), 1))
                return tile;
        }
        return center;
    }

    /**
     * Checks whether or not the tile at the specified position is currently blank.
     *
     * @param level the level of the position to check.
     * @param x     the x of the position to  check.
     * @param y     the y of the position to check.
     * @param size  the size of of the entity we are checking for.
     * @return <code>true</code> if the tile is blank otherwise <code>false</code>.
     */
    public static boolean isTileBlank(int level, int x, int y, int size) {
        int currentX = x;
        int currentY = y;
        for (; currentX < x + size; currentX++) {
            for (; currentY < y + size; currentY++) {
                int mask = getCollisionMask(level, currentX, currentY);
                if (mask != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * The data format type of the map files.
     *
     * @author Walied K. Yassen
     */
    public enum DataType {
        /**
         * The data is coming from an OSRS cache.
         */
        TYPE_317,

        /**
         * The data is coming from a 667 cache.
         */
        TYPE_667,

        /**
         * The data is coming from a 742 cache.
         */
        TYPE_742
    }

    /**
     * A single map index which holds data about one category of maps (osrs, 667, 742).
     *
     * @author Walied K. Yassen
     */
    public static final class MapIndex {

        /**
         * The maps that are within this index file.
         */
        @Getter
        private final Map<Integer, Pair<Integer, Integer>> maps = new HashMap<>();

        /**
         * Creates a {@link MapIndex} object populated with all of the existing map files that are within the specified
         *
         * @return the created {@link MapIndex} object.
         */
        public static MapIndex load() {
            MapIndex mapIndex = new MapIndex();
            try {
                final File file = new File("azura-server/data/clipping/map_index");
                final byte[] buffer = new byte[(int) file.length()];
                final DataInputStream input = new DataInputStream(new FileInputStream(file));
                input.readFully(buffer);
                input.close();
                final ByteStream stream = new ByteStream(buffer);
                int size = stream.getUShort();
                for (int i = 0; i < size; i++) {
                    int regionId = stream.getUShort();
                    int mapId = stream.getUShort();
                    int locId = stream.getUShort();
                    mapIndex.maps.put(regionId, Pair.of(mapId, locId));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mapIndex;
        }

        /**
         * Attempts to load a serialised {@link MapIndex} object.
         *
         * @param data the data of the serialised index object.
         * @return the loaded {@link MapIndex} object instance.
         */
        static MapIndex load(byte[] data) {
            ByteStream buffer = new ByteStream(data);
            int count = buffer.getUShort();
            MapIndex mapIndex = new MapIndex();
            for (int i = 0; i < count; i++) {
                int s = buffer.getUShort();
                int m = buffer.getUShort();
                int l = buffer.getUShort();
                mapIndex.maps.put(s, Pair.of(m, l));
            }
            return mapIndex;
        }
    }
}
