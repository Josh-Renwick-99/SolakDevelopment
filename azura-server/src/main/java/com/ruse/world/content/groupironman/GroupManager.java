package com.ruse.world.content.groupironman;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruse.GameServer;
import com.ruse.model.Item;
import com.ruse.model.PlayerRelations;
import com.ruse.model.input.impl.ChangeGroupIronmanName;
import com.ruse.model.input.impl.CreateIronmanGroup;
import com.ruse.model.input.impl.InviteGroupIronmanPlayer;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class GroupManager {

    public static ArrayList<IronmanGroup> GROUPS = new ArrayList<>();

    public static boolean handleButton(Player player, int buttonID) {
        if (buttonID == 104011) {
            player.setInputHandling(new ChangeGroupIronmanName());
            player.getPacketSender().sendEnterInputPrompt("Change Name");
        } else if (buttonID == 104012) {
            player.setInputHandling(new InviteGroupIronmanPlayer());
            player.getPacketSender().sendEnterInputPrompt("Invite Player");
        }
        return false;
    }

    public static void createGroup(Player player) {
        if (isInGroup(player)) {
            player.sendMessage("You are already in an ironman group.");
            return;
        }
        player.getPacketSender().sendInterfaceRemoval();
        player.setInputHandling(new CreateIronmanGroup());
        player.getPacketSender().sendEnterInputPrompt("Enter a Group Name:");
    }

    public static void createGroup(Player player, String name) {
        if (name.length() < 3 || name.length() > 18) {
            player.sendMessage("Your group name must be between 3-18 characters.");
            return;
        }
        if (getGroup(name) != null) {
            player.sendMessage("This group name is already taken.");
            return;
        }

        IronmanGroup newGroup = new IronmanGroup(name);
        newGroup.setLeader(player.getUsername());
        newGroup.addPlayer(player);
        newGroup.setUniqueId(getNextId());

        GROUPS.add(newGroup);
        saveGroup(newGroup);

        player.sendMessage("You made an Ironman group: " + newGroup.getName());
    }

    public static int getNextId() {
        int ID = -1;

        try {
            BufferedReader r = new BufferedReader(new FileReader("./data/saves/groupID.txt"));
            String line = r.readLine();
            ID = Integer.parseInt(line);
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader file = new BufferedReader(new FileReader("./data/saves/groupID.txt"));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            String inputStr = inputBuffer.toString().replace("\n", "").replace("\t", "");
            int number = Integer.parseInt(inputStr) + 1;
            inputStr = number + "";

            FileOutputStream fileOut = new FileOutputStream("./data/saves/groupID.txt");
            fileOut.write(inputStr.getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }

        return ID;
    }

    public static IronmanGroup getGroup(String name) {
        for (IronmanGroup group : GROUPS) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return null;
    }

    public static IronmanGroup getGroup(int groupId) {
        for (IronmanGroup group : GROUPS) {
            if (group.getUniqueId() == groupId) {
                return group;
            }
        }
        return null;
    }

    public static void openInterface(Player player) {
        if (player.getIronmanGroup() != null) {
            player.getPacketSender().sendInterface(104000);

            player.getPacketSender().sendString(104006, "Group Name: @whi@" + player.getIronmanGroup().getName());
            player.getPacketSender().sendString(104008, "Total Level");

            int interfaceId = 104013;
            for (int i = 0; i < 5; i++) {
                if (player.getIronmanGroup().getMembers().size() > i) {
                    String name = player.getIronmanGroup().getMembers().get(i);
                    Player target = World.getPlayerByName(name);
                    player.getPacketSender().sendString(interfaceId++, name);
                    if (target != null) {
                        player.getPacketSender().sendString(interfaceId++, "" + target.getSkillManager().getTotalLevel());
                        player.getPacketSender().sendString(interfaceId++,
                                player.getRelations().getStatus() == PlayerRelations.PrivateChatStatus.ON ? "@gre@Online" : "@red@Offline");
                    } else {
                        target = IronmanPlayerLoading.getResult(name);
                        player.getPacketSender().sendString(interfaceId++, "" + target.getSkillManager().getTotalLevel() );
                        player.getPacketSender().sendString(interfaceId++, "@red@Offline");
                    }
                } else {
                    player.getPacketSender().sendString(interfaceId++, "---");
                    player.getPacketSender().sendString(interfaceId++, "---");
                    player.getPacketSender().sendString(interfaceId++, "---");
                }
            }
        }
    }


    public static boolean isInGroup(Player player) {
        return player.getIronmanGroup() != null && isInGroup(player.getUsername());
    }

    public static boolean isInGroup(String name) {
        for (IronmanGroup group : GROUPS) {
            for (String member : group.getMembers()) {
                if (member.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void loadGroups() {
        GROUPS.clear();
        File[] files = new File(GroupConfig.path).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {

            IronmanGroup group = new IronmanGroup(file.getName().replace(".json", ""));
            try (FileReader fileReader = new FileReader(file)) {
                JsonParser fileParser = new JsonParser();
                Gson builder = new GsonBuilder().create();
                JsonObject reader = (JsonObject) fileParser.parse(fileReader);
                if (reader.has("Name")) {
                    group.setName(reader.get("Name").getAsString());
                }
                if (reader.has("UniqueID")) {
                    group.setUniqueId(reader.get("UniqueID").getAsInt());
                }
                if (reader.has("Leader")) {
                    group.setLeader(reader.get("Leader").getAsString());
                }
                if (reader.has("Members")) {
                    String[] members = builder.fromJson(reader.get("Members").getAsJsonArray(), String[].class);
                    for (String l : members) {
                        group.getMembers().add(l);
                    }
                }
                if (reader.has("Bank")) {
                    group.getBank()
                            .setItems(builder.fromJson(reader.get("Bank").getAsJsonArray(), Item[].class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            GROUPS.add(group);
        }
    }


    public static void saveGroups() {
        for (IronmanGroup group : GROUPS) {
            saveGroup(group);
        }
    }


    public static void saveGroup(IronmanGroup group) {
        File file = new File(GroupConfig.path + group.getName() + ".json");
        try (FileWriter writer = new FileWriter(file)) {

            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            object.addProperty("Name", group.getName());
            object.addProperty("UniqueID", group.getUniqueId());
            object.addProperty("Leader", group.getLeader());
            object.add("Members", builder.toJsonTree(group.getMembers()));
            object.add("Bank", builder.toJsonTree(group.getBank().getItems()));

            writer.write(builder.toJson(object));
        } catch (Exception e) {
            GameServer.getLogger().log(Level.WARNING, "An error has occured while saving a ironman group!", e);
        }
    }


}
