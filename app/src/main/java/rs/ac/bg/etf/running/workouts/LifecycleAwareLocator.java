package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.rest.OpenWeatherMapService;
import rs.ac.bg.etf.running.users.Session;

public class LifecycleAwareLocator implements DefaultLifecycleObserver {

    @Inject
    public LifecycleAwareLocator() {

    }

    private static ArrayList<Double> latitudeList, longitudeList;

    public static ArrayList<Double> getLatitudeList() {
        return latitudeList;
    }

    public void setLatitudeList(ArrayList<Double> latitudeList) {
        LifecycleAwareLocator.latitudeList = latitudeList;
    }

    public static ArrayList<Double> getLongitudeList() {
        return longitudeList;
    }

    public void setLongitudeList(ArrayList<Double> longitudeList) {
        LifecycleAwareLocator.longitudeList = longitudeList;
    }

    public static void allocateLocationsList(){
        latitudeList = new ArrayList<>();
        longitudeList = new ArrayList<>();
    }

    public void getLocation(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(MainActivity.LOG_TAG, "permission not granted");
            return;
        }

        FusedLocationProviderClient locationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);

        Task<Location> task = locationProviderClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken());

        task.addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d(MainActivity.LOG_TAG, "lat:" + latitude + " lon:" + longitude);

                OpenWeatherMapService openWeatherMapService = new OpenWeatherMapService();
                openWeatherMapService.getCurrentWeather(latitude, longitude);

                if(latitudeList == null && longitudeList == null){
                    allocateLocationsList();
                };

              //  Toast.makeText(Session.getMainActivity(), latitude + ":" + longitude, Toast.LENGTH_SHORT).show();

                latitudeList.add(latitude);
                longitudeList.add(longitude);

            }
        });
    }
}
