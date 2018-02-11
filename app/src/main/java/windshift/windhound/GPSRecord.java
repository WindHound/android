package windshift.windhound;

import android.location.Location;

public class GPSRecord {

    private long time;
    private double latitude;
    private double longitude;

    public GPSRecord(Location location) {
        time = location.getTime();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

}
