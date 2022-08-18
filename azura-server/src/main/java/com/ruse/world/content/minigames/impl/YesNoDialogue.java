package com.ruse.world.content.minigames.impl;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class YesNoDialogue extends Dialogue {

    public YesNoDialogue(Player p) {
        this.p = p;
        this.line1 = line1;
        this.line2 = line2;
        this.actionId = actionId;
    }
    public void setText(String text) {
        setText(text, "");
    }
    public void setText(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }
    public void setActionId(int id) {
        this.actionId = id;
    }

    public YesNoDialogue(Player p, String line1, String line2, int actionId) {
        this.p = p;
        this.line1 = line1;
        this.line2 = line2;
        this.actionId = actionId;
    }

    private Player p;
    private String line1;
    private String line2;
    private int actionId;

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
        return new String[] { line1,line2 };
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
                p.setDialogueActionId(actionId);
            }

        };
    }
}