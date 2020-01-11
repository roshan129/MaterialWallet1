package com.roshanadke.materialwallet.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.roshanadke.materialwallet.activities.DisplayListActivity;
import com.roshanadke.materialwallet.R;
import com.roshanadke.materialwallet.adapters.BottomSheetAdapter;
import com.roshanadke.materialwallet.model.ExpenseData;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalenderFragment extends Fragment {

    View view;
    private FirebaseFirestore db;
    private ArrayList<HashMap<String, String>> card_arrayList;
    private RecyclerView recyclerViewCardList;
    private BottomSheetAdapter bottomSheetAdapter;
    private ExpenseData expenseData;
    private ProgressBar progressBar;
    private String uId;
    String myDateString;

    public CalenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_calender, container, false);

        initView();
        getUserData();

        return view;
    }

    private void initView() {
        DisplayListActivity activity = (DisplayListActivity) getActivity();
        myDateString = activity.getMyData();
        progressBar = view.findViewById(R.id.progressBar);
        card_arrayList = new ArrayList<>();
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        recyclerViewCardList = view.findViewById(R.id.calenderRecycler);
        recyclerViewCardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        bottomSheetAdapter = new BottomSheetAdapter(getActivity(), card_arrayList);
        recyclerViewCardList.setAdapter(bottomSheetAdapter);
        bottomSheetAdapter.notifyDataSetChanged();

    }

    private void getUserData() {
        progressBar.setVisibility(View.VISIBLE);
        CollectionReference collectionReference = db.collection("expense_data");

        Query userQuery = collectionReference.whereEqualTo("exp_date", myDateString)
                .whereEqualTo("uid", uId);

        userQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    card_arrayList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        expenseData = documentSnapshot.toObject(ExpenseData.class);
                        Log.d(TAG, "User Data: " + expenseData.getPayment_opt() + expenseData.getCategory());
                        displayFragmentData();
                    }

                    if (bottomSheetAdapter != null) {
                        bottomSheetAdapter.notifyDataSetChanged();
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

    private void displayFragmentData() {
        String str_date, str_cate, str_pay, str_note, str_amt;
        HashMap<String, String> map = new HashMap<String, String>();
        str_date = expenseData.getExp_date();
        str_cate = expenseData.getCategory();
        str_pay = expenseData.getPayment_opt();
        str_note = expenseData.getExp_note();
        str_amt = expenseData.getExp_amt();

        map.put("date", str_date);
        map.put("category", str_cate);
        map.put("payment", str_pay);
        map.put("note", str_note);
        map.put("amt", str_amt);

        card_arrayList.add(map);

    }
}
