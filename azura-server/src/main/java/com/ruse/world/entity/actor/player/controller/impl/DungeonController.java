package com.ruse.world.entity.actor.player.controller.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.GroundItem;
import com.ruse.model.Hit;
import com.ruse.model.Position;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringBossNpc;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.actor.player.controller.Controller;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

// This class is used through reflection!
public class DungeonController extends Controller {

    // TODO: possibly use these for point calculation in the future?
    private int damageReceived;
    private int meleeDamage, rangeDamage, mageDamage, mixedDamage;
    Dungeoneering dungeon;

    public void start() {
        //this.dungeon = dungeon;
        dungeon = (Dungeoneering) getArguments()[0];
        setArguments(null); // because arguments save on char and we dont want to save dungmanager
        player.setForceMultiArea(true);
    }

    public void reset() {
        damageReceived = 0;
        meleeDamage = 0;
        rangeDamage = 0;
        mageDamage = 0;

    }


    /*
     *the dmg you receiving
     */
    public void processIncommingHit(Hit hit, Entity target) {
        damageReceived += hit.getDamage();
    }

    /*
     * the dmg you doing
     */
    public void processOutgoingHit(CombatContainer container) {

        // Keeping track of each style of damage
        if (container.getCombatType() == CombatType.MELEE)
            meleeDamage += container.getDamage();
        else if (container.getCombatType() == CombatType.RANGED)
            rangeDamage += container.getDamage();
        else if (container.getCombatType() == CombatType.MAGIC)
            mageDamage += container.getDamage();
        else if (container.getCombatType() == CombatType.MIXED)
            mixedDamage += container.getDamage();

        // Track in player attributes
        player.getMinigameAttributes().getDungeoneeringAttributes().incrementDamageDealt(container.getDamage());
    }

    public int getDamageReceived() {
        return damageReceived;
    }

    public int getMeleeDamage() {
        return meleeDamage;
    }

    public int getRangeDamage() {
        return rangeDamage;
    }

    public int getMageDamage() {
        return mageDamage;
    }

    public int getDamage() {
        return meleeDamage + rangeDamage + mageDamage + mixedDamage;
    }

    public void sendInterfaces() {

    }

    @Override
    public void processNPCDeath(NPC npc) {
        if(dungeon == null) {
            return;
        }

        if(npc.getId() == Dungeoneering.Constants.MINION_MOB) {
            dungeon.setMinionKills(dungeon.getMinionKills() + 1);
        } else if(DungeoneeringBossNpc.Companion.isBoss(npc.getId())) {
            dungeon.setBossDead(true);
        }
        dungeon.check();
        //player.sendMessage("KC " + dungeon.getMinionKills());
    }

    @Override
    public boolean appendDeath() {
        player.getPacketSender().sendInterfaceRemoval();
        player.getMovementQueue().setLockMovement(true).reset();

        TaskManager.submit(new Task(1, player, false) {
            int loop;

            @Override
            public void execute() {
                if (loop == 0) {
                    player.performAnimation(new Animation(836));
                } else if (loop == 1) {
                    player.getPA().sendMessage("Oh dear, you are dead!");
                    CurseHandler.deactivateAll(player);
                    PrayerHandler.deactivateAll(player);
                    if (dungeon != null) {
                        for (Player p2 : dungeon.getGroup().getPlayers()) {
                            if (p2 == player)
                                continue;
                            p2.getPA().sendMessage(player.getUsername() + " has died.");
                        }
                    }
                } else if (loop == 3) {
                    if (dungeon != null && dungeon.getGroup().getPlayers().contains(player)) {
                        Position startRoom = new Position(2285, 4999, dungeon.getHeight());
                        player.moveTo(startRoom);
                        increaseDeaths(player);
                        player.restart();
                        player.setDying(false);
                    }
                    player.performAnimation(new Animation(-1));
                } else if (loop == 4) {
                    stop();
                }
                loop++;
            }
        });
        return false;
    }


    public void increaseDeaths(Player player) {
        Integer deaths = dungeon.getPartyDeaths().get(player.getUsername());
        //player.sendMessage("Increasing deaths " +deaths + " +1");
        if (deaths == null)
            deaths = 0;
        else if (deaths == 15)
            return;
        dungeon.getPartyDeaths().put(player.getUsername(), deaths + 1);
        player.getMinigameAttributes().getDungeoneeringAttributes().incrementDeaths();
    }

    public int getDeaths(Player player) {
        if (dungeon == null)
            return 0;
        Integer deaths = dungeon.getPartyDeaths().get(player.getUsername());
        return deaths == null ? 0 : deaths;
    }

    @Override
    public boolean processMagicTeleport(Position toTile) {
        return true;
    }

    @Override
    public boolean processItemTeleport(Position toTile) {
        return false;
    }

    @Override
    public boolean canTakeItem(GroundItem item) {
        return true;
    }
}
