package com.ruse.world.entity.actor.player.controller;

import com.ruse.model.*;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.magic.MagicSpells;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public abstract class Controller {

    protected Player player;

    public final void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public final Object[] getArguments() {
        return player.getControllerManager().getLastControllerArguments();
    }

    public final void setArguments(Object[] objects) {
        player.getControllerManager().setLastControllerArguments(objects);
    }

    public final void removeController() {
        player.getControllerManager().removeControllerWithoutCheck();
    }

    public abstract void start();

    public boolean canEat(int heal) {
        return true;
    }

    public boolean canPot(int pot) {
        return true;
    }

    public boolean canTakeItem(GroundItem item) {
        return true;
    }

    public boolean keepCombating(Entity target) {
        return true;
    }

    public boolean canEquip(int slotId, int itemId) {
        return true;
    }

    public boolean canRemoveEquip(int slotId, int itemId) {
        return true;
    }

    public boolean canAttack(Entity target) {
        return true;
    }

    public void trackXP(int skillId, int addedXp) { }

    public boolean canDeleteInventoryItem(int itemId, int amount) {
        return true;
    }

    public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
        return true;
    }

    public boolean canAddInventoryItem(int itemId, int amount) {
        return true;
    }

    public boolean canPlayerOption1(Player target) {
        return true;
    }

    public boolean canPlayerOption2(Player target) {
        return true;
    }

    public boolean canPlayerOption3(Player target) {
        return true;
    }

    public boolean canPlayerOption4(Player target) {
        return true;
    }

    public boolean canHit(Entity entity) {
        return true;
    }


    public void process() {

    }

    public void moved() {

    }

    public void magicTeleported(int type) {

    }

    public void sendInterfaces() {

    }

    public boolean processMagicTeleport(Position toTile) {
        return true;
    }

    public boolean processItemTeleport(Position toTile) {
        return true;
    }

    public boolean processObjectTeleport(Position toTile) {
        return true;
    }

    public boolean processObjectClick1(GameObject object) {
        return true;
    }

    public boolean processItemClick1(int slot, Item item) {
        return true;
    }

    public boolean processButtonClick(int componentId) {
        return true;
    }

    public boolean processNPCClick1(NPC npc) {
        return true;
    }

    public boolean processNPCClick2(NPC npc) {
        return true;
    }

    public boolean processNPCClick3(NPC npc) {
        return true;
    }

    public boolean processNPCClick4(NPC npc) {
        return true;
    }

    public boolean processObjectClick2(GameObject object) {
        return true;
    }

    public boolean processObjectClick3(GameObject object) {
        return true;
    }

    public boolean processObjectClick4(GameObject object) {
        return true;
    }

    public boolean processObjectClick5(GameObject object) {
        return true;
    }

    public boolean handleItemOnObject(GameObject object, Item item) {
        return true;
    }

    public void processOutgoingHit(final CombatContainer hit) {
        System.out.println("test hit");
    }

    public void processIncommingHit(final Hit hit, Entity target) {

    }

    /**
     * return let default death
     */
    public boolean appendDeath() {
        return true;
    }

    /**
     * return can move that step
     */
    public boolean canMove(int dir) {
        return true;
    }

    /**
     * return can set that step
     */
    public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
        return true;
    }

    /**
     * return remove controller
     */
    public boolean login() {
        return true;
    }

    /**
     * return remove controller
     */
    public boolean logout() {
        return true;
    }

    public void forceClose() {
    }

    public boolean processItemOnNPC(NPC npc, Item item) {
        return true;
    }

    public boolean canDropItem(Item item) {
        return true;
    }

    public boolean canSummonFamiliar() {
        return true;
    }

    public boolean processItemOnPlayer(Player p2, int item, int slot) {
        return true;
    }

    public void processNPCDeath(NPC npc) {

    }

    public void trackLevelUp(int skillId, int level) {

    }

    public boolean handleMagicSpells(MagicSpells spell) {
        return false;
    }

    public boolean canMoneyPouchWithdraw(int amount) {
        return true;
    }
}
