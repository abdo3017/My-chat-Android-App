package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.Adapter.MessageAdapter;
import com.example.mychat.Model.Message;
import com.example.mychat.Model.User;
import com.example.mychat.Utils.Preferences_Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.Nullable;

import static java.lang.System.*;

public class MessageActivity extends AppCompatActivity {


    private User Reciver;
    private FirebaseUser currentuser;
    private FirebaseAuth auth;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private TextView Username;
    private TextView Status;
    private EditText Messagetext;
    private StorageReference mStorageRef;

    Calendar calendar;
    Bitmap BitmapImage ;
    Uri ImageMessage = null;
    private ArrayList<Message> Msgs;
    private ArrayList<Message> Msgs2;
    private int Collection_Size_Reciver;
    private int Collection_Size_Sender;

    UploadTask uploadTask;
    Preferences_Utils preferencesUtils;
    private boolean b;

    private int CameraPermission = 2;
    private int GallryPermission = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        auth = FirebaseAuth.getInstance();
        preferencesUtils = new Preferences_Utils();
        mStorageRef = FirebaseStorage.getInstance().getReference("ChatImages");
        currentuser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        Messagetext = findViewById(R.id.text_send);
        recyclerView = findViewById(R.id.MessageRecycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        Username = findViewById(R.id.usernameMessageActivity);
        Status = findViewById(R.id.Status);
        Msgs = new ArrayList<>();
        calendar = Calendar.getInstance();
        Intent intent = getIntent();
        Reciver = (User) intent.getSerializableExtra("CurrentUser");
        Username.setText(Reciver.getUsername());
        Status.setText(Reciver.getStatus());
        getCollectionsSize();

        LoadMessages();
        CheckReciverOnline();






    }



    private void CheckReciverOnline()
    {
        Query query = firestore.collection("Users");
        query.whereEqualTo("email",Reciver.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                   User user1 =  snapshot.toObject(User.class);
                    Toast.makeText(MessageActivity.this, user1.getUsername(), Toast.LENGTH_SHORT).show();
                   Status.setText(user1.getStatus());
                   break;
                }

            }
        });
    }
    private void CheckUserExist()
    {
        firestore.collection("Users").document(currentuser.getUid())
                .collection("UserChats").document(Reciver.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(!documentSnapshot.exists())
                    {
                        firestore.collection("Users").document(currentuser.getUid())
                                .collection("UserChats").document(Reciver.getId()).set(Reciver)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("Insert UserChat","Insert Successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Insert UserChat","Insert Not Successfully");
                            }
                        });
                    }
                }
            }
        });


        firestore.collection("Users").document(Reciver.getId())
                .collection("UserChats").document(currentuser.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(!documentSnapshot.exists())
                    {
                        User ChatUser = new User(
                                preferencesUtils.get_Email(getApplicationContext()),
                                preferencesUtils.get_ID(getApplicationContext()),
                                preferencesUtils.get_Image(getApplicationContext()),
                                preferencesUtils.get_Username(getApplicationContext()),
                                preferencesUtils.get_Password(getApplicationContext()),
                                preferencesUtils.get_Status(getApplicationContext()),
                                preferencesUtils.get_LastMessage(getApplicationContext()),
                                preferencesUtils.get_LastMessageHour(getApplicationContext()),
                                preferencesUtils.get_LastMessageDate(getApplicationContext()),
                                preferencesUtils.get_LastMessageMinute(getApplicationContext()),
                                preferencesUtils.get_LastMessageImage(getApplicationContext()));


                        firestore.collection("Users").document(Reciver.getId())
                                .collection("UserChats").document(currentuser.getUid()).set(ChatUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("Insert UserChat","Insert Successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Insert UserChat","Insert Not Successfully");
                            }
                        });
                    }
                }
            }
        });


    }

    public void SendMessage(View view) {

        if (!TextUtils.isEmpty(Messagetext.getText().toString()) && ImageMessage!=null) {

            SaveToDatabase(ImageMessage.toString(),Messagetext.getText().toString());

        }
        else if(!TextUtils.isEmpty(Messagetext.getText().toString()) && ImageMessage==null)
        {
            SaveToDatabase("",Messagetext.getText().toString());
        }
        else
        {
            Toast.makeText(this, "Text is Empty !!!", Toast.LENGTH_SHORT).show();
        }

    }



    private void SaveToDatabase(String Image, final String msg)
    {   CheckUserExist();
        getCollectionsSize();
        Collection_Size_Reciver++;
        Collection_Size_Sender++;
        calendar = Calendar.getInstance();
        int Hour = calendar.get(Calendar.HOUR);
        int Minute = calendar.get(Calendar.MINUTE);
        int Date = calendar.get(Calendar.DATE);
        final Message Current_Message = new Message(String.valueOf(Date),String.valueOf(Hour),
                String.valueOf(Minute),msg,Collection_Size_Sender, currentuser.getUid(),
                Reciver.getId(),Image.toString());

        firestore.collection("Users").document(currentuser.getUid())
                .collection("UserChats").document(Reciver.getId())
                .collection("Chats").document(Collection_Size_Sender + "").set(Current_Message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Messagetext.setText("");
                ImageMessage = null;
                UpdateLastMessage(Current_Message);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Error of Message Sender", Toast.LENGTH_SHORT).show();
            }
        });


        final Message Current_Message1 = new Message(String.valueOf(Date)
                ,String.valueOf(Hour)
                ,String.valueOf(Minute)
                ,msg
                ,Collection_Size_Reciver, currentuser.getUid(), Reciver.getId(), Image.toString());

        firestore.collection("Users").document(Reciver.getId())
                .collection("UserChats").document(currentuser.getUid())
                .collection("Chats").document(Collection_Size_Reciver + "")
                .set(Current_Message1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessageActivity.this, "Error of Message Receiver", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void UpdateLastMessage(Message Cm)
    {
        Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        preferencesUtils.Save_LastMessage(Cm.getMsg(),getApplicationContext());
        preferencesUtils.Save_LastMessageImage(Cm.getImageMessage(),getApplicationContext());
        preferencesUtils.Save_LastMessageDate(Cm.getDate(),getApplicationContext());
        preferencesUtils.Save_LastMessageMinute(Cm.getMinute(),getApplicationContext());
        preferencesUtils.Save_LastMessageHour(Cm.getHour(),getApplicationContext());

        firestore.collection("Users")
                .document(currentuser.getUid())
                .collection("UserChats")
                .document(Reciver.getId()).update("imageLastMessage",Cm.getImageMessage());

        firestore.collection("Users")
                .document(currentuser.getUid())
                .collection("UserChats")
                .document(Reciver.getId()).update("lastMessage",Cm.getMsg());

        firestore.collection("Users")
                .document(currentuser.getUid())
                .collection("UserChats")
                .document(Reciver.getId()).update("dateLastMessage",Cm.getDate());


        firestore.collection("Users")
                .document(currentuser.getUid())
                .collection("UserChats")
                .document(Reciver.getId()).update("minuteLastMessage",Cm.getMinute());


        firestore.collection("Users")
                .document(currentuser.getUid())
                .collection("UserChats")
                .document(Reciver.getId()).update("hourLastMessage",Cm.getHour());

       ////
        firestore.collection("Users")
                .document(Reciver.getId())
                .collection("UserChats")
                .document(currentuser.getUid()).update("imageLastMessage",Cm.getImageMessage());

        firestore.collection("Users")
                .document(Reciver.getId())
                .collection("UserChats")
                .document(currentuser.getUid()).update("lastMessage",Cm.getMsg());

        firestore.collection("Users")
                .document(Reciver.getId())
                .collection("UserChats")
                .document(currentuser.getUid()).update("dateLastMessage",Cm.getDate());


        firestore.collection("Users")
                .document(Reciver.getId())
                .collection("UserChats")
                .document(currentuser.getUid()).update("minuteLastMessage",Cm.getMinute());


        firestore.collection("Users")
                .document(Reciver.getId())
                .collection("UserChats")
                .document(currentuser.getUid()).update("hourLastMessage",Cm.getHour());




    }



    //////////////////
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (ImageMessage != null) {
            final StorageReference fileReference = mStorageRef.child(currentTimeMillis()
                    + "." + getFileExtension(ImageMessage));


            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);

            progressDialog.show();



            uploadTask =   fileReference.putFile(ImageMessage);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            ImageMessage = uri;
                            Messagetext.setText(ImageMessage.toString());

                        }
                    });

                }
            })      .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            })  .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

                }
            });

            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();//dismiss dialog

                    if(uploadTask.isComplete())
                    {
                        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(MessageActivity.this, "Delete Success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MessageActivity.this, "Not Delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        uploadTask.cancel();
                        Toast.makeText(MessageActivity.this, "Upload Canceled", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }


    }

    private void LoadMessages()
    {

        Msgs.clear();

        Query query =  firestore.collection("Users").document(currentuser.getUid())
                .collection("UserChats").document(Reciver.getId())
                .collection("Chats").orderBy("docName", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Msgs.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Message message = snapshot.toObject(Message.class);
                    Msgs.add(message);
                }

                messageAdapter = new MessageAdapter(getApplicationContext(), Msgs, Reciver);
                recyclerView.setAdapter(messageAdapter);

            }
        });


    }


    private void getCollectionsSize()
    {
        firestore.collection("Users").document(currentuser.getUid()).collection("UserChats")
                .document(Reciver.getId()).collection("Chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Collection_Size_Sender = queryDocumentSnapshots.size();
            }
        });

        firestore.collection("Users").document(Reciver.getId()).collection("UserChats")
                .document(currentuser.getUid()).collection("Chats").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Collection_Size_Reciver = queryDocumentSnapshots.size();
            }
        });
    }

    private void Status(final String status) {

        firestore.collection("Users").document(currentuser.getUid()).update("status", status);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
        MakeMeOffline();

    }

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

    public void MakeMeOnline()
    {
        Query query = firestore.collection("Users").document(currentuser.getUid()).
                collection("UserChats");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    User user1 = snapshot.toObject(User.class);
                    firestore.collection("Users")
                            .document(user1.getId()).collection("UserChats")
                            .document(currentuser.getUid()).update("status","Online");
                }
            }
        });
    }

    public void MakeMeOffline()
    {
        Query query = firestore.collection("Users").document(currentuser.getUid()).
                collection("UserChats");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                for(DocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    User user1 = snapshot.toObject(User.class);
                    firestore.collection("Users")
                            .document(user1.getId()).collection("UserChats")
                            .document(currentuser.getUid()).update("status","OffLine");
                }
            }
        });
    }



    public void btn_Image(View view) {


        requestStoragePermission(GallryPermission);
        Toast.makeText(this, "Image", Toast.LENGTH_SHORT).show();

    }


    public void btn_Camera(View view) {

        requestStoragePermission(CameraPermission);
        Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show();
    }


    private void requestStoragePermission(int Permission) {
        if(Permission==GallryPermission)
        {
            Toast.makeText(this, "Here1", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MessageActivity.this,
                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GallryPermission);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, GallryPermission);
            }
        }
        else
        {
            Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MessageActivity.this,
                                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CameraPermission);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, CameraPermission);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GallryPermission)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 2);
            } else {
                //Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            } else {
               // Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if(requestCode==2)
        {
            if(data!=null)
            {
                ImageMessage = data.getData();
                uploadFile();

            }

        }

        else if(requestCode==1)
        {

               BitmapImage= (Bitmap) data.getExtras().get("data");
               ImageMessage = getImageUri(getApplicationContext(),BitmapImage);
               uploadFile();

        }

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void btn_Voice(View view) {
    }


    public void BackArrow(View view) {

        Intent intent  = new Intent(MessageActivity.this,UserActivity.class);
        startActivity(intent);
        finish();
    }
}

