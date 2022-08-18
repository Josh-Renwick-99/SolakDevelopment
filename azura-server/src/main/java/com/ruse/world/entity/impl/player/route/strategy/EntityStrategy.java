package com.ruse.world.entity.impl.player.route.strategy;


import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.player.route.RouteStrategy;

public class EntityStrategy extends RouteStrategy {

    /**
     * Entity position x.
     */
    private int x;
    /**
     * Entity position y.
     */
    private int y;
    /**
     * Entity size.
     */
    private int size;
    /**
     * Access block flag, see RouteStrategy static final values.
     */
    private int accessBlockFlag;

    public EntityStrategy(Entity entity) {
        this(entity, 0);
    }

    public EntityStrategy(Entity entity, int accessBlockFlag) {
        this(entity.getPosition().getX(),  entity.getPosition().getY(), entity.getSize(), accessBlockFlag);
    }

    public EntityStrategy(int x, int y, int size) {
        this(x, y, size, 0);
    }

    public EntityStrategy(int x, int y, int size, int accessBlockFlag) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.accessBlockFlag = accessBlockFlag;
    }

    @Override
    public boolean canExit(int currentX, int currentY, int sizeXY, int[][] clip, int clipBaseX, int clipBaseY) {
        return RouteStrategy.checkFilledRectangularInteract(clip, currentX - clipBaseX, currentY - clipBaseY, sizeXY, sizeXY, x - clipBaseX, y - clipBaseY, size, size, accessBlockFlag);
    }

    @Override
    public int getApproxDestinationX() {
        return x;
    }

    @Override
    public int getApproxDestinationY() {
        return y;
    }

    @Override
    public int getApproxDestinationSizeX() {
        return size;
    }

    @Override
    public int getApproxDestinationSizeY() {
        return size;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof EntityStrategy))
            return false;
        EntityStrategy strategy = (EntityStrategy) other;
        return x == strategy.x && y == strategy.y && size == strategy.size && accessBlockFlag == strategy.accessBlockFlag;
    }

}
