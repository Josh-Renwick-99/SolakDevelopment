package com.ruse;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ruse.engine.GameEngine;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.LotteryTask;
import com.ruse.engine.task.impl.ServerTimeUpdateTask;
import com.ruse.model.container.impl.Shop.ShopManager;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.net.PipelineFactory;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.world.World;
import com.ruse.world.allornothing.DoubleOrNothing;
import com.ruse.world.clip.region.RegionClipping;
import com.ruse.world.content.*;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.ruse.world.content.combat.strategy.CombatStrategies;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.discordbot.Bot;
import com.ruse.world.content.grandLottery.GrandLottery;
import com.ruse.world.content.grandexchange.GrandExchangeOffers;
import com.ruse.world.content.groupironman.GroupManager;
import com.ruse.world.content.junkstore.GeneralStoreSelling;
import com.ruse.world.content.polling.PollManager;
import com.ruse.world.content.pos.PlayerOwnedShopManager;
import com.ruse.world.content.seasonpass.PassRewards;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.entity.actor.player.controller.ControllerHandler;
import com.ruse.world.entity.impl.mini.MiniOverride;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.region.RegionManager;
import com.server.service.login.ServiceManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Credit: lare96, Gabbe
 */
public final class GameLoader {

	private final ExecutorService serviceLoader = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("GameLoadingThread").build());
	private final GameEngine engine;
	private final int port;

	protected GameLoader(int port) {
		this.port = port;
		this.engine = new GameEngine();
	}

	public void init() {
		Preconditions.checkState(!serviceLoader.isShutdown(), "The bootstrap has been bound already!");

		ServiceManager.INSTANCE.init();

		executeServiceLoad();
		serviceLoader.shutdown();
		World.LOGIN_SERVICE.postLoad();


	}

	public void finish() throws IOException, InterruptedException {
		if (!serviceLoader.awaitTermination(15, TimeUnit.MINUTES))
			throw new IllegalStateException("The background service load took too long!");
		final ExecutorService networkExecutor = Executors.newCachedThreadPool();
		final ServerBootstrap serverBootstrap = new ServerBootstrap (new NioServerSocketChannelFactory(networkExecutor, networkExecutor));
		serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
		serverBootstrap.bind(new InetSocketAddress(port));
		engine.init();
		TaskManager.submit(new ServerTimeUpdateTask());
	}

	private void executeServiceLoad() {
		/*
		 * if (GameSettings.MYSQL_ENABLED) { serviceLoader.execute(() ->
		 * MySQLController.init()); }
		 */

		serviceLoader.execute(PassRewards::init);

		serviceLoader.execute(GroupManager::loadGroups);
		serviceLoader.execute(ConnectionHandler::init);
		serviceLoader.execute(PlayerPunishment::init);

		serviceLoader.execute(RegionClipping::init);
		serviceLoader.execute(CustomObjects::init);
		serviceLoader.execute(RegionManager::initialise);
		serviceLoader.execute(ControllerHandler::initialise);


		serviceLoader.execute(ItemDefinition::init);

		serviceLoader.execute(Lottery::init);
		serviceLoader.execute(GrandExchangeOffers::init);
		serviceLoader.execute(Scoreboards::init);

		serviceLoader.execute(WellOfGoodwill::init);
		serviceLoader.execute(ClanChatManager::init);
		serviceLoader.execute(CombatPoisonData::init);
		serviceLoader.execute(CombatStrategies::init);
		serviceLoader.execute(MiniOverride::loadOverrides);
		serviceLoader.execute(() -> NpcDefinition.parseNpcs().load());
		serviceLoader.execute(() -> NPCDrops.parseDrops().load());
		serviceLoader.execute(() -> WeaponInterfaces.parseInterfaces().load());
		serviceLoader.execute(WeaponInterfaces::init);
		serviceLoader.execute(() -> ShopManager.parseShops().load());
		serviceLoader.execute(PlayerOwnedShopManager::loadShops);
		serviceLoader.execute(() -> DialogueManager.parseDialogues().load());
		serviceLoader.execute(NPC::init);
		System.out.println("Initalised npcs");
		serviceLoader.execute(DoubleOrNothing::initialize);
		serviceLoader.execute(PollManager::initialize);
		serviceLoader.execute(GrandLottery::init);
		serviceLoader.execute(ShopManager::parseTaxShop);
		serviceLoader.execute(LotterySystem::loadTickets);
		serviceLoader.execute(AOESystem.getSingleton()::parseData);
		serviceLoader.execute(ServerPerks.getInstance()::load);
		if (!GameSettings.LOCALHOST)
			serviceLoader.execute(Bot::init);
		TaskManager.submit(new LotteryTask());

		serviceLoader.execute(GeneralStoreSelling::init);

	}

	public GameEngine getEngine() {
		return engine;
	}
}