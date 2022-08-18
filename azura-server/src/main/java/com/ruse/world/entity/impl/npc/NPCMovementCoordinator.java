package com.ruse.world.entity.impl.npc;

import com.ruse.model.Direction;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.movement.PathFinder;
import com.ruse.util.Misc;
import com.ruse.world.clip.region.RegionClipping;

/**
 * Will make all {@link NPC}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 *
 * @author lare96
 */
public class NPCMovementCoordinator {

    /**
     * The npc we are coordinating movement for.
     */
    private NPC npc;

    /**
     * The coordinate state this npc is in.
     */
    private CoordinateState coordinateState;

    /**
     * The coordinator for coordinating movement.
     */
    private Coordinator coordinator;

    public NPCMovementCoordinator(NPC npc) {
        this.npc = npc;
        this.coordinator = new Coordinator();
        this.coordinateState = CoordinateState.HOME;
    }

    public void sequence() {
        if (coordinateState == CoordinateState.HOME && !coordinator.isCoordinate()) {
            return;
        }
        updateCoordinator();
        switch (coordinateState) {
            case HOME:
                if (npc.getCombatBuilder().isBeingAttacked() || npc.getCombatBuilder().isAttacking())
                    return;
                if (npc.getMovementQueue().isMovementDone()) {
                    if (Misc.getRandom(10) <= 1) {
                        Position pos = generateLocalPosition();

                        if (pos != null && npc.getMovementQueue().canWalk(npc.getPosition(), npc.getPosition().copy().transform(pos), npc.getSize())) {

                            Position newPos = npc.getPosition().copy().transform(pos);
                           // PathFinder.findPath(npc, newPos.getX(),
                            //        newPos.getY(), false, npc.getSize(),
                            //        npc.getSize());

                            npc.getMovementQueue().walkStep(pos.getX(), pos.getY());
                        }
                    }
                }

                break;
            case AWAY:
                npc.getCombatBuilder().reset(true);
                coordinateState = CoordinateState.HOME;
                com.ruse.world.entity.impl.Character.walk(npc, npc.getDefaultPosition().getX(), npc.getDefaultPosition().getY(), 0);
                break;
        }
    }

    public void updateCoordinator() {
        int deltaX = npc.getPosition().getX() - npc.getDefaultPosition().getX();
        int deltaY = npc.getPosition().getY() - npc.getDefaultPosition().getY();

        if ((deltaX > coordinator.getRadius()) || (deltaY > coordinator.getRadius())) {
            if (Location.ignoreFollowDistance(npc) || npc.getMovementQueue().getFollowCharacter() != null
                    || npc.getCombatBuilder().isAttacking() || npc.getCombatBuilder().isBeingAttacked()) {
                return;
            }
            coordinateState = CoordinateState.AWAY;
        } else {
            coordinateState = CoordinateState.HOME;
        }
    }

    private Position generateLocalPosition() {
        int dir = -1;
        int x = 0, y = 0;

        if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH, npc.getSize())) {
            dir = 0;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.EAST, npc.getSize())) {
            dir = 4;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH, npc.getSize())) {
            dir = 8;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.WEST, npc.getSize())) {
            dir = 12;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH_EAST, npc.getSize())) {
            dir = 2;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH_EAST, npc.getSize())) {
            dir = 6;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH_WEST, npc.getSize())) {
            dir = 10;
        } else if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH_WEST, npc.getSize())) {
            dir = 14;
        }
        int random = Misc.getRandom(7);

        boolean found = false;

        if(random == 0) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH, npc.getSize())) {
                y = 1;
                found = true;
            }
        } else if(random == 1) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.EAST, npc.getSize())) {
                x = 1;
                found = true;
            }
        } else if(random == 2) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH, npc.getSize())) {
                y = -1;
                found = true;
            }
        } else if(random == 3) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.WEST, npc.getSize())) {
                x = -1;
                found = true;
            }
        } else if(random == 4) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH_EAST, npc.getSize())) {
                y = 1;
                x = 1;
                found = true;
            }
        } else if(random == 5) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH_EAST, npc.getSize())) {
                x = 1;
                y = -1;
                found = true;
            }
        } else if(random == 6) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.SOUTH_WEST, npc.getSize())) {
                x = -1;
                y = -1;
                found = true;
            }
        } else if(random == 7) {
            if(RegionClipping.canWalk(npc.getPosition(), Direction.NORTH_WEST, npc.getSize())) {
                x = -1;
                y = 1;
                found = true;
            }
        }

        if(!found) {
            if(dir == 0) {
                y = 1;
            } else if(dir == 4) {
                x = 1;
            } else if(dir == 8) {
                y = -1;
            } else if(dir == 12) {
                x = -1;
            } else if(dir == 2) {
                y = 1;
                x = 1;
            } else if(dir == 6) {
                x = 1;
                y = -1;
            } else if(dir == 10) {
                x = -1;
                y = -1;
            } else if(dir == 14) {
                x = -1;
                y = 1;
            }
        }
        if(x == 0 && y == 0)
            return null;
        int spawnX = npc.getDefaultPosition().getX();
        int spawnY = npc.getDefaultPosition().getY();
        if(x > 0) {
            if(npc.getPosition().getX() + x > spawnX + 1)
                return null;
        }
        if(x < 0) {
            if(npc.getPosition().getX() - x < spawnX - 1)
                return null;
        }
        if(y > 0) {
            if(npc.getPosition().getY() + y > spawnY + 1)
                return null;
        }
        if(y < 0) {
            if(npc.getPosition().getY() - y < spawnY - 1)
                return null;
        }
        return new Position(x, y);
    }


    public Coordinator getCoordinator() {
        return this.coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public CoordinateState getCoordinateState() {
        return coordinateState;
    }

    public void setCoordinateState(CoordinateState coordinateState) {
        this.coordinateState = coordinateState;
    }

    public enum CoordinateState {
        HOME, AWAY
    }

    public static class Coordinator {

        private boolean coordinate;
        private int radius;

        public Coordinator(boolean coordinate, int radius) {
            this.coordinate = coordinate;
            this.radius = radius;
        }
        public Coordinator() {

        }

        public boolean isCoordinate() {
            return coordinate;
        }

        public Coordinator setCoordinate(boolean coordinate) {
            this.coordinate = coordinate;
            return this;
        }

        public int getRadius() {
            return radius;
        }

        public Coordinator setRadius(int radius) {
            this.radius = radius;
            return this;
        }
    }
}