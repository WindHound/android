package windshift.windhound;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class RecordActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    // Constants needed for permissions and settings checking
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x1;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Boolean requestingLocationUpdates;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Queue<String> fileNames;

    // Temp
    private TextView textView_location;
    private TextView textView_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        createLocationRequest();
        requestingLocationUpdates = true;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Temporary text views to display latitude and longitude
        textView_location = findViewById(R.id.textView_location);
        textView_read = findViewById(R.id.textView_read);

        // Queue of the stored filenames.
        fileNames = new LinkedList<String>();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                writeLocationToFile(locationResult);
                textView_read.setText(readLastLocationFromFile());
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
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestingLocationUpdates = false;
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
        locationRequest.setFastestInterval(1000);
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

    private void writeLocationToFile(LocationResult locationResult) {
        String filename = Calendar.getInstance().getTimeInMillis() + ".txt";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext()
                    .openFileOutput(filename, Context.MODE_APPEND));
            for (Location location : locationResult.getLocations()) {
                outputStreamWriter.write("Lat: " + location.getLatitude() + ", Long: " +
                        location.getLongitude() + "\n\r");
                textView_location.setText("Lat: " + location.getLatitude() + ", Long: " +
                        location.getLongitude());
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileNames.add(filename);
    }

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
                result = stringBuilder.toString();
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

}
