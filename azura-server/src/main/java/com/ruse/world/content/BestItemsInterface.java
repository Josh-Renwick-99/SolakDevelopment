package com.ruse.world.content;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BestItemsInterface {
    private static String[] text = new String[]{"Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush",
            "Magic", "Range", "Strength", "Ranged Str", "Magic Damage"};
    public static List<Integer> itemsToIgnore = new ArrayList() {
        {
            add(5424);
            add(9253);
            add(22071);
            add(22076);
            add(21604);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);
            add(17694);


            add(17694);
            add(17696);
            add(21603);
            add(5423);
            add(5432);
            add(7014);
            add(19800);
            add(19802);
            add(20060);
            add(20062);
            add(20063);
            add(20073);
            add(7642);
            add(17678);
            add(7643);
            add(20533);
            add(20551);
            add(20552);
            add(20558);
            add(11179);
            add(11181);
            add(11182);
            add(11183);
            add(11184);
            add(11759);
            add(11762);
            add(5594);

            add(12285);

            add(21780);
            add(21605);
            add(21602);
            add(21601);
            add(21600);
            add(22093);
            add(21606);
            add(15219);
            add(1485);
            add(22073);
            add(14055);
            add(14054);
            add(14053);
            add(14052);
            add(14051);
            add(14050);


            add(17700);//Wrath Hammer Offhand
            add(18881);//Supreme Boots
            add(18883);//Supreme gloves
            add(19810);//Supreme spirit shield
            add(22118);//Group Ironman's Aura
            add(22119);//Ultimate Ironman's Aura
            add(22120);//Ironman's Aura
            add(21058);//dualsaber
            add(11320);//Max melee helm
            add(11321);//Max melee body
            add(11322);//Max melee legs
            add(3720);//Scarlet helm
            add(3721);//Scarlet body
            add(3722);//Scarlet legs
            add(4018);//Extreme Offhand
            add(15115);//Extreme helm
            add(15116);//Extreme body
            add(15117);//Extreme legs
            add(15118);//Extreme gloves
            add(15119);//Extreme boots
            add(21607);//Custom Extreme helm
            add(21608);//Custom Extreme body
            add(21609);//Custom Extreme legs
            add(21610);//Custom Extreme Teddy bear
            add(21611);//Custom Extreme gloves
            add(21612);//Custom Extreme boots
            add(734);//Demon big maul
            add(17708);//Joyxox Sword
            add(7543);//Perfect cell Rifle
            add(7544);//Perfect Cell Teddybear
            add(7545);//Perfect Cell apparatus
            add(9478);//Super buu helm
            add(9479);//Super 	 body
            add(9480);//Super buu legs
            add(9481);//Perfect cell helm
            add(9482);//Perfect cell body
            add(9483);//Perfect cell legs
            add(11763);//Freiza helm
            add(11764);//Freiza body
            add(11765);//Freiza legs
            add(11766);//Freiza gloves
            add(11767);//Freiza boots
            add(13323);//Goku head
            add(13324);//Goku body
            add(13325);//Goku legs
            add(14066);//Crystalized wings
            add(15278);//Super Buu Pet
            add(15832);//Super buu ward
            add(16265);//Super buu boots
            add(17702);//Freiza claws
            add(18838);//Dollar Chain
            add(22079);//ankoue maul
            add(21036);//Exorcism helm
            add(21038);//Exorcism legs
            add(523);//Saggy helm
            add(524);//Saggy body
            add(525);//Saggy legs
            add(5420);//Madman helm
            add(5422);//Madman body
            add(5428);//Madman legs
            add(14068);//Garfield mask
            add(14069);//Garfield body
            add(14070);//Garfield legs
            add(18751);//Corrupt Maxiblood legs
            add(18752);//Corrupt Maxiblood platebody
            add(18753);//Corrupt Maxiblood helm
            add(22075);//Starlight sword
            add(15449);//Extreme Aura
            add(15450);//Custom Extreme Aura
            add(22080);//Heavy chainsaw
            add(15230);//Zeus full helm
            add(15231);//Zeus body
            add(15232);//Zeus legs
            add(15234);//Zeus shield
            add(15235);//Electric gloves
            add(15236);//Electric boots
            add(17662);//zeus flagcape
            add(19984);//LegendarySlayer helm
            add(19985);//LegendarySlayer body


            add(19986);//LegendarySlayer legs
            add(22078);//Demon maul
            add(13023);//Grinch legs
            add(13025);//Grinch head
            add(13027);//Grinch body
            add(26);//Sassy Aura
            add(522);//Saggy's broomstick
            add(12608);//SS Aura
            add(21031);//Hades full helm
            add(21032);//Hades body
            add(21033);//Hades legs
            add(21035);//Hades shield
            add(17718);//Batman's bow
            add(17734);//GreatRealm Axe
            add(9250);//Yogifus helm
            add(9251);//Yogifus body
            add(9252);//Yogifus legs
            add(13300);//Satanic full helm
            add(13301);//Satanic hell body
            add(13304);//Satanic hell legs
            add(13306);//Satanic anti-hellshield
            add(19165);//Darthvader legs
            add(19166);//Darthvader body
            add(19468);//Darthvader mask
            add(19994);//LegendarySlayer cape
            add(21050);//Slayermaster hood
            add(21051);//Slayermaster body
            add(21052);//Slayermaster legs
            add(21062);//Ruthless helm
            add(21063);//Ruthless body
            add(21064);//Ruthless legs
            add(19940);//Maxiblood legs
            add(19941);//Maxiblood platebody
            add(19943);//Maxiblood helm
            add(20790);//Golden mining trousers
            add(20791);//Golden mining top
            add(20792);//Golden mining helmet
            add(9054);//Creeper helm
            add(9055);//Creeper body
            add(9056);//Creeper legs
            add(9057);//Creeper gloves
            add(9058);//Creeper boots
            add(11303);//Defender kiteshield
            add(11305);//Earthquake full helm
            add(11306);//Earthquake platebody
            add(11307);//Earthquake platelegs
            add(11308);//Earthquake battlesword
            add(14018);//Tornado Katana
            add(20555);//Scythe of vitur
            add(23097);//Warrior gloves
            add(676);//Lord longsword
            add(17726);//COL sword II
            add(21071);//Ruthless cape
            add(17724);//COL sword I
            add(13931);//Gilded spear
            add(8857);//Nebula helm
            add(8858);//Nebula body
            add(8859);//Nebula legs
            add(9254);//Madman helm
            add(9255);//Madman platebody
            add(9256);//Madman legs
            add(17714);//Lucien Axe
            add(22081);//Wooden chainsaw
            add(13925);//Gilded longsword
            add(14056);//Alex Solak bow


            add(16045);//Frozen rapier
            add(19987);//L.S spirit shield
            add(19988);//LegendarySlayer gloves
            add(19989);//LegendarySlayer boots
            add(22004);//Boss Slayer Helmet [5]
            add(4555);//Riddler's helm
            add(4556);//Riddler's body
            add(4557);//Riddler's legs
            add(8940);//Bulwark helm
            add(8941);//Bulwark body
            add(8942);//Bulwark legs
            add(20559);//Rabies spreader (offhand)
            add(19957);//Tetsu legs
            add(19958);//Tetsu body
            add(19959);//Tetsu helm
            add(22070);//Shadowspike long
            add(4558);//Riddler's Offhand
            add(8834);//Predator Reaper
            add(8835);//Predator Offhand
            add(11798);//Dinh's bulwark
            add(13760);//Elder Maul
            add(18823);//Collector ring II
            add(19888);//Collector necklace II
            add(20086);//Cursed helm
            add(20087);//Cursed body
            add(20088);//Cursed legs
            add(20089);//Cursed boots
            add(20090);//Cursed spirit shield
            add(20091);//Cursed gloves
            add(20092);//Cursed amulet
            add(20093);//Cursed ring
            add(20099);//Cursed cape
            add(21025);//Samurai head
            add(21026);//Samurai body
            add(21027);//Samurai legs
            add(21028);//eSamurai head
            add(21029);//eSamurai body
            add(21030);//eSamurai legs
            add(19919);//Arc rapier
            add(21057);//lightsaber
            add(22077);//Brutal Tentacle
            add(13326);//Goku gloves
            add(13327);//Goku boots
            add(8816);//Bulwark helm
            add(8817);//Bulwark body
            add(8818);//Bulwark legs
            add(15877);//Nuetron Dagger
            add(13302);//Satanic gloves
            add(13305);//Satanic boots
            add(14071);//Garfield gloves
            add(14072);//Garfield boots
            add(15511);//Royal amulet



            add(15818);//Lucien Defender
            add(15924);//Lucien helm
            add(15935);//Lucien legs
            add(16023);//Lucien platebody
            add(17686);//Lucien Crypt wings
            add(18416);//Water Partyhat
            add(18417);//Firehell Partyhat
            add(18418);//Velvet Partyhat
            add(18419);//Universal Partyhat
            add(18683);//Satanic tokhaar-kal
            add(18817);//Ring of sauron (hell)
            add(22003);//Boss Slayer Helmet [4]
            add(17624);//Shadow whip
            add(17730);//COL shield II
            add(14305);//Dagger (class 5)
            add(8329);//Art's Defender
            add(9257);//Epic Defender
            add(1486);//Creeper Cape
            add(2021);//Angelic wings
            add(15593);//Angelic platelegs
            add(16140);//Angelic amulet
            add(17638);//Angelic helm
            add(17640);//Angelic body
            add(17684);//Wyvern Cape
            add(19992);//L.S ring
            add(21065);//Ruthless spirit shield
            add(21066);//Ruthless gloves
            add(21067);//Ruthless boots
            add(21068);//Ruthless amulet
            add(21069);//Ruthless ring
            add(22002);//Boss Slayer Helmet [3]
            add(14024);//Drygore Rapier
            add(14303);//Dagger (class 4)
            add(13905);//Vesta's spear
            add(13907);//Vesta's spear (deg)
            add(13929);//Corrupt vesta's spear
            add(16258);//Primal spear (b)
            add(16259);//Primal spear (p) (b)
            add(16260);//Primal spear (p+) (b)
            add(16261);//Primal spear (p++) (b)
            add(17143);//Primal spear
            add(17145);//Primal spear (p)
            add(17147);//Primal spear (p+)
            add(17149);//Primal spear (p++)
            add(18790);//Tsutsaroth spear
            add(17728);//COL shield I
            add(17999);//COL Torva helm

        }
    };

    public static boolean buttonClicked(Player player, int buttonId) {
        if (buttonId >= 100010 && buttonId <= 100014) {
            int index = (buttonId - 100010);
            openInterface(player, index);
            return true;
        } else if (buttonId >= 100016 && buttonId <= 100020) {
            int index = (buttonId - 100016) + 5;
            openInterface(player, index);
            return true;
        } else if (buttonId == 100022) {
            openInterface(player, 14);
            return true;
        } else if (buttonId == 100023) {
            openInterface(player, 15);
            return true;
        } else if (buttonId == 100024) {
            openInterface(player, 17);
            return true;
        }
        return false;
    }

    public static void openInterface(Player player, int bonus) {

        for (int i = 0; i < 5; i++) {
            player.getPacketSender().sendString(100010 + i, (bonus == i ? "@whi@Check " : "Check ") + text[i]);
        }
        for (int i = 5; i < 10; i++) {
            player.getPacketSender().sendString(100011 + i, (bonus == i ? "@whi@Check " : "Check ") + text[i]);
        }
        player.getPacketSender().sendString(100022, (bonus == 14 ? "@whi@Check " : "Check ") + text[10]);
        player.getPacketSender().sendString(100023, (bonus == 15 ? "@whi@Check " : "Check ") + text[11]);
        player.getPacketSender().sendString(100024, (bonus == 17 ? "@whi@Check " : "Check ") + text[12]);

        ArrayList<ItemDefinition> objects = new ArrayList<ItemDefinition>();

        for (ItemDefinition i : ItemDefinition.getDefinitions()) {
            if (i != null) {
                if (!i.isNoted() && i.getBonus()[bonus] > 0 && !itemsToIgnore.contains(i.getId()))// && i.getEquipmentSlot() ==
                    // Equipment.BODY_SLOT)
                    objects.add(i);
            }
        }

        Collections.sort(objects, new Comparator<ItemDefinition>() {
            @Override
            public int compare(ItemDefinition p, ItemDefinition p1) {
                if (p.getBonus()[bonus] == p1.getBonus()[bonus]) {
                    return 0;
                } else if (p.getBonus()[bonus] > p1.getBonus()[bonus]) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        int interId = 100102;
        int size = (objects.size() >= 100 ? 100 : objects.size());
        for (int i = 0; i < size; i++) {
            player.getPacketSender().sendString(interId++,
                    "" + objects.get(i).getBonus()[bonus]);
            player.getPacketSender().sendString(interId++, "" + objects.get(i).getName());
            player.getPacketSender().sendItemOnInterface(interId++, objects.get(i).getId(), 1);

            System.out.println("add(" + objects.get(i).getId() + ");//" + objects.get(i).getName());
            interId++;
        }
        player.getPacketSender().sendInterface(100000);

        player.getPacketSender().setScrollBar(100050, size * 40);

    }

}