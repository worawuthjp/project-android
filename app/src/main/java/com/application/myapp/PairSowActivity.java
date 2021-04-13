package com.application.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.application.myapp.UHF.ScanUHF;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sound.Sound;

public class PairSowActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {
    private Button pairSowBtn;
    private EditText sowCodeEditText;
    private EditText sowUHFEditText;
    private ScanUHF scanUHF;
    private String UHFID;
    private Sound sound;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_sow);

        sound = new Sound(PairSowActivity.this);
        scanUHF = new ScanUHF(getApplicationContext());

        pairSowBtn = findViewById(R.id.pairSowBtn);
        sowCodeEditText = findViewById(R.id.sowCodeEditText);
        sowUHFEditText = findViewById(R.id.sowUHFEditText);

        sowUHFEditText.setOnKeyListener(this);
        pairSowBtn.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //navigation menu drawer
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
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.pairSowBtn:

                //pair uhf with sowCode on DB
                RequestQueue queue = Volley.newRequestQueue(this);
                Module mod = new Module();
                String url = mod.getUrl()+"/update/sow/pair?sowcode="+sowCodeEditText.getText().toString().trim()+"&uhf="+UHFID.trim();

                StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response);
                            if(obj.getString("status").equals("success")) {
                                Toast.makeText(getApplicationContext(), "ทำรายการสำเร็จ", Toast.LENGTH_LONG).show();
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
                }) ;
                queue.add(jsonObjectRequest);
                //Toast.makeText(getApplicationContext(),sowCodeEditText.getText().toString(),Toast.LENGTH_LONG);
                sowCodeEditText.setText("");
                sowUHFEditText.setText("");
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.sowUHFEditText :
                if ((keyCode == 293 || keyCode == 139 || keyCode == 280) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sound.playSound(1);
                    scanUHF.setPrtLen(0, 4);
                    String result[] = scanUHF.getUHFRead();
                    UHFID = result[1];
                    sowUHFEditText.setText(result[0]);
                    return true;
                }
                break;
        }
        return false;
    }

    public void pairApi(){
        //url api
        Module mod = new Module();
        String url = mod.getUrl();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        String url1 = url+"/update/sow/pair";

        StringRequest jsonObj = new StringRequest(Request.Method.PUT, url1
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uhf",sowUHFEditText.getText().toString());
                params.put("sowcode", sowCodeEditText.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;");
                return params;
            }
        };
        queue.add(jsonObj);
    }
}