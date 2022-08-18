package com.ruse.world.content.groupironman;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ruse.engine.task.impl.FamiliarSpawnTask;
import com.ruse.model.*;
import com.ruse.world.content.DropLog;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.LoyaltyProgramme;
import com.ruse.world.content.combat.magic.CombatSpells;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.grandexchange.GrandExchangeSlot;
import com.ruse.world.content.skill.SkillManager;
import com.ruse.world.content.skill.impl.slayer.SlayerMaster;
import com.ruse.world.entity.impl.player.Player;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IronmanPlayerLoading {


    @SneakyThrows
    public static Player getResult(String name) {

        // Create the path and file objects.
        Path path = Paths.get("./data/saves/characters/", name + ".json");
        File file = path.toFile();

        // If the file doesn't exist, we're logging in for the first
        // time and can skip all of this.
        if (!file.exists()) {
            return null;
        }
        Player player = new Player(null);

        // Now read the properties from the json parser.
        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            if (reader.has("total-play-time-ms")) {
                player.setTotalPlayTime(reader.get("total-play-time-ms").getAsLong());
            }

            if (reader.has("username")) {
                player.setUsername(reader.get("username").getAsString());
            }

            if (reader.has("password")) {
                String password = reader.get("password").getAsString();
                player.setPassword(password);
            }

            if (reader.has("skills")) {
                player.getSkillManager().setSkills(builder.fromJson(reader.get("skills"), SkillManager.Skills.class));
            }
            
            if (reader.has("group-ironman-id")) {
                IronmanGroup group = GroupManager.getGroup((reader.get("group-ironman-id").getAsInt()));
                if (group != null)
                    player.setIronmanGroup(group);
            }

            if (reader.has("group-ironman-locked"))
                player.setGroupIronmanLocked((reader.get("group-ironman-locked").getAsBoolean()));


        } catch (Exception e) {
            e.printStackTrace();
            return player;
        }
        return player;
    }

}
