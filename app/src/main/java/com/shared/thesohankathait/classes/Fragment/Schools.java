package com.shared.thesohankathait.classes.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shared.thesohankathait.notices.R;
import com.shared.thesohankathait.classes.Activity.AllNotification;
import com.shared.thesohankathait.classes.Utill.Initialisation;
import com.shared.thesohankathait.classes.Adapter.SchoolsArrayAdapter;
import com.shared.thesohankathait.classes.Utill.Admin;
import com.shared.thesohankathait.classes.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class Schools extends Fragment {

    private ListView schoolsListView;
    private ImageButton addSchoolsFloatingActionButton;
    public SchoolsArrayAdapter schoolsArrayAdapter;
    private TextView hintTextView;
    public static ProgressBar schoolProgressbar;

    public CardView cardFloatingLayout;
    public static Schools schoolsFragmentInstance;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schools_fragment, container, false);

        context = getContext();
        schoolProgressbar = view.findViewById(R.id.schoolProgressbar);
        // requied for refreshing school list from outside
        schoolsFragmentInstance = this;


        cardFloatingLayout = view.findViewById(R.id.cardFloatingLayout);
        schoolsListView = view.findViewById(R.id.schoolsListView);
        addSchoolsFloatingActionButton = view.findViewById(R.id.addSchoolsFloatingActionButton);
        hintTextView = view.findViewById(R.id.hintTextview);

        //fon movable textview
        hintTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        hintTextView.setSingleLine(true);
        hintTextView.setSelected(true);

        //it is null somtimes
        if (User.getCurrentUser() != null && !Admin.CheckAdmin(User.getCurrentUser().email)){
            addSchoolsFloatingActionButton.setVisibility(View.GONE);
        cardFloatingLayout.setVisibility(View.GONE);
    }

        //        for(int i=1;i<Initialisation.schools.size();i++){
//            schoolArrayList.add(Initialisation.schools.get(i));
//        }

        schoolsArrayAdapter=new SchoolsArrayAdapter(context,Initialisation.schoolArrayList);
        schoolsListView.setAdapter(schoolsArrayAdapter);
        schoolsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(getContext(), "itemClicked", Toast.LENGTH_SHORT).show();

                Intent allNotificationActivityIntent=new Intent(context,AllNotification.class);
                allNotificationActivityIntent.putExtra("SCHOOL",Initialisation.schoolArrayList.get(position));
                startActivity(allNotificationActivityIntent);
            }
        });

        schoolsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail()) && !Initialisation.schoolArrayList.get(position).equals("Notices"))
                    deleteWarning(Initialisation.schoolArrayList.get(position),position);
                return true;
            }
        });

        addSchoolsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    addNewSchool();

            }
        });

        return view;
    }

    private void deleteWarning(final String school, final int position) {

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("Do you really want to delete "+school+"?\n All data present in this school will be deleted and you can't restore them back.")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Delete")
                .setPositiveButton("Continue Anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSchoolFromFirebase(school);
                        Initialisation.schoolArrayList.remove(position);
                        schoolsArrayAdapter.notifyDataSetChanged();
                       // Upload.spinnerArrayAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Back", null)
                .show();

    }

    private void deleteSchoolFromFirebase(final String school) {

        FirebaseDatabase.getInstance().getReference(Initialisation.selectedCollege+"/Schools").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot finalDataSnapshot:dataSnapshot.getChildren()){
                    if(finalDataSnapshot.getValue().toString().equals(school)){
                        finalDataSnapshot.getRef().removeValue();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        deleteFromValue(school,"Category");
        deleteFromValue(school,"Requests");
        deleteFromValue(school,"PdfRequests");
        deleteFromValue(school,"PdfCategory");

    }

    private void deleteFromValue(final String school, String category) {

        FirebaseDatabase.getInstance().getReference(Initialisation.selectedCollege+"/"+category).child(school).removeValue();

    }

    private void addNewSchool() {

        LayoutInflater inflater=getLayoutInflater();
        final View view=inflater.inflate(R.layout.add_school_view,null,false);

        final EditText addSchoolEditText=view.findViewById(R.id.newSchoolEditText);
         AlertDialog builder=new AlertDialog.Builder(context)
                .setCancelable(false)
                 .setView(view)
                .setIcon(R.drawable.ic_launcher_background)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!addSchoolEditText.getText().toString().trim().equals("")&&addSchoolIntoFirebase(addSchoolEditText.getText().toString())) {
                            InputMethodManager inputMethodManager=(InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private boolean addSchoolIntoFirebase(String schoolName) {
        if(Admin.isCorrect(schoolName)){
            FirebaseDatabase.getInstance().getReference(Initialisation.selectedCollege+"/Schools").push().setValue(schoolName);
            return true;
        }
        else{
            return false;
        }

    }

    public static Schools newInstance() {
        
        Bundle args = new Bundle();
        
        Schools fragment = new Schools();
        fragment.setArguments(args);
        return fragment;
    }
}
