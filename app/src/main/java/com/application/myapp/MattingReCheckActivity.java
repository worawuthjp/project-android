package com.application.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class MattingReCheckActivity extends AppCompatActivity implements View.OnClickListener {
    private String UHFCODE;
    private String UnitCode;
    private String Barcode;
    private String UserBarcode;
    private Button saveBtn;
    private TextView sowUHF,sowUnit,sowBarcode,userBar;
    private String sowSementID,sowID,userID;
    private Module m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matting_re_check);

        m = new Module();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                UHFCODE= null;
                UnitCode=null;
                Barcode=null;
                UserBarcode=null;
            } else {
                UHFCODE= extras.getString("UHFCODE");
                UnitCode= extras.getString("UnitCode");
                Barcode= extras.getString("Barcode");
                UserBarcode= extras.getString("UserBarcode");
            }

        } else {
            UHFCODE= (String) savedInstanceState.getSerializable("UHFCODE");
            UnitCode= (String) savedInstanceState.getSerializable("UnitCode");
            Barcode= (String) savedInstanceState.getSerializable("Barcode");
            UserBarcode= (String) savedInstanceState.getSerializable("UserBarcode");
        }

        saveBtn = findViewById(R.id.MatingSaveBtn);
        sowUHF = findViewById(R.id.sowCodeTxt);
        sowUnit = findViewById(R.id.sowUnitTxt);
        sowBarcode = findViewById(R.id.barCodeTxt);
        userBar = findViewById(R.id.UserBarcodeTxt);

        sowUHF.setText(m.getUrl());
        sowUnit.setText(UnitCode);
        sowBarcode.setText(Barcode);
        userBar.setText(UserBarcode);

        RequestQueue queue = Volley.newRequestQueue(this);
        RequestQueue queue1 = Volley.newRequestQueue(this);
        RequestQueue queue2 = Volley.newRequestQueue(this);
        String url1 = m.getUrl()+"/getID/sowsemen/barcode/"+Barcode;
        String url2 = m.getUrl()+"/getID/sow/UHF/"+UHFCODE;
        String url3 = m.getUrl()+"/getID/user/barcode/"+UserBarcode;

        StringRequest jsonObj = new StringRequest(Request.Method.GET,url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != ""){
                            sowBarcode.setText(response+" + "+m.getUrl());
                        }else{
                            sowBarcode.setText("ไม่มีข้อมูล");
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"รับข้อมูลล้มเหลว",Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObj);
        StringRequest jsonObj1 = new StringRequest(Request.Method.GET,url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != ""){
                            sowUHF.setText(response);
                        }else{
                            sowUHF.setText("ไม่มีข้อมูล");
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"รับข้อมูลล้มเหลว",Toast.LENGTH_LONG).show();
            }
        });
        queue1.add(jsonObj1);

        StringRequest jsonObj2 = new StringRequest(Request.Method.GET,url3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != ""){
                            userBar.setText(response);
                        }else{
                            userBar.setText("ไม่มีข้อมูล");
                        }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"รับข้อมูลล้มเหลว",Toast.LENGTH_LONG).show();
            }
        });
        queue2.add(jsonObj2);


        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.MatingSaveBtn :
                sowSementID = sowBarcode.getText().toString();
                sowID = sowUHF.getText().toString();
                userID = userBar.getText().toString();

                //Request to server
                RequestQueue queue3 = Volley.newRequestQueue(this);
                String url = "https://pilowy.myminesite.com/add/sowmating";

                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
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
                        params.put("sowSemenID",sowSementID.trim());
                        params.put("sowID", sowID.trim());
                        params.put("userID", userID.trim());
                        return params;
                    }
                };
                queue3.add(jsonObjectRequest);

                Intent intent = new Intent(MattingReCheckActivity.this,MainMenu.class);
                startActivity(intent);
                finish();

                break;
        }
    }
}