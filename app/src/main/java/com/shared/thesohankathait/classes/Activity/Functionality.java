package com.shared.thesohankathait.classes.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shared.thesohankathait.classes.Utill.Initialisation;
import com.shared.thesohankathait.notices.R;
import com.shared.thesohankathait.classes.Adapter.ViewPagerAdapter;
import com.shared.thesohankathait.classes.Utill.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Functionality extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

   private TabLayout tabLayout;
   private ViewPager viewPager;

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if
                        (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }

                else {

                    showAlertMessage();

                }

                return;
            }
        }
    }

    private void showAlertMessage() {

        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please allow the permission to use this app")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Permission")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askForPermissions();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionality);

        //initialising app mob
        MobileAds.initialize(this,getString(R.string.app_admob_id));

        //subscribing to tokens
        String finalToken=Initialisation.selectedCollege+"Tokens";
        FirebaseMessaging.getInstance().subscribeToTopic(finalToken);

        if(Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            String finalAdminToken=Initialisation.selectedCollege+"AdminToken";
            FirebaseMessaging.getInstance().subscribeToTopic(finalAdminToken);
        }
        //starting services for admin


//        if(Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
//            Intent serviceIntent=new Intent(Functionality.this,RequestService.class);
//            startService(serviceIntent);
//        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

//        getActionBar().setTitle("Umang");
        getSupportActionBar().setTitle("SharedNotices");  // provide compatibility to all the versions
  //      getActionBar().setIcon(R.drawable.ic_launcher);
//        getSupportActionBar().setLogo(R.drawable.ic_launcher);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        tabLayout=findViewById(R.id.tabLayout);

        viewPager=findViewById(R.id.subscriptionViewPager);

        //First we will set the adapter to the viewPager
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);




        //And then we set the viewPager on the tabLayout
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setBackgroundColor(this.getResources().getColor(R.color.com_facebook_blue));
        tabLayout.setSelectedTabIndicatorColor(this.getResources().getColor(R.color.white));

        //This below one is used to set the custom textview on tabLayout items.
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions

            if(i==3){
                tabLayout.getTabAt(i).setIcon(R.drawable.ic_save_black_24dp);

                continue;

            }


        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                  Toast.makeText(Functionality.this, ""+position, Toast.LENGTH_SHORT).show();
                if(position==3){
                    tabLayout.getTabAt(3).setIcon(R.drawable.ic_save_black_24dp_whiter);
                }
                else{
                    tabLayout.getTabAt(3).setIcon(R.drawable.ic_save_black_24dp);
                }
                switch (position){
                    case 0:
                        navigationView.setCheckedItem(R.id.schools);
                        break;
                    case 1:
                        navigationView.setCheckedItem(R.id.srtcNotification);
                        break;
                    case 2:
                        navigationView.setCheckedItem(R.id.upload);
                        break;
                    case 3:
                        navigationView.setCheckedItem(R.id.saved);

                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        if(!hasAllPermissions())
            askForPermissions();

    }

    private void askForPermissions()
    {
        String permissions[]={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NETWORK_STATE};
        if(Build.VERSION.SDK_INT>=23)
            requestPermissions(permissions,1);
    }


    private boolean hasAllPermissions()
    {
        if(Build.VERSION.SDK_INT>=23)
            return ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    &&ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                    &&ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_NETWORK_STATE)==PackageManager.PERMISSION_GRANTED
                    &&ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;

        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(getSupportFragmentManager().getBackStackEntryCount()!=0) {
                getSupportFragmentManager().popBackStack();
        }

        else {
            exitAlert();
          //  super.onBackPressed();
        }
    }

    private void exitAlert() {
        AlertDialog builder=new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle(getString(R.string.exit))
                .setCancelable(false)
                .setMessage(getString(R.string.do_do_really_want_to_exit))
                .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mydownloads, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mydownloads) {
            //Toast.makeText(this, "pass", Toast.LENGTH_SHORT).show();
            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/Download/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");
            if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
            {
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Please download a file manager first!", Toast.LENGTH_SHORT).show();
                // if you reach this place, it means there is no any file
                // explorer app installed on your device
            }
            return true;
        }

        else if(id==R.id.search){
            Intent intent=new Intent(Functionality.this,Search.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.srtcNotification) {
            // Handle the srtcNotification
            tabLayout.setScrollPosition(1,0f,false);
            viewPager.setCurrentItem(1,true);
        } else if (id == R.id.schools) {
            tabLayout.setScrollPosition(0,0f,false);
            viewPager.setCurrentItem(0,true);

        } else if (id == R.id.upload) {
            tabLayout.setScrollPosition(2,0f,false);
            viewPager.setCurrentItem(2,true);
        } else if (id == R.id.saved) {
            tabLayout.setScrollPosition(3,0f,false);
            viewPager.setCurrentItem(3,true);

        }  else if (id == R.id.share) {
            shareMyApp(getString(R.string.app_link));

        } else if (id == R.id.feedback) {
            sendFeedbackViaMail();

        } else if (id == R.id.aboutUmang) {
            sendIntent("AboutUs");

        } else if (id == R.id.query) {
            sendIntent("Query");

        }  else if (id == R.id.request) {
            if(Admin.CheckAdmin(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                sendIntent("Request");
                }
            else{
                Toast.makeText(this, "You are not an Admin", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.privacyPolicy) {
            sendIntent("Policy");

        } else if (id == R.id.logout) {
            showAlertDialog();
        }
        else if(id==R.id.selectCollege){
            sendIntent("SelectCollege");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareMyApp(String appLink) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, appLink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void sendFeedbackViaMail() {

        String [] address = new String[Initialisation.adminList.size()];
        for(int i=0;i<Initialisation.adminList.size();i++){
            address[i]=Initialisation.adminList.get(i);

        }
        //If you want to ensure that your intent is handled only by an email app (and not other text messaging or social apps),
// then use the ACTION_SENDTO action and include the "mailto:" data scheme. For example:
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL,address);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void sendIntent(String fragmentName) {
        Intent requestIntent = new Intent(Functionality.this, Features.class);
        requestIntent.putExtra("FRAGMENT_NAME",fragmentName);
        startActivity(requestIntent);

    }

    private void showAlertDialog() {
        AlertDialog builder=new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Logout")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Login.googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                           //code later
                            }
                        });
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor   editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        LoginManager.getInstance().logOut();

                        finish();//finishing this activity and moving to next activity i.e. login activity

                        //again asking for use to login
                        Intent intent=new Intent(Functionality.this,Login.class);
                        startActivity(intent);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }
}
