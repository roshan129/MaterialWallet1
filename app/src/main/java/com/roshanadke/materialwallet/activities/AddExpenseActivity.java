package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.roshanadke.materialwallet.db.DatabaseHelper;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.model.ExpenseData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    EditText btn_select_date;
    AppCompatSpinner spin_cate, spin_pay;
    EditText edt_amount, edt_note;
    ImageView imageView;
    Button btn_add_exp;
    private DatabaseHelper databaseHelper;
    int mYear, mMonth, mDay;
    String str1, str_spin, str3, str4, str_spin_pay;
    CoordinatorLayout coordinatorLayout;
    ProgressBar progressBar;
    Calendar c;
    String d, date;
    List<String> list1, list2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference profileImageRef;
    private String m_cate, m_pay;

    ArrayAdapter<String> dataAdapter, dataAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        setUpOnClickListneres();

    }

    private void setUpOnClickListneres() {
        spin_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_spin = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spin_pay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_spin_pay = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_select_date.setOnClickListener(this);
        btn_add_exp.setOnClickListener(this);
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        btn_select_date = findViewById(R.id.edt_edit_date);
        spin_cate = findViewById(R.id.spinner_category);
        spin_pay = findViewById(R.id.spinner_payment_option);
        edt_amount = findViewById(R.id.edt_edit_amt);
        edt_note = findViewById(R.id.edt_edit_note);
        btn_add_exp = findViewById(R.id.btn_add_exp);

        coordinatorLayout = findViewById(R.id.coordinator);
        btn_select_date.setShowSoftInputOnFocus(false);

        databaseHelper = new DatabaseHelper(this);

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        setUpList();
        dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin_cate.setAdapter(dataAdapter);

        spin_cate.setSelection(0, true);
        View v = spin_cate.getSelectedView();
        ((TextView) v).setTextColor(getResources().getColor(R.color.grey_primary_dark));

        dataAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spin_pay.setAdapter(dataAdapter2);
    }

    private void setUpSpinAdapter() {

    }

    private void setUpList() {
        list1 = new ArrayList<>();
        list1.add("Select Category");
        Cursor c = null;
        c = databaseHelper.getCateTable();
        if (c.getCount() != 0) {
            c.moveToFirst();
            do {
                list1.add(c.getString(c.getColumnIndex("CATEGORY")));
            } while (c.moveToNext());
        }

        list2 = new ArrayList<>();
        list2.add("Select Payment Option");
        Cursor c1 = databaseHelper.getPayTable();
        if (c1.getCount() != 0) {
            c1.moveToFirst();
            do {
                list2.add(c1.getString(c1.getColumnIndex("PAY_OPT")));
            } while (c1.moveToNext());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_edit_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        d = dayOfMonth + "-" + month + "-" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = null;
                        try {
                            date1 = sdf.parse(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date1);
                        date = sdf.format(cal.getTime());

                        btn_select_date.setText(date);
                        btn_select_date.setError(null);
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.btn_add_exp:
                addExpenses();
                break;
        }
    }

    private void addExpenses() {
        str1 = btn_select_date.getText().toString();
        str3 = edt_note.getText().toString();
        str4 = edt_amount.getText().toString();

        if (TextUtils.isEmpty(str1)) {
            btn_select_date.setError("Select Date");
        } else if (str_spin.matches("Select Category")) {
            Toast.makeText(AddExpenseActivity.this, "Select Category", Toast.LENGTH_SHORT).show();
        } else if (str_spin_pay.matches("Select Payment Option")) {
            Toast.makeText(AddExpenseActivity.this, "Select Payment Option", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str3)) {
            edt_note.setError("Enter Note");
        } else if (TextUtils.isEmpty((str4))) {
            edt_amount.setError("Enter amount");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            DocumentReference ref = firebaseFirestore.collection("expense_data").document();
            String doc_id = ref.getId();
            String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            ExpenseData expenseData1 = new ExpenseData(doc_id, myId, str1, str_spin, str_spin_pay, str3, str4);

            ref.set(expenseData1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddExpenseActivity.this, "Record Added Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddExpenseActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.d("TAG", "onComplete: " + task.getException());
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        /*getMenuInflater().inflate(R.menu.add_exp_menu, menu);
        return true;*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_cate:
                setUpAlertDialogForCate();
                return true;
            case R.id.item_add_pay:
                setUpAlertDialogForPay();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpAlertDialogForCate() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Add Cateory");
        final EditText input = new EditText(AddExpenseActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_cate = input.getText().toString();
                list1.add(m_cate);
                dataAdapter.notifyDataSetChanged();
                Toast.makeText(AddExpenseActivity.this, "Category Added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void setUpAlertDialogForPay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Payment Option");
        final EditText input = new EditText(AddExpenseActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary));
        input.setBackgroundTintList(colorStateList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            input.setTextCursorDrawable(getResources().getColor(R.color.colorPrimary));
        }
        builder.setView(input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_pay = input.getText().toString();
                dataAdapter2.notifyDataSetChanged();
                Toast.makeText(AddExpenseActivity.this, "Payment Option Added", Toast.LENGTH_SHORT).show();
                list2.add(m_pay);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
