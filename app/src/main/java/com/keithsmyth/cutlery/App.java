package com.keithsmyth.cutlery;

import android.app.Application;
import android.content.Context;

import com.keithsmyth.cutlery.data.DataInjector;

public class App extends Application {

    // TODO: Generate daily notification

    private DataInjector injector;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public DataInjector inject() {
        if (injector == null) {
            injector = new DataInjector(this);
        }
        return injector;
    }
}
