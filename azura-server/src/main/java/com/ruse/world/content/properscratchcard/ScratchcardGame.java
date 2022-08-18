package com.ruse.world.content.properscratchcard;

import java.util.Arrays;

public class ScratchcardGame {

    private int[] items;

    private boolean gameStarted;

    public boolean isWinner() {
        return Arrays.stream(items).allMatch(item -> item == items[0]);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int[] getItems() {
        return items;
    }

    public void setItems(int[] items) {
        this.items = items;
    }

}
