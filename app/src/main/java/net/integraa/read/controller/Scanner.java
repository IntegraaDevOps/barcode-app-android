package net.integraa.read.controller;

import android.app.Application;

import net.integraa.read.controller.scanners.BarcodeDefault;
import net.integraa.read.controller.scanners.BarcodeScanbot;
import net.integraa.read.controller.interfaces.Barcode;
import net.integraa.read.controller.scanners.BarcodeScandit;

public class Scanner {
    protected static Application application;
    protected static String barcodeType="";
    protected static Barcode barcode = null;

    public static void initialize(Application value) {
        application=value;
    }
    public static Application getApplication() {
        return application;
    }
    public static void setBarcodeType(String value) {
        if(barcodeType!=value) {
            barcode = null;
        }
        barcodeType=value;
    }
    public static Barcode getBarcode() {
        if (barcode==null) {
            switch (barcodeType) {
                case "scanbot":
                    barcode = new BarcodeScanbot();
                    break;
                case "scandit":
                    barcode = new BarcodeScandit();
                    break;
                default:
                    barcode = new BarcodeDefault();
            }
        }
        return barcode;
    }
}
