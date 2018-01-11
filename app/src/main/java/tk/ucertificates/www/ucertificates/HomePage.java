package tk.ucertificates.www.ucertificates;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static tk.ucertificates.www.ucertificates.MainActivity.responseCode;

public class HomePage extends AppCompatActivity {
    private Button logout;
    private Button sendCertificates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //for send certificates button
        sendCertificates = (Button) findViewById(R.id.send_new_certificates);
        sendCertificates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this, Template.class);
                startActivity(intent);
            }
        });


        //for logout button
        logout = (Button) findViewById(R.id.logou_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                class Logout extends AsyncTask<String, Void, String> {
                    ProgressDialog progressDialog;


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = ProgressDialog.show(HomePage.this, "", "Loading", true);
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        Response response;
                        try {

                            OkHttpClient okHttpClient = new OkHttpClient();
                            // Build the get request
                            Request request = new Request.Builder()
                                    .url("http://www.ucertificates.tk/api/logout")
                                    .build();

                            // Reset the response code
                            responseCode = 0;

                            // Make the request
                            response = okHttpClient.newCall(request).execute();
                            if ((responseCode = response.code()) == 200) {
                                // Get response which is json string and pass it to on post execute method
                                return response.body().string();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(HomePage.this, "Please try again ", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                        } catch (IOException io) {
                            io.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            if (s != null) {
                                JSONObject jsonObject = new JSONObject(s);
                                String status = jsonObject.getString("status");
                                progressDialog.dismiss();
                                if (status.equals("success")) {
                                    Toast.makeText(HomePage.this, "You have successfully logged out!", Toast.LENGTH_LONG).show();

                                    //clear all stored data if user log out
                                    SaveSharedPreference.clearUserName(HomePage.this);

                                    Intent i1 = new Intent(HomePage.this, MainActivity.class);
                                    startActivity(i1);
                                    HomePage.this.finish();


                                }
                            } else {
                                Toast.makeText(HomePage.this, "Problem in logging out", Toast.LENGTH_LONG).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    Logout logout = new Logout();
                    logout.execute();
                } else {
                    Toast.makeText(HomePage.this, "No Internet connection available", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

}
