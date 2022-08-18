package com.ruse.world.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.ruse.model.CombatIcon;

/**
 *
 * @author Suic
 */

public class AOESystem {

	private List<AOEWeaponData> weaponData = new ArrayList<>();

	public void parseData() {

		Path filePath = Paths.get("data", "AOEWeapons.txt");

		try (Stream<String> lines = Files.lines(filePath)) {
			lines.forEach(line -> {
				String[] wepData = line.split(" ");
				weaponData.add(new AOEWeaponData(parseInt(wepData[0]), parseInt(wepData[1]), parseInt(wepData[2]),
						parseInt(wepData[3]), getIcon(wepData[4])));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public AOEWeaponData getAOEData(int id) {

		int index = -1;

		for (int i = 0; i < weaponData.size(); i++) {
			if (weaponData.get(i).getId() == id) {
				index = i;
				break;
			}
		}

		return index > -1 ? weaponData.get(index) : null;
	}

	private CombatIcon getIcon(String str) {
		CombatIcon icon = null;

		if (str.equalsIgnoreCase("Range")) {
			icon = CombatIcon.RANGED;
		} else if (str.equalsIgnoreCase("Melee")) {
			icon = CombatIcon.MELEE;
		} else {
			icon = CombatIcon.MAGIC;
		}

		return icon;
	}

	private int parseInt(String str) {
		return Integer.parseInt(str);
	}

	private static AOESystem SINGLETON = null;

	public static AOESystem getSingleton() {
		if (SINGLETON == null) {
			SINGLETON = new AOESystem();
		}
		return SINGLETON;
	}

}
