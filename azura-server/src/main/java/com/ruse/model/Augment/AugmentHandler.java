package com.ruse.model.Augment;

import com.ruse.GameSettings;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AugmentHandler {
    List<ItemAugment> augments = new ArrayList<>();
    private final int interfaceId = 46343;

    public void add(Integer toItemId, Integer augmentId, Player player){
        Augment newAugment = Augment.getById(augmentId);
        List<ItemAugment> newAugmentList = new ArrayList<>(augments);
        if (newAugmentedItem(toItemId)) {
            newAugmentList.add(ItemAugment.builder().itemId(toItemId).firstAugment(newAugment).build());
        }else if(!itemAugmentExists(newAugment.getItemId())){
            newAugmentList.add(ItemAugment.builder().itemId(toItemId).firstAugment(newAugment).build());
        } else if (augments.size() == 0){
            newAugmentList.add(ItemAugment.builder().itemId(toItemId).firstAugment(newAugment).build());
        } else {
            augments.forEach(augment -> {
                if (augment.getItemId().equals(toItemId)) {
                    if (augment.getFirstAugment() == null) {
                        augment.setFirstAugment(newAugment);
                    } else if (augment.getSecondAugment() == null) {
                        augment.setSecondAugment(newAugment);
                    } else if (augment.getThirdAugment() == null) {
                        augment.setThirdAugment(newAugment);
                    } else {
                        player.getPacketSender().sendMessage("This item is currently full of augments, please remove one to add more");
                    }
                }
            });
        }
        augments = newAugmentList;
    }

    public boolean newAugmentedItem(Integer newItemId){
        for (ItemAugment aug : augments){
            if (aug.getItemId().equals(newItemId)){
                return false;
            }
        }
        return true;
    }

    public void remove(Integer augmentSlot, Player player){
        augments.forEach(toRemove -> {
            if (augmentSlot == 1){
                toRemove.setFirstAugment(null);
                updateInterface(toRemove.getItemId(), player);
            } else if (augmentSlot == 2){
                toRemove.setSecondAugment(null);
                updateInterface(toRemove.getItemId(), player);
            } else if (augmentSlot == 3){
                toRemove.setThirdAugment(null);
                updateInterface(toRemove.getItemId(), player);
            }
        });
        removeUnusedItemAugments();
        updateInterface(player.getCurrentAugment(), player);
    }

    public void removeUnusedItemAugments(){
        List<ItemAugment> augmentsToRemove = new ArrayList<>();
        for (ItemAugment augment : augments) {
            if (augment.getFirstAugment() == null && augment.getSecondAugment() == null && augment.getThirdAugment() == null) {
                augmentsToRemove.add(augment);
            }
        }
        List<ItemAugment> newAugments = new ArrayList<>();
        augments.forEach(aug -> {
            if (!augmentsToRemove.contains(aug)){
                newAugments.add(aug);
            }
        });
        augments = newAugments;
    }

    public ItemAugment getItemsAugmentsFromItemId(Integer itemId){
        for (ItemAugment aug : augments){
            if (aug.getItemId().equals(itemId)){
                return aug;
            }
        }
        return null;
    }

    public void openInterface(Player player){
        if (player.getCurrentAugment() != null) {
            updateInterface(player.getCurrentAugment(), player);
        }
        player.getPacketSender().sendTabInterface(GameSettings.STAFF_TAB, interfaceId);
    }

    public void updateInterface(Integer itemAugment, Player player) {
        ItemAugment augment = null;

        for (ItemAugment aug : augments) {
            if (aug.getItemId().equals(itemAugment)) {
                augment = player.getAugmentHandler().getItemsAugmentsFromItemId(itemAugment);
            }
        }
        if (augment != null) {
            player.getPacketSender().sendString(interfaceId + 12, "Item: " + ItemDefinition.forId(augment.getItemId()).getName());
            player.getPacketSender().sendInterfaceModel(interfaceId + 8, augment.getItemId(), 500);
            if (augment.getFirstAugment() != null) {
                player.getPacketSender().sendString(interfaceId + 9, augment.getFirstAugment().getAugmentItemName());
                player.getPacketSender().sendString(interfaceId + 15, augment.getFirstAugment().name());
            } else {
                player.getPacketSender().sendString(interfaceId + 9, "Empty Socket");
                player.getPacketSender().sendString(interfaceId + 15, "No Equipped Augment");
            }

            if (augment.getSecondAugment() != null) {
                player.getPacketSender().sendString(interfaceId + 10, augment.getSecondAugment().getAugmentItemName());
                player.getPacketSender().sendString(interfaceId + 16, augment.getSecondAugment().name());
            } else {
                player.getPacketSender().sendString(interfaceId + 10, "Empty Socket");
                player.getPacketSender().sendString(interfaceId + 16, "No Equipped Augment");
            }

            if (augment.getThirdAugment() != null) {
                player.getPacketSender().sendString(interfaceId + 11, augment.getThirdAugment().getAugmentItemName());
                player.getPacketSender().sendString(interfaceId + 17, augment.getThirdAugment().name());
            } else {
                player.getPacketSender().sendString(interfaceId + 11, "Empty Socket");
                player.getPacketSender().sendString(interfaceId + 17, "No Equipped Augment");
            }
        } else {
            player.getPacketSender().sendString(interfaceId + 9, "Empty Socket");
            player.getPacketSender().sendString(interfaceId + 15, "No Equipped Augment");
            player.getPacketSender().sendString(interfaceId + 10, "Empty Socket");
            player.getPacketSender().sendString(interfaceId + 16, "No Equipped Augment");
            player.getPacketSender().sendString(interfaceId + 11, "Empty Socket");
            player.getPacketSender().sendString(interfaceId + 17, "No Equipped Augment");
        }

        player.getPacketSender().sendTabInterface(GameSettings.STAFF_TAB, interfaceId);
    }

    private boolean itemAugmentExists(Integer augmentId){
        for (ItemAugment aug : augments){
            if (aug.getFirstAugment() == null && aug.getSecondAugment() == null && aug.getThirdAugment() == null){
                return false;
            }
        }
        return true;
    }

    public void viewNextAugment(Player player){
        if (augments.size() > 1) {
            Integer currentAugmentId = player.getCurrentAugment();
            Integer augmentIndex = null;
            for (ItemAugment aug : augments) {
                if (aug.getItemId().equals(currentAugmentId)) {
                    augmentIndex = augments.indexOf(aug);
                    augmentIndex++;
                }
            }
            if (augmentIndex < augments.size()) {
                player.setCurrentAugment(augments.get(augmentIndex).getItemId());
            } else {
                player.setCurrentAugment(augments.get(0).getItemId());
            }
            updateInterface(player.getCurrentAugment(), player);
        }
    }

    public void viewPreviousAugment(Player player){
        if (augments.size() > 1) {
            Integer currentAugmentId = player.getCurrentAugment();
            Integer augmentIndex = null;
            for (ItemAugment aug : augments) {
                if (aug.getItemId().equals(currentAugmentId)) {
                    augmentIndex = augments.indexOf(aug);
                }
            }
            if (augmentIndex > 0) {
                player.setCurrentAugment(augments.get(augmentIndex - 1).getItemId());
            } else {
                player.setCurrentAugment(augments.get(augments.size() - 1).getItemId());
            }
            updateInterface(player.getCurrentAugment(), player);
        }
    }

    private boolean containsAugment(Integer augmentItemId){
        for(ItemAugment augment : augments) {
            if(augment.getFirstAugment() != null){
                if (augment.getFirstAugment().getItemId() == augmentItemId) {
                    return true;
                }
            }

            if (augment.getSecondAugment() != null) {
                if (augment.getSecondAugment().getItemId() == augmentItemId){
                    return true;
                }
            }

            if (augment.getThirdAugment() != null) {
                if (augment.getSecondAugment().getItemId() == augmentItemId) {
                    return true;
                }
            }
        }
        return false;
    }

    public AugmentHandler(){
        if(this.augments == null){
            this.augments = new ArrayList<>();
        }
    }

    public List<ItemAugment> getAugments(){
        if(this.augments == null){
            this.augments = new ArrayList<>();
        }
        return this.augments;
    }
    public void setAugments(List<ItemAugment> itemAugments){this.augments = itemAugments;}
}
