package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;

import static com.ruse.world.content.skill.impl.summoning.CharmingImp.*;

public class CurrencyPouch {
    private long upgradeTokens;
    private long pvmTickets;
    private long afkTickets;
    private long slayerTickets;
    private long voteScrolls;
    private long petFragments;
    private long instanceTokens;
    private long goldCharms;
    private long greenCharms;
    private long crimsonCharms;
    private long blueCharms;

    public static void checkBalance(Player player) {
        long amount;
        String currency;

        amount = player.getCurrencyPouch().upgradeTokens;
        currency = "Solak tokens";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().pvmTickets;
        currency = "PVM tickets";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().afkTickets;
        currency = "AFK tickets";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().slayerTickets;
        currency = "Slayer tickets";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().voteScrolls;
        currency = "Vote scrolls";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().petFragments;
        currency = "Pet fragments";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().instanceTokens;
        currency = "Instance tokens";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().goldCharms;
        currency = "Gold charms";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().greenCharms;
        currency = "Green charms";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().crimsonCharms;
        currency = "Crimson charms";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);

        amount = player.getCurrencyPouch().blueCharms;
        currency = "Blue charms";
        player.sendMessage("Your pouch contains @red@" + Misc.formatNumber(amount) + " @bla@" + currency);
    }

    public static void withdrawCurrency(Player player, int type, int amount) {
        if (amount <= 0) {
            DialogueManager.sendStatement(player, "You can not withdraw an invalid amount!");
            return;
        }

        boolean insufficientFundsError = false;
        boolean emptyFundsError = false;
        int currency;
        int inventoryAmount;

        switch (type) {
            case 0: //Solak tokens
                currency = ItemDefinition.TOKEN_ID;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().upgradeTokens < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().upgradeTokens <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().upgradeTokens >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().upgradeTokens -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 1: //Pvm Tickets
                currency = 5022;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().pvmTickets < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().pvmTickets <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().pvmTickets >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().pvmTickets -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 2: //AFK tickets
                currency = 5020;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().afkTickets < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().afkTickets <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().afkTickets >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().afkTickets -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 3: //Slayer tickets
                currency = 5023;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().slayerTickets < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().slayerTickets <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().slayerTickets >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().slayerTickets -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 4: //Vote scrolls
                currency = 23020;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().voteScrolls < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().voteScrolls <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().voteScrolls >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().voteScrolls -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 5: //Pet fragments
                currency = 19000;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().petFragments < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().petFragments <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().petFragments >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().petFragments -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 6: //Instance tokens
                currency = 4278;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().instanceTokens < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().instanceTokens <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().instanceTokens >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().instanceTokens -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 7: //Gold charm
                currency = GOLD_CHARM;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().goldCharms < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().goldCharms <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().goldCharms >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().goldCharms -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 8: //Green charm
                currency = GREEN_CHARM;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().greenCharms < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().greenCharms <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().greenCharms >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().greenCharms -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 9: //Crimson charm
                currency = CRIM_CHARM;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().crimsonCharms < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().crimsonCharms <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().crimsonCharms >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().crimsonCharms -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
            case 10: //Blue charm
                currency = BLUE_CHARM;
                inventoryAmount = player.getInventory().getAmount(currency);

                if (player.getCurrencyPouch().blueCharms < amount) {
                    insufficientFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().blueCharms <= 0) {
                    emptyFundsError = true;
                    break;
                }

                if (player.getCurrencyPouch().blueCharms >= amount) {
                    long longAmount = ((long)inventoryAmount + (long)amount);
                    if (longAmount > Integer.MAX_VALUE) {
                        long amountToSubtract = Integer.MAX_VALUE - inventoryAmount;
                        amount = (int) amountToSubtract;
                    }
                    if (amount <= 0) {
                        break;
                    }
                    player.getCurrencyPouch().blueCharms -= amount;
                    player.getInventory().add(new Item(currency, amount));
                }
                break;
        }
        if (insufficientFundsError) {
            DialogueManager.sendStatement(player, "You do not have this much to withdraw!");
            return;
        }
        if (emptyFundsError) {
            DialogueManager.sendStatement(player, "You do not have any of this currency!");
        }
    }

    public static void depositCurrency(Player player, int type, int amount) {
        if (amount <= 0) {
            DialogueManager.sendStatement(player, "You can not deposit an invalid amount!");
            return;
        }

        boolean insufficientFundsError = false;
        int currency;
        switch (type) {
            case 0: //Solak tokens
                currency = ItemDefinition.TOKEN_ID;
                break;
            case 1: //Pvm Tickets
                currency = 5022;
                break;
            case 2: //AFK tickets
                currency = 5020;
                break;
            case 3: //Slayer tickets
                currency = 5023;
                break;
            case 4: //Vote scrolls
                currency = 23020;
                break;
            case 5: //Pet fragments
                currency = 19000;
                break;
            case 6: //Instance tokens
                currency = 4278;
                break;
            case 7: //Gold charm
                currency = GOLD_CHARM;
                break;
            case 8: //Green charm
                currency = GREEN_CHARM;
                break;
            case 9: //Crimson charm
                currency = CRIM_CHARM;
                break;
            case 10: //Blue charm
                currency = BLUE_CHARM;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        if (attemptDeposit(player, amount, insufficientFundsError, currency)) {
            DialogueManager.sendStatement(player, "You do not have this much to withdraw!");
        }
    }

    private static boolean attemptDeposit(Player player, int amount, boolean insufficientFundsError, int currency) {
        int inventoryAmount = player.getInventory().getAmount(currency);

        if (inventoryAmount < amount) {
            return true;
        }

        if (currency == ItemDefinition.TOKEN_ID) {
            player.getCurrencyPouch().upgradeTokens += amount;
        } else if (currency == 5022) {
            player.getCurrencyPouch().pvmTickets += amount;
        } else if (currency == 5020) {
            player.getCurrencyPouch().afkTickets += amount;
        } else if (currency == 5023) {
            player.getCurrencyPouch().slayerTickets += amount;
        } else if (currency == 23020) {
            player.getCurrencyPouch().voteScrolls += amount;
        } else if (currency == 19000) {
            player.getCurrencyPouch().petFragments += amount;
        } else if (currency == 4278) {
            player.getCurrencyPouch().instanceTokens += amount;
        } else if (currency == GOLD_CHARM) {
            player.getCurrencyPouch().goldCharms += amount;
        } else if (currency == GREEN_CHARM) {
            player.getCurrencyPouch().greenCharms += amount;
        } else if (currency == CRIM_CHARM) {
            player.getCurrencyPouch().crimsonCharms += amount;
        } else if (currency == BLUE_CHARM) {
            player.getCurrencyPouch().blueCharms += amount;
        }
        player.getInventory().delete(new Item(currency, amount));
        return insufficientFundsError;
    }

    public static void handleItemOnItem(Player player, Item used) {
        if (used.getId() == ItemDefinition.TOKEN_ID) {
            depositCurrency(player, 0, used.getAmount());
        } else if (used.getId() == 5022) {
            depositCurrency(player, 1, used.getAmount());
        } else if (used.getId() == 5020) {
            depositCurrency(player, 2, used.getAmount());
        } else if (used.getId() == 5023) {
            depositCurrency(player, 3, used.getAmount());
        } else if (used.getId() == 23020) {
            depositCurrency(player, 4, used.getAmount());
        } else if (used.getId() == 19000) {
            depositCurrency(player, 5, used.getAmount());
        } else if (used.getId() == 4278) {
            depositCurrency(player, 6, used.getAmount());
        } else if (used.getId() == GOLD_CHARM) {
            depositCurrency(player, 7, used.getAmount());
        } else if (used.getId() == GREEN_CHARM) {
            depositCurrency(player, 8, used.getAmount());
        } else if (used.getId() == CRIM_CHARM) {
            depositCurrency(player, 9, used.getAmount());
        } else if (used.getId() == BLUE_CHARM) {
            depositCurrency(player, 10, used.getAmount());
        } else {
            DialogueManager.sendStatement(player, "You can not use this currency on the pouch!");
        }
    }
}
