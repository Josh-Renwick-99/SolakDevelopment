package com.ruse.world.content.summer_event

import com.ruse.model.Position
import com.ruse.model.definitions.NPCDrops
import com.ruse.world.World
import com.ruse.world.content.combat.CombatFactory
import com.ruse.world.entity.impl.npc.NPC
import com.ruse.world.entity.impl.player.Player
import java.util.*

class SummerBoss(npcId: Int, position: Position?) : NPC(npcId, position) {
    init {
        respawn = false
    }

    override fun appendDeath() {
        super.appendDeath()
        if(this == SummerEventHandler.currentBoss) {

            for(npc in SummerBossCombat.npcList) {
                World.deregister(npc)
            }
            SummerBossCombat.npcList.clear()
            World.deregister(SummerEventHandler.currentBoss)
            SummerEventHandler.currentBoss = null
        }

    }

    fun handleDrop() {
        if (combatBuilder.damageMap.isEmpty()) {
            return
        }
        val killers: MutableMap<Player, Int> = HashMap()
        for (entry in combatBuilder.damageMap.entries) {
            if (entry == null) {
                continue
            }
            val timeout = entry.value.stopwatch.elapsed()
            if (timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT) {
                continue
            }
            val player = entry.key
            if (player.constitution <= 0 || !player.isRegistered) {
                continue
            }
            killers[player] = entry.value.damage
        }
        combatBuilder.damageMap.clear()
        val result = sortEntries(killers)
        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val (killer) = iterator.next()
            println("Handling drop for ${killer.username}")
            NPCDrops.handleDrops(killer, this)
            iterator.remove()
        }

        World.deregister(SummerEventHandler.currentBoss)//causes nullpointer nucky..
        SummerEventHandler.currentBoss = null
    }

    /**
     *
     * @param map
     * @return
     */
    fun <K, V : Comparable<V>?> sortEntries(map: Map<K, V>): MutableList<Map.Entry<K, V>> {
        val sortedEntries: MutableList<Map.Entry<K, V>> = ArrayList(map.entries)
        sortedEntries.sortWith { (_, value), (_, value1) ->
            value1!!.compareTo(
                value
            )
        }
        return sortedEntries
    }
}