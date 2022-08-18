package com.ruse.world.content.combat;

import com.ruse.model.Graphic;
import com.ruse.model.Skill;
import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.effect.EquipmentBonus;
import com.ruse.world.content.combat.magic.CombatSpell;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.range.CombatRangedAmmo.RangedWeaponData;
import com.ruse.world.content.combat.weapon.FightType;
import com.ruse.world.content.serverperks.ServerPerks;
import com.ruse.world.content.skill.DropUtils;
import com.ruse.world.content.skill.impl.slayer.SlayerTasks;
import com.ruse.world.content.skill.impl.summoning.BossPets;
import com.ruse.world.content.skill.impl.summoning.Familiar;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class DesolaceFormulas {


    /**
     * Calculates a player's Melee attack level (how likely that they're going to
     * hit through defence)
     *
     * @param plr The player's Meelee attack level
     * @return The player's Melee attack level
     */
    @SuppressWarnings("incomplete-switch")
    public static int getMeleeAttack(Player plr) {
        int attackLevel = plr.getSkillManager().getCurrentLevel(Skill.ATTACK);
        switch (plr.getFightType().getStyle()) {
            case AGGRESSIVE:
                attackLevel += 3;
                break;
            case CONTROLLED:
                attackLevel += 1;
                break;
        }
        // boolean hasVoid = EquipmentBonus.wearingVoid(plr);//, CombatType.MELEE);

        if (PrayerHandler.isActivated(plr, PrayerHandler.CLARITY_OF_THOUGHT)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.05;
        } else if (PrayerHandler.isActivated(plr, PrayerHandler.IMPROVED_REFLEXES)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.1;
        } else if (PrayerHandler.isActivated(plr, PrayerHandler.INCREDIBLE_REFLEXES)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.15;
        } else if (PrayerHandler.isActivated(plr, PrayerHandler.CHIVALRY)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.15;
        } else if (PrayerHandler.isActivated(plr, PrayerHandler.PIETY)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.2;
        } else if (CurseHandler.isActivated(plr, CurseHandler.LEECH_ATTACK)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.05 + plr.getLeechedBonuses()[2];
        } else if (CurseHandler.isActivated(plr, CurseHandler.TURMOIL)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.3 + plr.getLeechedBonuses()[2];
        }  else if(plr.getPrayerActive()[PrayerHandler.FURY_SWIPE]) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.17;
        }

        if (EquipmentBonus.voidMelee(plr)) {
            attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.1;
            if (plr.getCurrentClanChat() != null && plr.getCurrentClanChat().getName().equalsIgnoreCase("Debug")) {
                plr.getPacketSender()
                        .sendMessage("Void Melee accuracy buff applied. Is " + attackLevel + ", pre-equip bonuses");
            }
        }
        attackLevel *= plr.isSpecialActivated() ? plr.getCombatSpecial().getAccuracyBonus() : 1;
        int i = (int) plr.getBonusManager().getAttackBonus()[bestMeleeAtk(plr)];

        // // System.out.println("hello world");
        return (int) (attackLevel + (attackLevel * 0.15) + (i + i * 0.04));
    }

    /**
     * Calculates a player's Melee Defence level
     *
     * @param plr The player to calculate Melee defence for
     * @return The player's Melee defence level
     */
    public static int getMeleeDefence(Player plr) {
        int defenceLevel = plr.getSkillManager().getCurrentLevel(Skill.DEFENCE);
        int i = (int) plr.getBonusManager().getDefenceBonus()[bestMeleeDef(plr)];

        if (plr.getPrayerActive()[PrayerHandler.THICK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
        } else if (plr.getPrayerActive()[PrayerHandler.ROCK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
        } else if (plr.getPrayerActive()[PrayerHandler.STEEL_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
        } else if (plr.getPrayerActive()[PrayerHandler.CHIVALRY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
        } else if (plr.getPrayerActive()[PrayerHandler.PIETY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.RIGOUR]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.AUGURY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
        //Holy prayers
        } else if(plr.getPrayerActive()[PrayerHandler.HUNTERS_EYE]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;

        }  else if(plr.getPrayerActive()[PrayerHandler.FURY_SWIPE]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.17;

        }  else if(plr.getPrayerActive()[PrayerHandler.DESTRUCTION]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.17;
        }
            return (int) (defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));

    }

    public static int bestMeleeDef(Player p) {
        if (p.getBonusManager().getDefenceBonus()[0] > p.getBonusManager().getDefenceBonus()[1]
                && p.getBonusManager().getDefenceBonus()[0] > p.getBonusManager().getDefenceBonus()[2]) {
            return 0;
        }
        if (p.getBonusManager().getDefenceBonus()[1] > p.getBonusManager().getDefenceBonus()[0]
                && p.getBonusManager().getDefenceBonus()[1] > p.getBonusManager().getDefenceBonus()[2]) {
            return 1;
        }
        return p.getBonusManager().getDefenceBonus()[2] <= p.getBonusManager().getDefenceBonus()[0]
                || p.getBonusManager().getDefenceBonus()[2] <= p.getBonusManager().getDefenceBonus()[1] ? 0 : 2;
    }

    public static int bestMeleeAtk(Player p) {
        if (p.getBonusManager().getAttackBonus()[0] > p.getBonusManager().getAttackBonus()[1]
                && p.getBonusManager().getAttackBonus()[0] > p.getBonusManager().getAttackBonus()[2]) {
            return 0;
        }
        if (p.getBonusManager().getAttackBonus()[1] > p.getBonusManager().getAttackBonus()[0]
                && p.getBonusManager().getAttackBonus()[1] > p.getBonusManager().getAttackBonus()[2]) {
            return 1;
        }
        return p.getBonusManager().getAttackBonus()[2] <= p.getBonusManager().getAttackBonus()[1]
                || p.getBonusManager().getAttackBonus()[2] <= p.getBonusManager().getAttackBonus()[0] ? 0 : 2;
    }

    /**
     * Obsidian items
     */

    @SuppressWarnings("incomplete-switch")
    public static int getStyleBonus(Player plr) {
        switch (plr.getFightType().getStyle()) {
            case AGGRESSIVE:
            case ACCURATE:
                return 3;
            case CONTROLLED:
                return 1;
        }
        return 0;
    }

    public static double getEffectiveStr(Player plr) {
        return ((plr.getSkillManager().getCurrentLevel(Skill.STRENGTH)) * getPrayerStr(plr)) + getStyleBonus(plr);
    }

    public static double getPrayerStr(Player plr) {
        if (plr.getPrayerActive()[1] || plr.getCurseActive()[CurseHandler.LEECH_STRENGTH])
            return 1.05;
        else if (plr.getPrayerActive()[6])
            return 1.1;
        else if (plr.getPrayerActive()[14])
            return 1.15;
        else if (plr.getPrayerActive()[24])
            return 1.18;
        else if (plr.getPrayerActive()[25])
            return 1.23;
        else if (plr.getCurseActive()[CurseHandler.TURMOIL])
            return 1.24;
        return 1;
    }

    /**
     * Calculates a player's Ranged attack (level). Credits: Dexter Morgan
     *
     * @param plr The player to calculate Ranged attack level for
     * @return The player's Ranged attack level
     */
    public static int getRangedAttack(Player plr) {
        int rangeLevel = plr.getSkillManager().getCurrentLevel(Skill.RANGED);
        // boolean hasVoid = EquipmentBonus.wearingVoid(plr);//, CombatType.RANGED);
        double accuracy = plr.isSpecialActivated() ? plr.getCombatSpecial().getAccuracyBonus() : 1;
        rangeLevel *= accuracy;
        if (plr.getCurseActive()[PrayerHandler.SHARP_EYE] || plr.getCurseActive()[CurseHandler.SAP_RANGER]) {
            rangeLevel *= 1.05;
        } else if (plr.getPrayerActive()[PrayerHandler.HAWK_EYE]) {
            rangeLevel *= 1.10;
        } else if (plr.getPrayerActive()[PrayerHandler.EAGLE_EYE]) {
            rangeLevel *= 1.15;
        } else if (plr.getPrayerActive()[PrayerHandler.RIGOUR]) {
            rangeLevel *= 1.22;
        } else if (plr.getCurseActive()[CurseHandler.LEECH_RANGED]) {
            rangeLevel *= 1.10;
        }  else if(plr.getPrayerActive()[PrayerHandler.HUNTERS_EYE]) {
            rangeLevel *= 1.25;
        }
        if (EquipmentBonus.voidRange(plr)) {
            rangeLevel *= 1.10;
            if (plr.getCurrentClanChat() != null && plr.getCurrentClanChat().getName().equalsIgnoreCase("Debug")) {
                plr.getPacketSender()
                        .sendMessage("Void Range accuracy buff applied. Is " + rangeLevel + ", pre-equip bonuses");
            }
            if (EquipmentBonus.voidRange(plr)) {
                rangeLevel *= 1.10;
            }
            if (plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 16879) {
                rangeLevel *= 1.10;
            }
            if (plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 18332) {
                rangeLevel *= 1.90;
            }
        }

        Familiar playerFamiliar = plr.getSummoning().getFamiliar();

        if (playerFamiliar != null) {
            double bonus = DropUtils.getDamageBonus(playerFamiliar.getSummonNpc().getId());

            if(!plr.isInsideRaids()) {
                if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.RANGED_PET.npcId) {
                    bonus += 0.10;
                }
                if (plr.isInMinigame()) {
                    if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GREEN_FENRIR_PET.npcId) {
                        bonus += 0.10;
                    }
                }
            }
            if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ODIN_PET.npcId) {
                bonus += 0.20;
            }
            rangeLevel *= bonus;
        }
        /*
         * if (hasVoid && accuracy > 1.15) rangeLevel *= 1.68;
         */
        /*
         * Slay helm
         *
         * if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != null &&
         * plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492) {
         * if(plr.getCombatAttributes().getCurrentEnemy() != null &&
         * plr.getCombatAttributes().getCurrentEnemy().isNpc()) { NPC n =
         * (NPC)plr.getCombatAttributes().getCurrentEnemy(); if(n != null && n.getId()
         * == plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId()) rangeLevel
         * *= 1.12; } }
         */
        return (int) (rangeLevel + (plr.getBonusManager().getAttackBonus()[4] * 2));
    }

    /**
     * Calculates a player's Ranged defence level.
     *
     * @param plr The player to calculate the Ranged defence level for
     * @return The player's Ranged defence level
     */
    public static int getRangedDefence(Player plr) {
        int defenceLevel = plr.getSkillManager().getCurrentLevel(Skill.DEFENCE);
        if (plr.getPrayerActive()[PrayerHandler.THICK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
        } else if (plr.getPrayerActive()[PrayerHandler.ROCK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
        } else if (plr.getPrayerActive()[PrayerHandler.STEEL_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
        } else if (plr.getPrayerActive()[PrayerHandler.CHIVALRY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
        } else if (plr.getPrayerActive()[PrayerHandler.PIETY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.RIGOUR]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.AUGURY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.20 + plr.getLeechedBonuses()[0];
        }
        return (int) (defenceLevel + plr.getBonusManager().getDefenceBonus()[4]
                + (plr.getBonusManager().getDefenceBonus()[4] / 2));
    }

    public static int getMagicAttack(Player plr) {
        // boolean voidEquipment = EquipmentBonus.wearingVoid(plr);//,
        // CombatType.MAGIC);
        int magicLevel = plr.getSkillManager().getCurrentLevel(Skill.MAGIC);
        magicLevel *= plr.isSpecialActivated() ? plr.getCombatSpecial().getAccuracyBonus() : 1;
        if (plr.getPrayerActive()[PrayerHandler.MYSTIC_WILL] || plr.getCurseActive()[CurseHandler.SAP_MAGE]) {
            magicLevel *= 1.05;
        } else if (plr.getPrayerActive()[PrayerHandler.MYSTIC_LORE]) {
            magicLevel *= 1.10;
        } else if (plr.getPrayerActive()[PrayerHandler.MYSTIC_MIGHT]) {
            magicLevel *= 1.15;
        } else if (plr.getPrayerActive()[PrayerHandler.AUGURY]) {
            magicLevel *= 1.22;
        } else if (plr.getCurseActive()[CurseHandler.LEECH_MAGIC]) {
            magicLevel *= 1.18;
        } else if (plr.getPrayerActive()[PrayerHandler.DESTRUCTION]) {
            magicLevel *= 1.25;
        }
        if (EquipmentBonus.voidMage(plr)) {
            magicLevel *= 1.3;
            if (plr.getCurrentClanChat() != null && plr.getCurrentClanChat().getName().equalsIgnoreCase("Debug")) {
                plr.getPacketSender()
                        .sendMessage("Void Mage accuracy buff applied. Is " + magicLevel + ", pre-equip bonuses");
            }
        }

        Familiar playerFamiliar = plr.getSummoning().getFamiliar();

        if (playerFamiliar != null) {
            double bonus = DropUtils.getDamageBonus(playerFamiliar.getSummonNpc().getId());
            if(!plr.isInsideRaids()) {
                if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.MAGE_PET.npcId) {
                    bonus += 0.10;
                }
                if (plr.isInMinigame()) {
                    if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.GREEN_FENRIR_PET.npcId) {
                        bonus += 0.10;
                    }
                }
            }
            if (plr.getSummoning().getFamiliar().getSummonNpc().getId() == BossPets.BossPet.ODIN_PET.npcId) {
                bonus += 0.20;
            }
            magicLevel *= bonus;
        }
        return (int) (magicLevel + (plr.getBonusManager().getAttackBonus()[3] * 2));
    }

    /**
     * Calculates a player's magic defence level
     *
     * @param player The player to calculate magic defence level for
     * @return The player's magic defence level
     */
    public static int getMagicDefence(Player plr) {

        int defenceLevel = plr.getSkillManager().getCurrentLevel(Skill.DEFENCE) / 2
                + plr.getSkillManager().getCurrentLevel(Skill.MAGIC) / 2;

        if (plr.getPrayerActive()[PrayerHandler.THICK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
        } else if (plr.getPrayerActive()[PrayerHandler.ROCK_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
        } else if (plr.getPrayerActive()[PrayerHandler.STEEL_SKIN]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
        } else if (plr.getPrayerActive()[PrayerHandler.CHIVALRY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
        } else if (plr.getPrayerActive()[PrayerHandler.PIETY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.RIGOUR]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getPrayerActive()[PrayerHandler.AUGURY]) {
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
        } else if (plr.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
            defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.20 + plr.getLeechedBonuses()[0];
        }

        return (int) (defenceLevel + plr.getBonusManager().getDefenceBonus()[3]
                + (plr.getBonusManager().getDefenceBonus()[3] / 3));
    }


    public static int getAttackDelay(Player plr) {
        int id = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
        String s = ItemDefinition.forId(id).getName().toLowerCase();
        if (id == -1)
            return 4;// unarmed
        if (id == 18357 || id == 14684)
            return 4;
        RangedWeaponData rangedData = plr.getRangedWeaponData();
        if (rangedData != null) {
            int speed = rangedData.getType().getAttackDelay();
            if (plr.getFightType() == FightType.SHORTBOW_RAPID || plr.getFightType() == FightType.DART_RAPID
                    || plr.getFightType() == FightType.KNIFE_RAPID || plr.getFightType() == FightType.THROWNAXE_RAPID
                    || plr.getFightType() == FightType.JAVELIN_RAPID
                    || plr.getFightType() == FightType.BLOWPIPE_RAPID) {
                speed--;
            }
            return speed;
        }
        if (id == 18365)
            return 3;
        else if (id == 18349) // CCbow and rapier
            return 4;
        if (id == 22034)
            return 4;
        if (id == 18799)
            return 2;
        if (id == 18358)
            return 1;
        if (id == 18353) // cmaul
            return 7;// chaotic maul
        if (id == 6818)
            return 1;
        if (id == 22010 || id == 19137 || id == 19136 || id == 14919 || id == 15785 || id == 20173 || id == 9929
                || id == 14056 || id == 21606 || id == 671 || id == 16879 || id == 18332 || id == 22083 || id == 20553
                || id == 18636 || id == 8411 || id == 8088 || id == 5073 || id == 8001 || id == 8253 || id == 22089
                || id == 7543 || id == 19843 || id == 9941 || id == 5012 || id == 5011 || id == 22113 || id == 5010 || id == 19135)
            return 1;
        if (id == 20000)
            return 4;// gs
        if (id == 22008)
            return 4;// abyssal tentacle == same speed as rapier
        if (id == 18686)
            return 2;// abyssal tentacle == same speed as rapier
        if (id == 18365 || id == 671)
            return 2;// abyssal tentacle == same speed as rapier
        if (id == 20001)
            return 4;// gs
        if (id == 20002)
            return 4;// gs
        if (id == 20003)
            return 4;// gs
        if (id == 18349)
            return 4;// chaotic rapier
        if (id == 14024)
            return 4;// drygore rapier
        if (id == 14023)
            return 5;// drygore long
        if (id == 18353) // cmaul
            return 7;// chaotic maul
        if (id == 16877)
            return 4;// dung 16877 shortbow
        if (id == 19143)
            return 3;// sara shortbow
        if (id == 19146)
            return 4;// guthix shortbow
        if (id == 19149)
            return 3;// zammy shortbow
        if (id == 16879)
            return 3;// Starter bow
        if (id == 18332)
            return 3;// Starter bow
        if (id == 20171) // zaryte
            return 5;
        if (id == 20553) // zaryte
            return 2;
        if (id == 16337) // zaryte
            return 5;
        if (id == 21053 || id == 21054 || id == 21055 || id == 21056) // zaryte
            return 5;
        if (id == 21018) // zaryte
            return 5;
        if (id == 12926) // blowpipe
            return 3;
        if (id == 14018) // tempest
            return 4;
        switch (id) {
            case 18357:
                return 4;
            case 11235:
            case 13405: // dbow
            case 15701: // dark bow
            case 15702: // dark bow
            case 15703: // dark bow
            case 15785:
            case 15704: // dark bow
            case 19146: // guthix bow
                return 9;

            case 13879:
                return 8;
            case 13956:
                return 6;
            case 15241: // hand cannon

                return 8;

            case 11730:
                return 4;
            case 14484:
            case 18685:
                return 5;
            case 13883:
                return 6;
            case 10887:
            case 6528:
            case 15039:
                return 7;
            case 13905:
                return 5;
            case 13907:
                return 5;
            case 18353:
                return 7;
            case 18349:
                return 4;
            case 20000:
            case 20001:
            case 20002:
            case 20003:
                return 4;
            case 16403: // long primal
                return 5;
            case 19843:
            case 8253:
            case 22089:
            case 7543:
            case 9929:
            case 14056:
            case 18636:
            case 8088:
            case 8411:
            case 5012:
            case 22113:
            case 5011:
            case 5010:
            case 9941:
            case 8011:
            case 21606:
            case 14919:
            case 19135:
                return 1;
            case 19137:
                return 4;
            case 19136:
                return 2;
            case 22010:
                return 1;
            case 16337:
            case 3738:
            case 21018:
            case 21053:
            case 21054:
            case 21055:
            case 21056:
                return 3;
        }

        if (s.endsWith("greataxe"))
            return 7;
        else if (s.equals("torags hammers"))
            return 5;
        else if (s.equals("guthans warspear"))
            return 5;
        else if (s.equals("veracs flail"))
            return 5;
        else if (s.equals("ahrims staff"))
            return 6;
        else if (s.equals("crossbow"))
            return 4;
        else if (s.contains("staff")) {
            if (s.contains("zamarok") || s.contains("guthix") || s.contains("saradomian") || s.contains("slayer")
                    || s.contains("ancient"))
                return 4;
            else
                return 5;
        } else if (s.contains("aril")) {
            if (s.contains("composite") || s.equals("seercull"))
                return 5;
            else if (s.contains("Ogre"))
                return 8;
            else if (s.contains("short") || s.contains("hunt") || s.contains("sword"))
                return 4;
            else if (s.contains("long") || s.contains("crystal"))
                return 6;
            else if (s.contains("'bow"))
                return 4;

            return 5;
        } else if (s.contains("dagger"))
            return 4;
        else if (s.contains("godsword") || s.contains("2h"))
            return 6;
        else if (s.contains("longsword"))
            return 5;
        else if (s.contains("sword"))
            return 4;
        else if (s.contains("scimitar"))
            return 4;
        else if (s.contains("katana"))
            return 4;
        else if (s.contains("tempest"))
            return 4;
        else if (s.contains("blowpipe"))
            return 3;
        else if (s.contains("mace"))
            return 5;
        else if (s.contains("battleaxe"))
            return 6;
        else if (s.contains("pickaxe"))
            return 5;
        else if (s.contains("thrownaxe"))
            return 5;
        else if (s.contains("axe"))
            return 5;
        else if (s.contains("warhammer"))
            return 6;
        else if (s.contains("2h"))
            return 7;
        else if (s.contains("spear"))
            return 5;
        else if (s.contains("claw"))
            return 4;
        else if (s.contains("halberd"))
            return 7;

            // sara sword, 2400ms
        else if (s.equals("granite maul"))
            return 7;
        else if (s.equals("toktz-xil-ak"))// sword
            return 4;
        else if (s.equals("tzhaar-ket-em"))// mace
            return 5;
        else if (s.equals("tzhaar-ket-om"))// maul
            return 7;
        else if (s.equals("chaotic maul"))// maul
            return 7;
        else if (s.equals("toktz-xil-ek"))// knife
            return 4;
        else if (s.equals("toktz-xil-ul"))// rings
            return 4;
        else if (s.equals("toktz-mej-tal"))// staff
            return 6;
        else if (s.contains("whip"))
            return 4;
        else if (s.contains("dart"))
            return 3;
        else if (s.contains("death-touched"))
            return 10;
        else if (s.contains("knife"))
            return 3;
        else if (s.contains("javelin"))
            return 6;
        return 5;
    }
}
