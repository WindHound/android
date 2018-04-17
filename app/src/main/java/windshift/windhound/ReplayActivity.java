package windshift.windhound;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
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

import java.text.DecimalFormat;
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
    class timedMoveDataComparator implements Comparator<MoveDataDTO> {
        @Override
        public int compare(MoveDataDTO d1, MoveDataDTO d2) {
            return d1.getTimeMilli().compareTo(d2.getTimeMilli());
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
    private int currentBoatNum = -1;
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
    private ArrayList<ArrayList<MoveDataDTO>> separatedMoveData = new ArrayList<>();
    private ArrayList<Pair<Long,MoveDataDTO>> boatsAndMoves = new ArrayList<>();
    private boolean loaded = false;
    private long totalMoves = 0;
    private boolean priorPauseStatus = false;
    private DecimalFormat twoDecimalPlaces = new DecimalFormat("0.00");

    public void detailsExpandAndContract(View v) {
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout current = findViewById(R.id.overallLayout);
        constraintSet.clone(current);
        if(expandedDetails) {
            pause=priorPauseStatus;
            constraintSet.setVisibility(R.id.seekBar,ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.map,ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.toggleButton,ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.details,ConstraintSet.GONE);
            constraintSet.connect(R.id.detailsTitle,ConstraintSet.TOP,R.id.map,ConstraintSet.BOTTOM);
//            constraintSet.constrainHeight(R.id.seekBar,(36*density/160));
//            constraintSet.constrainHeight(R.id.map,ConstraintSet.MATCH_CONSTRAINT);
//            constraintSet.constrainHeight(R.id.toggleButton,(36*density/160));
//            constraintSet.constrainHeight(R.id.detailsTitle,(45*density/160));
            showName(currentBoatNum);
        }
        else {
            priorPauseStatus=pause;
            pause=true;
            constraintSet.setVisibility(R.id.seekBar,ConstraintSet.GONE);
            constraintSet.setVisibility(R.id.map,ConstraintSet.GONE);
            constraintSet.setVisibility(R.id.toggleButton,ConstraintSet.GONE);
            constraintSet.setVisibility(R.id.details,ConstraintSet.VISIBLE);
            constraintSet.connect(R.id.detailsTitle,ConstraintSet.TOP,R.id.leaderboard,ConstraintSet.BOTTOM);
//            constraintSet.constrainHeight(R.id.seekBar,0);
//            constraintSet.constrainHeight(R.id.toggleButton,0);
//            constraintSet.constrainHeight(R.id.map,0);
//            constraintSet.constrainHeight(R.id.detailsTitle,ConstraintSet.MATCH_CONSTRAINT);
//            constraintSet.constrainHeight(R.id.detailsTitle,(45*density/160));
            showFullDetails(currentBoatNum);
        }
        expandedDetails = !expandedDetails;
        if (Build.VERSION.SDK_INT>=19) {
            TransitionManager.beginDelayedTransition(current);
        }
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

    void showName(int index) {
        String info;
        if (index==-1) {
            info = "Details";
        } else {
            boat boat = boats.get(index);
            info = "Boat " + boat.getName();
        }
        TextView details = findViewById(R.id.detailsTitle);
        details.setText(info);
    }

    void showFullDetails(int index) {
        String info;
        int time = step;
        if (time>=maxArraySize) {time=maxArraySize-1;}
        if (index==-1) {
            info = "";
        } else {
            boat boat = boats.get(index);
            MoveDataDTO current = separatedMoveData.get(index).get(time);
            info = "Latitude: "+current.getLongitude() + System.getProperty("line.separator")+
                "Longitude: "+current.getLatitude() + System.getProperty("line.separator")+
                "Acceleration: "+twoDecimalPlaces.format(current.getX().get(0)) +
                    ", " + twoDecimalPlaces.format(current.getY().get(0)) +
                    ", " + twoDecimalPlaces.format(current.getZ().get(0));
        }
        TextView details = findViewById(R.id.details);
        details.setText(info);
    }



    private class pathListener implements GoogleMap.OnPolylineClickListener {
        public void onPolylineClick(Polyline polyline) {
            int index = racePaths.indexOf(polyline);
            currentBoatNum=index;
            showName(index);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        currentBoatNum=position;
        showName(currentBoatNum);
        if (expandedDetails) {showFullDetails(currentBoatNum);}
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
        race_id=83;
        boat_ids = new ArrayList<>();
        boat_ids.add(2L);


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
                priorPauseStatus=isChecked;
            }
        });
    }

    private void fakeServerConnection() {
        boat_ids.clear();
        boat_ids.add(0L);
        MoveDataDTO move = new MoveDataDTO();
        move.setCompetitorID(0L);
        move.setBoatID(0L);
        move.setRaceID(0L);
        move.setTimeMilli(0L);
        move.setLatitude(0F);
        move.setLongitude(0F);
        ArrayList<Float> accels = new ArrayList<>();
        accels.add(0F);
        move.setX(accels);
        move.setY(accels);
        move.setZ(accels);
        Long id = new Long(0L);
        Pair<Long,MoveDataDTO> datum = new Pair<>(id, move);
        boatsAndMoves.add(datum);
        loaded=true;
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

    ArrayList<MoveDataDTO> getMoveDataForBoat (long boatID) {
        ArrayList<MoveDataDTO> filteredMoves = new ArrayList<>();
        for (int i=0;i<totalMoves;i++) {
            long currentID = boatsAndMoves.get(i).first.longValue();
            MoveDataDTO currentMove = boatsAndMoves.get(i).second;
            if (currentID == boatID) {
                filteredMoves.add(currentMove);
            }
        }
        return filteredMoves;
    }

    ArrayList<MoveDataDTO> sortMoveDataByTime(ArrayList<MoveDataDTO> data) {
        ArrayList<MoveDataDTO> output = new ArrayList<>(data);
        Collections.sort(output,new timedMoveDataComparator());
        return output;
    }

    //TODO: Change this back the correct way around once it's fixed server-side
    ArrayList<LatLng> extractLatLngFromMoveData(ArrayList<MoveDataDTO> data) {
        ArrayList<LatLng> locations = new ArrayList<>();
        for (MoveDataDTO datum : data) {
            LatLng coords = new LatLng(datum.getLongitude(),datum.getLatitude());
            locations.add(coords);
        }
        return locations;
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

        //TODO: Uncomment when using server.
        new HttpRequestMoveDataDTO().execute();
        //TODO: Comment out when server connection not necessary for testing.
//        fakeServerConnection();

        while (!loaded) {
            //Wait until data has been loaded from the server.
        }
        totalMoves=boatsAndMoves.size();
        for (int i=0;i<numberOfBoats;i++) {
            Long boat_id = boat_ids.get(i);
            ArrayList<MoveDataDTO> data = sortMoveDataByTime(getMoveDataForBoat(boat_id));
            ArrayList<LatLng> path = extractLatLngFromMoveData(data);
            if (!path.isEmpty()) {
                separatedMoveData.add(data);
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