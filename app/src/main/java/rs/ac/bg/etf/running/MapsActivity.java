package rs.ac.bg.etf.running;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.workouts.WorkoutDetailsFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "TAG_MAPE";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitudeStart = 41.385064;
        double longitudeStart = 2.173403;

        double latitudeEnd = 40.416775;
        double longitudeEnd = -3.173403;

        List<LatLng> l = new ArrayList<>();

        if(WorkoutDetailsFragment.locationDraw != null) {
            if(WorkoutDetailsFragment.locationDraw.size() > 0) {
                latitudeStart = WorkoutDetailsFragment.locationDraw.get(0).getLatitude();
                longitudeStart = WorkoutDetailsFragment.locationDraw.get(0).getLongitude();
                int s = WorkoutDetailsFragment.locationDraw.size() - 1;
                latitudeEnd = WorkoutDetailsFragment.locationDraw.get(s).getLatitude();
                longitudeEnd = WorkoutDetailsFragment.locationDraw.get(s).getLongitude();

                List<Location> locations = WorkoutDetailsFragment.locationDraw;
                for(int i = 0; i < locations.size(); i++){
                    l.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
                }

                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true).color(Color.BLUE)
                        .addAll(l));
            }
        }

        LatLng start = new LatLng(latitudeStart,longitudeStart);
        mMap.addMarker(new MarkerOptions().position(start).title("Marker in start"));

        LatLng end = new LatLng(latitudeEnd, longitudeEnd);
        mMap.addMarker(new MarkerOptions().position(end).title("Marker in end"));

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();

        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCYbRKyEasBZqrzCOPbwzcj_STkFwq-fJ8")
                .build();

        DirectionsApiRequest req = DirectionsApi.getDirections(context,
                latitudeStart + "," + longitudeStart,
                latitudeEnd + "," + longitudeEnd);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 6));
    }
}