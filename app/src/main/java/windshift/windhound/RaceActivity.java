package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        // Set textView to passed event id
        Intent intent = getIntent();
        String race = intent.getStringExtra(HomeActivity.EXTRA_RACE_ID);
        TextView textView = findViewById(R.id.textView_event_id);
        textView.setText("Race: " + race);
    }

    public void recordEvent(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
