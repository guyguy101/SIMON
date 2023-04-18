package com.example.firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    Button btnAddPost;
    Button btnGuestLogin;
    Button btnReg,btnLogin,btnGuest;//dialog buttons
    Button btnMainLogin,btnMainRegister; //Login and Register buttons
    EditText etEmail,etPass,etNickname;
    Dialog d;

    ProgressDialog progressDialog;
    private WifiReceiver wifiReceiver = new WifiReceiver();
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
        Intent serviceIntent = new Intent(this, PlayTimeService.class);
        startService(serviceIntent);

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



        UserDatabase dbHelper = new UserDatabase(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDatabase.COLUMN_EMAIL, "");
        values.put(UserDatabase.COLUMN_NICKNAME, "");
        values.put(UserDatabase.COLUMN_MAX_SCORE, 0);
        values.put(UserDatabase.COLUMN_DATE_JOINED, "");
        values.put(UserDatabase.COLUMN_LAST_DATE_PLAYED, "");
        long newRowId = db.insert(UserDatabase.TABLE_NAME, null, values);
        db.close();

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

    //region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.exitItem:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Closing Application")
                        .setMessage("Are you sure you want to close `Simon?`")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finishAndRemoveTask();
                                finishAffinity();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            case R.id.aboutProgramItem:
                intent = new Intent(this, AboutProgrammerActivity.class);
                startActivity(intent);
                finish();
            case R.id.aboutAppItem:
                intent = new Intent(this,AboutAppActivity.class);
                startActivity(intent);
                finish();


            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //endregion

    //region BroadcastReceiver
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);
    }
    //endregion

    private void scheduleBroadcast() {
        Intent intent = new Intent(this, WifiReceiver.class);
        intent.putExtra("requestCode", 123);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long delayMillis = 15 * 1000; // 15 seconds
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delayMillis, pendingIntent);
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
            if(TextUtils.isEmpty(email)  ){
                Toast.makeText(MainActivity.this, "Email field is missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(pass) ){
                Toast.makeText(MainActivity.this, "Password field is missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(!emailValidator(etEmail)){
                Toast.makeText(MainActivity.this, "Email isnt valid", Toast.LENGTH_LONG).show();
                return;
            }
            if(!passwordValidator(etPass)){
                Toast.makeText(MainActivity.this, "Password Should be at least 6 characters long!", Toast.LENGTH_LONG).show();
                return;
            }


            String nicknameToCheck = nickName;
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            Query nicknameQuery = usersRef.orderByChild("nickname").equalTo(nicknameToCheck);

            nicknameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this,"Nickname already exists!", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        register();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });




        }
        else if(v==btnLogin)
        {
            String pass = String.valueOf(etPass.getText());
            String email = String.valueOf(etEmail.getText());
            if(TextUtils.isEmpty(email)  ){
                Toast.makeText(MainActivity.this, "Email field is missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(pass) ){
                Toast.makeText(MainActivity.this, "Password field is missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(!emailValidator(etEmail)){
                Toast.makeText(MainActivity.this, "Email is not valid!", Toast.LENGTH_LONG).show();
                return;
            }


            login();




        }
        else if(v==btnGuest)
        {

            guest();


        }
        else if(v==btnGuestLogin){
            String nickName = String.valueOf(etNickname.getText());
            if(TextUtils.isEmpty(nickName)){
                Toast.makeText(this,"Field is empty", Toast.LENGTH_SHORT).show();
                return;
            }


            getOpenActivity();
            d.dismiss();
        }





    }
    //endregion

    //region UTILS
    public boolean emailValidator(EditText etMail) {

        String emailToText = etMail.getText().toString();

        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean passwordValidator(EditText etPassword) {
        String passwordToText = etPassword.getText().toString();
        if (passwordToText.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

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
                                UserDatabase dbHelper = new UserDatabase(MainActivity.this);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(UserDatabase.COLUMN_ID, user.getUid());
                                values.put(UserDatabase.COLUMN_EMAIL, user.getEmail());
                                values.put(UserDatabase.COLUMN_NICKNAME, user.getNickname());
                                values.put(UserDatabase.COLUMN_MAX_SCORE, user.getMaxScore());
                                values.put(UserDatabase.COLUMN_DATE_JOINED, new SimpleDateFormat("dd/MM/yyyy").format(user.getDateJoined()));
                                values.put(UserDatabase.COLUMN_LAST_DATE_PLAYED, new SimpleDateFormat("dd/MM/yyyy").format(user.getLastDatePlayed()));
                                long newRowId = db.insert(UserDatabase.TABLE_NAME, null, values);
                                db.close();
                                Toast.makeText(MainActivity.this, "Registered", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
                    btnMainLogin.setText("Logout");
                } else {
                    Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
                d.dismiss();
                getOpenActivity();
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
                        getOpenActivity();

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




