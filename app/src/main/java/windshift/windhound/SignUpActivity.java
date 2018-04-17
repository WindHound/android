package windshift.windhound;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /* Toolbar Configuration */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Change back button colour
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setTitle("Sign Up");
    }

    // When the back button in the toolbar is pressed, return to the parent activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
