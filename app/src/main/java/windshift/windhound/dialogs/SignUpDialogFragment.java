package windshift.windhound.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import windshift.windhound.R;
import windshift.windhound.objects.UserDTO;

public class SignUpDialogFragment extends DialogFragment {

    UserDTO currentUser;
    private Context parent_context;
    private boolean created;

    public interface SignUpDialogListener {
        public void onDialogAccountCreation();
    }

    SignUpDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parent_context = context;
        // Verify that the host activity implements the callback interface
        try {
            listener = (SignUpDialogFragment.SignUpDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SignUpDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        created = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppThemeDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View signUpView = inflater.inflate(R.layout.fragment_sign_up_dialog, null);
        builder.setTitle(R.string.title_signup)
                .setView(signUpView)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        EditText emailET = signUpView.findViewById(R.id.editText_signup_email);
                        String email = emailET.getText().toString();
                        EditText passwordET = signUpView.findViewById(R.id.editText_signup_password);
                        String password = passwordET.getText().toString();
                        currentUser = new UserDTO(email, password, "Will");
                        //disableSSLCertificateChecking();
                        new HttpRequestTask().execute();
                        while (!created) {

                        }
                        listener.onDialogAccountCreation();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                AssetManager assetManager = parent_context.getAssets();
                // Load CAs from an InputStream
                // (could be from a resource or ByteArrayInputStream or ...)
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = new BufferedInputStream(assetManager.open("cert.crt"));
                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("tomcat", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);

                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {

                        return true;
                    }
                };

                URL url = new URL(getResources().getString(R.string.server_address) +
                        "/user/add");
                HttpsURLConnection connection = (HttpsURLConnection) url
                        .openConnection();
                connection.setHostnameVerifier(hostnameVerifier);
                connection.setSSLSocketFactory(context.getSocketFactory());
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
                out.writeObject(currentUser);
                out.close();
                /*
                OutputStreamWriter writer = new OutputStreamWriter(
                        connection.getOutputStream());
                Gson gson = new Gson();
                String jsonInString = gson.toJson(currentUser);
                writer.write(jsonInString);
                writer.close();
                */
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // OK

                    // otherwise, if any other status code is returned, or no status
                    // code is returned, do stuff in the else block
                } else {
                    // Server returned HTTP error code.
                }
                return null; //arbitrary
            } catch (Exception e) {
                e.printStackTrace();
            }
            created = true;
            return null;
        }

    }

}
