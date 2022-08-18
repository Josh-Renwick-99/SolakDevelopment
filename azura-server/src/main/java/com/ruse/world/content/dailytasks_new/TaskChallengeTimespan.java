package com.ruse.world.content.dailytasks_new;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum TaskChallengeTimespan {
    DAILY(TimeUnit.DAYS.toMillis(1)),
    WEEKLY(TimeUnit.DAYS.toMillis(7)),
    BI_WEEKLY(TimeUnit.DAYS.toMillis(14)),
    MONTHLY(TimeUnit.DAYS.toMillis(30))
    ;

    @Getter
    private final long millis;
}
