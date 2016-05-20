package com.keithsmyth.cutlery.view;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AndroidUtil {

    private AndroidUtil() {
        // no instances
    }

    public static void closeKeyboard(View view) {
        if (view == null) { return; }
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
