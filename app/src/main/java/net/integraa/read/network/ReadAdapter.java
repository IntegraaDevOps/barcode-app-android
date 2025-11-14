package net.integraa.read.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDividerItemDecoration;

import net.integraa.read.R;
import net.integraa.read.dbhelper.DialogHelper;
import net.integraa.read.scanditLayout.FullscreenScanFragmentContainerActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.ViewHolder> {
    protected Context context=null;
    protected RecyclerView parent=null;
    protected List<ReadItem> items=new ArrayList<>(0);

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.parent = recyclerView;
        this.context = parent.getContext();
        MaterialDividerItemDecoration decoration = new MaterialDividerItemDecoration(this.context, DividerItemDecoration.VERTICAL);
        decoration.setDividerColor(ContextCompat.getColor(this.context,R.color.dark_dark_grey));
        this.parent.addItemDecoration(decoration);
        super.onAttachedToRecyclerView(recyclerView);
        refresh();
    }

    public void refresh() {
        IntegraaApi integraaApi= APIClient.getClient().create(IntegraaApi.class);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.read_data_waiting);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        integraaApi.getReads(ConfigNet.getToken()).enqueue(new Callback<ReadData>() {
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
                items=response.body().getItems();
                notifyDataSetChanged();
                parent.requestLayout();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ReadData> call, Throwable t) {
                onFailure(call, t, "");
            }
            public void onFailure(Call<ReadData> call, Throwable t, String message) {
                DialogHelper.alert(context,"",context.getString(R.string.read_data_failed)+"\n"+message);
                progressDialog.dismiss();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()) .inflate(R.layout.recycler_read, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(viewHolder==null) {
            return;
        }
        ReadItem item = items.get(position);
        if(item==null) {
            return;
        }
        String datetext="";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
            Date date = format.parse(item.getDate());
            format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            datetext = format.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        viewHolder.getReadDate().setText(datetext);
        viewHolder.getReadValue().setText(item.getRead());
        viewHolder.getReadSerial().setText(item.getSerial());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView readDate;
        protected TextView readValue;
        protected TextView readSerial;

        public ViewHolder(@NonNull View view) {
            super(view);
            readDate = view.findViewById(R.id.readDate);
            readValue = view.findViewById(R.id.readValue);
            readSerial = view.findViewById(R.id.readSerial);
        }

        public TextView getReadDate() {
            return readDate;
        }

        public TextView getReadSerial() {
            return readSerial;
        }

        public TextView getReadValue() {
            return readValue;
        }
    }
}
