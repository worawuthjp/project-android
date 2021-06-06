package com.application.myapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int SELECT_DEVICE = 102;
    private static final int LOCATION_PERMISSION_REQUEST = 101;
    private Button pairSowBtn;
    private Button scanDevice;
    private EditText sowCodeEditText;
    private EditText sowUHFEditText;
    private ScanUHF scanUHF;
    private String UHFID,EPC;
    private Sound sound;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_sow);

        sound = new Sound(PairSowActivity.this);
        scanUHF = new ScanUHF(getApplicationContext());
        setting = PreferenceManager.getDefaultSharedPreferences(this);

        pairSowBtn = findViewById(R.id.pairSowBtn);
        scanDevice = findViewById(R.id.scanDevice);
        sowCodeEditText = findViewById(R.id.sowCodeEditText);
        sowUHFEditText = findViewById(R.id.sowUHFEditText);

        sowUHFEditText.setOnKeyListener(this);
        pairSowBtn.setOnClickListener(this);
        scanDevice.setOnClickListener(this);

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
                break;

            case R.id.scanDevice:
                if(setting.getString("Setting_Scan_Device","").equals("NFC")){
                    checkPermissions();
                }else{
                    try {
                        scanUHFFunc();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

//                String text = intent_searchDevices.getStringExtra("message");
                break;
        }
    }

    public void scanUHFFunc(){
        sound.playSound(1);
        scanUHF.setPrtLen(0, 4);
        String[] result = scanUHF.getUHFRead();
        UHFID = result[1];
        EPC = result[0];
        sowUHFEditText.setText(result[0]);
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.sowUHFEditText :
                if ((keyCode == 293 || keyCode == 139 || keyCode == 280) && event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        scanUHFFunc();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
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

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(PairSowActivity.this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(PairSowActivity.this , new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            },LOCATION_PERMISSION_REQUEST);
        }else {
            Intent intent = new Intent(PairSowActivity.this , BluetoothAndroidActivity.class);
            startActivityForResult(intent , SELECT_DEVICE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(PairSowActivity.this , BluetoothAndroidActivity.class);
                startActivityForResult(intent , SELECT_DEVICE);
            }else{
                new AlertDialog.Builder(PairSowActivity.this)
                        .setCancelable(false)
                        .setMessage("Location permission is required \n Please grant")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermissions();
                            }
                        })
                        .setPositiveButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PairSowActivity.this.finish();
                            }
                        }).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String text = data.getStringExtra("message");
            Log.i("ScanDevice", "Message : " + text);
            sowUHFEditText.setText(text);
            UHFID = text;
        }
    }
}