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
import android.widget.TextView;
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

public class SowMatingActivity4 extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {
    private BarcodeScanner bs;
    private EditText barcodeEditText;
    private Button scanBtn;
    private Button nextBtn;
    private TextView showHeaderText,showInfoText;
    private String name,userID;
    private String sowID,sowSemenID,barcode;
    private Module mod;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_mating4);

        mod = new Module();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                this.sowID = "";
                this.sowSemenID = "";
            } else {
                this.sowID = extras.getString("sowID");
                this.sowSemenID = extras.getString("sowSemenID");
            }

        }

        barcodeEditText = (EditText) findViewById(R.id.barcodeUserEditText);
        final Sound sound = new Sound(getApplicationContext());
        bs = new BarcodeScanner();

        barcodeEditText.setOnKeyListener(this);
        scanBtn = (Button) findViewById(R.id.scanBarCodeBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText);
        showInfoText = (TextView) findViewById(R.id.showInfoText);
        nextBtn.setVisibility(View.INVISIBLE);
        showHeaderText.setVisibility(View.INVISIBLE);

        scanBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

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

    public void getAPI(){
        //URL
        mod = new Module();
        String url = mod.getUrl()+"/get/employee/barcode?barcode="+barcodeEditText.getText().toString().trim();

        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(SowMatingActivity4.this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET, url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        //Toast.makeText(getApplicationContext(), Integer.toString(jsonArray.length()), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            name = jsn.getString("fname")+" "+jsn.getString("lname");
                            userID = jsn.getString("empID");
                            showInfoText.setText("รหัสผู้ใช้ : " + userID + "\nชื่อ-นามสกุล : " + name +"\n\n");
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanBarCodeBtn :
                bs.scanCode(SowMatingActivity4.this);
                break;
            case R.id.nextBtn :
                //url
                String url = mod.getUrl();

                JSONObject fromdata = new JSONObject();
                try {
                    fromdata.put("sowSemenID",sowSemenID);
                    fromdata.put("sowID", sowID);
                    fromdata.put("userID", userID);;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Request to server
                RequestQueue queue2 = Volley.newRequestQueue(this);

                String url1 = url+"/add/sowmating" ;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url1,fromdata,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    String statusLogin = response.getString("status");
                                    if(statusLogin.equals("success")){
                                        Toast.makeText(getApplicationContext(),"บันทึกสำเร็จ",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SowMatingActivity4.this, SowMatingActivity.class);
                                        intent.putExtra("sowSemenID",sowSemenID);
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
                }) /*{
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username",usernameText.getText().toString());
                params.put("password", passwordText.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded;");
                return params;
            }
        }*/;
                queue2.add(jsonObjectRequest);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((keyCode == 293 || keyCode == 139 || keyCode == 280) && event.getAction() == KeyEvent.ACTION_DOWN){
            try{
                bs.scanCode(SowMatingActivity4.this);
            }catch (Exception e){
                e.printStackTrace();
            }
            bs.scanCode(SowMatingActivity4.this);
            return false;
        }

        if(barcodeEditText.getText().toString() != ""){
            nextBtn.setVisibility(View.VISIBLE);
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
        BarcodeScanner bs = new BarcodeScanner();
        if(result != null) {
            if(result.getContents() != null) {
                barcode = result.getContents();
                barcodeEditText.setText(barcode);

                if(barcodeEditText.getText().toString() != ""){
                    nextBtn.setVisibility(View.VISIBLE);
                    showHeaderText.setVisibility(View.VISIBLE);
                    getAPI();
                }

            }else{
                Toast.makeText(this,"ไม่มีบาร์โค๊ด",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}