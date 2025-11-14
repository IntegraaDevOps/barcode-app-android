package net.integraa.read.scanditLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.integraa.read.R;
import net.integraa.read.controller.Scanner;
import net.integraa.read.controller.interfaces.Barcode;
import net.integraa.read.controller.scanners.BarcodeDefault;
import net.integraa.read.dbhelper.DialogHelper;
import net.integraa.read.network.APIClient;
import net.integraa.read.network.ConfigNet;
import net.integraa.read.network.IntegraaApi;
import net.integraa.read.network.LoginData;
import net.integraa.read.network.ReadAdapter;
import net.integraa.read.network.ReadData;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadFragment  extends Fragment {
    protected View viewCapture;
    protected RecyclerView recyclerView;
    protected Button readAdd;
    protected Button readRefresh;
    protected ReadAdapter readAdapter;
    protected AlertDialog readAlertDialog=null;

    public boolean onBackPressed() {
        if(readAlertDialog!=null) {
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Scanner.setBarcodeType(ConfigNet.getBarcodeScannerType());
        Scanner.getBarcode().initialize(getActivity(),ConfigNet.getBarcodeScannerKey());
        Scanner.getBarcode().onResume();
        Scanner.getBarcode().applyTypes(Arrays.asList(Barcode.Type.Code128,Barcode.Type.ITF));
        viewCapture = inflater.inflate(R.layout.fragment_read, container, false);
        readAdd = viewCapture.findViewById(R.id.button_read_add);
        readRefresh = viewCapture.findViewById(R.id.button_read_refresh);
        recyclerView = viewCapture.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        readAdapter=new ReadAdapter();
        recyclerView.setAdapter(readAdapter);
        readAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntegraaApi integraaApi= APIClient.getClient().create(IntegraaApi.class);
                readAlertDialog = DialogHelper.readInput(getActivity(), getString(R.string.add_read), "", null, new DialogHelper.DialogInputsInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, String[] inputs) {
                        if(which==DialogInterface.BUTTON_POSITIVE) {
                            ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setTitle(R.string.add_read_waiting);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            integraaApi.addRead(ConfigNet.getToken(),RequestBody.create(inputs[0],MediaType.parse("text/plain")),RequestBody.create(inputs[1],MediaType.parse("text/plain"))).enqueue(new Callback<ReadData>() {
                                @Override
                                public void onResponse(Call<ReadData> call, Response<ReadData> response) {
                                    if (response.body()==null||!response.body().getCode().equals("0")) {
                                        String msg="";
                                        if (response.body()!=null&&response.body().getMessage()!=null) {
                                            msg = response.body().getMessage();
                                        }
                                        onFailure(call,null,msg);
                                        return;
                                    }
                                    ConfigNet.setToken(response.body().getToken());
                                    readAdapter.refresh();
                                    progressDialog.dismiss();
                                    readAlertDialog=null;
                                    dialog.dismiss();
                                }
                                @Override
                                public void onFailure(Call<ReadData> call, Throwable t) {
                                    onFailure(call, t, "");
                                }
                                public void onFailure(Call<ReadData> call, Throwable t, String message) {
                                    DialogHelper.alert(getActivity(),"",getString(R.string.add_read_failed)+"\n"+message);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else {
                            readAlertDialog=null;
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        readRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readAdapter.refresh();
            }
        });
        return viewCapture;
    }
}
