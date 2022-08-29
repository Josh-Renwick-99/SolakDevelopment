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

    public enum AugmentType {
        WEAPON, ARMOUR;
    }
}
