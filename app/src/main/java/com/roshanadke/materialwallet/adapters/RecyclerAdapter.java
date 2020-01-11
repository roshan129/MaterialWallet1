package com.roshanadke.materialwallet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roshanadke.materialwallet.R;

import java.util.ArrayList;
import java.util.HashMap;

/*
public class RecyclerAdapter extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerAdapter.ProgramViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> expense_list;

    public RecyclerAdapter(Context context, ArrayList<HashMap<String, String>> exp_list) {
        this.context = context;
        this.expense_list = exp_list;
    }

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.wallet_list, parent, false);
        return new ProgramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position) {
        holder.exp_id.setText(expense_list.get(position).get("id"));
        holder.exp_date.setText(expense_list.get(position).get("date"));
        holder.exp_cat.setText(expense_list.get(position).get("category"));
        holder.exp_amt.setText(expense_list.get(position).get("amt"));
    }

    @Override
    public int getItemCount() {
        return expense_list.size();
    }

    public class ProgramViewHolder extends RecyclerView.ViewHolder {
        TextView exp_id, exp_date, exp_cat, exp_amt;
        TableLayout layout_grid_position;

        public ProgramViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout_grid_position = itemView.findViewById(R.id.table_grid_position);
            this.exp_id = itemView.findViewById(R.id.list_id);
            this.exp_date = itemView.findViewById(R.id.list_date);
            this.exp_cat = itemView.findViewById(R.id.list_category);
            this.exp_amt = itemView.findViewById(R.id.list_amt);
        }
    }

}
*/
