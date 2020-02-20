package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roshanadke.materialwallet.DisplayImageActivity;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.db.DatabaseHelper;
import com.roshanadke.materialwallet.model.ExpenseData;
import com.roshanadke.materialwallet.model.UserInformation;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialCardView btn_card_add, btn_card_reports;
    private DatabaseHelper databaseHelper;
    private List<PieEntry> pieEntries;
    private TextView txt_profile_name;
    private CircleImageView img_profile_image;
    private String str_profile_name, strUrl,strDid;
    UserInformation userInfo;
    ExpenseData expenseData;
    private Cursor c;
    float tot;
    private String uId;
    View headerView;

    private FirebaseFirestore db;

    public static final int[] colors = {
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.rgb(179, 48, 80)
    };
    final int[] MY_COLORS = {Color.rgb(192, 0, 0), Color.rgb(255, 0, 0), Color.rgb(255, 192, 0),
            Color.rgb(127, 127, 127), Color.rgb(146, 208, 80), Color.rgb(0, 176, 80), Color.rgb(79, 129, 189)};
    ArrayList<Integer> colors1 = new ArrayList<Integer>();

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intitView();
        setProfile();

    }

    private void setProfile() {
        getUserInformation();
    }

    private void intitView() {
        db = FirebaseFirestore.getInstance();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_open);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        txt_profile_name = headerView.findViewById(R.id.txt_profile_name);
        img_profile_image =  headerView.findViewById(R.id.img_profile_image);

        btn_card_add = findViewById(R.id.btn_card_add_expense);
        btn_card_reports = findViewById(R.id.btn_card_reports);
        databaseHelper = new DatabaseHelper(this);

        btn_card_add.setOnClickListener(this);
        btn_card_reports.setOnClickListener(this);
        img_profile_image.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_card_add_expense:
                Intent intent = new Intent(this, AddExpenseActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_card_reports:
                Intent intent1 = new Intent(this, ShowReportsActivity.class);
                startActivity(intent1);
                break;
            case R.id.img_profile_image:
                /*Intent intent2 = new Intent(this, UpdateProfile.class);
                startActivity(intent2);*/
                Intent intent3 = new Intent(this, DisplayImageActivity.class);
                intent3.putExtra("url", strUrl);
                intent3.putExtra("did", strDid);
                startActivity(intent3);
                break;

        }
    }


    private void setUpPieData() {

        pieEntries = new ArrayList<>();
        Log.d(TAG, "setUpPieData: " + pieEntries.toString());
        counterMethod("Food");
        counterMethod("Travel");
        counterMethod("Clothes");
        counterMethod("Mobile");
        counterMethod("Others");

        /*} else {
            Toast.makeText(getApplicationContext(), "No Data found for list ...", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void counterMethod(final String strCat) {
        tot = 0f;

        CollectionReference collectionReference = db.collection("expense_data");

        Query userQuery = collectionReference
                .whereEqualTo("category", strCat)
                .whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            expenseData = documentSnapshot.toObject(ExpenseData.class);
                            tot += Float.parseFloat(expenseData.getExp_amt());
                            Log.d(TAG, "onComplete: " + expenseData.getExp_amt());
                        }
                        if (tot != 0f) {
                            pieEntries.add(new PieEntry(tot, strCat));
                            tot = 0f;
                            showPieChart();
                        }
                        Log.d(TAG, "onComplete: Total" + tot);
                        Log.d(TAG, "onComplete: " + pieEntries.toString());
                    } else {
                        Toast.makeText(MainActivity.this, "No data found for list", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "onComplete: Query failed");
                }
            }
        });

    }

    private void showPieChart() {
        for (int c : MY_COLORS) colors1.add(c);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        Log.d(TAG, "showPieChart: " + pieEntries.toString());
        pieDataSet.setColors(colors1);
        pieDataSet.setValueTextSize(6f);            // changes percentage's text size(y-axis value)
        PieData data = new PieData(pieDataSet);

        //get the chart
        PieChart chart = findViewById(R.id.pieChart);
        chart.setData(data);
        chart.animateXY(1500, 1500);
        //chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTextSize(8f);       //chnages label's text size
        chart.getDescription().setEnabled(false);  //hides description label
        chart.invalidate();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_date_report:
                Intent intent2 = new Intent(MainActivity.this, DatewiseReportActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_cate_report:
                Intent intent3 = new Intent(MainActivity.this, CategorywiseReportActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_calender:
                Intent intent4 = new Intent(MainActivity.this, CalenderActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_total_transaction:
                Intent intent1 = new Intent(MainActivity.this, TotalTransactionActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_setting:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_log_out:
                logOut();
                break;

        }

        return false;
    }

    private void logOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to Logout ?");
        alertDialogBuilder.setTitle("Confirm Logout");
                alertDialogBuilder.setPositiveButton("LOGOUT",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                                
                            }
                        });

        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();




    }


    private void getUserInformation() {
        CollectionReference collectionReference = db.collection("users");

        Query userQuery = collectionReference.whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        userInfo = documentSnapshot.toObject(UserInformation.class);

                        Log.d("tag", "User Data: " + userInfo.getName() + userInfo.getEmail());
                        displayUserDetails();
                    }
                } else {
                    Log.d("tag", "Query failed ");
                }
            }
        });

    }

    private void displayUserDetails() {
        str_profile_name = userInfo.getName();
        strUrl = userInfo.getUrl();
        strDid = userInfo.getDoc_id();
        txt_profile_name.setText(str_profile_name);
        Glide.with(this).load(strUrl).placeholder(R.drawable.user_icon_2).into(img_profile_image);
        /*new ImageLoadTask(strUrl, img_profile_image).execute();*/
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpPieData();
        setProfile();
    }

}
