package com.ruse.world.region;

import com.ruse.model.GameObject;
import com.ruse.model.Position;
import com.ruse.model.definitions.GameObjectDefinition;
import com.ruse.world.World;
import com.ruse.world.clip.stream.ByteStream;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.region.collision.CollisionMap;
import io.netty.util.collection.IntObjectHashMap;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * A single region (or map square) in the world, holds all of the data for that region, such as objects, players, npcs,
 * etc..
 *
 * @author Walied K. Yassen
 */
public class Region {

    /**
     * The shape to type look-up table array.
     */
    public static final int[] SHAPE_TO_TYPE = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3};

    /**
     * The objects that are currently spawned in the region.
     */
    @Getter
    private final Map<Integer, GameObject> spawnedObjects = new IntObjectHashMap<>();

    /**
     * The objects that are currently removed from the region.
     */
    @Getter
    private final Map<Integer, GameObject> removedObjects = new IntObjectHashMap<>();

    /**
     * The players that are currently visible to this region (has the map loaded in their viewport).
     */
    @Getter
    private final Set<Integer> visiblePlayers = new CopyOnWriteArraySet<>();

    /**
     * The npcs that are currently visible to this region (has the map loaded in their viewport).
     */
    @Getter
    private final Set<Integer> visibleNpcs = new CopyOnWriteArraySet<>();

    /**
     * The players that are currently within this region.
     */
    @Getter
    private final Set<Integer> players = new CopyOnWriteArraySet<>();

    /**
     * The npcs that are currently within this region.
     */
    @Getter
    private final Set<Integer> npcs = new CopyOnWriteArraySet<>();

    /**
     * The game objects of each tile in the region.
     */
    @Getter
    private GameObject[][][][] objects;

    /**
     * The id of the region which is composed of bit-packed {@code x} and {@code y} coordinate of the region.
     */
    @Getter
    protected final int id;

    /**
     * The collision map of the region.
     */
    @Getter
    private final CollisionMap collisionMap;

    /**
     * The projectile collision map of the region.
     */
    @Getter
    private final CollisionMap projectileCollisionMap;

    /**
     * The base position of the region.
     */
    @Getter
    private final Position base;

    /**
     * Constructs a new {@link Region} type object instance.
     *
     * @param id
     *         the id of the region.
     * @param dataType
     *         the data type which this region uses.
     */
    public Region(int id) {
        this.id = id;
        collisionMap = new CollisionMap(id, false);
        projectileCollisionMap = new CollisionMap(id, true);
        base = new Position(id >> 8 << 6, (id & 0xff) << 6, 0);
    }

    /**
     * Loads the region clipping and objects information from the specified data.
     *
     * @param mapData
     *         the map data of the region.
     * @param locData
     *         the loc data of the region.
     */
    public void load(byte[] mapData, byte[] locData) {
        ByteStream buffer = new ByteStream(mapData);
        byte[][][] flags = new byte[4][64][64];
        for (int level = 0; level < 4; level++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    for (; ; ) {
                        int opcode = buffer.getUByte();
                        if (opcode == 0) {
                            break;
                        }
                        if (opcode == 1) {
                            buffer.getUByte();
                            break;
                        }
                        if (opcode <= 49) {
                            buffer.getUByte();
                        } else if (opcode <= 81) {
                            flags[level][x][y] = (byte) (opcode - 49);
                        }
                    }
                }
            }
        }
        for (int level = 0; level < 4; level++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    if ((flags[level][x][y] & 0x1) != 0x1) {
                        continue;
                    }
                    int renderLevel = level;
                    if ((flags[1][x][y] & 0x2) == 0x2) {
                        renderLevel--;
                    }
                    if (renderLevel < 0) {
                        continue;
                    }
                    collisionMap.flagBlocked(renderLevel, x, y);
                }
            }
        }
        if (locData == null) {
            return;
        }
        buffer = new ByteStream(locData);
        int id = -1;
        for (; ; ) {
            int id_incr = buffer.getUSmart();
            if (id_incr == 0) {
                break;
            }
            id += id_incr;
            int data = 0;
            for (; ; ) {
                int data_incr = buffer.getUSmart();
                if (data_incr == 0) {
                    break;
                }
                data += data_incr - 1;
                int level = data >> 12;
                int x = data >> 6 & 0x3f;
                int y = data & 0x3f;
                int settings = buffer.getUByte();
                int shape = settings >> 2;
                int rotation = settings & 0x3;
                if ((flags[1][x][y] & 0x2) == 2) {
                    level--;
                }
                if (level < 0 || level > 3) {
                    continue;
                }
                addOriginalObject(id, level, x, y, shape, rotation);
            }
        }
        // We loop through all objects and remove the weird turkeys :)
        for (int level = 0; level < 4; level++) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    GameObject[] objects = getOriginalObjects(level, x, y);
                    if (objects == null) {
                        continue;
                    }
                    for (GameObject object : objects) {
                        if (object == null) {
                            continue;
                        }
                        //TODO :look into
                        if (object.getDefinition().disable || object.getId() == 1751) {
                            removeObject(object);
                        }
                    }
                }
            }
        }
    }

    /**
     * Refreshes everything in the region for the specified {@link Player player}.
     */
    public void refreshAll(Player player) {
        final Position sceneCenter = player.getCurrentMapCenter();
        for (GameObject removedObject : removedObjects.values()) {
            if (!removedObject.getPosition().withinScene(sceneCenter)) {
                continue;
            }
            player.getPacketSender().sendObjectRemoval(removedObject);
        }
        for (GameObject spawnedObject : spawnedObjects.values()) {
            if (!spawnedObject.getPosition().withinScene(sceneCenter)) {
                continue;
            }
            player.getPacketSender().sendObject(spawnedObject);
        }
    }

    /**
     * Adds the specified {@link GameObject} to the region.
     *
     * @param object
     *         the object to add to the region.
     */
    public void addObject(GameObject object) {
        if (object.getId() == -1) {
            removeObject(object);
            return;
        }
        // Grab the original object so we can check against.
        GameObject originalObject = getOriginalObject(object.getPosition(), object.getType());
        // Convert the object properties into a packed number.
        int packedProperties = toPackedProperties(object);
        // Check if the object we are attempting to spawn is an original object.
        if (object.equals(originalObject)) {
            // This is meant to be a bitwise-or operator so both lhs and rhs are executed.
            if (spawnedObjects.remove(packedProperties) != null | removedObjects.remove(packedProperties) != null) {
                forEachVisiblePlayer(player -> player.getPacketSender().sendObject(originalObject), originalObject.getPosition());
                clipObject(originalObject);
            }
            return;
        }
        // If the object is equals to the spawned one, there is no need to update.
        if (object.equals(spawnedObjects.get(packedProperties))) {
            return;
        }
        // Add the objects in the spawned objects list and update the players.
        spawnedObjects.put(packedProperties, object);
        forEachVisiblePlayer(player -> player.getPacketSender().sendObject(object), object.getPosition());
        clipObject(object);
    }

    /**
     * Removes a currently visible object from the region and update all the visible players.
     *
     * @param object
     *         the game object to remove from the world.
     */
    public void removeObject(GameObject object) {
        removeObject(object.getPosition().getZ(), object.getPosition().getInRegionX(), object.getPosition().getInRegionY(), object.getType());
    }

    /**
     * Removes a currently visible object from the region and update all the visible players.
     *
     * @param level
     *         the height level of the object to remove.
     * @param localX
     *         the local x position of the object to remove [0-63].
     * @param localY
     *         the local y position of the object to remove [0-63].
     * @param type
     *         the type of the object to remove [0-3].
     */
    public void removeObject(int level, int localX, int localY, int type) {
        // We do not care what id of the object is passed to this method, it will remove the object
        // as long as the packed properties match, see `toPackedProperties` to know which properties
        // matter to removing an object.
        int packedProperties = toPackedProperties(level, localX, localY, type);
        // If we have an object spawned at the specified object location, we just remove that object and stop there.
        GameObject spawnedObject = spawnedObjects.remove(packedProperties);
        if (spawnedObject != null) {
            unclipObject(spawnedObject);
            GameObject originalObject;
            // The check below is meant to be inlined, so it is not always being called whenever we are removing an object
            // but rather will only be called if the removedObjects does not contain the specified packed properties.
            if (removedObjects.containsKey(packedProperties) | (originalObject = getOriginalObject(level & 0x3, localX, localY, type)) == null) {
                forEachVisiblePlayer(player -> player.getPacketSender().sendObjectRemoval(spawnedObject), spawnedObject.getPosition());
            } else {
                forEachVisiblePlayer(player -> player.getPacketSender().sendObject(originalObject), originalObject.getPosition());
                clipObject(originalObject);
            }
            return;
        }
        // Since there is no spawned object at that specified object location, we check if we should remove
        // an original object, the condition for removing an original object is that object is currently
        // visible in the region and is currently not removed (does not exist in the $removedObjects list).
        if (removedObjects.containsKey(packedProperties)) {
            return;
        }
        // Grab the original object at the specified location.
        GameObject originalObject = getOriginalObject(level & 0x3, localX, localY, type);
        // Check if there is an original object at the specified location and call the object
        // delete packet for all of the visible players.
        if (originalObject != null) {
            removedObjects.put(packedProperties, originalObject);
            forEachVisiblePlayer(player -> player.getPacketSender().sendObjectRemoval(originalObject), originalObject.getPosition());
            unclipObject(originalObject);
        }
    }

    /**
     * Attempts to get the {@link GameObject} that is currently visible at the specified location and has the specified
     * {@code type}.
     *
     * @param level
     *         the height level which the object is located at.
     * @param localX
     *         the local x position which the object is located at.
     * @param localY
     *         the local y position which the object is located at.
     * @param type
     *         the type of the object.
     *
     * @return the {@link GameObject} object if it was found otherwise {@code null}.
     */
    public GameObject getObject(int level, int localX, int localY, int type) {
        // We will try to get the object that is currently visible to the players in the region
        // If there is a spawned object, we will prioritise getting that, if there is not, we will check
        // if the original object currently exists and try to get it if it does exist and is **currently**
        // visible to the players of the region.

        // Convert the specified properties into a packed number for ease.
        int packedProperties = toPackedProperties(level, localX, localY, type);
        // Check if we have a spawned object or not with the specified properties.
        GameObject object = spawnedObjects.get(packedProperties);
        if (object != null) {
            return object;
        }
        // Check if the original object was removed and return null if it was.
        if (removedObjects.containsKey(packedProperties)) {
            return null;
        }
        // Attempt to return the original object at the location.
        return getOriginalObject(level & 0x3, localX, localY, type);
    }

    /**
     * Adds an original object to the region at the specified local-space coordinate with the specified properties. An
     * original object is basically any object that comes with the map data or is loaded from.
     *
     * @param id
     *         the id of the object.
     * @param level
     *         the level of the local-coordinate of the object.
     * @param x
     *         the x of the local-coordinate of the object.
     * @param y
     *         the y of the local-coordinate of the object.
     * @param shape
     *         the shape of the object.
     * @param rotation
     *         the rotation of the object.
     */
    protected void addOriginalObject(int id, int level, int x, int y, int shape, int rotation) {
        Position position = toWorldCoordinate(level, x, y);
        GameObject object = new GameObject(id, position, shape, rotation);
        addOriginalObject(object);
    }


    /**
     * Adds the specified original {@link GameObject object} to the region.
     *
     * @param object
     *         the original object to add to the region.
     */
    protected void addOriginalObject(GameObject object) {
        setOriginalObject(object.getPosition().getInRegionZ(), object.getPosition().getInRegionX(), object.getPosition().getInRegionY(), SHAPE_TO_TYPE[object.getType()], object);
        clipObject(object);
    }

    /**
     * Returns the original {@link GameObject} at the specified local-space coordinate.
     *
     * @param position
     *         the position of the object.
     * @param type
     *         the type of of the object.
     *
     * @return the {@link GameObject} object if it was present otherwise {@code null}.
     */
    public GameObject getOriginalObject(Position position, int type) {
        return getOriginalObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), type);
    }

    /**
     * Returns the original {@link GameObject} at the specified local-space coordinate.
     *
     * @param level
     *         the level of the local-space coordinate.
     * @param x
     *         the x of the local-space coordinate.
     * @param y
     *         the y of the local-space coordinate.
     * @param type
     *         the type of of the object.
     *
     * @return the {@link GameObject} object if it was present otherwise {@code null}.
     */
    public GameObject getOriginalObject(int level, int x, int y, int type) {
        // Check if the objects array dimensions are initialised.
        if (objects == null || objects[level] == null || objects[level][x] == null || objects[level][x][y] == null) {
            return null;
        }
        // Return the object at the coordinate and type.
        return objects[level][x][y][type];
    }

    /**
     * Returns the original {@link GameObject}s at the specified local-space coordinate.
     *
     * @param level
     *         the level of the local-space coordinate.
     * @param x
     *         the x of the local-space coordinate.
     * @param y
     *         the y of the local-space coordinate.
     *
     * @return an array of all the {@link GameObject} if they were present otherwise {@code null}.
     */
    public GameObject[] getOriginalObjects(int level, int x, int y) {
        // Check if the objects array dimensions are initialised.
        if (objects == null || objects[level] == null || objects[level][x] == null || objects[level][x][y] == null) {
            return null;
        }
        // Return the object at the coordinate and type.
        return objects[level][x][y];
    }

    protected void clipObject(GameObject object) {
        GameObjectDefinition type = object.getDefinition();
        if (type == null) {
            return;
        }
        int shape = object.getType();
        //TODO new clipping opcodes
        //if (shape == 22 ? type.clipping_type != 1 : type.clipping_type == 0) {
        if(type.unwalkable) {
            return;
        }

        Position position = object.getPosition();
        if (shape >= 0 && shape <= 3) {
            collisionMap.flagWall(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), object.getType(), object.getFace(), type.impenetrable, !type.aBoolean766);
            if (type.impenetrable) {
                projectileCollisionMap.flagWall(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), object.getType(), object.getFace(), type.impenetrable, !type.aBoolean766);
            }
        } else if (shape >= 9 && shape <= 21) {
            int width;
            int length;
            if ((object.getFace() & 0x1) == 0) {
                width = type.tileSizeX;
                length = type.tileSizeY;
            } else {
                width = type.tileSizeY;
                length = type.tileSizeX;
            }
            collisionMap.flagObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), width, length, type.impenetrable, !type.aBoolean766);
            if (type.impenetrable) {
                projectileCollisionMap.flagObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), width, length, type.impenetrable, !type.aBoolean766);
            }
        } else if (shape == 22) {
            collisionMap.flagDecoration(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY());
        }
    }

    private void unclipObject(GameObject object) {
        GameObjectDefinition type = object.getDefinition();
        if (type == null) {
            return;
        }
        int shape = object.getType();
        //TODO new clipping opcodes
        //if (shape == 22 ? type.clipping_type != 1 : type.clipping_type == 0) {
        if(type.unwalkable) {
            return;
        }

        Position position = object.getPosition();
        if (shape >= 0 && shape <= 3) {
            collisionMap.unflagWall(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), object.getType(), object.getFace(), type.impenetrable, !type.aBoolean766);
            if (type.impenetrable) {
                projectileCollisionMap.unflagWall(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), object.getType(), object.getFace(), type.impenetrable, !type.aBoolean766);
            }
        } else if (shape >= 9 && shape <= 21) {
            int sizeX;
            int sizeY;
            if ((object.getFace() & 0x1) == 0) {
                sizeX = type.tileSizeX;
                sizeY = type.tileSizeY;
            } else {
                sizeX = type.tileSizeY;
                sizeY = type.tileSizeX;
            }
            collisionMap.unflagObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), sizeX, sizeY, type.impenetrable, !type.aBoolean766);
            if (type.impenetrable) {
                projectileCollisionMap.unflagObject(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY(), object.getType(), object.getFace(), type.impenetrable, !type.aBoolean766);
            }
        } else if (shape == 22) {
            collisionMap.unflagDecoration(position.getZ() & 0x3, position.getInRegionX(), position.getInRegionY());
        }
    }

    /**
     * Sets the specified {@link GameObject object} at the specified local-space coordinate in the region.
     *
     * @param level
     *         the level of the local-space coordinate.
     * @param x
     *         the x of the local-space coordinate.
     * @param y
     *         the y of the local-space coordinate.
     * @param type
     *         the type of the game object.
     * @param object
     *         the object to set.
     */
    protected void setOriginalObject(int level, int x, int y, int type, GameObject object) {
        if (objects == null) {
            objects = new GameObject[4][][][];
        }
        if (objects[level] == null) {
            objects[level] = new GameObject[64][][];
        }
        if (objects[level][x] == null) {
            objects[level][x] = new GameObject[64][];
        }
        if (objects[level][x][y] == null) {
            objects[level][x][y] = new GameObject[4];
        }
        objects[level][x][y][type] = object;
    }

    /**
     * Sets the specified {@link GameObject objects} at the specified local-space coordinate in the region.
     *
     * @param level
     *         the level of the local-space coordinate.
     * @param x
     *         the x of the local-space coordinate.
     * @param y
     *         the y of the local-space coordinate.
     * @param objects
     *         the objects to set at the coordinate
     */
    public void setOriginalObjects(int level, int x, int y, GameObject[] objects) {
        if (this.objects == null) {
            this.objects = new GameObject[4][][][];
        }
        if (this.objects[level] == null) {
            this.objects[level] = new GameObject[64][][];
        }
        if (this.objects[level][x] == null) {
            this.objects[level][x] = new GameObject[64][];
        }
        this.objects[level][x][y] = objects;
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
        return base.transform(localX, localY, level);
    }

    /**
     * Loops through each of the visible players and apply the action to them.
     *
     * @param action
     *         the action to apply for each of the visible players.
     * @param position
     *         the position to check if the player is visible from or {@code null} if there should be no check.
     */
    private void forEachVisiblePlayer(Consumer<Player> action, Position position) {
        for (int index : visiblePlayers) {
            Player player = World.getPlayers().get(index);
            if (player == null || !player.isActive() || position != null && !position.withinScene(player.getCurrentMapCenter())) {
                continue;
            }
            action.accept(player);
        }
    }

    /**
     * Returns a new {@link Position} object that contains the same coordinates as the specified {@code position} but
     * relative to {@link #base} position of this region.
     *
     * @param position
     *         the position to relativize.
     *
     * @return the created {@link Position} object instance.
     */
    public Position relativize(Position position) {
        return new Position(position.getX() - base.getX(), position.getY() - base.getY(), position.getZ());
    }

    /**
     * Returns the region x position.
     *
     * @return the region x position.
     */
    public int getRegionX() {
        return id >> 8;
    }

    /**
     * Returns the region y position.
     *
     * @return the region y position.
     */
    public int getRegionY() {
        return id & 0xff;
    }

    /**
     * Converts the specified object properties into a 32-bit packed integer.
     *
     * @param object
     *         the object to pack it's properties.
     *
     * @return the packed 32-bit integer.
     */
    private static int toPackedProperties(GameObject object) {
        return toPackedProperties(object.getPosition().getZ(), object.getPosition().getInRegionX(), object.getPosition().getInRegionY(), object.getType());
    }

    /**
     * Converts the specified object properties into a 32-bit packed integer.
     *
     * @param level
     *         the level of the local-space of the object.
     * @param x
     *         the x of the local-space of the object.
     * @param y
     *         the y of the local-space of the object.
     * @param type
     *         the type of the object.
     *
     * @return the packed 32-bit integer.
     */
    protected static int toPackedProperties(int level, int x, int y, int type) {
        return level << 14 | x << 8 | y << 2 | type;
    }
}
