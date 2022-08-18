package com.ruse.world.content.collectionlog;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class SearchForCollectionNpc extends Input {
    public void handleSyntax(Player player, String msg) {
    	player.getCollectionLog().search(msg);
    }
}
