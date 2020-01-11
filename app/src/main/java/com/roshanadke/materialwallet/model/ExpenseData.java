package com.roshanadke.materialwallet.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ExpenseData {

    private String doc_id;
    private String uid;
    private String exp_date;
    private String category;
    private String payment_opt;
    private String exp_note;
    private String exp_amt;

    public ExpenseData() {
    }


    public ExpenseData(String doc_id, String uid, String exp_date, String category, String payment_opt, String exp_note, String exp_amt) {
        this.doc_id = doc_id;
        this.uid = uid;
        this.exp_date = exp_date;
        this.category = category;
        this.payment_opt = payment_opt;
        this.exp_note = exp_note;
        this.exp_amt = exp_amt;

    }


    public String getDoc_id() {
        return doc_id;
    }

    public String getUid() {
        return uid;
    }

    public String getExp_date() {
        return exp_date;
    }

    public String getCategory() {
        return category;
    }

    public String getPayment_opt() {
        return payment_opt;
    }

    public String getExp_note() {
        return exp_note;
    }

    public String getExp_amt() {
        return exp_amt;
    }



}
