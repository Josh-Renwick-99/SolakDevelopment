package com.ruse.world.entity.object;

import com.ruse.world.region.Region;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Optional;

/**
 * The enumerated type whose elements represent all of the object types.
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author Maxi <http://www.rune-server.org/members/maxi/>
 * @author lare96 <http://github.com/lare96>
 * @author Artem batutin <artembatutin@gmail.com>
 */
public enum ObjectShape {
	
	/**
	 * Represents straight walls, fences etc.
	 */
	STRAIGHT_WALL(0),
	
	/**
	 * Represents diagonal walls corner, fences etc connectors.
	 */
	DIAGONAL_CORNER_WALL(1),
	
	/**
	 * Represents entire walls, fences etc corners.
	 */
	ENTIRE_WALL(2),
	
	/**
	 * Represents straight wall corners, fences etc connectors.
	 */
	WALL_CORNER(3),
	
	/**
	 * Represents straight inside wall decorations.
	 */
	STRAIGHT_INSIDE_WALL_DECORATION(4),
	
	/**
	 * Represents straight outside wall decorations.
	 */
	STRAIGHT_OUTSIDE_WALL_DECORATION(5),
	
	/**
	 * Represents diagonal outside wall decorations.
	 */
	DIAGONAL_OUTSIDE_WALL_DECORATION(6),
	
	/**
	 * Represents diagonal inside wall decorations.
	 */
	DIAGONAL_INSIDE_WALL_DECORATION(7),
	
	/**
	 * Represents diagonal in wall decorations.
	 */
	DIAGONAL_INTERIOR_WALL_DECORATION(8),
	
	/**
	 * Represents diagonal walls, fences etc.
	 */
	DIAGONAL_WALL(9),
	
	/**
	 * Represents all kinds of objects, trees, statues, signs, fountains etc.
	 */
	GENERAL_PROP(10),
	
	/**
	 * Represents ground objects like daisies etc.
	 */
	WALKABLE_PROP(11),
	
	/**
	 * Represents straight sloped roofs.
	 */
	STRAIGHT_SLOPED_ROOF(12),
	
	/**
	 * Represents diagonal sloped roofs.
	 */
	DIAGONAL_SLOPED_ROOF(13),
	
	/**
	 * Represents diagonal slope connecting roofs.
	 */
	DIAGONAL_SLOPED_CONNECTING_ROOF(14),
	
	/**
	 * Represents straight sloped corner connecting roofs.
	 */
	STRAIGHT_SLOPED_CORNER_CONNECTING_ROOF(15),
	
	/**
	 * Represents straight sloped corner roofs.
	 */
	STRAIGHT_SLOPED_CORNER_ROOF(16),
	
	/**
	 * Represents straight flat top roofs.
	 */
	STRAIGHT_FLAT_TOP_ROOF(17),
	
	/**
	 * Represents straight bottom arrav roofs.
	 */
	STRAIGHT_BOTTOM_EDGE_ROOF(18),
	
	/**
	 * Represents diagonal bottom arrav connecting roofs.
	 */
	DIAGONAL_BOTTOM_EDGE_CONNECTING_ROOF(19),
	
	/**
	 * Represents straight bottom arrav connecting roofs.
	 */
	STRAIGHT_BOTTOM_EDGE_CONNECTING_ROOF(20),
	
	/**
	 * Represents straight bottom arrav connecting corner roofs.
	 */
	STRAIGHT_BOTTOM_EDGE_CONNECTING_CORNER_ROOF(21),
	
	/**
	 * Represents ground decoration + map signs (quests, water fountains, shops, etc)
	 */
	GROUND_PROP(22);
	
	/**
	 * The identification of this type.
	 */
	private final int id;
	
	/**
	 * The {@link ObjectType} this type is associated to.
	 */
	private final ObjectType type;
	
	/**
	 * Creates a new {@link ObjectShape}.
     * @param id the identification of this type
     *
     */
	ObjectShape(int id) {
		this.id = id;
		this.type = ObjectType.valueOf(Region.SHAPE_TO_TYPE[id]).get();
	}
	
	/**
	 * Gets the identification of this type.
	 * @return the identification of this type.
	 */
	public final int getId() {
		return id;
	}
	
	/**
	 * Gets the group of this type.
	 * @return the group of this type.
	 */
	public final ObjectType getType() {
		return type;
	}
	
	/**
	 * A mutable {@link Int2ObjectOpenHashMap} of {@code int} keys to {@link ObjectShape}
	 * values.
	 */
	private static final Int2ObjectOpenHashMap<ObjectShape> values = new Int2ObjectOpenHashMap<>();
	
	/*
	 * Populates the {@link #values} cache.
	 */
	static {
		for(ObjectShape type : values()) {
			values.put(type.getId(), type);
		}
	}
	
	/**
	 * Returns a {@link ObjectShape} wrapped in an {@link Optional} for the
	 * specified {@code id}.
	 * @param id The game object type id.
	 * @return The optional game object type.
	 */
	public static Optional<ObjectShape> valueOf(int id) {
		return Optional.ofNullable(values.get(id));
	}
	
	/**
	 * Gets the next object type from the list.
	 * @return the next object type.
	 */
	public ObjectShape toggle() {
		int next = getId() + 1;
		if(next == 23)
			next = 0;
		return valueOf(next).get();
	}
	
}