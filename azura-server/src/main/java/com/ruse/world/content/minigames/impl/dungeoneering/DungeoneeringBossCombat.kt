package com.ruse.world.content.minigames.impl.dungeoneering


import com.ruse.model.Animation
import com.ruse.model.Graphic
import com.ruse.model.GraphicHeight
import com.ruse.model.Projectile
import com.ruse.util.Misc
import com.ruse.world.content.combat.CombatContainer
import com.ruse.world.content.combat.CombatType
import com.ruse.world.content.combat.strategy.CombatStrategy
import com.ruse.world.entity.impl.Character
import com.ruse.world.entity.impl.player.Player


object DungeoneeringBossCombat : CombatStrategy {

    //TODO replace
    private val meleeAnim = Animation(1312)
    private val explosionGfx = Graphic(2399)
    private val mageAttackGfx = Graphic(2413)
    private val mageHitGfx = Graphic(2417, 80, GraphicHeight.LOW)
    private val mageProj = Graphic(2414)
    private val rangeAttackGfx = Graphic(2394)
    private val rangeHitGfx = Graphic(2396, 80, GraphicHeight.LOW)
    private val rangeProj = Graphic(2395)

    override fun customContainerAttack(entity: Character, victim: Character): Boolean {
        if (entity is DungeoneeringBossNpc && victim is Player) {
            normalAttacks(entity, victim)
            return true
        }
        return false
    }

    private fun normalAttacks(npc: DungeoneeringBossNpc, player: Player) {

        val distanced = !Misc.isOnRange(npc.position.x, npc.position.y, npc.size, player.position.x, player.position.y, player.size, 0)
        var attack = Misc.random(2)
        if (attack == 0 && distanced)
            attack = Misc.random(1, 2);
        when (attack) {
            0 -> { //melee
                npc.animation = meleeAnim
                npc.combatBuilder.container = CombatContainer(npc, player, 1, 0, CombatType.MELEE, true)
            }
            1 -> { //green exploding blob attack (magic)
                npc.animation = meleeAnim
                npc.graphic = mageAttackGfx
                Projectile(npc, player, mageProj.id, 41, 50, 41, 16, 0).sendProjectile()
                npc.combatBuilder.container = CombatContainer(npc, player, 1, 1, CombatType.MAGIC, true)
                player.graphic = mageHitGfx
            }
            2 -> { //green blob attack (range)
                npc.animation = meleeAnim
                npc.graphic = rangeAttackGfx
                Projectile(npc, player, rangeProj.id, 41, 50, 41, 16, 0).sendProjectile()
                npc.combatBuilder.container = CombatContainer(npc, player, 1, 1, CombatType.RANGED, true)
                player.graphic = rangeHitGfx
            }
        }
    }

    override fun getCombatType() = CombatType.MIXED

    override fun canAttack(entity: Character, victim: Character) = true

    override fun attackDelay(entity: Character) = 7

    override fun attackDistance(entity: Character) = 30

    override fun attack(entity: Character, victim: Character) = null
}