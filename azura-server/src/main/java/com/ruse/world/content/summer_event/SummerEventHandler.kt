package com.ruse.world.content.summer_event

import com.ruse.model.Position
import com.ruse.util.Misc
import com.ruse.world.World
import com.ruse.world.entity.impl.npc.NPC
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object SummerEventHandler {


    /**
     * Schedules a boss to be spawned every hour
     */
    private fun scheduleTask() {
        val now = ZonedDateTime.now(ZoneId.of("America/New_York"))
        val hourLater = now.plusHours(1)
        var nextRun = hourLater.truncatedTo(ChronoUnit.HOURS)
        if (now > nextRun) nextRun = nextRun.plusHours(1)
        val duration = Duration.between(now, nextRun)
        val initalDelay = 15L

        val bossTaskWrapper = Runnable {
            spawnBoss()
        }

        val crabsTaskWrapper = Runnable {
            spawnCrabs()
        }
        val scheduler = Executors.newScheduledThreadPool(2)
        scheduler.scheduleAtFixedRate(bossTaskWrapper,
            initalDelay,
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(crabsTaskWrapper,
            0,
            TimeUnit.MINUTES.toSeconds(10),
            TimeUnit.SECONDS)

    }
    /**
     * @return The formatted time until the next summer boss
     */
    fun getTimeUntilNextBoss() : String {
        if(currentBoss != null) {
            return "@gre@Now"
        }
        val now = ZonedDateTime.now(ZoneId.of("America/New_York"))
        val hourLater = now.plusHours(1)
        var nextRun = hourLater.truncatedTo(ChronoUnit.HOURS)
        if (now > nextRun) nextRun = nextRun.plusHours(1)
        val duration = Duration.between(now, nextRun)
        val time = duration.toMillis()
        return Misc.getTimePlayed(time)
    }

    val SUMMER_HELPER_NPC_ID = 2713
    val SUMMER_NPC_ID = 2712
    val SAND_CRAB_NPC_ID = 2711
    val SUMMER_MINION_NPC_ID = 2710
    val SUMMER_BOSS_POS = Position(3104, 2911, 0)
    var currentBoss: NPC? = null

    var currentlySpawnedCrabs = ArrayList<NPC>()
    var CRAB_SPAWN_POINTS = listOf(Position(2824, 2587),
    Position(2822, 2586),
    Position(2826, 2590),
    Position(2823, 2589),
    Position(2829, 2599),
        Position(2830, 2601),
        Position(2830, 2603),
        Position(2829, 2602),
        Position(2829, 2611),
        Position(2828, 2613),
        Position(2826, 2617),
        Position(2824, 2616),
        Position(2816, 2620),
        Position(2814, 2619),
        Position(2812, 2617),
        Position(2815, 2617),
        Position(2784, 2607),
        Position(2781, 2605),
        Position(2781, 2602),
        Position(2784, 2604),
        Position(2785, 2606),
        Position(2782, 2596),
        Position(2783, 2593),
        Position(2787, 2595),
        Position(2788, 2593),
        Position(2791, 2594),
        Position(2795, 2595),
        Position(2791, 2597),
        Position(2788, 2598),
        Position(2799, 2594),
        Position(2803, 2592),
        Position(2802, 2594),
        Position(2812, 2588),
        Position(2810, 2590),
        Position(2812, 2592))

    public fun spawnCrabs() {
        World.sendBroadcastMessage("Sand crabs are chattering on the shores of skilling island!")

        // Remove any crab spawns that are left

        for(npc in currentlySpawnedCrabs) {
            World.deregister(npc)
        }
        currentlySpawnedCrabs.clear()

        // Add all the spawns back
        for(pos in CRAB_SPAWN_POINTS) {
            val n = NPC(SAND_CRAB_NPC_ID, pos)
            n.respawn = false
            World.register(n)
            currentlySpawnedCrabs.add(n)

        }
    }
    public fun spawnBoss() {
        if(currentBoss == null || currentBoss!!.isDying || !currentBoss!!.isRegistered) {
            if(!World.npcIsRegistered(SUMMER_NPC_ID)) {
                val npc = SummerBoss(SUMMER_NPC_ID, SUMMER_BOSS_POS)
                currentBoss = npc
                World.register(npc)
                World.sendSummerMessage("The Giant crab boss has spawned at the ::summer event island!")
            }

        }
    }

    init {
        // Schedule the task that starts the hourly bosses spawning
        scheduleTask()
    }
}