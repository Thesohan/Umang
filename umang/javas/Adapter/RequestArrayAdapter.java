package com.shared.thesohankathait.notices.javas.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.shared.thesohankathait.notices.R;
import com.shared.thesohankathait.notices.javas.Fragment.Request;
import com.shared.thesohankathait.notices.javas.Utill.Initialisation;
import com.shared.thesohankathait.notices.javas.model.NoticeRequest;
import com.shared.thesohankathait.notices.javas.model.Notices;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class RequestArrayAdapter extends ArrayAdapter{

        private ArrayList<NoticeRequest> noticeRequestsArrayList=new ArrayList<>();
        private Context context;
        private ImageView noticeImageView;
        private TextView senderNameTextView,titleTextView,descriptionTextView,schoolNameTextView;
        private Button editButton,allowButton,denyButton;
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view=layoutInflater.inflate(R.layout.notification_request_row,parent,false);

            senderNameTextView=view.findViewById(R.id.senderTextView);
            titleTextView=view.findViewById(R.id.allNoiceTitleTextView);
            descriptionTextView=view.findViewById(R.id.allNoticedescriptionTextView);
            schoolNameTextView=view.findViewById(R.id.schoolNameTextView);
            editButton=view.findViewById(R.id.editButton);
            allowButton=view.findViewById(R.id.AlowButton);
            denyButton=view.findViewById(R.id.DenyButton);
            noticeImageView=view.findViewById(R.id.allNoticeImageView);
            final Notices notices=noticeRequestsArrayList.get(position).getNotices();

            senderNameTextView.setText(notices.getSender());
            titleTextView.setText(notices.getTitle());
            descriptionTextView.setText(notices.getDescription());
            schoolNameTextView.setText(noticeRequestsArrayList.get(position).getSchoolName());
            Picasso.get().load(notices.getImageUrl()).into(noticeImageView);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "edit ", Toast.LENGTH_SHORT).show();
                    showEditBoxForNotice(noticeRequestsArrayList.get(position).getSchoolName(),notices);
                }
            });

            denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adminIsSureforDeny(noticeRequestsArrayList.get(position).getSchoolName(),notices);

                }
            });

            allowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "allow", Toast.LENGTH_SHORT).show();
                    adminIsSureforAllow(noticeRequestsArrayList.get(position).getSchoolName(),notices);
                }
            });



            return view;
        }
//*********************** for edit a notice************************

    private void showEditBoxForNotice(final String schoolName, final Notices notices) {

            View view=LayoutInflater.from(context).inflate(R.layout.edit_notice,null,false);
            final EditText titleEditText=view.findViewById(R.id.editTitleEditText);
            final EditText descriptionEditText=view.findViewById(R.id.editDescriptionEditText);
           final Spinner schoolNameSpinner=view.findViewById(R.id.editSchoolSpinner);
       SpinnerAdapter spinnerArrayAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_spinner_item,
                        Initialisation.schools); //selected item will look like a spinner set from XML
        ((ArrayAdapter) spinnerArrayAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolNameSpinner.setAdapter(spinnerArrayAdapter);

        titleEditText.setText(notices.getTitle());
            descriptionEditText.setText(notices.getDescription());

            AlertDialog builder=new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setView(view)
                    .setCancelable(false)
                .setTitle("Edit Notice")
                .setMessage("If you press Yes Notice will be available for all users")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!schoolNameSpinner.getSelectedItem().equals("Select Schools")) {
                            deleteNoticeFromFireBase(schoolName, notices);//deleting notice from request reference
                            //here we are creating a new notice instance since we will delete the old notice, so we can't assign the new values to old notice
                            Notices newNotice=new Notices(descriptionEditText.getText().toString(),titleEditText.getText().toString(),
                                    notices.getSender(),notices.getImageUrl());
                            alloWNotice(schoolNameSpinner.getSelectedItem().toString(),newNotice);   //adding notice into category.
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(context, "Select School first!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No",null)
                .show();


    }

    // //********************for allow a notice******************


    private void adminIsSureforAllow(final String schoolName, final Notices notices) {

        AlertDialog builder=new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Allow Notice")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNoticeFromFireBase(schoolName,notices); //deleting notice from request reference
                        alloWNotice(schoolName,notices);   //adding notice into category.
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No",null)
                .show();


    }

    private void alloWNotice(String schoolName, Notices notices) {

            FirebaseDatabase.getInstance().getReference("Category").child(schoolName).push().setValue(notices).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "You allowed ths notice", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    @Override
    public int getCount() {
        return noticeRequestsArrayList.size();
    }

    public RequestArrayAdapter(Context context, ArrayList<NoticeRequest> noticeRequestsArrayList) {
        super(context,R.layout.notification_request_row,noticeRequestsArrayList);
        this.noticeRequestsArrayList=noticeRequestsArrayList;
        this.context=context;
    }



    private boolean BothEquals(Notices currentNotice, Notices notices) {

            return currentNotice.getDescription().equals(notices.getDescription()) &&
                    currentNotice.getImageUrl().equals(notices.getImageUrl()) &&
                    currentNotice.getSender().equals(notices.getSender()) &&
                    currentNotice.getTitle().equals(notices.getTitle());

        }



        //********************for deny a notice******************


    private void adminIsSureforDeny(final String schoolName, final Notices notices) {

        AlertDialog builder=new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Delete Notice")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNoticeFromFireBase(schoolName,notices);
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No",null)
                .show();


    }


    private void deleteNoticeFromFireBase(String schoolName, final Notices notices) {

        FirebaseDatabase.getInstance().getReference("Requests").child(schoolName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot finalDataSnapshot:dataSnapshot.getChildren()){
                    Notices currentNotice=finalDataSnapshot.getValue(Notices.class);
                    if(BothEquals(currentNotice,notices)){
                        finalDataSnapshot.getRef().removeValue();

                        Toast.makeText(context, "Notice Removed", Toast.LENGTH_SHORT).show();
                        for(int i = 0; i<Request.noticeRequestArrayList.size(); i++){
                            if(BothEquals(Request.noticeRequestArrayList.get(i).getNotices(),notices)){
                                Request.noticeRequestArrayList.remove(i);
                                break;
                            }
                        }
                        Request.requestArrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
