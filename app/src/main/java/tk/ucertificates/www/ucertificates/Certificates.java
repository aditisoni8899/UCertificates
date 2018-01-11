package tk.ucertificates.www.ucertificates;

/**
 * Created by DELL STORE on 5/17/2017.
 */

public class Certificates {

    /** Image resource ID for the certificate */
    private int mImageResourceId = NO_IMAGE_PROVIDED;
    /** which type of certificates for ex- workshop, spandan etc */
    private  String certitype;
    /** it holds certificate id */
    private int certificate_id;

    /** Constant value that represents no image was provided for this word */
    private static final int NO_IMAGE_PROVIDED = -1;

    public Certificates(int certificateid ,String name, int c_id) {
        mImageResourceId = certificateid;
        certitype = name;
        certificate_id = c_id;
    }


    /**
     * Get the certificate type
     */
    public String getCertitype() {
        return certitype;
    }

    /**
     * Return the image resource ID of the word.
     */
    public int  getCertificateImageResourceId(){
        return mImageResourceId; }
    /**
     * Return the certificate ID of the certicicate.
     */
    public int  getCertificateId(){
        return  certificate_id; }
}
