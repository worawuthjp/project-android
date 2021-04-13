package com.application.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.application.myapp.UHF.ScanUHF;
import com.application.myapp.barcode.BarcodeScanner;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sound.Sound;

public class VaccineActivity2 extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private EditText QrVaccineEdit,commentTxt ;
    private Button saveBtn,scanBtn ;
    private TextView showHeaderText,showInfoText;
    private BarcodeScanner bs;
    private String vaccineID,empID,UHFID,EPC,sowID;
    private Module mod;
    private ScanUHF scanner;
    private Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine2);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mod = new Module();
        scanner = new ScanUHF(VaccineActivity2.this);
        sound = new Sound(VaccineActivity2.this);
        bs = new BarcodeScanner();
        QrVaccineEdit = findViewById(R.id.QrVaccineEditText2);
        saveBtn = findViewById(R.id.saveSowVaccineBtn2);
        scanBtn = findViewById(R.id.QrScanBtn_vaccine2);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText);
        showInfoText = (TextView) findViewById(R.id.showInfoText);
        showHeaderText.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.INVISIBLE);
        commentTxt = findViewById(R.id.commentEditText);

        QrVaccineEdit.setOnKeyListener(this);
        saveBtn.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        vaccineID = getIntent().getStringExtra("vaccineID");

        SharedPreferences sp = getApplicationContext().getSharedPreferences("SESSION", Context.MODE_PRIVATE);
        empID = sp.getString("userID","");
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

    public void ScanUHF(){

        try{
            scanner.setPrtLen(0, 4);
            String result[] = scanner.getUHFRead();
            UHFID = result[1];
            EPC = result[0];
            QrVaccineEdit.setText(EPC);

            if (QrVaccineEdit.getText().toString() != "") {
                saveBtn.setVisibility(View.VISIBLE);
                showHeaderText.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getAPI();
                    }
                }).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getAPI(){
        //url api
        Module mod = new Module();
        String url = mod.getUrl();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        String url1 = url+"/get/sow/UHF?id="+UHFID ;

        StringRequest jsonObj = new StringRequest(Request.Method.GET, url1
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        //Toast.makeText(getApplicationContext(), Integer.toString(jsonArray.length()), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            sowID = jsn.getString("sowID");
                            showInfoText.setText("UHF : " + EPC + "\nเป็นรหัสUHFของ\nเบอร์หมู : " + jsn.getString("sowCode") + "\nsowID : " + sowID);
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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((keyCode == 293 || keyCode == 139 || keyCode == 280) && (event.getAction() == KeyEvent.ACTION_DOWN)){
            try{
                sound.playSound(1);
                ScanUHF();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            return false;
        }

        if(QrVaccineEdit.getText().toString() != ""){
            saveBtn.setVisibility(View.VISIBLE);
            showHeaderText.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getAPI();
                }
            }).start();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.QrScanBtn :
                sound.playSound(1);
                ScanUHF();
                break;
            case R.id.saveSowVaccineBtn2 :
                //url
                String url = mod.getUrl();

                JSONObject fromdata = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {
                    fromdata.put("sowID",sowID);
                    fromdata.put("empID", empID);
                    fromdata.put("vaccineID", vaccineID);
                    fromdata.put("comment",commentTxt.getText().toString().trim() );
                    jsonArray.put(fromdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Request to server
                RequestQueue queue2 = Volley.newRequestQueue(this);

                String url1 = url+"/add/sowvaccine" ;

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url1,jsonArray,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                try {
                                    String statusLogin = response.getJSONObject(0).getString("status");
                                    if(statusLogin.equals("success")){
                                        Toast.makeText(getApplicationContext(),"บันทึกสำเร็จ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(VaccineActivity2.this, VaccineActivity.class);
                                        startActivity(intent);
                                        finish();

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
                queue2.add(jsonObjectRequest);
                break;
        }
    }
}