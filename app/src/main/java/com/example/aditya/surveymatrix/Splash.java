package com.example.aditya.surveymatrix;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aditya.surveygini.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }
        if (!haveNetworkConnection()){
            Toast.makeText(Splash.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
        }
       else{
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress(); // Calls onProgressUpdate()
            try {
                resp = fetchFromAPI();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            Intent intent = new Intent(Splash.this, Registration.class);
            intent.putExtra("resp", resp);
            if(resp.length()==0)
                Toast.makeText(Splash.this, "Pleae check your connection and try again.", Toast.LENGTH_LONG).show();
            else
                startActivity(intent);
            //Toast.makeText(Splash.this, resp, Toast.LENGTH_LONG).show();
            //finalResult.setText(result);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Splash.this,
                    "Survey Matrix is Loading",
                    "Please wait. . .");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);
        }
    }
    public String fetchFromAPI() throws UnsupportedEncodingException {
        String data = URLEncoder.encode("apiLogin", "UTF-8") + "=" + URLEncoder.encode("fraAPI", "UTF-8");
        data += "&" + URLEncoder.encode("apiPass", "UTF-8") + "=" + URLEncoder.encode("Vpe0ZWV", "UTF-8");
        data += "&" + URLEncoder.encode("array_format", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
        String text = "";
        BufferedReader reader = null;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Send data
        try {

            URL url = new URL("http://surveygini.com/fra_surveyapp_2017/api/get_registration_data");
            //URLConnection conn = url.openConnection();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            // Get the response


            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            text = sb.toString();
        } catch (Exception ex) {
            //content.setText("Exception");
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }
        return text;
    }
}
