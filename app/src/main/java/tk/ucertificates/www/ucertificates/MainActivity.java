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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText uname;
    private EditText pass;
    private Button login;
    private String login_name, passwrd;
    public static int responseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        uname = (EditText) findViewById(R.id.userid);
        pass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login_button);

        //if there is any save user exist then no need to login
        if (SaveSharedPreference.getUserName(MainActivity.this).length() != 0) {
            Intent i1 = new Intent(MainActivity.this, HomePage.class);
            startActivity(i1);
            MainActivity.this.finish();

        }
        //if there is not any saved user then login
        else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    login_name = uname.getText().toString().trim();
                    passwrd = pass.getText().toString().trim();

                    class Login extends AsyncTask<String, Void, String> {
                        ProgressDialog progressDialog;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog = ProgressDialog.show(MainActivity.this, "", "Loading", true);
                        }

                        @Override
                        protected String doInBackground(String... strings) {
                            Response response;
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();
                                // Build the request
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", strings[0])
                                        .add("password", strings[1])
                                        .build();
                                Request request = new Request.Builder().url("http://www.ucertificates.tk/api/login")
                                        .post(requestBody)
                                        .build();

                                // Reset the response code
                                responseCode = 0;

                                // Make the request
                                response = okHttpClient.newCall(request).execute();
                                if ((responseCode = response.code()) == 200) {
                                    // Get response whch is json string and pass it to on post execute method
                                    return response.body().string();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Please try again ", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_LONG).show();

                                        // if user login successful then set UserName using setUserName() function.
                                        SaveSharedPreference.setUserName(MainActivity.this, login_name);

                                        Intent i1 = new Intent(MainActivity.this, HomePage.class);
                                        startActivity(i1);
                                        MainActivity.this.finish();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
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
                        Login login = new Login();
                        login.execute(login_name, passwrd);
                    } else {
                        Toast.makeText(MainActivity.this, "No Internet connection available", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
