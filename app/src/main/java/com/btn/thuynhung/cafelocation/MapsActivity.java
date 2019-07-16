package com.btn.thuynhung.cafelocation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements LocationListener,
        GoogleMap.OnPolylineClickListener, TaskLoadedCallBack {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment mapFragment;
    private Location myLocation = null;
    private CameraPosition myCameraPosition;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    //Code request asking user's permission for showing current location
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    final String[] addresses = {"Санкт-Петербург, гражданский проспект, 41А",
            "Санкт-Петербург, гражданский проспект, 31"};
    List<String> cafeNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        cafeNames.add(getString(R.string.aka));
        cafeNames.add(getString(R.string.shop));

        if (savedInstanceState != null) {
            myLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            myCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        createProgressBar();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_maps);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (myMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, myMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, myLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void createProgressBar() {
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map loading...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        myProgress.show();
    }

    private void onMyMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        //showRoute();
        //example

        showCafeLocations();
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //close Dialog Progress after successfully loading Map
                myProgress.dismiss();

                //show user's location
                askPermissionsAndShowLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);
    }

    private void showRoute() {
       /* double[] coord = getLocationFromAddress(addresses[0]);
        MarkerOptions origin = new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        MarkerOptions dest = new MarkerOptions().position(new LatLng(coord[0], coord[1]));

        String url = getUrl(origin.getPosition(), dest.getPosition(), "driving");

        new FetchURL(MapsActivity.this).execute(url, "driving");
*/
        Polyline polyline1 = myMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(new LatLng(60.008669, 30.392760),
                        new LatLng(60.008363, 30.393757),
                        new LatLng(60.008502, 30.395699),
                        new LatLng(60.011205, 30.394584),
                        new LatLng(60.011296, 30.397137)));

        polyline1.setTag("A");
        stylePolyline(polyline1);
        //myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));

        // Set listeners for click events.
        myMap.setOnPolylineClickListener(this);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }

    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    private void showCafeLocations() {

        Integer pressedButton = getIntent().getIntExtra("PLACE", 0);

        if (pressedButton == 3)
            for (int i = 0; i < addresses.length; i++)
                showLocation(addresses[i], cafeNames.get(i));
        else if (pressedButton == 1)
            showLocation(addresses[0], cafeNames.get(0));
        else showLocation(addresses[1], cafeNames.get(1));
    }

    private void showLocation(String address, String cafeName) {

        Geocoder coder = new Geocoder(this);
        List<Address> addresses = null;
        try {
            addresses = coder.getFromLocationName(address, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null) return;
        Address location = addresses.get(0);
        LatLng cafeCoord = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions options = new MarkerOptions();
        options.title(cafeName);
        options.position(cafeCoord);
        myMap.addMarker(options);
        myMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(cafeCoord));

    }

    private void askPermissionsAndShowLocation() {
        //with API >= 23, we need to ask user for permission to show his location
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {

                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, permissions, REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
            }
        }
        this.showMyLocation();
    }

    //when user answered permission request (permit or reject)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                //if request was skipped, return
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    this.showMyLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            return null;
        }

        return bestProvider;
    }

    private void showMyLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        try {

            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

            myLocation = locationManager.getLastKnownLocation(locationProvider);
        }
        // For Android API >= 23, catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        final int duration = 13;
        if (myLocation != null) {

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, duration));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskDone(Object... values) {

    }
}
