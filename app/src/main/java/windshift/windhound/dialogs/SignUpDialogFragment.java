package windshift.windhound.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import windshift.windhound.R;
import windshift.windhound.objects.UserDTO;

public class SignUpDialogFragment extends DialogFragment {

    UserDTO currentUser;

    public interface SignUpDialogListener {
        public void onDialogAccountCreation();
    }

    SignUpDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                        currentUser = new UserDTO("smithwjv", password, "Will",
                                email);
                        new HttpRequestTask().execute();
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
                final String url = getResources().getString((R.string.server_address)) +
                        "/user/add";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                restTemplate.postForObject(url, currentUser, Void.class);
                return null; //arbitrary
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
