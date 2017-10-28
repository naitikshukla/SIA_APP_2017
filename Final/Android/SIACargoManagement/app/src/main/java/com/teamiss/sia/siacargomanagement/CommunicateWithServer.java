package com.teamiss.sia.siacargomanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.DataOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static android.R.attr.bitmap;
import static android.content.ContentValues.TAG;

/**
 * Created by Navi-PC on 26/10/2017.
 */

public class CommunicateWithServer extends AsyncTask<String, Void, String> {

    private Socket socket;
    private MainActivity context;
    private static final String SERVER_IP = "192.168.56.1";
    private static final int SERVER_PORT = 9999;

    public CommunicateWithServer(MainActivity context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... strings) {
        String response = null;
        Uri builtUri = Uri.parse("http://172.17.10.16:8080/upload_image/").buildUpon().build();
        //builtUri = Uri.withAppendedPath(builtUri,"upload_image").buildUpon().build();
        Log.v(TAG, builtUri.toString());
        URL url;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.v(TAG, e.getMessage());
            return null;
        }

        String result = null;
        HttpURLConnection httpURLConnection = null;
        DataOutputStream dos = null;
        JSONObject json = new JSONObject();
        try {
            json.put("image",strings[0]);
        } catch (JSONException e) {
            Log.v(TAG, e.getMessage());
        }

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=utf8");
            httpURLConnection.setDoOutput(true);


            dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeUTF(json.toString());
            //Log.v(TAG, "json " + json.toString());
            //dos.write(512);
            Log.v(TAG, "Http POST response " + httpURLConnection.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
            Log.v(TAG, "Http POST response message" + result);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.flush();
                    dos.close();
                } catch (IOException exception) {
                    Log.v(TAG, exception.getMessage());
                }
            }
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        response = result;
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        context.uploadDone(s);
        Log.d("test","test");
        super.onPostExecute(s);
    }
}
