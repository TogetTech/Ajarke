package com.togettech.ajarke;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.togettech.ajarke.ui.agence.AgenceFragment;
import com.togettech.ajarke.ui.explore.ExploreActivity;
import com.togettech.ajarke.ui.agence.AgenceListActivity;
import com.togettech.ajarke.ui.launch.LaunchActivity;
import com.togettech.ajarke.ui.reservation.ReservationFragment;
import com.togettech.ajarke.ui.weather.WeatherFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;


    private TabLayout tabLayout;

    
    public CircleImageView nav_image_profil;
    public TextView nav_username;
    public TextView nav_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ImageView menuIcon = (ImageView) findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        //******************************************************************************************
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //******************************************************************************************
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //******************************************************************************************

        View headerView = navigationView.getHeaderView(0);

        nav_image_profil = headerView.findViewById(R.id.nav_image_profil);
        nav_username = headerView.findViewById(R.id.nav_username);
        nav_email = headerView.findViewById(R.id.nav_email);

        Glide.with(this).load(user.getPhotoUrl()).into(nav_image_profil);
        nav_username.setText(user.getDisplayName());
        nav_email.setText(user.getEmail());


        LinearLayout nav_explore = headerView.findViewById(R.id.nav_explore);
        nav_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.fade_out);
            }
        });
        LinearLayout nav_agences = headerView.findViewById(R.id.nav_agences);
        nav_agences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AgenceListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.fade_out);
            }
        });
        LinearLayout nav_courrier = headerView.findViewById(R.id.nav_courrier);
        LinearLayout nav_profil = headerView.findViewById(R.id.nav_profil);
        LinearLayout nav_exit = headerView.findViewById(R.id.nav_exit);
        nav_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutDialog();
            }
        });



        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //******************************************************************************************

    }

    public void openLogoutDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Voulez-vous déconnecter votre compte ?");
        alert.setPositiveButton("Oui",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        logout();
                    }
                });
        alert.setNegativeButton("Non",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_bus,
                R.drawable.ic_time,
                R.drawable.ic_cloud
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AgenceFragment(), "ONE");
        adapter.addFrag(new ReservationFragment(), "TWO");
        adapter.addFrag(new WeatherFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            // return null to display only the icon
            return null;
        }
    }

    private void logout() {
        Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
        startActivity(intent);
        finish();
    }

    //**********************************************************************************************


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    //**********************************************************************************************

    //**********************************************************************************************

}
