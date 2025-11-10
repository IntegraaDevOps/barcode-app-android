/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.integraa.read.scanditLayout;

import androidx.lifecycle.ViewModel;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import net.integraa.read.MainActivity;
import net.integraa.read.controller.Scanner;
import net.integraa.read.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.List;
import net.integraa.read.controller.interfaces.Barcode;

public class ScanViewModel extends ViewModel implements Barcode.ValueListener {

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private ResultListener listener;

    private ArrayList<String> enabled_barcode = new ArrayList<>();

    //Barcode firstBarcode = null;//session.getNewlyRecognizedBarcodes().get(0);

    public ScanViewModel() {
        setBarcodeCaptureSettings();

        // Register self as a listener to get informed whenever a new barcode got recognized.
        Scanner.getBarcode().addListener(this);
    }

    private void setBarcodeCaptureSettings(){
        DBHelper dbHelper = new DBHelper(FullscreenScanFragmentContainerActivity.applicationContext);
        Cursor settings_enabled = dbHelper.getSettingsEnabled();
        if (settings_enabled != null) {
            if (settings_enabled.moveToFirst()) {
                do {
                    Log.e("Type",settings_enabled.getString(settings_enabled.getColumnIndex("type")));
                    enabled_barcode.add(settings_enabled.getString(settings_enabled.getColumnIndex("type")));
                } while (settings_enabled.moveToNext());
            }
        }

        ArrayList<Barcode.Type>enabled = new ArrayList<>();

        if ( enabled_barcode.size() == 0 ){
            enabled.add(Barcode.Type.Code128);
            enabled.add(Barcode.Type.ITF);
            enabled.add(Barcode.Type.DataMatrix);
        }
        else {
            for (String barcode:enabled_barcode) {
                switch (barcode) {
                    case "EAN13_UPCA":
                        enabled.add(Barcode.Type.EAN13);
                        break;
                    case "UPCE":
                        enabled.add(Barcode.Type.UPCE);
                        break;
                    case "UPCA":
                        enabled.add(Barcode.Type.UPCA);
                        break;
                    case "EAN8":
                        enabled.add(Barcode.Type.EAN8);
                        break;
                    case "CODE39":
                        enabled.add(Barcode.Type.Code39);
                        break;
                    case "CODE93":
                        enabled.add(Barcode.Type.Code93);
                        break;
                    case "CODE128":
                        enabled.add(Barcode.Type.Code128);
                        break;
                    case "CODE11":
                        enabled.add(Barcode.Type.Code11);
                        break;
                    case "CODE25":
                        enabled.add(Barcode.Type.Code25);
                        break;
                    case "CODABAR":
                        enabled.add(Barcode.Type.Codebar);
                        break;
                    case "INTERLEAVED_TWO_OF_FIVE":
                        enabled.add(Barcode.Type.ITF);
                        break;
                    case "MSI_PLESSEY":
                        enabled.add(Barcode.Type.MSI_PLESSEY);
                        break;
                    case "QR":
                        enabled.add(Barcode.Type.QrCode);
                        break;
                    case "MICRO_QR":
                        enabled.add(Barcode.Type.MicroQrCode);
                        break;
                    case "DATA_MATRIX":
                        enabled.add(Barcode.Type.DataMatrix);
                        break;
                    case "AZTEC":
                        enabled.add(Barcode.Type.AZTEC);
                        break;
                    case "MAXI_CODE":
                        enabled.add(Barcode.Type.MaxiCode);
                        break;
                    case "DOT_CODE":
                        enabled.add(Barcode.Type.DotCode);
                        break;
                    case "KIX":
                        enabled.add(Barcode.Type.Kix);
                        break;
                    case "RM4SCC":
                        enabled.add(Barcode.Type.RM4SCC);
                        break;
                    case "GS1_DATABAR":
                        enabled.add(Barcode.Type.GS1_DATABAR);
                        break;
                    case "GS1_DATABAR_EXPANDED":
                        enabled.add(Barcode.Type.GS1_DATABAR_EXPANDED);
                        break;
                    case "GS1_DATABAR_LIMITED":
                        enabled.add(Barcode.Type.GS1_DATABAR_LIMITED);
                        break;
                    case "PDF417":
                        enabled.add(Barcode.Type.Pdf417);
                        break;
                    case "MICRO_PDF417":
                        enabled.add(Barcode.Type.MicroPdf417);
                        break;
                    case "CODE32":
                        enabled.add(Barcode.Type.Code32);
                        break;
                    case "LAPA4SC":
                        enabled.add(Barcode.Type.LAPA4SC);
                        break;
                    case "IATA_TWO_OF_FIVE":
                        enabled.add(Barcode.Type.IATA_TWO_OF_FIVE);
                        break;
                    case "MATRIX_TWO_OF_FIVE":
                        enabled.add(Barcode.Type.MATRIX_TWO_OF_FIVE);
                        break;
                }
            }
        }

        Scanner.getBarcode().applyTypes(enabled);
    }

    public void setListener(@Nullable ResultListener listener) {
        this.listener = listener;
    }

    public void resumeScanning() {
        Scanner.getBarcode().resume();
    }

    public void pauseScanning() {
        Scanner.getBarcode().pause();
    }

    public void startFrameSource() {
        Scanner.getBarcode().start();
    }

    public void stopFrameSource() {
        Scanner.getBarcode().stop();
    }

    @Override
    public void onResults(List<Barcode.Result> results) {
        if (listener != null && results != null && results.size()>0) {
            pauseScanning();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onCodeScanned(results.get(0).getValue());
                    Log.e("InfoListener",results.get(0).getValue());
                }
            });
        }
    }

    public interface ResultListener {
        @MainThread
        void onCodeScanned(String barcodeResult);
    }
}
