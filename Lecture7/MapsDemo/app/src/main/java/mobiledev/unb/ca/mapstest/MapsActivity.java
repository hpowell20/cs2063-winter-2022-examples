package mobiledev.unb.ca.mapstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int LOCATION_REQUEST = 101;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create an instance of the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch the last known location
        fetchLastLocation();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

            // Location permissions not available
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = location;

                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "," +
                        currentLocation.getLongitude(), Toast.LENGTH_LONG).show();

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                assert mapFragment != null;
                mapFragment.getMapAsync(MapsActivity.this);
            } else {
                Toast.makeText(getApplicationContext(), "Unable to fetch the location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Fredericton.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
     @Override
     public void onMapReady(@NonNull GoogleMap googleMap) {

         // Add a marker based on the current location and move the camera
         LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
         MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
         googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
         googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
         googleMap.addMarker(markerOptions);
     }

    /**
     * Handles the result of the permissions check
     * @param requestCode The code to be checked against
     * @param permissions The list of permissions to be checked against
     * @param grantResults The list of granted results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Granted");
                fetchLastLocation();
            } else {
                Toast.makeText(this, "onRequestPermissionsResult: Denied", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onRequestPermissionsResult: Denied");
            }
        }
    }
}
