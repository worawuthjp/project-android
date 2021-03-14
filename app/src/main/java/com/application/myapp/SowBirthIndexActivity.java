package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sound.Sound;

public class SowBirthIndexActivity extends AppCompatActivity implements View.OnClickListener,View.OnKeyListener {
    private ScanUHF scanUHF;
    private Sound sound;
    private String sowID;
    private Button scanBtn,nextBtn;
    private EditText sowUHFEditText;
    private TextView showHeaderText,showInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_birth_index);

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
    }

    public void scanUHFFunc(){
        scanUHF.setPrtLen(0,4);
        final String result[] = scanUHF.getUHFRead();
        sowUHFEditText.setText(result[1]);

        Module mod = new Module();
        final String url = mod.getUrl()+"/get/sow/UHF?id="+result[1];

        if(sowUHFEditText.getText().toString() != ""){
            nextBtn.setVisibility(View.VISIBLE);
            showHeaderText.setVisibility(View.VISIBLE);

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
                                showInfoText.setText("UHF : " + result[0] + "\nเป็นของ\nSOWCODE : " + sowCode );
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
                sound.playSound(1);
                scanUHFFunc();
                break;
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