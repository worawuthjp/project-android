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

public class SowMatingActivity2 extends AppCompatActivity implements View.OnClickListener{
    private EditText blockIDEditText;
    private Button scanBtn;
    private Button nextBtn;
    private Sound sound;
    private ScanUHF scanUhf;
    private TextView showHeaderText,showInfoText;
    private String unitName,row,col;
    private String sowID,sowSemenID;

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

        scanBtn = (Button) findViewById(R.id.scanBlockBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText1);
        showInfoText = (TextView) findViewById(R.id.showInfoText1);
        nextBtn.setVisibility(View.INVISIBLE);
        showHeaderText.setVisibility(View.INVISIBLE);

        scanUhf = new ScanUHF(SowMatingActivity2.this);

        blockIDEditText = (EditText) findViewById(R.id.blockIDEditText);
        sound = new Sound(SowMatingActivity2.this);

        blockIDEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 293 || keyCode == 139 || keyCode == 280 )&& event.getAction() == KeyEvent.ACTION_DOWN){
                    sound.playSound(1);
                    ScanUHF();
                    return true;
                }
                return false;
            }
        });

        scanBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
    }

    public void ScanUHF(){
        scanUhf.setPrtLen(0,4);
        String result[] = scanUhf.getUHFRead();
        blockIDEditText.setText(result[0]);

        Module mod = new Module();
        final String url = mod.getUrl()+"/get/block/RFID?id="+result[0];

        if(blockIDEditText.getText().toString() != ""){

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
                                unitName = jsn.getString("unitCode");
                                row = jsn.getString("row");
                                col = jsn.getString("col");
                                showInfoText.setText("UnitName : " + unitName + "\nROW : " + row + "\nCOL : " + col);
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

                        /*Intent intent = new Intent(SowMatingActivity2.this,SowMatingActivity3.class);
                        intent.putExtra("UnitCode",blockIDEditText.getText().toString());
                        intent.putExtra("UHFCODE",UHFCODE);
                        startActivity(intent);
                        finish();*/
        }
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
                ScanUHF();
                break;
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