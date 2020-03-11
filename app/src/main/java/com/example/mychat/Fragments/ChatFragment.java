package com.example.mychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.mychat.Adapter.Chat_FragmentAdapter;
import com.example.mychat.Adapter.UserAdapter;
import com.example.mychat.Model.Message;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.file.FileStore;
import java.util.ArrayList;

import javax.annotation.Nullable;


public class ChatFragment extends Fragment {

   private RecyclerView recyclerView;
   private ArrayList<User>MUsers;
   private FirebaseFirestore fileStore;
   private Chat_FragmentAdapter chat_fragmentAdapter ;
   private ArrayList<User>AllUsers;
   FirebaseUser user;
   EditText TextSearch;
   private ArrayList<User>FilterUsers ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.ChatFragmentRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fileStore   =  FirebaseFirestore.getInstance();
        MUsers = new ArrayList<>();
        AllUsers = new ArrayList<>();
        FilterUsers = new ArrayList<>();
        TextSearch = view.findViewById(R.id.SearchText);
        user = FirebaseAuth.getInstance().getCurrentUser();

         Query query = fileStore.collection("Users")
                .document(user.getUid())
                .collection("UserChats").orderBy("dateLastMessage", Query.Direction.DESCENDING)
                 .orderBy("hourLastMessage", Query.Direction.DESCENDING)
                 .orderBy("minuteLastMessage", Query.Direction.DESCENDING);



         query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                 @Override
                 public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                     if(queryDocumentSnapshots!=null)
                     {
                         MUsers.clear();
                         for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                         {
                             User ChatUser  = snapshot.toObject(User.class);
                             MUsers.add(ChatUser);
                         }

                         chat_fragmentAdapter = new Chat_FragmentAdapter(getActivity(),MUsers);
                         recyclerView.setAdapter(chat_fragmentAdapter);

                     }
                 }
             }) ;


        TextSearch.setFocusableInTouchMode(true);
        TextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TextSearch.setEnabled(true);
            }
        });
        TextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence))
                {
                    chat_fragmentAdapter.getFilter().filter(charSequence.toString());
                }
                else
                {
                    chat_fragmentAdapter = new Chat_FragmentAdapter(getActivity(),MUsers);
                    recyclerView.setAdapter(chat_fragmentAdapter);
                    TextSearch.setHint("Search...");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

               User user1 = MUsers.get(viewHolder.getAdapterPosition());
                MUsers.remove(viewHolder.getAdapterPosition());
               fileStore.collection("Users")
                       .document(user.getUid())
                       .collection("UserChats")
                       .document(user1.getId())
                       .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {

                       chat_fragmentAdapter = new Chat_FragmentAdapter(getActivity(),MUsers);
                       recyclerView.setAdapter(chat_fragmentAdapter);
                       Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getContext(), "User not deleted", Toast.LENGTH_SHORT).show();
                   }
               });
            }
        }).attachToRecyclerView(recyclerView);


        return view;
    }







}
