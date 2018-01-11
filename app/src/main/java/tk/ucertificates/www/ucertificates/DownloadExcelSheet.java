package tk.ucertificates.www.ucertificates;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static tk.ucertificates.www.ucertificates.MainActivity.responseCode;

public class DownloadExcelSheet extends AppCompatActivity {
    private int certi_id;
    private Button next;
    private Button downloadsheet;
    private TextView certi_info_textview;
    private TextView certi_name;

    // progressbar for downloading
    private ProgressDialog mProgressDialog;
    // Application specific request code to match with a result reported to
    // onRequestPermissionsResult(int, String[], int[]). Should be >= 0.
    // it is third parameter of requestpermission method
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set xml for the activity
        setContentView(R.layout.activity_download_excel_sheet);


        //recieve the intent
        Intent mIntent = getIntent();
        //get the value from extras of variable certificate_id and store it in intValue variable
        int intValue = mIntent.getIntExtra("certificate_id", 0);

        certi_id = intValue;


        //to set template name to the textview
        certi_name = (TextView) findViewById(R.id.certi_name);
        switch (certi_id){

            case 1:
                certi_name.setText("Navarachanaa");
                break;
            case 2:
                certi_name.setText("Spandan");
                break;
            case 4:
                certi_name.setText("Workshop");
                break;
        }

        //if build version is greater than equal to marsh mallow(api level 23)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // this is to show pop up one time after installation on edit buttob click to allow permissions
            if (ContextCompat.checkSelfPermission(DownloadExcelSheet.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(DownloadExcelSheet.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    //This is called if user has denied the permission before
                    //In this case I am just asking the permission again

                    ActivityCompat.requestPermissions(DownloadExcelSheet.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(DownloadExcelSheet.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
        //class to get certi info
        class GetCertiInfo extends AsyncTask<Integer, Void, String> {


            @Override
            protected String doInBackground(Integer... integer) {
                Response response;
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // Build the request
                    RequestBody requestBody = new FormBody.Builder()
                            .add("certificate_id", String.valueOf(integer[0]))
                            .build();
                    Request request = new Request.Builder().url("http://www.ucertificates.tk/api/certificateinfo")
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
                        Toast.makeText(DownloadExcelSheet.this, "Please try again ", Toast.LENGTH_SHORT).show();
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
                        JSONObject message = jsonObject.getJSONObject("message");
                        JSONObject data = message.optJSONObject("data");
                        String certi_info = data.getString("c_info");
                        certi_info_textview = (TextView) findViewById(R.id.name);
                        certi_info_textview.setText(certi_info);


                    } else {

                        Toast.makeText(DownloadExcelSheet.this, "Error, Cannot sync to server ", Toast.LENGTH_LONG).show();
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
            //call preecxecute method of DownloadSheet class
            GetCertiInfo info = new GetCertiInfo();
            info.execute(certi_id);
        } else {
            Toast.makeText(DownloadExcelSheet.this, "No Internet connection available", Toast.LENGTH_LONG).show();
        }

        // on click of download sheet button downlod excel sheet logic
        downloadsheet = (Button) findViewById(R.id.downloadSpreadSheet);
        downloadsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // this is to show pop up one time after installation on edit buttob click to allow permissions
                if (ContextCompat.checkSelfPermission(DownloadExcelSheet.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DownloadExcelSheet.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again

                        ActivityCompat.requestPermissions(DownloadExcelSheet.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(DownloadExcelSheet.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    class DownloadSheet extends AsyncTask<Integer, Void, String> {
                        ProgressDialog progressDialog;
                        //excel sheet url
                        private String url;
                        //filename of downloading excel sheet
                        private  String sheet_name;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog = ProgressDialog.show(DownloadExcelSheet.this, "", "Loading", true);
                        }

                        @Override
                        protected String doInBackground(Integer... integer) {
                            Response response;
                            try {
                                OkHttpClient okHttpClient = new OkHttpClient();
                                // Build the request
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("certificate_id", String.valueOf(integer[0]))
                                        .build();
                                Request request = new Request.Builder().url("http://www.ucertificates.tk/api/certificateinfo")
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
                                    Toast.makeText(DownloadExcelSheet.this, "Please try again ", Toast.LENGTH_SHORT).show();
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
                                    JSONObject message = jsonObject.getJSONObject("message");
                                    JSONObject path = message.optJSONObject("paths");
                                    JSONObject data = message.optJSONObject("data");
                                    String sheet_name = data.getString("c_xlfilename");
                                    this.sheet_name = sheet_name;
                                    String url = path.getString("excel");
                                    this.url = "http://" + url;
                                    progressDialog.dismiss();
                                    startDownload();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(DownloadExcelSheet.this, "Downloading Error", Toast.LENGTH_LONG).show();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        private void startDownload() {
                            new DownloadFileAsync().execute(url);
                        }


                        //class to download excel sheet
                        class DownloadFileAsync extends AsyncTask<String, Integer, String> {

                            String filepath;
                            long total;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                // Create progress dialog
                                mProgressDialog = new ProgressDialog(DownloadExcelSheet.this);
                                // Set your progress dialog Title
                                mProgressDialog.setTitle("Excel Sheet");
                                // Set your progress dialog Message
                                mProgressDialog.setMessage("Downloading, Please Wait!");
                                mProgressDialog.setIndeterminate(false);
                                mProgressDialog.setMax(100);
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                // Show progress dialog
                                mProgressDialog.show();
                            }

                            @Override
                            protected String doInBackground(String... Url) {
                                try {
                                    URL url = new URL(Url[0]);
                                    URLConnection connection = url.openConnection();
                                    connection.connect();

                                    // Detect the file lenghth
                                    int fileLength = connection.getContentLength();

                                    // Locate storage location
                                    filepath = Environment.getExternalStorageDirectory()
                                            .getPath();
                                    // Download the file
                                    InputStream input = new BufferedInputStream(url.openStream());

                                    // Save the downloaded file
                                    OutputStream output = new FileOutputStream(filepath + "/" + sheet_name);

                                    byte data[] = new byte[1024];
                                    total = 0;
                                    int count;
                                    while ((count = input.read(data)) != -1) {
                                        total += count;
                                        // Publish the progress
                                        publishProgress((int) (total * 100 / fileLength));
                                        output.write(data, 0, count);
                                    }

                                    // Close connection
                                    output.flush();
                                    output.close();
                                    input.close();

                                } catch (Exception e) {
                                    // Error Log
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onProgressUpdate(Integer... progress) {
                                super.onProgressUpdate(progress);
                                // Update the progress dialog
                                mProgressDialog.setProgress(progress[0]);
                                // Dismiss the progress dialog
                                //mProgressDialog.dismiss();
                            }

                            @Override
                            protected void onPostExecute(String unused) {
                                if (total != 0) {

                                    mProgressDialog.dismiss();

                                    AlertDialog alertDialog = new AlertDialog.Builder(
                                            DownloadExcelSheet.this).create();

                                    // Setting Dialog Title
                                    alertDialog.setTitle("Downloaded succesfully!");

                                    // Setting Dialog Message
                                    alertDialog.setMessage("Location: " + filepath + "/" + sheet_name);

                                    // Setting Icon to Dialog
                                    alertDialog.setIcon(R.drawable.ic_check_circle_black_24dp);

                                    // Setting OK Button
                                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Write your code here to execute after dialog closed
                                            Toast.makeText(getApplicationContext(), "Check downloaded file at loaction", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    // Showing Alert Message
                                    alertDialog.show();
                                }

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
                        //call preecxecute method of DownloadSheet class
                        DownloadSheet login = new DownloadSheet();
                        login.execute(certi_id);
                    } else {
                        Toast.makeText(DownloadExcelSheet.this, "No Internet connection available", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        //for next button
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadExcelSheet.this, SelectSheet.class);
                startActivity(intent);
            }
        });
    }

    //Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //mProgressDialog.dismiss();
                    Toast.makeText(DownloadExcelSheet.this, "cannot access storage",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
