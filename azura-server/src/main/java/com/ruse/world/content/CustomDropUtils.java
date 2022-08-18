package com.ruse.world.content;

import com.ruse.GameSettings;
import com.ruse.model.GameMode;
import com.ruse.model.Locations;
import com.ruse.model.PlayerRights;
import com.ruse.model.container.impl.Equipment;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skill.impl.summoning.BossPets;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;

public class CustomDropUtils {


    /**
     * Increases Drop Rate
     *
     * @param player
     * @return
     */
    public static int drBonus(Player player, int npc) {
        int percentBoost = 0;
        if (player.getEquipment().contains(23044)) { //Tier 1 Aura
            percentBoost += 5;
        }
        if (player.getEquipment().contains(23045)) { //Tier 2 Aura
            percentBoost += 7;
        }
        if (player.getEquipment().contains(23046)) { //Tier 3 Aura
            percentBoost += 10;
        }
        if (player.getEquipment().contains(23047)) { //Tier 4 Aura
            percentBoost += 15;
        }
        if (player.getEquipment().contains(23048)) { //Tier 5 Aura
            percentBoost += 20;
        }
        if (player.getEquipment().contains(23049)) { //Tier 6 Aura
            percentBoost += 20;
        }

        if (player.getEquipment().contains(22100)) {
            percentBoost += 20;
        }

        if (player.getEquipment().contains(23092)
                || player.getEquipment().contains(23093)
                || player.getEquipment().contains(23094)) {// valor rings
            percentBoost += 10;
        }

        if(player.getLocation() == Locations.Location.SAPPHIRE_ZONE
                || player.getLocation() == Locations.Location.EMERALD_ZONE
                || player.getLocation() == Locations.Location.RUBY_ZONE
                || player.getLocation() == Locations.Location.DIAMOND_ZONE
                || player.getLocation() == Locations.Location.ZENYTE_ZONE) {
            percentBoost += 10;

        }
        // creator set:
        if (player.getEquipment().contains(23127))
            percentBoost += 4;
        if (player.getEquipment().contains(23128))
            percentBoost += 4;
        if (player.getEquipment().contains(23129))
            percentBoost += 4;
        if (player.getEquipment().contains(23130))
            percentBoost += 3;
        if (player.getEquipment().contains(23131))
            percentBoost += 3;
        if (player.getEquipment().contains(23132))
            percentBoost += 3;
        if (player.getEquipment().contains(23133))
            percentBoost += 4;
		//

        if (player.isInMinigame()) {
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GREEN_FENRIR_PET.npcId) {
                percentBoost += 10;
            }
        }

        if (npc == player.getSlayer().getSlayerTask().getNpcId()) {
            if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23071) {
                percentBoost += 5;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23069 || player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23070) {
                percentBoost += 7;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23074) {
                percentBoost += 10;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23072) {
                percentBoost += 15;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23073) {
                percentBoost += 20;
            }
        }

        if (!player.isInsideRaids()) {
            /*if (npc == player.getSlayer().getSlayerTask().getNpcId()) {
                if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                        && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.RED_FENRIR_PET.npcId) {
                    percentBoost += 25;
                }
            }*/
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.SKREEG_PET.npcId) {
                percentBoost += 10;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ORIX_PET.npcId) {
                percentBoost += 20;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.CRYSTAL_ORC_PET.npcId) {
                percentBoost += 25;
            }
        } else {
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.DEMON_PET.npcId) {
                percentBoost += 10;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GOLEM_PET.npcId) {
                percentBoost += 15;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.DRAGON_PET.npcId) {
                percentBoost += 25;
            }


        }
        if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ODIN_PET.npcId) {
            percentBoost += 25;
        }

        if (player.getInventory().contains(23174)) {
            percentBoost += 10;
        }

        /**
         * Donator Rank bonusses
         */
        if(player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT || player.getRights().equals(PlayerRights.YOUTUBER)) {
            percentBoost += 25;
        } else if(player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT) {
            percentBoost += 20;
        } else if(player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT) {
            percentBoost += 15;
        } else if(player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT) {
            percentBoost += 10;
        } else if(player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT) {
            percentBoost += 7;
        } else if(player.getAmountDonated() >= Donation.SAPPHIRE_DONATION_AMOUNT) {
            percentBoost += 5;
        }

        if (player.getInventory().contains(4440)) {
            percentBoost *= 1.5;
        }
        if (ServerPerks.getInstance().getActivePerk() == ServerPerks.Perk.DR) {
            percentBoost *= 1.5;
        }
        if (GameSettings.DOUBLEDR == true) {
            percentBoost *= 2;
        }

        if (player.getDoubleDRTimer() > 0) {
            percentBoost += 100;
        }
        if (player.getMinutesVotingDR() > 0) {
            percentBoost += 100;
        }

        if (GameSettings.DOUBLE_DROP) {
            percentBoost += 100;
        }


        if (player.getGameMode() == GameMode.GROUP_IRONMAN) {
            percentBoost += 6;
        }
        if (player.getGameMode() == GameMode.ULTIMATE_IRONMAN || player.getGameMode() == GameMode.IRONMAN) {
            percentBoost += 10;
        }
        if (player.getGameMode() == GameMode.VETERAN_MODE) {
            percentBoost += 15;
        }
        if (PrayerHandler.isActivated(player,PrayerHandler.GNOMES_GREED)) {
            percentBoost += 10;
        }

        return percentBoost;
    }


    public static int getDoubleDropChance(Player player, int npc) {
        int percentBoost = 0;

        Equipment equipment = player.getEquipment();
        if (equipment.contains(23092)
                || equipment.contains(23093)
                || equipment.contains(23094)) {// valor rings
			percentBoost += 10;
        }

        if (player.getEquipment().contains(23044)) { //Tier 1 Aura
            percentBoost += 5;
        }
        if (player.getEquipment().contains(23045)) { //Tier 2 Aura
            percentBoost += 7;
        }
        if (player.getEquipment().contains(23046)) { //Tier 3 Aura
            percentBoost += 10;
        }
        if (player.getEquipment().contains(23047)) { //Tier 4 Aura
            percentBoost += 15;
        }
        if (player.getEquipment().contains(23048)) { //Tier 5 Aura
            percentBoost += 20;
        }
        if (player.getEquipment().contains(23049)) { //Tier 6 Aura
            percentBoost += 20;
        }

        if (player.getGameMode() == GameMode.IRONMAN || player.getGameMode() == GameMode.ULTIMATE_IRONMAN) {
            percentBoost += 5;
        }
        if (player.getGameMode() == GameMode.GROUP_IRONMAN) {
            percentBoost += 3;
        }
        if (player.getGameMode() == GameMode.VETERAN_MODE) {
			percentBoost += 7;
        }

		// creator set:
		if (player.getEquipment().contains(23127))
			percentBoost += 4;
		if (player.getEquipment().contains(23128))
			percentBoost += 4;
		if (player.getEquipment().contains(23129))
			percentBoost += 4;
		if (player.getEquipment().contains(23130))
			percentBoost += 3;
		if (player.getEquipment().contains(23131))
			percentBoost += 3;
		if (player.getEquipment().contains(23132))
			percentBoost += 3;
		if (player.getEquipment().contains(23133))
			percentBoost += 4;
		//

        if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ODIN_PET.npcId) {
            percentBoost += 25;
        }

        if (player.isInMinigame()) {
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GREEN_FENRIR_PET.npcId) {
                percentBoost += 10;
            }
        }
        if (npc == player.getSlayer().getSlayerTask().getNpcId()) {
            if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23071) {
                percentBoost += 5;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23069 || player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23070) {
                percentBoost += 7;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23074) {
                percentBoost += 10;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23072) {
                percentBoost += 15;
            } else if (player.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 23073) {
                percentBoost += 20;
            }
        }
        if (!player.isInsideRaids()) {
          /*  if (npc == player.getSlayer().getSlayerTask().getNpcId()) {
                if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                        && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.RED_FENRIR_PET.npcId) {
                    percentBoost += 25;
                }
            }*/

            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.SKREEG_PET.npcId) {
                percentBoost += 10;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ORIX_PET.npcId) {
                percentBoost += 20;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.CRYSTAL_ORC_PET.npcId) {
                percentBoost += 25;
            }
        } else {
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.DEMON_PET.npcId) {
                percentBoost += 10;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GOLEM_PET.npcId) {
                percentBoost += 15;
            }
            if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
                    && player.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.DRAGON_PET.npcId) {
                percentBoost += 25;
            }
        }
        if (player.getDoubleDDRTimer() > 0) {
            percentBoost += 100;
        }

        /**
         * Donator Rank bonusses
         */
        if(player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT || player.getRights().equals(PlayerRights.YOUTUBER)) {
            percentBoost += 25;
        } else if(player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT) {
            percentBoost += 20;
        } else if(player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT) {
            percentBoost += 15;
        } else if(player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT) {
            percentBoost += 10;
        } else if(player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT) {
            percentBoost += 7;
        } else if(player.getAmountDonated() >= Donation.SAPPHIRE_DONATION_AMOUNT) {
            percentBoost += 5;
        }


        if (PrayerHandler.isActivated(player,PrayerHandler.GNOMES_GREED)) {
            percentBoost += 10;
        }

        return percentBoost;
    }


}
