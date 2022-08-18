package com.ruse.world.content.newspinner.boxes;

import com.ruse.model.Item;

import java.util.ArrayList;

public interface MysteryBox {

    public String getName();
    public int getId();
    public ArrayList<Item> getCommon_items();
    public ArrayList<Item> getUncommon_items();
    public ArrayList<Item> getRare_items();
    public ArrayList<Item> getSuper_rare_items();
}
