package com.ruse.model.input.impl;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.ruse.world.content.PlayerLogs.getTime;

public class EnterReferral extends Input {

    public static final String[] refs = {
            "unrealchris", "walkchaos", "baguette", "baguette rsps", "racoon", "racoon rsps",
            "casalusio","casa", "vihtic", "i pk max jr",  "wr3ckedyou",  "wr3cked you",  "lano",
            "sprad", "noobsown", "noobs own",
            "effigy", "effigyswiper", "winallday",
            "vihtic", "ipkmaxjr", "frimb", "chopper",
            "fpk", "fewb",   "fpk merk",
            "runescapeorig", "stable", "stabletv",
            "didyscape", "didy", "Jetman", "Treumer", "ceraxy",
    "runelist", "runelocus", "rsps-list", "rspslist", "rsps", "wet", "eggy", "sixx",
            "morgen"
    }; // codes here

    private static List<String> addValue(List<String> lines, String username) {
        List<String> newLines = new ArrayList<String>();
        for (String line : lines) {
            if (line.contains(username)) {
                String[] vals = line.split(" : ");
                newLines.add(vals[0] + " : " + (Integer.parseInt(vals[1]) + 1));
            } else {
                newLines.add(line);
            }

        }
        return newLines;
    }

    public static boolean referralResponse(Player player, String username) {
        if (Arrays.stream(refs).anyMatch(username::equalsIgnoreCase)) {
            player.getInventory().add(ItemDefinition.COIN_ID, 5000000);
            player.getInventory().add(23020, 2);
            player.sendMessage("@red@Congrats! Because you used the code " + username + " You have gotten a reward!");
            return true;
        }
        return false;
    }

    @SneakyThrows
    public static boolean checkIps(String ip) {
        File file = new File("data/refer/referral_data.txt");
        if (!file.exists())
            file.createNewFile();
        Scanner scanner = new Scanner(file);
        String content;
        int count = 0;
        while (scanner.hasNext()) {
            content = scanner.nextLine();
            String[] split = content.split(" : ");
            if (ip.equalsIgnoreCase(split[0])) {
                count++;
            }
            if (count >= 1) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    public static boolean checkMac(String mac) {
        File file = new File("data/refer/referral_data.txt");
        if (!file.exists())
            file.createNewFile();

        Scanner scanner = new Scanner(file);
        String content;
        int count = 0;
        while (scanner.hasNext()) {
            content = scanner.nextLine();
            String[] split = content.split(" : ");
            if (mac.equalsIgnoreCase(split[1])) {
                count++;
            }
            if (count >= 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleSyntax(Player player, String syntax) {
        if (checkIps(player.getHostAddress())) {
            player.getPacketSender().sendMessage("You have already received a referral reward!");
            return;
        }
        if (player.getMac() != null && checkMac(player.getMac())) {
            player.getPacketSender().sendMessage("You have already received a referral reward!");
            return;
        }

        if (referralResponse(player, syntax)) {
            try {
                BufferedWriter w = new BufferedWriter(new FileWriter("data/refer/referral_data.txt", true));
                w.write(player.getHostAddress() + " : " + player.getMac() + " : " + player.getUsername() + " : " + getTime() + ": " + syntax);
                w.newLine();
                w.flush();
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                List<String> lines;
                boolean contains = false;
                File f = new File("./data/refer/total.txt");
                if (!f.exists())
                    f.createNewFile();


                lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());

                for (String line : lines) {
                    if (line.split(" : ")[0].equalsIgnoreCase(syntax)) {
                        contains = true;
                    }
                }

                if (contains) {
                    addValue(lines, syntax);
                    Files.write(f.toPath(), addValue(lines, syntax), Charset.defaultCharset());
                } else {
                    FileWriter total = new FileWriter("./data/refer/total.txt", true);
                    if (total != null) {
                        total.write(syntax + " : 1");
                        total.write(System.lineSeparator());
                        total.close();
                    }
                }
            } catch (Exception e) {
                // log.error("Error while writing promo code data", e);
            }

        }
    }

    public static List<String> getReferrals() {
        try {
            List<String> lines;
            File f = new File("./data/refer/total.txt");
            lines = Files.readAllLines(f.toPath(), Charset.defaultCharset());
            return lines;
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }
}