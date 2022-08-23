package com.ruse.world.content.casketopening.impl;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.content.casketopening.Box;

public class LaunchBox {
    //TODO replace the items in this box with other stuff
    public static Box[] loot = { //
            new Box(ItemDefinition.COIN_ID, 1000000, 100),
            new Box(23033, 1, 33, true),
            new Box(23026, 1, 33, true),
            new Box(23039, 1, 33, true),
            new Box(23146, 1, 33, true),
            new Box(23145, 1, 33, true),
    };
}
