package com.ruse.world.content;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruse.GameServer;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.model.definitions.NpcDefinition;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;

public class DropConversion {

    public static ArrayList<Integer> placed = new ArrayList<>();

    public static ArrayList<NPCDrops> drops = new ArrayList<>();

    public static void run() {

        placed.clear();
        drops.clear();

        for (int data : NPCDrops.getDrops().keySet()) {

            NPCDrops drop = NPCDrops.getDrops().get(data);

            if (drop == null)
                return;
            if (drop.getNpcIds() == null)
                return;
           /* for (NPCDrops.NpcDropItem item : drop.getDropList()){
                item.setChance(item.getChance().getRandom());
            }*/

            for (int i = 0; i < drop.getNpcIds().length; i++) {
                NpcDefinition npcDefinition = NpcDefinition.forId(drop.getNpcIds()[i]);
                if (!placed.contains(drop.getNpcIds()[i])) {
                    System.out.println("Id: " + drop.getNpcIds()[i] + ", Name: " +
                            (npcDefinition != null ? npcDefinition.getName() : "NO"));

                    for (int z = 0; z < drop.getNpcIds().length; z++) {
                        placed.add(drop.getNpcIds()[z]);
                    }
                    drops.add(drop);
                }
            }
        }
        save();
    }

    public static void save() {
        Path path = Paths.get("./azura-server/data/def/drops.json");
        File file = path.toFile();
        file.getParentFile().setWritable(true);

        if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (SecurityException e) {
            }
        }

        try (FileWriter writer = new FileWriter(file)) {

            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            object.add("drops", builder.toJsonTree(drops));

            writer.write(builder.toJson(object));

        } catch (Exception e) {
            // An error happened while saving.
            GameServer.getLogger().log(Level.WARNING, "An error has occured while saving a character file!", e);
        }
    }


}
