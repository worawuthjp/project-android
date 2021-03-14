package com.application.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.application.module.Module;
import com.application.myapp.UHF.ScanUHF;

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
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.pairSowBtn:

                //pair uhf with sowCode on DB
                RequestQueue queue = Volley.newRequestQueue(this);
                Module mod = new Module();
                String url = mod.getUrl()+"/update/sow/pair";

                StringRequest jsonObjectRequest = new StringRequest(Request.Method.PUT, url,
                        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Response is: "+response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "ส่งข้อมูลไม่สำเร็จ", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("sowcode", sowCodeEditText.getText().toString().trim());
                        params.put("uhf", UHFID.trim());
                        return params;
                    }
                };
                queue.add(jsonObjectRequest);
                Toast.makeText(getApplicationContext(),sowCodeEditText.getText().toString(),Toast.LENGTH_LONG);


                // go to mainmenu
                intent = new Intent(PairSowActivity.this,MainMenu.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"บันทึกข้อมูลสำเร็จ",Toast.LENGTH_LONG).show();
                finish();
                break;
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
}