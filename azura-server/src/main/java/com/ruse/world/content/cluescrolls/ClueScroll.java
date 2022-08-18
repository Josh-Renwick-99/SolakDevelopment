package com.ruse.world.content.cluescrolls;

import com.ruse.model.container.impl.Bank;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.entity.impl.player.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ClueScroll {
    EASY(2677),
    MEDIUM(2831),
    HARD(2773);

    int id;

    ClueScroll(int id) {
        this.id = id;
    }

    public int getClueId() {
        return id;
    }
    public static final List<ClueScroll> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    public static boolean hasClue(Player player) {
        boolean cont = false;

        if(player.getCurrentClue().getCurrentTask() != SlayerTasks.NO_TASK) {
            cont = true;
        }

        for (int q = 0; q < player.getBanks().length; q++) {
            if (player.getBank(q).containsAny(2677, 2831, 2773)) {
                cont = true;
            }
        }
        if (player.getInventory().containsAny(2677, 2831, 2773)) {
            cont = true;
        }

        return cont;
    }

    public static boolean readClue(Player player, ClueScroll type) {

        if (player.getCurrentClue().getCurrentTask() == SlayerTasks.NO_TASK) {
            player.getCurrentClue().assignTask(type);

            int id = player.getCurrentClue().getCurrentTask().getNpcId();
            NpcDefinition def = NpcDefinition.forId(id);
            player.sendMessage("Assigning task " + def.getName() + " x " + player.getCurrentClue().getAmountToSlay());
            return false;
        } else {

            if(player.getCurrentClue().getAmountToSlay() <= 0) {
                player.sendMessage("You have completed the clue task!");
                return true;// delete the scroll @ give reward, we're done
            } else {
                int id = player.getCurrentClue().getCurrentTask().getNpcId();
                NpcDefinition def = NpcDefinition.forId(id);
                player.sendMessage("You're assigned to kill "
                        + Misc.formatText(def.getName())
                        + "s, only " + player.getCurrentClue().getAmountToSlay() + " more to go.");

            }

        }
        return false;
    }

    public static void getReward() {

    }
}
