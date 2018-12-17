package com.example.adam.qarobot;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button register;
    private Button login;
    private EditText username;
    private EditText passwd;
    private MyDBHelper myDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myDBHelper = new MyDBHelper(this,"Data.db",null, 1);
        myDBHelper.getWritableDatabase();
        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.user);
        passwd = (EditText) findViewById(R.id.passwd);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Register");
                View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.reg, null);
                builder.setView(view1);
                final EditText editText1 = (EditText) view1.findViewById(R.id.user_reg);
                final EditText editText2 = (EditText) view1.findViewById(R.id.passwd_reg);
                builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String user = editText1.getText().toString();
                        String pass = editText2.getText().toString();
                        SQLiteDatabase database = myDBHelper.getWritableDatabase();
                        Cursor cursor = database.rawQuery("select * from User where username=\"" + user +"\"", null);
                        if (cursor.moveToFirst()) {
                            Toast.makeText(LoginActivity.this,"This username has been used, please choose another one", Toast.LENGTH_LONG).show();
                        } else {
                            ContentValues values = new ContentValues();
                            values.put("username", user);
                            values.put("password", pass);
                            database.insert("User",null, values);
                            Toast.makeText(LoginActivity.this,"Successfully registered", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = passwd.getText().toString();
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select password from User where username=\""+user+"\"", null);
                if (cursor.moveToFirst()) {
                    String tmp = cursor.getString(cursor.getColumnIndex("password"));
                    if (pass.equals(tmp)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username",user);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    } else{
                        Toast.makeText(LoginActivity.this,"Wrong Password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,"No such user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
