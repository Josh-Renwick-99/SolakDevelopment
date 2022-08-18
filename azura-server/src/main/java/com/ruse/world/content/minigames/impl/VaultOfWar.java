package com.ruse.world.content.minigames.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class VaultOfWar {


    public static Box[] loot = { //
            new Box(23099, 1, 1D, true),
            new Box(23100, 1, 1D, true),
            new Box(23101, 1, 1D, true),
            new Box(23102, 1, 1D, true),
            new Box(23096, 1, 1D, true),
            new Box(23097, 1, 1D, true),
            new Box(23098, 1, 1D, true),
            new Box(23095, 1, 1D, true),
    };


    public static final int[] T_GLOVES = new int[] { 23099, 23100, 23101, 23102};
    private static final int[] COMBAT_GLOVES = new int[] { 23096, 23097, 23098};
    private static final int MERCENARY_GLOVE = 23095;
    public static final int AVATAR_ID = 9839;
    public static final int GLOVES_NPC = 1208;
    public static final int TOKEN_NPC = 9838;

    public static final Position TELEPORT_AREA = new Position(1776,5335,0);

    public static final int TOKEN_ID = 12852;

    public static void enterEasternDungeon(Player player) {
        if (player.getPosition().getX() >= 1764 &&
                !player.getInventory().contains(TOKEN_ID) && player.getInventory().getAmount(TOKEN_ID) < 100) {
            player.sendMessage("You need atleast 100 tokens to do this!");
            return;
        }
        if (player.getPosition().getX() >= 1764) {
            //enter
            player.moveTo(new Position(player.getPosition().getX() - 3,player.getPosition().getY(),0));
            handleTokenRemoval(player);
        } else {
            //leave
            player.moveTo(new Position(player.getPosition().getX() + 3,player.getPosition().getY(),0));
            resetTokenRoomVariables(player);
        }
    }

    public static void handleTokenRemoval(final Player player) {
        if (player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom())
            return;
        player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(true);
        TaskManager.submit(new Task(50, player, false) {
            @Override
            public void execute() {
                if (!player.getMinigameAttributes().getWarriorsGuildAttributes().enteredTokenRoom()) {
                    this.stop();
                    return;
                }
                if (player.getPosition().getRegionId() != 6995 || player.getLocation() != Locations.Location.VAULT_OF_WAR) {
                    player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
                    this.stop();
                    return;
                }
                if (player.getInventory().contains(TOKEN_ID)) {
                    player.getInventory().delete(TOKEN_ID, 25);
                    player.performGraphic(new Graphic(1368));
                    player.getPacketSender().sendMessage("Some of your tokens crumble to dust..");
                } else {
                    player.moveTo(new Position(1771, 5333, 0));
                    resetTokenRoomVariables(player);
                    player.getPacketSender().sendMessage("You have run out of tokens.");
                    this.stop();
                }
            }
        });
    }

    private static void resetTokenRoomVariables(Player player) {
        player.getMinigameAttributes().getWarriorsGuildAttributes().setEnteredTokenRoom(false);
        player.getCombatBuilder().cooldown(true);
        player.getMovementQueue().reset();
        resetNpcCombat(player);
    }

    public static void resetNpcCombat(Player player) {
        for (NPC n : player.getLocalNpcs()) {
            if (n == null)
                continue;
            if (n.getPosition().getRegionId() == 6995
                    && n.getCombatBuilder().getVictim() != null && n.getCombatBuilder().getVictim() == player) {
                n.getCombatBuilder().cooldown(true);
                n.getMovementQueue().reset();
            }
        }
    }

    public static Item replaceDrop(Player player, NPC npc) {
        int currentTGlovesIndex = player.lastTGloveIndex;
        Item glove;
        if ((currentTGlovesIndex + 1) >= T_GLOVES.length) {
            glove = new Item(COMBAT_GLOVES[Misc.random(COMBAT_GLOVES.length - 1)]);//combat glove
        } else {
            glove = new Item(T_GLOVES[getNextTGloveIndex(player)]);
        }

        int droprate = 999999999;

        if(glove != null) {
            if (glove.getId() == 23099) {//T1
                droprate = 25;
            } else if (glove.getId() == 23100) {//T2
                droprate = 75;
            } else if (glove.getId() == 23101) {//T3
                droprate = 150;
            } else if (glove.getId() == 23102) {//T4
                droprate = 200;
            } else if (glove.getId() == 23098) {//archer
                droprate = 500;
            } else if (glove.getId() == 23097) {//warrior
                droprate = 500;
            } else if (glove.getId() == 23096) {//wizard
                droprate = 500;
            }
        }

        return Misc.random(droprate) == 0 ? glove : null;
    }

    public static void useGlovesOnObject(Player player) {
        if (hasAllCombatRings(player)) {
            DialogueManager.start(player, new VowDialogue(player));
        } else {
            player.sendMessage("You must have all 3 combat gloves to forge mercenary gloves.");
        }
    }

    public static void forgeGloves(Player player) {
        player.getPacketSender().sendInterfaceRemoval();
        if (hasAllCombatRings(player)) {
            //player.performAnimation(new Animation(896));
            player.getInventory().delete(COMBAT_GLOVES[0],1);
            player.getInventory().delete(COMBAT_GLOVES[1],1);
            player.getInventory().delete(COMBAT_GLOVES[2],1);
            player.isForging = true;
            player.setCurrentTask(new Task(2, player, false) {
                int amountSpan = 0;


                @Override
                public void execute() {
                    if(amountSpan == 1) {
                        player.performAnimation(new Animation(4411));
                        player.performGraphic(new Graphic(734));
                    } else if(amountSpan == 4) {
                        if (Misc.random(100) <= 20) {
                            player.performGraphic(new Graphic(751));
                            player.getInventory().add(new Item(MERCENARY_GLOVE, 1));
                            player.sendMessage("You have successfully upgraded the gloves!");
                            World.sendFilteredMessage("@red@" + player.getUsername() + " has successfully upgraded to their mercenary's gloves.");
                            Achievements.doProgress(player, Achievements.Achievement.COLLECT_MERCENARY_GLOVES);
                        } else {
                            player.performGraphic(new Graphic(749));
                            player.sendMessage("You failed to forge the mercenary's gloves!");
                        }
                    } else if(amountSpan == 5) {
                        player.isForging = false;
                        stop();
                    }
                    amountSpan++;
                }
            });
            TaskManager.submit(player.getCurrentTask());
        }
    }

    private static boolean hasAllCombatRings(Player player) {
        boolean ring1 = false;
        boolean ring2 = false;
        boolean ring3 = false;
        for (int i = 0; i < COMBAT_GLOVES.length; i++) {
            if (player.getInventory().contains(COMBAT_GLOVES[i])/* || player.getEquipment().contains(COMBAT_GLOVES[i])*/) {
                if (i == 0)
                    ring1 = true;
                else if (i == 1)
                    ring2 = true;
                else if (i == 2)
                    ring3 = true;
            }
        }
        return ring1 && ring2 && ring3;
    }

    public static void useGlovesOnNPC(Player player, final Item item) {

        if (item.getId() >= 23099 && item.getId() <= 23102) {//t gloves
            int currentGloves = getTGloveIndex(player);
            if (currentGloves >= 3) {
                player.lastTGloveIndex = currentGloves;
                player.sendMessage("You've gotten the final gloves already.");
            } else {
                if (currentGloves > player.lastTGloveIndex || player.lastTGloveIndex < 0) {
                    player.lastTGloveIndex = currentGloves;
                    int newId = T_GLOVES[currentGloves + 1];
                    player.sendMessage("You are now hunting for: " + newId + "/" + ItemDefinition.forId(newId).getName() + ".");
                }
            }
        } else if (item.getId() >= 23096 && item.getId() <= 23098) {
            player.sendMessage("Combat gloves drop randomly. You don't need to show me them to unlock the next one.");
        }
    }

    public static int getTGloveIndex(Player player) {
        int foundIndex = -1;
        for (int i = 0; i < T_GLOVES.length; i++) {
            if (player.getInventory().contains(T_GLOVES[i]) || player.getEquipment().contains(T_GLOVES[i])) {
                foundIndex = i;
            }
        }
        return foundIndex;
    }

    public static int getNextTGloveIndex(Player player) {
        int foundIndex = player.lastTGloveIndex;

        if (foundIndex == -1)
            foundIndex = 0;
        else if (foundIndex != (T_GLOVES.length - 1))
            foundIndex++;
        return foundIndex;
    }

}