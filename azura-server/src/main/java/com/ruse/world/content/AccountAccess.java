package com.ruse.world.content;

import com.google.gson.*;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class AccountAccess {
	
	//	public static void main(String[] params) {
	//
	//		//PASSWORDS
	//
	//
	//		HashMap<String, String> itemss = AccountAccess.searchAccountsPasswords();
	//
	//		for (String d : itemss.keySet()) {
	//			try {
	//				FileWriter fw = new FileWriter("./Server/data/saves/passes" + ".txt", true);
	//				if (fw != null) {
	//					if (itemss.get(d) != null) {
	//						fw.write("Name: " + d + ", Password: " + itemss.get(d).toString());
	//						fw.write(System.lineSeparator());
	//					}
	//					fw.close();
	//				}
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	
	//	public static void main(String[] params) {
	//
	//		// ACC VALUES
	//
	//		HashMap<String, Long> objects1 = AccountAccess.searchAccounts();
	//		HashMap<String, Long> objects = new HashMap<String, Long>();
	//
	//		for (String d : objects1.keySet()) {
	//			if (!objects.containsValue(objects1.get(d)))
	//				objects.put(d, objects1.get(d));
	//		}
	//
	//		List<Long> wealths = new ArrayList<Long>(objects.values());
	//
	//		Collections.sort(wealths, new Comparator<Long>() {
	//
	//			public int compare(Long o1, Long o2) {
	//				return (int) (o2 - o1);
	//			}
	//		});
	//
	//		int offset = 0;
	//		for (String d : objects.keySet()) {
	//			final int offset1 = offset;
	//			System.out.println(Misc.insertCommasToNumber(wealths.get(offset1) + "") + " - "
	//					+ Utils.getKeysByValue(objects, wealths.get(offset1)).toString().replace("[", "").replace("]", ""));
	//			offset++;
	//		}
	//
	//	}
	
	//	public static void main(String[] params) {
	//
	//		//MIN ITEM VALUE
	//		HashMap<String, Item> itemss = AccountAccess.searchAccountsItems(1_000_000_000);
	//
	//		for (String d : itemss.keySet()) {
	//			System.out.println(d + " - Name: " + ItemDefinition.forId(itemss.get(d).getId()).getName() + ", Id: "
	//					+ itemss.get(d).getId() + ", Amount: " + Misc.insertCommasToNumber(itemss.get(d).getAmount() + ""));
	//		}
	//	}

	
	public static void check(Player player, int item, int amount) {
		// SEARCH ITEM
		HashMap<String, Item> itemss = AccountAccess.searchAccountsItemId(item);

		int total = 0;
		for(String d : itemss.keySet()) {
			if(itemss.get(d) != null && itemss.get(d).getAmount() >= amount) {
					total += itemss.get(d).getAmount();
				System.out.println(d + " - Name: " + ItemDefinition.forId(itemss.get(d).getId()).getName() + ", Id: " + itemss.get(d).getId() +
						", Amount: " + Misc.insertCommasToNumber(itemss.get(d).getAmount() + ""));
			}
		}
		System.out.println("total: " + total);
		
	}
	
	public static HashMap<String, Long> searchAccounts() {
		HashMap<String, Long> values = new HashMap<String, Long>();
		File[] files = new File("./data/saves/charactersTest/").listFiles();
		for(File file : files) {
			values.put(file.getName().replace(".json", ""), getWealth(file.getName().replace(".json", "")));
		}


		int total = 0;
		for(String d : values.keySet()) {
			if(values.get(d) != null && values.get(d) >= 1) {
				total += values.get(d);
				System.out.println(d + ", Amount: " + Misc.insertCommasToNumber(values.get(d) + ""));
			}
		}
		System.out.println("total: " + total);
		return values;
	}
	
	public static HashMap<String, Item> searchAccountsItems(int amount) {
		HashMap<String, Item> values = new HashMap<String, Item>();
		File[] files = new File("./Server/data/saves/characters/").listFiles();
		for(File file : files) {
			values.put(file.getName().replace(".json", ""), getItems(file.getName().replace(".json", ""), amount));
		}
		return values;
	}
	
	public static HashMap<String, Item> searchAccountsItemId(int id) {
		HashMap<String, Item> values = new HashMap<String, Item>();
		File[] files = new File("./data/saves/characters/characters/").listFiles();
		for(File file : files) {
			//System.out.println("here: "  + file.getName());
			if(getItemId(file.getName().replace(".json", ""), id) != null)
				values.put(file.getName().replace(".json", ""), getItemId(file.getName().replace(".json", ""), id));
		}
		return values;
	}
	
	public static HashMap<String, String> searchAccountsPasswords() {
		HashMap<String, String> values = new HashMap<String, String>();
		File[] files = new File("./Server/data/saves/characters/").listFiles();
		for(File file : files) {
			values.put(file.getName().replace(".json", ""), getPassword(file.getName().replace(".json", "")));
		}
		return values;
	}
	
	public static String getPassword(String name) {
		
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", name + ".json");
		File file = path.toFile();
		
		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if(!file.exists()) {
			return null;
		}
		
		// Now read the properties from the json parser.
		try(FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			Object object = fileParser.parse(fileReader);
			
			if(object instanceof JsonNull) {
				return null;
			}
			
			JsonObject reader = (JsonObject) object;
			
			String pass = null;
			
			if(reader.has("password")) {
				String password = reader.get("password").getAsString();
				pass = password;
			}
			return pass;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static long getWealth(String name) {
		
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/charactersTest/", name + ".json");
		File file = path.toFile();
		
		// If the file doesn't exist, we're logging in for the first
		// time and can skip all of this.
		if(!file.exists()) {
			return -1;
		}
		
		// Now read the properties from the json parser.
		try(FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			Object object = fileParser.parse(fileReader);
			
			if(object instanceof JsonNull) {
				return -1;
			}
			
			JsonObject reader = (JsonObject) object;
			
			long money = 0;
			
			if(reader.has("money-pouch")) {
				money += reader.get("money-pouch").getAsLong() / 1_000_000_000L;
			}
			if(reader.has("inventory")) {
				Item[] items = (builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("equipment")) {
				Item[] items = (builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-0")) {
				Item[] items = (builder.fromJson(reader.get("bank-0").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-1")) {
				Item[] items = (builder.fromJson(reader.get("bank-1").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-2")) {
				Item[] items = (builder.fromJson(reader.get("bank-2").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-3")) {
				Item[] items = (builder.fromJson(reader.get("bank-3").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-4")) {
				Item[] items = (builder.fromJson(reader.get("bank-4").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-5")) {
				Item[] items = (builder.fromJson(reader.get("bank-5").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-6")) {
				Item[] items = (builder.fromJson(reader.get("bank-6").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-7")) {
				Item[] items = (builder.fromJson(reader.get("bank-7").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			if(reader.has("bank-8")) {
				Item[] items = (builder.fromJson(reader.get("bank-8").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == 13664)
						money += item.getAmount();
				}
			}
			return money;
			
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static Item getItems(String name, int amount) {
		
		Path path = Paths.get("./data/saves/characters/", name + ".json");
		File file = path.toFile();
		
		if(!file.exists()) {
			return null;
		}
		
		// Now read the properties from the json parser.
		try(FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			Object object = fileParser.parse(fileReader);
			
			if(object instanceof JsonNull) {
				return null;
			}
			
			JsonObject reader = (JsonObject) object;
			
			if(reader.has("inventory")) {
				Item[] items = (builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("equipment")) {
				Item[] items = (builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			
			if(reader.has("bank-0")) {
				Item[] items = (builder.fromJson(reader.get("bank-0").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-1")) {
				Item[] items = (builder.fromJson(reader.get("bank-1").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-2")) {
				Item[] items = (builder.fromJson(reader.get("bank-2").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-3")) {
				Item[] items = (builder.fromJson(reader.get("bank-3").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-4")) {
				Item[] items = (builder.fromJson(reader.get("bank-4").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-5")) {
				Item[] items = (builder.fromJson(reader.get("bank-5").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-6")) {
				Item[] items = (builder.fromJson(reader.get("bank-6").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-7")) {
				Item[] items = (builder.fromJson(reader.get("bank-7").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			if(reader.has("bank-8")) {
				Item[] items = (builder.fromJson(reader.get("bank-8").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getAmount() * ItemDefinition.forId(item.getId()).getValue() >= amount || item.getAmount() * ItemDefinition.forId(item.getId()).getValue() <= -1)
						return item;
				}
			}
			
			return null;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Item getItemId(String name, int id) {
		
		Path path = Paths.get("./data/saves/characters/characters/", name + ".json");
		File file = path.toFile();
		
		if(!file.exists()) {
			return null;
		}
		
		// Now read the properties from the json parser.
		try(FileReader fileReader = new FileReader(file)) {
			JsonParser fileParser = new JsonParser();
			Gson builder = new GsonBuilder().create();
			Object object = fileParser.parse(fileReader);
			
			if(object instanceof JsonNull) {
				return null;
			}
			
			JsonObject reader = (JsonObject) object;
			
			int amount = 0;
			
			if(reader.has("inventory")) {
				Item[] items = (builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("equipment")) {
				Item[] items = (builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			
			if(reader.has("bank-0")) {
				Item[] items = (builder.fromJson(reader.get("bank-0").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-1")) {
				Item[] items = (builder.fromJson(reader.get("bank-1").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-2")) {
				Item[] items = (builder.fromJson(reader.get("bank-2").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-3")) {
				Item[] items = (builder.fromJson(reader.get("bank-3").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-4")) {
				Item[] items = (builder.fromJson(reader.get("bank-4").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-5")) {
				Item[] items = (builder.fromJson(reader.get("bank-5").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-6")) {
				Item[] items = (builder.fromJson(reader.get("bank-6").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-7")) {
				Item[] items = (builder.fromJson(reader.get("bank-7").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			if(reader.has("bank-8")) {
				Item[] items = (builder.fromJson(reader.get("bank-8").getAsJsonArray(), Item[].class));
				for(Item item : items) {
					if(item.getId() == id)
						amount += item.getAmount();
				}
			}
			
			if(amount >= 1)
				return new Item(id, amount);
			
			return null;
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
