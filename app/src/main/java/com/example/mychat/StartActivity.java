package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mychat.Utils.Preferences_Utils;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartActivity extends AppCompatActivity {

    FirebaseUser firebaseUser ;
    FirebaseFirestore firestore;
     public static String GetForm ;
     Preferences_Utils preferencesUtils;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);







    }

    public void Loginin(View view) {
        Intent intent = new Intent(StartActivity.this, LOGIN_IN.class);
        startActivity(intent);
    }

    public void Regi(View view) {
        Intent intent = new Intent(StartActivity.this, SIGN_UP.class);
        startActivity(intent);
    }


}
