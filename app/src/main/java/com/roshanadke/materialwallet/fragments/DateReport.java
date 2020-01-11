package com.roshanadke.materialwallet.fragments;


import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import com.roshanadke.materialwallet.db.DatabaseHelper;
import com.roshanadke.materialwallet.model.ExpenseData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class DateReport extends Fragment {
    String str_date = "";
    ImageView img_date;
    TextView txt_date2, txt_total_amt, txt_total_text;
    int mYear, mMonth, mDay;
    LinearLayout line1;
    ArrayList<HashMap<String, String>> arrayList;

    private DatabaseHelper databaseHelper;
    View view;
    String uId, month, day, year, day_n, d, d2;

    private FirebaseFirestore db;
    private ArrayList<HashMap<String, String>> card_arrayList;
    private RecyclerView recyclerViewCardList;
    private CardViewAdapter cardViewAdapter;
    private ExpenseData expenseData;
    private ProgressBar progressBar;

    public DateReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_date_report, container, false);
        intitView();

        line1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;

                        d  = dayOfMonth + "-" + month + "-" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = null;
                        try {
                            date1 = sdf. parse(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal = Calendar. getInstance();
                        cal. setTime(date1);
                        d2 = sdf.format(cal.getTime());

                        txt_date2.setText(d2);
                        str_date = txt_date2.getText().toString();
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        if (str_date.equals("Select Date")) {
                            Toast.makeText(getActivity(), "Select Date", Toast.LENGTH_SHORT).show();
                        } else {
                            /*adapter2();*/
                            getUserInformation();
                        }
                    }
                }, mYear, mMonth-1, mDay);
                datePickerDialog.show();
            }
        });

        return view;
    }

    private void intitView() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        str_date = sdf.format(c.getTime());
        uId = FirebaseAuth.getInstance().getUid();
        progressBar = view.findViewById(R.id.progressBar);
        arrayList = new ArrayList<>();
        card_arrayList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        recyclerViewCardList = view.findViewById(R.id.recyclerView2);
        /*layoutManager_re = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);*/
        recyclerViewCardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        cardViewAdapter = new CardViewAdapter(getActivity(), card_arrayList);
        recyclerViewCardList.setAdapter(cardViewAdapter);
        cardViewAdapter.notifyDataSetChanged();

        txt_total_amt = view.findViewById(R.id.txt_total_amt);
        txt_total_text = view.findViewById(R.id.txt_total_text);

        databaseHelper = new DatabaseHelper(getActivity());
        txt_date2 = view.findViewById(R.id.txt_date2);
        line1 = view.findViewById(R.id.line1);
        img_date = view.findViewById(R.id.img_date);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH)+1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        txt_date2.setText(str_date);
        if(!str_date.equals("")) getUserInformation();

    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("expense_data");

        Query userQuery = collectionReference.whereEqualTo("exp_date", str_date)
                .whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    card_arrayList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        expenseData = documentSnapshot.toObject(ExpenseData.class);
                        Log.d(TAG, "User Data: " + expenseData.getPayment_opt() + expenseData.getCategory());
                        displayUserDetails();
                    }

                    if (cardViewAdapter != null) {
                        cardViewAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                    if(card_arrayList.isEmpty()){
                        Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
                    }

                } else {
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
        calendar.set(Calendar.MONTH, month);
        return formatter.format(calendar.getTime());
    }

    public String formatDay(int day, int month, int year) {
        DateFormat formatter = new SimpleDateFormat("EEEE");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return formatter.format(calendar.getTime());
    }

}
