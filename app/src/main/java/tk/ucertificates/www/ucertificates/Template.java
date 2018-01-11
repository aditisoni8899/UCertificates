package tk.ucertificates.www.ucertificates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Template extends AppCompatActivity {
    private CertificateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);


        final ArrayList<Certificates> certi=new ArrayList<Certificates>();

        certi.add(new Certificates(R.drawable.navarachanaa,"Navarchanaa",1));
        certi.add(new Certificates(R.drawable.spandan,"Spandan",2));
        certi.add(new Certificates(R.drawable.workshop,"Workshop",4));


        // Create an {@link CertificateAdapter}, whose data source is a list of {@link Certificates}s. The
        // adapter knows how to create list items for each item in the list.

         adapter = new CertificateAdapter(this, certi);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // activity_template file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of certificates.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name adapter.
        listView.setAdapter(adapter);


        // Set an item click listener on the ListView, which sends an intent to a DownloadExcelsheet Activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current certificate that was clicked on
                Certificates currentCertificate = adapter.getItem(position);
                //send the intent to launch Download Excel Sheet
                Intent intent = new Intent(Template.this,DownloadExcelSheet.class);

                //pass certificate id of current activity to the next activity
                intent.putExtra("certificate_id", currentCertificate.getCertificateId());

                // launch a new activity Download excel sheet
                startActivity(intent);
            }
        });

    }
}
