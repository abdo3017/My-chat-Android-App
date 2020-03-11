package com.example.mychat.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.mychat.Adapter.Chat_FragmentAdapter;
import com.example.mychat.Adapter.UserAdapter;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.file.FileStore;
import java.util.ArrayList;

import javax.annotation.Nullable;


public class UsersFagment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;
    ArrayList<User> users;
    UserAdapter userAdapter;
    EditText searchText2;

    public UsersFagment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_users_fagment, container, false);
        recyclerView = view.findViewById(R.id.UserRecycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        searchText2 = view.findViewById(R.id.SearchText2);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        Query query = firestore.collection("Users");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

               if(queryDocumentSnapshots!=null)
               {
                   users.clear();
                   for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                   {
                       User ChatUser  = snapshot.toObject(User.class);
                       if(!ChatUser.getId().equals(user.getUid()))
                       {
                           users.add(ChatUser);
                       }

                   }


                   userAdapter = new UserAdapter(getActivity(),users);
                   recyclerView.setAdapter(userAdapter);

               }
            }
        }) ;






        searchText2.setFocusableInTouchMode(true);
        searchText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.equals(""))
                {
                    userAdapter.getFilter().filter(charSequence.toString());
                }
                else
                {
                    userAdapter = new UserAdapter(getActivity(),users);
                    recyclerView.setAdapter(userAdapter);
                    searchText2.setHint("Search...");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return  view;
    }



}
