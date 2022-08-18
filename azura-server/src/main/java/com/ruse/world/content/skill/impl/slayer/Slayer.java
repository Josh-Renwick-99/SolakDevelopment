package com.ruse.world.content.skill.impl.slayer;

import com.ruse.GameSettings;
import com.ruse.model.*;
import com.ruse.model.container.impl.Shop.ShopManager;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.NpcRequirements;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.dailytasks_new.DailyTask;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.skill.impl.summoning.BossPets;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;

public class Slayer {

    private Player player;

    public Slayer(Player p) {
        this.player = p;
    }

    private SlayerTasks slayerTask = SlayerTasks.NO_TASK, lastTask = SlayerTasks.NO_TASK;
    private SlayerMaster slayerMaster = SlayerMaster.EASY_SLAYER;
    private int amountToSlay, taskStreak;
    private String duoPartner, duoInvitation;

    public void assignTask() {
        boolean hasTask = getSlayerTask() != SlayerTasks.NO_TASK && player.getSlayer().getLastTask() != getSlayerTask();
        boolean duoSlayer = duoPartner != null;
        if (duoSlayer && !player.getSlayer().assignDuoSlayerTask())
            return;
        if (hasTask) {
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        SlayerTaskData taskData = SlayerTasks.getNewTaskData(slayerMaster);
        int slayerTaskAmount = taskData.getSlayerTaskAmount();
        SlayerTasks taskToSet = taskData.getTask();
        if(duoSlayer) {
            slayerTaskAmount *= 2;
        }
        if (taskToSet == player.getSlayer().getLastTask() || NpcDefinition.forId(taskToSet.getNpcId())
                .getSlayerLevel() > player.getSkillManager().getMaxLevel(Skill.SLAYER)) {
            assignTask();
            return;
        }
        for (NpcRequirements req : NpcRequirements.values()) {
            if (taskToSet.getNpcId() == req.getNpcId()) {
                if (req.getKillCount() > 0) {
                    if (player.getPointsHandler().getNPCKILLCount() < req.getKillCount()) {
                        assignTask();
                        return;
                    }
                } else {
                    int total = KillsTracker.getTotalKillsForNpc(req.getRequireNpcId(), player);
                    if (total < req.getAmountRequired()) {
                        assignTask();
                        return;
                    }
                }
                break;

            }
        }

        player.getPacketSender().sendInterfaceRemoval();
        this.amountToSlay = slayerTaskAmount;
        this.slayerTask = taskToSet;
        DialogueManager.start(player, SlayerDialogues.receivedTask(player, getSlayerMaster(), getSlayerTask()));
        PlayerPanel.refreshPanel(player);
        if (duoSlayer) {
            Player duo = World.getPlayerByName(duoPartner);
            duo.getSlayer().setSlayerTask(taskToSet);
            duo.getSlayer().setAmountToSlay(slayerTaskAmount);
            duo.getPacketSender().sendInterfaceRemoval();
            DialogueManager.start(duo, SlayerDialogues.receivedTask(duo, slayerMaster, taskToSet));
            PlayerPanel.refreshPanel(duo);
        }
    }

    public static int SKIP_TOKEN = 9719;

    public void resetSlayerTask() {
        SlayerTasks task = getSlayerTask();
        if (task == SlayerTasks.NO_TASK)
            return;
        if (player.getInventory().getAmount(SKIP_TOKEN) < 1) {
            player.getPacketSender().sendMessage("You must have a skip scroll to reset a task.");
            return;
        }
        this.slayerTask = SlayerTasks.NO_TASK;
        this.amountToSlay = 0;
        if (player.getSkillManager().skillCape(Skill.SLAYER)) {
            player.getPacketSender().sendMessage("Your cape allows you to keep your slayer streak.");
        } else {
            this.taskStreak = 0;
        }
        player.getInventory().delete(SKIP_TOKEN, 1);

        PlayerPanel.refreshPanel(player);
        Player duo = duoPartner == null ? null : World.getPlayerByName(duoPartner);
        if (duo != null) {
            if (duo.getSkillManager().skillCape(Skill.SLAYER)) {
                duo.getPacketSender().sendMessage("Your cape allows you to keep your slayer streak.");
            } else {
                duo.getSlayer().setTaskStreak(0);
            }
            duo.getSlayer().setSlayerTask(SlayerTasks.NO_TASK).setAmountToSlay(0);
            duo.getPacketSender()
                    .sendMessage("Your partner reset your team's Slayer task.");
            PlayerPanel.refreshPanel(duo);
            player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
        } else {
            player.getPacketSender().sendMessage("Your Slayer task has been reset.");
        }
    }

    public void killedNpc(NPC npc) {
        if (slayerTask != SlayerTasks.NO_TASK) {
            if (slayerTask.getNpcId() == npc.getId()) {
                handleSlayerTaskDeath(true);
                if (Misc.getRandom(50) == 0) {
                    player.sendMessage("You received a Slayer casket");
                    player.getInventory().add(2734, 1);
                }

                // custom tickets here?
                if (duoPartner != null) {
                    Player duo = World.getPlayerByName(duoPartner);
                    if (duo != null) {
                        if (checkDuoSlayer(player, false)) {
                            duo.getSlayer().handleSlayerTaskDeath(
                                    Locations.goodDistance(player.getPosition(), duo.getPosition(), 20));
                        } else {
                            resetDuo(player, duo);
                        }
                    }
                }
            }
        }
    }

    public void handleSlayerTaskDeath(boolean giveXp) {
        int xp = slayerTask.getXP();
        if (amountToSlay > 1) {
            amountToSlay--;
        } else {
            int amountOfTickets = 0;
            player.getPacketSender().sendMessage("You've completed your Slayer task! Return to a Slayer master for another one.");
            if (slayerTask.getTaskMaster() == SlayerMaster.EASY_SLAYER) {
                amountOfTickets += 25;
                DailyTask.EASY_SLAYER_TASKS.tryProgress(player);
            } else if (slayerTask.getTaskMaster() == SlayerMaster.MEDIUM_SLAYER) {
                amountOfTickets += 50;
                DailyTask.MEDIUM_SLAYER_TASKS.tryProgress(player);
            } else if (slayerTask.getTaskMaster() == SlayerMaster.HARD_SLAYER) {
                amountOfTickets += 75;
                DailyTask.HARD_SLAYER_TASKS.tryProgress(player);
            } else if (slayerTask.getTaskMaster() == SlayerMaster.BOSS_SLAYER) {
                amountOfTickets += 75;
            }

            /**
             * Donator Rank bonusses
             */
            if(player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT || player.getRights().equals(PlayerRights.YOUTUBER)) {
                amountOfTickets += Misc.getRandom(15, 20);
            } else if(player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT) {
                amountOfTickets += Misc.getRandom(12, 15);
            } else if(player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT) {
                amountOfTickets += Misc.getRandom(10, 12);
            } else if(player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT) {
                amountOfTickets += Misc.getRandom(7, 10);
            } else if(player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT) {
                amountOfTickets += Misc.getRandom(3, 7);
            } else if(player.getAmountDonated() >= Donation.SAPPHIRE_DONATION_AMOUNT) {
                amountOfTickets += Misc.getRandom(1, 5);
            }

            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.RED_FENRIR_PET.npcId) {
                amountOfTickets *= 2;
            }

            if(GameSettings.DOUBLE_SLAYER) {
                amountOfTickets *= 2;
            }

            if(amountOfTickets > 0) {
                player.sendMessage("You receive " + amountOfTickets + " total slayer tickets.");
                player.getInventory().addDropIfFull(5023, amountOfTickets);
            }

            taskStreak++;
            player.getPointsHandler().incrementSlayerSpree(1);

            Achievements.doProgress(player, Achievements.Achievement.COMPLETE_20_SLAYER_TASKS);
            Achievements.doProgress(player, Achievements.Achievement.COMPLETE_50_SLAYER_TASKS);
            Achievements.doProgress(player, Achievements.Achievement.COMPLETE_150_SLAYER_TASKS);


            lastTask = slayerTask;
            slayerTask = SlayerTasks.NO_TASK;
            amountToSlay = 0;


            player.getSkillManager().addExperience(Skill.SLAYER, xp);

            givePoints(player.getSlayer().getLastTask().getTaskMaster());

        }

        PlayerPanel.refreshPanel(player);
    }

    @SuppressWarnings("incomplete-switch")
    public void givePoints(SlayerMaster master) {
        int pointsReceived = 2;
        switch (master) {
            case MEDIUM_SLAYER:
                pointsReceived = 5;
                break;
            case HARD_SLAYER:
                pointsReceived = 7;
                break;

        }
        int per5 = pointsReceived * 3;
        int per10 = pointsReceived * 5;

        // Bonus tickets
        if (player.getSlayer().getTaskStreak() % 10 == 0) {
            player.getInventory().addDropIfFull(5023, 100);
            player.getPacketSender().sendMessage("You received 100 Slayer tickets");
        } else if (player.getSlayer().getTaskStreak() % 5 == 0) {
            player.getInventory().addDropIfFull(5023, 50);
            player.getPacketSender().sendMessage("You received 50 Slayer tickets");
        } else {
            player.getInventory().addDropIfFull(5023, 2);
            player.getPacketSender().sendMessage("You received 2 Slayer tickets.");
        }

        PlayerPanel.refreshPanel(player);
    }

    public boolean assignDuoSlayerTask() {
        player.getPacketSender().sendInterfaceRemoval();
        if (player.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
            player.getPacketSender().sendMessage("You already have a Slayer task.");
            return false;
        }
        Player partner = World.getPlayerByName(duoPartner);
        if (partner == null) {
            player.getPacketSender().sendMessage("");
            player.getPacketSender().sendMessage("You can only get a new Slayer task when your duo partner is online.");
            return false;
        }
        if (partner.getSlayer().getDuoPartner() == null
                || !partner.getSlayer().getDuoPartner().equals(player.getUsername())) {
            resetDuo(player, null);
            return false;
        }
        if (partner.getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
            player.getPacketSender().sendMessage("Your partner already has a Slayer task, head-ass.");
            return false;
        }
        if (partner.getSlayer().getSlayerMaster() != player.getSlayer().getSlayerMaster()) {
            player.getPacketSender().sendMessage("You and your partner need to have the same Slayer master.");
            return false;
        }
        if (partner.getInterfaceId() > 0) {
            player.getPacketSender().sendMessage("Your partner must close all their open interfaces.");
            return false;
        }
        return true;
    }

    public static boolean checkDuoSlayer(Player p, boolean login) {
        if (p.getSlayer().getDuoPartner() == null) {
            return false;
        }
        Player partner = World.getPlayerByName(p.getSlayer().getDuoPartner());
        if (partner == null) {
            return false;
        }
        if (partner.getSlayer().getDuoPartner() == null
                || !partner.getSlayer().getDuoPartner().equals(p.getUsername())) {
            resetDuo(p, null);
            return false;
        }
        if (partner.getSlayer().getSlayerMaster() != p.getSlayer().getSlayerMaster()) {
            resetDuo(p, partner);
            return false;
        }
        if (login) {
            p.getSlayer().setSlayerTask(partner.getSlayer().getSlayerTask());
            p.getSlayer().setAmountToSlay(partner.getSlayer().getAmountToSlay());
        }
        return true;
    }

    public static void resetDuo(Player player, Player partner) {
        if (partner != null) {
            if (partner.getSlayer().getDuoPartner() != null
                    && partner.getSlayer().getDuoPartner().equals(player.getUsername())) {
                partner.getSlayer().setDuoPartner(null);
                partner.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
                PlayerPanel.refreshPanel(partner);
            }
        }
        player.getSlayer().setDuoPartner(null);
        player.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
        PlayerPanel.refreshPanel(player);
    }

    public void handleInvitation(boolean accept) {
        if (duoInvitation != null) {
            Player inviteOwner = World.getPlayerByName(duoInvitation);
            if (inviteOwner != null) {
                if (accept) {
                    if (duoPartner != null) {
                        player.getPacketSender().sendMessage("You already have a Slayer duo partner.");
                        inviteOwner.getPacketSender()
                                .sendMessage("" + player.getUsername() + " already has a Slayer duo partner.");
                        return;
                    }
                    inviteOwner.getPacketSender()
                            .sendMessage("" + player.getUsername() + " has joined your duo Slayer team.")
                            .sendMessage("Seek respective Slayer master for a task.");
                    inviteOwner.getSlayer().setDuoPartner(player.getUsername());
                    PlayerPanel.refreshPanel(inviteOwner);
                    player.getPacketSender()
                            .sendMessage("You have joined " + inviteOwner.getUsername() + "'s duo Slayer team.");
                    player.getSlayer().setDuoPartner(inviteOwner.getUsername());
                    PlayerPanel.refreshPanel(player);
                } else {
                    player.getPacketSender().sendMessage("You've declined the invitation.");
                    inviteOwner.getPacketSender()
                            .sendMessage("" + player.getUsername() + " has declined your invitation.");
                }
            } else
                player.getPacketSender().sendMessage("Failed to handle the invitation.");
        }
    }

    public void handleSlayerRingTP2(int itemId) {
        if (!player.getClickDelay().elapsed(4500))
            return;
        if (player.getMovementQueue().isLockMovement())
            return;
        SlayerTasks task = getSlayerTask();
        if (task == SlayerTasks.NO_TASK)
            return;
        Position slayerTaskPos = new Position(task.getTaskPosition().getX(), task.getTaskPosition().getY(),
                task.getTaskPosition().getZ());
        if (!TeleportHandler.checkReqs(player, slayerTaskPos))
            return;
        TeleportHandler.teleportPlayer(player, slayerTaskPos, player.getSpellbook().getTeleportType());
    }

    public void handleSlayerRingTP(int itemId) {
        if (!player.getClickDelay().elapsed(4500))
            return;
        if (player.getMovementQueue().isLockMovement())
            return;
        SlayerTasks task = getSlayerTask();
        if (task == SlayerTasks.NO_TASK)
            return;
        Position slayerTaskPos = new Position(task.getTaskPosition().getX(), task.getTaskPosition().getY(),
                task.getTaskPosition().getZ());
        if (!TeleportHandler.checkReqs(player, slayerTaskPos))
            return;
        TeleportHandler.teleportPlayer(player, slayerTaskPos, player.getSpellbook().getTeleportType());
        Item slayerRing = new Item(itemId);
        player.getInventory().delete(slayerRing);
        if (slayerRing.getId() < 13288)
            player.getInventory().add(slayerRing.getId() + 1, 1);
        if (slayerRing.getId() == 27)
            player.getInventory().delete(28, 1).add(27, 1);


        else
            player.getPacketSender().sendMessage("Your Ring of Slaying degrades.");
    }

    public int getAmountToSlay() {
        return this.amountToSlay;
    }

    public Slayer setAmountToSlay(int amountToSlay) {
        this.amountToSlay = amountToSlay;
        return this;
    }

    public int getTaskStreak() {
        return this.taskStreak;
    }

    public Slayer setTaskStreak(int taskStreak) {
        this.taskStreak = taskStreak;
        return this;
    }

    public SlayerTasks getLastTask() {
        return this.lastTask;
    }

    public void setLastTask(SlayerTasks lastTask) {
        this.lastTask = lastTask;
    }

    public boolean doubleSlayerXP = false;

    public Slayer setDuoPartner(String duoPartner) {
        this.duoPartner = duoPartner;
        return this;
    }

    public String getDuoPartner() {
        return duoPartner;
    }

    public SlayerTasks getSlayerTask() {
        return slayerTask;
    }

    public Slayer setSlayerTask(SlayerTasks slayerTask) {
        this.slayerTask = slayerTask;
        return this;
    }

    public SlayerMaster getSlayerMaster() {
        return slayerMaster;
    }


    public void setSlayerMaster(SlayerMaster master) {
        this.slayerMaster = master;
    }

    public void setDuoInvitation(String player) {
        this.duoInvitation = player;
    }

    // TODO remove points related stuff
    public static boolean handleRewardsInterface(Player player, int button) {
        if (player.getInterfaceId() == 36000) {
            switch (button) {
                case -29534:
                    player.getPacketSender().sendInterfaceRemoval();
                    break;
                case -29531:
                    ShopManager.getShops().get(47).open(player);
                    break;
            }
            player.getPacketSender().sendString(36030,
                    "Current Points:   " + player.getPointsHandler().getSlayerPoints());
            return true;
        }
        return false;
    }
}
