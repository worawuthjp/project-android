package com.application.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import java.util.HashMap;
import java.util.Map;

import sound.Sound;

public class SowBirthActivity extends AppCompatActivity implements View.OnClickListener {
    private Button addBtn;
    private EditText alive,died,mummy,total_weight,userEditText;
    private Module mod;
    private String sowID,userID,a,d,m,t;
    private BarcodeScanner bs;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_birth);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                sowID = "";
            } else {
                sowID = extras.getString("sowID");
            }

        }

        addBtn = (Button) findViewById(R.id.addSowBirthBtn);
        alive = (EditText) findViewById(R.id.alive);
        died = (EditText) findViewById(R.id.died);
        mummy = (EditText) findViewById(R.id.mummy);
        total_weight = (EditText) findViewById(R.id.total_weight);
        userEditText = this.findViewById(R.id.userEditText);
        mod = new Module();
        bs = new BarcodeScanner();

        userEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 293 || keyCode == 139 || keyCode == 280) && event.getAction() == KeyEvent.ACTION_DOWN){
                    try {
                        bs.scanCode(SowBirthActivity.this);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }

                if(userEditText.getText().toString() != ""){
                    getAPI();
                }

                return false;
            }
        });

        addBtn.setOnClickListener(this);

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
        a = alive.getText().toString();
        d= died.getText().toString();
        m = mummy.getText().toString();
        t = total_weight.getText().toString();
        switch (v.getId()){
            case R.id.addSowBirthBtn :
                String url = mod.getUrl();
                //Request to server
                getAPI();
                RequestQueue queue1 = Volley.newRequestQueue(this);

                JSONObject fromdata = new JSONObject();
                try {
                    fromdata.put("alive", a);
                    fromdata.put("sowID", sowID);
                    fromdata.put("died", d);
                    fromdata.put("mummy", m);
                    fromdata.put("total_weight", t);
                    fromdata.put("userID", userID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Request to server
                RequestQueue queue2 = Volley.newRequestQueue(this);

                String url1 = url+"/add/sowbirth" ;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1,fromdata,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String statusLogin = response.getString("status");
                                    if(statusLogin.equals("success")){
                                        Toast.makeText(getApplicationContext(),"บันทึกสำเร็จ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SowBirthActivity.this, SowBirthIndexActivity.class);
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(getApplicationContext(), "username หรือ password ไม่ถูกต้อง", Toast.LENGTH_LONG).show();
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
                queue1.add(jsonObjectRequest);
                break;
        }
    }

    public void getAPI(){
        String url = mod.getUrl()+"/get/employee/barcode?barcode="+userEditText.getText().toString();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(SowBirthActivity.this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET, url
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
                            userID = jsn.getString("empID");
                            //Toast.makeText(getApplicationContext(), userID, Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        userID = "";
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        BarcodeScanner bs = new BarcodeScanner();
        if(result != null) {
            if(result.getContents() != null) {
                String barcode = result.getContents();
                userEditText.setText(barcode);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getAPI();
                    }
                }).start();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}