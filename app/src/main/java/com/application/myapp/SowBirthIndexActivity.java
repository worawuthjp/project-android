package com.application.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sound.Sound;

public class SowBirthIndexActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {
    private static final int LOCATION_PERMISSION_REQUEST = 101;
    private static final int SELECT_DEVICE = 102;
    private ScanUHF scanUHF;
    private Sound sound;
    private String sowID,UHFID;
    private Button scanBtn,nextBtn , scanDevice;
    private EditText sowUHFEditText;
    private TextView showHeaderText,showInfoText;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_birth_index);

        setting = PreferenceManager.getDefaultSharedPreferences(this);
        scanUHF = new ScanUHF(SowBirthIndexActivity.this);
        sound = new Sound(SowBirthIndexActivity.this);
        scanBtn = (Button) findViewById(R.id.scanSowBitrh);
        sowUHFEditText = (EditText) findViewById(R.id.sowUHFEditText);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText);
        showInfoText = (TextView) findViewById(R.id.showInfoText);

        nextBtn.setVisibility(View.INVISIBLE);
        showHeaderText.setVisibility(View.INVISIBLE);

        scanBtn.setOnClickListener(this);
        sowUHFEditText.setOnKeyListener(this);
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

    public void getApi(){
        Module mod = new Module();
        final String url = mod.getUrl()+"/get/sow/UHF?id="+UHFID;
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
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
                            sowID = jsn.getString("sowID");
                            String sowCode = jsn.getString("sowCode");
                            showInfoText.setText("UHF : " + UHFID + "\nเป็นของ\nSOWCODE : " + sowCode );
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

    public void scanUHFFunc(){
        try {
            scanUHF.setPrtLen(0,4);
            final String result[] = scanUHF.getUHFRead();
            sowUHFEditText.setText(result[0]);
            UHFID = result[1];

            if(sowUHFEditText.getText().toString() != ""){
                nextBtn.setVisibility(View.VISIBLE);
                showHeaderText.setVisibility(View.VISIBLE);


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(SowBirthIndexActivity.this, SowBirthActivity.class);
                intent.putExtra("sowID",sowID);
                startActivity(intent);
                break;
            case R.id.scanSowBitrh :
                if(setting.getString("Setting_Scan_Device","").equals("UHF")){
                    sound.playSound(1);
                    scanUHFFunc();
                }
                else{
                    checkPermissions();
                }
                break;
        }

    }

    private void checkPermissions(){
            if (ContextCompat.checkSelfPermission(SowBirthIndexActivity.this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(SowBirthIndexActivity.this , new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },LOCATION_PERMISSION_REQUEST);
            }else {
                Intent intent = new Intent(SowBirthIndexActivity.this , BluetoothAndroidActivity.class);
                startActivityForResult(intent , SELECT_DEVICE);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(SowBirthIndexActivity.this , BluetoothAndroidActivity.class);
                startActivityForResult(intent , SELECT_DEVICE);
            }else{
                new AlertDialog.Builder(SowBirthIndexActivity.this)
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
                                SowBirthIndexActivity.this.finish();
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
                if(sowUHFEditText.getText().toString() != ""){
                    nextBtn.setVisibility(View.VISIBLE);
                    showHeaderText.setVisibility(View.VISIBLE);
                }
                getApi();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((keyCode == 293 || keyCode == 139 || keyCode == 280 )&& event.getAction() == KeyEvent.ACTION_DOWN){
            sound.playSound(1);
            scanUHFFunc();
            return true;
        }
        return false;
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