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

public class SowMatingActivity4 extends AppCompatActivity implements View.OnKeyListener,View.OnClickListener {
    private BarcodeScanner bs;
    private EditText barcodeEditText;
    private Button scanBtn;
    private String UnitCode;
    private String UHFCODE;
    private String Barcode;
    private Button nextBtn;
    private TextView showHeaderText,showInfoText;
    private String name,userID;
    private String sowID,sowSemenID;
    private Module mod;

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scanBarcodeBtn :
                bs.scanCode(SowMatingActivity4.this);
                break;
            case R.id.nextBtn :
                Intent intent = new Intent(SowMatingActivity4.this, MattingReCheckActivity.class);
                //url
                String url = mod.getUrl()+"/add/sowmating";

                //Request to server
                RequestQueue queue3 = Volley.newRequestQueue(this);

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
                        params.put("sowSemenID",sowSemenID.trim());
                        params.put("sowID", sowID.trim());
                        params.put("userID", userID.trim());
                        return params;
                    }
                };
                queue3.add(jsonObjectRequest);

                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == 293 && event.getAction() == KeyEvent.ACTION_DOWN){
            bs.scanCode(SowMatingActivity4.this);
            return false;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        BarcodeScanner bs = new BarcodeScanner();
        if(result != null) {
            if(result.getContents() != null) {
                String barcode = result.getContents();
                barcodeEditText.setText(barcode);
                /*Intent intent = new Intent(SowMatingActivity4.this,MattingReCheckActivity.class);
                //Toast.makeText(getApplicationContext(),"บันทึกข้อมูลสำเร็จ",Toast.LENGTH_LONG);
                intent.putExtra("UserBarcode",barcodeEditText.getText().toString());
                intent.putExtra("UnitCode",UnitCode);
                intent.putExtra("UHFCODE",UHFCODE);
                intent.putExtra("Barcode",Barcode);
                startActivity(intent);
                finish();*/

                if(barcodeEditText.getText().toString() != ""){
                    nextBtn.setVisibility(View.VISIBLE);
                    showHeaderText.setVisibility(View.VISIBLE);

                    //URL
                    mod = new Module();
                    String url = mod.getUrl()+"/get/user/barcode?id="+barcode;

                    // SEND Request
                    RequestQueue queue = Volley.newRequestQueue(SowMatingActivity4.this);
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
                                        name = jsn.getString("fname")+" "+jsn.getString("lname");
                                        userID = jsn.getString("userID");
                                        showInfoText.setText("รหัสผู้ใช้ : " + userID + "\nชื่อ-นามสกุล : " + name );
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

            }else{
                Toast.makeText(this,"ไม่มีบาร์โค๊ด",Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}