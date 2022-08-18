package com.ruse.world.content.minigames.impl;

import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.content.skill.impl.slayer.SlayerMaster;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.entity.impl.player.Player;

public class VowDialogue extends Dialogue {

    public VowDialogue(Player p) {
        this.p = p;
    }
    /*
     * public static Dialogue getDialogue(Player player) { return new Dialogue() {
     *
     * @Override public DialogueType type() { return DialogueType.STATEMENT; }
     *
     * @Override public DialogueExpression animation() { return null; }
     *
     * @Override public String[] dialogue() { return new String[] {
     * "Who would you like to invite?"
     *
     * };
     *
     * } }; }
     */

    private Player p;

    @Override
    public DialogueType type() {
        return DialogueType.PLAYER_STATEMENT;
    }

    @Override
    public DialogueExpression animation() {
        return DialogueExpression.CALM;
    }

    @Override
    public String[] dialogue() {
        return new String[] { "Upgrading all three of the magical gloves will give me","the most powerfull glove in Solak at a chance of 20%." };
    }

    @Override
    public int npcId() {
        return -1;
    }

    @Override
    public Dialogue nextDialogue() {
        return new Dialogue() {

            @Override
            public DialogueType type() {
                return DialogueType.OPTION;
            }

            @Override
            public DialogueExpression animation() {
                return null;
            }

            @Override
            public String[] dialogue() {
                return new String[] { "Yes",
                        "No" };
            }

            @Override
            public void specialAction() {
                p.setDialogueActionId(666);
            }

        };
    }
}