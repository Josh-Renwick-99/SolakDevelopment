package com.ruse.world.content;

import com.ruse.model.container.impl.Shop;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public class DonatorShop {

    public static final int ITEM_CHILD_ID = 33900;
    public static final int ITEM_CHILD_ID_CLICK = -31636;
    public static final int INTERFACE_ID = 118000;

    private Player player;

    public DonatorShop(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {

        switch (buttonId) {
            case 118005:
                openInterface(DonatorShopType.WEAPON);
                return true;
            case 118006:
                openInterface(DonatorShopType.ARMOUR);
                return true;
            case 118007:
                openInterface(DonatorShopType.ACCESSORY);
                return true;
            case 118008:
                openInterface(DonatorShopType.MISC);
                return true;
        }

        return false;
    }

    public void openInterface(DonatorShopType type) {
        player.getPacketSender().sendConfig(5333, type.ordinal());
        Shop.ShopManager.getShops().get(type.getShopId()).open(player);
    }
    
    public static Object[] getPrice(int item){
        switch (item) {
            case 23146://dung weps
            case 23145:
            case 23144:
                return new Object[]{40, "Donator points"};
            case 23033://vod weps
            case 23026:
            case 23039:
                return new Object[]{50, "Donator points"};
            case 23134://kol pieces
            case 23135:
            case 23136:
            case 23137:
            case 23138:
                return new Object[]{50, "Donator points"};
            case 23021://vod pieces
            case 23022:
            case 23023:
            case 23024:
            case 23025:
            case 23027:
            case 23028:
            case 23029:
            case 23030:
            case 23031:
            case 23032:
            case 23034:
            case 23035:
            case 23036:
            case 23037:
            case 23038:
                return new Object[]{80, "Donator points"};
            case 23073://slay helm t4
                return new Object[]{60, "Donator points"};
            case 23102://vow gloves t4
                return new Object[]{50, "Donator points"};
            case 23096://vow gloves
            case 23097:
            case 23098:
                return new Object[]{125, "Donator points"};
            case 23092://hov rings
            case 23093:
            case 23094:
                return new Object[]{125, "Donator points"};
            case 23090://coll t2
                return new Object[]{50, "Donator points"};
            case 23147://attachements
            case 23148:
            case 23149:
                return new Object[]{200, "Donator points"};
            case 23124://pots
            case 23125:
            case 23126:
            case 23121:
            case 23122:
            case 23123:
            case 23118:
            case 23119:
            case 23120:
                return new Object[]{50, "Donator points"};

            case 23171://box
                return new Object[]{5, "Donator points"};
            case 23172://box
                return new Object[]{35, "Donator points"};
            case 23173://box
                return new Object[]{35, "Donator points"};

            case 23114://pet
                return new Object[]{200, "Donator points"};
            case 23117://pet
                return new Object[]{150, "Donator points"};
            case 23108://pet
                return new Object[]{75, "Donator points"};
            case 23112://pet
                return new Object[]{100, "Donator points"};
            case 23111://pet
                return new Object[]{500, "Donator points"};
            case 299://gamble
            case 15084:
                return new Object[]{250, "Donator points"};
            case 16441:
                return new Object[]{10, "Donator points"};
            default:
                return new Object[]{1000, "Donator points"};
        }
    }

    @Getter
    public enum DonatorShopType {
        WEAPON(80),
        ARMOUR(200),
        ACCESSORY(201),
        MISC(202);
        private int shopId;

        DonatorShopType(int shopId) {
            this.shopId = shopId;
        }
        
        public static boolean isDonatorStore(int id){
            for (DonatorShopType donatorShopType : values()){
                if ( id == donatorShopType.getShopId()){
                    return true;
                }
            }
            return false;
        }
    }

}
