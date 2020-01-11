package com.roshanadke.materialwallet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.fragments.CalenderFragment;

public class DisplayListActivity extends AppCompatActivity {

    String myDateString;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDateString = getIntent().getStringExtra("date");
        textView = findViewById(R.id.txt_date);

        textView.setText(myDateString);

        Fragment fragment = new CalenderFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.calenderFrame, fragment).commit();


    }

    public String getMyData() {
        return myDateString;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
