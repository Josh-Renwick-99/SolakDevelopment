package com.ruse.world.content.zombie;


import com.ruse.model.GameMode;
import com.ruse.model.PlayerRights;
import com.ruse.model.Position;
import com.ruse.model.Locations;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.concurrent.CopyOnWriteArrayList;


public class ZombieParty {

    private Player owner;
    private CopyOnWriteArrayList<Player> player_members;
    private CopyOnWriteArrayList<NPC> npc_members = new CopyOnWriteArrayList<NPC>();
    private boolean hasEnteredDungeon;
    private int entered;
    private int kills;
    private int height;
    private int currentPhase;
    private int phaseAmount;
    private int deathCount;

    public ZombieParty(Player owner) {
        this.owner = owner;
        player_members = new CopyOnWriteArrayList<Player>();
        player_members.add(owner);
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }

    public int getPhaseAmount() {
        return phaseAmount;
    }

    public void setPhaseAmount(int phaseAmount) {
        this.phaseAmount = phaseAmount;
    }

    public void invite(Player p) {
        if (getOwner() == null || p == getOwner())
            return;
        if (hasEnteredDungeon) {
            getOwner().getPacketSender().sendMessage("You cannot invite anyone right now.");
            return;
        }
        if (player_members.size() >= 12) {
            getOwner().getPacketSender().sendMessage("Your party is full.");
            return;
        }
        if (p.getLocation() != Locations.Location.ZOMBIE_LOBBY || p.isTeleporting()) {
            getOwner().getPacketSender().sendMessage("That player is not in Raids.");
            return;
        }
        if (player_members.contains(p)) {
            getOwner().getPacketSender().sendMessage("That player is already in your party.");
            return;
        }
        if (p.getZombieParty() != null) {
            getOwner().getPacketSender().sendMessage("That player is currently in another party.");
            return;
        }
        if (p.getRights() != PlayerRights.DEVELOPER && System.currentTimeMillis()
                - getOwner().getMinigameAttributes().getZombieAttributes().getLastInvitation() < 2000) {
            getOwner().getPacketSender().sendMessage("You must wait 2 seconds between each party invitation.");
            return;
        }
        if (p.busy()) {
            getOwner().getPacketSender().sendMessage("That player is currently busy.");
            return;
        }


        if (getOwner().getGameMode() == GameMode.GROUP_IRONMAN && !getOwner().getIronmanGroup().equals(p.getIronmanGroup())){
            p.getPacketSender().sendMessage("You are not a part of this players ironman group.");
            return;
        }
        if (p.getGameMode() == GameMode.GROUP_IRONMAN && !p.getIronmanGroup().equals(getOwner().getIronmanGroup())){
            p.getPacketSender().sendMessage("You are not a part of this players ironman group.");
            return;
        }

        getOwner().getMinigameAttributes().getZombieAttributes().setLastInvitation(System.currentTimeMillis());
        DialogueManager.start(p, new ZombiePartyInvitation(getOwner(), p));
        getOwner().getPacketSender().sendMessage("An invitation has been sent to " + p.getUsername() + ".");
    }

    public void add(Player p) {
        if (player_members.size() >= 12) {
            p.getPacketSender().sendMessage("That party is already full.");
            return;
        }
        if (hasEnteredDungeon) {
            p.getPacketSender().sendMessage("This party has already entered a dungeon.");
            return;
        }
        if (p.getLocation() != Locations.Location.ZOMBIE_LOBBY || p.isTeleporting()) {
            return;
        }


        sendMessage(p.getUsername() + " has joined the party.");
        p.getPacketSender().sendMessage("You've joined " + getOwner().getUsername() + "'s party.");
        player_members.add(p);
        p.setZombieParty(this);
        owner.setZombieParty(this);

    }

    public void remove(Player p, boolean fromParty) {
        if (fromParty) {
            player_members.remove(p);
        }
        p.getPacketSender().sendCameraNeutrality();
        p.getPacketSender().sendInterfaceRemoval();
        p.getPacketSender().sendString(111709, "Create");
        int id = 111716;
        for (int i = 111716; i < 111737; i++) {
            p.getPacketSender().sendString(id++, "---");
            p.getPacketSender().sendString(id++, "--");
            p.getPacketSender().sendString(id++, "-");
        }
        p.getPacketSender().sendString(111702, "Raiding Party: @whi@0");

        if (p.getPosition().getY() >= 5709) {
            if (fromParty) {
                // System.out.println("move: ");
            }
        }

        if (p == owner) {
            if (fromParty) {
                if (getPlayers().size() >= 1) {
                    Player newOwner = getPlayers().get(0);
                    sendMessage(p.getUsername() + " has left the party.");
                    sendMessage("The party owner has been transfered to: " + newOwner.getUsername());
                    setOwner(newOwner);
                } else {
                    sendMessage("The raids party has been destroyed.");
                }
            }

        } else {
            // System.out.println("else: ");
            if (fromParty) {
                sendMessage(p.getUsername() + " has left the party.");
            }
        }
        if (fromParty) {
            p.setZombieParty(null);
        }
        p.getPacketSender().sendInterfaceRemoval();

        boolean destruct = true;
        if (hasEnteredRaids()) {
            for (Player player : getPlayers()) {
                if (player.getLocation() == Locations.Location.ZOMBIE)
                    destruct = false;
            }
        } else {
            destruct = false;
        }
        if (getPlayers().size() <= 0 || destruct) {
            ZombieRaids.destroyInstance(this);
        }
    }

    public void sendMessage(String message) {
        for (Player member : getPlayers()) {
            if (member != null) {
                member.getPacketSender().sendMessage(message);
            }
        }
    }

    public void moveTo(Position position) {
        for (Player member : getPlayers()) {
            if (member != null) {
                member.moveTo(position);
            }
        }
    }

    public void create() {

        if (owner.getZombieParty() != null) {
            owner.getPacketSender().sendMessage("You are already in a Raids party.");
            return;
        }

        if (owner.getZombieParty() == null)
            owner.setZombieParty(new ZombieParty(owner));

        owner.getPacketSender().sendMessage("<col=660000>You've created a Raids party.");

        owner.setZombieParty(this);

    }

    public void refreshInterface() {
        for (Player member : getPlayers()) {
            if (member != null) {
                member.getPacketSender().sendString(111709, "Invite");

                int start = 111716;
                for (int i = 0; i < getPlayers().size(); i++) {
                    member.getPacketSender().sendString(start++, "" + getPlayers().get(i).getUsername());
                    member.getPacketSender().sendString(start++,
                            "" + getPlayers().get(i).getSkillManager().getTotalLevel());
                    member.getPacketSender().sendString(start++,
                            "" + getPlayers().get(i).getSkillManager().getCombatLevel());
                }

                for (int i = start; i < 111737; i++) {
                    member.getPacketSender().sendString(start++, "---");
                    member.getPacketSender().sendString(start++, "--");
                    member.getPacketSender().sendString(start++, "-");
                }

                member.getPacketSender().sendString(111702, "Raiding Party: @whi@" + getPlayers().size());
            }
        }
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public CopyOnWriteArrayList<Player> getPlayers() {
        return player_members;
    }

    public boolean hasEnteredRaids() {
        return hasEnteredDungeon;
    }

    public void enteredDungeon(boolean hasEnteredDungeon) {
        this.hasEnteredDungeon = hasEnteredDungeon;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public CopyOnWriteArrayList<NPC> getNpcs() {
        return npc_members;
    }

    public void startRaid() {
        ZombieRaids.firstWave(this);
    }


}
