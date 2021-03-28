package com.application.myapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
//import androidx.appcompat.widget.Toolbar;
import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar1;

    TextView fullnameText,positionText,usernameText,unitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        fullnameText = findViewById(R.id.fullnameTxt);
        positionText = findViewById(R.id.positionTxt);
        usernameText = findViewById(R.id.usernameTxt);
        unitText = findViewById(R.id.unitTxt);

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
                                Intent intentHome = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intentHome);

                                break;
                            case R.id.logout:
                                SharedPreferences sp = getApplicationContext().getSharedPreferences("SESSION",MODE_APPEND);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.remove("login");
                                editor.remove("userID");
                                editor.commit();

                                Toast.makeText(getApplicationContext(),"ออกจากระบบ",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);

                                break;
                            case R.id.mattingMenu :
                                Intent intent1 = new Intent(getApplicationContext(),SowMatingActivity3.class);
                                startActivity(intent1);

                                break;
                            case R.id.pairMenu :
                                Intent intentPair = new Intent(getApplicationContext(),PairSowActivity.class);
                                startActivity(intentPair);

                                break;
                            case R.id.birthMenu :
                                Intent intentBirth = new Intent(getApplicationContext(),SowBirthIndexActivity.class);
                                startActivity(intentBirth);

                                break;
                            case R.id.vaccineMenu1 :
                                Intent intentVaccine1 = new Intent(getApplicationContext(),VaccineUnitActivity1.class);
                                startActivity(intentVaccine1);

                                break;
                            case R.id.vaccineMenu2 :
                                Intent intentVaccine2 = new Intent(getApplicationContext(),VaccineActivity.class);
                                startActivity(intentVaccine2);

                                break;
                        }
                        return true;
                    }
                });
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar1,R.string.navi_open,R.string.navi_close);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();

            }

            getApi();
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void getApi(){
        //URL
        Module mod = new Module();
        String url = mod.getUrl()+"/get/user?id="+sp.getString("userID","").trim();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET,url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            fullnameText.setText(jsn.getString("fname").trim()+" "+jsn.getString("lname").trim());
                            positionText.setText(jsn.getString("pos_name").trim());
                            usernameText.setText(jsn.getString("username").trim());
                            unitText.setText(jsn.getString("unitName"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                return params;
            }

        };

        queue.add(jsonObj);
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