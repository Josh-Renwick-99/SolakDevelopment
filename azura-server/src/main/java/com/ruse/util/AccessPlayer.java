package com.ruse.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ruse.world.World;
import com.ruse.world.entity.impl.mini.MiniPlayer;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerLoading;
import com.ruse.world.entity.impl.player.PlayerSaving;

public abstract class AccessPlayer {
    public abstract void modifyPlayer(Player player);
    public abstract int playerLoadMethod(Player player);
    public abstract String playerName();
    public boolean complete() {
        String name = Misc.formatText(playerName().toLowerCase());
        Player owner = World.getPlayerByName(name);
        Path path = Paths.get("./data/saves/characters/", playerName() + ".json");
        File file = path.toFile();
        System.out.println("File exists: " + file.exists() + " for " + file.toPath().toString());
        if(owner == null && file.exists()) {
            Player player = new Player(null);
            player.setUsername(name);
            player.setLongUsername(NameUtils.stringToLong(name));
            if (name.contains("s pet")) {
            	return true;
            }
            if(playerLoadMethod(player) == -1) {
                PlayerLoading.getResult(player, true);
            }
            if (player instanceof MiniPlayer) {
            	return true;
            }
            modifyPlayer(player);
            PlayerSaving.save(player);
           // Driver.save(player);
            return true;
        } else  if(owner != null){
            modifyPlayer(owner);
            PlayerSaving.save(owner);
          //  Driver.save(owner);
            return true;
        } else {
            return false;
        }
    }
}
