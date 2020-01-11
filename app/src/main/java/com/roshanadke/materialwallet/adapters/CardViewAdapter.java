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

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> expense_list;

    public CardViewAdapter(Context context, ArrayList<HashMap<String, String>> exp_list) {
        this.context = context;
        this.expense_list = exp_list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.expense_list_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        String cat, pay, amt, note, small_day, small_month, small_year;
        cat = "Category : "+ expense_list.get(position).get("category");
        pay = "Payment :  "+ expense_list.get(position).get("payment");
        amt = "Amount :   "+ expense_list.get(position).get("amt")+ " Rs.";
        note= "Note :        "+ expense_list.get(position).get("note");

        small_day=  expense_list.get(position).get("day");
        small_month= expense_list.get(position).get("day_n") + " " + expense_list.get(position).get("month");
        small_year=  expense_list.get(position).get("year");

        holder.exp_cat.setText(cat);
        holder.exp_pay.setText(pay);
        holder.exp_amt.setText(amt);
        holder.exp_note.setText(note);

        holder.small_card_day.setText(small_day);
        holder.small_card_month.setText(small_month);
        holder.small_card_year.setText(small_year);
    }

    @Override
    public int getItemCount() {
        return expense_list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView exp_date, exp_cat, exp_pay, exp_amt, exp_note;
        TextView small_card_day,small_card_month,small_card_year;
        TableLayout layout_grid_position;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            exp_cat = itemView.findViewById(R.id.txtv_card_cate);
            exp_pay = itemView.findViewById(R.id.txtv_card_payment);
            exp_amt = itemView.findViewById(R.id.txtv_card_amt);
            exp_note = itemView.findViewById(R.id.txtv_card_note);

            small_card_day = itemView.findViewById(R.id.txt_card_day);
            small_card_month = itemView.findViewById(R.id.txt_card_month);
            small_card_year = itemView.findViewById(R.id.txt_card_year);

        }
    }

}
