package windshift.windhound;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
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

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import windshift.windhound.adapters.RecyclerViewAdapter;
import windshift.windhound.objects.MoveDataDTO;
import windshift.windhound.objects.Race;
//import windshift.windhound.objects.MoveDataDTO;

public class ReplayActivity extends AppCompatActivity implements OnMapReadyCallback, RecyclerViewAdapter.ItemClickListener {

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
        boat(String name, Integer displayColour, ArrayList<LatLng> coordinates) {
            ArrayList<Integer> scores = new ArrayList<>();
            scores.add(0);
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
    class timedLocationComparator implements Comparator<Pair<Calendar,LatLng>> {
        @Override
        public int compare(Pair<Calendar,LatLng> p1, Pair<Calendar,LatLng> p2) {
            return p1.first.compareTo(p2.first);
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

    private Race race;
    private long race_id;
    private int currentBoatNum = 0;
    private ArrayList<Long> boat_ids;
    Integer numberOfBoats = 0;
    private ArrayList<Integer> displayColours = new ArrayList<>();
    Boolean pause=false;
    Boolean expandedDetails=false;
    Integer maxArraySize = 0;
    ArrayList<boat> boats = new ArrayList<>();
    ArrayList<Polyline> racePaths = new ArrayList<>();
    Handler handler = new Handler();
    Integer step = 0;
    int density = 0;
    private GoogleMap mMap;
    private RecyclerViewAdapter adapter;
    private MoveDataDTO[] moves;
    private ArrayList<Pair<Long,MoveDataDTO>> boatsAndMoves = new ArrayList<>();
    private boolean loaded = false;
    private long totalMoves = 0;

    public void detailsExpandAndContract(View v) {
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout current = findViewById(R.id.overallLayout);
        constraintSet.clone(current);
        if(expandedDetails) {
            constraintSet.setVisibility(R.id.seekBar,ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.map,ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.toggleButton,ConstraintSet.VISIBLE);
            constraintSet.constrainHeight(R.id.details,(45*density/160));
        }
        else {
            constraintSet.setVisibility(R.id.seekBar,ConstraintSet.GONE);
            constraintSet.setVisibility(R.id.map,ConstraintSet.GONE);
            constraintSet.setVisibility(R.id.toggleButton,ConstraintSet.GONE);
            constraintSet.constrainHeight(R.id.details,ConstraintSet.MATCH_CONSTRAINT);
        }
        expandedDetails = !expandedDetails;
        constraintSet.applyTo(current);
    }

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
            currentBoatNum = index;
            boat boat = boats.get(index);
            String name="Boat "+boat.getName();
            TextView details = findViewById(R.id.details);
            details.setText(name);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        boat boat = boats.get(position);
        String name="Boat "+boat.getName();
        TextView details = findViewById(R.id.details);
        details.setText(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CREATION","App is running!");
        setContentView(R.layout.activity_replay);

        // get the race object to be replayed
        Intent intent = getIntent();
        race = (Race) intent.getSerializableExtra("Race");

        //TODO **UNCOMMENT THIS WHEN MOVING TO SERVER**
//        HashSet<Long> boatIdsIn = race.getBoats();
//        race_id = race.getID();
//        boat_ids = new ArrayList<>(boatIdsIn);

        //TODO **COMMENT THIS OUT WHEN MOVING TO SERVER**
        race_id=82;
        boat_ids = new ArrayList<>();
        boat_ids.add((long) 2);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        density=metrics.densityDpi;
        numberOfBoats = boat_ids.size();
        Random rn = new Random();
        for (int i=0;i<numberOfBoats;i++) {
            displayColours.add(rn.nextInt(16777215)+0xff000000);
        }

        // set up the RecyclerView
        RecyclerView rView = findViewById(R.id.leaderboard);
        LinearLayoutManager scrollManager
                = new LinearLayoutManager(ReplayActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rView.setLayoutManager(scrollManager);
        adapter = new RecyclerViewAdapter(this,displayColours,boat_ids);
        adapter.setClickListener(this);
        rView.setAdapter(adapter);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final SeekBar timeline=findViewById(R.id.seekBar);
        timeline.setOnSeekBarChangeListener(new timelineListener());
        ToggleButton toggle = findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pause=isChecked;
            }
        });
    }

    private class HttpRequestMoveDataDTO extends AsyncTask<Void, Void, ArrayList<Pair<Long,MoveDataDTO>>> {
        @Override
        protected ArrayList<Pair<Long,MoveDataDTO>> doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                for (int i=0;i<boat_ids.size();i++) {
                    final String raceURL = getResources().getString((R.string.server_address)) +
                            "/movedata/get/" + Long.toString(race_id) + "/" + Long.toString(boat_ids.get(i));
                    Log.d("TEST",raceURL);
                    moves =restTemplate.getForObject(raceURL, MoveDataDTO[].class);
                    Log.d("TEST","Complete");
                    for (int j=0;j<moves.length;j++) {
                        boatsAndMoves.add(new Pair<>(boat_ids.get(i),moves[j]));
                    }
                }
                loaded=true;
                return boatsAndMoves;
                } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Pair<Long, MoveDataDTO>> pairs) {
            loaded=true;
        }
    }


    ArrayList<Pair<Calendar,LatLng>> getTimesAndLocationsForBoat(long boatID) {
        ArrayList<Pair<Calendar,LatLng>> output = new ArrayList<>();

        //TODO: Uncomment this when moving to server.
        ArrayList<MoveDataDTO> filteredMoves = new ArrayList<>();
        for (int i=0;i<totalMoves;i++) {
            long currentID = boatsAndMoves.get(i).first.longValue();
            MoveDataDTO currentMove = boatsAndMoves.get(i).second;
            if (currentID == boatID) {
                filteredMoves.add(currentMove);
            }
        }
        for (int i=0;i<filteredMoves.size();i++) {
            //TODO: Change this back the correct way around once it's fixed server-side
            LatLng coords = new LatLng(filteredMoves.get(i).getLongitude(),filteredMoves.get(i).getLatitude());
            Calendar time=Calendar.getInstance();
            time.setTimeInMillis(filteredMoves.get(i).getTimeMilli());
            Pair<Calendar,LatLng> p = new Pair<>(time,coords);
            output.add(p);
        }



        //TODO: Comment this out when moving to server.
        //Temporary way to 'fake' getting data from server.
//            totalMoves=12;
//            if (boatID==0) {
//                Calendar c1 = Calendar.getInstance();
//                LatLng l1=new LatLng(51.455792, -2.603306);
//                c1.setTimeInMillis(0);
//                Pair<Calendar,LatLng> p1 = new Pair<>(c1,l1);
//                Calendar c2 = Calendar.getInstance();
//                LatLng l2=new LatLng(51.455622, -2.603381);
//                c1.setTimeInMillis(50);
//                Pair<Calendar,LatLng> p2 = new Pair<>(c2,l2);
//                Calendar c3 = Calendar.getInstance();
//                LatLng l3=new LatLng(51.455896, -2.604561);
//                c1.setTimeInMillis(100);
//                Pair<Calendar,LatLng> p3 = new Pair<>(c3,l3);
//                output.add(p1);
//                output.add(p2);
//                output.add(p3);
//            }
//            if (boatID==1) {
//                Calendar c1 = Calendar.getInstance();
//                c1.setTimeInMillis(0);
//                LatLng l1 = new LatLng (51.456133, -2.602818);
//                Pair<Calendar,LatLng> p1 = new Pair<>(c1,l1);
//                Calendar c2 = Calendar.getInstance();
//                c2.setTimeInMillis(25);
//                LatLng l2 = new LatLng (51.456160, -2.601976);
//                Pair<Calendar,LatLng> p2 = new Pair<>(c2,l2);
//                output.add(p1);
//                output.add(p2);
//            }
//            if (boatID==2) {
//                Calendar c1 = Calendar.getInstance();
//                c1.setTimeInMillis(0);
//                LatLng l1 = new LatLng (51.455511, -2.602914);
//                Pair<Calendar,LatLng> p1 = new Pair<>(c1,l1);
//                Calendar c2 = Calendar.getInstance();
//                c2.setTimeInMillis(1);
//                LatLng l2 = new LatLng (51.455183, -2.603563);
//                Pair<Calendar,LatLng> p2 = new Pair<>(c2,l2);
//                Calendar c3 = Calendar.getInstance();
//                c3.setTimeInMillis(2);
//                LatLng l3 = new LatLng (51.455661, -2.604287);
//                Pair<Calendar,LatLng> p3 = new Pair<>(c3,l3);
//                output.add(p1);
//                output.add(p2);
//                output.add(p3);
//            }
//            if (boatID==3) {
//                Calendar c1 = Calendar.getInstance();
//                c1.setTimeInMillis(0);
//                LatLng l1 = new LatLng (51.456012, -2.605435);
//                Pair<Calendar,LatLng> p1 = new Pair<>(c1,l1);
//                Calendar c2 = Calendar.getInstance();
//                c2.setTimeInMillis(0);
//                LatLng l2 = new LatLng (51.456279, -2.606089);
//                Pair<Calendar,LatLng> p2 = new Pair<>(c2,l2);
//                Calendar c3 = Calendar.getInstance();
//                c3.setTimeInMillis(0);
//                LatLng l3 = new LatLng (51.456032, -2.606143);
//                Pair<Calendar,LatLng> p3 = new Pair<>(c3,l3);
//                Calendar c4 = Calendar.getInstance();
//                c4.setTimeInMillis(0);
//                LatLng l4 = new LatLng (51.456045, -2.607838);
//                Pair<Calendar,LatLng> p4 = new Pair<>(c4,l4);
//                output.add(p1);
//                output.add(p2);
//                output.add(p3);
//                output.add(p4);
//            }


        return output;
    }

    ArrayList<LatLng> sortLocationsByTime(ArrayList<Pair<Calendar,LatLng>> locations) {
        ArrayList<LatLng> output = new ArrayList<>();
        Collections.sort(locations,new timedLocationComparator());
        for (Pair<Calendar,LatLng> location : locations) {
            output.add(location.second);
        }
        return output;
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
//        final TextView leaderboardView=findViewById(R.id.leaderboard);
//        leaderboardView.setSelected(true);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                step=step;
                if (step < maxArraySize && !pause) {
                    step = step+1;
                    for (int i=0;i<boats.size();i++) {
                        List<LatLng> currentCoordinates = boats.get(i).coordinates.subList(0, step);
                        racePaths.get(i).setPoints(currentCoordinates);
                    }
                    String dataUF = leaderboardUFOverTime.get(step-1);
//                    leaderboardView.setText(dataUF);
                    Integer raceCompletion = (step*100)/maxArraySize;
                    timeline.setProgress(raceCompletion);
                }
                handler.postDelayed(this,period);
            }
        };

        boat_ids = boat_ids;
        new HttpRequestMoveDataDTO().execute();
        while (!loaded) {
            //Wait until data has been loaded from the server.
        }
        totalMoves=boatsAndMoves.size();
        for (int i=0;i<numberOfBoats;i++) {
            Long boat_id = boat_ids.get(i);
            ArrayList<LatLng> path = sortLocationsByTime(getTimesAndLocationsForBoat(boat_id));
            if (!path.isEmpty()) {
                paths.add(path);
                Integer colour =displayColours.get(i);
                boat newBoat = new boat(boat_id.toString(),colour,path);
                ArrayList<Integer> score = new ArrayList<>();
                score.add(0);
                scoreList.add(score);
                boats.add(newBoat);
            }
        }


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
        for (int i=0;i<boats.size();i++) {
            ArrayList<Integer> score = scoreList.get(i);
            extendArray(score, maxArraySize);
            boats.get(i).setScores(score);
        }
        maxArraySize=maxArraySize;

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
//            newLine.color(Color.RED);
            newLine.color(boats.get(i).getDisplayColour()+0xff000000);
            newLine.width(4);
            racePathOptions.add(newLine);
            racePaths.add(mMap.addPolyline(racePathOptions.get(i)));
            racePaths.get(i).setClickable(true);
        }
        mMap.setOnPolylineClickListener(new pathListener());
        handler.post(runnable);

    }
}