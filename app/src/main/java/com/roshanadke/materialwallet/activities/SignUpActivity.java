package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.db.DatabaseHelper;
import com.roshanadke.materialwallet.model.UserInformation;

import java.util.ArrayList;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText edtName, edtMobile, edtEmail, edtPassword, edt_c_password;
    RadioGroup radioGroup;
    MaterialRadioButton radioButton;
    TextView btn_txt_dob;
    String str_name, str_birth_date, str_gender = "Male", str_mobile, str_email, str_password, str_c_password;
    int mYear, mMonth, mDay, yr;
    MaterialButton btnSignUp;
    DatabaseHelper databaseHelper;
    MaterialSpinner spinnerDay, spinnerMonth, spinnerYear;
    ArrayList<String> arrday, arrmonth, arryear;
    ArrayAdapter<String> adapterday, adaptermonth, adapteryear;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

        btn_txt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        btn_txt_dob.setText(dayOfMonth + "-" + month + "-" + year);
                        str_birth_date = btn_txt_dob.getText().toString();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (rb != null) {
                    str_gender = rb.getText().toString();
                }
            }
        });

        btnSignUp.setOnClickListener(this);
    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        btn_txt_dob = findViewById(R.id.txt_dob);
        radioGroup = findViewById(R.id.radio_group);
        radioButton = findViewById(R.id.radio1);

        btnSignUp = findViewById(R.id.btn_register);

        databaseHelper = new DatabaseHelper(this);
        arrday = new ArrayList<>();
        arryear = new ArrayList<>();
        arrmonth = new ArrayList<>();
      /*  spinnerDay = (MaterialSpinner) findViewById(R.id.day);
        spinnerMonth = (MaterialSpinner) findViewById(R.id.month);
        spinnerYear = (MaterialSpinner) findViewById(R.id.year);
*/
        /* setUpSpinner();*/
        edtName = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edt_c_password = findViewById(R.id.edt_c_password);

        Calendar c = Calendar.getInstance();
        yr = c.get(Calendar.YEAR);
        mYear = 1998;                //c.get(Calendar.YEAR);
        mMonth = 0;               // c.get(Calendar.MONTH);
        mDay = 1;               // c.get(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < 32; i++)
            arrday.add(String.valueOf(i));

        for (int j = 1950; j <= yr; j++)
            arryear.add(String.valueOf(j));

        for (int k = 1; k < 13; k++)
            arrmonth.add(String.valueOf(k));

    }

    private void setUpSpinner() {
        adapterday = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, arrday);
        spinnerDay.setAdapter(adapterday);

        adaptermonth = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, arrmonth);
        spinnerMonth.setAdapter(adaptermonth);

        adapteryear = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, arryear);
        spinnerYear.setAdapter(adapteryear);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                validateFields();
                break;
        }
    }

    private void validateFields() {
        str_name = edtName.getText().toString();
        str_birth_date = btn_txt_dob.getText().toString();
        str_mobile = edtMobile.getText().toString();
        str_email = edtEmail.getText().toString();
        str_password = edtPassword.getText().toString();
        str_c_password = edt_c_password.getText().toString();
        if (TextUtils.isEmpty(str_name)) {
            edtName.setError("Enter Name");
            edtName.requestFocus();
        } else if (str_birth_date.matches("Date Of Birth")) {
            Toast.makeText(SignUpActivity.this, "Select Date OF Birth", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mobile)) {
            edtMobile.setError("Enter Mobile No");
            edtMobile.requestFocus();
        } else if (TextUtils.isEmpty(str_email)) {
            edtEmail.setError("Enter Email");
            edtEmail.requestFocus();
        } else if (TextUtils.isEmpty((str_password))) {
            edtPassword.setError("Enter password");
            edtPassword.requestFocus();
        } else if (str_password.length() < 6) {
            edtPassword.setError("Password must be 6 characters long");
            edtPassword.requestFocus();
        } else if (TextUtils.isEmpty((str_c_password))) {
            edt_c_password.setError("Confirm Passsword");
            edt_c_password.requestFocus();
        } else if (!str_password.equals(str_c_password)) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        } else {
            /*registerUser();*/
            createUserWithFirebase();
        }
    }



    private void createUserWithFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    saveUserInfoToFirebase();

                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("tag", "error " + task.getException());
                    Toast.makeText(SignUpActivity.this, "Poor Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserInfoToFirebase() {
       /* CollectionReference dbUsers = db.collection("users");
            String id = dbUsers.getId();*/
        DocumentReference ref = db.collection("users").document();
        String doc_id = ref.getId();
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UserInformation userInfo = new UserInformation(doc_id, myId, str_name, str_birth_date, str_gender, str_mobile, str_email, str_password, null);
        ref.set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "User Registered!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Log.d("tag", "onComplete: " + task.getException());
                    Toast.makeText(SignUpActivity.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
