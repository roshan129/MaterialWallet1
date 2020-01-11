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

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder> {
    private Context context;
    private ArrayList<HashMap<String, String>> expense_list;


    public BottomSheetAdapter(Context context, ArrayList<HashMap<String, String>> expense_list) {
        this.context = context;
        this.expense_list = expense_list;
    }

    @NonNull
    @Override
    public BottomSheetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.exp_list_layout_two, parent, false);
        return new BottomSheetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetViewHolder holder, int position) {
        String cat, pay, amt, note;
        /*cat = "Category : "+ expense_list.get(position).get("category");
        pay = "Payment :  "+ expense_list.get(position).get("payment");
        amt = "Amount :   "+ expense_list.get(position).get("amt")+ " Rs.";
        note= "Note :        "+ expense_list.get(position).get("note");*/

        cat = "Category : "+ expense_list.get(position).get("category");
        pay = "Payment : "+ expense_list.get(position).get("payment");
        amt = "Amount : "+ expense_list.get(position).get("amt")+ " Rs.";
        note= "Note :         "+ expense_list.get(position).get("note");

        holder.exp_cat.setText(cat);
        holder.exp_pay.setText(pay);
        holder.exp_amt.setText(amt);
        holder.exp_note.setText(note);

    }

    @Override
    public int getItemCount() {
        return expense_list.size();
    }

    public class BottomSheetViewHolder extends RecyclerView.ViewHolder {
        TextView exp_date, exp_cat, exp_pay, exp_amt, exp_note;

        public BottomSheetViewHolder(@NonNull View itemView) {
            super(itemView);

            exp_cat = itemView.findViewById(R.id.txtv_card_cate);
            exp_pay = itemView.findViewById(R.id.txtv_card_payment);
            exp_amt = itemView.findViewById(R.id.txtv_card_amt);
            exp_note = itemView.findViewById(R.id.txtv_card_note);


        }
    }

}
