package net.integraa.read.controller.interfaces;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class Barcode {
    protected List<Barcode.ValueListener> scannedListeners=new ArrayList<>();
    protected List<Barcode.Type> types=new ArrayList<>(0);
    protected String licenseKey="";
    protected Activity activity=null;

    public void addListener(Barcode.ValueListener value) {
        scannedListeners.add(value);
    }
    public void removeListener(Barcode.ValueListener value) {
        scannedListeners.remove(value);
    }

    protected synchronized void onCodeScanned(List<Barcode.Result> results){
        if (this.hasPause()) {
            return;
        }
        for (ValueListener listener : scannedListeners) {
            listener.onResults(results);
        }
    }

    public void initialize(Activity activity, String licenseKey) {
        this.licenseKey=licenseKey;
        this.activity=activity;
    }
    public void applyTypes(List<Barcode.Type> types) {
        this.types=types;
    }
    public abstract void onResume();
    public abstract void onPause();
    public abstract void onDestroy();
    public abstract View getView();
    public abstract void start();
    public abstract void stop();
    public abstract boolean hasPause();
    public abstract void pause();
    public abstract void resume();
    public abstract void setVibrate(boolean value);
    public abstract void setSound(boolean value);

    public interface Result {
        String getValue();
        Barcode.Type getType();
    }

    public interface ValueListener {
        void onResults(List<Barcode.Result> results);
    }

    public enum Type {
        DataMatrix,
        QrCode,
        Code128,
        ITF,
        Code39,
        Code93,
        AZTEC,
        EAN13,
        UPCA,
        EAN8,
        Code11,
        Code25,
        Codebar,
        MSI_PLESSEY,
        MicroQrCode,
        MaxiCode,
        DotCode,
        Kix,
        RM4SCC,
        GS1_DATABAR,
        GS1_DATABAR_EXPANDED,
        GS1_DATABAR_LIMITED,
        Pdf417,
        MicroPdf417,
        Code32,
        LAPA4SC,
        IATA_TWO_OF_FIVE,
        MATRIX_TWO_OF_FIVE,
        UPCE,
    }
}
