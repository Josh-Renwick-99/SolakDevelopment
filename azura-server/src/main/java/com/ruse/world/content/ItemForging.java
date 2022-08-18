package com.ruse.world.content;

import com.ruse.model.Graphic;
import com.ruse.model.Item;
import com.ruse.model.Skill;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

/**
 * Handles item forging, such as Spirit shields making etc.
 *
 * @author Gabriel Hannason and Samy
 */
public class ItemForging {

    public static void forgeItem(final Player p, final int item1, final int item2) {
        if (item1 == item2)
            return;
        ItemForgeData data = ItemForgeData.getDataForItems(item1, item2);
        if (data == null || !p.getInventory().contains(item1) || !p.getInventory().contains(item2))
            return;
        if (!p.getClickDelay().elapsed(1000))
            return;
        if (p.getInterfaceId() > 0) {
            p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
            return;
        }
        Skill skill = Skill.forId(data.skillRequirement[0]);
        int skillReq = data.skillRequirement[1];
        if (p.getSkillManager().getCurrentLevel(skill) >= skillReq) {
            for (Item reqItem : data.requiredItems) {
                if (!p.getInventory().contains(reqItem.getId())
                        || p.getInventory().getAmount(reqItem.getId()) < reqItem.getAmount()) {
                    p.getPacketSender().sendMessage("You need " + Misc.anOrA(reqItem.getDefinition().getName()) + " "
                            + reqItem.getDefinition().getName() + " to forge a new item.");
                    return;
                }
            }
            p.performGraphic(new Graphic(2010));
            for (Item reqItem : data.requiredItems) {
                if (reqItem.getId() == 1755 || reqItem.getId() == 1595 || reqItem.getId() == 233
                        || reqItem.getId() == 7329 || reqItem.getId() == 7330 || reqItem.getId() == 7331
                        || reqItem.getId() == 10326 || reqItem.getId() == 10327)
                    continue;
                p.getInventory().delete(reqItem);
            }
            p.getInventory().add(data.product, true);
            final String itemName = Misc.formatText(ItemDefinition.forId(data.product.getId()).getName().toLowerCase());
            p.getPacketSender().sendMessage("<shad=1>@or2@You've made@mag@ " + Misc.anOrA(itemName) + " " + itemName + ".");

            if (data.isAnnounce()) {
                String msg = "<img=5><shad>@blu@Forging:@red@ " + p.getUsername() + " has created "  + Misc.anOrA(itemName) + " " + itemName + "!";
                World.sendMessage(msg);
            }

            p.getClickDelay().reset();
            if (data.skillRequirement[2]  != -1){
                p.getSkillManager().addExperience(skill, data.skillRequirement[2]);// data.skillRequirement[2]);
            }
            return;
        } else {
            p.getPacketSender().sendMessage("You need " + Misc.anOrA(skill.getFormatName()) + " "
                    + skill.getFormatName() + " level of at least " + skillReq + " to forge this item.");
            return;
        }
    }

    /**
     * * The enum holding all our data
     */
    @Getter
    private enum ItemForgeData {
        MASTER_MAX_CAPE(new Item[]{new Item(23158), new Item(23159), new Item(23160)}, new Item(23176), new int[]{1, -1, 0}, true),

        MAGIC_MAX_CAPE(new Item[]{new Item(23148), new Item(14019)}, new Item(23158), new int[]{1, -1, 0}, true),
        MELEE_MAX_CAPE(new Item[]{new Item(23147), new Item(14019)}, new Item(23159), new int[]{1, -1, 0}, true),
        RANGED_MAX_CAPE(new Item[]{new Item(23149), new Item(14019)}, new Item(23160), new int[]{1, -1, 0}, true),

     /*   BUNNY_HAT_GREEN(new Item[]{new Item(23013), new Item(23009)}, new Item(23014), new int[]{0, 0, 0}),
        BUNNY_HAT_GOLD(new Item[]{new Item(23013), new Item(23010)}, new Item(23015), new int[]{0, 0, 0}),
        BUNNY_HAT_BLUE(new Item[]{new Item(23013), new Item(23011)}, new Item(23016), new int[]{0, 0, 0}),
        BUNNY_HAT_RED(new Item[]{new Item(23013), new Item(23012)}, new Item(23017), new int[]{0, 0, 0}),

        EASTER_PENDANT(new Item[]{new Item(13558), new Item(18888)}, new Item(23000), new int[]{0, 0, 0}),
        ABYSSAL_TENTACLE(new Item[]{new Item(4151), new Item(22007)}, new Item(22008), new int[]{21, 0, 0}),

        Collector_ammy(new Item[]{new Item(19888), new Item(9084)}, new Item(18888), new int[]{21, 0, 0}),
        Collector_ring(new Item[]{new Item(18823), new Item(9084)}, new Item(18818), new int[]{21, 0, 0}),

        ENRAGEDCAPE(new Item[]{new Item(20591), new Item(9083)}, new Item(20400), new int[]{21, 0, 0}),
        TBOW(new Item[]{new Item(5012), new Item(10949, 3)}, new Item(5011), new int[]{21, 0, 0}),
        SCYTHE(new Item[]{new Item(12535), new Item(10949, 3)}, new Item(12537), new int[]{21, 0, 0}),
        SANG(new Item[]{new Item(17011), new Item(10949, 3)}, new Item(17013), new int[]{21, 0, 0}),

        MEGA1(new Item[]{new Item(18883), new Item(9080, 1), new Item(9081, 1), new Item(9082, 1)}, new Item(18885), new int[]{21, 0, 0}),
        MEGA2(new Item[]{new Item(18881), new Item(9080, 1), new Item(9081, 1), new Item(9082, 1)}, new Item(18887), new int[]{21, 0, 0}),
        MEGA3(new Item[]{new Item(19810), new Item(9080, 1), new Item(9081, 1), new Item(9082, 1)}, new Item(18889), new int[]{21, 0, 0}),
*/
        ;

        private Item[] requiredItems;
        private Item product;
        private int[] skillRequirement;
        private boolean announce = false;
        ItemForgeData(Item[] requiredItems, Item product, int[] skillRequirement) {
            this.requiredItems = requiredItems;
            this.product = product;
            this.skillRequirement = skillRequirement;
        }
        ItemForgeData(Item[] requiredItems, Item product, int[] skillRequirement, boolean announce) {
            this.requiredItems = requiredItems;
            this.product = product;
            this.skillRequirement = skillRequirement;
            this.announce = announce;
        }

        public static ItemForgeData getDataForItems(int item1, int item2) {
            for (ItemForgeData shieldData : ItemForgeData.values()) {
                int found = 0;
                for (Item it : shieldData.requiredItems) {
                    if (it.getId() == item1 || it.getId() == item2)
                        found++;
                }
                if (found >= 2)
                    return shieldData;
            }
            return null;
        }
    }
}
