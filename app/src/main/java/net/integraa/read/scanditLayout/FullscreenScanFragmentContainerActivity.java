package net.integraa.read.scanditLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import net.integraa.read.MainApplication;
import net.integraa.read.R;
import net.integraa.read.Util;
import net.integraa.read.ZActivity;
import net.integraa.read.activity.ErrorActivity;
import net.integraa.read.activity.SettingsActivity;
import net.integraa.read.controller.Scanner;
import net.integraa.read.dbhelper.DBHelper;
import net.integraa.read.dbhelper.DialogHelper;
import net.integraa.read.network.APIClient;
import net.integraa.read.network.ConfigNet;
import net.integraa.read.network.IntegraaApi;
import net.integraa.read.network.LoginData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullscreenScanFragmentContainerActivity extends ZActivity {

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
        //MainApplication.mountObbFile(this);
        setContentView(R.layout.activity_fullscreen_scan_fragment_container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.default_actionbar)));

        init_error(this);
        //double x=5/0;
        startScanning();

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
        //map_values.put("barcode_scanner_type","scandit");
        //map_values.put("barcode_scanner_key","AvUmm2DZRgP5FdlPGSNsju4V3j5LFKttRBG0GB8eXw6rRxdRi0T1zYVHDEGMGcaY1DO3D9N1+obhUGGwkBZQraZ6Pw9EcyCptFljwNwc0gvLNGtSPwOvBqEWUnSEdoW8Q2aYKoEDJhKTc/QqmmtxPMxt7L1LKR6iimzVbc5B5ok1Qj8uNEbwRagULf9XcqoBwxrzrkhKWdelcZVWfmQHGmQ6UfKxRVpkK10loSFvM+0WLzavOECgmDNwV06sUv6haEFLg2ovYJcZD8kOzVctSWNcX3DzOWQGhTNlHpp2p6FUVG54JjPrtR892yMFVkIdwlsKcvZG1uvYUN3nWhf6qgt683NBex3FA1kxBvMI+85xf1qYiXxZhJ9KSPJWLUaf0l5iIOlWeyWmLte1EVYZQCJVw4QKOhz4vwz67gkb8Fl9V1TlcVOSqcV6txvXdVI8IlYzuChJrReuPW9bQnoLvNxpbiq9VZ671GSwnRIUcQ3pUMjS+UuJCphHhv7qSoS85FqVJmBw0+5iMmnlHnHZCtl5ckahInJNFGd2qV98H5auNG0oJ2rxx+5b02q4UUTLPSjwQgRKqQaFQOzPMkDymvwNyNivaXZYsEEGNLxLIUOJVB2ow1El24Uf8BDXbYhjxk3RXEVFWlMLNPlJJVzsMylS/LkqSVMbsElo1btB2oa3S+b1Tn2Q0LYfh3ZIXm3/lkLGqXN+WZW4T/opyXNsu+VXD8ipFdBgKh2vujhE2QdOTqyeIVWrOUE6QBYqRNnXw0NJz2poUjTfafc1b37Wtzxx/3NtdoFsN12KkvZXp5zSaRFscnj5XEhbBzS1ftCHTHSgadtf5oI9X8INEFvE1yJgoO+IbcmRTHdhrEZeJ6qaGqaoQG+WahodRWBFUAiWbGW0Gf5+ZemBeW1gdAtGXJZrbdRkcFUtG0kVuctrSJshcAc98QWe9goybKVoUm/HKFJUyep7zanHFro9MiUf3vZh7z41M95JsHgxjILhvD8hZlZ+MwwM+QIfnqUQgv5Bh2ixiaMK0/nkZTbYSJ9455F1ccyAaZZQ3wecmI7gNILC9Gix0cJ6zDnT5Dyd9d1qzOFsazrc/VYGeikJY2tv/LgwZEdcQHHkkxOdwxmDqnRGIq29rklSlpT0TkBVhVG61rxvZhWrYwIUb1/A5vtNfPvZy5C+/VLGfLowi0XalAq7VpRBornLJ6zYOQPvne26dQVFImR81cfbtTFHmeriYUBoYJxt2S9f8Ki2436i4xzf8kf//Q6LP77eDeUu4NNamZwJEc5UG45QigXcMkbIOw3ImIczdYlTrJoa9QTZ6stbc/sk7eLfLo2hzdhqpsaKUj1hH1X+FHl2jWeplnIjvWaZAv08EPlA7hVI8VmOR9hg8COctoV0tUDqyVdWr1p3PwIsXOIzOP2oME0suNF74uMW8zjt85peZK9ZKaXIgyOnCNj6J3nTCMZwoqt5uuDy/5s6mSpjAB8FOJWbYFwO68PjBSIYxt3R1olGzYZD7pvan85cMn6XBnTPJTBst50Ezh/Ohff8Zozkwuwg9A4UJpvmEXUMZiU/1UHeYQR8itNL1I1SAdW8OYvcHRaTcUHRr4QlUTSoZTyvwcNYcqM4UmhYEJTEQ2lVEdDV3czAB0z3ZX54Q3cEx/XB4n1n5oO36SsA10UF");

        start(intent, savedInstanceState);
    }

    @Override
    public void finish() {
        if (currentFragment instanceof ReadFragment) {
            return;
        }
        super.finish();
    }


    protected Fragment currentFragment=null;
    protected Fragment setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fragment, null)
                .commit();
        currentFragment=fragment;
        return currentFragment;
    }

    public void start(Intent intent, Bundle savedInstanceState) {

        Bundle bundle = new Bundle();

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
            ConfigNet.setToken("");
            IntegraaApi integraaApi= APIClient.getClient().create(IntegraaApi.class);
            DialogHelper.clientAccessInput(FullscreenScanFragmentContainerActivity.this, getString(R.string.user_access), "", null, new DialogHelper.DialogInputsInterface() {
                @Override
                public void onClick(DialogInterface dialog, int which, String[] inputs) {
                    if(which==DialogInterface.BUTTON_POSITIVE) {
                        ProgressDialog progressDialog = new ProgressDialog(FullscreenScanFragmentContainerActivity.this);
                        progressDialog.setTitle(R.string.access_waiting);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        integraaApi.doLogin(RequestBody.create(inputs[0],MediaType.parse("text/plain")),RequestBody.create(inputs[1],MediaType.parse("text/plain"))).enqueue(new Callback<LoginData>() {
                            @Override
                            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                if (response.body()==null||!response.body().getCode().equals("0")) {
                                    String msg="";
                                    if (response.body()!=null&&response.body().getMessage()!=null) {
                                        msg = response.body().getMessage();
                                    }
                                    onFailure(call,null,msg);
                                    return;
                                }
                                ConfigNet.setToken(response.body().getToken());
                                ConfigNet.setBarcodeScannerKey(response.body().getBarcode_scanner_key());
                                ConfigNet.setBarcodeScannerType(response.body().getBarcode_scanner_type());
                                progressDialog.dismiss();
                                dialog.dismiss();
                                setFragment(new ReadFragment());
                            }
                            @Override
                            public void onFailure(Call<LoginData> call, Throwable t) {
                                onFailure(call, t, "");
                            }
                            public void onFailure(Call<LoginData> call, Throwable t, String message) {
                                DialogHelper.alert(FullscreenScanFragmentContainerActivity.this,"",getString(R.string.login_failed)+"\n"+message);
                                progressDialog.dismiss();
                            }
                        });
                    }
                    else {
                        dialog.dismiss();
                        finish();
                    }
                }
            });
            return;
            /*
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
            */
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

            setFragment(fragment);

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
        if (currentFragment!=null && currentFragment instanceof ReadFragment) {
            if (((ReadFragment)currentFragment).onBackPressed()) {
                return;
            }
            else {
                DialogHelper.confirm(this, getString(R.string.confirm), getString(R.string.confirm_exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==DialogInterface.BUTTON_POSITIVE) {
                            System.exit(0);
                        }
                    }
                });
            }
        }
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