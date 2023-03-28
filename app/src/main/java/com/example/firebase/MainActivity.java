package com.example.firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    Button btnAddPost;
    Button btnGuestLogin;
    Button btnReg,btnLogin,btnGuest;//dialog buttons
    Button btnMainLogin,btnMainRegister; //Login and Register buttons
    EditText etEmail,etPass,etNickname;
    Dialog d;

    ProgressDialog progressDialog;

    Button btnAllPost;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    static Guest guest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.mainLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.start();


        btnMainLogin = (Button)findViewById(R.id.btnLogin);
        btnMainLogin.setOnClickListener(this);
        btnGuest=findViewById(R.id.btnGuest);
        btnGuest.setOnClickListener(this);


        btnMainRegister = (Button)findViewById(R.id.btnRegister);
        btnMainRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);





        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            btnMainLogin.setText("Logout");

        }
        else
        {
            btnMainLogin.setText("Login");

        }



    }


    //region onClick
    @Override
    public void onClick(View v) {



        if(v==btnMainLogin)
        {

            if(btnMainLogin.getText().toString().equals("Login"))
            {
                createLoginDialog();
            }
            else if(btnMainLogin.getText().toString().equals("Logout"))
            {
                firebaseAuth.signOut();
                btnMainLogin.setText("Login");
            }

        }
        else if(v==btnMainRegister)
        {
            createRegisterDialog();

        }
        else if (v == btnReg)
        {
            String nickName = String.valueOf(etNickname.getText());
            String pass = String.valueOf(etPass.getText());
            String email = String.valueOf(etEmail.getText());
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "One of the fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            register();
            getOpenActivity();

        }
        else if(v==btnLogin)
        {
            String pass = String.valueOf(etPass.getText());
            String email = String.valueOf(etEmail.getText());
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) ){
                Toast.makeText(MainActivity.this, "One of the fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            login();
            getOpenActivity();



        }
        else if(v==btnGuest)
        {

            guest();


        }
        else if(v==btnGuestLogin){
            String nickName = String.valueOf(etNickname.getText());

            if( TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "One of the fields are missing", Toast.LENGTH_LONG).show();
                return;
            }
            getOpenActivity();
            d.dismiss();
        }





    }
    //endregion

    @Override
    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null)
            getOpenActivity();


    }
    public void getOpenActivity(){
        intent = new Intent(this,OpenActivity.class);
        startActivity(intent);
        finish();
    }
    //region closingApp
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Application")
                .setMessage("Are you sure you want to close `Simon?`")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finishAndRemoveTask();
                        finishAffinity();

                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    //endregion
    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
        

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       return super.onOptionsItemSelected(item);

    }
    //endregion
    //region FIREBASE - LOGIN/REGISTER/GUEST

    public void createRegisterDialog()
    {
        d= new Dialog(this);
        d.setContentView(R.layout.register_layout);
        d.setTitle("Register");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPassword);
        etNickname = d.findViewById(R.id.etNickname);
        btnReg=(Button)d.findViewById(R.id.btnRegister);

        btnReg.setOnClickListener(this);
        d.show();

    }

    public void register()
    {

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
                    User user = new User(etEmail.getText().toString(),etNickname.getText().toString(),uid);

                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                    btnMainLogin.setText("Logout");



                } else {
                    Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();

                }

                d.dismiss();
                progressDialog.dismiss();


            }
        });

    }



    public void createLoginDialog()
    {
        d= new Dialog(this);
        d.setContentView(R.layout.login_layout);
        d.setTitle("Login");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPassword);
        etNickname = d.findViewById(R.id.etNickname);
        btnLogin=(Button)d.findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(this);

        d.show();


    }

    public  void login()
    {

        progressDialog.setMessage("Login Please Wait...");
        progressDialog.show();



        firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Authentication Succeeded",Toast.LENGTH_SHORT).show();

                            btnMainLogin.setText("Logout");

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Authentication Failed",Toast.LENGTH_SHORT).show();

                        }
                        d.dismiss();
                        progressDialog.dismiss();

                    }
                });

    }
    public void createGuestDialog()
    {
        d= new Dialog(this);
        d.setContentView(R.layout.guest_layout);
        d.setTitle("GUEST");
        d.setCancelable(true);
        btnGuestLogin = d.findViewById(R.id.btnGuestLogin);
        etNickname = d.findViewById(R.id.etNickname);
        btnGuestLogin.setOnClickListener(this);
        guest = new Guest(etNickname.getText().toString());
        d.show();


    }
    public void guest()
    {
        createGuestDialog();


    }

    /*public void addUserDetails(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        User user = new User(etEmail.getText().toString(),etNickname.getText().toString(),uid);
        userRef = firebaseDatabase.getReference("users").push();



    }*/




    //endregion
}




