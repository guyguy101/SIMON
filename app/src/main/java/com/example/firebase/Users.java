package com.example.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users extends Guest{
    public String key;
    public String uid;//user uid
    public String email,firstname, lastname;
    public int age;
    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Users(String uid, String email, String firstname,String lastname, int age,String key) {
        super(firstname,lastname);
        this.uid = uid;
        this.email = email;
        this.age = age;
        this.key = key;
    }



}