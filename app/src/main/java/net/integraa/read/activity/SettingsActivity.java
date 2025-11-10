package net.integraa.read.activity;

import android.database.Cursor;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import net.integraa.read.R;
import net.integraa.read.dbhelper.DBHelper;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox checkbox_EAN13_UPCA,
                    checkbox_UPCE,
                    checkbox_EAN8,
                    checkbox_CODE39,
                    checkbox_CODE93,
                    checkbox_CODE128,
                    checkbox_CODE11,
                    checkbox_CODE25,
                    checkbox_CODABAR,
                    checkbox_INTERLEAVED_TWO_OF_FIVE,
                    checkbox_MSI_PLESSEY,
                    checkbox_QR,
                    checkbox_DATA_MATRIX,
                    checkbox_AZTEC,
                    checkbox_MAXI_CODE,
                    checkbox_DOT_CODE,
                    checkbox_KIX,
                    checkbox_RM4SCC,
                    checkbox_GS1_DATABAR,
                    checkbox_GS1_DATABAR_EXPANDED,
                    checkbox_GS1_DATABAR_LIMITED,
                    checkbox_PDF417,
                    checkbox_MICRO_PDF417,
                    checkbox_MICRO_QR,
                    checkbox_CODE32,
                    checkbox_LAPA4SC,
                    checkbox_IATA_TWO_OF_FIVE,
                    checkbox_MATRIX_TWO_OF_FIVE;

    private DBHelper dbHelper;
    private ArrayList<String> type_barcode = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DBHelper(getApplicationContext());

        checkbox_EAN13_UPCA = findViewById(R.id.checkbox_EAN13_UPCA);
        checkbox_UPCE = findViewById(R.id.checkbox_UPCE);
        checkbox_EAN8 = findViewById(R.id.checkbox_EAN8);
        checkbox_CODE39 = findViewById(R.id.checkbox_CODE39);
        checkbox_CODE93 = findViewById(R.id.checkbox_CODE93);
        checkbox_CODE128 = findViewById(R.id.checkbox_CODE128);
        checkbox_CODE11 = findViewById(R.id.checkbox_CODE11);
        checkbox_CODE25 = findViewById(R.id.checkbox_CODE25);
        checkbox_CODABAR = findViewById(R.id.checkbox_CODABAR);
        checkbox_INTERLEAVED_TWO_OF_FIVE = findViewById(R.id.checkbox_INTERLEAVED_TWO_OF_FIVE);
        checkbox_MSI_PLESSEY = findViewById(R.id.checkbox_MSI_PLESSEY);
        checkbox_QR = findViewById(R.id.checkbox_QR);
        checkbox_DATA_MATRIX = findViewById(R.id.checkbox_DATA_MATRIX);
        checkbox_AZTEC = findViewById(R.id.checkbox_AZTEC);
        checkbox_MAXI_CODE = findViewById(R.id.checkbox_MAXI_CODE);
        checkbox_DOT_CODE = findViewById(R.id.checkbox_DOT_CODE);
        checkbox_KIX = findViewById(R.id.checkbox_KIX);
        checkbox_RM4SCC = findViewById(R.id.checkbox_RM4SCC);
        checkbox_GS1_DATABAR = findViewById(R.id.checkbox_GS1_DATABAR);
        checkbox_GS1_DATABAR_EXPANDED = findViewById(R.id.checkbox_GS1_DATABAR_EXPANDED);
        checkbox_GS1_DATABAR_LIMITED = findViewById(R.id.checkbox_GS1_DATABAR_LIMITED);
        checkbox_PDF417 = findViewById(R.id.checkbox_PDF417);
        checkbox_MICRO_PDF417 = findViewById(R.id.checkbox_MICRO_PDF417);
        checkbox_MICRO_QR = findViewById(R.id.checkbox_MICRO_QR);
        checkbox_CODE32 = findViewById(R.id.checkbox_CODE32);
        checkbox_LAPA4SC = findViewById(R.id.checkbox_LAPA4SC);
        checkbox_IATA_TWO_OF_FIVE = findViewById(R.id.checkbox_IATA_TWO_OF_FIVE);
        checkbox_MATRIX_TWO_OF_FIVE = findViewById(R.id.checkbox_MATRIX_TWO_OF_FIVE);

        //populateDB();
        checkPopulateDB();
        check_settings();

    }

    private void check_settings() {

        checkbox_EAN13_UPCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_EAN13_UPCA.isChecked() ){
                    dbHelper.updateSettings("EAN13_UPCA","1");
                }
                else{
                    dbHelper.updateSettings("EAN13_UPCA","0");
                }
            }
        });

        checkbox_UPCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_UPCE.isChecked() ){
                    dbHelper.updateSettings("UPCE","1");
                }
                else{
                    dbHelper.updateSettings("UPCE","0");
                }
            }
        });

        checkbox_EAN8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_EAN8.isChecked() ){
                    dbHelper.updateSettings("EAN8","1");
                }
                else{
                    dbHelper.updateSettings("EAN8","0");
                }
            }
        });

        checkbox_CODE39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE39.isChecked() ){
                    dbHelper.updateSettings("CODE39","1");
                }
                else{
                    dbHelper.updateSettings("CODE39","0");
                }
            }
        });

        checkbox_CODE93.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE93.isChecked() ){
                    dbHelper.updateSettings("CODE93","1");
                }
                else{
                    dbHelper.updateSettings("CODE93","0");
                }
            }
        });

        checkbox_CODE128.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE128.isChecked() ){
                    dbHelper.updateSettings("CODE128","1");
                }
                else{
                    dbHelper.updateSettings("CODE128","0");
                }
            }
        });

        checkbox_CODE11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE11.isChecked() ){
                    dbHelper.updateSettings("CODE11","1");
                }
                else{
                    dbHelper.updateSettings("CODE11","0");
                }
            }
        });

        checkbox_CODE25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE25.isChecked() ){
                    dbHelper.updateSettings("CODE25","1");
                }
                else{
                    dbHelper.updateSettings("CODE25","0");
                }
            }
        });

        checkbox_CODABAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODABAR.isChecked() ){
                    dbHelper.updateSettings("CODABAR","1");
                }
                else{
                    dbHelper.updateSettings("CODABAR","0");
                }
            }
        });

        checkbox_INTERLEAVED_TWO_OF_FIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_INTERLEAVED_TWO_OF_FIVE.isChecked() ){
                    dbHelper.updateSettings("INTERLEAVED_TWO_OF_FIVE","1");
                }
                else{
                    dbHelper.updateSettings("INTERLEAVED_TWO_OF_FIVE","0");
                }
            }
        });

        checkbox_MSI_PLESSEY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_MSI_PLESSEY.isChecked() ){
                    dbHelper.updateSettings("MSI_PLESSEY","1");
                }
                else{
                    dbHelper.updateSettings("MSI_PLESSEY","0");
                }
            }
        });

        checkbox_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_QR.isChecked() ){
                    dbHelper.updateSettings("QR","1");
                }
                else{
                    dbHelper.updateSettings("QR","0");
                }
            }
        });

        checkbox_DATA_MATRIX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_DATA_MATRIX.isChecked() ){
                    dbHelper.updateSettings("DATA_MATRIX","1");
                }
                else{
                    dbHelper.updateSettings("DATA_MATRIX","0");
                }
            }
        });

        checkbox_AZTEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_AZTEC.isChecked() ){
                    dbHelper.updateSettings("AZTEC","1");
                }
                else{
                    dbHelper.updateSettings("AZTEC","0");
                }
            }
        });

        checkbox_MAXI_CODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_MAXI_CODE.isChecked() ){
                    dbHelper.updateSettings("MAXI_CODE","1");
                }
                else{
                    dbHelper.updateSettings("MAXI_CODE","0");
                }
            }
        });

        checkbox_DOT_CODE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_DOT_CODE.isChecked() ){
                    dbHelper.updateSettings("DOT_CODE","1");
                }
                else{
                    dbHelper.updateSettings("DOT_CODE","0");
                }
            }
        });

        checkbox_KIX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_KIX.isChecked() ){
                    dbHelper.updateSettings("KIX","1");
                }
                else{
                    dbHelper.updateSettings("KIX","0");
                }
            }
        });

        checkbox_RM4SCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_RM4SCC.isChecked() ){
                    dbHelper.updateSettings("RM4SCC","1");
                }
                else{
                    dbHelper.updateSettings("RM4SCC","0");
                }
            }
        });

        checkbox_GS1_DATABAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_GS1_DATABAR.isChecked() ){
                    dbHelper.updateSettings("GS1_DATABAR","1");
                }
                else{
                    dbHelper.updateSettings("GS1_DATABAR","0");
                }
            }
        });

        checkbox_GS1_DATABAR_EXPANDED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_GS1_DATABAR_EXPANDED.isChecked() ){
                    dbHelper.updateSettings("GS1_DATABAR_EXPANDED","1");
                }
                else{
                    dbHelper.updateSettings("GS1_DATABAR_EXPANDED","0");
                }
            }
        });

        checkbox_GS1_DATABAR_LIMITED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_GS1_DATABAR_LIMITED.isChecked() ){
                    dbHelper.updateSettings("GS1_DATABAR_LIMITED","1");
                }
                else{
                    dbHelper.updateSettings("GS1_DATABAR_LIMITED","0");
                }
            }
        });

        checkbox_PDF417.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_PDF417.isChecked() ){
                    dbHelper.updateSettings("PDF417","1");
                }
                else{
                    dbHelper.updateSettings("PDF417","0");
                }
            }
        });

        checkbox_MICRO_PDF417.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_MICRO_PDF417.isChecked() ){
                    dbHelper.updateSettings("MICRO_PDF417","1");
                }
                else{
                    dbHelper.updateSettings("MICRO_PDF417","0");
                }
            }
        });

        checkbox_MICRO_QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_MICRO_QR.isChecked() ){
                    dbHelper.updateSettings("MICRO_QR","1");
                }
                else{
                    dbHelper.updateSettings("MICRO_QR","0");
                }
            }
        });

        checkbox_CODE32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_CODE32.isChecked() ){
                    dbHelper.updateSettings("CODE32","1");
                }
                else{
                    dbHelper.updateSettings("CODE32","0");
                }
            }
        });

        checkbox_LAPA4SC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_LAPA4SC.isChecked() ){
                    dbHelper.updateSettings("LAPA4SC","1");
                }
                else{
                    dbHelper.updateSettings("LAPA4SC","0");
                }
            }
        });

        checkbox_IATA_TWO_OF_FIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_IATA_TWO_OF_FIVE.isChecked() ){
                    dbHelper.updateSettings("IATA_TWO_OF_FIVE","1");
                }
                else{
                    dbHelper.updateSettings("IATA_TWO_OF_FIVE","0");
                }
            }
        });

        checkbox_MATRIX_TWO_OF_FIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( checkbox_MATRIX_TWO_OF_FIVE.isChecked() ){
                    dbHelper.updateSettings("MATRIX_TWO_OF_FIVE","1");
                }
                else{
                    dbHelper.updateSettings("MATRIX_TWO_OF_FIVE","0");
                }
            }
        });

    }

    private void checkPopulateDB() {

        /*
        * barcodeCaptureSettings.enableSymbology(Symbology.CODE39,true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE128,true);
        barcodeCaptureSettings.enableSymbology(Symbology.CODE93,true);
        barcodeCaptureSettings.enableSymbology(Symbology.INTERLEAVED_TWO_OF_FIVE,true);
        barcodeCaptureSettings.enableSymbology(Symbology.DATA_MATRIX,true);
        barcodeCaptureSettings.enableSymbology(Symbology.AZTEC,true);
        barcodeCaptureSettings.enableSymbology(Symbology.PDF417,true);
        barcodeCaptureSettings.enableSymbology(Symbology.QR,true);
        * */

        Cursor settings = dbHelper.getSettings();
        if (settings.getCount() == 0) {
            populateDB();
            view_state();
            Toast.makeText(SettingsActivity.this,"Operazione completata",Toast.LENGTH_LONG).show();
        } else {
            view_state();
        }

    }

    private void view_state() {
        Cursor settings = dbHelper.getSettings();
        if (settings != null) {
            if (settings.moveToFirst()) {
                do {
                    Log.e("Type",settings.getString(settings.getColumnIndex("type")));
                    if ( settings.getString(settings.getColumnIndex("type")).equals("EAN13_UPCA") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_EAN13_UPCA.setChecked(true);
                        }
                        else{
                            checkbox_EAN13_UPCA.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("UPCE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_UPCE.setChecked(true);
                        }
                        else{
                            checkbox_UPCE.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("EAN8") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_EAN8.setChecked(true);
                        }
                        else{
                            checkbox_EAN8.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE39") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE39.setChecked(true);
                        }
                        else{
                            checkbox_CODE39.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE93") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE93.setChecked(true);
                        }
                        else{
                            checkbox_CODE93.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE128") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE128.setChecked(true);
                        }
                        else{
                            checkbox_CODE128.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE11") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE11.setChecked(true);
                        }
                        else{
                            checkbox_CODE11.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE25") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE25.setChecked(true);
                        }
                        else{
                            checkbox_CODE25.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODABAR") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODABAR.setChecked(true);
                        }
                        else{
                            checkbox_CODABAR.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("INTERLEAVED_TWO_OF_FIVE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_INTERLEAVED_TWO_OF_FIVE.setChecked(true);
                        }
                        else{
                            checkbox_INTERLEAVED_TWO_OF_FIVE.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("MSI_PLESSEY") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_MSI_PLESSEY.setChecked(true);
                        }
                        else{
                            checkbox_MSI_PLESSEY.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("QR") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_QR.setChecked(true);
                        }
                        else{
                            checkbox_QR.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("DATA_MATRIX") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_DATA_MATRIX.setChecked(true);
                        }
                        else{
                            checkbox_DATA_MATRIX.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("AZTEC") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_AZTEC.setChecked(true);
                        }
                        else{
                            checkbox_AZTEC.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("MAXI_CODE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_MAXI_CODE.setChecked(true);
                        }
                        else{
                            checkbox_MAXI_CODE.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("DOT_CODE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_DOT_CODE.setChecked(true);
                        }
                        else{
                            checkbox_DOT_CODE.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("KIX") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_KIX.setChecked(true);
                        }
                        else{
                            checkbox_KIX.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("RM4SCC") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_RM4SCC.setChecked(true);
                        }
                        else{
                            checkbox_RM4SCC.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("GS1_DATABAR") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_GS1_DATABAR.setChecked(true);
                        }
                        else{
                            checkbox_GS1_DATABAR.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("GS1_DATABAR_EXPANDED") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_GS1_DATABAR_EXPANDED.setChecked(true);
                        }
                        else{
                            checkbox_GS1_DATABAR_EXPANDED.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("GS1_DATABAR_LIMITED") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_GS1_DATABAR_LIMITED.setChecked(true);
                        }
                        else{
                            checkbox_GS1_DATABAR_LIMITED.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("PDF417") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_PDF417.setChecked(true);
                        }
                        else{
                            checkbox_PDF417.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("MICRO_PDF417") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_MICRO_PDF417.setChecked(true);
                        }
                        else{
                            checkbox_MICRO_PDF417.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("MICRO_QR") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_MICRO_QR.setChecked(true);
                        }
                        else{
                            checkbox_MICRO_QR.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("CODE32") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_CODE32.setChecked(true);
                        }
                        else{
                            checkbox_CODE32.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("LAPA4SC") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_LAPA4SC.setChecked(true);
                        }
                        else{
                            checkbox_LAPA4SC.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("IATA_TWO_OF_FIVE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_IATA_TWO_OF_FIVE.setChecked(true);
                        }
                        else{
                            checkbox_IATA_TWO_OF_FIVE.setChecked(false);
                        }
                    }
                    if ( settings.getString(settings.getColumnIndex("type")).equals("MATRIX_TWO_OF_FIVE") ){
                        if ( settings.getString(settings.getColumnIndex("enabled")).equals("1") ){
                            checkbox_MATRIX_TWO_OF_FIVE.setChecked(true);
                        }
                        else{
                            checkbox_MATRIX_TWO_OF_FIVE.setChecked(false);
                        }
                    }
                } while (settings.moveToNext());
            }
        }
    }

    private void populateDB(){
        dbHelper.insertSettings("EAN13_UPCA","0");
        dbHelper.insertSettings("UPCE","0");
        dbHelper.insertSettings("EAN8","0");
        dbHelper.insertSettings("CODE39","0");
        dbHelper.insertSettings("CODE93","0");
        dbHelper.insertSettings("CODE128","1");
        dbHelper.insertSettings("CODE11","0");
        dbHelper.insertSettings("CODE25","0");
        dbHelper.insertSettings("CODABAR","0");
        dbHelper.insertSettings("INTERLEAVED_TWO_OF_FIVE","1");
        dbHelper.insertSettings("MSI_PLESSEY","0");
        dbHelper.insertSettings("QR","0");
        dbHelper.insertSettings("DATA_MATRIX","1");
        dbHelper.insertSettings("AZTEC","0");
        dbHelper.insertSettings("MAXI_CODE","0");
        dbHelper.insertSettings("DOT_CODE","0");
        dbHelper.insertSettings("KIX","0");
        dbHelper.insertSettings("RM4SCC","0");
        dbHelper.insertSettings("GS1_DATABAR","0");
        dbHelper.insertSettings("GS1_DATABAR_EXPANDED","0");
        dbHelper.insertSettings("GS1_DATABAR_LIMITED","0");
        dbHelper.insertSettings("PDF417","0");
        dbHelper.insertSettings("MICRO_PDF417","0");
        dbHelper.insertSettings("MICRO_QR","0");
        dbHelper.insertSettings("CODE32","0");
        dbHelper.insertSettings("LAPA4SC","0");
        dbHelper.insertSettings("IATA_TWO_OF_FIVE","0");
        dbHelper.insertSettings("MATRIX_TWO_OF_FIVE","0");
    }

    @Override
    public void onBackPressed() {
        finishActivity(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        //System.exit(0); //ho un riavvio dell'applicazione
        finish();
    }

}