package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.adapters.CardViewAdapter;

import com.roshanadke.materialwallet.model.ExpenseData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class TotalTransactionActivity extends AppCompatActivity {
    private String str_category;
    ExpenseData expenseData;
    private Spinner spin;
    Cursor c;
    double total_amt2;
    CardViewAdapter cardViewAdapter;
    TextView txt_total_text2, txt_total_amt2;
    ArrayList<HashMap<String, String>> arrayList;
    ArrayList<HashMap<String, String>> card_arrayList;
    private FirebaseFirestore db;
    RecyclerView.LayoutManager layoutManager_re;
    RecyclerView recyclerViewCardList;
    List<String> list;
    private String uId,month, day, year, day_n;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        getUserInformation();
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        arrayList = new ArrayList<HashMap<String, String>>();
        card_arrayList = new ArrayList<HashMap<String, String>>();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("tag1", "initView: " + uId);
        recyclerViewCardList = findViewById(R.id.recyclerView);
        layoutManager_re = new LinearLayoutManager(TotalTransactionActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewCardList.setLayoutManager(layoutManager_re);
        cardViewAdapter = new CardViewAdapter(this, card_arrayList);
        recyclerViewCardList.setAdapter(cardViewAdapter);
        cardViewAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("expense_data");

        Query userQuery = collectionReference.whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    card_arrayList.clear();
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        expenseData = documentSnapshot.toObject(ExpenseData.class);
                        Log.d(TAG, "User Data: " + expenseData.getPayment_opt() + expenseData.getCategory());
                        displayUserDetails();
                    }
                    if(cardViewAdapter!= null){
                        cardViewAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                    if(card_arrayList.isEmpty()){
                        Toast.makeText(TotalTransactionActivity.this, "No data found...", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Query failed ");
                }
            }
        });
    }

    private void displayUserDetails() {

        String str_date, str_cate, str_pay, str_note, str_amt;
        HashMap<String, String> map = new HashMap<String, String>();
        str_date = expenseData.getExp_date();
        str_cate = expenseData.getCategory();
        str_pay = expenseData.getPayment_opt();
        str_note = expenseData.getExp_note();
        str_amt = expenseData.getExp_amt();

        setSmallCardData(str_date);

        map.put("date", str_date);
        map.put("category", str_cate);
        map.put("payment", str_pay);
        map.put("note", str_note);
        map.put("amt", str_amt);
        map.put("month", month);
        map.put("day", day);
        map.put("year", year);
        map.put("day_n", day_n);

        card_arrayList.add(map);

        /* progressBar.setVisibility(View.GONE);*/

    }

    public void setSmallCardData(String date){

        String small_day, small_month, small_year;
        small_day = date.substring(0, 2);
        small_month = date.substring(3, 5);
        small_year = date.substring(6, 10);

        day_n = small_day;
        month = formatMonth(Integer.parseInt(small_month));
        day = formatDay(Integer.parseInt(small_day),Integer.parseInt(small_month),Integer.parseInt(small_year));
        year = small_year;
        Log.d("month", "setSmallCardData: "+ month+ " "+ day);

    }

    public String formatMonth(int month) {
        DateFormat formatter = new SimpleDateFormat("MMMM");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month-1);
        return formatter.format(calendar.getTime());
    }

    public String formatDay(int day, int month, int year) {
        DateFormat formatter = new SimpleDateFormat("EEEE");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.YEAR, year);
        return formatter.format(calendar.getTime());
    }


}
