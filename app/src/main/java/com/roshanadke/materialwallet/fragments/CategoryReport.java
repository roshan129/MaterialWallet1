package com.roshanadke.materialwallet.fragments;


import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.roshanadke.materialwallet.adapters.CardViewAdapter;
import com.roshanadke.materialwallet.db.DatabaseHelper;
import com.roshanadke.materialwallet.R;

import com.roshanadke.materialwallet.model.ExpenseData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryReport extends Fragment {

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
    View view;
    ProgressBar progressBar;
    String uId, month, day, year, day_n;

    private DatabaseHelper databaseHelper;
    Button btn_s;

    public CategoryReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_category_report, container, false);
        initView();

        spin = view.findViewById(R.id.spinner_category);
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setUpList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dataAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_category = parent.getItemAtPosition(position).toString();
                if (str_category.equals("Select Category")) {
                    /*Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();*/
                } else {
                    /*adapter1();*/
                    getUserInformation();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        return view;
    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("expense_data");

        Query userQuery = collectionReference.whereEqualTo("category", str_category)
                .whereEqualTo("uid", uId);

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
                        Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
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

    private void initView() {
        progressBar = view.findViewById(R.id.progressbar);
        db = FirebaseFirestore.getInstance();
        arrayList = new ArrayList<HashMap<String, String>>();
        card_arrayList = new ArrayList<HashMap<String, String>>();

        recyclerViewCardList =  view.findViewById(R.id.recyclerView);
        layoutManager_re = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewCardList.setLayoutManager(layoutManager_re);
        cardViewAdapter = new CardViewAdapter(getActivity(), card_arrayList);
        recyclerViewCardList.setAdapter(cardViewAdapter);
        cardViewAdapter.notifyDataSetChanged();

        txt_total_amt2 = view.findViewById(R.id.txt_total_amt2);
        txt_total_text2 = view.findViewById(R.id.txt_total_text2);

        databaseHelper = new DatabaseHelper(getActivity());
    }



    private void setUpList() {
        list = new ArrayList<>();
        /*list.add("Select Category");*/
        list.add("Food");
        list.add("Travel");
        list.add("Clothes");
        list.add("Mobile");
        list.add("Others");
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
