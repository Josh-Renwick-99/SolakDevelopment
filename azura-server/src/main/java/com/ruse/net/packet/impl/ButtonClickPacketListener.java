package com.ruse.net.packet.impl;

import com.ruse.GameSettings;
import com.ruse.model.GameMode;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.RegionInstance;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Bank.BankSearchAttributes;
import com.ruse.model.container.impl.GroupIronmanBank;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponInterfaces.WeaponInterface;
import com.ruse.model.input.impl.*;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.allornothing.DoubleOrNothing;
import com.ruse.world.content.*;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.bossEvents.BossEventInterfaceHandler;
import com.ruse.world.content.clan.ClanChat;
import com.ruse.world.content.clan.ClanChatManager;
import com.ruse.world.content.clan.Guild;
import com.ruse.world.content.collectionlog.SearchForCollectionNpc;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.combat.magic.MagicSpells;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.weapon.CombatSpecial;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.dailyTask.DailyTaskInterface;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueOptions;
import com.ruse.world.content.goldenscratch.ScratchCard;
import com.ruse.world.content.grandLottery.GrandLottery;
import com.ruse.world.content.grandexchange.GrandExchange;
import com.ruse.world.content.groupironman.GroupManager;
import com.ruse.world.content.instanceMananger.InstanceInterfaceHandler;
import com.ruse.world.content.instanceMananger.InstanceManager;
import com.ruse.world.content.loyalty_streak.LoyaltyStreakManager;
import com.ruse.world.content.minigames.impl.Dueling;
import com.ruse.world.content.minigames.impl.PestControl;
import com.ruse.world.content.minigames.impl.dungeoneering.DungeoneeringParty;
import com.ruse.world.content.polling.PollCreation;
import com.ruse.world.content.polling.PollManager;
import com.ruse.world.content.rewardsList.RewardsHandler;
import com.ruse.world.content.serverperks.ServerPerkContributionInput;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skill.ChatboxInterfaceSkillAction;
import com.ruse.world.content.skill.impl.construction.Construction;
import com.ruse.world.content.skill.impl.crafting.LeatherMaking;
import com.ruse.world.content.skill.impl.crafting.Tanning;
import com.ruse.world.content.skill.impl.fletching.Fletching;
import com.ruse.world.content.skill.impl.herblore.ingredientsBook;
import com.ruse.world.content.skill.impl.old_dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.old_dungeoneering.ItemBinding;
import com.ruse.world.content.skill.impl.slayer.Slayer;
import com.ruse.world.content.skill.impl.smithing.SmithingData;
import com.ruse.world.content.skill.impl.summoning.PouchMaking;
import com.ruse.world.content.skill.impl.summoning.SummoningTab;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.content.wellForGlobalBosses.WellForGlobalBossesInterface;
import com.ruse.world.content.zombie.ZombieParty;
import com.ruse.world.content.zombie.ZombieRaidData;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.StartScreen;

import java.util.Arrays;
import java.util.List;

/**
 * This packet listener manages a button that the player has clicked upon.
 *
 * @author Gabriel Hannason
 */

public class ButtonClickPacketListener implements PacketListener {

    public static final int OPCODE = 185;
    private static final List<Integer> NEW_TELEPORT_BUTTONS_TAB = Arrays.asList(28215, 28216, 28217, 28218, 28219, 28220);
    private static final List<Integer> NEW_TELEPORT_BUTTONS = Arrays.asList(
            28231, 28232, 28233, 28234, 28235, 28236, 28237, 28238,
            28239, 28240, 28241, 28242,
            28243, 28244, 28245, 28246, 28247, 28248, 28249, 28250, 28251, 28252, 28253, 28254,
            28307, 28308, 28309, 28310, 28311, 28312, 28313, 28314, 28315, 28316, 28317, 28318, 28319,
            28320, 28321, 28322, 28323, 28324, 28325, 28326, 28327, 28328, 28329, 28330,
            28331, 28332, 28333, 28334, 28335, 28336, 28337, 28338, 28339);

    @Override
    public void handleMessage(Player player, Packet packet) {
        int bankid = 0;
        int id = packet.readInt();
        if (id >= 32768 && id < 65535) {
            id -= 65536;
        }

        if (player.getRights().isDeveloperOnly()) {
            player.getPacketSender().sendMessage("Clicked button: " + id);
        }
        if (!player.getControllerManager().processButtonClick(id)) {
            return;
        }
        if (id == -23480) {
            player.getPacketSender().sendEnterAmountPrompt("How much would you like to contribute?");
            player.setInputHandling(new ServerPerkContributionInput());
            return;
        }

        if (player.getAchievements().handleButtonClick(id)) {
            return;
        }
        if (checkHandlers(player, id))
            return;

        player.getGambling().handleChoice(id);


        if (player.getPlayerOwnedShopManager().handleButton(id)) {
            return;
        }

        if (player.getGoodieBag().handleClick(id)) {
            return;
        }
        if (PossibleLootInterface.handleButton(player, id)) {
            return;
        }

        if (player.getRaidsInterface().handleButton( id)) {
            return;
        }
        if (player.getUpgradeInterface().handleButton( id)) {
            return;
        }

        if (player.getDonatorShop().handleButton( id)) {
            return;
        }
        if (player.getPetShop().handleButton( id)) {
            return;
        }

        new ScratchCard(player).reveal(id);
        new DailyTaskInterface(player).button(id);
        new DailyTaskInterface(player).tabClicking(id);
        new InstanceInterfaceHandler(player).handleButtons(id);
        new WellForGlobalBossesInterface(player).button(id);

        switch (id) {
            case -19191:
                player.getAugmentHandler().remove(1, player);
                break;
            case -19189:
                player.getAugmentHandler().remove(2, player);
                break;
            case -19187:
                player.getAugmentHandler().remove(3, player);
                break;
            case 26070:
                player.levelNotifications = !player.levelNotifications;
                player.getPacketSender().sendMessage("Level-up notifications toggled: "+(player.levelNotifications ? "on" : "off")+".");
                break;
            case -18527:
                player.getPacketSender().sendString(1, GameSettings.StoreUrl);
            break;

            case -18522:
                player.getPacketSender().removeInterface();
                break;

            case -18532:
                player.getNewSpinner().findReward();
                break;

            case 21353:
                player.getWheelOfFortune().start();
                break;

            case 21372:
                player.getWheelOfFortune().open();
                break;

            case 21360:
                player.getPacketSender().sendInterfaceRemoval();
                break;

                // Previous Teleport Button
            case 1717:
                if (!player.getClickDelay().elapsed(4500) || player.getMovementQueue().isLockMovement())
                    return;

                if(player.lastTeleport != null) {
                    player.sendMessage("You have been teleported to your last teleport location.");
                    TeleportHandler.teleportPlayer(player, player.lastTeleport, player.getSpellbook().getTeleportType());
                } else {
                    player.sendMessage("You haven't teleported yet.");
                }

                player.getClickDelay().reset(0);
                break;

            case 21375:
                player.getWheelOfFortune().start();
                player.getPacketSender().updateInterfaceVisibility(21370, false);
                player.getPacketSender().updateInterfaceVisibility(21362, false);
                break;

            case 25408:
                player.getScratchcard().claimPrize();
                break;

            case 106009:
                if (!player.isBanking() || player.getInterfaceId() != 106000)
                    return;
                GroupIronmanBank.depositItems(player, player.getInventory(), false);
                break;
            case 106012:
                if (!player.isBanking() || player.getInterfaceId() != 106000)
                    return;
                GroupIronmanBank.depositItems(player, player.getEquipment(), false);
                break;

            case 110007:
                player.getCasketOpening().spin();
                break;
            case 110008:
                player.getCasketOpening().quickSpin();
                break;

            case 78395:
                // player.sendMessage("@red@This feature is currently disabled.");

                if (!player.isBanking() || (player.getInterfaceId() != 5292 && player.getInterfaceId() != 106000))
                    return;
                player.setPlaceholders(!player.isPlaceholders());
                player.getPacketSender().sendConfig(111, player.isPlaceholders() ? 0 : 1);

                break;
            case 111703:
                if (player.getSkillManager().getMaxLevel(Skill.SLAYER) < 98 && player.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) < 79) {
                    player.getPacketSender().sendMessage("<shad=1>@or2@You must be 99+ Slayer to do Raids [2].");
                    player.getPacketSender().sendMessage("You must be 80+ Dungeoneering to do Raids [2].");
                    return;
                }

                if (player.getLocation() == Location.ZOMBIE_LOBBY) {
                    if (player.getZombieParty() != null) {
                        if (player.getZombieParty().getOwner() != player) {
                            player.getPacketSender().sendMessage("Only the party leader can invite other players.");
                        } else {
                            player.setInputHandling(new InviteRaidsPlayer());
                            player.getPacketSender().sendEnterInputPrompt("Invite Player");
                        }
                    } else {
                        new ZombieParty(player).create();
                    }
                } else {
                    player.sendMessage("You must be in a raid to do this.");
                }
                return;

            case 111706:
                if (player.getLocation() == Location.ZOMBIE) {
                    if (player.getZombieParty() != null) {
                        player.getZombieParty().remove(player, true);
                        player.sendMessage("You left your Raids party.");
                    }
                    player.moveTo(ZombieRaidData.lobbyPosition);
                } else if (player.getLocation() == Location.ZOMBIE_LOBBY) {
                    if (player.getZombieParty() != null) {
                        player.getZombieParty().remove(player, true);
                        player.sendMessage("You left your Raids party.");
                    }
                } else {
                    player.sendMessage("You must be in a raid to do this.");
                }
                return;

            case 111211:
                player.getPacketSender().sendString(1, GameSettings.DomainUrl);
                break;
            case 111212:
                player.getPacketSender().sendString(1, GameSettings.DiscordUrl);
                break;
            case 111213:
                player.getPacketSender().sendString(1, GameSettings.VoteUrl);
                break;
            case 111214:
                player.getPacketSender().sendString(1, GameSettings.StoreUrl);
                break;
            case 111215:
                player.getPacketSender().sendString(1, GameSettings.DiscordUrl);
                break;
            case 111716:
            case 111719:
            case 111722:
            case 111725:
            case 111728:
            case 111731:
            case 111734:
            case 111737:
            case 111740:
            case 111743:
            case 111746:
            case 111749:
                if (player.getLocation() == Location.ZOMBIE || player.getLocation() == Location.ZOMBIE_LOBBY) {
                    if (player.getZombieParty() != null) {
                        if (player.equals(player.getZombieParty().getOwner())) {
                            if (player.getZombieParty().getPlayers()
                                    .size() >= ((id - 111716) / 3) + 1) {
                                Player playerToKick = player.getZombieParty()
                                        .getPlayers().get((id - 111716) / 3);
                                if (playerToKick == player) {
                                    player.sendMessage("You cannot kick yourself!");
                                } else {
                                    player.getZombieParty().remove(playerToKick,
                                            true);

                                }
                            }
                        } else {
                            player.sendMessage("Only the leader of the party can kick players!");
                        }
                    }
                } else {
                    player.sendMessage("You must be in a raid to do this.");
                }
                return;

            case -29536:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().drawInterface(0);
                break;
            case -26365:
                player.getAchievements().clickedAchievement = true;
                player.getAchievements().drawInterface(player.getAchievements().currentInterface);
                break;

            case 111602:
                DropsInterface.open(player);
                break;
            /*case 111603:
                BestItemsInterface.openInterface(player, 0);
                break;*/
            case 111603:
                player.getCollectionLog().open();
                break;
            case 111604:
                PossibleLootInterface.openInterface(player, PossibleLootInterface.LootData.values()[0]);
                break;
            case 111605:
                player.setInputHandling(new ChangePassword());
                player.getPacketSender().sendEnterInputPrompt("Enter a new password:");
                break;
            case 111606:
                player.setInputHandling(new SetPinPacketListener());
                player.getPacketSender().sendEnterInputPrompt("Enter the pin that you want to set$pin");
                break;
            case 111101:
                player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111000);
                player.getPacketSender().sendConfig(6000, 0);
                break;
            case 111102:
                player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111300);
                player.getPacketSender().sendConfig(6000, 1);
                break;
            case 111103:
                player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111500);
                player.getPacketSender().sendConfig(6000, 2);
                break;
            case 111601:
                player.getAchievements().refreshInterface(player.getAchievements().currentInterface);
                player.getPacketSender().sendConfig(6000, 3);
                break;
            case 31508:
                player.getEventBossManager().updateNpcIdentification();
                break;

            case 31509:
                player.getEventBossManager().updateNpcHealth();
                break;

            case 31513:
                player.getEventBossManager().initiateSpawn();
                break;

            case -16332:
                player.getGoodieBag().claim();
                if (player.getInventory().contains(7995)) {
                    World.sendMessage("<shad=1>@red@[OWNER CAPE] A player has received an Owner cape from the $75 goodiebag! GZ");
                }
                break;


            case 23638:
            case 23636:
                player.getScratchCard().scratch();
                break;

            case -8274:
                player.getDailyRewards().claimDay7();
                break;

            /**
             * Combiner
             */

            case -17500:
                if (player.getInventory().contains(player.getMysteryBoxOpener().getOpenBox())) { // example for mbox with random data.
                    player.getMysteryBoxOpener().open(player.getMysteryBoxOpener().getOpenBox());
                }


                break;

            case -17497: // example for mbox with random data. - open all
                if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
                    player.sendMessage("@red@As an Ultimate ironman you can't do this.");
                    return;
                }
                if (player.getInventory().contains(player.getMysteryBoxOpener().getOpenBox())) {
                    player.getMysteryBoxOpener().openAll(player.getMysteryBoxOpener().getOpenBox());
                }

                break;

            case 30902:
                GrandLottery.buy(player, 1);
                break;
            case -8384:
                player.sendMessage("Auto gambling has been disabled.");
                player.sendMessage("In order to gamble, you have to purchase the dice bag or seeds via ::Store");
                break;

            case -8383:
                if (player.getGambling().inGamble())
                    player.getGambling().declineGamble(true);
                break;
            case 30935:

                if (player.getInventory().contains(ItemDefinition.COIN_ID, 50000000)) {
                    player.setEntriesCurrency(player.getEntriesCurrency() + 5.0);
                    player.getInventory().delete(ItemDefinition.COIN_ID, 50000000);
                    player.getPA().sendMessage("<img=30>You have purchased a lottery ticket, go enter the lottery!");
                } else {
                    player.getPA().sendMessage("Each lottery ticket costs 50 Mill, Please have 50 Mill in your inventory.");
                }
                break;
            case 22095:
                DoubleOrNothing.handleGamble(player);
                break;
            case 22090:
            case 22098:
                DoubleOrNothing.handleKeep(player);
                break;
            case 19633:
                player.setInputHandling(new PollCreationTitle());
                player.getPacketSender().sendEnterInputPrompt("What Title would you like to apply?");
                break;
            case 19637:
                player.setInputHandling(new PollCreationOptionOne());
                player.getPacketSender().sendEnterInputPrompt("What First Option would you like to poll?");
                break;
            case 19641:
                player.setInputHandling(new PollCreationOptionTwo());
                player.getPacketSender().sendEnterInputPrompt("What Second Option would you like to poll?");
                break;
            case 19645:
                player.setInputHandling(new PollCreationTimeForPoll());
                player.getPacketSender().sendEnterInputPrompt("How long would you like the poll to stay active?P");
                break;
            case 19649:
                PollCreation.launchPoll(player);
                break;
            case 19654:
                PollCreation.resetPoll(player);
                break;
            case -30533:
                if (player.getData() != null) {
                    if (player.getData().getNpcid() == 1265) {
                        player.sendMessage("Solak lions can only be killed at ::train");
                        return;
                    }

                    if (player.getData().getNpcid() == 1265
                            || player.getData().getNpcid() == 1023
                            || player.getData().getNpcid() == 1233
                            || player.getData().getNpcid() == 1234) {
                        if (player.getPointsHandler().getNPCKILLCount() > 5000 && KillsTracker.getTotalKillsForNpc(player.getData().getNpcid(), player) > 500) {
                            player.sendMessage("This place is for new players with less than 5k npc kills.");
                            return;
                        }
                    }
                    new InstanceManager(player).createInstance(player.getData().getNpcid(), RegionInstance.RegionInstanceType.INSTANCE);
                } else {
                    player.getPA().sendMessage("Select the boss you'd like to instance.");
                }
                break;
            case -8254:
                player.getPacketSender().sendString(1, GameSettings.StoreUrl);
                player.getPacketSender().sendMessage("Attempting to open the store");
                break;
            case -11438:
                player.getPlayerOwnedShopManager().openEditor();
                break;

            case 106005:
            case -8305:
                player.getPacketSender().sendInterfaceRemoval();
                break;


            case -13233:
                player.getPacketSender().sendInterfaceRemoval();
                break;


            case -3292:

                DialogueManager.start(player, 217);
                player.setDialogueActionId(217);
                break;

            case -27454:
            case -27534:
            case -16534:
            case 36002:
            case 32606:
            case -31929:
            case -8255:
            case 19705:
            case 28005:
            case 26003:
            case 5384:

                player.getPacketSender().sendInterfaceRemoval();
                break;
            case 29133:
                player.getPacketSender().sendMessage("::leave to leave room");
                //player.getPacketSender().sendMessage("@red@Please re-group!");
                player.getPacketSender().sendInterfaceRemoval();

                //TeleportHandler.teleportPlayer(player, new Position(2722, 2737), player.getSpellbook().getTeleportType());

                player.getPacketSender().sendString(29053, "").sendString(29054, "");

                for (int i = 0; i < 10; i++) {
                    player.getPacketSender().sendString(29095 + i, ""); // this should be the final thing., added UI check for every place now.

                }


                break;

            // BATTLE BRAWL TELEPORT (NPC TIER LIST)
            case 28018:// imp
                if (player.getPointsHandler().getNPCKILLCount() <= 49) {
                    player.getPacketSender().sendMessage("You need 50 npc kill Count. You currently have @red@"
                            + player.getPointsHandler().getNPCKILLCount() + "@bla@ kills.");
                    player.getPacketSender().sendMessage("@blu@To get Fast NPC KILLS go to ::Starter");

                    return;
                }

                Position positionspawns = GameSettings.DEVILSPAWN;
                TeleportHandler.teleportPlayer(player, positionspawns, TeleportType.NORMAL);

                break;

            case 28019:// lord
                if (player.getPointsHandler().getSPAWNKILLCount() <= 99) {
                    player.getPacketSender().sendMessage("You need 100 Imp kills. You currently have @red@"
                            + player.getPointsHandler().getSPAWNKILLCount() + "@bla@ kills.");

                    return;
                }

                Position positionlord = GameSettings.LORDS;
                TeleportHandler.teleportPlayer(player, positionlord, TeleportType.NORMAL);
                break;

            case 28020:// demon
                if (player.getPointsHandler().getLORDKILLCount() <= 199) {
                    player.getPacketSender().sendMessage("You need 200 Lord kills. You currently have @red@"
                            + player.getPointsHandler().getLORDKILLCount() + "@bla@ kills.");

                    return;
                }

                Position position2 = GameSettings.DEMON;
                TeleportHandler.teleportPlayer(player, position2, TeleportType.NORMAL);
                break;
            case 28021:// dragon
                if (player.getPointsHandler().getDEMONKILLCount() <= 299) {
                    player.getPacketSender().sendMessage("You need 300 Demon kills. You currently have @red@"
                            + player.getPointsHandler().getDEMONKILLCount() + "@bla@ kills.");

                    return;
                }

                Position position3 = GameSettings.DRAGON;
                TeleportHandler.teleportPlayer(player, position3, TeleportType.NORMAL);
                break;
            case 28022:// beast
                if (player.getPointsHandler().getDRAGONKILLCount() <= 399) {
                    player.getPacketSender().sendMessage("You need 400 Dragon kills. You currently have @red@"
                            + player.getPointsHandler().getDRAGONKILLCount() + "@bla@ kills.");

                    return;
                }

                Position dragon = GameSettings.BEAST;
                TeleportHandler.teleportPlayer(player, dragon, TeleportType.NORMAL);
                break;
            case 28023:// king
                if (player.getPointsHandler().getBEASTKILLCount() <= 499) {
                    player.getPacketSender().sendMessage("You need 500 Beast kills. You currently have @red@"
                            + player.getPointsHandler().getBEASTKILLCount() + "@bla@ kills.");

                    return;
                }

                Position king = GameSettings.KING;
                TeleportHandler.teleportPlayer(player, king, TeleportType.NORMAL);
                break;
            case 28024:// avatar
                if (player.getPointsHandler().getKINGKILLCount() <= 999) {
                    player.getPacketSender().sendMessage("You need 1000 King kills. You currently have @red@"
                            + player.getPointsHandler().getKINGKILLCount() + "@bla@ kills.");

                    return;
                }

                Position avatar = new Position(3301, 3289, 0);
                TeleportHandler.teleportPlayer(player, avatar, TeleportType.NORMAL);
                break;
            case 28025:// angel
                if (player.getPointsHandler().getAVATARKILLCount() <= 1199) {
                    player.getPacketSender().sendMessage("You need 1200 Avatar kills. You currently have @red@"
                            + player.getPointsHandler().getAVATARKILLCount() + "@bla@ kills.");

                    return;
                }
                Position angel = new Position(3322, 3309, 0);
                TeleportHandler.teleportPlayer(player, angel, TeleportType.NORMAL);
                //player.getPacketSender().sendMessage("@blu@coming soon");

                break;
            case 28026:// lucien
                if (player.getPointsHandler().getANGELKILLCount() <= 1499) {
                    player.getPacketSender().sendMessage("You need 1500 Angel kills. You currently have @red@"
                            + player.getPointsHandler().getANGELKILLCount() + "@bla@ kills.");

                    return;
                }

                Position lucien = new Position(2907, 5455, 0);
                TeleportHandler.teleportPlayer(player, lucien, TeleportType.NORMAL);

                break;
            case 28027:// hercules
                if (player.getPointsHandler().getLUCIENKILLCount() <= 2499) {
                    player.getPacketSender().sendMessage("You need 2500 Lucien kills. You currently have @red@"
                            + player.getPointsHandler().getLUCIENKILLCount() + "@bla@ kills.");

                    return;
                }

                Position herc = new Position(2931, 5469, 0);
                TeleportHandler.teleportPlayer(player, herc, TeleportType.NORMAL);

                break;
            case 28028:// satan
                if (player.getPointsHandler().getHERCULESKILLCount() <= 3499) {
                    player.getPacketSender().sendMessage("You need 3500 Hercules kills. You currently have @red@"
                            + player.getPointsHandler().getHERCULESKILLCount() + "@bla@ kills.");

                    return;
                }

                Position sait = new Position(2910, 5491, 0);
                TeleportHandler.teleportPlayer(player, sait, TeleportType.NORMAL);

                break;
            case 28029:// zeus
                if (player.getPointsHandler().getSATANKILLCount() <= 4999) {
                    player.getPacketSender().sendMessage("You need 5000 Satan kills. You currently have @red@"
                            + player.getPointsHandler().getSATANKILLCount() + "@bla@ kills.");

                    return;
                }

                Position zzeus = new Position(2893, 5469, 0);
                TeleportHandler.teleportPlayer(player, zzeus, TeleportType.NORMAL);

                break;
            case -3306:
            case 30905:
                player.getPacketSender().sendInterfaceRemoval();
                break;
            case -4914:// 1
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(2695, 3714), TeleportType.NORMAL);// rock crabs
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(2855, 3543), TeleportType.NORMAL);// Warriors Guild
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(1908, 4367), TeleportType.NORMAL);// Daggonath Kings
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2847, 9637), TeleportType.NORMAL);// Scorpia
                } else if (player.getInterfaceId() == 61000) {
                    TeleportHandler.teleportPlayer(player, new Position(3087, 3517), TeleportType.NORMAL);// Wildy Ditch
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), TeleportType.NORMAL);// Godwars
                }
                break;
            case -4905:// 4
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3206, 3264), TeleportType.NORMAL);// yaks
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(3565, 3313), TeleportType.NORMAL);// Barrows
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(3281, 3914), TeleportType.NORMAL);// Chaos Elemental
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2514, 3045), TeleportType.NORMAL);// Lizardman
                    // Shaman
                } else if (player.getInterfaceId() == 61000) {
                    TeleportHandler.teleportPlayer(player, new Position(3107, 3937), TeleportType.NORMAL);// Mage Arena
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(3217, 9617), TeleportType.NORMAL);// Revs
                }
                break;
            case -4896:// 7
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3279, 2964), TeleportType.NORMAL);// dust devils
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(3483, 9482), TeleportType.NORMAL);// Kalphite Queen
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2371, 4673), TeleportType.NORMAL);// Kraken
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(2884, 9797), TeleportType.NORMAL);// Taverly Dungeon
                }
                break;
            case -4845:// 10
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(2480, 5175), TeleportType.NORMAL);// tzhaar
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(3050, 9573), TeleportType.NORMAL);// Glacors
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2796, 9332), TeleportType.NORMAL);// Skotizo
                }
                break;
            case -4911:// 2
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(2722, 4910), TeleportType.NORMAL);// pvm zone
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(2664, 2651), TeleportType.NORMAL);// Pest Control
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2541, 5794), TeleportType.NORMAL);// Tormented
                    // Demons
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(3097, 3704), TeleportType.NORMAL);// Cerberus
                } else if (player.getInterfaceId() == 61000) {
                    TeleportHandler.teleportPlayer(player, new Position(2980, 3597), TeleportType.NORMAL);// West Dragons
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(2731, 5095), TeleportType.NORMAL);// StrykWyrm
                    // Cavern
                }
                break;
            case -4902:// 5
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3420, 3510), TeleportType.NORMAL);// ghouls
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(2440, 5172), TeleportType.NORMAL);// Fight Caves
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(3292, 4942), TeleportType.NORMAL);// Slash Bash
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2970, 3948), TeleportType.NORMAL);// Thermonuclear
                    // Smoke Devil
                } else if (player.getInterfaceId() == 61000) {
                    player.moveTo(new Position(3048, 10266, player.getPosition().getZ()));
                    player.getPacketSender().sendInterfaceRemoval();
                    player.getPacketSender()
                            .sendMessage("@red@Teleport spells are blocked here. Use a portal or ladder to escape!");
                    player.getPacketSender().sendMessage("@red@Protect item does not work here either!");
                    CombatFactory.skullPlayer(player);// Death Arena
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(3185, 5471), TeleportType.NORMAL);// Chaos Tunnels
                }
                break;
            case -4842:// 11
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3132, 9909), TeleportType.NORMAL);// skeles
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2564, 4940), TeleportType.NORMAL);// Corp
                }
            case -4893:// 8
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3085, 9672), TeleportType.NORMAL);// skeletons
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2839, 9557), TeleportType.NORMAL);// Phoenix
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(1753, 5232), TeleportType.NORMAL);// Cellisto
                }
                break;
            case -4908:// 3
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3557, 9946), TeleportType.NORMAL);// experiments
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(3369, 3268), TeleportType.NORMAL);// Duel Arena
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2273, 4681), TeleportType.NORMAL);// King Black
                    // Dragon
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2454, 5213), TeleportType.NORMAL);// Abyssal Sire
                } else if (player.getInterfaceId() == 61000) {
                    TeleportHandler.teleportPlayer(player, new Position(3329, 3660), TeleportType.NORMAL);// East Dragons
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(1745, 5325), TeleportType.NORMAL);// Ancient Cavern
                }
                break;
            case -4899:// 6
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(2933, 9849), TeleportType.NORMAL);// Chaos Druids
                } else if (player.getInterfaceId() == 60700) {
                    TeleportHandler.teleportPlayer(player, new Position(1995, 4493), TeleportType.NORMAL);// Clanwars
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(3361, 5856), TeleportType.NORMAL);// Nomad
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(3186, 3830), TeleportType.NORMAL);// Venenatis
                } else if (player.getInterfaceId() == 61000) {
                    TeleportHandler.teleportPlayer(player, new Position(2948, 3904), TeleportType.NORMAL);// Frosts
                } else if (player.getInterfaceId() == 61100) {
                    TeleportHandler.teleportPlayer(player, new Position(2713, 9564), TeleportType.NORMAL);// Brimhaven
                }
                break;
            case -4890:// 9
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(2793, 2773), TeleportType.NORMAL);// Monkey Guards
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2857, 3808), TeleportType.NORMAL);// Bandos Avatar
                } else if (player.getInterfaceId() == 60900) {
                    TeleportHandler.teleportPlayer(player, new Position(2464, 4775), TeleportType.NORMAL);// Zulrah
                }
                break;
            case -4839:// 12
                if (player.getInterfaceId() == 60600) {
                    TeleportHandler.teleportPlayer(player, new Position(3235, 3295), TeleportType.NORMAL);// Chickens
                } else if (player.getInterfaceId() == 60800) {
                    TeleportHandler.teleportPlayer(player, new Position(2903, 5204), TeleportType.NORMAL);// Nex
                }
                break;
            case 1036:
                EnergyHandler.rest(player);
                break;
            case -26376:
                PlayersOnlineInterface.showInterface(player);
                break;
            case -26386:
                player.getPacketSender().sendTabInterface(GameSettings.STAFF_TAB, 46343);
                player.getAugmentHandler().updateInterface(player.getCurrentAugment(), player);
                break;
            case 30362:
                player.setInputHandling(new SearchForCollectionNpc());
                player.getPacketSender().sendEnterInputPrompt("Type the NPC name that you would like to search for");
                break;
            case 26250://invite player
                if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
                    if (!player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().equals(player)) {
                        player.getPacketSender().sendMessage("Only the party leader can invite other players.");
                    } else {
                        player.setInputHandling(new InviteDungeoneeringPlayer());
                        player.getPacketSender().sendEnterInputPrompt("Invite Player");
                    }
                }

                break;
            case 27229://form party
                if(player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
                    DungeoneeringParty.create(player);
                }
                break;
            case 26226://Exit TODO
                if (Dungeoneering.doingOldDungeoneering(player)) {
                    DialogueManager.start(player, 114);
                    player.setDialogueActionId(71);
                }
                break;
            case 26229://leave
                com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering.Companion.leaveParty(player);
                player.getPacketSender().sendDungeoneeringTabIcon(true).sendTabInterface(GameSettings.QUESTS_TAB, 27224).sendTab(GameSettings.QUESTS_TAB);
                break;
            case 26244:
            case 26247://floors
                /*
                if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
                    if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty().getOwner().getUsername()
                            .equals(player.getUsername())) {
                        DialogueManager.start(player, id == 26247 ? 106 : 105);
                        player.setDialogueActionId(id == 26247 ? 68 : 67);
                    } else {
                        player.getPacketSender().sendMessage("Only the party owner can change this setting.");
                    }
                }*/
                break;
            case 28180:
                player.getPacketSender().sendMessage("<shad=1>@red@Teleporting to PVP Arena!");
                TeleportHandler.teleportPlayer(player, new Position(2375, 4021), player.getSpellbook().getTeleportType());

                break;
            case 14176:
                player.setUntradeableDropItem(null);
                player.getPacketSender().sendInterfaceRemoval();
                break;

            /**
             * Player panel tab buttons
             */

            case 23835:
            case 23836:
            case 23837:
            case 23838:
                PlayerPanel.refreshPanel(player);
                break;

            case 14175:
                player.getPacketSender().sendInterfaceRemoval();
                if (player.getUntradeableDropItem() != null
                        && player.getInventory().contains(player.getUntradeableDropItem().getId())) {
                    ItemBinding.unbindItem(player, player.getUntradeableDropItem().getId());
                    player.getInventory().delete(player.getUntradeableDropItem());
                    PlayerLogs.log(player.getUsername(),
                            "Player destroying item: " + player.getUntradeableDropItem().getId() + ", amount: "
                                    + player.getUntradeableDropItem().getAmount());
                    player.getPacketSender().sendMessage("Your item vanishes as it hits the floor.");
                    Sounds.sendSound(player, Sound.DROP_ITEM);
                }
                player.setUntradeableDropItem(null);
                break;
            case 1013:
                player.getSkillManager().setTotalGainedExp(0);
                break;
            case -26373:
                if (WellOfGoodwill.isActive()) {
                    player.getPacketSender().sendMessage(
                            "<img=5> <col=008FB2>The Well of Goodwill is granting 30% bonus experience for another "
                                    + WellOfGoodwill.getMinutesRemaining() + " minutes.");
                } else {
                    player.getPacketSender()
                            .sendMessage("<img=5> <col=008FB2>The Well of Goodwill needs another "
                                    + Misc.insertCommasToNumber("" + WellOfGoodwill.getMissingAmount())
                                    + " coins before becoming full.");
                }
                break;
            case -26349:
                KillsTracker.open(player);
                break;
            case -26348:
                DropLog.open(player);
                break;
            case -10531:
                if (player.isKillsTrackerOpen()) {
                    player.setKillsTrackerOpen(false);
                    player.getPacketSender().sendTabInterface(GameSettings.QUESTS_TAB, 111000);
                    PlayerPanel.refreshPanel(player);
                }
                break;
            case 11213:
                player.getPacketSender().sendString(1, GameSettings.DomainUrl);
                player.getPacketSender().sendMessage("Attempting to open the homepage");
                break;
            case 11217:
                player.getPacketSender().sendString(1, GameSettings.DiscordUrl);
                player.getPacketSender().sendMessage("Attempting to open the Discord");
                break;
            case -26337:
            case 11216:
                player.getPacketSender().sendString(1, GameSettings.StoreUrl);
                player.getPacketSender().sendMessage("Attempting to open the Store");
                break;
            case -26338:
                player.getPacketSender().sendString(1, GameSettings.RuleUrl);
                player.getPacketSender().sendMessage("Attempting to open the rules");
                break;
            case -26339:
            case 11214:
                player.getPacketSender().sendString(1, GameSettings.ForumUrl);
                player.getPacketSender().sendMessage("Attempting to open the forums");
                break;
            case -26336:
            case 11215:
                player.getPacketSender().sendString(1, GameSettings.VoteUrl);
                player.getPacketSender().sendMessage("Attempting to open the Vote page");
                break;
            case -26335:
                player.getPacketSender().sendString(1, GameSettings.HiscoreUrl);
                player.getPacketSender().sendMessage("Attempting to open the Hiscore page");
                break;
            case -26334:
                player.getPacketSender().sendString(1, GameSettings.ReportUrl);
                player.getPacketSender().sendMessage("Attempting to open the report page");
                break;
            case 350:
                player.getPacketSender()
                        .sendMessage("To autocast a spell, please right-click it and choose the autocast option.")
                        .sendTab(GameSettings.MAGIC_TAB).sendConfig(108, player.getAutocastSpell() == null ? 3 : 1);
                break;
            case 29335:
                if (player.getInterfaceId() > 0) {
                    player.getPacketSender()
                            .sendMessage("Please close the interface you have open before opening another one.");
                    return;
                }
                DialogueManager.start(player, 60);
                player.setDialogueActionId(27);
                break;
            case -13390:
                player.getPacketSender().sendInterfaceRemoval();
                break;
            case 29455:
                if (player.getInterfaceId() > 0) {
                    player.getPacketSender()
                            .sendMessage("Please close the interface you have open before opening another one.");
                    return;
                }
                ClanChatManager.toggleLootShare(player);
                break;
            /*case 29456:
                if (player.getInterfaceId() > 0) {
                    player.getPacketSender()
                            .sendMessage("Please close the interface you have open before opening another one.");
                    return;
                }
                player.getPacketSender().sendInterfaceRemoval();
                PlayersOnlineInterface.showInterface(player);
                // player.getPacketSender().sendMessage("<shad=1>@or1@There are currently @whi@[ @gre@" + (17 + World.getPlayers().size()) + "@whi@ ] @or1@players online!");
                break;*/
            case 11001:
                TeleportHandler.teleportPlayer(player, GameSettings.DEFAULT_POSITION.copy(),
                        player.getSpellbook().getTeleportType());
                player.getPacketSender().sendInterfaceRemoval();
                //player.setDialogueActionId(217);
                //DialogueManager.start(player, 217);

                break;
            case 28211:
                TeleportHandler.teleportPlayer(player, GameSettings.DEFAULT_POSITION.copy(),
                        player.getSpellbook().getTeleportType());
                break;
            case 1159: // Bones to Bananas
            case 15877:// Bones to peaches
            case 30306:
                MagicSpells.handleMagicSpells(player, id);
                break;
            case 10001:
                if (player.getInterfaceId() == -1) {
                    Consumables.handleHealAction(player);
                } else {
                    player.getPacketSender().sendMessage("You cannot heal yourself right now.");
                }
                break;
            case 18025:
                if (PrayerHandler.isActivated(player, PrayerHandler.AUGURY)) {
                    PrayerHandler.deactivatePrayer(player, PrayerHandler.AUGURY);
                } else {
                    PrayerHandler.activatePrayer(player, PrayerHandler.AUGURY);
                }
                break;

            case 18018:
                if (PrayerHandler.isActivated(player, PrayerHandler.RIGOUR)) {
                    PrayerHandler.deactivatePrayer(player, PrayerHandler.RIGOUR);
                } else {
                    PrayerHandler.activatePrayer(player, PrayerHandler.RIGOUR);
                }
                break;
            case 10000:
            case 950:
                if (player.getInterfaceId() < 0)
                    player.getPacketSender().sendInterface(40030);
                else
                    player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
                break;
            case 3546:
            case 3420:
                if (System.currentTimeMillis() - player.getTrading().lastAction <= 300)
                    return;
                player.getTrading().lastAction = System.currentTimeMillis();
                if (player.getTrading().inTrade()) {
                    player.getTrading().acceptTrade(id == 3546 ? 2 : 1);
                } else {
                    player.getPacketSender().sendInterfaceRemoval();
                }
                break;
            case 10162:
            case -18269:
                player.getPacketSender().sendInterfaceRemoval();
                break;
            case 841:
                ingredientsBook.readBook(player, player.getCurrentBookPage() + 2, true);
                break;
            case 839:
                ingredientsBook.readBook(player, player.getCurrentBookPage() - 2, true);
                break;
            case 14922:
                player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
                break;
            case 14921:
                player.getPacketSender().sendMessage("Please visit the forums and ask for help in the support section.");
                break;
            case 5294:
                player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
                player.setDialogueActionId(player.getBankPinAttributes().hasBankPin() ? 8 : 7);
                DialogueManager.start(player,
                        DialogueManager.getDialogues().get(player.getBankPinAttributes().hasBankPin() ? 12 : 9));
                break;
            case 15002:

                if (!player.busy() && !player.getCombatBuilder().isBeingAttacked()
                        && !Dungeoneering.doingOldDungeoneering(player)) {
                    player.getSkillManager().stopSkilling();
                    player.getPriceChecker().open();
                } else {
                    player.getPacketSender().sendMessage("You cannot open this right now.");
                }
                break;
            case 2735:
            case 1511:
                if (player.getSummoning().getBeastOfBurden() != null) {
                    player.getSummoning().toInventory();
                    player.getPacketSender().sendInterfaceRemoval();
                } else {
                    player.getPacketSender().sendMessage("You do not have a familiar who can hold items.");
                }
                break;
            case -11501:

            case -11498:
            case -11507:
            case 1020:
            case 1021:
            case 1019:
            case 1018:
                if (id == 1020)
                    SummoningTab.renewFamiliar(player);
                else if (id == 1019 || id == -11507)
                    SummoningTab.callFollower(player);
                else if (id == 1021 || id == -11504)
                    SummoningTab.handleDismiss(player, true);
                //else if (id == -11507) // TODO swagyolo
               //     player.getSummoning().store();
                else if (id == 1018)
                    player.getSummoning().toInventory();
                break;
            case 1042:
                player.getPacketSender().sendInterface(57350);
                break;

             case 1037:
                SummoningTab.callFollower(player);
                break;
            case 1038:
                SummoningTab.renewFamiliar(player);
                break;
            case 1039:
                SummoningTab.handleDismiss(player, false);
                break;
            case 1040:
                player.getSummoning().toInventory();
                break;
            case 1041:
                player.getSummoning().store();
                break;
            case 1095:
                player.setExperienceLocked(!player.experienceLocked());
                if (player.experienceLocked()) {
                    player.getPacketSender().sendMessage("Your EXP is now locked, revert this lock to get EXP again.");
                } else {
                    player.getPacketSender().sendMessage("Your EXP is unlocked, and you will gain EXP as normal.");
                }
                break;
            /*
             * player.setDialogueActionId(0); DialogueManager.start(player, 0);
             */

            /*
             * case 11008: player.setDialogueActionId(0); DialogueManager.start(player, 0);
             * break; case 11017: DialogueManager.start(player, 34);
             * player.setDialogueActionId(15); break; case 11011:
             * DialogueManager.start(player, 22); player.setDialogueActionId(14); break;
             * case 11020: DialogueManager.start(player, 21);
             * player.setDialogueActionId(12); break; case 11014:
             * player.setDialogueActionId(36); DialogueManager.start(player, 136); break;
             */
            case 2799:
            case 2798:
            case 1747:
            case 1748:
            case 8890:
            case 8886:
            case 8875:
            case 8871:
            case 8894:
                ChatboxInterfaceSkillAction.handleChatboxInterfaceButtons(player, id);
                break;
            case 14873:
            case 14874:
            case 14875:
            case 14876:
            case 14877:
            case 14878:
            case 14879:
            case 14880:
            case 14881:
            case 14882:
                BankPin.clickedButton(player, id);
                break;
            case -12307:
                if (!StarterTasks.claimReward(player)) {
                    player.sendMessage("@red@You cannot claim the reward untill all tasks are complete.");
                    return;
                }
                player.getInventory().delete(757, 1);
                player.getPacketSender().sendInterfaceRemoval();
                // player.sendMessage("Enjoy your reward");
                break;
            case -12286:
            case 28133:
            case 27133:
            case 26133:
            case -16938:
            case -17492:
            case 31502:
                player.getPacketSender().sendInterfaceRemoval();
                //CLOSE INTERFACE
                break;

            case 27005:
            case 22012:
                if (!player.isBanking() || player.getInterfaceId() != 5292)
                    return;
                Bank.depositItems(player, id == 27005 ? player.getEquipment() : player.getInventory(), false);
                break;
            case 27023:
                if (!player.isBanking() || player.getInterfaceId() != 5292)
                    return;
                if (player.getSummoning().getBeastOfBurden() == null) {
                    player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
                    return;
                }
                Bank.depositItems(player, player.getSummoning().getBeastOfBurden(), false);
                break;
            case 22008:
                if (!player.isBanking() ||( player.getInterfaceId() != 106000 && player.getInterfaceId() != 5292))
                    return;
                player.setNoteWithdrawal(!player.withdrawAsNote());
                break;
            case 21000:
                if (!player.isBanking() || player.getInterfaceId() != 5292)
                    return;
                player.setSwapMode(!player.swapMode());
                break;
            case 27009:
                player.sendMessage("This option has been disabled");
                break;
            case 27014:
            case 27015:
            case 27016:
            case 27017:
            case 27018:
            case 27019:
            case 27020:
            case 27021:
            case 27022:
                if (!player.isBanking())
                    return;
                if (player.getBankSearchingAttribtues().isSearchingBank())
                    BankSearchAttributes.stopSearch(player, true);
                int bankId = id - 27014;
                boolean empty = bankId > 0 && Bank.isEmpty(player.getBank(bankId));
                if (!empty || bankId == 0) {
                    player.setCurrentBankTab(bankId);
                    player.getPacketSender().sendString(5385, "scrollreset");
                    player.getPacketSender().sendString(27002, Integer.toString(player.getCurrentBankTab()));
                    player.getPacketSender().sendString(27000, "1");
                    player.getBank(bankId).open();
                } else
                    player.getPacketSender().sendMessage("To create a new tab, please drag an item here.");
                break;
            case 22004:
                if (!player.isBanking())
                    return;
                if (!player.getBankSearchingAttribtues().isSearchingBank()) {
                    player.getBankSearchingAttribtues().setSearchingBank(true);
                    player.setInputHandling(new EnterSyntaxToBankSearchFor());
                    player.getPacketSender().sendEnterInputPrompt("What would you like to search for?");
                } else {
                    BankSearchAttributes.stopSearch(player, true);
                }
                break;
            case 22845:
            case 24115:
            case 24010:
            case 24041:
            case 150:
                player.setAutoRetaliate(!player.isAutoRetaliate());
                break;
            case 29332:
                ClanChat clan = player.getCurrentClanChat();
                if (clan == null) {
                    player.getPacketSender().sendMessage("You are not in a clanchat channel.");
                    return;
                }
                ClanChatManager.leave(player, false);
                player.setClanChatName(null);
                break;
            case 29329:
                if (player.getInterfaceId() > 0) {
                    player.getPacketSender()
                            .sendMessage("Please close the interface you have open before opening another one.");
                    return;
                }
                player.setInputHandling(new EnterClanChatToJoin());
                player.getPacketSender().sendEnterInputPrompt("Enter the name of the clanchat channel you wish to join:");
                break;
            case 19158:
            case 152:
                if (player.getRunEnergy() <= 1) {
                    player.getPacketSender().sendMessage("You do not have enough energy to do this.");
                    player.setRunning(false);
                } else
                    player.setRunning(!player.isRunning());
                player.getPacketSender().sendRunStatus();
                break;
            case 15004:
                player.setExperienceLocked(!player.experienceLocked());
                String type = player.experienceLocked() ? "locked" : "unlocked";
                player.getPacketSender().sendMessage("Your experience is now " + type + ".");
                PlayerPanel.refreshPanel(player);
                break;
            case 27651:
            case 15001:
                if (player.getInterfaceId() == -1) {
                    player.getSkillManager().stopSkilling();
                    BonusManager.update(player);
                    player.getPacketSender().sendInterface(21172);
                } else
                    player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
                break;
            case 15003:
                if (player.getInterfaceId() > 0) {
                    player.getPacketSender()
                            .sendMessage("Please close the interface you have open before opening another one.");
                    return;
                }
                player.getSkillManager().stopSkilling();
                ItemsKeptOnDeath.sendInterface(player);
                break;
            case 2458: // Logout
                if (player.logout()) {
                    World.removePlayer(player);
                    player.getSummoning().unsummon(true, true);
                }
                break;
            // case 10003:
            case 29138:
            case 29038:
            case 29063:
            case 29113:
            case 29163:
            case 29188:
            case 29213:
            case 29238:
            case 30007:
            case 48023:
            case 33033:
            case 30108:
            case 7473:
            case 7562:
            case 7487:
            case 7788:
            case 8481:
            case 7612:
            case 7587:
            case 7662:
            case 7462:
            case 7548:
            case 7687:
            case 7537:
            case 12322:
            case 7637:
            case 12311:
                CombatSpecial.activate(player);
                break;
            case 1772: // shortbow & longbow & blowpipe & crossbow
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
                    player.setFightType(FightType.CROSSBOW_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
                    player.setFightType(FightType.BLOWPIPE_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.BSOAT) {
                    player.setFightType(FightType.BSOAT_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
                    player.setFightType(FightType.ARMADYLXBOW_ACCURATE);
                }
                break;
            case -11504:
                SummoningTab.handleDismiss(player, true);
            case 1771:
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_RAPID);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_RAPID);
                } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
                    player.setFightType(FightType.CROSSBOW_RAPID);
                } else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
                    player.setFightType(FightType.BLOWPIPE_RAPID);
                } else if (player.getWeapon() == WeaponInterface.BSOAT) {
                    player.setFightType(FightType.BSOAT_RAPID);
                } else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
                    player.setFightType(FightType.ARMADYLXBOW_RAPID);
                }
                break;
            case 1770:
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
                    player.setFightType(FightType.CROSSBOW_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.BLOWPIPE) {
                    player.setFightType(FightType.BLOWPIPE_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.BSOAT) {
                    player.setFightType(FightType.BSOAT_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.ARMADYLXBOW) {
                    player.setFightType(FightType.ARMADYLXBOW_LONGRANGE);
                }
                break;
            case 2282: // dagger & sword
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_STAB);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_STAB);
                }
                break;
            case 2285:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_LUNGE);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_LUNGE);
                }
                break;
            case 2284:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_SLASH);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_SLASH);
                }
                break;
            case 2283:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_BLOCK);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_BLOCK);
                }
                break;
            case 2429: // scimitar & longsword
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_CHOP);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_CHOP);
                }
                break;
            case 2432:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_SLASH);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_SLASH);
                }
                break;
            case 2431:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_LUNGE);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_LUNGE);
                }
                break;
            case 2430:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_BLOCK);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_BLOCK);
                }
                break;
            case 3802: // mace
                player.setFightType(FightType.MACE_POUND);
                break;
            case 3805:
                player.setFightType(FightType.MACE_PUMMEL);
                break;
            case 3804:
                player.setFightType(FightType.MACE_SPIKE);
                break;
            case 3803:
                player.setFightType(FightType.MACE_BLOCK);
                break;
            case 4454: // knife, thrownaxe, dart & javelin
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_ACCURATE);
                }
                break;
            case 4453:
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_RAPID);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_RAPID);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_RAPID);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_RAPID);
                }
                break;
            case 4452:
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_LONGRANGE);
                }
                break;
            case 4685: // spear
                player.setFightType(FightType.SPEAR_LUNGE);
                break;
            case 4688:
                player.setFightType(FightType.SPEAR_SWIPE);
                break;
            case 4687:
                player.setFightType(FightType.SPEAR_POUND);
                break;
            case 4686:
                player.setFightType(FightType.SPEAR_BLOCK);
                break;
            case 4711: // 2h sword
                player.setFightType(FightType.TWOHANDEDSWORD_CHOP);
                break;
            case 4714:
                player.setFightType(FightType.TWOHANDEDSWORD_SLASH);
                break;
            case 4713:
                player.setFightType(FightType.TWOHANDEDSWORD_SMASH);
                break;
            case 4712:
                player.setFightType(FightType.TWOHANDEDSWORD_BLOCK);
                break;
            case 5576: // pickaxe
                player.setFightType(FightType.PICKAXE_SPIKE);
                break;
            case 5579:
                player.setFightType(FightType.PICKAXE_IMPALE);
                break;
            case 5578:
                player.setFightType(FightType.PICKAXE_SMASH);
                break;
            case 5577:
                player.setFightType(FightType.PICKAXE_BLOCK);
                break;
            case 7768: // claws
                player.setFightType(FightType.CLAWS_CHOP);
                break;
            case 7771:
                player.setFightType(FightType.CLAWS_SLASH);
                break;
            case 7770:
                player.setFightType(FightType.CLAWS_LUNGE);
                break;
            case 7769:
                player.setFightType(FightType.CLAWS_BLOCK);
                break;
            case 8466: // halberd
                player.setFightType(FightType.HALBERD_FEND);
                break;
            case 8468:
                player.setFightType(FightType.HALBERD_SWIPE);
                break;
            case 8467:
                player.setFightType(FightType.HALBERD_JAB);
                break;
            case 5862: // unarmed
                player.setFightType(FightType.UNARMED_PUNCH);
                break;
            case 5861:
                player.setFightType(FightType.UNARMED_KICK);
                break;
            case 5860:
                player.setFightType(FightType.UNARMED_BLOCK);
                break;
            case 12298: // whip
                player.setFightType(FightType.WHIP_FLICK);
                break;
            case 12297:
                player.setFightType(FightType.WHIP_LASH);
                break;
            case 12296:
                player.setFightType(FightType.WHIP_DEFLECT);
                break;
            case 336: // staff
                player.setFightType(FightType.STAFF_BASH);
                break;
            case 335:
                player.setFightType(FightType.STAFF_POUND);
                break;
            case 334:
                player.setFightType(FightType.STAFF_FOCUS);
                break;
            case 433: // warhammer
                player.setFightType(FightType.WARHAMMER_POUND);
                break;
            case 432:
                player.setFightType(FightType.WARHAMMER_PUMMEL);
                break;
            case 431:
                player.setFightType(FightType.WARHAMMER_BLOCK);
                break;
            case 782: // scythe
                player.setFightType(FightType.SCYTHE_REAP);
                break;
            case 784:
                player.setFightType(FightType.SCYTHE_CHOP);
                break;
            case 785:
                player.setFightType(FightType.SCYTHE_JAB);
                break;
            case 783:
                player.setFightType(FightType.SCYTHE_BLOCK);
                break;
            case 1704: // battle axe
                player.setFightType(FightType.BATTLEAXE_CHOP);
                break;
            case 1707:
                player.setFightType(FightType.BATTLEAXE_HACK);
                break;
            case 1706:
                player.setFightType(FightType.BATTLEAXE_SMASH);
                break;
            case 1705:
                player.setFightType(FightType.BATTLEAXE_BLOCK);
                break;
        }
    }

    private boolean checkHandlers(Player player, int id) {

        if (ServerPerks.getInstance().handleButton(player, id)) {
            return true;
        }
        if (player.getCollectionLog().handleButton(id))
            return true;
        if (BestItemsInterface.buttonClicked(player, id)) {
            return true;
        }
        if (KillTrackerInterface.handleButton(player, id))
            return true;
        if (Construction.handleButtonClick(id, player)) {
            return true;
        }
        switch (id) {
            case 14650:
                player.getPacketSender().removeInterface();
                return true;
            case 2494:
            case 2495:
            case 2496:
            case 2497:
            case 2498:
            case 2471:
            case 2472:
            case 2473:
            case 2461:
            case 2462:
            case 2482:
            case 2483:
            case 2484:
            case 2485:
                DialogueOptions.handle(player, id);
                return true;

            case -8307:
            case -8308:
            case -8309:
            case -8310:
            case -8311:
            case -8312:
                if (DailyRewards.handleRewards(player, id))
                    ;
                return true;

        }

        if (GroupManager.handleButton(player, id)) {
            return true;
        }
        
        if ((player.isPlayerLocked() || player.isGroupIronmanLocked())  && id != 2458 && !(id >= 116005 && id<= 116010) 
                && id != -12780 && id != -12779 && id != -12778 && id != -12763 && id != -12760 && id != -12767) {
            return true;
        }

        if (StartScreen.handleButton(player, id)) {
            return true;
        }
        if (player.isPlayerLocked() && !player.getHasPin()) {
            player.setInputHandling(new SetPinPacketListener());
            player.getPacketSender().sendEnterInputPrompt("Enter the pin that you want to set$pin");
            return false;
        }
        if (TeleportInterface.handleButton(player, id)) {
            return true;
        }

        if (new RewardsHandler(player).button(id)) {
            return true;
        }
        if (StarterTasks.handleButton(player, id)) {
            return true;
        }

        if (DropsInterface.handleButton(id)) {
            DropsInterface.handleButtonClick(player, id);
            return true;
        }
        if (player.isPlayerLocked() && id != 2458) {
            return true;
        }
        if (AchievementsOLD.handleButton(player, id)) {
            return true;
        }
        if (Sounds.handleButton(player, id)) {
            return true;
        }
        if (PrayerHandler.isButton(id)) {
            PrayerHandler.togglePrayerWithActionButton(player, id);
            return true;
        }
        if (CurseHandler.isButton(player, id)) {
            return true;
        }
        if (Autocasting.handleAutocast(player, id)) {
            return true;
        }
        if (SmithingData.handleButtons(player, id)) {
            return true;
        }
        if (PouchMaking.pouchInterface(player, id)) {
            return true;
        }
        if (LoyaltyProgramme.handleButton(player, id)) {
            return true;
        }
        if (Fletching.fletchingButton(player, id)) {
            return true;
        }
        if (LeatherMaking.handleButton(player, id) || Tanning.handleButton(player, id)) {
            return true;
        }
        if (Emotes.doEmote(player, id)) {
            return true;
        }
        if (PestControl.handleInterface(player, id)) {
            return true;
        }
        if (player.getLocation() == Location.DUEL_ARENA && Dueling.handleDuelingButtons(player, id)) {
            return true;
        }
        if (Slayer.handleRewardsInterface(player, id)) {
            return true;
        }
        if (ExperienceLamps.handleButton(player, id)) {
            return true;
        }
        if (PlayersOnlineInterface.handleButton(player, id)) {
            return true;
        }
        if (GrandExchange.handleButton(player, id)) {
            return true;
        }
        if (ClanChatManager.handleClanChatSetupButton(player, id)) {
            return true;
        }
        if (Guild.handleClanButtons(player, id)) {
            return true;
        }
        if (new BossEventInterfaceHandler(player).button(id)) {
            return true;
        }

        if (LoyaltyStreakManager.handleButtonClick(player, id)) {
            return true;
        }
        if (PollManager.handlePollClick(player, id)) {
            return true;
        }
        return false;
    }
}
