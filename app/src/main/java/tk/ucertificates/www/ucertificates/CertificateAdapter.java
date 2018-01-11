package tk.ucertificates.www.ucertificates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by DELL STORE on 5/17/2017.
 */

public class CertificateAdapter extends ArrayAdapter<Certificates> {
    /**
     * Constructs a new {@link CertificateAdapter}.
     *
     * @param context      of the app
     * @param certificates is the list of earthquakes, which is the data source of the adapter
     */

    public CertificateAdapter(Context context, List<Certificates> certificates) {
        super(context, 0, certificates);

    }


    /**
     * Returns a list item view that displays information about the certificate at the given position
     * in the list of certificates
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.certificate_item, parent, false);
        }

        // Find the certificate at the given position in the list of certificates
        Certificates currentCertificate = getItem(position);


        // Find the TextView with view ID certificate_type
        TextView certificateView = (TextView) listItemView.findViewById(R.id.certificate_type);
        // set the text to the textview
        certificateView.setText(currentCertificate.getCertitype());

        // Find the ImageView in the list_item.xml layout with the ID image.
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.certificate_image);
        // If an image is available, display the provided image based on the resource ID
        imageView.setImageResource(currentCertificate.getCertificateImageResourceId());


        // Return the whole certificate item layout (containing TextView and imageview)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
