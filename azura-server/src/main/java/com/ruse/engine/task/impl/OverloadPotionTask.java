package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.Animation;
import com.ruse.model.CombatIcon;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerRights;
import com.ruse.model.Skill;
import com.ruse.world.content.Consumables;
import com.ruse.world.entity.impl.player.Player;

public class OverloadPotionTask extends Task {

	public OverloadPotionTask(Player player) {
		super(1, player, true);
		this.player = player;
	}

	final Player player;

	@Override
	public void execute() {
		if (player == null || !player.isRegistered()) {
			stop();
			return;
		}
		int timer = player.getOverloadPotionTimer();
		if (timer == 125 || timer == 122 || timer == 119 || timer == 116 || timer == 113) {
			player.performAnimation(new Animation(3170));
			player.dealDamage(new Hit(100, Hitmask.RED, CombatIcon.NONE));
		}
		if (timer == 125 || timer == 120 || timer == 110 || timer == 90 || timer == 80 || timer == 60 || timer == 50
				|| timer == 40 || timer == 30) {
			Consumables.overloadIncrease(player, Skill.ATTACK, 0.38);
			Consumables.overloadIncrease(player, Skill.STRENGTH, 0.38);
			Consumables.overloadIncrease(player, Skill.DEFENCE, 0.38);
			Consumables.overloadIncrease(player, Skill.RANGED, 0.38);
			Consumables.overloadIncrease(player, Skill.MAGIC, 0.38);
		}
		player.setOverloadPotionTimer(timer - 1);
		if (player.getOverloadPotionTimer() == 10)
			player.getPacketSender().sendMessage("@red@Your Overload's effect is about to run out.");
		if (player.getOverloadPotionTimer() <= 0 || player.getLocation() == Location.DUEL_ARENA
				|| player.getLocation() == Location.DUNGEONEERING
				|| player.getLocation() == Location.WILDERNESS && player.getRights() != PlayerRights.DEVELOPER) {
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
