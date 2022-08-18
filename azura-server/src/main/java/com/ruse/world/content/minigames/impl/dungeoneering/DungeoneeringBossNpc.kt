package com.ruse.world.content.minigames.impl.dungeoneering

import com.ruse.model.Flag
import com.ruse.model.Position
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringBossNpc.Constants.BOSS_NOHEADICON
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringBossNpc.Constants.BOSS_PROT_MAGE
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringBossNpc.Constants.BOSS_PROT_MELEE
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringBossNpc.Constants.BOSS_PROT_RANGE
import com.ruse.world.entity.impl.npc.NPC

class DungeoneeringBossNpc(id: Int, tile: Position) : NPC(id, tile) {

    var tick = 0

    init {
        respawn = false
        isAggressive = true
        isForceAggressive = true
        size = 5
    }

    override fun sequence() {
        if (isDying) {
            return
        }

        if(tick % 30 == 0) {
            forceChat("Raaaaaaaaarrrrrgggggghhhhh!!!!")
            sendNPCTransformation()
        }
        tick++
        super.sequence()

    }


    private fun sendNPCTransformation() {
        transformationId = getNewNpcId()
        updateFlag.flag(Flag.TRANSFORM)
    }

    private fun getNewNpcId(): Int {
        return when (transformationId) {
            BOSS_NOHEADICON -> {
                return BOSS_PROT_MELEE
            }
            BOSS_PROT_MELEE -> {
                return BOSS_PROT_MAGE
            }
            BOSS_PROT_MAGE -> {
                return BOSS_PROT_RANGE
            }
            BOSS_PROT_RANGE -> {
                return BOSS_NOHEADICON
            }
            else -> BOSS_NOHEADICON
        }
    }

    companion object {
        fun isBoss(id: Int): Boolean {
            return id == BOSS_NOHEADICON || id == BOSS_PROT_RANGE || id == BOSS_PROT_MAGE || id == BOSS_PROT_MELEE
        }
    }

    object Constants {
        const val BOSS_NOHEADICON = 9845
        const val BOSS_PROT_MELEE = 9849
        const val BOSS_PROT_MAGE = 9850
        const val BOSS_PROT_RANGE = 9851
    }
}