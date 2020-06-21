package com.nk.studioshringaar.ui.account.address;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nk.studioshringaar.R;
import com.nk.studioshringaar.adapter.AddressData;

import java.util.ArrayList;

public class ShowAddressAdapter extends RecyclerView.Adapter<ShowAddressAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AddressData> arrayList;
    private String UID;
    private FirebaseFirestore db;
    private View.OnClickListener mOnItemClickListener;
    private static final String TAG = "ShowAddressAdapter";

    public ShowAddressAdapter(Context context, ArrayList<AddressData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public ShowAddressAdapter(Context context, ArrayList<AddressData> arrayList, String uid, FirebaseFirestore db) {
        this.context = context;
        this.arrayList = arrayList;
        UID = uid;
        this.db = db;
    }

    @Override
    public ShowAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_list_two, parent, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(final ShowAddressAdapter.ViewHolder holder, int position) {
        final AddressData p = arrayList.get(position);
//        Glide.with(context)
//            .load(p.getImg())
//            .into(holder.img);

        Log.e("ShowAddressAdapter","in addressadapted");

        holder.phone.setText(p.getPhone());
        holder.t.setText(p.getName());

        holder.addr.setText(p.getAddress());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.document("user/"+ UID +"/address/"+ p.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView phone, addr;
        TextView t;
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.first);
            addr = itemView.findViewById(R.id.addr);
            phone = itemView.findViewById(R.id.phone);
            delete = itemView.findViewById(R.id.delete);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}