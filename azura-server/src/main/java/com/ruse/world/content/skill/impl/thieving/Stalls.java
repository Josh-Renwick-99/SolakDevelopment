package com.ruse.world.content.skill.impl.thieving;

import com.ruse.model.Animation;
import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.entity.impl.player.Player;

public class Stalls {

   /* public static Box[] loot1 = new Box[]{
            new Box(5022, 1, 2, 100),  //Pvm tickets
            new Box(5022, 3, 4, 75),  //Pvm tickets
            new Box(5020, 1, 2, 100),  //Afk tickets
            new Box(5020, 3, 4, 70),  //Afk tickets
            new Box(ItemDefinition.MILL_ID, 1, 2, 100),  //Orbs
            new Box(ItemDefinition.MILL_ID, 2, 4, 60),  //Orbs
            new Box(6199, 1, 0.4D),  //mystery box
            new Box(7956, 1, 0.3D),  //Pvm box
            new Box(19116, 0.05D), //Super mbox
    };
    public static Box[] loot2 = new Box[]{
            new Box(5022, 2, 4, 100),  //Pvm tickets
            new Box(5022, 6, 8, 75),  //Pvm tickets
            new Box(5020, 2, 4, 100),  //Afk tickets
            new Box(5020, 6, 8, 70),  //Afk tickets
            new Box(ItemDefinition.MILL_ID, 2, 4, 100),  //Orbs
            new Box(ItemDefinition.MILL_ID, 4, 8, 60),  //Orbs
            new Box(6199, 3, 0.3D),  //mystery box
            new Box(7956, 3, 0.2D),  //Pvm box
            new Box(19115, 0.05D), //Ext mbox
    };
    public static Box[] loot3 = new Box[]{
            new Box(5022, 4, 8, 100),  //Pvm tickets
            new Box(5022, 12, 16, 75),  //Pvm tickets
            new Box(5020, 4, 8, 100),  //Afk tickets
            new Box(5020, 12, 16, 70),  //Afk tickets
            new Box(ItemDefinition.MILL_ID, 4, 8, 100),  //Orbs
            new Box(ItemDefinition.MILL_ID, 8, 16, 60),  //Orbs
            new Box(6199, 7, 0.3D),  //mystery box
            new Box(7956, 7, 0.2D),  //Pvm box
            new Box(19114, 0.05D), //Grand mbox
            new Box(10946, 0.01D), //$1 scroll
    };
    public static Box[] loot4 = new Box[]{
            new Box(5022, 8, 16, 100),  //Pvm tickets
            new Box(5022, 24, 32, 75),  //Pvm tickets
            new Box(5020, 8, 16, 100),  //Afk tickets
            new Box(5020, 24, 32, 70),  //Afk tickets
            new Box(ItemDefinition.MILL_ID, 8, 16, 100),  //Orbs
            new Box(ItemDefinition.MILL_ID, 16, 32, 60),  //Orbs
            new Box(6199, 7, 0.9D),  //mystery box
            new Box(7956, 7, 0.6D),  //Pvm box
            new Box(19114, 0.15D), //Grand mbox
            new Box(10946, 0.03D), //$1 scroll
    };*/

    public static void stealFromStall(Player player, GameObject  gameObject, int lvlreq, int xp, Item reward, String message) {
        if (player.getInventory().getFreeSlots() < 1) {
            player.getPacketSender().sendMessage("You need some more inventory space to do this.");
            return;
        }
        if (player.getCombatBuilder().isBeingAttacked()) {
            player.getPacketSender()
                    .sendMessage("You must wait a few seconds after being out of combat before doing this.");
            return;
        }
        if (!player.getClickDelay().elapsed(2000))
            return;
        if (player.getSkillManager().getMaxLevel(Skill.THIEVING) < lvlreq) {
            player.getPacketSender()
                    .sendMessage("You need a Thieving level of at least " + lvlreq + " to steal from this stall.");
            return;
        }

        if (gameObject.getId() == 22772)
        Achievements.doProgress(player, Achievements.Achievement.STEAL_100_LAMP_STALLS);
        
        if (gameObject.getId() == 22774) {
            Achievements.doProgress(player, Achievements.Achievement.STEAL_250_ARMOUR_STALLS);
            Achievements.doProgress(player, Achievements.Achievement.STEAL_750_ARMOUR_STALLS);
        }

        player.performAnimation(new Animation(881));
        player.getPacketSender().sendInterfaceRemoval();
        player.getSkillManager().addExperience(Skill.THIEVING, xp);
        player.getClickDelay().reset();
        {
            player.getPacketSender().sendMessage(message);
            player.getInventory().add(reward);
        }
        player.getSkillManager().stopSkilling();
    }
/*
    public static void stealFromAFKStall(Player player, int objectId, int tier) {
        player.getSkillManager().stopSkilling();
        player.getPacketSender().sendInterfaceRemoval();
        if (!player.getClickDelay().elapsed(2000))
            return;
        if (player.getInventory().getFreeSlots() <= 0) {
            player.getPacketSender().sendMessage("You need some more inventory space to do this.");
            return;
        }
        if (player.busy() || player.getCombatBuilder().isBeingAttacked() || player.getCombatBuilder().isAttacking()) {
            player.getPacketSender().sendMessage("You cannot do that right now.");
            return;
        }
        if (tier == 2 && (player.getAfkStallCount1() < 20000)) {
            player.sendMessage("@red@You need to have stole from the Afk stall (1)" + "("
                    + player.getAfkStallCount1() + "/20,000) times");
            return;
        } else if (tier == 3 && (player.getAfkStallCount2() < 50000)) {
            player.sendMessage("@red@You need to have stole from the Afk stall (2)" + "("
                    + player.getAfkStallCount2() + "/50,000) times");
            return;
        }

        int accounts = 1;
        for (Player p : World.getPlayers()) {
            if (p == null)
                continue;
            if (!player.equals(p) && player.getHostAddress().equals(p.getHostAddress())) {
                if (p.getInteractingObject() != null && (p.getInteractingObject().getId() == 52601
                        || p.getInteractingObject().getId() == 53654 || p.getInteractingObject().getId() == 30035)) {
                    accounts++;
                    continue;
                }
            }
        }
        if (accounts > 2){
            player.getPacketSender().sendMessage("You already have two accounts stealing from the AFK Zone.");
            return;
        }

        player.setCurrentTask(new Task(4, player, true) {

            @Override
            public void execute() {
                if (player.getInventory().getFreeSlots() <= 0) {
                    player.getPacketSender().sendMessage("You do not have any free inventory space left.");
                    this.stop();
                    return;
                }
                player.performAnimation(new Animation(881));
                Box[] loot = loot1;
                if (tier == 1) {
                    player.setAfkStallCount1(player.getAfkStallCount1() + 1);
                } else if (tier == 2) {
                    loot = loot2;
                    player.setAfkStallCount2(player.getAfkStallCount2() + 1);
                } else if (tier == 3) {
                    loot = loot3;
                    player.setAfkStallCount3(player.getAfkStallCount3() + 1);
                } else if (tier == 4) {
                    loot = loot4;
                 }

                Box reward = CasketOpening.getLoot(loot);
                player.getInventory().add(reward.getId(), reward.getMin() + Misc.getRandom(reward.getMax() - reward.getMin()));
                player.getInventory().refreshItems();
                player.getClickDelay().reset();
            }

            @Override
            public void stop() {
                setEventRunning(false);
                player.performAnimation(new Animation(65535));
            }
        });
        TaskManager.submit(player.getCurrentTask());
    }*/
}
