package windshift.windhound;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import windshift.windhound.race.Race;

/*
 * Race activity displays information about the passed race id, with the option to record if the
 * race is upcoming, and replay if the race is in the past.
 */
public class RaceActivity extends AppCompatActivity implements OnMapReadyCallback{

    private MapView mapView;
    private Race race;
    public static final String EXTRA_REPLAY_RACE_ID = "windshift.windhound.REPLAY_RACE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        Intent intent = getIntent();
        race = (Race) intent.getSerializableExtra("Race");

        /* Toolbar Configuration */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Change back button colour
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setTitle(race.getName());

        /* Record/replay button configuration */
        Button button = findViewById(R.id.button_record_replay);
        // Set record/replay button depending on whether an upcoming or past race was selected
        if (intent.getStringExtra(HomeActivity.EXTRA_UPCOMING_BOOL).equals("true")) {
            button.setText("Record");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordEvent();
                }
            });
        } else {
            button.setText("Replay");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replayEvent();
                }
            });
        }

        /* List view configuration*/
        ListView listView = findViewById(R.id.listView);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        // Add event and/or championship this race belongs to
        String[] values = new String[] {"Date: " + dateFormat.format(race.getStartDate().getTime()),
                "Time: " + timeFormat.format(race.getStartDate().getTime()), "Boat: "};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    selectBoat();
                }
            }
        });

        /* Map configuration*/
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng bristol = new LatLng(51.454514, -2.587910);
        googleMap.addMarker(new MarkerOptions().position(bristol)
                .title("Marker in Bristol"));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bristol));
        mapView.onResume();
    }

    // When the back button in the toolbar is pressed, return to the parent activity
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Called when the record button is pressed
    public void recordEvent() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    // Called when the replay button is pressed
    public void replayEvent() {
        Intent intent = new Intent(this, ReplayActivity.class);
        intent.putExtra(EXTRA_REPLAY_RACE_ID, race.getID());
        startActivity(intent);
    }

    // Called when the boat list item is pressed
    public void selectBoat() {
        SelectBoatDialogFragment selectBoat = new SelectBoatDialogFragment();
        selectBoat.show(getSupportFragmentManager(), "dialog");
    }

}
