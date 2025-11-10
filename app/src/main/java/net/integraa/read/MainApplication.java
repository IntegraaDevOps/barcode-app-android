package net.integraa.read;

import android.app.Application;

import net.integraa.read.controller.Scanner;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Scanner.initialize(this);
    }
}
