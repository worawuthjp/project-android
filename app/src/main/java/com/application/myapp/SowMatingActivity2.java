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
import com.application.myapp.barcode.BarcodeScanner;
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
    private String sowID,sowSemenID,barcode;
    private BarcodeScanner bs;

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
                    getAPI();
                }
                return false;
            }
        });

        scanBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(SowMatingActivity2.this, SowMatingActivity4.class);
                intent.putExtra("sowID",sowID);
                intent.putExtra("sowSemenID",sowSemenID);
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
        String url = mod.getUrl()+"/get/block/barcode?id="+blockIDEditText.getText().toString();

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
                            unitName = jsn.getString("unitName");
                            row = jsn.getString("row");
                            col = jsn.getString("col");
                            showInfoText.setText("โรงงเรือน : " + unitName + "\nRow : " + row + "\nCol :" + col );
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
                    getAPI();
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