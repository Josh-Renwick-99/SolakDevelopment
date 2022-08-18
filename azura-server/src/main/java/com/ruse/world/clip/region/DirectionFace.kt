package com.ruse.world.clip.region

import com.ruse.model.Position
import com.ruse.util.Misc


/**
 * Represents a single movement direction.
 * @author Graham
 */
enum class DirectionFace
/**
 * Creates the direction.
 * @param intValue The direction as an integer.
 */
private constructor(
        /**
         * The direction as an integer.
         */
        private val intValue: Int,
        /**
         * two deltas.
         */
        val x: Int, val y: Int) {

    /**
     * North movement.
     */
    NORTH(1, 0, 1),

    /**
     * North east movement.
     */
    NORTH_EAST(2, 1, 1),

    /**
     * East movement.
     */
    EAST(4, 1, 0),

    /**
     * South east movement.
     */
    SOUTH_EAST(7, 1, -1),

    /**
     * South movement.
     */
    SOUTH(6, 0, -1),

    /**
     * South west movement.
     */
    SOUTH_WEST(5, -1, -1),

    /**
     * West movement.
     */
    WEST(3, -1, 0),

    /**
     * North west movement.
     */
    NORTH_WEST(0, -1, 1),

    /**
     * No movement.
     */
    NONE(-1, 0, 0);

    /**
     * Gets the direction as an integer which the client can understand.
     * @return The movement as an integer.
     */
    fun toInteger(): Int {
        return intValue
    }
    /**
     * Gets the X delta from a [Position] of (0, 0).
     *
     * @return The delta of X from (0, 0).
     */
    fun deltaX(): Int {
        return when (this) {
            SOUTH_EAST, NORTH_EAST, EAST -> 1
            SOUTH_WEST, NORTH_WEST, WEST -> -1
            else -> 0
        }
    }

    /**
     * Gets the Y delta from a [Position] of (0, 0).
     *
     * @return The delta of Y from (0, 0).
     */
    fun deltaY(): Int {
        return when (this) {
            NORTH_WEST, NORTH_EAST, NORTH -> 1
            SOUTH_WEST, SOUTH_EAST, SOUTH -> -1
            else -> 0
        }
    }
    /**
     * Check if this direction is a diagonal direction.
     *
     * @return `true` if this direction is a diagonal direction, `false` otherwise.
     */
    fun isDiagonal(): Boolean {
        return this == SOUTH_EAST || this == SOUTH_WEST || this == NORTH_EAST || this == NORTH_WEST
    }
    companion object {
        /**
         * Get the 2 directions which make up a diagonal direction (i.e., NORTH and EAST for NORTH_EAST).
         *
         * @param directionFace The direction to get the components for.
         * @return The components for the given direction.
         */
        fun diagonalComponents(directionFace: DirectionFace): Array<DirectionFace> {
            when (directionFace) {
                NORTH_EAST -> return arrayOf(NORTH, EAST)
                NORTH_WEST -> return arrayOf(NORTH, WEST)
                SOUTH_EAST -> return arrayOf(SOUTH, EAST)
                SOUTH_WEST -> return arrayOf(SOUTH, WEST)
            }

            throw IllegalArgumentException("Must provide a diagonal direction.")
        }

        var DIRECTIONS = values()

        /**
         * Creates a direction from the differences between X and Y.
         * @param deltaX The difference between two X coordinates.
         * @param deltaY The difference between two Y coordinates.
         * @return The direction.
         */
        fun fromDeltas(deltaX: Int, deltaY: Int): DirectionFace {
            return if (deltaY == 1) {
                if (deltaX == 1) {
                    NORTH_EAST
                } else if (deltaX == 0) {
                    NORTH
                } else {
                    NORTH_WEST
                }
            } else if (deltaY == -1) {
                if (deltaX == 1) {
                    SOUTH_EAST
                } else if (deltaX == 0) {
                    SOUTH
                } else {
                    SOUTH_WEST
                }
            } else if (deltaX == 1) {
                EAST
            } else if (deltaX == -1) {
                return WEST
            } else {
                NONE
            }
        }

        /**
         * Creates a direction from the differences between X and Y.
         * @param delta The delta position
         * @return The direction.
         */
        fun fromDeltas(delta: Position): DirectionFace {
            val dx = delta.x
            val dy = delta.y
            return if (dx < 0) {
                if (dy < 0) {
                    SOUTH_WEST
                } else if (dy > 0) {
                    NORTH_WEST
                } else {
                    WEST
                }
            } else if (dx > 0) {
                if (dy < 0) {
                    SOUTH_EAST
                } else if (dy > 0) {
                    NORTH_EAST
                } else {
                    EAST
                }
            } else {
                if (dy < 0) {
                    SOUTH
                } else if (dy > 0) {
                    NORTH
                } else {
                    NONE
                }
            }
        }

        /**
         * Gets the Direction between the two [Position]s..
         *
         * @param current The difference between two X coordinates.
         * @param next The difference between two Y coordinates.
         * @return The direction.
         */
        fun between(current: Position, next: Position): DirectionFace {
            val deltaX = next.x - current.x
            val deltaY = next.y - current.y

            return fromDeltas(Integer.signum(deltaX), Integer.signum(deltaY))
        }

        /**
         * gets random direction.
         * @return random dir.
         */
        fun random(): DirectionFace {
            return DIRECTIONS[Misc.getRandom(7)]
        }
    }

}