package net.integraa.read.scanditLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import net.integraa.read.R;
import net.integraa.read.Util;
import net.integraa.read.activity.ErrorActivity;
import net.integraa.read.activity.SettingsActivity;
import net.integraa.read.controller.Scanner;
import net.integraa.read.dbhelper.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class FullscreenScanFragmentContainerActivity extends AppCompatActivity {

    private Fragment fragment;
    protected HashMap<String, String> map_values = null;

    public static Context applicationContext = null;
    public static Thread.UncaughtExceptionHandler defaultHandler = null;
    public static Thread.UncaughtExceptionHandler exceptionHandler = null;


    public static Intent getIntent(Context context) {
        return new Intent(context, FullscreenScanFragmentContainerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_scan_fragment_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init_error(this);
        //double x=5/0;
        startScanning();

        Bundle bundle = new Bundle();

        Intent intent = getIntent();

        try {
            map_values = (HashMap<String, String>) intent.getSerializableExtra("map_values");
        } catch (Exception e) {}
        if (map_values == null) {
            map_values = new HashMap<>();
        }

        //TODO TEST
        //intent.putExtra("barcode_mod","single");
        //intent.putExtra("flag_avviso","0");
        //map_values.put("barcode_scanner_type","scanbot");

        if ( intent.hasExtra("tipologia_modello") ){
            bundle.putBoolean("database",false);
            Intent intent_error = new Intent(FullscreenScanFragmentContainerActivity.this, ErrorActivity.class);
            startActivity(intent_error);
            return;
        }
        else if ( intent.hasExtra("settings") ){
            Intent intent_settings = new Intent(FullscreenScanFragmentContainerActivity.this, SettingsActivity.class);
            startActivity(intent_settings);
            return;
        }
        else if ( intent.hasExtra("database") ){
            Intent intent_error = new Intent(FullscreenScanFragmentContainerActivity.this, ErrorActivity.class);
            bundle.putBoolean("database",true);
            intent_error.putExtras(bundle);
            startActivity(intent_error);
            return;
        }

        else if ( !intent.hasExtra("barcode_mod") ){
            AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenScanFragmentContainerActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
            builder.setTitle("Attenzione");
            builder.setMessage("L'applicazione può essere eseguita solo da IntegraaApp\n\nVersione: "+ Util.version_app);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
            return;
        }

        Scanner.setBarcodeType(map_values.getOrDefault("barcode_scanner_type",""));
        Scanner.getBarcode().initialize(this,map_values.getOrDefault("barcode_scanner_key",""));
        Scanner.getBarcode().onResume();

        String barcode_mod = intent.getStringExtra("barcode_mod");
        String with_check = intent.getStringExtra("with_check");
        if(with_check==null){
            with_check="";
        }
        bundle.putString("barcode_mod",barcode_mod);
        bundle.putString("with_check",with_check);
        ArrayList<String> barcode_value = intent.getStringArrayListExtra("barcode_value");
        //bundle.putString("barcode_value",barcode_value);
        bundle.putStringArrayList("barcode_value",barcode_value);
        String flag_avviso = intent.getStringExtra("flag_avviso");
        bundle.putString("flag_avviso",flag_avviso);
        String statusID = intent.getStringExtra("statusID");
        bundle.putString("statusID",statusID);
        String barcode_rule = intent.getStringExtra("barcode_rule");
        bundle.putString("barcode_rule",barcode_rule);
        String barcode_regexp = intent.getStringExtra("barcode_regexp");
        bundle.putString("barcode_regexp",barcode_regexp);

        String barcode_regexp_integraa = intent.getStringExtra("barcode_pattern");
        bundle.putString("barcode_pattern",barcode_regexp_integraa);

        boolean barcode_distinta_recapito_effettuare = intent.getBooleanExtra("barcode_distinta_recapito_effettuare",false);
        bundle.putBoolean("barcode_distinta_recapito_effettuare",barcode_distinta_recapito_effettuare);


        if (savedInstanceState == null) {

            fragment = FullscreenScanFragment.newInstance();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,fragment, null)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Scanner.getBarcode().onResume();
    }
    protected void onPause() {
        super.onPause();
        Scanner.getBarcode().onPause();
    }

    public void startScanning(){
        Scanner.getBarcode().start();
        //PreferencesApp.executeQuery("UPDATE preferences_app SET value = 'off' where key = ?","stop_barcode_recognized");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void init_error(final Activity mActivity) {
        if(defaultHandler == null){
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        if(applicationContext == null){
            applicationContext = getApplicationContext();
        }

        if(exceptionHandler == null){
            exceptionHandler = new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                    Log.e("Uncaught Exception", paramThrowable.getMessage());
                    logError(paramThrowable,mActivity);
                    defaultHandler.uncaughtException(paramThread, paramThrowable);

                }
            };

            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        }
    }


    private static void logError(final Throwable paramThrowable, Activity mActivity){
        try {

            String stackTrace = "";
            for (int i = 0; i < paramThrowable.getStackTrace().length; i++) {
                stackTrace += paramThrowable.getStackTrace()[i].toString() + "\n";
            }

            Log.e("Saving error...", "");

            Throwable tmp = paramThrowable;
            int j = 0;
            while ((tmp = tmp.getCause()) != null && j < 5) {
                j++;
                stackTrace += "Coused by:\n";
                for (int i = 0; i < tmp.getStackTrace().length; i++) {
                    stackTrace += tmp.getStackTrace()[i].toString() + "\n";
                }
            }
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdfWithSeconds = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ", Locale.ITALY);
            String currentDateTimeSecond = sdfWithSeconds.format(calendar.getTime());
            DBHelper dbHelper = new DBHelper(mActivity);
            dbHelper.insertError(currentDateTimeSecond,paramThrowable.getMessage().replace("'", ""),stackTrace.replace("'", ""));
            Log.e("Saved error:", paramThrowable.getMessage() + "\n" + stackTrace);
        }catch(Exception e){
            Log.e("ExceptionErrorSave",e.getMessage());

        }
    }
}