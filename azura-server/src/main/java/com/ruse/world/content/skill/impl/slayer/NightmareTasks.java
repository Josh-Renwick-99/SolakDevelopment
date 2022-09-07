package com.ruse.world.content.skill.impl.slayer;

import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public enum NightmareTasks {
    NO_TASK(null, -1, null, -1, null),
    VALIX(NightmareSlayerMaster.NIGHTMARE_SLAYER, 1703, "Easy Slayer Dungeon", 15000, new Position(1813, 4908)),
    ;



    NightmareTasks(NightmareSlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition) {
        this.taskMaster = taskMaster;
        this.npcId = npcId;
        this.npcLocation = npcLocation;
        this.XP = XP;
        this.taskPosition = taskPosition;
    }

    public static Map<NightmareSlayerMaster, ArrayList<NightmareTasks>> tasks = new HashMap<>();
    static {
        for (NightmareSlayerMaster master : NightmareSlayerMaster.values()) {
            ArrayList<NightmareTasks> slayerMasterSet = new ArrayList<>();
            for(NightmareTasks t : NightmareTasks.values()) {
                if(t.taskMaster == master) {
                    slayerMasterSet.add(t);
                }
            }
            tasks.put(master, slayerMasterSet);
        }
    }

    private NightmareSlayerMaster taskMaster;
    private int npcId;
    private String npcLocation;
    private int XP;
    private Position taskPosition;

    public static NightmareTaskData getNewTaskData(NightmareSlayerMaster master) {
        int slayerTaskAmount = 20;
        ArrayList<NightmareTasks> possibleTasks = tasks.get(master);
        NightmareTasks task = possibleTasks.get(Misc.getRandom(possibleTasks.size() - 1));

        /*
         * Getting a task
         */
        if (master == NightmareSlayerMaster.NIGHTMARE_SLAYER) {
            slayerTaskAmount = 25 + Misc.getRandom(35);
        }
        return new NightmareTaskData(task, slayerTaskAmount);
    }

    public int getNpcId() {
        return this.npcId;
    }

    public String getName() {
        NpcDefinition def = NpcDefinition.forId(npcId);
        return def == null ? Misc.formatText(this.toString().toLowerCase().replaceAll("_", " ")) : def.getName();
    }

}
