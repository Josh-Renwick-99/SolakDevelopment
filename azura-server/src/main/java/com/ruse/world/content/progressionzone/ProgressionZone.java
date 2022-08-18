package com.ruse.world.content.progressionzone;

import com.ruse.model.GameObject;
import com.ruse.model.Item;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;

public class ProgressionZone {

    public static ZoneData.Monsters getCurrentZone(Player player) {
        if (player.getZonesComplete()[4] == true) {
            return ZoneData.Monsters.values()[4];
        }
        for (int i = player.getZonesComplete().length - 1; i >= 0; i--) {
            if (player.getZonesComplete()[i] == true) {
                return ZoneData.Monsters.values()[i + 1];
            }
        }
        return ZoneData.Monsters.SONIC;
    }

    public static void teleport(Player player) {
        TeleportHandler.teleportPlayer(player, getCurrentZone(player).getCoords(), TeleportType.NORMAL);
        player.sendMessage("You have been teleported to the " + getCurrentZone(player).getName() + " Zone.");

        player.sendMessage("@blu@Once you completed the zone, you can use the portal to advance to the next zone.");
    }
    public static void teleport(Player player, boolean alternate) {

        Position pos = getCurrentZone(player).getCoords().copy();
        if (alternate){
            pos = getCurrentZone(player).getCoords().copy().setZ( getCurrentZone(player).getCoords().getZ() + 20);
        }
        TeleportHandler.teleportPlayer(player, pos, TeleportType.NORMAL);
        player.sendMessage("You have been teleported to the " + getCurrentZone(player).getName() + " Zone.");

        player.sendMessage("@blu@Once you completed the zone, you can use the portal to advance to the next zone.");
    }

    public static void handleKill(Player player, int npcId) {
        if (!(npcId >= 9001 && npcId <= 9006)) {
            return;
        }

        ZoneData.Monsters monster = ZoneData.Monsters.forID(npcId);

        player.getProgressionZones()[monster.ordinal()] += 1;

        if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                && player.getSummoning().getFamiliar().getSummonNpc().getId() == 302) {
            player.getProgressionZones()[monster.ordinal()] += 1;
        }

        if (player.getProgressionZones()[monster.ordinal()] >= monster.getAmountToKill()
                && player.getZonesComplete()[monster.ordinal()] == false) {
            handleReward(player);
            player.getZonesComplete()[monster.ordinal()] = true;
        }
        updateInterface(player);
    }

    public static void updateInterface(Player player) {
        if (player.getLocation() != Locations.Location.PROGRESSION_ZONES)
            return;

        ZoneData.Monsters monster = getCurrentZone(player);

        for (int i = 0; i < monster.getRewards().length; i++) {
            player.getPacketSender().sendItemOnInterface(112006 + i, monster.getRewards()[i]);
        }
        double percentage = (double) player.getProgressionZones()[monster.ordinal()] / (double) monster.getAmountToKill() * 100;

        if (percentage >= 100)
            percentage = 100;

        player.getPacketSender().sendString(112002, monster.getName());

        player.getPacketSender().sendProgressBar(112004, 0, (int) percentage, 0);


        int amount = player.getProgressionZones()[monster.ordinal()];
        if (amount >= 100)
            amount = 100;
        player.getPacketSender().sendString(112005, (int) percentage + "% (" + amount + "/" + monster.getAmountToKill() + ")");

        player.getPacketSender().sendWalkableInterface(112000, true);

    }

    public static void handleReward(Player player) {
        ZoneData.Monsters monster = getCurrentZone(player);

        player.sendMessage("You killed x" + monster.getAmountToKill() + " " + monster.getName() + "'s and received a reward.");
        for (Item item : monster.getRewards()) {
            if (!player.getInventory().isFull()) {
                player.getInventory().add(item);
            } else {
                player.depositItemBank(item);
            }
        }
    }


    public static void handleGates(Player player, GameObject gameObject, boolean back) {
        ZoneData.Monsters monster = getCurrentZone(player);

        int ordinal = gameObject.getPosition().getZ() / 4;

        if (gameObject.getPosition().getZ() >= 20){
            player.sendMessage("You can only do this in the default zone. To progress type ::train2");
            return;
        }

        if (player.getPosition().getZ() == gameObject.getPosition().getZ() && back) {
            if (ordinal == 0) {
                player.sendMessage("@mag@You are already at the lowest zone!");
            } else {
                player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 4));
                player.sendMessage("@mag@You move to the previous zone!");
            }
            return;
        }
        if (ordinal +1 == ZoneData.Monsters.values().length) {
            player.sendMessage("@mag@You are at the highest zone.");
            return;
        }
        if (player.getZonesComplete()[ordinal]) {
            if (player.getPosition().getZ() == gameObject.getPosition().getZ()) {
                player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 4));
                player.sendMessage("@mag@You move to the next zone!");
            }
        } else {
            player.sendMessage("@mag@You need to kill x" + monster.getAmountToKill() + " " + monster.getName() + "'s to pass.");
        }
    }
}
