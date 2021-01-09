package com.application.request;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class API {
    RequestQueue queue;
    String url;
    Context context;

    public API(Context context,String url){
        this.context = context;
        queue = Volley.newRequestQueue(this.context);
        this.url =url;
    }

    /*public void getData(String url,Hashmap data,final VolleyCallback mResultCallback){
        RequestQueue requstQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(mResultCallback != null){
                            mResultCallback.notifySuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(mResultCallback != null){
                            mResultCallback.notifyError(error);
                        }
                    }
                }
        ){
            //here I want to post data to sever
        };
        requstQueue.add(jsonobj);

    }*/
}
