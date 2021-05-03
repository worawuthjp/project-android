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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.application.myapp.UHF.ScanUHF;
import com.application.myapp.barcode.BarcodeScanner;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sound.Sound;

public class SowMatingActivity2 extends AppCompatActivity implements View.OnClickListener{
    private EditText blockIDEditText;
    private Button scanBtn;
    private Button nextBtn;
    private TextView showHeaderText,showInfoText;
    private String unitName,row,col;
    private String sowID,sowSemenID,barcode,unit_block_id;
    private BarcodeScanner bs;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_mating2);
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

        bs = new BarcodeScanner();
        scanBtn = (Button) findViewById(R.id.scanBlockBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText1);
        showInfoText = (TextView) findViewById(R.id.showInfoText1);
        nextBtn.setVisibility(View.INVISIBLE);
        showHeaderText.setVisibility(View.INVISIBLE);

        blockIDEditText = (EditText) findViewById(R.id.blockIDEditText);

        blockIDEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 293 || keyCode == 139 || keyCode == 280 )&& event.getAction() == KeyEvent.ACTION_DOWN){
                    try {
                        bs.scanCode(SowMatingActivity2.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }

                if(blockIDEditText.getText().toString() != ""){
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
        });

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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(SowMatingActivity2.this, SowMatingActivity4.class);
                intent.putExtra("sowID",sowID);
                intent.putExtra("sowSemenID",sowSemenID);
                intent.putExtra("unit_block_id",unit_block_id);
                startActivity(intent);
                break;

            case R.id.scanBlockBtn :
                bs.scanCode(this);
                break;
        }
    }

    public void getAPI(){
        //URL
        Module mod = new Module();
        String url = mod.getUrl()+"/get/block/barcode?id="+blockIDEditText.getText().toString().trim();

        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObj = new StringRequest(Request.Method.GET, url
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    String alpha[] = {"A","B","C","D","E","F","G"};
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsn = jsonArray.getJSONObject(i);
                            unit_block_id = jsn.getString("unit_block_id");
                            unitName = jsn.getString("unitName");
                            row = jsn.getString("row");
                            col = jsn.getString("col");

                            showInfoText.setText("โรงงเรือน : " + unitName + "\n เบอร์ซอง : "+alpha[Integer.parseInt(row)-1]+col);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null) {
            if(result.getContents() != null) {
                barcode = result.getContents();
                blockIDEditText.setText(barcode);

                if(blockIDEditText.getText().toString() != ""){
                    nextBtn.setVisibility(View.VISIBLE);
                    showHeaderText.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getAPI();
                        }
                    }).start();
                }

            }else{
                Toast.makeText(this,"ไม่มีบาร์โค๊ด",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}