package com.example.mychat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.Fragments.ChatFragment;
import com.example.mychat.Fragments.ProfileFragment;
import com.example.mychat.Fragments.UsersFagment;
import com.example.mychat.MessageActivity;
import com.example.mychat.Model.Message;
import com.example.mychat.Model.User;
import com.example.mychat.R;
import com.example.mychat.StartActivity;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mychat.Fragments.ProfileFragment.userprof;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.holder> {

    private static final int MSG_LEFT_TEXT = 0;
    private static final int MSG_RIGHT_TEXT= 1;
    private static final int MSG_LEFT_IMAGE = 2;
    private static final int MSG_RIGHT_IMAGE= 3;
    private ArrayList<Message> CurrentMessages;
    private Context context;


    private FirebaseUser user;
    private  User  UserReciver;


    public  MessageAdapter(Context context , ArrayList<Message>chats ,User reciver )
    {
        CurrentMessages = new ArrayList<>(chats);
        this.context = context;


        UserReciver = reciver;
    }




    @NonNull
    @Override
    public MessageAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_RIGHT_TEXT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);


            return new holder(view);
        }
        else if(viewType==MSG_LEFT_TEXT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.holder(view) ;
        }
        else if(viewType==MSG_LEFT_IMAGE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.imagemessageleft,parent,false);
            return new MessageAdapter.holder(view) ;
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.imagemessageright,parent,false);
            return new MessageAdapter.holder(view) ;

        }

    }



    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.holder holder,  int position) {

        final Message message= CurrentMessages.get(position);

        if(!message.getSender_ID().equals(user.getUid())&&message.getMsg().equals(message.getImageMessage()))
        {  holder.ImageReceiver_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("Users").document(message.getSender_ID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                userprof =documentSnapshot.toObject(User.class);
                              context.startActivity(new Intent(context, StartActivity.class));
                            }
                        });
            }
        });
            if(UserReciver.getImageUrl().equals("default"))
            {
                holder.ImageReceiver_Image.setImageResource(R.drawable.user);
            }else
            {
                Glide.with(context).load(UserReciver.getImageUrl()).into(holder.ImageReceiver_Image);
            }

            Glide.with(context).load(message.getImageMessage()).into(holder.ImageMessageLeft);

            holder.textSeen_ImageLeft.setText(message.getHour()+":"+message.getMinute());
        }
        else if(!message.getSender_ID().equals(user.getUid())&&!message.getMsg().equals(message.getImageMessage()))
        {
            holder.ImageReceiver_Text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("Users").document(message.getSender_ID()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    userprof =documentSnapshot.toObject(User.class);
                                    context.startActivity(new Intent(context, StartActivity.class));
                                }
                            });
                }
            });
            if(UserReciver.getImageUrl().equals("default"))
            {
                holder.ImageReceiver_Text.setImageResource(R.drawable.user);
            }else
            {
                Glide.with(context).load(UserReciver.getImageUrl()).into(holder.ImageReceiver_Text);
            }
            holder.MyMessageLeft.setText(message.getMsg());

            holder.textSeen_TextLeft.setText(message.getHour()+":"+message.getMinute());

        }
        else if(message.getSender_ID().equals(user.getUid())&&message.getMsg().equals(message.getImageMessage()))//
        {
            Glide.with(context).load(message.getImageMessage()).into(holder.ImageMessageRight);


            holder.textSeen_ImageRight.setText(message.getHour()+":"+message.getMinute());
        }
        else if(message.getSender_ID().equals(user.getUid())&&!message.getMsg().equals(message.getImageMessage()))
        {

            holder.MyMessageRight.setText(message.getMsg());

            holder.textSeen_TextRight.setText(message.getHour()+":"+message.getMinute());

        }


    }

    @Override
    public int getItemCount() {
        return CurrentMessages.size();
    }


    class  holder extends  RecyclerView.ViewHolder
    {
        CircleImageView ImageReceiver_Text;
        CircleImageView ImageReceiver_Image;
        TextView MyMessageLeft;
        TextView MyMessageRight;
        TextView textSeen_TextLeft;
        TextView textSeen_TextRight;
        TextView textSeen_ImageLeft;
        TextView textSeen_ImageRight;
        ImageView ImageMessageLeft;
        ImageView ImageMessageRight;

        public holder(@NonNull View itemView) {
            super(itemView);
            ImageReceiver_Text = itemView.findViewById(R.id.ImageProfile_TEXT);
            ImageReceiver_Image = itemView.findViewById(R.id.ImageProfile_Image);
            MyMessageLeft = itemView.findViewById(R.id.MessageLeft);
            MyMessageRight = itemView.findViewById(R.id.MessageRight);
            textSeen_TextLeft = itemView.findViewById(R.id.TimeText);
            textSeen_TextRight = itemView.findViewById(R.id.txt_TimeTextRight);
            textSeen_ImageLeft = itemView.findViewById(R.id.txt_TimeImageLeft);
            textSeen_ImageRight = itemView.findViewById(R.id.txt_TimeImageRight);
            ImageMessageLeft = itemView.findViewById(R.id.ImageMessageLeft);
            ImageMessageRight = itemView.findViewById(R.id.ImageMessageRight);
        }
    }


    @Override
    public int getItemViewType(int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        Message message = CurrentMessages.get(position);
        if(message.getSender_ID().equals((user.getUid())) && message.getMsg().equals(message.getImageMessage()) )
        {
            return  MSG_RIGHT_IMAGE ;
        }
        else if(!message.getSender_ID().equals((user.getUid())) && message.getMsg().equals(message.getImageMessage()))
        {
            return  MSG_LEFT_IMAGE;
        }
        else if(message.getSender_ID().equals((user.getUid())) && !message.getMsg().equals(message.getImageMessage()))
        {
            return  MSG_RIGHT_TEXT;

        }
        else
        {
            return  MSG_LEFT_TEXT;
        }





    }
}
