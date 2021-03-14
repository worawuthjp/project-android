package com.application.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText)findViewById(R.id.userIDEditText);
        passwordText = (EditText)findViewById(R.id.passwordEditText);
        final Button loginButton = (Button) findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
    }

    public void checkLogin(){
        //url api
        Module mod = new Module();
        String url = mod.getUrl();
        // SEND Request
        RequestQueue queue = Volley.newRequestQueue(this);
        String url1 = url+"/check/user" ;

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String statusLogin = "";
                        JSONArray jsonArray = null;
                        try {
                            JSONObject jsn = new JSONObject(response);
                            statusLogin = jsn.getString("status");
                            if(statusLogin.equals("success")){
                                Toast.makeText(getApplicationContext(), "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_LONG).show();
                                SharedPreferences sp = getApplicationContext().getSharedPreferences("SESSION",MODE_APPEND);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username",usernameText.getText().toString());
                                editor.putBoolean("login",true);
                                editor.commit();


                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "username หรือ password ไม่ถูกต้อง", Toast.LENGTH_LONG).show();
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
                params.put("username",usernameText.getText().toString().trim());
                params.put("password", passwordText.getText().toString().trim());
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }
}