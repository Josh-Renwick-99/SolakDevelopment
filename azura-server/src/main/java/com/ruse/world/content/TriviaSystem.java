package com.ruse.world.content;

import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

import java.util.Random;

public class TriviaSystem {

	enum TriviaData {
		QUESTION_1("What is 10000 x 1", "10000"), 
		QUESTION_3("In what location can you find Swoodowoodo", "Ezonenpc"), 
		QUESTION_5("In what minigame do you get a boss room key", "Key minigame"), 
		QUESTION_9("What is the highest zone availble", "groudon"), 
		QUESTION_11("What is the main currency on Solak", "Solak coin"),
		QUESTION_12("Re-arrange these letters to make a npc.  'xytraniC'", "Cyantrix"), 
		QUESTION_13("Re-arrange these letters to make a npc.  'stvaneneR'", "Revenants"), 
		QUESTION_14("Re-arrange these letters to make a city.  'rorckVa'", "Varrock"), 
		QUESTION_15("How many member points does a owner cape goodiebag cost in webstore", "50"), 
		QUESTION_16("What drop rate bonus does cursed armour give individually", "10%"), 
		QUESTION_17("What drop rate bonus does Lava ring give you", "25%"), 
		QUESTION_21("Which barrows brother hits through prayer?", "Verac"), 
		QUESTION_22("What skill allows you to make bowstring?", "Crafting"), 
		QUESTION_23("What slayer level is required to kill jellies?", "52"), 
		QUESTION_24("What is the name of the magician in the barrow brothers", "Ahrim"), 
		QUESTION_26("Who can you reclaim a lost pet from", "Zookeeper"), 
		QUESTION_27("What is the name of the final boss in Raids [1]", "Olm boss"), 
		QUESTION_28("How much do you have to donate to use the command ::bank", "$50"), 
		QUESTION_30("What is the name of the pet that gives you x2 npc killcount", "Goku"), 
		QUESTION_32("Which boss is summoned by AFK thieving", "Garfield"), 
		QUESTION_33("Which boss is summoned by NPC kill count", "Goku"), 
		QUESTION_34("How long does well of goodwill last", "2 hours"), 
		QUESTION_36("What is the lightest chemical element", "Hydrogen"), 
		QUESTION_37("Which planet is nearest the sun", "Mercury"), 
		QUESTION_38("Which band had a number 1 hit with 'Barbie Girl'", "Aqua"), 
		QUESTION_39("Which dinosaur was larger than the tyrannosaurus", "Spinosaurus"), 
		QUESTION_40("The name of which dinosaur means 'triple horned'", "Triceratops"), 
		QUESTION_43("What is the upgraded ring after lava ring", "Art's ring"), 
		QUESTION_48("During what age did the barrows brother rise to power", "4th age"), 
		QUESTION_49("What is the name of the new skill in game", "Dungeoneering"),
		QUESTION_51("What killcount is required to start the npclist progression", "50"), 
		QUESTION_54("What is the droprate bonus for the first droprate pet", "3%"), 
		QUESTION_56("What device will loot coins, pvm tickets, and coins", "Loot device"), 
		QUESTION_59("What exp lamp gives you the most experience", "Zara"), 
		QUESTION_60("What do you use to upgrade your pet", "Pvm tickets"), 
		QUESTION_61("How long does the exp bonus skill last", "6 hours"), 
		QUESTION_62("What does skoll staff upgrade to", "Brutal staff"), 
		QUESTION_63("What does pernix T2 upgrade to", "Sorrow"), 
		QUESTION_64("Unscramble these letters 'nigw steikhleid'", "Wing kiteshield"), 
		QUESTION_65("Unscramble these letters 'aevyh niasawch'", "Heavy chainsaw"), 
		QUESTION_66("Type this : 'hy3gjkyuhfjfrq01fi'", "hy3gjkyuhfjfrq01fi"), 
		QUESTION_67("What dungeoneering level do you need for max tier?", "90"),
		QUESTION_68("Type the following : '21042919422'", "21042919422"), 
		QUESTION_69("Type the following : 'ximf2mc292m92'", "ximf2mc292m92"), 
		QUESTION_70("Type the following : '0k0k02k02kd2d'", "0k0k02k02kd2d"), 
		QUESTION_71("Type the following : 'v2img903m'", "v2img903m"), 
		QUESTION_72("Type the following : 'omv039f290k'", "omv039f290k"), 
		QUESTION_73("Type the following : 'foem30ffo3mfo2'", "foem30ffo3mfo2"), 
		QUESTION_74("Type the following : 'zomofo2mf20mf'", "zomofo2mf20mf"), 
		QUESTION_75("Type the following : 'pp20d20d2l02'", "pp20d20d2l02"), 
		QUESTION_76("Type the following : 'ao10fm30mg02'", "ao10fm30mg02"), 
		QUESTION_77("Type the following : 'omv2mf029fm290f2'", "omv2mf029fm290f2"), 
		QUESTION_78("What lets you go to a boss by your self? ", "Instance token"), 
		QUESTION_79("What is 5 x 5?", "25"), 
		QUESTION_80("What is the seventh planet from the sun", "uranus"), 
		QUESTION_81("Name the worlds largest ocean", "pacific ocean"), 
		QUESTION_82("What is 10000 x 0", "0"), 
		QUESTION_84("What npc can you buy potions from", "Healer"), 
		QUESTION_85("What altar is used to change your prayer book", "Chaos altar"), 
		QUESTION_86("When is double exp active?", "Weekend"), 
		QUESTION_87("What does inferno wings upgrade into?", "Solak wings"),
		QUESTION_88("What defence level is required to wear barrows", "70"), 
		QUESTION_89("What is the name of the server", "Solak"),
		QUESTION_90("What skill lets you make potions", "Herblore"), 
		QUESTION_91("What skill lets you make weapons and armour", "Smithing"), 
		QUESTION_92("Where do you store all of your items", "Bank"), 
		QUESTION_93("What points do you get for killing bosses", "Boss points"), 
		QUESTION_95("What should I do every day to help the server", "Vote"), 
		QUESTION_96("What skill do I use when crafting runes", "Runecrafting"), 
		QUESTION_97("What is the cube root of 216", "6"), 
		QUESTION_98("What summoning level is required to make an Iron Minotaur", "46"), 
		QUESTION_99("What summoning level is required to make an Moss Titan", "79"), 
		
		QUESTION_100("Guess a number 1-10", "1"), 
		QUESTION_101("Guess a number 1-10", "2"), 
		QUESTION_102("Guess a number 1-10", "3"), 
		QUESTION_103("Guess a number 1-10", "4"), 
		QUESTION_104("Guess a number 1-10", "5"), 
		QUESTION_105("Guess a number 1-10", "6"), 
		QUESTION_106("Guess a number 1-10", "7"), 
		QUESTION_107("Guess a number 1-10", "8"), 
		QUESTION_108("Guess a number 1-10", "9"), 
		LAST_QUESTION("Guess a number 1-10", "10");
																														// those
																														// are
																														// good
																														// enough
																														// for
																														// testing

		TriviaData(String question, String answer) {
			this.question = question;
			this.answer = answer;
		}

		private String question, answer;

		public String getQuestion() {
			return question;
		}

		public String getAnswer() {
			return answer;
		}
	}

	private static int timer = 1000; // 20minutes
	private static boolean active = false;
	private static TriviaData currentQuestion = null;
	
	public static String getCurrentQuestion() {
		return currentQuestion == null ? "None" : currentQuestion.getQuestion().toUpperCase().substring(0, 1) + currentQuestion.getQuestion().toLowerCase().substring(1);
	}

	public static void tick() {

		if (!active) {
			if (timer < 1) {
				startTrivia();
				timer = 2000;
			} else {
				timer--;
			}
		}
	}

	private static final TriviaData[] TRIVIA_DATA = TriviaData.values();

	private static void startTrivia() {
		setAndAskQuestion();
	}

	private static void setAndAskQuestion() {
		active = true;
		currentQuestion = TRIVIA_DATA[new Random().nextInt(TRIVIA_DATA.length)];
		World.sendMessage("<img=1396>@red@[TRIVIA]<img=1396> @red@" + currentQuestion.getQuestion() + "");
		World.getPlayers().forEach(PlayerPanel::refreshPanel);
	}
	
	public static void answer(Player player, String answer) {
		if(!active) {
			player.sendMessage("@red@There is no trivia going on at the moment");
			return;
		}
		if(answer.equalsIgnoreCase(currentQuestion.getAnswer())) {
			player.getInventory().add(6833, 1);
			active = false;
			World.sendMessage("<img=1396>@red@[TRIVIA]<img=1396> @blu@" + player.getUsername() + "@bla@ has recieved a @red@Goodiebag @bla@from Trivia");
			World.sendMessage("<img=1396>@red@[TRIVIA]<img=1396> @bla@ The answer for the trivia to the question was @red@" + currentQuestion.answer);
			currentQuestion = null;
			World.getPlayers().forEach(PlayerPanel::refreshPanel);//soz ok is there anything else u need or is that all
			player.sendMessage("@bla@congrats, you've guessed correctly and received a@blu@ Goodie bag@bla@!");
			
		} else {
			player.sendMessage("@bla@Incorrect answer - your answer was: @red@" + answer);
		}
	}

}
