package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public class RaidsInterface {

    private Player player;
    private Raids selectedRaids;

    public RaidsInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {

        switch (buttonId) {
            case 120005:
                handleTeleport();
                return true;
            case 120006:
                openInterface(Raids.FURY_RAIDS);
                return true;
            case 120007:
                openInterface(Raids.NAXRAMAX_RAIDS);
                return true;
        }

        return false;
    }

    public void handleTeleport() {
        if (selectedRaids != null)
            TeleportHandler.teleportPlayer(player, selectedRaids.getPosition(), TeleportType.NORMAL);
    }


    public void openInterface(Raids raids) {
        player.getPacketSender().sendConfig(2466, raids.ordinal());

        selectedRaids = raids;

        player.getPacketSender().sendString(120101, raids.getDescription());
        player.getPacketSender().sendNpcOnInterface(120051, raids.getNpcId(), raids.getAdjustedZoom());

        int id = 120201;
        for (int i = 0; i < 25; i++) {
            if (raids.getRewards().length > i) {
                player.getPacketSender().sendItemOnInterface(id++, raids.getRewards()[i]);
            } else {
                player.getPacketSender().sendItemOnInterface(id++, -1, 1);
            }
        }

        player.getPacketSender().sendInterface(120000);

    }


    @Getter
    public enum Raids {

        FURY_RAIDS("Fury Raids", "Something", new Position(1, 1, 0), 1615, 2000,
                new Item(1050, 1), new Item(1050, 1), new Item(1050, 1),
                new Item(1050, 1), new Item(1050, 1), new Item(1050, 1),
                new Item(1050, 1), new Item(1050, 1), new Item(1050, 1),
                new Item(1050, 1)),
        NAXRAMAX_RAIDS("Naxramax Raids", "Something", new Position(1, 1, 0), 1615, 2000,
                new Item(4151, 1), new Item(1038, 1), new Item(1040, 1)),
        ;

        private String name;
        private String description;
        private Position position;
        private int npcId, adjustedZoom;
        private Item[] rewards;

        Raids(String name, String description, Position position, int npcId, int adjustedZoom, Item... rewards) {
            this.name = name;
            this.description = description;
            this.npcId = npcId;
            this.adjustedZoom = adjustedZoom;
            this.position = position;
            this.rewards = rewards;
        }
    }

}
