package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.adapters.BottomSheetAdapter;
import com.roshanadke.materialwallet.model.ExpenseData;
import com.roshanadke.materialwallet.model.PreviousData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CalenderActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private String uId;
    List<EventDay> events;
    List<PreviousData> listData;
    private PreviousData previousData;

    private FirebaseFirestore db;
    private ArrayList<HashMap<String, String>> card_arrayList;
    private RecyclerView recyclerViewCardList;
    private BottomSheetAdapter bottomSheetAdapter;
    private ExpenseData expenseData;
    private ProgressBar progressBar;
    String myDateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       initView();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
               showFragment(eventDay);

            }
        });

        getUserInformation();

    }

    private void showFragment(EventDay eventDay) {
        Calendar clickedDayCalendar = eventDay.getCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        myDateString = dateFormat.format(clickedDayCalendar.getTime());

        Intent intent = new Intent(CalenderActivity.this, DisplayListActivity.class);
        intent.putExtra("date", myDateString);
        startActivity(intent);
     /*getFragmentData(myDateString);*/
        Toast.makeText(CalenderActivity.this, myDateString, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDayClick: "+listData.toString());
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        card_arrayList = new ArrayList<>();

        listData = new ArrayList<>();
        previousData = new PreviousData();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<>();
        calendarView = findViewById(R.id.calendarView);
        calendarView.setHeaderColor(R.color.white);
        calendarView.setHeaderLabelColor(R.color.black);

        calendarView.setForwardButtonImage(getResources().getDrawable(R.drawable.forward_arrow));
        calendarView.setPreviousButtonImage(getResources().getDrawable(R.drawable.backward_arrow));
    }

    private void getFragmentData(String str_date) {


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
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        expenseData = documentSnapshot.toObject(ExpenseData.class);
                        Log.d(TAG, "User Data: " + expenseData.getPayment_opt() + expenseData.getCategory());
                        CalenderActivity.this.displayUserDetails();

                    }
                    calendarView.setEvents(events);
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Query failed ");
                }
            }
        });

    }

    private void displayUserDetails() {
        String date, amount, strYear, strMonth, strDay;
        int tot;
        date = expenseData.getExp_date();
        amount = expenseData.getExp_amt();

        if(previousData.getDate() != date){
            previousData = new PreviousData(date, amount);
            listData.add(previousData);
            strDay = date.substring(0, 2);
            strMonth = date.substring(3, 5);
            strYear = date.substring(6, 10);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(strYear), Integer.parseInt(strMonth)-1, Integer.parseInt(strDay));
            events.add(new EventDay(calendar, R.drawable.ic_lens_black_24dp));
        }else{
            tot = Integer.parseInt(previousData.getAmount()) + Integer.parseInt(amount);
            previousData.setAmount(String.valueOf(tot));
            listData.add(previousData);
            strDay = date.substring(0, 2);
            strMonth = date.substring(3, 5);
            strYear = date.substring(6, 10);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(strYear), Integer.parseInt(strMonth)-1, Integer.parseInt(strDay));
            events.add(new EventDay(calendar, R.drawable.ic_lens_black_24dp));

        }
    }



   /* @Override
    public void onDayClick(EventDay eventDay) {
        Calendar clickedDayCalendar = eventDay.getCalendar();
        Toast.makeText(this, clickedDayCalendar.toString(), Toast.LENGTH_SHORT).show();
    }*/
}
