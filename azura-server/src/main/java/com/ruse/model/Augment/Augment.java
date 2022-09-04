package com.ruse.model.Augment;

import java.util.HashMap;
import java.util.Map;

import static com.ruse.model.Augment.AugmentPower.*;

public enum Augment {

    DAMAGE_BOOST_1("Damage boost 1", DAMAGE1, 4, 1, "Toolkit", AugmentBonusType.DAMAGE, 5000.0),
    HP_BOOST_1("Hp boost 1", HP, 3, 3, "Nulodian's notes", AugmentBonusType.HP, 10.0);

    private String augmentName;
    private AugmentPower augmentPower;
    private String augmentItemName;
    private Integer augmentId;
    private Integer itemId;
    private AugmentBonusType bonusType;
    private Double bonus;

    Augment(String name, AugmentPower augmentPower, Integer augmentId, Integer itemId, String augmentItemName, AugmentBonusType augmentBonusType, Double bonus){
        this.augmentPower = augmentPower;
        this.augmentName = name;
        this.augmentId = augmentId;
        this.augmentItemName = augmentItemName;
        this.itemId = itemId;
        this.bonusType = augmentBonusType;
        this.bonus = bonus;
    }

    public double getAugmentBonus(AugmentBonusType augmentBonusType){
        if (augmentBonusType.equals(AugmentBonusType.DAMAGE)){
            return this.bonus;
        } else if (augmentBonusType.equals(AugmentBonusType.HP)){
            return this.bonus;
        } else if (augmentBonusType.equals(AugmentBonusType.DR)){
            return this.bonus;
        } else if (augmentBonusType.equals(AugmentBonusType.DDR)){
            return this.bonus;
        }
        return 0.0;
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


