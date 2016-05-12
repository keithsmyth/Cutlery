package com.keithsmyth.cutlery.model;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import com.keithsmyth.cutlery.R;

public class Icon {

    public final int id;
    @DrawableRes public final int resId;

    @ColorInt
    public static int getDefaultColor(Context context) {
        return ContextCompat.getColor(context, R.color.icon_gray);
    }

    public Icon(int id, int resId) {
        this.id = id;
        this.resId = resId;
    }
}
