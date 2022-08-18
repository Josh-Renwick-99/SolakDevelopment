package com.ruse.net.packet;

import com.ruse.net.packet.impl.*;
import com.ruse.world.entity.impl.player.Player;

public class PacketConstants {

	public static final PacketListener[] PACKETS = new PacketListener[257];

	static {
		for (int i = 0; i < PACKETS.length; i++)
			PACKETS[i] = new SilencedPacketListener();

		PACKETS[225] = new WheelOfFortuneEndListener();


		PACKETS[4] = PACKETS[230] = new ChatPacketListener();
		PACKETS[EquipPacketListener.OPCODE] = new EquipPacketListener();
		PACKETS[140] = new PacketListener() {

			@Override
			public void handleMessage(Player player, Packet packet) {
				if (packet.getOpcode() == 140) {
					int interfaceId = packet.readLEShort();
					int id = packet.readShortA();
					int slot = packet.readShortA();
					if (-31915 == interfaceId) {
						player.getPlayerOwnedShopManager().handleWithdraw(slot, id, 5);
					} else if (32621 == interfaceId) {
						player.getPlayerOwnedShopManager().handleBuy(slot, id, 5);
					}
				}
			}

		};
		PACKETS[127] = new PacketListener() {

			@Override
			public void handleMessage(Player player, Packet packet) {
				if (packet.getOpcode() == 127) {
					String text = packet.readString();
					int index = text.indexOf(",");
					int id = Integer.parseInt(text.substring(0, index));
					String string = text.substring(index + 1);

					if (id == 32611) {
						player.getPlayerOwnedShopManager().searchItem(string);
					}
				}
			}

		};
		PACKETS[141] = new BankModifiableX();
		PACKETS[109] = new BadPacketListener();
		PACKETS[87] = new DropItemPacketListener();
		PACKETS[103] = new CommandPacketListener();
		PACKETS[121] = new FinalizedMapRegionChangePacketListener();
		PACKETS[130] = new CloseInterfacePacketListener();
		PACKETS[ButtonClickPacketListener.OPCODE] = new ButtonClickPacketListener();
		PACKETS[2] = new ExamineItemPacketListener();
		PACKETS[6] = new ExamineNpcPacketListener();
		PACKETS[5] = new SendClanChatMessagePacketListener();
		//PACKETS[7] = new WithdrawMoneyFromPouchPacketListener();
		PACKETS[8] = new ChangeRelationStatusPacketListener();
		PACKETS[11] = new ChangeAppearancePacketListener();
		PACKETS[202] = new IdleLogoutPacketListener();
		PACKETS[131] = new NPCOptionPacketListener();
		PACKETS[17] = new NPCOptionPacketListener();
		PACKETS[18] = new NPCOptionPacketListener();
		PACKETS[21] = new NPCOptionPacketListener();
		PACKETS[210] = new RegionChangePacketListener();
		PACKETS[214] = new SwitchItemSlotPacketListener();
		PACKETS[236] = new PickupItemPacketListener();
		PACKETS[73] = new FollowPlayerPacketListener();
		PACKETS[NPCOptionPacketListener.ATTACK_NPC] = PACKETS[NPCOptionPacketListener.FIRST_CLICK_OPCODE] = new NPCOptionPacketListener();
		PACKETS[EnterInputPacketListener.ENTER_SYNTAX_OPCODE] = PACKETS[EnterInputPacketListener.ENTER_AMOUNT_OPCODE] = PACKETS[EnterInputPacketListener.ENTER_LONG_AMOUNT_OPCODE] = new EnterInputPacketListener();
		PACKETS[UseItemPacketListener.ITEM_ON_GROUND_ITEM] = PACKETS[UseItemPacketListener.ITEM_ON_ITEM] = PACKETS[UseItemPacketListener.ITEM_ON_NPC] = PACKETS[UseItemPacketListener.ITEM_ON_OBJECT] = PACKETS[UseItemPacketListener.ITEM_ON_PLAYER] = new UseItemPacketListener();
		PACKETS[UseItemPacketListener.USE_ITEM] = new UseItemPacketListener();
		PACKETS[TradeInvitationPacketListener.TRADE_OPCODE] = new TradeInvitationPacketListener();
		PACKETS[TradeInvitationPacketListener.CHATBOX_TRADE_OPCODE] = new TradeInvitationPacketListener();
		PACKETS[DialoguePacketListener.DIALOGUE_OPCODE] = new DialoguePacketListener();
		PACKETS[PlayerRelationPacketListener.ADD_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.REMOVE_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.ADD_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.REMOVE_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.SEND_PM_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[MovementPacketListener.COMMAND_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[MovementPacketListener.GAME_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[MovementPacketListener.MINIMAP_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[ObjectActionPacketListener.FIRST_CLICK] = PACKETS[ObjectActionPacketListener.SECOND_CLICK] = PACKETS[ObjectActionPacketListener.THIRD_CLICK] = PACKETS[ObjectActionPacketListener.FOURTH_CLICK] = PACKETS[ObjectActionPacketListener.FIFTH_CLICK] = new ObjectActionPacketListener();
		PACKETS[ItemContainerActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.FOURTH_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.FIFTH_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.SIXTH_ITEM_ACTION_OPCODE] = new ItemContainerActionPacketListener();
		PACKETS[ItemActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = PACKETS[ItemActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = PACKETS[ItemActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = new ItemActionPacketListener();
		PACKETS[MagicOnItemsPacketListener.MAGIC_ON_ITEMS] = new MagicOnItemsPacketListener();
		PACKETS[MagicOnItemsPacketListener.MAGIC_ON_GROUNDITEMS] = new MagicOnItemsPacketListener();
		PACKETS[249] = new MagicOnPlayerPacketListener();
		PACKETS[153] = new PlayerOptionPacketListener();
		PACKETS[DuelAcceptancePacketListener.OPCODE] = new DuelAcceptancePacketListener();
		PACKETS[12] = new DungeoneeringPartyInvitatationPacketListener();
		PACKETS[204] = new GESelectItemPacketListener();
		PACKETS[222] = new ClickTextMenuPacketListener();
		PACKETS[223] = new PrestigeSkillPacketListener();
		PACKETS[229] = new HeightCheckPacketListener();
		PACKETS[199] = new ScratchCardEndPacketListener();
		PACKETS[GambleInvititationPacketListener.GAMBLE_OPCODE] = new GambleInvititationPacketListener();
		PACKETS[GambleInvititationPacketListener.CHATBOX_GAMBLE_OPCODE] = new GambleInvititationPacketListener();
		
		PACKETS[RaidRequestPacketListener.RAID_OPCODE] = new RaidRequestPacketListener();
		PACKETS[RaidRequestPacketListener.CHATBOX_RAID_OPCODE] = new RaidRequestPacketListener();
	}

	/**
	 * The size of packets sent from client to the server used to decode them.
	 */
	public final static int[] MESSAGE_SIZES = { 0, 0, 2, 1, -1, -1, 2, 4, 4, 4, // 0
			4, -1, -1, -1, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			9, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 1, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 0, // 50
			-1, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			-1, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0, // 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 0, 0, -1, -1, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 6, 2, // 130
			6, 10, 0, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0, // 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 4, 0, 0, 8, 1, // 180
			0, 2, 12, 2, 0, 0, 0, 0, 0, 1, // 190
			2, 0, 0, 0, 2, 0, 0, 0, 4, 8, // 200
			4, 0, 0, 0, 7, 8, 2, 0, 10, 0, // 210
			2, 0, 3, 2, 0, 1, -1, 0, 6, 1, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4, // 240
			0, 0, 6, 6, 0, 0 // 250
	};
}
