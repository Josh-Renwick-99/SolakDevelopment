package com.ruse.model.Augment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemAugment {
    private Integer itemId;
    private AugmentType augmentType;
    private Augment firstAugment;
    private Augment secondAugment;
    private Augment thirdAugment;
    private boolean equipped;

    public double getDamageBonus(){
        double bonus = 0.0;
        if (firstAugment != null){
            bonus += firstAugment.getAugmentBonus(AugmentBonusType.DAMAGE);
        }
        if (secondAugment != null){
            bonus += secondAugment.getAugmentBonus(AugmentBonusType.DAMAGE);
        }
        if(thirdAugment != null) {
            thirdAugment.getAugmentBonus(AugmentBonusType.DAMAGE);
        }
        return bonus;
    }

    public double getHpBonus(){
        double bonus = 0.0;
        if (firstAugment != null){
            bonus += firstAugment.getAugmentBonus(AugmentBonusType.HP);
        }
        if (secondAugment != null){
            bonus += secondAugment.getAugmentBonus(AugmentBonusType.HP);
        }
        if(thirdAugment != null) {
            thirdAugment.getAugmentBonus(AugmentBonusType.HP);
        }
        return bonus;
    }

    public double getDrBonus(){
        double bonus = 0.0;
        if (firstAugment != null){
            bonus += firstAugment.getAugmentBonus(AugmentBonusType.DR);
        }
        if (secondAugment != null){
            bonus += secondAugment.getAugmentBonus(AugmentBonusType.DR);
        }
        if(thirdAugment != null) {
            thirdAugment.getAugmentBonus(AugmentBonusType.DR);
        }
        return bonus;
    }

    public double getDdrBonus(){
        double bonus = 0.0;
        if (firstAugment != null){
            bonus += firstAugment.getAugmentBonus(AugmentBonusType.DDR);
        }
        if (secondAugment != null){
            bonus += secondAugment.getAugmentBonus(AugmentBonusType.DDR);
        }
        if(thirdAugment != null) {
            thirdAugment.getAugmentBonus(AugmentBonusType.DDR);
        }
        return bonus;
    }

    public enum AugmentType {
        WEAPON, ARMOUR;
    }
}
