package com.ruse.world.content;

import com.ruse.model.PlayerRights;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.mini.MiniPlayer;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;

public class MemberScrolls {

	public static void checkForRankUpdate(Player player) {
		if (player.getRights().isStaff()) {
			return;
		}
		PlayerRights rights = null;
		if (player.getAmountDonated() >= Donation.SAPPHIRE_DONATION_AMOUNT)
			rights = PlayerRights.SAPPHIRE_DONATOR;
		if (player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT)
			rights = PlayerRights.EMERALD_DONATOR;
		if (player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT)
			rights = PlayerRights.RUBY_DONATOR;
		if (player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT)
			rights = PlayerRights.DIAMOND_DONATOR;
		if (player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT)
			rights = PlayerRights.ONYX_DONATOR;
		if (player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT)
			rights = PlayerRights.ZENYTE_DONATOR;
		if (rights != null && rights != player.getRights()) {
			player.getPacketSender().sendMessage(
					"You've become a " + Misc.formatText(rights.toString().toLowerCase()) + "! Congratulations!");
			player.setRights(rights);
			player.getPacketSender().sendRights();
		}
	}

	/* 
	
	 */

	public static boolean handleScroll(Player player, int item, boolean claimAll) {
		if (!player.getInventory().contains(item) && !(player instanceof MiniPlayer)) {
			return false;
		}
		switch (item) {
			case 23057:
			case 23058:
			case 23059:
			case 23060:
				final int funds1 = item == 23057 ? 10
						: item == 23058 ? 25
						: item == 23059 ? 100 : item == 23060 ? 250 : 1;
				DialogueManager.start(player, new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public DialogueExpression animation() {
						return null;
					}

					@Override
					public String[] dialogue() {
						if (claimAll) {
							int amount = player.getInventory().getAmount(item);
							return new String[]{"Claim $" + (funds1 * amount) + " in bonds!", "Cancel"};
						} else {
							return new String[]{"Claim $" + funds1 + " bond!", "Cancel"};
						}
					}

					@Override
					public void specialAction() {
						player.bondClicked = item;
						player.bondClickedClaimAll = claimAll;
						player.setDialogueActionId(103);
					}

				});
				break;
		case 6769:
		case 10942:
		case 10934:
		case 10935:
		case 10943:
		case 10946:
			final int funds = item == 10946 ? 1
					: item == 6769 ? 5
					: item == 10942 ? 10 : item == 10934 ? 25 : item == 10935 ? 50 : 100;
			DialogueManager.start(player, new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.OPTION;
				}

				@Override
				public DialogueExpression animation() {
					return null;
				}

				@Override
				public String[] dialogue() {
					if (claimAll) {
						int amount = player.getInventory().getAmount(item);
						return new String[]{"Claim $" + (funds * amount) + " in bonds!", "Cancel"};
					} else {
						return new String[]{"Claim $" + funds + " bond!", "Cancel"};
					}
				}

				@Override
				public void specialAction() {
					player.bondClicked = item;
					player.bondClickedClaimAll = claimAll;
					player.setDialogueActionId(103);
				}

			});
			break;
		case 10944:
			if (player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was a staff member! (Unsuccessful)");
				break;
			}
			if (player.getRights().isMember()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(),
						"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(10944, 1);
				player.incrementAmountDonated(10);
				player.incrementAmountDonatedToday(10);
				player.setRights(PlayerRights.EMERALD_DONATOR);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a member account!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 19119:
			if (player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was a staff member! (Unsuccessful)");
				break;
			}
			if (player.getRights().isRegularDonator()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(),
						"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(19119, 1);
				player.setRights(PlayerRights.SAPPHIRE_DONATOR);
				player.incrementAmountDonated(10);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a donator rank!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 19120:
			if (player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was a staff member! (Unsuccessful)");
				break;
			}
			if (player.getRights().isSuperDonator()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(),
						"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(19120, 1);
				player.incrementAmountDonated(20);
				player.setRights(PlayerRights.EMERALD_DONATOR);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a member account!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 19121:
			if (player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was a staff member! (Unsuccessful)");
				break;
			}
			if (player.getRights().isExtremeDonator()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(),
						"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(19121, 1);
				player.incrementAmountDonated(60);
				player.setRights(PlayerRights.RUBY_DONATOR);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a member account!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 19122:
			if (player.getRights().isStaff()) {
				player.getPacketSender().sendMessage("As a staff member, you already have member rights.");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was a staff member! (Unsuccessful)");
				break;
			}
			if (player.getRights().isSponsorDonator()) {
				player.getPacketSender().sendMessage("You are a member, that would be wasteful!");
				PlayerLogs.log(player.getUsername(), "Attempted to redeem a " + ItemDefinition.forId(item).getName()
						+ ", but was already a member! (Unsuccessful)");
				break;
			} else {
				PlayerLogs.log(player.getUsername(),
						"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
				player.getPacketSender().sendMessage("Sending redemption request...");
				player.getInventory().delete(19122, 1);
				player.incrementAmountDonated(200);
				player.setRights(PlayerRights.DIAMOND_DONATOR);
				player.getPacketSender().sendMessage("Congratulations! You've upgraded to a member account!");
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
			}
			break;
			case 6542:
				if (!player.getClickDelay().elapsed(100)) {
					player.getPacketSender().sendMessage("Please wait before doing that.");
					break;
				}

				if (player.getInventory().getFreeSlots() >= 9) {
					player.getInventory().add(10942, 1);
					player.getInventory().add(15003, 1);
					player.getInventory().add(13101, 1);
					player.getInventory().add(20489, 1);
					player.getInventory().add(20488, 1);
					player.getInventory().add(19116, 5);
					player.getInventory().add(19115, 3);
					player.getInventory().add(19114, 2);
					player.getPacketSender().sendMessage("Enjoy your reward!");
					World.sendMessage( "<shad=1>@yel@[<img=18>" +player.getUsername() + "<img=18>] @whi@Killed 10K @red@Dan's present@whi@ and opened his holy present! Happy Bday Dan!");
					player.getInventory().delete(6542, 1);
					break;
				} else {
					player.getPacketSender().sendMessage("You'll need at least 9 free spaces to do that.");
				}

				player.getClickDelay().reset();
				break;
		case 15420:
			if (!player.getClickDelay().elapsed(100)) {
				player.getPacketSender().sendMessage("Please wait before doing that.");
				break;
			}

			if (player.getInventory().getFreeSlots() >= 6) {
				player.getInventory().add(13101, 1);
				player.getInventory().add(14595, 1);
				player.getInventory().add(14603, 1);
				player.getInventory().add(22043, 1);
				player.getInventory().add(14602, 1);
				player.getInventory().add(14605, 1);
				player.getPacketSender().sendMessage("Enjoy your presents!");
				player.getPacketSender().sendMessage("-Santa, 2016");
				player.getInventory().delete(15420, 1);
				break;
			} else {
				player.getPacketSender().sendMessage("You'll need at least 6 free spaces to do that.");
			}

			player.getClickDelay().reset();
			break;
		case 20481:
			PlayerLogs.log(player.getUsername(),
					"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
			if (player.getInventory().getFreeSlots() > 5) {
				player.getInventory().delete(20481, 1);
				player.getInventory().add(12158, 100);
				player.getInventory().add(12159, 100);
				player.getInventory().add(12160, 100);
				player.getInventory().add(12163, 100);
				player.getPacketSender().sendMessage("You unpack the crate, and find 100 of each summoning charm.");
			} else {
				player.getPacketSender().sendMessage("You need at least 5 free inventory space to unpack this.");
			}
			break;
		case 7630:
			PlayerLogs.log(player.getUsername(),
					"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");
			if (player.getInventory().getFreeSlots() > 0) {
				player.getInventory().delete(7630, 1);
				player.getInventory().add(12934, 1000);
				player.getPacketSender().sendMessage("You unpack the crate, and find 1000 of Zulrah's Scales inside.");
			} else {
				player.getPacketSender().sendMessage("You need at least 1 free inventory space to unpack this.");
			}
			break;
		case 3906:
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 8) {
				player.getInventory().delete(3906, 1);
				player.getInventory().add(19943, 1);
				player.getInventory().add(19941, 1);
				player.getInventory().add(19940, 1);
				player.getInventory().add(19939, 1);
				player.getInventory().add(19938, 1);
				player.getInventory().add(2178, 1);
				player.getInventory().add(19914, 1);
				player.getInventory().add(4178, 1);

				player.getPacketSender()
						.sendMessage("You unpack the package, and find the best melee armour set ingame.");
			} else {
				player.getPacketSender().sendMessage("You need at least 8 free inventory space to unpack this.");
			}
			break;
		case 9079:

			if (player.getInventory().getFreeSlots() > 4) {
				player.getInventory().delete(9079, 1);
				player.getInventory().add(19886, 1);
				player.getInventory().add(19116, 1);
				player.getInventory().add(4446, 1);

				player.incrementAmountDonated(100);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("$100 has been added to your amount donated.");
			} else {
				player.getPacketSender().sendMessage("You need at least 4 free inventory space to unpack this.");
			}
			break;
		case 9082:// custom pet donation
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 3) {
				player.getInventory().delete(9082, 1);
				player.getInventory().add(15228, 1);
				player.getInventory().add(19116, 1);
				// player.getInventory().add(4446, 1);

				player.incrementAmountDonated(195);
				// player.getPointsHandler().setDonatorPoints(100, true);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("$95 has been added to your amount donated.");
				player.getPacketSender()
						.sendMessage("@red@ PLEASE TRADE THIS ITEM TO ARROW FOR YOUR CUSTOM PET TRADE-IN");
				player.getPacketSender()
						.sendMessage("@red@ PLEASE TRADE THIS ITEM TO ARROW FOR YOUR CUSTOM PET TRADE-IN");
				player.getPacketSender()
						.sendMessage("@red@ PLEASE TRADE THIS ITEM TO ARROW FOR YOUR CUSTOM PET TRADE-IN");
			} else {
				player.getPacketSender().sendMessage("You need at least 3 free inventory space to unpack this.");
			}
			break;
		case 9080:// mybox donation
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 10) {
				player.getInventory().delete(9080, 1);
				player.getInventory().add(19116, 2);
				player.getInventory().add(19115, 3);
				player.getInventory().add(19114, 4);

				player.incrementAmountDonated(65);
				// player.getPointsHandler().setDonatorPoints(100, true);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("$65 has been added to your amount donated.");

			} else {
				player.getPacketSender().sendMessage("You need at least 10 free inventory space to unpack this.");
			}
			break;
		case 9081:// lootbox
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 3) {
				player.getInventory().delete(9081, 1);
				player.getInventory().add(290, 10);
				player.getInventory().add(290, 2);
				// player.getInventory().add(4446, 1);

				player.incrementAmountDonated(2);
				// player.getPointsHandler().setDonatorPoints(100, true);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("@blu@free lootboxes have been added to your donation");
				player.getPacketSender().sendMessage("$2 has been added to your amount donated.");

			} else {
				player.getPacketSender().sendMessage("You need at least 3 free inventory space to unpack this.");
			}
			break;
		case 9085:// weapon box
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 4) {
				player.getInventory().delete(9085, 1);
				player.getInventory().add(19843, 1);
				player.getInventory().add(4178, 1);
				player.getInventory().add(7640, 1);
				// player.getInventory().add(4446, 1);

				player.incrementAmountDonated(140);
				// player.getPointsHandler().setDonatorPoints(100, true);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("$140 has been added to your amount donated.");

			} else {
				player.getPacketSender().sendMessage("You need at least 4 free inventory space to unpack this.");
			}
			break;
		case 9083:// ring donation
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 5) {
				player.getInventory().delete(9083, 1);
				player.getInventory().add(15401, 1);
				player.getInventory().add(19116, 1);
				player.getInventory().add(6199, 4);
				// player.getInventory().add(4446, 1);

				player.incrementAmountDonated(37);
				// player.getPointsHandler().setDonatorPoints(100, true);

				checkForRankUpdate(player);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("You redeemed the donation");
				player.getPacketSender().sendMessage("$37 has been added to your amount donated.");

			} else {
				player.getPacketSender().sendMessage("You need at least 5 free inventory space to unpack this.");
			}
			break;
		case 3908:
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 8) {
				player.getInventory().delete(3908, 1);
				player.getInventory().add(18840, 1);
				player.getInventory().add(18837, 1);
				player.getInventory().add(18836, 1);
				player.getInventory().add(18802, 1);
				player.getInventory().add(18785, 1);
				player.getInventory().add(4357, 1);
				player.getInventory().add(20057, 1);
				player.getInventory().add(7640, 1);
				player.getPacketSender()
						.sendMessage("You unpack the package, and find the best magic armour set ingame.");
			} else {
				player.getPacketSender().sendMessage("You need at least 8 free inventory space to unpack this.");
			}
			break;
		case 3910:
			// PlayerLogs.log(player.getUsername(), "Has just redeemed a
			// "+ItemDefinition.forId(item).getName()+" successfully!");
			if (player.getInventory().getFreeSlots() > 7) {
				player.getInventory().delete(3910, 1);
				player.getInventory().add(19619, 1);
				player.getInventory().add(19618, 1);
				player.getInventory().add(19474, 1);
				player.getInventory().add(18766, 1);
				player.getInventory().add(18755, 1);
				player.getInventory().add(4355, 1);
				player.getInventory().add(19843, 1);
				player.incrementAmountDonated(100);
				player.getPacketSender()
						.sendMessage("You unpack the package, and find the best range armour set ingame.");
			} else {
				player.getPacketSender().sendMessage("You need at least 8 free inventory space to unpack this.");
			}
			break;
		case 13150:
			if (player.getInventory().getFreeSlots() > 1) {
				int boo = Misc.getRandom(10);
				if (player.getRights().isMember()) {
					boo = boo + 1;
					player.getPacketSender().sendMessage("Your Member status increases the odds of getting loot...");
				}

				if (boo >= 10) {
					player.getInventory().add(9013, 1);
					if (!player.didFriday13May2016()) {
						player.setFriday13May2016(true);
						player.getPacketSender()
								.sendMessage("<img=5> @red@Congratulations! You get a Skull sceptre from the box!");
					} else {
						player.getPacketSender()
								.sendMessage("<img=5> @red@You get another Skull sceptre from the box!");
					}
				} else {
					player.getPacketSender().sendMessage("That box was empty! Try again!");
				}
				player.getInventory().delete(13150, 1);
			} else {
				player.getPacketSender().sendMessage("You should have at least 2 open spaces first.");
			}
			break;

		}
		return false;
	}

	public static Dialogue getTotalFunds(final Player player) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}

			@Override
			public int npcId() {
				return 4657;
			}

			@Override
			public String[] dialogue() {
				return player.getAmountDonated() > 0
						? new String[] {
								"Your account has claimed scrolls worth $" + player.getAmountDonated() + " in total.",
								"Thank you for supporting us!" }
						: new String[] {
								"Your account has claimed scrolls worth $" + player.getAmountDonated() + " in total." };
			}

			@Override
			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(5);
			}
		};
	}

	public static void claimBond(Player player) {
		int item = player.getBondClicked();
		System.out.println(item);
		boolean claimAll = player.isBondClickedClaimAll();
		if (player.getInventory().contains(item)) {
			PlayerLogs.log(player.getUsername(),
					"Has just redeemed a " + ItemDefinition.forId(item).getName() + " successfully!");

			int funds = item == 10946 ? 1 : item == 23057 ? 10
					: item == 23058 ? 25
					: item == 23059 ? 100 : item == 23060 ? 250 : -1;

			int amount = claimAll ? player.getInventory().getAmount(item) : 1;
			if (claimAll) {
				funds *= amount;
			}
			player.getInventory().delete(item, amount);
			player.incrementAmountDonated(funds);
			player.incrementAmountDonatedToday(funds);
			player.getPointsHandler().setDonatorPoints(funds, true);
			player.getPacketSender().sendMessage("Your account has gained funds worth $" + (funds)
					+ ". Your total is now at $" + player.getAmountDonated() + ".");
			checkForRankUpdate(player);
			PlayerPanel.refreshPanel(player);
		}
		player.getPacketSender().sendInterfaceRemoval();
		player.bondClicked = -1;
		player.bondClickedClaimAll = false;
	}
}
