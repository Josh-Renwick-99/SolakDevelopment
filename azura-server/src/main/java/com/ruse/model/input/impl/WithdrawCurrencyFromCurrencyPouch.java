package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.CurrencyPouch;
import com.ruse.world.entity.impl.player.Player;


public class WithdrawCurrencyFromCurrencyPouch extends EnterAmount {
    int type;

    public WithdrawCurrencyFromCurrencyPouch(int type) {
        this.type = type;
    }

    @Override
    public void handleAmount(Player player, int amount) {
       CurrencyPouch.withdrawCurrency(player, type, amount);
    }
}
