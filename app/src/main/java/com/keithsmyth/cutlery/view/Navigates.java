package com.keithsmyth.cutlery.view;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

public interface Navigates {

    void to(Fragment fragment);

    void back();

    void setTitle(@StringRes int titleRes); // Expose the Activity.setTitle(int) method
}
