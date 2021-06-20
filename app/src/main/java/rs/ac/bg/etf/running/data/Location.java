package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private double latitude;
    private double longitude;
    private long idWorkout;
    private String username;

    public Location(long id, double latitude, double longitude, long idWorkout, String username) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idWorkout = idWorkout;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getIdWorkout() {
        return idWorkout;
    }

    public void setIdWorkout(long idWorkout) {
        this.idWorkout = idWorkout;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
