package com.example.firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    FirebaseAuth firebaseAuth;
    Button btnAddPost;
    Button btnReg,btnLogin,btnGuest;//dialog buttons
    Button btnMainLogin,btnMainRegister; //Login and Register buttons
    EditText etEmail,etPass;
    Dialog d;
    Button btnPlay,btnScoreboard;//opening_simon buttons
    int mode=0; // o means register 1 means login
    ProgressDialog progressDialog;
    EditText etAge,etFirstName,etLastName;
    Button btnAllPost;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        btnMainLogin = (Button)findViewById(R.id.btnLogin);
        btnMainLogin.setOnClickListener(this);
        btnGuest=findViewById(R.id.btnGuest);
        btnGuest.setOnClickListener(this);


        btnMainRegister = (Button)findViewById(R.id.btnRegister);
        btnMainRegister.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);




        FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
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
            setContentView(R.layout.opening_simon);
        }
        else if (btnReg==v)
        {
            register();
            setContentView(R.layout.opening_simon);
        }
        else if(v==btnLogin)
        {
            login();
            setContentView(R.layout.opening_simon);
        }
        else if(v==btnGuest)
        {
            guest();
            intent= new Intent(this,OpenActivity.class);
            startActivity(intent);
        }
        else if(v==btnPlay)
        {
            setContentView(R.layout.playing_activity);
        }
        else if(v==btnScoreboard)
        {
            setContentView(R.layout.playing_activity);
        }




    }
    //endregion

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
                        finish();
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
    //region FIREBASE

    public void createRegisterDialog()
    {
        d= new Dialog(this);
        d.setContentView(R.layout.register_layout);
        d.setTitle("Register");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPass);
        btnReg=(Button)d.findViewById(R.id.btnRegister);
        btnReg.setOnClickListener(this);
        d.show();

    }
    public void createLoginDialog()
    {
        d= new Dialog(this);
        d.setContentView(R.layout.login_layout);
        d.setTitle("Login");
        d.setCancelable(true);
        etEmail=(EditText)d.findViewById(R.id.etEmail);
        etPass=(EditText)d.findViewById(R.id.etPass);
        btnLogin=(Button)d.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        d.show();

    }

    public void register()
    {

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),etPass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    btnMainLogin.setText("Logout");

                } else {
                    Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();

                }

                d.dismiss();
                progressDialog.dismiss();


            }
        });

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
                            Toast.makeText(MainActivity.this, "auth_success",Toast.LENGTH_SHORT).show();
                            btnMainLogin.setText("Logout");

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "auth_failed",Toast.LENGTH_SHORT).show();

                        }
                        d.dismiss();
                        progressDialog.dismiss();

                    }
                });

    }
    public void guest()
    {


    }



    public void addUserDetails(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        int age = Integer.valueOf(etAge.getText().toString());

        Users user = new Users(uid,etEmail.getText().toString(),etFirstName.getText().toString(),etLastName.getText().toString(),age,"");

        userRef = firebaseDatabase.getReference("Users").push();
        user.key = userRef.getKey();
        userRef.setValue(user);

    }
    //endregion
}




