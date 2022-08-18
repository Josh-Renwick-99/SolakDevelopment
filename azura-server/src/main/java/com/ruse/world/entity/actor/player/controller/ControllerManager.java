package com.ruse.world.entity.actor.player.controller;


import com.ruse.model.*;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.magic.MagicSpells;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.io.Serializable;

public final class ControllerManager implements Serializable {

    public static final int MAGIC_TELEPORT = 0, ITEM_TELEPORT = 1, OBJECT_TELEPORT = 2;
    
    private transient Player player;
    private transient Controller controller;

    public Controller getController() {
        return controller;
    }
    private transient boolean inited;

    private String lastController;
    private Object[] lastControllerArguments;

    public ControllerManager(Player player) {
        this.player = player;
        lastController = "";
    }

    public void startController(String key, Object... parameters) {
        if (controller != null)
            forceStop();
        controller = ControllerHandler.getController(key);
        if (controller == null)
            return;
        controller.setPlayer(player);
        lastControllerArguments = parameters;
        lastController = (String) key;
        controller.start();
        inited = true;
    }

    public void login() {
        if (lastController == null)
            return;
        controller = ControllerHandler.getController(lastController);
        if (controller == null) {
            forceStop();
            return;
        }
        controller.setPlayer(player);
        if (controller.login())
            forceStop();
        else
            inited = true;
    }

    public void logout() {
        if (controller == null)
            return;
        if (controller.logout())
            forceStop();
    }

    public boolean canMove(int dir) {
        if (controller == null || !inited)
            return true;
        return controller.canMove(dir);
    }

    public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
        if (controller == null || !inited)
            return true;
        return controller.checkWalkStep(lastX, lastY, nextX, nextY);
    }

    public boolean canTakeItem(GroundItem item) {
        if (controller == null || !inited)
            return true;
        return controller.canTakeItem(item);
    }

    public boolean keepCombating(Entity target) {
        if (controller == null || !inited)
            return true;
        return controller.keepCombating(target);
    }

    public boolean canEquip(int slotId, int itemId) {
        if (controller == null || !inited)
            return true;
        return controller.canEquip(slotId, itemId);
    }

    public boolean canRemoveEquip(int slotId, int itemId) {
        if (controller == null || !inited)
            return true;
        return controller.canRemoveEquip(slotId, itemId);
    }

    public boolean canAddInventoryItem(int itemId, int amount) {
        if (controller == null || !inited)
            return true;
        return controller.canAddInventoryItem(itemId, amount);
    }

    public void trackXP(int skillId, int addedXp) {
        if (controller == null || !inited)
            return;
        controller.trackXP(skillId, addedXp);
    }

    public void trackLevelUp(int skillId, int level) {
        if (controller == null || !inited)
            return;
        controller.trackLevelUp(skillId, level);
    }

    public boolean canDeleteInventoryItem(int itemId, int amount) {
        if (controller == null || !inited)
            return true;
        return controller.canDeleteInventoryItem(itemId, amount);
    }

    public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
        if (controller == null || !inited)
            return true;
        return controller.canUseItemOnItem(itemUsed, usedWith);
    }

    public boolean canAttack(Entity entity) {
        if (controller == null || !inited)
            return true;
        return controller.canAttack(entity);
    }

    public boolean canPlayerOption1(Player target) {
        if (controller == null || !inited)
            return true;
        return controller.canPlayerOption1(target);
    }

    public boolean canPlayerOption2(Player target) {
        if (controller == null || !inited)
            return true;
        return controller.canPlayerOption2(target);
    }

    public boolean canPlayerOption3(Player target) {
        if (controller == null || !inited)
            return true;
        return controller.canPlayerOption3(target);
    }

    public boolean canPlayerOption4(Player target) {
        if (controller == null || !inited)
            return true;
        return controller.canPlayerOption4(target);
    }

    public boolean canHit(Entity entity) {
        if (controller == null || !inited)
            return true;
        return controller.canHit(entity);
    }

    public void moved() {
        if (controller == null || !inited)
            return;
        controller.moved();
    }

    public void magicTeleported(int type) {
        if (controller == null || !inited)
            return;
        controller.magicTeleported(type);
    }

    public void sendInterfaces() {
        if (controller == null || !inited)
            return;
        controller.sendInterfaces();
    }

    public void process() {
        if (controller == null || !inited)
            return;
        controller.process();
    }

    public boolean appendDeath() {
        if (controller == null || !inited)
            return true;
        return controller.appendDeath();
    }

    public boolean canEat(int heal) {
        if (controller == null || !inited)
            return true;
        return controller.canEat(heal);
    }

    public boolean canPot(int pot) {
        if (controller == null || !inited)
            return true;
        return controller.canPot(pot);
    }

    public boolean processTeleport(TeleportType teleType, Position toTile) {
        if (controller == null || !inited)
            return true;
        switch (teleType) {
            case NORMAL:
            case ANCIENT:
            case LUNAR:
                return processMagicTeleport(toTile);
            case RING_TELE:
            case TELE_TAB:
                return processItemTeleport(toTile);
            case LEVER:
                return processObjectTeleport(toTile);
            default:
                return processMagicTeleport(toTile);
        }
    }

    public boolean processMagicTeleport(Position toTile) {
        if (controller == null || !inited)
            return true;
        return controller.processMagicTeleport(toTile);
    }

    public boolean processItemTeleport(Position toTile) {
        if (controller == null || !inited)
            return true;
        return controller.processItemTeleport(toTile);
    }

    public boolean processObjectTeleport(Position toTile) {
        if (controller == null || !inited)
            return true;
        return controller.processObjectTeleport(toTile);
    }

    public boolean processObjectClick1(GameObject object) {
        if (controller == null || !inited)
            return true;
        return controller.processObjectClick1(object);
    }

    public boolean processItemClick1(int slot, Item item) {
        if (controller == null || !inited)
            return true;
        return controller.processItemClick1(slot, item);
    }

    public boolean processButtonClick(int componentId) {
        if (controller == null || !inited)
            return true;
        return controller.processButtonClick(componentId);
    }

    public boolean processNPCClick1(NPC npc) {
        if (controller == null || !inited)
            return true;
        return controller.processNPCClick1(npc);
    }

    public boolean canSummonFamiliar() {
        if (controller == null || !inited)
            return true;
        return controller.canSummonFamiliar();
    }

    public boolean processNPCClick2(NPC npc) {
        if (controller == null || !inited)
            return true;
        return controller.processNPCClick2(npc);
    }

    public boolean processNPCClick3(NPC npc) {
        if (controller == null || !inited)
            return true;
        return controller.processNPCClick3(npc);
    }

    public boolean processNPCClick4(NPC npc) {
        if (controller == null || !inited)
            return true;
        return controller.processNPCClick4(npc);
    }

    public boolean processObjectClick2(GameObject object) {
        if (controller == null || !inited)
            return true;
        return controller.processObjectClick2(object);
    }

    public boolean processObjectClick3(GameObject object) {
        if (controller == null || !inited)
            return true;
        return controller.processObjectClick3(object);
    }

    public boolean processItemOnNPC(NPC npc, Item item) {
        if (controller == null || !inited)
            return true;
        return controller.processItemOnNPC(npc, item);
    }

    public boolean canDropItem(Item item) {
        if (controller == null || !inited)
            return true;
        return controller.canDropItem(item);
    }

    public boolean canMoneyPouchWithdraw(int amount) {
        if (controller == null || !inited)
            return true;
        return controller.canMoneyPouchWithdraw(amount);
    }


    public void forceStop() {
        if (controller != null) {
            controller.forceClose();
            controller = null;
        }
        lastControllerArguments = null;
        lastController = null;
        inited = false;
    }

    public void removeControllerWithoutCheck() {
        controller = null;
        lastControllerArguments = null;
        lastController = null;
        inited = false;
    }

    public void setLastController(String controller, Object... args) {
        lastController = controller;
        lastControllerArguments = args;
    }

    public Object[] getLastControllerArguments() {
        return lastControllerArguments;
    }

    public String getLastController() {
        return lastController;
    }

    public void setLastControllerArguments(Object[] lastcontrollerArguments) {
        this.lastControllerArguments = lastcontrollerArguments;
    }

    public boolean processObjectClick4(GameObject object) {
        if (controller == null || !inited)
            return true;
        return controller.processObjectClick4(object);
    }

    public boolean processObjectClick5(GameObject object) {
        if (controller == null) {
            return true;
        }
        if (!inited) {
            return true;
        }
        return controller.processObjectClick5(object);
    }

    public boolean handleItemOnObject(GameObject object, Item item) {
        if (controller == null || !inited)
            return true;
        return controller.handleItemOnObject(object, item);
    }

    public boolean processItemOnPlayer(Player p2, int item, int slot) {
        if (controller == null || !inited)
            return true;
        return controller.processItemOnPlayer(p2, item, slot);
    }

    public void processNPCDeath(NPC id) {
        if (controller == null || !inited)
            return;
        controller.processNPCDeath(id);
    }

    public void processIncommingHit(Hit hit, Entity target) {
        if (controller == null || !inited)
            return;
        controller.processIncommingHit(hit, target);
    }

    public void processOutgoingHit(CombatContainer hit) {
        if (controller == null || !inited)
            return;
        controller.processOutgoingHit(hit);
    }

    public boolean handleMagicSpells(MagicSpells spell) {
        if(controller == null || !inited)
            return false;
        return controller.handleMagicSpells(spell);
    }

}
