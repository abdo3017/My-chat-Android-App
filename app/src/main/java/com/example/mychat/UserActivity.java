package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.Adapter.Chat_FragmentAdapter;
import com.example.mychat.Fragments.ChatFragment;
import com.example.mychat.Fragments.ProfileFragment;
import com.example.mychat.Fragments.UsersFagment;
import com.example.mychat.Model.User;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mychat.Fragments.ProfileFragment.userprof;
import static com.example.mychat.Utils.Constants.USER;

public class UserActivity extends AppCompatActivity {

    CircleImageView imageprofile;
    FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    Preferences_Utils preferencesUtils;
    ArrayList<User> AllUsers;
    TabLayout tabLayout;
    Boolean ISLogout = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        MakeMeOffline1();
        Status("OffLine");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();


    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Status("OffLine");
        if (ISLogout == false) {
            MakeMeOffline1();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUserActivity);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        AllUsers = new ArrayList<>();
        preferencesUtils = new Preferences_Utils();
        firestore = FirebaseFirestore.getInstance();
        imageprofile = findViewById(R.id.profile_image);
        setImage();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        userprof = USER;
        viewPageAdapter.addFragment(new ChatFragment(), "Chat");
        viewPageAdapter.addFragment(new UsersFagment(), "Users");
        viewPageAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(viewPageAdapter);
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_chat_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_group_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_assignment_ind_white_24dp);

        GetAllUsers();


    }

    public void MakeMeOnline() {
        Query query = firestore.collection("Users").document(firebaseUser.getUid()).
                collection("UserChats");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    User user1 = snapshot.toObject(User.class);
                    firestore.collection("Users")
                            .document(user1.getId()).collection("UserChats")
                            .document(firebaseUser.getUid()).update("status", "Online");
                }
            }
        });
    }

    private void GetAllUsers() {
        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    User user1 = snapshot.toObject(User.class);
                    if (user1.getId() != firebaseUser.getUid()) {
                        AllUsers.add(user1);
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("GetAllUsers", "Error When Load All Users");
            }
        });
    }

    private void setImage() {
        if (preferencesUtils.get_Image(getApplicationContext()).toString().equals("default")) {
            imageprofile.setImageResource(R.drawable.user);
        } else {
            Glide.with(UserActivity.this).load(preferencesUtils.get_Image(getApplicationContext()))
                    .into(imageprofile);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                Query query = firestore.collection("Users")
                        .document(firebaseUser.getUid()).
                                collection("UserChats");
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            User user1 = snapshot.toObject(User.class);
                            firestore.collection("Users")
                                    .document(user1.getId()).collection("UserChats")
                                    .document(firebaseUser.getUid()).update("status", "OffLine");
                        }


                        preferencesUtils.Save_ID("", getApplicationContext());
                        preferencesUtils.Save_Image("", getApplicationContext());
                        preferencesUtils.Save_Name("", getApplicationContext());
                        preferencesUtils.Save_Status("", getApplicationContext());
                        preferencesUtils.Save_Password("", getApplicationContext());
                        preferencesUtils.Save_Email("", getApplicationContext());
                        preferencesUtils.Save_LastMessageImage("", getApplicationContext());
                        preferencesUtils.Save_LastMessageImage("", getApplicationContext());
                        preferencesUtils.Save_LastMessageDate("", getApplicationContext());
                        preferencesUtils.Save_LastMessageMinute("", getApplicationContext());
                        preferencesUtils.Save_LastMessageHour("", getApplicationContext());
                        ISLogout = true;
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();


                        Intent intent = new Intent(UserActivity.this, LOGIN_IN.class);
                        startActivity(intent);
                        finish();

                    }
                });

                return true;
        }

        return false;
    }

    class ViewPageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPageAdapter(FragmentManager manager) {
            super(manager);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 2)
                userprof = USER;
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    private void Status(final String status) {

        firestore.collection("Users").document(firebaseUser.getUid()).update("status", status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Status("Online");
        MakeMeOnline();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("Online");
        MakeMeOnline();

    }

    public void MakeMeOffline1() {
        Query query = firestore.collection("Users").document(firebaseUser.getUid()).
                collection("UserChats");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    User user1 = snapshot.toObject(User.class);
                    firestore.collection("Users")
                            .document(user1.getId()).collection("UserChats")
                            .document(firebaseUser.getUid()).update("status", "OffLine");
                }

            }
        });


    }


}
