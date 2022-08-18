package com.ruse.world.content.transportation;

import com.ruse.GameSettings;
import com.ruse.model.Position;

public enum TeleportLocations {

	/**
	 * @Author Crimson Central location to be used as main repository for in-game
	 *         cordinates. There's no real reason to have them scattered across a
	 *         ton of files.
	 */

	// CITIES
	AL_KHARID(new Position(3292, 3176, 0), "Welcome to the sandy Al Kharid."),
	ARDOUGNE(new Position(2662, 3307, 0), "East Ardougne - the city that never sleeps."),
	CAMELOT(new Position(2757, 3477, 0), "Greetings adventurer, welcome to Camelot."),
	CANIFIS(new Position(3496, 3486, 0), "Canifis, an oldschool, spooky town."),
	DRAYNOR(new Position(3105, 3251, 0), "A run-down village, it's seen better days."),
	EDGEVILLE(new Position(3094, 3503, 0), "Welcome to the Edge of the world."),
	FALADOR(new Position(2965, 3379, 0), "Welcome to the White Knight city, traveler."),
	HOME(GameSettings.HOME_CORDS, "Teleporting you home."),
	VARROCK(new Position(3213, 3424, 0), "Greetings, from the kingdom of Varrock."),
	KARAMJA(new Position(2918, 3176, 0), "Enjoy the tropics of Karamja."),
	LUMBRIDGE(new Position(3224, 3219, 0), "Enjoy your visit to Lumbridge, adventurer!"),
	YANILLE(new Position(2607, 3093, 0), "Ah, Yanille - a fortress in a class of it's own."),
	// CUSTOM MAPS
	BEGINNERZONE(new Position(2527, 2527, 0), "<img=96>Welcome to the low level training area"),
	STORMTROOPERS(new Position(2090, 3224, 0),
			"<img=96>@red@Tip:@bla@ Stormtroopers drop saber crystals use them to fight Jedi Masters"),
	SKULL_ISLAND(new Position(2925, 3248, 0), "You will find monkey guards and skeletons here!"),
	EVENTZONE(new Position(2738, 3467, 0),
			"<img=97>@red@Event Zone:@bla@ You get event points from kills, Spend points in store"),
	UPGRADEZONE(new Position(3152, 2929, 0),
			"<img=97>@red@Upgrade Zone:@bla@ Buy items from shop and use shillings to upgrade items"),
	RAINBOWCITY(new Position(2008, 4440, 0),
			"<img=96>Welcome to @red@R@mag@a@yel@i@gre@n@cya@b@blu@o@red@w@mag@ C@yel@i@gre@t@cya@y@bla@ - Fun drops!"),
	DEVILSPAWN(new Position(2962, 9496, 0),
			"<img=96>@red@Devilspawn:@bla@ You need 500 NPC kills in order to attack spawns"),
	INFERIOR(new Position(2988, 9489, 0),
			"<img=97>@red@Inferior Lords:@bla@ You need 100 spawn kills in order to attack lords"),
	FRACTITE(new Position(2318, 3887, 0),
			"<img=98>@red@Fractite Demon:@bla@ You need 200 Lord kills in order to attack demons"),
	JOYX(new Position(2378, 3104, 0),
			"<img=98>@red@Joyx Dragon:@bla@ You need 300 Demon kills in order to attack dragons"),
	MageBeast(new Position(1698, 5600, 0),
			"<img=99>@red@MageBeast:@bla@ You need 400 Dragon kills in order to attack beasts"),
	DEVOURER(new Position(2399, 3488, 0),
			"<img=100>@red@Cyantrix:@bla@ You need 10K+ NPC kills in order to attack NPCS"),
	BULWARK(new Position(2417, 3522, 0),
			"<img=100>@red@Cyantrix:@bla@ You need 20K+ NPC kills in order to attack NPCS"),
	STAR_WARS(new Position(2090, 3224, 0), ""), STAR_WARS2(new Position(2090, 3224, 4), ""),
	KINGKONG(new Position(2925, 3248, 0), ""), GODZILLA(new Position(3024, 3026, 0), ""),
	// MONSTERS
	ROCK_CRABS(new Position(2510, 2510, 0), ""), EXPERIMENTS(new Position(2097, 4427, 0), ""),
	STARTER(new Position(3084, 3511, 0), "@or2@use the green portal to enter starterzone"),
	SLAYER1(new Position(1859, 5239, 0), "@blu@Betrayed zone: @bla@In this zone you will find a lot of different monsters to fight"),
	SLAYER2(new Position(2043, 5243, 0), "@blu@Damned zone: @bla@In this zone you will find a lot of different monsters to fight"),
	SLAYER3(new Position(2132, 5256, 0), "@blu@Hidden zone: @bla@In this zone you will a lot of find different monsters to fight"),
	SLAYER4(new Position(2361, 5218, 0), "@blu@cursed zone: @bla@In this zone you will find a lot of different monsters to fight"),
	YAKS(new Position(3204, 3264, 0), ""), MUMMY(new Position(2757, 9185, 0), ""),
	PREDATOR(new Position(2899, 3617, 0), ""), BANDITS(new Position(3171, 2981, 0), ""),
	GHOULS(new Position(3420, 3510, 0), ""), CHAOS_DRUIDS(new Position(2931, 9846, 0), ""),
	GOBLINS(new Position(3259, 3228, 0), ""), DUST_DEVILS(new Position(3279, 2964, 0), ""),
	CHICKENS(new Position(3235, 3295, 0), ""), MONKEY_SKELETONS(new Position(2802, 9148, 0), ""),
	MONKEY_GUARDS(new Position(2793, 2773, 0), ""), ARMOURED_ZOMBIES(new Position(3085, 9672, 0), ""),
	WARS(new Position(2912, 4064, 0),
			"<img=100>@red@Shadow Warrior:@bla@ You need 50+ Total prestiges in order to attack NPCS"),
	WARS2(new Position(2912, 4064, 0),
			"<img=100>@red@COL Warrior:@bla@ You need 100+ Total prestiges in order to attack NPCS"),

	// DUNGEONS
	EDGE_DUNGEON(new Position(3097, 9870, 0), ""), SLAYER_TOWER(new Position(3429, 3538, 0), ""),
	BRIMHAVEN_DUNGEON(new Position(2713, 9564, 0), ""), TAVERLY_DUNGEON(new Position(2884, 9797, 0), ""),
	GODWARS_DUNGEON(new Position(2871, 5318, 2), ""), STRYKEWYRM_CAVERN(new Position(2731, 5095, 0), ""),
	ANCIENT_CAVERN(new Position(1746, 5325, 0), ""), CHAOS_TUNNELS(new Position(3184, 5471, 0), ""),

	// MODERN BOSSES
	GWD(new Position(2527, 2652, 0), "Welcome - to the God Wars platform!."), DAGKINGS(new Position(1908, 4367, 0), ""),
	FROSTDRAGONSWILDY(new Position(2961, 3882, 0), ""), TORMENTEDDEMONS(new Position(2717, 9805, 0), ""),
	KBD(new Position(2273, 4681, 0), ""), CHAOSELE(new Position(3281, 3914, 0), ""),
	SLASHBASH(new Position(2547, 9448, 0), ""), KQ(new Position(3476, 9502, 0), ""),
	PHOENIX(new Position(2839, 9557, 0), ""), BANDOSAVATAR(new Position(2368, 4949, 0), ""),
	GLACORS(new Position(3050, 9573, 0), ""), CORPBEAST(new Position(2900, 4384, 0), ""),
	NEX(new Position(2852, 5207, 0), ""), // 2852, 5207
	// OLDSCHOOL BOSSES
	CALLISTO(new Position(3163, 3796, 0), ""), VETION(new Position(3009, 3767, 0), ""),
	VENENATIS(new Position(3005, 3732, 0), ""), ZULRAH(new Position(3406, 2794, 0), ""),
	KRAKEN(new Position(3683, 9888, 0), ""), SCORPIA(new Position(2849, 9640, 0), ""),

	EXODEN(new Position(2581,4510 , 0), ""),
	INFERNAL_DEMON(new Position(2323, 9624, 0), ""),

	// MINIGAME TELEPORTS
	EVENT(new Position(2732, 3465, 0), "Kill boxes for points."),
	PENGZONE(new Position(3039, 9545, 0), "Kill penguins for pebbles. higher multiplier more pebbles."),
	CUSTOMZONE(new Position(2577, 2565, 0), "Get key 1 and 2 to enter the next room. keep going till you kill boss."),
	WARRIORSGUILD(new Position(2855, 3543, 0), ""), PESTCONTROL(new Position(2663, 2656, 0), ""),
	DUELARENA(new Position(3372, 3269, 0), ""), BARROWS(new Position(3565, 3313, 0), ""),
	FIGHTCAVE(new Position(2441, 5173, 0), ""), FIGHTPIT(new Position(2399, 5177, 0), ""),
	GRAVEYARD(new Position(3503, 3562, 0), ""), NOMAD(new Position(1890, 3176, 0), ""),

	// WILDERNESS TELEPORTS
	EDGEVILLEDITCH(new Position(3087, 3517, 0), ""), MAGEBANK_WILDY(new Position(3090, 3956, 0), ""),
	MAGEBANK_SAFE(new Position(2539, 4712, 0), ""), EDGEWESTDRAGONS(new Position(2981, 3597, 0), ""),
	CHAOSALTAR(new Position(3241, 3620, 0), ""), EDGEEASTDRAGONS(new Position(3331, 3661, 0), ""),
	GHOSTTOWN(new Position(3651, 3486, 0), ""), DEMONIC_RUINS(new Position(3287, 3888, 0), ""),

	// COMMANDS
	DZONE(new Position(2851, 3348, 0), ""), SHOPS(new Position(3690, 2977, 0), ""),
	TRADE(new Position(3164, 3485, 0), ""),
	CHILL(new Position(3812, 2837, 0), "Ohhhh I'm on a resort. Finally a real vacation!"),
//CUSTOM BOSSES
	GALVEK(new Position(3348, 3345, 0), ""),

	;

	private Position pos;
	private String hint;

	private TeleportLocations(Position pos, String hint) {
		this.pos = pos;
		this.hint = hint;
	}

	public Position getPos() {
		return this.pos;
	}

	public String getHint() {
		return this.hint;
	}

}
