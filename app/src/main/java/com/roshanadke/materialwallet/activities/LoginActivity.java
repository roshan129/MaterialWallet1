package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.db.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    LinearLayout txtRegister;
    private EditText edtEmail, edt_password;
    private DatabaseHelper databaseHelper;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    String strEmail, strPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);
        edtEmail = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        databaseHelper = new DatabaseHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(LoginActivity.this, "Please check your internet connection and Try again!", Toast.LENGTH_SHORT).show();
                } else {
                    if (validateLogin()) {
                        userLogin();
                    }
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            finish();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Username or Passsword", Toast.LENGTH_SHORT).show();
                            Log.d("LoginActivity", "error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private boolean validateLogin() {
        strEmail = edtEmail.getText().toString();
        strPass = edt_password.getText().toString();
        if (strEmail.isEmpty()) {
            edtEmail.setError("Enter Email Id");
            edtEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError("Please enter a valid email!");
            edtEmail.requestFocus();
            return false;
        }
        if (strPass.isEmpty()) {
            edt_password.setError("Enter Password!");
            edt_password.requestFocus();
            return false;
        }
        if (strPass.length() < 6) {
            edt_password.setError("Password must be 6 characters long!");
            edt_password.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
