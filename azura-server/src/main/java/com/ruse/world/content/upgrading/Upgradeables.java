package com.ruse.world.content.upgrading;

import com.ruse.model.Item;
import lombok.Getter;

import java.util.ArrayList;

import static com.ruse.world.content.upgrading.Upgradeables.UpgradeType.*;

@Getter
public enum Upgradeables {

    HAT(WEAPON, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    WHIP(ARMOUR, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    RING(ACCESSORY, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    IDD(MISC, new Item(1042, 1), new Item(1050, 1), 250000000, 100),

    ;

    private UpgradeType type;
    private Item required, reward;
    private int cost, successRate;
    private boolean rare;

    Upgradeables(UpgradeType type, Item required, Item reward, int cost, int successRate, boolean rare) {
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.rare = rare;
    }

    Upgradeables(UpgradeType type, Item required, Item reward, int cost, int successRate) {
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.rare = false;
    }
    
    public static ArrayList<Upgradeables> getForType(UpgradeType type){
        ArrayList<Upgradeables> upgradeablesArrayList = new ArrayList<>();
        for (Upgradeables upgradeables : values()){
            if (upgradeables.getType() == type){
                upgradeablesArrayList.add(upgradeables);
            }
        }
        return upgradeablesArrayList;
    }
    

    public enum UpgradeType{

        WEAPON, ARMOUR, ACCESSORY, MISC;

    }


}
