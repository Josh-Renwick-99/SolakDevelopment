package com.ruse.world.content.seasonpass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.ruse.model.Item;

public class PassRewards {

	public static final String INFO_FILE_PATH = "./data/saves/seasonpass/info.txt";
	public static final String REWARDS_FILE_PATH = "./data/saves/seasonpass/memberRewards.txt";
	public static final String DEFAULT_REWARDS_FILE_PATH = "./data/saves/seasonpass/defaultRewards.txt";
	public static Item[] defaultRewards;
	public static Item[] memberRewards;
	public static int SEASON;
	public static int SEASONEND;

	public static void init() {
		loadInformation();
		loadDefaultRewards();
		loadMemberRewards();
	}

	public static void loadInformation() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(INFO_FILE_PATH));

			SEASON = Integer.parseInt(r.readLine());
			SEASONEND = Integer.parseInt(r.readLine());

			r.close();
		} catch (IOException e) {
			System.err.println("Did not load '"+ INFO_FILE_PATH +"'");
		}
	}

	public static void loadDefaultRewards() {
		defaultRewards = new Item[50];
		try {
			BufferedReader r = new BufferedReader(new FileReader(DEFAULT_REWARDS_FILE_PATH));
			int index = 0;
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				} else {
					line = line.trim();
				}
				String[] code = line.split(" : ");

				defaultRewards[index] = new Item(Integer.parseInt(code[0]), Integer.parseInt(code[1]));
				index++;
			}
			r.close();
		} catch (IOException e) {
			System.err.println("Did not load '"+DEFAULT_REWARDS_FILE_PATH+"'");
		}
	}

	public static void loadMemberRewards() {
		memberRewards = new Item[50];
		try {
			BufferedReader r = new BufferedReader(new FileReader(REWARDS_FILE_PATH));
			int index = 0;
			while (true) {
				String line = r.readLine();
				if (line == null) {
					break;
				} else {
					line = line.trim();
				}
				String[] code = line.split(" : ");

				memberRewards[index] = new Item(Integer.parseInt(code[0]), Integer.parseInt(code[1]));
				index++;
			}
			r.close();
		} catch (IOException e) {
			System.err.println("Did not load '"+ REWARDS_FILE_PATH +"'.");
		}
	}

}
