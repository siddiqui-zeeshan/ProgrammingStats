package com.zeeshan.programmingstats.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zeeshan.programmingstats.loginpage.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ApiCall {

    public static void volleyRequestPOST(final Activity activity, final String url
            , final Map<String, String> parameters, final ServerCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("POST.response", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            callback.onSuccess(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Error.Response", error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return parameters;

            }
        };
        queue.add(postRequest);
    }

    public static void volleyRequestGET(final Activity activity, final String url, final ServerCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("GET.response", response.toString());
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error.Response", error.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Wrong codeforces handle");
                builder.setMessage("You have entered a handle which doesn't exist. Please try again");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backIntent = new Intent(activity, MainActivity.class);
                        activity.startActivity(backIntent);
                    }
                });
                builder.create().show();


            }
        });
        queue.add(request);
    }
}
