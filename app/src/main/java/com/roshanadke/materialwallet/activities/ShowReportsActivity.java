package com.roshanadke.materialwallet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.roshanadke.materialwallet.fragments.CategoryReport;
import com.roshanadke.materialwallet.fragments.DateReport;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.adapters.TabAdapter;

public class ShowReportsActivity extends AppCompatActivity {

    TabAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reports);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reports");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager=findViewById(R.id.viewPager1);
        tabLayout=findViewById(R.id.tabLayout1);

        adapter=new TabAdapter(getSupportFragmentManager());
        adapter.addFrag(new DateReport(),"Datewise Report");
        adapter.addFrag(new CategoryReport(),"Categorywise Report");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
