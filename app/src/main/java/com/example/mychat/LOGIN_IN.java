package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychat.Model.User;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.mychat.Utils.Constants.USER;

public class LOGIN_IN extends AppCompatActivity {


    EditText Email;
    EditText Password;
    FirebaseAuth auth;
    FirebaseUser user;
    User currentuser;
    Preferences_Utils preferencesUtils;
    FirebaseFirestore firestore,checkdb;
    DocumentReference reference;
    EditText message;
    ProgressDialog progressDialog;
    private  boolean ShowPassword  = true;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        preferencesUtils = new Preferences_Utils();
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        firestore  = FirebaseFirestore.getInstance();
        checkdb=FirebaseFirestore.getInstance();
        preferencesUtils = new Preferences_Utils();
        message = findViewById(R.id.message);
        progressDialog = new ProgressDialog(this);
    }



    public void Login(View view) {
        if(TextUtils.isEmpty(Email.getText().toString()))
        {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Password.getText().toString()))
        {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String E = Email.getText().toString().toLowerCase();
            String P = Password.getText().toString().toLowerCase();
            progressDialog.setTitle("Signed in");
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            auth.signInWithEmailAndPassword(E,P)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                user = auth.getCurrentUser();
                                reference = firestore.collection("Users")
                                        .document(user.getUid());
                                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if((User)documentSnapshot.toObject(User.class)!=null)
                                        {
                                            currentuser = (User)documentSnapshot.toObject(User.class);
                                            USER=new User(currentuser);
                                            save(currentuser);
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LOGIN_IN.this , UserActivity.class);
                                            startActivity(intent);
                                            EmptyField();
                                            finish();
                                        }
                                        else{
                                            checkdb.collection("Insta Users").document(user.getEmail()).get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            currentuser = new User(documentSnapshot.getString("email").toLowerCase(),
                                                                    user.getUid(),
                                                                    documentSnapshot.getString("image"),
                                                                    documentSnapshot.getString("name"),
                                                                    documentSnapshot.getString("password"),
                                                                    "Online",
                                                                    "",
                                                                    "",
                                                                    "","","");
                                                            save(currentuser);
                                                            USER=new User(currentuser);
                                                            checkdb.collection("Users").document(user.getUid()).set(currentuser);
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(LOGIN_IN.this , UserActivity.class);
                                                            startActivity(intent);
                                                            EmptyField();
                                                            finish();
                                                        }
                                                    });

                                        }

                                    }
                                });

                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(LOGIN_IN.this, "Error At LOGIN_IN", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(LOGIN_IN.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }

    }
    void save(User currentuser){
        preferencesUtils.Save_ID(currentuser.getId(),getApplicationContext());
        preferencesUtils.Save_Image(currentuser.getImageUrl(),getApplicationContext());
        preferencesUtils.Save_Name(currentuser.getUsername(),getApplicationContext());
        preferencesUtils.Save_Password(currentuser.getPassword(),getApplicationContext());
        preferencesUtils.Save_Email(currentuser.getEmail(),getApplicationContext());
        preferencesUtils.Save_Status(currentuser.getStatus(),getApplicationContext());
        preferencesUtils.Save_LastMessageMinute(currentuser.getMinuteLastMessage(),getApplicationContext());
        preferencesUtils.Save_LastMessageHour(currentuser.getDateLastMessage(),getApplicationContext());
        preferencesUtils.Save_LastMessageDate(currentuser.getHourLastMessage(),getApplicationContext());
        preferencesUtils.Save_LastMessage(currentuser.getLastMessage(),getApplicationContext());
        preferencesUtils.Save_LastMessageImage(currentuser.getImageLastMessage(),getApplicationContext());
    }
    public void EmptyField()
    {
        Email.setText("");
        Password.setText("");
    }


    public void Sign_UP(View view) {
        Intent intent  = new Intent(LOGIN_IN.this, SIGN_UP.class);
        startActivity(intent);
        finish();
    }

    public void SHOW_PASSWORD(View view) {


        if(ShowPassword)
        {
            Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ShowPassword = false;
        }
        else {
            Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ShowPassword = true;
        }
    }
}
