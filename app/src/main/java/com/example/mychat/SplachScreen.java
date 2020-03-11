package com.example.mychat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.os.Handler;

import com.example.mychat.Model.User;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.example.mychat.Utils.Constants.USER;

public class SplachScreen extends AppCompatActivity {
    private Preferences_Utils Prefer;
    private FirebaseUser User;
FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);
        Prefer = new Preferences_Utils();
        User = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Prefer.get_ID(getApplicationContext()) != null &&
                        !Prefer.get_ID(getApplicationContext()).equals("") && User != null) {
                    System.out.println(User.getEmail()+"----------------");
                    firestore.collection("Users").document(User.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            USER=new User(documentSnapshot.toObject(User.class));
                            Intent intent1 = new Intent(SplachScreen.this, UserActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    });

                } else {
                    Intent intent1 = new Intent(SplachScreen.this, LOGIN_IN.class);
                    startActivity(intent1);
                    finish();
                }

            }
        }, 1000);


    }
}
