package com.ruse.world.entity.impl.npc;

import com.ruse.model.Direction;
import com.ruse.model.Position;

public class NPCSpawn {

        public int npcId;
        public Direction face;
        public Position position;
        public NPCMovementCoordinator.Coordinator coordinator;

    public NPCSpawn(int id, Direction face, Position pos) {
            this.npcId = id;
            this.face = face;
            this.position = pos;
            this.coordinator = new NPCMovementCoordinator.Coordinator(false, 1);
        }
         public NPCSpawn(int id, Direction face, Position pos, NPCMovementCoordinator.Coordinator cord) {
            this.npcId = id;
            this.face = face;
            this.position = pos;
            this.coordinator = cord;
        }
}
