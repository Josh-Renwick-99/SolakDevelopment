package com.ruse.world.content.priceGuide;

import java.util.HashMap;
import java.util.Map;

import com.ruse.model.Position;
import com.ruse.GameSettings;

/**
 * 
 * @author Adam_#6723
 *
 */

public enum PriceGuideData {

	Glaive(20577, "Nex Glaive", "30B"), Torvafullhelm1(6927, "Torva full helm", "500m"),
	Torvaplatebody2(6928, "Torva platebody", "650m"), Torvaplatelegs3(6929, "Torva platelegs", "600m"),
	Pernixcowl4(6930, "Pernix cowl", "500m"), Pernixbody5(6931, "pernix body", "650m"),
	Pernixlegs(6932, "pernix legs", "600m"), VirtusMask(6933, "virtus mask", "500m"),
	VirtusRobetop(6934, "virtus robe top", "650m"), Virtusrobebottoms(6935, "virtus robe bottoms", "600m"),
	Torvafullhelm(14008, "torva full helm", "100m"), Torvaplatebody(14009, "torva platebody", "150m"),
	Torvaplatelegs(14010, "torva platebody", "120m"), Pernixcowl6(14011, "pernix cowl", "100m"),
	Pernixbody7(14012, "pernix body", "150m"), Pernixchaps(14013, "pernix chaps", "120m"),

	// ETERNAL DRAGON!

	Eternalfullhelm(19901, "eternal full helm", "300m"), Eternalplatebody(19902, "eternal platebody", "500m"),
	Eternalplatelegs(19903, "eternal platelegs", "400m"), Eternalcape(19909, "eternal cape", "750m"),
	Eternalwhip(20534, "eternal whip", "600m"), Eternalboots(19905, "eternal boots", "200m"),
	Chaosshield(20439, "chaos shield", "200m"), EternalGloves(19904, "eternal gloves", "200m"),

	// SHADOW CORP!

	Shadowsigil(18784, "shadow sigil", "4b"), Hellfiresigil(18783, "hellfire sigil", "4b"),
	Elysiansigil(13751, "elysiansigil", "1.5b"), Divinesigil(13749, "divine sigil", "2b"),
	Arcanesigil(13747, "arcane sigil", "1.5b"), Spectralsigil(13753, "spectral sigil", "1b"),

	Tormentedtentacle(18686, "tormented tentacle", "100m"), Gildedhelmet(13996, "gilded helmet", "100m"),
	Gildedchainbody(13913, "gilded chainbody", "200m"), Gildedplateskirt(13919, "gilded plateskirt", "150m"),
	Kreearracrossbow(18799, "kree'arra crossbow", "100m"), Kreearrahelmet(18834, "kree'arra helmet", "100m"),
	Kreearrachestplate(18801, "kreearra chestplate", "200m"), Kreearrachainskirt(18800, "kree'arra chainskirt", "150m"),
	Skollstaff(5095, "skoll staff", "200m"), Magebeasthelm(19140, "magebeast helm", "200m"),
	Magebeastbody(19139, "magebeast body", "300m"), Magebeastlegs(19138, "magebeast legs", "250m"),
	Devilspawnwings(4411, "devilspawn wings", "300m"), Devilspawnnecklace(19887, "devilspawn necklace", "300m"),
	Demonsmaul(22078, "demons maul", "200m"), Icefury(15418, "ice fury", "300m"),
	Vixieboots(6937, "vixie boots", "400m"), Bossgloves(3905, "boss gloves", "500m"),

	// PREDATOR!

	Predatorfullhelm(8860, "predator full helm", "700m"), Predatorplatebody(8861, "predator platebody", "850m"),
	Predatorplatelegs(8862, "predator platelegs", "800m"), Predatorboots(15830, "predator boots", "200m"),
	Predatoroffhand(8835, "predator off-hand", "3b"),

	// BULWARK!

	Bulwarkhelmet(8940, "bulwark helmet", "3.5b"), Bulwarkplatebody(8817, "bulwark platebody", "5b"),
	Bulwarkplatelegs(8818, "bulwark legs", "4b"), Tornadokatana(14018, "tornado katana", "1b"),
	Viggoraschainmace(20544, "viggoras chainmace", "2b"),;

	// MISCELLANEOUS!

	PriceGuideData(int itemid, String itemName, String price) {
		this.itemid = itemid;
		this.itemName = itemName;
		this.price = price;
	}

	private int itemid;

	public int getItemid() {
		return itemid;
	}

	private String itemName, price;

	public String getItemName() {
		return itemName;
	}

	public String getPrice() {
		return price;
	}

}
