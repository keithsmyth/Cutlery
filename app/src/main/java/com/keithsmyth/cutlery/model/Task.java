package com.keithsmyth.cutlery.model;

import android.support.annotation.ColorInt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Task {

    public static final String FREQUENCY_EVERY_DAYS = "Days";
    public static final String FREQUENCY_EVERY_WEEKS = "Weeks";
    public static final String FREQUENCY_EVERY_MONTHS = "Months";

    public static final List<String> FREQUENCIES = Collections.unmodifiableList(Arrays.asList(
        FREQUENCY_EVERY_DAYS,
        FREQUENCY_EVERY_WEEKS,
        FREQUENCY_EVERY_MONTHS
    ));

    public final int id;
    public final String name;
    public final String frequency;
    public final int frequencyValue;
    public final int iconId;
    @ColorInt public final int colour;
    public final boolean isArchived;

    // TODO: Refactor Non DB fields into a ViewModel
    public final long dateTimeLastDone;
    public final int daysOverDue;

    public Task(int id, String name, String frequency, int frequencyValue, int iconId, int colour, boolean isArchived, long dateTimeLastDone, int daysOverDue) {
        this.id = id;
        this.name = name;
        this.frequency = frequency;
        this.frequencyValue = frequencyValue;
        this.iconId = iconId;
        this.colour = colour;
        this.isArchived = isArchived;
        this.dateTimeLastDone = dateTimeLastDone;
        this.daysOverDue = daysOverDue;
    }
}
