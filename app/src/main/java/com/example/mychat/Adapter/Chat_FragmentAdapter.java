package com.example.mychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.MessageActivity;
import com.example.mychat.Model.Message;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_FragmentAdapter extends RecyclerView.Adapter<Chat_FragmentAdapter.holder> {


    private ArrayList<User> UsersChats;
    private Context context;
    private ArrayList<User> CurrentUsersFilter;
    private Preferences_Utils preferencesUtils;
    FirebaseUser firebaseUser;


    public  Chat_FragmentAdapter(Context context , ArrayList<User>users )
    {
        UsersChats = new ArrayList<>(users);
        this.context = context;
        preferencesUtils = new Preferences_Utils();
        CurrentUsersFilter = new ArrayList<>(users);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Chat_FragmentAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);
        Chat_FragmentAdapter.holder holder = new Chat_FragmentAdapter.holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Chat_FragmentAdapter.holder holder, final int position) {
        holder.username.setText(UsersChats.get(position).getUsername());
        if(UsersChats.get(position).getImageUrl().equals("default"))
        {
            holder.imageprofile.setImageResource(R.drawable.user);
        }
        else
        {
            Glide.with(context).load(UsersChats.get(position).getImageUrl()).into(holder.imageprofile);
        }

        if(!UsersChats.get(position).getHourLastMessage().equals(""))
        {
            holder.LastMessageTime.setText(UsersChats.get(position).getHourLastMessage()+":"+UsersChats.get(position).getMinuteLastMessage());

        }

        if(UsersChats.get(position).getImageLastMessage().equals(UsersChats.get(position).getLastMessage()))
        {
            holder.LastMessage.setText("Image");

        }
        else
        {
            holder.LastMessage.setText(UsersChats.get(position).getLastMessage());
        }

        if(UsersChats.get(position).getStatus().equals("Online"))
        {
            holder.Offline.setVisibility(View.GONE);
            holder.Online.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.Offline.setVisibility(View.VISIBLE);
            holder.Online.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("CurrentUser", UsersChats.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return UsersChats.size();
    }


    class  holder extends  RecyclerView.ViewHolder
    {
        private CircleImageView imageprofile;
        private TextView username;
        private CircleImageView Online;
        private CircleImageView Offline;
        private TextView LastMessage ;
        private TextView LastMessageTime;
        public holder(@NonNull View itemView) {
            super(itemView);
            imageprofile = itemView.findViewById(R.id.userItemImageprofile);
            username = itemView.findViewById(R.id.Usernameitem);
            Online = itemView.findViewById(R.id.Image_Status_On);
            Offline =itemView.findViewById(R.id.Image_Status_Off);
            LastMessage  = itemView.findViewById(R.id.lastMessage);
            LastMessageTime = itemView.findViewById(R.id.Time);
        }
    }



    public Filter getFilter() {
        return examplefilter;
    }

    public Filter examplefilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length()==0)
            {
                filteredList.addAll(CurrentUsersFilter);
            }
            else
            {
                String filterpattern = constraint.toString().toLowerCase().trim();
                for(User item :CurrentUsersFilter)
                {
                    if(item.getUsername().toLowerCase().contains(filterpattern))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            UsersChats.clear();
            UsersChats.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
