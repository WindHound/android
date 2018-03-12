package windshift.windhound;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReplayActivity extends AppCompatActivity implements OnMapReadyCallback {



    private class boat {
        ArrayList<Integer> scores;
        String name;
        Integer displayColour;
        ArrayList<LatLng> coordinates;
        public ArrayList<Integer> getScores () {return scores;}
        public String getName (){return name;}
        public Integer getDisplayColour() {return displayColour;}
        public ArrayList<LatLng> getCoordinates() {return coordinates;}
        boat(ArrayList<Integer> scores, String name, Integer displayColour, ArrayList<LatLng> coordinates) {
            this.scores = scores;
            this.name = name;
            this.displayColour = displayColour;
            this.coordinates = coordinates;
        }
        public void setScores(ArrayList<Integer> scores) {
            this.scores = scores;
        }
        public void setCoordinates(ArrayList<LatLng> coordinates) {
            this.coordinates = coordinates;
        }
        public void setDisplayColour(Integer displayColour) {
            this.displayColour = displayColour;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class boatComparator implements Comparator<boat> {
        int current=0;
        boatComparator(int stepEntry) {
            current=stepEntry;
        }
        @Override
        public int compare(boat boat1, boat boat2) {
            return boat2.getScores().get(current)-boat1.getScores().get(current);
        }
    }

    static <T> void extendArray(ArrayList<T> array, Integer targetSize) {
        Integer startSize = array.size();
        Integer difference = targetSize - startSize;
        while (difference > 0) {
            array.add(array.get(startSize - 1));
            difference = difference - 1;
        }
    }

    private int race_id;
    Boolean pause=false;
    Integer maxArraySize = 0;
    ArrayList<boat> boats = new ArrayList<>();
    ArrayList<Polyline> racePaths = new ArrayList<>();
    Handler handler = new Handler();
    Integer step = 0;
    private GoogleMap mMap;

    private class timelineListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            step=progress*maxArraySize/100;
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }
    private class pathListener implements GoogleMap.OnPolylineClickListener {
        public void onPolylineClick(Polyline polyline) {
            int index = racePaths.indexOf(polyline);
            boat boat = boats.get(index);
            String name=boat.getName();
            Snackbar boatName = Snackbar.make(findViewById(R.id.details),name, BaseTransientBottomBar.LENGTH_SHORT);
            boatName.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);

        // Gets the id of the race to be replayed
        Intent intent = getIntent();
        race_id = Integer.parseInt(intent.getStringExtra(RaceActivity.EXTRA_REPLAY_RACE_ID));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final SeekBar timeline=findViewById(R.id.seekBar);
        timeline.setOnSeekBarChangeListener(new timelineListener());
        ToggleButton toggle = findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView leaderboardView=findViewById(R.id.leaderboard);
                if (isChecked) {
                    pause=true;
                    leaderboardView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                } else {
                    pause=false;
                    leaderboardView.setEllipsize(null);

                }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<ArrayList<LatLng>> paths = new ArrayList<>();
        ArrayList<ArrayList<Integer>> scoreList = new ArrayList<>();
        ArrayList<PolylineOptions> racePathOptions = new ArrayList<>();
        final ArrayList<String> leaderboardUFOverTime = new ArrayList<>();
        final Long period = Long.valueOf(2000);
        final SeekBar timeline=findViewById(R.id.seekBar);
        final TextView leaderboardView=findViewById(R.id.leaderboard);
        leaderboardView.setSelected(true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (step < maxArraySize && !pause) {
                    step = step+1;
                    for (int i=0;i<boats.size();i++) {
                        List<LatLng> currentCoordinates = boats.get(i).coordinates.subList(0, step);
                        racePaths.get(i).setPoints(currentCoordinates);
                    }
                    String dataUF = leaderboardUFOverTime.get(step-1);
                    leaderboardView.setText(dataUF);
                    Integer raceCompletion = (step*100)/maxArraySize;
                    timeline.setProgress(raceCompletion);
                }
                handler.postDelayed(this,period);
            }
        };

        //For now, fake data is here.  Eventually we will want to import data instead.
        LatLng purplePath1 = new LatLng(51.455792, -2.603306);
        LatLng purplePath2 = new LatLng(51.455622, -2.603381);
        LatLng purplePath3 = new LatLng(51.455896, -2.604561);
        Integer purpleScore1 = 100;
        Integer purpleScore2 = 100;
        Integer purpleScore3 = 100;
        Integer purpleScore4 = 100;
        LatLng redPath1 = new LatLng (51.456133, -2.602818);
        LatLng redPath2 = new LatLng (51.456160, -2.601976);
        Integer redScore1 = 60;
        Integer redScore2 = 60;
        LatLng greenPath1 = new LatLng (51.455511, -2.602914);
        LatLng greenPath2 = new LatLng (51.455183, -2.603563);
        LatLng greenPath3 = new LatLng (51.455661, -2.604287);
        Integer greenScore1 = 80;
        Integer greenScore2 = 80;
        Integer greenScore3 = 80;
        LatLng yellowPath1 = new LatLng (51.456012, -2.605435);
        LatLng yellowPath2 = new LatLng (51.456279, -2.606089);
        LatLng yellowPath3 = new LatLng (51.456032, -2.606143);
        LatLng yellowPath4 = new LatLng (51.456045, -2.607838);
        Integer yellowScore1 = 70;
        Integer yellowScore2 = 85;
        Integer yellowScore3 = 100;
        Integer yellowScore4 = 110;
        ArrayList<LatLng> purplePath = new ArrayList<>();
        purplePath.add(purplePath1);
        purplePath.add(purplePath2);
        purplePath.add(purplePath3);
        ArrayList<Integer> purpleScores = new ArrayList<>();
        purpleScores.add(purpleScore1);
        purpleScores.add(purpleScore2);
        purpleScores.add(purpleScore3);
        purpleScores.add(purpleScore4);
        ArrayList<LatLng> redPath = new ArrayList<>();
        redPath.add(redPath1);
        redPath.add(redPath2);
        ArrayList<Integer> redScores = new ArrayList<>();
        redScores.add(redScore1);
        redScores.add(redScore2);
        ArrayList<LatLng> greenPath = new ArrayList<>();
        greenPath.add(greenPath1);
        greenPath.add(greenPath2);
        greenPath.add(greenPath3);
        ArrayList<Integer> greenScores = new ArrayList<>();
        greenScores.add(greenScore1);
        greenScores.add(greenScore2);
        greenScores.add(greenScore3);
        ArrayList<LatLng> yellowPath = new ArrayList<>();
        yellowPath.add(yellowPath1);
        yellowPath.add(yellowPath2);
        yellowPath.add(yellowPath3);
        yellowPath.add(yellowPath4);
        ArrayList<Integer> yellowScores = new ArrayList<>();
        yellowScores.add(yellowScore1);
        yellowScores.add(yellowScore2);
        yellowScores.add(yellowScore3);
        yellowScores.add(yellowScore4);
        paths.add(purplePath);
        paths.add(redPath);
        paths.add(greenPath);
        paths.add(yellowPath);
        scoreList.add(purpleScores);
        scoreList.add(redScores);
        scoreList.add(greenScores);
        scoreList.add(yellowScores);

        boat purpleBoat = new boat(purpleScores, "Purple", 0x7b0083, purplePath);
        boats.add(purpleBoat);
        boat redBoat = new boat(redScores, "Red", 0xdd2828, redPath);
        boats.add(redBoat);
        boat greenBoat = new boat(greenScores, "Green", 0x239609, greenPath);
        boats.add(greenBoat);
        boat yellowBoat = new boat(yellowScores, "Yellow", 0xffb400, yellowPath);
        boats.add(yellowBoat);

        //Find the centre of the race.
        Double overallLat = 0.0;
        Double overallLng = 0.0;
        Integer Points = 0;
        for (ArrayList<LatLng> path: paths) {
            for (LatLng point: path) {
                overallLat = overallLat + point.latitude;
                overallLng = overallLng + point.longitude;
                Points = Points + 1;
            }
        }
        overallLat = overallLat / Points;
        overallLng = overallLng / Points;
        LatLng centre = new LatLng(overallLat, overallLng);

        //Find the largest array of all score-lists and paths.
        for (ArrayList<LatLng> path : paths) {
            if (path.size() > maxArraySize) {maxArraySize = path.size();}
        }
        for (ArrayList<Integer> score : scoreList) {
            if (scoreList.size() > maxArraySize) {maxArraySize = score.size();}
        }

        //Extend all arrays by duplicating their last element such that they are all the same size
        for (ArrayList<LatLng> path : paths) {
            extendArray(path, maxArraySize);
        }
        for (ArrayList<Integer> score : scoreList) {
            extendArray(score, maxArraySize);
        }

        // Create a list to display the leaderboard over time.
        for (int i=0;i<maxArraySize;i++) {
            ArrayList<boat> sorted = new ArrayList<>();
            sorted.addAll(boats);
            Collections.sort(sorted,new boatComparator(i));
            String leaderboardUFEntry="";
            for (int j=0;j<sorted.size();j++) {
                leaderboardUFEntry=leaderboardUFEntry+sorted.get(j).getName();
                if(j<sorted.size()-1) {
                    leaderboardUFEntry = leaderboardUFEntry+" - ";
                }
            }
            leaderboardUFOverTime.add(leaderboardUFEntry);
        }

        //Centre the camera above the race
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 16));

//        Set up paths.
        for (int i = 0;i<boats.size();i++) {
            PolylineOptions newLine = new PolylineOptions();
            newLine.add(boats.get(i).getCoordinates().get(0));
            newLine.color(boats.get(i).getDisplayColour()+0xff000000);
            newLine.width(4);
            racePathOptions.add(newLine);
            racePaths.add(mMap.addPolyline(racePathOptions.get(i)));
            racePaths.get(i).setClickable(true);
        }
        mMap.setOnPolylineClickListener(new pathListener());
        handler.post(runnable);

    }
    public void back(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void reset(View view) {
        final SeekBar timeline=findViewById(R.id.seekBar);
        ToggleButton toggle = findViewById(R.id.toggleButton);
        timeline.setProgress(0);
        step=0;
        toggle.setChecked(false);
    }
}
