package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.adapters.CardViewAdapter;
import com.roshanadke.materialwallet.adapters.SpinnerAdapter;
import com.roshanadke.materialwallet.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AddSpinnerItemsActivity extends AppCompatActivity implements SpinnerAdapter.OnSpinnerItemClickListener {

    String type;
    private DatabaseHelper databaseHelper;
    private RecyclerView recyclerViewSpinner;
    private ArrayList<HashMap<String, String>> spinnerArrayList;
    private SpinnerAdapter spinnerAdapter;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spinner_items);
        type = getIntent().getStringExtra("type");
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (type.equals("cate"))
            toolbar.setTitle("Add Category");
        else
            toolbar.setTitle("Add Payment Option");

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        initView();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.getBoolean("firstTime", false)) {
            databaseHelper.insertCateData("Food");
            databaseHelper.insertCateData("Travel");
            databaseHelper.insertCateData("Clothes");
            databaseHelper.insertCateData("Mobile");
            databaseHelper.insertCateData("Others");

            databaseHelper.insertPayData("Cash");
            databaseHelper.insertPayData("Debit/Credit Card");
            databaseHelper.insertPayData("Google Pay");
            databaseHelper.insertPayData("Check");
            databaseHelper.insertPayData("Others");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();


        }
        setUpSpinnerList();
        progressBar.setVisibility(View.GONE);

    }

    private void setUpSpinnerList() {
        spinnerArrayList.clear();
        Cursor c = null;
        if (type.equals("cate")) {
            c = databaseHelper.getCateTable();
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("category", c.getString(c.getColumnIndex("CATEGORY")));

                    spinnerArrayList.add(map);
                } while (c.moveToNext());
            } else {
                Toast.makeText(this, "Empty List", Toast.LENGTH_SHORT).show();
            }
        } else {
            c = databaseHelper.getPayTable();
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("category", c.getString(c.getColumnIndex("PAY_OPT")));

                    spinnerArrayList.add(map);
                } while (c.moveToNext());
            } else {
                Toast.makeText(this, "Empty List", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        spinnerArrayList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        recyclerViewSpinner = findViewById(R.id.recyclerViewSpinner);
        recyclerViewSpinner.setLayoutManager(new LinearLayoutManager(this));
        spinnerAdapter = new SpinnerAdapter(this, spinnerArrayList, this);
        recyclerViewSpinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDailog();
            }
        });
    }

    private void showAddCategoryDailog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_new_category, null);
        EditText categoryEditText = view.findViewById(R.id.categoryEditText);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (type.equals("cate")) {
            builder.setTitle("Add New Category");
            categoryEditText.setHint("Category");
        } else {
            builder.setTitle("New Payment Option");
            categoryEditText.setHint("Payment Option");
        }

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newCategory = categoryEditText.getText().toString();
                boolean isValid = true;
                if (newCategory.length() == 0) {
                    categoryEditText.setError("Enter Category");
                    isValid = false;

                }
                if (isValid) {
                    insertCategory(newCategory);
                }
                if (isValid) {
                    dialogInterface.dismiss();
                }
            }

        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }

    private void insertCategory(String newCategory) {
        boolean res = false;
        if (type.equals("cate")) {
            res = databaseHelper.insertCateData(newCategory);

        } else {
            res = databaseHelper.insertPayData(newCategory);
        }
        if (res) {
            setUpSpinnerList();
            spinnerAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        HashMap<String, String> map = spinnerArrayList.get(position);
                        if (type.equals("cate")) {
                            databaseHelper.deleteCateItem(map.get("category"));
                        } else {
                            databaseHelper.deletePayItem(map.get("category"));
                        }
                        setUpSpinnerList();
                        spinnerAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.reset) {
            progressBar.setVisibility(View.VISIBLE);
            if (type.equals("cate")) {
                databaseHelper.deleteAllCate();
                databaseHelper.insertCateData("Food");
                databaseHelper.insertCateData("Travel");
                databaseHelper.insertCateData("Clothes");
                databaseHelper.insertCateData("Mobile");
                databaseHelper.insertCateData("Others");
                setUpSpinnerList();
                spinnerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                databaseHelper.deleteAllPay();
                databaseHelper.insertPayData("Cash");
                databaseHelper.insertPayData("Debit/Credit Card");
                databaseHelper.insertPayData("Google Pay");
                databaseHelper.insertPayData("Check");
                databaseHelper.insertPayData("Others");
                setUpSpinnerList();
                spinnerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
