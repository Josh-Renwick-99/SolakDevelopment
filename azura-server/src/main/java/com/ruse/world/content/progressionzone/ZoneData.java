package com.ruse.world.content.progressionzone;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import lombok.Getter;

public class ZoneData {

    public enum Monsters {

        SONIC(9001, 10, new Item[]{new Item(19944), new Item(19945), new Item(19946)}),
        PATRICK(9002, 20, new Item[]{new Item(703), new Item(704), new Item(705)}),
        LUIGI(9003, 30, new Item[]{new Item(14924), new Item(14919), new Item(14915)}),
        SQUIRTLE(9004, 50, new Item[]{new Item(ItemDefinition.TOKEN_ID, 1000),  new Item(2734, 1),  new Item(19114, 5)}),
        MEWTWO(9005, 75, new Item[]{new Item(23044),  new Item(ItemDefinition.TOKEN_ID, 5000),  new Item(2734, 5)}),
        //BOWSER(9006, 150, new Item[]{new Item(ItemDefinition.COIN_ID, 10000000)})
        ;

        @Getter
        private int npcId;
        @Getter
        private int amountToKill;
        @Getter
        private Item[] rewards;

        Monsters(int npcId, int amountToKill, Item[] rewards) {
            this.npcId = npcId;
            this.amountToKill = amountToKill;
            this.rewards = rewards;
        }

        public static Monsters forID(int npcId) {
            for (Monsters monster : Monsters.values()) {
                if (monster.getNpcId() == npcId) {
                    return monster;
                }
            }
            return null;
        }

        public String getName() {
            return Misc.ucFirst(name().toLowerCase());
        }

        public Position getCoords() {
            return new Position(3037, 10288, ordinal() * 4);
        }
    }

}
