package com.keithsmyth.cutlery;

import android.app.Application;

import com.keithsmyth.cutlery.data.DataInjector;

public class App extends Application {

    private static DataInjector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        injector = new DataInjector(this);
        // TODO: Google analytics
    }

    public static DataInjector inject() {
        return injector;
    }
}
