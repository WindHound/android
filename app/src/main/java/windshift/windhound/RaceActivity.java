package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RaceActivity extends AppCompatActivity {

    private String race_id;
    public static final String EXTRA_REPLAY_RACE_ID = "windshift.windhound.REPLAY_RACE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        // Set textView to passed event id
        Intent intent = getIntent();
        race_id = intent.getStringExtra(HomeActivity.EXTRA_RACE_ID);
        TextView textView = findViewById(R.id.textView_event_id);
        textView.setText("Race: " + race_id);

        Button button = findViewById(R.id.button_record_replay);
        // Set record/replay button depending on whether an upcoming or past race was selected
        if (intent.getStringExtra(HomeActivity.EXTRA_UPCOMING_BOOL).equals("true")) {
            button.setText("Record");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordEvent(v);
                }
            });
        } else {
            button.setText("Replay");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replayEvent(v);
                }
            });
        }
    }

    public void recordEvent(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    public void replayEvent(View view) {
        Intent intent = new Intent(this, ReplayActivity.class);
        intent.putExtra(EXTRA_REPLAY_RACE_ID, race_id);
        startActivity(intent);
    }

}
