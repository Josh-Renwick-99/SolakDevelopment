package com.ruse.world.content.casketopening;

import com.ruse.util.Misc;

public class Box {

    private int id;
    private int min;
    private int max;
    private double rate;
    private boolean announce;

    public Box(int id, int min, int max, double rate, boolean announce) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.rate = rate;
        this.announce = announce;
    }

    public Box(int id, int min, int max, double rate) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.rate = rate;
        this.announce = false;
    }

    public Box(int id, double rate) {
        this.id = id;
        this.min = 1;
        this.max = 1;
        this.rate = rate;
        this.announce = false;
    }

    public Box(int id, double rate, boolean announce) {
        this.id = id;
        this.min = 1;
        this.max = 1;
        this.rate = rate;
        this.announce = announce;
    }

    public Box(int id, int amount, double rate, boolean announce) {
        this.id = id;
        this.min = amount;
        this.max = amount;
        this.rate = rate;
        this.announce = announce;
    }
    public Box(int id, int amount, double rate) {
        this.id = id;
        this.min = amount;
        this.max = amount;
        this.rate = rate;
        this.announce = false;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    
    public int getAmount() {
        return min + Misc.getRandom(max - min);
    }

}