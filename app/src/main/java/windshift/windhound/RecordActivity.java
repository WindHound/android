package windshift.windhound;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import windshift.windhound.objects.MoveDataDTO;

public class RecordActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, SensorEventListener {

    // constants needed for permissions and settings checking
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // ids to record for
    private long competitor_id;
    private long race_id;
    private long boat_id;

    MoveDataDTO currentMoveData;

    private Boolean requestingLocationUpdates;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Queue<String> fileNames;

    private Sensor accelerometer;
    private Sensor compass;
    private Sensor gyroscope;
    private SensorManager sensorManager;

    // test
    private ArrayList<SensorEvent> accelerometerEvents;
    private ArrayList<SensorEvent> compassEvents;
    private ArrayList<SensorEvent> gyroscopeEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // get the id of the race to be recorded
        Intent intent = getIntent();
        race_id = Long.parseLong(intent.getStringExtra(RaceActivity.EXTRA_RACE_ID));

        // TODO remove testing ids
        competitor_id = 8;
        race_id = 41;
        boat_id = 8;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // default sensors could be NULL
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accelerometerEvents = new ArrayList<>();
        compassEvents = new ArrayList<>();
        gyroscopeEvents = new ArrayList<>();

        createLocationRequest();
        requestingLocationUpdates = true;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Queue of the stored filenames.
        //fileNames = new LinkedList<String>();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //writeLocationToFile(locationResult);
                for (Location location : locationResult.getLocations()) {
                    List<Float> x = new ArrayList<>();
                    List<Float> y = new ArrayList<>();
                    List<Float> z = new ArrayList<>();
                    List<Float> angle = new ArrayList<>();
                    List<Float> dX = new ArrayList<>();
                    List<Float> dY = new ArrayList<>();
                    List<Float> dZ = new ArrayList<>();
                    try {
                        x.add(accelerometerEvents.get(0).values[0]);
                        y.add(accelerometerEvents.get(0).values[1]);
                        z.add(accelerometerEvents.get(0).values[2]);
                        angle.add(compassEvents.get(0).values[0]);
                        dX.add(gyroscopeEvents.get(0).values[0]);
                        dY.add(gyroscopeEvents.get(0).values[1]);
                        dZ.add(gyroscopeEvents.get(0).values[2]);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    currentMoveData = new MoveDataDTO();
                    currentMoveData.setCompetitorID(competitor_id);
                    currentMoveData.setBoatID(boat_id);
                    currentMoveData.setRaceID(race_id);
                    currentMoveData.setTimeMilli(location.getTime());
                    currentMoveData.setLatitude((float) location.getLatitude());
                    currentMoveData.setLongitude((float) location.getLongitude());
                    currentMoveData.setX(x);
                    currentMoveData.setY(y);
                    currentMoveData.setZ(z);
                    currentMoveData.setAngle(angle);
                    currentMoveData.setdX(dX);
                    currentMoveData.setdY(dY);
                    currentMoveData.setdZ(dZ);
                    new HttpRequestTask().execute();
                    accelerometerEvents = new ArrayList<>();
                    compassEvents = new ArrayList<>();
                    gyroscopeEvents = new ArrayList<>();
                }
            }
        };

        // Checks settings and starts location updates.
        checkLocationSettings();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_CANCELED) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                } else if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, compass,
                    SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, gyroscope,
                    SensorManager.SENSOR_DELAY_FASTEST);
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestingLocationUpdates = false;
        sensorManager.unregisterListener(this);
        stopLocationUpdates();
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(250);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        // Checks whether the current  location settings are satisfied
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied.
                    try {
                        // Check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(RecordActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                    null);
        } else {
            requestLocationPermissions();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /*
    private void writeLocationToFile(LocationResult locationResult) {
        String filename = Calendar.getInstance().getTimeInMillis() + ".txt";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext()
                    .openFileOutput(filename, Context.MODE_APPEND));
            for (Location location : locationResult.getLocations()) {
                outputStreamWriter.write("Lat: " + location.getLatitude() + ", Long: " +
                        location.getLongitude() + "\n\r");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileNames.add(filename);
    }
    */

    /*
    private String readLastLocationFromFile() {
        String result = "";
        String file = fileNames.element();
        try {
            InputStream inputStream = getApplicationContext().openFileInput(file);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                result = fileNames.element() + ": " + stringBuilder.toString();
                getApplicationContext().deleteFile(fileNames.element());
                fileNames.remove();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    */

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case 1:
                accelerometerEvents.add(event);
                break;
            case 3:
                compassEvents.add(event);
                break;
            case 4:
                gyroscopeEvents.add(event);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission was denied
                    finish();
                }
                return;
            }
            // Other 'case' lines to check for other permissions this app might request
        }
    }

    public void stopRecord(View view) {
        onPause();
        finish();
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Long> {

        @Override
        protected Long doInBackground(Void... params) {
            try {
                final String url = getResources().getString((R.string.server_address)) +
                        "/movedata/add";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                Long id = restTemplate.postForObject(url, currentMoveData, Long.class);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long id) {
            if (id != null) {

            }
        }

    }

}
