package com.application.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
//import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences("SESSION", Context.MODE_PRIVATE);

        if(sp.contains("login")){
            if(sp.getBoolean("login",false)){
                drawerLayout = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);
                toolbar1 = (MaterialToolbar) findViewById(R.id.toolbar);

                //set toolbar
                setSupportActionBar(toolbar1);


                //navigation menu drawer
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.homeMenu:
                                break;
                            case R.id.logout:
                                SharedPreferences sp = getApplicationContext().getSharedPreferences("SESSION",MODE_APPEND);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("login");
                                editor.remove("userID");
                                editor.commit();

                                Toast.makeText(getApplicationContext(),"ออกจากระบบ",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.mattingMenu :
                                Intent intent1 = new Intent(MainActivity.this,SowMatingActivity.class);
                                startActivity(intent1);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar1,R.string.navi_open,R.string.navi_close);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();

            }

        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }

}