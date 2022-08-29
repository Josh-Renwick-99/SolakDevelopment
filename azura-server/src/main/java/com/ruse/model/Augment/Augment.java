package com.ruse.model.Augment;

import java.util.HashMap;
import java.util.Map;

import static com.ruse.model.Augment.AugmentPower.*;

public enum Augment {

    DAMAGE_BOOST_1("Damage boost 1", DAMAGE1, 4, 1, "Toolkit"),
    HP_BOOST_1("Hp boost 1", HP, 3, 3, "Nulodian's notes");

    private String augmentName;
    private AugmentPower augmentPower;
    private String augmentItemName;
    private Integer augmentId;
    private Integer itemId;


    Augment(String name, AugmentPower augmentPower, Integer augmentId, Integer itemId, String augmentItemName){
        this.augmentPower = augmentPower;
        this.augmentName = name;
        this.augmentId = augmentId;
        this.augmentItemName = augmentItemName;
        this.itemId = itemId;
    }

    public String getAugmentItemName(){
        return this.augmentItemName;
    }

    public int getItemId(){
        return this.itemId;
    }

    private static final Map<Integer, Augment> byId = new HashMap<Integer, Augment>();
    static {
        for (Augment e : Augment.values()) {
            if (byId.put(e.getItemId(), e) != null) {
                throw new IllegalArgumentException("duplicate id: " + e.getId());
            }
        }
    }

    public static Augment getById(Integer id) {
        return byId.get(id);
    }

    public Integer getId(){ return this.augmentId; };
}


