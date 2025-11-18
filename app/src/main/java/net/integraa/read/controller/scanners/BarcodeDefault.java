package net.integraa.read.controller.scanners;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import net.integraa.read.R;
import net.integraa.read.controller.interfaces.Barcode;
import net.integraa.read.dbhelper.DialogHelper;

public class BarcodeDefault extends Barcode {

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public View getView() {
        DialogHelper.alertWithAction(activity, "", activity.getString(R.string.no_barcode_scanner_available), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(1);
            }
        });
        View view = new View(activity);
        view.setBackgroundColor(Color.RED);
        return view;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean hasPause() {
        return false;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void setVibrate(boolean value) {

    }

    @Override
    public void setSound(boolean value) {

    }

}
