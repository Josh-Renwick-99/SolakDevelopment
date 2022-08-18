package com.ruse.world.content;

import java.util.Iterator;

import com.ruse.model.CombatIcon;
import com.ruse.model.Hit;
import com.ruse.model.Hitmask;
import com.ruse.model.Locations;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.Maxhits;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import static com.ruse.world.content.combat.CombatFactory.npcsDeathDartDontWork;
import static com.ruse.world.content.combat.CombatType.RANGED;

/**
 * 861 100 3000 15 Range

861 = Item ID
100 = Minimum damage
3000 = maximum damage
15 = radius
Range = Weapon combat icon that it'll show.
 * @author HP Laptop
 *
 */
public class AOEHandler {

	public static void handleAttack(Character attacker, Character victim, int minimumDamage, int maximumDamage,
			int radius, CombatIcon combatIcon) {

		// if no radius, loc isn't multi, stops.
		if (radius == 0 || !Locations.Location.inMulti(victim)) {
			// System.out.println("Radius 0");
			return;
		}

		// We passed the checks, so now we do multiple target stuff.
		Iterator<? extends Character> it = null;
		if (attacker.isPlayer() && victim.isPlayer()) {
			it = ((Player) attacker).getLocalPlayers().iterator();
		} else if (attacker.isPlayer() && victim.isNpc()) {
			it = ((Player) attacker).getLocalNpcs().iterator();

			for (Iterator<? extends Character> $it = it; $it.hasNext();) {
				Character next = $it.next();

				if (next == null) {
					continue;
				}

				if (next.isNpc()) {
					NPC n = (NPC) next;
					if (!n.getDefinition().isAttackable() || n.isSummoningNpc()) {
						continue;
					}
				} else {
					Player p = (Player) next;
					if (p.getLocation() != Locations.Location.WILDERNESS || !Locations.Location.inMulti(p)) {
						continue;
					}
				}

				if (next.getPosition().isWithinDistance(victim.getPosition(), radius) && !next.equals(attacker)
						&& !next.equals(victim) && next.getConstitution() > 0) {
					if (next.isNpc() && next.getConstitution() <= 0 && ((NPC)next).isDying()){
						continue;
					}
					if(!RegionClipping.canProjectileAttack(attacker, next)) {
						continue;
					}
					//if (next.getConstitution() <= 0 && !((NPC)next).isDying()){
					//	next.setConstitution(((NPC)next).getDefinition().getHitpoints());
					//}
					int maxhit = maximumDamage;
					switch (((Player) attacker).getLastCombatType()) {
						case MELEE:
							maxhit = Maxhits.melee(attacker, victim) / 10;
							break;
						case RANGED:
							maxhit = Maxhits.ranged(attacker, victim) / 10;
							break;
						case MAGIC:
							maxhit = Maxhits.magic(attacker, victim) / 10;
							break;
					}

					int calc = RandomUtility.inclusiveRandom(minimumDamage, maxhit * 5);
					Player player = (Player) attacker;
					if (player.getEquipment().contains(22006) && player.getLastCombatType() == RANGED){
						NPC npc = (NPC) victim;
						if (!npcsDeathDartDontWork(npc)) {
							calc = victim.getConstitution();
						} else {
							player.sendMessage("The Death-touch dart didn't work on this.");
						}
					}
					next.dealDamage(new Hit(calc, Hitmask.RED, combatIcon));
					next.getCombatBuilder().addDamage(attacker, calc);
					next.getCombatBuilder().attack(attacker);
				}
			}
		}

	}
}
