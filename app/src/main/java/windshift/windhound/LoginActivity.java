package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.view.WindowManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Stops the on-screen keyboard automatically popping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void login(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

}
