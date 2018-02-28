package windshift.windhound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ReplayActivity extends AppCompatActivity {

    private int race_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        // Gets the id of the race to be replayed
        Intent intent = getIntent();
        race_id = Integer.parseInt(intent.getStringExtra(RaceActivity.EXTRA_REPLAY_RACE_ID));

        // Test
    }
}
