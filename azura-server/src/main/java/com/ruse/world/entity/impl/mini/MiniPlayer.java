package com.ruse.world.entity.impl.mini;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Position;
import com.ruse.model.Skill;
import com.ruse.net.PlayerSession;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public class MiniPlayer extends Player {
    public MiniPlayer(String username, Position position, PlayerSession session) {
        super(session);

        session.setPlayer(this);
        setUsername(username);
        setLongUsername(NameUtils.stringToLong(username));
        setHostAddress("127.0.0.1");
        setSerialNumber("1");
        setPassword("");
        setPosition(new Position(position.getX() + 1, position.getY(), position.getZ()));
        setMiniPlayer(true);
        setMiniPBanks();
        TaskManager.submit(insinuateGodMode(this));
    }
    @Override
    public boolean isActive() {
        return false;
    }
    @Getter
    private int miniPlayerDropRate = 0;

    public void incrementMiniPlayerDropRate(int amount) {
        miniPlayerDropRate += amount;
    }

    public Task insinuateGodMode(MiniPlayer miniPlayer) {
        return new Task(true) {
            int tick = 0;
            @Override
            protected void execute() {
                if(!World.getPlayers().contains(miniPlayer) && tick > 8)
                    stop();
               getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 15000);
               tick++;
            }
        };
    }
}
