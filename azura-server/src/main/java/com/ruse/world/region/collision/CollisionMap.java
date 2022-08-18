package com.ruse.world.region.collision;

import com.ruse.world.region.Region;
import com.ruse.world.region.RegionManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * A collision map for a single region, mainly used by our path finding system to detect whether or not a tile can be
 * walked on or is currently blocked.
 *
 * @author Walied K. Yassen
 */
@RequiredArgsConstructor
public final class CollisionMap {

    public static final int FLAG_BLOCKED = 0x200000;
    public static final int FLAG_DECORATION = 0x40000;

    @Getter
    private final int[][][] masks = new int[4][64][64];

    private final int id;
    private final boolean projectiles;

    public void flagDecoration(int level, int x, int y) {
        flag(level, x, y, FLAG_DECORATION);
    }

    public void unflagDecoration(int level, int x, int y) {
        unflag(level, x, y, FLAG_DECORATION);
    }

    public void flagObject(int level, int x, int y, int width, int length, boolean projectileBlocked, boolean notAlternative) {
        int mask = 0x100;
        if (projectileBlocked) {
            mask |= 0x20000;
        }
        if (notAlternative) {
            mask |= 0x40000000;
        }
        for (int fillX = x; fillX < x + width; fillX++) {
            for (int fillY = y; fillY < y + length; fillY++) {
                flag(level, fillX, fillY, mask);
            }
        }
    }

    public void unflagObject(int level, int x, int y, int width, int length, boolean projectileBlocked, boolean strict) {
        int mask = 0x100;
        if (projectileBlocked) {
            mask |= 0x20000;
        }
        if (strict) {
            mask |= 0x40000000;
        }
        for (int fillX = x; fillX < x + width; fillX++) {
            for (int fillY = y; fillY < y + length; fillY++) {
                unflag(level, fillX, fillY, mask);
            }
        }
    }

    public void flagWall(int level, int x, int y, int type, int rotation, boolean projectile_blocked, boolean notAlternative) {
        if (type == 0) {
            if (rotation == 0) {
                flag(level, x, y, 128);
                flag(level, x - 1, y, 8);
            }
            if (rotation == 1) {
                flag(level, x, y, 2);
                flag(level, x, 1 + y, 32);
            }
            if (rotation == 2) {
                flag(level, x, y, 8);
                flag(level, 1 + x, y, 128);
            }
            if (rotation == 3) {
                flag(level, x, y, 32);
                flag(level, x, -1 + y, 2);
            }
        }
        if (type == 1 || type == 3) {
            if (rotation == 0) {
                flag(level, x, y, 1);
                flag(level, -1 + x, 1 + y, 16);
            }
            if (rotation == 1) {
                flag(level, x, y, 4);
                flag(level, 1 + x, 1 + y, 64);
            }
            if (rotation == 2) {
                flag(level, x, y, 16);
                flag(level, x + 1, -1 + y, 1);
            }
            if (rotation == 3) {
                flag(level, x, y, 64);
                flag(level, x - 1, -1 + y, 4);
            }
        }
        if (type == 2) {
            if (rotation == 0) {
                flag(level, x, y, 130);
                flag(level, -1 + x, y, 8);
                flag(level, x, y + 1, 32);
            }
            if (rotation == 1) {
                flag(level, x, y, 10);
                flag(level, x, 1 + y, 32);
                flag(level, 1 + x, y, 128);
            }
            if (rotation == 2) {
                flag(level, x, y, 40);
                flag(level, 1 + x, y, 128);
                flag(level, x, -1 + y, 2);
            }
            if (rotation == 3) {
                flag(level, x, y, 160);
                flag(level, x, -1 + y, 2);
                flag(level, -1 + x, y, 8);
            }
        }
        if (projectile_blocked) {
            if (type == 0) {
                if (rotation == 0) {
                    flag(level, x, y, 0x10000);
                    flag(level, x - 1, y, 0x1000);
                }
                if (rotation == 1) {
                    flag(level, x, y, 0x400);
                    flag(level, x, 1 + y, 0x4000);
                }
                if (rotation == 2) {
                    flag(level, x, y, 0x1000);
                    flag(level, x + 1, y, 0x10000);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x4000);
                    flag(level, x, -1 + y, 0x400);
                }
            }
            if (type == 1 || type == 3) {
                if (rotation == 0) {
                    flag(level, x, y, 512);
                    flag(level, x - 1, y + 1, 8192);
                }
                if (rotation == 1) {
                    flag(level, x, y, 2048);
                    flag(level, x + 1, 1 + y, 32768);
                }
                if (rotation == 2) {
                    flag(level, x, y, 8192);
                    flag(level, x + 1, y - 1, 512);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x8000);
                    flag(level, x - 1, -1 + y, 0x800);
                }
            }
            if (type == 2) {
                if (rotation == 0) {
                    flag(level, x, y, 0x10400);
                    flag(level, -1 + x, y, 0x1000);
                    flag(level, x, y + 1, 0x4000);
                }
                if (rotation == 1) {
                    flag(level, x, y, 0x1400);
                    flag(level, x, y + 1, 0x4000);
                    flag(level, 1 + x, y, 0x10000);
                }
                if (rotation == 2) {
                    flag(level, x, y, 0x5000);
                    flag(level, x + 1, y, 0x10000);
                    flag(level, x, y - 1, 0x400);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x14000);
                    flag(level, x, -1 + y, 0x400);
                    flag(level, x - 1, y, 0x1000);
                }
            }
        }
        if (notAlternative) {
            if (type == 0) {
                if (rotation == 0) {
                    flag(level, x, y, 0x20000000);
                    flag(level, x - 1, y, 0x2000000);
                }
                if (rotation == 1) {
                    flag(level, x, y, 0x800000);
                    flag(level, x, y + 1, 0x8000000);
                }
                if (rotation == 2) {
                    flag(level, x, y, 0x2000000);
                    flag(level, x + 1, y, 0x20000000);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x8000000);
                    flag(level, x, y - 1, 0x800000);
                }
            }
            if (type == 1 || type == 3) {
                if (rotation == 0) {
                    flag(level, x, y, 0x400000);
                    flag(level, x - 1, y + 1, 0x4000000);
                }
                if (rotation == 1) {
                    flag(level, x, y, 0x1000000);
                    flag(level, 1 + x, 1 + y, 0x10000000);
                }
                if (rotation == 2) {
                    flag(level, x, y, 0x4000000);
                    flag(level, x + 1, -1 + y, 0x400000);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x10000000);
                    flag(level, -1 + x, y - 1, 0x1000000);
                }
            }
            if (type == 2) {
                if (rotation == 0) {
                    flag(level, x, y, 0x20800000);
                    flag(level, -1 + x, y, 0x2000000);
                    flag(level, x, 1 + y, 0x8000000);
                }
                if (rotation == 1) {
                    flag(level, x, y, 0x2800000);
                    flag(level, x, 1 + y, 0x8000000);
                    flag(level, x + 1, y, 0x20000000);
                }
                if (rotation == 2) {
                    flag(level, x, y, 0xa000000);
                    flag(level, 1 + x, y, 0x20000000);
                    flag(level, x, y - 1, 0x800000);
                }
                if (rotation == 3) {
                    flag(level, x, y, 0x28000000);
                    flag(level, x, y - 1, 0x800000);
                    flag(level, -1 + x, y, 0x2000000);
                }
            }
        }
    }

    public void unflagWall(int level, int x, int y, int shape, int rotation, boolean solid, boolean notAlternative) {
        if (shape == 0) {
            if (rotation == 0) {
                unflag(level, x, y, 128);
                unflag(level, x - 1, y, 8);
            }
            if (rotation == 1) {
                unflag(level, x, y, 2);
                unflag(level, x, 1 + y, 32);
            }
            if (rotation == 2) {
                unflag(level, x, y, 8);
                unflag(level, 1 + x, y, 128);
            }
            if (rotation == 3) {
                unflag(level, x, y, 32);
                unflag(level, x, -1 + y, 2);
            }
        }
        if (shape == 1 || shape == 3) {
            if (rotation == 0) {
                unflag(level, x, y, 1);
                unflag(level, -1 + x, 1 + y, 16);
            }
            if (rotation == 1) {
                unflag(level, x, y, 4);
                unflag(level, 1 + x, 1 + y, 64);
            }
            if (rotation == 2) {
                unflag(level, x, y, 16);
                unflag(level, x + 1, -1 + y, 1);
            }
            if (rotation == 3) {
                unflag(level, x, y, 64);
                unflag(level, x - 1, -1 + y, 4);
            }
        }
        if (shape == 2) {
            if (rotation == 0) {
                unflag(level, x, y, 130);
                unflag(level, -1 + x, y, 8);
                unflag(level, x, y + 1, 32);
            }
            if (rotation == 1) {
                unflag(level, x, y, 10);
                unflag(level, x, 1 + y, 32);
                unflag(level, 1 + x, y, 128);
            }
            if (rotation == 2) {
                unflag(level, x, y, 40);
                unflag(level, 1 + x, y, 128);
                unflag(level, x, -1 + y, 2);
            }
            if (rotation == 3) {
                unflag(level, x, y, 160);
                unflag(level, x, -1 + y, 2);
                unflag(level, -1 + x, y, 8);
            }
        }
        if (solid) {
            if (shape == 0) {
                if (rotation == 0) {
                    unflag(level, x, y, 0x10000);
                    unflag(level, x - 1, y, 4096);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 1024);
                    unflag(level, x, 1 + y, 16384);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 4096);
                    unflag(level, x + 1, y, 0x10000);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 16384);
                    unflag(level, x, -1 + y, 1024);
                }
            }
            if (shape == 1 || shape == 3) {
                if (rotation == 0) {
                    unflag(level, x, y, 512);
                    unflag(level, x - 1, y + 1, 8192);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 2048);
                    unflag(level, x + 1, 1 + y, 32768);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 8192);
                    unflag(level, x + 1, y - 1, 512);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 32768);
                    unflag(level, x - 1, -1 + y, 2048);
                }
            }
            if (shape == 2) {
                if (rotation == 0) {
                    unflag(level, x, y, 0x10400);
                    unflag(level, -1 + x, y, 4096);
                    unflag(level, x, y + 1, 16384);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 5120);
                    unflag(level, x, y + 1, 16384);
                    unflag(level, 1 + x, y, 0x10000);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 20480);
                    unflag(level, x + 1, y, 0x10000);
                    unflag(level, x, y - 1, 1024);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 0x14000);
                    unflag(level, x, -1 + y, 1024);
                    unflag(level, x - 1, y, 4096);
                }
            }
        }
        if (notAlternative) {
            if (shape == 0) {
                if (rotation == 0) {
                    unflag(level, x, y, 0x20000000);
                    unflag(level, x - 1, y, 0x2000000);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 0x800000);
                    unflag(level, x, y + 1, 0x8000000);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 0x2000000);
                    unflag(level, x + 1, y, 0x20000000);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 0x8000000);
                    unflag(level, x, y - 1, 0x800000);
                }
            }
            if (shape == 1 || shape == 3) {
                if (rotation == 0) {
                    unflag(level, x, y, 0x400000);
                    unflag(level, x - 1, y + 1, 0x4000000);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 0x1000000);
                    unflag(level, 1 + x, 1 + y, 0x10000000);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 0x4000000);
                    unflag(level, x + 1, -1 + y, 0x400000);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 0x10000000);
                    unflag(level, -1 + x, y - 1, 0x1000000);
                }
            }
            if (shape == 2) {
                if (rotation == 0) {
                    unflag(level, x, y, 0x20800000);
                    unflag(level, -1 + x, y, 0x2000000);
                    unflag(level, x, 1 + y, 0x8000000);
                }
                if (rotation == 1) {
                    unflag(level, x, y, 0x2800000);
                    unflag(level, x, 1 + y, 0x8000000);
                    unflag(level, x + 1, y, 0x20000000);
                }
                if (rotation == 2) {
                    unflag(level, x, y, 0xa000000);
                    unflag(level, 1 + x, y, 0x20000000);
                    unflag(level, x, y - 1, 0x800000);
                }
                if (rotation == 3) {
                    unflag(level, x, y, 0x28000000);
                    unflag(level, x, y - 1, 0x800000);
                    unflag(level, -1 + x, y, 0x2000000);
                }
            }
        }
    }

    public void flagBlocked(int level, int x, int y) {
        flag(level, x, y, FLAG_BLOCKED);
    }

    public void unflagBlocked(int level, int x, int y) {
        unflag(level, x, y, FLAG_BLOCKED);
    }

    public void flag(int level, int x, int y, int flag) {
        if (x < 0 || y < 0 || x > 63 || y > 63) {
            x += id >> 8 << 6;
            y += (id & 0xff) << 6;
            Region neighbor = RegionManager.getRegion(x, y);
            if (neighbor == null) {
                return;
            }
            (projectiles ? neighbor.getProjectileCollisionMap() : neighbor.getCollisionMap()).flag(level, x & 0x3f, y & 0x3f, flag);
        } else {
            masks[level][x][y] |= flag;
        }
    }

    public void unflag(int level, int x, int y, int flag) {
        if (x < 0 || y < 0 || x > 63 || y > 63) {
            x += id >> 8 << 6;
            y += (id & 0xff) << 6;
            Region neighbor = RegionManager.getRegion(x, y);
            if (neighbor == null) {
                return;
            }
            (projectiles ? neighbor.getProjectileCollisionMap() : neighbor.getCollisionMap()).unflag(level, x & 0x3f, y & 0x3f, flag);
        } else {
            masks[level][x][y] &= ~flag;
        }
    }
}

