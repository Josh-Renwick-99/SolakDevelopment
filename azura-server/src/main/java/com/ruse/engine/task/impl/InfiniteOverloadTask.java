package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.*;
import com.ruse.world.content.Consumables;
import com.ruse.world.entity.impl.player.Player;

public class InfiniteOverloadTask extends Task {

    public InfiniteOverloadTask(Player player, int time) {
        super(1, player, true);
        this.player = player;
        this.time = time;
    }

    int time;
    final Player player;

    @Override
    public void execute() {
        if (player == null || !player.isRegistered()) {
            stop();
            return;
        }
        int timer = player.getOverloadPotionTimer();

        if (timer == time * 2 || timer == (time * 2) - 6 || timer == (time * 2) - 12|| timer == (time * 2) - 18 || timer == (time * 2) - 24) {
            player.performAnimation(new Animation(3170));
            player.dealDamage(new Hit(100, Hitmask.RED, CombatIcon.NONE));
        }

        if (time == 300) {
            Consumables.levelIncrease(player, Skill.ATTACK, 130);
            Consumables.levelIncrease(player, Skill.STRENGTH, 130);
            Consumables.levelIncrease(player, Skill.DEFENCE, 130);
            Consumables.levelIncrease(player, Skill.RANGED, 130);
            Consumables.levelIncrease(player, Skill.MAGIC, 130);
        }

        if (time == 400) {
            Consumables.levelIncrease(player, Skill.ATTACK, 150);
            Consumables.levelIncrease(player, Skill.STRENGTH, 150);
            Consumables.levelIncrease(player, Skill.DEFENCE, 150);
            Consumables.levelIncrease(player, Skill.RANGED, 150);
            Consumables.levelIncrease(player, Skill.MAGIC, 150);
        }

        if (time == 500) {
            Consumables.levelIncrease(player, Skill.ATTACK, 180);
            Consumables.levelIncrease(player, Skill.STRENGTH, 180);
            Consumables.levelIncrease(player, Skill.DEFENCE, 180);
            Consumables.levelIncrease(player, Skill.RANGED, 180);
            Consumables.levelIncrease(player, Skill.MAGIC, 180);
        }

        player.setOverloadPotionTimer(timer - 1);
        if (player.getOverloadPotionTimer() == 20)
            player.getPacketSender().sendMessage("@red@Your Overload's effect is about to run out.");
        if (player.getOverloadPotionTimer() <= 0 || player.getLocation() == Locations.Location.DUEL_ARENA
                || player.getLocation() == Locations.Location.DUNGEONEERING
                || player.getLocation() == Locations.Location.WILDERNESS && player.getRights() != PlayerRights.DEVELOPER) {
            player.getPacketSender().sendMessage("@red@Your Overload's effect has run out.");
            for (int i = 0; i < 7; i++) {
                if (i == 3 || i == 5)
                    continue;
                player.getSkillManager().setCurrentLevel(Skill.forId(i), player.getSkillManager().getMaxLevel(i));
            }
            player.setOverloadPotionTimer(0);
            stop();
        }
    }
}
