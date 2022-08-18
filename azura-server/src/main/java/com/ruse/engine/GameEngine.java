package com.ruse.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruse.GameSettings;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.container.impl.Shop.ShopManager;
import com.ruse.util.playerSavingTimer;
import com.ruse.world.World;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.grandexchange.GrandExchangeOffers;
import com.ruse.world.content.groupironman.GroupManager;
import com.ruse.world.content.pos.PlayerOwnedShopManager;
import com.ruse.world.entity.impl.GlobalItemSpawner;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * @author lare96
 * @author Gabriel Hannason
 */
public final class GameEngine implements Runnable {

    private final ScheduledExecutorService logicService = GameEngine.createLogicService();
    private static final ThreadFactory THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder().setNameFormat("GameThread").build();

    // private static final int PROCESS_GAME_TICK = 2;

    // private EngineState engineState = EngineState.PACKET_PROCESSING;

    // private int engineTick = 0;

    /**
     * STATIC
     **/

    public static ScheduledExecutorService createLogicService() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("LogicServiceThread").build());
        executor.setKeepAliveTime(45, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
        return Executors.unconfigurableScheduledExecutorService(executor);
    }

    /*
     * private EngineState next() { if (engineTick == PROCESS_GAME_TICK) {
     * engineTick = 0; return EngineState.GAME_PROCESSING; } engineTick++; return
     * EngineState.PACKET_PROCESSING; }
     *
     * private enum EngineState { PACKET_PROCESSING, GAME_PROCESSING; }
     */

    @Override
    public void run() {
        try {
            final boolean PRINT_TIMESTAMP = true;
            long start = System.currentTimeMillis();
            long lastTime = System.currentTimeMillis();

            TaskManager.sequence();
            if (PRINT_TIMESTAMP) {
                if (System.currentTimeMillis() - lastTime > 150) {
                    System.out.println("Task sequencing took: " + (System.currentTimeMillis() - lastTime) + " ms");
                }
                lastTime = System.currentTimeMillis();
            }
            World.sequence();
            if (PRINT_TIMESTAMP) {
                if (System.currentTimeMillis() - lastTime > 150) {
                    System.out.println("World processing took: " + (System.currentTimeMillis() - lastTime) + " ms");
                }
                lastTime = System.currentTimeMillis();
            }
            playerSavingTimer.massSaving();
            if (PRINT_TIMESTAMP) {
                if (System.currentTimeMillis() - lastTime > 150) {
                    System.out.println("Player saving timer took: " + (System.currentTimeMillis() - lastTime) + " ms");
                }
                lastTime = System.currentTimeMillis();
            }
            GlobalItemSpawner.startup();
            if (PRINT_TIMESTAMP) {
                if (System.currentTimeMillis() - lastTime > 150) {
                    System.out.println("Global item spawner took: " + (System.currentTimeMillis() - lastTime) + " ms");
                }
                lastTime = System.currentTimeMillis();
                if (System.currentTimeMillis() - start > 600) {
                    System.out.println("Total tick time was " + (System.currentTimeMillis() - start) + " ms");
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            GroupManager.saveGroups();
            World.savePlayers();
            ShopManager.saveTaxShop();
            playerSavingTimer.massSaving();
            GrandExchangeOffers.save();
            ClanChatManager.save();
            PlayerOwnedShopManager.saveShops();
        }
    }

    public void init(){

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY_BUILDER);
        final ScheduledFuture<?> handle = executorService.scheduleAtFixedRate(this, 0,
                GameSettings.ENGINE_PROCESSING_CYCLE_RATE,
                TimeUnit.MILLISECONDS);
        final Thread exceptionHandlerThread = new Thread(() -> {
            try {
                handle.get();
            } catch (ExecutionException | InterruptedException e){
                GroupManager.saveGroups();
                World.savePlayers();
                ShopManager.saveTaxShop();
                playerSavingTimer.massSaving();
                GrandExchangeOffers.save();
                ClanChatManager.save();
                PlayerOwnedShopManager.saveShops();
            }
        }, "GameEngine Exception Handler");
        exceptionHandlerThread.start();
    }

    /**
     * Submit runnable to game thread.
     *
     * @param runnable the {@link Runnable} to be submitted.
     */
    public static void submit(Runnable runnable) {
        World.submitGameThreadJob(() -> {
            runnable.run();
            return null;
        });
    }
}