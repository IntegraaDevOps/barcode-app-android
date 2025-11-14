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
        Scanner.setBarcodeType("scandit");
        Scanner.getBarcode().initialize(getActivity(),"AvUmm2DZRgP5FdlPGSNsju4V3j5LFKttRBG0GB8eXw6rRxdRi0T1zYVHDEGMGcaY1DO3D9N1+obhUGGwkBZQraZ6Pw9EcyCptFljwNwc0gvLNGtSPwOvBqEWUnSEdoW8Q2aYKoEDJhKTc/QqmmtxPMxt7L1LKR6iimzVbc5B5ok1Qj8uNEbwRagULf9XcqoBwxrzrkhKWdelcZVWfmQHGmQ6UfKxRVpkK10loSFvM+0WLzavOECgmDNwV06sUv6haEFLg2ovYJcZD8kOzVctSWNcX3DzOWQGhTNlHpp2p6FUVG54JjPrtR892yMFVkIdwlsKcvZG1uvYUN3nWhf6qgt683NBex3FA1kxBvMI+85xf1qYiXxZhJ9KSPJWLUaf0l5iIOlWeyWmLte1EVYZQCJVw4QKOhz4vwz67gkb8Fl9V1TlcVOSqcV6txvXdVI8IlYzuChJrReuPW9bQnoLvNxpbiq9VZ671GSwnRIUcQ3pUMjS+UuJCphHhv7qSoS85FqVJmBw0+5iMmnlHnHZCtl5ckahInJNFGd2qV98H5auNG0oJ2rxx+5b02q4UUTLPSjwQgRKqQaFQOzPMkDymvwNyNivaXZYsEEGNLxLIUOJVB2ow1El24Uf8BDXbYhjxk3RXEVFWlMLNPlJJVzsMylS/LkqSVMbsElo1btB2oa3S+b1Tn2Q0LYfh3ZIXm3/lkLGqXN+WZW4T/opyXNsu+VXD8ipFdBgKh2vujhE2QdOTqyeIVWrOUE6QBYqRNnXw0NJz2poUjTfafc1b37Wtzxx/3NtdoFsN12KkvZXp5zSaRFscnj5XEhbBzS1ftCHTHSgadtf5oI9X8INEFvE1yJgoO+IbcmRTHdhrEZeJ6qaGqaoQG+WahodRWBFUAiWbGW0Gf5+ZemBeW1gdAtGXJZrbdRkcFUtG0kVuctrSJshcAc98QWe9goybKVoUm/HKFJUyep7zanHFro9MiUf3vZh7z41M95JsHgxjILhvD8hZlZ+MwwM+QIfnqUQgv5Bh2ixiaMK0/nkZTbYSJ9455F1ccyAaZZQ3wecmI7gNILC9Gix0cJ6zDnT5Dyd9d1qzOFsazrc/VYGeikJY2tv/LgwZEdcQHHkkxOdwxmDqnRGIq29rklSlpT0TkBVhVG61rxvZhWrYwIUb1/A5vtNfPvZy5C+/VLGfLowi0XalAq7VpRBornLJ6zYOQPvne26dQVFImR81cfbtTFHmeriYUBoYJxt2S9f8Ki2436i4xzf8kf//Q6LP77eDeUu4NNamZwJEc5UG45QigXcMkbIOw3ImIczdYlTrJoa9QTZ6stbc/sk7eLfLo2hzdhqpsaKUj1hH1X+FHl2jWeplnIjvWaZAv08EPlA7hVI8VmOR9hg8COctoV0tUDqyVdWr1p3PwIsXOIzOP2oME0suNF74uMW8zjt85peZK9ZKaXIgyOnCNj6J3nTCMZwoqt5uuDy/5s6mSpjAB8FOJWbYFwO68PjBSIYxt3R1olGzYZD7pvan85cMn6XBnTPJTBst50Ezh/Ohff8Zozkwuwg9A4UJpvmEXUMZiU/1UHeYQR8itNL1I1SAdW8OYvcHRaTcUHRr4QlUTSoZTyvwcNYcqM4UmhYEJTEQ2lVEdDV3czAB0z3ZX54Q3cEx/XB4n1n5oO36SsA10UF");
        Scanner.getBarcode().applyTypes(Arrays.asList(Barcode.Type.Code128,Barcode.Type.ITF));
        Scanner.getBarcode().onResume();
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
