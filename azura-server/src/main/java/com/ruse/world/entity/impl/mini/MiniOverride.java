package com.ruse.world.entity.impl.mini;

import com.ruse.world.content.AOEWeaponData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MiniOverride {


    public static List<Integer> overrides = new ArrayList<Integer>();

    public static void loadOverrides() {
        Path filePath = Paths.get("data", "miniOverrides.txt");

        try (Stream<String> lines = Files.lines(filePath)) {
            lines.forEach(line -> {
                int overrideID = Integer.parseInt(line);
                overrides.add(overrideID);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkOverride(int id) {
        if (overrides.contains(id)) {
            return true;
        }
        return false;
    }

}
