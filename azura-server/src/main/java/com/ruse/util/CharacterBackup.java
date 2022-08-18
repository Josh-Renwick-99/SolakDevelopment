package com.ruse.util;

import com.ruse.GameSettings;
import com.ruse.world.World;
import com.ruse.world.content.groupironman.GroupManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CharacterBackup {

    private static final int TIME = 900_000; // 15 minutes
    public static Stopwatch timer = new Stopwatch().reset();

    public static void sequence() {
        if (timer.elapsed(TIME)) {
            GroupManager.saveGroups();
            World.savePlayers();
            backup("./data/saves/characters/", "./data/saves/backups/characters/Backup " + getDate() + ".zip", "Characters");
            backup("./data/saves/shops/", "./data/saves/backups/shops/Backup " + getDate() + ".zip", "Shops");
            timer.reset();
        }
    }

    public static void backup(String from, String to, String name) {
        if (timer.elapsed(TIME)) {
            try {
                File f1 = new File(from);
                File f2 = new File(to);
                if (!f2.exists()) {
                    try {
                        System.out.println("[BACKUP] " + name + " successfully backed up.");
                        zipDirectory(f1, f2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("[BACKUP] " + name + " already backed up, backup canceled..");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void zipDirectory(File f, File zf) throws IOException {
        ZipOutputStream z = new ZipOutputStream(new FileOutputStream(zf));
        zip(f, f, z);
        z.close();
    }

    private static void zip(File directory, File base, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[8192];
        int read;
        for (File file : files) {
            if (file.isDirectory()) {
                zip(file, base, zos);
            } else {
                FileInputStream in = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(file.getPath().substring(base.getPath().length() + 1));
                zos.putNextEntry(entry);
                while (-1 != (read = in.read(buffer))) {
                    zos.write(buffer, 0, read);
                }
                in.close();
            }
        }
    }

    public static String getDate() {
        Date getTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy  hh-mm a");
        return dateFormat.format(getTime);
    }
}