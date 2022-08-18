package com.ruse.world.entity.impl.mini;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Inventory;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.impl.EnterAmountOfBonesForMiniPlayer;
import com.ruse.net.PlayerSession;
import com.ruse.net.packet.impl.EquipPacketListener;
import com.ruse.util.AccessPlayer;
import com.ruse.world.World;
import com.ruse.world.content.Consumables;
import com.ruse.world.content.MemberScrolls;
import com.ruse.world.content.skill.impl.prayer.BonesData;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerHandler;
import com.ruse.world.entity.impl.player.PlayerLoading;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class MiniPManager {
    private final Player player;

    @Getter
    @Setter
    private MiniPlayer miniPlayer;
    @Getter
    @Setter
    private boolean dead;

    /**
     * Unlocks the mini player pet for a player
     */
    public void unlockMiniPlayer() {
        if(miniPlayer != null) {
            player.sendMessage("You have already unlocked a mini player.");
            return;
        }
        if(spawnMiniPlayer()) {
            player.setHasMiniPlayer(true);
        }
    }

    /**
     * Spawns a player mini player and registers to game world
     */
    public boolean spawnMiniPlayer() {
        player.setSpawningMiniPlayer(true);
        TaskManager.submit(new Task(2) {
            int tick = 0;
            @SuppressWarnings("unlikely-arg-type")
			@Override
            protected void execute() {
                switch (tick) {
                    case 1:
                        player.sendMessage("Attempting to summon mini player.");
                        player.getPA().sendInterfaceRemoval();
                        break;

                    case 2:
                        player.getPA().sendInterfaceRemoval();
                        if(!player.getInventory().contains(10944)) {
                            stop();
                        }
                        MiniPlayer playerMini = new MiniPlayer(player.getUsername() + "s Pet", player.getPosition(), new PlayerSession(null));
                        playerMini.setMiniPlayerOwner(player);

                        PlayerLoading.getResult(playerMini);

                        if (World.getPlayerByName(playerMini.getUsername()) != null) {
                            System.out.println("Error: unable to add bot '" + playerMini.getUsername() + "' - already logged in.");
                            stop();
                        }

                        if (World.getLoginQueue().contains(this) || World.getLogoutQueue().contains(this)) {
                            System.out.println("Error: unable to add bot '" + playerMini.getUsername() + "' - already in queue.");
                            stop();
                        }
                        if(playerMini != null) {
                            PlayerHandler.handleLogin(playerMini);
                            setMiniPlayer(playerMini);
                            miniPlayer.save();
                        }
                        break;
                    case 3:
                        if(miniPlayer.getMiniPlayerOwner() == null || !World.getPlayers().contains(miniPlayer.getMiniPlayerOwner()) || World.getLogoutQueue().contains(miniPlayer.getMiniPlayerOwner()))
                            World.getLogoutQueue().add(miniPlayer);
                        stop();
                }
                tick++;
            }

            @Override
            public void stop() {
                player.setSpawningMiniPlayer(false);
                setEventRunning(false);

                
            }
        });
        return true;
    }

    /**
     * Picks the mini player up and deregisters from game world
     */
    public void pickupMiniPlayer() {
        if(player == null || miniPlayer == null)
        	return;
        player.getMiniPManager().getMiniPlayer().save();
        World.getLogoutQueue().add(miniPlayer);
        miniPlayer = null;

        
    }

    //10944
    public boolean handleSummoningScroll() {
        if(player.busy())
            return false;
        boolean pickUp = miniPlayer != null;
        if(!player.isHasMiniPlayer()) {
            unlockMiniPlayer();
            return true;
        }
        if(pickUp) {
            pickupMiniPlayer();
        } else {
            spawnMiniPlayer();
        }
        return true;
    }
    /**
     * Disposes of a mini player, removing all traces of it
     */
    public void disposeMiniPlayer() {
        if(miniPlayer != null)
            pickupMiniPlayer();
        TaskManager.submit(new Task(3) {
            @Override
            protected void execute() {
                player.setSpawningMiniPlayer(true);
                boolean delete = true;
                Path path = Paths.get("./data/saves/characters/", player.getUsername() + "s pet.json");
                File file = path.toFile();
                if(!file.exists())
                    delete = false;
                if(miniPlayer != null) {
                }
                if(delete) {
                    if(file.delete())
                        player.sendMessage("Your mini player has been disposed off successfully.");
                }
                player.setHasMiniPlayer(false);
                player.setSpawningMiniPlayer(false);
                stop();
            }
        });
    }

    /**
     * Force player mini player to follow owner
     */
    public void followOwner() {
        if(dead)
            return;
        miniPlayer.setPositionToFace(player.getPosition());
        miniPlayer.setEntityInteraction(player);
        miniPlayer.getMovementQueue().setFollowCharacter(player);
    }

    /**
     * Handle loot from npc killed by mini player
     * @param loot - item received
     */
    public void onNpcKill(Item loot) {
        player.getBank(0).add(loot);
        player.sendMessage("Your mini player has retrieved " + loot.getAmount() + " " + loot.getDefinition().getName() +" and placed it in your bank.");
    }
    public static void removeMiniPlayerFromPlayer(String username) {
        new AccessPlayer() {

            @Override
            public void modifyPlayer(Player player) {
                player.getMiniPManager().disposeMiniPlayer();
            }

            @Override
            public int playerLoadMethod(Player player) {
                return -1;
            }

            @Override
            public String playerName() {
                return username;
            }
        }.complete();
    }

    /**
     * Use an item on mini player
     * @param item - item id
     * @param amount - item amount
     * @param slot - item slot on container
     * @return
     */
    public void useItemWith(int item, int amount, int slot) {
        final String[] result = {"An error has occured when using an item on your mini player. Please report this bug."};
        TaskManager.submit(new Task(2) {
            @Override
            protected void execute() {
                try {
                    if(!player.getInventory().contains(item)) {
                        stop();
                        return;
                    }
                    if(item == 6643) {
                        if(miniPlayer.getMiniPlayerDropRate() == 100) {
                            result[0] = "Your mini players drop rate already sits at the maximum allowed(100%)";
                            stop();
                            return;
                        }
                        player.getInventory().delete(6643, 1);
                        miniPlayer.incrementMiniPlayerDropRate(10);
                        player.sendMessage("Your mini players drop rate has been increased by 10%");
                        result[0] = "Your mini players drop rate now sits at " + ((int) (miniPlayer.getMiniPlayerDropRate())) + "% drop rate.";
                        stop();
                        return;
                    }
                    if(BonesData.forId(item) != null) {
                    	getMiniPlayer().setSelectedSkillingItem(item);
                        player.getPacketSender().sendEnterAmountPrompt("How many bones should your mini player bury?");
                        player.setInputHandling(new EnterAmountOfBonesForMiniPlayer());
                        result[0] = "Keep the bones in your inventory whilst your mini player burys them.";
                        stop();
                        return;
                    }
                    if(MemberScrolls.handleScroll(miniPlayer, item, false)) {
                        result[0] = "Your mini players funds now sit at $" + miniPlayer.getAmountDonated();
                        stop();
                        return;
                    }
                    if(Consumables.isFood(miniPlayer, item, slot)) {
                        result[0] = "Your mini player has eaten it's meal.";
                        stop();
                        return;
                    }

                    //Always equip last
                    if(ItemDefinition.forId(item).isEquitable || MiniOverride.checkOverride(item)) {
                        miniPlayer.getEquipment().forSlot(ItemDefinition.forId(item).getEquipmentSlot()).setAmount(-1).setId(-1);
                        if (EquipPacketListener.equipItem(miniPlayer, item, slot, Inventory.INTERFACE_ID)) {
                            result[0] = "You have equipped your mini player with a " + ItemDefinition.forId(item).getName();
                            stop();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stop();
            }

            @Override
            public void stop() {
                
                sendResult();
                setEventRunning(false);
                if(miniPlayer != null)
                    miniPlayer.save();
            }

            private void sendResult() {
                player.sendMessage(result[0]);
            }
        });
    }

}