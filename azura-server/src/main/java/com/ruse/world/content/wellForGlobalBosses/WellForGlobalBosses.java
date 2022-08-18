package com.ruse.world.content.wellForGlobalBosses;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;

import java.util.concurrent.CopyOnWriteArrayList;

//import com.ruse.tools.discord.DiscordConstant;
//import com.ruse.tools.discord.DiscordManager;

public class WellForGlobalBosses {

	private static final int AMOUNT_NEEDED = 10; // 1b
	private static final int LEAST_DONATE_AMOUNT_ACCEPTED = 1; // 1m

	private static final CopyOnWriteArrayList<Player> DONATORS = new CopyOnWriteArrayList<Player>();
	private static int MONEY_IN_WELL = 0;

	public static void donate(Player player, int amount) {
		if (MONEY_IN_WELL >= AMOUNT_NEEDED) {
			player.getPA().sendMessage("well is already full.");
			return;
		}
		if (amount < LEAST_DONATE_AMOUNT_ACCEPTED) {
			DialogueManager.sendStatement(player, "You must donate at least 1 Solak token.");
			return;
		}
		if (amount > getMissingAmount()) {
			amount = getMissingAmount();
		}
		if (player.getInventory().getAmount(ItemDefinition.TOKEN_ID) < amount) {
			DialogueManager.sendStatement(player, "You Don't have that many Solak tokens in your inventory.");
			return;
		}

		player.getInventory().delete(ItemDefinition.TOKEN_ID, amount);

		if (!DONATORS.contains(player)) {
			DONATORS.add(player);
		}
		MONEY_IN_WELL += amount;
		Player.Amount_Donated = amount;
		if (amount > 5) {
			DialogueManager.sendStatement(player, "Keep donating we need a total of 10b!");
			World.sendMessage("<img=5> <col=6666FF>" + player.getUsername() + " has donated "
					+ Misc.insertCommasToNumber("" + amount + "") + " bill bags to the Well of Global Bosses!");
		}
		DialogueManager.sendStatement(player, "Thank you for your donation");
		if (getMissingAmount() <= 0) {
			//World.sendMessage("<img=5> <col=6666FF>The global boss has initiated, thanks for filling it up!");
		//	TheGeneral.sequence();
		//	setDefaults();
		}
		player.getPA().sendString(16566, player.getUsername() + "(" + Misc.formatRunescapeStyle(amount) + ")");
		WellForGlobalBossesInterface.donators.add(player.getUsername());
	}

	public static void setDefaults() {
		DONATORS.clear();
		MONEY_IN_WELL = 0;
	}

	public static int getMissingAmount() {
		return (AMOUNT_NEEDED - MONEY_IN_WELL);
	}
}
