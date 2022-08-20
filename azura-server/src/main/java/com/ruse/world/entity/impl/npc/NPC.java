package com.ruse.world.entity.impl.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruse.engine.task.TaskManager;
import com.ruse.engine.task.impl.NPCDeathTask;
import com.ruse.model.DamageDealer;
import com.ruse.model.Direction;
import com.ruse.model.Item;
import com.ruse.model.Locations.Location;
import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.JsonLoader;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.effect.CombatPoisonEffect.PoisonType;
import com.ruse.world.content.combat.strategy.CombatStrategies;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.KalphiteQueen;
import com.ruse.world.content.combat.strategy.impl.Nex;
import com.ruse.world.content.skill.impl.hunter.Hunter;
import com.ruse.world.content.skill.impl.hunter.PuroPuro;
import com.ruse.world.content.skill.impl.runecrafting.DesoSpan;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPCMovementCoordinator.Coordinator;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.region.Region;
import com.ruse.world.region.RegionManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a non-playable character, which players can interact with.
 *
 * @author Gabriel Hannason
 */

public class NPC extends Character {


    @Getter
    @Setter
    private Character leader;
    
    private boolean aggressive;

    private boolean forceAggressive;

    public boolean isAggressive() {
        return aggressive;
    }

    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    public boolean isForceAggressive() {
        return forceAggressive;
    }

    public void setForceAggressive(boolean forceAggressive) {
        this.forceAggressive = forceAggressive;
    }
    private int aggressionDistance = 7;
    public void setAggressiveDistance(int distance) {
        aggressionDistance = distance;
    }

    public List<Entity> getPossibleTargets() {
        return getPossibleTargets(false, true);
    }

    public List<Entity> getPossibleTargets(boolean checkNPCs, boolean checkPlayers) {
        int size = getSize();
        int aggressionDistance = getAggressionDistance();
        int agroRatio = 2;
        int combatLevel = getCombatLevel();
        ArrayList<Entity> possibleTarget = new ArrayList<>();
        Region region = RegionManager.getRegion(currentRegionId);
        if (region == null) {
            return Collections.emptyList();
        }
        if (checkPlayers) {
            for (int playerIndex : region.getVisiblePlayers()) {
                Player player = World.getPlayers().get(playerIndex);
                if (player == null) {
                    continue;
                }
                if (position.withinScene(player.getCurrentMapCenter())) {
                    if (player.isDying() || !player.isRegistered() || !player.isRunning() || !Misc.isOnRange(getPosition().getX(), getPosition().getY(), size, player.getPosition().getX(), player.getPosition().getY(), player.getSize(), aggressionDistance > 0 ? aggressionDistance : agroRatio) || player.getSkillManager().getCombatLevel() >= getCombatLevel() * 2) {
                        continue;
                    }
                    possibleTarget.add(player);
                    if (checkNPCs) {
                        NPC familiar = player.getSummoning().getFamiliar().getSummonNpc();
                        if (familiar == null || familiar.isDying() || !Misc.isOnRange(getPosition().getX(), getPosition().getY(), size, familiar.getPosition().getX(), familiar.getPosition().getY(), familiar.getSize(), aggressionDistance > 0 ? aggressionDistance : agroRatio))
                            continue;
                        possibleTarget.add(familiar);
                    }
                }
            }
        }
        if (checkNPCs) {
            for (int npcIndex : region.getVisibleNpcs()) {
                NPC npc = World.getNpcs().get(npcIndex);
                if (npc == null) {
                    continue;
                }
                if (position.withinScene(npc.getCurrentMapCenter())) {
                    if (npc.isSummoningNpc() || npc.getPosition().getZ() != getPosition().getZ() || npc == this || npc.isDying() || !Misc.isOnRange(getPosition().getX(), getPosition().getY(), size, npc.getPosition().getX(), npc.getPosition().getY(), npc.getSize(), aggressionDistance > 0 ? aggressionDistance : agroRatio) || !npc.getDefinition().isAttackable()) {
                        continue;
                    }
                    possibleTarget.add(npc);
                }
            }
        }
        return possibleTarget;
    }


    public int getCombatLevel() {
        return getDefinition().getCombatLevel();
    }

    /**
     * INSTANCES
     **/
    private Position defaultPosition = null;
    /**
     * INTS
     **/
    private int id = 0;
    public boolean maxHitDummy;
    private int distance;
    private boolean attackable = true;
    private NPCMovementCoordinator movementCoordinator = new NPCMovementCoordinator(this);
    private Player spawnedFor;
    private NpcDefinition definition;
    public boolean respawn;
    /**
     * LISTS
     **/
    private List<DamageDealer> damageDealerMap = new ArrayList<DamageDealer>();
    private List<Item> evenBossRewards = new ArrayList<>();
    private int constitution = 100;
    private int defaultConstitution;
    private int transformationId = -1;
    /**
     * BOOLEANS
     **/
    private boolean[] attackWeakened = new boolean[3], strengthWeakened = new boolean[3];
    private boolean summoningNpc, summoningCombat;
    private boolean isDying;
    private boolean eventBoss;
    private boolean visible = true;
    private boolean healed, chargingAttack;
    private boolean findNewTarget;
    private boolean fetchNewDamageMap;
    public NPC(int id, Position position) {
        this(id, position, true);
    }
    public NPC(int id, Position position, boolean respawn) {
        super(position);
        NpcDefinition definition = NpcDefinition.forId(id);
        if (definition == null) {
            System.out.println("NPC " + id + " is not defined!");
            definition = NpcDefinition.createDummy();
        }
        this.defaultPosition = position;
        this.id = id;
        this.definition = definition;
        this.respawn = respawn;
        this.aggressive = definition.isAggressive();
        this.defaultConstitution = definition.getHitpoints() < 100 ? 100 : definition.getHitpoints();
        this.constitution = defaultConstitution;
        setLocation(Location.getLocation(this));
    }

    /**
     * Prepares the dynamic json loader for loading world npcs.
     *
     * @return the dynamic json loader.
     * @throws Exception if any errors occur while preparing for load.
     */
    public static List<NPC> spawnedWorldsNpcs = new ArrayList<>();

    public static List<NPCSpawn> spawns = new ArrayList<>();


    @Override
    public void loadMap() {
        Region previousRegion = RegionManager.getRegion(currentRegionId);
        if (previousRegion != null) {
            previousRegion.getNpcs().remove(index);
        }
        currentRegionId = position.getRegionId();
        Region currentRegion = RegionManager.getRegion(currentRegionId);
        if (currentRegion != null) {
            currentRegion.getNpcs().add(index);
        }
        // Calculate the current chunk x and y positions.
        int baseChunkX = position.getRealChunkX();
        int baseChunkY = position.getRealChunkY();
        // Clear the player region(id)s and visbility in other region(s).
        regionIds
                .stream()
                .map(RegionManager::getRegion)
                .filter(Objects::nonNull)
                .forEach(region -> region.getVisibleNpcs().remove(index));
        regionIds.clear();
        for (int chunkX = baseChunkX - 6; chunkX < baseChunkX + 6; chunkX++) {
            for (int chunkY = baseChunkY - 6; chunkY < baseChunkY + 6; chunkY++) {
                int regionId = ((chunkX >> 3) << 8) | (chunkY >> 3);
                if (regionIds.contains(regionId)) {
                    continue;
                }
                regionIds.add(regionId);
                Region region = RegionManager.getRegion(regionId);
                if (region != null) {
                    region.getVisibleNpcs().add(index);
                }
            }
        }
        setCurrentMapCenter(position.copy());
    }

    @Override
    public void removeFromMap() {
        Region currentRegion = RegionManager.getRegion(currentRegionId);
        if (currentRegion != null) {
            currentRegion.getNpcs().remove(index);
        }
        regionIds.stream().map(RegionManager::getRegion).filter(Objects::nonNull).forEach(region -> region.getVisibleNpcs().remove(index));
    }

    public static void init() {
        spawns.clear();
        new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                if (reader == null || reader.get("npc-id") == null)
                    return;
                int id = reader.get("npc-id").getAsInt();
                Position position = builder.fromJson(reader.get("position").getAsJsonObject(), Position.class);
                Coordinator coordinator = builder.fromJson(reader.get("walking-policy").getAsJsonObject(), Coordinator.class);
                Direction direction = Direction.valueOf(reader.get("face").getAsString());
                NPCSpawn spawn = new NPCSpawn(id, direction, position, coordinator);
                if (spawns.contains(spawn)) {
                    return;
                }
                NPC npc = new NPC(spawn.npcId, spawn.position);
                npc.movementCoordinator.setCoordinator(coordinator);
                npc.setDirection(direction);
                World.register(npc);
                spawns.add(spawn);
                spawnedWorldsNpcs.add(npc);
                if (id == 9019) {
                    for (int i = 0; i < 8; i ++) {
                        Position newPos = position.copy().setZ((i * 4) + 5);
                        NPC newnpc = new NPC(id, newPos);
                        newnpc.movementCoordinator.setCoordinator(coordinator);
                        newnpc.setDirection(direction);
                        World.register(newnpc);
                    }
                }
                if (id == 9001) {
                    for (int i = 1; i < 5; i ++) {
                        Position newPos = position.copy().setZ((i * 4));
                        NPC newnpc = new NPC(id + i, newPos);
                        newnpc.movementCoordinator.setCoordinator(coordinator);
                        newnpc.setDirection(direction);
                        World.register(newnpc);
                    }
                    for (int i = 0; i < 5; i ++) {
                        Position newPos = position.copy().setZ((i * 4) + 20);
                        NPC newnpc = new NPC(id + i, newPos);
                        newnpc.movementCoordinator.setCoordinator(coordinator);
                        newnpc.setDirection(direction);
                        World.register(newnpc);
                    }
                }

                if (id > 5070 && id < 5081) {
                    Hunter.HUNTER_NPC_LIST.add(npc);
                }
                position = null;
                coordinator = null;
                direction = null;
            }

            @Override
            public String filePath() {
                return "./azura-server/data/def/json/world_npcs.json";
            }
        }.load();
        System.out.println("Loaded "+spawns.size()+" NPC Spawns.");
        Nex.spawn();
        PuroPuro.spawn();
        DesoSpan.spawn();
    }

    public static NPC of(int id, Position position) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Fields
     */

    public void sequence() {
        if (leader != null){
            if (leader == null || leader.getConstitution() <= 0){
                World.deregister(this);
            }
        }
        getCombatBuilder().process();
        /**
         * HP restoration
         */
		/*if (constitution < defaultConstitution) {//btw this is probably a client side visual bug, if u count the hits, its 1200 total but a client side bug where any npc dies at 128 hp even if it had 100m hp
			if (!isDying) {
				if (getLastCombat()
						.elapsed((id == 2042 || id == 2043 || id == 2044 || id == 13447 || id == 3200 ? 50000 : 5000))
						&& !getCombatBuilder().isAttacking() && !getCombatBuilder().isBeingAttacked() && getLocation() != Location.PEST_CONTROL_GAME
						&& getLocation() != Location.DUNGEONEERING && getLocation() != Location.ZULRAH) {
					setConstitution(constitution + (int) (defaultConstitution * 0.1));
					if (constitution > defaultConstitution)// ZULRAH
						setConstitution(defaultConstitution);
				}
			}
		}*/
    }

    @Override
    public void appendDeath() {
        if (!isDying && !summoningNpc) {
            TaskManager.submit(new NPCDeathTask(this));
            isDying = true;
        }
    }

    @Override
    public int getConstitution() {
        return constitution;
    }

    @Override
    public NPC setConstitution(int constitution) {
        this.constitution = constitution;
        if (this.constitution <= 0)
            appendDeath();
        return this;
    }

    @Override
    public void heal(int heal) {
        System.out.println("HEALING NPC");
        if ((this.constitution + heal) > getDefaultConstitution()) {
            setConstitution(getDefaultConstitution());
            return;
        }
        setConstitution(this.constitution + heal);
    }

    @Override
    public int getBaseAttack(CombatType type) {
        return getDefinition().getAttackBonus();
    }

    @Override
    public int getAttackSpeed() {
        return this.getDefinition().getAttackSpeed();
    }

    @Override
    public int getBaseDefence(CombatType type) {

        if (type == CombatType.MAGIC)
            return getDefinition().getDefenceMage();
        else if (type == CombatType.RANGED)
            return getDefinition().getDefenceRange();

        return getDefinition().getDefenceMelee();
    }

    @Override
    public boolean isNpc() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof NPC && ((NPC) other).getIndex() == getIndex();
    }

    @Override
    public int getSize() {
        return getDefinition() == null ? 1 : getDefinition().getSize();
    }

    @Override
    public void poisonVictim(Character victim, CombatType type) {
        if (getDefinition().isPoisonous()) {
            CombatFactory.poisonEntity(victim,
                    type == CombatType.RANGED || type == CombatType.MAGIC ? PoisonType.MILD : PoisonType.EXTRA);
        }

    }

    public boolean isAttackable() {
        return attackable;
    }

    public void setAttackable(boolean attackable) {
        this.attackable = attackable;
    }

    @Override
    public CombatStrategy determineStrategy() {
        return CombatStrategies.getStrategy(id);
    }

    public boolean switchesVictim() {
        if (getLocation() == Location.DUNGEONEERING) {
            return true;
        }
        return id == 6263 || id == 6265 || id == 6203 || id == 6208 || id == 6206 || id == 6247 || id == 6250
                || id == 3200 || id == 4540 || id == 1158 || id == 1160 || id == 8133 || id == 13447 || id == 13451
                || id == 13452 || id == 13453 || id == 13454 || id == 2896 || id == 2882 || id == 2881 || id == 6260
                || id == 109 || id == 2001 || id == 2006 || id == 2009 || id == 2000;
    }

    public int getAggressionDistance() {
        switch (id) {
            case 2030:
            case 2029:
            case 2028:
            case 2027:
            case 2026:
            case 2025:
                return 15;
        }
        if (Nex.nexMob(id)) {
            return 60;
        } else if (id == 2896) {
            return 50;
        } else if(id == 9014 || id == 9017 || id == 3305) {//globalbosses
            return 50;
        }
        return aggressionDistance;
    }

    public int getAttackDistance() {
        return distance;
    }

    /*
     * Getters and setters
     */

    public void setAttackDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public Position getDefaultPosition() {
        return defaultPosition;
    }

    public int getDefaultConstitution() {
        return defaultConstitution;
    }

    public void setDefaultConstitution(int defaultConstitution) {
        this.defaultConstitution = defaultConstitution;
    }

    public int getTransformationId() {
        return transformationId;
    }

    public void setTransformationId(int transformationId) {
        this.transformationId = transformationId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<Item> getEventBossRewards() {
        return evenBossRewards;
    }

    public void setEventBossRewards(List<Item> evenBossRewards) {
        this.evenBossRewards = evenBossRewards;
    }

    public boolean isEventBoss() {
        return eventBoss;
    }

    public void setEventBoss(boolean eventBoss) {
        this.eventBoss = eventBoss;
    }

    /**
     * @return the statsWeakened
     */
    public boolean[] getDefenceWeakened() {
        return attackWeakened;
    }

    public boolean isSummoningNpc() {
        return summoningNpc;
    }

    public void setSummoningNpc(boolean summoningNpc) {
        this.summoningNpc = summoningNpc;
    }

    public boolean isDying() {
        return isDying;
    }

    public void setDying(boolean isDying) {
        this.isDying = isDying;
    }

    /**
     * @return the statsBadlyWeakened
     */
    public boolean[] getStrengthWeakened() {
        return strengthWeakened;
    }

    public NPCMovementCoordinator getMovementCoordinator() {
        return movementCoordinator;
    }

    public NpcDefinition getDefinition() {
        return definition;
    }

    public Player getSpawnedFor() {
        return spawnedFor;
    }

    public NPC setSpawnedFor(Player spawnedFor) {
        this.spawnedFor = spawnedFor;
        return this;
    }

    public Character copy() {
        return this;
    }

    public boolean hasHealed() {
        return healed;
    }

    public void setHealed(boolean healed) {
        this.healed = healed;
    }

    public boolean isChargingAttack() {
        return chargingAttack;
    }

    public NPC setChargingAttack(boolean chargingAttack) {
        this.chargingAttack = chargingAttack;
        return this;
    }

    public boolean findNewTarget() {
        return findNewTarget;
    }

    public void setFindNewTarget(boolean findNewTarget) {
        this.findNewTarget = findNewTarget;
    }

    public boolean summoningCombat() {
        return summoningCombat;
    }

    public void setSummoningCombat(boolean summoningCombat) {
        this.summoningCombat = summoningCombat;
    }

    public void setFetchNewDamageMap(boolean fetchNewDamageMap) {
        this.fetchNewDamageMap = fetchNewDamageMap;
    }

    public boolean fetchNewDamageMap() {
        return fetchNewDamageMap;
    }

    public List<DamageDealer> getDamageDealerMap() {
        return damageDealerMap;
    }

    public void setDamageDealerMap(List<DamageDealer> damageDealerMap) {
        this.damageDealerMap = damageDealerMap;
    }


    public void removeInstancedNpcs(Location loc, int height) {
        int checks = loc.getX().length - 1;
        for (int i = 0; i <= checks; i += 2) {
            if (getPosition().getX() >= loc.getX()[i] && getPosition().getX() <= loc.getX()[i + 1]) {
                if (getPosition().getY() >= loc.getY()[i] && getPosition().getY() <= loc.getY()[i + 1]) {
                    if (getPosition().getZ() == height) {
                        World.deregister(this);
                    }
                }
            }
        }
    }

    public void removeInstancedNpcs(Location loc, int height, Player player) {
        int checks = loc.getX().length - 1;
        for (int i = 0; i <= checks; i += 2) {
            if (getPosition().getX() >= loc.getX()[i] && getPosition().getX() <= loc.getX()[i + 1]) {
                if (getPosition().getY() >= loc.getY()[i] && getPosition().getY() <= loc.getY()[i + 1]) {
                    if (getPosition().getZ() == height && (player == null || (getSpawnedFor() != null && getSpawnedFor().getIndex() == player.getIndex()))) {
                        World.deregister(this);
                    }
                }
            }
        }
    }
    public void removeNpcs(Location loc, int height) {
        int checks = loc.getX().length - 1;
        for (int i = 0; i <= checks; i += 2) {
            if (getPosition().getX() >= loc.getX()[i] && getPosition().getX() <= loc.getX()[i + 1]) {
                if (getPosition().getY() >= loc.getY()[i] && getPosition().getY() <= loc.getY()[i + 1]) {
                    if (getPosition().getZ() == height) {
                        World.deregister(this);
                    }
                }
            }
        }
    }

    public ObjectArrayList<Player> getClosePlayers(int distance) {
        return World.getNearbyPlayers(getPosition(), distance);
    }

    public ObjectArrayList<NPC> getCloseNpcs(int distance) {
        return World.getNearbyNPCs(getPosition(), distance);
    }
}
