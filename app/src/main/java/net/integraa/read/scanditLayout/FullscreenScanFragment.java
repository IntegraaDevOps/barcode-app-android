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

import android.app.AlertDialog;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

//import androidx.camera.view.PreviewView;

import net.integraa.read.R;
import net.integraa.read.controller.Scanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class FullscreenScanFragment extends CameraPermissionFragment implements ScanViewModel.ResultListener {

    private ScanViewModel viewModel;
    static FullscreenScanFragment newInstance() {
        return new FullscreenScanFragment(null);
    }

    private AlertDialog dialog = null;
    private ArrayList<String> processing_id_validi = new ArrayList<>();
    private int bundle_barcode_singolo,status_id_esito,flag_avviso;
    private ArrayList<String> barcodeList = new ArrayList<>();
    private Button btn;
    private ActionBar actionBar;
    private ColorDrawable colorDrawable;

    private String ultimo_esito;
    private String ultimo_esito_codificato;

    private Bundle bundle;
    private Boolean isStopReadBarcode = false;
    //private DBHelperLite dbHelperLite;

    private ArrayList<String> posta_massiva_element = new ArrayList<>();

    private ArrayList<String> tmpArrayBarcode = new ArrayList<>();

    protected View viewCapture;
    //protected PreviewView fragment_scan_barcode_preview_view;

    private ArrayList<String> barcode = new ArrayList<>();
    private String barcode_input;
    private ArrayList<String> barcode_value_list = new ArrayList<String>();
    private ArrayList<String> barcode_value_list_fine_giornata = new ArrayList<String>();
    private String status = "";

    private String statusID = "";

    private String flag_barcode_rule = null; //Parametro passato da Giacenze
    private String barcode_regexp = null; //Paramentro passato da Giacenze

    private String barcode_regexp_integraa = null;
    private boolean barcode_distinta_recapito_effettuare = false;
    protected ResultListener<FullscreenScanFragment> readListener=null;
    protected CharSequence actionBarTitle = null;
    protected CharSequence actionBarSubitle = null;
    protected Drawable actionBarBackground = null;

    public FullscreenScanFragment(Bundle arguments) {
        super();
        if(arguments!=null) {
            setArguments(arguments);
        }
    }
    public FullscreenScanFragment() {
        super();
        Bundle b = new Bundle();
        b.putString("barcode_mod","single");
        b.putString("flag_avviso","0");
        b.putString("internal_management","1");
        setArguments(b);
    }

    public void setResultListener(ResultListener<FullscreenScanFragment> readListener) {
        this.readListener=readListener;
    }

    protected void setResult(int code, Intent data, boolean finish) {
        if (actionBarTitle != null) {
            actionBar.setTitle(actionBarTitle);
        }
        if (actionBarSubitle!= null) {
            actionBar.setSubtitle(actionBarSubitle);
        }
        if (actionBarBackground!= null) {
            actionBar.setBackgroundDrawable(actionBarBackground);
        }
        if(readListener!=null) {
            readListener.onResult(code, data, finish, this);
        }
        else {
            if (data!=null) {
                getActivity().setResult(code,data);
            }
            else {
                getActivity().setResult(code);
            }
            if(finish){
                getActivity().finish();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);

            Scanner.getBarcode().setVibrate(true);
            Scanner.getBarcode().setSound(false);

            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //getVibrateSound();

            viewModel = ViewModelProviders.of(this).get(ScanViewModel.class);

            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

            actionBarTitle=actionBar.getTitle();
            actionBarSubitle=actionBar.getSubtitle();
            actionBarBackground = new ColorDrawable(getResources().getColor(R.color.default_actionbar));

            actionBar.setTitle((Html.fromHtml("<font color=\"#000000\">Barcode Scanner</font>")));
            actionBar.setSubtitle((Html.fromHtml("<font color=\"#000000\">Integraa</font>")));
            colorDrawable = new ColorDrawable(Color.parseColor("#FEDA44"));
            actionBar.setBackgroundDrawable(colorDrawable);

            checkModel("neutral");

            if ( getArguments() != null ){
                flag_avviso = Integer.valueOf(getArguments().getString("flag_avviso"));
                flag_barcode_rule = getArguments().getString("barcode_rule");
                barcode_regexp = getArguments().getString("barcode_regexp");

                barcode_regexp_integraa = getArguments().getString("barcode_pattern");

                barcode_distinta_recapito_effettuare = getArguments().getBoolean("barcode_distinta_recapito_effettuare");



                if ( flag_avviso == 1 ){
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    alert.setTitle("Attenzione");
                    alert.setMessage("Si è rilevata un'incongruenza tra i prodotti da acquisire e quelli attualmente acquisiti.\nPer tale motivo si prega di sparare nuovamente i prodotti in proprio possesso.\n\nN.B. Terminata la fase di lettura dei prodotti clicca il tasto INDIETRO per concludere la procedura");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.setIcon(getActivity().getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("Attenzione");
            builder.setMessage("Si è verificato un problema durante l'apertura del lettore barcode \n\nErrore: "+e.getMessage());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {


        try {
            viewCapture = inflater.inflate(R.layout.fragment_scan, container, false);
            //fragment_scan_barcode_preview_view=viewCapture.findViewById(R.id.fragment_scan_barcode_preview_view);
        }
        catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("Attenzione");
            builder.setMessage("Si è verificato un problema durante la creazione del lettore \n\nErrore: "+e.getMessage());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        View barcodeScannerView = Scanner.getBarcode().getView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        barcodeScannerView.setLayoutParams(params);
        ((FrameLayout)viewCapture).addView(barcodeScannerView);
        //fragment_scan_layout.addView(barcodeScannerView);
        //barcodeScannerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //applyEdgeToEdge(viewCapture.findViewById(R.id.fragment_scan_root_view));
        Scanner.getBarcode().onResume();
        return viewCapture;
    }

    protected void applyEdgeToEdge(View rootView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars());
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginParams.topMargin = insets.top;
                marginParams.bottomMargin = insets.bottom;
                marginParams.leftMargin = insets.left;
                marginParams.rightMargin = insets.right;
                v.setLayoutParams(marginParams);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {

        try {

            viewModel.setListener(this);
            // Switch camera on to start streaming frames.
            // The camera is started asynchronously and will take some time to completely turn on.
            viewModel.startFrameSource();
            if (!isShowingDialog()) {
                viewModel.resumeScanning();
            }
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("Attenzione");
            builder.setMessage("Errore durante la creazione della schermata \nErrore: "+e.getMessage());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    private boolean isShowingDialog() {
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.destroyScanning();
    }

    @Override
    public void onPause() {

        try{
            super.onPause();
            viewModel.setListener(null);
            // Switch camera off to stop streaming frames.
            // The camera is stopped asynchronously and will take some time to completely turn off.
            // Until it is completely stopped, it is still possible to receive further results, hence
            // it's a good idea to first disable barcode tracking as well.
            viewModel.pauseScanning();
            viewModel.stopFrameSource();

        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("Attenzione");
            builder.setMessage("Si è verificato un errore durante la creazione della finestra\nErrore: "+e.getMessage());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }

    }

    @Override
    public void onCodeScanned(String barcodeResult) {

        String modality = "";
        String with_check = "";
        //String esito = checkIfInDistinta(barcodeResult.getData());

        Intent intent = new Intent();
        if ( getArguments() != null ){

            with_check = getArguments().getString("with_check","");
            modality = getArguments().getString("barcode_mod");
            barcode_value_list = getArguments().getStringArrayList("barcode_value");
            statusID = getArguments().getString("statusID");
            flag_barcode_rule = getArguments().getString("barcode_rule");
            //status = getArguments().getString("status");


            //1) Leggo il barcode prelevato dal lettore
            barcode_input = barcodeResult;

            if ( modality.equals("single") ){
                if(with_check.equals("1")){
                    multiBarcode(true);
                }
                else {
                    //Barcode singolo
                    intent.putExtra("barcode", barcode_input);
                    intent.setFlags(0);
                    setResult(99, intent, true);
                }
            }
            if ( modality.equals("multi") ) {
                multiBarcode();
                intent.putStringArrayListExtra("barcode", barcode_value_list);
                setResult(999, intent, false);
            }
            if ( modality.equals("annulla_singolo")){
                intent.putExtra("barcode", barcode_input);
                intent.setFlags(0);
                setResult(1000, intent, true);
            }
            if ( modality.equals("annulla_multi") ){
                multiBarcode();
                intent.putStringArrayListExtra("barcode", barcode_value_list);
                setResult(1000, intent, false);
            }

            if ( modality.equals("recapito_da_effettuare_single") ){

            }

            if ( modality.equals("recapito_da_effettuare_multi") ){
                multiBarcode();
                intent.putStringArrayListExtra("barcode", barcode_value_list);
                setResult(1002, intent, false);
            }
            if ( modality.equals("posta_tracciata") ){
                multiBarcodeFineGiornata();
                intent.putStringArrayListExtra("barcode", barcode_value_list_fine_giornata);
                setResult(600, intent, false);
            }
            if ( modality.equals("posta_massiva") ){
                multiBarcodeFineGiornata();
                intent.setFlags(0);
                intent.putStringArrayListExtra("barcode", barcode_value_list_fine_giornata);
                setResult(Integer.parseInt(statusID), intent, false);
            }
            if ( modality.equals("posta_massiva_distinta") ){

                intent.putExtra("barcode", barcode_input);
                intent.setFlags(0);
                setResult(991, intent, true);

                /*multiBarcodeFineGiornata();
                intent.setFlags(0);
                intent.putStringArrayListExtra("barcode", barcode_value_list_fine_giornata);
                getActivity().setResult(Integer.parseInt(statusID), intent);*/
                //getActivity().finish();
            }
        }
    }

    private void checkControlloFineGiornata(int tipologia, String data) {
        String productName = "";
        if ( tipologia == 500 ){
            productName = "massiv";
        }
        if ( tipologia == 600 ){
            productName = "racco";
        }

        String query_condition = "";

        query_condition = getConditionQuery();

        /*Cursor rs_type_massivo = dbHelperLite.getType(productName,query_condition);
        if (rs_type_massivo != null) {
            if (rs_type_massivo.moveToFirst()) {
                do {
                    posta_massiva_element.add(rs_type_massivo.getString((rs_type_massivo.getColumnIndex("PRODUCTBARCODE"))));
                } while (rs_type_massivo.moveToNext());
            }
        }
        rs_type_massivo.close();*/

        /*if ( !posta_massiva_element.contains(data) ){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Attenzione");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            if ( tipologia == 500 ) {
                builder.setMessage("Questo barcode " + data + " non è presente nelle tue distinte da lavorare oppure non è un prodotto di POSTA MASSIVA");
                //RiepilogoBarcode.executeQuery("INSERT INTO riepilogo_barcode (barcode,lettura_automatica,stato,tipologia,found) VALUES ('"+data+"','n','"+status_id_esito+"','PM','n') ");
                List<ProductsNotFound> productsNotFounds = ProductsNotFound.findWithQuery(ProductsNotFound.class,"Select * from products_not_found where barcode = ? and type = ?",data,"PM");
                if ( productsNotFounds.size() == 0 ) {
                    ProductsNotFound.executeQuery("INSERT INTO products_not_found (barcode,date,type) VALUES ('" + data + "','" + formatter.format(date) + "','PM') ");
                }
            }
            if ( tipologia == 600 ) {
                builder.setMessage("Questo barcode " + data + " non è presente nelle tue distinte da lavorare oppure non è un prodotto di POSTA RACCOMANDATA");
                //RiepilogoBarcode.executeQuery("INSERT INTO riepilogo_barcode (barcode,lettura_automatica,stato,tipologia,found) VALUES ('"+data+"','n','"+status_id_esito+"','PT','n') ");
                List<ProductsNotFound> productsNotFounds = ProductsNotFound.findWithQuery(ProductsNotFound.class,"Select * from products_not_found where barcode = ? and type = ?",data,"PT");
                if ( productsNotFounds.size() == 0 ) {
                    ProductsNotFound.executeQuery("INSERT INTO products_not_found (barcode,date,type) VALUES ('"+data+"','"+formatter.format(date)+"','PT') ");
                }
            }

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewModel.resumeScanning();
                }
            });
            builder.setCancelable(false);
            builder.show();

        }
        else{
            List<DeliveryProducts> deliveryProductsList = DeliveryProducts.findWithQuery(DeliveryProducts.class,"Select * from delivery_products where productbarcode = ?",data.toUpperCase().trim());

            if ( tipologia == 500 ) {
                List<TableBarcodeMassiva> check = TableBarcodeMassiva.findWithQuery(TableBarcodeMassiva.class, "Select * from table_barcode_massiva where barcode = ? AND tipologia = 'PM' ", data.toUpperCase());
                if (check.size() == 0 ){
                    List<DescriptionMap> descriptionMapList = DescriptionMap.findWithQuery(DescriptionMap.class,"Select * from description_map where productid = ? ", String.valueOf(status_id_esito));
                    CountValuesRiepilogo.executeQuery("INSERT INTO count_values_riepilogo (barcode,type,description,status,found) VALUES ('"+data+"','PM','"+descriptionMapList.get(0).getDescription()+"','"+status_id_esito+"','s') ");
                    TemporaryTableBarcode.executeQuery("INSERT INTO table_barcode_massiva (barcode,status_id,tracking,tipologia,letto) VALUES ('" + data.trim() + "','"+ String.valueOf(status_id_esito)+"','"+deliveryProductsList.get(0).getTracking_id()+"','PM','s')");
                    Toast.makeText(getContext(),"Barcode "+data+" inserito correttamente", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(),"Barcode "+data+" già letto", Toast.LENGTH_LONG).show();
                }
            }
            if ( tipologia == 600 ) {
                List<TableBarcodeMassiva> check = TableBarcodeMassiva.findWithQuery(TableBarcodeMassiva.class, "Select * from table_barcode_massiva where barcode = ? AND tipologia = 'PT' ", data.toUpperCase());
                if (check.size() == 0 ){
                    List<DescriptionMap> descriptionMapList = DescriptionMap.findWithQuery(DescriptionMap.class,"Select * from description_map where productid = ? ", String.valueOf(status_id_esito));
                    CountValuesRiepilogo.executeQuery("INSERT INTO count_values_riepilogo (barcode,type,description,status,found) VALUES ('"+data+"','PT','"+descriptionMapList.get(0).getDescription()+"','"+status_id_esito+"','s') ");
                    TemporaryTableBarcode.executeQuery("INSERT INTO table_barcode_massiva (barcode,status_id,tracking,tipologia,letto) VALUES ('" + data.trim() + "','"+ String.valueOf(status_id_esito)+"','"+deliveryProductsList.get(0).getTracking_id()+"','PT','s')");
                    Toast.makeText(getContext(),"Barcode "+data+" inserito correttamente", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(),"Barcode "+data+" già letto", Toast.LENGTH_LONG).show();
                }
            }

            viewModel.resumeScanning();
        }*/

    }

    /*public String checkIfInDistinta(String lastBarcode) {
        boolean found = false;

        ArrayList<String> processID = new ArrayList<>();

        String process = "( ";

        for(int x = 0; x <processing_id_validi.size(); x++){
            processID.add(processing_id_validi.get(x));
            process += "processingid = "+processing_id_validi.get(x)+" or ";
        }

        process = process.substring(0,process.length()-3);
        process+=")";

        Log.i("InfoQuery: ",process);

        List<DeliveryProducts> last_status_id = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where "+process+" and productbarcode = ? and laststatusid is not null", lastBarcode);
        if ( last_status_id.size() == 0 ){
            return "";
        }

        //List<DeliveryProducts> check_ultimo_stato = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where "+process+" and productbarcode = ? and laststatusid is not null AND laststatusid <> 59", lastBarcode);
        List<DeliveryProducts> check_ultimo_stato = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where "+process+" and productbarcode = ?", lastBarcode);
        if (check_ultimo_stato.size() != 0) {
            if ( check_ultimo_stato.get(0).getLast_status_id().equals("59")){
                return "";
            }
            else {
                List<DescriptionMap> descriptionMaps = DescriptionMap.findWithQuery(DescriptionMap.class, "Select * from description_map where productid = ?", check_ultimo_stato.get(0).getLast_status_id());
                ultimo_esito_codificato = descriptionMaps.get(0).getDescription();
                return "lavorato";
            }
        }
        else{
            List<DeliveryProducts> notes = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where "+process+" and productbarcode = ? ", lastBarcode);
            if ( notes.size() == 0 ){
                return "not_found";
            }
        }

        *//*for (int x = 0; x < processing_id_validi.size(); x++) {
            List<DeliveryProducts> check_ultimo_stato = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where processingid = ? and productbarcode = ? and laststatusid is not null ", processing_id_validi.get(x), lastBarcode);
            if (check_ultimo_stato.size() != 0) {
                List<DescriptionMap> descriptionMaps = DescriptionMap.findWithQuery(DescriptionMap.class, "Select * from description_map where productid = ?", check_ultimo_stato.get(0).getLast_status_id());
                ultimo_esito_codificato = descriptionMaps.get(0).getDescription();
                return "lavorato";
            } else {
                List<DeliveryProducts> notes = DeliveryProducts.findWithQuery(DeliveryProducts.class, "Select * from delivery_products where processingid = ? and productbarcode = ? ", processing_id_validi.get(x), lastBarcode);
                if (notes.size() == 0) {
                    return "not_found";
                }
            }
            return "valido";
        }*//*
        return "";
    }*/

    public void checkModel(String messaggio){
    }

    public void checkValue_new(String data){
        String barcode = data;
    }

    private String getConditionQuery(){
        String query = "( ";
        for(int i=0; i<processing_id_validi.size(); i++){
            query += " processingid = "+processing_id_validi.get(i).trim()+" OR ";
        }
        query = query.substring(0,query.length()-3);
        query += ")";

        return query;
    }

    public void multiBarcode(){
        multiBarcode(false);
    }
    public void multiBarcode(boolean once){
        if ( /*barcode_value_list.get(0).isEmpty()*/ barcode_value_list.size() != 0 ){
            if ( barcode_value_list.get(0).isEmpty() ) {
                barcode_value_list.remove(0);
            }
        }
        for (int i = 0; i < barcode_value_list.size(); i++) {
            if (!tmpArrayBarcode.contains(barcode_value_list.get(i))) {
                tmpArrayBarcode.add(barcode_value_list.get(i));
            }
        }
        viewModel.pauseScanning();
        AlertDialog.Builder info = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        if (tmpArrayBarcode.contains(barcode_input)) {
            checkModel("warning");
            Toast.makeText(getContext(), "Attenzione barcode "+barcode_input+" già letto", Toast.LENGTH_LONG).show();
            viewModel.resumeScanning();
        } else {

            if ( flag_barcode_rule != null  ){
                info.setTitle("Attenzione");
                //if (Pattern.matches("^(AG?B|RR?B)[0-9]{10}$",barcode_input) ){
                if (Pattern.matches(barcode_regexp,barcode_input) ){
                    checkModel("success");
                    Toast.makeText(getContext(),"OK",Toast.LENGTH_LONG).show();
                    info.setMessage("Il barcode " + barcode_input + " è corretto ? ");
                    info.setPositiveButton("Si, è corretto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(once) {
                                barcode_value_list.clear();
                                barcode_value_list.add(barcode_input);
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra("barcode", barcode_value_list);
                                setResult(1001, intent, true);
                                return;
                            }
                            barcode_value_list.add(barcode_input);
                            viewModel.resumeScanning();
                            actionBar.setSubtitle((Html.fromHtml("<strong><big><font color=\"#000000\">Barcode letti: "+barcode_value_list.size()+"</font></big></strong>")));
                        }
                    });
                    info.setNegativeButton("No, non è corretto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewModel.resumeScanning();
                        }
                    });
                }
                else{
                    checkModel("error");
                    info.setMessage("Il barcode " + barcode_input + " non corrisponde ai criteri impostati ");
                    info.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewModel.resumeScanning();
                        }
                    });
                    Toast.makeText(getContext(),"NOT OK",Toast.LENGTH_LONG).show();
                }
                info.setCancelable(false);
                info.show();
            }
            else{
                info.setTitle("Attenzione");
                info.setMessage("Il barcode " + barcode_input + " è corretto ? ");
                info.setPositiveButton("Si, è corretto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(once) {
                            barcode_value_list.clear();
                            barcode_value_list.add(barcode_input);
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra("barcode", barcode_value_list);
                            setResult(1001, intent, true);
                            return;
                        }
                        barcode_value_list.add(barcode_input);
                        viewModel.resumeScanning();
                        actionBar.setSubtitle((Html.fromHtml("<strong><big><font color=\"#000000\">Barcode letti: "+barcode_value_list.size()+"</font></big></strong>")));
                    }
                });
                info.setNegativeButton("No, non è corretto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.resumeScanning();
                    }
                });
                info.setCancelable(false);
                info.show();
            }
        }

        if ( flag_barcode_rule == null ) {
            //Remove duplicate
            HashSet hs = new HashSet();
            hs.addAll(barcode_value_list);
            barcode_value_list.clear();
            barcode_value_list.addAll(hs);
        }
    }

    private void multiBarcodeFineGiornata(){
        viewModel.pauseScanning();
        AlertDialog.Builder info = new AlertDialog.Builder(getContext(),AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        if ( barcode_value_list_fine_giornata.contains(barcode_input) ){
            Toast.makeText(getContext(),"Attenzione barcode "+barcode_input+" già letto",Toast.LENGTH_LONG).show();
            checkModel("warning");
            viewModel.resumeScanning();
        }
        else{
            checkModel("success");

            if (Pattern.matches(barcode_regexp_integraa,barcode_input) ){
                checkModel("success");
                Toast.makeText(getContext(),"OK",Toast.LENGTH_LONG).show();
                info.setMessage("Il barcode " + barcode_input + " è corretto ? ");
                info.setPositiveButton("Si, è corretto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        barcode_value_list_fine_giornata.add(barcode_input);
                        viewModel.resumeScanning();
                        actionBar.setSubtitle((Html.fromHtml("<strong><big><font color=\"#000000\">Barcode letti: "+barcode_value_list_fine_giornata.size()+"</font></big></strong>")));
                    }
                });
                info.setNegativeButton("No, non è corretto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.resumeScanning();
                    }
                });
                info.setCancelable(false);
                info.show();
            }
            else{
                checkModel("error");
                info.setMessage("Il barcode " + barcode_input + " non corrisponde ai criteri impostati ");
                info.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.resumeScanning();
                    }
                });
                info.show();
                Toast.makeText(getContext(),"NOT OK",Toast.LENGTH_LONG).show();
            }


            /*info.setTitle("Attenzione");
            info.setMessage("Il barcode "+barcode_input+" è corretto ? ");
            info.setPositiveButton("Si, è corretto", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    barcode_value_list_fine_giornata.add(barcode_input);
                    viewModel.resumeScanning();
                    actionBar.setSubtitle((Html.fromHtml("<font color=\"#000000\">Barcode letti: "+barcode_value_list_fine_giornata.size()+"</font>")));
                }
            });
            info.setNegativeButton("No, non è corretto", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewModel.resumeScanning();
                }
            });
            info.setCancelable(false);
            info.show();*/
        }
    }

    public interface ResultListener<T> {
        void onResult(int code, Intent data, boolean finish, T object);
    }

}
