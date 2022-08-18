package com.ruse.world.instance;

/**
 * Represents the map instance destroy mode.
 *
 * @author Walied K. Yassen
 */
public enum DestroyMode {
    /**
     * The instance will be manually destroyed.
     */
    MANUAL,

    /**
     * The map instance gets destroyed when there is no more players inside it.
     */
    AUTO_ON_EMPTY,

    /**
     * The map instance gets destroyed when there are no more players and the
     * time has run out
     */
    GROUP,
}
