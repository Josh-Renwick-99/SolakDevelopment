package com.ruse.world.content.upgrading;

import com.ruse.model.Item;
import com.ruse.model.PlayerRights;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.skill.impl.summoning.BossPets;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeInterface {

    private Player player;
    private Upgradeables selectedUpgrade;
    private ArrayList<Upgradeables> upgradeablesArrayList;

    public UpgradeInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {

        switch (buttonId) {
            case 121020:
                handleUpgrade(true);
                return true;
            case 121021:
                handleUpgrade(false);
                return true;
            case 121005:
                openInterface(Upgradeables.UpgradeType.WEAPON);
                return true;
            case 121006:
                openInterface(Upgradeables.UpgradeType.ARMOUR);
                return true;
            case 121007:
                openInterface(Upgradeables.UpgradeType.ACCESSORY);
                return true;
            case 121008:
                openInterface(Upgradeables.UpgradeType.MISC);
                return true;
        }

        return false;
    }


    public void handleUpgrade(boolean all) {

        if (!player.getClickDelay().elapsed(1200)) {
            player.getPacketSender().sendMessage("<shad=1>@red@Please wait a few seconds before trying to upgrade again.");
            return;
        }
        player.getClickDelay().reset();

        if (selectedUpgrade == null) {
            player.sendMessage("Choose an item to upgrade.");
            return;
        }
        Arrays.stream(Upgradeables.values()).forEach(val -> {
            if (val.getRequired().getId() == selectedUpgrade.getRequired().getId()) {
                if (getRestrictions(val, all)) {
                    ItemDefinition definition = ItemDefinition.forId(val.getRequired().getId() + 1);
                    boolean noted = false;
                    if (all) {
                        if (definition.isNoted() && definition.isStackable()) {
                            String name = definition.getName();
                            definition = ItemDefinition.forId(val.getRequired().getId());
                            String originalName = definition.getName();
                            noted = name.equals(originalName);
                        }
                        int amount = player.getInventory().getAmount(selectedUpgrade.getRequired().getId()) +
                                (noted ? player.getInventory().getAmount(selectedUpgrade.getRequired().getAmount() + 1) : 0);
                        int failed = 0, success = 0;
                        for (int i = 0; i < amount; i++) {
                            if (player.getInventory().contains(ItemDefinition.TOKEN_ID,
                                    val.getCost())) {
                                if (noted) {
                                    if (player.getInventory().contains(val.getRequired().getId())) {
                                        player.getInventory().delete(val.getRequired());
                                    } else if (player.getInventory().contains(val.getRequired().getId() + 1)) {
                                        player.getInventory().delete(new Item(val.getRequired().getId() + 1,
                                                val.getRequired().getAmount()));
                                    }
                                } else {
                                    player.getInventory().delete(val.getRequired());
                                }
                                player.getInventory().delete(ItemDefinition.TOKEN_ID,
                                        val.getCost());

                              
                                boolean random =  Misc.getRandomDouble(99) < getBoost(val.getSuccessRate());
                                if (random) {
                                    player.getSkillManager().addExperience(Skill.DUNGEONEERING, 1000);
                                    success++;
                                    if (noted) {
                                        player.getBank(Bank.getTabForItem(player, val.getReward().getId())).add(val.getReward(), false);
                                        player.sendMessage("Your items have been banked.");
                                    } else {
                                        player.getInventory().add(val.getReward());
                                    }
                                    if (val.isRare()) {
                                        String msg = "@blu@<img=5>[UPGRADE]<img=5>@red@ " + player.getUsername()
                                                + " Has successfully upgraded his "
                                                + val.getRequired().getDefinition().getName() + " to "
                                                + val.getReward().getDefinition().getName();
                                        World.sendMessage(msg);
                                    }
                                    PlayerLogs.logUpgrades(player.getUsername(), "UPGRADED : " + val.getRequired().getDefinition().getName()
                                            + ", id: " + val.getRequired().getId() + ", amount: " + val.getRequired().getAmount() + " --into-- " +
                                            val.getReward().getDefinition().getName() + ", id: " + val.getReward().getId() + ", amount: " + val.getReward().getAmount());

                                } else {
                                    failed++;
                                }
                            } else {
                                break;
                            }
                        }
                        player.getPacketSender().sendMessage("You successfully upgraded " + success
                                + " items and failed to upgrade " + failed + " items.");
                    } else {
                        if (definition.isNoted() && definition.isStackable()) {
                            String name = definition.getName();
                            definition = ItemDefinition.forId(val.getRequired().getId());
                            String originalName = definition.getName();
                            noted = name.equals(originalName);
                        }
                        if (noted) {
                            if (player.getInventory().contains(val.getRequired().getId())) {
                                player.getInventory().delete(val.getRequired());
                            } else if (player.getInventory().contains(val.getRequired().getId() + 1)) {
                                player.getInventory().delete(new Item(val.getRequired().getId() + 1,
                                        val.getRequired().getAmount()));
                            }
                        } else {
                            player.getInventory().delete(val.getRequired());
                        }
                        player.getInventory().delete(ItemDefinition.TOKEN_ID, val.getCost());

                        TimerTask task = new TimerTask() {
                            int tick = 0;

                            @Override
                            public void run() {
                                if (tick == 0) {
                                    player.getPacketSender().sendMessage("You try to upgrade....");
                                } else if (tick == 2) {
                                    boolean success =  Misc.getRandomDouble(99) < getBoost(val.getSuccessRate());
                                    if (success) {
                                        player.getSkillManager().addExperience(Skill.DUNGEONEERING, 1000);
                                        player.getPacketSender().sendMessage("You successfully upgraded your item!");
                                        player.getInventory().add(val.getReward());
                                        if (val.isRare()) {
                                            String msg = "@blu@<img=5>Upgrade:<img=5>@red@ " + player.getUsername()
                                                    + " Has successfully upgraded his "
                                                    + val.getRequired().getDefinition().getName() + " to "
                                                    + val.getReward().getDefinition().getName();
                                            World.sendMessage(msg);
                                        }

                                    } else {
                                        player.getPacketSender().sendMessage("You failed to upgrade!");
                                    }
                                    cancel();
                                }
                                tick++;
                            }

                        };

                        Timer timer = new Timer();
                        timer.schedule(task, 500, 500);
                    }
                }
            }
        });
    }
    
    public double getBoost(double chance){
        double percentBoost = 0D;
        if(player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT || player.getRights().equals(PlayerRights.YOUTUBER)) {
            percentBoost += 15;
        } else if(player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT) {
            percentBoost += 10;
        } else if(player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT) {
            percentBoost += 7;
        } else if(player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT) {
            percentBoost += 5;
        }

        // creator set:
        if (player.getEquipment().contains(23127))
            percentBoost += 2;
        if (player.getEquipment().contains(23128))
            percentBoost += 2;
        if (player.getEquipment().contains(23129))
            percentBoost += 2;
        if (player.getEquipment().contains(23130))
            percentBoost += 1;
        if (player.getEquipment().contains(23131))
            percentBoost += 1;
        if (player.getEquipment().contains(23132))
            percentBoost += 1;
        if (player.getEquipment().contains(23133))
            percentBoost += 1;
        //

        if(!player.isInsideRaids()) {
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.RAMMUS_PET.npcId) {
                percentBoost += 20;
            }
        }
        if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ODIN_PET.npcId) {
            percentBoost += 25;
        }


        double multiplier = 1 + (percentBoost / 100D);

        chance *= multiplier;

        return chance;
    }

    private boolean getRestrictions(Upgradeables data, boolean all) {
        ItemDefinition definition = ItemDefinition.forId(data.getRequired().getId() + 1);
        boolean noted = false;
        if (definition.isNoted() && definition.isStackable()) {
            String name = definition.getName();
            definition = ItemDefinition.forId(data.getRequired().getId());
            String originalName = definition.getName();
            noted = name.equals(originalName);
        }
        if (noted && player.getInventory().contains(data.getRequired().getId() + 1)) {
            int amount = all ? player.getInventory().getAmount(data.getRequired().getId() + 1) : 1;
            if (!player.getInventory().contains(data.getRequired().getId() + 1, amount) ||
                    !player.getInventory().contains(ItemDefinition.TOKEN_ID, amount *
                            data.getCost())) {
                player.getPacketSender().sendMessage("You do not have the required items!");
                return false;
            }
        } else {
            if (!player.getInventory().contains(data.getRequired().getId()) ||
                    !player.getInventory().contains(ItemDefinition.TOKEN_ID,  data.getCost())) {
                player.getPacketSender().sendMessage("You do not have the required items!");
                return false;
            }
        }
        return true;
    }

    public void handleItemAction(int slot) {
        if (upgradeablesArrayList != null && upgradeablesArrayList.get(slot) != null) {
            showUpgrade(upgradeablesArrayList.get(slot));
        }
    }


    public void showUpgrade(Upgradeables upgrade) {

        selectedUpgrade = upgrade;

        player.getPacketSender().sendItemOnInterface(121017, upgrade.getReward());
        player.getPacketSender().sendString(121018, "Tokens req: @whi@" + Misc.formatNumber(upgrade.getCost()));
        player.getPacketSender().sendString(121019, "Success rate: @whi@" + upgrade.getSuccessRate() + "%");

    }

    public void openInterface(Upgradeables.UpgradeType type) {
        player.getPacketSender().sendConfig(5334, type.ordinal());

        selectedUpgrade = null;

        player.getPacketSender().sendItemOnInterface(121017, -1, 1);
        player.getPacketSender().sendString(121018, "Tokens req: @whi@---");
        player.getPacketSender().sendString(121019, "Success rate: @whi@---");

        upgradeablesArrayList = Upgradeables.getForType(type);
        for (int i = 0; i < 50; i++) {
            if (upgradeablesArrayList.size() > i) {
                player.getPacketSender().sendItemOnInterface(62209,
                        upgradeablesArrayList.get(i).getRequired().getId(), i, upgradeablesArrayList.get(i).getRequired().getAmount());
            } else {
                player.getPacketSender().sendItemOnInterface(62209, -1, i, 1);
            }
        }

        player.getPacketSender().sendInterface(121000);

    }

}
