package com.ruse.world.entity.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ruse.engine.task.impl.GroundItemsTask;
import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.Sounds;
import com.ruse.world.content.Sounds.Sound;
import com.ruse.world.content.skill.impl.old_dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.old_dungeoneering.UltimateIronmanHandler;
import com.ruse.world.entity.impl.player.Player;

public class GroundItemManager {
	/**
	 * Removes a grounditem from the world
	 *
	 * @param groundItem  The grounditem to remove from the world.
	 * @param delistGItem Should the grounditem be deleted from the arraylist
	 *                    aswell?
	 */
	public static void remove(GroundItem groundItem, boolean delistGItem) {
		if (groundItem != null) {
			if (groundItem.isGlobal()) {
				for (Player p : World.getPlayers()) {
					if (p == null)
						continue;
					if (p.getPosition().distanceToPoint(groundItem.getPosition().getX(),
							groundItem.getPosition().getY()) <= 120) {
						removeItemFromScene(p, groundItem);
					}
				}
			} else {
				Player person = World.getPlayerByName(groundItem.getOwner());
				if (person != null && person.getPosition().distanceToPoint(groundItem.getPosition().getX(),
						groundItem.getPosition().getY()) <= 120) {
					removeItemFromScene(person, groundItem);
				}
			}
			if (delistGItem) {
				removeItemFromPile(groundItem);
			}
		}
	}

	/**
	 * This method spawns a grounditem for a player.
	 *
	 * @param p The owner of the grounditem
	 * @param g The grounditem to spawn
	 */
	public static void spawnGroundItem(Player p, GroundItem g) {
		if (p == null)// || p.getRights() == PlayerRights.DEVELOPER)
			return;
		final Item item = g.getItem();
		if (item.getId() > ItemDefinition.getMaxAmountOfItems() || item.getId() <= 0) {
			return;
		}
		// if(item.getDefinition().getName().toLowerCase().contains("clue scroll"))
		// return;
		if (item.getId() >= 2412 && item.getId() <= 2414) {
			p.getPacketSender().sendMessage("The cape vanishes as it touches the ground.");
			return;
		}
		if (Dungeoneering.doingOldDungeoneering(p)) {
			g = new GroundItem(item, g.getPosition(), "Dungeoneering", true, -1, false, -1);
			p.getMinigameAttributes().getDungeoneeringAttributes().getParty().getGroundItems().add(g);
			if (item.getId() == 17489) {
				p.getMinigameAttributes().getDungeoneeringAttributes().getParty()
						.setGatestonePosition(g.getPosition().copy());
			}
		}
		if (ItemDefinition.forId(item.getId()).isStackable()) {
			GroundItem it = getGroundItem(p, item, g.getPosition());
			if (it != null) {// I'll brb, I know the fix, gotta go eat rq aight.
				it.getItem().setAmount(Math.min(it.getItem().getAmount() + g.getItem().getAmount(), Integer.MAX_VALUE));
				if (it.getItem().getAmount() <= 0)
					remove(it, true);
				else
					it.setRefreshNeeded(true);
				return;
			}
		}
		/*
		 * if(Misc.getMinutesPlayed(p) < 15) { g.setGlobalStatus(false);
		 * g.setGoGlobal(false); }
		 */
		add(g, true);
	}

	/**
	 * Adds a grounditem to the world
	 *
	 * @param groundItem The grounditem to add to the world
	 * @param listGItem  Should the grounditem be added to the arraylist?
	 */
	public static void add(GroundItem groundItem, boolean listGItem) {
		if (groundItem.isGlobal()) {
			for (Player p : World.getPlayers()) {
				if (p == null)
					continue;
				if (groundItem.getPosition().getZ() == p.getPosition().getZ() && p.getPosition()
						.distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120)
					addItemToScene(p, groundItem);
			}
		} else {
			Player person = World.getPlayerByName(groundItem.getOwner());
			if (person != null && groundItem.getPosition().getZ() == person.getPosition().getZ() && person.getPosition()
					.distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120) {
				addItemToScene(person, groundItem);
			}
		}
		if (listGItem) {
//			if (Location.getLocation(groundItem) == Location.DUNGEONEERING)
//				groundItem.setShouldProcess(false);
			addItemToPile(groundItem);

			GroundItemsTask.fireTask();
		}
	}

	private static void addItemToPile(GroundItem groundItem) {
		List<GroundItem> pile = groundTilesByTile.get(groundItem.getPosition().longPacked());
		if (pile == null) {
			groundTilesByTile.put(groundItem.getPosition().longPacked(), pile = new CopyOnWriteArrayList<>());
		}
		pile.add(groundItem);
	}

	private static void removeItemFromPile(GroundItem groundItem) {
		List<GroundItem> pile = groundTilesByTile.get(groundItem.getPosition().longPacked());
		if (pile == null) {
			return;
		}
		pile.remove(groundItem);
	}

	private static void addItemToScene(Player p, GroundItem groundItem) {
		p.getPacketSender().createGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(),
				groundItem.getPosition().getY(), groundItem.getItem().getAmount());
		p.getItemsInScene().add(groundItem);
	}

	private static void removeItemFromScene(Player p, GroundItem groundItem) {
		p.getPacketSender().removeGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(),
				groundItem.getPosition().getY(), groundItem.getItem().getAmount());
		p.getItemsInScene().remove(groundItem);

	}

	/**
	 * Handles the pick up of a grounditem
	 *
	 * @param p        The player picking up the item
	 * @param item
	 * @param position
	 */
	public static void pickupGroundItem(Player p, Item item, Position position) {
		if (!p.getLastItemPickup().elapsed(500))
			return;
		boolean canAddItem = p.getInventory().getFreeSlots() > 0
				|| item.getDefinition().isStackable() && p.getInventory().contains(item.getId());
		if (!canAddItem) {
			p.getInventory().full();
			return;
		}
		GroundItem gt = getGroundItem(p, item, position);

		if (gt == null || gt.hasBeenPickedUp() || !p.getItemsInScene().contains(gt)) {
			return;
		}

		if (UltimateIronmanHandler.hasItemsStored(p) && p.getLocation() != Location.DUNGEONEERING) {
			p.getPacketSender().sendMessage(
					"<shad=0>@red@You cannot pick up items until you claim your stored Dungeoneering items.");
			return;
		}

		if (p.getGameMode().isIronman() && !Dungeoneering.doingOldDungeoneering(p)) {
			String owner = gt.getOwner();
			if (owner != null) { // && !owner.equals("null")
				if (!gt.isBossDrop()) {
					if (!owner.equals(p.getUsername())) {
						p.getPacketSender().sendMessage("You cannot pick this item up because it was not spawned for you.");
						return;
					}
				}
			}
		}

		if (item.getId() == 17489 && Dungeoneering.doingOldDungeoneering(p)) {
			p.getMinigameAttributes().getDungeoneeringAttributes().getParty().setGatestonePosition(null);
		}
		item = gt.getItem();
		if (item.getId() == 7509 && position.equals(GlobalItemSpawner.ROCKCAKE_POSITION)) {
			item = new Item(7510, gt.getItem().getAmount());
		}
		gt.setPickedUp(true);
		remove(gt, true);
		p.getInventory().add(item);
		if (ItemDefinition.forId(item.getId()) != null && ItemDefinition.forId(item.getId()).getName() != null) {
			PlayerLogs.log(p.getUsername(),
					"Picked up gr.Item " + item.getDefinition().getName() + ", amount: " + item.getAmount());
		} else {
			PlayerLogs.log(p.getUsername(), "Picked up gr.Item " + item.getId() + ", amount: " + item.getAmount());
		}

		PlayerLogs.logPickupItems(p.getUsername(), "Player picked up item: " + item.getDefinition().getName()
				+ ", id: " + item.getId() + ", amount: " + item.getAmount() + ", owner: " + gt.getOwner());

		p.getLastItemPickup().reset();
		Sounds.sendSound(p, Sound.PICKUP_ITEM);

	}

	/**
	 * Handles a region change for a player. This method respawns all grounditems
	 * for a player who has changed region.
	 *
	 * @param p The player who has changed region
	 */
	public static void handleRegionChange(Player p) {
		p.getItemsInScene().forEach(item -> removeItemFromScene(p, item));
		for (List<GroundItem> pile : groundTilesByTile.values()) {
			for (GroundItem gi : pile) {
				if (gi == null || p.getPosition().getZ() != gi.getPosition().getZ()
						|| p.getPosition().distanceToPoint(gi.getPosition().getX(), gi.getPosition().getY()) > 120)
					continue;
				if (gi.isGlobal() || !gi.isGlobal() && gi.getOwner().equals(p.getUsername())) {
					addItemToScene(p, gi);
				}
			}
		}
	}

	/**
	 * Checks if a grounditem exists in the stated position.
	 *
	 * @param p        The player trying to check if the grounditem exists
	 * @param item     The grounditem's item
	 * @param position The position to check if a grounditem exists on
	 * @return true if a grounditem exists in the specified position, otherwise
	 *         false
	 */

	private static final Map<Long, List<GroundItem>> groundTilesByTile = new ConcurrentHashMap<>();

	public static List<GroundItem> getGroundItems(Position position) {
		List<GroundItem> groundItems = groundTilesByTile.get(position.longPacked());
		if (groundItems == null) {
			return Collections.emptyList();
		}
		return groundItems;
	}

	public static GroundItem getGroundItem(Player p, Item item, Position position) {
		List<GroundItem> pile = getGroundItems(position);
		for (GroundItem l : pile) {
			if (l.getItem().getId() == item.getId()) {
				if (l.isGlobal()) {
					return l;
				} else if (p != null) {
					Player owner = World.getPlayerByName(l.getOwner());
					if (owner == null || owner.getIndex() != p.getIndex())
						continue;
					return l;
				}
			}
		}
		return null;
	}

	public static Map<Long, List<GroundItem>> getGroundTilesByTile() {
		return groundTilesByTile;
	}
}