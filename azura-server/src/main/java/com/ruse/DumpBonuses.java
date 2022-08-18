package com.ruse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.ItemDefinition.EquipmentType;

public class DumpBonuses {

	// Run this code to redump the item bonuses for knd6060's client sided
	// ctrl-hover item feature.
	// It dumps Itemdefs.txt file in the server source folder. Put that into the
	// cache and replace existing one.
	public static void main(String[] args) {
		new DumpBonuses().run2();
	}// ok perfect do i need to upload that in cache as wel? Yes defo, otherwise
		// client will freeze/crash, also you can change color

	public void run2() {
		try {
			ItemDefinition.init();

			File f = new File("ItemDefs.txt");
			FileWriter fw = new FileWriter(f);

			fw.append(ItemDefinition.getMaxAmountOfItems() + "\n");

			for (ItemDefinition item : ItemDefinition.getDefinitions()) {
				if (item == null)
					continue;
				// System.out.println("dumping " + item.getId());

				int[] intbonus = new int[14]; // 5 att, 5 def, 1 pray, 1 str, 1 ranged str, 1 magic dmg
				int index = 0;
				for (int i = 0; i < item.getBonus().length; i++) {
					if (i >= 10 && i <= 13) {
						continue;
					}
					int value = (int) item.getBonus()[i];
					if (value > 30000)
						value = 30000;
					intbonus[index] = value;
					index++;
				}

				if (item.getEquipmentType() == EquipmentType.WEAPON && !item.isWeapon()) {
					continue;
				}

				fw.append(item.getId() + "\n");
				for (int bonusNumber : intbonus) {
					fw.append(bonusNumber + "\n");
				}
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
