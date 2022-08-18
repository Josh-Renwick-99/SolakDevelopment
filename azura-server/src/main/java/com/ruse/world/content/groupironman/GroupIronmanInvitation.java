package com.ruse.world.content.groupironman;

import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;

public class GroupIronmanInvitation extends Dialogue {
	
	public GroupIronmanInvitation(Player inviter, Player p) {
		this.inviter = inviter;
		this.p = p;
	}
	
	private Player inviter, p;
	
	@Override
	public DialogueType type() {
		return DialogueType.STATEMENT;
	}
	
	@Override
	public DialogueExpression animation() {
		return null;
	}
	
	@Override
	public String[] dialogue() {
		return new String[]{"" + inviter.getUsername() + " has invited you to join their Ironman group."};
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
				return new String[]{"Join " + inviter.getUsername() + "'s group", "Don't join " + inviter.getUsername() + "'s group."};
			}
			
			@Override
			public void specialAction() {
				p.setDialogueActionId(670);
			}
		};
	}
}