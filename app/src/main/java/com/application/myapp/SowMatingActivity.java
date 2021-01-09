package com.application.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class SowMatingActivity extends AppCompatActivity implements View.OnClickListener {
    private ScanUHF scanner;
    private Button nextBtn;
    private Button scanBtn,backBtn;
    private EditText sowIDEditText;
    private TextView showHeaderText;
    private TextView showInfoText;
    private Sound sound;
    private String sowID,sowCode,sowSemenID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sow_mating);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                this.sowSemenID = "";
            } else {
                this.sowSemenID = extras.getString("sowSemenID");
            }

        }

        scanBtn = (Button) findViewById(R.id.scanBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        backBtn = (Button) findViewById(R.id.backtoSemenBtn);
        showHeaderText = (TextView) findViewById(R.id.showHeaderText);
        showInfoText = (TextView) findViewById(R.id.showInfoText);
        showHeaderText.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        sowIDEditText = (EditText) findViewById(R.id.sowIDEditText);
        sound = new Sound(SowMatingActivity.this);
        scanner = new ScanUHF(getApplicationContext());

        sowIDEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 293 || keyCode == 139 || keyCode == 280 )&& event.getAction() == KeyEvent.ACTION_DOWN){
                    sound.playSound(1);
                    ScanUHF();

                    //sowIDEditText.setText(sowIDEditText.getText().toString()+"E");
                    /*Intent intent = new Intent(SowMatingActivity.this,SowMatingActivity2.class);
                    intent.putExtra("UHFCODE",sowIDEditText.getText().toString());
                    startActivity(intent);
                    finish();*/
                    return true;
                }
                return false;
            }
        });
        scanBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    public void ScanUHF(){
        scanner.setPrtLen(0, 4);
        final String UHFID = scanner.getUHFRead()[0];
        sowIDEditText.setText(UHFID);

        //url api
        Module mod = new Module();
        String url = mod.getUrl();

        if (sowIDEditText.getText().toString() != "") {
            nextBtn.setVisibility(View.VISIBLE);
            showHeaderText.setVisibility(View.VISIBLE);

            // SEND Request
            RequestQueue queue = Volley.newRequestQueue(this);
            String url1 = url+"/get/sow/UHF?id="+UHFID ;

            StringRequest jsonObj = new StringRequest(Request.Method.GET, url1
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
                                    sowCode = jsn.getString("sowCode");
                                    sowID = jsn.getString("sowID");
                                    showInfoText.setText("UHF : " + UHFID + "\nเป็นรหัสUHFของ\nเบอร์หมู : " + sowCode + "\nsowID : " + sowID);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(SowMatingActivity.this, SowMatingActivity2.class);
                intent.putExtra("sowID",sowID);
                intent.putExtra("sowSemenID",sowSemenID);
                startActivity(intent);
                break;

            case R.id.scanBtn :
                sound.playSound(1);
                ScanUHF();
                break;
            case R.id.backtoSemenBtn :
                Intent intent1 = new Intent(SowMatingActivity.this, SowMatingActivity3.class);
                startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}