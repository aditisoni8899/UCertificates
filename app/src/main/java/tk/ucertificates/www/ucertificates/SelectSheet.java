package tk.ucertificates.www.ucertificates;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;


public class SelectSheet extends AppCompatActivity {

    private Button select;
    private Button send;
    private TextView file;
    private TextView mfilesize;


    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sheet);

        //for upload & send button
        send = (Button) findViewById(R.id.upload_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* class SignupWithImageTask extends AsyncTask<File, Integer, String> {

                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(SelectSheet.this);
                        progressDialog.setMessage("Please Wait....");
                        progressDialog.show();
                    }

                    @Override
                    protected String doInBackground(File... str) {
                        Response response;
                        try {

                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody =  new MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart("file", file.getName(),
                                            RequestBody.create(MediaType.parse("text/plain"), String.valueOf(file)))
                                    .addFormDataPart("other_field", "other_field_value")
                                    .build();

                            Request request = new Request.Builder().url("http://www.ucertificates.tk/api/uploadandsend")
                                    .post(requestBody)
                                    .build();

                            // Make the request
                            response = okHttpClient.newCall(request).execute();
                            if ((response.code()) == 200) {
                                // Get response whch is json string and pass it to on post execute method
                                return response.body().string();


                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SelectSheet.this, "Please try again ", Toast.LENGTH_SHORT).show();
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
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);


                    }

                }


                 */
                Intent intent = new Intent(SelectSheet.this, RequestSent.class);
                startActivity(intent);
                SelectSheet.this.finish();
            }
        });

        // for select file button
        select = (Button) findViewById(R.id.select_button);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();

            }
        });

    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private void showFileChooser() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/x-excel");
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // special intent for Samsung file manager
        Intent sintent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sintent.putExtra("CONTENT_TYPE", "*/*");
        sintent.addCategory(Intent.CATEGORY_DEFAULT);


        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sintent, 0) != null){
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sintent,"Select Excel File to Upload");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        }
        else {
            chooserIntent = Intent.createChooser(intent,"Select Excel File to Upload");
        }


        try {

            startActivityForResult(chooserIntent,FILE_SELECT_CODE);

        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            // The ACTION_OPEN_DOCUMENT intent was sent with the request code
            // FILE_SELECT_CODE. If the request code seen here doesn't match, it's the
            // response to some other intent, and the code below shouldn't run at all.

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to onactivityresult method as a parameter.
                    // Pull that URI using resultData.getData().
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("onActivityREsult", "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = SelectSheet.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d("OnActivityResult", "File Path: " + path);
                    // to extract filename from the path
                    String filename = path.substring(path.lastIndexOf("/") + 1);
                    // set it to the textview
                    file = (TextView) findViewById(R.id.filename);
                    file.setText("NAME - " + filename);
                    // Get the file instance
                    File fyl = new File(path);
                    //to get the file size in kb
                    int file_size = Integer.parseInt(String.valueOf(fyl.length() / 1024));
                    mfilesize = (TextView) findViewById(R.id.filesize);
                    mfilesize.setText("SIZE - " + file_size + " kB");
                    String extension = path.substring(path.lastIndexOf("."));
                    Log.d("extension",extension);

                    if(file_size!=0 && (extension.equalsIgnoreCase(".xlsx") || extension.equalsIgnoreCase(".xls") || extension.equalsIgnoreCase(".csv")  ))
                    {
                        send.setEnabled(true);
                    }

                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                // The query, since it only applies to a single document, will only return
                // one row. There's no need to filter, sort, or select fields, since we want
                // all fields for one document.
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                // "if there's anything to look at, look at it" conditionals.
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

}
