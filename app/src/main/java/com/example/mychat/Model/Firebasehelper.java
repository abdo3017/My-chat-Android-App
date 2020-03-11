package com.example.mychat.Model;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class Firebasehelper {

    private FirebaseFirestore firestore;
    private FirebaseUser user ;
    private DocumentReference reference;
    private FirebaseAuth auth;
    private  User userClass;
    static public int donesignup ;
    static public int donesave;
    private Context context;
    public Firebasehelper(Context context) {

        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userClass = null;



    }

    public boolean getnotvalid()
    {
        return  false;

    }
    public boolean getvalid()
    {
        return  true;
    }
    public User Signin (String email , String password)
    {

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    user = auth.getCurrentUser();
                    reference = firestore.collection("Users").document(user.getUid());
                    reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            User user1 = (User)documentSnapshot.toObject(User.class);
                            if(user!=null)
                            {
                                userClass= user1;

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            userClass =null;
                        }
                    });


                }
                else
                {
                    userClass = null;
                }
            }
        });



        if(userClass!=null)
        {
            return  userClass;
        }
        else
            return  null;

        /////////////////////

    }


    public void SignUp( User currentuser )
    {
        userClass= currentuser;
        auth.createUserWithEmailAndPassword(userClass.getEmail(),userClass.getPassword()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {


                    user = auth.getCurrentUser();

                    userClass.setId(user.getUid());

                    SaveUser(userClass);





                }



            }
        });

    }


    public  void SaveUser(User user)
    {



        firestore.collection("Users").document(user.getId()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, "fialure", Toast.LENGTH_SHORT).show();
            }
        });





    }





}
