package com.ruse.world.instance;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GroupInstanceDialogue {}/* extends AbstractDialogue {

    private final String bossName;
    private final Class<? extends BossInstance> instance;
    private final boolean alreadyInInstance;



    @Override
    public void start() {
        if(alreadyInInstance) {
            sendOptions("Would you like to leave your " + bossName + " instance", "Yes", "Nevermind");
        } else {
            sendOptions("Would you like to create a " + bossName + " instance or join another party?", "Create instance", "Join another party", "Nevermind");
        }
    }

    @Override
    public void run(DialogueOption option) {
        if(!alreadyInInstance) {
            if (option == DialogueOption.OPTION_1) {
                Optional<MapInstance> mi = InstanceManager.findPlayerInstance(player.getUsername(), instance);
                if (mi.isEmpty()) {
                    if (player.getInventory().contains(7478)) {
                        player.getInventory().delete(7478, 1);
                        player.getPacketSender().sendInterfaceRemoval();
                        try {
                            BossInstance a = instance.newInstance();
                            a.setMode(DestroyMode.GROUP);
                            a.initialise();
                            a.add(player);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendDialogue("You need an instance token to create a new " + bossName + " instance.", "Pre-existing instances are free to join.");
                    }
                } else {
                    sendDialogue("You already own instance.", "Please rejoin your existing party.");
                }
            } else if (option == DialogueOption.OPTION_2) {
                player.setInputHandling(new Input() {
                    @Override
                    public void handleSyntax(Player player, String val) {
                        Optional<MapInstance> mi = InstanceManager.findPlayerInstance(val, instance);
                        if (mi.isEmpty()) {
                            sendDialogue("No instance found with that player.", "Please try again.");
                        } else {
                            mi.get().add(player);
                        }
                    }
                });
                player.getPacketSender().sendEnterInputPrompt("Enter instance owner's username:");
            } else {
                player.getClickDelay().reset();
                player.getPacketSender().sendInterfaceRemoval();
            }
        } else {// Leave instance
            if (option == DialogueOption.OPTION_1) {
                player.getMapInstance().remove(player, true);
            } else {
                player.getClickDelay().reset();
                player.getPacketSender().sendInterfaceRemoval();
            }
        }
    }
}*/
