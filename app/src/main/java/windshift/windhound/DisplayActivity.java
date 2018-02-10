package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Set textView to passed event id
        Intent intent = getIntent();
        String event = intent.getStringExtra(HomeActivity.EXTRA_EVENT_ID);
        TextView textView = findViewById(R.id.textView_event_id);
        textView.setText("Event: " + event);
    }

    public void recordEvent(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
