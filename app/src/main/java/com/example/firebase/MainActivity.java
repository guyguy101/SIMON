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
    Intent intent1;
    Intent intent2;
    Button btnAddPost;
    Button btnGuestLogin;
    Button btnReg,btnLogin,btnGuest;//dialog buttons
    Button btnMainLogin,btnMainRegister; //Login and Register buttons
    EditText etEmail,etPass,etNickname;
    Dialog d;


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



        LinearLayout linearLayout = findViewById(R.id.mainLayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.setVisible(true, true);
        animationDrawable.start();


        btnMainLogin = (Button)findViewById(R.id.btnLogin);
        btnMainLogin.setOnClickListener(this);
        btnGuest=findViewById(R.id.btnGuest);
        btnGuest.setOnClickListener(this);


        btnMainRegister = (Button)findViewById(R.id.btnRegister);
        btnMainRegister.setOnClickListener(this);




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
            case R.id.aboutAppItem:
                intent2 = new Intent(this,AboutAppActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.aboutProgramItem:
                intent1 = new Intent(this, AboutProgrammerActivity.class);
                startActivity(intent1);
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
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
            if(TextUtils.isEmpty(pass) && TextUtils.isEmpty(email) && TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "ALL FIELDS ARE MISSING!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(pass) && TextUtils.isEmpty(email) ){
                Toast.makeText(MainActivity.this, "Password and email field are missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "Email and nickname fields are missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(pass) && TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "Password and nickname fields are missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(nickName)){
                Toast.makeText(MainActivity.this, "Nickname field is missing!", Toast.LENGTH_LONG).show();
                return;
            }
            if(TextUtils.isEmpty(email) ){
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


            register();




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

        //שקר אחרת תקין הוא אם אמת ותחזיר אימייל המקבלת פעולה

        String emailToText = etMail.getText().toString();
        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean passwordValidator(EditText etPassword) {
        //שקר אחרת תקין הוא אם אמת ותחזיר סיסמא המקבלת פעולה
        String passwordToText = etPassword.getText().toString();
        if (passwordToText.length() >= 6) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onStart(){

        // רשום כבר המשתמש אם הניווט למסך מעביר
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null)
            getOpenActivity();


    }
    public void getOpenActivity(){
        //הניווט למסך המעבירה פעולה
        intent = new Intent(this,OpenActivity.class);
        startActivity(intent);
        finish();
    }
    //endregion

    //region FIREBASE - LOGIN/REGISTER/GUEST

    public void createRegisterDialog()
    {
        //הירשמות דיאלוג היוצרת פעולה
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

        //הניווט למסך נעבור נוצר המשתמש כאשר , הנתונים למסדי שלו הנתונים את מוסיפה ,וסיסמא אימייל באמצעות משתמש היוצרת פעולה

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
                    d.dismiss();
                    getOpenActivity();
                } else {
                    Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    public void createLoginDialog()
    {
        //משתמש התחברות דיאלוג היוצרת פעולה
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
        //הניווט למסך נעבור מתחבר המשתמש כאשר  ,וסיסמא אימייל באמצעות משתמש המחברת פעולה
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

                        getOpenActivity();

                    }
                });

    }
    public void createGuestDialog()
    {
        //אורח התחברות דיאלוג היוצרת פעולה
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



    //endregion
}




