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
import com.example.mychat.Model.User;
import com.example.mychat.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.holder> {


    private ArrayList<User> CurrentUsers;
    private Context context;
    private ArrayList<User> CurrentUsersFilter;

    public  UserAdapter(Context context , ArrayList<User>users)
    {
        CurrentUsers = new ArrayList<>(users);
        this.context = context;
        CurrentUsersFilter = new ArrayList<>(users);
    }



    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.userlayout2,parent,false);
        holder  holder = new holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, final int position) {
        holder.username2.setText(CurrentUsers.get(position).getUsername());
        if(CurrentUsers.get(position).getImageUrl().equals("default"))
        {
            holder.imageprofile2.setImageResource(R.drawable.user);
        }
        else
        {
            Glide.with(context).load(CurrentUsers.get(position).getImageUrl()).into(holder.imageprofile2);
        }

        if(CurrentUsers.get(position).getStatus().equals("Online"))
        {
            holder.Offline2.setVisibility(View.GONE);
            holder.Online2.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.Offline2.setVisibility(View.VISIBLE);
            holder.Online2.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("CurrentUser", CurrentUsers.get(position));
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return CurrentUsers.size();
    }


    class  holder extends  RecyclerView.ViewHolder
    {
        private CircleImageView imageprofile2;
        private TextView username2;
        private CircleImageView Online2;
        private CircleImageView Offline2;
        public holder(@NonNull View itemView) {
            super(itemView);
            imageprofile2 = itemView.findViewById(R.id.userItemImageprofile2);
            username2 = itemView.findViewById(R.id.Usernameitem2);
            Online2 = itemView.findViewById(R.id.Image_Status_On2);
            Offline2 =itemView.findViewById(R.id.Image_Status_Off2);
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
            CurrentUsers.clear();
            CurrentUsers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
