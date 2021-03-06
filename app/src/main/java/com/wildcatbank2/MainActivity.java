package com.wildcatbank2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements Login.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, Deposit.OnFragmentInteractionListener, StartPage.OnFragmentInteractionListener, AccountTabs.OnFragmentInteractionListener, ButtonSignIn.OnFragmentInteractionListener {

    private static MainActivity instance;

    public static MainActivity getInstance() {return instance;}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instance = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.content_frame) != null){
            if (savedInstanceState != null){
                return;
            }

            StartPage startPageFragment = new StartPage();

            startPageFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.content_frame, startPageFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pay_bills) {
            // Handle the camera action
        } else if (id == R.id.nav_transfer) {

        } else if (id == R.id.nav_deposit) {
            launchDepositFragment();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_locations) {
            launchLocateActivity(findViewById(R.id.locationButton));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onAccountTabFragmentInteraction(Uri uri) {

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public void launchDialog(View view){
        FragmentManager fm = getFragmentManager();
        ContactUs dialog = new ContactUs();
        dialog.show(fm, "fragment_contact_us");
    }
    public void launchLocateActivity(View view){
        Fragment locateFragment = new LocateActivity();
        Bundle args = new Bundle();

        locateFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, locateFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void launchSignIn(View view){
        Fragment signInFragment = new Login();
        Bundle args = new Bundle();

        signInFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, signInFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void launchDepositFragment(){
        Fragment deposit = new Deposit();
        Bundle args = new Bundle();

        deposit.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, deposit);
        transaction.addToBackStack(null);
        transaction.commit();

    }


}
