package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
                if(keyCode == 293 && event.getAction() == KeyEvent.ACTION_DOWN){

                    try {
                        bs.scanCode(SowBirthActivity.this);
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                    return false;
                }

                return false;
            }
        });

        addBtn.setOnClickListener(this);

    }

    public void createAPI(){

    }

    @Override
    public void onClick(View v) {
        a = alive.getText().toString();
        d= died.getText().toString();
        m = mummy.getText().toString();
        t = total_weight.getText().toString();
        switch (v.getId()){
            case R.id.addSowBirthBtn :
                String url = mod.getUrl()+"/add/sowbirth";
                //Request to server
                RequestQueue queue1 = Volley.newRequestQueue(this);

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
                        params.put("alive", a);
                        params.put("sowID", sowID);
                        params.put("died", d);
                        params.put("mummy", m);
                        params.put("total_weight", t);
                        params.put("userID", userID);
                        return params;
                    }
                };
                queue1.add(jsonObjectRequest);

                Intent intent = new Intent(SowBirthActivity.this,DamActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        BarcodeScanner bs = new BarcodeScanner();
        if(result != null) {
            if(result.getContents() != null) {
                String barcode = result.getContents();
                userEditText.setText(barcode);
                if(userEditText.getText().toString() != ""){
                    String url = mod.getUrl()+"/get/user/barcode?id="+userEditText.getText().toString();
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
                                        userID = jsn.getString("userID");
                                        Toast.makeText(getApplicationContext(), userID, Toast.LENGTH_LONG).show();
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

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}