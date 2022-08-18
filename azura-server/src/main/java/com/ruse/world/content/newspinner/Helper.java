package com.ruse.world.content.newspinner;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {

    public static int random(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int random(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(maxInclusive - minInclusive + 1) + minInclusive;
    }

    public static <T> T random(List<T> list) {
        int n = random(list.size());
        return list.get(n);
    }

    public static String getAorAn(String targetWord) {
        String anA = "a";
        targetWord = targetWord.toLowerCase();
        if (targetWord.startsWith("a") || targetWord.startsWith("e") || targetWord.startsWith("i") || targetWord.startsWith("o") || targetWord.startsWith("u")) {
            anA = "an";
        }
        return anA;
    }
}
