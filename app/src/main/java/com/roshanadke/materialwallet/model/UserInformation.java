package com.roshanadke.materialwallet.model;

public class UserInformation {

    private String doc_id;
    private String uid;
    private String name;
    private String dob;
    private String gender;
    private String mobile;
    private String email;
    private String password;
    private String url;

    public UserInformation() {

    }

    public UserInformation(String doc_id, String uid, String name, String dob, String gender, String mobile, String email, String password, String url) {
        this.doc_id = doc_id;
        this.uid = uid;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.url = url;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
