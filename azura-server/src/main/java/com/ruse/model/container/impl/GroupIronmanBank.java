package com.ruse.model.container.impl;

import com.ruse.model.Flag;
import com.ruse.model.GameMode;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.WeaponAnimations;
import com.ruse.model.definitions.WeaponInterfaces;
import com.ruse.world.World;
import com.ruse.world.content.BonusManager;
import com.ruse.world.content.combat.magic.Autocasting;
import com.ruse.world.content.skill.impl.old_dungeoneering.Dungeoneering;
import com.ruse.world.entity.impl.player.Player;

/**
 * 100% safe Bank System
 * 
 * @author Gabriel Hannason
 */

public class GroupIronmanBank extends ItemContainer {

	public GroupIronmanBank() {
		super(null);
	}

	public void add(Item[] items) {
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			add(item);
		}

	}

	public GroupIronmanBank open(Player player) {
		player.getPacketSender().sendClientRightClickRemoval();
		if (Dungeoneering.doingOldDungeoneering(player)) {
			return this;
		}
		if (player.getLocation() != Location.DUNGEONEERING) {
			if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
				player.getPacketSender().sendInterfaceRemoval()
						.sendMessage("Ultimate-ironman-players cannot use banks.");
				return this;
			}
		}
		sortItems();
		refreshItems(player);
		player.setBanking(true).setInputHandling(null);
		player.getPacketSender().sendConfig(115, player.withdrawAsNote() ? 0 : 1)
				.sendConfig(304, player.swapMode() ? 1 : 0)
				.sendConfig(111, player.isPlaceholders() ? 0 : 1)
				.sendInterfaceSet(106000, 5063);
		return this;
	}

	public GroupIronmanBank switchItem(Player player, ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if (!player.isBanking() || player.getInterfaceId() != 106000 || to instanceof Inventory
				&& !(player.getGroupIronmanBank().contains(item.getId())
						|| player.getBankSearchingAttribtues().getSearchedBank() != null
								&& player.getBankSearchingAttribtues().getSearchedBank().contains(item.getId()))) {
			player.getPacketSender().sendClientRightClickRemoval();
			return this;
		}

		ItemDefinition def = ItemDefinition.forId(item.getId() + 1);
		if (to.getFreeSlots() <= 0 && (!(to.contains(item.getId()) && item.getDefinition().isStackable()))
				&& !(player.withdrawAsNote() && def != null && def.isNoted() && to.contains(def.getId()))) {
			to.full();
			return this;
		}
		if (item.getAmount() > to.getFreeSlots() && !item.getDefinition().isStackable()) {
			if (to instanceof Inventory) {
				if (player.withdrawAsNote()) {
					if (def == null || !def.isNoted())
						item.setAmount(to.getFreeSlots());
				} else
					item.setAmount(to.getFreeSlots());
			}
		}


			if (getItems()[slot].getId() != item.getId() || !contains(item.getId()))
				return this;
			if (item.getAmount() > getAmount(item.getId())) {
				item.setAmount(getAmount(item.getId()));
			}

			if (item.getAmount() <= 0) {
				// Placeholder
				getItems()[slot].setId(-1);
				refreshItems(player);
				return this;
			}

			if (to instanceof Inventory) {
				boolean withdrawAsNote = player.withdrawAsNote() && def != null && def.isNoted()
						&& item.getDefinition() != null
						&& def.getName().equalsIgnoreCase(item.getDefinition().getName())
						&& !def.getName().contains("Torva") && !def.getName().contains("Virtus")
						&& !def.getName().contains("Pernix") && !def.getName().contains("Torva");
				int checkId = withdrawAsNote ? item.getId() + 1 : item.getId();
				if (to.getAmount(checkId) + item.getAmount() > Integer.MAX_VALUE
						|| to.getAmount(checkId) + item.getAmount() <= 0) {
					player.getPacketSender()
							.sendMessage("You cannot withdraw that entire amount into your inventory.");
					return this;
				}
			}

		setPlayer(player);
		delete(item, slot, refresh, to);

		if (player.withdrawAsNote()) {
			if (def != null && def.isNoted() && item.getDefinition() != null
					&& def.getName().equalsIgnoreCase(item.getDefinition().getName())
					&& !def.getName().contains("Torva") && !def.getName().contains("Virtus")
					&& !def.getName().contains("Pernix") && !def.getName().contains("Torva"))
				item.setId(item.getId() + 1);
			else
				player.getPacketSender().sendMessage("This item cannot be withdrawn as a note.");
		}
		to.add(item, refresh);

		if (sort)
			sortItems();
		if (refresh) {
			refreshItems(player);
			to.refreshItems();
		}
		return this;
	}

	@Override
	public int capacity() {
		return 1000;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public ItemContainer refreshItems() {
		return null;
	}

	@Override
	public ItemContainer full() {
		return null;
	}

	public GroupIronmanBank refreshItems(Player player) {
		refreshInterface(player);
		if (player.getIronmanGroup() != null){
			for (String name : player.getIronmanGroup().getMembers()){
				Player target = World.getPlayerByName(name);
				if (target != null &&!target.equals(player) &&  target.getInterfaceId() == 106000){
					target.getGroupIronmanBank().refreshInterface(target);
				}
			}
		}
		return this;
	}

	public GroupIronmanBank refreshInterface(Player player) {
		GroupIronmanBank bank = this;
		player.getPacketSender().sendString(106015, "" + bank.getValidItems().size());
		player.getPacketSender().sendString(106016, "" + bank.capacity());
		player.getPacketSender().sendItemContainer(INTERFACE_ID_REAL, bank);
		player.getPacketSender().sendItemContainer(INVENTORY_INTERFACE_ID, player.getInventory());
		if (!player.isBanking() || player.getInterfaceId() != 106000)
			player.getPacketSender().sendClientRightClickRemoval();
		return this;
	}

	public GroupIronmanBank full(Player player) {
		player.getPacketSender().sendMessage("Not enough space in bank.");
		return this;
	}

	public static void depositItems(Player p, ItemContainer from, boolean ignoreReqs) {
		if (!ignoreReqs)
			if (!p.isBanking() || p.getInterfaceId() != 106000)
				return;

		if (p.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
			if (Dungeoneering.doingOldDungeoneering(p)) {
				p.getPacketSender().sendMessage("Your items have been banked.");
			} else {
				p.getPacketSender().sendMessage("You can only withdraw items here, not deposit");
				return;
			}
		}

		for (Item it : from.getValidItems()) {
			if (p.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
				if (p.getGroupIronmanBank().getFreeSlots() <= 322
						&& !(p.getGroupIronmanBank().contains(it.getId())
								&& it.getDefinition().isStackable())) {
					p.getPacketSender()
							.sendMessage("@red@Bank full. You only have 30 slots as a hc iron man for dungeoneering.");

					return;
				}
			}
			if (p.getGroupIronmanBank().getFreeSlots() <= 0
					&& !(p.getGroupIronmanBank().contains(it.getId()) && it.getDefinition().isStackable())) {
				p.getPacketSender().sendMessage("Bank full.");
				return;
			}

			Item toBank = new Item(ItemDefinition.forId(it.getId()).isNoted() ? (it.getId() - 1) : it.getId(),
					it.getAmount());


			int bankAmt = p.getGroupIronmanBank().getAmount(toBank.getId());
			if (bankAmt + toBank.getAmount() >= Integer.MAX_VALUE || bankAmt + toBank.getAmount() <= 0) {
				p.getPacketSender().sendMessage("Your bank cannot hold that amount of that item.");
				continue;
			}
			p.getGroupIronmanBank().add(toBank.copy(), false);

			from.delete(it.getId(), it.getAmount(), false);
		}
		from.refreshItems();
		p.getGroupIronmanBank().sortItems();
		p.getGroupIronmanBank().refreshItems(p);
		if (from instanceof Equipment) {
			Autocasting.resetAutocast(p, true);
			p.getPacketSender().sendMessage("Autocast spell cleared.");
			WeaponInterfaces.assign(p, p.getEquipment().get(Equipment.WEAPON_SLOT));
			BonusManager.update(p);
			p.setStaffOfLightEffect(-1);
			p.getUpdateFlag().flag(Flag.APPEARANCE);
			WeaponAnimations.update(p);
		}
	}

	public static boolean isEmpty(GroupIronmanBank bank) {
		return bank.sortItems().getValidItems().size() <= 0;
	}

	public static int getInterfaceModel(GroupIronmanBank bank) {
		if (bank.getItems()[0] == null)
			return -1;
		int model = bank.getItems()[0].getId();
		int amount = bank.getItems()[0].getAmount();
		if (model == 995) {
			if (amount > 9999) {
				model = 1004;
			} else if (amount > 999) {
				model = 1003;
			} else if (amount > 249) {
				model = 1002;
			} else if (amount > 99) {
				model = 1001;
			} else if (amount > 24) {
				model = 1000;
			} else if (amount > 4) {
				model = 999;
			} else if (amount > 3) {
				model = 998;
			} else if (amount > 2) {
				model = 997;
			} else if (amount > 1) {
				model = 996;
			}
		}
		return model;
	}

	/**
	 * The bank interface id.
	 */
	public static final int INTERFACE_ID = -25069;
	public static final int INTERFACE_ID_REAL = 106003;

	/**
	 * The bank inventory interface id.
	 */
	public static final int INVENTORY_INTERFACE_ID = 5064;

	/**
	 * The item id of the selected item in the 'bank X' option
	 */


	/**
	 * The items in this container.
	 */
	private Item[] items;

	public Item[] array() {
		return items.clone();
	}

}