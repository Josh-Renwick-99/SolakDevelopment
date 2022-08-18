package com.ruse.net.packet.impl;

import com.ruse.model.Item;
import com.ruse.model.container.impl.Bank;
import com.ruse.model.container.impl.Inventory;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener is called when an item is dragged onto another slot.
 * 
 * @author relex lawl
 */

public class SwitchItemSlotPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		int interfaceId = packet.readLEShortA();
		packet.readByteC();
		int fromSlot = packet.readLEShortA();
		int toSlot = packet.readLEShort();

		switch (interfaceId) {
			case Inventory.INTERFACE_ID:
			case Bank.INVENTORY_INTERFACE_ID:
				if (fromSlot >= 0 && fromSlot < player.getInventory().capacity() && toSlot >= 0 && toSlot < player.getInventory().capacity() && toSlot != fromSlot) {
					player.getInventory().swap(fromSlot, toSlot);
				}
				break;
			case Bank.INTERFACE_ID:
				if (player.isBanking() && player.getInterfaceId() == 5292) {
					if (fromSlot == toSlot)
						return;
					if (player.getBankSearchingAttribtues().isSearchingBank()) {
						Bank bank = player.getBankSearchingAttribtues().getSearchedBank();
						if (bank != null) {
							if (player.swapMode()) {
								bank.shiftSwap(fromSlot, toSlot);
							} else {
								bank.swap(fromSlot, toSlot);//.sortItems();
							}
							bank.open(player, false, false);
						}
					} else {
						if (player.swapMode()) {
							player.getBank(player.getCurrentBankTab()).shiftSwap(fromSlot, toSlot);
						} else {
							player.getBank(player.getCurrentBankTab()).swap(fromSlot, toSlot);
						}
						player.getBank(player.getCurrentBankTab()).open(player, false, false);
					}
					return;
				} else {
					player.getPacketSender().sendInterfaceRemoval();
				}
				break;
		}
		if (player.isBanking() && player.getInterfaceId() == 5292) {
			boolean toBankTab = false;
			for (int i = 0; i < Bank.BANK_TAB_INTERFACES.length; i++) {
				toBankTab = Bank.BANK_TAB_INTERFACES[i][0] == interfaceId;
				Item item = new Item(player.getBank(player.getCurrentBankTab()).getItems()[fromSlot].getId(), 
						player.getBank(player.getCurrentBankTab()).getItems()[fromSlot].getAmount());

				int realAmt = player.getBank(player.getCurrentBankTab()).getAmount(item.getId());
				if (realAmt == 0) {
					return;
				}

				if (!player.getBank(player.getCurrentBankTab()).contains(item.getId()) || realAmt < item.getAmount())
					return;
				if (toBankTab) {
					if (player.getBankSearchingAttribtues().isSearchingBank()) {
						player.getPacketSender().sendMessage("You cannot do that right now.");
						return;
					}
					int bankTab = Bank.BANK_TAB_INTERFACES[i][1];
					int slot = player.getBank(player.getCurrentBankTab()).slotOf(item);
					if (slot < 0)
						return;
					player.setNoteWithdrawal(false);
					player.getBank(Bank.getTabForItem(player, item)).switchItem(player.getBank(bankTab), item, slot, false, false);
					Bank.sendTabs(player, null);
					player.getBank(player.getCurrentBankTab()).open(player, false, false);
					//player.getBank(player.getCurrentBankTab()).open(false);
					return;
				}
			}
		}
	}
}