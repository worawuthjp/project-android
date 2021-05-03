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
import com.application.myapp.barcode.BarcodeScanner;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VaccineUnitActivity4 extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ArrayList<String> sowID = new ArrayList<String>();
    private EditText QrVaccineEdit,commentTxt ;
    private Button saveBtn,scanBtn ;
    private TextView showHeaderText,showInfoText;
    private BarcodeScanner bs;
    private String barcode,empID,vaccineID;
    private Module mod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_unit4);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mod = new Module();
        bs = new BarcodeScanner();
        QrVaccineEdit = findViewById(R.id.QrVaccineEditText);
        saveBtn = findViewById(R.id.saveSowVaccineBtn);
        scanBtn = findViewById(R.id.QrScanBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText2);
        showInfoText = (TextView) findViewById(R.id.showInfoText);
        showHeaderText.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.INVISIBLE);
        commentTxt = findViewById(R.id.commentEditText);

        QrVaccineEdit.setOnKeyListener(this);
        saveBtn.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        sowID = new ArrayList<String>();
        sowID = getIntent().getStringArrayListExtra("sowID");

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
                }
                return true;
            }
        });
    }

    public void getAPI() {
        //URL
        Module mod = new Module();
        String url = mod.getUrl()+"/get/vaccine/Barcode?id="+QrVaccineEdit.getText().toString().trim();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET,url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                saveBtn.setVisibility(View.VISIBLE);
                showHeaderText.setVisibility(View.VISIBLE);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        String data = "";
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            vaccineID = jsn.getString("vaccineID");
                            showInfoText.setText("วัคซีน : "+jsn.getString("vaccineName") + "\nID : "+jsn.getString("vaccineID"));
                        }

                    } else{
                        showInfoText.setText("ไม่มีข้อมูล");
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
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((keyCode == 293 || keyCode == 139 || keyCode == 280) && (event.getAction() == KeyEvent.ACTION_DOWN)){
            try{
                bs.scanCode(this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null) {
            if(result.getContents() != null) {
                barcode = result.getContents();
                QrVaccineEdit.setText(barcode);
                if(QrVaccineEdit.getText().toString() != ""){
                    saveBtn.setVisibility(View.VISIBLE);
                    showHeaderText.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getAPI();
                        }
                    }).start();
                    //getAPI();
                }

            }else{
                Toast.makeText(this,"ไม่มีบาร์โค๊ด",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.QrScanBtn :
                bs.scanCode(VaccineUnitActivity4.this);
                break;
            case R.id.saveSowVaccineBtn :
                String url = mod.getUrl();
                JSONArray formbody = new JSONArray();
                for (String s : sowID) {
                    JSONObject fromdata = new JSONObject();
                    try {
                        fromdata.put("sowID", s);
                        fromdata.put("empID", empID);
                        fromdata.put("vaccineID", vaccineID);
                        fromdata.put("comment", commentTxt.getText().toString().trim());
                        formbody.put(fromdata);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Request to server
                RequestQueue queue2 = Volley.newRequestQueue(this);

                String url1 = url+"/add/sowvaccine" ;

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, url1,formbody,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    String statusAdd = response.getJSONObject(0).getString("status");
                                    if(statusAdd.equals("success")){
                                        Toast.makeText(getApplicationContext(),"บันทึกสำเร็จ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(VaccineUnitActivity4.this, VaccineUnitActivity1.class);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        //Toast.makeText(getApplicationContext(), "มีบางอย่างผิดพลาด", Toast.LENGTH_LONG).show();
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