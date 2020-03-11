package com.example.mychat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mychat.Utils.Constants.USER;


public class ProfileFragment extends Fragment {
    CircleImageView Image_Profile;
    TextView Username, ProfileEmail, posts, Following, Followers;
    Button Followme;
    ImageView Group;
    ImageView Comments;
    FirebaseFirestore firestore;
    Calendar calendar;
    public static User userprof;
    FirebaseAuth auth;
    ProgressDialog dialog;
    RelativeLayout profile2;
    public static String password, email;
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firestore = FirebaseFirestore.getInstance();
        Image_Profile = view.findViewById(R.id.ImageProfile);
        Username = view.findViewById(R.id.UserProfileName);
        Followers = view.findViewById(R.id.Followers);
        Following = view.findViewById(R.id.Following);
        profile2 = view.findViewById(R.id.profile2);
        posts = view.findViewById(R.id.posts);
        ProfileEmail = view.findViewById(R.id.ProfileEmail);
        Followme = view.findViewById(R.id.FOLLOWME);

     init();

        return view;
    }
void init(){
    dialog = new ProgressDialog(getActivity());
    dialog.show();
    dialog.setContentView(R.layout.dialog);
    dialog.getWindow().setBackgroundDrawableResource(
            android.R.color.transparent
    );
    firestore.collection("Insta Users").document(USER.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {

            if (documentSnapshot.get("followers") != null) {

                profile2.setVisibility(View.VISIBLE);
                List<String> s = (List<String>) documentSnapshot.get("followers");
                Following.setText(String.valueOf(s.size()));
                s = (List<String>) documentSnapshot.get("following");
                Followers.setText(String.valueOf(s.size()));
                s = (List<String>) documentSnapshot.get("posts");
                posts.setText(String.valueOf(s.size()));
            }
            else
                profile2.setVisibility(View.GONE);
            dialog.dismiss();
        }
    });
    ProfileEmail.setText(userprof.getEmail());
    Username.setText(userprof.getUsername());
    firestore.collection("Users")
            .document(userprof.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            User user1 = documentSnapshot.toObject(User.class);
            if (user1.getImageUrl().equals("default")) {
                Image_Profile.setImageResource(R.drawable.user);
            } else {
                Glide.with(getContext()).load(user1.getImageUrl()).into(Image_Profile);
            }
        }
    });
}
}
