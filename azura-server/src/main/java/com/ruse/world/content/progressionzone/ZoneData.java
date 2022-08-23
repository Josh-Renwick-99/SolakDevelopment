package com.ruse.world.content.progressionzone;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import lombok.Getter;

public class ZoneData {

    public enum Monsters {

        SONIC(9001, 10, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
        PATRICK(9002, 25, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
        LUIGI(9003, 50, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
        SQUIRTLE(9004, 100, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
        MEWTWO(9005, 175, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
        BOWSER(9006, 225, new Item[]{new Item(995, 69), new Item(995, 69), new Item(995, 69)}),
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
