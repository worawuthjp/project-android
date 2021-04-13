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
import java.util.List;
import java.util.Map;

public class VaccineUnitActivity1 extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private BarcodeScanner bs;
    private Button unitScanBarBtn,nextBtn;
    private EditText unitQrEditText;
    private TextView showHeaderText,showInfoText;
    private String barcode;
    private List<String> sowID = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_unit1);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        sowID = new ArrayList<String>();
        bs = new BarcodeScanner();
        unitScanBarBtn = findViewById(R.id.QrUnitScanBtn);
        unitQrEditText = findViewById(R.id.QrUnitEditText);
        nextBtn = findViewById(R.id.nextToVaccine4);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText);
        showInfoText = (TextView) findViewById(R.id.showInfoText);
        showHeaderText.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);

        unitQrEditText.setOnKeyListener(this);
        unitScanBarBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

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

    public void getAPI() {
        //URL
        Module mod = new Module();
        String url = mod.getUrl()+"/get/sowblock?unitID="+unitQrEditText.getText().toString().trim();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET,url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                nextBtn.setVisibility(View.VISIBLE);
                showHeaderText.setVisibility(View.VISIBLE);
                sowID = new ArrayList<String>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() > 0) {
                        String data = "";
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            data += jsn.getString("sowCode")+"\n";
                            sowID.add(jsn.getString("sowID").toString());
                        }
                        showInfoText.setText("รหัสสุกร : \n"+data);
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

        if(unitQrEditText.getText().toString() != ""){
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
        if(result != null) {
            if(result.getContents() != null) {
                barcode = result.getContents();
                unitQrEditText.setText(barcode);
                if(unitQrEditText.getText().toString() != ""){
                    nextBtn.setVisibility(View.VISIBLE);
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
            case R.id.QrUnitScanBtn :
                bs.scanCode(VaccineUnitActivity1.this);
                break;
            case R.id.nextToVaccine4 :
                Intent intent = new Intent(VaccineUnitActivity1.this, VaccineUnitActivity4.class);
                intent.putExtra("sowID",(ArrayList<String>) sowID);
                startActivity(intent);
                finish();
                break;
        }
    }
}