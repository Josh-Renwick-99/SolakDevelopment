package com.ruse.world.allornothing;

import com.ruse.model.Item;

/**
 * Created by Grant_ | Rune-server.ee | on 2/22/2018.
 */
public class AONItem {

	private Item item;
	private AONType type;

	public AONItem(Item item, AONType type) {
		this.item = item;
		this.type = type;
	}

	public Item getItem() {
		return item;
	}

	public AONType getType() {
		return type;
	}
}
