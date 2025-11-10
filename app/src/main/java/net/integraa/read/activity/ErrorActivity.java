package net.integraa.read.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.integraa.read.R;
import net.integraa.read.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class ErrorActivity extends AppCompatActivity {

    private ListView listView_errori;
    private Button button_pulisci_errori;
    private TextView textview_conteggio_errori;
    private DBHelper dbHelper;
    private ArrayAdapter<String> statusListAdapter;
    private List<String> formatsList = new ArrayList<String>();
    private List<String> ErrorsList = new ArrayList<String>();
    private Context mContext;
    private boolean database = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        textview_conteggio_errori = findViewById(R.id.textview_conteggio_errori);
        button_pulisci_errori = findViewById(R.id.button_pulisci_errori);
        listView_errori = findViewById(R.id.listView_errori);

        mContext = this;

        Bundle extras = getIntent().getExtras();

        if ( extras != null ) {
            database = extras.getBoolean("database");
        }

        dbHelper = new DBHelper(getApplicationContext());

        if ( database ){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    23);
            /*dbHelper.exportDB(mContext,"barcodeDB.db","net.integraa.read");*/
        }



        Cursor errors = dbHelper.getData();




        if ( errors.getCount() == 0 ){
            textview_conteggio_errori.setText("N° Errori: "+errors.getCount());
        }
        else{
            if (errors != null) {
                textview_conteggio_errori.setText("N° Errori: "+errors.getCount());
                if (errors.moveToFirst()) {
                    do {
                        ErrorsList.add(errors.getString(errors.getColumnIndex("id"))+"# "
                                +"[ "+errors.getString(errors.getColumnIndex("date"))+"] \n"+
                                errors.getString(errors.getColumnIndex("error")));
                    } while (errors.moveToNext());
                }
            }

            statusListAdapter = new ArrayAdapter<String>(this, /*android.R.layout.simple_list_item_1*/ R.layout.row, ErrorsList);
            ListView statusList = (ListView) this.findViewById(R.id.listView_errori);
            statusList.setAdapter(statusListAdapter);

            statusList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String titoloriga = (String) adapterView.getItemAtPosition(i);
                    String[] splitText = titoloriga.split("#");
                    Cursor errors = dbHelper.getDataInfo(splitText[0]);
                    String errore="";
                    if (errors != null) {
                        if (errors.moveToFirst()) {
                            do {
                                errore=errors.getString(errors.getColumnIndex("stack"));
                            } while (errors.moveToNext());
                        }
                    }
                    final String finalErrore = errore;
                    AlertDialog.Builder info = new AlertDialog.Builder(mContext);
                    info.setTitle("Errore");
                    info.setMessage(Html.fromHtml("<small>"+finalErrore+"</small>"));
                    info.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    info.setCancelable(false);
                    info.show();
                }
            });

        }

        button_pulisci_errori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(mContext);
                new android.app.AlertDialog.Builder(mContext)
                        .setView(edittext)
                        .setTitle("Password")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if ( edittext.getText().toString().equals("integraa1344") ){
                                    dbHelper.removeAllData();
                                }
                                else{
                                    Toast.makeText(mContext,"Password non valida",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == 23 ){

            if ( grantResults[0] == PackageManager.PERMISSION_DENIED ){
                AlertDialog.Builder info = new AlertDialog.Builder(mContext);
                info.setTitle("Attenzione");
                info.setMessage("Devi condcedere i permessi per effettuare l'export del database");
                info.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                info.setCancelable(false);
                info.show();
            }
            else {
                dbHelper.exportDB(mContext, "barcodeDB.db", "net.integraa.read");
            }
        }
    }
}