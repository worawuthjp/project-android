package com.application.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateMatingActivity2 extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    private Spinner spin;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView showInfoText;
    private String sowID,selectID,sowMatingID;
    private Button saveBtn;
    private Module mod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mating2);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                this.sowID = "";
            } else {
                this.sowID = extras.getString("sowID");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getAPI();
                    }
                }).start();
            }
        }

        spin = findViewById(R.id.statusDropdown);
        showInfoText = findViewById(R.id.showInfoText);
        saveBtn = findViewById(R.id.saveStatusMating);
        mod = new Module();
        spin.setOnItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        saveBtn.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.homeMenu:
                        Intent intentHome = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intentHome);
                        finish();
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
                        finish();
                        break;
                    case R.id.mattingMenu :
                        Intent intent1 = new Intent(getApplicationContext(),SowMatingActivity3.class);
                        startActivity(intent1);
                        finish();
                        break;
                    case R.id.pairMenu :
                        Intent intentPair = new Intent(getApplicationContext(),PairSowActivity.class);
                        startActivity(intentPair);
                        finish();
                        break;
                    case R.id.birthMenu :
                        Intent intentBirth = new Intent(getApplicationContext(),SowBirthIndexActivity.class);
                        startActivity(intentBirth);
                        finish();
                        break;
                    case R.id.vaccineMenu1 :
                        Intent intentVaccine1 = new Intent(getApplicationContext(),VaccineUnitActivity1.class);
                        startActivity(intentVaccine1);
                        finish();
                        break;
                    case R.id.vaccineMenu2 :
                        Intent intentVaccine2 = new Intent(getApplicationContext(),VaccineActivity.class);
                        startActivity(intentVaccine2);
                        finish();
                        break;
                    case R.id.statusMatingMenu :
                        Intent intentMating = new Intent(getApplicationContext(),UpdateMatingActivity.class);
                        startActivity(intentMating);
                        finish();
                        break;
                    case R.id.settingMenu :
                        Intent intentSetting = new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(intentSetting);
                        finish();
                        break;
                    case R.id.blockMenu:
                        Intent intentBlockMenu = new Intent(getApplicationContext(),BlockActivity.class);
                        startActivity(intentBlockMenu);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void getAPI(){
        //URL
        Module mod = new Module();
        String url = mod.getUrl()+"/get/sowmating/status/lastest?id="+sowID;

        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET, url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            int status = Integer.parseInt(jsn.getString("status"));
                            sowMatingID = jsn.getString("sowMatingID");
                            if(status == 1){
                                showInfoText.setText("ผสมติด");
                                showInfoText.setTextColor(Color.parseColor("#0352E9"));
                            }else if(status == 2){
                                showInfoText.setText("ผสมไม่ติด");
                                showInfoText.setTextColor(Color.parseColor("#E14848"));
                            }
                            else if(status == 0){
                                showInfoText.setText("ยังไม่ทราบผล");
                                showInfoText.setTextColor(Color.parseColor("#9A9A9A"));
                            }

                        }
                    } else
                        showInfoText.setText("ไม่มีข้อมูล");
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObj);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        long res = id+1;
        selectID = Long.toString(res);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveStatusMating:
                String url = mod.getUrl();

                JSONObject fromdata = new JSONObject();
                try {
                    fromdata.put("id",sowMatingID);
                    fromdata.put("status", selectID);;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Request to server
                RequestQueue queue2 = Volley.newRequestQueue(this);

                String url1 = url+"/update/sowmating/status" ;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url1,fromdata,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String statusLogin = response.getString("status");
                                    if(statusLogin.equals("success")){
                                        Toast.makeText(getApplicationContext(),"บันทึกสำเร็จ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(UpdateMatingActivity2.this, UpdateMatingActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        Toast.makeText(getApplicationContext(), "ทำรายการไม่สำเร็จ", Toast.LENGTH_LONG).show();
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
                });
                queue2.add(jsonObjectRequest);
                break;
        }
    }
}