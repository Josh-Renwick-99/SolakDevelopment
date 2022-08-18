package com.ruse.world.content;

import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.content.skill.impl.summoning.BossPets.BossPet;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public class PetShop {

    public static final int ITEM_CHILD_ID = 33901;
    public static final int ITEM_CHILD_ID_CLICK = -31635;
    public static final int INTERFACE_ID = 119000;

    private Player player;

    public PetShop(Player player) {
        this.player = player;
    }

    public static Object[] getPrice(int item) {
        switch (item) {
            case 23041://damage
            case 23042:
            case 23043:
                return new Object[]{350000, "Solak tokens"};
            case 23114://kil
                return new Object[]{500000, "Solak tokens"};
            case 23115:
                return new Object[]{250000, "Solak tokens"};
            case 23116:
                return new Object[]{500000, "Solak tokens"};
            case 23117:
                return new Object[]{700000, "Solak tokens"};
            case 23164:
                return new Object[]{1000000, "Solak tokens"};
            case 23108:
            case 23109:
                return new Object[]{400000, "Solak tokens"};
            case 23110:
                return new Object[]{500000, "Solak tokens"};
            case 23112:
                return new Object[]{1500000, "Solak tokens"};
            case 23111:
                return new Object[]{5000000, "Solak tokens"};
        }
        return new Object[]{100000000, "Solak tokens"};
    }

    public boolean handleButton(int buttonId) {

        switch (buttonId) {
            case 119005:
                openInterface(PetShopType.DAMAGE);
                return true;
            case 119006:
                openInterface(PetShopType.RAID);
                return true;
            case 119007:
                openInterface(PetShopType.DROP_RATE);
                return true;
            case 119008:
                openInterface(PetShopType.MISC);
                return true;
        }

        return false;
    }

    public void openInterface(PetShopType type) {
        player.getPacketSender().sendConfig(5334, type.ordinal());
        Shop.ShopManager.getShops().get(type.getShopId()).open(player);
    }

    public boolean hasRequirements(int id) {
        BossPet[] pets = PetRestrictions.getRequired(id);
        if (pets != null) {
            for (BossPet pet : pets) {
                if (!player.getObtainedPets().contains(pet)) {
                    getRequirementMessage(pets);
                    return false;
                }
            }
        }
        return true;
    }

    public void getRequirementMessage(BossPet[] pets) {
        String s = "You need to have summoned: @or2@";
        for (BossPet pet : pets) {
            s += (player.getObtainedPets().contains(pet) ? "<str>": "") + ItemDefinition.forId(pet.getItemId()).getName() + "</str>, ";
        }
        s = s.substring(0, s.length()-2);

        player.sendMessage(s);
    }

    @Getter
    public enum PetShopType {
        DAMAGE(203),
        RAID(204),
        DROP_RATE(205),
        MISC(206);
        private int shopId;

        PetShopType(int shopId) {
            this.shopId = shopId;
        }

        public static boolean isPetStore(int id) {
            for (PetShopType petShopType : values()) {
                if (id == petShopType.getShopId()) {
                    return true;
                }
            }
            return false;
        }
    }


    @Getter
    public enum PetRestrictions {
        KILJAEDEN_PET(BossPet.KILJAEDEN_PET, BossPet.MELEE_PET, BossPet.RANGED_PET, BossPet.MAGE_PET),
        GOLEM_PET(BossPet.GOLEM_PET, BossPet.DEMON_PET),
        DRAGON_PET(BossPet.DRAGON_PET, BossPet.GOLEM_PET),
        ORIX_PET(BossPet.ORIX_PET, BossPet.SKREEG_PET),
        CRYSTAL_ORC_PET(BossPet.CRYSTAL_ORC_PET, BossPet.ORIX_PET),
        RAMMUS_PET(BossPet.RAMMUS_PET, BossPet.CRYSTAL_ORC_PET),
        ODIN_PET(BossPet.ODIN_PET, BossPet.KILJAEDEN_PET, BossPet.RAMMUS_PET, BossPet.RED_FENRIR_PET, BossPet.HEIMDALL_PET),
        ;

        private BossPet pet;
        private BossPet[] required;

        PetRestrictions(BossPet pet, BossPet... required) {
            this.pet = pet;
            this.required = required;
        }

        public static BossPet[] getRequired(int id) {
            BossPet pet = BossPet.forId(id);
            for (PetRestrictions data : values()) {
                if (pet == data.getPet()) {
                    return data.getRequired();
                }
            }
            return null;
        }
    }

}
