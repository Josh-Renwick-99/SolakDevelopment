package com.ruse.world.instance;

import com.ruse.model.Position;
import com.ruse.world.entity.impl.player.Player;

/**
 * Represents a cerberus map instance.
 *
 * @author Walied K. Yassen
 */
public final class TestInstance extends BossInstance {

    /**
     * Constructs a new {@link TestInstance} type object instance.
     */
    public TestInstance() {
        super(DestroyMode.AUTO_ON_EMPTY, 400, 400, 4, 4);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNpcs() {
        createNpc(422, 22, 35, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getTeleportPosition(Player player) {
        return toPosition(0, 0);
    }
}

