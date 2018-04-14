package windshift.windhound.objects;

import java.util.List;

public class MoveDataDTO {

    // setup
    private Long competitorID;
    private Long boatID;
    private Long raceID;
    private Long timeMilli;

    // gps
    private Float latitude;
    private Float longitude;

    // accelerometer
    private List<Float> x;
    private List<Float> y;
    private List<Float> z;

    // compass
    private List<Float> angle;

    // gyroscope
    private List<Float> dX;
    private List<Float> dY;
    private List<Float> dZ;

    public MoveDataDTO() {

    }

    public Long getCompetitorID() {
        return competitorID;
    }

    public void setCompetitorID(Long competitorID) {
        this.competitorID = competitorID;
    }

    public Long getBoatID() {
        return boatID;
    }

    public void setBoatID(Long boatID) {
        this.boatID = boatID;
    }

    public Long getRaceID() {
        return raceID;
    }

    public void setRaceID(Long raceID) {
        this.raceID = raceID;
    }

    public Long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(Long timeMilli) {
        this.timeMilli = timeMilli;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public List<Float> getX() {
        return x;
    }

    public void setX(List<Float> x) {
        this.x = x;
    }

    public List<Float> getY() {
        return y;
    }

    public void setY(List<Float> y) {
        this.y = y;
    }

    public List<Float> getZ() {
        return z;
    }

    public void setZ(List<Float> z) {
        this.z = z;
    }

    public List<Float> getAngle() {
        return angle;
    }

    public void setAngle(List<Float> angle) {
        this.angle = angle;
    }

    public List<Float> getdX() {
        return dX;
    }

    public void setdX(List<Float> dX) {
        this.dX = dX;
    }

    public List<Float> getdY() {
        return dY;
    }

    public void setdY(List<Float> dY) {
        this.dY = dY;
    }

    public List<Float> getdZ() {
        return dZ;
    }

    public void setdZ(List<Float> dZ) {
        this.dZ = dZ;
    }

}
