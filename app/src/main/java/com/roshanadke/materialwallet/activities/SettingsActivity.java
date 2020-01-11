package com.roshanadke.materialwallet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.model.UserInformation;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txt_profile_nm, txt_mobile_no, txt_email_id, txt_help, txt_terms, txt_t;
    private String m_Text;
    private FirebaseFirestore db;
    UserInformation userInfo;
    private CircleImageView circleImageView;
    private String uId;
    private String str_doc_id, str_name, str_email, str_phone, str_gender, str_birth, strUrl;
    private ProgressBar progressBar;
    private TextView txt_pay_opt, txt_add_cate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        getUserInformation();

    }

    private void initView() {
        db=FirebaseFirestore.getInstance();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        circleImageView = findViewById(R.id.profile_image);
       /* txt_mobile_no = findViewById(R.id.txt_mobile_no);
        txt_email_id = findViewById(R.id.txt_email_id);*/
        txt_profile_nm = findViewById(R.id.txt_profile_nm);
        progressBar = findViewById(R.id.progressBar);

        txt_pay_opt = findViewById(R.id.txt_pay_opt);
        txt_add_cate = findViewById(R.id.txt_add_cate);
        txt_pay_opt.setOnClickListener(this);
        txt_add_cate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case 1:
                setUpAlertDialogForCate();
                break;
            case 2:
                setUpAlertDialogForPay();
                break;
            case R.id.txt_add_cate:
                Intent intent = new Intent(this, AddSpinnerItemsActivity.class);
                intent.putExtra("type", "cate");
                startActivity(intent);
                break;
            case R.id.txt_pay_opt:
                Intent intent1 = new Intent(this, AddSpinnerItemsActivity.class);
                intent1.putExtra("type", "pay");
                startActivity(intent1);
                break;

        }
    }

    private void getUserInformation() {
        progressBar.setVisibility(View.VISIBLE);
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
                    progressBar.setVisibility(View.GONE);
                    Log.d("tag", "Query failed ");
                }
            }
        });

    }

    private void displayUserDetails() {
        loadImage();
        str_doc_id = userInfo.getDoc_id();
        str_name = userInfo.getName();
        str_email = userInfo.getEmail();
        str_phone = userInfo.getMobile();
        str_birth = userInfo.getDob();
        str_gender = userInfo.getGender();
        strUrl = userInfo.getUrl();

        txt_profile_nm.setText(str_name);
/*
        txt_email_id.setText(str_email);
        txt_mobile_no.setText(str_phone);
*/
        progressBar.setVisibility(View.GONE);

    }

    private void loadImage() {
        if(strUrl != null) {
            Glide.with(this).load(strUrl).placeholder(R.drawable.user_icon_2).into(circleImageView);
        }
    }

    private void setUpAlertDialogForCate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Cateory");

        final EditText input = new EditText(SettingsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                Log.d("tag", "onClick: " + m_Text);
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

        final EditText input = new EditText(SettingsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                m_Text = input.getText().toString();
                Log.d("tag", "onClick: " + m_Text);
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

        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return true;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edt_profile :
                Intent intent = new Intent(SettingsActivity.this, UpdateProfile.class);
                intent.putExtra("name", str_name);
                intent.putExtra("email", str_email);
                intent.putExtra("phone", str_phone);
                intent.putExtra("birth", str_birth);
                intent.putExtra("gender", str_gender);
                intent.putExtra("did", str_doc_id);
                startActivity(intent);
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInformation();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
