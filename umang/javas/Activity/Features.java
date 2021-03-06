package com.shared.thesohankathait.notices.javas.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.shared.thesohankathait.notices.R;
import com.shared.thesohankathait.notices.javas.Fragment.PdfNotice;
import com.shared.thesohankathait.notices.javas.Fragment.Policy;
import com.shared.thesohankathait.notices.javas.Fragment.Query;
import com.shared.thesohankathait.notices.javas.Fragment.Request;

public class Features extends AppCompatActivity {

    private FrameLayout featureFramlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        featureFramlayout=findViewById(R.id.featureFrameLayout);
        Intent intent=getIntent();
        String fragmentName=intent.getStringExtra("FRAGMENT_NAME");
        switch (fragmentName){
            case "Request":
                addDifferentFragment(Request.newInstance());
                break;
            case "Query":
                addDifferentFragment(Query.newInstance());
                break;
            case "Policy":
                addDifferentFragment(Policy.newInstance());
                break;
            case "PdfNotice":
                String schoolName=getIntent().getStringExtra("SCHOOL_NAME");
                addDifferentFragment(PdfNotice.newInstance(schoolName));
                break;
        }

    }

    private void addDifferentFragment(Fragment replacableFragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.featureFrameLayout,replacableFragment).commit();

    }
}
