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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import windshift.windhound.dialogs.SelectBoatDialogFragment;
import windshift.windhound.objects.Race;

/*
 * Race activity displays information about the passed race id, with the option to record if the
 * race is upcoming, and replay if the race is in the past.
 */
public class RaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        SelectBoatDialogFragment.SelectBoatDialogListener {

    private ArrayAdapter<String> adapter;
    private boolean boatSelected;
    private long boatNum;
    private MapView mapView;
    private Race race;
    private String[] raceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
        Intent intent = getIntent();
        race = (Race) intent.getSerializableExtra("Race");

        boatSelected = false;

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
        raceInfo = new String[] {"Date: " + dateFormat.format(race.getStartDate()),
                "Time: " + timeFormat.format(race.getStartDate()), "Boat: "};
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, raceInfo);
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
        LatLng UBBC = new LatLng(51.3985, -2.4474);
        googleMap.addMarker(new MarkerOptions().position(UBBC)
                .title("Marker at the University of Bristol Boat Club"));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(UBBC));
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
        if (boatSelected) {
            Intent intent = new Intent(this, RecordActivity.class);
            intent.putExtra("Race", race);
            intent.putExtra("Boat", String.valueOf(boatNum));
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(this, "Please select a boat.",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Called when the replay button is pressed
    public void replayEvent() {
        // TODO change from record to replay
        Intent intent = new Intent(this, ReplayActivity.class);
        intent.putExtra("Race", race);
        startActivity(intent);
    }

    // Called when the boat list item is pressed
    public void selectBoat() {
        String[] boats;
        if (race.getBoats().length != 0) {
            boats = new String[race.getBoats().length];
            for (int i = 0; i < race.getBoats().length; i++) {
                boats[i] = String.valueOf(race.getBoats()[i]);
            }
        } else {
            boats = new String[] {"Default"};
        }
        SelectBoatDialogFragment selectBoat = SelectBoatDialogFragment.newInstance(boats);
        selectBoat.show(getSupportFragmentManager(), "SelectBoatDialog");
    }

    @Override
    public void onDialogBoatClick(String boat) {
        boatNum = Long.parseLong(boat);
        raceInfo[2] = "Boat: " + boat;
        adapter.notifyDataSetChanged();
        boatSelected = true;
    }

}
