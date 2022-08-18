package com.ruse.world.content.achievement;

import com.ruse.world.entity.impl.player.Player;

/**
 * @author Leviticus http://www.rune-server.org/members/leviticus
 * @date May, 9, 2018
 */
public abstract class AchievementRequirement {
    /**
     * @param player
     * @return The state at which the Player object has the ability or pre-requisites
     */
    abstract boolean isAble(Player player);
}
