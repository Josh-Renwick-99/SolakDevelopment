package com.ruse.world.content.dailytasks_new;

import com.ruse.world.entity.impl.player.Player;

import java.util.Arrays;
import java.util.function.Predicate;

public enum DailyTask {
    EASY_SLAYER_TASKS(
            "Easy Slayer Tasks",
            1, 5,
            "",
            "Complete %d easy slayer tasks.", TaskTimeType.DAILY, false),
    MEDIUM_SLAYER_TASKS(
            "Medium Slayer Tasks",
            1, 5,
            "",
            "Complete %d medium slayer tasks.", TaskTimeType.DAILY, false),
    HARD_SLAYER_TASKS(
            "Hard Slayer Tasks",
            1, 5,
            "",
            "Complete %d hard slayer tasks.", TaskTimeType.DAILY, false),
    HALLS_OF_VALOR(
            "Halls of Valor",
            5, 5,
            "",
            "Unlock %d halls of valor chests.", TaskTimeType.DAILY, false),
    KEEPERS_OF_LIGHT(
            "Keepers of Light",
            5, 5,
            "",
            "Complete keepers of light %d times.", TaskTimeType.DAILY, false),
    DUNGEONEERING(
            "Dungeoneering",
            5, 5,
            "",
            "Complete %d dungeoneering runs.", TaskTimeType.DAILY, false),
    TREASURE_HUNTER(
            "Treasure Hunter",
            5, 5,
            "",
            "Unlock %d treasure hunter chests.", TaskTimeType.DAILY, false),
    VOID_OF_DARKNESS(
            "Void of Darkness",
            5, 5,
            "",
            "Unlock %d void of darkness chests.", TaskTimeType.DAILY, false),
    BOSSES(
            "Bosses",
            15, 30,
            "",
            "Kill %d bosses.", TaskTimeType.DAILY, false),
    GLOBAL_BOSSES(
            "Global Bosses",
            2, 2,
            "",
            "Kill %d global bosses.", TaskTimeType.DAILY, false),
    COOK_SHARKS(
            "Cook Sharks",
            50, 150,
            "",
            "Cook %d sharks.", TaskTimeType.DAILY, true),
    CHOP_MAGIC_LOGS(
            "Chop Magic Logs",
            50, 150,
            "",
            "Chop %d magic logs.", TaskTimeType.DAILY, true),
    FISH_MONKFISH(
            "Fish Monkfish",
            50, 150,
            "",
            "Fish %d monkfish.", TaskTimeType.DAILY, true),
    CUT_DRAGONSTONES(
            "Cut Dragonstones",
            50, 150,
            "",
            "Cut %d dragonstones.", TaskTimeType.DAILY, true),
    STRING_YEW_LONGBOWS(
            "String Yew Longbows",
            50, 150,
            "",
            "String %d yew longbows.", TaskTimeType.DAILY, true),
    ;

    public static final DailyTask[] VALUES = values();
    public static final DailyTask[] DAILIES = Arrays.stream(VALUES)
            .filter(t -> t.type == TaskTimeType.DAILY).toArray(DailyTask[]::new);
    public static final DailyTask[] SKILLING_TASKS = Arrays.stream(VALUES)
            .filter(t -> t.skillingTask == true).toArray(DailyTask[]::new);
    public static final DailyTask[] PVM_TASKS = Arrays.stream(VALUES)
            .filter(t -> t.skillingTask == false).toArray(DailyTask[]::new);
    public Predicate<Player> condition;
    public int assignmentMin;
    public int assignmentMax;
    public String title;
    public String shortDescr;
    public String longDescr;
    public TaskTimeType type;
    public boolean skillingTask;

    DailyTask(String title, final int min, final int max, String shortDescr, String longDescr, TaskTimeType type, boolean skillingTask) {
        this(null, title, min, max, shortDescr, longDescr, type, skillingTask);
    }

    DailyTask(Predicate<Player> condition, String title, final int min, final int max, String shortDescr, String longDescr, TaskTimeType type, boolean skillingTask) {
        this.condition = condition;
        this.title = title;
        this.assignmentMin = min;
        this.assignmentMax = max;
        this.shortDescr = shortDescr;
        this.longDescr = longDescr;
        this.type = type;
        this.skillingTask = skillingTask;
    }

    public void tryProgress(Player player) {
        if (condition == null || condition.test(player)) {
            DailyTasks.progress(player, this);
        }
    }
}
