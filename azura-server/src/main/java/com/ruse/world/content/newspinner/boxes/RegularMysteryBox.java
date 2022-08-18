package com.ruse.world.content.newspinner.boxes;

import com.ruse.model.Item;
import com.ruse.util.Misc;

import java.util.ArrayList;

public class RegularMysteryBox implements MysteryBox {

    private ArrayList<Item> common_items = new ArrayList<Item>() {
        {
            add(new Item(4708, 1));
            add(new Item(4710, 1));
            add(new Item(4712, 1));
            add(new Item(4714, 1));
            add(new Item(4716, 1));
            add(new Item(4718, 1));
            add(new Item(4720, 1));
            add(new Item(4722, 1));
            add(new Item(4732, 1));
            add(new Item(4734, 1));
            add(new Item(4736, 1));
            add(new Item(4738, 1));
            add(new Item(4153, 1));
            add(new Item(6914, 1));
            add(new Item(6916, 1));
            add(new Item(6918, 1));
            add(new Item(6920, 1));
            add(new Item(6922, 1));
            add(new Item(6924, 1));
        }
    };

    private ArrayList<Item> uncommon_items = new ArrayList<Item>() {
        {
                    add(new Item(10943, 1));
                    add(new Item(2703, 2));
                    add(new Item(2572, 1));
                    add(new Item(12833, 1));
                    add(new Item(4151, 1));
                    add(new Item(6585, 1));
                    add(new Item(6737, 1));
                    add(new Item(2704, 1));
                     add(new Item(6733, 1));
                    add(new Item(6731, 1));
            add(new Item(20238 , 1));
            add(new Item(6889, 1));
        }
    };
    private ArrayList<Item> rare_items = new ArrayList<Item>() {
        {
            add(new Item(11802, 1));
            add(new Item(11808, 1));
            add(new Item(11832, 1));
            add(new Item(11834, 1));
            add(new Item(11907, 1));
            add(new Item(11791, 1));
            add(new Item(11283, 1));
            add(new Item(11785, 1));
            add(new Item(13271, 1));
            add(new Item(1419, 1));
            add(new Item(2572  , 1));
        }
    };

    private ArrayList<Item> super_rare_items = new ArrayList<Item>() {
        {
                   add(new Item(1037, 1));
                    add(new Item(1038, 1));
                    add(new Item(1040, 1));
                    add(new Item(1042, 1));
                    add(new Item(1044, 1));
                    add(new Item(1046, 1));
                    add(new Item(1048, 1));
                    add(new Item(1050, 1));
                    add(new Item(1053, 1));
                    add(new Item(1055, 1));
                    add(new Item(1057, 1));
        }
    };

    @Override
    public String getName() {
        return "Sample Mystery Box";
    }

    @Override
    public int getId() {
        return 6199;
    }

    @Override
    public ArrayList<Item> getCommon_items() {
        return common_items;
    }

    @Override
    public ArrayList<Item> getUncommon_items() {
        return uncommon_items;
    }

    @Override
    public ArrayList<Item> getRare_items() {
        return rare_items;
    }

    @Override
    public ArrayList<Item> getSuper_rare_items() {
        return super_rare_items;
    }
}
