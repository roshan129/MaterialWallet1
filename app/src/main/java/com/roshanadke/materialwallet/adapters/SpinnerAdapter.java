package com.roshanadke.materialwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.SpinnerViewHolder> {

    private OnSpinnerItemClickListener onSpinnerItemClickListener;
    private Context context;
    private ArrayList<HashMap<String, String>> spinner_list;


    public SpinnerAdapter(Context context, ArrayList<HashMap<String, String>> spinner_list
            , OnSpinnerItemClickListener onSpinnerItemClickListener) {
        this.context = context;
        this.spinner_list = spinner_list;
        this.onSpinnerItemClickListener = onSpinnerItemClickListener;

    }

    @NonNull
    @Override
    public SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.category_list_layout, parent, false);
        return new SpinnerViewHolder(view, onSpinnerItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SpinnerViewHolder holder, int position) {
        holder.txt_list_item.setText(spinner_list.get(position).get("category"));
    }

    @Override
    public int getItemCount() {
        return spinner_list.size();
    }


    public class SpinnerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_list_item;
        ImageView imgv_delete;
        OnSpinnerItemClickListener onSpinnerItemClickListener;

        SpinnerViewHolder(@NonNull View itemView,
                          OnSpinnerItemClickListener onSpinnerItemClickListener) {
            super(itemView);
            this.onSpinnerItemClickListener = onSpinnerItemClickListener;
            txt_list_item = itemView.findViewById(R.id.txt_new_category);
            imgv_delete = itemView.findViewById(R.id.imgv_delete);

            imgv_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onSpinnerItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnSpinnerItemClickListener {
        void onItemClick(int position);
    }
}