package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Intent intent = getIntent();
        String event = intent.getStringExtra(HomeActivity.EXTRA_EVENT_ID);

        TextView textView = findViewById(R.id.textView_event_id);
        textView.setText("Event: " + event);
    }
}
