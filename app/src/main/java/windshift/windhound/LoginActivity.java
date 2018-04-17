package windshift.windhound;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import windshift.windhound.dialogs.SignUpDialogFragment;

public class LoginActivity extends AppCompatActivity implements
        SignUpDialogFragment.SignUpDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // stops the on-screen keyboard automatically popping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    // Called when the login button is pressed
    public void login(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showSignUp(View view) {
        DialogFragment signUpFragment = new SignUpDialogFragment();
        signUpFragment.show(getSupportFragmentManager(), "signUp");
    }

    @Override
    public void onDialogAccountCreation() {
        Toast toast = Toast.makeText(this, "Account created.",
                Toast.LENGTH_SHORT);
        toast.show();
    }

}
