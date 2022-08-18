package com.ruse.world.content.groupironman;

import com.ruse.model.GameMode;
import com.ruse.model.Position;
import com.ruse.model.container.impl.GroupIronmanBank;
import com.ruse.world.World;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class IronmanGroup {

    @Getter
    @Setter
    private ArrayList<String> members = new ArrayList<>();

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String leader;
    @Getter
    @Setter
    private GroupIronmanBank bank;
    @Getter
    @Setter
    private int uniqueId;

    public IronmanGroup(String name) {
        this.name = name;
        this.bank = new GroupIronmanBank();
    }


    public void addPlayer(Player player) {
        if (GroupManager.isInGroup(player)) {
            return;
        }
        members.add(player.getUsername());
        player.setIronmanGroup(this);
        GroupManager.saveGroup(this);
        player.setGroupIronmanLocked(false);
    }

    public void sendMessage(String message) {
        for (String member : members) {
            Player player = World.getPlayerByName(member);
            if (player == null)
                continue;
            else
                player.sendMessage("@blu@GIM: " + message);
        }
    }

    public void kickPlayer(Player player, String name) {
        if (!GroupManager.isInGroup(name)) {
            player.sendMessage("This player is not in this group");
            return;
        }
        if (getLeader().equalsIgnoreCase(name)) {
            if (getMembers().size() > 1) {
                setLeader(getMembers().get(1));
            } else {
                player.sendMessage("There is only one person in this group.");
                return;
            }
        }

        members.remove(name);

        Player kicked = World.getPlayerByName(name);
        if (kicked != null) {
            kicked.setIronmanGroup(null);
            kicked.moveTo(new Position(3039, 2845, 1));
            kicked.setGroupIronmanLocked(true);
        }
        GroupManager.saveGroup(this);

    }

    public void changeName(Player player, String input) {
        if (input.length() < 3 || input.length() > 18) {
            player.sendMessage("Your group name must be between 3-18 characters.");
            return;
        }
        if (GroupManager.getGroup(input) != null) {
            player.sendMessage("This group name is already taken.");
            return;
        }

        setName(input);
        player.sendMessage("Group name changed to: " + getName());
        GroupManager.openInterface(player);
    }

    public Player getOwner() {
        return World.getPlayerByName(getLeader());
    }


    public void invite(Player p) {
        if (getOwner() == null || p == getOwner())
            return;
        if (p.getGameMode() != GameMode.GROUP_IRONMAN){
            getOwner().getPacketSender().sendMessage("That player is is not a group ironman account.");
            return;
        }
        if (members.size() >= 5) {
            getOwner().getPacketSender().sendMessage("Your group is full.");
            return;
        }
        if (members.contains(p.getUsername())) {
            getOwner().getPacketSender().sendMessage("That player is already in your party.");
            return;
        }
        if (GroupManager.isInGroup(p)) {
            getOwner().getPacketSender().sendMessage("That player is currently in another group.");
            return;
        }
        if (p.busy()) {
            getOwner().getPacketSender().sendMessage("That player is currently busy.");
            return;
        }

        p.setGroupInvitationId(uniqueId);
        DialogueManager.start(p, new GroupIronmanInvitation(getOwner(), p));
        getOwner().getPacketSender().sendMessage("An invitation has been sent to " + p.getUsername() + ".");
    }
}
