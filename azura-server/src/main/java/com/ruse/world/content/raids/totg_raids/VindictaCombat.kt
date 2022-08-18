package com.ruse.world.content.raids.totg_raids


import com.ruse.engine.task.Task
import com.ruse.engine.task.TaskManager
import com.ruse.model.*
import com.ruse.util.Misc
import com.ruse.world.World
import com.ruse.world.content.combat.CombatContainer
import com.ruse.world.content.combat.CombatType
import com.ruse.world.content.combat.strategy.CombatStrategy
import com.ruse.world.content.combat.strategy.impl.ScarletFalcon
import com.ruse.world.content.raids.RaidsBoss
import com.ruse.world.entity.impl.Character
import com.ruse.world.entity.impl.npc.NPC
import com.ruse.world.entity.impl.player.Player
import java.util.*


public object VindictaCombat : CombatStrategy {

    private val meleeAnim = Animation(1312)

    private val mageHitGfx = Graphic(89, 80, GraphicHeight.LOW)
    private val mageProj = Graphic(88)

    private val rangeHitGfx = Graphic(287, 80, GraphicHeight.LOW)
    private val rangeProj = Graphic(286)

    override fun customContainerAttack(entity: Character, victim: Character): Boolean {
        if (entity is RaidsBoss && victim is Player) {
            normalAttacks(entity, victim)
            return true
        }
        return false
    }

    private fun normalAttacks(npc: RaidsBoss, player: Player) {

        val distanced = !Misc.isOnRange(npc.position.x, npc.position.y, npc.size, player.position.x, player.position.y, player.size, 0)
        var spec = Misc.random(100)
        if(spec == 0) {
            specialAttack(npc, player)
            return
        }
        var attack = Misc.random(2)
        if (attack == 0 && distanced)
            attack = Misc.random(1, 2)
        when (attack) {
            0 -> { //melee
                 meleeAttack(npc, player)
            }
            1 -> { //green exploding blob attack (magic)
                magicAttack(npc, player)
            }
            2 -> { //green blob attack (range)
                rangeAttack(npc, player)
            }
        }
    }
    private fun meleeAttack(npc: RaidsBoss, player: Player) {
        npc.animation = meleeAnim
        npc.combatBuilder.container = CombatContainer(npc, player, 1, 0, CombatType.MELEE, true)
    }

    private fun specialAttack(boss: RaidsBoss, victim: Player) {
        boss.isChargingAttack = true
        boss.performAnimation(Animation(2368))
        boss.forceChat("Click clack click click clack")
        TaskManager.submit(object : Task(1, boss, false) {
            var tick = 0
            override fun execute() { // every 2 ticks for 5 seconds
                boss.performAnimation(Animation(1316))
                if (tick < 10 && tick % 2 != 0) {
                    // try using
                    for (t in boss.possibleTargets) {
                        if(t == null) continue
                        val target = t as Player
                        if (Locations.goodDistance(target, boss, 6)) {
                            target.performGraphic(Graphic(60))
                            target.dealDamage(Hit(500, Hitmask.RED, CombatIcon.NONE))
                        }
                    }
                } else if (tick == 11) {
                    boss.isChargingAttack = false
                    stop()
                }
                tick++
            }
        })
    }

    private fun magicAttack(npc: RaidsBoss, player: Player) {
        npc.animation = meleeAnim
        Projectile(npc, player, mageProj.id, 41, 50, 41, 16, 0).sendProjectile()
        npc.combatBuilder.container = CombatContainer(npc, player, 1, 1, CombatType.MAGIC, true)
        player.graphic = mageHitGfx
    }
    private fun rangeAttack(npc: RaidsBoss, player: Player) {
        npc.animation = meleeAnim
        Projectile(npc, player, rangeProj.id, 41, 50, 41, 16, 0).sendProjectile()
        npc.combatBuilder.container = CombatContainer(npc, player, 1, 1, CombatType.RANGED, true)
        player.graphic = rangeHitGfx
    }
    override fun getCombatType() = CombatType.MIXED

    override fun canAttack(entity: Character, victim: Character) = true

    override fun attackDelay(entity: Character) = 7

    override fun attackDistance(entity: Character) = 30

    override fun attack(entity: Character, victim: Character) = null
}